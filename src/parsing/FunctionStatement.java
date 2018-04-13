package parsing;

import java.util.ArrayList;

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
		else{
			int count = 0;
			boolean isString = false;
			int prev = 0;
			ArrayList<String> res = new ArrayList<>();
			for(int i = 0; i <= params.length(); i++){
				if(i == params.length() || (!isString && count == 0 && params.charAt(i) == ',')){
					res.add(params.substring(prev, i));
					prev = i + 1;
				}else if(!isString && (params.charAt(i) == ')' || params.charAt(i) == ']' || params.charAt(i) == '}')) count++;
				else if(!isString && (params.charAt(i) == '(' || params.charAt(i) == '[' || params.charAt(i) == '{')) count--;
				else if(params.charAt(i) == '"') isString = !isString;
			}
			
			this.params = res;
		}
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
