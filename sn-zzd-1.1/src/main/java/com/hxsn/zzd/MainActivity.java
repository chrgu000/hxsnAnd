package com.hxsn.zzd;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.andbase.ssk.utils.AndShared;
import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.hxsn.zzd.activity.HomeActivity;
import com.hxsn.zzd.activity.LoginActivity;
import com.hxsn.zzd.activity.MoreActivity;
import com.hxsn.zzd.activity.WelcomeActivity;
import com.hxsn.zzd.utils.CheckPermission;

public class MainActivity extends Activity {


    static final String[] PERMISSION = new String[]{
            android.Manifest.permission.READ_PHONE_STATE,        //读取设备信息
    };

    public static final int PERMISSION_DENIEG = 1;//权限不足，权限被拒绝的时候
    public static final int PERMISSION_REQUEST_CODE = 0;//系统授权管理页面时的结果参数
    public static final String PACKAGE_URL_SCHEME = "package:";//权限方案
    public CheckPermission checkPermission;//检测权限类的权限检测器
    private boolean isrequestCheck = true;//判断是否需要系统权限检测。防止和系统提示框重叠


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // process(savedInstanceState);
       // setContentView(R.layout.activity_main);

        baiduPush();

        Intent intent = new Intent();
        String isLogin = AndShared.getValue("login");
        TApplication.user = AndShared.getUser();
        if (isLogin.length()==0) {
            intent.setClass(this, WelcomeActivity.class);
        } else {
            if (isLogin.equals("1")) {
                if(TextUtils.isEmpty(TApplication.defaultGreenHouse.getId()) ){
                    intent.setClass(this, MoreActivity.class);
                }else {
                    intent.setClass(this, HomeActivity.class);
                }
            } else {
                intent.setClass(this, LoginActivity.class);
            }
        }
        startActivity(intent);
        finish();
    }


    private void baiduPush(){

        // Push: 无账号初始化，用api key绑定
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "8Zvu75UCbx7ZHgj78O38qgFa");

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
