package com.hxsn.iot.uitls;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.hxsn.iot.TApplication;
import com.videogo.exception.InnerException;
import com.videogo.openapi.EZPlayer;
import com.videogo.util.MediaScanner;
import com.videogo.util.SDCardUtil;
import com.videogo.util.Utils;


/**
 * Created by jiely on 2017/2/10.
 */
public class RealPlayUtil {

    /**
     * 抓拍按钮响应函数
     *
     * @since V1.0
     */
    public static void onCapturePicBtnClick(final Context context,final Activity activity, final EZPlayer mEZPlayer) {

        if (SDCardUtil.getSDCardRemainSize() < SDCardUtil.PIC_MIN_MEM_SPACE) {
            // 提示内存不足
            Utils.showToast(context, "内存不足");
            return;
        }

        if (mEZPlayer != null) {
            //mCaptureDisplaySec = 4;
            //updateCaptureUI();
            AudioPlayUtil.getInstance(TApplication.instance).playAudioFile(AudioPlayUtil.CAPTURE_SOUND);
            Thread thr = new Thread() {
                @Override
                public void run() {
                    Bitmap bmp = mEZPlayer.capturePicture();
                    if (bmp != null) {
                        try {// 可以采用deviceSerial+时间作为文件命名，demo中简化，只用时间命名
                            java.util.Date date = new java.util.Date();
                            final String path = Environment.getExternalStorageDirectory().getPath() + "/EZOpenSDK/CapturePicture/" + String.format("%tY", date)
                                    + String.format("%tm", date) + String.format("%td", date) + "/"
                                    + String.format("%tH", date) + String.format("%tM", date) + String.format("%tS", date) + String.format("%tL", date) +".jpg";

                            if (TextUtils.isEmpty(path)) {
                                bmp.recycle();
                                bmp = null;
                                return;
                            }
                            EZUtils.saveCapturePictrue(path, bmp);

                            MediaScanner mMediaScanner = new MediaScanner(context);
                            mMediaScanner.scanFile(path, "jpg");

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "已保存至相册"+path, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (InnerException e) {
                            e.printStackTrace();
                        } finally {
                            if (bmp != null) {
                                bmp.recycle();
                                return;
                            }
                        }
                    }
                    super.run();
                }
            };
            thr.start();
        }
    }
}
