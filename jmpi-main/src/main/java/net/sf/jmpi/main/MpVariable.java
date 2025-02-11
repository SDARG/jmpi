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

public class MpVariable {

	protected final Object var;
	protected final Number lower;
	protected final Number upper;
	protected final Type type;

	/**
	 * The variable type.
	 * 
	 * @author lukasiewycz
	 * 
	 */
	public enum Type {

		/**
		 * Boolean
		 */
		BOOL,
		/**
		 * Integer
		 */
		INT,
		/**
		 * Real
		 */
		REAL;

		public boolean isInt() {
			switch (this) {
			case BOOL:
			case INT:
				return true;
			default:
				return false;
			}
		}

		@Override
		public String toString() {
			switch (this) {
			case BOOL:
				return "binary";
			case INT:
				return "integer";
			default: // REAL
				return "continuous";
			}
		}

		public static Type get(Class<?> type) {
			if (type.equals(Integer.class)) {
				return INT;
			} else if (type.equals(Boolean.class)) {
				return BOOL;
			} else if (type.equals(Double.class)) {
				return REAL;
			} else {
				throw new IllegalArgumentException();
			}
		}
	}

	public MpVariable(Object var, Number lower, Number upper, Type type) {
		super();
		this.var = var;
		this.lower = lower;
		this.upper = upper;
		this.type = type;
	}

	public Object getVar() {
		return var;
	}

	public Number getLower() {
		return lower;
	}

	public Number getUpper() {
		return upper;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		String s = "";
		if (lower.doubleValue() > -Double.MAX_VALUE) {
			s += lower + " <= ";
		}
		s += var;
		if (upper.doubleValue() < Double.MAX_VALUE) {
			s += " <= " + upper;
		}

		return s + " : " + type;
	}

}
