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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.sf.jmpi.main.MpDirection;
import net.sf.jmpi.main.MpProblem;
import net.sf.jmpi.main.MpResult;
import net.sf.jmpi.main.MpSolver;
import net.sf.jmpi.main.expression.MpExpr;

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractTestLinear {

	protected abstract MpSolver getSolver();

	@Test
	public void testIntegerPositive() {
		Random random = new Random(0);

		int VARS = 100;
		int CONS = 100;
		MpProblem problem = new MpProblem();

		MpExpr objective = sum();

		for (int i = 0; i < VARS; i++) {
			problem.addVar(0, "x" + i, Integer.class);
			objective.add("x" + i);
		}

		for (int c = 0; c < CONS; c++) {
			MpExpr expr = sum();
			for (int i = 0; i < 10; i++) {
				expr.addTerm(1 + random.nextInt(5), "x" + random.nextInt(VARS));
			}
			problem.add(expr, "<=", 1 + random.nextInt(50));
		}

		problem.setObjective(objective, MpDirection.MAX);

		MpSolver solver = getSolver();
		solver.add(problem);

		MpResult result = solver.solve();

		Assert.assertEquals(53.0, result.getObjective().doubleValue(), 0.00000001);
	}

	@Test
	public void testSimple() {

		/**
		 * Constructing a Problem: Maximize: 143x+60y Subject to: 120x+210y <=
		 * 15000 110x+30y <= 4000 x+y <= 75
		 * 
		 * With x,y being integers
		 * 
		 */

		MpProblem problem = new MpProblem();
		problem.addVar("x", Integer.class);
		problem.addVar("y", Integer.class);

		MpExpr objective = sum(prod(143, "x"), prod(60, "y"));
		problem.setObjective(objective, MpDirection.MAX);

		problem.add(sum(prod(100, "x"), prod(20, "x"), prod(210, "y")), "<=", 15000);
		problem.add(sum(prod(110, "x"), prod(30, "y")), "<=", 4000);
		problem.add(sum("x"), "<=", sum(75, prod(-1, "y")));

		MpSolver solver = getSolver();
		solver.add(problem);

		MpResult result = solver.solve();
		Assert.assertEquals(6266.0, result.getObjective().doubleValue(), 0.00000001);
	}

	@Test
	public void testIntegerPositiveSmall() {
		Random random = new Random(0);

		int VARS = 10;
		int CONS = 10;
		MpProblem problem = new MpProblem();

		MpExpr objective = sum();

		for (int i = 0; i < VARS; i++) {
			problem.addVar(0, "x" + i, Integer.class);
			objective.add("x" + i);
		}

		for (int c = 0; c < CONS; c++) {
			MpExpr expr = sum();
			for (int i = 0; i < 10; i++) {
				expr.addTerm(random.nextDouble(), "x" + random.nextInt(VARS));
			}
			problem.add(expr, "<=", 20 * random.nextDouble());
		}

		problem.setObjective(objective, MpDirection.MAX);

		MpSolver solver = getSolver();
		solver.add(problem);

		MpResult result = solver.solve();
		Assert.assertEquals(result.getObjective().doubleValue(), 4.0, 0.00000001);
	}

}
