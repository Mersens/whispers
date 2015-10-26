package com.zzuli.whispers.main;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.social.share.core.BMShareListener;
import cn.bmob.social.share.core.ErrorInfo;
import cn.bmob.social.share.core.data.BMPlatform;
import cn.bmob.social.share.core.data.ShareData;
import cn.bmob.social.share.view.BMShare;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzuli.whispers.adapter.CommentAdapter;
import com.zzuli.whispers.bean.Comment;
import com.zzuli.whispers.bean.QCommunity;
import com.zzuli.whispers.bean.User;
import com.zzuli.whispers.utils.ImageLoadOptions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class CommentActivity extends ActivityBase implements OnClickListener {
	private ListView commentList;
	private EditText commentContent ;
	private Button commentCommit;
	public ImageView head;
	public ImageView picture;
	public TextView content;
	public TextView userName;
	public TextView shared;
	public TextView comment;
	private QCommunity qiangYu;
	private String commentEdit = "";
	private CommentAdapter mAdapter;
	private User user;
	private List<Comment> comments = new ArrayList<Comment>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		initTopBarForLeft("发表评论");
		qiangYu = (QCommunity)getIntent().getSerializableExtra("data");
		user=qiangYu.getAuthor();
		initView();
		initEvent();
	}

	private void initView() {
		commentList = (ListView)findViewById(R.id.comment_list);
		commentContent = (EditText)findViewById(R.id.comment_content);
		commentCommit = (Button)findViewById(R.id.comment_commit);
		comment = (TextView)findViewById(R.id.item_action_comment);
		head=(ImageView) findViewById(R.id.img_head);
		content=(TextView) findViewById(R.id.content);
		picture=(ImageView) findViewById(R.id.img_photo);
		userName=(TextView) findViewById(R.id.userName);
		shared=(TextView) findViewById(R.id.item_action_share);
		comment=(TextView) findViewById(R.id.item_action_comment);
		comment.setVisibility(View.GONE);
		shared.setVisibility(View.GONE);

	}
	private void initEvent() {
		mAdapter = new CommentAdapter(CommentActivity.this, comments);
		fetchComment();
		commentList.setAdapter(mAdapter);
		setListViewHeightBasedOnChildren(commentList);
		commentList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
		commentList.setCacheColorHint(0);
		commentList.setScrollingCacheEnabled(false);
		commentList.setScrollContainer(false);
		commentList.setFastScrollEnabled(true);
		commentList.setSmoothScrollbarEnabled(true);
		commentCommit.setOnClickListener(this);
		initMoodView(qiangYu);
	}

	private void initMoodView(QCommunity mood2) {
		// TODO Auto-generated method stub
		if(mood2 == null){
			return;
		}
		userName.setText(user.getUsername());
		content.setText(qiangYu.getContent());
		if(null == qiangYu.getContentfigureurl()){
			picture.setVisibility(View.GONE);
		}else{
			picture.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(qiangYu.getContentfigureurl().getFileUrl(this), picture,
					ImageLoadOptions.getOptions());			}
		String avatar = user.getAvatar();
		if(avatar!=null&& !avatar.equals("")){
			ImageLoader.getInstance().displayImage(avatar, head, ImageLoadOptions.getOptions());
		}else{
			head.setImageResource(R.drawable.head);
		}
	}

	private void fetchComment(){
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		query.addWhereRelatedTo("relation", new BmobPointer(qiangYu));
		query.include("user");
		query.order("createdAt");
		query.findObjects(this, new FindListener<Comment>() {
			@Override
			public void onSuccess(List<Comment> data) {
		     comments.clear();
             comments.addAll(data);
			 setListViewHeightBasedOnChildren(commentList);
			 mAdapter.notifyDataSetChanged();
			}
			@Override
			public void onError(int arg0, String arg1) {

			}
		});
	}
	private void onClickCommit() {
		// TODO Auto-generated method stub
		User currentUser = BmobUser.getCurrentUser(this,User.class);
		if(currentUser != null){//已登录
			commentEdit = commentContent.getText().toString().trim();
			if(TextUtils.isEmpty(commentEdit)){
				return;
			}
			publishComment(currentUser,commentEdit);
		}
		
	}
	
	private void hideSoftInput(){
		InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);  

		imm.hideSoftInputFromWindow(commentContent.getWindowToken(), 0);
	}
	private void publishComment(User user,String content){
		
		final Comment comment = new Comment();
		comment.setUser(user);
		comment.setCommentContent(content);
		comment.save(this, new SaveListener() {
			@Override
			public void onSuccess() {
				commentContent.setText("");
				Toast.makeText(CommentActivity.this, "发表成功！", 2).show();
				hideSoftInput();
				//将该评论与强语绑定到一起
				BmobRelation relation = new BmobRelation();
				relation.add(comment);
				qiangYu.setRelation(relation);
				qiangYu.update(CommentActivity.this, new UpdateListener() {
					@Override
					public void onSuccess() {
					  fetchComment();	
					}
					@Override
					public void onFailure(int arg0, String arg1) {
					Toast.makeText(CommentActivity.this, "发表失败！"+arg1, 2).show();
					}
				});
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
			}
		});
	}



	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.comment_commit:
			onClickCommit();
			break;
		default:
			break;
		}		
	}
	/*** 
     * 动态设置listview的高度 
     *  item 总布局必须是linearLayout
     * @param listView 
     */  
    public void setListViewHeightBasedOnChildren(ListView listView) {  
        ListAdapter listAdapter = listView.getAdapter();  
        if (listAdapter == null) {  
            return;  
        }  
        int totalHeight = 0;  
        for (int i = 0; i < listAdapter.getCount(); i++) {  
            View listItem = listAdapter.getView(i, null, listView);  
            listItem.measure(0, 0);  
            totalHeight += listItem.getMeasuredHeight();  
        }  
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
        params.height = totalHeight  
                + (listView.getDividerHeight() * (listAdapter.getCount()-1))  
                +15;  
        listView.setLayoutParams(params);  
    }

	

}
