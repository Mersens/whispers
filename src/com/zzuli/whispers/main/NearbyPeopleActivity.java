package com.zzuli.whispers.main;

import java.util.List;
import java.util.Map;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.FindListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.zzuli.whispers.adapter.NearbyPeopleAdapter;
import com.zzuli.whispers.adapter.UserFriendAdapter;
import com.zzuli.whispers.bean.User;
import com.zzuli.whispers.utils.CollectionUtils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class NearbyPeopleActivity extends ActivityBase {
	private List<User> list;
	private ListView nearpeople;
	private boolean isFirstIn = true;
	private ProgressDialog progress;
	private double QUERY_KILOMETERS = 1;// 默认查询1公里范围的人
	private double mLatitude;
	private double mLongtitude;
	private NearbyPeopleAdapter adapter;
	// mLongtitude, mLatitude===113.514961------34.818265
	// 定位相关
	private LocationClient mLocationClient;
	private MyLocationListener mLocationListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_nearby);
		initEvent();
		initLocation();

	}

	public void initEvent() {
		initTopBarForLeft("附近的人");
		nearpeople = (ListView) findViewById(R.id.nearpeople);
		nearpeople.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(NearbyPeopleActivity.this,
						NearItemActivity.class);
				User itemuser = list.get(position);
				Bundle bundle = new Bundle();
				bundle.putSerializable("itemuser", itemuser);
				intent.putExtras(bundle);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		});

	}

	private void queryMyfriends() {
		CustomApplcation.getInstance().setContactList(
				CollectionUtils.list2map(BmobDB.create(this).getContactList()));
		Map<String, BmobChatUser> users = CustomApplcation.getInstance()
				.getContactList();
		filledData(CollectionUtils.map2list(users));
	}

	private void filledData(List<BmobChatUser> frdlist) {
		for(int i=0;i<frdlist.size();i++){
			String name=frdlist.get(i).getUsername();
			for(int j=0;j<list.size();j++){
				if(name.equals(list.get(j).getUsername())){
					list.remove(j);
				}
			}
		}
	}

	private void initNearByList(boolean isUpdate) {
		if (!isUpdate) {
			progress = new ProgressDialog(this);
			progress.setMessage("正在查询附近的人...");
			progress.setCanceledOnTouchOutside(true);
			progress.show();
		}

		userManager.queryKiloMetersListByPage(isUpdate, 0, "location",
				mLongtitude, mLatitude, true, QUERY_KILOMETERS, null, false,
				new FindListener<User>() {

					@Override
					public void onSuccess(List<User> arg0) {
						progress.dismiss();
						if (CollectionUtils.isNotNull(arg0)) {
							list = arg0;
							queryMyfriends();
							adapter = new NearbyPeopleAdapter(
									NearbyPeopleActivity.this, list);
							nearpeople.setAdapter(adapter);
						} else {
							ShowToast("暂无陌生人!");
							progress.dismiss();
						}
					}

					@Override
					public void onError(int arg0, String arg1) {
						ShowToast("查找陌生人失败!" + arg1);
						progress.dismiss();
					}

				});
	}

	@Override
	protected void onStop() {
		super.onStop();
		mLocationClient.stop();
		isFirstIn = true;
	}

	private void initLocation() {

		mLocationClient = new LocationClient(this);
		mLocationClient.start();
		mLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mLocationListener);
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);

	}

	private class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {

			if (isFirstIn) {
				mLatitude = location.getLatitude();
				mLongtitude = location.getLongitude();
				initNearByList(false);
				isFirstIn = false;
			}

		}
	}
}
