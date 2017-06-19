package com.hxsn.hf.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
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
import com.dh.DpsdkCore.fDPSDKStatusCallback;
import com.hxsn.hf.MainActivity;
import com.hxsn.hf.R;




public class TestDpsdkCoreActivity extends Activity {
    /** Called when the activity is first created. */
	
	Button m_btLogin;
	static IDpsdkCore dpsdkcore = new IDpsdkCore();
	Resources res;
	
	//标记是否第一次登入
	private String isfirstLogin;
 	static long m_loginHandle = 0;
 	static int m_nLastError = 0;
    static Return_Value_Info_t m_ReValue = new Return_Value_Info_t();
    protected ProgressDialog mProgressDialog;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    	Log.d("onCreate:",m_nLastError+"");
        int nType = 1;
        m_nLastError = IDpsdkCore.DPSDK_Create(nType, m_ReValue);
        
        IDpsdkCore.DPSDK_SetDPSDKStatusCallback(m_ReValue.nReturnValue, new fDPSDKStatusCallback() {
			
			@Override
			public void invoke(int nPDLLHandle, int nStatus) {
				Log.v("fDPSDKStatusCallback", "nStatus = " + nStatus);
			}
		});
   
    	Log.d("DpsdkCreate:",m_nLastError+"");
        
    	m_btLogin 			= (Button)findViewById(R.id.buttonLogin);
    	isfirstLogin = getSharedPreferences("LOGININFO", 0).getString("ISFIRSTLOGIN", "");
    	if(isfirstLogin.equals("false")){
//    		setEditTextContent();
    	}
    	
    	m_btLogin.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
			    showLoadingProgress("正在登录");
				new LoginTask().execute();
			}
    	});	
        
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
//	    	loginInfo.szIp 	= m_serverIp.getText().toString().getBytes();
	    	loginInfo.szIp 	= "60.191.94.119".getBytes();
//	    	String strPort 	= m_serverPort.getText().toString().trim();
	    	String strPort 	= "9000".trim();
	    	loginInfo.nPort = Integer.parseInt(strPort);
//	    	loginInfo.szUsername = m_serverUserName.getText().toString().getBytes();
	    	loginInfo.szUsername = "user".getBytes();
//	    	loginInfo.szPassword = m_serverPassword.getText().toString().getBytes();
	    	loginInfo.szPassword = "hxsn1234".getBytes();
	    	loginInfo.nProtocol = 2;
//	    	saveLoginInfo();
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
	    	} else {
		    	Log.d("DpsdkLogin failed:",result+"");
		    	Toast.makeText(getApplicationContext(), "login failed" + result, Toast.LENGTH_SHORT).show();
	    		m_loginHandle = 0;
	    		//jumpToContentListActivity();
	    	}
		}

    }
    /**
     * 取出 sharedpreference的登录信息并显示
     */
//    private void setEditTextContent(){
//    	SharedPreferences sp = getSharedPreferences("LOGININFO", 0);
//    	String content = sp.getString("INFO", "");
//    	String[] loginInfo = content.split(",");
//    	if(loginInfo != null){
//    		m_serverIp.setText(loginInfo[0]);
//        	m_serverPort.setText(loginInfo[1]);
//        	m_serverPassword.setText(loginInfo[2]);
//        	m_serverUserName.setText(loginInfo[3]);
//    	}
//    	Log.i("TestDpsdkCoreActivity", "setEditTextContent" + content);
//    }
//    private void saveLoginInfo(){
//    	SharedPreferences sp = getSharedPreferences("LOGININFO", 0);
//		Editor ed = sp.edit();
//		StringBuilder sb = new StringBuilder();
//		sb.append(m_serverIp.getText().toString()).append(",").append(m_serverPort.getText().toString()).append(",")
//			.append(m_serverPassword.getText().toString()).append(",").append(m_serverUserName.getText().toString());
//		ed.putString("INFO", sb.toString());
//		ed.putString("ISFIRSTLOGIN", "false");
//		ed.commit(); 	
//		Log.i("TestDpsdkCoreActivity", "saveLoginInfo" + sb.toString());
//    }
    

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
  
    public void jumpToItemListActivity()
    {
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		//intent.setClass(this, ItemListActivity.class);
		startActivityForResult(intent, 2);
    }
    
    public void Logout()
    {
    	if (m_loginHandle==0)
    	{
    		return;
    	}
    	int nRet = IDpsdkCore.DPSDK_Logout(m_ReValue.nReturnValue, 30000);
    	
    	if ( 0 == nRet )
    	{
    		m_loginHandle = 0;
    	}
    }
    
	@Override
	protected void onDestroy() 
	{   
		Logout();
		
		IDpsdkCore.DPSDK_Destroy(m_ReValue.nReturnValue);
		
		super.onDestroy();		
	}
}