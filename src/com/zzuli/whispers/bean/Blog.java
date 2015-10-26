package com.zzuli.whispers.bean;

import cn.bmob.v3.BmobObject;

public class Blog extends BmobObject {
	
	private String brief;
	private User user;
	
	public Blog(){
		
	}
	
	public Blog(String brief, User user) {
		super();
		this.brief = brief;
		this.user = user;
	}
	
	public String getBrief() {
		return brief;
	}
	public void setBrief(String brief) {
		this.brief = brief;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}
