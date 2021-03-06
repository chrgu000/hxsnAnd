package com.hxsn.ssk.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andbase.library.http.model.AbRequestParams;
import com.andbase.library.util.AbDateUtil;
import com.andbase.library.util.AbFileUtil;
import com.andbase.library.util.AbToastUtil;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.hxsn.ssk.R;
import com.hxsn.ssk.TApplication;
import com.hxsn.ssk.base.BasePicAdd;
import com.hxsn.ssk.base.BaseTitle;
import com.hxsn.ssk.utils.Const;

import java.io.File;


public class SendQuestActivity extends Activity implements View.OnClickListener{
    private EditText edtInfo;
    private TextView txtSend;
    private TextView txtPhotograph, txtPicture, txtCancel;
    private ImageView imgAdd;
    private BasePicAdd basePicAdd;
    //private boolean isAddPic = false;//图片是否已添加
    private String imgPath="";//添加活动图片的路径
    private String fileName="";//添加活动图片的文件名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_problem);

        BaseTitle.getInstance(this).setTitle("提问题");
        addView();
        addListener();
    }


    private void addListener() {
        txtSend.setOnClickListener(this);
        imgAdd.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
        txtPhotograph.setOnClickListener(this);
        txtPicture.setOnClickListener(this);

    }

    private void addView() {
        edtInfo = (EditText)findViewById(R.id.edt_info);
        txtPicture = (TextView) findViewById(R.id.txt_picture);
        txtPhotograph = (TextView) findViewById(R.id.txt_photograph);
        txtCancel = (TextView) findViewById(R.id.txt_cancel);
        txtSend = (TextView)findViewById(R.id.txt_send);
        imgAdd = (ImageView)findViewById(R.id.img_add_pic);
    }

    @Override
    public void onClick(View v) {
        if(basePicAdd != null){
            boolean is = basePicAdd.onClick(v);
            if(is){
                return;
            }
        }

        switch (v.getId()){
            case R.id.txt_send:

                final String info = edtInfo.getText().toString();
                if(info.length() == 0){
                    Toast.makeText(this, "请输入描述信息", Toast.LENGTH_SHORT).show();
                    break;
                }

                String url = Const.URL_ASK_QUEST;
                AbRequestParams map = new AbRequestParams();
                map.put("userid", TApplication.user.getUserId());
                map.put("username", TApplication.user.getUserName());
                map.put("neirong", info);

                File file;
                if(imgPath.length() > 0){
                    file = new File(imgPath);
                    map.put(imgPath,file);
                }

                new AndHttpRequest(TApplication.context) {
                    @Override
                    public void getResponse(String response) {
                        String code = AndJsonUtils.getCode(response);
                        if(code.equals("200")){
                            AbToastUtil.showToast(SendQuestActivity.this, "提交问题成功");
                            onBackPressed();
                        }else {
                            AbToastUtil.showToast(SendQuestActivity.this, AndJsonUtils.getResult(response));
                        }
                        //AndroidUtil.show(SendQuestActivity.this, "提交问题成功");
                        //onBackPressed();
                    }
                }.doPost(url,map);

                break;
            case R.id.img_add_pic:
                if(basePicAdd == null){
                    basePicAdd = new BasePicAdd(this);
                }
                basePicAdd.setVisible();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(basePicAdd != null){
            basePicAdd.onActivityResult(requestCode,data);
            if(requestCode == 3){
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        try {
                            //把照片暂存至Const.PATH_SHOP_TEMP文件夹
                            Bitmap bitmap = bundle.getParcelable("data");
                            imgAdd.setImageBitmap(bitmap);

                            fileName = AbDateUtil.getCurrentDate("yyyy-MM-dd")+".jpg";
                            imgPath = Const.PATH_WEN_IMAGE + fileName;
                            AbFileUtil.writeBitmapToSD(imgPath,bitmap);
                            //isAddPic = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
