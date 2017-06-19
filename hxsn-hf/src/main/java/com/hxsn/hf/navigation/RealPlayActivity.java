package com.hxsn.hf.navigation;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.company.PlaySDK.IPlaySDK;
import com.company.PlaySDK.IPlaySDKCallBack.pCallFunction;
import com.dh.DpsdkCore.Audio_Fun_Info_t;
import com.dh.DpsdkCore.Enc_Channel_Info_Ex_t;
import com.dh.DpsdkCore.Get_RealStream_Info_t;
import com.dh.DpsdkCore.Get_TalkStream_Info_t;
import com.dh.DpsdkCore.IDpsdkCore;
import com.dh.DpsdkCore.Return_Value_Info_t;
import com.dh.DpsdkCore.Send_Audio_Data_Info_t;
import com.dh.DpsdkCore.Talk_Sample_Rate_e;
import com.dh.DpsdkCore.dpsdk_audio_type_e;
import com.dh.DpsdkCore.dpsdk_talk_bits_e;
import com.dh.DpsdkCore.dpsdk_talk_type_e;
import com.dh.DpsdkCore.dpsdk_trans_type_e;
import com.dh.DpsdkCore.fDPSDKTalkParamCallback;
import com.dh.DpsdkCore.fMediaDataCallback;
import com.hxsn.hf.R;
import com.hxsn.hf.login.TestDpsdkCoreActivity;

@SuppressLint({ "ClickableViewAccessibility", "ShowToast" })
public class RealPlayActivity extends Activity{
	
	public final static String IMAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/snapshot/";
	public final static String IMGSTR = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
	
	private byte[] m_szCameraId_1 = null;
	private byte[] m_szCameraId_2 = null;
	private byte[] m_szCameraId_3 = null;
	private int m_pDLLHandle = 0;
	SurfaceView m_svPlayer = null;
	SurfaceView m_svPlayerq = null;
	SurfaceView m_svPlayerqp = null;
	private ImageView sp_duijiang;
	private ImageView shipindb;
	private int m_nPort = 0;
	private int m_nPortq = 1;
	private int m_nPortqp = 2;
	private int m_nSeq = 0;
	private int mTimeOut = 30*1000;
	
	private final static String SPNAME = "dahua_1";
	private final static String SPID = "1000000$1$0$0";
	
	private final static String QSPNAME = "PZB3MN088D00166_1";
	private final static String QSPID = "1000004$1$0$0";
	
	private boolean isOne = true;
	private boolean isOne_1 = true;
	private boolean isOne_2 = true;
	
	private LinearLayout lin1;
	private LinearLayout lin2;
	
	public static final int MSG_SHOW_DIALOG = 1;
    /** 打开声音提醒 */
    public static final int MSG_LIVE_OPEN_SOUND = 2;
    public static final int MSG_LIVE_CLOSE_SOUND = 3;
    /** 打开对讲提醒 */
    public static final int MSG_LIVE_OPEN_TALK = 5;
    public static final int MSG_LIVE_CLOSE_TALK = 6;
    /** 对讲成功消息 */
    public static final int MSG_LIVE_TALK_SUCCUSS = 7;
    /** 对讲失败消息 */
    public static final int MSG_LIVE_TALK_FAIL = 8;
    /** 对讲状态 */
    public static final int MSG_LIVE_TALK_STATUS = 9;
    /** 关闭对讲成功消息 */
    public static final int MSG_LIVE_STOP_TALK_SUCCUSS = 10;
    /** 关闭对讲失败消息 */
    public static final int MSG_LIVE_STOP_TALK_FAIL = 11;
    public static final int MSG_TOAST_OPEN_SOUND = 12;
    public static final int MSG_TOAST_CLOSE_SOUND = 13;
    // 超时时间
    private static final int DPSDK_CORE_DEFAULT_TIMEOUT = 30000;
    private int ret;
    private Audio_Fun_Info_t afInfo = null;
    // playsdk端口
    private int port = 0;
    private Send_Audio_Data_Info_t sadInfo = null;
    private Boolean ISTALKOPEN = false;
    private int m_pDLLHandle_sound;
    private Return_Value_Info_t rvi;
    
    private int mAudioType = dpsdk_audio_type_e.Talk_Coding_G711a;
    private int mSampleRate = Talk_Sample_Rate_e.Talk_Audio_Sam_8K;
    private int mTalkBits = dpsdk_talk_bits_e.Talk_Audio_Bits_16;
    private int callBackTag = 0;
    private Handler mHandler;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quanpingvideo);
		
		m_pDLLHandle = TestDpsdkCoreActivity.getDpsdkHandle();
		
		 // 查找控件
        findViews();
        //SurfaceHolder
        
        //SurfaceView点击事件
        initdianji();
        initSound();
        //单兵播放
//        bovideo();
//        //球播放
//        bovideoq();
        //全屏播放
      bovideoqp(SPID,QSPNAME);
        
	}
	

	private void findViews(){
		m_svPlayer = (SurfaceView)findViewById(R.id.sv_player);
		m_svPlayerq = (SurfaceView) findViewById(R.id.sv_playerq);
		m_svPlayerqp = (SurfaceView) findViewById(R.id.m_qpsvPlayer);
		sp_duijiang = (ImageView) findViewById(R.id.sp_duijiang);
		shipindb = (ImageView) findViewById(R.id.shipindb);
		shipindb.setVisibility(View.INVISIBLE);
		lin1 = (LinearLayout) findViewById(R.id.lin1);
		lin2 = (LinearLayout) findViewById(R.id.qp_parent);
		
	}
	
	private void bovideo(){
		SurfaceHolder holder = m_svPlayer.getHolder();
        holder.addCallback(new Callback() {
     	   	public void surfaceCreated(SurfaceHolder holder)
     		{
     	   		Log.d("xss", "surfaceCreated");
     	   	    IPlaySDK.InitSurface(m_nPort, m_svPlayer);
     		}
     		
     		public void surfaceChanged(SurfaceHolder holder, int format, int width,
     				int height)
     		{
     			Log.d("xss", "surfaceChanged");
     		}

     		public void surfaceDestroyed(SurfaceHolder holder)
     		{
     			Log.d("xss", "surfaceDestroyed");
     			holder.removeCallback(this);
     			StopRealPlay();
     		}
        });
		
		m_szCameraId_1 = SPID.getBytes();
		//int nRet;
		m_nPort = IPlaySDK.PLAYGetFreePort();
		
		final fMediaDataCallback fm = new fMediaDataCallback() {
			
			@Override
			public void invoke(int nPDLLHandle, int nSeq, int nMediaType,
					byte[] szNodeId, int nParamVal, byte[] szData, int nDataLen) {

				int ret = IPlaySDK.PLAYInputData(m_nPort, szData, nDataLen);
				if(ret == 1){
					Log.e("xss","playing success=" + nSeq + " package size=" + nDataLen);
				}else{
					Log.e("xss","playing failed=" + nSeq + " package size=" + nDataLen);
				}
			}
		};
		
		if(!StartRealPlay()){
			Log.e("xss", "StartRealPlay failed!");
			Toast.makeText(getApplicationContext(), "Open video failed!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		try{					
			Return_Value_Info_t retVal = new Return_Value_Info_t();

			Get_RealStream_Info_t getRealStreamInfo = new Get_RealStream_Info_t();
			//m_szCameraId = etCam.getText().toString().getBytes();
			
			System.arraycopy(m_szCameraId_1, 0, getRealStreamInfo.szCameraId, 0, m_szCameraId_1.length);
			//getRealStreamInfo.szCameraId = "1000096$1$0$0".getBytes();
			getRealStreamInfo.nMediaType = 1;
			getRealStreamInfo.nRight = 1;
			getRealStreamInfo.nStreamType = 1;
			getRealStreamInfo.nTransType = 1;
			Enc_Channel_Info_Ex_t ChannelInfo = new Enc_Channel_Info_Ex_t();
			IDpsdkCore.DPSDK_GetChannelInfoById(m_pDLLHandle, m_szCameraId_1, ChannelInfo);
			int ret = IDpsdkCore.DPSDK_GetRealStream(m_pDLLHandle, retVal, getRealStreamInfo, fm, mTimeOut);
			if(ret == 0){
				m_nSeq = retVal.nReturnValue;
				Log.e("xss DPSDK_GetRealStream success!",ret+"");
				Toast.makeText(getApplicationContext(), "Open video success!", Toast.LENGTH_SHORT).show();
			}else{
				StopRealPlay();
				Log.e("xss DPSDK_GetRealStream failed!",ret+""); 
				Toast.makeText(getApplicationContext(), "Open video failed!", Toast.LENGTH_SHORT).show();
			}
		}catch(Exception e){
			Log.e("xss", e.toString());
		}
	}
	
	private void bovideoq(){
		SurfaceHolder holderq = m_svPlayerq.getHolder();
        holderq.addCallback(new Callback() {
     	   	public void surfaceCreated(SurfaceHolder holder)
     		{
     	   		Log.d("xss", "surfaceCreated");
     	   	    IPlaySDK.InitSurface(m_nPortq, m_svPlayerq);
     		}
     		
     		public void surfaceChanged(SurfaceHolder holder, int format, int width,
     				int height)
     		{
     			Log.d("xss", "surfaceChanged");
     		}

     		public void surfaceDestroyed(SurfaceHolder holder)
     		{
     			Log.d("xss", "surfaceDestroyed");
     			holder.removeCallback(this);
     			StopRealPlayq();
     		}
        });
		
		m_szCameraId_2 = QSPID.getBytes();
//        Log.i("aaaa", getIntent().getStringExtra("channelId"));
		//int nRet;
		m_nPortq = IPlaySDK.PLAYGetFreePort();
		
		final fMediaDataCallback fm = new fMediaDataCallback() {
			
			@Override
			public void invoke(int nPDLLHandle, int nSeq, int nMediaType,
					byte[] szNodeId, int nParamVal, byte[] szData, int nDataLen) {

				int ret = IPlaySDK.PLAYInputData(m_nPortq, szData, nDataLen);
				if(ret == 1){
					Log.e("xss","playing success=" + nSeq + " package size=" + nDataLen);
				}else{
					Log.e("xss","playing failed=" + nSeq + " package size=" + nDataLen);
				}
			}
		};
		
		if(!StartRealPlayq()){
			Log.e("xss", "StartRealPlay failed!");
			Toast.makeText(getApplicationContext(), "Open video failed!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		try{					
			Return_Value_Info_t retVal = new Return_Value_Info_t();

			Get_RealStream_Info_t getRealStreamInfo = new Get_RealStream_Info_t();
			//m_szCameraId = etCam.getText().toString().getBytes();
			
			System.arraycopy(m_szCameraId_2, 0, getRealStreamInfo.szCameraId, 0, m_szCameraId_2.length);
			//getRealStreamInfo.szCameraId = "1000096$1$0$0".getBytes();
			getRealStreamInfo.nMediaType = 1;
			getRealStreamInfo.nRight = 1;
			getRealStreamInfo.nStreamType = 1;
			getRealStreamInfo.nTransType = 1;
			Enc_Channel_Info_Ex_t ChannelInfo = new Enc_Channel_Info_Ex_t();
			IDpsdkCore.DPSDK_GetChannelInfoById(m_pDLLHandle, m_szCameraId_2, ChannelInfo);
			int ret = IDpsdkCore.DPSDK_GetRealStream(m_pDLLHandle, retVal, getRealStreamInfo, fm, mTimeOut);
			if(ret == 0){
				m_nSeq = retVal.nReturnValue;
				Log.e("xss DPSDK_GetRealStream success!",ret+"");
				Toast.makeText(getApplicationContext(), "Open video success!", Toast.LENGTH_SHORT).show();
			}else{
				StopRealPlay();
				Log.e("xss DPSDK_GetRealStream failed!",ret+""); 
				Toast.makeText(getApplicationContext(), "Open video failed!", Toast.LENGTH_SHORT).show();
			}
		}catch(Exception e){
			Log.e("xss", e.toString());
		}
	}
	
	private void bovideoqp(String id,String name){
		SurfaceHolder holderqp = m_svPlayerqp.getHolder();
        holderqp.addCallback(new Callback() {
     	   	public void surfaceCreated(SurfaceHolder holder)
     		{
     	   		Log.d("xss", "surfaceCreated");
     	   	    IPlaySDK.InitSurface(m_nPortqp, m_svPlayerqp);
     		}
     		
     		public void surfaceChanged(SurfaceHolder holder, int format, int width,
     				int height)
     		{
     			Log.d("xss", "surfaceChanged");
     		}

     		public void surfaceDestroyed(SurfaceHolder holder)
     		{
     			Log.d("xss", "surfaceDestroyed");
     			holder.removeCallback(this);
     			StopRealPlayqp();
     		}
        });
        
		m_szCameraId_3 = id.getBytes();
		//int nRet;
		m_nPortqp = IPlaySDK.PLAYGetFreePort();
		
		final fMediaDataCallback fm = new fMediaDataCallback() {
			
			@Override
			public void invoke(int nPDLLHandle, int nSeq, int nMediaType,
					byte[] szNodeId, int nParamVal, byte[] szData, int nDataLen) {

				int ret = IPlaySDK.PLAYInputData(m_nPortqp, szData, nDataLen);
				if(ret == 1){
					Log.e("xss","playing success=" + nSeq + " package size=" + nDataLen);
				}else{
					Log.e("xss","playing failed=" + nSeq + " package size=" + nDataLen);
				}
			}
		};
		
		if(!StartRealPlayqp()){
			Log.e("xss", "StartRealPlay failed!");
			Toast.makeText(getApplicationContext(), "Open video failed!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		try{					
			Return_Value_Info_t retVal = new Return_Value_Info_t();

			Get_RealStream_Info_t getRealStreamInfo = new Get_RealStream_Info_t();
			//m_szCameraId = etCam.getText().toString().getBytes();
			
			System.arraycopy(m_szCameraId_3, 0, getRealStreamInfo.szCameraId, 0, m_szCameraId_3.length);
			//getRealStreamInfo.szCameraId = "1000096$1$0$0".getBytes();
			getRealStreamInfo.nMediaType = 1;
			getRealStreamInfo.nRight = 1;
			getRealStreamInfo.nStreamType = 1;
			getRealStreamInfo.nTransType = 1;
			Enc_Channel_Info_Ex_t ChannelInfo = new Enc_Channel_Info_Ex_t();
			IDpsdkCore.DPSDK_GetChannelInfoById(m_pDLLHandle, m_szCameraId_3, ChannelInfo);
			int ret = IDpsdkCore.DPSDK_GetRealStream(m_pDLLHandle, retVal, getRealStreamInfo, fm, mTimeOut);
			if(ret == 0){
				m_nSeq = retVal.nReturnValue;
				Log.e("xss DPSDK_GetRealStream success!",ret+"");
				Toast.makeText(getApplicationContext(), "Open video success!", Toast.LENGTH_SHORT).show();
			}else{
				StopRealPlay();
				Log.e("xss DPSDK_GetRealStream failed!",ret+""); 
				Toast.makeText(getApplicationContext(), "Open video failed!", Toast.LENGTH_SHORT).show();
			}
		}catch(Exception e){
			Log.e("xss", e.toString());
		}
	}
    
    private void initdianji(){
    	m_svPlayer.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (isOne) {
						isOne = false;
						run();
					} else {
						showOnce();
					}
				}
				return true;
			}
		});
    	
    	m_svPlayerq.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (isOne_1) {
						isOne_1 = false;
						run1();
					} else {
						showOnceq();
					}
				}
				return true;
			}
		});
    	
    	m_svPlayerqp.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (isOne_2) {
						isOne_2 = false;
						run2();
					} else {
						showOnceqp();
					}
				}
				return true;
			}
		});
    	
    	sp_duijiang.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ISTALKOPEN) {
					soundClose();
				} else {
					soundOpen();
				}
			}
		});
    }
    
    private void soundOpen() {
    	callBackTag = 0;
		ISTALKOPEN = true;
		sp_duijiang.setImageResource(R.drawable.microphone_1);
		    new Thread() {
			public void run()
			{
				startTalk();
				Log.i("thread", "run thread");
			}
		}.start();
    }
    
    private void soundClose() {
    	ISTALKOPEN = false;
    	sp_duijiang.setImageResource(R.drawable.microphone_0);
		sendMessage(mHandler, MSG_LIVE_CLOSE_TALK, 0, 0);
    }
    
    private void run() {
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(300);
					isOne = true;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
    }
    
    private void run1() {
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(300);
					isOne_1 = true;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
    }
    
    private void run2() {
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(300);
					isOne_2 = true;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
    }
    
    private void showOnce() {
    	Toast.makeText(this, "您双击了视频", 0).show();
    	
		int ret = IDpsdkCore.DPSDK_CloseRealStreamBySeq(m_pDLLHandle, m_nSeq, mTimeOut);
		if(ret == 0){
			Log.e("xss","DPSDK_CloseRealStreamByCameraId success!");
			Toast.makeText(getApplicationContext(), "Close video success!", Toast.LENGTH_SHORT).show();
		}else{
			Log.e("xss","DPSDK_CloseRealStreamByCameraId failed! ret = " + ret);
			Toast.makeText(getApplicationContext(), "Close video failed!", Toast.LENGTH_SHORT).show();
		}
		StopRealPlay();
		StopRealPlayq();
		StopRealPlayqp();
		
		lin1.setVisibility(View.INVISIBLE);
    	lin2.setVisibility(View.VISIBLE);
    	m_svPlayer.setVisibility(View.INVISIBLE);
		m_svPlayerq.setVisibility(View.INVISIBLE);
		m_svPlayerqp.setVisibility(View.VISIBLE);
//		shold();
		bovideoqp(SPID, SPNAME);
    }
    
    private void showOnceq() {
    	int ret = IDpsdkCore.DPSDK_CloseRealStreamBySeq(m_pDLLHandle, m_nSeq, mTimeOut);
		if(ret == 0){
			Log.e("xss","DPSDK_CloseRealStreamByCameraId success!");
			Toast.makeText(getApplicationContext(), "Close video success!", Toast.LENGTH_SHORT).show();
		}else{
			Log.e("xss","DPSDK_CloseRealStreamByCameraId failed! ret = " + ret);
			Toast.makeText(getApplicationContext(), "Close video failed!", Toast.LENGTH_SHORT).show();
		}
		StopRealPlay();
		StopRealPlayq();
		StopRealPlayqp();
		
		lin1.setVisibility(View.INVISIBLE);
    	lin2.setVisibility(View.VISIBLE);
    	m_svPlayer.setVisibility(View.INVISIBLE);
		m_svPlayerq.setVisibility(View.INVISIBLE);
		m_svPlayerqp.setVisibility(View.VISIBLE);
//		shold();
		bovideoqp(QSPID, QSPNAME);
    }
    
    private void showOnceqp() {
    	Toast.makeText(this, "您双击了视频", 0).show();
    	
		int ret = IDpsdkCore.DPSDK_CloseRealStreamBySeq(m_pDLLHandle, m_nSeq, mTimeOut);
		if(ret == 0){
			Log.e("xss","DPSDK_CloseRealStreamByCameraId success!");
			Toast.makeText(getApplicationContext(), "Close video success!", Toast.LENGTH_SHORT).show();
		}else{
			Log.e("xss","DPSDK_CloseRealStreamByCameraId failed! ret = " + ret);
			Toast.makeText(getApplicationContext(), "Close video failed!", Toast.LENGTH_SHORT).show();
		}
		StopRealPlay();
		StopRealPlayq();
		StopRealPlayqp();
		
		lin1.setVisibility(View.VISIBLE);
    	lin2.setVisibility(View.INVISIBLE);
    	m_svPlayer.setVisibility(View.VISIBLE);
		m_svPlayerq.setVisibility(View.VISIBLE);
		m_svPlayerqp.setVisibility(View.INVISIBLE);
//		shold();a
		bovideo();
		bovideoq();
    }
    
	public void StopRealPlay()
    {
    	try {
    		IPlaySDK.PLAYStopSoundShare(m_nPort);
    		IPlaySDK.PLAYStop(m_nPort);  		
    		IPlaySDK.PLAYCloseStream(m_nPort);
    		IPlaySDK.PLAYDestroyStream(m_nPort);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	public void StopRealPlayq()
    {
    	try {
    		IPlaySDK.PLAYStopSoundShare(m_nPortq);
    		IPlaySDK.PLAYStop(m_nPortq);  		
    		IPlaySDK.PLAYCloseStream(m_nPortq);
    		IPlaySDK.PLAYDestroyStream(m_nPortq);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	public void StopRealPlayqp()
    {
    	try {
    		IPlaySDK.PLAYStopSoundShare(m_nPortqp);
    		IPlaySDK.PLAYStop(m_nPortqp);  		
    		IPlaySDK.PLAYCloseStream(m_nPortqp);
    		IPlaySDK.PLAYDestroyStream(m_nPortqp);
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	public boolean StartRealPlay()
    { 
        if(m_svPlayer == null)
        {
        	Log.i("aaaa", "1");
        	return false;
        }
        boolean bOpenRet = IPlaySDK.PLAYOpenStream(m_nPort,null,0,1500*1024) == 0? false : true;
    	if(bOpenRet)
		{
    		Log.i("aaaa", "2");
			boolean bPlayRet = IPlaySDK.PLAYPlay(m_nPort, m_svPlayer) == 0 ? false : true;
			Log.i("StartRealPlay", "StartRealPlay1");
			if(bPlayRet)
			{
				Log.i("aaaa", "3");
				boolean bSuccess = IPlaySDK.PLAYPlaySoundShare(m_nPort) == 0 ? false : true;

				Log.i("StartRealPlay", "StartRealPlay2");
				if(!bSuccess)
				{
					Log.i("aaaa", "4");
					IPlaySDK.PLAYStop(m_nPort);
					IPlaySDK.PLAYCloseStream(m_nPort);
					Log.i("StartRealPlay", "StartRealPlay3");
					return false;
				}
			}
			else
			{
				Log.i("aaaa", "5");
				IPlaySDK.PLAYCloseStream(m_nPort);
				Log.i("StartRealPlay", "StartRealPlay4");
				return false;
			}
		}
    	else
    	{
    		Log.i("StartRealPlay", "StartRealPlay5");
    		return false;
    	}
        
        return true;
    }
	
	public boolean StartRealPlayq()
    { 
        if(m_svPlayerq == null)
        {
        	Log.i("aaaa", "1");
        	return false;
        }
        boolean bOpenRet = IPlaySDK.PLAYOpenStream(m_nPortq,null,0,1500*1024) == 0? false : true;
    	if(bOpenRet)
		{
    		Log.i("aaaa", "2");
			boolean bPlayRet = IPlaySDK.PLAYPlay(m_nPortq, m_svPlayerq) == 0 ? false : true;
			Log.i("StartRealPlay", "StartRealPlay1");
			if(bPlayRet)
			{
				Log.i("aaaa", "3");
				boolean bSuccess = IPlaySDK.PLAYPlaySoundShare(m_nPortq) == 0 ? false : true;

				Log.i("StartRealPlay", "StartRealPlay2");
				if(!bSuccess)
				{
					Log.i("aaaa", "4");
					IPlaySDK.PLAYStop(m_nPortq);
					IPlaySDK.PLAYCloseStream(m_nPortq);
					Log.i("StartRealPlay", "StartRealPlay3");
					return false;
				}
			}
			else
			{
				Log.i("aaaa", "5");
				IPlaySDK.PLAYCloseStream(m_nPortq);
				Log.i("StartRealPlay", "StartRealPlay4");
				return false;
			}
		}
    	else
    	{
    		Log.i("StartRealPlay", "StartRealPlay5");
    		return false;
    	}
        
        return true;
    }
	
	public boolean StartRealPlayqp()
    { 
        if(m_svPlayerqp == null)
        {
        	Log.i("aaaa", "1");
        	return false;
        }
        boolean bOpenRet = IPlaySDK.PLAYOpenStream(m_nPortqp,null,0,1500*1024) == 0? false : true;
    	if(bOpenRet)
		{
    		Log.i("aaaa", "2");
			boolean bPlayRet = IPlaySDK.PLAYPlay(m_nPortqp, m_svPlayerqp) == 0 ? false : true;
			Log.i("StartRealPlay", "StartRealPlay1");
			if(bPlayRet)
			{
				Log.i("aaaa", "3");
				boolean bSuccess = IPlaySDK.PLAYPlaySoundShare(m_nPortqp) == 0 ? false : true;

				Log.i("StartRealPlay", "StartRealPlay2");
				if(!bSuccess)
				{
					Log.i("aaaa", "4");
					IPlaySDK.PLAYStop(m_nPortqp);
					IPlaySDK.PLAYCloseStream(m_nPortqp);
					Log.i("StartRealPlay", "StartRealPlay3");
					return false;
				}
			}
			else
			{
				Log.i("aaaa", "5");
				IPlaySDK.PLAYCloseStream(m_nPortqp);
				Log.i("StartRealPlay", "StartRealPlay4");
				return false;
			}
		}
    	else
    	{
    		Log.i("StartRealPlay", "StartRealPlay5");
    		return false;
    	}
        
        return true;
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		int ret = IDpsdkCore.DPSDK_CloseRealStreamBySeq(m_pDLLHandle, m_nSeq, mTimeOut);
		if(ret == 0){
			Log.e("xss","DPSDK_CloseRealStreamByCameraId success!");
			Toast.makeText(getApplicationContext(), "Close video success!", Toast.LENGTH_SHORT).show();
		}else{
			Log.e("xss","DPSDK_CloseRealStreamByCameraId failed! ret = " + ret);
			Toast.makeText(getApplicationContext(), "Close video failed!", Toast.LENGTH_SHORT).show();
		}
		StopRealPlay();
	}

	private void initSound() {
		Handler.Callback callback = new Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_TOAST_OPEN_SOUND:
					Toast.makeText(RealPlayActivity.this, "open sound", Toast.LENGTH_SHORT).show();
					break;
				case MSG_TOAST_CLOSE_SOUND:
					Toast.makeText(RealPlayActivity.this, "stop sound", Toast.LENGTH_SHORT).show();
					break;
				case MSG_LIVE_OPEN_SOUND:
					//若对讲是打开的，关闭对讲，打开声音
					//closeTalk();
					playSound(true);
					break;
				case MSG_LIVE_CLOSE_SOUND:	
					playSound(false);
					break;
				case MSG_LIVE_OPEN_TALK:
					//关闭声音，开始对讲，打开语音对讲等待框
					//playSound(false);
					//showProgressDialog();
					startTalk();
					break;
				case MSG_LIVE_CLOSE_TALK:
					closeTalk();
				case MSG_LIVE_TALK_SUCCUSS:
					Toast.makeText(RealPlayActivity.this, "打开对讲成功", Toast.LENGTH_SHORT).show();
					break;
				case MSG_LIVE_TALK_FAIL:
		             Toast.makeText(RealPlayActivity.this, "打开对讲失败" + ",errCode:" + msg.arg1, Toast.LENGTH_SHORT).show();
					break;
				case MSG_LIVE_STOP_TALK_SUCCUSS:
					Toast.makeText(RealPlayActivity.this, "关闭对讲成功", Toast.LENGTH_SHORT).show();
					break;
				case MSG_LIVE_STOP_TALK_FAIL:
					Toast.makeText(RealPlayActivity.this, "关闭对讲失败" + ",errCode:" + msg.arg1, Toast.LENGTH_SHORT).show();
					break;
				}

				return false;
			}
		};
		mHandler = new Handler(getMainLooper(), callback);
		
		m_pDLLHandle_sound = TestDpsdkCoreActivity.getDpsdkHandle();
		IDpsdkCore.DPSDK_SetDPSDKTalkParamCallback(m_pDLLHandle_sound, fdpsdkCallback);
	}
	
private void startTalk() {
		
		// 码流请求信息
        Get_TalkStream_Info_t gti = new Get_TalkStream_Info_t();
        gti.nAudioType = mAudioType;
        gti.nBitsType = mTalkBits;
        gti.nSampleType = mSampleRate;

        gti.nTalkType = dpsdk_talk_type_e.Talk_Type_Device;
        gti.nTransType = dpsdk_trans_type_e.DPSDK_CORE_TRANSTYPE_TCP;
        
        gti.szCameraId = "1000000".getBytes();  //设备ID
        // 码流请求序号,可作为后续操作标识
        rvi = new Return_Value_Info_t();
        int ret = IDpsdkCore.DPSDK_GetTalkStream(m_pDLLHandle_sound, rvi, gti, fmdCallback,
                DPSDK_CORE_DEFAULT_TIMEOUT);
        Log.e("startTalk", "rvi.nReturnValue=" + rvi.nReturnValue);

        if (ret != 0 ) {
        	return;
        } else if (ret == 0) {
    	   sendMessage(mHandler, MSG_LIVE_TALK_SUCCUSS, 0, 0);
           playSound(true);
    	}
        /************** 手机采集数据发送给设备 ****************/
        // 获取语音采集回调信息
        afInfo = new Audio_Fun_Info_t();
        ret = IDpsdkCore.DPSDK_GetSdkAudioCallbackInfo(m_pDLLHandle_sound, afInfo);
        if (ret != 0) {

        }

        // 打开音频采集功能
        ret = IPlaySDK.PLAYOpenAudioRecord(fun, dpsdk_talk_bits_e.Talk_Audio_Bits_16,
                Talk_Sample_Rate_e.Talk_Audio_Sam_8K, 1024, 0);
        if (ret != 1) {

        }
	}

private void closeTalk() {
    // 停止数据采集
	playSound(false);
    IPlaySDK.PLAYCloseAudioRecord();
    // 根据通道号关闭对讲
    if ( rvi != null ) {
    	  ret = IDpsdkCore.DPSDK_CloseTalkStreamBySeq(m_pDLLHandle_sound, rvi.nReturnValue,
                  DPSDK_CORE_DEFAULT_TIMEOUT);
          Log.e("stopTalk", "rvi.nReturnValue=" + rvi.nReturnValue);
    }
  //2807281471
    if (ret != 0) {
        Log.e("stopTalk", "ret=" + ret);
        sendMessage(mHandler, MSG_LIVE_STOP_TALK_FAIL, ret, 0);
        return;
    }
    sendMessage(mHandler, MSG_LIVE_STOP_TALK_SUCCUSS, 0, 0);
	ISTALKOPEN = false;
}

fDPSDKTalkParamCallback fdpsdkCallback = new fDPSDKTalkParamCallback() {
    @Override
    public void invoke(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
            mAudioType = arg1;
            mSampleRate = arg4;
            mTalkBits = arg3;
            if (callBackTag < 1) {
            	new Thread() {
					public void run()
					{
						startTalk();
						Log.i("thread", "run thread");
					}
				}.start();	
            } else {
            	sendMessage(mHandler, MSG_LIVE_TALK_FAIL, 0, 0);
            	Log.i("fdpsdkCallback", "start talk failed");
            }
            ++callBackTag;
            Log.i("fDPSDKTalkParamCallback", "callBackTag = " + callBackTag);
    }
};

private void sendMessage(Handler mHandler, int msg, int org1,
		int org2) {
	    Message Msg = mHandler.obtainMessage();
        Msg.what = msg;
        Msg.arg1 = org1;
        Msg.arg2 = org2;
        Msg.sendToTarget();
}

fMediaDataCallback fmdCallback = new fMediaDataCallback() {
    @Override
    public void invoke(int nPDLLHandle, int nSeq, int nMediaType, byte[] szNodeId, int nParamVal, byte[] szData,
            int nDataLen) {
        // TODO 接收对讲的语音数据
        IPlaySDK.PLAYInputData(port, szData, nDataLen);
    }
};

pCallFunction fun = new pCallFunction() {
    @Override
    public void invoke(byte[] arg0, int arg1, long arg2) {

        sadInfo = new Send_Audio_Data_Info_t(arg1);
        sadInfo.pData = arg0;
        sadInfo.nLen = arg1;
        sadInfo.nAudioType = dpsdk_audio_type_e.Talk_Coding_PCM;
        sadInfo.nTalkBits = dpsdk_talk_bits_e.Talk_Audio_Bits_16;
        sadInfo.nSampleRate = Talk_Sample_Rate_e.Talk_Audio_Sam_8K;
        sadInfo.pCallBackFun = afInfo.pCallBackFun;
        sadInfo.pUserParam = afInfo.pUserParam;
        IDpsdkCore.DPSDK_SendAudioData(m_pDLLHandle_sound, sadInfo);

    }
};

private void playSound (boolean isPlaySound) {
    if (isPlaySound) {
        port = IPlaySDK.PLAYGetFreePort();
        if (port == -1) {
            return;
        }
        // open stream
        IPlaySDK.PLAYOpenStream(port, null, 0, 1024 * 1024);
        int ret = IPlaySDK.PLAYPlay(port, null);
        if (ret == 0) {
            // release port
            IPlaySDK.PLAYReleasePort(port);
            port = -1;
            return;
        }
    	IPlaySDK.PLAYPlaySound(port);
    	sendMessage(mHandler, MSG_TOAST_OPEN_SOUND, 0, 0);
    } else {
    	IPlaySDK.PLAYStopSound();
    	sendMessage(mHandler, MSG_TOAST_CLOSE_SOUND, 0, 0);
    }
}
}
