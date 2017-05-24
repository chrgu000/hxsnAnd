package com.hxsn.ssk.fragment;

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
import com.hxsn.ssk.R;
import com.hxsn.ssk.TApplication;
import com.hxsn.ssk.activity.AboutUsActivity;
import com.hxsn.ssk.activity.LoginActivity;
import com.hxsn.ssk.activity.PasswordEditActivity;
import com.hxsn.ssk.activity.SettingActivity;
import com.hxsn.ssk.activity.VersionInfoActivity;
import com.hxsn.ssk.utils.Const;
import com.hxsn.ssk.utils.UpdateUtil;


/**
 * A simple {@link Fragment} subclass.  随时看
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */

@SuppressLint("ValidFragment")
public class Mine5Fragment extends Fragment implements View.OnClickListener {

    private Context context;
    private TextView txtPersonal, txtModifyPsw, txtHelp, txtExit, txtName, txtVersion;//txtSystemSetting;
    private RelativeLayout rl1;
    private String version;


    public Mine5Fragment() {
    }

    public Mine5Fragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine5, container, false);

        addView(view);
        addListener();
        return view;
    }

    private void addListener() {
        txtPersonal.setOnClickListener(this);
        txtModifyPsw.setOnClickListener(this);
        txtHelp.setOnClickListener(this);
        txtExit.setOnClickListener(this);
        //txtSystemSetting.setOnClickListener(this);

        if(TApplication.versionType != Const.RELEASE_VERTION){
            rl1.setOnClickListener(this);
        }

    }

    private void addView(View v) {
        rl1 = (RelativeLayout)v.findViewById(R.id.rl_1);
        txtPersonal = (TextView) v.findViewById(R.id.txt_personal_set);
        txtModifyPsw = (TextView) v.findViewById(R.id.txt_modify_psw);
        txtHelp = (TextView) v.findViewById(R.id.txt_help);
        txtExit = (TextView) v.findViewById(R.id.txt_exit);
        txtVersion = (TextView) v.findViewById(R.id.txt_version);
        version = "ssk_v" + UpdateUtil.getThisAppVersion(getActivity());
        //txtSystemSetting = (TextView)v.findViewById(R.id.txt_system_setting);
        switch (TApplication.versionType) {
            case Const.TEST_VERTION:
                version += "T";
                break;
            case Const.TEST_VERTION1:
                version += "T1";
                break;
            case Const.DEBUG_VERTION:
                version += "D";
                break;
            case Const.RELEASE_VERTION:
                version += "R";
                break;
        }
        txtVersion.setText(version);
        txtName = (TextView) v.findViewById(R.id.txt_name);
        txtName.setText(TApplication.user.getUserName());
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.txt_personal_set:
                intent.setClass(context, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_modify_psw:
                intent.setClass(context, PasswordEditActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_help:
                intent.setClass(context, AboutUsActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_exit:
                TApplication.user = new User();
                AndShared.clearUser();
                AndShared.setValue("login","0");
                //intent.setClass(context, NewsActivity.class);
                intent.setClass(context, LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.rl_1:
                intent.setClass(context, VersionInfoActivity.class);
                intent.putExtra("version",version);
                startActivity(intent);
                break;
           /* case R.id.txt_system_setting:
                intent.setClass(context, SystemSettingActivity.class);
                intent.putExtra("version",version);
                startActivity(intent);*/
        }
    }
}
