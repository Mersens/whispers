package com.zzuli.whispers.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import com.zzuli.whispers.bean.QCommunity;
import com.zzuli.whispers.bean.User;
import com.zzuli.whispers.main.HeaderLayout.onRightImageButtonClickListener;
import com.zzuli.whispers.utils.CacheUtils;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class EditActivity extends ActivityBase implements OnClickListener {

	private static final int REQUEST_CODE_ALBUM = 1;
	private EditText content;
	private ImageView btnaddimg;
	private String dateTime;
	private String targeturl = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		initView();
		initEvent();
	}

	private void initEvent() {
		btnaddimg.setOnClickListener(this);
		initTopBarForBoth("轻社区", R.drawable.base_action_bar_true_bg_selector,
				new onRightImageButtonClickListener() {
					@Override
					public void onClick() {
						String commitContent = content.getText().toString()
								.trim();
						if (commitContent == null) {
							Toast.makeText(EditActivity.this, "请输入内容！",
									Toast.LENGTH_LONG).show();
							return;
						}
						if (targeturl == null) {
							publishWithoutFigure(commitContent, null);
						} else {
							publish(commitContent);
						}

					}
				});
	}

	private void initView() {
		content=(EditText) findViewById(R.id.et_usertel);
		btnaddimg=(ImageView) findViewById(R.id.btnaddimg);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnaddimg:
			Date date1 = new Date(System.currentTimeMillis());
			dateTime = date1.getTime() + "";
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(intent, REQUEST_CODE_ALBUM);
			break;

		default:
			break;
		}

	}

	/*
	 * 发表带图片
	 */
	private void publish(final String commitContent) {
		QCommunity qcommunity = new QCommunity();
		final BmobFile figureFile =  new BmobFile(new File(targeturl));
		figureFile.upload(EditActivity.this, new UploadFileListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				publishWithoutFigure(commitContent, figureFile);

			}

			@Override
			public void onProgress(Integer arg0) {
				System.out.println("进度！===="+arg0);

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				System.out.println("失败！===="+arg1);
				// TODO Auto-generated method stub
			}
		});

	}

	private void publishWithoutFigure(final String commitContent,
			final BmobFile figureFile) {
		User user = BmobUser.getCurrentUser(EditActivity.this, User.class);

		final QCommunity qiangYu = new QCommunity();
		qiangYu.setAuthor(user);
		qiangYu.setContent(commitContent);
		if (figureFile != null) {
			qiangYu.setContentfigureurl(figureFile);
		}
		qiangYu.setLove(0);
		qiangYu.setHate(0);
		qiangYu.setShare(0);
		qiangYu.setComment(0);
		qiangYu.setPass(true);
		qiangYu.save(EditActivity.this, new SaveListener() {

			@Override
			public void onSuccess() {
				Toast.makeText(EditActivity.this, "发表成功！", Toast.LENGTH_LONG)
						.show();
				Intent intent = new Intent(EditActivity.this,
						QCommunityActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				Toast.makeText(EditActivity.this, "发表失败！----->" + arg1,
						Toast.LENGTH_LONG).show();

			}
		});
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_ALBUM:
				String fileName = null;
				if (data != null) {
					Uri originalUri = data.getData();
					ContentResolver cr = getContentResolver();
					Cursor cursor = cr.query(originalUri, null, null, null,
							null);
					if (cursor.moveToFirst()) {
						do {
							fileName = cursor.getString(cursor
									.getColumnIndex("_data"));
						} while (cursor.moveToNext());
					}
					Bitmap bitmap = compressImageFromFile(fileName);
					targeturl = saveToSdCard(bitmap);
					btnaddimg.setImageBitmap(bitmap);
				}
				break;

			default:
				break;
			}
		}
	}

	private Bitmap compressImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;//
		float ww = 480f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置采样率

		newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		// return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
		// 其实是无效的,大家尽管尝试
		return bitmap;
	}

	public String saveToSdCard(Bitmap bitmap) {
		String files = CacheUtils.getCacheDirectory(EditActivity.this, true,
				"pic") + dateTime + "_11.jpg";
		File file = new File(files);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file.getAbsolutePath();
	}

}
