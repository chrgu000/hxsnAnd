package com.hxsn.iot.videogo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andbase.library.util.AbToastUtil;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.AndShared;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.iot.R;
import com.hxsn.iot.base.BaseTitle;
import com.hxsn.iot.uitls.JsonUtils;
import com.hxsn.iot.uitls.Tools;



/**
 * 通过扫描二维码方式添加设备
 */
public class AddScanDeviceActivity extends Activity {
    private String TAG = "AddScanDeviceActivity";
    private TextView txtSerialNo,txtVerifyCode;
    private Button btnAdd;
    private  String mSerialNoStr = "";//设备序列号
    /** 添加设备输入验证码 */
    private String mVerifyCode = null;

    private LinearLayout mQueryingCameraRyt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        BaseTitle.getInstance(this).setTitle("添加设备");

        LogUtil.i(TAG,"onCreate");
        addView();
        addData();
    }

    private void addData() {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mSerialNoStr = mBundle.getString("SerialNo");
            mVerifyCode = mBundle.getString("very_code");
            if(mSerialNoStr == null || mSerialNoStr.length() == 0){
                txtVerifyCode.setVisibility(View.INVISIBLE);
                btnAdd.setVisibility(View.INVISIBLE);
                txtSerialNo.setText("二维码不匹配");
            }else {
                txtVerifyCode.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
                txtSerialNo.setText("设备序列号:"+mSerialNoStr);
                txtVerifyCode.setText("设备验证码："+mVerifyCode);
            }
        }
    }

    private void addView() {
        mQueryingCameraRyt = (LinearLayout)findViewById(R.id.queryingCameraRyt);
        txtSerialNo = (TextView)findViewById(R.id.txt_series_no);
        txtVerifyCode = (TextView)findViewById(R.id.txt_verify_code);

        btnAdd = (Button)findViewById(R.id.addBtn);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭设备视频加密
                String cancelUrl = "https://open.ys7.com/api/lapp/device/encrypt/off?accessToken="+ AndShared.getValue("accessToken")+"&deviceSerial="+mSerialNoStr+"&validateCode="+mVerifyCode;
                new AndHttpRequest(AddScanDeviceActivity.this) {
                    @Override
                    public void getResponse(String response) {
                        String code = AndJsonUtils.getCode(response);
                        if(code.equals("200") || code.equals("60016")){
                            Tools.addDevice(AddScanDeviceActivity.this,mSerialNoStr,mVerifyCode);
                        }else {
                            AbToastUtil.showToast(AddScanDeviceActivity.this, JsonUtils.getMessage(response));
                        }
                    }
                }.doPost(cancelUrl);
            }
        });
    }

}
