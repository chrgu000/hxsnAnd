package com.hxsn.witwork.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FirstAdapter extends PagerAdapter {
	  
    private ArrayList<View> list;

	public FirstAdapter(ArrayList<View> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(list ==null){
			return 0;
		} else {
			return list.size();
		}
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}
	
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		View v =list.get(position);
		container.addView(v);
		return v;
	}
	
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		container.removeView(list.get(position));
	}
}
