package com.lge.alljoyn.simulator.about;

public class InterfaceRangeObject {

	private int _id;
	private String range_index;
	private String range_label;
	private int if_id;

	public InterfaceRangeObject() {

	}

	public InterfaceRangeObject(int id, String _range_index, String _range_label, int _if_id) {
		this._id = id;
		this.range_index = _range_index;
		this.range_label = _range_label;
		this.if_id = _if_id;

	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getRange_index() {
		return range_index;
	}

	public void setRange_index(String range_index) {
		this.range_index = range_index;
	}

	public String getRange_label() {
		return range_label;
	}

	public void setRange_label(String range_label) {
		this.range_label = range_label;
	}

	public int getIf_id() {
		return if_id;
	}

	public void setIf_id(int if_id) {
		this.if_id = if_id;
	}

}
