package com.hxsn.ssklf.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.andbase.ssk.utils.AndHttpRequest;
import com.hxsn.ssklf.R;
import com.hxsn.ssklf.TApplication;
import com.hxsn.ssklf.adapter.SiteListAdapter;
import com.hxsn.ssklf.base.BaseTitle;
import com.hxsn.ssklf.model.SiteInfo;
import com.hxsn.ssklf.utils.Const;
import com.hxsn.ssklf.utils.JsonUtil;
import com.hxsn.ssklf.utils.Shared;

import java.util.ArrayList;
import java.util.List;

public class SelectSiteActivity extends Activity implements View.OnClickListener{

    //private TextView txtSelect1,txtSelect2,txtSelect3,txtSelect4,txtSelect5,txtSelect6,txtSelect7,txtSelect8,txtSelect9,txtSelect10;
    private List<SiteInfo> sites = new ArrayList<>();
    private ListView listView;
    private SiteListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_site);
        
        addData();
        
        addView();

        BaseTitle.getInstance(this).setTitle("选择气象哨");

        //获取气象哨列表
        obtainSiteList();
    }

    private void addData() {
        sites = Shared.getSiteList();
    }


    /**
     * 网络获取实时数据，并刷新
     */
    private void obtainSiteList() {
        String url = Const.URL_GET_SITES;
        new AndHttpRequest(this) {
            @Override
            public void getResponse(String response) {
                if(JsonUtil.getStatus(response)){
                    List<SiteInfo> tempSites = JsonUtil.getSiteInfoList(response);
                    Shared.setSiteList(tempSites);
                    //解决notifydatasetchanged不刷新问题
                    sites.clear();
                    sites.addAll(tempSites);
                    if(sites.size() > 0){
                       adapter.notifyDataSetChanged();
                    }
                }
            }
        }.doGet(url);
    }


    private void addView() {
        listView = (ListView)findViewById(R.id.list);
        adapter = new SiteListAdapter(SelectSiteActivity.this, sites);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TApplication.curSiteInfo = sites.get(position);
                Shared.setValue("siteName",sites.get(position).getName());
                Shared.setValue("siteUUid",sites.get(position).getUuid());
                Intent intent = new Intent();
                intent.setClass(SelectSiteActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override

    public void onClick(View v) {


    }

}
