package com.hxsn.iot.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.andbase.library.util.AbDialogUtil;
import com.andbase.library.view.wheel.AbStringWheelAdapter;
import com.andbase.library.view.wheel.AbWheelView;
import com.hxsn.iot.R;
import com.hxsn.iot.base.BaseTitle;
import com.hxsn.iot.entity.DefaultEntity;

import java.util.List;


/**
 * 创建园区
 */
public class AddGardenActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnAdd;
    private TextView txtSelectProvince,txtSelectCity,txtSelectCounty;
    private EditText edtName,edtAddress,edtPhone,edtContact,edtInfo,edtArea;

    private View vSelectProvince = null;
    private View vSelectCity = null;
    private View vSelectCounty = null;
    private LayoutInflater mInflater;

    private List<String> provinces,cities,counties;
    private List<DefaultEntity> provinceList,cityList,countyList;
    private DefaultEntity province,city,county;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_garden);
        BaseTitle.getInstance(this).setTitle("创建园区");

        addView();
        addListener();

    }

    private void addListener() {
        btnAdd.setOnClickListener(this);
        txtSelectProvince.setOnClickListener(this);
        txtSelectCity.setOnClickListener(this);
        txtSelectCounty.setOnClickListener(this);
    }

    private void addView() {
        mInflater = LayoutInflater.from(this);
        btnAdd = (Button)findViewById(R.id.btn_add);
        txtSelectProvince = (TextView)findViewById(R.id.txt_select_province);
        txtSelectCity = (TextView)findViewById(R.id.txt_select_city);
        txtSelectCounty = (TextView)findViewById(R.id.txt_select_county);
        edtName = (EditText)findViewById(R.id.edt_name);
        edtAddress = (EditText)findViewById(R.id.edt_address);
        edtPhone = (EditText)findViewById(R.id.edt_phone);
        edtContact = (EditText)findViewById(R.id.edt_contact);
        edtInfo = (EditText)findViewById(R.id.edt_info);
        edtArea = (EditText)findViewById(R.id.edt_area);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_add:
                break;
            case R.id.txt_select_province:
                initWheelData1();
                AbDialogUtil.showDialog(vSelectProvince, Gravity.CENTER);
                break;
            case R.id.txt_select_city:
                AbDialogUtil.showDialog(vSelectCity, Gravity.CENTER);
                break;
            case R.id.txt_select_county:
                AbDialogUtil.showDialog(vSelectCounty, Gravity.CENTER);
                break;
        }
    }


    //选择园区的轮子
    public void initWheelData1(){
        vSelectProvince = mInflater.inflate(R.layout.choose_one, null);
        final AbWheelView mWheelView1 = (AbWheelView)vSelectProvince.findViewById(R.id.zzd_wheelView);
        mWheelView1.setAdapter(new AbStringWheelAdapter(provinces));
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

        Button okBtn = (Button)vSelectProvince.findViewById(R.id.okBtn);
        Button cancelBtn = (Button)vSelectProvince.findViewById(R.id.cancelBtn);
        okBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(AddGardenActivity.this);
                int index = mWheelView1.getCurrentItem();
                String val = mWheelView1.getAdapter().getItem(index);
                txtSelectProvince.setText(val);
                String gardenId = provinceList.get(index).getId();
                //selectedGarden = new Gardening(gardenId,val);
            }

        });

        cancelBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //AbDialogUtil.removeDialog(AddZZDActivity.this);
            }

        });
    }
}
