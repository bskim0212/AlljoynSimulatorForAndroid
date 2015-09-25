package com.lge.alljoyn.simulator.activity;

import java.util.ArrayList;

import com.lge.alljoyn.simulator.R;
import com.lge.alljoyn.simulator.about.DeviceAboutObject;
import com.lge.alljoyn.simulator.database.AJDeviceDBAdapter;
import com.lge.alljoyn.simulator.utils.DeviceMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class AddDeviceActivity extends Activity {
	
	private Context mContext = AddDeviceActivity.this;
	private AJDeviceDBAdapter dbAdapter;
	
	private ImageButton ib_back, ib_add;
	
	private ListView lv_device;
	private DeviceAdapter mDeviceAdapter;
	private ArrayList<DeviceAboutObject> device_list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_add);
		
		dbAdapter = new AJDeviceDBAdapter(mContext);
		dbAdapter.open();
		
		mDeviceAdapter = new DeviceAdapter();
		lv_device = (ListView) findViewById(R.id.lv_device);
		
		reloadData();
		lv_device.setAdapter(mDeviceAdapter);
			
		ib_back = (ImageButton) findViewById(R.id.ib_back);
		ib_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		ib_add = (ImageButton) findViewById(R.id.ib_add);
		ib_add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AddDialog();
			}
		});
	}
	
	private void reloadData(){
		device_list = dbAdapter.getDeviceList(8);
		mDeviceAdapter.setDeviceList(device_list);
		mDeviceAdapter.notifyDataSetChanged();
	}
	
	private void AddDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
		alert.setTitle("안내");
		alert.setMessage("추가할 디바이스 이름을 입력해주세요.");
		final EditText input = new EditText(this);
		input.setSingleLine(true);
		alert.setView(input);
		alert.setPositiveButton("등록", new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				DeviceAboutObject d_obj = new DeviceAboutObject();
				d_obj.setDeviceId(input.getText().toString());
				d_obj.setDeviceName(input.getText().toString());
				dbAdapter.insertDevice(d_obj);
				reloadData();
			}
		}).setNegativeButton("취소", null).show();
	}
	
	public class DeviceAdapter extends BaseAdapter {
		private ArrayList<DeviceAboutObject> items;
		private LayoutInflater Inflater;
		
		
		public void setDeviceList(ArrayList<DeviceAboutObject> device_list) {
			items = device_list;
		}
		
		@Override
		public int getCount() {
			if (items == null)
				return 0;
			return items.size();
		}

		@Override
		public Object getItem(int arg0) {
			if (items == null)
				return null;
			return items.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = Inflater.inflate(R.layout.item_add_device, null, true);
			}
			
			TextView tv = (TextView) convertView.findViewById(R.id.textView1);
			tv.setText(items.get(position).getDeviceName());
			
			ImageButton ib_delete = (ImageButton) convertView.findViewById(R.id.ib_delete);
			ImageButton ib_modify = (ImageButton) convertView.findViewById(R.id.ib_modify);

			
			convertView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext, AddInterfaceActivity.class);
					intent.putExtra(DeviceMap.IK_DEVICE_ID, items.get(position).get_id());
					intent.putExtra("Device_name", items.get(position).getDeviceName());
					startActivity(intent);
				}
			});
			
			ib_delete.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
					builder.setTitle("안내");
					builder.setMessage("삭제 하시겠습니까?");
					builder.setPositiveButton("확인", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (dbAdapter.deleteDevice(items.get(position).get_id()) != 0) {
								reloadData();
								dialog.dismiss();
							}
						}
					});
					builder.setNegativeButton("취소", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.show();
				}
			});
			ib_modify.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
					alert.setTitle("안내");
					alert.setMessage("변경할 디바이스 이름을 입력해주세요.");
					final EditText input = new EditText(mContext);
					input.setSingleLine(true);
					input.setText(items.get(position).getDeviceName());
					alert.setView(input);

					alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							String value = input.getText().toString();
							//Log.e("test", "device name = " + value);
							items.get(position).setDeviceName(value);
							if (dbAdapter.updateRegistedDevice(items.get(position)) != 0) {
								reloadData();
								dialog.dismiss();
							}
						}
					});
					alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.dismiss();
						}
					});
					alert.show();
				}
			});
			
			return convertView;
		}
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		dbAdapter.close();
		super.onDestroy();
	}
	

}
