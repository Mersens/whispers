package com.zzuli.whispers.robot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zzuli.whispers.main.ActivityBase;
import com.zzuli.whispers.main.R;
import com.zzuli.whispers.robot.ChatMeassage.Type;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RobotActivity extends ActivityBase {

	private List<ChatMeassage> list;
	private ChatMessageAdapter adapter;
	private ListView robot_liListView;

	private EditText msgs;
	private Button send_btn;

	
	private ImageView robot_to_img;
	private TextView robot_mynickname_txt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_robot);
		
		findView();
		initdatas();
		clickEvents();
	}

	private void clickEvents() {

		send_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final String msg = msgs.getText().toString();
				if (TextUtils.isEmpty(msg)) {
					return;
				}
				ChatMeassage toMessage = new ChatMeassage();
				toMessage.setDate(new Date());
				toMessage.setMsg(msg);
				toMessage.setType(Type.OUTCOMING);
				list.add(toMessage);
				adapter.notifyDataSetChanged();
				robot_liListView.setSelection(list.size() - 1);

				msgs.setText("");
				new Thread(new Runnable() {

					@Override
					public void run() {
						ChatMeassage chatMeassage = httpUtils.sendMessage(msg);
						Message message = Message.obtain();
						message.obj = chatMeassage;
						message.what = 0x100;
						handler.sendMessage(message);
					}
				}).start();
			}
		});
	}

	private void initdatas() {
		list = new ArrayList<ChatMeassage>();
		list.add(new ChatMeassage("您好，这里是牧笛。\n牧笛目前支持的功能有："
				+ "\n陪唠嗑✪ω✪\n看新闻٩(●̮̮̃●̃)۶\n查天气(^o^)/YES\n查列车┏ (゜ω゜)=☞\n查航班~(≧▽≦)/~\n偷偷告诉你：小牧笛还知\n道各种菜系做法的说（¯﹃¯）", Type.INCOMING,
				new Date()));
		adapter = new ChatMessageAdapter(RobotActivity.this, list);
		robot_liListView.setAdapter(adapter);

	}

	private void findView() {
		robot_liListView = (ListView) findViewById(R.id.robot_listView);
		msgs = (EditText) findViewById(R.id.et_input_msg);
		send_btn = (Button) findViewById(R.id.btn_send_msg);
		robot_to_img = (ImageView) findViewById(R.id.robot_to_img);
		robot_mynickname_txt = (TextView) findViewById(R.id.robot_mynickname_txt);
		
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 0x100) {
				ChatMeassage chatMeassage = (ChatMeassage) msg.obj;
				list.add(chatMeassage);
				adapter.notifyDataSetChanged();
				robot_liListView.setSelection(list.size() - 1);
			}
		}

	};

}
