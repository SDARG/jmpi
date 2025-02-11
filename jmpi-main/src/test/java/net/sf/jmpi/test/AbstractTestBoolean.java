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
package net.sf.jmpi.test;

import static net.sf.jmpi.main.expression.MpExpr.prod;
import static net.sf.jmpi.main.expression.MpExpr.sum;

import java.util.Random;

import net.sf.jmpi.main.MpDirection;
import net.sf.jmpi.main.MpProblem;
import net.sf.jmpi.main.MpResult;
import net.sf.jmpi.main.MpSolver;
import net.sf.jmpi.main.expression.MpExpr;

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractTestBoolean {

	protected abstract MpSolver getSolver();

	@Test
	public void testBooleanInfeasible() {
		MpProblem problem = new MpProblem();
		problem.addVar(0, "x", 1, Boolean.class);
		problem.addVar(0, "y", 1, Boolean.class);
		problem.addVar(0, "z", 1, Boolean.class);

		problem.add(sum("x", "y", "z"), "<=", 1);
		problem.add(sum("x", "y"), ">=", 1);
		problem.add(sum("x", "z"), ">=", 1);
		problem.add(sum("y", "z"), ">=", 1);
		problem.setObjective(sum("x", "y", "z"), MpDirection.MAX);

		MpSolver solver = getSolver();
		solver.add(problem);
		
		MpResult result = solver.solve();

		Assert.assertEquals(result, null);
	}
	
	
	@Test
	public void testBooleanSimpleEquality() {
		MpProblem problem = new MpProblem();
		problem.addVar(0, "x", 1, Boolean.class);
		problem.addVar(0, "y", 1, Boolean.class);

		problem.add(sum("x", "y"), "<=", 1);
		problem.setObjective(sum(prod(3, "x"), prod(4, "y")), MpDirection.MAX);

		MpSolver solver = getSolver();
		solver.add(problem);
		

		MpResult result = solver.solve();

		Assert.assertEquals(4.0, result.getObjective().doubleValue(), 0.000001);
	}

	@Test
	public void testMax() {

		MpProblem problem = getProblem(8, 0, MpDirection.MAX);
		MpSolver solver = getSolver();

		solver.add(problem);

		MpResult result = solver.solve();

		Assert.assertEquals(537.0, result.getObjective().doubleValue(), 0.000001);
	}

	@Test
	public void testMin() {

		MpProblem problem = getProblem(8, 0, MpDirection.MIN);
		MpSolver solver = getSolver();

		solver.add(problem);

		MpResult result = solver.solve();

		Assert.assertEquals(result.getObjective().doubleValue(), 219.0, 0.000001);
	}

	@Test
	public void testMinSmall() {
		MpProblem problem = getProblem(4, 0, MpDirection.MIN);
		MpSolver solver = getSolver();

		solver.add(problem);

		MpResult result = solver.solve();

		Assert.assertEquals(result.getObjective().doubleValue(), 183.0, 0.000001);
	}

	protected MpProblem getProblem(int size, int seed, MpDirection dir) {
		Random random = new Random(seed);

		MpProblem problem = new MpProblem();

		MpExpr objective = sum();

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int var = size * i + j;
				problem.addVar("x" + var, Boolean.class);
				objective.addTerm(random.nextInt(100), "x" + var);
			}
		}
		if (dir != null) {
			problem.setObjective(objective, dir);
		}

		for (int i = 0; i < size; i++) {
			MpExpr l1 = sum();
			MpExpr l2 = sum();
			for (int j = 0; j < size; j++) {
				l1.addTerm(1, "x" + (i * size + j));
				l2.addTerm(1, "x" + (j * size + i));
			}

			problem.add(l1, "=", 1);
			problem.add(l2, "=", 1);
		}

		for (int k = -size + 1; k < size; k++) {
			// diagonal 1
			MpExpr linear = sum();
			for (int j = 0; j < size; j++) {
				int i = k + j;
				if (0 <= i && i < size) {
					linear.addTerm(1, "x" + (i * size + j));
				}
			}
			problem.add(linear, "<=", 1);
		}

		for (int k = 0; k < 2 * size - 1; k++) {
			// diagonal 2
			MpExpr linear = sum();
			for (int j = 0; j < size; j++) {
				int i = k - j;
				if (0 <= i && i < size) {
					linear.addTerm(1, "x" + (i * size + j));
				}
			}
			problem.add(linear, "<=", 1);
		}
		// System.out.println(problem);
		return problem;
	}

}
