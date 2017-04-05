package com.hxsn.jwb.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.andbase.library.http.model.AbRequestParams;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.LogUtil;
import com.hxsn.jwb.R;
import com.hxsn.jwb.TApplication;
import com.hxsn.jwb.activity.ChickCurveActivity;
import com.hxsn.jwb.ui.MyGridView;
import com.hxsn.jwb.utils.Const;
import com.hxsn.jwb.utils.JsonUtils;
import com.hxsn.jwb.utils.Shared;
import com.hxsn.jwb.utils.Tools;


/**
 * 首页面
 * create an instance of this fragment.
 */
@SuppressLint("ValidFragment")
public class RealTimeFragment extends Fragment implements View.OnClickListener {
    private Context context;

    private static final String TAG ="Ssk1Fragment";
   // private RelativeLayout layout1, layout2, layout3, layout4, layout5;
   // private ImageView img1, img2, img3, img4, img5;
   // private TextView txt1, txt2, txt3, txt4, txt5;
    private TextView txtFirst, txtPre, txtMiddle, txtNext, txtEnd;
    private ImageView imgPoint;//报警预警的圆点
    private WebView webViewHome;
    private String urlWebView;//webView地址
    private MyGridView gridView;
    private ScrollView linearLayoutHome;


    private int currentPage = 0;
    private int totalPage = 1;
    private MyAdapter adapter;

    public RealTimeFragment() {
    }

    public RealTimeFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onStart() {
        LogUtil.i("RealtimeFragment", "onStart");
        super.onStart();
        obtainChickHomeList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtil.i("RealtimeFragment", "onCreate");
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.i("RealtimeFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_realtime, container, false);
        addView(view);

        addListener();

        addWebView();

        return view;
    }

    private void addWebView() {
        urlWebView = Const.URL_WEATHER_WEB+ Tools.getHomeId();
        webViewHome.loadUrl(urlWebView);
    }

    private void obtainChickHomeList() {
        final AbRequestParams map = new AbRequestParams();
        map.put("uid",TApplication.user.getUserId());
        map.put("pageStart",String.valueOf(currentPage));
        map.put("pageSize","6");
        //String url = "http://192.168.12.97:8080/jwb/getHomeList.json"+"?uid=1&pageStart="+currentPage+"&pageSize=6";
        String url = Const.URL_GET_HOME_LIST;//Const.URL_WARN_HOUSE_UNREAD + TApplication.user.getUserId()+"&dyid="+TApplication.defaultGreenHouse.getId();
        new AndHttpRequest(context) {
            @Override
            public void getResponse(String response) {
                TApplication.homeList = JsonUtils.getChickHomeList(response);
                if(TApplication.homeList.size()>0){
                    Shared.saveChickHome(TApplication.homeList.get(0));
                }
                LogUtil.i("ssk1Fragment","-------------homelist.size="+ TApplication.homeList.size());
                int size = JsonUtils.getTotalSize(response);
                totalPage = size/6;
                if(size%6!=0){
                    totalPage++;
                }

                if(currentPage>0){
                    txtPre.setTextColor(getResources().getColor(R.color.black_text_n));
                    txtFirst.setTextColor(getResources().getColor(R.color.black_text_n));
                    if(currentPage==totalPage-1 && totalPage>1){
                        txtNext.setTextColor(getResources().getColor(R.color.gray));
                        txtEnd.setTextColor(getResources().getColor(R.color.gray));
                    }else {
                        txtNext.setTextColor(getResources().getColor(R.color.black_text_n));
                        txtEnd.setTextColor(getResources().getColor(R.color.black_text_n));
                    }
                }else {
                    if(totalPage>1){
                        txtNext.setTextColor(getResources().getColor(R.color.black_text_n));
                        txtEnd.setTextColor(getResources().getColor(R.color.black_text_n));
                    }else {
                        txtNext.setTextColor(getResources().getColor(R.color.gray));
                        txtEnd.setTextColor(getResources().getColor(R.color.gray));
                    }

                    txtPre.setTextColor(getResources().getColor(R.color.gray));
                    txtFirst.setTextColor(getResources().getColor(R.color.gray));
                }

                addAdapter();
            }
        }.doPost(url,map);
    }

    public void addAdapter(){
        adapter = new MyAdapter();
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String homeId = TApplication.homeList.get(position).getId();
                Intent intent = new Intent(context, ChickCurveActivity.class);
                intent.putExtra("homeId",homeId);
                startActivity(intent);
            }
        });
    }

    private void addListener() {
        txtFirst.setOnClickListener(this);
        txtPre.setOnClickListener(this);
        txtMiddle.setOnClickListener(this);
        txtNext.setOnClickListener(this);
        txtEnd.setOnClickListener(this);
    }

    private void addView(View view) {

        webViewHome = (WebView)view.findViewById(R.id.webView_home);
        linearLayoutHome = (ScrollView)view.findViewById(R.id.line_home);
        imgPoint = (ImageView)view.findViewById(R.id.img_red_point);
        gridView = (MyGridView)view.findViewById(R.id.grid_view);

        txtFirst = (TextView) view.findViewById(R.id.txt_first);
        txtPre = (TextView) view.findViewById(R.id.txt_pre);
        txtMiddle = (TextView) view.findViewById(R.id.txt_middle);
        txtNext = (TextView) view.findViewById(R.id.txt_next);
        txtEnd = (TextView) view.findViewById(R.id.txt_end);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {

            case R.id.txt_first:
                LogUtil.i("Ssk1Fragment","点击首页");
                if(currentPage != 0){
                    currentPage = 0;
                }
                break;
            case R.id.txt_pre:
                LogUtil.i("Ssk1Fragment","点击上一页");
                if(currentPage>0){
                    currentPage--;
                    obtainChickHomeList();
                }
                break;
            case R.id.txt_next:
                LogUtil.i("Ssk1Fragment","点击下一页");
                if(currentPage<totalPage-1){
                    currentPage++;
                    obtainChickHomeList();
                }

                break;
            case R.id.txt_middle:
                LogUtil.i("Ssk1Fragment","点击曲线对比");
                if(TApplication.homeList != null && TApplication.homeList.size()>0){
                    intent.setClass(context, ChickCurveActivity.class);
                    intent.putExtra("homeId","homeId");
                    startActivity(intent);
                }
                break;
            case R.id.txt_end:
                LogUtil.i("Ssk1Fragment","点击尾页");
                if(currentPage != totalPage-1){
                    currentPage = totalPage-1;
                    obtainChickHomeList();
                }
                break;
        }

    }


    class MyAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public MyAdapter() {
            super();
            inflater = LayoutInflater.from(getActivity());
        }

        @Override
        public int getCount() {
            return TApplication.homeList.size();
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
                convertView = inflater.inflate(R.layout.item_home, parent, false);
                viewHolder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
                viewHolder.txtDayAge = (TextView) convertView.findViewById(R.id.txt_day_age);
                viewHolder.txtTemperate = (TextView) convertView.findViewById(R.id.txt_temperate);
                viewHolder.imgRing = (ImageView)convertView.findViewById(R.id.img_ring);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.txtName.setText(TApplication.homeList.get(position).getName());
            viewHolder.txtDayAge.setText(TApplication.homeList.get(position).getDayAge());
            viewHolder.txtTemperate.setText(String.valueOf(TApplication.homeList.get(position).getTemperature())+"℃");
            if(TApplication.homeList.get(position).getIsWarning().equals("1")){
                viewHolder.imgRing.setVisibility(View.VISIBLE);
            }else {
                viewHolder.imgRing.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    //     ViewHolder 模式, 效率提高 50%
    static class ViewHolder {
        TextView txtName,txtDayAge,txtTemperate;
        ImageView imgRing;
    }
}
