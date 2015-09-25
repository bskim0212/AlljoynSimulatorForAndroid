package com.lge.alljoyn.simulator.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.lge.alljoyn.simulator.R;
import com.lge.alljoyn.simulator.about.DeviceAboutObject;
import com.lge.alljoyn.simulator.database.AJDeviceDBAdapter;
import com.lge.alljoyn.simulator.interfaces.DeviceValueObject;
import com.lge.alljoyn.simulator.service.BusConnectionService;
import com.lge.alljoyn.simulator.utils.DeviceMap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends CommonActivity {
	private ProgressDialog mProgressDialog;
	private Intent serviceIntent;

	private static int list_index = 0;

	private ImageButton btn_file, btn_add;
	private ListView lv_device;
	private DeviceAdapter mDeviceAdapter;
	private AJDeviceDBAdapter dbAdapter;
	private ArrayList<DeviceAboutObject> device_list;
	private Context mContext = MainActivity.this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DeviceMap.sInterfaceeMap = new HashMap<String, HashMap<String, DeviceValueObject>>();

		dbAdapter = new AJDeviceDBAdapter(mContext);
		dbAdapter.open();
		// Fragment fr = MainFragment.newInstance();
		// if (fr != null && !fr.isDetached()) {
		// FragmentTransaction ft = getFragmentManager().beginTransaction();
		// ft.replace(R.id.frame, fr, "");
		// ft.commit();
		// }
		
		int test = dbAdapter.getLastDeviceId();
		Log.e("bskim", "getLastDeviceId=" + test);

		lv_device = (ListView) findViewById(R.id.lv_device);
		device_list = dbAdapter.getRegistedDeviceList();

		mDeviceAdapter = new DeviceAdapter(R.layout.item_device, device_list);

		serviceIntent = new Intent(this, BusConnectionService.class);

		lv_device.setAdapter(mDeviceAdapter);
		
		btn_file = (ImageButton) findViewById(R.id.btn_file);
		btn_file.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
//				Intent intent = new Intent(mContext, FileExplorerActivity.class);
//				startActivity(intent);
				Intent intent = new Intent(mContext, AddDeviceActivity.class);
				startActivity(intent);
			}
		});

		btn_add = (ImageButton) findViewById(R.id.btn_add);
		btn_add.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AddDialog();
			}
		});

	}

	private void runConnectionService(int registed_id, int startFlag) {
		serviceIntent.putExtra(DeviceMap.IK_DEVICE_ID, registed_id);
		serviceIntent.putExtra(DeviceMap.IK_START_TYPE, startFlag);
		startService(serviceIntent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent serviceIntent = new Intent(this, BusConnectionService.class);
		stopService(serviceIntent);

		if (dbAdapter != null) {
			dbAdapter.close();
		}
	}

	// @Override
	// public void dataChenged(String device_id) {
	// // 서비스에서 특정 디바이스 정보 갱신 후 알림
	// // Log.e("bskim", "dataChenged");
	// Log.v("bskim", "detailactivity dataChenged");
	// reloadData(null);
	// }

	private void AddDialog() {

		final ArrayList<DeviceAboutObject> items_list = dbAdapter.getDeviceList(0);
		final String[] items = new String[items_list.size()];
		for (int i = 0; i < items_list.size(); i++) {
			items[i] = items_list.get(i).getDeviceId();
		}

		list_index = 0;

		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setTitle("Device");
		ad.setSingleChoiceItems(items, 0, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, final int which) {
				// TODO Auto-generated method stub
				list_index = which;
				// Log.v("bskim", "index = " + list_index);

			}
		});

		ad.setPositiveButton("OK", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// device_list.add(items[type_index]);
				// Log.v("bskim", "insert register data : device_id=" +
				// items_list.get(list_index).get_id() + " name="
				// + items_list.get(list_index).getDeviceName());
				DeviceAboutObject tempObj = new DeviceAboutObject();
				tempObj.setDeviceName(items_list.get(list_index).getDeviceName());
				tempObj.setDeviceType(items_list.get(list_index).get_id());
				// 신규 디바이스 추가

				if (dbAdapter.insertRegistedDevice(tempObj) > 0) {
					// 디바이스 등록 성공

					reloadData(null, DeviceMap.FLAG_CHANGED_DATA);
				}

			}
		});
		ad.setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		ad.show();
	}

	@Override
	public void reloadData(String device_id, int flag) {
		// device_list.add(new DeviceAboutObject());
		// Log.e("bskim", "등록성공");

		if (flag == DeviceMap.FLAG_CHANGED_DATA) {
			device_list = dbAdapter.getRegistedDeviceList();
			mDeviceAdapter.setDeviceList(device_list);
			mDeviceAdapter.notifyDataSetChanged();
		}

		if (flag == DeviceMap.FLAG_CONNECTION_SUCCESS) {
			reloadData(device_id, DeviceMap.FLAG_CHANGED_DATA);

			// 다이얼로그 close
		}

		if (flag == DeviceMap.FLAG_CONNECTION_FAIL) {
			// 다이얼로그 close
			Toast.makeText(mContext, "alljoyn service running fail", Toast.LENGTH_SHORT).show();
		}
		dismissProgressDialog();

	}

	public class DeviceAdapter extends BaseAdapter {
		private int res;
		private ArrayList<DeviceAboutObject> items;

		LayoutInflater Inflater;

		public DeviceAdapter(int resource, ArrayList<DeviceAboutObject> device_list) {
			this.items = device_list;
			this.res = resource;
			Inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void setDeviceList(ArrayList<DeviceAboutObject> device_list) {
			items = device_list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (items == null)
				return 0;
			return items.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			if (items == null)
				return null;
			return items.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = Inflater.inflate(res, null, true);
			}

			Button btn_onoff = (Button) convertView.findViewById(R.id.btn_onoff);

			TextView tv = (TextView) convertView.findViewById(R.id.textView1);
			tv.setText(items.get(position).getDeviceName());

			LinearLayout ll_sub = (LinearLayout) convertView.findViewById(R.id.ll_sub);
			ImageButton ib_delete = (ImageButton) convertView.findViewById(R.id.ib_delete);
			ImageButton ib_modify = (ImageButton) convertView.findViewById(R.id.ib_modify);

			convertView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					// 해당 디바이스의 서비스가 구동중인지 체크
					if (!DeviceMap.sInterfaceeMap.containsKey("" + items.get(position).get_id())) {
						Toast.makeText(mContext, "장비를 실행시켜주세요.", Toast.LENGTH_SHORT).show();
					} else {
						//Log.v("bskim", "registed = " + items.get(position).get_id());
						Intent intent = new Intent(mActivity, DetailActivity.class);
						intent.putExtra(DeviceMap.IK_DEVICE_ID, items.get(position).get_id());
						startActivity(intent);
					}
				}
			});

			if (!DeviceMap.sInterfaceeMap.containsKey("" + items.get(position).get_id())) {
				ll_sub.setVisibility(View.VISIBLE);
				// ib_delete.setVisibility(View.VISIBLE);
				// ib_modify.setVisibility(View.VISIBLE);
			} else {
				ll_sub.setVisibility(View.INVISIBLE);
				// ib_delete.setVisibility(View.INVISIBLE);
				// ib_modify.setVisibility(View.INVISIBLE);
			}

			// 서비스 실행여부
			if (DeviceMap.sInterfaceeMap.containsKey("" + items.get(position).get_id())) {
				// Log.e("bskim", "device state running");
				btn_onoff.setText("ON");
				btn_onoff.setBackgroundColor(Color.parseColor("#e0a3a3"));
				btn_onoff.setTextColor(Color.parseColor("#712627"));
			} else {
				// Log.e("bskim", "device state stop");
				btn_onoff.setText("OFF");
				btn_onoff.setBackgroundColor(Color.parseColor("#d5d5d5"));
				btn_onoff.setTextColor(Color.parseColor("#bdbdbd"));
			}

			btn_onoff.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 해당 디바이스의 서비스가 구동중인지 체크

					if (!DeviceMap.sInterfaceeMap.containsKey("" + items.get(position).get_id())) {
						// Log.e("bskim", "device run");
						showProgressDialog(R.string.data_loading, "" + items.get(position).get_id());

						// reLoadDeviceList();
					} else {
						// Log.e("bskim", "device stop");
						// DeviceMap.sInterfaceeMap.remove("" +
						// items.get(position).get_id());
						runConnectionService(items.get(position).get_id(), 0);
						// reLoadDeviceList();
					}

				}
			});
			ib_delete.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle("안내");
					builder.setMessage("삭제 하시겠습니까?");
					builder.setPositiveButton("확인", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (dbAdapter.deleteRegistedDevice(items.get(position).get_id()) != 0) {
								reloadData(null, DeviceMap.FLAG_CHANGED_DATA);
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
					AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
					alert.setTitle("안내");
					alert.setMessage("변경할 디바이스 이름을 입력해주세요.");
					final EditText input = new EditText(MainActivity.this);
					input.setSingleLine(true);
					input.setText(items.get(position).getDeviceName());
					alert.setView(input);

					alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							String value = input.getText().toString();
							//Log.e("test", "device name = " + value);
							items.get(position).setDeviceName(value);
							if (dbAdapter.updateRegistedDevice(items.get(position)) != 0) {
								reloadData(null, DeviceMap.FLAG_CHANGED_DATA);
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

	public void showProgressDialog(int dataLoading, final String id) {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		mProgressDialog = new ProgressDialog(this);
		if (id != null) {
			mProgressDialog.setCancelable(true);
			mProgressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					if (DeviceMap.sInterfaceeMap.containsKey(id)) {
						DeviceMap.sInterfaceeMap.remove(id);
						runConnectionService(Integer.valueOf(id), 0);
					}
				}
			});
		}
		mProgressDialog.setMessage(getString(dataLoading));
		mProgressDialog.show();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				runConnectionService(Integer.valueOf(id), 1);

			}
		}, 100);

	}

	public void dismissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	@Override
	public void onResume() {
		mActivity = this;
		super.onResume();
	}

}
