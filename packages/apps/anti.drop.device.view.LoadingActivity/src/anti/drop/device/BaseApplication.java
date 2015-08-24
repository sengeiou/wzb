package anti.drop.device;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.R.integer;
import android.app.Application;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;
import anti.drop.device.utils.BluetoothLeClass;
import anti.drop.device.utils.LocationUtil;
import anti.drop.device.utils.SharedPreferencesUtils;

import com.baidu.mapapi.SDKInitializer;

/**
 * Ӧ�������һЩ��ֵ̬�ڸ����н��г�ʼ��
 * @author LuoYong
 */
public class BaseApplication extends Application{
	
	private final String TAG = BaseApplication.class.getSimpleName();
	
	public static Context mContext;
	private static Properties mProperties;
	public static BaseApplication instance;
	
	public BluetoothLeClass ble;
	public BluetoothGatt gatt[]=new BluetoothGatt[100];
	
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this.getApplicationContext();
		init();
		instance = this;
		SDKInitializer.initialize(getApplicationContext());
		
	}
	
	private void init() {
		loadConfigFile();//���������ļ�
		// һ����Ӧ�ã��жϸ��豸�Ƿ�֧�ֶ�����BLE��֧��
		boolean isSupport = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
		if (isSupport) {
			ble=new BluetoothLeClass(mContext);
			LocationUtil location = new LocationUtil(this);
			double lati = location.getmLatitude()+0.00256;
			double longi = location.getmLongitude()+0.00256;
			SharedPreferencesUtils.getInstanse(this).setDeviceLatitude(String.valueOf(lati));
			SharedPreferencesUtils.getInstanse(this).setDeviceLongitude(String.valueOf(longi));
		}else{
			Toast.makeText(this, "��ǰ�豸��֧����������4.0����", 1000);
		}
	}
	
	public void set_ble(BluetoothLeClass b){
		ble=b;
	}
	
	public BluetoothLeClass get_ble(){
		return ble;
	}
	
	public void set_gatt(BluetoothGatt bluetoothGatt,int arg0){
		if(arg0<100){
			gatt[arg0]=bluetoothGatt;
		}
		Log.d("www","app set i="+arg0+"gatt ="+gatt[arg0]);
	}
	public BluetoothGatt get_gatt(int i){
		if(i<100){
			Log.d("www","app get i="+i+"gatt="+gatt[i]);
		return gatt[i];
		}
		return null;
	}
	/**
	 * ���������ļ�
	 */
	private void loadConfigFile() {
		mProperties = new Properties();
		try {
			InputStream input = BaseApplication.this.getAssets().open("configuration.properties");
			mProperties.load(input);
		} catch (IOException e) {
			e.printStackTrace();
			Log.i(TAG, "���������ļ�����:" + e.toString());
		}
	}
	
	/**
	 * Log����
	 * @return
	 */
	public static boolean isOpenLog() {
		String isOpenLog = mProperties.getProperty("isOpenLog").trim();
		if ("0".equals(isOpenLog)) {
			return true;
		} else {
			return false;
		}
	}

}
