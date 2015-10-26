package com.zzuli.whispers.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzuli.whispers.bean.User;
import com.zzuli.whispers.fragment.RecentFragment;
import com.zzuli.whispers.main.R;
import com.zzuli.whispers.main.SlideView;
import com.zzuli.whispers.main.SlideView.OnSlideListener;
import com.zzuli.whispers.utils.FaceTextUtils;
import com.zzuli.whispers.utils.ImageLoadOptions;
import com.zzuli.whispers.utils.MessageItem;
import com.zzuli.whispers.utils.TimeUtil;
/** 会话适配器
 * @ClassName: MessageRecentAdapter
 * @Description: TODO
 */
public class MessageRecentAdapter extends ArrayAdapter<BmobRecent> implements Filterable{
	private LayoutInflater inflater;
	private List<BmobRecent> list;
	private Context context;
	private User u=new User();
	private SlideView mLastSlideViewWithStatusOn;
	private Handler handler;
	public MessageRecentAdapter(Context context, int textViewResourceId, List<BmobRecent> objects,Handler handler) {
		super(context, textViewResourceId, objects);
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.list = objects;
		this.handler=handler;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final BmobRecent item = list.get(position);
		SlideView slideView = (SlideView) convertView;
		if (slideView == null) {
			View itemView  = inflater.inflate(R.layout.item_conversation, null);
			slideView = new SlideView(context);
			RelativeLayout layout=(RelativeLayout) slideView.findViewById(R.id.holder);
			layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteRecent(item);
				}
			});
			
			// 这里把item加入到slideView
			slideView.setContentView(itemView);
			// 下面是做一些数据缓存
			slideView.setOnSlideListener(new OnSlideListener() {
				
				@Override
				public void onSlide(View view, int status) {
					// 如果当前存在已经打开的SlideView，那么将其关闭
					if (mLastSlideViewWithStatusOn != null
							&& mLastSlideViewWithStatusOn != view) {
						mLastSlideViewWithStatusOn.shrink();
					}
					// 记录本次处于打开状态的view
					if (status == SLIDE_STATUS_ON) {
						mLastSlideViewWithStatusOn = (SlideView) view;
					}
					
				}
			});
		}
		ImageView iv_recent_avatar = ViewHolder.get(slideView, R.id.iv_recent_avatar);
		TextView tv_recent_name = ViewHolder.get(slideView, R.id.tv_recent_name);
		TextView tv_recent_msg = ViewHolder.get(slideView, R.id.tv_recent_msg);
		TextView tv_recent_time = ViewHolder.get(slideView, R.id.tv_recent_time);
		TextView tv_recent_unread = ViewHolder.get(slideView, R.id.tv_recent_unread);
		//填充数据
		String avatar = item.getAvatar();
		if(avatar!=null&& !avatar.equals("")){
			ImageLoader.getInstance().displayImage(avatar, iv_recent_avatar, ImageLoadOptions.getOptions());
		}else{
			iv_recent_avatar.setImageResource(R.drawable.head);
		}
		tv_recent_name.setText(item.getNick());
		tv_recent_time.setText(TimeUtil.getChatTime(item.getTime()));
		//显示内容
		if(item.getType()==BmobConfig.TYPE_TEXT){
			SpannableString spannableString = FaceTextUtils.toSpannableString(context, item.getMessage());
			tv_recent_msg.setText(spannableString);
		}else if(item.getType()==BmobConfig.TYPE_IMAGE){
			tv_recent_msg.setText("[图片]");
		}else if(item.getType()==BmobConfig.TYPE_LOCATION){
			String all =item.getMessage();
			if(all!=null &&!all.equals("")){
				//位置类型的信息组装格式：地理位置&维度&经度
				String address = all.split("&")[0];
				tv_recent_msg.setText("[位置]"+address);
			}
		}else if(item.getType()==BmobConfig.TYPE_VOICE){
			tv_recent_msg.setText("[语音]");
		}
		
		int num = BmobDB.create(context).getUnreadCount(item.getTargetid());
		if (num > 0) {
			tv_recent_unread.setVisibility(View.VISIBLE);
			tv_recent_unread.setText(num + "");
		} else {
			tv_recent_unread.setVisibility(View.GONE);
		}
		MessageItem.putMsg(item.getTargetid(), slideView);
        slideView.shrink();
		return slideView;
	}

	public void deleteRecent(BmobRecent recent){
		this.remove(recent);
		BmobDB.create(context).deleteRecent(recent.getTargetid());
		BmobDB.create(context).deleteMessages(recent.getTargetid());
		MessageItem.removeItem(recent.getTargetid());
		Message msg=new Message();
		msg.what=1;
		handler.sendMessage(msg);


	}


	
	
}
