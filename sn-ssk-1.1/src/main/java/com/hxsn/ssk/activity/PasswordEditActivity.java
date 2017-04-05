package com.hxsn.ssk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.andbase.library.util.AbToastUtil;
import com.andbase.ssk.entity.User;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndShared;
import com.hxsn.ssk.R;
import com.hxsn.ssk.TApplication;
import com.hxsn.ssk.utils.Const;


public class PasswordEditActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imgBack;
    private EditText edtCurPsw, edtNewPsw, edtVerifyPsw;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_edit);

        addView();
        addListener();
    }

    private void addListener() {
        imgBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    private void addView() {
        edtCurPsw = (EditText) findViewById(R.id.edt_cur_psw);
        edtNewPsw = (EditText) findViewById(R.id.edt_new_psw);
        edtVerifyPsw = (EditText) findViewById(R.id.edt_verify_psw);
        imgBack = (ImageView) findViewById(R.id.img_left);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
    }

    @Override
    public void onClick(View v) {
        final Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.img_left:
                onBackPressed();
                break;
            case R.id.btn_submit:
                String curPsw = edtCurPsw.getText().toString();
                String newPsw = edtNewPsw.getText().toString();
                String verifyPsw = edtVerifyPsw.getText().toString();

                if (curPsw.length() != 0) {
                    if (newPsw.equals(verifyPsw)) {
                        String url = Const.URL_EDIT_PSW + TApplication.user.getUserId() + "&oldpwd=" + curPsw + "&newpwd=" + newPsw;
                        new AndHttpRequest(TApplication.context) {
                            @Override
                            public void getResponse(String response) {
                                AbToastUtil.showToast(PasswordEditActivity.this, "密码修改成功请重新登录");
                                TApplication.user = new User();
                                AndShared.saveUser(new User());
                                intent.setClass(PasswordEditActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                onBackPressed();
                            }
                        }.doPost(url);

                    } else {
                        AbToastUtil.showToast(this, "密码不一致");
                    }
                }
                break;
        }
    }
}
