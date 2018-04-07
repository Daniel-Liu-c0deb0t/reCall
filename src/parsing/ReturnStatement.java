package parsing;

import objects.reObject;

public class ReturnStatement implements Statement{
	public int lineNum;
	public String exp;
	
	public ReturnStatement(String exp, int lineNum){
		this.exp = exp;
		this.lineNum = lineNum;
	}
	
	@Override
	public reObject calc(int start, int end){
		return Expression.recursiveCalc(exp, null, start, end, lineNum);
	}
	
	@Override
	public int getLineNum(){
		return lineNum;
	}
}
