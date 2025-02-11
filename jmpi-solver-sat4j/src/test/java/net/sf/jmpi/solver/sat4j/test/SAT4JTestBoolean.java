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
package net.sf.jmpi.solver.sat4j.test;

import static net.sf.jmpi.main.expression.MpExpr.prod;
import static net.sf.jmpi.main.expression.MpExpr.sum;
import net.sf.jmpi.main.MpDirection;
import net.sf.jmpi.main.MpProblem;
import net.sf.jmpi.main.MpResult;
import net.sf.jmpi.main.MpSolver;
import net.sf.jmpi.main.expression.MpExpr;
import net.sf.jmpi.solver.sat4j.SolverSAT4J;
import net.sf.jmpi.test.AbstractTestBoolean;

import org.junit.Assert;
import org.junit.Test;

public class SAT4JTestBoolean extends AbstractTestBoolean {

	@Override
	protected MpSolver getSolver() {
		return new SolverSAT4J();
	}

	public static void main(String[] args) {
		large();
	}

	public static void test() {
		MpProblem problem = new MpProblem();
		problem.addVar(0, "x", 1, Boolean.class);
		problem.addVar(0, "y", 1, Boolean.class);

		problem.add(sum("x", "y"), ">=", 1);
		problem.setObjective(sum(prod(3, "x"), prod(4, "y")), MpDirection.MIN);

		MpSolver solver = new SolverSAT4J();
		solver.add(problem);

		MpResult result = solver.solve();
		System.out.println(result);

		Assert.assertEquals(3.0, result.getObjective().doubleValue(), 0.000001);
	}
	
	public static void large() {
		MpProblem problem = new MpProblem();
		
		for(int i=0;i<100;i++){
			problem.addVar(0, "x"+i, 1, Boolean.class);
			problem.addVar(0, "y"+i, 1, Boolean.class);
			problem.addVar(0, "z"+i, 1, Boolean.class);
			problem.add(sum("x"+i, "y"+i), ">=", sum("z"+i));
		}

		MpSolver solver = new SolverSAT4J();
		solver.add(problem);

		MpResult result = solver.solve();
		System.out.println(result);
	}

	public static void old() {
		SAT4JTestBoolean t = new SAT4JTestBoolean();
		t.testBooleanInfeasible();
	}
	
	@Test
	public void testPhaseSetting() {
		MpProblem problem = new MpProblem();
		problem.addVar(0, "x", 1, Boolean.class);
		problem.addVar(0, "y", 1, Boolean.class);

		problem.add(sum("x", "y"), ">=", 0);

		SolverSAT4J solver = new SolverSAT4J();
		solver.add(problem);
		
		solver.setPhase("x", true);
		solver.setPhase("y", true);
		solver.setActivity("x", 1000.0);
		solver.setActivity("y", 1000.0);

		MpResult result = solver.solve();

		Assert.assertEquals(1.0, result.get("x").doubleValue(), 0.000001);
		Assert.assertEquals(1.0, result.get("y").doubleValue(), 0.000001);
		
		solver.setPhase("x", false);
		solver.setPhase("y", false);

		result = solver.solve();
		
		Assert.assertEquals(0.0, result.get("x").doubleValue(), 0.000001);
		Assert.assertEquals(0.0, result.get("y").doubleValue(), 0.000001);
		
		MpProblem problemEx= new MpProblem();
		problemEx.add(sum("x", "y"), ">=", 1);
		solver.add(problemEx);

		solver.setActivity("x", 1000.0);
		solver.setActivity("y", 1000000.0);

		result = solver.solve();

		Assert.assertEquals(1.0, result.get("x").doubleValue(), 0.000001);
		Assert.assertEquals(0.0, result.get("y").doubleValue(), 0.000001);


		solver.setActivity("x", 1000000.0);
		solver.setActivity("y", 1000.0);

		result = solver.solve();

		Assert.assertEquals(0.0, result.get("x").doubleValue(), 0.000001);
		Assert.assertEquals(1.0, result.get("y").doubleValue(), 0.000001);
		
		problemEx= new MpProblem();
		problemEx.addVar("z", Boolean.class);
		problemEx.add(sum("x", "y", "z"), ">=", 2);
		
		for(int i=0;i<10000;i++){
			problemEx.addVar("z"+i, Boolean.class);
		}
		
		solver.add(problemEx);
		

		solver.setActivity("x", 1000000.0);
		solver.setActivity("y", 1000.0);

		result = solver.solve();

		Assert.assertEquals(0.0, result.get("x").doubleValue(), 0.000001);
		Assert.assertEquals(1.0, result.get("y").doubleValue(), 0.000001);

	
	}
	
	@Test
	public void testAcivitySetting() {
		MpProblem problem = new MpProblem();
		problem.addVar(0, "x", 1, Boolean.class);
		problem.addVar(0, "y", 1, Boolean.class);

		problem.add(sum("x", "y"), "<=", 1);

		SolverSAT4J solver = new SolverSAT4J();
		solver.add(problem);
		
		solver.setPhase("x", true);
		solver.setPhase("y", true);
		solver.setActivity("x", 1000.0);

		MpResult result = solver.solve();
		System.out.println(result);

		Assert.assertEquals(1.0, result.get("x").doubleValue(), 0.000001);
		
		problem = new MpProblem();
		
		MpExpr sum = sum();
		for(int i=0;i<1000;i++){
			problem.addVar("y"+i, Boolean.class);
			sum.add("y"+i);
		}
		sum.add("x");
		problem.add(sum, "<=", 1);
		
		solver.add(problem);
		
		result = solver.solve();
		System.out.println(result);

		Assert.assertEquals(1.0, result.get("x").doubleValue(), 0.000001);

	}

}
