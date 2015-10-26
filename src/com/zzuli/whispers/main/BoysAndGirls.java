package com.zzuli.whispers.main;

import com.zzuli.whispers.bean.User;
import com.zzuli.whispers.game.game256MainActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class BoysAndGirls extends Activity {

	private String item;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		user=(User) getIntent().getSerializableExtra("user");
		final String[] items = new String[] { "Top", "Botoom", "Right", "Left" };
		final Builder builder = new AlertDialog.Builder(BoysAndGirls.this);
		builder.setTitle("传说男左女右的来源是：\nBoys are left because girls always ?");
		builder.setSingleChoiceItems(items, 0, new OnClickListener() {

			@Override
			public void onClick(DialogInterface diaog, int which) {
				// TODO Auto-generated method stub
				item = items[which];
			}
		});
		builder.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				if (item.equals("Right")) {
					Intent intent=new Intent(BoysAndGirls.this,SetMyInfoActivity.class);
					intent.putExtra("user", user);
					intent.putExtra("from", "other");
					intent.putExtra("stranger", "stranger");
					intent.putExtra("username", user.getUsername());
					startActivity(intent);
					finish();
				} else {
					Toast.makeText(BoysAndGirls.this, "您选的好像不是很对呢~",
							Toast.LENGTH_LONG).show();
					builder.create().show();
				}
			}
		});
		builder.create().show();
	}

}
