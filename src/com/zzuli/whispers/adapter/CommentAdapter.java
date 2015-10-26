package com.zzuli.whispers.adapter;

import java.util.List;

import com.zzuli.whispers.bean.Comment;
import com.zzuli.whispers.bean.QCommunity;
import com.zzuli.whispers.main.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class CommentAdapter extends BaseAdapter {
    private Context context;
    private List<Comment> list;
    private View view;
    private Comment comment;
	public TextView userName;
	public TextView commentContent;
	public TextView index;
	private LayoutInflater inflater;
    public CommentAdapter(Context context,List<Comment> list){
    	this.context=context;
    	this.list=list;
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
		comment=list.get(position);
		inflater=LayoutInflater.from(context);
		view=inflater.inflate(R.layout.comment_item, null);
		initView(view);
		setMessage(comment,position);
		return view;
	}
	private void initView(View v) {
		userName = (TextView)v.findViewById(R.id.userName_comment);
		commentContent = (TextView)v.findViewById(R.id.content_comment);
		index = (TextView)v.findViewById(R.id.index_comment);
	}
	private void setMessage(Comment comment,int position) {
		if(comment.getUser()!=null){
			userName.setText(comment.getUser().getUsername());
		}else{
			userName.setText("墙友");
		}
		index.setText((position+1)+"楼");
		commentContent.setText(comment.getCommentContent());
	}
}
