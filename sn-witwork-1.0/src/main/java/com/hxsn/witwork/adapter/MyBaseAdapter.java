package com.hxsn.witwork.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hxsn.witwork.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public abstract class MyBaseAdapter<T> extends BaseAdapter {
	protected Context context;
	protected LayoutInflater inflater;
	protected ArrayList<T> myList=new ArrayList<T>();
	protected ImageLoader imageLoader;
	protected DisplayImageOptions options;
	public MyBaseAdapter(Context context){
		this.context=context;
		this.inflater=LayoutInflater.from(context);
		 imageLoader = ImageLoader.getInstance();
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
			options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.contactor)			// 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.contactor)	// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.contactor)		// 设置图片加载或解码过程中发生错误显示的图片	
				.cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
//				.cacheOnDisc(true)							// 设置下载的图片是否缓存在SD卡中
				.cacheInMemory(true)
				.cacheOnDisk(true)
//				.displayer(new RoundedBitmapDisplayer(20))	// 设置成圆角图片
				.build();									// 创建配置过得DisplayImageOption对象
	}
	
	public ArrayList<T>  getAdapterData(){
		return myList;
	}
	
	//��װ����ݵķ���--1��  ����     
	public void appendData(T t,boolean isClearOld){
		if(t==null)
			return;
		if(isClearOld)
			myList.clear();
		myList.add(t);
	}
	public void appendData(ArrayList<T> data,boolean isClearOld){
		if(data==null)
			return;
		if(isClearOld)
			myList.clear();
		myList.addAll(data);
	}
	//��װ����ݵķ���--1��  ����    �ڶ������
	public void appendDataTop(T t,boolean isClearOld){
		if(t==null)
			return;
		if(isClearOld)
			myList.clear();
		myList.add(0, t);
	}
	public void appendDataTop(ArrayList<T> data,boolean isClearOld){
		if(data==null)
			return;
		if(isClearOld)
			myList.clear();
		myList.addAll(0, data);
	}
	
	public void update(){
		this.notifyDataSetChanged();
	}
	
	public void clear(){
		myList.clear();
	}
	

	public int getCount() {
		if(myList==null)
			return 0;
		return myList.size();
	}

	public T getItem(int position) {
		if(myList==null)
			return null;
		if(position>myList.size()-1)
			return null;
		return myList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return getMyView(position, convertView, parent);
	}
	public abstract View getMyView(int position, View convertView, ViewGroup parent);
	
}
