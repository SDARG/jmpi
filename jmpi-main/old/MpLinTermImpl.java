package net.sf.jmpi.main.expression;

import java.util.Iterator;

public class MpLinTermImpl extends MpConstTermImpl implements MpLinTerm {

	protected final MpVar var;

	public MpLinTermImpl(MpVal coeff, MpVar var) {
		super(coeff);
		this.var = var;
	}
	
	public MpLinTermImpl(Number coeff, Object var) {
		super(coeff);
		this.var = MpExprBuilder.var(var);
	}

	public MpVar getVar() {
		return var;
	}

	@Override
	public Iterator<MpTerminalExpr> iterator() {
		return new Iterator<MpTerminalExpr>() {
			int i = 0;

			@Override
			public boolean hasNext() {
				return i < 2;
			}

			@Override
			public MpTerminalExpr next() {
				i++;
				switch (i) {
				case 1:
					return coeff;
				case 2:
					return var;
				default:
					return null;
				}
			}

			@Override
			public void remove() {
				throw new IllegalArgumentException("operation not supported");
			}
		};
	}

	@Override
	public int size() {
		return 2;
	}
	
	@Override
	public MpTermType getType() {
		return MpTermType.LINEAR;
	}

	@Override
	public MpVar getFirstVar() {
		return getVar();
	}

	@Override
	public MpVar getSecondVar() {
		throw new IllegalArgumentException("operation not supported");
	}

	@Override
	public String toString() {
		return coeff +"*"+getFirstVar();
	}
	
	

}
