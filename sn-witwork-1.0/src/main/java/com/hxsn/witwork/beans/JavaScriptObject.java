package com.hxsn.witwork.beans;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.hxsn.witwork.activity.ProStatActivity;
import com.hxsn.witwork.activity.WorkDetalActivity;


public class JavaScriptObject {
   Context mContxt;

   public JavaScriptObject(Context mContxt) {
       this.mContxt = mContxt;
   }//sdk17版本以上加上注解
//    @JavascriptInterface
   //获取地块编号
//    public void javaFun(String dkid) {
//      	Intent intent = new Intent(mContxt, ProductDetailActivity.class);
//        intent.putExtra("dkid", dkid);
//      	mContxt.startActivity(intent);
//
//      }
   @JavascriptInterface
   public void javaFun(String title,int code) {
       Intent intent = new Intent();

       if(code==3)
       {
       intent.setAction("com.hxsn.action.jiankong.broadcast");
       //要发送的内容
       intent.putExtra("title",title);
       //发送 一个无序广播
       mContxt.sendBroadcast(intent);
     }
       else 	if(code==2)
       {
           intent = new Intent(mContxt, WorkDetalActivity.class);
           intent.putExtra("jobuuid", title);
             mContxt.startActivity(intent);
       }
       else
       {
           intent = new Intent(mContxt, ProStatActivity.class);
           intent.putExtra("code", title);
             mContxt.startActivity(intent);
       }
       }


}
