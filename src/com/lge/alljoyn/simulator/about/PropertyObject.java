package com.lge.alljoyn.simulator.about;

public class PropertyObject {

	private String label;
	private String value;
	private int index;
	private InterfaceObject obj;

	public InterfaceObject getObj() {
		return obj;
	}

	public void setObj(InterfaceObject obj) {
		this.obj = obj;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
