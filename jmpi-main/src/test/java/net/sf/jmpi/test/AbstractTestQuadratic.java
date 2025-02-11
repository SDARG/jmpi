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
import net.sf.jmpi.main.MpDirection;
import net.sf.jmpi.main.MpProblem;
import net.sf.jmpi.main.MpResult;
import net.sf.jmpi.main.MpSolver;
import net.sf.jmpi.main.expression.MpExpr;

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractTestQuadratic {

	protected abstract MpSolver getSolver();

	@Test
	public void testIntegerPositive() {
		MpProblem problem = new MpProblem();

		MpExpr objective = sum("x");

		problem.addVar("x", Double.class);
		problem.addVar(-0.5, "y", 0.5, Double.class);

		problem.setObjective(objective, MpDirection.MAX);
		problem.add(sum(prod("x", "x"), prod("y", "y")), "<=", 1);

		// System.out.println(problem);

		MpSolver solver = getSolver();
		solver.add(problem);

		MpResult result = solver.solve();
		Assert.assertEquals(1.0, result.getObjective().doubleValue(), 0.0001);
	}

	public void testOneVoid() {
		MpProblem problem = new MpProblem();

		MpExpr objective = sum(prod("x1", "x1"), prod(0.1, "x2", "x2"),
				prod("x3", "x3"), prod(-1, "x1", "x3"), "x2");
		problem.setObjective(objective, MpDirection.MIN);

		problem.addVar(0, "x1", Double.class);
		problem.addVar(0, "x2", Double.class);
		problem.addVar(0, "x3", Double.class);

		problem.add(
				sum("x1", "x2", "x3", prod(-1, "x1", "x1"),
						prod(-1, "x2", "x2"), prod(-0.1, "x3", "x3"),
						prod(0.2, "x1", "x3")), ">=", 1);

		MpSolver solver = getSolver();
		solver.add(problem);

		MpResult result = solver.solve();
		Assert.assertEquals(0.4161924543450218, result.getObjective()
				.doubleValue(), 0.0001);
	}
	
	@Test
	public void testOne() {
		MpProblem problem = new MpProblem();
		problem.addVar(0, "x", 1000.0, Double.class);
		problem.addVar(0, "y", 1000.0, Double.class);
		problem.addVar("b", Boolean.class);
		problem.addVar(0, "i", 1000.0, Integer.class);


		problem.add(sum("x",-0.26, prod(-1,"y")), ">=", sum(prod(0.26, "b", "i")));

		MpSolver solver = getSolver();
		solver.add(problem);

		MpResult result = solver.solve();
		Assert.assertNotNull(result);
	}

	@Test
	public void testTwo() {
		MpProblem problem = new MpProblem();
		problem.addVar("x1", Boolean.class);
		problem.addVar("x2", Boolean.class);
		problem.addVar("x3", Boolean.class);

		problem.add(sum("x1"), "=", sum(1, prod(1, "x2", "x3")));

		MpSolver solver = getSolver();
		solver.add(problem);

		MpResult result = solver.solve();
		Assert.assertNotNull(result);
		Assert.assertTrue(result.getBoolean("x1"));
		Assert.assertFalse(result.getBoolean("x2") && result.getBoolean("x3"));
	}

}
