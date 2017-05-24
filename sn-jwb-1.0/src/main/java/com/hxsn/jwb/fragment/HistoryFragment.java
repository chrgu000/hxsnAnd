package com.hxsn.jwb.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.andbase.library.asynctask.AbTask;
import com.andbase.library.asynctask.AbTaskItem;
import com.andbase.library.asynctask.AbTaskListListener;
import com.andbase.library.http.model.AbRequestParams;
import com.andbase.library.util.AbToastUtil;
import com.andbase.library.view.refresh.AbPullToRefreshView;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.jwb.R;
import com.hxsn.jwb.TApplication;
import com.hxsn.jwb.model.ChickHome;
import com.hxsn.jwb.utils.Const;
import com.hxsn.jwb.utils.JsonUtils;
import com.hxsn.jwb.utils.Shared;
import com.hxsn.jwb.utils.Tools;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */

@SuppressLint("ValidFragment")
public class HistoryFragment extends Fragment  implements
        AbPullToRefreshView.OnHeaderRefreshListener, AbPullToRefreshView.OnFooterLoadListener {

    private Context context;
    private AbPullToRefreshView mAbPullToRefreshView;
    private ListView listView;
    private List<ChickHome> historyAllList;
    private List<ChickHome> historyList;

    private int currentPage = 0;
    private int pageSize = 15;
    private int totalSize;
    private MyAdapter adapter;

    public HistoryFragment() {
    }

    public HistoryFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        historyAllList = new ArrayList<>();
        addView(view);

        if(adapter == null){
            adapter = new MyAdapter(context);
            listView.setAdapter(adapter);
        }

        obtainHistories(currentPage,pageSize);

        return view;
    }

    private void addView(View view) {
        listView = (ListView)view.findViewById(R.id.list);
        // 获取ListView对象
        mAbPullToRefreshView = (AbPullToRefreshView) view.findViewById(R.id.mPullRefreshView);


        // 设置监听器
        mAbPullToRefreshView.setOnHeaderRefreshListener(this);
        mAbPullToRefreshView.setOnFooterLoadListener(this);
    }

    private void obtainHistories(final int pageStart, int pageSize) {
        //String url = "http://192.168.12.97:8080/jwb/getHomeHistoryList.json";
        String url = Const.URL_GET_HOME_HISTORY_LIST;
        AbRequestParams map = new AbRequestParams();
        map.put("uid", TApplication.user.getUserId());
        ChickHome chickHome = Shared.getChickHome();
        if(chickHome !=null){
            String homeId = chickHome.getId();
            map.put("homeid",homeId);
        }

        map.put("pageStart",String.valueOf(pageStart));
        map.put("pageSize",String.valueOf(pageSize));

        new AndHttpRequest(context){

            @Override
            public void getResponse(String response) {
                if(AndJsonUtils.isSuccess(response)){
                    totalSize = JsonUtils.getTotalSize(response);
                    historyList = JsonUtils.getHistoryList(response);
                    if(pageStart == 0){
                        refreshTask();
                    }else {
                        loadMoreTask();
                    }
                }
            }
        }.doPost(url,map);
    }

    @Override
    public void onFooterLoad(AbPullToRefreshView view) {
        LogUtil.i("WarningFragment","----------------onFooterLoad------------------");
        obtainHistories(++currentPage,pageSize);
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
                List<ChickHome> newList = new ArrayList<>();
                try {
                    Thread.sleep(1000);
                    newList.addAll(historyList);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return newList;
            }

            @Override
            public void update(List<?> paramList) {
                List<ChickHome> newList = (List<ChickHome>) paramList;
                historyAllList.clear();
                if (newList != null && newList.size() > 0) {
                    historyAllList.addAll(newList);
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
            public List<ChickHome> getList() {
                List<ChickHome> newList = null;
                try {
                    Thread.sleep(1000);
                    newList = new ArrayList<>();
                    if(historyAllList.size()<totalSize){
                        newList.addAll(historyList);
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
                List<ChickHome> newList = (List<ChickHome>) paramList;
                if (newList != null && newList.size() > 0) {
                    historyAllList.addAll(newList);
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
            return historyAllList.size();
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
                convertView = inflater.inflate(R.layout.item_history, parent, false);
                viewHolder.txtNumber = (TextView) convertView.findViewById(R.id.txt_number);
                viewHolder.txtTemperature = (TextView)convertView.findViewById(R.id.txt_temperature);
                viewHolder.txtCreateTime = (TextView)convertView.findViewById(R.id.txt_date);

                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            ChickHome history = historyAllList.get(position);


            long time = history.getCreateTime();
            viewHolder.txtCreateTime.setText(Tools.formatTime(time,"yy-MM-dd HH:mm"));
            viewHolder.txtNumber.setText(String.valueOf(position+1));
            viewHolder.txtTemperature.setText(String.valueOf(history.getTemperature()));
            return convertView;
        }
    }

    @Override
    public void onStop() {
        LogUtil.i("WarningFragment","----------------onStop------------------");
        adapter = null;
        super.onStop();
    }

    static class ViewHolder {
        TextView txtNumber,txtTemperature,txtCreateTime;
    }


}
