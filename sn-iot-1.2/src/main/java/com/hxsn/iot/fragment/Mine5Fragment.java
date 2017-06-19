package com.hxsn.iot.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andbase.ssk.entity.User;
import com.andbase.ssk.utils.AndShared;
import com.hxsn.iot.R;
import com.hxsn.iot.TApplication;
import com.hxsn.iot.activity.AboutUsActivity;
import com.hxsn.iot.activity.AddZZDActivity;
import com.hxsn.iot.activity.LoginActivity;
import com.hxsn.iot.activity.SettingActivity;
import com.hxsn.iot.activity.SystemSettingActivity;
import com.hxsn.iot.activity.UserManualActivity;


/**
 * A simple {@link Fragment} subclass.  物联网 我的页面
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */

@SuppressLint("ValidFragment")
public class Mine5Fragment extends Fragment implements View.OnClickListener {

    private Context context;
    private TextView txtAboutUs, txtExit,txtSetting;
    private TextView txtUserManual,txtAddDevice,txtUserCallback;
    private RelativeLayout rl1;



    public Mine5Fragment(Context context) {
        this.context = context;
    }

    public Mine5Fragment() {
    }
    public static Mine5Fragment newInstance(Context context,int mode) {
        Mine5Fragment fragment = new Mine5Fragment(context);
        Bundle args = new Bundle();
        args.putInt("fragment_mode", mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine5, container, false);
        //TApplication.mode = 5;
        addView(view);
        addListener();
        return view;
    }

    private void addListener() {
        txtAboutUs.setOnClickListener(this);
        txtSetting.setOnClickListener(this);
        txtExit.setOnClickListener(this);
        txtUserManual.setOnClickListener(this);
        txtUserCallback.setOnClickListener(this);

        txtAddDevice.setOnClickListener(this);
        txtUserManual.setOnClickListener(this);
    }

    private void addView(View v) {
        rl1 = (RelativeLayout)v.findViewById(R.id.rl_1);
        txtAboutUs = (TextView) v.findViewById(R.id.txt_about_us);
        txtSetting = (TextView) v.findViewById(R.id.txt_setting);
        txtUserManual = (TextView)v.findViewById(R.id.txt_user_manual);
        txtAddDevice = (TextView)v.findViewById(R.id.txt_add_device);
        txtUserCallback = (TextView)v.findViewById(R.id.txt_user_callback);
        txtExit = (TextView) v.findViewById(R.id.txt_exit);
        TextView txtName = (TextView) v.findViewById(R.id.txt_name);
        if(TApplication.user != null){
            txtName.setText(TApplication.user.getUserName());
            if(TApplication.user.getHasAddDevice() == 1){
                txtAddDevice.setVisibility(View.VISIBLE);
            }else {
                txtAddDevice.setVisibility(View.GONE);
            }
        }else {
            txtName.setText("");
            txtAddDevice.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.txt_setting://设置
                intent.setClass(context, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_about_us://关于我们
                intent.setClass(context, AboutUsActivity.class);
                startActivity(intent);
                break;

            case R.id.txt_user_manual://用户手册
                intent.setClass(context, UserManualActivity.class);
                startActivity(intent);
                break;

            case R.id.txt_exit://退出登录
                TApplication.user = new User();
                AndShared.saveUser(new User());
                AndShared.setValue("login","0");
                //intent.setClass(context, NewsActivity.class);
                intent.setClass(context, LoginActivity.class);
                startActivity(intent);
                TApplication.mode = 1;
                getActivity().finish();
                break;
            case R.id.txt_add_device://添加设备
                intent.setClass(context, AddZZDActivity.class);
                startActivity(intent);
                break;

            case R.id.txt_user_callback://用户反馈
                intent.setClass(context, SystemSettingActivity.class);
                startActivity(intent);
                break;
        }
    }
}
