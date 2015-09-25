package com.lge.alljoyn.simulator.activity;

import com.lge.alljoyn.simulator.listener.DeviceDataChangedListener;

import android.app.Activity;

public class CommonActivity extends Activity implements DeviceDataChangedListener {

	public static CommonActivity mActivity;

	@Override
	public void dataChenged(final String device_id,final int flag) {
		// TODO Auto-generated method stub
		Runnable run = new Runnable(){
		     public void run(){
		    	 reloadData(device_id, flag);
		     }
		};
		runOnUiThread(run);
	}

	public void reloadData(String device_id, int flag) {

	}

}
