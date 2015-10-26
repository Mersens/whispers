package com.zzuli.whispers.main;

import com.zzuli.whispers.bean.User;
import com.zzuli.whispers.game.game256MainActivity;

import B.t;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


public class GameMainActivity extends Activity
{
	private User user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		user=(User) getIntent().getSerializableExtra("user");
		int num=getIntent().getIntExtra("num",0);
		switch (num) {
		case 0:
			intentAction(user,this,TestActivity.class);
			break;
        case 1:
        	intentAction(user,this,game256MainActivity.class);
			break;
        case 2:
        	intentAction(user,this, BoysAndGirls.class);
	   break;
		default:
			break;
		}
		
	}
	  
	public <T> void intentAction(User user,Context contex,Class<T> cls){
		Intent intent = new Intent(contex, cls);
		intent.putExtra("user", user);
		startActivityForResult(intent, 1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == 2) {
			showToast(this,"欢迎回来~");
			
		}
		if (requestCode == 1 && resultCode == 3) {
			showToast(this,"nice boy,欢迎回来~");
		}
		if (requestCode == 1 && resultCode == 4) {
			showToast(this," boy,欢迎回来~");
		}
	}

	public void showToast(Context context,String msg ){
		Toast.makeText(context, "nice boy,欢迎回来~", Toast.LENGTH_LONG).show();
		finish();
	}
}
