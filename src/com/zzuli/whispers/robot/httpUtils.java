package com.zzuli.whispers.robot;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.zzuli.whispers.robot.ChatMeassage.Type;

public class httpUtils {

	private static final String URL = "http://www.tuling123.com/openapi/api";
	private static final String API_KEY = "11970f0f8906dd4d11b0ed26fed3b713";

	public static ChatMeassage sendMessage(String msg) {

		ChatMeassage chatMessage = new ChatMeassage();
		String json = doGet(msg);
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(json);
			int code = jsonObject.getInt("code");
			switch (code) {
			case 100000:
				String text = jsonObject.getString("text");
				chatMessage.setMsg(text);
				break;
			case 200000:
				String utext = jsonObject.getString("text")+"\n";
				String url = jsonObject.getString("url");
				String texturl = utext + url;
				chatMessage.setMsg(texturl);
				break;
			case 308000:
				String recipeText = jsonObject.getString("text") + "\n";
				JSONArray recipelist = jsonObject.getJSONArray("list");
				String recipeText2 = "";
				for (int i = 0; i < recipelist.length(); i++) {
					JSONObject recipeObject = recipelist.getJSONObject(i);
					String recipename = recipeObject.getString("name") + ":\n";
					String recipeinfo = recipeObject.getString("info") + "\n";
					String recipedetailurl = "";
					if (i == recipelist.length() - 1) {
						recipedetailurl = "详情请戳："
								+ recipeObject.getString("detailurl");
					} else {
						recipedetailurl = "详情请戳："
								+ recipeObject.getString("detailurl") + "\n";
					}
					recipeText2 = recipeText2 + recipename + recipeinfo
							+ recipedetailurl;
				}
				chatMessage.setMsg(recipeText + recipeText2);
				break;
			case 302000:
				String newstext = jsonObject.getString("text") + ":\n";
				JSONArray newsJsonArray = jsonObject.getJSONArray("list");
				String newsText = "";
				for (int i = 0; i < newsJsonArray.length(); i++) {
					JSONObject newsObject = newsJsonArray.getJSONObject(i);
					String newsarticle = "标题："
							+ newsObject.getString("article");
			
					String newsdetailurl = "";
					if (i == newsJsonArray.length() - 1) {
						newsdetailurl = "详情请戳："
								+ newsObject.getString("detailurl");
					} else {
						newsdetailurl = "详情请戳："
								+ newsObject.getString("detailurl") + "\n";
					}
					newsText = newsText + newsarticle 
							+ newsdetailurl;
				}
				chatMessage.setMsg(newstext + newsText);
				break;
			case 305000:
				String traintext = jsonObject.getString("text") + "\n";
				JSONArray trainJsonArray = jsonObject.getJSONArray("list");
				String trainText = "";
				for (int i = 0; i < trainJsonArray.length(); i++) {
					JSONObject trainJsonObject = trainJsonArray
							.getJSONObject(i);
					String trainnum = trainJsonObject.getString("trainnum")
							+ "\n";
					String trainstart = "起始站:"
							+ trainJsonObject.getString("start") + "\n";
					String trainterminal = "到达站："
							+ trainJsonObject.getString("terminal") + "\n";
					String trainstarttime = "开车时间："
							+ trainJsonObject.getString("starttime") + "\n";
					String trainendtime = "到达时间："
							+ trainJsonObject.getString("endtime") + "\n";
					String traindetailurl = "";
					if (i == trainJsonArray.length() - 1) {
						traindetailurl = "详情请戳："
								+ trainJsonObject.getString("detailurl");
					} else {
						traindetailurl = "详情请戳："
								+ trainJsonObject.getString("detailurl") + "\n";
					}
					trainText = trainText + trainnum + trainstart
							+ trainterminal + trainstarttime + trainendtime
							+ traindetailurl;
				}

				chatMessage.setMsg(traintext + trainText);
				break;
			case 306000:
				String flighttext = jsonObject.getString("text") + "\n";
				JSONArray flightJsonArray = jsonObject.getJSONArray("list");
				String flightText = "";
				for (int i = 0; i < flightJsonArray.length(); i++) {
					JSONObject flightJsonObject = flightJsonArray
							.getJSONObject(i);
					String flight = "航班："
							+ flightJsonObject.getString("flight") + "\n";

					String flightstarttime = "起飞时间："
							+ flightJsonObject.getString("starttime") + "\n";
					String flightendtime = "";
					if (i == flightJsonArray.length() - 1) {
						flightendtime = "到达时间："
								+ flightJsonObject.getString("endtime") ;
					} else {
						flightendtime = "到达时间："
								+ flightJsonObject.getString("endtime") + "\n";
					}
					if (flightendtime.equals("")) {
						flightendtime= "到达时间：未知";
					}

					flightText = flightText + flight + flightstarttime
							+ flightendtime;
				}
				chatMessage.setMsg(flighttext + flightText);
				break;
			case 40002:
				chatMessage.setMsg("请求内容为空，啊哦~");
				break;
			case 40005:
				chatMessage.setMsg("暂不支持该功能，啊哦~");
				break;
			case 40006:
				chatMessage.setMsg("服务器升级中，啊哦~");
				break;
			case 40007:
				chatMessage.setMsg("服务器数据格式异常，啊哦~");
				break;
			}

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		chatMessage.setDate(new Date());
		chatMessage.setType(Type.INCOMING);
		return chatMessage;

	}

	private static String doGet(String msg) {
		String result = "";
		try {
			String url = URL + "?key=" + API_KEY + "&info="
					+ URLEncoder.encode(msg, "UTF-8");
			HttpClient client = new DefaultHttpClient();

			HttpGet get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

}
