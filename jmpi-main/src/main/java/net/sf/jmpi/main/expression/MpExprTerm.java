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

import java.util.Arrays;

public class MpExprTerm {

	protected final Number coeff;
	protected final Object[] vars;

	public MpExprTerm(Number coeff, Object... vars) {
		this.coeff = coeff;
		this.vars = vars;
	}

	public Number getCoeff() {
		return coeff;
	}

	public Object[] getVars() {
		return vars;
	}

	public int size() {
		return vars.length;
	}

	public Object var(int i) {
		return vars[i];
	}

	public MpExprTerm mul(Number number) {
		return new MpExprTerm(number.doubleValue() * this.coeff.doubleValue(), vars);
	}

	public MpExprTerm mul(Object object) {
		Object[] array = Arrays.copyOf(vars, vars.length + 1);
		array[vars.length] = object;
		return new MpExprTerm(this.coeff, array);
	}

	public MpExprTerm mul(MpExprTerm other) {
		Object[] array = new Object[this.size() + other.size()];
		for (int i = 0; i < this.size(); i++) {
			array[i] = this.var(i);
		}
		for (int i = 0; i < other.size(); i++) {
			array[i + this.size()] = other.var(i);
		}
		double coeff = this.getCoeff().doubleValue() * other.getCoeff().doubleValue();
		return new MpExprTerm(coeff, array);
	}

	@Override
	public String toString() {
		String s = "(";
		if (coeff.doubleValue() != 1 || vars.length == 0) {
			s += coeff;
			if (vars.length > 0) {
				s += " * ";
			}
		}

		for (int i = 0; i < vars.length; i++) {
			s += vars[i];
			if (i < vars.length - 1) {
				s += " * ";
			}
		}
		s += ")";
		return s;
	}

}
