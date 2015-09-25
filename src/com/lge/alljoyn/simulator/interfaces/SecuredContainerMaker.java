package com.lge.alljoyn.simulator.interfaces;

import java.util.HashMap;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Variant;

import com.lge.alljoyn.simulator.about.InterfaceObject;
import com.lge.alljoyn.simulator.interfaces.controlpanel.CPContainerSecuredInterface;

public class SecuredContainerMaker implements BusObject, CPContainerSecuredInterface {

	private short VERSION = (short)1;
	private int STATES = 0x01;
	private String label;
	public String iName = "org.alljoyn.ControlPanel.Container";
	
	public SecuredContainerMaker(String _label, InterfaceObject obj) {
		this.label = _label;
		if (obj.getIf_writable() == 0) {
			this.STATES = 0x00;
		}
	}
	
	
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
		test.put((short) 0, new Variant(label));
		test.put((short) 1, new Variant(0x400));
		test.put((short) 2, new Variant(new short[] { 1 }));
		return test;
	}


	@Override
	public void MetadataChanged() throws BusException {
		// TODO Auto-generated method stub

	}

}
