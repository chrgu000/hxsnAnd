package com.hxsn.zzd.fragment;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.andbase.ssk.utils.LogUtil;
import com.hxsn.zzd.R;
import com.hxsn.zzd.TApplication;
import com.hxsn.zzd.utils.Const;

/**
 * 天气预告页面
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class WeatherFragment extends Fragment{
    private Context context;
    private WebView webView;
    private String urlWebView;

    public WeatherFragment(Context context) {
        this.context = context;
    }

    public WeatherFragment() {
    }
    public static WeatherFragment newInstance(Context context,int mode) {
        WeatherFragment fragment = new WeatherFragment(context);
        Bundle args = new Bundle();
        args.putInt("fragment_mode", mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_webview, container, false);

        webView = (WebView)view.findViewById(R.id.web_view);

        webView.getSettings().setJavaScriptEnabled(true);

        if(TApplication.user == null || TApplication.defaultGreenHouse == null){
            return view;
        }
        urlWebView = Const.URL_WEATHER_WEB+ TApplication.user.getUserId()+"&dyid="+ TApplication.defaultGreenHouse.getId();
        LogUtil.i("WeatherFragment", "---------urlWebView="+urlWebView);
        webView.loadUrl(urlWebView);

        return view;
    }


}
