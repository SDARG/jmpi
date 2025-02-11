package net.sf.jmpi.main.expression;

public class MpVal implements MpTerminalExpr {

	protected final Number value;

	public MpVal(Number value) {
		super();
		this.value = value;
	}

	public Number getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public int size() {
		return 1;
	}

}
