package com.lge.alljoyn.simulator.activity;

import com.lge.alljoyn.simulator.R;
import com.lge.alljoyn.simulator.utils.DeviceMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class AddInterfaceActivity extends Activity {
	
	private int deviceId;
	private String deviceName;

	private ImageButton ib_back;
	private TextView tv_name;
	private EditText et_if_path;
	private Spinner sp_if_type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interface_add);
		
		Intent intent = getIntent();
		deviceId = intent.getIntExtra(DeviceMap.IK_DEVICE_ID, 0);
		deviceName = intent.getStringExtra("Device_name");
		
		ib_back = (ImageButton) findViewById(R.id.ib_back);
		ib_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_name.setText(deviceName);
		
		et_if_path = (EditText) findViewById(R.id.et_if_path);
		sp_if_type = (Spinner) findViewById(R.id.sp_if_type);

	}

}
