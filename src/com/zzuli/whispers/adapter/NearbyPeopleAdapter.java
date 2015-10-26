package com.zzuli.whispers.adapter;

import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzuli.whispers.bean.User;
import com.zzuli.whispers.main.CustomApplcation;
import com.zzuli.whispers.main.R;
import com.zzuli.whispers.utils.ImageLoadOptions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NearbyPeopleAdapter extends BaseAdapter {

	private List<User> list;
	private User user;
	private LayoutInflater inflater;
	

	public NearbyPeopleAdapter(Context context, List<User> list) {
		this.list=list;
		inflater=LayoutInflater.from(context);

	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=inflater.inflate(R.layout.item_near_people, null);
			holder.imageView=(ImageView) convertView.findViewById(R.id.iv_avatar);
			holder.name=(TextView) convertView.findViewById(R.id.tv_name);
			holder.distance=(TextView) convertView.findViewById(R.id.tv_distance);
			holder.logintime=(TextView) convertView.findViewById(R.id.tv_logintime);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		user=list.get(position);
		String avatar=user.getAvatar();
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, holder.imageView, ImageLoadOptions.getOptions());
		} else {
			holder.imageView.setImageResource(R.drawable.default_head);
		}
		holder.name.setText(user.getNick());
		BmobGeoPoint location = user.getLocation();
		String currentLat = CustomApplcation.getInstance().getLatitude();
		String currentLong = CustomApplcation.getInstance().getLongtitude();
		if(location!=null && !currentLat.equals("") && !currentLong.equals("")){
			double distance = DistanceOfTwoPoints(Double.parseDouble(currentLat),Double.parseDouble(currentLong),user.getLocation().getLatitude(), 
					user.getLocation().getLongitude());
			holder.distance.setText(String.valueOf(distance)+"米");
		}else{
			holder.distance.setText("未知");
		}
		holder.logintime.setText("最近登录时间:"+user.getUpdatedAt());

		return convertView;
	}


	
	static class ViewHolder{
		public ImageView imageView;
		public TextView name;
		public TextView distance;
		public TextView logintime;
	}
	
	private static final double EARTH_RADIUS = 6378137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	public static double DistanceOfTwoPoints(double lat1, double lng1,double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}
}
