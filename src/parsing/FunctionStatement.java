package parsing;

import java.util.ArrayList;
import java.util.Arrays;

import objects.reFunction;
import objects.reObject;
import utils.Utils;

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
		Utils.putVarByName(var, new reFunction(var, lines, params, this.start, this.end), start, end);
		return null;
	}
	
	@Override
	public int getLineNum(){
		return start;
	}
}
