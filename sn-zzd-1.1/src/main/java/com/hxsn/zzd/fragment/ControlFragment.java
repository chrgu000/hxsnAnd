package com.hxsn.zzd.fragment;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andbase.library.http.model.AbRequestParams;
import com.andbase.library.util.AbToastUtil;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.PermissionUtils;
import com.hxsn.zzd.R;
import com.hxsn.zzd.TApplication;
import com.hxsn.zzd.utils.Const;

import static com.hxsn.zzd.TApplication.context;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ControlFragment extends Fragment implements View.OnClickListener{
    private TextView txtName,txtStatus;
    private ImageView imgStart,imgStop;
    private String telPhone,phoneType;//本机号码，手机型号
    //private Button btnRegister;//分机号码登记

    public ControlFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_control, container, false);


        addView(view);
        addListener();

        obtainDeviceStatus();
        return view;
    }



    private void addListener() {
        imgStart.setOnClickListener(this);
        imgStop.setOnClickListener(this);

    }



    private void addView(View view) {
        txtName = (TextView)view.findViewById(R.id.txt_name);
        txtStatus = (TextView)view.findViewById(R.id.txt_status);
        imgStart = (ImageView)view.findViewById(R.id.img_start);
        imgStop = (ImageView)view.findViewById(R.id.img_stop);
        txtName.setText(TApplication.defaultGreenHouse.getName());

    }

    private void obtainDeviceStatus() {
         
        if(TApplication.defaultGreenHouse == null ||  TApplication.user == null){
            return;
        }
        String url = Const.URL_DEVICE_STATUS + TApplication.user.getUserId()+"&dyid="+ TApplication.defaultGreenHouse.getId();
        new AndHttpRequest(context) {
            @Override
            public void getResponse(String response) {
                String result = AndJsonUtils.getResult(response);
                if(result.equals("KZW")){//没有未读预警
                    txtStatus.setText("当前状态：自动模式已启动");
                }else if (result.equals("TZW")){
                    txtStatus.setText("当前状态：自动模式已停止");
                }else {
                    txtStatus.setText("当前状态：自动模式未知");
                }
            }
        }.doGet(url);
    }


    int[] permissionIndex = new int[]{PermissionUtils.CODE_READ_PHONE_STATE,PermissionUtils.CODE_BODY_SENSORS};
    @Override
    public void onClick(final View v) {

        switch(v.getId()){
            case R.id.img_start:
            case R.id.img_stop:

                //动态申请CODE_READ_PHONE_STATE权限
                /*if(Integer.parseInt(android.os.Build.VERSION.SDK) < 23){
                    sendControlDemand(v);
                }else {
                    PermissionUtils.requestMultiPermissions(getActivity(),permissionIndex , new PermissionUtils.PermissionGrant() {
                        @Override
                        public void onPermissionGranted(int requestCode) {
                            sendControlDemand(v);
                        }
                    });
                }*/

                sendControlDemand(v);

                break;

        }
    }

    private void sendControlDemand(View v){
        AbRequestParams map = new AbRequestParams();
        map.put("uid", TApplication.user.getUserId());
        map.put("dyid", TApplication.defaultGreenHouse.getId());
        String cmd = "";
        if(v.getId() == R.id.img_start){
            cmd += "KZW";
        }else{
            cmd += "TZW";
        }
        map.put("cmd",cmd);
        map.put("telPhone",TApplication.user.getPhone());
        map.put("phoneModel",phoneType);

       /* if(telPhone.length() == 0){
            AbToastUtil.showToast(context,"读取手机号失败，请确认您的手机是否安装了手机卡");
            return;
        }*/

        new AndHttpRequest(context) {
            @Override
            public void getResponse(String response) {
                String result = AndJsonUtils.getResult(response);
                String code = AndJsonUtils.getCode(response);
                if(code.equals("200")){
                    if(result.equals("TZW")){
                        txtStatus.setText("当前状态：自动模式已停止");
                    }else if(result.equals("KZW")){
                        txtStatus.setText("当前状态：自动模式已启动");
                    }else {
                        AbToastUtil.showToast(context,result);
                    }
                //}else if(code.equals("100")){
                //    setDialog();
                }else {
                    AbToastUtil.showToast(context,result);
                }
            }
        }.doPost(Const.URL_CONTROL_DEVICE,map);
    }

    /*private void setDialog() {
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(getActivity());
        normalDialog.setTitle("手机号鉴权失败");
        normalDialog.setMessage("你的手机号码是"+telPhone
                +",而用户绑定的手机号码是"+ TApplication.user.getPhone()+"，如果你想对设备进行控制，你需要让已绑定手机号码的手机在APP中对您的手机号码进行授权登记，登记成功后就可以控制了。如果用户绑定的手机号码不正确，请联系管理员为您添加手机号信息进行绑定。");
        normalDialog.show();
    }*/

    /*class RegisterDialog extends Dialog {
        public RegisterDialog(Context context) {
            super(context);
            setCustomDialog();
        }

        private void setCustomDialog() {
            View mView = LayoutInflater.from(context).inflate(R.layout.dialog_normal, null);
            final EditText editText = (EditText) mView.findViewById(R.id.edt_info);
            TextView positiveButton = (TextView) mView.findViewById(R.id.txt_send);
            TextView negativeButton = (TextView) mView.findViewById(R.id.txt_cancel);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String subPhone = editText.getText().toString();
                    if(subPhone.length()==11){
                        AbRequestParams map = new AbRequestParams();
                        map.put("uid", TApplication.user.getUserId());
                        map.put("telPhone1",telPhone);
                        map.put("telPhone2",subPhone);
                        map.put("phoneModel",phoneType);
                        new AndHttpRequest(context) {
                            @Override
                            public void getResponse(String response) {
                                if(AndJsonUtils.getCode(response).equals("200")){
                                    AbToastUtil.showToast(context,"登记成功");
                                    dismiss();
                                }else {
                                    AbToastUtil.showToast(context,AndJsonUtils.getResult(response));
                                }
                            }
                        }.doPost(Const.URL_REGISTER_PHONE,map);
                    }else {
                        AbToastUtil.showToast(context,"请填写正确的手机号码");
                    }
                }
            });
            super.setContentView(mView);
        }
    }*/

}
