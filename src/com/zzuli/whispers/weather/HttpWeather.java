package com.zzuli.whispers.weather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HttpWeather {

	public HttpWeather() {
		super();
	}

	public static List<String> parseJsonArray(String cityJson) {
		List<String> list = new ArrayList<String>();
		try {
			JSONArray jsonArray = new JSONArray(cityJson);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String province = jsonObject.getString("省");
				JSONArray shiJsonArray = jsonObject.getJSONArray("市");
				for (int j = 0; j < shiJsonArray.length(); j++) {
					JSONObject fenshiJsonObject = shiJsonArray.getJSONObject(j);
					String fenshiName = fenshiJsonObject.getString("市名");
					list.add(fenshiName);
					String fenshiCode = fenshiJsonObject.getString("编码");
					list.add(fenshiCode);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	public static String getWeatherData(String url) {
		
		String result = "";

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = client.execute(get);

			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.i("TAG", "result=" + result);
		return result;
	}

	public static List<String> getWeatherNow(String result) throws Exception {
		List<String> listNow = new ArrayList<String>();
		JSONObject jsonObjectNow = new JSONObject(result);
		JSONObject weatherinfoNow = jsonObjectNow.getJSONObject("weatherinfo");
		String tempNow_0 = weatherinfoNow.getString("temp");
		listNow.add(tempNow_0);
		String windDirectionNow_1 = weatherinfoNow.getString("WD");
		listNow.add(windDirectionNow_1);
		String windLevelNow_2 = weatherinfoNow.getString("WS");
		listNow.add(windLevelNow_2);
		String relativeHumidity_3 = weatherinfoNow.getString("SD");
		listNow.add(relativeHumidity_3);
		String timeNow_4 = weatherinfoNow.getString("time");
		listNow.add(timeNow_4);
		String cityNow_5 = weatherinfoNow.getString("city");
		listNow.add(cityNow_5);
		Log.i("TAG", "temp=" + listNow.get(4));
		return listNow;

	}
}
