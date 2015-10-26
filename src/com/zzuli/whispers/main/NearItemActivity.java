package com.zzuli.whispers.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzuli.whispers.adapter.NearItemListAdapter;
import com.zzuli.whispers.adapter.ViewPagerAdapter;
import com.zzuli.whispers.bean.QCommunity;
import com.zzuli.whispers.bean.User;
import com.zzuli.whispers.utils.EneityUtils;
import com.zzuli.whispers.utils.ImageLoadOptions;
import com.zzuli.whispers.view.ViewPagerCompat;

public class NearItemActivity extends ActivityBase {
	private User user=null;
	// ViewPager页
	private View view1, view2; // 需要滑动的页卡
	private ViewPagerCompat viewPager; // viewpager
	private ViewPagerAdapter shopViewPagerAdapter;
	private List<QCommunity> qcommunitylist;
	@SuppressWarnings("unused")
	private PagerTitleStrip pagerTitleStrip; // viewpager的标题
	
	private PagerTabStrip pagerTabStrip; // 一个viewpager的指示器，效果就是一个横的粗的下划线
	private List<View> viewList; // 把需要滑动的页卡添加到这个list中
	private List<String> titleList; // viewpager的标题
	private TextView name;
	private TextView nick;
	private TextView sex;
	private TextView sign;
	private ImageView head;
	private ListView listView;
	private TextView no_msg;
	private Button add_frid;
	private NearItemListAdapter adapter=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_item);
		initViews();
		initDatas();
	
	}
	private void initViews() {
		initTopBarForLeft("附近的人");
		viewPager = (ViewPagerCompat) findViewById(R.id.viewpager);
		pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagertab);
		pagerTabStrip.setTabIndicatorColor(Color.argb(255, 255, 127, 39));
		pagerTabStrip.setDrawFullUnderline(false);
		pagerTabStrip.setTextSpacing(50);
		pagerTabStrip.setTextColor(Color.argb(255, 255, 127, 39));
		view1 = LayoutInflater.from(this)
				.inflate(R.layout.layout_near_msg, null);
		view2 = LayoutInflater.from(this).inflate(R.layout.layout_near_list,
				null);
		add_frid=(Button) view1.findViewById(R.id.add_frid);
		add_frid.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//生成进度条
				final ProgressDialog progress = new ProgressDialog(NearItemActivity.this);
				progress.setMessage("正在添加...");
				progress.setCanceledOnTouchOutside(false);
				progress.show();
				//向陌生人发送添加好友请求
				BmobChatManager.getInstance(NearItemActivity.this).sendTagMessage(BmobConfig.TAG_ADD_CONTACT, user.getObjectId(),new PushListener() {
					
					@Override
					public void onSuccess() {
						progress.dismiss();
						ShowToast("发送请求成功，等待对方验证!");
					}
					
					@Override
					public void onFailure(int arg0, final String arg1) {
						progress.dismiss();
						ShowToast("发送请求失败，请重新添加!");
						ShowLog("发送请求失败:"+arg1);
					}
				});
			}
		});
		name=(TextView) view1.findViewById(R.id.tv_set_name);
		nick=(TextView) view1.findViewById(R.id.nick);
		sex=(TextView) view1.findViewById(R.id.tv_set_gender);
		sign=(TextView) view1.findViewById(R.id.user_sign_text);
		head=(ImageView) view1.findViewById(R.id.head);
		
		listView=(ListView) view2.findViewById(R.id.listView);
		no_msg=(TextView) view2.findViewById(R.id.txt_nochat);
		no_msg.setVisibility(View.GONE);

		viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
		viewList.add(view1);
		viewList.add(view2);
		
		titleList = new ArrayList<String>();// 每个页面的Title数据
		titleList.add("详细信息");
		titleList.add("最近动态");
		shopViewPagerAdapter = new ViewPagerAdapter(viewList, titleList);
		viewPager.setAdapter(shopViewPagerAdapter);
		viewPager.setCurrentItem(0);
	}
	
		private void setMessage() {
		name.setText(user.getUsername());
		nick.setText(user.getNick());
		sign.setText(user.getSignature());
		sex.setText(user.getSex() == true ? "男" : "女");
		String avatar=user.getAvatar();
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, head,
					ImageLoadOptions.getOptions());
		} else {
			head.setImageResource(R.drawable.head);
		}
		if(qcommunitylist.size()==0){
			no_msg.setVisibility(View.VISIBLE);
		}
		adapter=new NearItemListAdapter(NearItemActivity.this,qcommunitylist);
		listView.setAdapter(adapter);
	}
	
	private void initDatas() {
		user=(User) getIntent().getSerializableExtra("itemuser");
		BmobQuery<QCommunity> query = new BmobQuery<QCommunity>();
		query.order("-createdAt");
		BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
		query.include("author");
		query.findObjects(NearItemActivity.this, new FindListener<QCommunity>() {
			@Override
			public void onSuccess(List<QCommunity> list) {
				qcommunitylist=EneityUtils.getQcommunity(user, list);
				setMessage();
			}
			@Override
			public void onError(int arg0, String arg1) {
				
			}
		});
	}

	
}


