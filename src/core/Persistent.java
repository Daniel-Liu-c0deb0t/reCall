package core;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import objects.reClass;
import objects.reNumber;
import objects.reObject;

public class Persistent{
	public static BufferedReader src, in;
	public static ArrayDeque<StackItem> stack = new ArrayDeque<>();
	public static int progLine;
	
	public static final RoundingMode defaultRounding = RoundingMode.HALF_EVEN;
	public static final MathContext defaultMath = new MathContext(100, defaultRounding);
	
	public static final BigDecimal constE = new BigDecimal("2.7182818284590452353602874713526624977572470936999595749669676277240766303535475945713821785251664274");
	public static final BigDecimal constPI = new BigDecimal("3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679");
	
	static{
		stack.push(new StackItem(0, Integer.MAX_VALUE)); //includes all of the source code
		
		stack.peek().vars.put("E", new reNumber(constE));
		stack.peek().vars.put("PI", new reNumber(constPI));
		
		stack.peek().vars.put("Number", new reClass("Number", new ArrayList<String>(), new ArrayList<String>(), -1));
		stack.peek().vars.put("String", new reClass("String", new ArrayList<String>(), new ArrayList<String>(), -1));
		stack.peek().vars.put("List", new reClass("List", new ArrayList<String>(), new ArrayList<String>(), -1));
		stack.peek().vars.put("Map", new reClass("Map", new ArrayList<String>(), new ArrayList<String>(), -1));
		stack.peek().vars.put("Function", new reClass("Function", new ArrayList<String>(), new ArrayList<String>(), -1));
		stack.peek().vars.put("FileReader", new reClass("FileReader", new ArrayList<String>(), new ArrayList<String>(), -1));
		stack.peek().vars.put("FileWriter", new reClass("FileWriter", new ArrayList<String>(), new ArrayList<String>(), -1));
		stack.peek().vars.put("Window", new reClass("Window", new ArrayList<String>(), new ArrayList<String>(), -1));
	}
	
	public static class StackItem{
		public HashMap<String, reObject> vars;
		public int start, end; //interval of code for this stack item
		
		public StackItem(int start, int end){
			this.vars = new HashMap<>();
			this.start = start;
			this.end = end;
		}
		
		@Override
		public String toString(){
			return "start: " + start + " end: " + end + " " + vars.toString();
		}
	}
}
