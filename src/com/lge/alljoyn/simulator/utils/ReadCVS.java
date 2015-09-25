package com.lge.alljoyn.simulator.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.lge.alljoyn.simulator.about.DeviceAboutObject;
import com.lge.alljoyn.simulator.about.InterfaceObject;
import com.lge.alljoyn.simulator.about.InterfaceRangeObject;
import com.lge.alljoyn.simulator.database.AJDeviceDBAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

public class ReadCVS {
	
	private Context mContext;
	
	String[] columns;
	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ",";
	private AJDeviceDBAdapter dbAdapter;
	int i = 0;
	private String deviceName;

	public void run (String csvFile, String FileName, Context ctx) {
		mContext = ctx;
		deviceName = FileName;
		
		dbAdapter = new AJDeviceDBAdapter(ctx);
		dbAdapter.open();
		DeviceInsert();
		try {
			
			
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				i++;
				if(i==1){
					continue;
				}
				columns = line.split(cvsSplitBy);
				
				InterfaceInsert();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		Log.i("ReadCVS","Done");
		new AlertDialog.Builder(mContext).setTitle("알림").setMessage("등록이 완료되었습니다.")
		.setPositiveButton("확인", null).show();
		dbAdapter.close();
	}
	public void DeviceInsert(){
		DeviceAboutObject d_obj = new DeviceAboutObject();
		d_obj.setDeviceId(deviceName);
		d_obj.setDeviceName(deviceName);
		dbAdapter.insertDevice(d_obj);
	}
	
	public void InterfaceInsert(){
		InterfaceObject obj = new InterfaceObject();
		try{
			obj.setIf_name(columns[1]);
			obj.setIf_path(columns[2]);
			obj.setIf_type(columns[3]);
			obj.setIf_signal(columns[4]);
			obj.setIf_device_id(dbAdapter.getLastDeviceId());
			obj.setIf_default_value(columns[6]);
			obj.setIf_min_value(columns[7]);
			obj.setIf_max_value(columns[8]);
			obj.setIf_has_index(Integer.parseInt(columns[9]));
			obj.setIf_description(columns[10]);
			obj.setIf_unit(columns[11]);
			obj.setIf_dialog_action1(columns[13]);
			obj.setIf_dialog_action2(columns[14]);
			obj.setIf_dialog_action3(columns[15]);
			obj.setIf_noti_flag(Integer.parseInt(columns[16]));
			obj.setIf_ui_type(Integer.parseInt(columns[17]));
			obj.setIf_writable(Integer.parseInt(columns[18]));
			obj.setIf_secured(Integer.parseInt(columns[19]));
			obj.setIf_dialog_button1(columns[20]);
			obj.setIf_dialog_button2(columns[21]);
			obj.setIf_dialog_button3(columns[22]);
			obj.setIf_dialog_msg(columns[23]);
			
			if(obj.getIf_has_index() == 1){
				InterfaceRangeObject rangeObj = new InterfaceRangeObject();
				rangeObj.setIf_id(dbAdapter.getLastInterfaceId());
				
				dbAdapter.insertInterfaceRange(rangeObj);
			}
		}
		catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		dbAdapter.insertInterface(obj);
	}

}
