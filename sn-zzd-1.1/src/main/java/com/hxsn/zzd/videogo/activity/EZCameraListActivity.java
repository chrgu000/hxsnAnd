package com.hxsn.zzd.videogo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.AndShared;
import com.andbase.ssk.utils.LogUtil;
import com.andbase.ssk.utils.PermissionUtils;
import com.hxsn.zzd.MainActivity;
import com.hxsn.zzd.R;
import com.hxsn.zzd.TApplication;
import com.hxsn.zzd.utils.Const;
import com.hxsn.zzd.utils.JsonUtils;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.util.LocalInfo;

/**
 * 摄像头列表Activity
 */
public class EZCameraListActivity extends Activity implements View.OnClickListener{


    static final String[] PERMISSION = new String[]{
            android.Manifest.permission.READ_PHONE_STATE,        //读取设备信息
            Manifest.permission.CAMERA,                          //摄像头
    };

    private String TAG = "EZCameraListActivity";
    private ListAdapter mAdapter;
    private ListView listView;
    private ImageButton btnAdd;//添加设备的按钮
    private String tempUrl;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ezcamera_list);

        addView();
        //动态申请CODE_READ_PHONE_STATE权限
        if(Integer.parseInt(android.os.Build.VERSION.SDK) < 23){
            getAllGrantedPermission();
        }else {
            PermissionUtils.requestPermission(this, PermissionUtils.CODE_READ_PHONE_STATE, new PermissionUtils.PermissionGrant() {
                @Override
                public void onPermissionGranted(int requestCode) {
                    getAllGrantedPermission();
                }
            });
        }

       // obtainTokenAndCameraList(0);
    }

    private void getAllGrantedPermission() {
        initSDK();
        //验证token是否过期，然后获取摄像头列表
        checkAccessToken();
    }

    /**
     * 用于权限管理
     * 如果全部授权的话，则直接通过进入
     * 如果权限拒绝，缺失权限时，则使用dialog提示
     *
     * @param requestCode  请求代码
     * @param permissions  权限参数
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtil.i(MainActivity.class,"***********onRequestPermissionsResult************");

        PermissionUtils.requestPermissionsResult(this, PermissionUtils.CODE_READ_PHONE_STATE, permissions, grantResults, new PermissionUtils.PermissionGrant() {
            @Override
            public void onPermissionGranted(int requestCode) {
                getAllGrantedPermission();
            }
        });
    }


    /**
     * 萤石云视频监控初始化
     */
    private void initSDK() {
        /**********国内版本初始化EZOpenSDK**************/
        {
            /**
             * sdk日志开关，正式发布需要设置未false
             */
            EZOpenSDK.showSDKLog(false);

            /**
             * 设置是否支持P2P取流,详见api
             */
            EZOpenSDK.enableP2P(true);

            /**
             * APP_KEY请替换成自己申请的
             */
            EZOpenSDK.initLib(TApplication.instance, "896e18cccd614e9ab54daf169e9e7348", "");
        }
    }

    /**
     * 验证token是否过期，如果本地没有token,则想萤石云要token
     * 通过访问萤石云获取摄像头列表的接口的返回参数code是否为10002，如果是则调用zzd的获取设备接口，并发送参数reloadToken=1
     */
    private void checkAccessToken() {
        accessToken = AndShared.getValue("accessToken");
        Log.i("EZCameraListActivity","----token="+accessToken+"------------");
        tempUrl = Const.URL_GET_ACCESS_TOKEN;
        if(accessToken.length() == 0){
            new AndHttpRequest(EZCameraListActivity.this) {
                @Override
                public void getResponse(String response) {
                    accessToken = JsonUtils.getAccessToken(response);
                    Log.i("EZCameraListActivity","----token-new="+accessToken+"------------");
                    /*********************!!!!!!非常关键*********************************/
                    LocalInfo.getInstance().setAccessToken(accessToken);

                    AndShared.setValue("accessToken",accessToken);
                    visitYsy();
                }
            }.doPost(tempUrl);
        }else {
            visitYsy();
        }

    }

    private void addView() {
        listView = (ListView) findViewById(R.id.list);
        btnAdd = (ImageButton)findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);
    }

    private void visitYsy(){

        tempUrl = Const.URL_GET_CAMERA_LIST+accessToken;
        new AndHttpRequest(EZCameraListActivity.this) {
            @Override
            public void getResponse(String response) {
                String code = AndJsonUtils.getCode(response);
                if(code.equals("10002")){
                    obtainTokenAndCameraList(1);
                }else {
                    obtainTokenAndCameraList(0);
                }
            }
        }.doPost(tempUrl);
    }

    /**
     *获取accessToken和摄像头列表从zzd的后台
     * @param reloadToken 0-不需要获取token，1-需要获取token
     */
    public void obtainTokenAndCameraList(final int reloadToken){
        tempUrl = Const.URL_GET_CAMERA_LIST_FOR_HXSN+TApplication.user.getUserId()+"&reloadToken="+reloadToken;
        //tempUrl = Const.URL_GET_CAMERA_LIST+accessToken;
        new AndHttpRequest(EZCameraListActivity.this){
            @Override
            public void getResponse(String response) {
                String code = AndJsonUtils.getCode(response);
                if(code.equals("200")){
                    String accessToken = JsonUtils.getTokenFromZzd(response);
                    if(reloadToken == 1){
                        LocalInfo.getInstance().setAccessToken(accessToken);
                    }
                    AndShared.setValue("accessToken",accessToken);
                    TApplication.cameraInfoList = JsonUtils.getCameraListForZzd(response);
                    mAdapter = new ListAdapter(EZCameraListActivity.this);
                    listView.setAdapter(mAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TApplication.cameraInfo = TApplication.cameraInfoList.get(position);

                            Intent intent = new Intent(EZCameraListActivity.this, RealPlayActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        }.doPost(tempUrl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_left:
                onBackPressed();
                break;
            case R.id.btn_add:
                Intent intent = new Intent();
                intent.setClass(this,CaptureActivity.class);
                startActivity(intent);
                break;
        }
    }


    static class ListAdapter extends BaseAdapter{

        private LayoutInflater inflater;

        public ListAdapter(Context context) {
            super();
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return TApplication.cameraInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return TApplication.cameraInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_cameral, parent, false);
                viewHolder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
                viewHolder.rlPlay = (RelativeLayout)convertView.findViewById(R.id.rl_left);
                viewHolder.imgHead = (ImageView) convertView.findViewById(R.id.img_head);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.txtName.setText(TApplication.cameraInfoList.get(position).getCameraName());
            return convertView;
        }
    }

    //     ViewHolder 模式, 效率提高 50%
    static class ViewHolder {
        TextView txtName;
        RelativeLayout rlPlay;
        ImageView imgHead;
    }

}
