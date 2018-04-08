package utils;

import java.util.ArrayList;
import java.util.Collections;

import utils.Functions.Function;

import static utils.Functions.*;

public class Operators{
	public static ArrayList<Symbol> operators = new ArrayList<>();
	public static ArrayList<String> reversePrecedance = new ArrayList<>();
	public static int precedanceCount = 0;
	public static int precedanceCountShort = 0;
	
	static{
		operators.add(new Symbol("+", 3, false, add, "add"));
		operators.add(new Symbol("-", 3, false, subtract, "subtract"));
		operators.add(new Symbol("*", 2, false, multiply, "multiply"));
		operators.add(new Symbol("/", 2, false, divide, "divide"));
		operators.add(new Symbol("//", 2, false, floorDivide, "floor divide"));
		operators.add(new Symbol("%", 2, false, mod, "modulo"));
		operators.add(new Symbol("**", 1, false, pow, "exponentiate"));
		
		//unary not operators
		//'^' is used as a placeholder to simplify code
		//unary '!' should be used in actual source code
		operators.add(new Symbol("!", 0, false, null, "not"));
		operators.add(new Symbol("^", 0, false, not, "not (should not actually be used in code)"));
		
		operators.add(new Symbol(">", 4, false, cmpGreater, "greater than"));
		operators.add(new Symbol("<", 4, false, cmpLess, "less than"));
		operators.add(new Symbol(">=", 4, false, cmpGreaterEq, "greater than or equal to"));
		operators.add(new Symbol("<=", 4, false, cmpLessEq, "less than or equal to"));
		operators.add(new Symbol("==", 5, false, cmpEq, "equal to"));
		operators.add(new Symbol("!=", 5, false, cmpNotEq, "not equal to"));
		
		//logical operators are specially handled to support short circuiting
		operators.add(new Symbol("&&", 0, true, null, "logical and"));
		reversePrecedance.add("&&");
		operators.add(new Symbol("||", 1, true, null, "logical or"));
		reversePrecedance.add("||");
		
		for(Symbol s : operators){
			if(s.shortCircuit)
				precedanceCountShort = Math.max(precedanceCountShort, s.precedance + 1);
			else
				precedanceCount = Math.max(precedanceCount, s.precedance + 1);
		}
		Collections.sort(operators, (a, b) -> b.op.length() - a.op.length());
	}
	
	public static Symbol getOperatorStart(String s){
		for(Symbol op : operators){
			if(s.startsWith(op.op)){
				return op;
			}
		}
		return null;
	}
	
	public static Symbol getOperatorEnd(String s){
		for(Symbol op : operators){
			if(s.endsWith(op.op)){
				return op;
			}
		}
		return null;
	}
	
	public static class Symbol{
		public String op, desc;
		public int precedance;
		public boolean shortCircuit;
		public Function func;
		
		public Symbol(String op, int precedance, boolean shortCircuit, Function func, String desc){
			this.op = op;
			this.precedance = precedance;
			this.shortCircuit = shortCircuit;
			this.func = func;
			this.desc = desc;
		}
	}
}
