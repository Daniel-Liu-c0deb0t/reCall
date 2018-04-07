package parsing;

import java.math.BigDecimal;

import objects.reNumber;
import objects.reObject;

public class ElseStatement implements Statement{ //elif or else
	public String exp;
	public int start, end, indent;
	
	public ElseStatement(String exp, int start, int indent){
		this.exp = exp;
		this.start = start;
		this.indent = indent;
	}
	
	public ElseStatement(int start, int indent){
		this.exp = null;
		this.start = start;
		this.indent = indent;
	}
	
	@Override
	public reObject calc(int start, int end){
		if(exp == null)
			return new reNumber(BigDecimal.ONE);
		return new reNumber(new BigDecimal(Expression.recursiveCalc(exp, null, start, end, start).toBool()));
	}

	@Override
	public int getLineNum(){
		return start;
	}
}
