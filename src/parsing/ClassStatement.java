package parsing;

import java.util.ArrayList;

import objects.reClass;
import objects.reObject;

public class ClassStatement implements Statement{
	public ArrayList<String> varsInit = new ArrayList<>();
	public ArrayList<String> varsStatic = new ArrayList<>();
	public String name;
	public int lineNum;
	
	public ClassStatement(String name, String vars, int lineNum){
		this.name = name;
		
		int count = 0;
		boolean isString = false;
		int prev = 0;
		ArrayList<String> s = new ArrayList<>();
		for(int i = 0; i <= vars.length(); i++){
			if(i == vars.length() || (!isString && count == 0 && vars.charAt(i) == ';')){ //split by semicolon
				s.add(vars.substring(prev, i));
				prev = i + 1;
			}else if(!isString && (vars.charAt(i) == ')' || vars.charAt(i) == ']' || vars.charAt(i) == '}')) count++;
			else if(!isString && (vars.charAt(i) == '(' || vars.charAt(i) == '[' || vars.charAt(i) == '{')) count--;
			else if(vars.charAt(i) == '"') isString = !isString;
		}
		
		if(s.size() != 1 || !s.get(0).isEmpty()){
			for(int i = 0; i < s.size(); i++){
				String str = s.get(i).startsWith("static") ? s.get(i).substring(6) : s.get(i);
				
				count = 0;
				isString = false;
				prev = 0;
				ArrayList<String> split = new ArrayList<>();
				for(int j = 0; j <= str.length(); j++){
					if(j == str.length() || (!isString && count == 0 && str.charAt(j) == ',')){ //split by comma
						split.add(str.substring(prev, j));
						prev = j + 1;
					}else if(!isString && (str.charAt(j) == ')' || str.charAt(j) == ']' || str.charAt(j) == '}')) count++;
					else if(!isString && (str.charAt(j) == '(' || str.charAt(j) == '[' || str.charAt(j) == '{')) count--;
					else if(str.charAt(j) == '"') isString = !isString;
				}
				
				if(s.get(i).startsWith("static")){
					this.varsStatic = split;
				}else{
					this.varsInit = split;
				}
			}
		}
		this.lineNum = lineNum;
	}
	
	@Override
	public reObject calc(int start, int end){
		SetStatement.set(name, new reClass(name, varsStatic, varsInit, lineNum), start, end, start);
		return null;
	}
	
	@Override
	public int getLineNum(){
		return lineNum;
	}
}
