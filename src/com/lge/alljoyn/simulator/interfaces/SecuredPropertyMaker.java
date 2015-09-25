package com.lge.alljoyn.simulator.interfaces;

import java.util.HashMap;
import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.SignalEmitter;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.Position;
import org.alljoyn.ns.NotificationServiceException;

import com.lge.alljoyn.simulator.about.InterfaceObject;
import com.lge.alljoyn.simulator.about.InterfaceRangeObject;
import com.lge.alljoyn.simulator.activity.CommonActivity;
import com.lge.alljoyn.simulator.interfaces.controlpanel.CPPropertySecuredInterface;
import com.lge.alljoyn.simulator.service.BusConnectionService;
import com.lge.alljoyn.simulator.service.BusMapObject;
import com.lge.alljoyn.simulator.utils.DeviceMap;

import android.util.Log;

public class SecuredPropertyMaker implements BusObject, CPPropertySecuredInterface {

	private short VERSION = 1;
	private int STATES = 0x02;
	private String deviceId;

	public final String iName = "org.alljoyn.ControlPanel.Property";
	private String iPath;
	private String unit;
	private String min;
	private String max;
	private String def;
	private RangeValue[] labelArray = null;
	private InterfaceObject obj;

	public SecuredPropertyMaker(InterfaceObject _obj, String _deviceId) {

		this.deviceId = _deviceId;
		this.iPath = _obj.getIf_path();
		this.unit = _obj.getIf_unit();
		this.min = _obj.getIf_min_value();
		this.max = _obj.getIf_max_value();
		this.def = _obj.getIf_default_value();
		this.obj = _obj;
		if (!obj.getIf_signal().equals(BusMapObject.DATA_TYPE_S)) {
			if (obj.getIf_has_index() == 1 && obj.getInterface_range().size() > 0) {
				labelArray = new RangeValue[obj.getInterface_range().size()];
				for (int i = 0; i < obj.getInterface_range().size(); i++) {
					String label = obj.getInterface_range().get(i).getRange_label();
					Variant vValue = null;
					if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_B)) {
						vValue = new Variant(Boolean.valueOf(obj.getInterface_range().get(i).getRange_index()),
								obj.getIf_signal());
					} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_D)) {
						vValue = new Variant(Double.valueOf(obj.getInterface_range().get(i).getRange_index()),
								obj.getIf_signal());
					} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_Q)
							|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_N)) {
						vValue = new Variant(Short.valueOf(obj.getInterface_range().get(i).getRange_index()),
								obj.getIf_signal());
					} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_T)
							|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_X)) {
						vValue = new Variant(Long.valueOf(obj.getInterface_range().get(i).getRange_index()),
								obj.getIf_signal());
					} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_I)
							|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_U)) {
						vValue = new Variant(Integer.valueOf(obj.getInterface_range().get(i).getRange_index()),
								obj.getIf_signal());
					}

					RangeValue lv = new RangeValue(vValue, label);
					labelArray[i] = lv;
				}
			} else if (obj.getIf_min_value() != null && obj.getIf_max_value() != null) {
				int imin = Integer.valueOf(obj.getIf_min_value());
				int imax = Integer.valueOf(obj.getIf_max_value());
				int rag = imax - imin + 1;
				labelArray = new RangeValue[rag];
				int tempIndex = 0;
				for (int i = imin; i <= imax; i++) {
					String label = "" + i;
					Variant vValue = null;
					if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_D)) {
						vValue = new Variant((double) i, obj.getIf_signal());
					} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_Q)
							|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_N)) {
						vValue = new Variant((short) i, obj.getIf_signal());
					} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_T)
							|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_X)) {
						vValue = new Variant((long) i, obj.getIf_signal());
					} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_I)
							|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_U)) {
						vValue = new Variant((int) i, obj.getIf_signal());
					}

					if (obj.getIf_has_index() == 1 && obj.getInterface_range().size() > 0) {
						for (int k = 0; k < obj.getInterface_range().size(); k++) {
							if (tempIndex == Integer.valueOf(obj.getInterface_range().get(k).getRange_index())) {
								label = obj.getInterface_range().get(k).getRange_label();
							}
						}
					}

					RangeValue lv = new RangeValue(vValue, label);
					labelArray[tempIndex] = lv;
					tempIndex += 1;
				}
			}
		}

		if (obj.getIf_writable() == 0) {
			this.STATES = 0x01;
		} else {
			this.STATES = 0x03;
		}
	}

	public class Values2 {
		@Position(0)
		public Variant min;
		@Position(1)
		public Variant max;
		@Position(2)
		public Variant increment;

		public Values2(Variant _min, Variant _max, Variant _increment) {
			this.min = _min;
			this.max = _max;
			this.increment = _increment;
		}

	}

	@Override
	public short getVersion() throws BusException {
		// TODO Auto-generated method stub
		return VERSION;
	}

	@Override
	public int getStates() throws BusException {
		// TODO Auto-generated method stub
		return STATES;
	}

	@Override
	public Map<Short, Variant> getOptParams() throws BusException {
		Map<Short, Variant> test = new HashMap<Short, Variant>();

		test.put((short) 0,
				new Variant(iPath.substring(iPath.lastIndexOf("/"), iPath.length()).replace("/", "") + " : "));
		test.put((short) 1, new Variant(0x008000));
		test.put((short) 2, new Variant(new short[] { (short) obj.getIf_ui_type() }));
		if (unit != null) {
			test.put((short) 3, new Variant(unit));
		} else {
			test.put((short) 3, new Variant(""));
		}

		if (!obj.getIf_signal().equals(BusMapObject.DATA_TYPE_S)) {
			if (labelArray != null) {
				test.put((short) 4, new Variant(labelArray));
			}

			// range

			if (min != null && max != null) {
				Variant vMin = null;
				Variant vMax = null;
				Variant inc = null;
				if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_D)) {
					vMin = new Variant(Double.valueOf(min), obj.getIf_signal());
					vMax = new Variant(Double.valueOf(max), obj.getIf_signal());
					inc = new Variant((double) 1, obj.getIf_signal());
				} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_Q)
						|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_N)) {
					vMin = new Variant(Short.valueOf(min), obj.getIf_signal());
					vMax = new Variant(Short.valueOf(max), obj.getIf_signal());
					inc = new Variant((short) 1, obj.getIf_signal());
				} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_X)
						|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_T)) {
					vMin = new Variant(Long.valueOf(min), obj.getIf_signal());
					vMax = new Variant(Long.valueOf(max), obj.getIf_signal());
					inc = new Variant((long) 1, obj.getIf_signal());
				} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_I)
						|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_U)) {
					vMin = new Variant(Integer.valueOf(min), obj.getIf_signal());
					vMax = new Variant(Integer.valueOf(max), obj.getIf_signal());
					inc = new Variant((int) 1, obj.getIf_signal());
				}
				test.put((short) 5, new Variant(new Values2(vMin, vMax, inc)));

			}
		}

		return test;
	}

	@Override
	public Variant getValue() throws BusException {
		Log.e(iPath, "getValue");
		Log.e("getValue", "" + DeviceMap.getValue(deviceId, iPath, obj.getIf_signal(), def));
		return DeviceMap.getValue(deviceId, iPath, obj.getIf_signal(), def);
	}

	@Override
	public void setValue(Variant value) throws BusException {
		Log.e(iPath, "setValue");
		if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_D)) {
			DeviceMap.setValue(deviceId, iPath, value.getObject(Double.class), obj.getIf_signal());
		} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_B)) {
			DeviceMap.setValue(deviceId, iPath, value.getObject(Boolean.class), obj.getIf_signal());
		} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_S)) {
			DeviceMap.setValue(deviceId, iPath, value.getObject(String.class), obj.getIf_signal());
		} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_Q)
				|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_N)) {
			DeviceMap.setValue(deviceId, iPath, value.getObject(Short.class), obj.getIf_signal());
		} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_X)
				|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_T)) {
			DeviceMap.setValue(deviceId, iPath, value.getObject(Long.class), obj.getIf_signal());
		} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_I)
				|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_U)) {
			DeviceMap.setValue(deviceId, iPath, value.getObject(Integer.class), obj.getIf_signal());
		}

		ValueChanged(value);
	}

	@BusSignal(name = "ValueChanged")
	// @BusSignalHandler(iface="org.alljoyn.About", signal="Announce")
	@Override
	public void ValueChanged(Variant newValue) throws BusException {
		Log.e("ValueChanged", "ValueChanged : " + iPath);

		if (BusConnectionService.joinerInfo != null && !BusConnectionService.joinerInfo.isEmpty()
				&& BusConnectionService.joinerInfo.containsKey(deviceId)
				&& BusConnectionService.joinerInfo.get(deviceId) != null
				&& !BusConnectionService.joinerInfo.get(deviceId).isEmpty()) {
			for (String sKey : BusConnectionService.joinerInfo.get(deviceId).keySet()) {
				SignalEmitter emitter = new SignalEmitter(this, sKey,
						BusConnectionService.joinerInfo.get(deviceId).get(sKey), SignalEmitter.GlobalBroadcast.On);

				CPPropertySecuredInterface sss = emitter.getInterface(CPPropertySecuredInterface.class);
				sss.ValueChanged(newValue);
			}

			if (obj.getIf_noti_flag() == 1) {
				String msg = obj.getIf_description();
				String qv = "";
				if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_B)) {
					qv = "" + DeviceMap.getValue(deviceId, iPath, obj.getIf_signal(), obj.getIf_default_value())
							.getObject(Boolean.class);
				} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_D)) {
					qv = "" + DeviceMap.getValue(deviceId, iPath, obj.getIf_signal(), obj.getIf_default_value())
							.getObject(Double.class);
				} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_S)) {
					qv = "" + DeviceMap.getValue(deviceId, iPath, obj.getIf_signal(), obj.getIf_default_value())
							.getObject(String.class);
				} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_Q)
						|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_N)) {
					qv = "" + DeviceMap.getValue(deviceId, iPath, obj.getIf_signal(), obj.getIf_default_value())
							.getObject(Short.class);
				} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_X)
						|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_T)) {
					qv = "" + DeviceMap.getValue(deviceId, iPath, obj.getIf_signal(), obj.getIf_default_value())
							.getObject(Long.class);
				} else if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_I)
						|| obj.getIf_signal().equals(BusMapObject.DATA_TYPE_U)) {
					qv = "" + DeviceMap.getValue(deviceId, iPath, obj.getIf_signal(), obj.getIf_default_value())
							.getObject(Integer.class);
				}

				//
				if (obj.getIf_has_index() == 1) {
					for (int i = 0; i < obj.getInterface_range().size(); i++) {
						InterfaceRangeObject ro = obj.getInterface_range().get(i);
						if (ro.getRange_index().equalsIgnoreCase(qv)) {
							msg = msg + " " + ro.getRange_label();
							break;
						}
					}
				} else {
					msg = msg + " " + qv;
				}

				if (iPath.contains("/ControlPanel/SmartPlug/rootContainer/en/State")) {
					// Log.e("bskim", "2222");
					msg = DeviceMap.getValue(deviceId, iPath, obj.getIf_signal(), def).getObject(String.class);

					if (BusConnectionService.notificationServiceObject != null
							&& BusConnectionService.notificationServiceObject.isStartNoti()) {
						try {
							BusConnectionService.notificationServiceObject.sendNoti(msg);
						} catch (NotificationServiceException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					CommonActivity.mActivity.dataChenged("" + deviceId, DeviceMap.FLAG_CHANGED_DATA);

					return;

				} else if (iPath.contains("RefrigeratorIcePlusStatusValue")) {
					if (qv.equalsIgnoreCase("2")) {
						msg = "Ice Plus Start";

						Log.e("noti", "deviceId=" + deviceId);
						if (BusConnectionService.notificationServiceObject != null
								&& BusConnectionService.notificationServiceObject.isStartNoti()) {
							try {
								BusConnectionService.notificationServiceObject.sendNoti(msg);
							} catch (NotificationServiceException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						CommonActivity.mActivity.dataChenged("" + deviceId, DeviceMap.FLAG_CHANGED_DATA);

						return;
					}

				} else if (iPath.contains("RefrigeratorDoorOpenStatusValue")) {
					if (qv.equalsIgnoreCase("1")) {
						msg = "Door Open";

						Log.e("noti", "deviceId=" + deviceId);
						if (BusConnectionService.notificationServiceObject != null
								&& BusConnectionService.notificationServiceObject.isStartNoti()) {
							try {
								BusConnectionService.notificationServiceObject.sendNoti(msg);
							} catch (NotificationServiceException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						CommonActivity.mActivity.dataChenged("" + deviceId, DeviceMap.FLAG_CHANGED_DATA);

						return;
					}
				}

				if (BusConnectionService.notificationServiceObject != null
						&& BusConnectionService.notificationServiceObject.isStartNoti()) {
					try {
						BusConnectionService.notificationServiceObject.sendNoti(msg);
					} catch (NotificationServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

		}
		CommonActivity.mActivity.dataChenged("" + deviceId, DeviceMap.FLAG_CHANGED_DATA);
	}

	@Override
	public void MetadataChanged() throws BusException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return iPath;
	}

}
