package com.hxsn.hf.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.dh.DpsdkCore.IDpsdkCore;
import com.dh.DpsdkCore.Login_Info_t;
import com.dh.DpsdkCore.Return_Value_Info_t;
import com.hxsn.hf.MainActivity;
import com.hxsn.hf.R;
import com.hxsn.hf.base.BaseActivity;

public class LoginActivity extends BaseActivity {

	private Button loginBt;
	
 	static long m_loginHandle = 0;
 	static int m_nLastError = 0;
    static Return_Value_Info_t m_ReValue = new Return_Value_Info_t();
    protected ProgressDialog mProgressDialog;
    
    private static final String IP = "60.191.94.119";
    private static final String PORT = "9000";
    private static final String USERNAME = "user";
    private static final String PWD = "hxsn1234";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_login);
		init();
		initOnClick();
	}

	public void init() {
		loginBt = (Button) findViewById(R.id.login_bt);
	}

	public void initOnClick() {



		OnClickListener onClick = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.login_bt:
					showLoadingProgress("正在登陆");
					new LoginTask().execute();
					break;

				default:
					break;
				}
			}
		};

		loginBt.setOnClickListener(onClick);

	}
	
	protected void showLoadingProgress(String a) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
        } else {
            mProgressDialog = ProgressDialog.show(this, null, a);
            mProgressDialog.setCancelable(false);
        }
    }
	
	static public int getDpsdkHandle(){
    	if(m_loginHandle == 1)
    		return m_ReValue.nReturnValue;
    	else
    		return 0;
    }
	
	
	class LoginTask extends AsyncTask<Void, Integer, Integer>{

		@Override
		protected Integer doInBackground(Void... arg0) {               //在此处处理UI会导致异常
			if (m_loginHandle != 0) {
	    		IDpsdkCore.DPSDK_Logout(m_ReValue.nReturnValue, 30000);
        		m_loginHandle = 0;
	    	}
	    	Login_Info_t loginInfo = new Login_Info_t();
	    	Integer error = Integer.valueOf(0);
	    	loginInfo.szIp 	= IP.getBytes();
	    	String strPort 	= PORT.trim();
	    	loginInfo.nPort = Integer.parseInt(strPort);
	    	loginInfo.szUsername = USERNAME.getBytes();
	    	loginInfo.szPassword = PWD.getBytes();
	    	loginInfo.nProtocol = 2;
	    	saveLoginInfo();
	    	int nRet = IDpsdkCore.DPSDK_Login(m_ReValue.nReturnValue, loginInfo, 30000);
    		return nRet;
		}

		@Override
		protected void onPostExecute(Integer result) {
			
			super.onPostExecute(result);
			mProgressDialog.dismiss();   
    		if (result == 0) {
		    	Log.d("DpsdkLogin success:",result+"");
		    	IDpsdkCore.DPSDK_SetCompressType(m_ReValue.nReturnValue, 0);
	    		m_loginHandle = 1;
	    		jumpToItemListActivity();
	    		finish();
	    	} else {
		    	Log.d("DpsdkLogin failed:",result+"");
		    	Toast.makeText(getApplicationContext(), "login failed" + result, Toast.LENGTH_SHORT).show();
	    		m_loginHandle = 0;
	    		//jumpToContentListActivity();
	    	}
		}
    }
	
	private void saveLoginInfo(){
    	SharedPreferences sp = getSharedPreferences("LOGININFO", 0);
		Editor ed = sp.edit();
		StringBuilder sb = new StringBuilder();
		sb.append("60.191.94.119").append(",").append("9000").append(",")
			.append("hxsn1234").append(",").append("user");
		ed.putString("INFO", sb.toString());
		ed.putString("ISFIRSTLOGIN", "false");
		ed.commit(); 	
		Log.i("TestDpsdkCoreActivity", "saveLoginInfo" + sb.toString());
    }
	
	 public void jumpToItemListActivity() {
			Intent intent = new Intent();
			intent.setClass(this, MainActivity.class);
			//intent.setClass(this, ItemListActivity.class);
			startActivityForResult(intent, 2);
	 }
}
