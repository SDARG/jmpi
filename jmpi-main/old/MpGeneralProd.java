package net.sf.jmpi.main.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MpGeneralProd implements MpProd<MpExpression> {

	protected List<MpExpression> elements = new ArrayList<MpExpression>(1);

	public MpGeneralProd() {

	}

	public MpGeneralProd(MpExpression... elements) {
		this.elements.addAll(Arrays.asList(elements));
	}

	@Override
	public Iterator<MpExpression> iterator() {
		return elements.iterator();
	}

	public MpGeneralProd mult(MpExpression expression) {
		if (expression instanceof MpGeneralProd) {
			for (MpExpression e : (MpGeneralProd) expression) {
				mult(e);
			}
		} else {
			elements.add(expression);
		}
		return this;
	}

	public List<MpExpression> getAll() {
		return elements;
	}

	public int size() {
		return elements.size();
	}

	@Override
	public String toString() {
		String s = "( ";
		for (int i = 0; i < elements.size(); i++) {
			s += elements.get(i);
			if (i < elements.size() - 1) {
				s += " * ";
			}
		}
		s += " )";
		return s;
	}

}
