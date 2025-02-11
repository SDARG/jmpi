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

import java.util.HashMap;
import java.util.Map;

/**
 * The class {@code ResultImpl} is a {@code Map} based implementation of the
 * {@link MpResult}.
 * 
 * @author lukasiewycz
 * 
 */
public class MpResultImpl implements MpResult {

	protected Map<Object, Number> primalValues;
	protected Number objectiveValue = null;

	/**
	 * Constructs a {@code ResultImpl} for a {@code Problem} without objective
	 * function.
	 */
	public MpResultImpl() {
		super();
		this.primalValues = new HashMap<Object, Number>();
	}

	/**
	 * Constructs a {@code ResultImpl} for a {@code Problem} with objective
	 * function and the optimal value.
	 */
	public MpResultImpl(Number objectiveValue) {
		super();
		this.primalValues = new HashMap<Object, Number>();
		this.objectiveValue = objectiveValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Result#getObjective()
	 */
	public Number getObjective() {
		return objectiveValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Result#getBoolean(java.lang.Object)
	 */
	public boolean getBoolean(Object key) {
		Number number = primalValues.get(key);
		double v = number.doubleValue();
		if (1.0E-8 > v && v > -1.0E-8) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Result#get(java.lang.Object)
	 */
	public Number get(Object key) {
		return primalValues.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Result#put(java.lang.Object, java.lang.Number)
	 */
	public void put(Object key, Number value) {
		primalValues.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.javailp.Result#containsVar(java.lang.Object)
	 */
	public Boolean containsVar(Object var) {
		return primalValues.containsKey(var);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractMap#toString()
	 */
	@Override
	public String toString() {
		return "Objective: " + getObjective() + " " + primalValues.toString();
	}

}