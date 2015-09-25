package com.lge.alljoyn.simulator.interfaces.controlpanel;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

@BusInterface (name = "org.alljoyn.ControlPanel.SecuredDialog" , announced="true")
public interface CPDialogSecuredInterface {

	/**
	 * @return Interface version
	 */
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
	public Map<Short,Variant> getOptParams() throws BusException;
    
	/**
	 * @return Returns the dialog message
	 * @throws BusException
	 */
	@BusProperty(signature="s")
	public String getMessage() throws BusException;
	
	/**
	 * @return Returns the number of the dialog buttons
	 * @throws BusException
	 */
	@BusProperty(signature="q")
	public short getNumActions() throws BusException;
	
	/**
	 * Call the method if is relevant
	 */
	@BusMethod
	public void Action1() throws BusException;;
	
	/**
	 * Call the method if is relevant
	 */
	@BusMethod
	public void Action2() throws BusException;

	/**
	 * Call the method if is relevant
	 */
	@BusMethod
	public void Action3() throws BusException;

	/**
	 * Signal is sent when the UI container metadata changed 
	 * @param metadata
	 */
	@BusSignal
	public void MetadataChanged() throws BusException;
}
