package parsing;

import java.util.ArrayList;
import java.util.Arrays;

import objects.reArrayAccessible;
import objects.reClassInst;
import objects.reList;
import objects.reMemberSelectable;
import objects.reObject;
import utils.Utils;

public class SetStatement implements Statement{
	public String var;
	public String exp;
	public int lineNum;
	
	public SetStatement(String var, String exp, int lineNum){
		this.var = var;
		this.exp = exp;
		this.lineNum = lineNum;
	}
	
	@Override
	public int getLineNum(){
		return lineNum;
	}
	
	@Override
	public reObject calc(int start, int end){
		calcSet(var, exp, start, end, lineNum);
		return null;
	}
	
	public static reObject calcSet(String var, String exp, int start, int end, int lineNum){
		reObject res = Expression.recursiveCalc(exp, null, start, end, lineNum);
		
		int count = 0;
		int prev = 0;
		boolean isString = false;
		ArrayList<String> vars = new ArrayList<>();
		for(int i = 0; i <= var.length(); i++){ //split by ','
			if(i == var.length() || (!isString && count == 0 && var.charAt(i) == ',')){
				vars.add(var.substring(prev, i));
				prev = i + 1;
			}else if(!isString && var.charAt(i) == '[') count++;
			else if(!isString && var.charAt(i) == ']') count--;
			else if(var.charAt(i) == '"') isString = !isString;
		}
		
		if(vars.size() == 1){
			set(vars.get(0), res, start, end, lineNum);
		}else{
			if(res instanceof reList){ //unpack list
				if(vars.size() == res.getListVal().size()){
					for(int i = 0; i < vars.size(); i++){
						set(vars.get(i), res.getListVal().get(i), start, end, lineNum);
					}
				}else{
					throw new IllegalArgumentException("The expression, \"" + exp + "\", cannot be unpacked into " + vars.size() + " elements!");
				}
			}else if(res instanceof reClassInst){ //unpack class variables
				if(vars.size() == ((reClassInst)res).vars.size()){
					for(int i = 0; i < vars.size(); i++){
						reObject o = ((reClassInst)res).vars.get(((reClassInst)res).c.order.get(i));
						set(vars.get(i), o, start, end, lineNum);
					}
				}else{
					throw new IllegalArgumentException("The expression, \"" + exp + "\", cannot be unpacked into " + vars.size() + " elements!");
				}
			}else{
				for(String s : vars){
					set(s, res, start, end, lineNum);
				}
			}
		}
		
		return res;
	}
	
	public static void set(String var, reObject res, int start, int end, int lineNum){
		int count = 0;
		boolean isString = false;
		int idxArr = -1;
		for(int i = var.length() - 1; i >= 0; i--){
			if(!isString && var.charAt(i) == ']') count++;
			else if(!isString && var.charAt(i) == '[') count--;
			else if(var.charAt(i) == '"') isString = !isString;
			else if(!isString && count == 0){
				idxArr = i + 1;
				break;
			}
		}
		
		if(idxArr != -1 && idxArr < var.length() && var.charAt(var.length() - 1) == ']'){ //handle array set
			String varName = var.substring(0, idxArr);
			reObject curr = Expression.recursiveCalc(varName, null, start, end, lineNum);
			if(curr == null)
				throw new IllegalArgumentException("Undefined variable name: \"" + var + "\"");
			
			if(curr instanceof reArrayAccessible){
				count = 0;
				int prev = 0;
				isString = false;
				
				for(int i = idxArr; i < var.length(); i++){
					if(var.charAt(i) == '"') isString = !isString;
					else if(!isString && var.charAt(i) == '['){
						count++;
						if(count == 1){
							prev = i + 1;
						}
					}else if(!isString && var.charAt(i) == ']'){
						if(count == 1){
							if(curr instanceof reArrayAccessible){
								if(i == var.length() - 1)
									((reArrayAccessible)curr).set(
											Expression.recursiveCalc(var.substring(prev, i), null, start, end, lineNum), res);
								else
									curr = ((reArrayAccessible)curr).get(new ArrayList<reObject>(
										Arrays.asList(Expression.recursiveCalc(var.substring(prev, i), null, start, end, lineNum))));
							}else{
								throw new IllegalArgumentException("\"" + var + "\" cannot be accessed using []!");
							}
						}
						count--;
					}
				}
			}else{
				throw new IllegalArgumentException("\"" + var + "\" cannot be accessed using []!");
			}
		}else{
			count = 0;
			isString = false;
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
			
			if(idxDot == -1){
				Utils.putVarByName(var, res, start, end);
			}else{
				reObject o = Expression.recursiveCalc(var.substring(0, idxDot), null, start, end, lineNum);
				if(o instanceof reMemberSelectable){
					((reMemberSelectable)o).set(var.substring(idxDot + 1), res);
				}else{
					throw new IllegalArgumentException("\"" + var + "\" cannot be selected using the \".\" operator!");
				}
			}
		}
	}
}
