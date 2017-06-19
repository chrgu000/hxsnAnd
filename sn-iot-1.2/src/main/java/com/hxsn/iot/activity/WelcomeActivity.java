package com.hxsn.iot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.andbase.ssk.utils.LogUtil;
import com.andbase.ssk.utils.PermissionUtils;
import com.hxsn.iot.MainActivity;
import com.hxsn.iot.R;
import com.hxsn.iot.uitls.Tools;


/**
 * 欢迎页
 */
public class WelcomeActivity extends Activity {

    private TextView txtCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView txtIn = (TextView) findViewById(R.id.txt_in);
        txtIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        txtCity = (TextView)findViewById(R.id.txt_city);


        //动态申请CODE_READ_PHONE_STATE权限
        if(Integer.parseInt(android.os.Build.VERSION.SDK) < 23){
            displayCity();
        }else {
            PermissionUtils.requestPermission(this, PermissionUtils.CODE_ACCESS_FINE_LOCATION, new PermissionUtils.PermissionGrant() {
                @Override
                public void onPermissionGranted(int requestCode) {
                    displayCity();
                }
            });
        }
    }


    private void displayCity(){
        String city = Tools.getCityName(this);
        txtCity.setText(city);
    }


    /**
     * 用于权限管理
     * 如果全部授权的话，则直接通过进入
     * 如果权限拒绝，缺失权限时，则使用dialog提示
     *
     * @param requestCode  请求代码
     * @param permissions  权限参数
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtil.i(MainActivity.class,"***********onRequestPermissionsResult************");

        PermissionUtils.requestPermissionsResult(this, PermissionUtils.CODE_READ_PHONE_STATE, permissions, grantResults, new PermissionUtils.PermissionGrant() {
            @Override
            public void onPermissionGranted(int requestCode) {
                displayCity();
            }
        });
    }


}
