package net.sf.jmpi.main.expression;

import java.util.Iterator;

public class MpQuadTermImpl extends MpLinTermImpl implements MpQuadTerm {

	protected final MpVar var1;

	public MpQuadTermImpl(MpVal coeff, MpVar var0, MpVar var1) {
		super(coeff, var0);
		this.var1 = var1;
	}

	public MpQuadTermImpl(Number coeff, Object var0, Object var1) {
		super(coeff, var0);
		this.var1 = MpExprBuilder.var(var1);
	}

	public MpVal getCoeff() {
		return coeff;
	}

	public MpVar getFirstVar() {
		return getVar();
	}

	public MpVar getSecondVar() {
		return var1;
	}

	@Override
	public Iterator<MpTerminalExpr> iterator() {
		return new Iterator<MpTerminalExpr>() {
			int i = 0;

			@Override
			public boolean hasNext() {
				return i < 3;
			}

			@Override
			public MpTerminalExpr next() {
				i++;
				switch (i) {
				case 1:
					return coeff;
				case 2:
					return getFirstVar();
				case 3:
					return getSecondVar();
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
		return 3;
	}

	@Override
	public MpTermType getType() {
		return MpTermType.QUADRATIC;
	}

	@Override
	public String toString() {
		return coeff + "*" + getFirstVar() + "*" + getSecondVar();
	}

}
