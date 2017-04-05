package com.hxsn.jwb;

import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import com.andbase.ssk.utils.AndShared;
import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.hxsn.jwb.activity.HomeActivity;
import com.hxsn.jwb.activity.LoginActivity;
import com.hxsn.jwb.activity.WelcomeActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Push: 无账号初始化，用api key绑定
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "CLHlWrgGWp4c9iYjL28tnylj");

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
        cBuilder.setNotificationSound(Uri.withAppendedPath(
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "6").toString());
        // 推送高级设置，通知栏样式设置为下面的ID
        PushManager.setNotificationBuilder(this, 1, cBuilder);

        //  PushManager.setNotificationBuilder(this, 1, bBuilder);

    }


}
