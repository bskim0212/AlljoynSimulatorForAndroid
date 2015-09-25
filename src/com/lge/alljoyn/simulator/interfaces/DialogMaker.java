package com.lge.alljoyn.simulator.interfaces;

import java.util.HashMap;
import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.ns.NotificationServiceException;

import com.lge.alljoyn.simulator.about.InterfaceObject;
import com.lge.alljoyn.simulator.about.InterfaceRangeObject;
import com.lge.alljoyn.simulator.activity.CommonActivity;
import com.lge.alljoyn.simulator.interfaces.controlpanel.CPDialogInterface;
import com.lge.alljoyn.simulator.service.BusConnectionService;
import com.lge.alljoyn.simulator.service.BusMapObject;
import com.lge.alljoyn.simulator.utils.DeviceMap;

import android.util.Log;

public class DialogMaker implements BusObject, CPDialogInterface {

	private short VERSION = 1;
	private int STATES = 0x01;

	public final String iName = "org.alljoyn.ControlPanel.Dialog";
	private String iPath;
	private String propertyKey;
	private String signalType;
	private InterfaceObject obj;
	private String deviceId;
	private short actionCount = 0;

	public DialogMaker(InterfaceObject _obj, String _propertyKey, String _deviceId) {
		this.obj = _obj;
		this.iPath = _obj.getIf_path();
		this.propertyKey = _propertyKey;
		this.signalType = _obj.getIf_signal();
		this.deviceId = _deviceId;
		if (_obj.getIf_dialog_button1() != null && obj.getIf_dialog_button1().length() > 0) {
			actionCount = (short) (actionCount + 1);
		}

		if (_obj.getIf_dialog_button2() != null && obj.getIf_dialog_button2().length() > 0) {
			actionCount = (short) (actionCount + 1);
		}

		if (_obj.getIf_dialog_button3() != null && obj.getIf_dialog_button3().length() > 0) {
			actionCount = (short) (actionCount + 1);
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
		test.put((short) 1, new Variant(0x400));
		test.put((short) 2, new Variant(new short[] { 1 }));
		if (obj.getIf_dialog_button1() != null && obj.getIf_dialog_button1().length() > 0) {
			test.put((short) 6, new Variant(obj.getIf_dialog_button1()));
		}
		if (obj.getIf_dialog_button2() != null && obj.getIf_dialog_button2().length() > 0) {
			test.put((short) 7, new Variant(obj.getIf_dialog_button2()));
		}
		if (obj.getIf_dialog_button3() != null && obj.getIf_dialog_button3().length() > 0) {
			test.put((short) 8, new Variant(obj.getIf_dialog_button3()));
		}
		return test;
	}

	@Override
	public String getMessage() throws BusException {
		// TODO Auto-generated method stub
		Log.e("DialogMaker", "getMessage");
		return obj.getIf_dialog_msg();
	}

	@Override
	public short getNumActions() throws BusException {
		// TODO Auto-generated method stub
		return actionCount;
	}

	@BusMethod(name = "Action1")
	@Override
	public void Action1() throws BusException {
		// TODO Auto-generated method stub
		Log.e("bskim", "Dialog Action1 " + propertyKey);
		if (propertyKey != null && obj.getIf_dialog_action1() != null && obj.getIf_dialog_action1().length() > 0) {
			//
			if (obj.getIf_path().equals(
					"/ControlPanel/LgSmartAppliance/rootContainer/en/WasherSet/WasherEnSet/WasherEnSetOperationStartOrPuase/WasherEnSetOperationStartOrPuaseConfirm")) {
				// 세탁기 하드코딩
				String nowValue = DeviceMap
						.getValue(deviceId, propertyKey, obj.getIf_signal(), obj.getIf_dialog_action1())
						.getObject(String.class);
				if (nowValue.equals("Power Off") || nowValue.equals("Puase")) {
					actionDialog("Start");
				} else {
					actionDialog("Puase");
				}
			} else {
				actionDialog(obj.getIf_dialog_action1());
			}

		}
	}

	@BusMethod(name = "Action2")
	@Override
	public void Action2() throws BusException {
		Log.e("bskim", "Dialog Action2 " + propertyKey);
		if (propertyKey != null && obj.getIf_dialog_action2() != null && obj.getIf_dialog_action2().length() > 0) {
			actionDialog(obj.getIf_dialog_action2());

		}

	}

	@BusMethod(name = "Action3")
	@Override
	public void Action3() throws BusException {
		Log.e("bskim", "Dialog Action3 " + propertyKey);
		if (propertyKey != null && obj.getIf_dialog_action3() != null && obj.getIf_dialog_action3().length() > 0) {
			actionDialog(obj.getIf_dialog_action3());

		}

	}

	@Override
	public void MetadataChanged() throws BusException {
		// TODO Auto-generated method stub

	}

	private void actionDialog(String action) throws BusException {
		Variant variant = new Variant("");

		if (signalType.equals(BusMapObject.DATA_TYPE_Q) || signalType.equals(BusMapObject.DATA_TYPE_N)) {
			short value = Short.valueOf(action);
			// Log.e("bskim", "Short value chaged : " + value);
			if (action.contains("+") || action.contains("-")) {
				short nowValue = DeviceMap.getValue(deviceId, propertyKey, obj.getIf_signal(), action)
						.getObject(Short.class);
				short dValue = (short) (nowValue + Short.valueOf(action));
				variant = new Variant(dValue);
			} else {
				variant = new Variant(value, signalType);
			}
			DeviceMap.setValue(deviceId, propertyKey, value, signalType);
		}

		if (signalType.equals(BusMapObject.DATA_TYPE_S)) {
			Log.e("bskim", "value : " + action);
			String value = action;
			if (value.contains("0+")) {
				value = value.replace("0+", "+");
			}
			if (isNumber(value) && value.contains("+") || value.contains("-")) {
				Log.e("bskim", "+- string value chaged : " + value);
				value = value.replace("+", "");
				int nowValue = Integer.valueOf(
						DeviceMap.getValue(deviceId, propertyKey, obj.getIf_signal(), value).getObject(String.class));
				Log.e("bskim", "now value : " + nowValue);
				int dValue = nowValue + Integer.valueOf(value);
				variant = new Variant("" + dValue);
				value = "" + dValue;
				Log.e("bskim", "change value : " + value);
			} else {
				Log.e("bskim", "string value chaged : " + value);
				variant = new Variant(value);

			}
			DeviceMap.setValue(deviceId, propertyKey, value, obj.getIf_signal());
		}

		if (signalType.equals(BusMapObject.DATA_TYPE_I) || signalType.equals(BusMapObject.DATA_TYPE_U)) {
			int value = Integer.valueOf(action);
			if (action.contains("+") || action.contains("-")) {
				int nowValue = DeviceMap.getValue(deviceId, propertyKey, obj.getIf_signal(), action)
						.getObject(Integer.class);
				int dValue = (nowValue + Integer.valueOf(action));
				variant = new Variant(dValue);
			} else {
				variant = new Variant(value, signalType);
			}
			DeviceMap.setValue(deviceId, propertyKey, value, signalType);
		}

		if (signalType.equals(BusMapObject.DATA_TYPE_B)) {

			boolean value = Boolean.valueOf(action);
			variant = new Variant(value);
			DeviceMap.setValue(deviceId, propertyKey, value, obj.getIf_signal());
		}

		if (signalType.equals(BusMapObject.DATA_TYPE_D)) {

			double value = Double.valueOf(action);
			if (action.contains("+") || action.contains("-")) {
				double nowValue = DeviceMap.getValue(deviceId, propertyKey, obj.getIf_signal(), action)
						.getObject(Double.class);
				double dValue = (nowValue + Double.valueOf(action));
				variant = new Variant(dValue);
			} else {
				variant = new Variant(value);
			}
			DeviceMap.setValue(deviceId, propertyKey, value, obj.getIf_signal());
		}

		if (signalType.equals(BusMapObject.DATA_TYPE_X) || signalType.equals(BusMapObject.DATA_TYPE_T)) {

			long value = Long.valueOf(action);
			if (action.contains("+") || action.contains("-")) {
				long nowValue = DeviceMap.getValue(deviceId, propertyKey, obj.getIf_signal(), action)
						.getObject(Long.class);
				long dValue = (nowValue + Long.valueOf(action));
				variant = new Variant(dValue);
			} else {
				variant = new Variant(value, signalType);
			}
			DeviceMap.setValue(deviceId, propertyKey, value, signalType);
		}

		HashMap<String, BusObject> bObjMap = BusConnectionService.busMap.getBusObjMap(deviceId);
		if (bObjMap != null && bObjMap.containsKey(propertyKey)) {
			if (obj.getIf_signal().equals(BusMapObject.DATA_TYPE_QQQ)) {
				QQQQPropertyMaker busObj = (QQQQPropertyMaker) bObjMap.get(propertyKey);
				busObj.ValueChanged(variant);
			} else {
				PropertyMaker busObj = (PropertyMaker) bObjMap.get(propertyKey);
				busObj.ValueChanged(variant);
			}

			CommonActivity.mActivity.dataChenged("" + deviceId, DeviceMap.FLAG_CHANGED_DATA);

			sendNoti(action);

		}

	}

	private void sendNoti(String action) throws BusException {
//		if (obj.getIf_noti_flag() == 1) {
//			String msg = obj.getIf_description() + " ";
//
//			if (signalType.equals(BusMapObject.DATA_TYPE_S)) {
//				msg = msg + DeviceMap.getValue(deviceId, propertyKey, signalType, action).getObject(String.class);
//			}
//
//			if (signalType.equals(BusMapObject.DATA_TYPE_Q) || signalType.equals(BusMapObject.DATA_TYPE_N)) {
//				short qv = DeviceMap.getValue(deviceId, propertyKey, signalType, action).getObject(Short.class);
//				//
//				if (obj.getIf_has_index() == 1) {
//					for (int i = 0; i < obj.getInterface_range().size(); i++) {
//						InterfaceRangeObject ro = obj.getInterface_range().get(i);
//						if (ro.getRange_index().equalsIgnoreCase("" + qv)) {
//							msg = msg + ro.getRange_label();
//							break;
//						}
//					}
//				} else {
//					msg = msg + qv;
//				}
//			}
//
//			if (signalType.equals(BusMapObject.DATA_TYPE_I) || signalType.equals(BusMapObject.DATA_TYPE_U)) {
//				int iv = DeviceMap.getValue(deviceId, propertyKey, signalType, action).getObject(Integer.class);
//				if (obj.getIf_has_index() == 1) {
//					for (int i = 0; i < obj.getInterface_range().size(); i++) {
//						InterfaceRangeObject ro = obj.getInterface_range().get(i);
//						if (ro.getRange_index().equalsIgnoreCase("" + iv)) {
//							msg = msg + ro.getRange_label();
//							break;
//						}
//					}
//				} else {
//					msg = msg + iv;
//				}
//			}
//
//			if (signalType.equals(BusMapObject.DATA_TYPE_B)) {
//				boolean bv = DeviceMap.getValue(deviceId, propertyKey, BusMapObject.DATA_TYPE_B, action)
//						.getObject(Boolean.class);
//				if (obj.getIf_has_index() == 1) {
//					for (int i = 0; i < obj.getInterface_range().size(); i++) {
//						InterfaceRangeObject ro = obj.getInterface_range().get(i);
//						if (ro.getRange_index().equalsIgnoreCase("" + bv)) {
//							msg = msg + ro.getRange_label();
//							break;
//						}
//					}
//				} else {
//					msg = msg + bv;
//				}
//			}
//
//			if (signalType.equals(BusMapObject.DATA_TYPE_D)) {
//				double iv = DeviceMap.getValue(deviceId, propertyKey, BusMapObject.DATA_TYPE_D, action)
//						.getObject(Double.class);
//				if (obj.getIf_has_index() == 1) {
//					for (int i = 0; i < obj.getInterface_range().size(); i++) {
//						InterfaceRangeObject ro = obj.getInterface_range().get(i);
//						if (ro.getRange_index().equalsIgnoreCase("" + iv)) {
//							msg = msg + ro.getRange_label();
//							break;
//						}
//					}
//				} else {
//					msg = msg + iv;
//				}
//			}
//
//			if (signalType.equals(BusMapObject.DATA_TYPE_X) || signalType.equals(BusMapObject.DATA_TYPE_T)) {
//				long iv = DeviceMap.getValue(deviceId, propertyKey, signalType, action).getObject(Long.class);
//				if (obj.getIf_has_index() == 1) {
//					for (int i = 0; i < obj.getInterface_range().size(); i++) {
//						InterfaceRangeObject ro = obj.getInterface_range().get(i);
//						if (ro.getRange_index().equalsIgnoreCase("" + iv)) {
//							msg = msg + ro.getRange_label();
//							break;
//						}
//					}
//				} else {
//					msg = msg + iv;
//				}
//			}
//
//			if (BusConnectionService.notificationServiceObject != null
//					&& BusConnectionService.notificationServiceObject.isStartNoti()) {
//				try {
//					BusConnectionService.notificationServiceObject.sendNoti(msg);
//				} catch (NotificationServiceException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			}
//
//		}
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
