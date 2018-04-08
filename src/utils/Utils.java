package utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import core.Persistent.StackItem;
import objects.reList;
import objects.reNumber;
import objects.reObject;
import objects.reString;
import parsing.Statement;

import static core.Persistent.*;

public class Utils{
	public static final String varName = "^[a-zA-Z_][0-9a-zA-Z_]*$";
	public static final Pattern varNamePat = Pattern.compile(varName);
	
	public static final String funcName = "^[a-zA-Z_][0-9a-zA-Z_]*$";
	public static final Pattern funcNamePat = Pattern.compile(funcName);
	
	//only variables from stack items that include the range are examined
	public static reObject getVarByName(String name, int start, int end){
		boolean flag = false;
		for(Iterator<StackItem> it = stack.iterator(); it.hasNext();){
			StackItem item = it.next();
			//this also works for single line (lambda) recursive functions because
			//variables cannot be defined in those functions
			if((!flag || (start == item.start && start == end && item.start == item.end) ||
					(item.start < start && end <= item.end)) && item.vars.containsKey(name)){
				return item.vars.get(name);
			}
			flag = true;
		}
		return null;
	}
	
	public static void putVarByName(String name, reObject val, int start, int end){
		boolean flag = false;
		for(Iterator<StackItem> it = stack.iterator(); it.hasNext();){
			StackItem item = it.next();
			if((!flag || (start == item.start && start == end && item.start == item.end) ||
					(item.start < start && end <= item.end)) && item.vars.containsKey(name)){
				item.vars.put(name, val);
				return;
			}
			flag = true;
		}
		stack.peek().vars.put(name, val);
	}
	
	public static int findLastLine(ArrayList<Statement> arr){
		for(int i = arr.size() - 1; i >= 0; i--){
			if(arr.get(i).getLineNum() != -1){
				return arr.get(i).getLineNum();
			}
		}
		return -1;
	}
	
	public static void printAllVars(){
		for(Iterator<StackItem> it = stack.descendingIterator(); it.hasNext();){
			HashMap<String, reObject> map = it.next().vars;
			for(String s : map.keySet()){
				System.out.println("\"" + s + "\": " + map.get(s).toString());
			}
		}
	}
	
	public static boolean isVarName(String s){
		return varNamePat.matcher(s).matches();
	}
	
	public static boolean isFuncName(String s){
		return funcNamePat.matcher(s).matches();
	}
	
	public static String removeSpaces(String s){
		boolean isString = false;
		StringBuilder res = new StringBuilder();
		for(int i = 0; i < s.length(); i++){
			if(s.charAt(i) == '"'){
				isString = !isString;
			}
			if(isString){
				res.append(s.charAt(i));
			}else{
				if(!Character.isWhitespace(s.charAt(i))){
					res.append(s.charAt(i));
				}
			}
		}
		return res.toString();
	}
	
	public static int countLeftSpaces(String s){
		int i = 0;
		while(i < s.length() && Character.isWhitespace(s.charAt(i))){
			i++;
		}
		return i;
	}
	
	public static String join(reObject[] o, String d, int off, boolean prettyStr){
		StringBuilder s = new StringBuilder();
		for(int i = off; i < o.length; i++){
			if(prettyStr && o[i] instanceof reString)
				s.append("\"" + o[i].toString() + "\"" + d);
			else
				s.append(o[i].toString() + d);
		}
		if(s.length() > 0)
			s.delete(s.length() - d.length(), s.length());
		return s.toString();
	}
	
	public static void swap(reObject[] arr, int i, int j){
		reObject t = arr[i];
		arr[i] = arr[j];
		arr[j] = t;
	}
	
	public static void swap(ArrayList<reObject> arr, int i, int j){
		Collections.swap(arr, i, j);
	}
	
	public static int compare(reObject a, reObject b){
		if(a instanceof reNumber && b instanceof reNumber){
			return ((reNumber)a).compareTo((reNumber)b);
		}else if((a instanceof reString && b instanceof reString) ||
				((a instanceof reString || b instanceof reString) &&
				(a instanceof reNumber || b instanceof reNumber))){
			a = a instanceof reString ? (reString)a : new reString(a.toString());
			b = b instanceof reString ? (reString)b : new reString(b.toString());
			return ((reString)a).compareTo((reString)b);
		}else if(a instanceof reList && b instanceof reList){
			return ((reList)a).compareTo((reList)b);
		}else{
			throw new IllegalArgumentException("Cannot compare \"" + a.toString() + "\" and \"" + b.toString() + "\"!");
		}
	}
	
	public static int getRGB(ArrayList<reObject> arr){
		int r = ((reNumber)arr.get(0)).val.intValue();
		int g = ((reNumber)arr.get(1)).val.intValue();
		int b = ((reNumber)arr.get(2)).val.intValue();
		return ((r & 0x0FF) << 16) | ((g & 0x0FF) << 8) | (b & 0x0FF);
	}
	
	private static BigInteger maxExp = new BigInteger("999999999");
	private static BigInteger two = new BigInteger("2");
	public static BigDecimal intPow(BigDecimal a, BigInteger b){
		if(b.compareTo(maxExp) <= 0){
			return a.pow(b.intValue(), defaultMath);
		}
		
		BigDecimal x = intPow(a, b.divide(two));
		if(b.testBit(0)){ //odd
			return a.multiply(x.pow(2, defaultMath), defaultMath);
		}else{ //even
			return x.pow(2, defaultMath);
		}
	}
	
	public static void handleException(Throwable e, String message){
		if(message == null){
			System.err.println("Error on line " +
					progLine + " in the source code!\n" + e.getMessage());
		}else{
			System.err.println(message);
		}
		System.err.println("\nNote that the stack trace below only applies to the interpreter!");
		e.printStackTrace();
		System.exit(1);
	}
}
