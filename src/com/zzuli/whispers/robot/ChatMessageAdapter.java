package com.zzuli.whispers.robot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;

import cn.bmob.v3.BmobUser;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzuli.whispers.bean.User;
import com.zzuli.whispers.main.R;
import com.zzuli.whispers.robot.ChatMeassage.Type;
import com.zzuli.whispers.utils.ImageLoadOptions;

import android.R.integer;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatMessageAdapter extends BaseAdapter {

	private List<ChatMeassage> list;
	private LayoutInflater inflater;
    private Context context;
    private ImageView imgView;
    private TextView tv;
	public ChatMessageAdapter(Context context, List<ChatMeassage> list) {
		inflater = LayoutInflater.from(context);
		this.list = list;
		this.context=context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		ChatMeassage chatMeassage = list.get(position);
		if (chatMeassage.getType() == Type.INCOMING) {
			return 0;
		}

		return 1;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		User user = BmobUser.getCurrentUser(context, User.class);
		ChatMeassage chatMeassage = list.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			if (getItemViewType(position) == 0) {
				convertView = inflater.inflate(R.layout.item_from_msg, null);
				viewHolder = new ViewHolder();
				viewHolder.date = (TextView) convertView
						.findViewById(R.id.id_form_msg_date);
				viewHolder.message = (TextView) convertView
						.findViewById(R.id.tv_from_msg_info);

			} else {
				convertView = inflater.inflate(R.layout.item_to_msg, null);
				viewHolder = new ViewHolder();
                tv=(TextView) convertView.findViewById(R.id.robot_mynickname_txt);
				imgView=(ImageView) convertView.findViewById(R.id.robot_to_img);
				String avatar = user.getAvatar();
				if(avatar!=null&& !avatar.equals("")){
					ImageLoader.getInstance().displayImage(avatar, imgView, ImageLoadOptions.getOptions());
				}else{
					imgView.setImageResource(R.drawable.head);
				}
				tv.setText(user.getUsername());
				viewHolder.date = (TextView) convertView
						.findViewById(R.id.id_to_msg_date);
				viewHolder.message = (TextView) convertView
						.findViewById(R.id.tv_to_msg_info);
			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		viewHolder.date.setText(format.format(chatMeassage.getDate()));
		viewHolder.message.setText(chatMeassage.getMsg());
		return convertView;
	}
	public class ViewHolder {
		TextView date;
		TextView message;
	}

}
