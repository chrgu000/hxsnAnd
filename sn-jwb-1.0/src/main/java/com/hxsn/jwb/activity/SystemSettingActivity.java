package com.hxsn.jwb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import com.andbase.library.http.model.AbRequestParams;
import com.andbase.library.util.AbToastUtil;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.hxsn.jwb.R;
import com.hxsn.jwb.TApplication;
import com.hxsn.jwb.base.BaseTitle;
import com.hxsn.jwb.utils.Const;
import com.hxsn.jwb.utils.Shared;


public class SystemSettingActivity extends Activity implements View.OnClickListener{


    private EditText edtUp,edtDown;
    private TextView txtSure,txtCancel,txtHome,txtSelect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_setting);

        BaseTitle.getInstance(this).setTitle("报警设置");


        addView();
        addListener();

       // final WebView webView = (WebView)findViewById(R.id.web_view);


    }

    private void addListener() {
        txtSure.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
        txtSelect.setOnClickListener(this);
    }

    private void addView() {
        edtDown = (EditText)findViewById(R.id.edt_down);
        edtUp = (EditText)findViewById(R.id.edt_up);
        txtSure = (TextView)findViewById(R.id.txt_sure);
        txtCancel = (TextView)findViewById(R.id.txt_cancel);
        txtHome = (TextView)findViewById(R.id.txt_current_home);
        txtSelect = (TextView)findViewById(R.id.txt_select);


        txtHome.setText(Shared.getChickHome().getName());
        edtUp.setText(Shared.getValue("maxValue"));
        edtDown.setText(Shared.getValue("minValue"));
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.txt_sure:
                final String maxValue = edtUp.getText().toString();
                final String minValue = edtDown.getText().toString();

                if(TextUtils.isEmpty(maxValue) || TextUtils.isEmpty(minValue)){
                    AbToastUtil.showToast(this,"请填入数字");
                    break;
                }

                if(Double.parseDouble(minValue)>Double.parseDouble(maxValue)){
                    AbToastUtil.showToast(this,"上限温度要大于下限温度");
                    break;
                }

                AbRequestParams params = new AbRequestParams();
                params.put("uid", TApplication.user.getUserId());
                params.put("homeid", Shared.getChickHome().getId());
                params.put("minValue", minValue);
                params.put("maxValue", maxValue);

                new AndHttpRequest(this) {
                    @Override
                    public void getResponse(String response) {
                        if(AndJsonUtils.isSuccess(response)){
                            Shared.setValue("maxValue",maxValue);
                            Shared.setValue("minValue",minValue);
                            AbToastUtil.showToast(SystemSettingActivity.this,"设置成功");
                            onBackPressed();
                        }
                    }
                }.doPost(Const.URL_SETTING_WARNNING_PARAMS,params);

                break;
            case R.id.txt_cancel:
                onBackPressed();
                break;
            case R.id.txt_select:

                intent.setClass(this,SelectHomeActivity.class);
                intent.putExtra("intent","systemSetting");
                startActivity(intent);
                break;
        }
    }
}
