package net.sf.jmpi.main.expression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MpQuadSum implements MpTermSum<MpQuadTerm> {

	protected List<MpQuadTerm> terms = new ArrayList<MpQuadTerm>();

	public MpQuadSum(MpQuadTerm... terms) {
		super();
		for (MpQuadTerm term : terms) {
			addTerm(term);
		}
	}

	public MpQuadSum addTerm(MpQuadTerm term) {
		terms.add(term);
		return this;
	}

	public MpQuadSum addConst(Number coeff) {
		return addTerm(MpExprBuilder.constTerm(coeff));
	}

	public MpQuadSum addLin(Number coeff, Object var) {
		MpExprBuilder.checkVar(var);
		return addTerm(MpExprBuilder.linTerm(coeff, var));
	}

	public MpQuadSum addLin(Object var) {
		MpExprBuilder.checkVar(var);
		return addTerm(MpExprBuilder.linTerm(1, var));
	}

	public MpQuadSum addQuad(Number coeff, Object var0, Object var1) {
		MpExprBuilder.checkVar(var0);
		MpExprBuilder.checkVar(var1);
		return addTerm(MpExprBuilder.quadTerm(coeff, var0, var1));
	}

	public MpQuadSum addQuad(Object var0, Object var1) {
		MpExprBuilder.checkVar(var0);
		MpExprBuilder.checkVar(var1);
		return addTerm(MpExprBuilder.quadTerm(1, var0, var1));
	}

	@Override
	public Iterator<MpQuadTerm> iterator() {
		return terms.iterator();
	}

	@Override
	public int size() {
		return terms.size();
	}

	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < terms.size(); i++) {
			s += terms.get(i);
			if (i < terms.size() - 1) {
				s += " + ";
			}
		}
		return s;
	}

}
