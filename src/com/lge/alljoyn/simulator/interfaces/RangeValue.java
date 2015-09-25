package com.lge.alljoyn.simulator.interfaces;

import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.Position;

public class RangeValue {
	public RangeValue() {

	}

	public RangeValue(Variant _value, String _label) {
		this.Value = _value;
		this.Label = _label;
	}

	@Position(0)
	public Variant Value;
	@Position(1)
	public String Label;
}
