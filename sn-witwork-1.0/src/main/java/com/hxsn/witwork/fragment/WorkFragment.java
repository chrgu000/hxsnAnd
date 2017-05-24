package com.hxsn.witwork.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.hxsn.witwork.R;
import com.hxsn.witwork.activity.WorkFarCheckInActivity;
import com.hxsn.witwork.activity.WorkRecordActivity;
import com.hxsn.witwork.activity.WorkSelectActivity;
import com.hxsn.witwork.utils.ImgBtnEffact;


public class WorkFragment extends BaseFrgament {

	Button workDJBtn, workCXBtn, workNCCZDJBtn, workYCCZDJBtn;//作业登记，作业查询,农场采摘登记，远程采摘登记
	Intent intent = null;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_work_fragment,container, false);
		inintView(view);
		initListener();
		return view;
	}

	private void inintView(View view) {

		workDJBtn = (Button) view.findViewById(R.id.workDJBtn);
		workCXBtn = (Button) view.findViewById(R.id.workCXBtn);
		workNCCZDJBtn = (Button) view.findViewById(R.id.workNCCZDJBtn);
		workYCCZDJBtn = (Button) view.findViewById(R.id.workYCCZDJBtn);
	}

	private void initListener() {

		workDJBtn.setOnTouchListener(ImgBtnEffact.btnTL);
		workCXBtn.setOnTouchListener(ImgBtnEffact.btnTL);
        workNCCZDJBtn.setOnTouchListener(ImgBtnEffact.btnTL);
        workYCCZDJBtn.setOnTouchListener(ImgBtnEffact.btnTL);
		
		workDJBtn.setOnClickListener(new NewListener());
		workCXBtn.setOnClickListener(new NewListener());
		workNCCZDJBtn.setOnClickListener(new NewListener());
		workYCCZDJBtn.setOnClickListener(new NewListener());
	}

	class NewListener implements OnClickListener {

		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.workDJBtn://作业登记
				intent = new Intent(getActivity(),WorkRecordActivity.class);
				WorkRecordActivity.isFarm=false;
				startActivity(intent);
				break;
			case R.id.workCXBtn:
				intent = new Intent(getActivity(),WorkSelectActivity.class);
				startActivity(intent);
				break;
			case R.id.workNCCZDJBtn:
				intent = new Intent(getActivity(),WorkRecordActivity.class);
				WorkRecordActivity.isFarm=true;
				startActivity(intent);
				break;
			case R.id.workYCCZDJBtn:
				intent = new Intent(getActivity(),WorkFarCheckInActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}

		}

	}

}