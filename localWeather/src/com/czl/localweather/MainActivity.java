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

	private BaiduWeatherUtil mUtil; // ����������
	private List<DayWeather> mWeather; // ��������Դ
	private WeatherAdapter mAdapter; // ����������
	private HorizontalListView listView; // ������ʾ
	private TextView tvToday; // �ض���ʵʱ��ʾ
	private RelativeLayout background; // ���汳��

	private ProgressDialog mDialog; // ��ȡ����ǰ��ʾ

	// ͨ��handler��ȡ���ص�����ֹͣ��λ����ͨ���ӿڻص���������Դ
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
		mLocationClient = new LocationClient(getApplicationContext()); // ����LocationClient��
		mLocationClient.registerLocationListener(myListener); // ע���������
		initLocation();
		findViews();
		initDialog();
		mLocationClient.start();
	}

	/**
	 * ˢ�°�ť�ĵ���¼�
	 */
	public void refresh(View v) {
		initDialog();
		mLocationClient.start();
	}

	/**
	 * ��ȡ���ݲ���ʾǰ��ʾһ�����ȿ�
	 */
	private void initDialog() {
		mDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setTitle("���Ժ�");
		mDialog.setMessage("���ڻ�ȡ��������");
		mDialog.show();
	}

	/**
	 * ��ȡ�ؼ�
	 */
	private void findViews() {
		tvToday = (TextView) findViewById(R.id.tv_today);
		listView = (HorizontalListView) findViewById(R.id.listView);
		background = (RelativeLayout) findViewById(R.id.background);
	}

	/**
	 * ���ö�λ����
	 */
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// ��ѡ��Ĭ�ϸ߾��ȣ����ö�λģʽ���߾��ȣ��͹��ģ����豸
		option.setCoorType("bd09ll");// ��ѡ��Ĭ��gcj02�����÷��صĶ�λ�������ϵ
		int span = 10000;
		option.setScanSpan(span);// ��ѡ��Ĭ��0��������λһ�Σ����÷���λ����ļ����Ҫ���ڵ���1000ms������Ч��
		option.setIsNeedAddress(true);// ��ѡ�������Ƿ���Ҫ��ַ��Ϣ��Ĭ�ϲ���Ҫ
		option.setOpenGps(true);// ��ѡ��Ĭ��false,�����Ƿ�ʹ��gps
		option.setLocationNotify(true);// ��ѡ��Ĭ��false�������Ƿ�gps��Чʱ����1S1��Ƶ�����GPS���
		option.setIsNeedLocationDescribe(true);// ��ѡ��Ĭ��false�������Ƿ���Ҫλ�����廯�����������BDLocation.getLocationDescribe��õ�����������ڡ��ڱ����찲�Ÿ�����
		option.setIsNeedLocationPoiList(true);// ��ѡ��Ĭ��false�������Ƿ���ҪPOI�����������BDLocation.getPoiList��õ�
		option.setIgnoreKillProcess(false);// ��ѡ��Ĭ��true����λSDK�ڲ���һ��SERVICE�����ŵ��˶������̣������Ƿ���stop��ʱ��ɱ��������̣�Ĭ�ϲ�ɱ��
		option.SetIgnoreCacheException(false);// ��ѡ��Ĭ��false�������Ƿ��ռ�CRASH��Ϣ��Ĭ���ռ�
		option.setEnableSimulateGps(false);// ��ѡ��Ĭ��false�������Ƿ���Ҫ����gps��������Ĭ����Ҫ
		mLocationClient.setLocOption(option);
	}

	/**
	 * ÿ�ν���ǰ���ж�����״̬
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * ѡ����汳��
	 */
	public void chooseBackground(String weather) {
		try {
			weather = new String(weather.getBytes(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (weather.contains("��")) {
			background.setBackgroundResource(R.drawable.bg_qing);
		} else if (weather.contains("��")) {
			background.setBackgroundResource(R.drawable.bg_yu);
		} else if (weather.contains("��")) {
			background.setBackgroundResource(R.drawable.bg_duoyun);
		} else if (weather.contains("��")) {
			background.setBackgroundResource(R.drawable.bg_yu);
		} else if (weather.contains("ѩ")) {
			background.setBackgroundResource(R.drawable.bg_xue);
		}
	}

	/**
	 * ��λ������
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
			// GPS��λ���
			// sb.append("\nspeed : ");
			// sb.append(location.getSpeed());// ��λ������ÿСʱ
			// sb.append("\nsatellite : ");
			// sb.append(location.getSatelliteNumber());
			// sb.append("\nheight : ");
			// sb.append(location.getAltitude());// ��λ����
			// sb.append("\ndirection : ");
			// sb.append(location.getDirection());// ��λ��
			// sb.append("\naddr : ");
			// sb.append(location.getAddrStr());
			// sb.append("\ndescribe : ");
			// sb.append("gps��λ�ɹ�");
			//
			// } else if (location.getLocType() ==
			// BDLocation.TypeNetWorkLocation) {// ���綨λ���
			// sb.append("\naddr : ");
			// sb.append(location.getAddrStr());
			// // ��Ӫ����Ϣ
			// sb.append("\noperationers : ");
			// sb.append(location.getOperators());
			// sb.append("\ndescribe : ");
			// sb.append("���綨λ�ɹ�");
			// } else if (location.getLocType() ==
			// BDLocation.TypeOffLineLocation) {// ���߶�λ���
			// sb.append("\ndescribe : ");
			// sb.append("���߶�λ�ɹ������߶�λ���Ҳ����Ч��");
			// } else if (location.getLocType() == BDLocation.TypeServerError) {
			// sb.append("\ndescribe : ");
			// sb.append("��������綨λʧ�ܣ����Է���IMEI�źʹ��嶨λʱ�䵽loc-bugs@baidu.com��������׷��ԭ��");
			// } else if (location.getLocType() ==
			// BDLocation.TypeNetWorkException) {
			// sb.append("\ndescribe : ");
			// sb.append("���粻ͬ���¶�λʧ�ܣ����������Ƿ�ͨ��");
			// } else if (location.getLocType() ==
			// BDLocation.TypeCriteriaException) {
			// sb.append("\ndescribe : ");
			// sb.append("�޷���ȡ��Ч��λ���ݵ��¶�λʧ�ܣ�һ���������ֻ���ԭ�򣬴��ڷ���ģʽ��һ���������ֽ�����������������ֻ�");
			// }
			// sb.append("\nlocationdescribe : ");
			// sb.append(location.getLocationDescribe());// λ�����廯��Ϣ
			// List<Poi> list = location.getPoiList();// POI����
			// if (list != null) {
			// sb.append("\npoilist size = : ");
			// sb.append(list.size());
			// for (Poi p : list) {
			// sb.append("\npoi= : ");
			// sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
			// }
			// }
			// Log.i("czl", sb.toString());
			// ����Ϊnullʱ��ʾ��
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
				Toast.makeText(MainActivity.this, "������������", 1).show();
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
