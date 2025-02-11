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
 * The class {@code Result} is a result of a {@code Problem}.
 * 
 * @author lukasiewycz
 * 
 */
public interface MpResult {

	/**
	 * Returns the objective value.
	 * 
	 * @return the objective value
	 */
	public Number getObjective();

	/**
	 * Returns the primal value for a specific var as a boolean. (value!=0)
	 * 
	 * @param var
	 *            the var
	 * @return {@code true} if the value is not 0
	 */
	public boolean getBoolean(Object var);

	/**
	 * Returns the primal value of the variable.
	 * 
	 * @param var
	 *            the variable
	 * @return the resulting value
	 */
	public Number get(Object var);
	
	/**
	 * Sets the primal value of the variable.
	 * 
	 * @param var
	 *            the variable
	 * @param value
	 *            the value
	 */
	public void put(Object var, Number value);

	/**
	 * Returns {@code true} if the result contains the variable.
	 * 
	 * @param var
	 *            the variable
	 * @return {@code true} if the result contains the variable
	 */
	public Boolean containsVar(Object var);

}
