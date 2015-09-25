package com.lge.alljoyn.simulator.interfaces.controlpanel;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

@BusInterface(name = "org.alljoyn.ControlPanel.Property")
public interface CPPropertyInterface {
	
	public final String iName = "org.alljoyn.ControlPanel.Property";
	
	public String getPath();
	
	@BusProperty(signature="q")
	public short getVersion() throws BusException;
	
	/**
	 * @return States bitmask 
	 * @throws BusException
	 */
	@BusProperty(signature="u")
	public int getStates() throws BusException;
	
	/**
	 * @return Optional parameters
	 * @throws BusException
	 */
	@BusProperty(signature="a{qv}")
	public Map<Short, Variant> getOptParams() throws BusException;
	
	/**
	 * @return Returns the property current value
	 */
	@BusProperty(signature="v")
	public Variant getValue() throws BusException;
	
	/**
	 * @param value The property value
	 */
	@BusProperty(signature="v")
	public void setValue(Variant value) throws BusException;

	/**
	 * Signal is sent when the property value changed 
	 * @param newValue The new property value
	 */
	@BusSignal(signature="v")
	public void ValueChanged(Variant newValue) throws BusException;
	
	/**
	 * Signal is sent when the property metadata changed 
	 * @param metadata
	 */
	@BusSignal
	public void MetadataChanged() throws BusException;
}
