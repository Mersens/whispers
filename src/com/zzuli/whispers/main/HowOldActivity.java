package com.zzuli.whispers.main;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facepp.error.FaceppParseException;


import com.zzuli.whispers.utils.BitmapUtils;
import com.zzuli.whispers.utils.Config;
import com.zzuli.whispers.utils.FaceDetection;
import com.zzuli.whispers.utils.NetworkAvailable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HowOldActivity extends Activity implements OnClickListener {
	private ImageView img;
	private ImageView select,check;
	private RelativeLayout layout_choose;
	private RelativeLayout layout_photo;
	private PopupWindow avatorPop;
	public String filePath = "";
	private RelativeLayout layout_all;
	private Bitmap bitmap;
	private TextView tv;
	private TextView no_picture;
	private  ProgressDialog progress;
	private boolean isOpenNetWork;
	private String localCameraPath = "";// 拍照后得到的图片地址
	private static final int MSG_SUCCESS=0X111;
	private static final int MSG_ERROR=0X112;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_howold);
		//判断是否有网络连接
		isOpenNetWork=NetworkAvailable.isNetworkAvailable(this);
		if(isOpenNetWork){
			initView();
		    initEvent();
		}else{
			showToast(this, "当前无网络连接，请检查网络设置！");
		}
	}

	/**
	 * Handler
	 */
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				JSONObject jsonObject=(JSONObject) msg.obj;
				resolutionJsonObject(jsonObject);
				img.setImageBitmap(bitmap);
				progress.dismiss();
				break;

			case MSG_ERROR:
				showToast(HowOldActivity.this, msg.obj.toString());
				progress.dismiss();
				break;
			}
			
			super.handleMessage(msg);
		}

	};
	
	/**
	 * @author Mersens
	 * 解析服务器返回的json数据，封装到Bitmap中
	 * @param jsonObject
	 */
	private void resolutionJsonObject(JSONObject jsonObject) {
		 Bitmap cbitmap=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),bitmap.getConfig());
		 Canvas canvas=new Canvas(cbitmap);
		 canvas.drawBitmap(bitmap, 0, 0,null);
		 Paint paint=new Paint();
		 try {
			 JSONArray facearray=jsonObject.getJSONArray("face");
			 for(int i=0;i<facearray.length();i++){
				 JSONObject face=facearray.getJSONObject(i);
				 JSONObject posObj=face.getJSONObject("position");
				 float x=(float) posObj.getJSONObject("center").getDouble("x");
				 float y=(float) posObj.getJSONObject("center").getDouble("y");
				 float h=(float) posObj.getDouble("height");
				 float w=(float) posObj.getDouble("width");
				 x=x/100*cbitmap.getWidth();
				 y=y/100*cbitmap.getHeight();
				 h=h/100*cbitmap.getHeight();
				 w=w/100*cbitmap.getWidth();
				 paint.setColor(0xffffffff);
				 paint.setStrokeWidth(3);
				 canvas.drawLine(x-w/2, y-h/2, x-w/2, y+h/2, paint);
				 canvas.drawLine(x-w/2, y-h/2, x+w/2, y-h/2, paint);
				 canvas.drawLine(x+w/2, y-h/2, x+w/2, y+h/2, paint);
				 canvas.drawLine(x-w/2, y+h/2, x+w/2, y+h/2, paint);
				 int age=face.getJSONObject("attribute").getJSONObject("age").getInt("value");
				 String sex=face.getJSONObject("attribute").getJSONObject("gender").getString("value");
				 Bitmap ageBitmap=getAgeBitmap(age,"Male".equals(sex));
				 int ageWidth=ageBitmap.getWidth();
				 int ageHight=ageBitmap.getHeight();
				 if(bitmap.getWidth()<img.getWidth() && bitmap.getHeight()<img.getHeight()){
					 float ratio=Math.max(bitmap.getWidth()*1.0f/img.getWidth(), bitmap.getHeight()*1.0F/img.getHeight());
					 ageBitmap=Bitmap.createScaledBitmap(ageBitmap,(int)(ageWidth*ratio),(int)(ageHight*ratio),false);
				 }
				 canvas.drawBitmap(ageBitmap, x-ageBitmap.getWidth()/2, y-h/2-ageBitmap.getHeight(),null);
				 bitmap=cbitmap;
			 }
			
		} catch (JSONException e) {
			e.printStackTrace();
		}			
	}
	
	
	private Bitmap getAgeBitmap(int age,boolean isMale) {
		tv.setText(age+"");
		if(isMale){
			tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.male), null, null,null);
		}else{
			tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.female), null, null,null);

		}
		tv.setDrawingCacheEnabled(true);
		Bitmap bitmap=Bitmap.createBitmap(tv.getDrawingCache());
		tv.destroyDrawingCache();
		return bitmap;
	}
	/**
	 * 初始化事件
	 */
	private void initEvent() {
		select.setOnClickListener(this);
		check.setOnClickListener(this);	
	}

	/**
	 * 初始化方法
	 */
	private void initView() {
		layout_all =  (RelativeLayout) findViewById(R.id.layout_all);
		img=(ImageView) findViewById(R.id.imageView);
		select=(ImageView) findViewById(R.id.select);
		check=(ImageView) findViewById(R.id.check);
		no_picture=(TextView) findViewById(R.id.no_picture);
		no_picture.setVisibility(View.VISIBLE);
		check.setVisibility(View.GONE);
		tv=(TextView) findViewById(R.id.age_and_sex);
		progress = new ProgressDialog(HowOldActivity.this);
		progress.setMessage("正在检测...");
		progress.setCanceledOnTouchOutside(false);
	}

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select:
			showAvatarPop();
			break;
		case R.id.check:
			progress.show();
			//把处理结果通过Handler进行传递到主线程中
			FaceDetection.decete(bitmap, new FaceDetection.Callback() {
				
				@Override
				public void success(JSONObject result) {
					Message msg=Message.obtain();
					msg.what=MSG_SUCCESS;
					msg.obj=result;
					mHandler.sendMessage(msg);
				}
				@Override
				public void error(FaceppParseException exception) {
					Message msg=Message.obtain();
					msg.what=MSG_ERROR;
					msg.obj=exception.getErrorMessage();
					mHandler.sendMessage(msg);
				}
			});
			break;
		}
	}

	/**
	 * 通过判断请求码，imageView进行数据设置
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode) {
		case 2:
			Cursor cursor=getContentResolver().query(intent.getData(), null, null, null, null);
			cursor.moveToFirst();
			int dex=cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			bitmap=BitmapUtils.getBitMap(cursor.getString(dex));
			if(bitmap!=null){
				check.setVisibility(View.VISIBLE);
				no_picture.setVisibility(View.GONE);
			}
			cursor.close();
			img.setImageBitmap(bitmap);
			avatorPop.dismiss();
			break;
		case 1:
			Bitmap bm=BitmapUtils.getBitMap(localCameraPath);
			if(bm!=null){
				check.setVisibility(View.VISIBLE);
				no_picture.setVisibility(View.GONE);
			}
			img.setImageBitmap(bm);
			avatorPop.dismiss();
			break;
		}
	}
	
	/**
	 * 底部弹出view的设置
	 */
	private void showAvatarPop() {
		View view = LayoutInflater.from(HowOldActivity.this).inflate(R.layout.pop_showavator,null);
		layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
		layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
		layout_photo.setVisibility(View.GONE);
        //调用系统的相机拍摄照片
		layout_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				layout_choose.setBackgroundColor(getResources().getColor(
						R.color.base_color_text_white));
				layout_photo.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.pop_bg_press));

				Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File dir = new File(Config.PICTURE_PATH);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File file = new File(dir, String.valueOf(System.currentTimeMillis())
						+ ".jpg");
				localCameraPath = file.getPath();
				Uri imageUri = Uri.fromFile(file);
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(openCameraIntent,1);
						
			}
		});
		
        //从相册选择图片
		layout_choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				layout_photo.setBackgroundColor(getResources().getColor(
						R.color.base_color_text_white));
				layout_choose.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.pop_bg_press));
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent,2);						
			}
		});
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		avatorPop = new PopupWindow(view, metric.widthPixels, 600);
		avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		avatorPop.setTouchable(true);
		avatorPop.setFocusable(true);
		avatorPop.setOutsideTouchable(true);
		avatorPop.setBackgroundDrawable(new BitmapDrawable());
		// 动画效果 从底部弹起
		avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
		avatorPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
	}
	
	public void showToast(Context context,String msg){
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

}
