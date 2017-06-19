package com.hxsn.iot.videogo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.andbase.ssk.utils.LogUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.hxsn.iot.R;
import com.hxsn.iot.ui.ViewfinderView;
import com.hxsn.iot.videogo.CameraManager;
import com.hxsn.iot.videogo.CaptureActivityHandler;
import com.hxsn.iot.videogo.DecodeFormatManager;
import com.hxsn.iot.videogo.InactivityTimer;
import com.hxsn.iot.videogo.Intents;
import com.videogo.exception.BaseException;
import com.videogo.exception.ExtraException;
import com.videogo.util.Base64;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LocalValidate;
import com.videogo.util.Utils;
import com.videogo.widget.TitleBar;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;

/**
 * 二维码扫描添加摄像头
 */
public class CaptureActivity extends RootActivity implements SurfaceHolder.Callback {

    // 常量定义区
    private static final String TAG = "CaptureActivity";// 打印标识

    private static final String PRODUCT_SEARCH_URL_PREFIX = "http://www.google";

    private static final String PRODUCT_SEARCH_URL_SUFFIX = "/m/products/scan";

    private static final String[] ZXING_URLS = { "http://zxing.appspot.com/scan", "zxing://scan/"};

    private static final float BEEP_VOLUME = 0.10f;// 读取成功后音效大小

    private static final long VIBRATE_DURATION = 200L;// 震动持续时间 单位：微秒

    public static final int REQUEST_CODE_CLOUD = 1;
    public static final int REQUEST_ADD_PROBE = 2;

    /** 扫描View变量 */
    private ViewfinderView mViewfinderView = null;

    /** 显示扫描提示变量 */
    private TextView mTxtResult = null;

    /** 定时器变量 */
    private InactivityTimer mInactivityTimer = null;

    /** 播放器句柄 */
    private MediaPlayer mMediaPlayer = null;

    /** 本地数据合法性检测变量 */
    private LocalValidate mLocalValidate = null;

    /** 是否有声音变量 */
    private final boolean mPlayBeep = false;

    /** 是否震动变量 */
    private boolean mVibrate = false;

    /** 序列号结果变量 */
    private String mSerialNoStr = null;

    private String mSerialVeryCodeStr = null;

    /** 在页面重绘回调函数中标识是否计算过控件的大小 */
    private boolean mHasMeasured = false;

    private CameraManager cameraManager;

    private CaptureActivityHandler handler;

    private Result savedResultToShow;

    private boolean hasSurface;

    // private IntentSource source;

    private String sourceUrl;

    private Collection<BarcodeFormat> decodeFormats;

    private String characterSet;

    /** 是否已经启动相机 */
    private boolean mHasShow = true;// 进来直接启动 ture 否则false

    /** 是否点击了现在扫描 */
    private boolean mScanNow = false;
    private String deviceType = "";
    private TitleBar mTitleBar;
    private CheckBox ckbLight;//开启闪光灯
    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        /**
         *android m 当摄像头权限是询问时，动态获取摄像头权限
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if( ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                String[] permissions = {Manifest.permission.CAMERA};
                requestPermissions(permissions, 1);
            }
        }

        hasSurface = false;

        init();
        initTitleBar();
        findViews();
        setListener();
    }


    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setTitle("扫描二维码");
        mTitleBar.addBackButton(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });
        //手动添加按钮
        mTitleBar.addRightButton(R.drawable.common_title_input_selector, new View.OnClickListener() {

            @Override
            public void onClick(View v) {//跳转手动输入序列号页面

                Bundle bundle = new Bundle();
                bundle.putInt("type", 0);
                Intent intent = new Intent(CaptureActivity.this, AddEditDeviceActivity.class);
                intent.putExtras(bundle);
                CaptureActivity.this.startActivity(intent);
            }
        });
    }

    private void init() {
        mInactivityTimer = new InactivityTimer(this);
        mLocalValidate = new LocalValidate();

        // 闪光点默认关闭
        setPramaFrontLight(false);
    }

    private void findViews() {
        ckbLight = (CheckBox) findViewById(R.id.ckbLight);
        mViewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        mTxtResult = (TextView) findViewById(R.id.txtResult);
        boolean isLightOn = getPramaFrontLight();
        ckbLight.setChecked(isLightOn);
    }

    public static final String KEY_FRONT_LIGHT = "preferences_front_light";
    private boolean getPramaFrontLight() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CaptureActivity.this);
        boolean currentSetting = prefs.getBoolean(KEY_FRONT_LIGHT, false);
        return currentSetting;
    }

    private void setPramaFrontLight(boolean isChecked) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CaptureActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_FRONT_LIGHT, isChecked);
        editor.apply();
    }

    private void setListener() {

        ckbLight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setPramaFrontLight(!getPramaFrontLight());
                reScan();
            }
        });
        ViewTreeObserver vto = mTxtResult.getViewTreeObserver();

        /**
         * @ClassName: 匿名类
         * @Description: 用于监听在重绘前获得控件的大小按照屏幕的比例放置控件的位置
         * @author wangnanayf1
         * @date 2012-12-3 上午9:14:25
         */
        vto.addOnPreDrawListener( new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        if (mHasMeasured == false) {
                            DisplayMetrics dm = new DisplayMetrics();
                            // 取得窗口属性
                            getWindowManager().getDefaultDisplay().getMetrics(dm);
                            int windowsHeight = dm.heightPixels;
                            int windowsWidth = dm.heightPixels;
                            int moveLength = (int) ((windowsHeight - windowsWidth * 0.83f) / 2 - mTxtResult.getMeasuredHeight() / 2f);

                            if (moveLength > 0) {
                                // 移动控件的位置
                                mTxtResult.setPadding(0, 0, 0, moveLength);
                            }
                            mHasMeasured = true;
                        }
                        return true;
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraManager = new CameraManager(getApplication());

        getmViewfinderView().setCameraManager(cameraManager);
        handler = null;

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            if (mHasShow && !mScanNow) {
                initCamera();
            }
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        mInactivityTimer.onResume();
        Intent intent = getIntent();
        decodeFormats = null;
        characterSet = null;

        if (intent != null) {

            String action = intent.getAction();
            String dataString = intent.getDataString();

            if (Intents.Scan.ACTION.equals(action)) {
                decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
                if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
                    int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
                    int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
                    if (width > 0 && height > 0) {
                        cameraManager.setManualFramingRect(width, height);
                    }
                }
            } else if (dataString != null && dataString.contains(PRODUCT_SEARCH_URL_PREFIX)
                    && dataString.contains(PRODUCT_SEARCH_URL_SUFFIX)) {
                sourceUrl = dataString;
                decodeFormats = DecodeFormatManager.PRODUCT_FORMATS;

            } else if (isZXingURL(dataString)) {
                sourceUrl = dataString;
                Uri inputUri = Uri.parse(sourceUrl);
                decodeFormats = DecodeFormatManager.parseDecodeFormats(inputUri);
            }
            characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
        }
        ckbLight.setChecked(getPramaFrontLight());
    }

    private static boolean isZXingURL(String dataString) {
        if (dataString == null) {
            return false;
        }
        for (String url : ZXING_URLS) {
            if (dataString.startsWith(url)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        mInactivityTimer.onPause();
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();

        mHasShow = true;
        mScanNow = false;
    }

    @Override
    protected void onDestroy() {
        mInactivityTimer.shutdown();
        super.onDestroy();
    }

    private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
        // Bitmap isn't used yet -- will be used soon
        if (handler == null) {
            savedResultToShow = result;
        } else {
            if (result != null) {
                savedResultToShow = result;
            }
            if (savedResultToShow != null) {
                Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
                handler.sendMessage(message);
            }
            savedResultToShow = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if (holder == null) {
            LogUtil.i(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            if (mHasShow && !mScanNow) {
                LogUtil.i(TAG, "-----------surfaceCreated---hasSurface =false -mHasShow=true----mScanNow=false-------");
                initCamera();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param resultString
     *            The contents of the barcode.
     * @param barcode
     *            A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(String resultString, Bitmap barcode) {
        mInactivityTimer.onActivity();
        playBeepSoundAndVibrate();

        if (resultString == null) {
            LogUtil.i(TAG, "handleDecode-> resultString is null");
            return;
        }
        LogUtil.i(TAG, "resultString = " + resultString);


        // 关注二维码名片地址
        // 例如：https://test.shipin7.com/h5/qrcode/intro?
        if (resultString.startsWith("http://") && resultString.contains("smart.jd.com")) {
            mSerialNoStr = "";
            mSerialVeryCodeStr = "";
            deviceType = "";
            try {
                String deviceInfoMarker = "$$$";
                String contentMarker = "f=";
                resultString = URLDecoder.decode(resultString, "UTF-8");
                // 验证url有效性 f=打头的为 需要的内容
                int contentIndex = resultString.indexOf(contentMarker);
                if (contentIndex < 0) {
                    mSerialNoStr = resultString;
                    isValidate();
                    return;
                }
                contentIndex += contentMarker.length();
                resultString = new String(Base64.decode(resultString.substring(contentIndex).trim()));
                int index = resultString.indexOf(deviceInfoMarker);
                // 二次判断有效性 $$$打头的为萤石信息
                if (index < 0) {
                    mSerialNoStr = resultString;
                    isValidate();
                    return;
                }
                index += deviceInfoMarker.length();
                resultString = resultString.substring(index);
                String[] infos = resultString.split("\r\n");
                if (infos.length >= 2) {
                    mSerialNoStr = infos[1];
                }
                if (infos.length >= 3) {
                    mSerialVeryCodeStr = infos[2];
                }
                if (infos.length >= 4) {
                    deviceType = infos[3];
                }
                isValidate();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            // 初始化数据
            mSerialNoStr = "";
            mSerialVeryCodeStr = "";
            deviceType = "";
            LogUtil.i(TAG, resultString);

            // 字符集合
            String[] newlineCharacterSet = {
                    "\n\r", "\r\n", "\r", "\n"};
            String stringOrigin = resultString;
            // 寻找第一次出现的位置
            int a = -1;
            int firstLength = 1;
            for (String string : newlineCharacterSet) {
                if (a == -1) {
                    a = resultString.indexOf(string);
                    if (a > stringOrigin.length() - 3) {
                        a = -1;
                    }
                    if (a != -1) {
                        firstLength = string.length();
                    }
                }
            }

            // 扣去第一次出现回车的字符串后，剩余的是第二行以及以后的
            if (a != -1) {
                resultString = resultString.substring(a + firstLength);
            }
            // 寻找最后一次出现的位置
            int b = -1;
            for (String string : newlineCharacterSet) {
                if (b == -1) {
                    b = resultString.indexOf(string);
                    if (b != -1) {
                        mSerialNoStr = resultString.substring(0, b);
                        firstLength = string.length();
                    }
                }
            }

            // 寻找遗失的验证码阶段
            if (mSerialNoStr != null && b != -1 && (b + firstLength) <= resultString.length()) {
                resultString = resultString.substring(b + firstLength);
            }

            // 再次寻找回车键最后一次出现的位置
            int c = -1;
            for (String string : newlineCharacterSet) {
                if (c == -1) {
                    c = resultString.indexOf(string);
                    if (c != -1) {
                        mSerialVeryCodeStr = resultString.substring(0, c);
                    }
                }
            }

            // 寻找CS-C2-21WPFR 判断是否支持wifi
            if (mSerialNoStr != null && c != -1 && (c + firstLength) <= resultString.length()) {
                resultString = resultString.substring(c + firstLength);
            }
            if (resultString != null && resultString.length() > 0) {
                deviceType = resultString;
            }

            if (b == -1) {
                mSerialNoStr = resultString;
            }

            if (mSerialNoStr == null) {
                mSerialNoStr = stringOrigin;
            }

            // 判断是不是9位
            isValidate();
        }
    }

    private void showDecodeFailedTip() {
        if (isFinishing()) {
            return;
        }

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        TextView view = new TextView(this);
        view.setText(R.string.unable_identify_two_dimensional_code_tip);
        view.setTextSize(20);
        view.setTextColor(getResources().getColor(R.color.common_text));
        view.setBackgroundResource(R.drawable.decode_failed_tip_bg);
        view.setPadding(Utils.dip2px(this, 6), Utils.dip2px(this, 16), Utils.dip2px(this, 6), Utils.dip2px(this, 16));
        toast.setView(view);
        toast.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CLOUD) {
            setResult(REQUEST_CODE_CLOUD, data);
            finish();
        } else if (requestCode == REQUEST_ADD_PROBE && resultCode == -1) {
            setResult(-1, data);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 判断是不是合法
     *
     * @throws
     */
    private void isValidate() {
        mLocalValidate = new LocalValidate();
        try {
            mLocalValidate.localValidatSerialNo(mSerialNoStr);

        } catch (BaseException e) {
            handleLocalValidateSerialNoFail(e.getErrorCode());
            LogUtil.i(TAG, "searchCameraBySN-> local validate serial no fail, errCode:" + e.getErrorCode());
            return;
        }

        if (!ConnectionDetector.isNetworkAvailable(this)) {
            showToast(R.string.query_camera_fail_network_exception);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("SerialNo", mSerialNoStr);
        bundle.putString("very_code", mSerialVeryCodeStr);

        Intent intent = new Intent(CaptureActivity.this, AddScanDeviceActivity.class);
        intent.putExtras(bundle);
        CaptureActivity.this.startActivity(intent);

        mScanNow = false;
    }

    private void handleLocalValidateSerialNoFail(int errCode) {
        switch (errCode) {
            case ExtraException.SERIALNO_IS_NULL:
                showToast(R.string.serial_number_is_null);
                break;
            case ExtraException.SERIALNO_IS_ILLEGAL:
                showDecodeFailedTip();
                break;
            default:
                showToast(R.string.serial_number_error, errCode);
                LogUtil.i(TAG, "handleLocalValidateSerialNoFail-> unkown error, errCode:" + errCode);
                break;
        }
        reScan();
    }

    /**
     * 初始化搜索
     *
     * @see
     * @since V1.8.2
     */
    private void reScan() {
        onPause();
        onResume();
    }

    private void initCamera() {
        try {
            initBeepSound(); // 声音效果
            mVibrate = true; // 是否震动
            mSerialNoStr = null;

            LogUtil.i("CaptureActivity", "permission is ok");
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            cameraManager.openDriver(surfaceHolder);

            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats, characterSet, cameraManager);
            }
            decodeOrStoreSavedBitmap(null, null);

        } catch (IOException ioe) {
            ioe.printStackTrace();
            showToast(R.string.open_camera_fail);
        } catch (RuntimeException e) {
            e.printStackTrace();
            showToast(R.string.open_camera_fail);
        }
    }

    public void drawViewfinder() {
        mViewfinderView.drawViewfinder();
    }

    /**
     * 初始化音效,并设置监听
     */
    private void initBeepSound() {
        if (mPlayBeep) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mMediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                mMediaPlayer = null;
            }
        }
    }

    /**
     * 设置声音以及震动
     *
     * @throws
     */
    private void playBeepSoundAndVibrate() {
        if (mPlayBeep && mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        if (mVibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(0, R.anim.fade_down);
    }

    /**
     * @return the mViewfinderView
     */
    public ViewfinderView getmViewfinderView() {
        return mViewfinderView;
    }



    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        setPramaFrontLight(false);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        setPramaFrontLight(false);
    }
}
