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

import static java.lang.Double.MAX_VALUE;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import mosek.ArrayLengthException;
import mosek.Env;
import mosek.Env.boundkey;
import mosek.Env.prosta;
import mosek.Env.rescode;
import mosek.Env.solsta;
import mosek.Env.soltype;
import mosek.Error;
import mosek.Task;
import mosek.Warning;
import net.sf.jmpi.main.MpConstraint;
import net.sf.jmpi.main.MpDirection;
import net.sf.jmpi.main.MpOperator;
import net.sf.jmpi.main.MpResult;
import net.sf.jmpi.main.MpResultImpl;
import net.sf.jmpi.main.MpSolver;
import net.sf.jmpi.main.MpVariable;
import net.sf.jmpi.main.MpVariable.Type;
import net.sf.jmpi.main.expression.MpExpr;
import net.sf.jmpi.main.expression.MpExprTerm;
import net.sf.jmpi.main.expression.MpVars;
import net.sf.jmpi.solver.AbstractMpSolver;

public class SolverMosek extends AbstractMpSolver<Integer, Integer> implements MpSolver {

	class msgclass extends mosek.Stream {
		public msgclass() {
			super();
		}

		public void stream(String msg) {
			System.out.print(msg);
		}
	}

	int constraintId = 0;
	protected Env env;
	protected Task task;
	protected soltype soltype = Env.soltype.bas;
	msgclass msg_obj = new msgclass();

	public SolverMosek() {
		super();

		/*
		 * try { System.setProperty("java.library.path",
		 * "C:/Mosek/6/tools/platform/win64x86/bin");
		 * 
		 * Field fieldSysPath; fieldSysPath =
		 * ClassLoader.class.getDeclaredField("sys_paths");
		 * fieldSysPath.setAccessible(true); fieldSysPath.set(null, null); }
		 * catch (SecurityException e) { e.printStackTrace(); } catch
		 * (NoSuchFieldException e) { e.printStackTrace(); } catch
		 * (IllegalArgumentException e) { e.printStackTrace(); } catch
		 * (IllegalAccessException e) { e.printStackTrace(); }
		 */

		env = new Env();
		env.set_Stream(mosek.Env.streamtype.log, msg_obj);
		env.init();
		task = new Task(env, 0, 0);
	}

	@Override
	public MpResult solve() {
		try {
			task.set_Stream(mosek.Env.streamtype.log, msg_obj);
			rescode rescode = task.optimize();

			prosta[] prosta = new prosta[1];
			solsta[] solsta = new solsta[1];

			// mosek.Env.soltype.bas

			task.getsolutionstatus(soltype, prosta, solsta);

			prosta p = prosta[0];

			System.out.println(objectiveFunction);
			System.out.println("vars " + varIndex + " " + p);

			if (p == mosek.Env.prosta.prim_infeas || p == mosek.Env.prosta.prim_and_dual_infeas
					|| p == mosek.Env.prosta.dual_infeas
					|| p == mosek.Env.prosta.prim_infeas_or_unbounded) {
				task.dispose();
				return null;
			}

			double[] x = new double[varIndex];
			task.getsolutionslice(soltype, mosek.Env.solitem.xx, 0, varIndex, x);

			Map<Object, Double> map = new HashMap<Object, Double>();

			for (int j = 0; j < x.length; ++j) {
				Object variable = variables.get(j).getVar();

				double value = x[j];
				// System.out.println(variable + " " + value);
				map.put(variable, value);
			}

			double objectiveValue = evaluate(objectiveFunction, map);

			MpResult result = new MpResultImpl(objectiveValue);

			for (Entry<Object, Double> entry : map.entrySet()) {
				result.put(entry.getKey(), entry.getValue());
			}

			task.dispose();

			return result;

		} catch (Warning e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		} catch (ArrayLengthException e) {
			e.printStackTrace();
		}

		return null;
	}

	protected double evaluate(MpExpr expr, Map<Object, Double> values) {
		double value = 0.0;
		for (MpExprTerm term : expr) {
			double v = term.getCoeff().doubleValue();
			for (Object var : term.getVars()) {
				v *= values.get(var);
			}
			value += v;
		}
		return value;
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
	protected void setObjective(MpExpr objective, MpDirection direction) {
		IntegerRepresentation res = convert(new MpConstraint(objective, MpOperator.EQ,
				MpExpr.sum(0.0)));

		try {
			task.putcfix(-res.rhs);

			if (res.aval.length > 0) {
				task.putclist(res.asub, res.aval);
			}
			if (res.qval.length > 0) {
				soltype = Env.soltype.itr;
				task.putqobj(res.qsubi, res.qsubj, res.qval);
			}

			if (direction == MpDirection.MAX) {
				task.putobjsense(mosek.Env.objsense.maximize);
			} else {
				task.putobjsense(mosek.Env.objsense.minimize);
			}

		} catch (Warning e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		} catch (ArrayLengthException e) {
			e.printStackTrace();
		}

	}

	protected static class IntegerRepresentation {
		public int asub[];
		public double aval[];
		public int qsubi[];
		public int qsubj[];
		public double qval[];
		public double rhs;
	}

	protected IntegerRepresentation convert(MpConstraint c) {
		IntegerRepresentation res = new IntegerRepresentation();

		MpConstraint norm = normalize(c);
		res.rhs = rhsValue(norm);

		MpExpr lhs = norm.getLhs();

		int a = 0;
		int q = 0;

		for (MpExprTerm term : lhs) {
			switch (term.getVars().length) {
			case 1:
				a++;
				break;
			case 2:
				q++;
				break;
			default:
				throw new IllegalArgumentException("not linear " + c);
			}
		}

		res.asub = new int[a];
		res.aval = new double[a];
		res.qsubi = new int[q];
		res.qsubj = new int[q];
		res.qval = new double[q];

		a = 0;
		q = 0;

		for (MpExprTerm term : lhs) {
			final int i = objectToVar.get(term.getVars()[0]);
			final double coeff = term.getCoeff().doubleValue();
			if (term.getVars().length == 1) {
				res.asub[a] = i;
				res.aval[a] = coeff;
				a++;
			} else if (term.getVars().length == 2) {
				final int j = objectToVar.get(term.getVars()[1]);
				res.qsubi[q] = Math.max(i, j);
				res.qsubj[q] = Math.min(i, j);
				res.qval[q] = coeff * (i == j ? 2 : 1);
				q++;
			}
		}

		return res;
	}

	@Override
	protected Integer addConstraint(MpConstraint c) {

		IntegerRepresentation res = convert(c);

		try {
			task.append(mosek.Env.accmode.con, 1);

			if (res.aval.length > 0) {
				task.putavec(mosek.Env.accmode.con, conIndex, res.asub, res.aval);
			}
			if (res.qval.length > 0) {
				soltype = Env.soltype.itr;
				task.putqconk(conIndex, res.qsubi, res.qsubj, res.qval);
			}

			boundkey comp;
			switch (c.getOperator()) {
			case LE:
				comp = Env.boundkey.up;
				break;
			case GE:
				comp = Env.boundkey.lo;
				break;
			default: // EQ
				comp = Env.boundkey.fx;
			}
			task.putbound(Env.accmode.con, conIndex, comp, res.rhs, res.rhs);

			conIndex++;
			return conIndex - 1;

		} catch (Warning e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		} catch (ArrayLengthException e) {
			e.printStackTrace();
		}

		return null;
	}

	protected double rhsValue(MpConstraint c) {
		return c.getRhs().iterator().next().getCoeff().doubleValue();
	}

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

	int conIndex = 0;
	int varIndex = 0;

	@Override
	protected Integer addVariable(MpVariable variable) {
		try {
			task.append(mosek.Env.accmode.var, 1);

			Type type = variable.getType();

			Double lb = variable.getLower().doubleValue();
			Double ub = variable.getUpper().doubleValue();

			if (type == Type.BOOL || type == Type.INT) {
				task.putvartype(varIndex, Env.variabletype.type_int);
				if (soltype == Env.soltype.bas) {
					soltype = Env.soltype.itg;
				}
			} else {
				task.putvartype(varIndex, Env.variabletype.type_cont);
			}

			if (type == Type.BOOL) {
				lb = max(min(lb, 1.0), 0.0);
				ub = min(max(ub, 0.0), 1.0);
			}

			boundkey bounds = Env.boundkey.fr;
			if (lb != -MAX_VALUE && ub != MAX_VALUE) {
				bounds = Env.boundkey.ra;
			} else if (lb != -MAX_VALUE) {
				bounds = Env.boundkey.lo;
			} else if (ub != MAX_VALUE) {
				bounds = Env.boundkey.up;
			}

			task.putbound(mosek.Env.accmode.var, varIndex, bounds, lb, ub);

			varIndex++;
			return varIndex - 1;

		} catch (Warning e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		return null;
	}
}
