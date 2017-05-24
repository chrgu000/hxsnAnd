package com.hxsn.witwork.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.HybridBinarizer;
import com.hxsn.witwork.R;
import com.hxsn.witwork.TApplication;
import com.hxsn.witwork.ui.LoadingDialog;
import com.hxsn.witwork.ui.ViewfinderView;
import com.hxsn.witwork.utils.Const;
import com.hxsn.witwork.utils.camera.CameraManager;
import com.hxsn.witwork.utils.camera.InactivityTimer;
import com.hxsn.witwork.utils.camera.PlanarYUVLuminanceSource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

/**
 * 作业登记
 */
public class WorkRecordActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private WorkRecordActivityHandler handler;
    private ViewfinderView wrviewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    public static boolean isFarm=false;
    private LinearLayout workCX_layout;
    private LinearLayout reidLayout;
    private FrameLayout mianLayout;
    static String strings="";
    static int count=1;
    private TextView wrcamerTitleTV;
    private String TAG = "WorkRecordActivity";
    //高频扫码判断
    private  TextView t;
    private String text="";
    private String intentText="";
    boolean RFIDflag=false;
    private String idStrings="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_record);

        wrviewfinderView = (ViewfinderView) findViewById(R.id.wrviewfinder_view);
        wrviewfinderView.measure(ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT);

        CameraManager.init(getApplication());

        WindowManager wm = (WindowManager)this .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        mianLayout = (FrameLayout) findViewById(R.id.mainLayout);

        reidLayout  = new LinearLayout(this);
        reidLayout.setPadding(0, height-240, 0, 0);
        reidLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        t  =  new TextView(this);
        t.setGravity(Gravity.CENTER_HORIZONTAL);
        t.setText(text);
        t.setTextColor(Color.WHITE);
        t.setMovementMethod(ScrollingMovementMethod.getInstance());
        reidLayout.addView(t);
        mianLayout.addView(reidLayout);

        workCX_layout = (LinearLayout) findViewById(R.id.workCX_layout);
        wrcamerTitleTV=(TextView) findViewById(R.id.wrcamerTitleTV);
        if(isFarm) {
            wrcamerTitleTV.setText("收获扫描");
        } else {
            wrcamerTitleTV.setText("作业登记");
            if (TApplication.getRfid() != null){
                LoadingDialog.showLoading(WorkRecordActivity.this, "正在初始化RFID");
                rfidRun();
            }
        }
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        wrcamerTitleTV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent inte=new Intent(WorkRecordActivity.this,HomeActivity.class);
                inte.putExtra("tag", 0);
                startActivity(inte);
                finish();
               }
        });

        workCX_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent inte = new Intent(WorkRecordActivity.this, WorkSelectActivity.class);
                startActivity(inte);
            }
        });
    }


    private void rfidRun(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                Message msg =new Message();
                msg.what=2;
                while(!rfidInit){
                    if (TApplication.getRfid() != null) {
                        TApplication.getRfid().getInterrogatorModel();
                        rfidInit=true;
                        msg.what=2;
                        myHandler.sendMessage(msg);
                    } else {
                        rfidInit=true;
                    }
                }
            }
        }.start();
    }

    Handler myHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                t.setText(text);
            }
            if(msg.what==2){
                LoadingDialog.dissmissLoading();
            }
        }
    };

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new WorkRecordActivityHandler(decodeFormats, characterSet);
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

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    @SuppressWarnings("deprecation")
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        playBeepSoundAndVibrate();
        String resultString = result.getText();

        try {
            resultString = URLDecoder.decode(resultString,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resultString = "";
        }

        if (resultString.equals("")) {
            Toast.makeText(WorkRecordActivity.this,  "扫描失败!", Toast.LENGTH_SHORT)
                    .show();
        } else 	if(isFarm) {
            Intent resultIntent = new Intent(this,WorkCZActivity.class);
            resultIntent.putExtra("code",resultString);
            startActivity(resultIntent);
            WorkRecordActivity.this.finish();
        } else {
            Intent resultIntent = new Intent(this,WorkDJActivity.class);
            strings+=(resultString);

            if(resultString.contains("HJ")||resultString.contains("hj"))
            {
                if(RFIDflag){

                    if(strings!=null){
                        strings=intentText.substring(0, intentText.length()-1)+strings;
                    }
                    idStrings+=strings;

                    resultIntent.putExtra("RFIDS",idStrings);
                    strings="";
                }
                else{
                    resultIntent.putExtra("result",strings);
                }
                startActivity(resultIntent);
                strings="";
                RFIDflag=false;
                count=1;
                WorkRecordActivity.this.finish();
            }
            else
            {
                count++;
                wrviewfinderView.setName(resultString);

                TApplication.stop();
                if (handler != null) {
                    handler.quitSynchronously();
                    handler = null;
                }
                CameraManager.get().closeDriver();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("扫描结果")
                        .setCancelable(false)
                        .setMessage("扫描成功，继续下一个")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SurfaceView surfaceView = (SurfaceView) findViewById(R.id.wrpreview_view);
                                SurfaceHolder surfaceHolder = surfaceView.getHolder();
                                if (hasSurface) {
                                    initCamera(surfaceHolder);
                                } else {
                                    surfaceHolder.addCallback(WorkRecordActivity.this);
                                    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                                }
                                decodeFormats = null;
                                characterSet = null;
                                playBeep = true;
                                AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
                                if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL){
                                    playBeep = false;
                                }
                                initBeepSound();
                                vibrate = true;
                            }
                        }).show();
            }
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

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
                    R.raw.tag_inventoried);
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

    private boolean rfidInit=false;
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



    enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }
    class WorkRecordActivityHandler extends Handler {


        private final WorkRecordDecodeThread decodeThread;
        private State state;

        public WorkRecordActivityHandler( Vector<BarcodeFormat> decodeFormats,
                                         String characterSet) {
            decodeThread = new WorkRecordDecodeThread( decodeFormats, characterSet,
                    new ViewfinderResultPointCallback(wrviewfinderView));
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

                    if (state == State.PREVIEW) {
                        CameraManager.get().requestAutoFocus(WorkRecordActivityHandler.this, R.id.auto_focus);
                    }
                    break;
                case R.id.restart_preview:
                    Log.d(TAG, "Got restart preview message");
                    restartPreviewAndDecode();
                    break;
                case R.id.decode_succeeded:
                    Log.d(TAG, "Got decode succeeded message");
                    state = State.SUCCESS;
                    Bundle bundle = message.getData();

                    /***********************************************************************/
                    Bitmap barcode = bundle == null ? null :(Bitmap) bundle.getParcelable(Const.BARCODE_BITMAP);//閿熸枻鎷烽敓鐭唻鎷烽敓鏂ゆ嫹閿熺绛规嫹

                    WorkRecordActivity.this.handleDecode((Result) message.obj, barcode);//閿熸枻鎷烽敓鎴枻鎷烽敓锟�       /***********************************************************************/
                    break;
                case R.id.decode_failed:
                    // We're decoding as fast as possible, so when one decode fails, start another.
                    state = State.PREVIEW;
                    CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                    break;
                case R.id.return_scan_result:
                    Log.d(TAG, "Got return scan result message");
                    WorkRecordActivity.this.setResult(Activity.RESULT_OK, (Intent) message.obj);
                    WorkRecordActivity.this.finish();
                    break;
                case R.id.launch_product_query:
                    Log.d(TAG, "Got product query message");
                    String url = (String) message.obj;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    WorkRecordActivity.this.startActivity(intent);
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
                wrviewfinderView.drawViewfinder();
            }
        }
    }

    class WorkRecordDecodeThread extends Thread {

        private final Hashtable<DecodeHintType, Object> hints;
        private Handler handler;
        private final CountDownLatch handlerInitLatch;

        WorkRecordDecodeThread(Vector<BarcodeFormat> decodeFormats,
                               String characterSet,
                               ResultPointCallback resultPointCallback) {

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
            handler = new WorkRecordDecodeHandler(hints);
            handlerInitLatch.countDown();
            Looper.loop();
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


    class WorkRecordDecodeHandler extends Handler {


        private final MultiFormatReader multiFormatReader;

        WorkRecordDecodeHandler( Hashtable<DecodeHintType, Object> hints) {
            multiFormatReader = new MultiFormatReader();
            multiFormatReader.setHints(hints);
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
                Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
                Message message = Message.obtain(handler, R.id.decode_succeeded, rawResult);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Const.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
                message.setData(bundle);
                //Log.d(TAG, "Sending decode succeeded message...");
                message.sendToTarget();
            } else {
                Message message = Message.obtain(handler, R.id.decode_failed);
                message.sendToTarget();
            }
        }

    }
}
