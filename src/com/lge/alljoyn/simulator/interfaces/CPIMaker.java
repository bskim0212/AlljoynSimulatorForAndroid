package com.lge.alljoyn.simulator.interfaces;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;

import com.lge.alljoyn.simulator.interfaces.controlpanel.CPControlPanelInterface;

public class CPIMaker implements BusObject, CPControlPanelInterface{

	
	private short VERSION = (short)1;
	public String iName = "org.alljoyn.ControlPanel.ControlPanel";
	
	@Override
	public short getVersion() throws BusException {
		// TODO Auto-generated method stub
		return VERSION;
	}

}
