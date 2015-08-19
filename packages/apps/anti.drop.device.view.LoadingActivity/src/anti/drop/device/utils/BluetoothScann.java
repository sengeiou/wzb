package anti.drop.device.utils;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import anti.drop.device.pojo.DeviceBean;

public class BluetoothScann {

	public static List<DeviceBean> bluetoothInfo = new ArrayList<DeviceBean>();//���б��������汾�ν���Ӧ�õĽ��
	private DBHelper mDBHelper;
	private static BluetoothScann instance;
	private Context mContext;
	String bluetoothAddress = "";//������ַ
	private Handler mHandler = null;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothManager bluetoothManager;
	
	public BluetoothScann(Context context){
		mContext = context;
		mDBHelper = DBHelper.getInstance(mContext);
		mHandler = new Handler();
		bluetoothManager = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
	}
	
	public static BluetoothScann getInstance(Context context){
		if(instance==null){
			instance = new BluetoothScann(context);
		}
		return instance;
	}
	
	//����ɨ��
	public void startScan(){
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				mBluetoothAdapter.startLeScan(mLeScanCallback);
			}
		});
	}
	
	//ֹͣɨ��
	public void stopScan(){
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			}
		});
	}
	
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		public void onLeScan(android.bluetooth.BluetoothDevice device, int rssi, byte[] scanRecord) {
			
			bluetoothAddress = device.getAddress();
			if(null!=bluetoothInfo&&bluetoothInfo.size()>0){
				for(int i=0;i<bluetoothInfo.size();i++){
					//ȥ�ش���
					if(!bluetoothAddress.equals(bluetoothInfo.get(i).getAddress())){
						//��ӵ��б�
						bluetoothInfo.add(new DeviceBean(
								device.getAddress(),
								device.getName(),
								device.getBondState(),
								0));
						//���뵽���ݿ�
						mDBHelper.insertDevice(new DeviceBean(
								device.getAddress(),
								device.getName(),
								device.getBondState()
								,0));
					}
				}
			}
		};
	};

}
