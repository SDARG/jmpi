package net.sf.jmpi.main.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MpGeneralSum implements MpSum<MpExpression>  {

	protected List<MpExpression> elements = new ArrayList<MpExpression>(1);
	
	public MpGeneralSum(){
		
	}
	
	public MpGeneralSum(MpExpression... elements){
		this.elements.addAll(Arrays.asList(elements));
	}

	@Override
	public Iterator<MpExpression> iterator() {
		return elements.iterator();
	}
	
	public MpGeneralSum add(MpExpression expression){
		if (expression instanceof MpGeneralSum) {
			for (MpExpression e : (MpGeneralSum) expression) {
				add(e);
			}
		} else {
			elements.add(expression);
		}
		return this;
	}
	
	public List<MpExpression> getAll(){
		return elements;
	}
	
	public int size(){
		return elements.size();
	}
	
	@Override
	public String toString() {
		String s = "( ";
		for (int i = 0; i < elements.size(); i++) {
			s += elements.get(i);
			if (i < elements.size() - 1) {
				s += " + ";
			}
		}
		s += " )";
		return s;
	}

}
