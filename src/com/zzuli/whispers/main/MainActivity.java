package com.zzuli.whispers.main;

import com.zzuli.whispers.arcmenu.ArcMenu;
import com.zzuli.whispers.arcmenu.ArcMenu.OnMenuItemClickListener;
import com.zzuli.whispers.fragment.ContactFragment;
import com.zzuli.whispers.fragment.FindFragment;
import com.zzuli.whispers.fragment.RecentFragment;
import com.zzuli.whispers.fragment.SettingsFragment;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.inteface.EventListener;

public class MainActivity extends ActivityBase implements EventListener {
	private LinearLayout whispersLayout;
	private LinearLayout findLayout;
	private LinearLayout frdLayout;
	private LinearLayout setLayout;

	private ImageButton mImgWeixin;
	private ImageButton mImgFrd;
	private ImageButton mImgAddress;
	private ImageButton mImgSettings;

	private ImageView whispersImg;

	private ImageView findImg;
	private ImageView frdImg;
	private ImageView setImg;
	private LinearLayout[] mTabs;
	private ContactFragment contactFragment;
	private RecentFragment recentFragment;
	private FindFragment findFragment;
	private SettingsFragment settingFragment;
	private Fragment[] fragments;
	private int index;
	private int currentTabIndex;

	private ArcMenu mArcMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initNewMessageBroadCast();
		initTagMessageBroadCast();
		initView();
		initTab();
	}

	private void initView() {
		mTabs = new LinearLayout[4];
		mArcMenu = (ArcMenu) findViewById(R.id.id_menu);
		mArcMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public void onClick(View view, int pos) {
				switch (pos) {
				case 1:
					index = 3;
					setTab(3);
					break;
				case 2:
					index = 2;
					setTab(2);
					break;
				case 3:
					index = 1;
					setTab(1);
					break;
				case 4:
					index = 0;
					setTab(0);
					break;

				}
				if (currentTabIndex != index) {
					FragmentTransaction trx = getSupportFragmentManager()
							.beginTransaction();
					trx.hide(fragments[currentTabIndex]);
					if (!fragments[index].isAdded()) {
						trx.add(R.id.fragment_container, fragments[index]);
					}
					trx.show(fragments[index]).commit();
				}
				mTabs[currentTabIndex].setSelected(false);
				// 把当前tab设为选中状态
				mTabs[index].setSelected(true);
				currentTabIndex = index;
			}
		});
		whispersImg = (ImageView) findViewById(R.id.whispers_img);
		findImg = (ImageView) findViewById(R.id.find_img);
		frdImg = (ImageView) findViewById(R.id.frd_img);
		setImg = (ImageView) findViewById(R.id.set_img);
		// mImgWeixin = (ImageButton) findViewById(R.id.id_tab_weixin_img);
		// mImgFrd = (ImageButton) findViewById(R.id.id_tab_frd_img);
		// mImgAddress = (ImageButton) findViewById(R.id.id_tab_address_img);
		// mImgSettings = (ImageButton) findViewById(R.id.id_tab_settings_img);

		whispersLayout = (LinearLayout) findViewById(R.id.whispers_layout);
		findLayout = (LinearLayout) findViewById(R.id.find_layout);
		frdLayout = (LinearLayout) findViewById(R.id.frd_layout);
		setLayout = (LinearLayout) findViewById(R.id.set_layout);
		mTabs[0] = whispersLayout;
		mTabs[1] = findLayout;
		mTabs[2] = frdLayout;
		mTabs[3] = setLayout;
		mTabs[0].setSelected(true);
	}

	private void initTab() {

		contactFragment = new ContactFragment();
		recentFragment = new RecentFragment();
		settingFragment = new SettingsFragment();
		findFragment = new FindFragment();
		fragments = new Fragment[] { recentFragment, findFragment,
				contactFragment, settingFragment };
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, recentFragment)
				.add(R.id.fragment_container, contactFragment)
				.hide(contactFragment).show(recentFragment).commit();
	}

	// @Override
	// public void onClick(View v) {
	// switch (v.getId()) {
	// case R.id.id_tab_weixin:
	// index = 0;
	// setTab(0);
	// break;
	// case R.id.id_tab_frd:
	// index = 1;
	// setTab(1);
	// break;
	// case R.id.id_tab_address:
	// index = 2;
	// setTab(2);
	// break;
	// case R.id.id_tab_settings:
	// index = 3;
	// setTab(3);
	// break;
	// }
	// if (currentTabIndex != index) {
	// FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
	// trx.hide(fragments[currentTabIndex]);
	// if (!fragments[index].isAdded()) {
	// trx.add(R.id.fragment_container, fragments[index]);
	// }
	// trx.show(fragments[index]).commit();
	// }
	// mTabs[currentTabIndex].setSelected(false);
	// //把当前tab设为选中状态
	// mTabs[index].setSelected(true);
	// currentTabIndex = index;
	// }

	@Override
	protected void onResume() {
		super.onResume();
		MyMessageReceiver.ehList.add(this);
		MyMessageReceiver.mNewNum = 0;

	}

	private void setTab(int i) {
		resetImgs();
		switch (i) {
		case 0:
			whispersImg.setImageResource(R.drawable.tab_whispers_pressed);
			break;
		case 1:
			findImg.setImageResource(R.drawable.tab_find_pressed);
			break;
		case 2:
			frdImg.setImageResource(R.drawable.tab_frd_pressed);
			break;
		case 3:
			setImg.setImageResource(R.drawable.tab_settings_pressed);
			break;

		}
	}

	private void resetImgs() {
		whispersImg.setImageResource(R.drawable.tab_whispers_normal);
		findImg.setImageResource(R.drawable.tab_find_normal);
		frdImg.setImageResource(R.drawable.tab_frd_normal);
		setImg.setImageResource(R.drawable.tab_settings_normal);
	}

	// private void setTab(int i)
	// {
	// resetImgs();
	// switch (i)
	// {
	// case 0:
	// mImgWeixin.setImageResource(R.drawable.tab_weixin_pressed);
	// break;
	// case 1:
	// mImgFrd.setImageResource(R.drawable.tab_find_frd_pressed);
	// break;
	// case 2:
	// mImgAddress.setImageResource(R.drawable.tab_address_pressed);
	// break;
	// case 3:
	// mImgSettings.setImageResource(R.drawable.tab_settings_pressed);
	// break;
	// }
	// }
	//
	// private void resetImgs()
	// {
	// mImgWeixin.setImageResource(R.drawable.tab_weixin_normal);
	// mImgFrd.setImageResource(R.drawable.tab_find_frd_normal);
	// mImgAddress.setImageResource(R.drawable.tab_address_normal);
	// mImgSettings.setImageResource(R.drawable.tab_settings_normal);
	// }

	@Override
	protected void onPause() {
		super.onPause();
		MyMessageReceiver.ehList.remove(this);
	}

	@Override
	public void onMessage(BmobMsg message) {
		refreshNewMsg(message);
	}

	private void refreshNewMsg(BmobMsg message) {
		boolean isAllow = CustomApplcation.getInstance().getSpUtil()
				.isAllowVoice();
		if (isAllow) {
			CustomApplcation.getInstance().getMediaPlayer().start();
		}

		if (message != null) {
			BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(
					true, message);
		}
		if (currentTabIndex == 0) {
			if (recentFragment != null) {
				recentFragment.refresh();
			}
		}
	}

	NewBroadcastReceiver newReceiver;

	private void initNewMessageBroadCast() {
		newReceiver = new NewBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(
				BmobConfig.BROADCAST_NEW_MESSAGE);
		intentFilter.setPriority(3);
		registerReceiver(newReceiver, intentFilter);
	}

	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			refreshNewMsg(null);
			abortBroadcast();
		}
	}

	TagBroadcastReceiver userReceiver;

	private void initTagMessageBroadCast() {
		userReceiver = new TagBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(
				BmobConfig.BROADCAST_ADD_USER_MESSAGE);
		intentFilter.setPriority(3);
		registerReceiver(userReceiver, intentFilter);
	}

	private class TagBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			BmobInvitation message = (BmobInvitation) intent
					.getSerializableExtra("invite");
			refreshInvite(message);
			abortBroadcast();
		}
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		if (isNetConnected) {
			ShowToast(R.string.network_tips);
		}
	}

	@Override
	public void onAddUser(BmobInvitation message) {
		refreshInvite(message);
	}

	private void refreshInvite(BmobInvitation message) {
		boolean isAllow = CustomApplcation.getInstance().getSpUtil()
				.isAllowVoice();
		if (isAllow) {
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		if (currentTabIndex == 1) {
			if (contactFragment != null) {
				contactFragment.refresh();
			}
		} else {
			// 同时提醒通知
			String tickerText = message.getFromname() + "请求添加好友";
			boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil()
					.isAllowVibrate();
			BmobNotifyManager.getInstance(this).showNotify(isAllow,
					isAllowVibrate, R.drawable.ic_launcher, tickerText,
					message.getFromname(), tickerText.toString(),
					NewFriendActivity.class);
		}
	}

	@Override
	public void onOffline() {
		showOfflineDialog(this);
	}

	@Override
	public void onReaded(String conversionId, String msgTime) {
	}

	private static long firstTime;

	@Override
	public void onBackPressed() {
		if (firstTime + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
		} else {
		}
		firstTime = System.currentTimeMillis();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(newReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(userReceiver);
		} catch (Exception e) {
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { 
        	this.confirmExit();
             return false; 
        }else { 
            return super.onKeyDown(keyCode, event); 
        } 
	}
	public void confirmExit() {
	  	 // 退出确认  
	  	AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);  
		  	ad.setTitle("退出");  
		  	ad.setMessage("是否退出软件?");  
		  	
		  	ad.setPositiveButton("是", new DialogInterface.OnClickListener() {// 退出按钮  
	  	@SuppressWarnings("deprecation")
		@Override  
	  public void onClick(DialogInterface dialog, int i) {   
	  	int sdk_Version = android.os.Build.VERSION.SDK_INT;     
	  	if (sdk_Version >= 8) {     
		  	Intent startMain = new Intent(Intent.ACTION_MAIN);     
		  	startMain.addCategory(Intent.CATEGORY_HOME);     
		  	startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
		  	startActivity(startMain);     
	  	    System.exit(0);     
	  	 }else if (sdk_Version < 8) {     
		  	ActivityManager activityMgr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);     
		  	activityMgr.restartPackage(getPackageName());     
	  	  }  
	  	}  
	  });  
	  	   ad.setNegativeButton("否", new DialogInterface.OnClickListener() {  
	  @Override  
	  public void onClick(DialogInterface dialog, int i) {  
	  	}  
	  });  
	  	ad.show();
	  	}

}
