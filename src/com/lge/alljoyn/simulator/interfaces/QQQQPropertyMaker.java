package com.lge.alljoyn.simulator.interfaces;

import java.util.HashMap;
import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.SignalEmitter;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.ns.NotificationServiceException;

import com.lge.alljoyn.simulator.about.InterfaceObject;
import com.lge.alljoyn.simulator.activity.CommonActivity;
import com.lge.alljoyn.simulator.interfaces.controlpanel.CPPropertyInterface;
import com.lge.alljoyn.simulator.service.BusConnectionService;
import com.lge.alljoyn.simulator.service.BusMapObject;
import com.lge.alljoyn.simulator.utils.DeviceMap;

import android.os.Handler;
import android.util.Log;

public class QQQQPropertyMaker implements BusObject, CPPropertyInterface {

	private short VERSION = 1;
	private int STATES = 0x02;
	private String deviceId;

	public final String iName = "org.alljoyn.ControlPanel.Property";
	private String iPath;
	private String unit;
	private String def;
	private InterfaceObject obj;
	private Handler mHadler;
	
	public void stopHandler() {
		if (mHadler != null) {
			mHadler.removeCallbacks(mRunnable);
		}
	}

	public QQQQPropertyMaker(InterfaceObject _obj, String _deviceId) {

		this.deviceId = _deviceId;
		this.deviceId = _deviceId;
		this.iPath = _obj.getIf_path();
		this.unit = _obj.getIf_unit();
		this.def = _obj.getIf_default_value();
		this.obj = _obj;
		
		if (obj.getIf_writable() == 0) {
			this.STATES = 0x01;
		} else {
			this.STATES = 0x03;
		}
		
		
		try {
			Variant vvalue =  DeviceMap.getValue(deviceId, iPath, BusMapObject.DATA_TYPE_QQQ, def);
			ListQQQQValuesP qqqqvalue =  vvalue.getObject(ListQQQQValuesP.class);
			if (qqqqvalue.fv == 1) {
				// time
				Log.e("test", "time");
				mHadler =  new Handler();
				mHadler.postDelayed(mRunnable, 1000);
			}
		} catch (BusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			Log.e("test", "mRunnable");
			try {
				Variant vvalue =  DeviceMap.getValue(deviceId, iPath, BusMapObject.DATA_TYPE_QQQ, def);
				ListQQQQValuesP qqqqvalue =  vvalue.getObject(ListQQQQValuesP.class);
				if (qqqqvalue.sv.fv != 0 || qqqqvalue.sv.sv != 0 || qqqqvalue.sv.tv != 0) {
					Log.e("test", "over");
					if (qqqqvalue.sv.tv > 0) {
						qqqqvalue.sv.tv -= 1;
						setValue(new Variant(qqqqvalue));
					} else {
						if (qqqqvalue.sv.sv > 0) {
							qqqqvalue.sv.sv -= 1;
							qqqqvalue.sv.tv = 59;
						} else {
							qqqqvalue.sv.fv -= 1;
							qqqqvalue.sv.sv = 59;
							qqqqvalue.sv.tv = 59;
							
						}
					}
				}
				Log.e("bskim", "" + qqqqvalue.sv.fv +":" + qqqqvalue.sv.sv + ":"+qqqqvalue.sv.tv);
				mHadler.postDelayed(mRunnable, 1000);
				
			} catch (BusException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	};

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
		
		test.put((short) 0, new Variant(iPath.substring(iPath.lastIndexOf("/"), iPath.length()).replace("/", "") + " : "));
		test.put((short) 1, new Variant(0x008000));
		test.put((short) 2, new Variant(new short[] {(short)obj.getIf_ui_type()}));
		if (unit != null) {
			test.put((short) 3, new Variant(unit));
		} else {
			test.put((short) 3, new Variant(""));
		}
		return test;
	}

	@Override
	public Variant getValue() throws BusException {
		Log.e(iPath, "getValue");
		
		return DeviceMap.getValue(deviceId, iPath, BusMapObject.DATA_TYPE_QQQ, def);
	}

	@Override
	public void setValue(Variant value) throws BusException {
		Log.e("setValue", "path : " + iPath);
		// short val = value.getObject(Short.class);
		ListQQQQValuesP qqqqvalue = value.getObject(ListQQQQValuesP.class);
		short[] shortV = new short[4];
		shortV[0] = qqqqvalue.fv;
		shortV[1] = qqqqvalue.sv.fv;
		shortV[2] = qqqqvalue.sv.sv;
		shortV[3] = qqqqvalue.sv.tv;
		DeviceMap.setValue(deviceId, iPath, shortV, obj.getIf_signal());
		ValueChanged(value);
	}

	@BusSignal(name = "ValueChanged")
	@Override
	public void ValueChanged(Variant newValue) throws BusException {
		Log.e("ValueChanged", "ValueChanged : " + iPath);

		if (BusConnectionService.joinerInfo != null && !BusConnectionService.joinerInfo.isEmpty()
				&& BusConnectionService.joinerInfo.containsKey(deviceId)
				&& BusConnectionService.joinerInfo.get(deviceId) != null
				&& !BusConnectionService.joinerInfo.get(deviceId).isEmpty()) {
			
			Log.e("ValueChanged", "dataChenged noti 1");
			for (String sKey : BusConnectionService.joinerInfo.get(deviceId).keySet()) {
				SignalEmitter emitter = new SignalEmitter(this, sKey, BusConnectionService.joinerInfo.get(deviceId).get(sKey),
						SignalEmitter.GlobalBroadcast.On);

				CPPropertyInterface sss = emitter.getInterface(CPPropertyInterface.class);
				sss.ValueChanged(newValue);
			}
			Log.e("ValueChanged", "dataChenged noti");

			if (iPath.contains("WashingMachineRemainingTime")) {
				ListQQQQValuesP qqq = newValue.getObject(ListQQQQValuesP.class);
				if (qqq.sv.fv == 0 && qqq.sv.sv == 0 && qqq.sv.tv == 0) {
					String msg = "Washing has been completed.";

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

			
		} else {
			Log.e("ValueChanged", "dataChenged noti 2");
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
