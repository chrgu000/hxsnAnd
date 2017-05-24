package com.hxsn.jwb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.andbase.library.http.model.AbRequestParams;
import com.andbase.ssk.utils.AndHttpRequest;
import com.hxsn.jwb.R;
import com.hxsn.jwb.TApplication;
import com.hxsn.jwb.base.BaseTitle;
import com.hxsn.jwb.model.ChickHome;
import com.hxsn.jwb.utils.Const;
import com.hxsn.jwb.utils.JsonUtils;
import com.hxsn.jwb.utils.Shared;

import java.util.List;

public class SelectHomeActivity extends Activity {

    private List<ChickHome> chickHomeList;
    private String intentStr;
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_home);

        BaseTitle.getInstance(this).setTitle("选择鸡舍");
        obtainChickHomeList();

        intentStr = getIntent().getStringExtra("intent");

    }


    private void obtainChickHomeList() {
        final AbRequestParams map = new AbRequestParams();
        map.put("uid",TApplication.user.getUserId());
        map.put("pageStart",String.valueOf(0));
        map.put("pageSize","500");

        //String url = "http://192.168.12.97:8080/jwb/getHomeList.json";
        String url = Const.URL_GET_HOME_LIST;
        new AndHttpRequest(this) {
            @Override
            public void getResponse(String response) {
                chickHomeList = JsonUtils.getChickHomeList(response);
                addAdapter();
            }
        }.doPost(url,map);
    }

    private void addAdapter() {
        ListView listView = (ListView) findViewById(R.id.list);
        MyAdapter adapter = new MyAdapter();

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TApplication.mode = 7;
                Shared.saveChickHome(chickHomeList.get(position));
                //Shared.setValue("homeId",chickHomeList.get(position).getId());
                Intent intent = new Intent(SelectHomeActivity.this,HomeActivity.class);
                if(TextUtils.isEmpty(intentStr)){
                    intent = new Intent(SelectHomeActivity.this,SystemSettingActivity.class);
                }
                
                startActivity(intent);
            }
        });
    }


    class MyAdapter extends BaseAdapter{

        private LayoutInflater inflater;

        public MyAdapter() {
            super();
            inflater = LayoutInflater.from(SelectHomeActivity.this);
        }

        @Override
        public int getCount() {
            return chickHomeList.size();
        }

        @Override
        public Object getItem(int position) {
            return chickHomeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_home_list, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
                viewHolder.txtDayAge = (TextView) convertView.findViewById(R.id.txt_day_age);
                viewHolder.txtTemperate = (TextView) convertView.findViewById(R.id.txt_temperate);
                viewHolder.imgRing = (ImageView)convertView.findViewById(R.id.img_ring);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.txtName.setText(chickHomeList.get(position).getName());
            viewHolder.txtDayAge.setText(chickHomeList.get(position).getDayAge());
            viewHolder.txtTemperate.setText(String.valueOf(chickHomeList.get(position).getTemperature())+"℃");
            if(chickHomeList.get(position).getIsWarning().equals("1")){
                viewHolder.imgRing.setVisibility(View.VISIBLE);
            }else {
                viewHolder.imgRing.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
    }

    //     ViewHolder 模式, 效率提高 50%
    static class ViewHolder {
        TextView txtName, txtDayAge, txtTemperate;
        ImageView imgRing;
    }

}
