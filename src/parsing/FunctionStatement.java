package parsing;

import java.util.ArrayList;
import java.util.Arrays;

import objects.reFunction;
import objects.reObject;

public class FunctionStatement implements Statement{
	public int start, end;
	public String var;
	public ArrayList<String> params;
	public ArrayList<Statement> lines;
	
	public FunctionStatement(String var, String params, int start){
		this.var = var;
		if(params.isEmpty())
			this.params = new ArrayList<String>();
		else
			this.params = new ArrayList<String>(Arrays.asList(params.split(",")));
		this.start = start;
	}
	
	@Override
	public reObject calc(int start, int end){
		int count = 0;
		boolean isString = false;
		int idxDot = -1;
		for(int i = var.length() - 1; i >= 0; i--){
			if(!isString && (var.charAt(i) == ')' || var.charAt(i) == ']' || var.charAt(i) == '}')) count++;
			else if(!isString && (var.charAt(i) == '(' || var.charAt(i) == '[' || var.charAt(i) == '{')) count--;
			else if(var.charAt(i) == '"') isString = !isString;
			else if(!isString && count == 0 && var.charAt(i) == '.'){
				idxDot = i;
				break;
			}
		}
		String funcName = idxDot == -1 ? var : var.substring(idxDot + 1);
		SetStatement.set(var, new reFunction(funcName, lines, params, this.start, this.end), start, end, start);
		return null;
	}
	
	@Override
	public int getLineNum(){
		return start;
	}
}
