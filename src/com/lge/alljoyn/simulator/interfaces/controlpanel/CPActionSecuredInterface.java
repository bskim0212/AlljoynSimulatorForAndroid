package com.lge.alljoyn.simulator.interfaces.controlpanel;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

@BusInterface(name = "org.alljoyn.ControlPanel.SecuredAction", announced = "true")
public interface CPActionSecuredInterface {
	
	public final String iName = "org.alljoyn.ControlPanel.SecuredAction";
	/**
	 * @return Interface version
	 */
	@BusProperty(signature = "q")
	public short getVersion() throws BusException;

	/**
	 * @return States bitmask
	 * @throws BusException
	 */
	@BusProperty(signature = "u")
	public int getStates() throws BusException;

	/**
	 * @return Optional parameters
	 * @throws BusException
	 */
	@BusProperty(signature = "a{qv}")
	public Map<Short, Variant> getOptParams() throws BusException;

	/**
	 * Called when the action is executed on the widget
	 */
	@BusMethod
	public void Exec() throws BusException;

	/**
	 * Signal is sent when the UI container metadata changed
	 * 
	 * @param metadata
	 */
	@BusSignal
	public void MetadataChanged() throws BusException;
}
