package com.hxsn.zzd.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.andbase.library.util.AbLogUtil;
import com.andbase.ssk.entity.NotifyInfo;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.AndShared;
import com.baidu.android.pushservice.PushMessageReceiver;
import com.hxsn.zzd.TApplication;
import com.hxsn.zzd.activity.HomeActivity;
import com.hxsn.zzd.utils.Const;
import com.videogo.util.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 百度推送的service
 *  Created by jiely on 2016/5/5.
 */
public class BaiduPushMessageReceiver extends PushMessageReceiver {
    
    final static String Tgg1 = "BaiduPushMessageReceiver";
    
    @Override
    public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {

        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;

        String code = AndShared.getValue(Const.CODE_CHANNEL_ID);
        if(channelId != null && !channelId.equals(code)){
            AndShared.setValue(Const.CODE_CHANNEL_ID,channelId);
        }

        if(errorCode == 0){
            //绑定成功
        }
        
        AbLogUtil.i(Tgg1, "onBind-responseString=" + responseString);
    }

    @Override
    public void onUnbind(Context context, int i, String s) {
        AbLogUtil.d(Tgg1, "onUnbind-responseString=" + s);
    }

    /**
     * setTags() 的回调函数。
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
     * @param successTags
     *            设置成功的tag
     * @param failTags
     *            设置失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onSetTags(Context context, int errorCode, List<String> successTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + successTags + " failTags=" + failTags
                + " requestId=" + requestId;
        AbLogUtil.d(Tgg1, "onSetTags-" + responseString);

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        //updateContent(context, responseString);
    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {
        AbLogUtil.d(Tgg1, "onDelTags-"+s);
    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {
        AbLogUtil.d(Tgg1, "onListTags-"+s);
    }

    /**
     * 接收透传消息的函数。  接到通知后点击
     * @param context
     *            上下文
     * @param message
     *            推送的消息
     * @param customContentString
     *            自定义内容,为空或者json字符串
     */
    @Override
    public void onMessage(Context context, String message, String customContentString) {
        String messageString = "透传消息 message=\"" + message + "\" customContentString=" + customContentString;
        LogUtil.i(Tgg1, "onMessage-" + messageString);
         /*NotificationManager mNotificationManager;
         NotificationCompat.Builder mBuilder;

        TApplication.isNotify = true;
        AbLogUtil.d(Tgg1, "TApplication.isNotify="+TApplication.isNotify);

        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("设备状态改变")
                .setContentText(message)
                .setContentIntent(showIntentActivityNotify(context)) //点击的意图ACTION是跳转到Intent
                //.setTicker("测试通知来啦")//通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
                .setAutoCancel(false)//设置这个标志当用户单击面板就可以让通知将自动取消
                // .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.icon_logo);
        mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(100, mBuilder.build());
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(message);
            boolean isChange = jsonObject.optBoolean("succeed");
            if(isChange){
                EventBus.getDefault().post("deviceStatus");//设备状态改变了
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

    }

    /** 显示通知栏点击跳转到指定Activity */
    public PendingIntent showIntentActivityNotify(Context context){
        //点击的意图ACTION是跳转到Intent
        Intent resultIntent = new Intent(context, HomeActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }


    /**
     * 接收通知点击的函数。
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定义内容，为空或者json字符串
     */
    @Override
    public void onNotificationClicked(Context context, String title, String description, String customContentString) {
        String notifyString = "通知点击 title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        LogUtil.i(Tgg1, "onNotificationClicked-notifyString=" + notifyString);
        NotifyInfo notifyInfo = getNotifyInfo(description,title,customContentString);

        try {
            JSONObject jsonObject = new JSONObject(customContentString);
            TApplication.warningId = jsonObject.optString("warningid");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TApplication.notifyInfo = notifyInfo;

        if(notifyInfo != null){
            Intent intent = new Intent();
            switch (notifyInfo.getType()){
                case 1://新闻类

                    break;
                case 2://报警类
                    intent.setClass(context.getApplicationContext(), HomeActivity.class);
                    TApplication.mode = 7;
                    break;
                case 3://设备
                    intent.setClass(context.getApplicationContext(), HomeActivity.class);
                    TApplication.mode = 8;
                    break;
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
        }
    }

    /**
     * 接收通知到达的函数。
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定义内容，为空或者json字符串
     */
    @Override
    public void onNotificationArrived(Context context, String title, String description, String customContentString) {

        String notifyString = "onNotificationArrived  title=\"" + title
                + "\" description=\"" + description + "\" customContent="
                + customContentString;
        LogUtil.i(Tgg1, "onNotificationArrived-notifyString=" + notifyString);

        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
        TApplication.notifyInfo = getNotifyInfo(title, description, customContentString);
    }

    /**
     * 拼接为NotifyInfo对象
     * @param title title
     * @param description description
     * @param customContentString type id
     * @return NotifyInfo
     */
    private NotifyInfo getNotifyInfo(String title, String description, String customContentString){

        NotifyInfo notifyInfo = new NotifyInfo();
        notifyInfo.setTitle(title);
        notifyInfo.setDescription(description);

        NotifyInfo notifyInfo2 = AndJsonUtils.getNotifyInfo(customContentString);
        if(notifyInfo2 != null){
            notifyInfo.setId(notifyInfo2.getId());
            notifyInfo.setType(notifyInfo2.getType());
        }

        if(notifyInfo2.getType() == 3){
            EventBus.getDefault().post("deviceStatus"+description);//设备状态改变了
        }

        return  notifyInfo;
    }
}
