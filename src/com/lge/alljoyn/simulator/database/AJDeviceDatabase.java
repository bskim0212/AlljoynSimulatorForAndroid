package com.lge.alljoyn.simulator.database;

import android.provider.BaseColumns;

public final class AJDeviceDatabase {

	public static final class DB4AJDevice implements BaseColumns {
		// 테이블명
		public static final String TABLENAME_DEVICE = "tb_device";
		public static final String TABLENAME_REGISTED_DEVICE = "tb_registed_device";
		public static final String TABLENAME_INTERFACE = "tb_interface";
		public static final String TABLENAME_INTERFACE_RANGE = "tb_if_range";

		// tb_device 컬럼명
		public static final String DEVICE_ID = "_id";
		public static final String DEVICE_MODEL_NUMBER = "ModelNumber";
		public static final String DEVICE_NAME = "DeviceName";
		public static final String DEVICE_USE_FLAG = "UseFlag";
		
		// tb_registed_device 컬럼명
		public static final String REGISTED_ID = "_id";
		public static final String REGISTED_DEVICE_ID = "device_id";
		public static final String REGISTED_NAME = "name";
		
		// tb_interface 컬럼명
		public static final String INTERFACE_ID = "_id";
		public static final String INTERFACE_NAME = "if_name";
		public static final String INTERFACE_PATH = "if_path";
		public static final String INTERFACE_TYPE = "if_type";
		public static final String INTERFACE_SIGNAL = "if_signal";
		public static final String INTERFACE_DEVICE_ID = "if_device_id";
		public static final String INTERFACE_DEFAULT_VALUE = "if_default_value";
		public static final String INTERFACE_MIN_VALUE = "if_min_value";
		public static final String INTERFACE_MAX_VALUE = "if_max_value";
		public static final String INTERFACE_HAS_INDEX = "if_has_index";
		public static final String INTERFACE_DESCRIPTION = "if_description";
		public static final String INTERFACE_UNIT = "if_unit";
		public static final String INTERFACE_DIALOG_ACTION_1 = "if_dialog_action1";
		public static final String INTERFACE_DIALOG_ACTION_2 = "if_dialog_action2";
		public static final String INTERFACE_DIALOG_ACTION_3 = "if_dialog_action3";
		public static final String INTERFACE_NOTI_FLAG = "if_noti_flag";
		public static final String INTERFACE_UI_TYPE = "if_ui_type";
		public static final String INTERFACE_WRITABLE = "if_writable";
		public static final String INTERFACE_SECURED = "if_secured";
		public static final String INTERFACE_DIALOG_BUTTON1 = "if_dialog_button1";
		public static final String INTERFACE_DIALOG_BUTTON2 = "if_dialog_button2";
		public static final String INTERFACE_DIALOG_BUTTON3 = "if_dialog_button3";
		public static final String INTERFACE_DIALOG_MSG = "if_dialog_msg";
		
		// tb_if_range 컬럼명
		public static final String RANGE_ID = "_id";
		public static final String RANGE_INDEX = "range_index";
		public static final String RANGE_LABEL = "range_label";
		public static final String RANGE_INTERFACE_ID = "if_id";
		
		
		public static final String CREATE_TB_DEVICE = "create table "
                + TABLENAME_DEVICE + "(" + _ID
                + " integer primary key autoincrement not null unique, "
                + DEVICE_MODEL_NUMBER + " text, " //
                + DEVICE_NAME + " text, "
                + DEVICE_USE_FLAG + " integer not null default(1)"
                + " );";
		
		public static final String CREATE_TB_REGISTED_DEVICE = "create table "
                + TABLENAME_REGISTED_DEVICE + "(" + _ID
                + " integer primary key autoincrement not null unique, "
                + REGISTED_DEVICE_ID + " integer not null, " 
                + REGISTED_NAME + " text "
                + " );";
		

		public static final String CREATE_TB_INTERFACE = "create table "
				+ TABLENAME_INTERFACE + "(" + _ID
				+ " integer primary key autoincrement not null unique, "
				+ INTERFACE_NAME + " text, "
				+ INTERFACE_PATH + " text, "
				+ INTERFACE_TYPE + " text, "
				+ INTERFACE_SIGNAL + " text, "
				+ INTERFACE_DEVICE_ID + " integer not null "
				+ INTERFACE_DEFAULT_VALUE + " text, "
				+ INTERFACE_MIN_VALUE + " text, "
				+ INTERFACE_MAX_VALUE + " text, "
				+ INTERFACE_HAS_INDEX + " integer not null default(0), "
				+ INTERFACE_DESCRIPTION + " text, "
				+ INTERFACE_UNIT + " text, "
				+ INTERFACE_DIALOG_ACTION_1 + " text, "
				+ INTERFACE_DIALOG_ACTION_2 + " text, "
				+ INTERFACE_DIALOG_ACTION_3 + " text, "//
				+ INTERFACE_NOTI_FLAG + " integer not null default(0), "
				+ INTERFACE_UI_TYPE + " integer not null default(1), "
				+ INTERFACE_WRITABLE + " integer not null default(1), "
				+ INTERFACE_SECURED + " integer not null default(0), "
				+ INTERFACE_DIALOG_BUTTON1 + " text, "
				+ INTERFACE_DIALOG_BUTTON2 + " text, "
				+ INTERFACE_DIALOG_BUTTON3 + " text, "
				+ INTERFACE_DIALOG_MSG + " text "
				+ " );";
		
		public static final String CREATE_TB_INTERFACE_RANGE = "create table "
				+ TABLENAME_INTERFACE_RANGE + "(" + _ID
				+ " integer primary key autoincrement not null unique, "
				+ RANGE_INDEX + " text, "
				+ RANGE_LABEL + " text, "
				+ RANGE_INTERFACE_ID + " integer not null "
				+ " );";
	}

}
