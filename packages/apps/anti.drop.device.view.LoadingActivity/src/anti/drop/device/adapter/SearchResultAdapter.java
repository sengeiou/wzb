package anti.drop.device.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import anti.drop.device.BaseApplication;
import anti.drop.device.R;
import anti.drop.device.pojo.DeviceBean;
import anti.drop.device.utils.BluetoothLeClass;
import anti.drop.device.utils.DBHelper;

@SuppressLint({ "ViewHolder", "InflateParams" })
public class SearchResultAdapter extends BaseAdapter {

	private Activity mContext;
	private List<DeviceBean> listData;
	private List<DeviceBean> mDBData;
	private BluetoothLeClass mBLE;
	private DBHelper mDB;
	private BaseApplication mApp;

	public SearchResultAdapter(Activity context, List<DeviceBean> list) {
		this.mContext = context;
		this.listData = list;
		mApp = (BaseApplication)mContext.getApplication();
		mBLE = mApp.get_ble();
		if (!mBLE.initialize()) {
			mContext.finish();
		}
		mDB = DBHelper.getInstance(mContext);
		mDB.open();
		mDBData = mDB.query();
	}

	public void setListData(List<DeviceBean> listData) {
		this.listData = listData;
	}

	@Override
	public int getCount() {
		if (null != listData && listData.size() > 0) {
			return listData.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		convertView = LayoutInflater.from(mContext).inflate(R.layout.search_result_item, null);
		TextView name = (TextView) convertView
				.findViewById(R.id.search_result_item_name);
		TextView address = (TextView) convertView
				.findViewById(R.id.search_result_item_ip);
		final Button connect = (Button) convertView
				.findViewById(R.id.search_result_connected);

		name.setText(listData.get(position).getName());
		address.setText(listData.get(position).getAddress());
		int isconn = listData.get(position).getStatus();

		if (isconn == 0x0000000c) {
			connect.setBackgroundResource(R.drawable.connected_btn);
		} else {
			connect.setBackgroundResource(R.drawable.notconnect_btn);
		}

		connect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean issuccess = mBLE.connect(listData.get(position).getAddress());
				if (issuccess) {
					connect.setBackgroundResource(R.drawable.connected_btn);
					DeviceBean bean = new DeviceBean();
					bean.name = listData.get(position).getName();
					bean.address = listData.get(position).getAddress();
					bean.status = BluetoothDevice.BOND_BONDED;
					bean.bell = "����1";
					if(mDBData.contains(listData.get(position))){
						//�У�����״̬�����ˡ�
						mDB.alter(bean, BluetoothDevice.BOND_BONDED);
					}else{
						//û�У��������ݲ������ݿ�
						mDB.insertDevice(bean);
					}
					mContext.finish();
				}
			}
		});
		return convertView;
	}
}
