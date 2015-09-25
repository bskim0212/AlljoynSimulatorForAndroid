package com.lge.alljoyn.simulator.about;

import java.util.ArrayList;

public class InterfaceObject {
	private int _id;
	private String if_name;
	private String if_path;
	private String if_type;
	private String if_signal;
	private int if_device_id;
	private String if_default_value;
	private String if_min_value;
	private String if_max_value;
	private int if_has_index = 0;
	private String if_description;
	private String if_unit;
	private String if_dialog_action1;
	private String if_dialog_action2;
	private String if_dialog_action3;
	private int if_noti_flag = 0;
	private int if_ui_type = 1;
	private int if_writable = 0;
	private int if_secured = 0;
	private String if_dialog_button1;
	private String if_dialog_button2;
	private String if_dialog_button3;
	private String if_dialog_msg;

	private ArrayList<InterfaceRangeObject> interface_range;

	public InterfaceObject() {

	}

	public InterfaceObject(InterfaceObject obj) {
		this._id = obj.get_id();
		this.if_name = obj.getIf_name();
		this.if_path = obj.getIf_path();
		this.if_type = obj.getIf_type();
		this.if_signal = obj.getIf_signal();
		this.if_device_id = obj.getIf_device_id();
		this.if_default_value = obj.getIf_default_value();
		this.if_min_value = obj.getIf_min_value();
		this.if_max_value = obj.getIf_max_value();
		this.if_has_index = obj.getIf_has_index();
		this.if_description = obj.getIf_description();
		this.if_unit = obj.getIf_unit();
		this.if_dialog_action1 = obj.getIf_dialog_action1();
		this.if_dialog_action2 = obj.getIf_dialog_action2();
		this.if_dialog_action3 = obj.getIf_dialog_action3();
		this.if_noti_flag = obj.getIf_noti_flag();

		this.interface_range = obj.getInterface_range();
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getIf_name() {
		return if_name;
	}

	public void setIf_name(String if_name) {
		this.if_name = if_name;
	}

	public String getIf_path() {
		return if_path;
	}

	public void setIf_path(String if_path) {
		this.if_path = if_path;
	}

	public String getIf_type() {
		return if_type;
	}

	public void setIf_type(String if_type) {
		this.if_type = if_type;
	}

	public String getIf_signal() {
		return if_signal;
	}

	public void setIf_signal(String if_signal) {
		this.if_signal = if_signal;
	}

	public int getIf_device_id() {
		return if_device_id;
	}

	public void setIf_device_id(int if_device_id) {
		this.if_device_id = if_device_id;
	}

	public String getIf_default_value() {
		return if_default_value;
	}

	public void setIf_default_value(String if_default_value) {
		this.if_default_value = if_default_value;
	}

	public String getIf_min_value() {
		return if_min_value;
	}

	public void setIf_min_value(String if_min_value) {
		this.if_min_value = if_min_value;
	}

	public String getIf_max_value() {
		return if_max_value;
	}

	public void setIf_max_value(String if_max_value) {
		this.if_max_value = if_max_value;
	}

	public int getIf_has_index() {
		return if_has_index;
	}

	public void setIf_has_index(int if_has_index) {
		this.if_has_index = if_has_index;
	}

	public String getIf_description() {
		return if_description;
	}

	public void setIf_description(String if_description) {
		this.if_description = if_description;
	}

	public String getIf_unit() {
		return if_unit;
	}

	public void setIf_unit(String if_unit) {
		this.if_unit = if_unit;
	}

	public ArrayList<InterfaceRangeObject> getInterface_range() {
		return interface_range;
	}

	public void setInterface_range(ArrayList<InterfaceRangeObject> interface_range) {
		this.interface_range = interface_range;
	}


	public String getIf_dialog_action1() {
		return if_dialog_action1;
	}

	public void setIf_dialog_action1(String if_dialog_action1) {
		this.if_dialog_action1 = if_dialog_action1;
	}

	public String getIf_dialog_action2() {
		return if_dialog_action2;
	}

	public void setIf_dialog_action2(String if_dialog_action2) {
		this.if_dialog_action2 = if_dialog_action2;
	}

	public String getIf_dialog_action3() {
		return if_dialog_action3;
	}

	public void setIf_dialog_action3(String if_dialog_action3) {
		this.if_dialog_action3 = if_dialog_action3;
	}

	public int getIf_noti_flag() {
		return if_noti_flag;
	}

	public void setIf_noti_flag(int if_noti_flag) {
		this.if_noti_flag = if_noti_flag;
	}

	public int getIf_ui_type() {
		return if_ui_type;
	}

	public void setIf_ui_type(int if_ui_type) {
		this.if_ui_type = if_ui_type;
	}

	public int getIf_writable() {
		return if_writable;
	}

	public void setIf_writable(int if_writable) {
		this.if_writable = if_writable;
	}

	public int getIf_secured() {
		return if_secured;
	}

	public void setIf_secured(int if_secured) {
		this.if_secured = if_secured;
	}

	public String getIf_dialog_button1() {
		return if_dialog_button1;
	}

	public void setIf_dialog_button1(String if_dialog_button1) {
		this.if_dialog_button1 = if_dialog_button1;
	}

	public String getIf_dialog_button2() {
		return if_dialog_button2;
	}

	public void setIf_dialog_button2(String if_dialog_button2) {
		this.if_dialog_button2 = if_dialog_button2;
	}

	public String getIf_dialog_button3() {
		return if_dialog_button3;
	}

	public void setIf_dialog_button3(String if_dialog_button3) {
		this.if_dialog_button3 = if_dialog_button3;
	}

	public String getIf_dialog_msg() {
		return if_dialog_msg;
	}

	public void setIf_dialog_msg(String if_dialog_msg) {
		this.if_dialog_msg = if_dialog_msg;
	}

}
