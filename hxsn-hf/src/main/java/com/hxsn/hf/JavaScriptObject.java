package com.hxsn.hf;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.hxsn.hf.navigation.NewRealPlayActivity;

 public class JavaScriptObject {
    Context mContxt;
    
    public JavaScriptObject(Context mContxt) {
        this.mContxt = mContxt;
    }//sdk17版本以上加上注解
    @JavascriptInterface
    public void javaFun() {
    	Intent intent = new Intent(mContxt, NewRealPlayActivity.class);
    	mContxt.startActivity(intent);
    } 
}
