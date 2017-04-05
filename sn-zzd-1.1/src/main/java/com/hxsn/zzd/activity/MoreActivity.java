package com.hxsn.zzd.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.andbase.ssk.utils.AndHttpRequest;
import com.hxsn.zzd.R;
import com.hxsn.zzd.TApplication;
import com.hxsn.zzd.base.BaseTitle;
import com.hxsn.zzd.model.Gardening;
import com.hxsn.zzd.model.GreenHouse;
import com.hxsn.zzd.utils.Const;
import com.hxsn.zzd.utils.JsonUtils;
import com.hxsn.zzd.utils.Shared;

import java.util.List;

/**
 * 棚室选择
 */
public class MoreActivity extends Activity {

    private List<Gardening> departList;
    private ExpandableListView listView;
    private DepartListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        BaseTitle.getInstance(this).setTitle("棚室选择");
        listView = (ExpandableListView)findViewById(R.id.expanded_list);

        addData();

        addAdapter();

        //获取棚室列表
        obtainHouseList();
    }

    private void obtainHouseList() {
        new AndHttpRequest(this) {
            @Override
            public void getResponse(String response) {
                List<Gardening> tempDepartList = JsonUtils.getGardeningList(response);
                if(tempDepartList.size()>0){
                    departList.clear();
                    departList.addAll(tempDepartList);
                    TApplication.defaultGreenHouse = departList.get(0).getGreenHouseList().get(0);
                    Shared.saveGreenHouse(TApplication.defaultGreenHouse);
                    Shared.saveGardingList(departList);
                    adapter.notifyDataSetChanged();
                }

            }
        }.doPost(Const.URL_GET_HOUSE_LIST + TApplication.user.getUserId());
    }

    private void addAdapter() {
        adapter = new DepartListAdapter(MoreActivity.this);

        listView.setAdapter(adapter);

        //设置Group的第1条默认展开
        listView.expandGroup(0);
    }

    class DepartListAdapter extends BaseExpandableListAdapter {

        private LayoutInflater inflater;

        public DepartListAdapter(Context context){
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getGroupCount() {
            return departList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if(departList.size() == 0){
                return 0;
            }
            return departList.get(groupPosition).getGreenHouseList().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            if(departList.size() == 0){
                return null;
            }
            return departList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if(departList.size() == 0){
                return null;
            }
            return departList.get(groupPosition).getGreenHouseList().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            Gardening depart = (Gardening) this.getGroup(groupPosition);
            DepartHolder departHolder;
            if(convertView == null){
                departHolder = new DepartHolder();
                convertView = inflater.inflate(R.layout.item_depart, null);
                departHolder.txtName = (TextView) convertView.findViewById(R.id.txt_depart_name);
                convertView.setTag(departHolder);
            }else{
                departHolder =  (DepartHolder)convertView.getTag();
            }

            departHolder.txtName.setText(depart.getName());
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final GreenHouse house = departList.get(groupPosition).getGreenHouseList().get(childPosition);

            HouseHolder houseViewHolder;
            if(convertView == null){
                houseViewHolder = new HouseHolder();
                convertView = inflater.inflate(R.layout.item_greenhouse, null);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TApplication.defaultGreenHouse = house;
                        Shared.saveGreenHouse(house);
                        Intent intent = new Intent();
                        intent.setClass(MoreActivity.this,HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                houseViewHolder.txtName = (TextView) convertView.findViewById(R.id.txt_greenhouse_name);

                convertView.setTag(houseViewHolder);
            }else{
                houseViewHolder = (HouseHolder) convertView.getTag();
            }

            houseViewHolder.txtName.setText(house.getName());
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        class DepartHolder{
            TextView txtName;
        }

        class HouseHolder{
            TextView txtName;
        }
    }

    private void addData(){
      departList = Shared.getGardingList();
    }
}
