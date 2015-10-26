package com.zzuli.whispers.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtils {
	public static Bitmap getBitMap(String path){
		System.out.println("path================="+path);
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeFile(path,options);
		double ratio=Math.max(options.outWidth * 1.0d/1024f, options.outHeight * 1.0d/1024f);
		options.inSampleSize=(int) Math.ceil(ratio);
		options.inJustDecodeBounds=false;
		return BitmapFactory.decodeFile(path, options);
	}

}
