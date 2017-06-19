package com.hxsn.iot;

import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.andbase.ssk.utils.AndShared;
import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.hxsn.iot.activity.HomeActivity;
import com.hxsn.iot.activity.LoginActivity;
import com.hxsn.iot.activity.WelcomeActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baiduPush();
        Intent intent = new Intent();
        String isLogin = AndShared.getValue("login");
        TApplication.user = AndShared.getUser();
        if (isLogin.length()==0) {
            intent.setClass(this, WelcomeActivity.class);
        } else {
            if (isLogin.equals("1")) {
                if(TextUtils.isEmpty(TApplication.defaultGreenHouse.getId()) ){
                    TApplication.mode = 9;//首次，未选择棚室，先进入棚室界面
                }
                intent.setClass(this, HomeActivity.class);
            } else {
                intent.setClass(this, LoginActivity.class);
            }
        }
        startActivity(intent);
        finish();
    }


    private void baiduPush(){

        // Push: 无账号初始化，用api key绑定
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "53pUWKOIgSBt3qoEtuVOYx4d");

        String pkgName = this.getPackageName();
        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
                this.getResources().getIdentifier(
                        "notification_custom_builder", "layout", pkgName),
                this.getResources().getIdentifier("notification_icon", "id", pkgName),
                this.getResources().getIdentifier("notification_title", "id", pkgName),
                this.getResources().getIdentifier("notification_text", "id", pkgName));
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_VIBRATE);
        cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
        cBuilder.setLayoutDrawable(this.getResources().getIdentifier(
                "simple_notification_icon", "drawable", pkgName));
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.windows);
        cBuilder.setNotificationSound(uri.toString());
        //设置手机震动
        //   //第一个，0表示手机静止的时长，第二个，1000表示手机震动的时长
        //   //第三个，1000表示手机震动的时长，第四个，1000表示手机震动的时长
        //   //此处表示手机先震动1秒，然后静止1秒，然后再震动1秒
        long[] vibrates = {0, 1000, 1000, 1000};
        //notification.vibrate = vibrates;
        cBuilder.setNotificationVibrate(vibrates);
        PushManager.setNotificationBuilder(this, 1, cBuilder);
    }
}
