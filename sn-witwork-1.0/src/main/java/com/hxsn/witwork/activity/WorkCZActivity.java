package com.hxsn.witwork.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andbase.ssk.utils.LogUtil;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hxsn.witwork.MainActivity;
import com.hxsn.witwork.R;
import com.hxsn.witwork.beans.Printer;
import com.hxsn.witwork.ui.LoadingDialog;
import com.hxsn.witwork.ui.Shared;
import com.hxsn.witwork.utils.Const;
import com.hxsn.witwork.utils.ImgBtnEffact;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

public class WorkCZActivity extends BaseActivity {
    String code = "";
    String lvnums="";
    ImageButton shczBackBtn;
    TextView objecNameTV,unitTV,setllTV,setqdTV,setdyewmTV;
    EditText cznumET;
    Button subshBtn;
    Printer m_printer;
    //DKN1A03
    LinearLayout tab1Layout, tab2Layout, tab3Layout, tab4Layout;
    Intent tabIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_cz);

        m_printer =new Printer();

        initView();
        Intent inte = getIntent();
        if (inte != null) {
            code = inte.getStringExtra("code");
            getInfors();
        }

        initListener();
    }


    private void getInfors() {
        String uid = "";
        if (Shared.getUserID().length() > 0)
            uid = Shared.getUserID();
        LoadingDialog.showLoading(this);
        try {
            StringRequest stringRequest = new StringRequest(Const.SHSM + uid+"&code="+code, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject j = null;
                    try {
                        try {
                            j = new JSONObject(new String(response.getBytes(),
                                    "UTF-8"));
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            showToast("解析异常");
                            e.printStackTrace();
                        }
                        if (j.getString("code").equals("200")) {
                            //{"code":200,"result":{"name":"芹菜",//生成的商品名称 "lvnums":"5",//履历信息个数"units":"Kg"}}
                            JSONObject newObject =j.getJSONObject("result");
                            String name = newObject.getString("name");
                            lvnums = newObject.getString("lvnums");
                            String units = newObject
                                    .getString("units");
                            if (name.length() > 0)
                                objecNameTV.setText(name);
                            if (units.length() > 0)
                                unitTV.setText(units);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("解析异常");
                    }
                    LoadingDialog.dissmissLoading();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("eeeeeeee", error.getMessage(), error);
                    showToast("网络异常");
                    LoadingDialog.dissmissLoading();
                }
            });
            mQueue.add(stringRequest);
        } catch (Exception w) {
            w.printStackTrace();
            LoadingDialog.dissmissLoading();
        }

    }

    private void initView() {
        shczBackBtn=(ImageButton)findViewById(R.id.shczBackBtn);
        objecNameTV=(TextView)findViewById(R.id.objecNameTV);
        cznumET=(EditText)findViewById(R.id.cznumET);
        unitTV = (TextView) findViewById(R.id.unitTV);
        setllTV=(TextView)findViewById(R.id.setllTV);
        setqdTV=(TextView)findViewById(R.id.setqdTV);
        setdyewmTV=(TextView)findViewById(R.id.setdyewmTV);

        subshBtn=(Button)findViewById(R.id.subshBtn);

        TextView dsfhTV=(TextView)findViewById(R.id.dsfhTV);
        dsfhTV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

        tab1Layout = (LinearLayout) findViewById(R.id.tab1);
        tab2Layout = (LinearLayout) findViewById(R.id.tab2);
        tab3Layout = (LinearLayout) findViewById(R.id.tab3);
        tab4Layout = (LinearLayout) findViewById(R.id.tab4);
        tabIntent = new Intent(WorkCZActivity.this, MainActivity.class);

        // 模块跳转
        tab1Layout.setOnClickListener(new NewIVListener());
        tab2Layout.setOnClickListener(new NewIVListener());
        tab3Layout.setOnClickListener(new NewIVListener());
        tab4Layout.setOnClickListener(new NewIVListener());
    }
    // 生成QR图
    private void createImage(String text) {
        int QR_WIDTH=200;
        int QR_HEIGHT=150;
        try {
            // 需要引入core包
            QRCodeWriter writer = new QRCodeWriter();


            if (text == null || "".equals(text) || text.length() < 1) {
                return;
            }

            // 把输入的文本转为二维码
            BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE,
                    QR_WIDTH	, QR_HEIGHT);

            System.out.println("w:" + martix.getWidth() + "h:"
                    + martix.getHeight());

            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }

                }
            }

            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
                    Bitmap.Config.ARGB_8888);

            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            if(bitmap!=null)
            {
                m_printer.PrintBitmap(bitmap);
            }


        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
    private void initListener() {

        setllTV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(setllTV.getText().equals("关闭"))
                    setllTV.setText("打开");
                else
                    setllTV.setText("关闭");
            }
        });

        setqdTV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(setqdTV.getText().equals("关闭"))
                    setqdTV.setText("打开");
                else
                    setqdTV.setText("关闭");
            }
        });

        setdyewmTV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(setdyewmTV.getText().equals("关闭"))
                    setdyewmTV.setText("打开");
                else
                    setdyewmTV.setText("关闭");
            }
        });


        subshBtn.setOnTouchListener(ImgBtnEffact.btnTL);
        subshBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name=objecNameTV.getText().toString();
                String nums=cznumET.getText().toString();
                String caidan="";
                if(setllTV.getText().equals("关闭"))
                    caidan="0";
                else
                    caidan="1";
                String songcai="";
                if(setqdTV.getText().equals("关闭"))
                    songcai="0";
                else
                    songcai="1";
                String qrcode="";
                if(setdyewmTV.getText().equals("关闭"))
                    qrcode="0";
                else
                    qrcode="1";

                //LoadingDialog.showLoading(WorkCZActivity.this,2);
                /**提交收货接口
                 code	地块编码，扫描的二维码内容
                 name	商品名称
                 lvnums	履历信息个数
                 caidan	更新菜单主料：0不更新、1更新
                 songcai	更新送菜清单：0不更新、1更新
                 qrcode	打印二维码：0不打印、1打印（手持二维码打印机用）
                 nums	收获数量**/
                if(name.length()>0&&nums.length()>0)
                {

                    AsyncHttpClient httpClient = new AsyncHttpClient();
                    RequestParams param = new RequestParams();
                    try {
                        // "&code=" + code+"&name="+name+"&lvnums="+lvnums+"&caisan="+caidan+
//					"&songcai="+songcai+"&qrcode="+qrcode+"&nums="+nums
                        param.put("app", "3");
                        param.put("uid", Shared.getUserID());
                        param.put("code", code);
                        param.put("name", name);
                        param.put("lvnums", lvnums);
                        param.put("caisan", caidan);
                        param.put("songcai", songcai);
                        param.put("qrcode", qrcode);
                        param.put("nums", nums);
                        LogUtil.i("WorkCZActivity", Const.SUBMITSH);
                        LogUtil.i("WorkCZActivity", param.toString());
                        httpClient.post(Const.SUBMITSH, param,
                                new AsyncHttpResponseHandler() {

                                    public void onStart() {
                                        LoadingDialog.showLoading(WorkCZActivity.this,
                                                "正在提交...");
                                    }

                                    public void onFailure(int arg0, Header[] arg1,
                                                          byte[] arg2, Throwable arg3) {
                                        arg3.printStackTrace();
                                        LoadingDialog.dissmissLoading();
                                        showToast("提交失败...");
                                    }

                                    public void onSuccess(int arg0, Header[] arg1,
                                                          byte[] arg2) {
                                        String s;
                                        LoadingDialog.dissmissLoading();
                                        try {
                                            s = new String(arg2, "GB2312");
                                            LogUtil.i("WorkCZActivity", s);
                                            JSONObject js;
                                            js = new JSONObject(s);
                                            if (js.getString("code").equals("200")) {
                                                showToast("提交成功");
                                                LoadingDialog.dissmissLoading();

                                                /**
                                                 * 手持二维码打印机 ---- 打印信息
                                                 * */
                                                String qrcode="";
                                                if(setdyewmTV.getText().equals("关闭"))
                                                    qrcode="0";
                                                else
                                                    qrcode="1";
                                                String name=objecNameTV.getText().toString();
                                                String nums=cznumET.getText().toString();

                                                if(!m_printer.IsOutOfPaper()){


                                                    if(qrcode.equals("1")){
                                                        m_printer.Open();

                                                        createImage(js.getString("result")) ;
                                                        m_printer.PrintLineInit(88);
                                                        //		m_printer.PrintLineString("产品名称", 24, PrintType.Left, true);
                                                        m_printer.PrintLineString(name, 40, Printer.PrintType.TopCentering, true);
                                                        m_printer.PrintLineEnd();
                                                        m_printer.Close();

                                                    }

                                                    finish();
                                                }else{
                                                    showToast("缺纸！！请放入纸张");
                                                }


                                            } else {
                                                showToast("提交失败");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            showToast("提交失败");
                                        }

                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                        LoadingDialog.dissmissLoading();
                    }

                }}
        });


        shczBackBtn.setOnTouchListener(ImgBtnEffact.btnTL);
        shczBackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    class NewIVListener implements View.OnClickListener {
        public void onClick(View view) {
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
