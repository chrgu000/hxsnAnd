package com.hxsn.zzd.fragment;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andbase.ssk.utils.AndHttpRequest;
import com.hxsn.zzd.R;
import com.hxsn.zzd.TApplication;
import com.hxsn.zzd.model.Gardening;
import com.hxsn.zzd.model.GreenHouse;
import com.hxsn.zzd.utils.Const;
import com.hxsn.zzd.utils.JsonUtils;
import com.hxsn.zzd.utils.Shared;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 棚室选择页面
 */
@SuppressLint("ValidFragment")
public class HouseSelectFragment extends Fragment{
    private Context context;

    private List<Gardening> gardeningList;
    private ExpandableListView listView;
    private gardeningListAdapter adapter;
    private int mode  = 1;

    public HouseSelectFragment(Context context) {
        this.context = context;
    }
    public HouseSelectFragment() {
    }

    public static HouseSelectFragment newInstance(Context context,int mode) {
        HouseSelectFragment fragment = new HouseSelectFragment(context);
        Bundle args = new Bundle();
        args.putInt("fragment_mode", mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_house_select, container, false);
        mode = getArguments().getInt("fragment_mode");


        listView = (ExpandableListView)view.findViewById(R.id.expanded_list);
        addData();
        addAdapter();

        //获取棚室列表
        obtainHouseList();
        return view;
    }

    private void obtainHouseList() {
        new AndHttpRequest(context) {
            @Override
            public void getResponse(String response) {
                List<Gardening> tempGardeningList = JsonUtils.getGardeningList(response);
                if(tempGardeningList.size()>0){
                    gardeningList.clear();
                    gardeningList.addAll(tempGardeningList);
                    if(TApplication.defaultGreenHouse == null || TextUtils.isEmpty(TApplication.defaultGreenHouse.getId())){
                        TApplication.defaultGreenHouse = gardeningList.get(0).getGreenHouseList().get(0);
                        Shared.saveGreenHouse(TApplication.defaultGreenHouse);
                    }
                    Shared.saveGardingList(gardeningList);
                    adapter.notifyDataSetChanged();
                }

            }
        }.doPost(Const.URL_GET_HOUSE_LIST + TApplication.user.getUserId());
    }

    private void addAdapter() {
        adapter = new gardeningListAdapter(context);

        listView.setAdapter(adapter);

        //设置Group的第1条默认展开
        listView.expandGroup(0);
        //选择棚室
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                final GreenHouse house = gardeningList.get(groupPosition).getGreenHouseList().get(childPosition);
                TApplication.defaultGreenHouse = house;
                Log.i("HouseSelectFragment", "--------选择棚室"+TApplication.defaultGreenHouse.getName());
                Shared.saveGreenHouse(house);
                TApplication.mode = mode;
                EventBus.getDefault().post("house_select_to_zzd");//通知zzdFragment切换相应的fragment页面
                return false;
            }
        });

    }

    /**
     * 棚室列表适配器
     */
    class gardeningListAdapter extends BaseExpandableListAdapter {

        private LayoutInflater inflater;

        public gardeningListAdapter(Context context){
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getGroupCount() {
            return gardeningList==null?0:gardeningList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {

            if(gardeningList==null){
                return 0;
            }

            if(gardeningList.size() == 0){
                return 0;
            }
            List greenHouseList = gardeningList.get(groupPosition).getGreenHouseList();
            if(greenHouseList == null){
                return 0;
            }
            return greenHouseList.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            if(gardeningList.size() == 0){
                return null;
            }
            return gardeningList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if(gardeningList.size() == 0){
                return null;
            }
            return gardeningList.get(groupPosition).getGreenHouseList().get(childPosition);
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
            final DepartHolder departHolder;
            if(convertView == null){
                departHolder = new DepartHolder();
                convertView = inflater.inflate(R.layout.item_depart, null);
                departHolder.txtName = (TextView) convertView.findViewById(R.id.txt_depart_name);
                departHolder.imgAddMinus = (ImageView)convertView.findViewById(R.id.img_add_minus);
                departHolder.lineSelect = (LinearLayout)convertView.findViewById(R.id.line_select);
                convertView.setTag(departHolder);
            }else{
                departHolder =  (DepartHolder)convertView.getTag();
            }

            departHolder.txtName.setText(depart.getName());

            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final GreenHouse house = gardeningList.get(groupPosition).getGreenHouseList().get(childPosition);

            HouseHolder houseViewHolder;
            if(convertView == null){
                houseViewHolder = new HouseHolder();
                convertView = inflater.inflate(R.layout.item_greenhouse, null);
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
            LinearLayout lineSelect;//园区Item项
            TextView txtName;
            ImageView imgAddMinus;//左边的+号或-号图标
        }

        class HouseHolder{
            TextView txtName;
        }
    }

    private void addData(){
        gardeningList = Shared.getGardingList();
        if(gardeningList == null){
            gardeningList = new ArrayList();
        }
    }

}
