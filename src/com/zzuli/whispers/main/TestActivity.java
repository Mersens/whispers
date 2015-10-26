package com.zzuli.whispers.main;

import cn.bmob.v3.listener.InitListener;

import com.zzuli.whispers.bean.User;
import com.zzuli.whispers.main.GamePintuLayout.GamePintuListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;


public class TestActivity extends Activity {

	private GamePintuLayout mGamePintuLayout;
	private TextView mTime;
    private User user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
        user=(User) getIntent().getSerializableExtra("user");
		mTime = (TextView) findViewById(R.id.id_time);
		mGamePintuLayout = (GamePintuLayout) findViewById(R.id.id_gamepintu);
		mGamePintuLayout.setTimeEnabled(true);

		mGamePintuLayout.setOnGamePintuListener(new GamePintuListener() {
			@Override
			public void timechanged(int currentTime) {
				mTime.setText("" + currentTime);
			}

			@Override
			public void success() {
				new AlertDialog.Builder(TestActivity.this)
						.setTitle("温馨提示：").setMessage("解密完成！")
						.setPositiveButton("去添加新朋友~", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which)

							{
								Message msg = new Message();
								msg.what = 0x100;
								handler.sendMessage(msg);
								Intent intent=new Intent(TestActivity.this,SetMyInfoActivity.class);
								intent.putExtra("user", user);
								intent.putExtra("from", "other");
								intent.putExtra("stranger", "stranger");
								intent.putExtra("username", user.getUsername());
								startActivity(intent);
							}
						}).show();
			}

			@Override
			public void gameover() {
				new AlertDialog.Builder(TestActivity.this)
						.setTitle("温馨提示：").setMessage("再接再厉，相信你可以的~")
						.setPositiveButton("再玩一次", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mGamePintuLayout.restart();
							}
						}).setNegativeButton("离开", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}).show();
			}
		});
	}

	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 0x100) {
				Intent intent = new Intent(TestActivity.this,
						MainActivity.class);
				setResult(2, intent);
				finish();
			}
		}

	};

	@Override
	protected void onPause() {
		super.onPause();

		mGamePintuLayout.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGamePintuLayout.resume();
	}

}
