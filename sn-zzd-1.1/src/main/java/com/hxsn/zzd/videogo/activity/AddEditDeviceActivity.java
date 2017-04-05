package com.hxsn.zzd.videogo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.andbase.library.util.AbToastUtil;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.AndShared;
import com.hxsn.zzd.R;
import com.hxsn.zzd.base.BaseTitle;
import com.hxsn.zzd.utils.JsonUtils;
import com.hxsn.zzd.utils.Tools;

public class AddEditDeviceActivity extends Activity {

    private EditText edtSeriesNo,edtVerifyCode;
    private String mSeriesNo,mVerifyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        BaseTitle.getInstance(this).setTitle("添加设备");
        addView();
    }

    private void addView() {
        edtSeriesNo = (EditText)findViewById(R.id.edt_series_no);
        edtVerifyCode = (EditText)findViewById(R.id.edt_verify_code);
        findViewById(R.id.addBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSeriesNo = edtSeriesNo.getText().toString();
                mVerifyCode = edtVerifyCode.getText().toString();




                if(mSeriesNo.length()==0 || mVerifyCode.length()==0){
                    AbToastUtil.showToast(AddEditDeviceActivity.this,"输入不能为空");
                }else {
                    //关闭设备视频加密
                    String cancelUrl = "https://open.ys7.com/api/lapp/device/encrypt/off?accessToken="+
                            AndShared.getValue("accessToken")+"&deviceSerial="+mSeriesNo+"&validateCode="+mVerifyCode;
                    new AndHttpRequest(AddEditDeviceActivity.this) {
                        @Override
                        public void getResponse(String response) {
                            String code = AndJsonUtils.getCode(response);
                            if(code.equals("200") || code.equals("60016")){
                                Tools.addDevice(AddEditDeviceActivity.this,mSeriesNo,mVerifyCode);
                            }else {
                                AbToastUtil.showToast(AddEditDeviceActivity.this, JsonUtils.getMessage(response));
                            }
                        }
                    }.doPost(cancelUrl);

                }
            }
        });
    }
}
