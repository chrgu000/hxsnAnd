package com.hxsn.jwb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.andbase.library.http.model.AbRequestParams;
import com.andbase.ssk.entity.User;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.AndShared;
import com.hxsn.jwb.R;
import com.hxsn.jwb.TApplication;
import com.hxsn.jwb.utils.Const;
import com.hxsn.jwb.utils.Shared;
import com.hxsn.jwb.utils.UpdateUtil;


public class LoginActivity extends Activity implements View.OnClickListener {

    private Button btnLogin;
    private EditText edtUsername, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        addView();
        addListener();
    }

    private void addView() {
        btnLogin = (Button) findViewById(R.id.btn_login);
        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        if (TApplication.versionType != Const.RELEASE_VERTION) {
            edtUsername.setText("shouji");
            edtPassword.setText("123456");
        } else {
            String username = Shared.getValue("username");
            edtUsername.setText(username);//显示上次登录过的用户名
        }

    }

    private void addListener() {
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                String name = edtUsername.getText().toString();
                String psw = edtPassword.getText().toString();
                if (name.length() == 0) {
                    UpdateUtil.show(this, "用户名不能为空");
                    break;
                }
                if (psw.length() == 0) {
                    UpdateUtil.show(this, "密码不能为空");
                    break;
                }
                request(name, psw);
                break;
        }
    }

    private void request(String name, final String psw) {
        //LoadingDialog.showLoading(this);
        String channelId = Shared.getValue(Const.CODE_CHANNEL_ID);
        String url = Const.URL_LOGIN;// + name + "&logpwd=" + psw + "&chid=" + channelId + "&devtype=3";
        AbRequestParams params = new AbRequestParams();
        params.put("logname",name);
        params.put("logpwd",psw);
        params.put("chid",channelId);
        params.put("devtype","3");
        params.put("appType","jwb");
        new AndHttpRequest(this) {
            @Override
            public void getResponse(String response) {
                String code = AndJsonUtils.getCode(response);
                if(code.equals("200")){
                    User user = AndJsonUtils.getUser(response);
                    TApplication.user = user;
                    if (user != null) {
                        user.setPassword(psw);
                        user.setUserRole("");
                        AndShared.saveUser(user);
                        AndShared.setValue("login","1");
                        startHomeActivity();
                    }
                }else if(code.equals("301")){
                    UpdateUtil.show(LoginActivity.this,"用户名不存在或密码错误");
                }else if(code.equals("302")){
                    UpdateUtil.show(LoginActivity.this,"该账号被管理员停用");
                } else {
                    UpdateUtil.show(LoginActivity.this,"登录错误");
                }
            }
        }.doPost(url,params);
    }

    public void startHomeActivity(){
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        TApplication.exit();
    }
}
