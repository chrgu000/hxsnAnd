package com.hxsn.witwork.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.hxsn.witwork.R;
import com.hxsn.witwork.adapter.WorkCommAdapter;
import com.hxsn.witwork.beans.ChatListEntity;

import java.util.ArrayList;

public class ComFragment extends BaseFrgament {

	private EditText et_work;
	private ListView lv_work_com;
	private WorkCommAdapter adapter;
	// 全部列表信息
	private ArrayList<ChatListEntity> allList;
	// 查询结果列表
	private ArrayList<ChatListEntity> selectList;
	private String selStr;
	
	public static ComFragment comFragment = null;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_com_fragment, container, false);

		return view;
	}


}
