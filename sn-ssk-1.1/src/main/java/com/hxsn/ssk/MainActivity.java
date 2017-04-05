package com.hxsn.ssk;


import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.andbase.ssk.utils.AndShared;
import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.hxsn.ssk.activity.HomeActivity;
import com.hxsn.ssk.activity.LoginActivity;
import com.hxsn.ssk.activity.WelcomeActivity;



public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);

        baiduPush();

        Intent intent = new Intent();
        String isLogin = AndShared.getValue("login");
        TApplication.user = AndShared.getUser();
        if (isLogin.length()==0) {
            intent.setClass(this, WelcomeActivity.class);
        } else {
            if (isLogin.equals("1")) {
                intent.setClass(this, HomeActivity.class);
            } else {
                intent.setClass(this, LoginActivity.class);
            }
        }
        startActivity(intent);
        finish();
    }

    private void baiduPush(){
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "jAvXSGRoxPzaUBSwaMSNG3gm");

        /*BasicPushNotificationBuilder bBuilder = new BasicPushNotificationBuilder();
        bBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        bBuilder.setStatusbarIcon(R.mipmap.icon_logo);*/
        //bBuilder.setNotificationVibrate(new long[]{500, 250, 250, 500, 500, 250, 250, 500, 500, 250, 250, 500});

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

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm);
        cBuilder.setNotificationSound(uri.toString());

        //设置手机震动
        //   //第一个，0表示手机静止的时长，第二个，1000表示手机震动的时长
        //   //第三个，1000表示手机震动的时长，第四个，1000表示手机震动的时长
        //   //此处表示手机先震动1秒，然后静止1秒，然后再震动1秒
        long[] vibrates = {0, 1000, 1000, 1000};
        //notification.vibrate = vibrates;
        cBuilder.setNotificationVibrate(vibrates);
        //PushManager.setDefaultNotificationBuilder(this, bBuilder);
        PushManager.setNotificationBuilder(this, 1, cBuilder);
    }


}
