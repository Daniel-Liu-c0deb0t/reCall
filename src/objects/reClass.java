package objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import parsing.Expression;

public class reClass implements reObject, reMemberSelectable{
	public HashMap<String, reObject> varsStatic = new HashMap<>();
	public HashMap<String, reObject> varsInit = new HashMap<>();
	public ArrayList<String> order = new ArrayList<>();
	public String name;
	
	public reClass(String name, ArrayList<String> varsStatic, ArrayList<String> varsInit, int lineNum){
		this.name = name;
		for(String s : varsStatic){
			int idxEq = s.indexOf('=');
			String str = null;
			reObject o = null;
			if(idxEq != -1){
				str = s.substring(0, idxEq);
				o = Expression.recursiveCalc(s.substring(idxEq + 1), null, lineNum, lineNum, lineNum);
			}else{
				str = s;
			}
			this.varsStatic.put(str, o);
		}
		order = new ArrayList<>(varsInit);
		for(String s : varsInit){
			this.varsInit.put(s, null);
		}
	}
	
	@Override
	public String getType(){
		return name;
	}
	
	@Override
	public reObject select(String s){
		if(varsStatic.containsKey(s)){
			return varsStatic.get(s);
		}else{
			throw new IllegalArgumentException(s + " cannot be selected using the \".\" operator!");
		}
	}
	
	@Override
	public void set(String s, reObject o){
		varsStatic.put(s, o);
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
	
	@Override
	public boolean equals(Object o){
		if(o == null)
			return false;
		if(getClass() != o.getClass())
			return false;
		if(!name.equals(((reClass)o).name) ||
				!varsStatic.keySet().equals(((reClass)o).varsStatic.keySet()) ||
				!order.equals(((reClass)o).order))
			return false;
		return true;
	}
	
	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + name.hashCode();
		result = prime * result + varsStatic.keySet().hashCode();
		result = prime * result + varsInit.hashCode();
		return result;
	}
	
	@Override
	public String toString(){
		return "Class: " + name;
	}
}
