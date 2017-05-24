package com.hxsn.witwork.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.hxsn.witwork.MainActivity;
import com.hxsn.witwork.R;
import com.hxsn.witwork.ui.ViewfinderView;
import com.hxsn.witwork.utils.Const;
import com.hxsn.witwork.utils.RGBLuminanceSource;
import com.hxsn.witwork.utils.camera.CameraManager;
import com.hxsn.witwork.utils.camera.InactivityTimer;
import com.hxsn.witwork.utils.camera.PlanarYUVLuminanceSource;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class SuYuanActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private SuYuanActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    int ifOpenLight = 0;
    TextView suyuanTitleTV,suyuandtTV;

    ImageView suyuan_logoIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_su_yuan);

        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.suyuan_viewfinder_view);
        suyuanTitleTV=(TextView)findViewById(R.id.suyuanTitleTV);
        suyuan_logoIV=(ImageView)findViewById(R.id.suyuan_logoIV);
        suyuandtTV=(TextView)findViewById(R.id.suyuandtTV);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

        suyuanTitleTV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent inte=new Intent(SuYuanActivity.this,MainActivity.class);
                inte.putExtra("tag", 0);
                startActivity(inte);
                finish();
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.suyuan_preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

//		// quit the scan view
//		suyuanTitleTV.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent inte=new Intent(SuYuanActivity.this,MainActivity.class);
//				inte.putExtra("tag", 0);
//				startActivity(inte);
//				SuYuanActivity.this.finish();
//			}
//		});
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        // FIXME
        if (resultString.equals("")) {
            Toast.makeText(SuYuanActivity.this,  "扫描失败!", Toast.LENGTH_SHORT)
                    .show();
        } else {
            //Intent resultIntent = new Intent(this,WebActivity.class);
            //扫码溯源
//			if(title.equals("su"))
//			{
            Intent inte=new Intent(SuYuanActivity.this,ProStatActivity.class);
            inte.putExtra("code", resultString);
            startActivity(inte);

        }

    }


    String  photo_path;
    ProgressDialog mProgress;
    Bitmap scanBitmap;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    // 获取选中图片的路径
                    Cursor cursor = getContentResolver().query(data.getData(),
                            null, null, null, null);
                    if (cursor.moveToFirst()) {
                        photo_path = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Images.Media.DATA));

                        Log.i("路径", photo_path);
                    }
                    cursor.close();

                    mProgress = new ProgressDialog(SuYuanActivity.this);
                    mProgress.setMessage("正在扫描...");
                    mProgress.setCancelable(false);
                    mProgress.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Result result = scanningImage(photo_path);
                            if (result != null) {
                                Message m = mHandler.obtainMessage();
                                m.what = 1;
                                m.obj = result.getText();
                                mHandler.sendMessage(m);
                            } else {
                                Message m = mHandler.obtainMessage();
                                m.what = 2;
                                m.obj = "Scan failed!";
                                mHandler.sendMessage(m);
                            }

                        }
                    }).start();
                    break;

                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            switch (msg.what) {
                case 1:
                    mProgress.dismiss();
                    String resultString = msg.obj.toString();
                    if (resultString.equals("")) {
                        Toast.makeText(SuYuanActivity.this, "扫描失败!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // System.out.println("Result:"+resultString);
                        Intent resultIntent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("result", resultString);
                        resultIntent.putExtras(bundle);
                        SuYuanActivity.this.setResult(RESULT_OK, resultIntent);
                    }
                    SuYuanActivity.this.finish();
                    break;

                case 2:
                    mProgress.dismiss();
                    Toast.makeText(SuYuanActivity.this, "解析错误！", Toast.LENGTH_LONG)
                            .show();

                    break;
                default:
                    break;
            }

            super.handleMessage(msg);
        }

    };

    /**
     * 扫描二维码图片的方法
     *
     * 目前识别度不高，有待改进
     *
     * @param path
     * @return
     */
    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        int sampleSize = (int) (options.outHeight / (float) 100);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new SuYuanActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent inte=new Intent(SuYuanActivity.this,MainActivity.class);
            inte.putExtra("tag", 0);
            startActivity(inte);
            finish();

        }
        return super.onKeyDown(keyCode, event);
    }




    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    class SuYuanActivityHandler extends Handler {


        private final SuYuanActivity activity;
        private final SuYuanDecodeThread decodeThread;
        private State state;

        public SuYuanActivityHandler(SuYuanActivity activity, Vector<BarcodeFormat> decodeFormats,
                                     String characterSet) {
            this.activity = activity;
            decodeThread = new SuYuanDecodeThread(activity, decodeFormats, characterSet,
                    new ViewfinderResultPointCallback(activity.getViewfinderView()));
            decodeThread.start();
            state = State.SUCCESS;
            // Start ourselves capturing previews and decoding.
            CameraManager.get().startPreview();
            restartPreviewAndDecode();
        }

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case R.id.auto_focus:
                    //Log.d(TAG, "Got auto-focus message");
                    // When one auto focus pass finishes, start another. This is the closest thing to
                    // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
                    if (state == State.PREVIEW) {
                        CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                    }
                    break;
                case R.id.restart_preview:

                    restartPreviewAndDecode();
                    break;
                case R.id.decode_succeeded:

                    state = State.SUCCESS;
                    Bundle bundle = message.getData();

                    /***********************************************************************/
                    Bitmap barcode = bundle == null ? null :
                            (Bitmap) bundle.getParcelable(Const.BARCODE_BITMAP);//閿熸枻鎷烽敓鐭唻鎷烽敓鏂ゆ嫹閿熺绛规嫹

                    activity.handleDecode((Result) message.obj, barcode);//閿熸枻鎷烽敓鎴枻鎷烽敓锟�       /***********************************************************************/
                    break;
                case R.id.decode_failed:
                    // We're decoding as fast as possible, so when one decode fails, start another.
                    state = State.PREVIEW;
                    CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                    break;
                case R.id.return_scan_result:

                    activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
                    activity.finish();
                    break;
                case R.id.launch_product_query:

                    String url = (String) message.obj;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    activity.startActivity(intent);
                    break;
            }
        }

        public void quitSynchronously() {
            state = State.DONE;
            CameraManager.get().stopPreview();
            Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
            quit.sendToTarget();
            try {
                decodeThread.join();
            } catch (InterruptedException e) {
                // continue
            }

            // Be absolutely sure we don't send any queued up messages
            removeMessages(R.id.decode_succeeded);
            removeMessages(R.id.decode_failed);
        }

        private void restartPreviewAndDecode() {
            if (state == State.SUCCESS) {
                state = State.PREVIEW;
                CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                activity.drawViewfinder();
            }
        }
    }

    class ViewfinderResultPointCallback implements ResultPointCallback {

        private final ViewfinderView viewfinderView;

        public ViewfinderResultPointCallback(ViewfinderView viewfinderView) {
            this.viewfinderView = viewfinderView;
        }

        public void foundPossibleResultPoint(ResultPoint point) {
            viewfinderView.addPossibleResultPoint(point);
        }

    }

    class SuYuanDecodeThread extends Thread {

        public static final String BARCODE_BITMAP = "barcode_bitmap";
        private final SuYuanActivity activity;
        private final Hashtable<DecodeHintType, Object> hints;
        private Handler handler;
        private final CountDownLatch handlerInitLatch;

        SuYuanDecodeThread(SuYuanActivity activity,
                           Vector<BarcodeFormat> decodeFormats,
                           String characterSet,
                           ResultPointCallback resultPointCallback) {

            this.activity = activity;
            handlerInitLatch = new CountDownLatch(1);

            hints = new Hashtable<DecodeHintType, Object>(3);

            if (decodeFormats == null || decodeFormats.isEmpty()) {
                decodeFormats = new Vector<BarcodeFormat>();
                decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
                decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
                decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
            }

            hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

            if (characterSet != null) {
                hints.put(DecodeHintType.CHARACTER_SET, characterSet);
            }

            hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
        }

        Handler getHandler() {
            try {
                handlerInitLatch.await();
            } catch (InterruptedException ie) {
                // continue?
            }
            return handler;
        }

        @Override
        public void run() {
            Looper.prepare();
            handler = new SuYuanDecodeHandler(activity, hints);
            handlerInitLatch.countDown();
            Looper.loop();
        }

    }








    class SuYuanDecodeHandler extends Handler {


        private final SuYuanActivity activity;
        private final MultiFormatReader multiFormatReader;

        SuYuanDecodeHandler(SuYuanActivity activity, Hashtable<DecodeHintType, Object> hints) {
            multiFormatReader = new MultiFormatReader();
            multiFormatReader.setHints(hints);
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case R.id.decode:
                    //Log.d(TAG, "Got decode message");
                    decode((byte[]) message.obj, message.arg1, message.arg2);
                    break;
                case R.id.quit:
                    Looper.myLooper().quit();
                    break;
            }
        }

        /**
         * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
         * reuse the same reader objects from one decode to the next.
         *
         * @param data   The YUV preview frame.
         * @param width  The width of the preview frame.
         * @param height The height of the preview frame.
         */
        private void decode(byte[] data, int width, int height) {
            long start = System.currentTimeMillis();
            Result rawResult = null;

            //modify here
            byte[] rotatedData = new byte[data.length];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++)
                    rotatedData[x * height + height - y - 1] = data[x + y * width];
            }
            int tmp = width; // Here we are swapping, that's the difference to #11
            width = height;
            height = tmp;

            PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(rotatedData, width, height);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = multiFormatReader.decodeWithState(bitmap);
            } catch (ReaderException re) {
                // continue
            } finally {
                multiFormatReader.reset();
            }

            if (rawResult != null) {
                long end = System.currentTimeMillis();

                Message message = Message.obtain(activity.getHandler(), R.id.decode_succeeded, rawResult);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Const.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
                message.setData(bundle);
                //Log.d(TAG, "Sending decode succeeded message...");
                message.sendToTarget();
            } else {
                Message message = Message.obtain(activity.getHandler(), R.id.decode_failed);
                message.sendToTarget();
            }
        }
    }
}
