package anti.drop.device.utils;

import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import anti.drop.device.BaseApplication;
import anti.drop.device.R;

/**
 * AUTHER WZB<wangzhibin_x@foxmail.com> 2015-7-28??02:03:38
 */
public class BluetoothLeClass {

	private final String TAG = "wzb";
	private final int PLAY_BELL = 0x000010;

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;
	public boolean isconnectedSuccess = false;
	private BaseApplication mApp;

	private BluetoothGattCharacteristic ff[] = new BluetoothGattCharacteristic[7];

	private static int BLERSSI = 0;

	public interface OnConnectListener {
		public void onConnect(BluetoothGatt gatt,String addr);
	}

	public interface OnDisconnectListener {
		public void onDisconnect(BluetoothGatt gatt,String addr);
	}

	public interface OnServiceDiscoverListener {
		public void onServiceDiscover(BluetoothGatt gatt);
	}

	public interface OnDataAvailableListener {
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status);

		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic);
	}

	public interface camera_data_listener {
		public void camera_data(String value);
	}

	public interface media_data_listener {
		public void media_data(String value);
	}

	private media_data_listener m_media_data_listener;
	private camera_data_listener m_camera_data_listener;
	private OnConnectListener mOnConnectListener;
	private OnDisconnectListener mOnDisconnectListener;
	private OnServiceDiscoverListener mOnServiceDiscoverListener;
	private OnDataAvailableListener mOnDataAvailableListener;
	private Context mContext;

	public void set_media_data_listener(media_data_listener l) {
		m_media_data_listener = l;
	}

	public void set_camera_data_listener(camera_data_listener l) {
		m_camera_data_listener = l;
	}

	public void setOnConnectListener(OnConnectListener l) {
		mOnConnectListener = l;
	}

	public void setOnDisconnectListener(OnDisconnectListener l) {
		mOnDisconnectListener = l;
	}

	public void setOnServiceDiscoverListener(OnServiceDiscoverListener l) {
		mOnServiceDiscoverListener = l;
	}

	public void setOnDataAvailableListener(OnDataAvailableListener l) {
		mOnDataAvailableListener = l;
	}

	public BluetoothLeClass(Context c) {
		mContext = c;
	}

	// Implements callback methods for GATT events that the app cares about. For
	// example,
	// connection change and services discovered.
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String address =
				 SharedPreferencesUtils.getInstanse(mContext).getAddress();
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				if (mOnConnectListener != null)
					mOnConnectListener.onConnect(gatt,address);
				isconnectedSuccess = true;
				Log.i(TAG, "Connected to GATT server.");
				// Attempts to discover services after successful connection.
				Log.i(TAG, "Attempting to start service discovery:"
						+ mBluetoothGatt.discoverServices());
				
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				ring();//??????
				if (mOnDisconnectListener != null)
					mOnDisconnectListener.onDisconnect(gatt,address);
				isconnectedSuccess = false;
				Log.i(TAG, "Disconnected from GATT server.");
				 
				// isconnectedSuccess=false;
				 if(null!=address&&!address.equals("")){
					  if (mBluetoothGatt != null) {
                          mBluetoothGatt.close();
                          mBluetoothGatt.disconnect();
                          mBluetoothGatt = null;
					  }
				 }
			}else{
				ring();//??????
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			displayGattServices(getSupportedGattServices());
			if (status == BluetoothGatt.GATT_SUCCESS
					&& mOnServiceDiscoverListener != null) {
				mOnServiceDiscoverListener.onServiceDiscover(gatt);
				//displayGattServices(getSupportedGattServices());
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
			boolean ret = setCharacteristicNotification(
				getBluetoothGattCharacteristic(0), true);
			Log.d("wzb","ble class read data "+ret);
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (mOnDataAvailableListener != null)
				mOnDataAvailableListener.onCharacteristicRead(gatt,
						characteristic, status);
			Log.d("wzb",
					"read ok characteristic="
							+ characteristic.getUuid().toString()
							+ "value="
							+ CommunicationUtil.bytesToHexString(characteristic
									.getValue()));
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			super.onCharacteristicChanged(gatt, characteristic);
			if (mOnDataAvailableListener != null)
				mOnDataAvailableListener.onCharacteristicWrite(gatt,
						characteristic);
			if (m_camera_data_listener != null) {
				m_camera_data_listener.camera_data(CommunicationUtil
						.bytesToHexString(characteristic.getValue()));
			}
			if (m_media_data_listener != null) {
				m_media_data_listener.media_data(CommunicationUtil
						.bytesToHexString(characteristic.getValue()));
			}
			Log.d("wzb",
					"CharacteristicChanged uuid:"
							+ characteristic.getUuid().toString()
							+ " value="
							+ CommunicationUtil.bytesToHexString(characteristic.getValue()));
			String result = CommunicationUtil.bytesToHexString(characteristic.getValue());
			boolean isEnter = SharedPreferencesUtils.getInstanse(mContext).getIsEnter();
			boolean isEqust = (result.equals("b1")||result.equals("b2"));
			Log.d("wzb","isEnter="+isEnter+"isEqust= "+isEqust);
			if(!isEnter&&isEqust){
				mHandler.sendEmptyMessage(PLAY_BELL);
			}
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			super.onReadRemoteRssi(gatt, rssi, status);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				// ???RSSI, RSSI ????? ? ?? ??,? -33 ; ?????????,?????????
				// ??mBluetoothGatt.readRemoteRssi();???
				Log.d("wzb", "aaa rssi=" + rssi);
				// ????RSSI???
				BLERSSI = rssi;
			}

		};

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.d("wzb",
						"write ok uuid="
								+ characteristic.getUuid().toString()
								+ " value="
								+ CommunicationUtil
										.bytesToHexString(characteristic
												.getValue()));
			}

		};

	};

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PLAY_BELL:
				ring();
				break;
			default:
				break;
			}
		};
	};

	MediaPlayer player;
	private void ring() {
		if(player!=null&&player.isPlaying()){
			player.stop();
		}
		String name = SharedPreferencesUtils.getInstanse(mContext).getMusicName();
		if(name.equals("??1")){
			player = MediaPlayer.create(mContext, R.raw.bell_1);
		}else if(name.equals("??2")){
			player = MediaPlayer.create(mContext, R.raw.bell_2);
		}else if(name.equals("??3")){
			player = MediaPlayer.create(mContext, R.raw.bell_3);
		}else{
			player = MediaPlayer.create(mContext, R.raw.bell_1);
		}
		player.start();
	}
	
	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter
		// through
		// BluetoothManager.
		Log.d("wzb", "mble init");
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) mContext
					.getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 * 
	 * @param address
	 *            The device address of the destination device.
	 * 
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			Log.w(TAG,
					"BluetoothAdapter not initialized or unspecified address.");
			isconnectedSuccess = false;
		}

		// Previously connected device. Try to reconnect.
		if (mBluetoothDeviceAddress != null
				&& address.equals(mBluetoothDeviceAddress)
				&& mBluetoothGatt != null) {
			Log.d(TAG,
					"Trying to use an existing mBluetoothGatt for connection.");
			if (mBluetoothGatt.connect()) {
				isconnectedSuccess = true;
			} else {
				isconnectedSuccess = false;
			}
		}

		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			isconnectedSuccess = false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
		mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
		
		//add by wzb 20150823
		
		
		Log.d(TAG, "Trying to create a new connection.");
		mBluetoothDeviceAddress = address;
		return isconnectedSuccess;
	}
//	
//	public boolean connect(final String address ,int num) {
//		if (mBluetoothAdapter == null || address == null) {
//			Log.w(TAG,
//					"BluetoothAdapter not initialized or unspecified address.");
//			isconnectedSuccess = false;
//		}
//
//		// Previously connected device. Try to reconnect.
//		if (mBluetoothDeviceAddress != null
//				&& address.equals(mBluetoothDeviceAddress)
//				&& mBluetoothGatt != null) {
//			Log.d(TAG,
//					"Trying to use an existing mBluetoothGatt for connection.");
//			if (mBluetoothGatt.connect()) {
//				isconnectedSuccess = true;
//			} else {
//				isconnectedSuccess = false;
//			}
//		}
//
//		final BluetoothDevice device = mBluetoothAdapter
//				.getRemoteDevice(address);
//		if (device == null) {
//			Log.w(TAG, "Device not found.  Unable to connect.");
//			isconnectedSuccess = false;
//		}
//		// We want to directly connect to the device, so we are setting the
//		// autoConnect
//		// parameter to false.
//		mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
//		
//		//add by wzb 20150823
//		
//		Log.d(TAG, "Trying to create a new connection.");
//		mBluetoothDeviceAddress = address;
//		return isconnectedSuccess;
//	}
	
	

	public BluetoothGatt getBluetoothGatt() {
		return mBluetoothGatt;
	}
	
	public void setBluetoothGatt(BluetoothGatt l) {
		mBluetoothGatt=l;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 * 
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	/**
	 * Enables or disables notification on a give characteristic.
	 * 
	 * @param characteristic
	 *            Characteristic to act on.
	 * @param enabled
	 *            If true, enable notification. False otherwise.
	 */
	public boolean setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return false;
		}
		
		if (!mBluetoothGatt.setCharacteristicNotification(characteristic,
				enabled)) {
			return false;
		}
		BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
		if (clientConfig == null) {
			
			return false;
		}
		if (enabled) {
			clientConfig
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		} else {
			clientConfig
					.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
		}
		return mBluetoothGatt.writeDescriptor(clientConfig);
	}

	public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
		if(mBluetoothGatt!=null){
			mBluetoothGatt.writeCharacteristic(characteristic);
		}
	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 * 
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null)
			return null;

		return mBluetoothGatt.getServices();
	}

	// ???????RSSI?
	public static int getBLERSSI() {
		return BLERSSI;
	}

	// ????????????RSSI?
	// ???????,??????onReadRemoteRssi()??
	/**
	 * Read the RSSI for a connected remote device.
	 * */
	public boolean getRssiVal() {
		if (mBluetoothGatt == null)
			return false;
		return mBluetoothGatt.readRemoteRssi();

	}

	public BluetoothGattCharacteristic getBluetoothGattCharacteristic(int id) {
		return ff[id];

	}

	public void setBluetoothGattCharacteristic(BluetoothGattCharacteristic b,
			int id) {
		ff[id] = b;

	}
	
	private void displayGattServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null)
			return;

		for (BluetoothGattService gattService : gattServices) {
			// -----Service?????-----//
			int type = gattService.getType();
			Log.e("wzb",
					"-->service type:" + CommunicationUtil.getServiceType(type));
			Log.e("wzb", "-->includedServices size:"
					+ gattService.getIncludedServices().size());
			Log.e("wzb", "-->service uuid:" + gattService.getUuid());

			// -----Characteristics?????-----//
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();
			for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				Log.e("wzb", "---->char uuid:"
						+ gattCharacteristic.getUuid().toString());

				int permission = gattCharacteristic.getPermissions();
				Log.e("wzb",
						"---->char permission:"
								+ CommunicationUtil
										.getCharPermission(permission));

				int property = gattCharacteristic.getProperties();
				Log.e("wzb",
						"---->char property:"
								+ CommunicationUtil.getCharPropertie(property));

				byte[] data = gattCharacteristic.getValue();
				if (data != null && data.length > 0) {
					Log.e("wzb", "---->char value:" + new String(data));
				}

				// ff1 --ff7 ?? ff[0]-ff[6]
				if (gattCharacteristic.getUuid().toString()
						.equals("0000fff2-0000-1000-8000-00805f9b34fb")) {
					setBluetoothGattCharacteristic(gattCharacteristic, 1);
				} else if (gattCharacteristic.getUuid().toString()
						.equals("0000fff1-0000-1000-8000-00805f9b34fb")) {
					setBluetoothGattCharacteristic(gattCharacteristic, 0);
				} else if (gattCharacteristic.getUuid().toString()
						.equals("0000fff3-0000-1000-8000-00805f9b34fb")) {
					setBluetoothGattCharacteristic(gattCharacteristic, 2);
				} else if (gattCharacteristic.getUuid().toString()
						.equals("0000fff4-0000-1000-8000-00805f9b34fb")) {
					setBluetoothGattCharacteristic(gattCharacteristic, 3);
				} else if (gattCharacteristic.getUuid().toString()
						.equals("0000fff5-0000-1000-8000-00805f9b34fb")) {
					setBluetoothGattCharacteristic(gattCharacteristic, 4);
				} else if (gattCharacteristic.getUuid().toString()
						.equals("0000fff6-0000-1000-8000-00805f9b34fb")) {
					setBluetoothGattCharacteristic(gattCharacteristic, 5);
				} else if (gattCharacteristic.getUuid().toString()
						.equals("0000fff7-0000-1000-8000-00805f9b34fb")) {
					setBluetoothGattCharacteristic(gattCharacteristic, 6);
				}

				// -----Descriptors?????-----//
				List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic
						.getDescriptors();
				for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) {
					Log.e("wzb",
							"-------->desc uuid:" + gattDescriptor.getUuid());
					int descPermission = gattDescriptor.getPermissions();
					Log.e("wzb",
							"-------->desc permission:"
									+ CommunicationUtil
											.getDescPermission(descPermission));

					byte[] desData = gattDescriptor.getValue();
					if (desData != null && desData.length > 0) {
						Log.e("wzb", "-------->desc value:"
								+ new String(desData));
					}
				}
			}
		}
	}

}
