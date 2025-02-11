package net.sf.jmpi.main.expression;

public class MpVar implements MpTerminalExpr {

	protected final Object variable;

	public MpVar(Object variable) {
		super();
		this.variable = variable;
	}

	public Object getVariable() {
		return variable;
	}

	@Override
	public String toString() {
		return variable.toString();
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((variable == null) ? 0 : variable.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MpVar other = (MpVar) obj;
		if (variable == null) {
			if (other.variable != null)
				return false;
		} else if (!variable.equals(other.variable))
			return false;
		return true;
	}
	
	

}
