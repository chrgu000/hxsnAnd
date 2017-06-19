package com.hxsn.iot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hxsn.iot.R;
import com.hxsn.iot.TApplication;


/**
 * 单项个人设置
 */
public class PersonalSettingActivity extends Activity implements View.OnClickListener {

    private RelativeLayout  rlRealName,rlPhone, rlEmail, rlAddress;
    private TextView  txtRealname, txtEmail, txtPhone, txtAddress,txtEditPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_setting);

        TApplication.activities.add(this);

        addView();
        addListener();
    }

    private void addListener() {

        String realName = TApplication.user.getRealName();
        if(realName.length() == 0){//如果真实姓名已填写则不能修改
            rlRealName.setOnClickListener(this);
        }
        rlPhone.setOnClickListener(this);
        rlAddress.setOnClickListener(this);
        rlEmail.setOnClickListener(this);

        txtEditPassword.setOnClickListener(this);
    }

    private void addView() {

        rlRealName = (RelativeLayout) findViewById(R.id.rl_real_name);
        rlPhone = (RelativeLayout) findViewById(R.id.rl_phone);
        rlEmail = (RelativeLayout) findViewById(R.id.rl_email);
        rlAddress = (RelativeLayout) findViewById(R.id.rl_address);
        txtEditPassword = (TextView) findViewById(R.id.txt_edit_password);
        TextView txtUsername = (TextView) findViewById(R.id.txt_username);
        txtAddress = (TextView) findViewById(R.id.txt_address);
        txtEmail = (TextView) findViewById(R.id.txt_email);
        txtPhone = (TextView) findViewById(R.id.txt_phone);
        txtRealname = (TextView) findViewById(R.id.txt_realname);

        txtUsername.setText(TApplication.user.getUserName());
        txtRealname.setText(TApplication.user.getRealName());
        txtPhone.setText(TApplication.user.getPhone());

        txtEmail.setText(TApplication.user.getEmail());
        txtAddress.setText(TApplication.user.getAddress());

        ImageView imgBack = (ImageView) findViewById(R.id.img_left);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
        TextView txtTitle = (TextView) findViewById(R.id.txt_middle);
        txtTitle.setText("个人设置");
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(this, SetTextActivity.class);

        switch (v.getId()) {
            case R.id.rl_phone://电话
                intent.putExtra("mode",4);
                startActivityForResult(intent, 4);
                break;
            case R.id.rl_email://邮箱
                intent.putExtra("mode",5);
                startActivityForResult(intent, 5);
                break;
            case R.id.rl_address://地址
                intent.putExtra("mode",6);
                startActivityForResult(intent, 6);
                break;
            case R.id.txt_edit_password://密码修改
                intent.setClass(this, PasswordEditActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    /**
     * 从SetTextActivity 返回来的处理
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            String stringName = data.getStringExtra("stringName");
            switch (requestCode) {
                case 4://电话
                    txtPhone.setText(stringName);
                    break;
                case 5://邮箱
                    txtEmail.setText(stringName);
                    break;
                case 6://地址
                    txtAddress.setText(stringName);
                    break;
            }
        }
    }
}
