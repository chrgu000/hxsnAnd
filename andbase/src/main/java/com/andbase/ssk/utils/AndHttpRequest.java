package com.andbase.ssk.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andbase.library.http.AbHttpUtil;
import com.andbase.library.http.listener.AbStringHttpResponseListener;
import com.andbase.library.http.model.AbRequestParams;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public void  get(final String uriApi){
        LogUtil.i("httpRequest","url="+uriApi);
        Handler.Callback callback = new HttpHandlerCallback();
        final Handler handler = new Handler(callback);
        new Thread(){
            @Override
            public void run() {
                HttpGet httpRequest = new HttpGet(uriApi);
                try {
                    /*发送请求并等待响应*/
                    HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
                     /*若状态码为200 ok*/
                    LogUtil.i("httpRequest","getStatusCode="+httpResponse.getStatusLine().getStatusCode());
                    if(httpResponse.getStatusLine().getStatusCode() == 200) {
                         /*读*/
                        String result =  EntityUtils.toString(httpResponse.getEntity()).trim();
                        result = replaceBlank(result);
                        LogUtil.i("httpRequest","content="+result);
                        Message message = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString("result",result);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    class HttpHandlerCallback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String result = bundle.getString("result");
            getResponse(result);
            return false;
        }
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

}
