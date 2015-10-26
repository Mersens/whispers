package com.zzuli.whispers.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzuli.whispers.bean.QCommunity;
import com.zzuli.whispers.bean.User;
import com.zzuli.whispers.main.R;
import com.zzuli.whispers.utils.ImageLoadOptions;

public class NearItemListAdapter extends  BaseContentAdapter<QCommunity> {
	private List<QCommunity> list=null;
	public NearItemListAdapter(Context context, List<QCommunity> list) {
		super(context, list);
		this.list=list;
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if(convertView==null){
			convertView=inflater.inflate(R.layout.layout_item, null);
			holder=new ViewHolder();
			holder.head=(ImageView) convertView.findViewById(R.id.img_head);
			holder.content=(TextView) convertView.findViewById(R.id.content);
			holder.picture=(ImageView) convertView.findViewById(R.id.img_photo);
			holder.userName=(TextView) convertView.findViewById(R.id.userName);
			holder.comment=(TextView) convertView.findViewById(R.id.item_action_comment);
			holder.shared=(TextView) convertView.findViewById(R.id.item_action_share);
			convertView.setTag(holder);			
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		setMessage(holder,position);
		return convertView;
	}
	
	private void setMessage(ViewHolder holder,int pos) {
		
		QCommunity community=list.get(pos);
		User user=community.getAuthor();
		String avatar = user.getAvatar();
		if(avatar!=null&& !avatar.equals("")){
			ImageLoader.getInstance().displayImage(avatar, holder.head, ImageLoadOptions.getOptions());
		}else{
			holder.head.setImageResource(R.drawable.header);
		}
		holder.comment.setVisibility(View.GONE);
		holder.shared.setVisibility(View.GONE);
		holder.userName.setText(user.getNick());
		holder.content.setText(community.getContent());
		if(null == community.getContentfigureurl()){
			holder.picture.setVisibility(View.GONE);
			}else{
				ImageLoader.getInstance().displayImage(community.getContentfigureurl().getFileUrl(context), holder.picture,
						ImageLoadOptions.getOptions());	
			}
	}

	static class ViewHolder{
		public ImageView head;
		public ImageView picture;
		public TextView content;
		public TextView userName;
		public TextView comment;
		public TextView shared;
		
	}
}
