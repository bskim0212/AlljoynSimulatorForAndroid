package com.lge.alljoyn.simulator.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionListener;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.Status;

import com.lge.alljoyn.simulator.about.DeviceAboutObject;
import com.lge.alljoyn.simulator.about.InterfaceObject;
import com.lge.alljoyn.simulator.database.AJDeviceDBAdapter;
import com.lge.alljoyn.simulator.interfaces.ActionMaker;
import com.lge.alljoyn.simulator.interfaces.CPIMaker;
import com.lge.alljoyn.simulator.interfaces.ContainerMaker;
import com.lge.alljoyn.simulator.interfaces.DialogMaker;
import com.lge.alljoyn.simulator.interfaces.PropertyMaker;
import com.lge.alljoyn.simulator.interfaces.QQQQPropertyMaker;
import com.lge.alljoyn.simulator.utils.DeviceMap;

import android.util.Log;

public class BusMapObject {

	public static final String CP_CONTROLPANEL = "ControlPanel";
	public static final String CP_CONTAINER = "Container";
	public static final String CP_PROPERTY = "Property";
	public static final String CP_ACTION = "Action";
	public static final String CP_DIALOG = "Dialog";
	
	public static final String SEPARATOR = ","; 

	public static final String DATA_TYPE_Q = "q"; // unsigned int 16 - short : q
	public static final String DATA_TYPE_N = "n"; // signed int 16 - short : n
	public static final String DATA_TYPE_S = "s"; // string : s
	public static final String DATA_TYPE_I = "i"; // signed int 32 - int : i
	public static final String DATA_TYPE_U = "u"; // unsigned int 32 - int : u
	public static final String DATA_TYPE_B = "b"; // boolean : b
	public static final String DATA_TYPE_D = "d"; // double : d
	public static final String DATA_TYPE_X = "x"; // signed int 64 - long : x
	public static final String DATA_TYPE_T = "t"; // unsigned int 64 - long : t
	public static final String DATA_TYPE_QQQ = "q(qqq)"; // date or time

	// 서비스에서 실행중인 시뮬레이터의 BusAttachment 맵
	private Map<String, BusAttachment> busMap = new HashMap<String, BusAttachment>();
	private Map<String, String> busNameMap = new HashMap<String, String>();

	private HashMap<String, HashMap<String, BusObject>> sBusPropertyObjMap = new HashMap<String, HashMap<String, BusObject>>();
	public HashMap<String, HashMap<String, QQQQPropertyMaker>> sBusQQQMap = new HashMap<String, HashMap<String, QQQQPropertyMaker>>();

	// BusAttachment 셋팅
	public void setBus(String key, BusAttachment value) {
		busMap.put(key, value);
	}

	public Map<String, BusAttachment> getBusMap() {
		return busMap;
	}

	public BusAttachment getBus(String key) {
		return busMap.get(key);
	}

	public void removeBus(String key) {
		busMap.remove(key);
	}

	public void removeBusObjMap(String key) {
		if (sBusPropertyObjMap != null && sBusPropertyObjMap.containsKey(key)) {
			sBusPropertyObjMap.remove(key);
		}
	}

	public HashMap<String, BusObject> getBusObjMap(String key) {
		if (sBusPropertyObjMap != null && sBusPropertyObjMap.containsKey(key)) {
			return sBusPropertyObjMap.get(key);
		}

		return null;
	}

	public void disconnectBus(String key) {
		BusAttachment bus = busMap.get(key);
		bus.disconnect();
		// Log.e("bskim", "????");
		setBus(key, bus);
	}

	// 버스 리스너 등록
	public void registerBusListener(String key, BusListener busListener) {
		BusAttachment bus = busMap.get(key);
		bus.registerBusListener(busListener);
		setBus(key, bus);
	}

	// 커넥션
	public Status connect(String key) {
		BusAttachment bus = busMap.get(key);
		Status status = bus.connect();
		setBus(key, bus);
		return status;
	}

	//
	public Status bindSessionPort(String key, Mutable.ShortValue contactPort, SessionOpts opt,
			SessionPortListener sessionPortListener) {
		BusAttachment bus = busMap.get(key);
		Status status = bus.bindSessionPort(contactPort, opt, sessionPortListener);
		setBus(key, bus);
		return status;
	}

	public boolean advertiseName(DeviceAboutObject aboutObj) {
		// Log.e("device id", aboutObj.getDeviceId());
		BusAttachment bus = busMap.get("" + aboutObj.get_id());
		String myNodeName = "node" + UUID.randomUUID().toString().hashCode();
		String serviceFullName = "org.alljoyn.simulator" + "." + myNodeName + "." + aboutObj.getDeviceId();
		// Log.e("serviceFullName", serviceFullName);
		busNameMap.put("" + aboutObj.get_id(), serviceFullName);

		Status status = bus.requestName(serviceFullName,
				BusAttachment.ALLJOYN_REQUESTNAME_FLAG_REPLACE_EXISTING
						| BusAttachment.ALLJOYN_REQUESTNAME_FLAG_DO_NOT_QUEUE
						| BusAttachment.ALLJOYN_NAME_FLAG_ALLOW_REPLACEMENT);
		if (status == Status.OK) {
			status = bus.advertiseName(serviceFullName, SessionOpts.TRANSPORT_ANY);
			if (status != Status.OK) {
				status = bus.releaseName(serviceFullName);
				Log.e("advertiseName", "advertiseName fail : " + status);
				return false;
			}
		} else {
			Log.e("advertiseName", "requestName fail : " + status);
			return false;
		}
		setBus("" + aboutObj.get_id(), bus);
		return true;
	}

	public Status setSessionListener(String device_id, int sessionId, SessionListener sessionListener) {
		BusAttachment bus = busMap.get(device_id);
		Status status = bus.setSessionListener(sessionId, sessionListener);
		setBus(device_id, bus);
		return status;
	}

	public Status releaseName(String device_id) {
		BusAttachment bus = busMap.get(device_id);
		String name = busNameMap.get(device_id);
		// Log.e("releaseName", "name=" + name);
		Status status = bus.releaseName(name);// SessionOpts.TRANSPORT_ANY);
		setBus(device_id, bus);
		return status;
	}

	public boolean registerCPInterface(DeviceAboutObject aboutObj, AboutServiceImpl aboutService,
			AJDeviceDBAdapter mDBAdapter) {

		DeviceMap.sInterfaceeMap.put("" + aboutObj.get_id(), null);

		BusAttachment bus = busMap.get("" + aboutObj.get_id());
		Status status;

		HashMap<String, String> propertyMap = new HashMap<String, String>();
		HashMap<String, BusObject> propertyBusObjMap = new HashMap<String, BusObject>();

		ArrayList<InterfaceObject> ifObjectList = mDBAdapter.getInterfaceList(aboutObj.getDeviceType(), null);
		// sBusQQQMap

		// Log.e("registerCPInterface", "total interface count=" +
		// ifObjectList.size());

		if (ifObjectList != null && ifObjectList.size() > 0) {
			HashMap<String, BusObject> busObjMap = new HashMap<String, BusObject>();

			for (int i = 0; i < ifObjectList.size(); i++) {
				InterfaceObject tempObj = ifObjectList.get(i);
				Log.e("bskim", "cp name = " + tempObj.getIf_type());
				// Log.e("registerCPInterface", "regist >???????? =" +
				// tempObj.getIf_path());
				// regist ControlPanel
				if (tempObj.getIf_type().equalsIgnoreCase(CP_CONTROLPANEL)) {
					// Log.e("registerCPInterface", "regist CP =" +
					// tempObj.getIf_path());
					CPIMaker cPIMaker = new CPIMaker();
					aboutService.addObjectDescription(tempObj.getIf_path(), new String[] { tempObj.getIf_name() });
					status = bus.registerBusObject(cPIMaker, tempObj.getIf_path());
					if (status != Status.OK) {
						Log.e("registerCPInterface", "registerBusObject CPIMaker fail : " + status);
						return false;
					}
				}

				// regist Container
				if (tempObj.getIf_type().equalsIgnoreCase(CP_CONTAINER)) {
					// Log.e("registerCPInterface", "regist CP_CONTAINER =" +
					// tempObj.getIf_path());
					ContainerMaker cMaker = new ContainerMaker(tempObj.getIf_path()
							.substring(tempObj.getIf_path().lastIndexOf("/"), tempObj.getIf_path().length()), tempObj);
					status = bus.registerBusObject(cMaker, tempObj.getIf_path());
					if (status != Status.OK) {
						Log.e("registerCPInterface", "registerBusObject CPIMaker fail : " + status);
						return false;
					}
				}

				// regist Prorerty
				if (tempObj.getIf_type().equalsIgnoreCase(CP_PROPERTY)) {

					if (tempObj.getIf_signal().equalsIgnoreCase(DATA_TYPE_QQQ)) {
						QQQQPropertyMaker maker = new QQQQPropertyMaker(tempObj, "" + aboutObj.get_id());
						propertyMap.put(tempObj.getIf_description(), tempObj.getIf_path());
						propertyBusObjMap.put(tempObj.getIf_description(), maker);
						status = bus.registerBusObject(maker, tempObj.getIf_path());
						busObjMap.put(tempObj.getIf_path(), maker);
						if (status != Status.OK) {
							Log.e("registerCPInterface",
									"registerBusObject " + tempObj.getIf_path() + " fail : " + status);
							maker.stopHandler();
							return false;
						}

						if (!sBusQQQMap.containsKey("" + aboutObj.get_id())) {
							sBusQQQMap.put("" + aboutObj.get_id(), new HashMap<String, QQQQPropertyMaker>());
						}

						HashMap<String, QQQQPropertyMaker> qqqMap = sBusQQQMap.get("" + aboutObj.get_id());
						qqqMap.put(tempObj.getIf_path(), maker);
						sBusQQQMap.put("" + aboutObj.get_id(), qqqMap);

					} else {
						PropertyMaker maker = new PropertyMaker(tempObj, "" + aboutObj.get_id());
						propertyMap.put(tempObj.getIf_description(), tempObj.getIf_path());
						propertyBusObjMap.put(tempObj.getIf_description(), maker);
						status = bus.registerBusObject(maker, tempObj.getIf_path());
						busObjMap.put(tempObj.getIf_path(), maker);
						if (status != Status.OK) {
							Log.e("registerCPInterface",
									"registerBusObject " + tempObj.getIf_path() + " fail : " + status);
							return false;
						}
					}

				}

			}

			sBusPropertyObjMap.put("" + aboutObj.get_id(), busObjMap);

			// regist action or dialog
			for (int i = 0; i < ifObjectList.size(); i++) {
				InterfaceObject tempObj = ifObjectList.get(i);

				if (tempObj.getIf_type().equalsIgnoreCase(CP_ACTION)) {
					// Log.e("registerCPInterface", "regist CP_ACTION =" +
					// tempObj.getIf_path());
					String propertyKey = null;
					if (propertyMap.containsKey(tempObj.getIf_description())) {
						propertyKey = propertyMap.get(tempObj.getIf_description());
					}

					ActionMaker maker = new ActionMaker(tempObj, propertyKey, "" + aboutObj.get_id());
					status = bus.registerBusObject(maker, tempObj.getIf_path());
					if (status != Status.OK) {
						Log.e("registerCPInterface", "registerBusObject " + tempObj.getIf_path() + " fail : " + status);
						return false;
					}

				}

				if (tempObj.getIf_type().equalsIgnoreCase(CP_DIALOG)) {
					// Log.e("registerCPInterface", "regist CP_DIALOG =" +
					// tempObj.getIf_path());
					String propertyKey = null;

					if (propertyMap.containsKey(tempObj.getIf_description())) {
						propertyKey = propertyMap.get(tempObj.getIf_description());
					}

					DialogMaker maker = new DialogMaker(tempObj, propertyKey, "" + aboutObj.get_id());
					status = bus.registerBusObject(maker, tempObj.getIf_path());

					if (status != Status.OK) {
						Log.e("registerCPInterface", "registerBusObject " + tempObj.getIf_path() + " fail : " + status);
						return false;
					}

				}

			}

		}

		busMap.put("" + aboutObj.get_id(), bus);

		return true;
	}

}
