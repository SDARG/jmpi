package net.sf.jmpi.main.expression;

import java.util.Iterator;

public class MpConstTermImpl implements MpConstTerm {

	protected final MpVal coeff;

	public MpConstTermImpl(MpVal coeff) {
		super();
		this.coeff = coeff;
	}

	public MpConstTermImpl(Number coeff) {
		super();
		this.coeff = MpExprBuilder.val(coeff);
	}

	public MpVal getCoeff() {
		return coeff;
	}

	@Override
	public Iterator<MpTerminalExpr> iterator() {
		return new Iterator<MpTerminalExpr>() {
			int i = 0;

			@Override
			public boolean hasNext() {
				return i < 1;
			}

			@Override
			public MpTerminalExpr next() {
				i++;
				switch (i) {
				case 1:
					return coeff;
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
		return 1;
	}

	@Override
	public MpTermType getType() {
		return MpTermType.CONSTANT;
	}

	@Override
	public MpVar getFirstVar() {
		throw new IllegalArgumentException("operation not supported");
	}

	@Override
	public MpVar getSecondVar() {
		throw new IllegalArgumentException("operation not supported");
	}
	
	@Override
	public String toString() {
		return ""+coeff;
	}

}
