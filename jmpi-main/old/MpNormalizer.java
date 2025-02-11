package net.sf.jmpi.main.expression;

import static net.sf.jmpi.main.expression.MpExprBuilder.prod;
import static net.sf.jmpi.main.expression.MpExprBuilder.sum;
import static net.sf.jmpi.main.expression.MpExprBuilder.val;
import static net.sf.jmpi.main.expression.MpExprBuilder.var;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jmpi.main.MpConstraint;
import net.sf.jmpi.main.MpDirection;
import net.sf.jmpi.main.MpOperator;
import net.sf.jmpi.main.MpProblem;
import net.sf.jmpi.main.MpVariable.Type;
import net.sf.jmpi.main.MpVariables;

public class MpNormalizer {

	protected Map<Set<Object>, Object> booleanProdMap = new HashMap<Set<Object>, Object>();

	public MpProblem normalizeConstraint(MpConstraint constraint, MpVariables problem) {
		constraint = normalize(constraint, problem);

		MpProblem result = new MpProblem();
		result.add(constraint);

		resolveBooleanProducts(constraint.getLhs(), result, problem);

		return result;
	}

	public MpProblem normalizeObjective(MpExpression objective, MpDirection direction,
			MpVariables problem) {
		objective = normalize(objective, problem);

		MpProblem result = new MpProblem();
		result.setObjective(objective, direction);

		resolveBooleanProducts(objective, result, problem);

		return result;
	}

	protected void resolveBooleanProducts(MpExpression expr, MpProblem result, MpVariables problem) {
		for (MpExpression e0 : (MpGeneralSum) expr) {
			MpGeneralProd prod = (MpGeneralProd) e0;

			Set<MpVar> booleanVars = new HashSet<MpVar>();
			for (Iterator<MpExpression> it = prod.iterator(); it.hasNext();) {
				MpExpression e1 = it.next();
				if (e1 instanceof MpVar && isBoolean((MpVar) e1, problem)) {
					booleanVars.add((MpVar) e1);
					it.remove();
				}
			}

			if (booleanVars.size() == 1) {
				prod.mult(booleanVars.iterator().next());
			} else if (booleanVars.size() > 1) {
				Set<Object> set = new HashSet<Object>();
				for (MpVar var : booleanVars) {
					set.add(var.getVariable());
				}
				if (!booleanProdMap.containsKey(set)) {
					result.addVar(set, Boolean.class);
					booleanProdMap.put(set, set);

					// set -> x1 x2 x3
					for (MpVar var : booleanVars) {
						MpGeneralSum lhs = sum(prod(-1, set), prod(1, var));
						MpConstraint c = new MpConstraint(lhs, MpOperator.GE, val(0));
						result.add(c);
					}

					MpGeneralSum lhs = sum();
					for (MpVar var : booleanVars) {
						lhs.add(prod(-1, var));
					}
					lhs.add(prod(1, set));
					lhs.add(prod(booleanVars.size() - 1));

					MpConstraint c = new MpConstraint(lhs, MpOperator.GE, val(0));
					result.add(c);

				}
				prod.mult(var(booleanProdMap.get(set)));
			}
		}
	}

	public static MpConstraint normalize(MpConstraint constraint, MpVariables problem) {
		MpExpression lhs = constraint.getLhs();
		MpExpression rhs = constraint.getRhs();
		MpOperator operator = constraint.getOperator();

		lhs = normalize(lhs, rhs, problem);
		rhs = val(0);

		return new MpConstraint(lhs, operator, rhs);
	}

	public static MpExpression normalize(MpExpression expr, MpVariables problem) {
		MpExpression e = toSumOfProducts(expr);
		return summarize(e, problem);
	}

	public static MpExpression normalize(MpExpression lhs, MpExpression rhs, MpVariables problem) {
		MpGeneralSum e0 = toSumOfProducts(lhs);
		MpGeneralSum e1 = toSumOfProducts(rhs);

		for (MpExpression e : e1) {
			MpGeneralProd prod = ((MpGeneralProd) e).mult(val(-1));
			e0.add(prod);
		}

		return summarize(e0, problem);
	}

	protected static boolean isBoolean(MpVar var, MpVariables problem) {
		/*System.out.println(var.getVariable()+" "+problem.getVariable(var.getVariable()));
		
		for(Object object: ((MpProblem)problem).getVariablesMp().keySet()){
			System.out.println(var.getVariable()+" "+object+" "+var.getVariable().getClass()+" "+object.getClass());
			if(var.getVariable().equals(object)){
				System.out.println("found");
			}
		}*/
		
		
		return problem.getVariable(var.getVariable()).getType().equals(Type.BOOL);
	}

	protected static MpExpression summarize(MpExpression e, MpVariables problem) {
		Map<Bag, List<Number>> map = new HashMap<Bag, List<Number>>();

		for (MpExpression sub : (MpGeneralSum) e) {
			MpGeneralProd prod = (MpGeneralProd) sub;
			List<Number> coeffs = new ArrayList<Number>();
			Bag bag = new Bag();
			for (MpExpression element : prod) {
				if (element instanceof MpVar) {
					if (isBoolean((MpVar) element, problem) && bag.containsKey(element)) {
						// do not add
					} else {
						bag.add(element);
					}
				} else if (element instanceof MpVal) {
					coeffs.add(((MpVal) element).getValue());
				}
			}
			double value = 1;
			for (Number n : coeffs) {
				value *= n.doubleValue();
			}
			if (map.containsKey(bag)) {
				map.get(bag).add(value);
			} else {
				map.put(bag, new ArrayList<Number>(Arrays.asList(value)));
			}
		}

		MpGeneralSum sum = sum();
		for (Bag bag : map.keySet()) {
			MpGeneralProd prod = new MpGeneralProd();
			double value = 0;
			for (Number number : map.get(bag)) {
				value += number.doubleValue();
			}
			if (value != 0) {
				prod.mult(val(value));
				for (Object obj : bag.keySet()) {
					int n = bag.get(obj);
					for (int i = 0; i < n; i++) {
						prod.mult((MpVar) obj);
					}
				}
				sum.add(prod);
			}
		}

		return sum;
	}

	public static MpGeneralSum toSumOfProducts(MpExpression expr) {
		if (expr instanceof MpGeneralProd) {
			MpGeneralProd prod = (MpGeneralProd) expr;

			List<MpExpression> factors = new ArrayList<MpExpression>();
			for (MpExpression e : prod) {
				factors.add(toSumOfProducts(e));
			}

			int[] size = new int[factors.size()];
			int[] values = new int[factors.size()];

			for (int i = 0; i < factors.size(); i++) {
				MpExpression factor = factors.get(i);
				if (factor instanceof MpGeneralSum) {
					size[i] = factor.size();
				} else {
					size[i] = 1;
				}
				values[i] = 0;
			}

			MpGeneralSum sum = sum();
			do {
				MpGeneralProd p = prod();
				for (int i = 0; i < factors.size(); i++) {
					MpExpression factor = factors.get(i);
					if (factor instanceof MpGeneralSum) {
						p.mult(((MpGeneralSum) factor).getAll().get(values[i]));
					} else {
						p.mult(factor);
					}
				}
				sum.add(p);
			} while (next(values, size));
			return sum;

		} else if (expr instanceof MpGeneralSum) {
			MpGeneralSum sum = sum();
			for (MpExpression e : (MpGeneralSum) expr) {
				sum.add(toSumOfProducts(e));
			}
			return sum;
		} else {
			return sum(prod(expr));
		}
	}

	private static boolean next(int[] values, int[] size) {
		boolean carry = true;
		for (int i = 0; i < values.length && carry; i++) {
			values[i]++;
			if (values[i] >= size[i]) {
				values[i] = 0;
				carry = true;
			} else {
				carry = false;
			}
		}
		return !carry;
	}

}
