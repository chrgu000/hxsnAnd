package com.hxsn.witwork.fragment;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.andbase.library.http.model.AbRequestParams;
import com.andbase.ssk.utils.AndHttpRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hxsn.witwork.R;
import com.hxsn.witwork.activity.FarmintroActivity;
import com.hxsn.witwork.activity.LoginActivity;
import com.hxsn.witwork.ui.LoadingDialog;
import com.hxsn.witwork.ui.Shared;
import com.hxsn.witwork.utils.BitmapUtil;
import com.hxsn.witwork.utils.Const;
import com.hxsn.witwork.utils.HeadImgUtil;
import com.hxsn.witwork.utils.ImgBtnEffact;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MineFragment extends BaseFrgament {

	private ImageView pheadIV;
	private TextView pnameTV, psexTV, paddressTV,pfarmTV;
	TextView myswitch;
	private Button pexitBtn;
	private Dialog myNameDialog = null;
	Dialog myAddressDialog = null;
	private Bitmap bmp;
	String path;
	Bitmap bitmaps;
	public static String filePath;
	public final static int CONSULT_DOC_PICTURE = 1000;
	public final static int CONSULT_DOC_CAMERA = 1001;
	private int SELECT_PICTURE = 0;
	private int SELECT_CAMERA = 1;
	public static Uri outputFileUri;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_mine_fragment,
				container, false);
		inintView(view);
		getMyInfor();
		initListener();
		return view;
	}

	private void inintView(View view) {
		pheadIV = (ImageView) view. findViewById(R.id.pheadIV);
		pnameTV = (TextView) view. findViewById(R.id.pnameTV);
		psexTV = (TextView)  view.findViewById(R.id.psexTV);
		paddressTV = (TextView)  view.findViewById(R.id.paddressTV);
		myswitch= (TextView)  view.findViewById(R.id.myswitch);
		pfarmTV = (TextView)  view.findViewById(R.id.pfarmTV);
		pexitBtn = (Button)  view.findViewById(R.id.pexitBtn);
		
		if (Shared.getMessageAlert().equals("1"))
			myswitch.setText("打开");
		else
			myswitch.setText("关闭");

		myswitch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Shared.getMessageAlert().equals("1")) {
					myswitch.setText("关闭");
					Shared.setMessageAlert("0");
				} else {
					myswitch.setText("打开");
					Shared.setMessageAlert("1");
				}

			}
		});
	}
	
	
	private void getMyInfor() {
		// TODO Auto-generated method stub
		String uid = "";
		if (Shared.getUserID().length() > 0)
			uid = Shared.getUserID();
		LoadingDialog.showLoading(getActivity());
		try {
			StringRequest stringRequest = new StringRequest(Const.GETMYINFO + uid, new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {

					JSONObject j = null;
					try {
						try {
							j = new JSONObject(new String(response.getBytes(),
									"UTF-8"));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (j.getString("code").equals("200")) {

							JSONArray arrayObject = j.getJSONArray("result");
							for (int i = 0; i < arrayObject.length(); i++) {
								JSONObject newObject = (JSONObject) arrayObject
										.get(i);
								String sex = newObject.getString("sex");
								String address = newObject.getString("address");
								String nickname = newObject
										.getString("nickname");
								String photo = newObject.getString("photo");
								if (photo.length() > 0) {
									new HeadImgUtil(Shared.getUserID(), photo, pheadIV, getActivity());
								}
								else
									pheadIV.setImageResource(R.drawable.personhead);
								if (nickname.length() > 0)
									pnameTV.setText(nickname);
								else
									pnameTV.setText("暂无，请填写");
								if (sex.length() > 0)
									psexTV.setText(sex);
								else
									psexTV.setText("暂无，请填写");
								if (address.length() > 0)
									paddressTV.setText(address);
								else
									paddressTV.setText("暂无，请填写");
							}

						}
						LoadingDialog.dissmissLoading();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					Log.e("eeeeeeee", error.getMessage(), error);
				}
			});
			mQueue.add(stringRequest);
		} catch (Exception w) {

		}

	}
	private void initListener() {
		// TODO Auto-generated method stub
		// 点击更换头像
		pheadIV.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				CharSequence[] items = { "相册", "拍摄" };
				new Builder(getActivity())
						.setTitle("请选择")
						.setItems(items, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == SELECT_PICTURE) {
									Intent intent = new Intent(
											Intent.ACTION_PICK);
									intent.setDataAndType(
											MediaStore.Images.Media.INTERNAL_CONTENT_URI,
											"image/*");
									startActivityForResult(intent,
											CONSULT_DOC_PICTURE);
								} else if (which == SELECT_CAMERA) {
									File file = new File(Environment
											.getExternalStorageDirectory(),
											"/head.jpg");
									outputFileUri = Uri.fromFile(file);
									Intent intent = new Intent(
											MediaStore.ACTION_IMAGE_CAPTURE);
									intent.putExtra(MediaStore.EXTRA_OUTPUT,
											outputFileUri);
									startActivityForResult(intent,
											CONSULT_DOC_CAMERA);
								}
							}
						}).create().show();

			}
		});

		// 点击修改姓名
		pnameTV.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				myNameDialog = new Dialog(getActivity());
				myNameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				myNameDialog.show();
				myNameDialog.getWindow().setBackgroundDrawable(
						new ColorDrawable(0));
				myNameDialog.setCanceledOnTouchOutside(false);
				Window win = myNameDialog.getWindow();
				win.setContentView(R.layout.dialog_name);

				Button setting = (Button) win.findViewById(R.id.p_nmsetBtn);
				Button close = (Button) win.findViewById(R.id.pnm_cancleBtn);
				final EditText nameET = (EditText) win
						.findViewById(R.id.p_nameET);
				setting.setOnTouchListener(ImgBtnEffact.btnTL);
				close.setOnTouchListener(ImgBtnEffact.btnTL);

				setting.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (nameET.getText().toString().length() > 0) {
							pnameTV.setText(nameET.getText().toString());
							try {
								// updateName(new String(
								// nameET.getText().toString().getBytes(),
								// "UTF-8"));
								updateName(nameET.getText().toString());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						myNameDialog.dismiss();
					}

					private void updateName(String name) {
						// TODO Auto-generated method stub
						String uid = "";
						if (Shared.getUserID().length() > 0)
							uid = Shared.getUserID();
						try {
							name = URLEncoder.encode(name, "utf-8");
							StringRequest stringRequest = new StringRequest(
									Const.UPDATENAME + uid + "&nickname="
											+ name,
									new Response.Listener<String>() {
										@Override
										public void onResponse(String response) {

											JSONObject j;
											try {
												j = new JSONObject(response);
												if (j.getString("code").equals(
														"200")) {

													showToast("修改昵称成功");

												}
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
									}, new Response.ErrorListener() {
										@Override
										public void onErrorResponse(
												VolleyError error) {
											Log.e("eeeeeeee",
													error.getMessage(), error);
										}
									});
							mQueue.add(stringRequest);
						} catch (Exception w) {

						}

					}
				});

				close.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						myNameDialog.dismiss();
					}
				});

				myNameDialog.setOnDismissListener(new OnDismissListener() {
					public void onDismiss(DialogInterface dialog) {
						// TODO Auto-generated method stub
						myNameDialog = null;
					}
				});

			}
		});

		// 点击修改性别
		psexTV.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Builder builder = new Builder(
						getActivity());
				builder.setTitle("请选择您的性别");
				// 定义单选的选项
				final String[] items = new String[] { "男", "女" };
				// 设置单选选项
				// arg1：表示默认选中哪一项，-1表示没有默认选中项
				builder.setSingleChoiceItems(items, 0,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 执行选中某个选项后的业务逻辑
								// 点击某个选项后，触发onClick执行，要让对话框消失
								psexTV.setText(items[which]);
								updateSex(which + "");
								dialog.dismiss();
							}

							private void updateSex(String sex) {
								// TODO Auto-generated method stub
								String uid = "";
								if (Shared.getUserID().length() > 0)
									uid = Shared.getUserID();
								try {
									StringRequest stringRequest = new StringRequest(
											Const.UPDATESEX + uid + "&sex="
													+ sex,
											new Response.Listener<String>() {
												@Override
												public void onResponse(
														String response) {

													JSONObject j;
													try {
														j = new JSONObject(
																response);
														if (j.getString("code")
																.equals("200")) {

															showToast("修改性别成功");

														}

													} catch (JSONException e) {
														e.printStackTrace();
													}
												}
											}, new Response.ErrorListener() {
												@Override
												public void onErrorResponse(
														VolleyError error) {
													Log.e("eeeeeeee",
															error.getMessage(),
															error);
												}
											});
									mQueue.add(stringRequest);
								} catch (Exception w) {

								}
							}
						});

				// 直接调用builder的show方法同样可以显示对话框，其内部也是先创建对话框对象，然后调用对话框的show()
				builder.show();

			}
		});

		// 点击修改地址
		paddressTV.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				myAddressDialog = new Dialog(getActivity());
				myAddressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				myAddressDialog.show();
				myAddressDialog.getWindow().setBackgroundDrawable(
						new ColorDrawable(0));
				myAddressDialog.setCanceledOnTouchOutside(false);
				Window win = myAddressDialog.getWindow();
				win.setContentView(R.layout.dialog_address);

				Button setting = (Button) win.findViewById(R.id.p_adsetBtn);
				Button close = (Button) win.findViewById(R.id.pad_cancleBtn);
				final EditText addressET = (EditText) win
						.findViewById(R.id.p_addressET);
				setting.setOnTouchListener(ImgBtnEffact.btnTL);
				close.setOnTouchListener(ImgBtnEffact.btnTL);

				setting.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (addressET.getText().toString().length() > 0) {
							paddressTV.setText(addressET.getText().toString());
							updateAddress(addressET.getText().toString());
						}
						myAddressDialog.dismiss();
					}

					private void updateAddress(String address) {
						// TODO Auto-generated method stub
						String uid = "";
						if (Shared.getUserID().length() > 0)
							uid = Shared.getUserID();
						try {
							address = URLEncoder.encode(address, "utf-8");
							StringRequest stringRequest = new StringRequest(
									Const.UPDATEADDRESS + uid + "&address="
											+ address,
									new Response.Listener<String>() {
										@Override
										public void onResponse(String response) {

											JSONObject j;
											try {
												j = new JSONObject(response);
												if (j.getString("code").equals(
														"200")) {
													showToast("修改地址成功");
												}

											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
									}, new Response.ErrorListener() {
										@Override
										public void onErrorResponse(
												VolleyError error) {
											Log.e("eeeeeeee",
													error.getMessage(), error);
										}
									});
							mQueue.add(stringRequest);
						} catch (Exception w) {

						}
					}
				});

				close.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						myAddressDialog.dismiss();
					}
				});

				myAddressDialog.setOnDismissListener(new OnDismissListener() {

					public void onDismiss(DialogInterface dialog) {

						myNameDialog = null;
					}
				});

			}
		});
		
		//关于农庄
		pfarmTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent inte=new Intent(getActivity(),FarmintroActivity.class);
				getActivity().startActivity(inte);
			}
		});
		

		// 退出
		pexitBtn.setOnTouchListener(ImgBtnEffact.btnTL);
		pexitBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Shared.setAddress("");
				Shared.setNickName("");
				Shared.setPassword("");
				Shared.setSex("");
				Shared.setUserID("");
				Shared.setUserName("");
				Shared.setCode("");
				//MyApplication.app.servers.clear();
				Intent inte = new Intent(getActivity(),
						LoginActivity.class);
				startActivity(inte);
				getActivity().finish();
			}
		});
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CONSULT_DOC_PICTURE) {
			if (data == null) {
				return;
			}
			LoadingDialog.showLoading(getActivity(),"正在上传头像...");
			Uri uri = data.getData();
			String[] proj = { MediaStore.Images.Media.DATA };
			@SuppressWarnings("deprecation")
			Cursor cursor = getActivity().managedQuery(uri, proj, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			path = cursor.getString(column_index);

			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			newOpts.inJustDecodeBounds = true;
			bitmaps = BitmapFactory.decodeFile(path, newOpts);
			newOpts.inJustDecodeBounds = false;
			int w = newOpts.outWidth;
			int h = newOpts.outHeight;
			@SuppressWarnings("deprecation")
			float hh = getActivity().getWindowManager().getDefaultDisplay().getHeight();
			@SuppressWarnings("deprecation")
			float ww = getActivity().getWindowManager().getDefaultDisplay().getWidth();
			int be = 1;
			if (w > h && w > ww) {
				be = (int) (w / ww);
			} else if (w < h && h > hh) {
				be = (int) (h / hh);
			}
			if (be <= 0)
				be = 1;
			newOpts.inSampleSize = be;
			bitmaps = BitmapFactory.decodeFile(path, newOpts);
			if (be == 1)
				bitmaps = BitmapFactory.decodeFile(path);
			bitmaps = BitmapUtil.centerSquareScaleBitmap(bitmaps, 100);
			HeadImgUtil.headDelete(Shared.getUserID());
			HeadImgUtil.save(bitmaps, HeadImgUtil.getImgParentPath(), Shared.getUserID() + ".jpeg");
			uploadFile(HeadImgUtil.getImgParentPath() + Shared.getUserID() + ".jpeg");
			
		} else if (requestCode == CONSULT_DOC_CAMERA) {

			if (bmp != null)
				bmp.recycle();
			String path = Environment.getExternalStorageDirectory()
					+ "/head.jpg";
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			newOpts.inJustDecodeBounds = true;
			bmp = BitmapFactory.decodeFile(path, newOpts);
			newOpts.inJustDecodeBounds = false;
			int w = newOpts.outWidth;
			int h = newOpts.outHeight;
			// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
			@SuppressWarnings("deprecation")
			float hh = getActivity().getWindowManager().getDefaultDisplay().getHeight();// 这里设置高度
			@SuppressWarnings("deprecation")
			float ww = getActivity().getWindowManager().getDefaultDisplay().getWidth();// 这里设置宽度
			// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
			int be = 1;// be=1表示不缩放
			if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
				be = (int) (w / ww);
			} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
				be = (int) (h / hh);
			}
			if (be <= 0)
				be = 1;
			newOpts.inSampleSize = be;// 设置缩放比例
			// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
			bmp = BitmapFactory.decodeFile(path, newOpts);
			bmp = BitmapUtil.centerSquareScaleBitmap(bmp, 100);
			HeadImgUtil.headDelete(Shared.getUserID());
			HeadImgUtil.save(bmp, HeadImgUtil.getImgParentPath(), Shared.getUserID() + ".jpeg");
			uploadFile(HeadImgUtil.getImgParentPath() + Shared.getUserID() + ".jpeg");

		} else {
			// showToast("");
		}

	}
	
	
	public void saveBitmapInCache(String picName, Bitmap bp, boolean isUpLoad) {

		Map<String, SoftReference<Bitmap>> softReferences = new HashMap<String, SoftReference<Bitmap>>();
		softReferences.put(picName, new SoftReference<Bitmap>(bp));

		File fileDir = getActivity().getCacheDir();
		if (!fileDir.exists()) {
			fileDir.mkdir();
		}
		OutputStream os = null;
		try {
			
			os = new FileOutputStream(new File(fileDir, picName));
			BitmapUtil.compressImage(bp).compress(CompressFormat.JPEG, 100, os);
//			bp.compress(CompressFormat.JPEG, 100, os);
			os.flush();
			os.close();
			filePath =getActivity(). getCacheDir() + "/head.png";
			if (isUpLoad) {
				// uploadFile(filePath);
				pheadIV.setImageBitmap(null);
				bitmaps = BitmapFactory.decodeFile(filePath);
				pheadIV.setImageBitmap(bitmaps);
				uploadFile(filePath);
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	private void uploadFile(final String uri) {

		AbRequestParams params = new AbRequestParams();
		try {
//			BitmapUtil.compressImage(BitmapFactory.decodeFile(filePath));
			params.put("app", "3");
			params.put("uid", Shared.getUserID());
			params.put("filedata", new File(uri));
			Log.i("wh", "3---------------------------------------"+uri);
			new AndHttpRequest(getContext()) {
				@Override
				public void getResponse(String response) {
					JSONObject js;
					try {
						js = new JSONObject(response);
						if (js.getInt("code") == 200) {
							new HeadImgUtil(Shared.getUserID(), null, pheadIV, getActivity());
						} else {
							showToast("上传失败");
						}
						LoadingDialog.dissmissLoading();
					} catch (JSONException e) {
						e.printStackTrace();
						LoadingDialog.dissmissLoading();
					}
				}
			}.doPost(Const.UPDATEHEAD,params);

		} catch (Exception e) {
			e.printStackTrace();
			LoadingDialog.dissmissLoading();
		}
	}

}
