package com.hxsn.jwb.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andbase.library.http.AbHttpUtil;
import com.andbase.library.http.listener.AbStringHttpResponseListener;
import com.andbase.library.http.model.AbRequestParams;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.LoadingDialogUtils;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.jwb.R;

import org.apache.http.message.BasicNameValuePair;

import java.util.List;

/**
 *
 * Created by jiely on 2017/3/16.
 */
public abstract class MyHttpRequest {
    private AbHttpUtil httpUtil = null;
    private Context context;
    private View inFresh;//网络问题页面
    private ImageView imgRefresh;//刷新按钮
    private TextView txtError;
    private Dialog freshDialog;
    private View view;


    public abstract void getResponse(String response);

    public MyHttpRequest(Context context){
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




    /**
     * 加载分多次显示listView
     * @param view 父view容器
     * @param url 地址
     * @param params map
     */
    public void doPost(final View view, final String url, final AbRequestParams params){
        LogUtil.i("httpRequest","url="+url);
        List<BasicNameValuePair> basicNameValuePairs = params.getParamsList();
        for(BasicNameValuePair pair:basicNameValuePairs){
            LogUtil.i("httpRequest","name="+pair.getName()+",value="+pair.getValue());
        }


        final View inFresh =  view.findViewById(R.id.in_refresh);
        imgRefresh = (ImageView) inFresh.findViewById(R.id.img_refresh);
        txtError = (TextView) inFresh.findViewById(R.id.txt_error);
        imgRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                doPost(view,url,params);
            }
        });

        httpUtil.post(url,params, new AbStringHttpResponseListener() {
            @Override
            public void onStart() {
                freshDialog = LoadingDialogUtils.createLoadingDialog(context, "加载中...");
            }

            @Override
            public void onFinish() {
                LoadingDialogUtils.closeDialog(freshDialog);
            }

            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
                inFresh.setVisibility(View.VISIBLE);
                txtError.setText(content);
            }

            @Override
            public void onSuccess(int statusCode, String content) {
                onHttpSuccess(statusCode,content);
                if(!AndJsonUtils.isSuccess(content)){
                    inFresh.setVisibility(View.VISIBLE);
                }else {
                    inFresh.setVisibility(View.GONE);
                }
            }
        });
    }

}
