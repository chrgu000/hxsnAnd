package com.hxsn.witwork.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hxsn.witwork.R;
import com.hxsn.witwork.fragment.ComFragment;
import com.hxsn.witwork.fragment.MineFragment;
import com.hxsn.witwork.fragment.WorkFragment;
import com.hxsn.witwork.fragment.ZhuiSuFragment;

public class HomeActivity extends FragmentActivity {

    LinearLayout tab1Layout, tab2Layout, tab3Layout, tab4Layout;
    TextView tab1TV, tab2TV, tab3TV, tab4TV;
    FragmentManager fm;
    ImageView tab1IV, tab2IV, tab3IV, tab4IV;

    Fragment workFragment,comFragment,zhuisuFragment,mineFragment;
    int tag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        addView();

        Intent intent = getIntent();
        if (intent != null){
            tag = intent.getIntExtra("tag", 0);
        }

        setChoiceItem(tag);

        tab1Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clear();
                tab1TV.setTextColor(Color.rgb(139, 193, 17));
                tab1IV.setBackgroundResource(R.drawable.zuo01);
                setChoiceItem(0);
            }
        });
        tab2Layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                clear();
                tab2TV.setTextColor(Color.rgb(139, 193, 17));
                tab2IV.setBackgroundResource(R.drawable.jiao01);
                setChoiceItem(1);
            }
        });
        tab3Layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                clear();
                tab3TV.setTextColor(Color.rgb(139, 193, 17));
                tab3IV.setBackgroundResource(R.drawable.su01);
                setChoiceItem(2);
            }
        });
        tab4Layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                clear();
                tab4TV.setTextColor(Color.rgb(139, 193, 17));
                tab4IV.setBackgroundResource(R.drawable.wo01);
                setChoiceItem(3);
            }
        });
    }

    private void addView() {
        tab1Layout = (LinearLayout) findViewById(R.id.tab1);
        tab2Layout = (LinearLayout) findViewById(R.id.tab2);
        tab3Layout = (LinearLayout) findViewById(R.id.tab3);
        tab4Layout = (LinearLayout) findViewById(R.id.tab4);
        tab1TV = (TextView) findViewById(R.id.hometltv);
        tab2TV = (TextView) findViewById(R.id.tab2IVltv);
        tab3TV = (TextView) findViewById(R.id.tab3IVltv);
        tab4TV = (TextView) findViewById(R.id.tab4IVltv);
        tab1IV = (ImageView) findViewById(R.id.tab1IV);
        tab2IV = (ImageView) findViewById(R.id.tab2IV);
        tab3IV = (ImageView) findViewById(R.id.tab3IV);
        tab4IV = (ImageView) findViewById(R.id.tab4IV);
        workFragment = new WorkFragment();
        comFragment = new ComFragment();
        zhuisuFragment = new ZhuiSuFragment();
        mineFragment = new MineFragment();
    }

    private void setChoiceItem(int id) {
        fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (id == 0) {
            clear();
            tab1TV.setTextColor(Color.rgb(139, 193, 17));
            tab1IV.setBackgroundResource(R.drawable.zuo01);
            transaction.replace(R.id.tabContent, workFragment);
        }else if(id==1){
            clear();
            tab2TV.setTextColor(Color.rgb(139, 193, 17));
            tab2IV.setBackgroundResource(R.drawable.jiao01);
            transaction.replace(R.id.tabContent, comFragment);
        }else if(id==2){
            clear();
            tab3TV.setTextColor(Color.rgb(139, 193, 17));
            tab3IV.setBackgroundResource(R.drawable.su01);
            Intent inte =new Intent(HomeActivity.this,SuYuanActivity.class);
            startActivity(inte);
        }else if(id==3){
            clear();
            tab4TV.setTextColor(Color.rgb(139, 193, 17));
            tab4IV.setBackgroundResource(R.drawable.wo01);
            transaction.replace(R.id.tabContent, mineFragment);
        }

        transaction.commit();
    }


    private void clear() {

        tab1TV.setTextColor(Color.rgb(91, 91, 99));
        tab2TV.setTextColor(Color.rgb(91, 91, 99));
        tab3TV.setTextColor(Color.rgb(91, 91, 99));
        tab4TV.setTextColor(Color.rgb(91, 91, 99));

        tab1IV.setBackgroundResource(R.drawable.zuo);
        tab2IV.setBackgroundResource(R.drawable.jiao);
        tab3IV.setBackgroundResource(R.drawable.su);
        tab4IV.setBackgroundResource(R.drawable.wo);
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tab1:
                setChoiceItem(0);
                break;
            case R.id.tab2:
                setChoiceItem(1);
                break;
            case R.id.tab3:
                setChoiceItem(2);
                break;
            case R.id.tab4:
                setChoiceItem(3);
                break;
            default:
                break;
        }
    }
}
