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
package net.sf.jmpi.main;

import net.sf.jmpi.main.expression.MpExpr;

/**
 * The class {@code Constraint} represent a linear constraint.
 * 
 * @author lukasiewycz
 * 
 */
public class MpConstraint {

	protected final MpExpr lhs;
	protected final MpOperator operator;
	protected final MpExpr rhs;

	/**
	 * Constructs a {@code Constraint}.
	 * 
	 * @param lhs
	 *            the left hand side
	 * @param operator
	 *            the operator
	 * @param rhs
	 *            the right hand side
	 */
	public MpConstraint(MpExpr lhs, MpOperator operator, MpExpr rhs) {
		this.lhs = lhs;
		this.operator = operator;
		this.rhs = rhs;
	}

	/**
	 * Returns the left-hand-side expression.
	 * 
	 * @return the left-hand-side expression
	 */
	public MpExpr getLhs() {
		return lhs;
	}

	/**
	 * Returns the Boolean operator.
	 * 
	 * @return the Boolean operator
	 */
	public MpOperator getOperator() {
		return operator;
	}

	/**
	 * Returns the right-hand-side expression.
	 * 
	 * @return the right-hand-side expression
	 */
	public MpExpr getRhs() {
		return rhs;
	}

	/**
	 * Returns the size of the linear expression.
	 * 
	 * @return the size
	 */
	/*
	 * public int size() { return lhs.size(); }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return lhs + " " + operator + " " + rhs;
	}

}
