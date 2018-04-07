package objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import core.Interpreter;
import core.Persistent.StackItem;

import static core.Persistent.*;

import parsing.Expression;
import parsing.Statement;

public class reFunction implements reObject{
	public ArrayList<Statement> lines = new ArrayList<>();
	public ArrayList<String> names = new ArrayList<>(); //parameter names
	public int start, end;
	public String funcName;
	
	public HashMap<ArrayList<reObject>, reObject> cacheMap = new HashMap<>();
	public int cacheSize = -1;
	
	public reFunction(String funcName, ArrayList<Statement> lines, ArrayList<String> names, int start, int end){
		this.funcName = funcName;
		this.lines = lines;
		
		if(names.size() >= 1 && names.get(names.size() - 1).startsWith("CACHE=")){
			String s = names.get(names.size() - 1).substring(6);
			cacheSize = s.equals("INF") ? Integer.MAX_VALUE :
				((reNumber)Expression.recursiveCalc(s, null, start, start, start)).val.intValue();
			names.remove(names.size() - 1);
		}
		
		this.names = names;
		this.start = start;
		this.end = end;
	}
	
	public reObject apply(reObject[] params){
		if(params.length != names.size())
			throw new IllegalArgumentException("Wrong number of arguments for function call!");
		
		ArrayList<reObject> key = new ArrayList<>(Arrays.asList(params));
		if(cacheSize != -1){
			if(cacheMap.containsKey(key)){
				return cacheMap.get(key);
			}
		}
		
		stack.push(new StackItem(start, end));
		for(int i = 0; i < params.length; i++){
			stack.peek().vars.put(names.get(i), params[i]);
		}
		stack.peek().vars.put(funcName, this); //reduce the need to traverse up the stack
		
		if(cacheMap.size() < cacheSize){ //deep clone objects so functions that modify params will work
			for(int i = 0; i < key.size(); i++){
				key.set(i, key.get(i).deepClone());
			}
		}
		
		reObject res = Interpreter.run(lines, start, end);
		if(cacheMap.size() < cacheSize){
			cacheMap.put(key, res);
		}
		return res;
	}
	
	@Override
	public String toString(){
		return "Function";
	}
	
	@Override
	public int hashCode(){
		return lines.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null)
			return false;
		if(getClass() != o.getClass())
			return false;
		if(!lines.equals(((reFunction)o).lines))
			return false;
		return true;
	}
	
	@Override
	public reObject deepClone(){
		return this;
	}
	
	@Override
	public ArrayList<reObject> getListVal(){
		return new ArrayList<reObject>(Arrays.asList(this));
	}
	
	@Override
	public int toBool(){
		return 1;
	}
}
