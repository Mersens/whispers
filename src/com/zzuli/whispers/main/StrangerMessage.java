package com.zzuli.whispers.main;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.PushListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzuli.whispers.bean.User;
import com.zzuli.whispers.utils.ImageLoadOptions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StrangerMessage extends Activity {

	private TextView name,distance,time;
	private ImageView img;
	private User user;
	private View view;
	private RelativeLayout relativeLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_near_people);
		user=(User) getIntent().getSerializableExtra("user");
		initView();
		initEvent();
	}

	private void initView() {
		relativeLayout=(RelativeLayout) findViewById(R.id.near_people);
		name=(TextView) findViewById(R.id.tv_name);
		distance=(TextView) findViewById(R.id.tv_distance);
		time=(TextView) findViewById(R.id.tv_logintime);
		img=(ImageView) findViewById(R.id.iv_avatar);

	}
	private void initEvent() {
		String avatar = user.getAvatar();
		ImageLoader.getInstance().displayImage(avatar, img,
				ImageLoadOptions.getOptions());
		name.setText(user.getNick());
		distance.setText(user.getSex()==true?"男":"女");
		time.setText("最近登录时间："+user.getUpdatedAt());
		//img.setImageResource(R.drawable.head);
		relativeLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
            BmobChatManager.getInstance(StrangerMessage.this).sendTagMessage(BmobConfig.TAG_ADD_CONTACT, user.getObjectId(),new PushListener() {
					
					@Override
					public void onSuccess() {
                     Toast.makeText(StrangerMessage.this, "发送好友请求成功！", Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void onFailure(int arg0, final String arg1) {
	                Toast.makeText(StrangerMessage.this, "发送好友请求失败！", Toast.LENGTH_SHORT).show();

					}
				});
				Intent intent=new Intent(StrangerMessage.this,ChatActivity.class);
				intent.putExtra("user",user);
				startActivity(intent);
				finish();
			}
		});
	}

}
