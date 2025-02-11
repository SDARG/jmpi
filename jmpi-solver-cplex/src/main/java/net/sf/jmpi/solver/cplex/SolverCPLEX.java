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
package net.sf.jmpi.solver.cplex;

import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.concert.IloObjectiveSense;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.DoubleParam;

import java.util.ArrayList;
import java.util.List;

import net.sf.jmpi.main.MpConstraint;
import net.sf.jmpi.main.MpDirection;
import net.sf.jmpi.main.MpOperator;
import net.sf.jmpi.main.MpResult;
import net.sf.jmpi.main.MpResultImpl;
import net.sf.jmpi.main.MpSolver;
import net.sf.jmpi.main.MpVariable;
import net.sf.jmpi.main.expression.MpExpr;
import net.sf.jmpi.main.expression.MpExprTerm;
import net.sf.jmpi.solver.AbstractMpSolver;

public class SolverCPLEX extends AbstractMpSolver<IloNumVar, IloConstraint> implements MpSolver {

	int constraintId = 0;
	protected final IloCplex cplex;

	public SolverCPLEX() {
		super();
		try {
			cplex = new IloCplex();
			cplex.addMinimize();
			setVerbose(0);
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	protected IloNumVar addVariable(MpVariable variable) {
		IloNumVarType type;
		switch (variable.getType()) {
		case BOOL:
			type = IloNumVarType.Bool;
			break;
		case INT:
			type = IloNumVarType.Int;
			break;
		default: // REAL
			type = IloNumVarType.Float;
			break;
		}

		try {
			double lower = variable.getLower().doubleValue();
			double upper = variable.getUpper().doubleValue();

			IloNumVar var = cplex.numVar(lower, upper, type, variable.getVar().toString());

			return var;
		} catch (IloException e) {
			e.printStackTrace();
			return null;
		}
	}

	public double getValue(Object object) {
		IloNumVar var = objectToVar.get(object);

		try {
			double value = cplex.getValue(var);
			if (variables.get(objectToVar.get(object)).getType() != MpVariable.Type.REAL) {
				value = Math.round(value);
			}
			return value;
		} catch (IloException e) {
			System.err.println(object + " " + var);
			throw new RuntimeException(e);
		}
	}

	public boolean getBoolean(Object object) {
		return getValue(object) > 0;
	}

	public MpResult solve() {

		try {
			boolean ret = cplex.solve();

			if (!ret) {
				return null;
			}

			double objValue = 0;
			if (objectiveFunction.size() > 0) {
				objValue = cplex.getObjValue();
			}

			MpResult result = new MpResultImpl(objValue);

			for (Object var : objectToVar.keySet()) {
				double primalValue = getValue(var);
				result.put(var, primalValue);
			}

			return result;

		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	public void setTimeout(int value) {
		try {
			cplex.setParam(DoubleParam.TiLim, value);
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

	public void setVerbose(int value) {
		if (value == 0) {
			cplex.setOut(null);
		} else {
			cplex.setOut(System.out);
		}
	}

	public void setObjective(MpExpr expression, MpDirection type) {
		try {
			if (type == MpDirection.MIN) {
				cplex.getObjective().setSense(IloObjectiveSense.Minimize);
			} else {
				cplex.getObjective().setSense(IloObjectiveSense.Maximize);
			}
			cplex.getObjective().clearExpr();
			cplex.getObjective().setExpr(toExpression(expression));
		} catch (IloException e) {
			e.printStackTrace();
		}
		objectiveFunction = expression;
	}

	// assume its normalized
	protected IloConstraint addConstraint(MpConstraint constraint) {
		IloNumExpr lhsExpr = toExpression(constraint.getLhs());
		IloNumExpr rhsExpr = toExpression(constraint.getRhs());

		try {
			MpOperator op = constraint.getOperator();

			IloConstraint con = null;

			switch (op) {
			case GE:
				con = cplex.addGe(lhsExpr, rhsExpr, "c" + (constraintId++));
				break;
			case LE:
				con = cplex.addLe(lhsExpr, rhsExpr, "c" + (constraintId++));
				break;
			case EQ:
				con = cplex.addEq(lhsExpr, rhsExpr, "c" + (constraintId++));
				break;
			default:
				throw new RuntimeException("Invalid operator " + op);
			}

			return con;

		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	// assume its normalized
	@SuppressWarnings("unchecked")
	protected IloNumExpr toExpression(MpExpr expression) {
		try {
			List<IloNumExpr> expr = new ArrayList<IloNumExpr>();

			for (MpExprTerm term : expression) {
				double coeff = term.getCoeff().doubleValue();
				switch (term.size()) {
				case 0:
					expr.add(cplex.constant(coeff));
					break;
				case 1:
					expr.add(cplex.prod(coeff, getVar(term.var(0))));
					break;
				case 2:
					expr.add(cplex.prod(coeff, getVar(term.var(0)), getVar(term.var(1))));
					break;
				default:
					throw new IllegalArgumentException("non linear: " + expr);

				}
			}

			IloNumExpr[] array = expr.toArray(new IloNumExpr[0]);

			return cplex.sum(array);

		} catch (IloException e) {
			throw new RuntimeException(e);
		}

	}
}
