/** 
 * Copyright (c) 2014 JMPI - Java Mathematical Programming Interface
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE. 
 */
package net.sf.jmpi.main.expression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MpExpr implements Iterable<MpExprTerm> {

	List<MpExprTerm> terms = new ArrayList<MpExprTerm>();

	public void addTerm(MpExprTerm term) {
		this.terms.add(term);
	}

	protected List<MpExprTerm> mulTerm(List<MpExprTerm> terms, MpExprTerm term) {
		List<MpExprTerm> result = new ArrayList<MpExprTerm>();
		if (term.getCoeff().doubleValue() == 0) {
			return result;
		} else {
			for (MpExprTerm t : terms) {
				result.add(term.mul(t));
			}
		}
		return result;
	}

	public MpExpr addTerm(Object... objects) {
		double coeff = 1;
		List<Object> vars = new ArrayList<Object>();
		for (int i = 0; i < objects.length; i++) {
			Object object = objects[i];
			if (object instanceof MpExpr) {
				throw new IllegalArgumentException("illegal argument " + object);
			} else if (object instanceof Number) {
				coeff *= ((Number) object).doubleValue();
			} else { // variable
				vars.add(object);
			}
		}
		if (coeff != 1 || !vars.isEmpty()) {
			Object[] array = new Object[vars.size()];
			for (int i = 0; i < vars.size(); i++) {
				array[i] = vars.get(i);
			}

			addTerm(new MpExprTerm(coeff, array));
		}

		return this;
	}

	public MpExpr add(Object... objects) {
		for (int i = 0; i < objects.length; i++) {
			Object object = objects[i];
			if (object instanceof MpExpr) {
				for (MpExprTerm term : (MpExpr) object) {
					addTerm(term);
				}
			} else if (object instanceof Number) {
				addTerm(new MpExprTerm((Number) object));
			} else { // variable
				addTerm(new MpExprTerm(1, object));
			}
		}
		return this;
	}

	public MpExpr mul(Object... objects) {
		double coeff = 1;
		List<Object> vars = new ArrayList<Object>();
		for (int i = 0; i < objects.length; i++) {
			Object object = objects[i];
			if (object instanceof MpExpr) {
				List<MpExprTerm> org = new ArrayList<MpExprTerm>(terms);
				terms.clear();
				for (MpExprTerm t0 : org) {
					for (MpExprTerm t1 : ((MpExpr) object)) {
						addTerm(t0.mul(t1));
					}
				}

			} else if (object instanceof Number) {
				coeff *= ((Number) object).doubleValue();
			} else { // variable
				vars.add(object);
			}
		}
		if (coeff != 1 || !vars.isEmpty()) {
			Object[] array = new Object[vars.size()];
			for (int i = 0; i < vars.size(); i++) {
				array[i] = vars.get(i);
			}

			List<MpExprTerm> newTerms = mulTerm(new ArrayList<MpExprTerm>(terms),
					new MpExprTerm(coeff, array));
			terms = newTerms;
		}

		return this;
	}

	public static MpExpr sum(Object... objects) {
		MpExpr e = new MpExpr();
		e.add(objects);
		return e;
	}

	public static MpExpr prod(Object... objects) {
		MpExpr e = new MpExpr();
		e.add(1);
		e.mul(objects);
		return e;
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

	protected static MpExpr[] interprete(Object... objects) {
		MpExpr[] expr = new MpExpr[objects.length];
		for (int i = 0; i < objects.length; i++) {
			Object object = objects[i];
			if (object instanceof MpExpr) {
				expr[i] = (MpExpr) object;
			} else if (object instanceof Number) {
				expr[i] = sum(prod(object));
			}
		}
		return expr;
	}

	@Override
	public Iterator<MpExprTerm> iterator() {
		return terms.iterator();
	}

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

	public int getOrder() {
		int i = 0;
		for (MpExprTerm term : this) {
			i = Math.max(i, term.size());
		}
		return i;
	}

	public MpExprType type() {
		switch (getOrder()) {
		case 0:
		case 1:
			return MpExprType.LINEAR;
		case 2:
			return MpExprType.QUADRATIC;
		default:
			return MpExprType.MORE;
		}

	}

	public static void main(String[] args) {

		MpExpr e = sum(1, prod(3, "x2"), 4, "x1");
		MpExpr e2 = sum(10, prod("x1"));

		System.out.println(prod(e, e2, 4.5));

	}

}
