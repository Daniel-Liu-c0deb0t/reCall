package parsing;

import java.math.BigDecimal;

import objects.reNumber;
import objects.reObject;

public class IfStatement implements Statement{
	public String exp;
	public int indent, start, end;
	
	public IfStatement(String exp, int start, int indent){
		this.exp = exp;
		this.start = start;
		this.indent = indent;
	}
	
	@Override
	public reObject calc(int start, int end){
		return new reNumber(new BigDecimal(Expression.recursiveCalc(exp, null, start, end, start).toBool()));
	}

	@Override
	public int getLineNum(){
		return start;
	}
}
