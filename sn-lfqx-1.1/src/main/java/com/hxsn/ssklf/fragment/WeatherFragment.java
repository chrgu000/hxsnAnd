package com.hxsn.ssklf.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.andbase.ssk.BaseWebViewClient;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.ssklf.R;

/**
 * 查看历史数据页面
 * create an instance of this fragment.
 */
@SuppressLint("ValidFragment")
public class WeatherFragment extends Fragment{

    private Context context;
    private WebView webView;
    private  String urlWebView;

    public WeatherFragment() {
    }

    public WeatherFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);

        webView = (WebView)view.findViewById(R.id.web_view);

        webView.getSettings().setJavaScriptEnabled(true);

        urlWebView = "http://www.lfqxfw.com/forecast.php";
        LogUtil.i("WeatherFragment", "---------urlWebView="+urlWebView);
        setWebView();
        return view;
    }


    //设置webView
    private void setWebView() {

        webView.getSettings().setJavaScriptEnabled(true);
        LogUtil.i("WenFragment","urlWebView="+urlWebView);

        BaseWebViewClient webViewClient = new BaseWebViewClient("wen_canGoBack");
        webView.setWebViewClient(webViewClient);

        webView.loadUrl(urlWebView);
    }
}
