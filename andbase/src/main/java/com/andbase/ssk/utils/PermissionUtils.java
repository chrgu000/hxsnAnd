package com.andbase.ssk.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * android 23以上的权限申请
 *使用方法
 *1.  if(Integer.parseInt(android.os.Build.VERSION.SDK) < 23){
        getAllGrantedPermission();
    }else {
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_READ_PHONE_STATE, new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            getAllGrantedPermission();
        }
    });
 }
 *2. 在activity的onRequestPermissionsResult方法中调用requestPermissionsResult
 *
 *
 * Created by jiely on 2017/3/29.
 */
public class PermissionUtils {//Manifest.permission.ACCESS_FINE_LOCATION

    private static final String TAG = PermissionUtils.class.getSimpleName();
    public static final int CODE_WRITE_CONTACTS = 0;
    public static final int CODE_GET_ACCOUNTS = 1;
    public static final int CODE_READ_CONTACTS = 2;
    public static final int CODE_READ_CALL_LOG = 3;
    public static final int CODE_READ_PHONE_STATE = 4;
    public static final int CODE_PHONE = 5;
    public static final int CODE_WRITE_CALL_LOG = 6;
    public static final int CODE_USE_SIP = 7;
    public static final int CODE_PROCESS_OUTGOING_CALLS = 8;

    public static final int CODE_READ_CALENDAR = 9;
    public static final int CODE_WRITE_CALENDAR = 10;
    public static final int CODE_CAMERA = 11;
    public static final int CODE_BODY_SENSORS = 12;
    public static final int CODE_ACCESS_FINE_LOCATION = 13;
    public static final int CODE_ACCESS_COARSE_LOCATION = 14;

    public static final int CODE_READ_EXTERNAL_STORAGE = 15;
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 16;
    public static final int CODE_RECORD_AUDIO = 17;
    public static final int CODE_READ_SMS = 18;
    public static final int CODE_RECEIVE_WAP_PUSH = 19;
    public static final int CODE_RECEIVE_MMS = 20;
    public static final int CODE_RECEIVE_SMS = 21;
    public static final int CODE_SEND_SMS = 22;

    public static final int CODE_MULTI_PERMISSION = 100;
    

    private static final String[] allPermissions = {
            //1.group:android.permission-group.CONTACTS 0,1,2  请求联系人权限
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS,

            //2.group:android.permission-group.PHONE 3,4,5,6,7,8 请求电话卡信息
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
    //permission:com.android.voicemail.permission.ADD_VOICEMAIL

    //3.group:android.permission-group.CALENDAR 9,10 请求日历权限
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,

    //4.group:android.permission-group.CAMERA   11 请求摄像头权限
            Manifest.permission.CAMERA,

    //5.group:android.permission-group.SENSORS 12 请求设备隐私权限
            Manifest.permission.BODY_SENSORS,

    //6.group:android.permission-group.LOCATION 13,14 请求获取定位权限
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,

    //7.group:android.permission-group.STORAGE    15,16  请求SD卡存储权限
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,

    //8.group:android.permission-group.MICROPHONE 17
            Manifest.permission.RECORD_AUDIO,

    //9.group:android.permission-group.SMS      18,19,20,21,22    请求访问短信权限
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS
            //Manifest.permission.READ_READ_CELL_BROADCASTS
    };

    //申请成功后的处理
    public interface PermissionGrant {
        void onPermissionGranted(int requestCode);
    }

    /**
     * 通过偏移量返回要请求的权限名称
     * @param i 偏移量
     * @return
     */
    private static String getStrMessage(int i){
        String msg = "";
        switch (i){
            case 0:
            case 1:
            case 2:
                msg = "联系人";
                break;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                msg = "电话";
                break;
            case 9:
            case 10:
                msg = "日历";
                break;
            case 11:
                msg =  "摄像头";
                break;
            case 12:
                msg =  "设备隐私";
                break;
            case 13:
            case 14:
                msg =  "获取定位";
                break;
            case 15:
            case 16:
                msg =  "SD卡存储";
                break;
            case 17:
                msg =  "录制音频";
                break;
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
                msg =  "短信";
                break;
        }
        return msg;
    }

    /**
     * Requests permission.
     * 请求单个权限
     * @param activity a
     * @param requestCode request code, e.g. if you need request CAMERA permission,parameters is PermissionUtils.CODE_CAMERA
     */
    public static void requestPermission(final Activity activity, final int requestCode, PermissionGrant permissionGrant) {
        if (activity == null) {
            return;
        }

        LogUtil.i(TAG, "requestPermission requestCode:" + requestCode);
        if (requestCode < 0 || requestCode >= allPermissions.length) {
            Log.w(TAG, "requestPermission illegal requestCode:" + requestCode);
            return;
        }

        final String requestPermission = allPermissions[requestCode];

        //如果是6.0以下的手机，ActivityCompat.checkSelfPermission()会始终等于PERMISSION_GRANTED，
        // 但是，如果用户关闭了你申请的权限，ActivityCompat.checkSelfPermission(),会导致程序崩溃(java.lang.RuntimeException: Unknown exception code: 1 msg null)，
        // 你可以使用try{}catch(){},处理异常，也可以在这个地方，低于23就什么都不做，
        // 个人建议try{}catch(){}单独处理，提示用户开启权限。
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }

        int checkSelfPermission;
        try {
            checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
        } catch (RuntimeException e) {
            Toast.makeText(activity, "please open this permission", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "RuntimeException:" + e.getMessage());
            return;
        }

        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            LogUtil.i(TAG, "ActivityCompat.checkSelfPermission != PackageManager.PERMISSION_GRANTED");

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                LogUtil.i(TAG, "requestPermission shouldShowRequestPermissionRationale");
                shouldShowRationale(activity, requestCode, requestPermission);

            } else {
                LogUtil.i(TAG, "requestCameraPermission else");
                ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
            }

        } else {
            LogUtil.i(TAG, "ActivityCompat.checkSelfPermission ==== PackageManager.PERMISSION_GRANTED");
           // Toast.makeText(activity, "opened:" + allPermissions[requestCode], Toast.LENGTH_SHORT).show();
            permissionGrant.onPermissionGranted(requestCode);
        }
    }


    /**
     * @param activity
     * @param requestCode  Need consistent with requestPermission
     * @param permissions
     * @param grantResults
     */
    public static void requestPermissionsResult(final Activity activity, final int requestCode, @NonNull String[] permissions,
                                                @NonNull int[] grantResults, PermissionGrant permissionGrant) {
        if (activity == null) {
            return;
        }
        LogUtil.i(TAG, "requestPermissionsResult requestCode:" + requestCode);

        if (requestCode == CODE_MULTI_PERMISSION) {
            requestMultiResult(activity, permissions, grantResults, permissionGrant);
            return;
        }

        if (requestCode < 0 || requestCode >= allPermissions.length) {
            Log.w(TAG, "requestPermissionsResult illegal requestCode:" + requestCode);
            Toast.makeText(activity, "illegal requestCode:" + requestCode, Toast.LENGTH_SHORT).show();
            return;
        }

        LogUtil.i(TAG, "onRequestPermissionsResult requestCode:" + requestCode + ",permissions:" + permissions.toString()
                + ",grantResults:" + grantResults.toString() + ",length:" + grantResults.length);

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LogUtil.i(TAG, "onRequestPermissionsResult PERMISSION_GRANTED");

            permissionGrant.onPermissionGranted(requestCode);

        } else {
            LogUtil.i(TAG, "onRequestPermissionsResult PERMISSION NOT GRANTED");
            //String[] permissionsHint = activity.getResources().getStringArray(R.array.permissions);
            String meg = getStrMessage(requestCode);
            openSettingActivity(activity, "Result" + "没有"+meg+"权限，无法开启"+meg+"功能，请开启");
        }
    }


    /**
     * 一次申请多个权限
     */
    public static void requestMultiPermissions(final Activity activity,int[] permInts, PermissionGrant grant) {

        final List<String> permissionsList = getNoGrantedPermission(activity,permInts, false);
        final List<String> shouldRationalePermissionsList = getNoGrantedPermission(activity, permInts,true);
        
        if (permissionsList == null || shouldRationalePermissionsList == null) {
            return;
        }
        LogUtil.i(TAG, "requestMultiPermissions permissionsList:" + permissionsList.size() + ",shouldRationalePermissionsList:" + shouldRationalePermissionsList.size());

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]),
                    CODE_MULTI_PERMISSION);
            LogUtil.i(TAG, "showMessageOKCancel requestPermissions");

        } else if (shouldRationalePermissionsList.size() > 0) {
            showMessageOKCancel(activity, "should open those permission",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, shouldRationalePermissionsList.toArray(new String[shouldRationalePermissionsList.size()]),
                                    CODE_MULTI_PERMISSION);
                            LogUtil.i(TAG, "showMessageOKCancel requestPermissions");
                        }
                    });
        } else {
            grant.onPermissionGranted(CODE_MULTI_PERMISSION);
        }
    }


    private static void requestMultiResult(Activity activity, String[] permissions, int[] grantResults, PermissionGrant permissionGrant) {

        if (activity == null) {
            return;
        }

        LogUtil.i(TAG, "onRequestPermissionsResult permissions length:" + permissions.length);
        Map<String, Integer> perms = new HashMap<>();

        ArrayList<String> notGranted = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            LogUtil.i(TAG, "permissions: [i]:" + i + ", permissions[i]" + permissions[i] + ",grantResults[i]:" + grantResults[i]);
            perms.put(permissions[i], grantResults[i]);
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                notGranted.add(permissions[i]);
            }
        }

        if (notGranted.size() == 0) {
            Toast.makeText(activity, "all permission success" + notGranted, Toast.LENGTH_SHORT).show();
            permissionGrant.onPermissionGranted(CODE_MULTI_PERMISSION);
        } else {
            openSettingActivity(activity, "those permission need granted!");
        }
    }
    
    private static void shouldShowRationale(final Activity activity, final int requestCode, final String requestPermission) {
        //String[] permissionsHint = activity.getResources().getStringArray(R.array.permissions);
        String meg = getStrMessage(requestCode);
        showMessageOKCancel(activity, "Rationale: " + "没有"+meg+"权限，无法开启"+meg+"功能，请开启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
                LogUtil.i(TAG, "showMessageOKCancel requestPermissions:" + requestPermission);
            }
        });
    }

    private static void showMessageOKCancel(final Activity context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();

    }


    private static void openSettingActivity(final Activity activity, final String message) {

        showMessageOKCancel(activity, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                LogUtil.i(TAG, "getPackageName(): " + activity.getPackageName());
                Uri uri = Uri.fromParts("package", activity.getPackageName(), message);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        });
    }
    
    /**
     * @param activity
     * @param isShouldRationale true: return no granted and shouldShowRequestPermissionRationale permissions, false:return no granted and !shouldShowRequestPermissionRationale
     * @return
     */
    public static ArrayList<String> getNoGrantedPermission(Activity activity, int[] permissionInts, boolean isShouldRationale) {

        ArrayList<String> permissions = new ArrayList<>();

        for (int i = 0; i < permissionInts.length; i++) {
            String requestPermission = allPermissions[i];
            
            int checkSelfPermission = -1;
            try {
                checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
            } catch (RuntimeException e) {
                Toast.makeText(activity, "please open those permission", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "RuntimeException:" + e.getMessage());
                return null;
            }

            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                LogUtil.i(TAG, "getNoGrantedPermission ActivityCompat.checkSelfPermission != PackageManager.PERMISSION_GRANTED:" + requestPermission);

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                    LogUtil.i(TAG, "shouldShowRequestPermissionRationale if");
                    if (isShouldRationale) {
                        permissions.add(requestPermission);
                    }
                } else {

                    if (!isShouldRationale) {
                        permissions.add(requestPermission);
                    }
                    LogUtil.i(TAG, "shouldShowRequestPermissionRationale else");
                }
            }
        }

        return permissions;
    }
}
