package com.hxsn.hf.navigation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
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
import com.dh.DpsdkCore.Login_Info_t;
import com.dh.DpsdkCore.Return_Value_Info_t;
import com.dh.DpsdkCore.Send_Audio_Data_Info_t;
import com.dh.DpsdkCore.Talk_Sample_Rate_e;
import com.dh.DpsdkCore.dpsdk_audio_type_e;
import com.dh.DpsdkCore.dpsdk_talk_bits_e;
import com.dh.DpsdkCore.dpsdk_talk_type_e;
import com.dh.DpsdkCore.dpsdk_trans_type_e;
import com.dh.DpsdkCore.fDPSDKStatusCallback;
import com.dh.DpsdkCore.fDPSDKTalkParamCallback;
import com.dh.DpsdkCore.fMediaDataCallback;
import com.hxsn.hf.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

@SuppressLint({ "ClickableViewAccessibility", "HandlerLeak" })
public class NewRealPlayActivity extends Activity {

	private final static String SPNAME = "dahua_1";
	private final static String SPID = "1000000$1$0$0";

	private final static String QSPNAME = "PZB3MN088D00166_1";
	private final static String QSPID = "1000004$1$0$0";

	private SurfaceView real_play_new_sv_fill;
	private SurfaceView real_play_new_four_ball;
	private SurfaceView real_play_new_four_move;
	private LinearLayout real_play_new_four_parent;
	private ImageView real_play_new_four_microphone;
	private ImageView real_play_new_four_move_img;

	private boolean isOne = true;
	private boolean isOne_1 = true;
	private boolean isOne_2 = true;

	private boolean isBallFill = false;
	private boolean isMoveFill = false;

	private byte[] m_szCameraId = null;
	private int m_pDLLHandle = 0;
	private int m_nPort = 0;
	private int m_nSeq = 0;
	private int mTimeOut = 30 * 1000;

	private byte[] m_szCameraId_ball = null;
	private int m_nPort_ball = 0;
	private int m_nSeq_ball = 0;

	private byte[] m_szCameraId_move = null;
	private int m_nPort_move = 0;
	private int m_nSeq_move = 0;

	static IDpsdkCore dpsdkcore = new IDpsdkCore();
	static long m_loginHandle = 0;
	static int m_nLastError = 0;
	static Return_Value_Info_t m_ReValue = new Return_Value_Info_t();

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
	private Return_Value_Info_t rvi;

	private int mAudioType = dpsdk_audio_type_e.Talk_Coding_G711a;
	private int mSampleRate = Talk_Sample_Rate_e.Talk_Audio_Sam_8K;
	private int mTalkBits = dpsdk_talk_bits_e.Talk_Audio_Bits_16;
	private int callBackTag = 0;
	private Handler mHandler;
	
	private boolean isRun = true;
	private boolean isShowMoveFour = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_real_play_new);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		init();
		initView();
		GetNetWork();
		initListener();
		initSound();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (isBallFill) {
			closeBall();
		} else if (isMoveFill) {
			closeMove();
		} else {
			closeBallFour();
			closeMoveFour();
		}
		IDpsdkCore.DPSDK_Logout(m_ReValue.nReturnValue, 30000);
		IDpsdkCore.DPSDK_Destroy(m_ReValue.nReturnValue);
	}

	private void init() {
		m_nLastError = IDpsdkCore.DPSDK_Create(1, m_ReValue);

		IDpsdkCore.DPSDK_SetDPSDKStatusCallback(m_ReValue.nReturnValue,
				new fDPSDKStatusCallback() {

					@Override
					public void invoke(int nPDLLHandle, int nStatus) {
						Log.v("fDPSDKStatusCallback", "nStatus = " + nStatus);
					}
				});

		Login_Info_t loginInfo = new Login_Info_t();
		Integer error = Integer.valueOf(0);
		loginInfo.szIp = "60.191.94.119".getBytes();
		String strPort = "9000".trim();
		loginInfo.nPort = Integer.parseInt(strPort);
		loginInfo.szUsername = "user".getBytes();
		loginInfo.szPassword = "hxsn1234".getBytes();
		loginInfo.nProtocol = 2;
		IDpsdkCore.DPSDK_Login(m_ReValue.nReturnValue, loginInfo, 30000);
		IDpsdkCore.DPSDK_SetCompressType(m_ReValue.nReturnValue, 0);
		m_pDLLHandle = m_ReValue.nReturnValue;
	}

	private void initView() {
		real_play_new_sv_fill = (SurfaceView) findViewById(R.id.real_play_new_sv_fill);
		real_play_new_four_ball = (SurfaceView) findViewById(R.id.real_play_new_four_ball);
		real_play_new_four_move = (SurfaceView) findViewById(R.id.real_play_new_four_move);
		real_play_new_four_parent = (LinearLayout) findViewById(R.id.real_play_new_four_parent);
		real_play_new_four_microphone = (ImageView) findViewById(R.id.real_play_new_four_microphone);
		real_play_new_four_move_img = (ImageView) findViewById(R.id.real_play_new_four_move_img);

		real_play_new_four_move_img.setVisibility(View.VISIBLE);
		isBallFill = true;
		showBall();
	}

	private void initListener() {
		real_play_new_sv_fill.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isOne) {
					isOne = false;
					run();
				} else {
					if (isBallFill) {
						closeBall();
					}
					if (isMoveFill) {
						closeMove();
					}
					showFour();
				}
			}
		});

		real_play_new_four_ball.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isOne_1) {
					isOne_1 = false;
					run1();
				} else {
					closeBallFour();
					sleep();
					closeMoveFour();
					sleep();

					isBallFill = true;
					showBall();
				}
			}
		});

		real_play_new_four_move.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isShowMoveFour) {
					if (isOne_2) {
						isOne_2 = false;
						run2();
					} else {
						closeBallFour();
						sleep();
						closeMoveFour();
						sleep();
						isMoveFill = true;
						showMove();
					}
				}
			}
		});

		real_play_new_four_microphone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isShowMoveFour) {
					if (ISTALKOPEN) {
						soundClose();
					} else {
						soundOpen();
					}
				}
			}
		});
	}
	
	private void initSound() {
		Handler.Callback callback = new Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_TOAST_OPEN_SOUND:
					break;
				case MSG_TOAST_CLOSE_SOUND:
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
					Toast.makeText(NewRealPlayActivity.this, "打开对讲成功", Toast.LENGTH_SHORT).show();
					break;
				case MSG_LIVE_TALK_FAIL:
		             Toast.makeText(NewRealPlayActivity.this, "打开对讲失败" + ",errCode:" + msg.arg1, Toast.LENGTH_SHORT).show();
					break;
				case MSG_LIVE_STOP_TALK_SUCCUSS:
					Toast.makeText(NewRealPlayActivity.this, "关闭对讲成功", Toast.LENGTH_SHORT).show();
					break;
				case MSG_LIVE_STOP_TALK_FAIL:
					Toast.makeText(NewRealPlayActivity.this, "关闭对讲失败" + ",errCode:" + msg.arg1, Toast.LENGTH_SHORT).show();
					break;
				}

				return false;
			}
		};
		mHandler = new Handler(getMainLooper(), callback);
		
		IDpsdkCore.DPSDK_SetDPSDKTalkParamCallback(m_pDLLHandle, fdpsdkCallback);
	}

	private void soundOpen() {
		callBackTag = 0;
		ISTALKOPEN = true;
		real_play_new_four_microphone.setImageResource(R.drawable.microphone_1);
		new Thread() {
			public void run() {
				startTalk();
				Log.i("thread", "run thread");
			}
		}.start();
	}

	private void soundClose() {
		ISTALKOPEN = false;
		real_play_new_four_microphone.setImageResource(R.drawable.microphone_0);
		sendMessage(mHandler, MSG_LIVE_CLOSE_TALK, 0, 0);
	}

	private void startTalk() {

		// 码流请求信息
		Get_TalkStream_Info_t gti = new Get_TalkStream_Info_t();
		gti.nAudioType = mAudioType;
		gti.nBitsType = mTalkBits;
		gti.nSampleType = mSampleRate;

		gti.nTalkType = dpsdk_talk_type_e.Talk_Type_Device;
		gti.nTransType = dpsdk_trans_type_e.DPSDK_CORE_TRANSTYPE_TCP;

		gti.szCameraId = "1000000".getBytes(); // 设备ID
		// 码流请求序号,可作为后续操作标识
		rvi = new Return_Value_Info_t();
		int ret = IDpsdkCore.DPSDK_GetTalkStream(m_pDLLHandle, rvi, gti,
				fmdCallback, DPSDK_CORE_DEFAULT_TIMEOUT);
		Log.e("startTalk", "rvi.nReturnValue=" + rvi.nReturnValue);

		if (ret != 0) {
			return;
		} else if (ret == 0) {
			sendMessage(mHandler, MSG_LIVE_TALK_SUCCUSS, 0, 0);
			playSound(true);
		}
		/************** 手机采集数据发送给设备 ****************/
		// 获取语音采集回调信息
		afInfo = new Audio_Fun_Info_t();
		ret = IDpsdkCore.DPSDK_GetSdkAudioCallbackInfo(m_pDLLHandle,
				afInfo);
		if (ret != 0) {

		}

		// 打开音频采集功能
		ret = IPlaySDK.PLAYOpenAudioRecord(fun,
				dpsdk_talk_bits_e.Talk_Audio_Bits_16,
				Talk_Sample_Rate_e.Talk_Audio_Sam_8K, 1024, 0);
		if (ret != 1) {

		}
	}

	private void closeTalk() {
		// 停止数据采集
		playSound(false);
		IPlaySDK.PLAYCloseAudioRecord();
		// 根据通道号关闭对讲
		if (rvi != null) {
			ret = IDpsdkCore.DPSDK_CloseTalkStreamBySeq(m_pDLLHandle,
					rvi.nReturnValue, DPSDK_CORE_DEFAULT_TIMEOUT);
			Log.e("stopTalk", "rvi.nReturnValue=" + rvi.nReturnValue);
		}
		// 2807281471
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
		public void invoke(int arg0, int arg1, int arg2, int arg3, int arg4,
				int arg5) {
			mAudioType = arg1;
			mSampleRate = arg4;
			mTalkBits = arg3;
			if (callBackTag < 1) {
				new Thread() {
					public void run() {
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

	private void sendMessage(Handler mHandler, int msg, int org1, int org2) {
		Message Msg = mHandler.obtainMessage();
		Msg.what = msg;
		Msg.arg1 = org1;
		Msg.arg2 = org2;
		Msg.sendToTarget();
	}

	fMediaDataCallback fmdCallback = new fMediaDataCallback() {
		@Override
		public void invoke(int nPDLLHandle, int nSeq, int nMediaType,
				byte[] szNodeId, int nParamVal, byte[] szData, int nDataLen) {

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
			IDpsdkCore.DPSDK_SendAudioData(m_pDLLHandle, sadInfo);

		}
	};

	private void playSound(boolean isPlaySound) {
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

	private void run() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(300);
					isOne = true;
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
		}).start();
	}

	private void run1() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(300);
					isOne_1 = true;
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
		}).start();
	}

	private void run2() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(300);
					isOne_2 = true;
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
		}).start();
	}

	private void showFour() {

		changeLayout(true);
		if (isShowMoveFour) {
			real_play_new_four_move_img.setVisibility(View.GONE);
			showMoveFour();
		} else {
			real_play_new_four_move_img.setVisibility(View.VISIBLE);
		}
		showBallFour();
	}

	private void showBall() {
		changeLayout(false);
		m_szCameraId = QSPID.getBytes();
		m_nPort = IPlaySDK.PLAYGetFreePort();
		SurfaceHolder holder = real_play_new_sv_fill.getHolder();
		holder.addCallback(new Callback() {

			public void surfaceCreated(SurfaceHolder holder) {
				Log.d("zxw", "surfaceCreated");
				IPlaySDK.InitSurface(m_nPort, real_play_new_sv_fill);
			}

			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				Log.d("zxw", "surfaceChanged");
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				Log.d("zxw", "surfaceDestroyed");
				holder.removeCallback(this);
			}
		});

		final fMediaDataCallback fm = new fMediaDataCallback() {

			@Override
			public void invoke(int nPDLLHandle, int nSeq, int nMediaType,
					byte[] szNodeId, int nParamVal, byte[] szData, int nDataLen) {

				int ret = IPlaySDK.PLAYInputData(m_nPort, szData, nDataLen);
				if (ret == 1) {
					Log.e("xss", "playing success=" + nSeq + " package size="
							+ nDataLen);
				} else {
					Log.e("xss", "playing failed=" + nSeq + " package size="
							+ nDataLen);
				}
			}
		};

		if (!StartRealPlay()) {
			Log.e("zxw", "StartRealPlay failed!");
			return;
		}

		try {
			Return_Value_Info_t retVal = new Return_Value_Info_t();

			Get_RealStream_Info_t getRealStreamInfo = new Get_RealStream_Info_t();
			// m_szCameraId = etCam.getText().toString().getBytes();

			System.arraycopy(m_szCameraId, 0, getRealStreamInfo.szCameraId, 0,
					m_szCameraId.length);
			// getRealStreamInfo.szCameraId = "1000096$1$0$0".getBytes();
			getRealStreamInfo.nMediaType = 1;
			getRealStreamInfo.nRight = 1;
			getRealStreamInfo.nStreamType = 1;
			getRealStreamInfo.nTransType = 1;
			Enc_Channel_Info_Ex_t ChannelInfo = new Enc_Channel_Info_Ex_t();
			IDpsdkCore.DPSDK_GetChannelInfoById(m_pDLLHandle, m_szCameraId,
					ChannelInfo);
			int ret = IDpsdkCore.DPSDK_GetRealStream(m_pDLLHandle, retVal,
					getRealStreamInfo, fm, mTimeOut);
			if (ret == 0) {
				m_nSeq = retVal.nReturnValue;
				Log.e("NewRealPlayActivity", ret + "");
			} else {
				StopRealPlay();
				Log.e("NewRealPlayActivity", ret + "");
				Toast.makeText(getApplicationContext(), "Open video failed!",
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Log.e("zxw", e.toString());
		}
	}

	private void closeBall() {
		int ret = IDpsdkCore.DPSDK_CloseRealStreamBySeq(m_pDLLHandle, m_nSeq,
				mTimeOut);
		if (ret == 0) {
			Log.e("zxw", "DPSDK_CloseRealStreamByCameraId success!");
		} else {
			Log.e("zxw", "DPSDK_CloseRealStreamByCameraId failed! ret = " + ret);
		}
		StopRealPlay();
		isBallFill = false;
	}

	public boolean StartRealPlay() {
		if (real_play_new_sv_fill == null)
			return false;

		boolean bOpenRet = IPlaySDK.PLAYOpenStream(m_nPort, null, 0,
				1500 * 1024) == 0 ? false : true;
		if (bOpenRet) {
			boolean bPlayRet = IPlaySDK
					.PLAYPlay(m_nPort, real_play_new_sv_fill) == 0 ? false
					: true;
			Log.i("StartRealPlay", "StartRealPlay1");
			if (bPlayRet) {

			} else {
				IPlaySDK.PLAYCloseStream(m_nPort);
				Log.i("StartRealPlay", "StartRealPlay4");
				return false;
			}
		} else {
			Log.i("StartRealPlay", "StartRealPlay5");
			return false;
		}

		return true;
	}

	public void StopRealPlay() {
		try {
			// IPlaySDK.PLAYStopSoundShare(m_nPort);
			IPlaySDK.PLAYStop(m_nPort);
			IPlaySDK.PLAYCloseStream(m_nPort);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showMove() {

		changeLayout(false);

		m_szCameraId = SPID.getBytes();
		m_nPort = IPlaySDK.PLAYGetFreePort();
		SurfaceHolder holder = real_play_new_sv_fill.getHolder();
		holder.addCallback(new Callback() {

			public void surfaceCreated(SurfaceHolder holder) {
				Log.d("zxw", "surfaceCreated");
				IPlaySDK.InitSurface(m_nPort, real_play_new_sv_fill);
			}

			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				Log.d("zxw", "surfaceChanged");
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				Log.d("zxw", "surfaceDestroyed");
				holder.removeCallback(this);
			}
		});

		final fMediaDataCallback fm = new fMediaDataCallback() {

			@Override
			public void invoke(int nPDLLHandle, int nSeq, int nMediaType,
					byte[] szNodeId, int nParamVal, byte[] szData, int nDataLen) {

				int ret = IPlaySDK.PLAYInputData(m_nPort, szData, nDataLen);
				if (ret == 1) {
					Log.e("xss", "playing success=" + nSeq + " package size="
							+ nDataLen);
				} else {
					Log.e("xss", "playing failed=" + nSeq + " package size="
							+ nDataLen);
				}
			}
		};

		if (!StartRealPlay()) {
			Log.e("zxw", "StartRealPlay failed!");
			return;
		}

		try {
			Return_Value_Info_t retVal = new Return_Value_Info_t();

			Get_RealStream_Info_t getRealStreamInfo = new Get_RealStream_Info_t();
			// m_szCameraId = etCam.getText().toString().getBytes();

			System.arraycopy(m_szCameraId, 0, getRealStreamInfo.szCameraId, 0,
					m_szCameraId.length);
			// getRealStreamInfo.szCameraId = "1000096$1$0$0".getBytes();
			getRealStreamInfo.nMediaType = 1;
			getRealStreamInfo.nRight = 1;
			getRealStreamInfo.nStreamType = 1;
			getRealStreamInfo.nTransType = 1;
			Enc_Channel_Info_Ex_t ChannelInfo = new Enc_Channel_Info_Ex_t();
			IDpsdkCore.DPSDK_GetChannelInfoById(m_pDLLHandle, m_szCameraId,
					ChannelInfo);
			int ret = IDpsdkCore.DPSDK_GetRealStream(m_pDLLHandle, retVal,
					getRealStreamInfo, fm, mTimeOut);
			if (ret == 0) {
				m_nSeq = retVal.nReturnValue;
				Log.e("NewRealPlayActivity", ret + "");
			} else {
				StopRealPlay();
				Log.e("NewRealPlayActivity", ret + "");
				Toast.makeText(getApplicationContext(), "Open video failed!",
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Log.e("zxw", e.toString());
		}
	}

	private void closeMove() {
		int ret = IDpsdkCore.DPSDK_CloseRealStreamBySeq(m_pDLLHandle, m_nSeq,
				mTimeOut);
		if (ret == 0) {
			Log.e("zxw", "DPSDK_CloseRealStreamByCameraId success!");
		} else {
			Log.e("zxw", "DPSDK_CloseRealStreamByCameraId failed! ret = " + ret);
		}
		StopRealPlay();
		isMoveFill = false;
	}

	private void changeLayout(boolean isFour) {
		if (isFour) {
			real_play_new_sv_fill.setVisibility(View.GONE);
			real_play_new_sv_fill.setEnabled(false);
			real_play_new_four_parent.setVisibility(View.VISIBLE);
			real_play_new_four_ball.setVisibility(View.VISIBLE);
			real_play_new_four_move.setVisibility(View.VISIBLE);
			real_play_new_four_ball.setEnabled(true);
			real_play_new_four_move.setEnabled(true);
		} else {
			real_play_new_sv_fill.setVisibility(View.VISIBLE);
			real_play_new_sv_fill.setEnabled(true);
			real_play_new_four_parent.setVisibility(View.GONE);
			real_play_new_four_ball.setVisibility(View.GONE);
			real_play_new_four_move.setVisibility(View.GONE);
			real_play_new_four_ball.setEnabled(false);
			real_play_new_four_move.setEnabled(false);
		}
	}

	private void showBallFour() {

		m_szCameraId_ball = QSPID.getBytes();
		m_nPort_ball = IPlaySDK.PLAYGetFreePort();
		SurfaceHolder holder = real_play_new_four_ball.getHolder();
		holder.addCallback(new Callback() {

			public void surfaceCreated(SurfaceHolder holder) {
				Log.d("zxw", "surfaceCreated");
				IPlaySDK.InitSurface(m_nPort_ball, real_play_new_four_ball);
			}

			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				Log.d("zxw", "surfaceChanged");
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				Log.d("zxw", "surfaceDestroyed");
				holder.removeCallback(this);
			}
		});

		final fMediaDataCallback fm = new fMediaDataCallback() {

			@Override
			public void invoke(int nPDLLHandle, int nSeq, int nMediaType,
					byte[] szNodeId, int nParamVal, byte[] szData, int nDataLen) {

				int ret = IPlaySDK
						.PLAYInputData(m_nPort_ball, szData, nDataLen);
				if (ret == 1) {
					Log.e("xss", "playing success=" + nSeq + " package size="
							+ nDataLen);
				} else {
					Log.e("xss", "playing failed=" + nSeq + " package size="
							+ nDataLen);
				}
			}
		};

		if (!StartRealPlayBallFour()) {
			Log.e("zxw", "StartRealPlay failed!");
			return;
		}

		try {
			Return_Value_Info_t retVal = new Return_Value_Info_t();

			Get_RealStream_Info_t getRealStreamInfo = new Get_RealStream_Info_t();
			// m_szCameraId = etCam.getText().toString().getBytes();

			System.arraycopy(m_szCameraId_ball, 0,
					getRealStreamInfo.szCameraId, 0, m_szCameraId_ball.length);
			// getRealStreamInfo.szCameraId = "1000096$1$0$0".getBytes();
			getRealStreamInfo.nMediaType = 1;
			getRealStreamInfo.nRight = 1;
			getRealStreamInfo.nStreamType = 1;
			getRealStreamInfo.nTransType = 1;
			Enc_Channel_Info_Ex_t ChannelInfo = new Enc_Channel_Info_Ex_t();
			IDpsdkCore.DPSDK_GetChannelInfoById(m_pDLLHandle,
					m_szCameraId_ball, ChannelInfo);
			int ret = IDpsdkCore.DPSDK_GetRealStream(m_pDLLHandle, retVal,
					getRealStreamInfo, fm, mTimeOut);
			if (ret == 0) {
				m_nSeq_ball = retVal.nReturnValue;
				Log.e("NewRealPlayActivity", ret + "");
			} else {
				StopRealPlayBallFour();
				Log.e("NewRealPlayActivity", ret + "");
				Toast.makeText(getApplicationContext(), "Open video failed!",
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Log.e("zxw", e.toString());
		}
	}

	public boolean StartRealPlayBallFour() {
		if (real_play_new_four_ball == null)
			return false;

		boolean bOpenRet = IPlaySDK.PLAYOpenStream(m_nPort_ball, null, 0,
				1500 * 1024) == 0 ? false : true;
		if (bOpenRet) {
			boolean bPlayRet = IPlaySDK.PLAYPlay(m_nPort_ball,
					real_play_new_four_ball) == 0 ? false : true;
			Log.i("StartRealPlay", "StartRealPlay1");
			if (bPlayRet) {
				// boolean bSuccess = IPlaySDK.PLAYPlaySoundShare(m_nPort_ball)
				// == 0 ? false : true;
				//
				// Log.i("StartRealPlay", "StartRealPlay2");
				// if(!bSuccess)
				// {
				// IPlaySDK.PLAYStop(m_nPort_ball);
				// IPlaySDK.PLAYCloseStream(m_nPort_ball);
				// Log.i("StartRealPlay", "StartRealPlay3");
				// return false;
				// }
			} else {
				IPlaySDK.PLAYCloseStream(m_nPort_ball);
				Log.i("StartRealPlay", "StartRealPlay4");
				return false;
			}
		} else {
			Log.i("StartRealPlay", "StartRealPlay5");
			return false;
		}

		return true;
	}

	public void StopRealPlayBallFour() {
		try {
			// IPlaySDK.PLAYStopSoundShare(m_nPort_ball);
			IPlaySDK.PLAYStop(m_nPort_ball);
			IPlaySDK.PLAYCloseStream(m_nPort_ball);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showMoveFour() {
 		m_szCameraId_move = SPID.getBytes();
		m_nPort_move = IPlaySDK.PLAYGetFreePort();
		SurfaceHolder holder = real_play_new_four_move.getHolder();
		holder.addCallback(new Callback() {

			public void surfaceCreated(SurfaceHolder holder) {
				Log.d("zxw", "surfaceCreated");
				IPlaySDK.InitSurface(m_nPort_move, real_play_new_four_move);
			}

			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				Log.d("zxw", "surfaceChanged");
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				Log.d("zxw", "surfaceDestroyed");
				holder.removeCallback(this);
			}
		});

		final fMediaDataCallback fm = new fMediaDataCallback() {

			@Override
			public void invoke(int nPDLLHandle, int nSeq, int nMediaType,
					byte[] szNodeId, int nParamVal, byte[] szData, int nDataLen) {

				int ret = IPlaySDK
						.PLAYInputData(m_nPort_move, szData, nDataLen);
				if (ret == 1) {
					Log.e("xss", "playing success=" + nSeq + " package size="
							+ nDataLen);
				} else {
					Log.e("xss", "playing failed=" + nSeq + " package size="
							+ nDataLen);
				}
			}
		};

		if (!StartRealPlayMoveFour()) {
			Log.e("zxw", "StartRealPlay failed!");
			return;
		}

		try {
			Return_Value_Info_t retVal = new Return_Value_Info_t();

			Get_RealStream_Info_t getRealStreamInfo = new Get_RealStream_Info_t();
			// m_szCameraId = etCam.getText().toString().getBytes();

			System.arraycopy(m_szCameraId_move, 0,
					getRealStreamInfo.szCameraId, 0, m_szCameraId_move.length);
			// getRealStreamInfo.szCameraId = "1000096$1$0$0".getBytes();
			getRealStreamInfo.nMediaType = 1;
			getRealStreamInfo.nRight = 1;
			getRealStreamInfo.nStreamType = 1;
			getRealStreamInfo.nTransType = 1;
			Enc_Channel_Info_Ex_t ChannelInfo = new Enc_Channel_Info_Ex_t();
			IDpsdkCore.DPSDK_GetChannelInfoById(m_pDLLHandle,
					m_szCameraId_move, ChannelInfo);
			int ret = IDpsdkCore.DPSDK_GetRealStream(m_pDLLHandle, retVal,
					getRealStreamInfo, fm, mTimeOut);
			if (ret == 0) {
				m_nSeq_move = retVal.nReturnValue;
				Log.e("NewRealPlayActivity", ret + "");
			} else {
				StopRealPlayMoveFour();
				Log.e("NewRealPlayActivity", ret + "");
				Toast.makeText(getApplicationContext(), "Open video failed!",
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Log.e("zxw", e.toString());
		}
	}

	public boolean StartRealPlayMoveFour() {
		if (real_play_new_four_move == null)
			return false;

		boolean bOpenRet = IPlaySDK.PLAYOpenStream(m_nPort_move, null, 0,
				1500 * 1024) == 0 ? false : true;
		if (bOpenRet) {
			boolean bPlayRet = IPlaySDK.PLAYPlay(m_nPort_move,
					real_play_new_four_move) == 0 ? false : true;
			Log.i("StartRealPlay", "StartRealPlay1");
			if (bPlayRet) {
				// boolean bSuccess = IPlaySDK.PLAYPlaySoundShare(m_nPort_move)
				// == 0 ? false : true;
				//
				// Log.i("StartRealPlay", "StartRealPlay2");
				// if(!bSuccess)
				// {
				// IPlaySDK.PLAYStop(m_nPort_move);
				// IPlaySDK.PLAYCloseStream(m_nPort_move);
				// Log.i("StartRealPlay", "StartRealPlay3");
				// return false;
				// }
			} else {
				IPlaySDK.PLAYCloseStream(m_nPort_move);
				Log.i("StartRealPlay", "StartRealPlay4");
				return false;
			}
		} else {
			Log.i("StartRealPlay", "StartRealPlay5");
			return false;
		}

		return true;
	}

	public void StopRealPlayMoveFour() {
		try {
			// IPlaySDK.PLAYStopSoundShare(m_nPort_move);
			IPlaySDK.PLAYStop(m_nPort_move);
			IPlaySDK.PLAYCloseStream(m_nPort_move);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void closeBallFour() {
		int ret = IDpsdkCore.DPSDK_CloseRealStreamBySeq(m_pDLLHandle,
				m_nSeq_ball, mTimeOut);
		if (ret == 0) {
			Log.e("zxw", "DPSDK_CloseRealStreamByCameraId success!");
		} else {
			Log.e("zxw", "DPSDK_CloseRealStreamByCameraId failed! ret = " + ret);
		}
		StopRealPlayBallFour();
	}

	private void closeMoveFour() {
		int ret = IDpsdkCore.DPSDK_CloseRealStreamBySeq(m_pDLLHandle,
				m_nSeq_move, mTimeOut);
		if (ret == 0) {
			Log.e("zxw", "DPSDK_CloseRealStreamByCameraId success!");
		} else {
			Log.e("zxw", "DPSDK_CloseRealStreamByCameraId failed! ret = " + ret);
		}
		StopRealPlayMoveFour();
	}

	private void sleep() {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void GetNetWork() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {

				while (isRun) {
					String result = doNetWork();
					if (result.equals("1")) {
						isShowMoveFour = true;
						isRun = false;
						handler.sendEmptyMessage(0);
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	private String doNetWork() {

		PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL("http://192.168.12.33:8080/zhnc/zixun/pick.json");
            URLConnection conn = realUrl.openConnection();
            
            conn.setRequestProperty("Content-Type", "html/text");
            conn.setConnectTimeout(20*1000);
			conn.setReadTimeout(20*1000);
            conn.setDoOutput(false);
            conn.setDoInput(true);
            conn.connect();

            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
            	if(result.equals("")){
            		result += line;
            	} else {
            		result += "\n" + line;
            	}
            }
            
            System.out.println("result: " + result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        
        return result.equals("")? "0":result;
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (!isBallFill && !isMoveFill) {
				real_play_new_four_move_img.setVisibility(View.GONE);
				showMoveFour();
			}
		};
	};
}