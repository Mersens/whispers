package com.zzuli.whispers.main;

import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzuli.whispers.adapter.QCommunityAdapter;
import com.zzuli.whispers.bean.QCommunity;
import com.zzuli.whispers.bean.User;
import com.zzuli.whispers.main.HeaderLayout.onRightImageButtonClickListener;
import com.zzuli.whispers.utils.ImageLoadOptions;

public class QCommunityActivity extends ActivityBase  {
	private QCommunityAdapter adapter;
	private ListView listView;
	private View head;
	private TextView name;
	private ImageView ic_head;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qcommunity_activity);
		initView();
		fetchData();
	}

	private void initView() {
		listView=(ListView) findViewById(R.id.listView1);
		head=getLayoutInflater().inflate(R.layout.layout_header, null);
		setHead();
		listView.addHeaderView(head);
		initTopBarForBoth("轻社区", R.drawable.ic_menu_edit,
				new onRightImageButtonClickListener() {
					@Override
					public void onClick() {
                        Intent intent=new Intent(QCommunityActivity.this,EditActivity.class);
					    startActivity(intent);
					    overridePendingTransition(R.anim.push_right_in,
								R.anim.push_right_out);
					}
				});
	 }
	
	public  void setHead() {
		name=(TextView) head.findViewById(R.id.name);
		ic_head=(ImageView) head.findViewById(R.id.head);
		User user = userManager.getCurrentUser(User.class);
		initOtherData(user.getUsername());
	}

	private void initOtherData(String name) {
		userManager.queryUser(name, new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowLog("onError onError:" + arg1);
			}

			@Override
			public void onSuccess(List<User> arg0) {
				// TODO Auto-generated method stub
				if (arg0 != null && arg0.size() > 0) {
					User user = arg0.get(0);
					updateUser(user);
				} else {
					ShowLog("onSuccess 查无此人");
				}
			}

		});
	}

	public  void updateUser(User user) {
		name.setText(user.getNick());
		String avatar=user.getAvatar();
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, ic_head,
					ImageLoadOptions.getOptions());
		} else {
			ic_head.setImageResource(R.drawable.head);
		}
	}
	@SuppressLint("ShowToast")
	private void fetchData() {
		BmobQuery<QCommunity> query = new BmobQuery<QCommunity>();
		query.order("-createdAt");
		BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
		//query.addWhereLessThan("createdAt", date);
		query.include("author");
		query.findObjects(QCommunityActivity.this, new FindListener<QCommunity>() {
			@Override
			public void onSuccess(List<QCommunity> list) {
				// TODO Auto-generated method stub
				if(list.size()!=0){
				  adapter=new QCommunityAdapter(QCommunityActivity.this,list,QCommunityActivity.this);
				  listView.setAdapter(adapter);
				}else{
				  Toast.makeText(QCommunityActivity.this, "没有数据哦！", Toast.LENGTH_LONG).show();	
				}
			}
			@Override
			public void onError(int arg0, String arg1) {
			Toast.makeText(QCommunityActivity.this, "数据查询出错哦！---->"+arg1, Toast.LENGTH_LONG).show();	

			}
		});
	}

}
