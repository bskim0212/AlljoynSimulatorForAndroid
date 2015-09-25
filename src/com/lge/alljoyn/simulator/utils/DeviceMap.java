package com.lge.alljoyn.simulator.utils;

import java.util.HashMap;

import org.alljoyn.bus.Variant;

import com.lge.alljoyn.simulator.interfaces.DeviceValueObject;
import com.lge.alljoyn.simulator.service.BusMapObject;

import android.util.Log;

public class DeviceMap {

	public static final String IK_START_TYPE = "start_type";
	public static final String IK_DEVICE_ID = "device_id";

	public static final int FLAG_CHANGED_DATA = 0;
	public static final int FLAG_CONNECTION_SUCCESS = 1;
	public static final int FLAG_CONNECTION_FAIL = 2;

	public static HashMap<String, HashMap<String, DeviceValueObject>> sInterfaceeMap;
	

	public static Variant getValue(String device_id, String ipath, String unit, String def) {

		if (sInterfaceeMap == null) {
			// Log.e("bskim", "sInterfaceeMap is null");
			sInterfaceeMap = new HashMap<String, HashMap<String, DeviceValueObject>>();
		}

		if (!sInterfaceeMap.containsKey(device_id) || sInterfaceeMap.get(device_id) == null
				|| !sInterfaceeMap.get(device_id).containsKey(ipath)) {
			// Log.e("bskim", "최초호출");
			if (unit.equals(BusMapObject.DATA_TYPE_Q) || unit.equals(BusMapObject.DATA_TYPE_N)) {
				setValue(device_id, ipath, Short.parseShort(def), unit);
			} else if (unit.equals(BusMapObject.DATA_TYPE_I) || unit.equals(BusMapObject.DATA_TYPE_U)) {
				setValue(device_id, ipath, Integer.valueOf(def), unit);
			} else if (unit.equals(BusMapObject.DATA_TYPE_B)) {
				setValue(device_id, ipath, Boolean.valueOf(def), unit);
			} else if (unit.equals(BusMapObject.DATA_TYPE_D)) {
				setValue(device_id, ipath, Double.valueOf(def), unit);
			} else if (unit.equals(BusMapObject.DATA_TYPE_X) || unit.equals(BusMapObject.DATA_TYPE_T)) {
				setValue(device_id, ipath, Long.valueOf(def), unit);
			} else if (unit.equals(BusMapObject.DATA_TYPE_QQQ) && def.contains(BusMapObject.SEPARATOR)) {
				String[] defArr = def.split(BusMapObject.SEPARATOR);
				setValue(device_id, ipath, new short[] { Short.valueOf(defArr[0]), Short.valueOf(defArr[1]),
						Short.valueOf(defArr[2]), Short.valueOf(defArr[3]) }, unit);
			} else {
				setValue(device_id, ipath, def, unit);
			}

		}

		
		DeviceValueObject dvo = (DeviceValueObject) sInterfaceeMap.get(device_id).get(ipath);
		
		return dvo.getValue(unit);
	}

	public static void setValue(String device_id, String ipath, String value, String valueType) {
		if (!sInterfaceeMap.containsKey(device_id)) {
			DeviceValueObject dvoTemp = new DeviceValueObject(valueType);
			dvoTemp.setValue(value);
			HashMap<String, DeviceValueObject> mapTemp = new HashMap<String, DeviceValueObject>();
			mapTemp.put(ipath, dvoTemp);
			sInterfaceeMap.put(device_id, mapTemp);
			return;
		}

		HashMap<String, DeviceValueObject> map = sInterfaceeMap.get(device_id);

		if (map == null) {
			map = new HashMap<String, DeviceValueObject>();
		}

		if (!map.containsKey(ipath)) {
			DeviceValueObject dvoTemp = new DeviceValueObject(valueType);
			dvoTemp.setValue(value);
			map.put(ipath, dvoTemp);
			sInterfaceeMap.put(device_id, map);
			return;
		}

		DeviceValueObject dvo = map.get(ipath);
		dvo.setValue(value);
		map.put(ipath, dvo);
		sInterfaceeMap.put(device_id, map);
	}

	public static void setValue(String device_id, String ipath, short[] value, String valueType) {
		if (!sInterfaceeMap.containsKey(device_id)) {
			DeviceValueObject dvoTemp = new DeviceValueObject(valueType);
			dvoTemp.setValue(value);
			HashMap<String, DeviceValueObject> mapTemp = new HashMap<String, DeviceValueObject>();
			mapTemp.put(ipath, dvoTemp);
			sInterfaceeMap.put(device_id, mapTemp);
			return;
		}

		HashMap<String, DeviceValueObject> map = sInterfaceeMap.get(device_id);

		if (map == null) {
			map = new HashMap<String, DeviceValueObject>();
		}

		if (!map.containsKey(ipath)) {
			DeviceValueObject dvoTemp = new DeviceValueObject(valueType);
			dvoTemp.setValue(value);
			map.put(ipath, dvoTemp);
			sInterfaceeMap.put(device_id, map);
			return;
		}

		DeviceValueObject dvo = map.get(ipath);
		dvo.setValue(value);
		map.put(ipath, dvo);
		sInterfaceeMap.put(device_id, map);
	}

	public static void setValue(String device_id, String ipath, boolean value, String valueType) {
		if (!sInterfaceeMap.containsKey(device_id)) {
			DeviceValueObject dvoTemp = new DeviceValueObject(valueType);
			dvoTemp.setValue(value);
			HashMap<String, DeviceValueObject> mapTemp = new HashMap<String, DeviceValueObject>();
			mapTemp.put(ipath, dvoTemp);
			sInterfaceeMap.put(device_id, mapTemp);
			return;
		}

		HashMap<String, DeviceValueObject> map = sInterfaceeMap.get(device_id);
		if (map == null) {
			map = new HashMap<String, DeviceValueObject>();
		}

		if (!map.containsKey(ipath)) {
			DeviceValueObject dvoTemp = new DeviceValueObject(valueType);
			dvoTemp.setValue(value);
			map.put(ipath, dvoTemp);
			sInterfaceeMap.put(device_id, map);
			return;
		}

		DeviceValueObject dvo = map.get(ipath);
		dvo.setValue(value);
		map.put(ipath, dvo);
		sInterfaceeMap.put(device_id, map);
	}

	public static void setValue(String device_id, String ipath, int value, String valueType) {
		if (!sInterfaceeMap.containsKey(device_id)) {
			DeviceValueObject dvoTemp = new DeviceValueObject(valueType);
			dvoTemp.setValue(value);
			HashMap<String, DeviceValueObject> mapTemp = new HashMap<String, DeviceValueObject>();
			mapTemp.put(ipath, dvoTemp);
			sInterfaceeMap.put(device_id, mapTemp);
			return;
		}

		HashMap<String, DeviceValueObject> map = sInterfaceeMap.get(device_id);

		if (map == null) {
			map = new HashMap<String, DeviceValueObject>();
		}

		if (!map.containsKey(ipath)) {
			DeviceValueObject dvoTemp = new DeviceValueObject(valueType);
			dvoTemp.setValue(value);
			map.put(ipath, dvoTemp);
			sInterfaceeMap.put(device_id, map);
			return;
		}

		DeviceValueObject dvo = map.get(ipath);
		dvo.setValue(value);
		map.put(ipath, dvo);
		sInterfaceeMap.put(device_id, map);
	}

	public static void setValue(String device_id, String ipath, short value, String valueType) {
		Log.e("bskim", "setValue : q");
		if (!sInterfaceeMap.containsKey(device_id)) {
			// Log.e("bskim", "device not found");
			DeviceValueObject dvoTemp = new DeviceValueObject(valueType);
			dvoTemp.setValue(value);
			HashMap<String, DeviceValueObject> mapTemp = new HashMap<String, DeviceValueObject>();
			mapTemp.put(ipath, dvoTemp);
			sInterfaceeMap.put(device_id, mapTemp);
			return;
		}

		HashMap<String, DeviceValueObject> map = sInterfaceeMap.get(device_id);

		if (map == null) {
			map = new HashMap<String, DeviceValueObject>();
		}

		if (!map.containsKey(ipath)) {
			// Log.e("bskim", "path not found");
			DeviceValueObject dvoTemp = new DeviceValueObject(valueType);
			dvoTemp.setValue(value);
			map.put(ipath, dvoTemp);
			sInterfaceeMap.put(device_id, map);
			// Log.e("bskim", "set first");
			return;
		}

		// Log.e("bskim", "device & path found");
		DeviceValueObject dvo = map.get(ipath);
		dvo.setValue(value);
		map.put(ipath, dvo);
		sInterfaceeMap.put(device_id, map);
		Log.e("bskim", "setValue success");
	}
	
	public static void setValue(String device_id, String ipath, double value, String valueType) {
		if (!sInterfaceeMap.containsKey(device_id)) {
			DeviceValueObject dvoTemp = new DeviceValueObject(valueType);
			dvoTemp.setValue(value);
			HashMap<String, DeviceValueObject> mapTemp = new HashMap<String, DeviceValueObject>();
			mapTemp.put(ipath, dvoTemp);
			sInterfaceeMap.put(device_id, mapTemp);
			return;
		}

		HashMap<String, DeviceValueObject> map = sInterfaceeMap.get(device_id);

		if (map == null) {
			map = new HashMap<String, DeviceValueObject>();
		}

		if (!map.containsKey(ipath)) {
			DeviceValueObject dvoTemp = new DeviceValueObject(valueType);
			dvoTemp.setValue(value);
			map.put(ipath, dvoTemp);
			sInterfaceeMap.put(device_id, map);
			return;
		}

		DeviceValueObject dvo = map.get(ipath);
		dvo.setValue(value);
		map.put(ipath, dvo);
		sInterfaceeMap.put(device_id, map);
	}
	
	
	public static void setValue(String device_id, String ipath, long value, String valueType) {
		if (!sInterfaceeMap.containsKey(device_id)) {
			// Log.e("bskim", "device not found");
			DeviceValueObject dvoTemp = new DeviceValueObject(valueType);
			dvoTemp.setValue(value);
			HashMap<String, DeviceValueObject> mapTemp = new HashMap<String, DeviceValueObject>();
			mapTemp.put(ipath, dvoTemp);
			sInterfaceeMap.put(device_id, mapTemp);
			return;
		}

		HashMap<String, DeviceValueObject> map = sInterfaceeMap.get(device_id);

		if (map == null) {
			map = new HashMap<String, DeviceValueObject>();
		}

		if (!map.containsKey(ipath)) {
			// Log.e("bskim", "path not found");
			DeviceValueObject dvoTemp = new DeviceValueObject(valueType);
			dvoTemp.setValue(value);
			map.put(ipath, dvoTemp);
			sInterfaceeMap.put(device_id, map);
			// Log.e("bskim", "set first");
			return;
		}

		// Log.e("bskim", "device & path found");
		DeviceValueObject dvo = map.get(ipath);
		dvo.setValue(value);
		map.put(ipath, dvo);
		sInterfaceeMap.put(device_id, map);
	}

	// public static

}
