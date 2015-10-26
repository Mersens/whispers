package com.zzuli.whispers.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.social.share.core.BMShareListener;
import cn.bmob.social.share.core.ErrorInfo;
import cn.bmob.social.share.core.data.BMPlatform;
import cn.bmob.social.share.core.data.ShareData;
import cn.bmob.social.share.view.BMShare;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzuli.whispers.bean.QCommunity;
import com.zzuli.whispers.bean.User;
import com.zzuli.whispers.main.CommentActivity;
import com.zzuli.whispers.main.QCommunityActivity;
import com.zzuli.whispers.main.R;
import com.zzuli.whispers.utils.ImageLoadOptions;


public class QCommunityAdapter extends BaseContentAdapter<QCommunity> {

	private QCommunityActivity activity;
	private List<QCommunity> list=null;

	
	public QCommunityAdapter(Context context, List<QCommunity> list,QCommunityActivity activity) {
		super(context, list);

		this.context=context;
		this.list=list;
		this.activity=activity;
		
	}
	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		
		    ViewHolder holder=null;
			convertView=inflater.inflate(R.layout.layout_item, null);
			holder=new ViewHolder();
			holder.head=(ImageView) convertView.findViewById(R.id.img_head);
			holder.content=(TextView) convertView.findViewById(R.id.content);
			holder.picture=(ImageView) convertView.findViewById(R.id.img_photo);
			holder.userName=(TextView) convertView.findViewById(R.id.userName);
			holder.shared=(TextView) convertView.findViewById(R.id.item_action_share);
			holder.comment=(TextView) convertView.findViewById(R.id.item_action_comment);

		final QCommunity community=list.get(position);
		final User user=community.getAuthor();
		String avatar = user.getAvatar();
		if(avatar!=null&& !avatar.equals("")){
			ImageLoader.getInstance().displayImage(avatar, holder.head, ImageLoadOptions.getOptions());
		}else{
			holder.head.setImageResource(R.drawable.header);
		}
		holder.userName.setText(user.getNick());
		holder.content.setText(community.getContent());
		if(null == community.getContentfigureurl()){
			holder.picture.setVisibility(View.GONE);
			}else{
				ImageLoader.getInstance().displayImage(community.getContentfigureurl().getFileUrl(context), holder.picture,
						ImageLoadOptions.getOptions());	
			}
		
		holder.shared.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				testShare(community.getContent().toString(),user.getAvatar().toString());
			}
		});
		
		holder.comment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(context, CommentActivity.class);
				intent.putExtra("data", community);
				context.startActivity(intent);
				activity.overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		});
		return convertView;
	}


	private void testShare(String content,String imgurl) {
		ShareData shareData = new ShareData();
		shareData.setTitle("轻语分享");
		shareData.setDescription("轻社区分享");
		shareData.setText(content);
		shareData.setTarget_url("http://www.codenow.cn/");
		shareData.setImageUrl(imgurl);
		BMShareListener whiteViewListener = new BMShareListener() {
			@Override
			public void onSuccess() {
			}

			@Override
			public void onPreShare() {
			}

			@Override
			public void onError(ErrorInfo error) {
			}

			@Override
			public void onCancel() {
			}

		};
		
		BMShare share = new BMShare(activity);
		share.setShareData(shareData);
		share.addListener(BMPlatform.PLATFORM_WECHAT, whiteViewListener);
		share.addListener(BMPlatform.PLATFORM_WECHATMOMENTS, whiteViewListener);
		share.addListener(BMPlatform.PLATFORM_SINAWEIBO, whiteViewListener);
		share.addListener(BMPlatform.PLATFORM_RENN, whiteViewListener);
		share.addListener(BMPlatform.PLATFORM_TENCENTWEIBO, whiteViewListener);
		share.addListener(BMPlatform.PLATFORM_QQ, whiteViewListener);
		share.addListener(BMPlatform.PLATFORM_QZONE, whiteViewListener);
		share.show();
		Log.d("bmob", "分享end");
	}
	
	
	
	
	static class ViewHolder{
		public ImageView head;
		public ImageView picture;
		public TextView content;
		public TextView userName;
		public TextView shared;
		public TextView comment;
	}
}
