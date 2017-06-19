package com.hxsn.hf.me;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.hxsn.hf.R;
import com.hxsn.hf.base.BaseFragment;


public class MeFragment extends BaseFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View v =LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_me, null);
		init(v);
		initOnClick();
		return v;
		
		
	}
	
	private void init(View v){
		
	}
	
	private void initOnClick(){
		
		//-------------------����¼��߼�-----------------------------
		
		 OnClickListener onClick=new OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case 2  :
					
					break;

				default:
					break;
				}
			}
		};
		//----------------------�ؼ���Ӽ���---------------------
		
	}
	
	
}
