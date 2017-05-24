package com.hxsn.witwork.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.hxsn.witwork.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;


public class BaseFrgament extends Fragment {
	
	protected RequestQueue mQueue;
	private Toast toast;
	protected DisplayImageOptions options;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mQueue = Volley.newRequestQueue(getActivity());
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.contactor)			// 设置图片下载期间显示的图片
		.showImageForEmptyUri(R.drawable.contactor)	// 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.contactor)		// 设置图片加载或解码过程中发生错误显示的图片	
		.cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
//		.cacheOnDisc(true)							// 设置下载的图片是否缓存在SD卡中
		.cacheInMemory(true)
		.cacheOnDisk(true)
//		.displayer(new RoundedBitmapDisplayer(20))	// 设置成圆角图片
		.build();									// 创建配置过得DisplayImageOption对象
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		mQueue.cancelAll(this);
		super.onStop();
	}
	
	public void showToast(int resId) {
		showToast(getString(resId));
	}

	public void showToast(String msg) {
		toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
		toast.show();
	}
}
