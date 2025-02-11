package net.sf.jmpi.main.expression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MpLinSum implements MpTermSum<MpLinTerm> {

	protected List<MpLinTerm> terms = new ArrayList<MpLinTerm>();

	public MpLinSum(MpLinTerm... terms) {
		super();
		for (MpLinTerm term : terms) {
			add(term);
		}
	}

	public MpLinSum add(MpLinTerm term) {
		terms.add(term);
		return this;
	}
	
	public MpLinSum addConst(Number coeff) {
		return add(MpExprBuilder.constTerm(coeff));
	}

	public MpLinSum addLin(Number coeff, Object var) {
		MpExprBuilder.checkVar(var);
		return add(MpExprBuilder.linTerm(coeff, var));
	}
	
	public MpLinSum addLin(Object var) {
		MpExprBuilder.checkVar(var);
		return add(MpExprBuilder.linTerm(1, var));
	}

	@Override
	public Iterator<MpLinTerm> iterator() {
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
