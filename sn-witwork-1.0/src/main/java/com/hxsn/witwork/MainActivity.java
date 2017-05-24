package com.hxsn.witwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hxsn.witwork.activity.HomeActivity;
import com.hxsn.witwork.activity.LoginActivity;
import com.hxsn.witwork.ui.Shared;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent();
        if (Shared.getCode().length() > 0){
            intent.setClass(this, HomeActivity.class);
        }else {
            intent.setClass(this, LoginActivity.class);
        }
        //intent.setClass(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
