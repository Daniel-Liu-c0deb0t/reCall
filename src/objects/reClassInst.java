package objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class reClassInst implements reObject, reMemberSelectable{
	public HashMap<String, reObject> vars;
	public reClass c;
	
	public reClassInst(reClass c, ArrayList<reObject> vars){
		this.c = c;
		this.vars = new HashMap<>();
		
		if(vars.size() != c.varsInit.size())
			throw new IllegalArgumentException("\"" + c.name + "\" only accepts " + c.varsInit.size() + " arguments!");
		
		for(int i = 0; i < vars.size(); i++){
			this.vars.put(c.order.get(i), vars.get(i));
		}
	}
	
	public reClassInst(reClass c, HashMap<String, reObject> map){
		this.c = c;
		this.vars = new HashMap<>(map);
	}
	
	@Override
	public String getType(){
		return c.name;
	}
	
	@Override
	public reObject select(String s){
		if(vars.containsKey(s)){
			return vars.get(s);
		}else if(c.varsStatic.containsKey(s)){
			return c.varsStatic.get(s);
		}else{
			throw new IllegalArgumentException(s + " cannot be selected using the \".\" operator!");
		}
	}
	
	@Override
	public void set(String s, reObject o){
		if(c.varsStatic.containsKey(s))
			c.varsStatic.put(s, o);
		else if(vars.containsKey(s))
			vars.put(s, o);
		else
			throw new IllegalArgumentException("The variable, \"" + s + "\", is not found!");
	}
	
	@Override
	public reObject deepClone(){
		return new reClassInst(c, vars);
	}
	
	@Override
	public ArrayList<reObject> getListVal(){
		return new ArrayList<reObject>(Arrays.asList(this));
	}
	
	@Override
	public int toBool(){
		return 1;
	}
	
	@Override
	public String toString(){
		ArrayList<String> pairs = new ArrayList<>();
		for(String key : vars.keySet()){
			String keyStr = key;
			String valStr = vars.get(key) instanceof reString ?
					("\"" + vars.get(key).toString() + "\"") : vars.get(key).toString();
			pairs.add(keyStr + ": " + valStr);
		}
		return c.name + ": {" + String.join(", ", pairs) + "}";
	}
	
	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + c.hashCode();
		result = prime * result + vars.hashCode();
		return result;
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null)
			return false;
		if(getClass() != o.getClass())
			return false;
		if(!c.equals(((reClassInst)o).c) || !vars.equals(((reClassInst)o).vars))
			return false;
		return true;
	}
}
