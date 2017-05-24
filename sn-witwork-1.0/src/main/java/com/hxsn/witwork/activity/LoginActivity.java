package com.hxsn.witwork.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hxsn.witwork.R;
import com.hxsn.witwork.ui.ClearEditText;
import com.hxsn.witwork.ui.LoadingDialog;
import com.hxsn.witwork.ui.Shared;
import com.hxsn.witwork.utils.Const;
import com.hxsn.witwork.utils.ImgBtnEffact;
import com.hxsn.witwork.utils.NetUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity {


    Button loginBtn;
    EditText loginPhoneET;
    ClearEditText loginPwdET;
    ImageButton loginScannerBtn;
    //NetworkStateReceiver myReceiver;
    String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }


    private void initView() {

        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginPhoneET = (EditText) findViewById(R.id.loginPhoneET);
        loginPwdET = (ClearEditText) findViewById(R.id.loginPwdET);
       /* loginScannerBtn = (ImageButton) findViewById(R.id.loginScannerBtn);
        loginScannerBtn.setOnTouchListener(ImgBtnEffact.btnTL);
        loginScannerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Intent inte = new Intent(LoginActivity.this,  CaptureActivity.class);
                startActivity(inte);
                finish();
            }
        });*/
        loginBtn.setOnTouchListener(ImgBtnEffact.btnTL);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String userName = loginPhoneET.getText().toString();
                String userPwd = loginPwdET.getText().toString();
                if (userName.equals("") || userPwd.equals("")) {
                    showToast("请将账号信息填写完善");

                } else {
                    request(userName, userPwd);

                }
            }
        });
        if (!NetUtils.isConnected(getApplication()))
            showToast("请检查网络连接！！");
    }


    public void request(final String userName, final String userPwd) {
        if (NetUtils.isConnected(getApplication())) {
            LoadingDialog.showLoading(this);
            try {
                Log.i("LoginActivity","url="+ Const.URL_LOGIN+ "logname=" + userName + "&logpwd=" + userPwd);
                StringRequest stringRequest = new StringRequest(Const.URL_LOGIN+ "logname=" + userName + "&logpwd=" + userPwd, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("LoginActivity","response="+ response);
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            String code = jsonObject.optString("code");
                            String result = jsonObject.getString("result");
                            Log.i("LoginActivity","code="+ code+",result="+result);
                            if (code.equals("200")) {
                                uid = jsonObject.getString("result");
                                Shared.setUserID(uid);
                                Shared.setUserName(userName);
                                Shared.setPassword(userPwd);
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                showToast("登录失败，返回码：" + jsonObject.getString("code"));
                            }
                            LoadingDialog.dissmissLoading();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        if (response != null)
                            switch (response.statusCode) {
                                case 408:// 超时

                                    break;

                                case 404://
                                    break;
                            }
                    }
                });
                mQueue.add(stringRequest);
            } catch (Exception w) {

            }
        } else {
            showToast("请检查网络连接！！");
        }
    }
}
