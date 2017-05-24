package com.hxsn.witwork.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.andbase.ssk.utils.LogUtil;
import com.hxsn.witwork.MainActivity;
import com.hxsn.witwork.R;
import com.hxsn.witwork.adapter.PopListAdapter;
import com.hxsn.witwork.beans.PopListInfo;
import com.hxsn.witwork.ui.LoadingDialog;
import com.hxsn.witwork.ui.Shared;
import com.hxsn.witwork.utils.BitmapUtil;
import com.hxsn.witwork.utils.Const;
import com.hxsn.witwork.utils.HeadImgUtil;
import com.hxsn.witwork.utils.ImgBtnEffact;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class WorkDJActivity extends BaseActivity {


    ImageButton sugstBackBtn;
    TextView objectsTV, czhjTV;
    EditText contentET;
    Button submitDJBtn;
    ImageView uploadPicIV;

    LinearLayout tab1Layout, tab2Layout, tab3Layout, tab4Layout;
    Intent tabIntent;
    String infors = null;
    int mycount = 0;
    String cz = "";
    String objects = "";

    private Bitmap bmp;
    String path;
    Bitmap bitmaps;
    public static String filePath;
    public final static int CONSULT_DOC_PICTURE = 1000;
    public final static int CONSULT_DOC_CAMERA = 1001;
    private int SELECT_PICTURE = 0;
    private int SELECT_CAMERA = 1;
    public static Uri outputFileUri;

    private boolean rfidFalg = false;

    private String imgPath = null;
    private String rfIDS = null;
    private TextView startdateTV,enddateTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_dj);

        Intent inte = getIntent();
        if (inte != null) {
            if (inte.getStringExtra("result") != null)
                infors = inte.getStringExtra("result");

            if (inte.getStringExtra("RFIDS") != null) {
                rfIDS = inte.getStringExtra("RFIDS");
                rfidFalg = true;
            }
            // LogUtil.i("WorkDJActivity", "infors: " + infors);
            // LogUtil.i("WorkDJActivity", "rfIDS: " + rfIDS);
        }
        initView();
        initListener();
    }



    private void initView() {
        sugstBackBtn = (ImageButton) findViewById(R.id.sugstBackBtn);
        submitDJBtn = (Button) findViewById(R.id.submitDJBtn);
        objectsTV = (TextView) findViewById(R.id.objectsTV);
        czhjTV = (TextView) findViewById(R.id.czhjTV);
        contentET = (EditText) findViewById(R.id.contentET);
        uploadPicIV = (ImageView) findViewById(R.id.uploadPicIV);
        tab1Layout = (LinearLayout) findViewById(R.id.tab1);
        tab2Layout = (LinearLayout) findViewById(R.id.tab2);
        tab3Layout = (LinearLayout) findViewById(R.id.tab3);
        tab4Layout = (LinearLayout) findViewById(R.id.tab4);
        tabIntent = new Intent(WorkDJActivity.this, MainActivity.class);

        startdateTV= (TextView) findViewById(R.id.startdateTV);
        enddateTV= (TextView) findViewById(R.id.enddateTV);

        TextView dsfhTV = (TextView) findViewById(R.id.dsfhTV);
        dsfhTV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

        if (infors != null && infors.length() > 0) {
            // LogUtil.i("WorkDJActivity", "infors: " + infors.length());
            int idex = infors.indexOf("HJ");
            // LogUtil.i("WorkDJActivity", "idex: " + idex);

            if (idex > 0) {
                objects = infors.substring(0, idex);
                if (!objects.equals("")) {
                    String[] objList = objects.split("DK");

                    for (int i = 0; i < objList.length - 1; i++) {
                        for (int j = i + 1; j < objList.length; j++) {
                            if (objList[i].equals(objList[j])) {
                                objList[i] = "";
//								 count--;
                            }
                        }
                    }
                    LogUtil.i("WorkDJActivity", "objects: " + objects);
                    objects = "";

                    for (int i = 0; i < objList.length; i++) {
                        String msg = objList[i];
                        if (i == objList.length - 1) {
                            if (!msg.equals("")) {
                                objects += objList[i];
                                mycount++;
                            } else {
                                objects = objects.substring(0,
                                        objects.length() - 1);
                            }
                        } else {
                            if (!msg.equals("")) {
                                objects += objList[i] + ",";
                                mycount++;
                            }
                        }
                    }// END FOR
                    LogUtil.i("WorkDJActivity", "objects: " + objects);
                }// END IF 110
            }

            cz = infors.substring(idex + 2);
            getName(objects);
            czhjTV.setText(cz);
        } else {
            int ids = rfIDS.indexOf("HJ");
            // //LogUtil.i("WorkDJActivity", "rfIDS: " + rfIDS.length());
            if (ids > 0) {
                objects = rfIDS.substring(0, ids);
                String[] objList = objects.split(",");

                for (int i = 0; i < objList.length - 1; i++) {
                    for (int j = i + 1; j < objList.length; j++) {
                        if (objList[i].equals(objList[j])) {
                            objList[i] = "";
//							 count--;
                        }
                    }
                }
                LogUtil.i("WorkDJActivity", "objects: " + objects);
                objects = "";

                for (int i = 0; i < objList.length; i++) {
                    String msg = objList[i];
                    if (i == objList.length - 1) {
                        if (!msg.equals("")) {
                            objects += objList[i];
                            mycount++;
                        } else {
                            objects = objects.substring(0,
                                    objects.length() - 1);
                        }
                    } else {
                        if (!msg.equals("")) {
                            objects += objList[i] + ",";
                            mycount++;
                        }
                    }
                }// END FOR

                cz = rfIDS.substring(ids + 2);
                objectsTV.setText("已选择" + (mycount - 1) + "个操作对象：[" + objects + "]");
                czhjTV.setText(cz);
            }
        }// END ELSE

    }


    @SuppressWarnings("deprecation")
    protected void showPopu(final TextView pray_selTermTV) {
        // TODO Auto-generated method stub
        View view = LayoutInflater.from(this).inflate(R.layout.pop_list, null);
        final PopupWindow pop = new PopupWindow(view, 180, 220, false);
        // 需要设置一下此参数，点击外边可消失
        pop.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        pop.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pop.setFocusable(true);

        ListView listView = (ListView) view.findViewById(R.id.pop_listView);
        ArrayList<PopListInfo> list = new ArrayList<PopListInfo>();
        for (int i = 0; i < 24; i++) {
            PopListInfo info = new PopListInfo();
            String dates = i + ":00";
            info.setMessage(dates);
            list.add(info);
        }
        PopListAdapter adapter = new PopListAdapter(list, this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                TextView text = (TextView) view.findViewById(R.id.pop_list_text);
                text.setTextColor(Color.parseColor("#666666"));
                pray_selTermTV.setText(text.getText().toString());
                pop.dismiss();
            }
        });

        pop.showAsDropDown(pray_selTermTV);
    }



    private void getName(final String nameCode) {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams param = new RequestParams();
        try {
            param.put("app", 3);
            param.put("uid", Shared.getUserID());
            param.put("code", nameCode);
            Log.i("param", param.toString());

            // LogUtil.i("WorkDJActivity", Const.SUBMITWORK);
            // LogUtil.i("WorkDJActivity", param.toString());
            httpClient.post(Const.DK_NAME, param,
                    new AsyncHttpResponseHandler() {

                        public void onStart() {
                        }

                        public void onFailure(int arg0, Header[] arg1,
                                              byte[] arg2, Throwable arg3) {
                            arg3.printStackTrace();
                        }

                        public void onSuccess(int arg0, Header[] arg1,
                                              byte[] arg2) {
                            String s;
                            try {
                                s = new String(arg2, "UTF-8");
                                LogUtil.i("WorkDJActivity", "result: " + s);
                                JSONObject js;
                                js = new JSONObject(s);
                                String code = js.getString("code");
                                if (code.equals("200")) {
                                    JSONObject object = new JSONObject(js.getString("result"));
                                    String[] SignCode = nameCode.split(",");
                                    LogUtil.i("WorkDJActivity", nameCode);
                                    String result = "";
                                    for (int i = 0; i < SignCode.length; i++) {
                                        result += object.getString(SignCode[i]) + ",";
                                        LogUtil.i("WorkDJActivity", SignCode[i]);
                                    }
                                    result = result.substring(0, result.length() - 1);


                                    objectsTV.setText("已选择" + (mycount) + "个操作对象：\n[" + result + "]");

                                } else if(code.equals("101")) {
                                    showToast("二维码信息无效");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListener() {
        // 返回
        sugstBackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        startdateTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showPopu(startdateTV);

            }
        });

        enddateTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showPopu(enddateTV);
            }
        });


        // 拍摄选择图片
        uploadPicIV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                CharSequence[] items = { "相册", "拍摄" };
                new AlertDialog.Builder(WorkDJActivity.this).setTitle("请选择")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (which == SELECT_PICTURE) {
                                    Intent intent = new Intent(
                                            Intent.ACTION_PICK);
                                    intent.setDataAndType(
                                            MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                                            "image/*");
                                    startActivityForResult(intent,
                                            CONSULT_DOC_PICTURE);
                                } else if (which == SELECT_CAMERA) {
                                    File file = new File(Environment
                                            .getExternalStorageDirectory(),
                                            "/img.jpg");
                                    outputFileUri = Uri.fromFile(file);
                                    Intent intent = new Intent(
                                            MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                            outputFileUri);
                                    startActivityForResult(intent,
                                            CONSULT_DOC_CAMERA);
                                }
                            }
                        }).create().show();

            }
        });

        // 提交信息
        submitDJBtn.setOnTouchListener(ImgBtnEffact.btnTL);
        submitDJBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String neirong = contentET.getText().toString();

                uploadFile(czhjTV.getText().toString(), neirong);
            }
        });

        // 模块跳转
        tab1Layout.setOnClickListener(new NewIVListener());
        tab2Layout.setOnClickListener(new NewIVListener());
        tab3Layout.setOnClickListener(new NewIVListener());
        tab4Layout.setOnClickListener(new NewIVListener());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONSULT_DOC_PICTURE) {
            if (data == null) {
                imgPath = null;
                uploadPicIV.setImageResource(R.drawable.addphoto);
                return;
            }

            Uri uri = data.getData();
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = WorkDJActivity.this.managedQuery(uri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);

            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            bitmaps = BitmapFactory.decodeFile(path, newOpts);
            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            float hh = WorkDJActivity.this.getWindowManager()
                    .getDefaultDisplay().getHeight();
            float ww = WorkDJActivity.this.getWindowManager()
                    .getDefaultDisplay().getWidth();
            int be = 1;
            if (w > 1000)
                w = 1000;
            if (h > 1000)
                h = 1000;
            if (w > h && w > ww) {
                be = (int) (w / ww);
            } else if (w < h && h > hh) {
                be = (int) (h / hh);
            }
            if (be <= 0)
                be = 1;
            newOpts.inSampleSize = be;
            bitmaps = BitmapFactory.decodeFile(path, newOpts);
            if (be == 1)
                bitmaps = BitmapFactory.decodeFile(path);
            if (rfidFalg) {
                String[] p = path.split("/");
                path = path.replace(p[p.length - 1], "");
                // bitmaps = BitmapUtil.centerSquareScaleBitmap(bitmaps, 100);
                HeadImgUtil.headDelete("imgsss");
                HeadImgUtil.save(bitmaps, path, "imgsss.jpeg");
                imgPath = path + "imgsss.jpeg";
            } else {
                HeadImgUtil.headDelete("imgsss");
                imgPath = HeadImgUtil.getImgParentPath() + "imgsss.jpeg";
                HeadImgUtil.save(bitmaps, HeadImgUtil.getImgParentPath(),
                        "imgsss.jpeg");
            }
            // uploadFile(HeadImgUtil.getImgParentPath() + Shared.getUserID() +
            // ".jpeg");

            uploadPicIV.setImageBitmap(bitmaps);

        } else if (requestCode == CONSULT_DOC_CAMERA) {
            // LogUtil.i("WorkDJActivity", "CONSULT_DOC_CAMERA");
            if (bmp != null)
                bmp.recycle();
            String path = Environment.getExternalStorageDirectory()
                    + "/img.jpg";

            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            bmp = BitmapFactory.decodeFile(path, newOpts);
            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
            @SuppressWarnings("deprecation")
            float hh = getWindowManager().getDefaultDisplay().getHeight();// 这里设置高度
            @SuppressWarnings("deprecation")
            float ww = getWindowManager().getDefaultDisplay().getWidth();// 这里设置宽度
            // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;// be=1表示不缩放
            if (w > 1000)
                w = 1000;
            if (h > 1000)
                h = 1000;
            if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
                be = (int) (w / ww);
            } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
                be = (int) (h / hh);
            }
            if (be <= 0)
                be = 1;

            newOpts.inSampleSize = be;// 设置缩放比例
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            bmp = BitmapFactory.decodeFile(path, newOpts);
            bmp = BitmapUtil.centerSquareScaleBitmap(bmp, 100);
            HeadImgUtil.headDelete("img");
            HeadImgUtil.save(bmp, HeadImgUtil.getImgParentPath(), "img.jpeg");
            uploadPicIV.setImageBitmap(bmp);

            newOpts.inSampleSize = be;// 设置缩放比例
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            bmp = BitmapFactory.decodeFile(path, newOpts);
            bmp = BitmapUtil.centerSquareScaleBitmap(bmp, 100);

            if (HeadImgUtil.save(bmp, HeadImgUtil.getImgParentPath(),
                    "img.jpeg")) {
                imgPath = HeadImgUtil.getImgParentPath() + "img.jpeg";
                uploadPicIV.setImageBitmap(bmp);
            } else {
                imgPath = null;
                uploadPicIV.setImageResource(R.drawable.addphoto);
            }

        } else {
            showToast("");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadFile(String hjname, String neirong) {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams param = new RequestParams();
        try {

            param.put("app", "3");
            param.put("uid", Shared.getUserID());
            if (rfidFalg) {
                param.put("dkcodes", "RF" + objects);
            } else {
                param.put("dkcodes", "DK" + objects.replace(",", ",DK"));
            }
            param.put("hjname", "HJ" + hjname);
            param.put("neirong", neirong);
            if (imgPath != null) {
                param.put("filedata", new File(imgPath));// imgPath
            }
            if(startdateTV.getText().toString().equals("请选择")||enddateTV.getText().toString().equals("请选择"))
            {
            }
            else
            {
                int start=Integer.parseInt(startdateTV.getText().toString());
                int end=Integer.parseInt(enddateTV.getText().toString());
//				if(start>end)
//				{
//					Toast.makeText(getApplicationContext(), "时间段的设置不正确", Toast.LENGTH_LONG).show();
//				}
//				else
//				{
                param.put("starttime",start );
                param.put("endtime", end);
//}
            }
            Log.i("param", param.toString());
            // LogUtil.i("WorkDJActivity", Const.SUBMITWORK);
            // LogUtil.i("WorkDJActivity", param.toString());
            httpClient.post(Const.SUBMITWORK, param,
                    new AsyncHttpResponseHandler() {

                        public void onStart() {
                            LoadingDialog.showLoading(WorkDJActivity.this,
                                    "正在上传作业信息...");
                        }

                        public void onFailure(int arg0, Header[] arg1,
                                              byte[] arg2, Throwable arg3) {
                            arg3.printStackTrace();
                            LoadingDialog.dissmissLoading();
                            showToast("作业信息提交失败");
                        }

                        public void onSuccess(int arg0, Header[] arg1,
                                              byte[] arg2) {
                            String s;
                            LoadingDialog.dissmissLoading();
                            try {
                                s = new String(arg2, "GB2312");
                                LogUtil.i("WorkDJActivity", s);
                                JSONObject js;
                                js = new JSONObject(s);
                                LogUtil.i("WorkDJActivityCode",
                                        js.getString("code"));
                                if (js.getString("code").equals("200")) {
                                    showToast("作业信息提交成功");
                                    finish();
                                    // uploadPicIV.setImageBitmap(null);
                                    // bitmaps =
                                    // BitmapFactory.decodeFile(imgPath);
                                    // //uploadPicIV.setImageBitmap(bitmaps);
                                } else {
                                    showToast("作业信息提交失败");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                showToast("作业信息提交失败");
                            }

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            LoadingDialog.dissmissLoading();
            showToast("数据加载异常");
        }
    }

    class NewIVListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            // TODO Auto-generated method stub

            switch (view.getId()) {
                case R.id.tab1:
                    tabIntent.putExtra("tag", 0);
                    startActivity(tabIntent);
                    finish();
                    break;
                case R.id.tab2:
                    tabIntent.putExtra("tag", 1);
                    startActivity(tabIntent);
                    finish();
                    break;
                case R.id.tab3:
                    tabIntent.putExtra("tag", 2);
                    startActivity(tabIntent);
                    finish();
                    break;
                case R.id.tab4:
                    tabIntent.putExtra("tag", 3);
                    startActivity(tabIntent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
}
