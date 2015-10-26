package com.zzuli.whispers.utils;

import java.io.ByteArrayOutputStream;
import org.json.JSONObject;
import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import android.graphics.Bitmap;


public class FaceDetection {
	/**
	 * 
	 * @author Mersens
	 * 对外提供两个接口，用于返回事件的回调
	 *
	 */
	public interface Callback{
		void success(JSONObject result);
		void error(FaceppParseException exception);
	}
	
	/**
	 * @author Mersens
	 * 开启线程，向服务器提交数据请求处理,返回json的数据格式
	 * @param bm
	 * @param callback
	 * 
	 */
	public static void decete(final Bitmap bm,final Callback callback){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					HttpRequests requests=new HttpRequests(Config.KET,Config.SECRET,true,true);
					Bitmap bitmap=Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight());
					ByteArrayOutputStream st=new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG,100, st);
					byte[] arrays=st.toByteArray();
					PostParameters parm=new PostParameters();
					parm.setImg(arrays);
					JSONObject  jaJsonObject=requests.detectionDetect(parm);
					if(callback!=null){
						callback.success(jaJsonObject);
					}
				} catch (FaceppParseException e) {
					e.printStackTrace();
					callback.error(e);
				}
				
				
			}
		}).start();;
		
	}
	



	
	
}
