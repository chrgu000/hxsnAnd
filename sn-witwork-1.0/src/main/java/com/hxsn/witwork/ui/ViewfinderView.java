/*
 * Copyright (C) 2016 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hxsn.witwork.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.andbase.library.app.camera.CameraManager;
import com.andbase.ssk.utils.LogUtil;
import com.google.zxing.ResultPoint;
import com.hxsn.witwork.R;
import com.hxsn.witwork.utils.Const;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 */
@SuppressLint("ClickableViewAccessibility")
public final class ViewfinderView extends View implements OnTouchListener {
	private static final String TAG = "log";
	/**
	 * 刷新界面的时间
	 */
	private static final long ANIMATION_DELAY = 10L;
	private static final int OPAQUE = 0xFF;

	/**
	 * 四个绿色边角对应的长度
	 */
	private int ScreenRate;

	/**
	 * 四个绿色边角对应的宽度
	 */
	private static final int CORNER_WIDTH = 5;
	/**
	 * 扫描框中的中间线的宽度
	 */
	private static final int MIDDLE_LINE_WIDTH = 6;

	/**
	 * 扫描框中的中间线的与扫描框左右的间隙
	 */
	private static final int MIDDLE_LINE_PADDING = 2;

	/**
	 * 中间那条线每次刷新移动的距离
	 */
	private static final int SPEEN_DISTANCE = 5;

	/**
	 * 手机的屏幕密度
	 */
	private static float density;
	/**
	 * 字体大小
	 */
	private static final int TEXT_SIZE = 14;
	/**
	 * 字体距离扫描框下面的距离
	 */
	private static final int TEXT_PADDING_TOP = 30;

	/**
	 * 画笔对象的引用
	 */
	private Paint paint;

	/**
	 * 中间滑动线的最顶端位置
	 */
	private int slideTop;

	/**
	 * 中间滑动线的最底端位置
	 */
	private int slideBottom;

	/**
	 * 将扫描的二维码拍下来，这里没有这个功能，暂时不考虑
	 */
	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;

	private final int resultPointColor;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;

	boolean isFirst;

	private int num = 0;
//	private int firstNum = 0;
	private ArrayList<String> name = null;
	private Context con;

	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		con = context;
		name = new ArrayList<String>();
		density = context.getResources().getDisplayMetrics().density;
		// 将像素转换成dp
		ScreenRate = (int) (20 * density);

		paint = new Paint();
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);

		resultPointColor = resources.getColor(R.color.possible_result_points);
		possibleResultPoints = new HashSet<ResultPoint>(5);
	}

	@Override
	public void onDraw(Canvas canvas) {
		// 中间的扫描框，你要修改扫描框的大小，去CameraManager里面修改
		if(CameraManager.get() == null){
			return;
		}
		Rect frame = CameraManager.get().getFramingRect();
		if (frame == null) {
			return;
		}

		// 初始化中间线滑动的最上边和最下边
		if (!isFirst) {
			isFirst = true;
			slideTop = frame.top;
			slideBottom = frame.bottom;
		}

		// 获取屏幕的宽和高
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		paint.setColor(resultBitmap != null ? resultColor : maskColor);

		// 画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
		// 扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
		canvas.drawRect(0, 0, width, frame.top + 15, paint);
		canvas.drawRect(0, frame.top + 15, frame.left + 15, frame.bottom - 14,
				paint);
		canvas.drawRect(frame.right - 14, frame.top + 15, width,
				frame.bottom - 14, paint);
		canvas.drawRect(0, frame.bottom - 14, width, height, paint);

		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
		} else {

			// 画扫描框边上的角，总共8个部分
			paint.setColor(Color.rgb(139, 193, 17));
			canvas.drawRect(frame.left, frame.top, frame.left + ScreenRate,
					frame.top + CORNER_WIDTH, paint);
			canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH,
					frame.top + ScreenRate, paint);
			canvas.drawRect(frame.right - ScreenRate, frame.top, frame.right,
					frame.top + CORNER_WIDTH, paint);
			canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right,
					frame.top + ScreenRate, paint);
			canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left
					+ ScreenRate, frame.bottom, paint);
			canvas.drawRect(frame.left, frame.bottom - ScreenRate, frame.left
					+ CORNER_WIDTH, frame.bottom, paint);
			canvas.drawRect(frame.right - ScreenRate, frame.bottom
					- CORNER_WIDTH, frame.right, frame.bottom, paint);
			canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom
					- ScreenRate, frame.right, frame.bottom, paint);

			// 绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
			slideTop += SPEEN_DISTANCE;
			if (slideTop >= frame.bottom) {
				slideTop = frame.top;
			}
			// canvas.drawRect(frame.left + MIDDLE_LINE_PADDING, slideTop -
			// MIDDLE_LINE_WIDTH/2, frame.right - MIDDLE_LINE_PADDING,slideTop +
			// MIDDLE_LINE_WIDTH/2, paint);
			Rect lineRect = new Rect();

			lineRect.left = frame.left;
			lineRect.right = frame.right;
			lineRect.top = slideTop;
			lineRect.bottom = slideTop + 18;
			canvas.drawBitmap(((BitmapDrawable) (getResources()
					.getDrawable(R.drawable.fgx))).getBitmap(), null, lineRect,
					paint);

			// 画扫描框下面的字
			paint.setColor(Color.WHITE);
			paint.setTextSize(TEXT_SIZE * density);
			// paint.setAlpha(0x40);
			// paint.setTypeface(Typeface.create("System", Typeface.BOLD));

			canvas.drawText(
					"将二维码/条码放入框内，即可自动扫描",
					frame.left,
					(float) (frame.bottom + (float) TEXT_PADDING_TOP * density),
					paint);
			canvas.drawText(
					"已扫描" + num + "个对象",
					frame.left,
					(float) (frame.bottom + (float) TEXT_PADDING_TOP * density + 50),
					paint);

			int max = 7;
			if (name.size() < max) {
				max = name.size();
				for (int i = 0; i < max; i++) {
					canvas.drawText(name.get(i), frame.left,
							(float) (frame.bottom + (float) TEXT_PADDING_TOP
									* density + 100 + 50 * i), paint);
				}
			} else {
				for (int i = 6; i >= 0; i--) {
					canvas.drawText(name.get((name.size()-1) - (6 - i)), frame.left,
							(float) (frame.bottom + (float) TEXT_PADDING_TOP
									* density + 100 + 50 * i), paint);
				}
			}
			
			paint.setStyle(Style.STROKE);
			canvas.drawRect(frame.left + 15, frame.top + 15, frame.right - 15,
					frame.bottom - 15, paint);
			setViewY(frame.bottom);
			paint.setStyle(Style.FILL);

			Collection<ResultPoint> currentPossible = possibleResultPoints;
			Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			if (currentPossible.isEmpty()) {
				lastPossibleResultPoints = null;
			} else {
				possibleResultPoints = new HashSet<ResultPoint>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(OPAQUE);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentPossible) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 6.0f, paint);
				}
			}
			if (currentLast != null) {
				paint.setAlpha(OPAQUE / 2);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentLast) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 3.0f, paint);
				}
			}

			// 只刷新扫描框的内容，其他地方不刷新
			postInvalidate();
			// postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
			// frame.right, frame.bottom);

		}
	}

	public int getViewY() {
		return viewY + 40;
	}

	private int viewY;

	public void setViewY(int height) {
		viewY = height;

	}

	public void drawViewfinder() {
		resultBitmap = null;
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}

	public void setName(String name) {
		getName(name);
	}

	public int getNameSize() {
		return this.name.size();
	}

//	public void setFirstNum(int firstNum) {
//		this.firstNum = firstNum;
//	}

	private void getName(final String nameCode) {
		AsyncHttpClient httpClient = new AsyncHttpClient();
		RequestParams param = new RequestParams();
		try {
			param.put("app", 3);
			param.put("uid", Shared.getUserID());
			param.put("code", nameCode);
			Log.i("param", param.toString());

			// LogUtil.showLog("WorkDJActivity", GetURL.SUBMITWORK);
			// LogUtil.showLog("WorkDJActivity", param.toString());
			httpClient.post(Const.DK_NAME, param,
					new AsyncHttpResponseHandler() {

						public void onStart() {
						}

						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							arg3.printStackTrace();
						}

						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							String s;
							try {
								s = new String(arg2, "UTF-8");
								LogUtil.i("WorkDJActivity", s);
								JSONObject js;
								js = new JSONObject(s);
								String code = js.getString("code");
								if (code.equals("200")) {
									JSONObject object = new JSONObject(js
											.getString("result"));
									String result = object.getString(nameCode);

									for (int i = 0; i < ViewfinderView.this.name
											.size(); i++) {
										if (ViewfinderView.this.name.get(i)
												.equals(result)) {
											return;
										}
									}
									ViewfinderView.this.name.add(result);
									num++;
								} else if (code.equals("101")) {
									Toast.makeText(con, "二维码信息无效",
											Toast.LENGTH_SHORT).show();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
//		float DownY = -1;
//		switch (event.getAction()) {
//		   case MotionEvent.ACTION_DOWN:
//		    DownY = event.getY();//float DownY
//		    break;
//		   case MotionEvent.ACTION_MOVE:
//		    float moveY = event.getY() - DownY;//y轴距离
//		    moveY = Math.abs(moveY);
//		    int num = (int) (moveY/50);
//		    LogUtil.showLog(TAG, num + "");
//		    if (!(num > getNameSize() - 5)) {
//		    	setFirstNum(num);
//		    }
//		    break;
//		   case MotionEvent.ACTION_UP:
//			   DownY = -1;
//		    break;
//		   }
		return false;
	}
}
