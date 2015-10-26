package com.zzuli.whispers.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AboutUs extends Activity {
	private Button bupdate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.aboutus_activity);
	    initView();
	    initEvent();
	}
	private void initView() {
		bupdate=(Button) findViewById(R.id.btn_aboutus);
	}
	private void initEvent() {
		bupdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(AboutUs.this, "已是最新版本！",1).show();
			}
		});
	}
}
