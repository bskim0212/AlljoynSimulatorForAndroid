package com.lge.alljoyn.simulator.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Variant;

import com.lge.alljoyn.simulator.R;
import com.lge.alljoyn.simulator.about.DeviceAboutObject;
import com.lge.alljoyn.simulator.about.InterfaceObject;
import com.lge.alljoyn.simulator.about.InterfaceRangeObject;
import com.lge.alljoyn.simulator.about.PropertyObject;
import com.lge.alljoyn.simulator.database.AJDeviceDBAdapter;
import com.lge.alljoyn.simulator.interfaces.PropertyMaker;
import com.lge.alljoyn.simulator.interfaces.ListQQQQValuesC;
import com.lge.alljoyn.simulator.interfaces.ListQQQQValuesP;
import com.lge.alljoyn.simulator.interfaces.QQQQPropertyMaker;
import com.lge.alljoyn.simulator.service.BusConnectionService;
import com.lge.alljoyn.simulator.service.BusMapObject;
import com.lge.alljoyn.simulator.utils.DeviceMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DetailActivity extends CommonActivity {

	// private int device_id;
	private DeviceAboutObject mDeviceAboutObject;
	private AJDeviceDBAdapter dbAdapter;
	private ArrayList<InterfaceObject> interfaceList;
	private ArrayList<PropertyObject> propertyList;
	private ListView iv_interface;
	private InterfaceAdapter mInterfaceAdapter;

	private ImageButton ib_back;

	private TextView tv_name, tv_model;

	private int deviceId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		Intent intent = getIntent();
		deviceId = intent.getIntExtra(DeviceMap.IK_DEVICE_ID, 0);
		// Log.e("test", "device_id=" + deviceId);
		ib_back = (ImageButton) findViewById(R.id.ib_back);
		ib_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_model = (TextView) findViewById(R.id.tv_model);
		iv_interface = (ListView) findViewById(R.id.iv_interface);

		dbAdapter = new AJDeviceDBAdapter(this);
		dbAdapter.open();
		mDeviceAboutObject = dbAdapter.getRegistedDevice(deviceId);

		tv_name.setText(mDeviceAboutObject.getDeviceName());
		tv_model.setText(mDeviceAboutObject.getDeviceId());

		mInterfaceAdapter = new InterfaceAdapter(this, R.layout.item_interface);
		iv_interface.setAdapter(mInterfaceAdapter);
		interfaceList = dbAdapter.getInterfaceList(mDeviceAboutObject.getDeviceType(), BusMapObject.CP_PROPERTY);
		// dbAdapter.close();
		reloadData("" + mDeviceAboutObject.get_id(), DeviceMap.FLAG_CHANGED_DATA);
	}

	@Override
	public void reloadData(String device_id, int flag) {
		// Log.v("bskim", "detailactivity reloadData");
		if (flag == DeviceMap.FLAG_CHANGED_DATA) {
			// Log.v("bskim", "flag data changed");
			propertyList = new ArrayList<PropertyObject>();
			for (int i = 0; i < interfaceList.size(); i++) {
				// Log.v("bskim", "interfaceList[" + i + "]");
				String label = interfaceList.get(i).getIf_description();
				Variant vv = DeviceMap.getValue("" + mDeviceAboutObject.get_id(), interfaceList.get(i).getIf_path(),
						interfaceList.get(i).getIf_signal(), interfaceList.get(i).getIf_default_value());

				if (vv == null) {
					// Log.e("vv", "null");
					continue;
				}

				PropertyObject obj = new PropertyObject();
				obj.setObj(interfaceList.get(i));

				if (interfaceList.get(i).getIf_signal().equals(BusMapObject.DATA_TYPE_S)) {
					String value = null;
					try {
						value = vv.getObject(String.class);
						obj.setLabel(label);
						if (interfaceList.get(i).getIf_unit() != null && mDeviceAboutObject.getDeviceType() < 11) {

							if (interfaceList.get(i).getIf_unit().equals("℉") && isNumber(value)) {
								int fValue = FtoC(Integer.valueOf(value));
								obj.setValue(value + interfaceList.get(i).getIf_unit() + "(" + fValue + "℃)");
							} else if (interfaceList.get(i).getIf_unit().equals("℃") && isNumber(value)) {
								int cValue = CtoF(Integer.valueOf(value));
								obj.setValue(value + interfaceList.get(i).getIf_unit() + "(" + cValue + "℉)");
							} else {
								obj.setValue(value + interfaceList.get(i).getIf_unit());
							}

						} else {
							obj.setValue(value);
						}
						propertyList.add(obj);
					} catch (BusException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				if (interfaceList.get(i).getIf_signal().equals(BusMapObject.DATA_TYPE_Q)
						|| interfaceList.get(i).getIf_signal().equals(BusMapObject.DATA_TYPE_N)) {
					Short value = 0;
					// Log.e("bskim", "q data");
					try {

						if (interfaceList.get(i).getIf_has_index() == 1) {
							// Log.e("bskim", "has range");
							ArrayList<InterfaceRangeObject> rangeList = interfaceList.get(i).getInterface_range();
							value = vv.getObject(Short.class);
							String rValue = null;
							int rIndex = 0;
							for (int j = 0; j < rangeList.size(); j++) {

								if (rangeList.get(j).getRange_index().equals("" + value)) {
									rValue = rangeList.get(j).getRange_label();
									rIndex = Integer.valueOf(rangeList.get(j).getRange_index());
								}
							}

							obj.setLabel(label);
							obj.setIndex(rIndex);
							if (interfaceList.get(i).getIf_unit() != null) {
								obj.setValue("" + rValue + interfaceList.get(i).getIf_unit());

							} else {
								obj.setValue("" + rValue);
							}
							// Log.e("bskim", "add view " +
							// interfaceList.get(i).getIf_description());

							propertyList.add(obj);
						} else {
							// Log.e("bskim", "has not range");
							value = vv.getObject(Short.class);
							obj.setIndex((int) value);
							obj.setLabel(label);
							if (interfaceList.get(i).getIf_unit() != null) {
								if (interfaceList.get(i).getIf_unit().equals("℉")) {
									int fValue = FtoC(Integer.valueOf("" + value));
									obj.setValue("" + value + interfaceList.get(i).getIf_unit() + "(" + fValue + "℃)");
								} else if (interfaceList.get(i).getIf_unit().equals("℃")) {
									int cValue = CtoF(Integer.valueOf("" + value));
									obj.setValue("" + value + interfaceList.get(i).getIf_unit() + "(" + cValue + "℉)");
								} else {
									obj.setValue("" + value + interfaceList.get(i).getIf_unit());
								}

							} else {
								obj.setValue("" + value);
							}
							propertyList.add(obj);

							// Log.e("bskim", "add view " +
							// interfaceList.get(i).getIf_description());

							// 조명 on/off 하드코딩
							if (interfaceList.get(i).getIf_description().equalsIgnoreCase("Stand Light Dim Level")) {
								PropertyObject obj2 = new PropertyObject();
								InterfaceObject iObj = new InterfaceObject(interfaceList.get(i));
								ArrayList<InterfaceRangeObject> iRObjArr = new ArrayList<InterfaceRangeObject>();
								iRObjArr.add(new InterfaceRangeObject(0, "0", "Off", iObj.get_id()));
								iRObjArr.add(new InterfaceRangeObject(1, "1", "On", iObj.get_id()));
								iObj.setInterface_range(iRObjArr);
								iObj.setIf_description("Stand Light Status");
								iObj.setIf_has_index(1);
								iObj.setIf_default_value("1");
								iObj.setIf_min_value("0");
								iObj.setIf_max_value("1");
								obj2.setLabel(iObj.getIf_description());
								obj2.setObj(iObj);

								if (value > 0) {
									obj2.setIndex(1);
									obj2.setValue("On");
								} else {
									obj2.setIndex(0);
									obj2.setValue("Off");
								}
								// Log.e("bskim", "add view " +
								// iObj.getIf_description());
								propertyList.add(obj2);

							}

							else if (interfaceList.get(i).getIf_description()
									.equalsIgnoreCase("Down Light Dim Level")) {
								PropertyObject obj2 = new PropertyObject();
								InterfaceObject iObj = new InterfaceObject(interfaceList.get(i));
								ArrayList<InterfaceRangeObject> iRObjArr = new ArrayList<InterfaceRangeObject>();
								iRObjArr.add(new InterfaceRangeObject(0, "0", "Off", iObj.get_id()));
								iRObjArr.add(new InterfaceRangeObject(1, "1", "On", iObj.get_id()));
								iObj.setInterface_range(iRObjArr);
								iObj.setIf_description("Down Light Status");
								iObj.setIf_has_index(1);
								iObj.setIf_default_value("1");
								iObj.setIf_min_value("0");
								iObj.setIf_max_value("1");
								obj2.setLabel(iObj.getIf_description());
								obj2.setObj(iObj);

								if (value > 0) {
									obj2.setIndex(1);
									obj2.setValue("On");
								} else {
									obj2.setIndex(0);
									obj2.setValue("Off");
								}
								// Log.e("bskim", "add view " +
								// iObj.getIf_description());
								propertyList.add(obj2);
							}

							else if (interfaceList.get(i).getIf_description()
									.equalsIgnoreCase("Surface Mount Light Dim Level")) {
								PropertyObject obj2 = new PropertyObject();
								InterfaceObject iObj = new InterfaceObject(interfaceList.get(i));
								ArrayList<InterfaceRangeObject> iRObjArr = new ArrayList<InterfaceRangeObject>();
								iRObjArr.add(new InterfaceRangeObject(0, "0", "Off", iObj.get_id()));
								iRObjArr.add(new InterfaceRangeObject(1, "1", "On", iObj.get_id()));
								iObj.setInterface_range(iRObjArr);
								iObj.setIf_description("Surface Mount Light Status");
								iObj.setIf_has_index(1);
								iObj.setIf_default_value("1");
								iObj.setIf_min_value("0");
								iObj.setIf_max_value("1");
								obj2 = new PropertyObject();
								obj2.setLabel(iObj.getIf_description());
								obj2.setObj(iObj);

								if (value > 0) {
									obj2.setIndex(1);
									obj2.setValue("On");
								} else {
									obj2.setIndex(0);
									obj2.setValue("Off");
								}
								// Log.e("bskim", "add view " +
								// iObj.getIf_description());
								propertyList.add(obj2);

							}
						}

					} catch (BusException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (interfaceList.get(i).getIf_signal().equals(BusMapObject.DATA_TYPE_D)) {

					try {

						if (interfaceList.get(i).getIf_has_index() == 1) {
							ArrayList<InterfaceRangeObject> rangeList = interfaceList.get(i).getInterface_range();
							Double value = vv.getObject(Double.class);
							String rValue = null;
							int rIndex = 0;
							for (int j = 0; j < rangeList.size(); j++) {

								if (rangeList.get(j).getRange_index().equals("" + value)) {
									rValue = rangeList.get(j).getRange_label();
									rIndex = Integer.valueOf(rangeList.get(j).getRange_index());
								}
							}

							obj.setLabel(label);
							obj.setIndex(rIndex);
							if (interfaceList.get(i).getIf_unit() != null) {
								obj.setValue("" + rValue + interfaceList.get(i).getIf_unit());
							} else {
								obj.setValue("" + rValue);
							}
							propertyList.add(obj);
						} else {
							Double value = vv.getObject(Double.class);
							obj.setIndex(Integer.valueOf(value.toString()));
							obj.setLabel(label);
							if (interfaceList.get(i).getIf_unit() != null) {

								if (interfaceList.get(i).getIf_unit().equals("℉")) {
									double fValue = FtoC(Double.valueOf("" + value));
									obj.setValue("" + value + interfaceList.get(i).getIf_unit() + "(" + fValue + "℃)");
								} else if (interfaceList.get(i).getIf_unit().equals("℃")) {
									double cValue = CtoF(Double.valueOf("" + value));
									obj.setValue("" + value + interfaceList.get(i).getIf_unit() + "(" + cValue + "℉)");
								} else {
									obj.setValue("" + value + interfaceList.get(i).getIf_unit());
								}

							} else {
								obj.setValue("" + value);
							}
							propertyList.add(obj);
						}

					} catch (BusException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (interfaceList.get(i).getIf_signal().equals(BusMapObject.DATA_TYPE_I)
						|| interfaceList.get(i).getIf_signal().equals(BusMapObject.DATA_TYPE_U)) {
					try {
						if (interfaceList.get(i).getIf_has_index() == 1) {
							ArrayList<InterfaceRangeObject> rangeList = interfaceList.get(i).getInterface_range();
							Integer value = vv.getObject(Integer.class);
							String rValue = null;
							int rIndex = 0;
							for (int j = 0; j < rangeList.size(); j++) {
								if (rangeList.get(j).getRange_index().equals("" + value)) {
									rValue = rangeList.get(j).getRange_label();
									rIndex = Integer.valueOf(rangeList.get(j).getRange_index());
								}
							}

							obj.setLabel(label);
							obj.setIndex(rIndex);
							if (interfaceList.get(i).getIf_unit() != null) {
								obj.setValue("" + rValue + interfaceList.get(i).getIf_unit());
							} else {
								obj.setValue("" + rValue);
							}
							propertyList.add(obj);

						} else {
							Integer value = vv.getObject(Integer.class);
							obj.setIndex(value);
							obj.setLabel(label);
							if (interfaceList.get(i).getIf_unit() != null) {
								if (interfaceList.get(i).getIf_unit().equals("℉")) {
									int fValue = FtoC(Integer.valueOf("" + value));
									obj.setValue("" + value + interfaceList.get(i).getIf_unit() + "(" + fValue + "℃)");
								} else if (interfaceList.get(i).getIf_unit().equals("℃")) {
									int cValue = CtoF(Integer.valueOf("" + value));
									obj.setValue("" + value + interfaceList.get(i).getIf_unit() + "(" + cValue + "℉)");
								} else {
									obj.setValue("" + value + interfaceList.get(i).getIf_unit());
								}

							} else {
								obj.setValue("" + value);
							}
							propertyList.add(obj);
						}

					} catch (BusException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (interfaceList.get(i).getIf_signal().equals(BusMapObject.DATA_TYPE_B)) {
					try {
						if (interfaceList.get(i).getIf_has_index() == 1) {
							ArrayList<InterfaceRangeObject> rangeList = interfaceList.get(i).getInterface_range();
							Boolean value = vv.getObject(Boolean.class);
							String rValue = null;
							int rIndex = 0;
							for (int j = 0; j < rangeList.size(); j++) {
								if (rangeList.get(j).getRange_index().equals("" + value)) {
									rValue = rangeList.get(j).getRange_label();
									rIndex = Integer.valueOf(rangeList.get(j).getRange_index());
								}
							}

							obj.setLabel(label);
							obj.setIndex(rIndex);
							if (interfaceList.get(i).getIf_unit() != null) {
								obj.setValue("" + rValue + interfaceList.get(i).getIf_unit());
							} else {
								obj.setValue("" + rValue);
							}
							propertyList.add(obj);
						} else {
							Boolean value = vv.getObject(Boolean.class);

							obj.setLabel(label);
							if (interfaceList.get(i).getIf_unit() != null) {
								obj.setValue("" + value + interfaceList.get(i).getIf_unit());
							} else {
								obj.setValue("" + value);
							}
							propertyList.add(obj);
						}

					} catch (BusException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (interfaceList.get(i).getIf_signal().equals(BusMapObject.DATA_TYPE_X)
						|| interfaceList.get(i).getIf_signal().equals(BusMapObject.DATA_TYPE_T)) {

					try {

						if (interfaceList.get(i).getIf_has_index() == 1) {
							ArrayList<InterfaceRangeObject> rangeList = interfaceList.get(i).getInterface_range();
							long value = vv.getObject(Long.class);
							String rValue = null;
							int rIndex = 0;
							for (int j = 0; j < rangeList.size(); j++) {

								if (rangeList.get(j).getRange_index().equals("" + value)) {
									rValue = rangeList.get(j).getRange_label();
									rIndex = Integer.valueOf(rangeList.get(j).getRange_index());
								}
							}

							obj.setLabel(label);
							obj.setIndex(rIndex);
							if (interfaceList.get(i).getIf_unit() != null) {
								obj.setValue("" + rValue + interfaceList.get(i).getIf_unit());
							} else {
								obj.setValue("" + rValue);
							}
							propertyList.add(obj);
						} else {
							int value = vv.getObject(Integer.class);
							obj.setIndex(value);
							obj.setLabel(label);
							if (interfaceList.get(i).getIf_unit() != null) {
								obj.setValue("" + value + interfaceList.get(i).getIf_unit());
							} else {
								obj.setValue("" + value);
							}
							propertyList.add(obj);
						}

					} catch (BusException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (interfaceList.get(i).getIf_signal().equals(BusMapObject.DATA_TYPE_QQQ)) {
					try {
						ListQQQQValuesP value = vv.getObject(ListQQQQValuesP.class);

						String fs = "" + value.sv.fv;
						String ss = "" + value.sv.sv;
						String ts = "" + value.sv.tv;
						if (fs.length() == 1) {
							fs = "0" + fs;
						}

						if (ss.length() == 1) {
							ss = "0" + ss;
						}

						if (ts.length() == 1) {
							ts = "0" + ts;
						}

						obj.setLabel(label);
						obj.setValue(fs + ":" + ss + ":" + ts);

						propertyList.add(obj);

					} catch (BusException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

			mInterfaceAdapter.setItem(propertyList);
			mInterfaceAdapter.notifyDataSetChanged();

		}

	}

	public class InterfaceAdapter extends BaseAdapter {
		private Context mCtx;
		private int res;
		private ArrayList<PropertyObject> items;

		// LayoutInflater Inflater;

		public InterfaceAdapter(Context mContext, int resource) {
			this.mCtx = mContext;
			this.res = resource;
			// Inflater = (LayoutInflater)
			// mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void setItem(ArrayList<PropertyObject> items) {

			this.items = items;

		}

		@Override
		public int getCount() {
			if (items == null)
				return 0;
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (items == null)
				return null;
			return items.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater Inflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = Inflater.inflate(res, null, true);
			}

			TextView tv_label = (TextView) convertView.findViewById(R.id.tv_label);
			TextView tv_value = (TextView) convertView.findViewById(R.id.tv_value);
			final EditText et_value = (EditText) convertView.findViewById(R.id.et_value);

			ImageButton btn_up = (ImageButton) convertView.findViewById(R.id.btn_up);
			ImageButton btn_down = (ImageButton) convertView.findViewById(R.id.btn_down);
			LinearLayout ll_btn = (LinearLayout) convertView.findViewById(R.id.ll_btn);

			tv_label.setText(items.get(position).getLabel());
			tv_value.setText(items.get(position).getValue());
			et_value.setText(items.get(position).getValue());

			final InterfaceObject obj = items.get(position).getObj();

			et_value.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
					if (arg1 == EditorInfo.IME_ACTION_DONE) {
						try {
							actionEditChanged(obj, arg0.getText().toString());
						} catch (BusException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					return false;
				}
			});

			if (obj.getIf_path().equals("/ControlPanel/SmartPlug/rootContainer/en/MeasureContainer/PowerProperty")
					|| obj.getIf_path().equals(
							"/ControlPanel/SmartPlug/rootContainer/en/MeasureContainer/AccumulateEnergyProperty")) {
				// 스마트 플러그 하드코딩

				btn_up.setEnabled(true);
				btn_down.setEnabled(true);

				String tempVlue = items.get(position).getValue().replace("kWh", "").replace("W", "");

				if (Integer.valueOf(tempVlue) == 0) {
					btn_down.setEnabled(false);
				} else {
					btn_down.setEnabled(true);
				}

				// btn_up.setBackgroundColor(Color.parseColor("#B2EBF4"));
				// btn_down.setBackgroundColor(Color.parseColor("#B2EBF4"));

				ll_btn.setVisibility(View.VISIBLE);
				btn_up.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Log.e("bskim", "btn_up");
						try {
							actionBtnUp(obj);

						} catch (BusException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});

				btn_down.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Log.e("bskim", "btn_down");
						try {
							actionBtnDown(obj);
						} catch (BusException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});

			} else if (obj.getIf_path()
					.equals("/ControlPanel/LgSmartAppliance/rootContainer/en/RefSet/RefEnSet/RefEnSetRRoomCurrentTempDisplay")
					|| obj.getIf_path()
							.equals("/ControlPanel/LgSmartAppliance/rootContainer/en/RefSet/RefEnSet/RefEnSetFRoomCurrentTempDisplay")
					|| obj.getIf_path()
							.equals("/ControlPanel/LgSmartAppliance/rootContainer/en/RefSet/RefEnSet/RefEnSetEnergyMon")
					|| obj.getIf_path()
							.equals("/ControlPanel/LgSmartAppliance/rootContainer/en/RefSet/RefEnSet/RefEnSetDoorStatus")
					|| obj.getIf_path().equals("/ControlPanel/RobotCleaner/rootContainer/en/BatteryLevelProperty")
					|| obj.getIf_path().equals("/ControlPanel/SmartPlug/rootContainer/en/State")
					|| obj.getIf_path().equals(
							"/ControlPanel/LgSmartAppliance/rootContainer/en/OvenSet/OvenKoSet/OvenKoSetSettingCookTemp")) {
				//
				// 냉장고 하드코딩
				btn_up.setEnabled(true);
				btn_down.setEnabled(true);

				ll_btn.setVisibility(View.VISIBLE);
				btn_up.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Log.e("bskim", "btn_up");
						try {
							actionBtnUp(obj);

						} catch (BusException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});

				btn_down.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Log.e("bskim", "btn_down");
						try {
							actionBtnDown(obj);
						} catch (BusException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});

				if (obj.getIf_path()
						.equals("/ControlPanel/LgSmartAppliance/rootContainer/en/RefSet/RefEnSet/RefEnSetRRoomCurrentTempDisplay")
						|| obj.getIf_path()
								.equals("/ControlPanel/LgSmartAppliance/rootContainer/en/RefSet/RefEnSet/RefEnSetFRoomCurrentTempDisplay")
						|| obj.getIf_path().equals(
								"/ControlPanel/LgSmartAppliance/rootContainer/en/OvenSet/OvenKoSet/OvenKoSetSettingCookTemp")) {
					// 냉장고온도
					int nowValue = Integer.valueOf(
							items.get(position).getValue().substring(0, items.get(position).getValue().lastIndexOf("("))
									.replace("℉", "").replace("℃", ""));

					if (nowValue >= Integer.valueOf(obj.getIf_max_value())) {
						btn_up.setEnabled(false);

						// btn_up.setBackgroundColor(Color.parseColor("#EAEAEA"));
						btn_up.setImageResource(android.R.color.transparent);
					} else {
						btn_up.setImageResource(android.R.drawable.arrow_up_float);
					}

					if (nowValue <= Integer.valueOf(obj.getIf_min_value())) {
						btn_down.setEnabled(false);

						// btn_up.setBackgroundColor(Color.parseColor("#EAEAEA"));
						btn_down.setImageResource(android.R.color.transparent);
					} else {
						btn_down.setImageResource(android.R.drawable.arrow_down_float);
					}
				}

				if (obj.getIf_path()
						.equals("/ControlPanel/LgSmartAppliance/rootContainer/en/RefSet/RefEnSet/RefEnSetEnergyMon")) {
					btn_up.setEnabled(true);
					btn_down.setEnabled(true);

					ll_btn.setVisibility(View.VISIBLE);
					btn_up.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// Log.e("bskim", "btn_up");
							try {
								actionBtnUp(obj);

							} catch (BusException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});

					btn_down.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// Log.e("bskim", "btn_down");
							try {
								actionBtnDown(obj);
							} catch (BusException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});
				}

				if (obj.getIf_path()
						.equals("/ControlPanel/LgSmartAppliance/rootContainer/en/RefSet/RefEnSet/RefEnSetDoorStatus")) {
					btn_up.setEnabled(true);
					btn_down.setEnabled(true);

					ll_btn.setVisibility(View.VISIBLE);
					btn_up.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// Log.e("bskim", "btn_up");
							try {
								actionBtnUp(obj);

							} catch (BusException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});

					btn_down.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// Log.e("bskim", "btn_down");
							try {
								actionBtnDown(obj);
							} catch (BusException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});

					if (items.get(position).getValue().equals("Close")) {
						btn_down.setEnabled(false);
						btn_down.setImageResource(android.R.color.transparent);
						btn_up.setImageResource(android.R.drawable.arrow_up_float);
					} else {
						btn_up.setEnabled(false);
						btn_up.setImageResource(android.R.color.transparent);
						btn_down.setImageResource(android.R.drawable.arrow_down_float);
					}
				}

			} else if (obj.getIf_signal().equalsIgnoreCase(BusMapObject.DATA_TYPE_S)) {
				// Log.e("bskim", "non index case" + position);
				btn_up.setEnabled(false);
				btn_down.setEnabled(false);

				// btn_up.setBackgroundColor(Color.parseColor("#EAEAEA"));
				// btn_down.setBackgroundColor(Color.parseColor("#EAEAEA"));
				ll_btn.setVisibility(View.GONE);
				tv_value.setVisibility(View.GONE);
				et_value.setVisibility(View.VISIBLE);

			} else {
				btn_up.setEnabled(true);
				btn_down.setEnabled(true);

				// btn_up.setBackgroundColor(Color.parseColor("#B2EBF4"));
				// btn_down.setBackgroundColor(Color.parseColor("#B2EBF4"));
				ll_btn.setVisibility(View.VISIBLE);
				btn_up.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Log.e("bskim", "btn_up");
						try {
							actionBtnUp(obj);

						} catch (BusException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});

				btn_down.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Log.e("bskim", "btn_down");
						try {
							actionBtnDown(obj);
						} catch (BusException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});

				if (obj.getIf_min_value() != null && obj.getIf_max_value() != null && obj.getIf_max_value().length() > 0
						&& obj.getIf_min_value().length() > 0) {
					int nowIndex = items.get(position).getIndex();
					int maxIndex = Integer.valueOf(obj.getIf_max_value());
					int minIndex = Integer.valueOf(obj.getIf_min_value());

					if (nowIndex >= maxIndex) {
						btn_up.setEnabled(false);

						// btn_up.setBackgroundColor(Color.parseColor("#EAEAEA"));
						btn_up.setImageResource(android.R.color.transparent);

					} else {
						btn_up.setImageResource(android.R.drawable.arrow_up_float);
					}

					if (nowIndex <= minIndex) {
						btn_down.setEnabled(false);
						btn_down.setImageResource(android.R.color.transparent);
					} else {
						btn_down.setImageResource(android.R.drawable.arrow_down_float);
					}
				} else {
					btn_up.setImageResource(android.R.drawable.arrow_up_float);
					btn_down.setImageResource(android.R.drawable.arrow_down_float);
				}
			}

			return convertView;
		}

	}

	public void actionEditChanged(InterfaceObject obj, String text) throws BusException {
		HashMap<String, BusObject> bObjMap = BusConnectionService.busMap.getBusObjMap("" + deviceId);
		PropertyMaker busObj = (PropertyMaker) bObjMap.get(obj.getIf_path());
		busObj.setValue(new Variant(text));
	}

	public void actionBtnUp(InterfaceObject obj) throws BusException {
		ArrayList<InterfaceRangeObject> renge = obj.getInterface_range();
		// BusAttachment tBus = BusConnectionService.busMap.getBus(""+deviceId);
		HashMap<String, BusObject> bObjMap = BusConnectionService.busMap.getBusObjMap("" + deviceId);
		Variant nowValue = DeviceMap.getValue("" + deviceId, obj.getIf_path(), obj.getIf_signal(),
				obj.getIf_default_value());
		if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_Q)
				|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_N)) {
			if (bObjMap == null || !bObjMap.containsKey(obj.getIf_path())) {
				return;
			}

			PropertyMaker busObj = (PropertyMaker) bObjMap.get(obj.getIf_path());
			short sValue = nowValue.getObject(Short.class);
			short max = Short.valueOf(obj.getIf_max_value());
			if (obj.getIf_has_index() == 0) {

				if (max <= sValue) {
					return;
				}

				if (obj.getIf_description().contains("Light Dim Level")) {
					sValue += 5;
					if (sValue > 100) {
						sValue = 100;
					}
				} else {
					sValue += 1;
				}

				busObj.setValue(new Variant(sValue));

			} else {

				// 조명 on/off 하드코딩
				if (obj.getIf_description().equalsIgnoreCase("Stand Light Status")
						|| obj.getIf_description().equalsIgnoreCase("Surface Mount Light Status")
						|| obj.getIf_description().equalsIgnoreCase("Down Light Status")) {
					if (sValue > 0) {
						busObj.setValue(new Variant((short) 0));

					} else {
						busObj.setValue(new Variant((short) 100));

					}

					return;
				}

				if (max <= sValue) {
					return;
				}

				short nextValue = 0;
				boolean it = false;
				for (int i = 0; i < renge.size(); i++) {
					short index = Short.valueOf(renge.get(i).getRange_index());
					if (it) {
						nextValue = index;
						break;
					}

					if (index == sValue) {
						it = true;
					}
				}

				busObj.setValue(new Variant(nextValue));

			}

		} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_B)) {

		} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_D)) {

		} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_I)
				|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_U)) {

		} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_X)
				|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_T)) {
			if (bObjMap == null || !bObjMap.containsKey(obj.getIf_path())) {
				return;
			}

			PropertyMaker busObj = (PropertyMaker) bObjMap.get(obj.getIf_path());
			double sValue = nowValue.getObject(Double.class);
			double max = Double.valueOf(obj.getIf_max_value());
			if (obj.getIf_has_index() == 0) {

				if (max <= sValue || (sValue + 1) > max) {
					return;
				}

				sValue += 1;

				busObj.setValue(new Variant(sValue));

			} else {
				// 인덱스 옵션이 있는데 double 형을 사용할 이유는 없을듯....
				return;
			}
		} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_QQQ)) {

			QQQQPropertyMaker qqqqObj = (QQQQPropertyMaker) bObjMap.get(obj.getIf_path());
			ListQQQQValuesP qqqqValue = qqqqObj.getValue().getObject(ListQQQQValuesP.class);
			ListQQQQValuesC cValue = qqqqValue.sv;
			if (qqqqValue.fv == 0) {
				Log.e("test", " date ");

			} else {
				// Log.e("test", " time ");

				if (cValue.sv > 58) {
					cValue.fv += 1;
					cValue.sv = 0;
				} else {
					cValue.sv += 1;
				}
				qqqqValue.sv = cValue;
				qqqqObj.setValue(new Variant(qqqqValue));
			}

		} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_S)) {
			// s
			// nowValue
			String nValue = nowValue.getObject(String.class);
			if (obj.getIf_path().equals("/ControlPanel/SmartPlug/rootContainer/en/MeasureContainer/PowerProperty")
					|| obj.getIf_path().equals(
							"/ControlPanel/SmartPlug/rootContainer/en/MeasureContainer/AccumulateEnergyProperty")) {
				// 스마트플러그 하드코딩
				int nowTempValue = Integer.valueOf(nValue);
				nowTempValue += 1;

				PropertyMaker busObj = (PropertyMaker) bObjMap.get(obj.getIf_path());
				busObj.setValue(new Variant("" + nowTempValue));

			} else if (obj.getIf_path()
					.equals("/ControlPanel/LgSmartAppliance/rootContainer/en/RefSet/RefEnSet/RefEnSetEnergyMon")
					|| obj.getIf_path()
							.equals("/ControlPanel/LgSmartAppliance/rootContainer/en/RefSet/RefEnSet/RefEnSetRRoomCurrentTempDisplay")
							|| obj.getIf_path().equals(
									"/ControlPanel/LgSmartAppliance/rootContainer/en/RefSet/RefEnSet/RefEnSetFRoomCurrentTempDisplay")
							|| obj.getIf_path().equals(
									"/ControlPanel/LgSmartAppliance/rootContainer/en/OvenSet/OvenKoSet/OvenKoSetSettingCookTemp")) {
				// 스마트플러그 하드코딩 냉장고 하드코딩
				int nowIntValue = Integer.parseInt(nValue);
				int maxIntValue = Integer.parseInt(obj.getIf_max_value());

				if (nowIntValue < maxIntValue) {
					nowIntValue += 1;
					PropertyMaker busObj = (PropertyMaker) bObjMap.get(obj.getIf_path());
					busObj.setValue(new Variant("" + nowIntValue));
				}
			} else if (obj.getIf_path()
					.equals("/ControlPanel/LgSmartAppliance/rootContainer/en/RefSet/RefEnSet/RefEnSetDoorStatus")) {
				PropertyMaker busObj = (PropertyMaker) bObjMap.get(obj.getIf_path());
				busObj.setValue(new Variant("Open"));
			} else if (obj.getIf_path().equals("/ControlPanel/SmartPlug/rootContainer/en/State")) {
				PropertyMaker busObj = (PropertyMaker) bObjMap.get(obj.getIf_path());
				if (nValue.equals("Switch On")) {
					busObj.setValue(new Variant("Switch Off"));
				} else {
					busObj.setValue(new Variant("Switch On"));
				}
			} else if (obj.getIf_path().equals("/ControlPanel/RobotCleaner/rootContainer/en/BatteryLevelProperty")) {
				PropertyMaker busObj = (PropertyMaker) bObjMap.get(obj.getIf_path());
				if (nValue.equals("Low")) {
					busObj.setValue(new Variant("Medium"));
				} else if (nValue.equals("Medium")) {
					busObj.setValue(new Variant("High"));
				} else {
					busObj.setValue(new Variant("Low"));
				}
			}

		}
	}

	public void actionBtnDown(InterfaceObject obj) throws BusException {
		ArrayList<InterfaceRangeObject> renge = obj.getInterface_range();
		// BusAttachment tBus = BusConnectionService.busMap.getBus(""+deviceId);
		HashMap<String, BusObject> bObjMap = BusConnectionService.busMap.getBusObjMap("" + deviceId);

		Variant nowValue = DeviceMap.getValue("" + deviceId, obj.getIf_path(), obj.getIf_signal(),
				obj.getIf_default_value());

		if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_Q)
				|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_N)) {
			if (bObjMap == null || !bObjMap.containsKey(obj.getIf_path())) {
				return;
			}
			PropertyMaker busObj = (PropertyMaker) bObjMap.get(obj.getIf_path());
			short sValue = nowValue.getObject(Short.class);
			short min = Short.valueOf(obj.getIf_min_value());
			if (obj.getIf_has_index() == 0) {

				if (min >= sValue) {
					return;
				}

				if (obj.getIf_description().contains("Light Dim Level")) {
					sValue -= 5;
					if (sValue < 0) {
						sValue = 0;
					}
				} else {
					sValue -= 1;
				}

				busObj.setValue(new Variant(sValue));

			} else {
				// 조명 on/off 하드코딩
				if (obj.getIf_description().equalsIgnoreCase("Stand Light Status")
						|| obj.getIf_description().equalsIgnoreCase("Surface Mount Light Status")
						|| obj.getIf_description().equalsIgnoreCase("Down Light Status")) {
					if (sValue > 0) {
						busObj.setValue(new Variant((short) 0));

					} else {
						busObj.setValue(new Variant((short) 100));

					}

					return;
				}

				if (min >= sValue) {
					return;
				}

				short prevValue = 0;
				for (int i = 0; i < renge.size(); i++) {
					short index = Short.valueOf(renge.get(i).getRange_index());
					if (index == sValue) {
						break;
					}

					prevValue = index;

				}
				busObj.setValue(new Variant(prevValue));

			}
		} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_B)) {

		} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_D)) {

		} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_I)
				|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_U)) {

		} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_X)
				|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_T)) {

		} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_QQQ)) {
			QQQQPropertyMaker qqqqObj = (QQQQPropertyMaker) bObjMap.get(obj.getIf_path());
			ListQQQQValuesP qqqqValue = qqqqObj.getValue().getObject(ListQQQQValuesP.class);
			ListQQQQValuesC cValue = qqqqValue.sv;
			if (qqqqValue.fv == 0) {
				Log.e("test", " date ");
			} else {

				if (cValue.sv == 0 && cValue.fv != 0) {
					cValue.sv = 59;
					cValue.fv -= 1;
				} else if (cValue.sv == 0 && cValue.fv == 0) {
					return;
				} else {
					cValue.sv -= 1;
				}
				qqqqValue.sv = cValue;
				qqqqObj.setValue(new Variant(qqqqValue));
			}
		} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_S)) {
			// s
			// nowValue
			String nValue = nowValue.getObject(String.class);
			if (obj.getIf_path().equals("/ControlPanel/SmartPlug/rootContainer/en/MeasureContainer/PowerProperty")
					|| obj.getIf_path().equals(
							"/ControlPanel/SmartPlug/rootContainer/en/MeasureContainer/AccumulateEnergyProperty")) {
				// 스마트플러그 하드코딩
				int nowTempValue = Integer.valueOf(nValue);
				nowTempValue -= 1;

				PropertyMaker busObj = (PropertyMaker) bObjMap.get(obj.getIf_path());
				busObj.setValue(new Variant("" + nowTempValue));

			} else if (obj.getIf_path().equals("/ControlPanel/SmartPlug/rootContainer/en/State")
					|| obj.getIf_path()
							.equals("/ControlPanel/LgSmartAppliance/rootContainer/en/RefSet/RefEnSet/RefEnSetEnergyMon")
					|| obj.getIf_path()
							.equals("/ControlPanel/LgSmartAppliance/rootContainer/en/RefSet/RefEnSet/RefEnSetRRoomCurrentTempDisplay")
					|| obj.getIf_path()
							.equals("/ControlPanel/LgSmartAppliance/rootContainer/en/RefSet/RefEnSet/RefEnSetFRoomCurrentTempDisplay")
					|| obj.getIf_path().equals(
							"/ControlPanel/LgSmartAppliance/rootContainer/en/OvenSet/OvenKoSet/OvenKoSetSettingCookTemp")) {

				int nowIntValue = Integer.parseInt(nValue);
				int minIntValue = Integer.parseInt(obj.getIf_min_value());

				if (nowIntValue > minIntValue) {
					nowIntValue -= 1;
					PropertyMaker busObj = (PropertyMaker) bObjMap.get(obj.getIf_path());
					busObj.setValue(new Variant("" + nowIntValue));
				}
			} else if (obj.getIf_path()
					.equals("/ControlPanel/LgSmartAppliance/rootContainer/en/RefSet/RefEnSet/RefEnSetDoorStatus")) {
				PropertyMaker busObj = (PropertyMaker) bObjMap.get(obj.getIf_path());
				busObj.setValue(new Variant("Close"));
			} else if (obj.getIf_path().equals("/ControlPanel/SmartPlug/rootContainer/en/State")) {
				PropertyMaker busObj = (PropertyMaker) bObjMap.get(obj.getIf_path());
				if (nValue.equals("Switch On")) {
					busObj.setValue(new Variant("Switch Off"));
				} else {
					busObj.setValue(new Variant("Switch On"));
				}
			} else if (obj.getIf_path().equals("/ControlPanel/RobotCleaner/rootContainer/en/BatteryLevelProperty")) {
				PropertyMaker busObj = (PropertyMaker) bObjMap.get(obj.getIf_path());
				if (nValue.equals("Low")) {
					busObj.setValue(new Variant("High"));
				} else if (nValue.equals("Medium")) {
					busObj.setValue(new Variant("Low"));
				} else {
					busObj.setValue(new Variant("Medium"));
				}
			}

		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (dbAdapter != null) {
			dbAdapter.close();
		}

	}

	@Override
	public void onResume() {
		mActivity = this;
		super.onResume();
	}

	// @Override
	// public void dataChenged(String device_id) {
	//
	// new Handler().postDelayed(new Runnable() {
	//
	// @Override
	// public void run() {
	// Log.v("bskim", "detailactivity dataChenged");
	// reloadData();
	//
	// }
	// }, 5000);
	//
	// }

	public static boolean isNumber(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public int CtoF(int c) {

		return (int) (c * 1.8) + 32;
	}

	public int FtoC(int f) {

		return (int) ((f - 32) / 1.8);
	}

	public double CtoF(double c) {

		return (double) (c * 1.8) + 32;
	}

	public double FtoC(double f) {

		return (double) ((f - 32) / 1.8);
	}

}
