package com.hxsn.zzd.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;

import com.hxsn.zzd.R;

/**
 *
 * Created by jiely on 2017/3/29.
 */
public class CheckPermission {

    private Context context;
    public CheckPermission(Context context){
        this.context = context;
    }

    //检查权限时，判断系统的权限集合
    public boolean permissionSet(String... permissions) {
        for (String permission : permissions) {
            if (isLackPermission(permission)) {//是否添加完全部权限集合
                return true;
            }
        }
        return false;
    }

    //检查系统权限是，判断当前是否缺少权限(PERMISSION_DENIED:权限是否足够)
    private boolean isLackPermission(String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED;
    }

    //获取全部权限
    public static boolean hasAllPermissionGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }


    //显示对话框提示用户缺少权限
    public static void showMissingPermissionDialog(final Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("早知道权限设置");//提示帮助
        builder.setMessage("请设置为允许");
        builder.setIcon(activity.getResources().getDrawable(R.mipmap.icon_logo));

        //如果是拒绝授权，则退出应用
        //退出
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        //打开设置，让用户选择打开权限
        builder.setPositiveButton("setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//打开设置
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivity(intent);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
}
