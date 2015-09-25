package com.lge.alljoyn.simulator.interfaces;

import java.util.HashMap;
import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusSignal;

import com.lge.alljoyn.simulator.about.InterfaceObject;
import com.lge.alljoyn.simulator.about.InterfaceRangeObject;
import com.lge.alljoyn.simulator.activity.CommonActivity;
import com.lge.alljoyn.simulator.interfaces.controlpanel.CPActionInterface;
import com.lge.alljoyn.simulator.service.BusConnectionService;
import com.lge.alljoyn.simulator.service.BusMapObject;
import com.lge.alljoyn.simulator.utils.DeviceMap;

import android.util.Log;

public class ActionMaker implements BusObject, CPActionInterface {

	private short VERSION = 1;
	private int STATES = 0x01;

	public final String iName = "org.alljoyn.ControlPanel.Action";
	private String iPath;
	private String propertyKey;
	private String signalType;
	private InterfaceObject obj;
	private String deviceId;

	public ActionMaker(InterfaceObject _obj, String _propertyKey, String _deviceId) {
		this.obj = _obj;
		this.iPath = _obj.getIf_path();
		this.propertyKey = _propertyKey;
		this.signalType = _obj.getIf_signal();
		this.deviceId = _deviceId;

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
		if (isNumber(obj.getIf_default_value()) || obj.getIf_default_value().contains("+1")) {
			test.put((short) 0, new Variant(iPath.substring(iPath.lastIndexOf("/"), iPath.length()).replace("/", "")));
		} else {
			test.put((short) 0, new Variant(obj.getIf_default_value()));
		}
		
		test.put((short) 1, new Variant(0x400));
		test.put((short) 2, new Variant(new short[] { 1 }));
		return test;

	}

	@Override
	public void Exec() throws BusException {
		// TODO Auto-generated method stub
		// Log.e("bskim", "Exec() : " + iPath);
		if (propertyKey != null) {
			Variant variant = new Variant("");

			if (signalType.equals(BusMapObject.DATA_TYPE_Q) || signalType.equals(BusMapObject.DATA_TYPE_N)) {
				short value = Short.valueOf(obj.getIf_default_value());
				Log.e("bskim", "Short value chaged : " + value);
				if (isNumber("" + value) && obj.getIf_default_value().contains("+")
						|| obj.getIf_default_value().contains("-")) {
					short nowValue = DeviceMap
							.getValue(deviceId, propertyKey, obj.getIf_signal(), obj.getIf_default_value())
							.getObject(Short.class);
					short dValue = (short) (nowValue + Short.valueOf(obj.getIf_default_value()));
					variant = new Variant(dValue);
				} else {
					variant = new Variant(value, signalType);
				}

				DeviceMap.setValue(deviceId, propertyKey, value, signalType);
			}

			if (signalType.equals(BusMapObject.DATA_TYPE_S)) {

				String value = obj.getIf_default_value();
				if (isNumber(value) && value.contains("+") || value.contains("-")) {

					int nowValue = Integer.valueOf(DeviceMap.getValue(deviceId, propertyKey, obj.getIf_signal(), value)
							.getObject(String.class));
					int dValue = nowValue + Integer.valueOf(value);
					variant = new Variant("" + dValue);
				} else {
					Log.e("bskim", "string value chaged : " + value);
					variant = new Variant(value);

				}
				DeviceMap.setValue(deviceId, propertyKey, value, obj.getIf_signal());

			}

			if (signalType.equals(BusMapObject.DATA_TYPE_I) || signalType.equals(BusMapObject.DATA_TYPE_U)) {
				int value = Integer.valueOf(obj.getIf_default_value());
				if (isNumber("" + value) && obj.getIf_default_value().contains("+")
						|| obj.getIf_default_value().contains("-")) {
					int nowValue = DeviceMap
							.getValue(deviceId, propertyKey, obj.getIf_signal(), obj.getIf_default_value())
							.getObject(Integer.class);
					int dValue = (nowValue + Integer.valueOf(obj.getIf_default_value()));
					variant = new Variant(dValue);
				} else {
					variant = new Variant(value, signalType);
				}
				DeviceMap.setValue(deviceId, propertyKey, value, signalType);
			}

			if (signalType.equals(BusMapObject.DATA_TYPE_D)) {
				double value = Double.valueOf(obj.getIf_default_value());
				if (isNumber("" + value) && obj.getIf_default_value().contains("+")
						|| obj.getIf_default_value().contains("-")) {
					double nowValue = DeviceMap
							.getValue(deviceId, propertyKey, obj.getIf_signal(), obj.getIf_default_value())
							.getObject(Double.class);
					double dValue = (nowValue + Double.valueOf(obj.getIf_default_value()));
					variant = new Variant(dValue);
				} else {
					variant = new Variant(value);
				}
				DeviceMap.setValue(deviceId, propertyKey, value, obj.getIf_signal());
			}

			if (signalType.equals(BusMapObject.DATA_TYPE_X) || signalType.equals(BusMapObject.DATA_TYPE_T)) {
				long value = Long.valueOf(obj.getIf_default_value());
				if (isNumber("" + value) && obj.getIf_default_value().contains("+")
						|| obj.getIf_default_value().contains("-")) {
					long nowValue = DeviceMap
							.getValue(deviceId, propertyKey, obj.getIf_signal(), obj.getIf_default_value())
							.getObject(Long.class);
					long dValue = (nowValue + Long.valueOf(obj.getIf_default_value()));
					variant = new Variant(dValue);
				} else {
					variant = new Variant(value, signalType);
				}
				DeviceMap.setValue(deviceId, propertyKey, value, signalType);
			}

			if (signalType.equals(BusMapObject.DATA_TYPE_B)) {

				boolean value = Boolean.valueOf(obj.getIf_default_value());
				variant = new Variant(value);
				DeviceMap.setValue(deviceId, propertyKey, value, obj.getIf_signal());
			}

			if (BusConnectionService.joinerInfo.containsKey(deviceId)) {

				HashMap<String, BusObject> bObjMap = BusConnectionService.busMap.getBusObjMap(deviceId);
				if (bObjMap != null && bObjMap.containsKey(propertyKey)) {
					if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_QQQ)) {
						QQQQPropertyMaker busObj = (QQQQPropertyMaker) bObjMap.get(propertyKey);
						busObj.ValueChanged(variant);
					} else {
						PropertyMaker busObj = (PropertyMaker) bObjMap.get(propertyKey);
						busObj.ValueChanged(variant);
					}
				}

				if (obj.getIf_noti_flag() == 1) {
					String msg = obj.getIf_description() + " ";

					if (signalType.equals(BusMapObject.DATA_TYPE_S)) {
						msg = msg + DeviceMap.getValue(deviceId, propertyKey, signalType, obj.getIf_default_value())
								.getObject(String.class);
					}

					if (signalType.equals(BusMapObject.DATA_TYPE_Q) || signalType.equals(BusMapObject.DATA_TYPE_N)) {
						short qv = DeviceMap.getValue(deviceId, propertyKey, signalType, obj.getIf_default_value())
								.getObject(Short.class);
						if (obj.getIf_has_index() == 1) {
							for (int i = 0; i < obj.getInterface_range().size(); i++) {
								InterfaceRangeObject ro = obj.getInterface_range().get(i);
								if (ro.getRange_index().equalsIgnoreCase("" + qv)) {
									msg = msg + ro.getRange_label();
									break;
								}
							}
						} else {
							msg = msg + qv;
						}

					}

					if (signalType.equals(BusMapObject.DATA_TYPE_I) || signalType.equals(BusMapObject.DATA_TYPE_U)) {
						int iv = DeviceMap.getValue(deviceId, propertyKey, signalType, obj.getIf_default_value())
								.getObject(Integer.class);
						if (obj.getIf_has_index() == 1) {
							for (int i = 0; i < obj.getInterface_range().size(); i++) {
								InterfaceRangeObject ro = obj.getInterface_range().get(i);
								if (ro.getRange_index().equalsIgnoreCase("" + iv)) {
									msg = msg + ro.getRange_label();
									break;
								}
							}
						} else {
							msg = msg + iv;
						}
					}

					if (signalType.equals(BusMapObject.DATA_TYPE_X) || signalType.equals(BusMapObject.DATA_TYPE_T)) {
						long iv = DeviceMap.getValue(deviceId, propertyKey, signalType, obj.getIf_default_value())
								.getObject(Long.class);
						if (obj.getIf_has_index() == 1) {
							for (int i = 0; i < obj.getInterface_range().size(); i++) {
								InterfaceRangeObject ro = obj.getInterface_range().get(i);
								if (ro.getRange_index().equalsIgnoreCase("" + iv)) {
									msg = msg + ro.getRange_label();
									break;
								}
							}
						} else {
							msg = msg + iv;
						}
					}

					if (signalType.equals(BusMapObject.DATA_TYPE_D)) {
						double iv = DeviceMap.getValue(deviceId, propertyKey, signalType, obj.getIf_default_value())
								.getObject(Double.class);
						if (obj.getIf_has_index() == 1) {
							for (int i = 0; i < obj.getInterface_range().size(); i++) {
								InterfaceRangeObject ro = obj.getInterface_range().get(i);
								if (ro.getRange_index().equalsIgnoreCase("" + iv)) {
									msg = msg + ro.getRange_label();
									break;
								}
							}
						} else {
							msg = msg + iv;
						}
					}

					if (signalType.equals(BusMapObject.DATA_TYPE_B)) {
						boolean bv = DeviceMap.getValue(deviceId, propertyKey, signalType, obj.getIf_default_value())
								.getObject(Boolean.class);
						if (obj.getIf_has_index() == 1) {
							for (int i = 0; i < obj.getInterface_range().size(); i++) {
								InterfaceRangeObject ro = obj.getInterface_range().get(i);
								if (ro.getRange_index().equalsIgnoreCase("" + bv)) {
									msg = msg + ro.getRange_label();
									break;
								}
							}
						} else {
							msg = msg + bv;
						}
					}

					

				}

				CommonActivity.mActivity.dataChenged("" + deviceId, DeviceMap.FLAG_CHANGED_DATA);
			}
		}

	}

	@BusSignal(name = "MetadataChanged")
	@Override
	public void MetadataChanged() throws BusException {
		// TODO Auto-generated method stub

	}

	public static boolean isNumber(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
