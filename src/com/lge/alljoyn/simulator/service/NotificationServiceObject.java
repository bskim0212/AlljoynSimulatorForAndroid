package com.lge.alljoyn.simulator.service;


import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.Status;
import org.alljoyn.ns.Notification;
import org.alljoyn.ns.NotificationMessageType;
import org.alljoyn.ns.NotificationServiceException;
import org.alljoyn.ns.NotificationText;
import org.alljoyn.services.common.PropertyStore;

import com.lge.alljoyn.simulator.about.DeviceAboutObject;
import com.lge.alljoyn.simulator.utils.GlobalUtils;

import android.util.Log;

public class NotificationServiceObject {

	private final String LOG_TAG = "NotificationServiceObject";
	private final short CONTACT_PORT = 1000;
	private final String serviceName = "SimulatorNotificationService";

	private BusAttachment bus;

	private NotificationService mService;
	private NotificationSender mNotificationSender;
	private AboutServiceImpl aboutServiceImpl;

	
	private String serviceFullName;

	// 노티피케이션 서비스 실행여부
	private boolean isStart = false;

	
	public NotificationServiceObject() {

	}

	// init
	public boolean initNoti() {
		isStart = true;
		bus = new BusAttachment(serviceName, BusAttachment.RemoteMessage.Receive);
		bus.registerBusListener(new BusListener());
		Status status;
		status = bus.connect();
		if (status != Status.OK) {
			Log.e(LOG_TAG, "connect fail : " + status);
			isStart = false;
		}

		Mutable.ShortValue contactPort = new Mutable.ShortValue(CONTACT_PORT);
		SessionOpts so = new SessionOpts();
		so.traffic = SessionOpts.TRAFFIC_MESSAGES;
		so.isMultipoint = false;
		so.proximity = SessionOpts.PROXIMITY_ANY;
		so.transports = SessionOpts.TRANSPORT_ANY;

		status = bus.bindSessionPort(contactPort, so, new SessionPortListener() {

			@Override
			public boolean acceptSessionJoiner(short sessionPort, String joiner, SessionOpts sessonOpts) {
				//Log.e(LOG_TAG, "acceptSessionJoiner : " + joiner);

				return sessionPort == CONTACT_PORT;
			}

			@Override
			public void sessionJoined(short sessionPort, int id, String joiner) {
				//Log.e(LOG_TAG, "sessionJoined : " + joiner + "  id : " + id + " sessionPort : " + sessionPort);

			}

		});

		if (status != Status.OK) {
			Log.e(LOG_TAG, "bindSessionPort fail : " + status);
			isStart = false;
		}

		String myNodeName = "node" + UUID.randomUUID().toString().hashCode();
		serviceFullName = "org.alljoyn.simulator" + "." + myNodeName + "." + serviceName;
		Log.e(LOG_TAG, "serviceFullName=" + serviceFullName);

		status = bus.requestName(serviceFullName,
				BusAttachment.ALLJOYN_REQUESTNAME_FLAG_REPLACE_EXISTING
						| BusAttachment.ALLJOYN_REQUESTNAME_FLAG_DO_NOT_QUEUE
						| BusAttachment.ALLJOYN_NAME_FLAG_ALLOW_REPLACEMENT);
		if (status == Status.OK) {
			status = bus.advertiseName(serviceFullName, SessionOpts.TRANSPORT_ANY);
			if (status != Status.OK) {
				status = bus.releaseName(serviceFullName);
				Log.e(LOG_TAG, "advertiseName fail : " + status);
				isStart = false;
			}
		} else {
			Log.e(LOG_TAG, "requestName fail : " + status);
			isStart = false;
		}

		try {
			DeviceAboutObject obj = new DeviceAboutObject(0,serviceName,serviceName,0);
			aboutServiceImpl = new AboutServiceImpl();
			PropertyStore test = GlobalUtils.getAboutData(obj);
			aboutServiceImpl.getInstance().startAboutServer(CONTACT_PORT, test, bus);

			mService = new NotificationService();
			mNotificationSender = mService.initSend(bus, test);

			aboutServiceImpl.getInstance().announce();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isStart = false;
		}

		return isStart;
	}

	public boolean isStartNoti() {
		return isStart;
	}

	// 노티보내기
	public void sendNoti(String msg) throws NotificationServiceException {

		List<NotificationText> text = new LinkedList<NotificationText>();
		text.add(new NotificationText("en", msg));
		Notification notif = new Notification(NotificationMessageType.INFO, text);

		mNotificationSender.send(notif, 4000);
		Log.e(LOG_TAG, "sent noti : " + msg);

	}

	// 노티 release
	public void stopNoti() throws NotificationServiceException {
		if (mService != null) {
			mService.shutdownSender();
		}
		if (aboutServiceImpl != null) {
			aboutServiceImpl.stopAboutServer();
		}
		if (bus != null) {
			bus.releaseName(serviceFullName);
			bus.disconnect();
		}
	}

	


}
