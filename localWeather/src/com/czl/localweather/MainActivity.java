package com.czl.localweather;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.location.Poi;
import com.czl.localweather.adapter.WeatherAdapter;
import com.czl.localweather.bean.DayWeather;
import com.czl.localweather.util.BaiduWeatherUtil;
import com.czl.localweather.util.BaiduWeatherUtil.VolleyCallBack;
import com.czl.localweather.view.HorizontalListView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	private BaiduWeatherUtil mUtil; // 天气工具类
	private List<DayWeather> mWeather; // 天气数据源
	private WeatherAdapter mAdapter; // 天气适配器
	private HorizontalListView listView; // 布局显示
	private TextView tvToday; // 特定的实时显示
	private RelativeLayout background; // 界面背景

	private ProgressDialog mDialog; // 获取数据前显示

	// 通过handler获取本地地区，停止定位。并通过接口回调返回数据源
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mLocationClient.stop();
			String param = (String) msg.obj;
			mUtil.getWeatherData(param, new VolleyCallBack() {
				@Override
				public void onSuccess(List<DayWeather> res) {
					mWeather.clear();
					tvToday.append(res.get(0).getDate());
					mWeather.addAll(res);
					listView.setAdapter(mAdapter);
					chooseBackground(res.get(0).getWeather());
					mDialog.dismiss();
					Log.i("czl", res.toString());
				}
			});
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mUtil = new BaiduWeatherUtil(this);
		mWeather = new ArrayList<DayWeather>();
		mAdapter = new WeatherAdapter(this, mWeather);
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		initLocation();
		findViews();
		initDialog();
		mLocationClient.start();
	}

	/**
	 * 刷新按钮的点击事件
	 */
	public void refresh(View v) {
		initDialog();
		mLocationClient.start();
	}

	/**
	 * 获取数据并显示前显示一个进度框
	 */
	private void initDialog() {
		mDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setTitle("请稍后");
		mDialog.setMessage("正在获取天气数据");
		mDialog.show();
	}

	/**
	 * 获取控件
	 */
	private void findViews() {
		tvToday = (TextView) findViewById(R.id.tv_today);
		listView = (HorizontalListView) findViewById(R.id.listView);
		background = (RelativeLayout) findViewById(R.id.background);
	}

	/**
	 * 配置定位参数
	 */
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 10000;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
	}

	/**
	 * 每次进入前都判断网络状态
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 选择界面背景
	 */
	public void chooseBackground(String weather) {
		try {
			weather = new String(weather.getBytes(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (weather.contains("晴")) {
			background.setBackgroundResource(R.drawable.bg_qing);
		} else if (weather.contains("雨")) {
			background.setBackgroundResource(R.drawable.bg_yu);
		} else if (weather.contains("云")) {
			background.setBackgroundResource(R.drawable.bg_duoyun);
		} else if (weather.contains("雷")) {
			background.setBackgroundResource(R.drawable.bg_yu);
		} else if (weather.contains("雪")) {
			background.setBackgroundResource(R.drawable.bg_xue);
		}
	}

	/**
	 * 定位监听器
	 *
	 */
	class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			// StringBuffer sb = new StringBuffer(256);
			// sb.append("time : ");
			// sb.append(location.getTime());
			// sb.append("\nerror code : ");
			// sb.append(location.getLocType());
			// sb.append("\nlatitude : ");
			// sb.append(location.getLatitude());
			// sb.append("\nlontitude : ");
			// sb.append(location.getLongitude());
			// sb.append("\nradius : ");
			// sb.append(location.getRadius());
			// if (location.getLocType() == BDLocation.TypeGpsLocation) {//
			// GPS定位结果
			// sb.append("\nspeed : ");
			// sb.append(location.getSpeed());// 单位：公里每小时
			// sb.append("\nsatellite : ");
			// sb.append(location.getSatelliteNumber());
			// sb.append("\nheight : ");
			// sb.append(location.getAltitude());// 单位：米
			// sb.append("\ndirection : ");
			// sb.append(location.getDirection());// 单位度
			// sb.append("\naddr : ");
			// sb.append(location.getAddrStr());
			// sb.append("\ndescribe : ");
			// sb.append("gps定位成功");
			//
			// } else if (location.getLocType() ==
			// BDLocation.TypeNetWorkLocation) {// 网络定位结果
			// sb.append("\naddr : ");
			// sb.append(location.getAddrStr());
			// // 运营商信息
			// sb.append("\noperationers : ");
			// sb.append(location.getOperators());
			// sb.append("\ndescribe : ");
			// sb.append("网络定位成功");
			// } else if (location.getLocType() ==
			// BDLocation.TypeOffLineLocation) {// 离线定位结果
			// sb.append("\ndescribe : ");
			// sb.append("离线定位成功，离线定位结果也是有效的");
			// } else if (location.getLocType() == BDLocation.TypeServerError) {
			// sb.append("\ndescribe : ");
			// sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
			// } else if (location.getLocType() ==
			// BDLocation.TypeNetWorkException) {
			// sb.append("\ndescribe : ");
			// sb.append("网络不同导致定位失败，请检查网络是否通畅");
			// } else if (location.getLocType() ==
			// BDLocation.TypeCriteriaException) {
			// sb.append("\ndescribe : ");
			// sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
			// }
			// sb.append("\nlocationdescribe : ");
			// sb.append(location.getLocationDescribe());// 位置语义化信息
			// List<Poi> list = location.getPoiList();// POI数据
			// if (list != null) {
			// sb.append("\npoilist size = : ");
			// sb.append(list.size());
			// for (Poi p : list) {
			// sb.append("\npoi= : ");
			// sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
			// }
			// }
			// Log.i("czl", sb.toString());
			// 当区为null时显示市
			String district = location.getDistrict();
			if (null == district || "null".equals(district)) {
				tvToday.setText(location.getCity() + " ");
			} else {
				tvToday.setText(district + " ");
			}
			String param = location.getLongitude() + "," + location.getLatitude();
			Log.i("czl", param);
			Message msg = Message.obtain();
			msg.obj = param;
			if (isNetworkConnected()) {
				mHandler.sendMessage(msg);
			} else {
				Toast.makeText(MainActivity.this, "请检查网络连接", 1).show();
				mLocationClient.stop();
				mDialog.dismiss();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("czl", "ondestroy");
		if (mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
	}
}
