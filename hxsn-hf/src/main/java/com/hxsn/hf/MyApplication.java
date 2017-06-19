package com.hxsn.hf;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.hxsn.hf.utils.Shared;

import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application{
	public static MyApplication app;
	private static Map<String, Object> CacheData;
	public static boolean isShowLog = true;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		app = this;
		CacheData = new HashMap<String, Object>();
		Shared.init(this);
		getVersion();
	}
	
	private void getVersion() {
		// TODO Auto-generated method stub
		PackageManager manager = getPackageManager();
		String version = null;
		try {
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (version != null) {
			Shared.setVersion(version);
		}
	}
	
	public Map<String, Object> getCacheData() {
		return CacheData;
	}
	
	public void AddCacheData(String key,Object value){
		CacheData.put(key, value);
	}
	
	public void DeleteCacheData(String key){
		if(CacheData.containsKey(key))
		  CacheData.remove(key);
	}
	
	public void ClearCacheData(String key){
		CacheData.clear();
	}
	
	public Object GetCacheData(String key){
		if(CacheData.containsKey(key)){    
			return CacheData.get(key);
		}
		return null;
	}
}
