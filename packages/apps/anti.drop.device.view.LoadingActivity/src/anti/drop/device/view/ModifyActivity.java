package anti.drop.device.view;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import anti.drop.device.BaseActivity;
import anti.drop.device.R;
import anti.drop.device.adapter.DeviceAdapter;
import anti.drop.device.pojo.DeviceBean;
import anti.drop.device.utils.DBHelper;
import anti.drop.device.utils.SharedPreferencesUtils;

public class ModifyActivity extends BaseActivity{
	
	private ImageView backView;
	private TextView titleView;
	private ListView deviceList;//���������豸
	
	private DeviceAdapter mAdapter;
	private List<String> listData;
	private DBHelper mDBHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_device);
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void findViewById(){
		
		backView = (ImageView)findViewById(R.id.title_back);
		titleView = (TextView)findViewById(R.id.title_text);
		deviceList = (ListView)findViewById(R.id.choose_device_listview);
		
	}
	
	private void setListener(){
		
		backView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		deviceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
//				SharedPreferencesUtils.getInstanse(ModifyActivity.this)
//				.setDeviceName(listData.get(arg2));
				SharedPreferencesUtils.getInstanse(ModifyActivity.this)
				.setDeviceNamefromAddr(SharedPreferencesUtils.getInstanse(ModifyActivity.this).getAddress(),listData.get(arg2));
				//ͬ�������豸�������豸�󶨲���¼������
				DeviceBean device = new DeviceBean();
				device.address = SharedPreferencesUtils.getInstanse(ModifyActivity.this).getAddress();
				device.name = listData.get(arg2);
				device.status = 0x00000c;
				mDBHelper.alter(device, 0x00000c);
				Log.d("wzb","222222rrr"+listData.get(arg2));
				SharedPreferencesUtils.getInstanse(ModifyActivity.this).set_modify_name(listData.get(arg2));
				finish();
			}
		});
		
	}
	
	private void initView(){
		findViewById();
		titleView.setText("����");
		mDBHelper = new DBHelper(this);
		mDBHelper.open();
		listData = new ArrayList<String>();
		listData.add("Կ��");
		listData.add("Ǯ��");
		listData.add("���İ�");
		listData.add("IPAD");
		listData.add("�ʼǱ�����");
		listData.add("����");
		listData.add("������1");
		listData.add("������2");
		listData.add("������3");
		listData.add("������4");
		
		mAdapter = new DeviceAdapter(this,listData);
		deviceList.setAdapter(mAdapter);
		mDBHelper = DBHelper.getInstance(this);
		mDBHelper.open();
		setListener();
	}
	
}
