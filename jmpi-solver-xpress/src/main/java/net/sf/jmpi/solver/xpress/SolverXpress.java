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
package net.sf.jmpi.solver.xpress;

import java.util.Map.Entry;

import net.sf.jmpi.main.MpConstraint;
import net.sf.jmpi.main.MpDirection;
import net.sf.jmpi.main.MpOperator;
import net.sf.jmpi.main.MpResult;
import net.sf.jmpi.main.MpResultImpl;
import net.sf.jmpi.main.MpVariable;
import net.sf.jmpi.main.expression.MpExpr;
import net.sf.jmpi.main.expression.MpExprTerm;
import net.sf.jmpi.solver.AbstractMpSolver;

import com.dashoptimization.XPRB;
import com.dashoptimization.XPRBctr;
import com.dashoptimization.XPRBexpr;
import com.dashoptimization.XPRBprob;
import com.dashoptimization.XPRBrelation;
import com.dashoptimization.XPRBvar;

public class SolverXpress extends AbstractMpSolver<XPRBvar, XPRBctr> {

	final XPRBprob problem;

	public SolverXpress() {
		super();
		XPRB xprb = new XPRB();
		problem = xprb.newProb("lala");
	}

	@Override
	public MpResult solve() {
		// problem.lpOptimize();
		problem.print();
		problem.mipOptimize();

		double objectiveValue = problem.getObjVal();
		MpResult result = new MpResultImpl(objectiveValue);

		for (Entry<XPRBvar, MpVariable> entry : variables.entrySet()) {
			XPRBvar nativeVariable = entry.getKey();
			MpVariable variable = entry.getValue();
			double value = nativeVariable.getSol();
			result.put(variable.getVar(), value);
		}

		return result;
	}

	@Override
	public void setTimeout(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVerbose(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setObjective(MpExpr objective, MpDirection optType2) {
		XPRBexpr nativeObjective = toXPRBexpr(objective);

		problem.setObj(nativeObjective);
		problem.setSense(optType2 == MpDirection.MAX ? XPRB.MAXIM : XPRB.MINIM);

	}

	@Override
	protected XPRBctr addConstraint(MpConstraint c) {
		XPRBexpr lhs = toXPRBexpr(c.getLhs());
		XPRBexpr rhs = toXPRBexpr(c.getRhs());

		MpOperator op = c.getOperator();

		XPRBrelation relation;

		switch (op) {
		case GE:
			relation = lhs.gEql(rhs);
			break;
		case LE:
			relation = lhs.lEql(rhs);
			break;
		case EQ:
			relation = lhs.eql(rhs);
			break;
		default:
			throw new RuntimeException("Invalid operator " + op);
		}

		XPRBctr nativeConstraint = problem.newCtr("c" + (i++), relation);
		return nativeConstraint;
	}

	protected int i = 0;

	@Override
	protected XPRBvar addVariable(MpVariable variable) {
		String id = variable.getVar().toString();
		Number lower = variable.getLower();
		Number upper = variable.getUpper();

		if (variable.getType() == MpVariable.Type.BOOL) {
			lower = 0;
			upper = 1;
		}

		int vartype = XPRB.UI;
		if (variable.getType() == MpVariable.Type.REAL) {
			vartype = XPRB.PL;
		}

		if (upper.doubleValue() == Double.MAX_VALUE) {
			upper = XPRB.INFINITY;
		}
		if (lower.doubleValue() == -Double.MAX_VALUE) {
			lower = -XPRB.INFINITY;
		}

		System.out.println(id + " " + lower + " " + upper + " " + XPRB.INFINITY);

		XPRBvar nativeVar = problem.newVar(id, vartype, lower.doubleValue(),
				upper.doubleValue());

		return nativeVar;
	}

	protected XPRBexpr toXPRBexpr(MpExpr expr) {

		XPRBexpr e = new XPRBexpr(0);

		for (MpExprTerm term : expr) {
			double coeff = term.getCoeff().doubleValue();
			switch (term.size()) {
			case 0:
				e.add(coeff);
				break;
			case 1:
				e.addTerm(coeff, getVar(term.var(0)));
				break;
			case 2: 
				e.addTerm(coeff, getVar(term.var(0)), getVar(term.var(1)));
				break;
			default: 
				throw new IllegalArgumentException("non linear: "+expr);
			}
		}

		return e;
	}
}
