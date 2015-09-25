package com.lge.alljoyn.simulator.database;

import java.util.ArrayList;

import com.lge.alljoyn.simulator.about.DeviceAboutObject;
import com.lge.alljoyn.simulator.about.InterfaceObject;
import com.lge.alljoyn.simulator.about.InterfaceRangeObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AJDeviceDBAdapter {
	private static final int DATABASE_VERSION = 1;
	private final String LOG_TAG = "AJDeviceDBAdapter";

	private final String DATABASE_NAME = "AJDatabase.sqlite";
	private SQLiteDatabase mDB = null;
	private AJDeviceDBHelper mDBHelper;
	private final Context mContext;

	private class AJDeviceDBHelper extends SQLiteOpenHelper {

		public AJDeviceDBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// Log.e("bskik", "AJDeviceDBHelper");
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(AJDeviceDatabase.DB4AJDevice.CREATE_TB_DEVICE);
			db.execSQL(AJDeviceDatabase.DB4AJDevice.CREATE_TB_REGISTED_DEVICE);
			db.execSQL(AJDeviceDatabase.DB4AJDevice.CREATE_TB_INTERFACE);
			db.execSQL(AJDeviceDatabase.DB4AJDevice.TABLENAME_INTERFACE_RANGE);
			// Log.e(LOG_TAG, "DB CREATE");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Log.e(LOG_TAG, "DB onUpgrade");
			// if (oldVersion != newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + AJDeviceDatabase.DB4AJDevice.TABLENAME_DEVICE);
			db.execSQL("DROP TABLE IF EXISTS " + AJDeviceDatabase.DB4AJDevice.TABLENAME_REGISTED_DEVICE);
			db.execSQL("DROP TABLE IF EXISTS " + AJDeviceDatabase.DB4AJDevice.CREATE_TB_INTERFACE);
			db.execSQL("DROP TABLE IF EXISTS " + AJDeviceDatabase.DB4AJDevice.TABLENAME_INTERFACE_RANGE);
			onCreate(db);
			// }

		}

	}

	public AJDeviceDBAdapter(Context ctx) {
		this.mContext = ctx;
	}

	public AJDeviceDBAdapter open() throws SQLException {
		if (mDBHelper == null) {
			mDBHelper = new AJDeviceDBHelper(mContext);
		}

		if (mDB == null && mDBHelper != null) {
			try {
				mDB = mDBHelper.getWritableDatabase();
			} catch (SQLiteDatabaseLockedException sdle) {
				sdle.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this;
	}

	public void close() {
		if (mDB.isOpen()) {
			mDB.close();
			mDB = null;
		}
		if (mDBHelper != null) {
			mDBHelper.close();
			mDBHelper = null;
		}
	}

	/****************************************** DEVICE *******************************************/

	public long insertDevice(DeviceAboutObject data) {
		long returnValue = 0;
		mDB.beginTransaction();

		ContentValues values = new ContentValues();
		values.put(AJDeviceDatabase.DB4AJDevice.DEVICE_MODEL_NUMBER, data.getDeviceId());
		values.put(AJDeviceDatabase.DB4AJDevice.DEVICE_NAME, data.getDeviceName());
		values.put(AJDeviceDatabase.DB4AJDevice.DEVICE_USE_FLAG, data.getUseFlag());
		
		try {
			synchronized (mDB) {
				returnValue = mDB.insert(AJDeviceDatabase.DB4AJDevice.TABLENAME_DEVICE, null, values);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
			returnValue = 0;
		}
		mDB.setTransactionSuccessful();
		mDB.endTransaction();
		return returnValue;
	}
	
	public long updateDevice(DeviceAboutObject data) {
		long returnValue = 0;
		mDB.beginTransaction();

		ContentValues values = new ContentValues();
		values.put(AJDeviceDatabase.DB4AJDevice.DEVICE_NAME, data.getDeviceName());
		values.put(AJDeviceDatabase.DB4AJDevice.DEVICE_USE_FLAG, data.getUseFlag());

		try {
			synchronized (mDB) {
				returnValue = mDB.update(AJDeviceDatabase.DB4AJDevice.TABLENAME_DEVICE, values,
						AJDeviceDatabase.DB4AJDevice.DEVICE_ID + "=" + data.get_id(), null);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
			returnValue = 0;
		}
		
		mDB.setTransactionSuccessful();
		mDB.endTransaction();
		return returnValue;
	}
	
	public long deleteDevice(int device_id) {
		long returnValue = 0;
		mDB.beginTransaction();

		try {
			synchronized (mDB) {
				returnValue = mDB.delete(AJDeviceDatabase.DB4AJDevice.TABLENAME_DEVICE,
						AJDeviceDatabase.DB4AJDevice.DEVICE_ID + "=" + device_id, null);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
			returnValue = 0;
		}
		mDB.setTransactionSuccessful();
		mDB.endTransaction();
		return returnValue;
	}
	
	public int getLastDeviceId() {
		int returnValue = 0;
		Cursor cursor = null;
		try {
			//select max(a.row_time_seq) from tb_sche_row
			synchronized (mDB) {
				cursor = mDB.query("sqlite_sequence", null,
						"name = 'tb_device'", null, null, null, null);
			}
			// Log.e(LOG_TAG, "cursor.getCount()=" + cursor.getCount());
			if (cursor != null && cursor.getCount() == 1) {
				if (cursor.moveToFirst()) {
					do {
						returnValue = cursor.getInt(cursor.getColumnIndex("seq"));

					} while (cursor.moveToNext());
				}
			} else {
				Log.e(LOG_TAG, "cursor.getCount() != 1");
			}

		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		if (cursor != null) {
			cursor.close();
		}
		return returnValue;
	}
	
	public int getLastInterfaceId() {
		int returnValue = 0;
		Cursor cursor = null;
		try {
			//select max(a.row_time_seq) from tb_sche_row
			synchronized (mDB) {
				cursor = mDB.query("sqlite_sequence", null,
						"name = 'tb_interface'", null, null, null, null);
			}
			// Log.e(LOG_TAG, "cursor.getCount()=" + cursor.getCount());
			if (cursor != null && cursor.getCount() == 1) {
				if (cursor.moveToFirst()) {
					do {
						returnValue = cursor.getInt(cursor.getColumnIndex("seq"));

					} while (cursor.moveToNext());
				}
			} else {
				Log.e(LOG_TAG, "cursor.getCount() != 1");
			}

		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		if (cursor != null) {
			cursor.close();
		}
		return returnValue;
	}

	public ArrayList<DeviceAboutObject> getDeviceList(int index) {
		ArrayList<DeviceAboutObject> returnList = null;
		Cursor cursor = null;
		try {
			synchronized (mDB) {
				cursor = mDB.query(AJDeviceDatabase.DB4AJDevice.TABLENAME_DEVICE, null, AJDeviceDatabase.DB4AJDevice.DEVICE_ID + " > " + index + " and " + AJDeviceDatabase.DB4AJDevice.DEVICE_USE_FLAG + "= 1" , null, null, null, null);
			}

			// Log.e(LOG_TAG, "count=" + cursor.getCount());
			if (cursor != null && cursor.getCount() > 0) {
				returnList = new ArrayList<DeviceAboutObject>();
				if (cursor.moveToFirst()) { 
					do {
						DeviceAboutObject tempData = new DeviceAboutObject();
						tempData.set_id(cursor.getInt(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.DEVICE_ID)));
						tempData.setDeviceId(cursor
								.getString(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.DEVICE_MODEL_NUMBER)));
						tempData.setDeviceName(
								cursor.getString(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.DEVICE_NAME)));
						returnList.add(tempData);
					} while (cursor.moveToNext());
				}
			}

		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		if (cursor != null) {
			cursor.close();
		}

		return returnList;

	}

	public DeviceAboutObject getDeeviceInfo(int device_id) {

		Cursor cursor = null;
		DeviceAboutObject returnData = null;
		try {
			synchronized (mDB) {
				cursor = mDB.query(AJDeviceDatabase.DB4AJDevice.TABLENAME_DEVICE, null,
						AJDeviceDatabase.DB4AJDevice._ID + "=" + device_id + "", null, null, null, null);
			}
			// Log.e(LOG_TAG, "cursor.getCount()=" + cursor.getCount());
			if (cursor != null && cursor.getCount() == 1) {
				returnData = new DeviceAboutObject();
				if (cursor.moveToFirst()) {
					do {
						returnData.set_id(cursor.getInt(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.DEVICE_ID)));
						returnData.setDeviceId(cursor
								.getString(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.DEVICE_MODEL_NUMBER)));
						returnData.setDeviceName(
								cursor.getString(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.DEVICE_NAME)));

					} while (cursor.moveToNext());
				}
			} else {
				Log.e(LOG_TAG, "cursor.getCount() != 1");
			}

		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		if (cursor != null) {
			cursor.close();
		}

		return returnData;
	}

	public ArrayList<InterfaceObject> getInterfaceList(int if_device_id, String if_type) {
		ArrayList<InterfaceObject> returnList = null;
		Cursor cursor = null;
		String sqlStr = "= " + if_device_id + " ";
		if (if_type != null) {
			sqlStr = sqlStr + " and " + AJDeviceDatabase.DB4AJDevice.INTERFACE_TYPE + " = '" + if_type + "'";
		}
		try {
			synchronized (mDB) {
				cursor = mDB.query(AJDeviceDatabase.DB4AJDevice.TABLENAME_INTERFACE, null,
						AJDeviceDatabase.DB4AJDevice.INTERFACE_DEVICE_ID + sqlStr, null, null, null, null);
			}

			// Log.e(LOG_TAG, "count=" + cursor.getCount());
			if (cursor != null && cursor.getCount() > 0) {
				returnList = new ArrayList<InterfaceObject>();
				if (cursor.moveToFirst()) {
					do {
						InterfaceObject tempData = new InterfaceObject();
						tempData.set_id(
								cursor.getInt(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_ID)));
						tempData.setIf_name(
								cursor.getString(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_NAME)));
						tempData.setIf_path(
								cursor.getString(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_PATH)));
						tempData.setIf_type(
								cursor.getString(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_TYPE)));
						tempData.setIf_signal(
								cursor.getString(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_SIGNAL)));
						tempData.setIf_device_id(
								cursor.getInt(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_DEVICE_ID)));
						tempData.setIf_default_value(cursor.getString(
								cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_DEFAULT_VALUE)));
						tempData.setIf_min_value(cursor
								.getString(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_MIN_VALUE)));
						tempData.setIf_max_value(cursor
								.getString(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_MAX_VALUE)));
						tempData.setIf_has_index(
								cursor.getInt(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_HAS_INDEX)));
						tempData.setIf_description(cursor
								.getString(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_DESCRIPTION)));
						tempData.setIf_unit(
								cursor.getString(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_UNIT)));

						
						tempData.setIf_dialog_action1(cursor.getString(
								cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_DIALOG_ACTION_1)));
						tempData.setIf_dialog_action2(cursor.getString(
								cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_DIALOG_ACTION_2)));
						tempData.setIf_dialog_action3(cursor.getString(
								cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_DIALOG_ACTION_3)));
						tempData.setIf_noti_flag(
								cursor.getInt(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_NOTI_FLAG)));
						tempData.setIf_ui_type(
								cursor.getInt(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_UI_TYPE)));
						tempData.setIf_writable(
								cursor.getInt(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_WRITABLE)));
						tempData.setIf_secured(
								cursor.getInt(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_SECURED)));

						tempData.setIf_dialog_button1(cursor.getString(
								cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_DIALOG_BUTTON1)));
						tempData.setIf_dialog_button2(cursor.getString(
								cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_DIALOG_BUTTON2)));
						tempData.setIf_dialog_button3(cursor.getString(
								cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_DIALOG_BUTTON3)));
						tempData.setIf_dialog_msg(cursor
								.getString(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.INTERFACE_DIALOG_MSG)));

						if (tempData.getIf_has_index() == 1) {
							ArrayList<InterfaceRangeObject> objList = getRange(tempData.get_id());
							tempData.setInterface_range(objList);

						}

						returnList.add(tempData);

					} while (cursor.moveToNext());
				}
			}

		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		if (cursor != null) {
			cursor.close();
		}

		return returnList;

	}
	
	public long insertInterface(InterfaceObject data) {
		long returnValue = 0;
		mDB.beginTransaction();

		ContentValues values = new ContentValues();
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_NAME, data.getIf_name());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_PATH, data.getIf_path());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_TYPE, data.getIf_type());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_SIGNAL, data.getIf_signal());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_DEVICE_ID, data.getIf_device_id());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_DEFAULT_VALUE, data.getIf_default_value());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_MIN_VALUE, data.getIf_min_value());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_MAX_VALUE, data.getIf_max_value());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_HAS_INDEX, data.getIf_has_index());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_DESCRIPTION, data.getIf_description());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_UNIT, data.getIf_unit());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_DIALOG_ACTION_1, data.getIf_dialog_action1());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_DIALOG_ACTION_2, data.getIf_dialog_action2());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_DIALOG_ACTION_3, data.getIf_dialog_action3());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_NOTI_FLAG, data.getIf_noti_flag());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_UI_TYPE, data.getIf_ui_type());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_WRITABLE, data.getIf_writable());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_SECURED, data.getIf_secured());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_DIALOG_BUTTON1, data.getIf_dialog_button1());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_DIALOG_BUTTON2, data.getIf_dialog_button2());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_DIALOG_BUTTON3, data.getIf_dialog_button3());
		values.put(AJDeviceDatabase.DB4AJDevice.INTERFACE_DIALOG_MSG, data.getIf_dialog_msg());
		
		try {
			synchronized (mDB) {
				returnValue = mDB.insert(AJDeviceDatabase.DB4AJDevice.TABLENAME_INTERFACE, null, values);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
			returnValue = 0;
		}
		mDB.setTransactionSuccessful();
		mDB.endTransaction();
		return returnValue;
	}

	public int getInterfaceCount(String if_type) {
		Cursor cursor = null;
		int count = -1;
		try {
			synchronized (mDB) {

				cursor = mDB.rawQuery("select count(*) from " + AJDeviceDatabase.DB4AJDevice.TABLENAME_INTERFACE
						+ " where " + AJDeviceDatabase.DB4AJDevice.INTERFACE_TYPE + "='" + if_type + "'", null);
			}
			if (cursor != null && cursor.getCount() > 0) {

				if (cursor.moveToFirst()) {
					do {

						count = cursor.getInt(0);

					} while (cursor.moveToNext());
				}
			}

		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		if (cursor != null) {
			cursor.close();
		}

		return count;

	}
	
	public long insertInterfaceRange(InterfaceRangeObject data) {
		long returnValue = 0;
		mDB.beginTransaction();

		ContentValues values = new ContentValues();
		values.put(AJDeviceDatabase.DB4AJDevice.RANGE_INTERFACE_ID, data.getIf_id());
		values.put(AJDeviceDatabase.DB4AJDevice.RANGE_INDEX, data.getRange_index());
		values.put(AJDeviceDatabase.DB4AJDevice.RANGE_LABEL, data.getRange_label());
		
		
		try {
			synchronized (mDB) {
				returnValue = mDB.insert(AJDeviceDatabase.DB4AJDevice.TABLENAME_INTERFACE_RANGE, null, values);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
			returnValue = 0;
		}
		mDB.setTransactionSuccessful();
		mDB.endTransaction();
		return returnValue;
	}

	public ArrayList<InterfaceRangeObject> getRange(int if_id) {
		ArrayList<InterfaceRangeObject> returnList = null;
		Cursor cursor = null;

		try {
			synchronized (mDB) {
				cursor = mDB.query(AJDeviceDatabase.DB4AJDevice.TABLENAME_INTERFACE_RANGE, null,
						AJDeviceDatabase.DB4AJDevice.RANGE_INTERFACE_ID + " = " + if_id, null, null, null,
						AJDeviceDatabase.DB4AJDevice.RANGE_ID + " asc");
			}

			// Log.e(LOG_TAG, "count=" + cursor.getCount());
			if (cursor != null && cursor.getCount() > 0) {
				returnList = new ArrayList<InterfaceRangeObject>();
				if (cursor.moveToFirst()) {
					do {
						InterfaceRangeObject tempData = new InterfaceRangeObject();
						tempData.set_id(cursor.getInt(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.RANGE_ID)));
						tempData.setRange_index(
								cursor.getString(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.RANGE_INDEX)));
						tempData.setRange_label(
								cursor.getString(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.RANGE_LABEL)));

						returnList.add(tempData);

					} while (cursor.moveToNext());
				}
			}

		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		if (cursor != null) {
			cursor.close();
		}

		return returnList;
	}

	public ArrayList<DeviceAboutObject> getRegistedDeviceList() {
		ArrayList<DeviceAboutObject> returnList = null;
		Cursor cursor = null;

		try {
			synchronized (mDB) {
				cursor = mDB.query(AJDeviceDatabase.DB4AJDevice.TABLENAME_REGISTED_DEVICE, null, null, null, null, null,
						null);
			}

			// Log.e(LOG_TAG, "count=" + cursor.getCount());
			if (cursor != null && cursor.getCount() > 0) {
				returnList = new ArrayList<DeviceAboutObject>();
				if (cursor.moveToFirst()) {
					do {
						DeviceAboutObject tempData = new DeviceAboutObject();
						tempData.set_id(cursor.getInt(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.REGISTED_ID)));
						tempData.setDeviceType(
								cursor.getInt(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.REGISTED_DEVICE_ID)));
						tempData.setDeviceName(
								cursor.getString(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.REGISTED_NAME)));

						returnList.add(tempData);

					} while (cursor.moveToNext());
				}
			}

		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		if (cursor != null) {
			cursor.close();
		}

		return returnList;
	}

	public DeviceAboutObject getRegistedDevice(int registed_id) {
		DeviceAboutObject returnObject = null;
		Cursor cursor = null;

		try {
			synchronized (mDB) {
				cursor = mDB.rawQuery("SELECT registed._id as " + AJDeviceDatabase.DB4AJDevice.REGISTED_ID
						+ ", registed.name as " + AJDeviceDatabase.DB4AJDevice.REGISTED_NAME
						+ ", registed.device_id as " + AJDeviceDatabase.DB4AJDevice.REGISTED_DEVICE_ID
						+ ", (select device.ModelNumber from tb_device device where device._id = registed.device_id ) as "
						+ AJDeviceDatabase.DB4AJDevice.DEVICE_MODEL_NUMBER + " FROM "
						+ AJDeviceDatabase.DB4AJDevice.TABLENAME_REGISTED_DEVICE + " registed  WHERE registed._id="
						+ registed_id, null);

			}

			// Log.e(LOG_TAG, "getRegistedDevice count=" + cursor.getCount());
			if (cursor != null && cursor.getCount() == 1) {
				returnObject = new DeviceAboutObject();
				if (cursor.moveToFirst()) {
					do {

						returnObject
								.set_id(cursor.getInt(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.REGISTED_ID)));
						returnObject.setDeviceType(
								cursor.getInt(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.REGISTED_DEVICE_ID)));
						returnObject.setDeviceName(
								cursor.getString(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.REGISTED_NAME)));
						returnObject.setDeviceId(cursor
								.getString(cursor.getColumnIndex(AJDeviceDatabase.DB4AJDevice.DEVICE_MODEL_NUMBER)));

					} while (cursor.moveToNext());
				}
			}

		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		if (cursor != null) {
			cursor.close();
		}

		return returnObject;

	}

	public long insertRegistedDevice(DeviceAboutObject obj) {
		long returnValue = 0;
		mDB.beginTransaction();

		ContentValues values = new ContentValues();
		values.put(AJDeviceDatabase.DB4AJDevice.REGISTED_DEVICE_ID, obj.getDeviceType());
		values.put(AJDeviceDatabase.DB4AJDevice.REGISTED_NAME, obj.getDeviceName());

		try {
			synchronized (mDB) {
				returnValue = mDB.insert(AJDeviceDatabase.DB4AJDevice.TABLENAME_REGISTED_DEVICE, null, values);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
			returnValue = 0;
		}
		mDB.setTransactionSuccessful();
		mDB.endTransaction();
		return returnValue;
	}

	public long deleteRegistedDevice(int registed_id) {
		long returnValue = 0;
		mDB.beginTransaction();

		try {
			synchronized (mDB) {
				returnValue = mDB.delete(AJDeviceDatabase.DB4AJDevice.TABLENAME_REGISTED_DEVICE,
						AJDeviceDatabase.DB4AJDevice.REGISTED_ID + "=" + registed_id, null);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
			returnValue = 0;
		}
		mDB.setTransactionSuccessful();
		mDB.endTransaction();
		return returnValue;
	}

	public long updateRegistedDevice(DeviceAboutObject data) {
		long returnValue = 0;
		mDB.beginTransaction();

		ContentValues values = new ContentValues();
		values.put(AJDeviceDatabase.DB4AJDevice.REGISTED_NAME, data.getDeviceName());

		try {
			synchronized (mDB) {
				returnValue = mDB.update(AJDeviceDatabase.DB4AJDevice.TABLENAME_REGISTED_DEVICE, values,
						AJDeviceDatabase.DB4AJDevice.REGISTED_ID + "=" + data.get_id(), null);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
			returnValue = 0;
		}
		mDB.setTransactionSuccessful();
		mDB.endTransaction();
		return returnValue;
	}

}
