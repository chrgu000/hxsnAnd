package com.andbase.ssk.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andbase.library.http.AbHttpUtil;
import com.andbase.library.http.listener.AbStringHttpResponseListener;
import com.andbase.library.http.model.AbRequestParams;

import org.apache.http.message.BasicNameValuePair;

import java.util.List;

/**
 *
 * Created by jiely on 2017/3/16.
 */
public abstract class AndHttpRequest {
    private AbHttpUtil httpUtil = null;
    private Context context;
    private View inFresh;//网络问题页面
    private ImageView imgRefresh;//刷新按钮
    private TextView txtError;
    private Dialog freshDialog;
    private View view;


    public abstract void getResponse(String response);

    public AndHttpRequest(Context context){
        this.context = context;
        httpUtil = AbHttpUtil.getInstance(context);
    }

    private void onHttpFailure(String url,int statusCode, String content){
        LogUtil.i("httpRequest","statusCode="+statusCode+",content="+content+",url="+url);

    }

    private void onHttpSuccess(int statusCode, String content){
        LogUtil.i("httpRequest","statusCode="+statusCode+",content="+content);
        getResponse(content);
    }

    public void doGet(final String url) {
        LogUtil.i("httpRequest","url="+url);
        httpUtil.get(url,new AbStringHttpResponseListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
                onHttpFailure(url,statusCode,content);
            }

            @Override
            public void onSuccess(int statusCode, String content) {
                onHttpSuccess(statusCode,content);
            }
        });
    }


    public void doPost(final String url, final AbRequestParams params){
        if(params == null){
            doPost(url);
            return;
        }
        LogUtil.i("httpRequest","url="+url);
        List<BasicNameValuePair> basicNameValuePairs = params.getParamsList();
        for(BasicNameValuePair pair:basicNameValuePairs){
            LogUtil.i("httpRequest","name="+pair.getName()+",value="+pair.getValue());
        }
        httpUtil.post(url,params, new AbStringHttpResponseListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
                onHttpSuccess(statusCode,content);
            }

            @Override
            public void onSuccess(int statusCode, String content) {
                onHttpSuccess(statusCode,content);
            }
        });
    }

    public void doPost(final String url){
        LogUtil.i("httpRequest","url="+url);

        httpUtil.post(url, new AbStringHttpResponseListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
                onHttpSuccess(statusCode,content);
            }

            @Override
            public void onSuccess(int statusCode, String content) {
                onHttpSuccess(statusCode,content);
            }
        });
    }



}
