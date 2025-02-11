package net.sf.jmpi.main.expression;

public interface MpTerm extends MpProd<MpTerminalExpr> {
	
	public MpTermType getType();
	
	public MpVal getCoeff();
	
	public MpVar getFirstVar();
	
	public MpVar getSecondVar();

}
