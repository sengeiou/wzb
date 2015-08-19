package anti.drop.device.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import anti.drop.device.R;
import anti.drop.device.utils.SharedPreferencesUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

public class MapLocationActivity extends Activity{

	private ImageView backView;
	private TextView titleView;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private MapStatusUpdate msu;
	private LocationMode mCurrentMode;
	// ��λ���
	private LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	BitmapDescriptor mCurrentMarker;
	BitmapDescriptor mDeviceMarker;
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ
	private String deviceLongitude = "";
	private String deviceLatitude = "";

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);
		setContentView(R.layout.map);
		initView();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		deviceLongitude = SharedPreferencesUtils.getInstanse(this).getDeviceLongitude();
		deviceLatitude = SharedPreferencesUtils.getInstanse(this).getDeviceLatitude();
		addMarker(Double.parseDouble(deviceLatitude),Double.parseDouble(deviceLongitude));
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	
	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
		
	}
	
	@Override
	protected void onDestroy() {
		mMapView = null;
//		mMapView.onDestroy();
		super.onDestroy();
		
	}

	private void findViewById() {
		backView = (ImageView) findViewById(R.id.title_back);
		titleView = (TextView) findViewById(R.id.title_text);
		mMapView = (MapView) findViewById(R.id.prelost_machine_map);
	}

	private void setListener() {
		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	private void addMarker(double arg1,double arg2){
		LatLng deviceLng = new LatLng(arg1, arg2);
		OverlayOptions ooA = new MarkerOptions().position(deviceLng).zIndex(9).icon(mDeviceMarker);
		mBaiduMap.addOverlay(ooA);
	}

	private void initView() {
		findViewById();
		titleView.setText("��ͼ");
		mBaiduMap = mMapView.getMap();
		// ������λͼ��
		mBaiduMap.setMyLocationEnabled(true);
		msu = MapStatusUpdateFactory.zoomTo(14.0f);//1��������ű���
		mBaiduMap.setMapStatus(msu);
		mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.curr_location);
		mDeviceMarker = BitmapDescriptorFactory.fromResource(R.drawable.device_location);
		mCurrentMode = LocationMode.NORMAL;
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
		// ��λ��ʼ��
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		setListener();
	}
	
	/**
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null)
				return;
			
			double lati = location.getLatitude()+0.00256;
			double longi = location.getLongitude()+0.00256;
			SharedPreferencesUtils.getInstanse(MapLocationActivity.this).setDeviceLatitude(String.valueOf(lati));
			SharedPreferencesUtils.getInstanse(MapLocationActivity.this).setDeviceLongitude(String.valueOf(longi));
			
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

}
