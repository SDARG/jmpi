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
package net.sf.jmpi.solver.sat4j;

import java.math.BigInteger;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.jmpi.main.MpConstraint;
import net.sf.jmpi.main.MpDirection;
import net.sf.jmpi.main.MpOperator;
import net.sf.jmpi.main.MpResult;
import net.sf.jmpi.main.MpResultImpl;
import net.sf.jmpi.main.MpVariable;
import net.sf.jmpi.main.expression.MpExpr;
import net.sf.jmpi.main.expression.MpExprTerm;
import net.sf.jmpi.solver.AbstractMpSolver;

import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.pb.ObjectiveFunction;
import org.sat4j.pb.PseudoOptDecorator;
import org.sat4j.pb.SolverFactory;
import org.sat4j.pb.core.PBSolverResolution;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;

public class SolverSAT4J extends AbstractMpSolver<Integer, Integer> {

	protected final PBSolverResolution solver;
	protected final SolverSAT4JVariableOrder variableOrder;
	protected int timeout = 3600 * 24;
	protected boolean variableOrderInitiliazed = false;

	public SolverSAT4J() {
		solver = SolverFactory.newPBResMixedConstraintsObjective();
		solver.newVar(100);
		
		variableOrder = new SolverSAT4JVariableOrder();
		solver.setOrder(variableOrder);
	}

	@Override
	public MpResult solve() {
		solver.setTimeout(timeout);
		

		boolean feasible = false;
		PseudoOptDecorator decorator = new PseudoOptDecorator(solver);
		decorator.newVar(varNum.get());
		try {
			if (decorator.hasNoObjectiveFunction()) {
				feasible = decorator.isSatisfiable();
			} else {
				while (decorator.admitABetterSolution()) {
					feasible = true;
					BigInteger rhs = toBigInteger(decorator.getObjectiveValue().intValue() - 1);
					ObjectiveFunction lhs = decorator.getObjectiveFunction();

					decorator.addPseudoBoolean(lhs.getVars(), lhs.getCoeffs(), false, rhs);
				}
			}

		} catch (ContradictionException e) {
		} catch (TimeoutException e) {
		}

		if (!feasible) {
			return null;
		} else {
			MpResult result;
			if (decorator.hasNoObjectiveFunction()) {
				result = new MpResultImpl();
			} else {
				result = new MpResultImpl(decorator.getObjectiveValue().intValue()
						* (optType == MpDirection.MAX ? -1 : 1));
			}
			for (Entry<Object, Integer> entry : objectToVar.entrySet()) {
				result.put(entry.getKey(), decorator.model(entry.getValue()) ? 1 : 0);
			}
			return result;
		}

	}
	
	public void setActivity(Object variable, double value){
		variableOrder.setVarActivity(getVar(variable), value);
	}
	
	public void setPhase(Object variable, boolean phase){
		variableOrder.setVarPhase(getVar(variable), phase);
	}

	@Override
	public void setTimeout(int value) {
		this.timeout = value;
	}

	@Override
	public void setVerbose(int value) {
		// do nothing
	}

	@Override
	protected void setObjective(MpExpr objective, MpDirection optType) {
		PBExpr result = convert(objective);

		BigInteger minusOne = new BigInteger("-1");

		if (optType == MpDirection.MAX) {
			IVec<BigInteger> invCoeffs = new Vec<BigInteger>();
			for (int i = 0; i < result.coeffs.size(); i++) {
				invCoeffs.push(result.coeffs.get(i).multiply(minusOne));
			}
			result.coeffs = invCoeffs;
		}

		solver.setObjectiveFunction(new ObjectiveFunction(result.literals, result.coeffs));
	}

	protected AtomicInteger con = new AtomicInteger(1);

	protected class PBExpr {
		IVecInt literals = new VecInt();
		IVec<BigInteger> coeffs = new Vec<BigInteger>();
	}

	protected PBExpr convert(MpExpr expr) {
		PBExpr result = new PBExpr();
		for (MpExprTerm term : expr) {
			int coeff = term.getCoeff().intValue();
			Object obj = term.getVars()[0];
			int var = getVar(obj);
			result.literals.push(var);
			result.coeffs.push(toBigInteger(coeff));
		}

		return result;
	}

	protected BigInteger toBigInteger(int value) {
		BigInteger bi = new BigInteger("" + value);
		return bi;
	}

	@Override
	protected Integer addConstraint(MpConstraint c) {
		MpConstraint normalized = normalize(c);

		PBExpr lhs = convert(normalized.getLhs());
		MpOperator op = c.getOperator();
		BigInteger rhs = toBigInteger((int) normalizedRhsValue(normalized));

		try {
			if (op == MpOperator.EQ || op == MpOperator.GE) {
				solver.addPseudoBoolean(lhs.literals, lhs.coeffs, true, rhs);
			}
			if (op == MpOperator.EQ || op == MpOperator.LE) {
				solver.addPseudoBoolean(lhs.literals, lhs.coeffs, false, rhs);
			}
		} catch (ContradictionException e) {
			throw new RuntimeException(e);
		}

		return con.getAndIncrement();
	}

	protected AtomicInteger var = new AtomicInteger(1);
	protected AtomicInteger varNum = new AtomicInteger(100);

	@Override
	protected Integer addVariable(MpVariable variable) {
		int i = var.getAndIncrement();
		if (i > varNum.get()) {
			varNum.addAndGet(100);
			solver.newVar(100);
		}
		return i;
	}

}
