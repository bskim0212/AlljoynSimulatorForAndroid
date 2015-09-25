package com.lge.alljoyn.simulator.interfaces.controlpanel;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusProperty;

@BusInterface(name="org.alljoyn.ControlPanel.ControlPanel")
public interface CPControlPanelInterface {
	
	/**
	 * @return Interface version
	 */
	@BusProperty(signature="q")
	public short getVersion() throws BusException;
}
