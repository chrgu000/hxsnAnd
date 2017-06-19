package com.hxsn.hf.discovery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hxsn.hf.R;
import com.hxsn.hf.base.BaseFragment;

public class DiscoveryFragment extends BaseFragment {
	RelativeLayout discovery_Cookbook;
	RelativeLayout discovery_Order;
	RelativeLayout discovery_Sweep;
	RelativeLayout discovery_Swing;
	RelativeLayout discovery_Dynamic;
	RelativeLayout discovery_Inquire;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View v =LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_discovery, null);
		init(v);
		initOnClick();
		return v;
		
		
	}
	
	private void init(View v){
		discovery_Cookbook =(RelativeLayout) v.findViewById(R.id.discovery_Cookbook);
		discovery_Order=(RelativeLayout) v.findViewById(R.id.discovery_Order);
		discovery_Sweep=(RelativeLayout) v.findViewById(R.id.discovery_Sweep);
		discovery_Swing=(RelativeLayout) v.findViewById(R.id.discovery_Swing);
		discovery_Dynamic=(RelativeLayout) v.findViewById(R.id.discovery_Dynamic);
		discovery_Inquire=(RelativeLayout) v.findViewById(R.id.discovery_Inquire);
	}
	
	private void initOnClick(){
		

		
		 OnClickListener onClick=new OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent;
				switch (v.getId()) {
				case R.id.discovery_Cookbook :		
					 intent =new Intent(getActivity(),CookBook_listActivity.class);
					 startActivity(intent);
					break;
				case R.id.discovery_Order:
					 intent =new Intent(getActivity(),CookBookActivity.class);
					 startActivity(intent);
					break;
				case R.id.discovery_Sweep:
					intent=new Intent(getActivity(),SweepActivity.class);
					startActivity(intent);
					break;
				case R.id.discovery_Swing:
					intent=new Intent(getActivity(),SwingActivity.class);
					startActivity(intent);
					break;
				case R.id.discovery_Dynamic:
					intent=new Intent(getActivity(),Fram_Dynamic_listActivity.class);
					startActivity(intent);
					break;
				case R.id.discovery_Inquire:
					intent=new Intent(getActivity(),Fram_order_listActivity.class);
					startActivity(intent);
					break;
				default:
					break;
				}
				
			}
		};

		discovery_Cookbook.setOnClickListener(onClick);
		discovery_Order.setOnClickListener(onClick);
		discovery_Sweep.setOnClickListener(onClick);
		discovery_Swing.setOnClickListener(onClick);
		discovery_Dynamic.setOnClickListener(onClick);
		discovery_Inquire.setOnClickListener(onClick);
	}
	
	
}
