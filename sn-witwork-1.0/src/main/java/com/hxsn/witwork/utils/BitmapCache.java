/**
 * Copyright 2015 
 *
 *All right reserved
 *
 *Created on 2015-7-8  ����3:29:28
 *
 * @author  ����
*/
package com.hxsn.witwork.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * Copyright 2015 
 *
 *All right reserved
 *
 *Created on 2015-7-8  ����3:29:28
 *
 * @author  ����
 */
public class BitmapCache implements ImageCache {
	  
    private LruCache<String, Bitmap> mCache;
  
    public BitmapCache() {  
        int maxSize = 10 * 1024 * 1024;  
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @Override  
            protected int sizeOf(String key, Bitmap bitmap) {  
                return bitmap.getRowBytes() * bitmap.getHeight();  
            }  
        };  
    }  
  
    @Override  
    public Bitmap getBitmap(String url) {  
        return mCache.get(url);  
    }  
  
    @Override  
    public void putBitmap(String url, Bitmap bitmap) {  
        mCache.put(url, bitmap);  
    }

  
}  
