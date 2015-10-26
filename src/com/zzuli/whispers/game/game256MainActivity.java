package com.zzuli.whispers.game;


import com.zzuli.whispers.bean.User;
import com.zzuli.whispers.game.GameView256.GameView256Listener;
import com.zzuli.whispers.main.R;
import com.zzuli.whispers.main.SetMyInfoActivity;
import com.zzuli.whispers.main.StrangerMessage;
import com.zzuli.whispers.main.TestActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
public class game256MainActivity extends Activity {
	
	
	private User user;
	 
	
	private int score = 0;
	private TextView tvScore;
	private LinearLayout root = null;
	private Button btnNewGame;
	private GameView256 gameView;
	private AnimLayer animLayer = null;

	private static game256MainActivity mainActivity = null;
	public static final String SP_KEY_BEST_SCORE = "bestScore";

	public game256MainActivity() {
		mainActivity = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game256_activity_main);
		root = (LinearLayout) findViewById(R.id.container);
		root.setBackgroundColor(0xfffaf8ef);
		user=(User) getIntent().getSerializableExtra("user");
		tvScore = (TextView) findViewById(R.id.tvScore);

		gameView = (GameView256) findViewById(R.id.gameView);

		btnNewGame = (Button) findViewById(R.id.btnNewGame);
		btnNewGame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				gameView.startGame();
			}
		});

		animLayer = (AnimLayer) findViewById(R.id.animLayer);
		gameView.setOnGameView256Listener(new GameView256Listener() {
			
			@Override
			public void success() {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(game256MainActivity.this)
				.setTitle("温馨提示：").setMessage("解密完成！")
				.setPositiveButton("去添加新朋友~", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which)

					{
						Message msg = new Message();
						msg.what = 0x100;
						handler.sendMessage(msg);
						Intent intent=new Intent(game256MainActivity.this,SetMyInfoActivity.class);
						intent.putExtra("user", user);
						intent.putExtra("from", "other");
						intent.putExtra("stranger", "stranger");
						intent.putExtra("username", user.getUsername());
						startActivity(intent);
					}
				}).show();
			}
		});	

	}


	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0x100) {
				Intent intent = new Intent(game256MainActivity.this, StrangerMessage.class);
				intent.putExtra("user", user);
				Log.i("TAG", "username="+user.getUsername());
				startActivity(intent);
				finish();
			}
		};
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void clearScore() {
		score = 0;
		showScore();
	}

	public void showScore() {
		tvScore.setText(score + "");
	}

	public void addScore(int s) {
		score = s;
		showScore();

	
	}




	public AnimLayer getAnimLayer() {
		return animLayer;
	}

	public static game256MainActivity getMainActivity() {
		return mainActivity;
	}

}
