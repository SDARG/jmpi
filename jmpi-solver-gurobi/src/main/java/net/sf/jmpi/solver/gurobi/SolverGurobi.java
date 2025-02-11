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
package net.sf.jmpi.solver.gurobi;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBExpr;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBQuadExpr;
import gurobi.GRBVar;

import java.util.Map.Entry;

import net.sf.jmpi.main.MpConstraint;
import net.sf.jmpi.main.MpDirection;
import net.sf.jmpi.main.MpOperator;
import net.sf.jmpi.main.MpResult;
import net.sf.jmpi.main.MpResultImpl;
import net.sf.jmpi.main.MpSolver;
import net.sf.jmpi.main.MpVariable;
import net.sf.jmpi.main.expression.MpExpr;
import net.sf.jmpi.main.expression.MpExprTerm;
import net.sf.jmpi.main.expression.MpExprType;
import net.sf.jmpi.solver.AbstractMpSolver;

/**
 * The {@code SolverGurobi} is the {@code Solver} Gurobi.
 * 
 * @author fabiogenoese, lukasiewycz
 * 
 */
public class SolverGurobi extends AbstractMpSolver<GRBVar, Object> implements MpSolver {

	static GRBEnv env = null;
	final GRBModel model;

	public SolverGurobi() {
		super();
		try {
			if (env == null) {
				env = new GRBEnv();
			}
			model = new GRBModel(env);
			setVerbose(0);
		} catch (GRBException e) {
			throw new RuntimeException(e);
		}
	}
	
	public GRBEnv getGRBEnv(){
		return env;
	}
	
	public GRBModel getGRBModel(){
		return model;
	}

	@Override
	public MpResult solve() {
		try {
			//model.write("output.mps");
			model.optimize();

			int optimstatus = model.get(GRB.IntAttr.Status);
			//System.out.println("status "+optimstatus+" "+GRB.Status.INF_OR_UNBD+" "+GRB.INFEASIBLE);

			if (optimstatus == GRB.Status.INF_OR_UNBD || optimstatus == GRB.Status.INFEASIBLE) {
				return null;
			}

			double objValue = 0;
			if (objectiveFunction.size() > 0) {
				objValue = model.get(GRB.DoubleAttr.ObjVal);
			}

			MpResult result = new MpResultImpl(objValue);

			for (Entry<Object, GRBVar> entry : objectToVar.entrySet()) {
				Object variable = entry.getKey();
				GRBVar var = entry.getValue();

				double primalValue = var.get(GRB.DoubleAttr.X);

				result.put(variable, primalValue);
			}

			return result;

		} catch (GRBException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void setTimeout(int value) {
		try {
			model.getEnv().set(GRB.DoubleParam.TimeLimit, (double)value);
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setVerbose(int value) {
		try {
			model.getEnv().set(GRB.IntParam.OutputFlag, value);
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setObjective(MpExpr objective, MpDirection optType2) {
		if (addedVar) {
			addedVar = false;
			try {
				model.update();
			} catch (GRBException e) {
				e.printStackTrace();
			}
		}

		int dir = optType == MpDirection.MAX ? GRB.MAXIMIZE : GRB.MINIMIZE;

		GRBExpr expr = toGRBExpr(objectiveFunction);
		try {
			model.setObjective(expr, dir);
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Object addConstraint(MpConstraint c) {
		if (addedVar) {
			addedVar = false;
			try {
				model.update();
			} catch (GRBException e) {
				e.printStackTrace();
			}
		}

		GRBExpr lhs = toGRBExpr(c.getLhs());
		GRBExpr rhs = toGRBExpr(c.getRhs());

		MpOperator op = c.getOperator();

		final char grbop;
		switch (op) {
		case GE:
			grbop = GRB.GREATER_EQUAL;
			break;
		case LE:
			grbop = GRB.LESS_EQUAL;
			break;
		case EQ:
			grbop = GRB.EQUAL;
			break;
		default:
			throw new RuntimeException("Invalid operator " + op);
		}

		try {
			Object constraint = addConstraint(model, lhs, grbop, rhs, "c" + (i++));
			return constraint;
		} catch (GRBException e) {
			throw new RuntimeException(e);
		}
	}

	protected Object addConstraint(GRBModel model, GRBExpr lhs, char comp, GRBExpr rhs,
			String id) throws GRBException {
		if (lhs instanceof GRBQuadExpr && rhs instanceof GRBQuadExpr) {
			return model.addQConstr((GRBQuadExpr) lhs, comp, (GRBQuadExpr) rhs, id);
		} else if (lhs instanceof GRBLinExpr && rhs instanceof GRBQuadExpr) {
			return model.addQConstr((GRBLinExpr) lhs, comp, (GRBQuadExpr) rhs, id);
		} else if (lhs instanceof GRBQuadExpr && rhs instanceof GRBLinExpr) {
			return model.addQConstr((GRBQuadExpr) lhs, comp, (GRBLinExpr) rhs, id);
		} else {
			return model.addConstr((GRBLinExpr) lhs, comp, (GRBLinExpr) rhs, id);
		}
	}

	protected int i = 0;

	@Override
	protected GRBVar addVariable(MpVariable variable) {
		char type;
		switch (variable.getType()) {
		case BOOL:
			type = GRB.BINARY;
			break;
		case INT:
			type = GRB.INTEGER;
			break;
		default: // REAL
			type = GRB.CONTINUOUS;
			break;
		}

		try {
			double lower = variable.getLower().doubleValue();
			double upper = variable.getUpper().doubleValue();
			GRBVar var = model.addVar(lower, upper, 0, type, variable.getVar().toString());
			addedVar = true;
			return var;
		} catch (GRBException e) {
			throw new RuntimeException(e);
		}
	}

	protected boolean addedVar = false;

	@SuppressWarnings("unchecked")
	protected GRBExpr toGRBExpr(MpExpr expr) {
		MpExprType type = expr.type();
		if (type == MpExprType.MORE) {
			throw new IllegalArgumentException("more than quadratic: " + expr);
		}

		boolean isQuadratic = type == MpExprType.QUADRATIC;
		GRBExpr e = isQuadratic ? new GRBQuadExpr() : new GRBLinExpr();

		for (MpExprTerm term : expr) {
			double coeff = term.getCoeff().doubleValue();
			switch (term.size()) {
			case 0:
				if (isQuadratic) {
					((GRBQuadExpr) e).addConstant(coeff);
				} else {
					((GRBLinExpr) e).addConstant(coeff);
				}
				break;
			case 1:
				if (isQuadratic) {
					((GRBQuadExpr) e).addTerm(coeff, getVar(term.var(0)));
				} else {
					((GRBLinExpr) e).addTerm(coeff, getVar(term.var(0)));
				}
				break;
			default: // QUADRATIC
				((GRBQuadExpr) e).addTerm(coeff, getVar(term.var(0)), getVar(term.var(1)));
				break;
			}

		}
		return e;
	}
}