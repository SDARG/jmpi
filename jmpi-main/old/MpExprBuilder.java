package net.sf.jmpi.main.expression;

import java.util.ArrayList;
import java.util.List;

import net.sf.jmpi.main.MpConstraint;
import net.sf.jmpi.main.MpProblem;

public class MpExprBuilder {

	protected static MpExpression[] interprete(Object... objects) {
		MpExpression[] expr = new MpExpression[objects.length];
		for (int i = 0; i < objects.length; i++) {
			Object object = objects[i];
			if (object instanceof MpExpression) {
				expr[i] = (MpExpression) object;
			} else if (object instanceof Number) {
				expr[i] = val((Number) object);
			} else {
				expr[i] = var(object);
			}
		}
		return expr;
	}

	public static MpConstTerm constTerm(Number coeff) {
		return new MpConstTermImpl(coeff);
	}

	public static MpLinSum linSum(MpLinTerm... linearTerms) {
		return new MpLinSum(linearTerms);
	}

	public static MpLinSum linSum(Object... linearTerms) {
		MpLinSum sum = new MpLinSum();
		for (Object object : linearTerms) {
			if (object instanceof MpTerm) {
				MpTerm term = (MpTerm) object;
				switch (term.getType()) {
				case CONSTANT:
				case LINEAR:
					sum.add((MpLinTerm) term);
					break;
				default:
					throw new IllegalArgumentException("cannot add quadratic term " + term
							+ " to linear sum");
				}
			} else if (object instanceof Number) {
				sum.add(term((Number) object));
			} else if (object instanceof MpExpression) {
				throw new IllegalArgumentException("cannot add expression " + object
						+ " to linear sum");
			} else {
				sum.add(term(object));
			}
		}
		return sum;
	}

	public static MpLinTerm term(Number number) {
		return new MpConstTermImpl(number);
	}

	public static MpLinTerm term(Object var) {
		return new MpLinTermImpl(1.0, var);
	}

	public static MpLinTerm linTerm(Object var) {
		return new MpLinTermImpl(1.0, var);
	}

	public static MpLinTerm term(Number coeff, Object var) {
		return new MpLinTermImpl(coeff, var);
	}

	public static MpLinTerm linTerm(Number coeff, Object var) {
		return new MpLinTermImpl(coeff, var);
	}

	public static MpQuadSum quadSum(MpQuadTerm... quadraticTerms) {
		return new MpQuadSum(quadraticTerms);
	}

	public static MpQuadTerm quadTerm(Number coeff, Object var0, Object var1) {
		return new MpQuadTermImpl(coeff, var0, var1);
	}

	public static MpQuadTerm quadTerm(Object var0, Object var1) {
		return new MpQuadTermImpl(1, var0, var1);
	}

	@Deprecated
	public static MpGeneralSum sum(Object... objects) {
		return new MpGeneralSum(interprete(objects));
	}

	@Deprecated
	public static MpGeneralProd prod(Object... objects) {
		return new MpGeneralProd(interprete(objects));
	}

	@Deprecated
	public static MpGeneralProd mul(Object... objects) {
		return new MpGeneralProd(interprete(objects));
	}

	@Deprecated
	public static MpVar var(Object object) {
		return new MpVar(object);
	}

	public static Object var(Object o0, Object o1, Object... objects) {
		List<Object> jointVar = new ArrayList<Object>();
		jointVar.add(o0);
		jointVar.add(o1);
		for (Object o : objects) {
			jointVar.add(o);
		}
		return jointVar;
	}

	@Deprecated
	public static MpVal val(Number number) {
		return new MpVal(number);
	}

	public static void checkVar(Object object) {
		if (object instanceof Number) {
			throw new IllegalArgumentException("Variable " + object + " is a number.");
		} else if (object instanceof MpExpression) {
			throw new IllegalArgumentException("Variable " + object + " is an expression.");
		}
	}



	public static MpIsBoolean isBooleanTrue = new MpIsBoolean() {
		@Override
		public boolean isBoolean(Object var) {
			return true;
		}
	};

	public static MpIsBoolean isBooleanFalse = new MpIsBoolean() {
		@Override
		public boolean isBoolean(Object var) {
			return false;
		}
	};

}
