package com.hxsn.jwb.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.andbase.library.asynctask.AbTask;
import com.andbase.library.asynctask.AbTaskItem;
import com.andbase.library.asynctask.AbTaskListListener;
import com.andbase.library.http.model.AbRequestParams;
import com.andbase.library.util.AbToastUtil;
import com.andbase.library.view.refresh.AbPullToRefreshView;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.jwb.R;
import com.hxsn.jwb.TApplication;
import com.hxsn.jwb.model.ChickHome;
import com.hxsn.jwb.model.Warning;
import com.hxsn.jwb.utils.Const;
import com.hxsn.jwb.utils.JsonUtils;
import com.hxsn.jwb.utils.MyHttpRequest;
import com.hxsn.jwb.utils.Shared;
import com.hxsn.jwb.utils.Tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */

@SuppressLint("ValidFragment")
public class WarningFragment extends Fragment  implements
        AbPullToRefreshView.OnHeaderRefreshListener, AbPullToRefreshView.OnFooterLoadListener {

    private Context context;
    private AbPullToRefreshView mAbPullToRefreshView;
    private ListView listView;
    private List<Warning> warningAllList;
    private List<Warning> warningTempList;
    private boolean isAllWarning= false;
    private int currentPage = 0;
    private int pageSize = 15;
    private int totalSize;
    private MyAdapter adapter;
    private TextView txtTitle;
    private View parentView;

    public WarningFragment() {
    }

    public WarningFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warning, container, false);
        parentView = view;
        warningAllList = new ArrayList<>();
        addView(view);

        if(adapter == null){
            adapter = new MyAdapter(context);
            listView.setAdapter(adapter);
        }

        obtainWarnings(currentPage,pageSize);

        return view;
    }


    @Override
    public void onStop() {
        LogUtil.i("WarningFragment","----------------onStop------------------");
        adapter = null;
        super.onStop();
    }

    private void addView(View view) {
        listView = (ListView)view.findViewById(R.id.list);
        txtTitle = (TextView)view.findViewById(R.id.txt_title);
        // 获取ListView对象
        mAbPullToRefreshView = (AbPullToRefreshView) view.findViewById(R.id.mPullRefreshView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new WarningDialog(context,position,R.style.noTitleDialog).show();
            }
        });

        view.findViewById(R.id.btn_view_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAllWarning = true;
                obtainWarnings(currentPage,pageSize);
            }
        });

        // 设置监听器
        mAbPullToRefreshView.setOnHeaderRefreshListener(this);
        mAbPullToRefreshView.setOnFooterLoadListener(this);
    }

    private void obtainWarnings(final int pageStart, int pageSize) {
        // homeId = getActivity().getIntent().getStringExtra("selectHomeId");
       // String url = "http://192.168.12.97:8080/jwb/getWarningList.json";
        String url = Const.URL_GET_WARNING_LIST;
        AbRequestParams map = new AbRequestParams();
        map.put("uid", TApplication.user.getUserId());
        ChickHome chickHome = Shared.getChickHome();
        if(!isAllWarning && chickHome !=null){
            String homeId = chickHome.getId();
            txtTitle.setText(chickHome.getName()+"报警预警信息");
            map.put("homeId",homeId);
        }else {
            txtTitle.setText("所有报警预警信息");
        }
        map.put("pageStart",String.valueOf(pageStart));
        map.put("pageSize",String.valueOf(pageSize));

        new MyHttpRequest(context) {
            @Override
            public void getResponse(String response) {
                totalSize = JsonUtils.getTotalSize(response);
                warningTempList = JsonUtils.getWarningList(response);
                if(pageStart == 0){
                    refreshTask();
                }else {
                    loadMoreTask();
                }
            }
        }.doPost(parentView,url,map);

    }

    @Override
    public void onFooterLoad(AbPullToRefreshView view) {
        LogUtil.i("WarningFragment","----------------onFooterLoad------------------");
        obtainWarnings(++currentPage,pageSize);
    }

    @Override
    public void onHeaderRefresh(AbPullToRefreshView view) {
        LogUtil.i("WarningFragment","------------------onHeaderRefresh----------------------");
        refreshTask();
    }

    public void refreshTask() {

        AbTask mAbTask = AbTask.newInstance();
        final AbTaskItem item = new AbTaskItem();
        item.setListener(new AbTaskListListener() {

            @Override
            public List<?> getList() {
                List<Warning> newList = new ArrayList<>();
                try {
                    Thread.sleep(1000);
                    newList.addAll(warningTempList);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return newList;
            }

            @Override
            public void update(List<?> paramList) {
                List<Warning> newList = (List<Warning>) paramList;
                warningAllList.clear();
                if (newList != null && newList.size() > 0) {
                    warningAllList.addAll(newList);
                    if(adapter == null){
                        adapter = new MyAdapter(context);
                        listView.setAdapter(adapter);
                    }else {
                        adapter.notifyDataSetChanged();
                    }

                    newList.clear();
                }
                mAbPullToRefreshView.onHeaderRefreshFinish();
            }
        });
        mAbTask.execute(item);
    }

    public void loadMoreTask() {
        AbTask mAbTask = AbTask.newInstance();
        final AbTaskItem item = new AbTaskItem();
        item.setListener(new AbTaskListListener() {

            @Override
            public List<Warning> getList() {
                List<Warning> newList = null;
                try {
                    Thread.sleep(1000);
                    newList = new ArrayList<>();
                    if(warningAllList.size()<totalSize){
                        newList.addAll(warningTempList);
                    }
                } catch (InterruptedException e) {
                    currentPage--;
                    newList.clear();
                    AbToastUtil.showToastInThread(context, e.getMessage());
                    e.printStackTrace();
                }

                return newList;
            }

            @Override
            public void update(List<?> paramList) {
                List<Warning> newList = (List<Warning>) paramList;
                if (newList != null && newList.size() > 0) {
                    warningAllList.addAll(newList);
                    if(adapter == null){
                        adapter = new MyAdapter(context);
                        listView.setAdapter(adapter);
                    }else {
                        adapter.notifyDataSetChanged();
                    }
                    newList.clear();
                }
                mAbPullToRefreshView.onFooterLoadFinish();
            }
        });
        mAbTask.execute(item);
    }

    class MyAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            super();
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return warningAllList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
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
                convertView = inflater.inflate(R.layout.item_warning, parent, false);
                viewHolder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
                viewHolder.txtInfo = (TextView)convertView.findViewById(R.id.txt_info);
                viewHolder.txtCreateTime = (TextView)convertView.findViewById(R.id.txt_date);
                viewHolder.imgPoint = (ImageView)convertView.findViewById(R.id.img_red_point);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            Warning warning = warningAllList.get(position);
            viewHolder.txtName.setText(warning.getName());
            if(warning.getIsReaded() == 0){
                viewHolder.imgPoint.setVisibility(View.VISIBLE);
            }else {
                viewHolder.imgPoint.setVisibility(View.GONE);
            }

            viewHolder.txtInfo.setText(warning.getInfo());
            long time = warning.getCreateTime();
            Date date = new Date();
            date.getDate();
            long curTime = new Date().getTime();
            long subTime = curTime - time;
            if(subTime < 1000*60*60){
                viewHolder.txtCreateTime.setText(subTime/60000+"分钟前");
            }else if(date.getDate() ==new Date(time).getDate() ){
                viewHolder.txtCreateTime.setText(Tools.formatTime(time,"HH:mm"));
            }else {
                viewHolder.txtCreateTime.setText(Tools.formatTime(time,"MM-dd HH:mm"));
            }

            return convertView;
        }
    }

    static class ViewHolder {
        TextView txtName,txtInfo,txtCreateTime;
        ImageView imgPoint;
    }


    class WarningDialog extends Dialog {
        private int position;
        public WarningDialog(Context context,int position) {
            super(context);
            this.position = position;
            setCustomDialog();

        }

        public WarningDialog(Context context,int position,int style) {
            super(context, style);
            this.position = position;
            setCustomDialog();

        }

        private void setCustomDialog() {
            View mView = LayoutInflater.from(context).inflate(R.layout.dialog_warning, null);

            TextView txtName = (TextView) mView.findViewById(R.id.txt_name);
            TextView txtInfo = (TextView) mView.findViewById(R.id.txt_info);
            TextView txtTime = (TextView) mView.findViewById(R.id.txt_create_time);
            TextView txtDeviceName = (TextView) mView.findViewById(R.id.txt_device_name);

            Warning warning = warningAllList.get(position);
            txtName.setText(warning.getName());
            txtDeviceName.setText(warning.getDeviceName());
            txtInfo.setText(warning.getInfo());
            long time = warning.getCreateTime();
            txtTime.setText(Tools.formatTime(time,"yyyy-MM-dd HH:mm"));

            ImageView imgX = (ImageView) mView.findViewById(R.id.img_x);
            imgX.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });


            super.setContentView(mView);
        }
    }


}
