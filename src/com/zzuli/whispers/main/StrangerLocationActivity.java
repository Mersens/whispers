package com.zzuli.whispers.main;
import java.util.List;
import java.util.Map;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfigeration;
import com.baidu.mapapi.map.MyLocationConfigeration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.zzuli.whispers.bean.User;
import com.zzuli.whispers.main.MyOrientationListener.OnOrientationListener;
import com.zzuli.whispers.utils.CollectionUtils;
import com.zzuli.whispers.utils.RandomUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class StrangerLocationActivity extends Activity {
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private Context context;
	BmobUserManager userManager;
	// 定位相关
	private LocationClient mLocationClient;
	private MyLocationListener mLocationListener;
	private boolean isFirstIn = true;
	private double mLatitude;
	private double mLongtitude;
	// 自定义定位图标
	private BitmapDescriptor mIconLocation;
	private MyOrientationListener myOrientationListener;
	private float mCurrentX;
	private LocationMode mLocationMode;
	// 覆盖物相关
	private BitmapDescriptor mMarker;
	private RelativeLayout mMarkerLy;
	private double QUERY_KILOMETERS = 1;//默认查询1公里范围的人
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		userManager = BmobUserManager.getInstance(this);
		setContentView(R.layout.activity_map);

		this.context = this;

		initView();
		// 初始化定位
		initLocation();
		initMarker();

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener()
		{
			@Override
			public boolean onMarkerClick(Marker marker)
			{
				Bundle extraInfo = marker.getExtraInfo();
				User info = (User) extraInfo.getSerializable("info");
			    Toast.makeText(StrangerLocationActivity.this, info.getUsername(), Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(StrangerLocationActivity.this, GameMainActivity.class);
				intent.putExtra("user",info);
				intent.putExtra("num", RandomUtils.getRandom());
				startActivity(intent);
				return true;
			}
		});
		mBaiduMap.setOnMapClickListener(new OnMapClickListener()
		{

			@Override
			public boolean onMapPoiClick(MapPoi arg0)
			{
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0)
			{
				mMarkerLy.setVisibility(View.GONE);
				mBaiduMap.hideInfoWindow();
			}
		});
	}

	private void initMarker()
	{
		mMarker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
		mMarkerLy = (RelativeLayout) findViewById(R.id.id_maker_ly);

	}

	private void initLocation()
	{

		mLocationMode = LocationMode.NORMAL;
		mLocationClient = new LocationClient(this);
		mLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mLocationListener);

		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
		// 初始化图标
		mIconLocation = BitmapDescriptorFactory
				.fromResource(R.drawable.navi_map_gps_locked);
		myOrientationListener = new MyOrientationListener(context);

		myOrientationListener
				.setOnOrientationListener(new OnOrientationListener()
				{
					@Override
					public void onOrientationChanged(float x)
					{
						mCurrentX = x;
					}
				});

	}

	private void initView()
	{
		mMapView = (MapView) findViewById(R.id.id_bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
		
	}
	int curPage = 0;
	ProgressDialog progress ;
	private void initNearByList(boolean isUpdate) {
		if(!isUpdate){
			progress = new ProgressDialog(this);
			progress.setMessage("正在查询附近的人...");
			progress.setCanceledOnTouchOutside(true);
			progress.show();
		}
		
			userManager.queryKiloMetersListByPage(isUpdate,0,"location", mLongtitude, mLatitude, true,QUERY_KILOMETERS,null,false,new FindListener<User>() {
				@Override
				public void onSuccess(List<User> arg0) {
					progress.dismiss();
					if (CollectionUtils.isNotNull(arg0)) {
						queryMyfriends(arg0);
						  
				    }else{
				    Toast.makeText(StrangerLocationActivity.this, "暂无陌生人!", Toast.LENGTH_SHORT).show();
				     progress.dismiss();
				    }
				}
				@Override
				public void onError(int arg0, String arg1) {
					Toast.makeText(StrangerLocationActivity.this, "查找陌生人失败!", Toast.LENGTH_SHORT).show();
					progress.dismiss();
				}

			});
	}
	
	private void queryMyfriends(List<User> arg0) {
		CustomApplcation.getInstance().setContactList(
				CollectionUtils.list2map(BmobDB.create(this).getContactList()));
		Map<String, BmobChatUser> users = CustomApplcation.getInstance()
				.getContactList();
		filledData(CollectionUtils.map2list(users),arg0);
	}

	private void filledData(List<BmobChatUser> frdlist,List<User> arg0) {
		for(int i=0;i<frdlist.size();i++){
			String name=frdlist.get(i).getUsername();
			for(int j=0;j<arg0.size();j++){
				if(name.equals(arg0.get(j).getUsername())){
					arg0.remove(j);
				}
			}
		}
		addOverlays(arg0);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		// 开启定位
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted())
			mLocationClient.start();
		// 开启方向传感器
		myOrientationListener.start();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onPause();
	}

	@Override
	protected void onStop()
	{
		super.onStop();

		// 停止定位
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		// 停止方向传感器
		myOrientationListener.stop();

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	 
	/**
	 * 添加覆盖物
	 * 
	 * @param infos
	 */
	private void addOverlays(List<User> infos)
	{
		mBaiduMap.clear();
		LatLng latLng = null;
		Marker marker = null;
		OverlayOptions options;
		for (User user : infos)
		{
			// 经纬度
			latLng = new LatLng(user.getLocation().getLatitude(), user.getLocation().getLongitude());
			// 图标
			options = new MarkerOptions().position(latLng).icon(mMarker).zIndex(5);
	        OverlayOptions textOption = new TextOptions().bgColor(R.id.id_menu)
            .fontSize(40).fontColor(0xFFFFFFFF).text("[ "+user.getNick()+" ]").rotate(0)
            .position(latLng);
            mBaiduMap.addOverlay(textOption);
			marker = (Marker) mBaiduMap.addOverlay(options);
			Bundle arg0 = new Bundle();
			arg0.putSerializable("info", user);
			marker.setExtraInfo(arg0);
		}

		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.setMapStatus(msu);
	}

	/**
	 * 定位到我的位置
	 */
	private void centerToMyLocation()
	{
		LatLng latLng = new LatLng(mLatitude, mLongtitude);
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.animateMapStatus(msu);
	}

	private class MyLocationListener implements BDLocationListener
	{
		@Override
		public void onReceiveLocation(BDLocation location)
		{

			MyLocationData data = new MyLocationData.Builder()//
					.direction(mCurrentX)//
					.accuracy(location.getRadius())//
					.latitude(location.getLatitude())//
					.longitude(location.getLongitude())//
					.build();
			mBaiduMap.setMyLocationData(data);
			mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(
					com.baidu.mapapi.map.MyLocationConfigeration.LocationMode.NORMAL, true, null));

			// 更新经纬度
			mLatitude = location.getLatitude();
			mLongtitude = location.getLongitude();

			if (isFirstIn)
			{
				LatLng latLng = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
				mBaiduMap.animateMapStatus(msu);
				isFirstIn = false;
				initNearByList(false);
				Toast.makeText(context, location.getAddrStr(),
						Toast.LENGTH_SHORT).show();
			}

		}
	}

}
