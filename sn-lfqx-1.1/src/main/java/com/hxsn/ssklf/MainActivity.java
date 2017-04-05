package com.hxsn.ssklf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hxsn.ssklf.activity.HomeActivity;

/**
 * 主Activity  loading页，5秒自动进入主页，点击按钮进入主页
 */
public class MainActivity extends Activity {

    //TimerTask task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txtComeIn = (TextView)findViewById(R.id.txt_come_in);

        txtComeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, HomeActivity.class);
                startActivity(intent);
               // task.cancel();
                finish();
            }
        });
    }



}
