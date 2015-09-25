package com.lge.alljoyn.simulator.interfaces;

import org.alljoyn.bus.Variant;

import com.lge.alljoyn.simulator.service.BusMapObject;

public class DeviceValueObject {

	private String sValue;
	private int iValue = 0;
	private short qValue = 0;
	private boolean bValue = false;
	private Variant variant;
	private long xValue = 0;
	private double dValue = 0;

	private String valueType;

	// public DeviceValueObject() {
	//
	// }

	public DeviceValueObject(String type) {
		this.valueType = type;
	}

	public void setValue(String value) {

		this.sValue = value;
	}

	public void setValue(int value) {
		this.iValue = value;
	}

	public void setValue(short value) {
		this.qValue = value;
	}
	
	public void setValue(double value) {
		this.dValue = value;
	}

	public void setValue(boolean value) {
		this.bValue = value;
	}
	
	public void setValue(long value) {
		this.xValue = value;
	}

	public void setValue(short[] value) {
		ListQQQQValuesP values1 = new ListQQQQValuesP();
		values1.fv = value[0];

		ListQQQQValuesC values2 = new ListQQQQValuesC();
		values2.fv = value[1];
		values2.sv = value[2];
		values2.tv = value[3];

		values1.sv = values2;

		Variant setVal = new Variant(values1, "(q(qqq))");
		this.variant = setVal;
	}



	public Variant getValue(String type) {
		Variant returnValue;
		if (type.equals(BusMapObject.DATA_TYPE_Q)) {
			returnValue = new Variant(qValue, "q");
		} else if (type.equals(BusMapObject.DATA_TYPE_N)) {
			returnValue = new Variant(bValue);
		} else if (type.equals(BusMapObject.DATA_TYPE_B)) {
			returnValue = new Variant(bValue);
		} else if (type.equals(BusMapObject.DATA_TYPE_I)) {
			returnValue = new Variant(iValue);
		} else if (type.equals(BusMapObject.DATA_TYPE_U)) {
			returnValue = new Variant(iValue,"u");
		} else if (type.equals(BusMapObject.DATA_TYPE_QQQ)) {
			returnValue = variant;
		} else if (type.equals(BusMapObject.DATA_TYPE_D)) {
			returnValue = new Variant(dValue);
		} else if (type.equals(BusMapObject.DATA_TYPE_X)) {
			returnValue = new Variant(xValue);
		} else if (type.equals(BusMapObject.DATA_TYPE_T)) {
			returnValue = new Variant(xValue, "t");
		} else if (type.equals(BusMapObject.DATA_TYPE_S)){
			// string
			returnValue = new Variant(sValue);
		} else {
			returnValue =  new Variant(null);
		}

		return returnValue;
	}
	

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
}
