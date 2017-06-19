package com.hxsn.iot.videogo.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hxsn.iot.R;
import com.hxsn.iot.TApplication;
import com.hxsn.iot.base.BaseTitle;
import com.hxsn.iot.ui.LoadingTextView;
import com.hxsn.iot.uitls.AudioPlayUtil;
import com.hxsn.iot.uitls.RealPlayUtil;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.util.RotateViewUtil;
import com.videogo.util.SDCardUtil;
import com.videogo.util.Utils;

import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class RealPlayActivity extends Activity implements View.OnClickListener{

    private final String TAG = "RealPlayActivity";
    // UI消息
    public static final int MSG_PLAY_UI_UPDATE = 200;

    private EZPlayer mEZPlayer = null;
    private EZCameraInfo mCameraInfo;
    private Handler mHandler;
    private Handler.Callback callback;
    private SurfaceHolder surfaceHold;
    private SurfaceView surfaceView;
    private LoadingTextView mRealPlayPlayLoading;
    private ImageButton mRealPlayCaptureBtn = null;//截图按钮
    private LinearLayout mRealPlayRecordLy;//录像的视图大容器
    private View mRealPlayRecordContainer;//录像的视图小容器
    private ImageButton mRealPlayRecordBtn;//录像按钮
    private ImageButton mRealPlayRecordStartBtn;//开始录像后按钮变为这个
    private ImageView mRealPlayRecordIv = null;//开始录像后显示的录像计时按钮
    private TextView mRealPlayRecordTv;//录像计时显示
    private String mRecordTime;//录像时间显示的内容
    private TimerTask mUpdateTimerTask;//更新时间任务
    private Timer mUpdateTimer;//更新时间计时器
    private ImageButton mRealPlayBtn = null;//播放工具条上的播放暂停按钮

    private boolean mIsRecording = false;//是否正在录像
    private boolean mIsOnStop = false;//是否正在播放

    /**
     * 录像时间 单位为秒
     */
    private int mRecordSecond = 0;
    /**
     * 屏幕当前方向
     */
    private int mOrientation = Configuration.ORIENTATION_PORTRAIT;
    private RotateViewUtil mRecordRotateViewUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_play);

        addData();
        addView();

        BaseTitle.getInstance(this).setTitle(TApplication.cameraInfo.getCameraName());

        initPlay();
    }

    private void addData() {
        mRecordRotateViewUtil = new RotateViewUtil();
        mCameraInfo = TApplication.cameraInfo;
        callback = new MyCallBack();
        mHandler = new Handler(callback);
    }

    private void addView() {
        surfaceView = (SurfaceView) findViewById(R.id.realplay_sv);
        surfaceHold = surfaceView.getHolder();
        surfaceHold.addCallback(new SurfaceCallback() );
        mRealPlayPlayLoading = (LoadingTextView) findViewById(R.id.real_play_loading);
        mRealPlayCaptureBtn = (ImageButton) findViewById(R.id.realplay_previously_btn);
        mRealPlayCaptureBtn.setEnabled(false);
        mRealPlayRecordLy = (LinearLayout) findViewById(R.id.realplay_record_ly);//播放区域录像的视图容器
        mRealPlayRecordLy.setVisibility(View.GONE);
        mRealPlayRecordBtn = (ImageButton) findViewById(R.id.in_video_control).findViewById(R.id.realplay_video_btn);//录像的按钮
        mRealPlayRecordStartBtn = (ImageButton) findViewById(R.id.in_video_control).findViewById(R.id.realplay_video_start_btn);//开始录像的按钮
        mRealPlayRecordContainer = findViewById(R.id.in_video_control).findViewById(R.id.realplay_video_container);//录像的视图小容器
        mRealPlayRecordIv = (ImageView) findViewById(R.id.realplay_record_iv);//开始录像后显示的录像计时按钮

        mRealPlayBtn = (ImageButton) findViewById(R.id.realplay_play_btn);
        mRealPlayRecordTv = (TextView) findViewById(R.id.realplay_record_tv);//录像计时显示

    }

    private void initPlay() {

        if(mEZPlayer == null){
            mEZPlayer = TApplication.getOpenSDK().createPlayer(mCameraInfo.getDeviceSerial(), mCameraInfo.getCameraNo());
        }
        mEZPlayer.setHandler(mHandler);
        mEZPlayer.setSurfaceHold(surfaceHold);
        mEZPlayer.startRealPlay();
        mRealPlayPlayLoading.setVisibility(View.VISIBLE);
        updateLoadingProgress(0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopUpdateTimer();
    }

    /**
     * 加载视频进度
     * @param progress
     */
    private void updateLoadingProgress(final int progress) {

        mRealPlayPlayLoading.setTag(Integer.valueOf(progress));
        mRealPlayPlayLoading.setText(progress + "%");
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mRealPlayPlayLoading != null) {
                    Integer tag = (Integer) mRealPlayPlayLoading.getTag();
                    if (tag != null && tag.intValue() == progress) {
                        Random r = new Random();
                        mRealPlayPlayLoading.setText((progress + r.nextInt(20)) + "%");
                    }
                }
            }

        }, 500);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.realplay_previously_btn://截图
                RealPlayUtil.onCapturePicBtnClick(this,this,mEZPlayer);
                break;
            case R.id.realplay_video_btn:
            case R.id.realplay_video_start_btn:
                onRecordBtnClick();
                break;
        }
    }

    /**
     * 开始录像
     *
     * @see
     * @since V1.0
     */
    private void onRecordBtnClick() {
        //mControlDisplaySec = 0;
        if (mIsRecording) {
            stopRealPlayRecord();
            return;
        }

        if (SDCardUtil.getSDCardRemainSize() < SDCardUtil.PIC_MIN_MEM_SPACE) {
            // 提示内存不足
            Utils.showToast(RealPlayActivity.this,"内存不足");
            return;
        }

        if (mEZPlayer != null) {
            mRealPlayRecordLy.setVisibility(View.GONE);
            AudioPlayUtil.getInstance(getApplication()).playAudioFile(AudioPlayUtil.RECORD_SOUND);

            // 可以采用deviceSerial+时间作为文件命名，demo中简化，只用时间命名
            java.util.Date date = new java.util.Date();
            String strRecordFile = Environment.getExternalStorageDirectory().getPath() + "/EZOpenSDK/Records/" + String.format("%tY", date)
                    + String.format("%tm", date) + String.format("%td", date) + "/"
                    + String.format("%tH", date) + String.format("%tM", date) + String.format("%tS", date) + String.format("%tL", date) + ".mp4";

            if (mEZPlayer.startLocalRecordWithFile(strRecordFile)) {
                handleRecordSuccess();
            } else {
                //handleRecordFail();
            }
        }
    }

    /**
     * 开始录像成功
     */
    private void handleRecordSuccess() {

        // 设置录像按钮为check状态
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            if (!mIsOnStop) {//播放画面时 录像按钮旋转变换为正在录像的状态
                mRecordRotateViewUtil = new RotateViewUtil();
                mRecordRotateViewUtil.applyRotation(mRealPlayRecordContainer, mRealPlayRecordBtn,mRealPlayRecordStartBtn, 0, 90);
            } else {
                mRealPlayRecordBtn.setVisibility(View.GONE);
                mRealPlayRecordStartBtn.setVisibility(View.VISIBLE);
            }

        }
        mIsRecording = true;
        // 计时按钮可见
        mRealPlayRecordLy.setVisibility(View.VISIBLE);
        mRealPlayRecordTv.setText("00:00");
        mRecordSecond = 0;
    }

    /**
     * 停止录像
     *
     * @see
     * @since V1.0
     */
    private void stopRealPlayRecord() {
        if (mEZPlayer == null || !mIsRecording) {
            return;
        }
        stopUpdateTimer();
        Toast.makeText(this, "已保存至相册", Toast.LENGTH_SHORT).show();

        // 设置录像按钮为check状态
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            if (!mIsOnStop) {
                mRecordRotateViewUtil.applyRotation(mRealPlayRecordContainer, mRealPlayRecordStartBtn,
                        mRealPlayRecordBtn, 0, 90);
            } else {
                mRealPlayRecordStartBtn.setVisibility(View.GONE);
                mRealPlayRecordBtn.setVisibility(View.VISIBLE);
            }

        }
        AudioPlayUtil.getInstance(getApplication()).playAudioFile(AudioPlayUtil.RECORD_SOUND);
        mEZPlayer.stopLocalRecord();

        // 计时按钮不可见
        mRealPlayRecordLy.setVisibility(View.GONE);
        mIsRecording = false;
    }

    /**
     * 停止定时器
     *
     * @see
     * @since V1.0
     */
    private void stopUpdateTimer() {
        mHandler.removeMessages(MSG_PLAY_UI_UPDATE);
        // 停止录像计时
        if (mUpdateTimer != null) {
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }

        if (mUpdateTimerTask != null) {
            mUpdateTimerTask.cancel();
            mUpdateTimerTask = null;
        }
    }

    /**
     * 启动定时器
     *
     * @see
     * @since V1.0
     */
    private void startUpdateTimer() {
        stopUpdateTimer();
        // 开始录像计时
        mUpdateTimer = new Timer();
        mUpdateTimerTask = new TimerTask() {
            @Override
            public void run() {

                // 更新录像时间
                if (mEZPlayer != null && mIsRecording) {
                    // 更新录像时间
                    Calendar OSDTime = mEZPlayer.getOSDTime();
                    if (OSDTime != null) {
                        String playtime = Utils.OSD2Time(OSDTime);
                        if (!TextUtils.equals(playtime, mRecordTime)) {
                            mRecordSecond++;
                            mRecordTime = playtime;
                        }
                    }
                }
                if (mHandler != null) {
                    Log.i(TAG,"-----------sendEmptyMessage-200--------------");
                    mHandler.sendEmptyMessage(MSG_PLAY_UI_UPDATE);
                }
            }
        };
        // 延时1000ms后执行，1000ms执行一次
        mUpdateTimer.schedule(mUpdateTimerTask, 0, 1000);
    }


    /**
     * 视频组件SurfaceView必须的回调方法
     */
    class SurfaceCallback implements SurfaceHolder.Callback{

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG,"--------------------surfaceCreated-----------------");
            if (mEZPlayer != null) {
                mEZPlayer.setSurfaceHold(holder);
            }
            surfaceHold = holder;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.i(TAG,"--------------------surfaceChanged-----------------format="+format+",width="+width+",height="+height);

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG,"--------------------surfaceDestroyed-----------------");
            if (mEZPlayer != null) {
                mEZPlayer.stopLocalRecord();
                mEZPlayer.stopRealPlay();
                mEZPlayer.release();
            }
        }
    }

    /**
     * 请求视频资源时所需要处理的各种情况
     */
    class MyCallBack implements Handler.Callback{

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_SUCCESS://成功取得画面
                    Log.i(TAG,"----------------ok---------------");
                    handlePlaySuccess();
                    break;
                case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_FAIL:
                    Log.i(TAG,"----------error-msg-----------------="+msg);
                    break;
                case MSG_PLAY_UI_UPDATE://更新计时内容
                    updateRealPlayUI();
                default:
                    Log.i(TAG,"-------------default msg-----------="+msg);
            }
            return false;
        }
    }

    private void updateRealPlayUI() {
        if (mIsRecording) {
            updateRecordTime();
        }
    }

    /**
     * 更新录像时间
     *
     * @see
     * @since V1.0
     */
    private void updateRecordTime() {
        if (mRealPlayRecordIv.getVisibility() == View.VISIBLE) {
            mRealPlayRecordIv.setVisibility(View.INVISIBLE);
        } else {
            mRealPlayRecordIv.setVisibility(View.VISIBLE);
        }
        // 计算分秒
        int leftSecond = mRecordSecond % 3600;
        int minitue = leftSecond / 60;
        int second = leftSecond % 60;

        // 显示录像时间
        String recordTime = String.format("%02d:%02d", minitue, second);
        mRealPlayRecordTv.setText(recordTime);
    }

    private void handlePlaySuccess() {
        mRealPlayCaptureBtn.setEnabled(true);           //截图按钮可点击了
        mRealPlayPlayLoading.setVisibility(View.GONE);  //加载图标消失了

        setRealPlaySuccessUI();

    }

    /**
     * 成功获取画面后更新的ＵＩ
     */
    private void setRealPlaySuccessUI() {

        mRealPlayBtn.setBackgroundResource(R.drawable.play_stop_selector);

        if (mCameraInfo != null ) {
            mRealPlayCaptureBtn.setEnabled(true);
            mRealPlayRecordBtn.setEnabled(true);

        }

        startUpdateTimer();
    }
}
