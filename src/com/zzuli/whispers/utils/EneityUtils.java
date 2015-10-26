package com.zzuli.whispers.utils;

import java.util.ArrayList;
import java.util.List;

import com.zzuli.whispers.bean.QCommunity;
import com.zzuli.whispers.bean.User;

public class EneityUtils {
	
	public static List<QCommunity> getQcommunity(User user,List<QCommunity> list){
		List<QCommunity> beanlist=new ArrayList<QCommunity>();
		if(list==null || user==null){
			return beanlist;
		}
		String username=user.getUsername();
		for(QCommunity bean:list){
			if(bean.getAuthor().getUsername().equals(username)){
				beanlist.add(bean);
			}
		}
		return beanlist;
	}

}
