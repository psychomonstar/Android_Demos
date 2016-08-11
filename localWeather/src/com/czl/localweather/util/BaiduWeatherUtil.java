package com.czl.localweather.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.czl.localweather.bean.DayWeather;
import com.google.gson.Gson;

import android.content.Context;
import android.widget.Toast;

public class BaiduWeatherUtil {
	public static final String APPKEY = "UUqIdrFccfSUsROMPdDzQMQl7mOr4Geb";
	private RequestQueue mQueue;
	private Context context;

	public BaiduWeatherUtil(Context context) {
		this.context = context;
		mQueue = Volley.newRequestQueue(context);
	}

	public static String getWeatherUrl(String param) {
		String url = null;
		// try {
		// url =
		// "http://api.map.baidu.com/telematics/v3/weather?location="+URLEncoder.encode(param,
		// "utf-8")+"&output=json&ak="+APPKEY;
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		url = "http://api.map.baidu.com/telematics/v3/weather?location=" + param + "&output=json&ak=" + APPKEY;
		return url;
	}

	public void getWeatherData(String name, final VolleyCallBack mCallBack) {
		StringRequest mRequest = new StringRequest(getWeatherUrl(name), new Listener<String>() {

			@Override
			public void onResponse(String result) {
				try {
					mCallBack.onSuccess(getWeatherList(result));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				Toast.makeText(context, "获取天气数据异常", 1).show();
			}
		});
		mQueue.add(mRequest);
	}

	/**
	 * 获得DayWeather的集合
	 */
	public List<DayWeather> getWeatherList(String res) throws JSONException {
		List<DayWeather> list = new ArrayList<DayWeather>();
		// Log.i("czl", res);
		JSONObject object = new JSONObject(res);
		JSONArray results = object.getJSONArray("results");
		JSONObject result = results.getJSONObject(0);
		String pm25 = result.getString("pm25");
		JSONArray weathers = result.getJSONArray("weather_data");
		Gson gson = new Gson();
		for (int i = 0; i < weathers.length(); i++) {
			list.add(gson.fromJson(weathers.get(i).toString(), DayWeather.class));
		}
		list.get(0).setPm25(pm25);
		return list;
	}

	public interface VolleyCallBack {
		void onSuccess(List<DayWeather> res);
	}
}
