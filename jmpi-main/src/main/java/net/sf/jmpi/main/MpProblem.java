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
package net.sf.jmpi.main;

import static net.sf.jmpi.main.expression.MpExpr.sum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import net.sf.jmpi.main.MpVariable.Type;
import net.sf.jmpi.main.expression.MpExpr;

public class MpProblem implements MpVariables {

	protected final List<MpConstraint> constraints = new ArrayList<MpConstraint>();
	protected final HashMap<Object, MpVariable> variables = new LinkedHashMap<Object, MpVariable>();

	protected MpExpr objective = null;
	protected MpDirection optType = MpDirection.UNDEFINED;

	public MpConstraint add(MpExpr lhs, MpOperator operator, Number rhs) {
		return add(lhs, operator, sum(rhs));
	}

	public MpConstraint add(Number lhs, MpOperator operator, MpExpr rhs) {
		return add(sum(rhs), operator, rhs);
	}

	public MpConstraint add(MpExpr lhs, String operator, MpExpr rhs) {
		return add(lhs, MpOperator.get(operator), rhs);
	}

	public MpConstraint add(MpExpr lhs, String operator, Number rhs) {
		return add(lhs, MpOperator.get(operator), rhs);
	}

	public MpConstraint add(Number lhs, String operator, MpExpr rhs) {
		return add(lhs, MpOperator.get(operator), rhs);
	}

	public MpVariable addVar(Object variable, Class<?> type) {
		return addVar(-Double.MAX_VALUE, variable, Double.MAX_VALUE, type);
	}

	public MpVariable addVar(Number lower, Object variable, Class<?> type) {
		return addVar(lower, variable, Double.MAX_VALUE, type);
	}

	public MpVariable addVar(Object variable, Number upper, Class<?> type) {
		return addVar(-Double.MAX_VALUE, variable, upper, type);
	}

	public MpConstraint add(MpExpr lhs, MpOperator operator, MpExpr rhs) {
		MpConstraint constraint = new MpConstraint(lhs, operator, rhs);
		addConstraint(constraint);
		return constraint;
	}

	public MpVariable addVar(Number lower, Object var, Number upper, Class<?> type) {
		MpVariable variable = new MpVariable(var, lower, upper, MpVariable.Type.get(type));
		addVariable(variable);
		return variable;
	}

	public MpConstraint add(MpConstraint constraint) {
		addConstraint(constraint);
		return constraint;
	}

	public MpVariable add(MpVariable variable) {
		addVariable(variable);
		return variable;
	}

	public void add(MpProblem problem) {
		for (MpVariable variable : problem.getVariables()) {
			addVariable(variable);
		}
		for (MpConstraint constraint : problem.getConstraints()) {
			addConstraint(constraint);
		}
		if (problem.getObjective() != null) {
			setObjective(problem.getObjective(), problem.getOptType());
		}
	}

	public void setObjective(MpExpr expression, MpDirection type) {
		this.objective = expression;
		this.optType = type;
	}

	public void addConstraint(MpConstraint constraint) {
		this.constraints.add(constraint);
	}

	public void addVariable(MpVariable variable) {
		this.variables.put(variable.getVar(), variable);
	}

	public Collection<MpConstraint> getConstraints() {
		return constraints;
	}

	public int getConstraintsCount() {
		return constraints.size();
	}

	public Collection<MpVariable> getVariables() {
		return variables.values();
	}

	public int getVariablesCount() {
		return variables.size();
	}

	public MpExpr getObjective() {
		return objective;
	}

	public MpDirection getOptType() {
		return optType;
	}

	@Override
	public String toString() {
		String s = "";
		if (objective != null) {
			s += optType + ":" + objective + "\n";
		}
		for (MpConstraint constraint : constraints) {
			s += "constraint: " + constraint + "\n";
		}
		for (MpVariable variable : variables.values()) {
			s += "variable: " + variable + "\n";
		}
		return s;
	}

	public MpVariable getVariable(Object variable) {
		return variables.get(variable);
	}

	public Type getVariableType(Object variable) {
		return getVariable(variable).getType();
	}

	/*public Map<Object, MpVariable> getVariablesMp() {
		return variables;
	}*/

}
