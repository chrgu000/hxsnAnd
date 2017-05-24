package com.hxsn.witwork.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

/**
 * 作者：jiely on 2017/5/17 11:42
 * 邮箱：songlj@fweb.cn
 */
public class BaseActivity extends Activity {
    private Toast toast;
    protected static int screenW, screenH;
    protected RequestQueue mQueue;

    protected static ArrayList<Activity> allActivity=new ArrayList<Activity>();

    public static BaseActivity baseActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allActivity.add(this);
        baseActivity = this;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenW = metrics.widthPixels;
        screenH = metrics.heightPixels;
        mQueue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onStop() {

        mQueue.cancelAll(this);
        super.onStop();

    }

    protected void showToast(int resId) {
        showToast(getString(resId));
    }

    protected void showToast(String msg) {
        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void clearAllActivity(){
        if(allActivity!=null){
            for (int i = 0; i < allActivity.size(); i++) {
                allActivity.get(i).finish();
            }
        }
    }
}
