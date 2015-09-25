package com.lge.alljoyn.simulator.about;

public class DeviceAboutObject {

	private int _id;
	private String DeviceId; // model number
	private String DeviceName; //
	private int deviceType; // registed device id
	private int UseFlag = 1; // 1 use , 2 not use

	public DeviceAboutObject(){
		
	}
	public DeviceAboutObject(int id, String _DeviceId, String _DeviceName, int _deviceType) {
		this._id = id;
		this.DeviceId = _DeviceId;
		this.DeviceName = _DeviceName;
		this.deviceType = _deviceType;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getDeviceId() {
		return DeviceId;
	}

	public void setDeviceId(String deviceId) {
		DeviceId = deviceId;
	}

	public String getDeviceName() {
		return DeviceName;
	}

	public void setDeviceName(String deviceName) {
		DeviceName = deviceName;
	}

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	public int getUseFlag() {
		return UseFlag;
	}
	public void setUseFlag(int useFlag) {
		UseFlag = useFlag;
	}

}
