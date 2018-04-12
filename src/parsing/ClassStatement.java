package parsing;

import java.util.ArrayList;
import java.util.Arrays;

import objects.reClass;
import objects.reObject;

public class ClassStatement implements Statement{
	public ArrayList<String> varsInit = new ArrayList<>();
	public ArrayList<String> varsStatic = new ArrayList<>();
	public String name;
	public int lineNum;
	
	public ClassStatement(String name, String vars, int lineNum){
		this.name = name;
		String[] s = vars.split(";");
		for(int i = 0; i < s.length; i++){
			if(s[i].startsWith("static"))
				this.varsStatic = new ArrayList<>(Arrays.asList(s[i].substring(6).split(",")));
			else
				this.varsInit = new ArrayList<>(Arrays.asList(s[i].split(",")));
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
