package com.hxsn.iot.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.andbase.library.http.model.AbRequestParams;
import com.andbase.library.model.AbEntity;
import com.andbase.library.util.AbDialogUtil;
import com.andbase.library.util.AbToastUtil;
import com.andbase.library.view.wheel.AbEntityWheelAdapter;
import com.andbase.library.view.wheel.AbWheelView;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.AndShared;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.iot.R;
import com.hxsn.iot.TApplication;
import com.hxsn.iot.base.BaseTitle;
import com.hxsn.iot.uitls.Const;
import com.hxsn.iot.uitls.JsonUtils;
import com.hxsn.iot.uitls.UpdateUtil;

import java.util.List;

/**
 * 添加棚温宝设备
 */
public class AddZZDActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView txtSelectGarden,txtSelectPlc;
    private View vSelectHome = null;
    private View vSelectPlc = null;
    private LayoutInflater mInflater;
   // private List<String> greenhouseStrList,plcStrList;//大棚名称列表，plc类型名称列表
    private List<AbEntity> greenhouseList,plcList;//大棚列表，plc类型列表
   // private String plcId;//园区ID和plc类型ID
    private EditText edtGreenhouseName,edtDnsAddress;
    private Button btnAdd;
    private AbEntity selectedGarden,defaultGarden,selectedPlc,defaultPlc;//已选择的园区或默认显示的园区

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_zzd);

        BaseTitle.getInstance(this).setTitle("添加设备");

        addView();
        addListener();

        //获取大棚列表信息
        obtaingreenhouseList();
        //获取PLC类型列表
        obtainPlcList();
    }

    //获取大棚列表信息
    private void obtaingreenhouseList() {
        if(TApplication.user == null){
            return;
        }
        String url = Const.URL_GET_ALL_GARDEN + TApplication.user.getUserId();
        new AndHttpRequest(this) {
            @Override
            public void getResponse(String response) {
                greenhouseList = JsonUtils.getEntityList(response);
                LogUtil.i(AddZZDActivity.class,"greenHouseList.size="+greenhouseList.size());
            }
        }.doGet(url);
    }

    //获取PLC类型列表
    private void obtainPlcList() {
        String url = Const.URL_GET_PLC_LIST;
        new AndHttpRequest(this) {
            @Override
            public void getResponse(String response) {
                plcList = JsonUtils.getEntityList(response);
            }
        }.doGet(url);
    }

    private void addView() {
        mInflater = LayoutInflater.from(this);
        txtSelectGarden = (TextView)findViewById(R.id.txt_select_home);
        txtSelectPlc = (TextView)findViewById(R.id.txt_select_plc);
        edtDnsAddress = (EditText)findViewById(R.id.edt_dns_address);
        edtGreenhouseName = (EditText)findViewById(R.id.edt_greenhouse_name);
        btnAdd = (Button) findViewById(R.id.btn_add);

        defaultGarden = AndShared.getEntity("garden");
        if(TextUtils.isEmpty(defaultGarden.getName())){
            txtSelectGarden.setText(defaultGarden.getName());
        }

        defaultPlc = AndShared.getEntity("plc");

        if(TextUtils.isEmpty(defaultPlc.getName())){
            txtSelectPlc.setText(defaultPlc.getName());
        }
    }

    private void addListener() {
        txtSelectGarden.setOnClickListener(this);
        txtSelectPlc.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_select_home://选择园区

                initWheelData1();
                AbDialogUtil.showDialog(vSelectHome, Gravity.BOTTOM);
                break;
            case R.id.txt_select_plc://选择plc

                initWheelData2();
                AbDialogUtil.showDialog(vSelectPlc, Gravity.BOTTOM);
                break;
            case R.id.btn_add://添加
                final String name = edtGreenhouseName.getText().toString();
                String dns = edtDnsAddress.getText().toString();
                String gardenId = "";
                String plcId = "";

                if(selectedGarden == null){//未选择园区
                    if(TextUtils.isEmpty(defaultGarden.getId())){//没有默认园区
                        AbToastUtil.showToast(this,"请选择园区");
                        break;
                    }else {
                        gardenId = defaultGarden.getId();
                    }
                }else {
                    gardenId = selectedGarden.getId();
                    if(TextUtils.isEmpty(gardenId)){//没有默认园区
                        AbToastUtil.showToast(this,"请选择园区");
                        break;
                    }
                }

                if(selectedPlc == null){//未选择plc
                    if(TextUtils.isEmpty(defaultPlc.getId())){//没有默认plc
                        AbToastUtil.showToast(this,"请选择PLC类型");
                        break;
                    }else {
                        plcId = defaultPlc.getId();
                    }
                }else {
                    plcId = selectedPlc.getId();
                    if(TextUtils.isEmpty(plcId)){//没有默认plc
                        AbToastUtil.showToast(this,"请选择PLC类型");
                        break;
                    }
                }


                if(TextUtils.isEmpty(name)){
                    AbToastUtil.showToast(this,"请填写大棚名称");
                    break;
                }
                if(TextUtils.isEmpty(dns)){
                    AbToastUtil.showToast(this,"请填写网关地址");
                    break;
                }

                if(dns.length() != 18){
                    AbToastUtil.showToast(this,"请确认网关地址是18位");
                    break;
                }

                if(TextUtils.isEmpty(TApplication.user.getUserId())){
                    AbToastUtil.showToast(this,"请再次登录");
                    break;
                }

                String url = Const.URL_ADD_ZZD;
                AbRequestParams params = new AbRequestParams();

                params.put("uid", TApplication.user.getUserId());
                params.put("name", name);
                params.put("gateway", dns);
                params.put("yquuid", gardenId);
                params.put("plcType", plcId);

                //提交后台
                new AndHttpRequest(this) {
                    @Override
                    public void getResponse(String response) {
                        String code = AndJsonUtils.getCode(response);
                        if(code.equals("200")){

                            if(selectedGarden != null && !selectedGarden.equals(defaultGarden)){//如果已手动选择了园区，且选择的不是默认园区，则把已选择的园区保存为默认园区
                                AndShared.saveEntity(selectedGarden,"garden");
                            }

                            if(selectedPlc != null && !selectedPlc.equals(defaultPlc)){
                                AndShared.saveEntity(selectedPlc,"plc");
                            }

                            UpdateUtil.show(AddZZDActivity.this, "添加设备成功");
                            onBackPressed();
                        }else {
                            UpdateUtil.show(AddZZDActivity.this, AndJsonUtils.getResult(response));
                        }
                    }
                }.doPost(url,params);

                break;
        }
    }

    //选择园区的轮子
    public void initWheelData1(){
        vSelectHome = mInflater.inflate(R.layout.choose_one, null);

        final AbWheelView mWheelView1 = (AbWheelView)vSelectHome.findViewById(R.id.zzd_wheelView);
        mWheelView1.setAdapter(new AbEntityWheelAdapter(greenhouseList));
        // 可循环滚动
        mWheelView1.setCyclic(false);
        // 添加文字
     //   mWheelView1.setLabel(getResources().getString(com.andbase.R.string.data1_unit));
        // 初始化时显示的数据
        mWheelView1.setCurrentItem(2);//设置默认的那个item
        mWheelView1.setValueTextSize(35);
        mWheelView1.setLabelTextSize(35);
        mWheelView1.setLabelTextColor(0x80000000);
       // mWheelView1.setCenterSelectDrawable(this.getResources().getDrawable(com.andbase.R.drawable.wheel_select));

        Button okBtn = (Button)vSelectHome.findViewById(R.id.okBtn);
        Button cancelBtn = (Button)vSelectHome.findViewById(R.id.cancelBtn);
        okBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(AddZZDActivity.this);
                int index = mWheelView1.getCurrentItem();
                String val = mWheelView1.getAdapter().getItem(index);
                txtSelectGarden.setText(val);
                String gardenId = greenhouseList.get(index).getId();
                selectedGarden = new AbEntity(gardenId,val);
            }

        });

        cancelBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(AddZZDActivity.this);
            }

        });
    }

    //选择PLC类型的轮子
    public void initWheelData2(){
        vSelectPlc = mInflater.inflate(R.layout.choose_one, null);
        final AbWheelView mWheelView1 = (AbWheelView)vSelectPlc.findViewById(R.id.zzd_wheelView);
        mWheelView1.setAdapter(new AbEntityWheelAdapter(plcList));
        // 可循环滚动
        mWheelView1.setCyclic(false);
        // 添加文字
        //   mWheelView1.setLabel(getResources().getString(com.andbase.R.string.data1_unit));
        // 初始化时显示的数据
        mWheelView1.setCurrentItem(2);//设置默认的那个item
        mWheelView1.setValueTextSize(35);
        mWheelView1.setLabelTextSize(35);
        mWheelView1.setLabelTextColor(0x80000000);
        // mWheelView1.setCenterSelectDrawable(this.getResources().getDrawable(com.andbase.R.drawable.wheel_select));

        Button okBtn = (Button)vSelectPlc.findViewById(R.id.okBtn);
        Button cancelBtn = (Button)vSelectPlc.findViewById(R.id.cancelBtn);
        okBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(AddZZDActivity.this);
                int index = mWheelView1.getCurrentItem();
                String val = mWheelView1.getAdapter().getItem(index);
                txtSelectPlc.setText(val);

                String plcId = plcList.get(index).getId();
                selectedPlc = new AbEntity(plcId,val);
            }

        });

        //取消
        cancelBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(AddZZDActivity.this);
            }

        });
    }
}
