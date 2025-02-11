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

/**
 * The Type of Boolean operator.
 * 
 * @author lukasiewycz
 * 
 */
public enum MpOperator {

	/**
	 * Less equal
	 */
	LE,
	/**
	 * Equal
	 */
	EQ,
	/**
	 * Greater equal
	 */
	GE;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		switch (this) {
		case LE:
			return "<=";
		case GE:
			return ">=";
		default: // EQ
			return "=";
		}
	}

	public static MpOperator get(String op) {
		if (op.equals("<=")) {
			return LE;
		} else if (op.equals("=")) {
			return EQ;
		} else if (op.equals(">=")) {
			return GE;
		} else {
			throw new IllegalArgumentException("Unknown Boolean operator: " + op);
		}
	}

}
