package com.hxsn.zzd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hxsn.zzd.R;
import com.hxsn.zzd.TApplication;
import com.hxsn.zzd.base.BaseTitle;
import com.hxsn.zzd.utils.Const;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        BaseTitle.getInstance(this).setTitle("设置");

        final Intent intent = new Intent();

        //个人设置
       findViewById(R.id.txt_set_personal).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               intent.setClass(SettingActivity.this, PersonalSettingActivity.class);
               startActivity(intent);
           }
       });

        //手工设置
        findViewById(R.id.txt_set_plc_manual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TApplication.webUrl = Const.URL_SYSTEM_SETTING+ TApplication.user.getUserId();
                intent.setClass(SettingActivity.this, WebViewActivity.class);
                intent.putExtra("title","PLC手工设置");
                startActivity(intent);
            }
        });

        //模板设置
        findViewById(R.id.txt_set_plc_template).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TApplication.webUrl = Const.URL_TEMPLATE_SETTING+ TApplication.user.getUserId();
                intent.setClass(SettingActivity.this, WebViewActivity.class);
                intent.putExtra("title","PLC模板设置");
                startActivity(intent);
            }
        });
    }
}
