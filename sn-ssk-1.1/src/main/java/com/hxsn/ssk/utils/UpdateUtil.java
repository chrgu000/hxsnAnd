package com.hxsn.ssk.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.andbase.library.util.AbAppUtil;
import com.andbase.library.util.AbFileUtil;
import com.andbase.ssk.entity.AppVersion;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class UpdateUtil {

    private static String urlString;
    private static final String CODE_NSH_VERSION_KEY="nsh_version_key";
    public static final File SDCARD_ROOT = Environment.getExternalStorageDirectory();// .getAbsolutePath();
    private static final String PATH_APK = SDCARD_ROOT + "/apkDownload";
    private static final int TIMEOUT = 10 * 1000;

    public static void show(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }


    /**
     * desc: APP版本升级
     * auther:jiely
     * create at 2015/11/4 11:06
     */
    public static void updateAPP(final Context context,String url) {
        if (AbAppUtil.isWifi(context)) {
            updateNowifiApp(context,url);
        }
    }


    /**
     *  没有wifi情况下也能升级
     * @param context 11
     * @param url 11
     */
    public static void updateNowifiApp(final Context context,String url) {
        new AndHttpRequest(context) {
            @Override
            public void getResponse(String response) {
               // String jsonString = HttpRequest.result;
                AppVersion appVersion = AndJsonUtils.getAppVersion(response);
                urlString = appVersion.getUrl();
                if (appVersion != null) {
                    String thisAppVersion = UpdateUtil.getThisAppVersion(context);
                    if (UpdateUtil.isNewVersion(appVersion.getVersion(), thisAppVersion)) {
                        new AlertDialog.Builder(context)
                                .setMessage("是否升级")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        UpdateUtil.show(context, "APP在下载中，请稍等");
                                        try {
                                            new DownApkTask(context).execute(urlString);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).setNegativeButton("取消", null).show();
                    }
                }
            }
        }.doGet(url);
    }


    /**
     * desc:下载文件到本地，url为网络地址 absolutePath为文件地址
     * auther:jiely
     * create at 2015/10/10 20:35
     */
    public static long downloadFileToLocal(String down_url, String absolutePath, Handler handler) throws Exception {
        int down_step = 5;// 提示step
        int totalSize;// 文件总大小
        int downloadCount = 0;// 已经下载好的大小
        int updateCount = 0;// 已经上传的文件大小
        InputStream inputStream;
        OutputStream outputStream;
        URL url = new URL(down_url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(TIMEOUT);
        httpURLConnection.setReadTimeout(TIMEOUT);
        // 获取下载文件的size
        totalSize = httpURLConnection.getContentLength();
        if (httpURLConnection.getResponseCode() == 404) {
            throw new Exception("fail!");
        }
        inputStream = httpURLConnection.getInputStream();


        File file = new File(absolutePath);

        if (!file.exists()) {
            file.createNewFile();
        }

        if (file.isDirectory()) {
            return 0;
        }

        outputStream = new FileOutputStream(absolutePath, false);// 文件存在则覆盖掉
        byte buffer[] = new byte[1024];
        int readsize = 0;
        while ((readsize = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, readsize);
            downloadCount += readsize;// 时时获取下载到的大小
            /**
             * 每次增张5%
             */
            if (updateCount == 0 || (downloadCount * 100 / totalSize - down_step) >= updateCount) {
                updateCount += down_step;
            }

            if (handler != null) {
                Integer progress = downloadCount * 100 / totalSize;
                //TApplication.keyValueMap.put(Const.DOWNLOAD_PROGRESS, progress);
                Message message = handler.obtainMessage();
                message.what = 11;//Const.MSG_DOWNLOAD_PROGRESS;
                handler.sendMessage(message);
            }

        }

        if (handler != null) {
            Message message = handler.obtainMessage();
            message.what = 12;//Const.MSG_DOWNLOAD_OK;
            handler.sendMessage(message);
        }


        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
        inputStream.close();
        outputStream.close();

        return downloadCount;
    }



    /**
     * desc: 获取当前版本
     * auther:jiely
     * create at 2015/11/24 10:00
     */
    public static String getThisAppVersion(Context context) {

        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    //dp与px转换的方法：
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }



    /**
     * desc:判断版本号是否是新的
     * auther:jiely
     * create at 2015/11/23 19:39
     */
    public static boolean isNewVersion(String newVersion, String oldVersion) {
        String[] olds = oldVersion.split("\\.");
        String[] news = newVersion.split("\\.");

        for (int i = 0; i < olds.length; i++) {
            Integer newv = Integer.parseInt(news[i]);
            Integer oldv = Integer.parseInt(olds[i]);

            if (newv > oldv) {
                return true;
            } else if (newv < oldv) {
                return false;
            } else {
                if (i == olds.length - 1) {
                    return false;
                }
            }
        }
        return false;
    }


    static class DownApkTask extends AsyncTask<String, Void, Integer> {
        private String absolutePath;
        private Context context;
        public DownApkTask(Context context){
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... params) {
            String path;
            try {
                Boolean isSuccess = AbFileUtil.deleteFile(new File(PATH_APK));

                String fileName = AbFileUtil.getCacheFileNameFromUrl(urlString);
                absolutePath = PATH_APK +"/"+ fileName;
                if (!isSuccess) {//如果删除失败，再次删除文件夹下的指定文件，再次删除失败，就不删除了，而是创建新的文件夹和文件 SDCARD_ROOT + "/apkDownloadTemp"
                    boolean isDeleteSuccess = AbFileUtil.deleteFile(new File(absolutePath));
                    if(!isDeleteSuccess){
                        absolutePath = PATH_APK +"Temp/"+ fileName;
                        AbFileUtil.deleteFile(new File(PATH_APK));
                    }
                }

                path = AbFileUtil.downloadFile(urlString, absolutePath);//.downloadFileToLocal(urlString, absolutePath, null);
                if (!TextUtils.isEmpty(path) ) {
                    return 200;
                }
            } catch (Exception e) {
                e.printStackTrace();

                return 500;
            }

            return 500;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == 200) {
                // 下载完成，点击安装
                File file = new File(absolutePath);
                Uri uri = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                context.startActivity(intent);
            } else if (result == 500) {
                UpdateUtil.show(context, "下载失败");
            } else {
                UpdateUtil.show(context, "文件错误");
            }
        }
    }

    public static final int  EXIT_APPLICATION = 0x0001;
    //完全退出应用
    public static void exit(Context mContext,Class mainActivity){

//      1.5 - 2.1之前下面两行是ok的,2.2之后就不行了，所以不通用
//      ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
//      am.restartPackage("com.tutor.exit");

        Intent mIntent = new Intent();
        mIntent.setClass(mContext, mainActivity);
        //这里设置flag还是比较 重要的
        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //发出退出程序指示
        mIntent.putExtra("flag", EXIT_APPLICATION);
        mContext.startActivity(mIntent);
    }
}
