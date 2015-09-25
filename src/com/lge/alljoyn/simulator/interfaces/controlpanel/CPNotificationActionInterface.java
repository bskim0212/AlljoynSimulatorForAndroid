package com.lge.alljoyn.simulator.interfaces.controlpanel;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

@BusInterface(name = "org.alljoyn.ControlPanel.NotificationAction", announced = "true")
public interface CPNotificationActionInterface {

	public static final int ID_MASK    = 0x04;
	/**
	 * @return Interface version
	 */
	@BusProperty(signature="q")
	public short getVersion() throws BusException;
	
	/**
	 * The controller needs to dismiss this NotificationAction panel
	 */
	@BusSignal
	public void Dismiss() throws BusException;
}
