package com.hxsn.witwork.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class BitmapUtil {

    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength)
    {
     if(null == bitmap || edgeLength <= 0)
     {
      return  null;
     }
     
     Bitmap result = bitmap;
     int widthOrg = bitmap.getWidth();
     int heightOrg = bitmap.getHeight();
     
     if(widthOrg > edgeLength && heightOrg > edgeLength)
     {
   
      int longerEdge = (int)(edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
      int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
      int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
      Bitmap scaledBitmap;
      
            try{
             scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            } 
            catch(Exception e){
             return null;
            }
            
      
         int xTopLeft = (scaledWidth - edgeLength) / 2;
         int yTopLeft = (scaledHeight - edgeLength) / 2;
         
         try{
          result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
          scaledBitmap.recycle();
         }
         catch(Exception e){
          return null;
         }         
     }
          
     return result;
    }
    
    public static Bitmap centerBitmap(Bitmap bitmap)
    {
    	int edgeLength = 0;
     if(null == bitmap)
     {
      return  null;
     }
     
     Bitmap result = bitmap;
     int widthOrg = bitmap.getWidth();
     int heightOrg = bitmap.getHeight();
     
     if (widthOrg > heightOrg) {
    	 edgeLength = heightOrg - 1;
     } else if (widthOrg < heightOrg) {
    	 edgeLength = widthOrg - 1;
     } else {
    	 return result;
     }
     
     if(widthOrg > edgeLength && heightOrg > edgeLength)
     {
        
         int xTopLeft = (widthOrg - edgeLength) / 2;
         int yTopLeft = (heightOrg - edgeLength) / 2;
         
         try{
          result = Bitmap.createBitmap(bitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
          bitmap.recycle();
         }
         catch(Exception e){
          return null;
         }         
     }
          
     return result;
    }
    /**
     *  压缩图片大小到100K
     * @param image
     * @return
     */
    public static  Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while ( baos.toByteArray().length / 1024>100) {	//循环判断如果压缩后图片是否大于100kb,大于继续压缩		
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;//每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;
	}
}
