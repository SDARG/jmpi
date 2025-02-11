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
package net.sf.jmpi.solver;

import static net.sf.jmpi.main.expression.MpExpr.sum;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.jmpi.main.MpConstraint;
import net.sf.jmpi.main.MpDirection;
import net.sf.jmpi.main.MpProblem;
import net.sf.jmpi.main.MpSolver;
import net.sf.jmpi.main.MpVariable;
import net.sf.jmpi.main.expression.MpExpr;
import net.sf.jmpi.main.expression.MpExprTerm;
import net.sf.jmpi.main.expression.MpVars;

/**
 * 
 * @author lukasiewycz
 * 
 */
public abstract class AbstractMpSolver<V, C> implements MpSolver {

	final protected Map<Object, V> objectToVar = new HashMap<Object, V>();
	final protected Map<V, MpVariable> variables = new HashMap<V, MpVariable>();
	final protected Map<C, MpConstraint> constraints = new HashMap<C, MpConstraint>();

	protected MpExpr objectiveFunction = sum();
	protected MpDirection optType = MpDirection.MIN;

	protected V getVar(Object object) {
		if (object instanceof MpVariable) {
			throw new IllegalArgumentException("" + object + " is a Variable class");
		}
		V var = objectToVar.get(object);
		if (var == null) {
			System.out.println(objectToVar.get("x0")+" "+object.getClass());
			throw new IllegalArgumentException("Variable " + object + " is undefined.");
		}
		return var;
	}

	@Override
	public void add(MpProblem problem) {
		for (MpVariable variable : problem.getVariables()) {
			V nativeVariable = addVariable(variable);
			registerVariable(variable, nativeVariable);
		}

		for (MpConstraint constraint : problem.getConstraints()) {
			C nativeConstraint = addConstraint(constraint);
			registerConstraint(constraint, nativeConstraint);
			/*
			 * MpProblem p = normalizer.normalizeConstraint(constraint,
			 * problem); for (MpVariable variable : p.getVariables()) { V
			 * nativeVariable = addVariable(variable);
			 * registerVariable(variable, nativeVariable); } for (MpConstraint c
			 * : p.getConstraints()) { C nativeConstraint = addConstraint(c);
			 * registerConstraint(constraint, nativeConstraint); }
			 */

		}
		if (problem.getObjective() != null) {
			/*
			 * MpProblem p =
			 * normalizer.normalizeObjective(problem.getObjective(),
			 * problem.getOptType(), problem); for (MpVariable variable :
			 * p.getVariables()) { V nativeVariable = addVariable(variable);
			 * registerVariable(variable, nativeVariable); } for (MpConstraint c
			 * : p.getConstraints()) { C nativeConstraint = addConstraint(c);
			 * registerConstraint(c, nativeConstraint); } if(p.getOptType() !=
			 * MpDirection.UNDEFINED){ MpExpr objective = p.getObjective();
			 * if(objective == null){ objective = MpExprBuilder.sum(); }
			 * this.objectiveFunction = objective; this.optType =
			 * p.getOptType(); setObjective(objective, p.getOptType()); }
			 */

			if (problem.getOptType() != MpDirection.UNDEFINED) {
				this.objectiveFunction = problem.getObjective();
				this.optType = problem.getOptType();
				setObjective(objectiveFunction, optType);
			}
		}
	}

	protected void registerVariable(MpVariable variable, V nativeVar) {
		objectToVar.put(variable.getVar(), nativeVar);
		variables.put(nativeVar, variable);
	}

	protected void registerConstraint(MpConstraint constraint, C nativeConstraint) {
		constraints.put(nativeConstraint, constraint);
	}

	/*
	 * protected double identifyTerm(MpGeneralProd prod, List<Object> variables)
	 * { double coeff = 1d; for (MpExpression element : prod) { if (element
	 * instanceof MpVar) { variables.add(((MpVar) element).getVariable()); }
	 * else if (element instanceof MpVal) { coeff *= ((MpVal)
	 * element).getValue().doubleValue(); } else { throw new
	 * RuntimeException("not normalized"); } } return coeff; }
	 */

	protected abstract void setObjective(MpExpr objective, MpDirection optType2);

	protected abstract C addConstraint(MpConstraint c);

	protected abstract V addVariable(MpVariable variable);
	
	protected MpConstraint normalize(MpConstraint c) {
		MpExpr lhs = c.getLhs();
		MpExpr rhs = c.getRhs();

		double rhsValue = 0.0;

		Map<MpVars, Double> terms = new HashMap<MpVars, Double>();

		for (MpExprTerm term : lhs) {
			rhsValue += add(term.getVars(), term.getCoeff().doubleValue(), terms);
		}
		for (MpExprTerm term : rhs) {
			rhsValue += add(term.getVars(), -term.getCoeff().doubleValue(), terms);
		}

		lhs = MpExpr.sum();
		for (Entry<MpVars, Double> entry : terms.entrySet()) {
			MpExprTerm term = new MpExprTerm(entry.getValue(), entry.getKey().getVars());
			lhs.addTerm(term);
		}

		rhs = MpExpr.sum(rhsValue);

		return new MpConstraint(lhs, c.getOperator(), rhs);
	}
	
	protected double add(Object[] vars, double coeff, Map<MpVars, Double> terms) {
		if (vars.length == 0) {
			return -coeff;
		}
		MpVars key = new MpVars(vars);
		Double value = terms.get(key);
		if (value == null) {
			value = 0.0;
		}
		terms.put(key, value + coeff);
		return 0.0;
	}
	
	protected double normalizedRhsValue(MpConstraint c) {
		return c.getRhs().iterator().next().getCoeff().doubleValue();
	}


}