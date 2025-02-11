/*******************************************************************************
 * Copyright (c) 2014 Opt4J
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/

package net.sf.jmpi.solver.sat4j;

import java.util.HashMap;
import java.util.Map;

import org.sat4j.core.LiteralsUtils;
import org.sat4j.minisat.orders.PhaseInLastLearnedClauseSelectionStrategy;
import org.sat4j.minisat.orders.VarOrderHeap;

/**
 * A {@link VariableOrder} implementation for the SAT4J interface. <br>
 * (not documented, see <a href="http://www.sat4j.org">Sat4J.org</a>)
 * 
 * @author lukasiewycz
 * 
 */
public class SolverSAT4JVariableOrder extends VarOrderHeap {

	private static final long serialVersionUID = 1L;

	protected Map<Integer, Double> priorities = new HashMap<Integer, Double>();
	protected Map<Integer, Boolean> phases = new HashMap<Integer, Boolean>();

	/**
	 * Constructs a {@link VariableOrder}.
	 */
	public SolverSAT4JVariableOrder() {
		super(new PhaseInLastLearnedClauseSelectionStrategy());
	}

	public void init() {
		final int length = this.activity.length;

		int[] phasetmp = new int[length];
		double[] activitytmp = new double[length];

		for (int i = 1; i < length; i++) {
			phasetmp[i] = this.phaseStrategy.select(i);
			activitytmp[i] = this.activity[i];
		}

		super.init();

		for (int i = 1; i < length; i++) {
			this.phaseStrategy.init(i, phasetmp[i]);
			this.activity[i] = activitytmp[i];
			if (heap.inHeap(i)) {
				heap.increase(i);
			}
		}

		for (Map.Entry<Integer, Double> priorityEntry : priorities.entrySet()) {
			int var = priorityEntry.getKey();
			double value = priorityEntry.getValue();

			activity[var] += value;
			if (heap.inHeap(var)) {
				heap.increase(var);
			}
		}

		for (Map.Entry<Integer, Boolean> phaseEntry : phases.entrySet()) {
			int var = phaseEntry.getKey();
			boolean value = phaseEntry.getValue();

			this.phaseStrategy.updateVar(value ? LiteralsUtils.posLit(var) : LiteralsUtils.negLit(var));
		}
		phases.clear();
		priorities.clear();
	}

	/**
	 * Sets the activity of a variable {@code var} to the specified
	 * {@code value}.
	 * 
	 * @param var
	 *            the variable
	 * @param value
	 *            the activity to set
	 */
	public void setVarActivity(int var, double value) {
		priorities.put(var, value);
	}

	/**
	 * Sets the {@code phase} of a variable {@code var}.
	 * 
	 * @param var
	 *            the variable
	 * @param phase
	 *            the phase
	 */
	public void setVarPhase(int var, boolean phase) {
		phases.put(var, phase);
	}
}
