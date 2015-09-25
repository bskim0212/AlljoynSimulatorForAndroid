package com.lge.alljoyn.simulator.interfaces.controlpanel;


import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;

@BusInterface(name = "org.alljoyn.ControlPanel.HTTPControl", announced = "true")
public interface CPHttpInterface {

	public final String iName = "org.alljoyn.ControlPanel.HTTPControl";
	public static final int ID_MASK = 0x02;

	public static final short VERSION = 1;

	/**
	 * @return Interface version
	 */
	@BusProperty(signature = "q")
	public short getVersion() throws BusException;

	/**
	 * @return Retrieve the URL of the home page
	 * @throws BusException
	 */
	@BusMethod
	public String GetRootURL() throws BusException;
}
