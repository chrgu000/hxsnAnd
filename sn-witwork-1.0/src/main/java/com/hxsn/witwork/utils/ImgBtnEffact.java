package com.hxsn.witwork.utils;

import android.graphics.ColorMatrixColorFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public final class ImgBtnEffact {
	
	  public final static float[] BT_SELECTED=new float[] {      
	      2, 0, 0, 0, 2,      
	      0, 2, 0, 0, 2,      
	      0, 0, 2, 0, 2,      
	      0, 0, 0, 1, 0 };     
	       
	 
	  public final static float[] BT_NOT_SELECTED=new float[] {      
	      1, 0, 0, 0, 0,      
	      0, 1, 0, 0, 0,      
	      0, 0, 1, 0, 0,      
	      0, 0, 0, 1, 0 };     
	  public final static OnTouchListener btnTL=new OnTouchListener() {     
  	  @SuppressWarnings("deprecation")
	@Override    
  	  public boolean onTouch(View v, MotionEvent event) {     
  	   if(event.getAction() == MotionEvent.ACTION_DOWN){     
  	    v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));     
  	    v.setBackgroundDrawable(v.getBackground());     
  	    }     
  	    else if(event.getAction() == MotionEvent.ACTION_UP){     
  	     v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));     
  	     v.setBackgroundDrawable(v.getBackground());     
  	    }     
  	   return false;     
  	  }     
  	 };     
}
