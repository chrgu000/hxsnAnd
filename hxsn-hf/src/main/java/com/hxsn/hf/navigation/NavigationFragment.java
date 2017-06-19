package com.hxsn.hf.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.hxsn.hf.R;
import com.hxsn.hf.base.BaseFragment;

public class NavigationFragment extends BaseFragment {
	Button navigation_fram_btn;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View v =LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_navigation, null);
		init(v);
		initOnClick();
		return v;
		
		
	}
	
	private void init(View v){
		navigation_fram_btn=(Button) v.findViewById(R.id.navigation_fram_btn);
	}
	
	private void initOnClick(){
		
		//-------------------����¼��߼�-----------------------------
		
		 OnClickListener onClick=new OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent;
				switch (v.getId()) {
				case R.id.navigation_fram_btn:
					intent =new Intent(getActivity(),FramClassListActivity.class);
					startActivity(intent);
					break;

				default:
					break;
				}
			}
		};
		//----------------------�ؼ���Ӽ���---------------------
		navigation_fram_btn.setOnClickListener(onClick);
	}
	
	
}
