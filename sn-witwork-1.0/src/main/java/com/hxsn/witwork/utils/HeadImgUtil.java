package com.hxsn.witwork.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.hxsn.witwork.R;
import com.hxsn.witwork.TApplication;
import com.hxsn.witwork.ui.Shared;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@SuppressLint("HandlerLeak")
public class HeadImgUtil {
	
	private static RequestQueue mQueue = null;
	private ImageView headImg = null;
	private String url = null;
	private String fileName = null;
	private String filePath = null;
	private String userID = null;
	
	public HeadImgUtil(String userID, String url, ImageView headImg, Context context) {
		// TODO Auto-generated constructor stub
		if (mQueue == null) {
			mQueue = Volley.newRequestQueue(context);
		}
		
		this.headImg = headImg;
		this.userID = userID;
		fileName = userID + ".jpeg";
		this.url = TApplication.URL_STRING + url;
		filePath = SDCardUtil.getInnerSDCardPath() + "/Intelliwork/";
		
		File fileDir = new File(filePath);
		if (!fileDir.exists()) {
			fileDir.mkdir();
		}
		
		filePath += "head/";
		fileDir = null;
		fileDir = new File(filePath);
		if (!fileDir.exists()) {
			fileDir.mkdir();
		}
		
		if (isHave(filePath + fileName)) {
			if (userID.equals(Shared.getUserID()) || Shared.getUserHeadUrl(userID).equals(this.url)) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Bitmap bitmap = getDiskBitmap(filePath + fileName);

						Message message = new Message();
						message.obj = bitmap;
						handler.sendMessage(message);
					}
				}).start();
			} else {
				headDelete(userID);
				Shared.setUserHeadUrl(userID, "");
				downLoad();
			}
		} else {
			downLoad();
		}
	}
	
	public static void headDelete(String userID) {
		String imgPath = SDCardUtil.getInnerSDCardPath() + "/Intelliwork/head/" + userID + ".jpeg";
		
		File fileDir = new File(imgPath);
		if (fileDir.exists()) {
			fileDir.delete();
		}
	}
	
	private Bitmap getDiskBitmap(String pathString)  
	{  
	    Bitmap bitmap = null;  
	    try  
	    {  
	        File file = new File(pathString);  
	        if(file.exists())  
	        {  
	            bitmap = BitmapFactory.decodeFile(pathString);
	        }  
	    } catch (Exception e)  
	    {  
	        // TODO: handle exception
	    	e.printStackTrace();
	    }  
	      
	      
	    return bitmap;  
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			headImg.setImageBitmap((Bitmap)msg.obj);
		};
	};
	
	private boolean isHave(String headFile) {
		// TODO Auto-generated method stub
		File file = new File(headFile);
		return file.exists();
	}

	@SuppressWarnings("deprecation")
	private void downLoad() {
		ImageRequest imageRequest = new ImageRequest(
				url , new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap response) {
						response = BitmapUtil.centerSquareScaleBitmap(response, 100);
						save(response, filePath, fileName);
						Shared.setUserHeadUrl(userID, url);
						headImg.setImageBitmap(response);
						mQueue.cancelAll(this);
					}
				}, 0, 0, Config.RGB_565, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						error.printStackTrace();
						mQueue.cancelAll(this);
						if (Shared.getSex().equals("男")) {
							headImg.setImageResource(R.drawable.personhead);
						} else if (Shared.getSex().equals("女")) {
							headImg.setImageResource(R.drawable.nv);
						} else {
							headImg.setImageResource(R.drawable.personhead);
						}
					}
				});
		mQueue.add(imageRequest);
	}

	public static String getImgParentPath() {
		String imgParentPath = SDCardUtil.getInnerSDCardPath() + "/Intelliwork/head/";
		return imgParentPath;
	}
	public static String getImgParentPath(Context context){
		String imgParentPath = context.getFilesDir().getAbsolutePath() + "/Intelliwork/head/";

		return imgParentPath;
	}
	
	public static boolean save(Bitmap bp, String imgParentPath, String imgName) {
		boolean isSucceed = false;
		OutputStream os = null;
		try {
			// 文件输出流到文件name中
			os = new FileOutputStream(new File(imgParentPath, imgName));
			bp.compress(CompressFormat.JPEG, 100, os);
			os.flush();
			os.close();
			isSucceed = true;
		} catch (Exception e) {
			e.printStackTrace();
			isSucceed = false;
		}
		return isSucceed;
	}
}
