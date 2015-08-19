package com.tvtelecontroller.utils;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBHelper extends SQLiteOpenHelper {
	
	private final String TAG = DBHelper.class.getSimpleName();

	private static final String DB_NAME = "tv";
	private static final int DB_VERSION = 1;
	private SQLiteDatabase db;
	private static DBHelper instance;

	private static final String TABLE_DEVICE = "device";
	private static final String CREATE_TABLE_DEVICE = "create table "
			+ TABLE_DEVICE
			+ " (_id integer primary key autoincrement,name text,connIp text,"
			+ "isConn text)";

	public DBHelper(Context mContext) {
		super(mContext, DB_NAME, null, DB_VERSION);
	}

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public static DBHelper getInstance(Context mContext) {
		if (instance == null) {
			instance = new DBHelper(mContext);
		}
		return instance;
	}

	// ��ʼ�����ݿ⣬�򿪶����ݿ������
	public void open() {
		if (db == null) {
			db = getWritableDatabase();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		operateTable(db, CREATE_TABLE_DEVICE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == newVersion) {
			return;
		}
		operateTable(db, "DROP TABLE IF EXISTS " + TABLE_DEVICE);
		onCreate(db);
	}

	private void operateTable(SQLiteDatabase db, String actionString) {
		db.execSQL(actionString);
	}
	
	//���һ�����м�¼
	public void insertDevice(DeviceBean device){
		ContentValues param = new ContentValues();
		param.put("name", device.name);
		param.put("connIp", device.connIp);
		param.put("isConn", device.isConn);
		db.insert(TABLE_DEVICE, null, param);
	}
	
	//ɾ��һ�����м�¼
	public void deleteDevice(DeviceBean device){
		db.delete(TABLE_DEVICE, "connIp=?", new String[]{device.connIp});
	}
	
	//��ѯ���еĳ�������
	public ArrayList<DeviceBean> query(){
		ArrayList<DeviceBean> data = new ArrayList<DeviceBean>();
		Cursor cur = null;
		try {
			cur = db.query(TABLE_DEVICE, null, null, null, null, null, null);
			cur.moveToFirst();
			for (int i = 0; i < cur.getCount(); i++){
				DeviceBean device = new DeviceBean();
				device.name = cur.getString(1);
				device.connIp = cur.getString(2);
				device.isConn = cur.getString(3);
				data.add(device);
				cur.moveToNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if (cur != null) {
					cur.close();
				}
			} catch (Exception e) {
				Log4L.e(TAG, e.toString());
			}
		}
		return data;
	}
	
	/**
	 * ɾ�����ű�����
	 */
	public void deleteAll(){
		ArrayList<DeviceBean> data = new ArrayList<DeviceBean>();
		Cursor cur = null;
		try {
			cur = db.query(TABLE_DEVICE, null, null, null, null, null, null);
			cur.moveToFirst();
			
			for (int i = 0; i < cur.getCount(); i++){
				DeviceBean device = new DeviceBean();
				device.name = cur.getString(1);
				device.connIp = cur.getString(2);
				device.isConn = cur.getString(3);
				deleteDevice(device);
				cur.moveToNext();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if (cur != null) {
					cur.close();
				}
			} catch (Exception e) {
				Log4L.e(TAG, e.toString());
			}
		}
	}

}
