package com.lge.alljoyn.simulator.service;

import java.util.HashMap;
import java.util.Map;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.Status;
import org.alljoyn.ns.NotificationServiceException;
import org.alljoyn.services.common.PropertyStore;

import com.lge.alljoyn.simulator.about.DeviceAboutObject;
import com.lge.alljoyn.simulator.activity.CommonActivity;
import com.lge.alljoyn.simulator.database.AJDeviceDBAdapter;
import com.lge.alljoyn.simulator.interfaces.QQQQPropertyMaker;
import com.lge.alljoyn.simulator.utils.DeviceMap;
import com.lge.alljoyn.simulator.utils.GlobalUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BusConnectionService extends Service {

	private final String LOG_TAG = "BusConnectionService";

	static {
		System.loadLibrary("alljoyn_java");
	}

	// public static final String SERVICE_NAME = "org.alljoyn.simulator";
	// private BusAttachment bus;
	public static BusMapObject busMap;

	private static final short CONTACT_PORT = 1000;

	private AJDeviceDBAdapter mDBAdapter;
	private Context mContext = BusConnectionService.this;

	public static Map<String, AboutServiceImpl> asiMap;

	public static Map<String, Map<String, Integer>> joinerInfo;

	public static NotificationServiceObject notificationServiceObject;

	@Override
	public void onCreate() {
		super.onCreate();
		// Log.e(LOG_TAG, "BusConnectionService onCreate");
		busMap = new BusMapObject();
		asiMap = new HashMap<String, AboutServiceImpl>();
		// DeviceMap.sInterfaceeMap = new HashMap<String, HashMap<String,
		// DeviceValueObject>>();
		joinerInfo = new HashMap<String, Map<String, Integer>>();
		mDBAdapter = new AJDeviceDBAdapter(mContext);
		mDBAdapter.open();
		notificationServiceObject = new NotificationServiceObject();
		// CommonActivity.mActivity.dataChenged("sdfadsfdsfdsfds");
		// 데몬 준비
		boolean ok = org.alljoyn.bus.alljoyn.DaemonInit.PrepareDaemon(getApplicationContext());
		if (!ok) {
			// Log.e(LOG_TAG, "PrepareDaemon fail : " + ok);
			// 데몬 init 실패시 서비스 종료
			Toast.makeText(mContext, "서비스 실행중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
			stopSelf();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Log.e(LOG_TAG, "BusConnectionService onStartCommand");

		int device_id = intent.getIntExtra(DeviceMap.IK_DEVICE_ID, 0);
		int startFlag = intent.getIntExtra(DeviceMap.IK_START_TYPE, 0);
		DeviceAboutObject aboutObj = mDBAdapter.getRegistedDevice(device_id);
		// Log.e("device info", "id=" + aboutObj.get_id() + " name=" +
		// aboutObj.getDeviceName() + " model="
		// + aboutObj.getDeviceId() + " type=" + aboutObj.getDeviceType());
		if (startFlag == 1) {
			// connection 실행
			// Log.e("service", "start device command");

			if (startAJ(aboutObj)) {
				Log.e(LOG_TAG, "started aj service");
				CommonActivity.mActivity.dataChenged("" + aboutObj.get_id(), DeviceMap.FLAG_CONNECTION_SUCCESS);
				if (notificationServiceObject != null && !notificationServiceObject.isStartNoti()) {
					if (notificationServiceObject.initNoti()) {
						Log.e(LOG_TAG, "notificationServiceObject init complete");
					} else {
						Log.e(LOG_TAG, "notificationServiceObject init fail");
					}
				}
			} else {
				Log.e(LOG_TAG, "error start aj service");
				// 실패시

				if (DeviceMap.sInterfaceeMap != null && DeviceMap.sInterfaceeMap.containsKey("" + device_id)) {
					DeviceMap.sInterfaceeMap.remove("" + device_id);
				}

				if (asiMap != null && asiMap.containsKey("" + device_id)) {
					asiMap.get("" + device_id).getInstance().stopAboutServer();
				}

				if (!joinerInfo.isEmpty() && joinerInfo.containsKey("" + device_id)) {
					joinerInfo.remove("" + device_id);
				}

				if (busMap != null && busMap.getBusMap().containsKey("" + device_id)) {

					if (busMap.releaseName("" + device_id) == Status.OK) {
						// Log.v("bskim", "releaseName success");
					} else {
						// Log.v("bskim", "releaseName fail");
					}

					// Log.e("bskim", "disconnect bus");
					busMap.getBus("" + device_id).disconnect();
					busMap.removeBus("" + device_id);

					if (busMap.sBusQQQMap != null && busMap.sBusQQQMap.containsKey("" + device_id)) {
						for (String sKey : busMap.sBusQQQMap.get("" + device_id).keySet()) {
							QQQQPropertyMaker temp = busMap.sBusQQQMap.get("" + device_id).get(sKey);
							temp.stopHandler();
						}
					}
					busMap.sBusQQQMap.remove("" + device_id);
					busMap.removeBusObjMap("" + device_id);
				}
				CommonActivity.mActivity.dataChenged("" + aboutObj.get_id(), DeviceMap.FLAG_CONNECTION_FAIL);
				// stopSelf();
			}

		} else {
			Log.e("service", "stop device command");
			// 디바이스 종료

			DeviceMap.sInterfaceeMap.remove("" + device_id);
			if (asiMap != null && asiMap.containsKey("" + device_id)) {
				asiMap.get("" + device_id).getInstance().stopAboutServer();
			}
			if (busMap != null && busMap.getBusMap().containsKey("" + device_id)) {

				if (busMap.releaseName("" + device_id) == Status.OK) {
					// Log.v("bskim", "releaseName success");
				} else {
					// Log.v("bskim", "releaseName fail");
				}

				if (!joinerInfo.isEmpty() && joinerInfo.containsKey("" + device_id)) {
					joinerInfo.remove("" + device_id);
				}

				// Log.e("bskim", "disconnect bus");
				busMap.getBus("" + device_id).disconnect();
				if (busMap.sBusQQQMap != null && busMap.sBusQQQMap.containsKey("" + device_id)) {
					for (String sKey : busMap.sBusQQQMap.get("" + device_id).keySet()) {
						QQQQPropertyMaker temp = busMap.sBusQQQMap.get("" + device_id).get(sKey);
						temp.stopHandler();
					}
				}
				busMap.sBusQQQMap.remove("" + device_id);
				busMap.removeBus("" + device_id);
			}

			CommonActivity.mActivity.dataChenged("" + aboutObj.get_id(), DeviceMap.FLAG_CHANGED_DATA);

		}

		return super.onStartCommand(intent, flags, startId);

	}

	// AJ start!!!
	private boolean startAJ(final DeviceAboutObject aboutObj) {

		// 버스생성
		BusAttachment tbus = new BusAttachment(aboutObj.getDeviceName(), BusAttachment.RemoteMessage.Receive);
		busMap.setBus("" + aboutObj.get_id(), tbus);
		busMap.registerBusListener("" + aboutObj.get_id(), new BusListener());

		// 버스 상태 확인을 위한 status
		Status status;

		// 커넥션
		status = busMap.connect("" + aboutObj.get_id());
		// bus.connect();
		if (status != Status.OK) {
			Log.e(LOG_TAG, "connect fail : " + status);
			return false;
		}

		// 포트와 세션 셋팅
		Mutable.ShortValue contactPort = new Mutable.ShortValue(CONTACT_PORT);
		SessionOpts so = new SessionOpts();
		so.traffic = SessionOpts.TRAFFIC_MESSAGES;
		so.isMultipoint = false;
		so.proximity = SessionOpts.PROXIMITY_ANY;
		so.transports = SessionOpts.TRANSPORT_ANY;

		// busMap.getBus("").setSessionListener(sessionId, listener)

		// bind
		status = busMap.bindSessionPort("" + aboutObj.get_id(), contactPort, so, new SessionPortListener() {

			@Override
			public boolean acceptSessionJoiner(short sessionPort, String joiner, SessionOpts sessonOpts) {
				Log.e(LOG_TAG, "acceptSessionJoiner : " + joiner);

				return sessionPort == CONTACT_PORT;
			}

			@Override
			public void sessionJoined(short sessionPort, int id, String joiner) {
				Log.e(LOG_TAG, "sessionJoined : " + joiner + "  id : " + id + " sessionPort : " + sessionPort);
				if (!joinerInfo.containsKey("" + aboutObj.get_id())) {
					joinerInfo.put("" + aboutObj.get_id(), new HashMap<String, Integer>());
				}

				Map<String, Integer> tempMap = joinerInfo.get("" + aboutObj.get_id());
				tempMap.put(joiner, id);

				joinerInfo.put("" + aboutObj.get_id(), tempMap);

				// if (notificationServiceObject != null
				// && notificationServiceObject.isStartNoti()) {
				// try {
				// String msg = aboutObj.getDeviceName() + " connected";
				// notificationServiceObject.sendNoti(msg);
				// } catch (NotificationServiceException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				//
				// }
			}

		});

		if (status != Status.OK) {
			Log.e(LOG_TAG, "bindSessionPort fail : " + status);
			return false;
		}

		// status = busMap.announceAbout(device_id, contactPort.value,
		// device_type);
		//
		// if (status != Status.OK) {
		// Log.e(LOG_TAG, "aboutObj Announce fail : " + status);
		// return false;
		// }

		final AboutServiceImpl aboutServiceImpl = new AboutServiceImpl();

		try {
			PropertyStore test = GlobalUtils.getAboutData(aboutObj);
			BusAttachment bus = busMap.getBus("" + aboutObj.get_id());

			aboutServiceImpl.startAboutServer(CONTACT_PORT, test, bus);
			// isSenderStarted = true;
			// List<NotificationText> text = new LinkedList<NotificationText>();
			// text.add(new NotificationText("en", "The fridge door is open"));
			// Notification notif = new
			// Notification(NotificationMessageType.INFO, text);

			if (!busMap.registerCPInterface(aboutObj, aboutServiceImpl, mDBAdapter)) {
				Log.e(LOG_TAG, "registerCPInterface fail");
				return false;
			}

			aboutServiceImpl.announce();

			// List<BusObjectDescription> description =
			// aboutServiceImpl.getBusObjectDescriptions();
			// for (int i = 0; i < description.size(); i++) {
			// Log.e("bskim", "description[" + i + "] path = " +
			// description.get(i).path);
			// String[] test2 = description.get(i).interfaces;
			// for (int k = 0; k < test2.length; k++) {
			// Log.e("bskim", "description[" + i + "] interface[" + k + "] : " +
			// test2[k]);
			// }
			// }

			busMap.setBus("" + aboutObj.get_id(), bus);
			asiMap.put("" + aboutObj.get_id(), aboutServiceImpl);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		if (!busMap.advertiseName(aboutObj)) {
			Log.e(LOG_TAG, "advertiseName fail");
			return false;
		}

		return true;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// Log.e("bskim", "onBind Service");

		return null;
	}

	@Override
	public void onDestroy() {
		Log.e(LOG_TAG, "BusConnectionService onDestroy");
		super.onDestroy();
		// bus.unregisterBusObject(responseService);
		// AboutServiceImpl.getInstance().stopAboutServer();
		if (notificationServiceObject != null && notificationServiceObject.isStartNoti()) {
			try {
				notificationServiceObject.stopNoti();
			} catch (NotificationServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (asiMap != null) {

			for (String sKey : asiMap.keySet()) {
				asiMap.get(sKey).getInstance().stopAboutServer();
			}
		}

		if (busMap != null && busMap.getBusMap() != null) {

			for (String sKey : busMap.getBusMap().keySet()) {
				// Log.e("key", "key=" + sKey);
				busMap.getBus(sKey).disconnect();
				// busMap.removeBus(sKey);
			}
		}

		try {
			if (busMap.sBusQQQMap != null && !busMap.sBusQQQMap.isEmpty()) {
				for (String pKey : busMap.sBusQQQMap.keySet()) {
					if (busMap.sBusQQQMap.get(pKey) != null && !busMap.sBusQQQMap.get(pKey).isEmpty())
						for (String sKey : busMap.sBusQQQMap.get(pKey).keySet()) {
							QQQQPropertyMaker temp = busMap.sBusQQQMap.get(pKey).get(sKey);
							temp.stopHandler();
						}
					busMap.sBusQQQMap.remove(pKey);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("bskim", "killProcess");
			android.os.Process.killProcess(android.os.Process.myPid());
		}

	}

}
