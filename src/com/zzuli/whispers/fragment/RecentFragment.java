package com.zzuli.whispers.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;

import com.renn.rennsdk.param.GetAppParam;
import com.zzuli.whispers.adapter.DialogTips;
import com.zzuli.whispers.adapter.MessageRecentAdapter;
import com.zzuli.whispers.main.ChatActivity;
import com.zzuli.whispers.main.ClearEditText;
import com.zzuli.whispers.main.FragmentBase;
import com.zzuli.whispers.main.ListViewCompat;
import com.zzuli.whispers.main.R;
import com.zzuli.whispers.main.SlideView;
import com.zzuli.whispers.main.SlideView.OnSlideListener;
import com.zzuli.whispers.utils.MessageItem;

/** 最近会话
  * @ClassName: RecentFragment
  * @Description: TODO
  */
public class RecentFragment extends FragmentBase implements OnItemClickListener,OnItemLongClickListener{
	private ListViewCompat listview;
	private ClearEditText mClearEditText;
	private MessageRecentAdapter adapter;
	private List<BmobRecent> list;
	// 上次处于打开状态的SlideView
	private SlideView mLastSlideViewWithStatusOn;
	private View v;
	private TextView tv;
    Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
		switch (msg.what) {
		case 1:
		refresh();	
	    break;

		default:
			break;
		}	
			
		}
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v=inflater.inflate(R.layout.fragment_recent, container, false);
		tv=(TextView) v.findViewById(R.id.txt_nochat);
		tv.setVisibility(View.GONE);
		return v;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	
	
	private void initView(){
		initTopBarForOnlyTitle("轻语");
		listview = (ListViewCompat)findViewById(R.id.list);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
		list=BmobDB.create(getActivity()).queryRecents();
		if(list==null || list.size()==0){
			tv.setVisibility(View.VISIBLE);
		}
		adapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, list,handler);
		listview.setAdapter(adapter);
		mClearEditText = (ClearEditText)findViewById(R.id.et_msg_search);
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				adapter.getFilter().filter(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
	}

	private void deleteRecent(BmobRecent recent){
		adapter.remove(recent);
		BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
		BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
		MessageItem.removeItem(recent.getTargetid());
		refresh();

	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		BmobRecent recent = adapter.getItem(position);
		showDeleteDialog(recent);
		return true;
	}
	
	public void showDeleteDialog(final BmobRecent recent) {
		DialogTips dialog = new DialogTips(getActivity(),recent.getUserName(),"删除会话", "确定",true,true);
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteRecent(recent);
			}
		});
		dialog.show();
		dialog = null;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		BmobRecent recent = adapter.getItem(position);
		BmobDB.create(getActivity()).resetUnread(recent.getTargetid());
		BmobChatUser user = new BmobChatUser();
		user.setAvatar(recent.getAvatar());
		user.setNick(recent.getNick());
		user.setUsername(recent.getUserName());
		user.setObjectId(recent.getTargetid());
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra("user", user);
		startAnimActivity(intent);
	}
	
	private boolean hidden;
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if(!hidden){
			refresh();
		}
	}
	
	public void refresh(){
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					adapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, BmobDB.create(getActivity()).queryRecents(),handler);
					if(adapter.isEmpty()){
						tv.setVisibility(View.VISIBLE);
					}else{
						tv.setVisibility(View.GONE);
					}
					listview.setAdapter(adapter);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(!hidden){
			refresh();
		}
	}
	

		
	}
	

	

