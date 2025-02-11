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
package net.sf.jmpi.main.expression;

import static net.sf.jmpi.main.expression.MpExpr.prod;
import static net.sf.jmpi.main.expression.MpExpr.sum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.sf.jmpi.main.MpConstraint;
import net.sf.jmpi.main.MpOperator;
import net.sf.jmpi.main.MpProblem;
import net.sf.jmpi.main.MpVariable;
import net.sf.jmpi.main.MpVariable.Type;

public class MpNormalizer {

	protected Map<BooleanProduct, Object> booleanProdMap = new HashMap<BooleanProduct, Object>();

	public static class BooleanProduct extends HashSet<Object> {

		private static final long serialVersionUID = 1L;

		@Override
		public String toString() {
			String s = super.toString();
			return "<" + s.substring(1, s.length() - 1) + ">";
		}

	}

	public MpProblem resolveBooleanProducts(MpProblem org) {
		MpProblem result = new MpProblem();
		for (MpVariable var : org.getVariables()) {
			result.addVariable(var);
		}

		for (MpConstraint constraint : org.getConstraints()) {
			MpConstraint c = resolveBooleanProducts(constraint, result);
			result.add(c);
		}

		if (org.getObjective() != null) {
			MpExpr objective = resolveBooleanProducts(org.getObjective(), result);
			result.setObjective(objective, org.getOptType());
		}

		return result;
	}

	public MpConstraint resolveBooleanProducts(MpConstraint constraint, MpProblem problem) {
		MpExpr lhs = resolveBooleanProducts(constraint.getLhs(), problem);
		MpExpr rhs = resolveBooleanProducts(constraint.getRhs(), problem);
		return new MpConstraint(lhs, constraint.getOperator(), rhs);
	}

	public MpExpr resolveBooleanProducts(MpExpr expr, MpProblem problem) {
		boolean e = false;
		for (MpExprTerm term : expr) {
			int i = 0;
			for (Object var : term.getVars()) {
				if (problem.getVariableType(var) == Type.BOOL) {
					i++;
				}
			}
			if (i > 1) {
				e = true;
				break;
			}
		}
		//System.out.println(e);
		if (!e) {
			return expr;
		}

		MpExpr result = MpExpr.sum();

		for (MpExprTerm term : expr) {
			MpExpr newTerm = MpExpr.prod(term.getCoeff());

			BooleanProduct booleanVars = new BooleanProduct();
			for (Object var : term.getVars()) {
				if (problem.getVariableType(var) == Type.BOOL) {
					booleanVars.add(var);
				} else {
					newTerm.mul(var);
				}
			}
			if (booleanVars.size() < 2) {
				for (Object obj : booleanVars) {
					newTerm.mul(obj);
				}
			} else {
				BooleanProduct set = new BooleanProduct();
				for (Object var : booleanVars) {
					set.add(var);
				}
				newTerm.mul(set);

				if (!booleanProdMap.containsKey(set)) {
					problem.addVar(set, Boolean.class);
					booleanProdMap.put(set, set);

					// set -> x1 x2 x3
					for (Object var : booleanVars) {
						MpExpr lhs = sum(prod(-1, set), prod(1, var));
						problem.add(lhs, ">=", 0);
					}

					MpExpr lhs = sum();
					for (Object var : booleanVars) {
						lhs.add(prod(-1, var));
					}
					lhs.add(prod(1, set));
					lhs.add(prod(booleanVars.size() - 1));

					problem.add(lhs, ">=", 0);

				}
			}
			result.add(newTerm);

		}
		//System.out.println("from " + expr);
		//System.out.println("to " + result);
		return result;
	}

}
