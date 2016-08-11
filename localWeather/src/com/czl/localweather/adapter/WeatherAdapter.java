package com.czl.localweather.adapter;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;
import com.czl.localweather.R;
import com.czl.localweather.bean.DayWeather;
import com.czl.localweather.util.BitmapCacheUtil;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherAdapter extends BaseAdapter {

	private Context context;
	private List<DayWeather> list;
	private ImageLoader imgLoader;
	private RequestQueue mQueue;
	private ImageCache mCache;

	private static final int TYPE_1 = 0;
	private static final int TYPE_2 = 1;

	/**
	 * adapter≥ı ºªØ
	 */
	public WeatherAdapter(Context context, List<DayWeather> list) {
		this.context = context;
		this.list = list;
		mQueue = Volley.newRequestQueue(context);
		mCache = new BitmapCacheUtil();
		imgLoader = new ImageLoader(mQueue, mCache);
	}

	@Override
	public int getItemViewType(int position) {
		int p = position;
		if (p == 0) {
			return TYPE_1;
		} else {
			return TYPE_2;
		}
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_2 + 1;
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
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
		ViewHolder1 holder1 = null;
		ViewHolder2 holder2 = null;
		int type = getItemViewType(position);
		if (convertView == null) {
			switch (type) {
			case TYPE_1:
				convertView = LayoutInflater.from(context).inflate(R.layout.item1, null);
				holder1 = new ViewHolder1();
				holder1.img = (ImageView) convertView.findViewById(R.id.img);
				holder1.tvTemp = (TextView) convertView.findViewById(R.id.tv_temp);
				holder1.tvWeather = (TextView) convertView.findViewById(R.id.tv_weather);
				holder1.tvWind = (TextView) convertView.findViewById(R.id.tv_wind);
				holder1.tvPm = (TextView) convertView.findViewById(R.id.tv_pm);
				convertView.setTag(holder1);
				break;
			case TYPE_2:
				convertView = LayoutInflater.from(context).inflate(R.layout.item2, null);
				holder2 = new ViewHolder2();
				holder2.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
				holder2.img = (ImageView) convertView.findViewById(R.id.img);
				holder2.tvTemp = (TextView) convertView.findViewById(R.id.tv_temp);
				holder2.tvWeather = (TextView) convertView.findViewById(R.id.tv_weather);
				holder2.tvWind = (TextView) convertView.findViewById(R.id.tv_wind);
				convertView.setTag(holder2);
				break;
			}

		} else {
			switch (type) {
			case TYPE_1:
				holder1 = (ViewHolder1) convertView.getTag();
				break;
			case TYPE_2:
				holder2 = (ViewHolder2) convertView.getTag();
				break;
			}
		}
		DayWeather weather = list.get(position);
		switch (type) {
		case TYPE_1:
			getIcon(weather.getWeather(), holder1.img);
			holder1.tvTemp.setText(weather.getTemperature());
			holder1.tvWeather.setText(weather.getWeather());
			holder1.tvWind.setText(weather.getWind());
			String pm = weather.getPm25();
			if(!"".equals(pm)&&null!=pm){
				int pmInt = Integer.parseInt(pm);
				if(pmInt<=35){
					holder1.tvPm.setText(pmInt+" ”≈");
					holder1.tvPm.setBackgroundColor(Color.parseColor("#057748"));
				}else if(pmInt<=75){
					holder1.tvPm.setText(pmInt+" ¡º");
					holder1.tvPm.setBackgroundColor(Color.parseColor("#057748"));
				}else if(pmInt<=115){
					holder1.tvPm.setText(pmInt+" «·∂»Œ€»æ");
					holder1.tvPm.setTextColor(Color.parseColor("#ffa400"));
				}else if(pmInt<=150){
					holder1.tvPm.setText(pmInt+" ÷–∂»Œ€»æ");
					holder1.tvPm.setTextColor(Color.parseColor("#ffa400"));
				}else{
					holder1.tvPm.setText(pmInt+" —œ÷ÿŒ€»æ");
					holder1.tvPm.setTextColor(Color.parseColor("#ff2d51"));
				}
			}else{
				holder1.tvPm.setText("‘›Œﬁ");
			}
			break;
		case TYPE_2:
			getIcon(weather.getWeather(), holder2.img);
			holder2.tvDate.setText(weather.getDate());
			holder2.tvTemp.setText(weather.getTemperature());
			holder2.tvWeather.setText(weather.getWeather());
			holder2.tvWind.setText(weather.getWind());
			break;

		}
		return convertView;
	}

	class ViewHolder1 {
		ImageView img;
		TextView tvTemp;
		TextView tvWeather;
		TextView tvWind;
		TextView tvPm;
	}

	class ViewHolder2 {
		TextView tvDate;
		ImageView img;
		TextView tvTemp;
		TextView tvWeather;
		TextView tvWind;
	}

	public void getIcon(String weather, ImageView img) {
		try {
			weather = new String(weather.getBytes(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (weather.contains("«Á")) {
			img.setBackgroundResource(R.drawable.sunny);
		} else if (weather.contains("”Í")) {
			img.setBackgroundResource(R.drawable.rainy);
		} else if (weather.contains("‘∆")) {
			img.setBackgroundResource(R.drawable.cloudy);
		} else if (weather.contains("¿◊")) {
			img.setBackgroundResource(R.drawable.thunder);
		} else if (weather.contains("—©")) {
			img.setBackgroundResource(R.drawable.snowy);
		}

	}
}
