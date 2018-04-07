package parsing;

import objects.reArrayAccessible;
import objects.reFunction;
import objects.reList;
import objects.reMap;
import objects.reNumber;
import objects.reObject;
import objects.reString;
import utils.Functions;

import static utils.Utils.*;
import static utils.Operators.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class Expression implements Statement{
	public String exp;
	public int lineNum;
	
	public Expression(String exp, int lineNum){
		this.exp = exp;
		this.lineNum = lineNum;
	}
	
	@Override
	public reObject calc(int start, int end){
		return recursiveCalc(exp, null, start, end, lineNum);
	}
	
	@Override
	public int getLineNum(){
		return lineNum;
	}
	
	public static reObject recursiveCalc(String s, String prevS, int start, int end, int lineNum){
		if(s == null || s.isEmpty()) return null;
		if(s.equals(prevS)){
			throw new IllegalArgumentException("Syntax error: \"" + s + "\"!");
		}
		
		//get rid of extra parentheses
		int begin = 0;
		while(begin < s.length() && s.charAt(begin) == '('){
			begin++;
		}
		int ending = s.length() - 1;
		while(ending >= 0 && s.charAt(ending) == ')'){
			ending--;
		}
		ending++;
		
		//min = number of levels of 'useless' parentheses
		//ex: (( (1 + 2) * 3 ))
		int min = Integer.MAX_VALUE;
		int count = begin;
		boolean isString = false;
		for(int i = begin; i < ending; i++){
			if(!isString && s.charAt(i) == '(') count++;
			else if(!isString && s.charAt(i) == ')') count--;
			else if(s.charAt(i) == '"') isString = !isString;
			min = Math.min(min, count);
		}
		
		s = s.substring(min, s.length() - min);
		
		//base cases for recursion
		if(reNumber.isNumber(s)){
			return new reNumber(s);
		}
		
		if(isVarName(s)){
			reObject o = getVarByName(s, start, end);
			if(o == null)
				throw new IllegalArgumentException("Bad variable name: \"" + s + "\"!");
			return o;
		}
		
		//check if it's a string literal
		isString = false;
		for(int i = 0; i < s.length() - 1; i++){
			if(s.charAt(i) == '"') isString = !isString;
			if(!isString) break;
		}
		if(isString && s.endsWith("\"")){
			return new reString(s.substring(1, s.length() - 1));
		}
		
		//check if it's a list literal
		count = 0;
		isString = false;
		for(int i = 0; i < s.length() - 1; i++){
			if(!isString && s.charAt(i) == '[') count++;
			else if(!isString && s.charAt(i) == ']') count--;
			else if(s.charAt(i) == '"') isString = !isString;
			if(count == 0) break;
		}
		if(count == 1 && s.charAt(s.length() - 1) == ']'){
			count = 0;
			isString = false;
			int prev = 1;
			ArrayList<reObject> res = new ArrayList<>();
			
			for(int i = 1; i < s.length(); i++){
				if(i == s.length() - 1 ||
						(!isString && count == 0 && s.charAt(i) == ',')){
					reObject o = recursiveCalc(s.substring(prev, i), s, start, end, lineNum);
					if(o != null)
						res.add(o);
					prev = i + 1;
				}else if(!isString && (s.charAt(i) == '(' || s.charAt(i) == '{' || s.charAt(i) == '[')) count++;
				else if(!isString && (s.charAt(i) == ')' || s.charAt(i) == '}' || s.charAt(i) == ']')) count--;
				else if(s.charAt(i) == '"') isString = !isString;
			}
			
			return new reList(res);
		}
		
		//check if its a map literal
		count = 0;
		isString = false;
		for(int i = 0; i < s.length() - 1; i++){
			if(!isString && s.charAt(i) == '{') count++;
			else if(!isString && s.charAt(i) == '}') count--;
			else if(s.charAt(i) == '"') isString = !isString;
			if(count == 0) break;
		}
		if(count == 1 && s.charAt(s.length() - 1) == '}'){
			count = 0;
			isString = false;
			int prev = 1;
			int colon = -1;
			reObject key = null;
			HashMap<reObject, reObject> res = new HashMap<>();
			
			for(int i = 1; i < s.length(); i++){
				if(i == s.length() - 1 ||
						(!isString && count == 0 && s.charAt(i) == ',')){
					reObject val = recursiveCalc(s.substring(Math.max(prev, colon), i), s, start, end, lineNum);
					if(val != null){
						if(colon == -1)
							res.put(val, new reNumber(BigDecimal.ZERO));
						else
							res.put(key, val);
					}
					prev = i + 1;
				}else if(!isString && count == 0 && s.charAt(i) == ':'){
					key = recursiveCalc(s.substring(prev, i), s, start, end, lineNum);
					colon = i + 1;
				}else if(!isString && (s.charAt(i) == '(' || s.charAt(i) == '[' || s.charAt(i) == '{')) count++;
				else if(!isString && (s.charAt(i) == ')' || s.charAt(i) == ']' || s.charAt(i) == '}')) count--;
				else if(s.charAt(i) == '"') isString = !isString;
			}
			
			return new reMap(res);
		}
		
		//handles array access for strings, lists, maps
		//has to not be a list literal
		int lIdx = -1;
		count = 0;
		isString = false;
		for(int i = 0; i < s.length() - 1; i++){ //search for '['
			if(!isString && (s.charAt(i) == '(' || s.charAt(i) == '{')) count++;
			else if(!isString && (s.charAt(i) == ')' || s.charAt(i) == '}')) count--;
			else if(s.charAt(i) == '"') isString = !isString;
			else if(lIdx != -1 && !isString && s.charAt(i) == '[') count++;
			else if(lIdx != -1 && !isString && s.charAt(i) == ']') count--;
			else if(lIdx == -1 && !isString && count == 0){
				if(s.charAt(i) == '['){
					lIdx = i;
					count++;
				}else if(getOperatorStart(s.substring(i)) != null){
					break;
				}
			}else if(lIdx != -1 && count == 0) break;
		}
		
		if(lIdx > 0 && count == 1 && s.charAt(s.length() - 1) == ']'){ //if array access
			reObject arr = recursiveCalc(s.substring(0, lIdx), s, start, end, lineNum); //get array
			if(arr instanceof reArrayAccessible){
				int prev = 0;
				count = 0;
				isString = false;
				ArrayList<reObject> indexes = new ArrayList<>();
				for(int i = lIdx; i < s.length(); i++){
					if(s.charAt(i) == '"') isString = !isString;
					else if(!isString && s.charAt(i) == '['){
						count++;
						if(count == 1){
							indexes.clear();
							prev = i + 1;
						}
					}else if(!isString && s.charAt(i) == ']'){
						if(count == 1){
							reObject o = recursiveCalc(s.substring(prev, i), s, start, end, lineNum);
							if(o == null) //if empty
								indexes.add(null);
							else
								indexes.add(o);
							if(arr instanceof reArrayAccessible)
								arr = ((reArrayAccessible)arr).get(indexes);
							else
								throw new IllegalArgumentException("\"" + s + "\" cannot be accessed using []!");
							prev = i + 1;
						}
						count--;
					}else if(!isString && count == 1 && s.charAt(i) == ':'){
						reObject o = recursiveCalc(s.substring(prev, i), s, start, end, lineNum);
						if(o == null) //if empty
							indexes.add(null);
						else
							indexes.add(o);
						prev = i + 1;
					}
				}
			}else{
				throw new IllegalArgumentException("\"" + s + "\" cannot be accessed using []!");
			}
			
			return arr;
		}
		
		//handles function calls
		lIdx = s.indexOf('(');
		count = 1;
		isString = false;
		for(int i = lIdx + 1; i < s.length() - 1; i++){
			if(!isString && s.charAt(i) == '(') count++;
			else if(!isString && s.charAt(i) == ')') count--;
			else if(s.charAt(i) == '"') isString = !isString;
			if(count == 0) break;
		}
		
		if(lIdx > 0 && count > 0 && s.charAt(s.length() - 1) == ')'){
			String funcName = s.substring(0, lIdx);
			if(isFuncName(funcName)){
				reObject func = getVarByName(funcName, start, end);
				
				if(!funcName.equals("eval") && (func == null || !(func instanceof reFunction))
						&& !Functions.functions.containsKey(funcName)){
					throw new IllegalArgumentException("Bad function call using \"" + s + "\"!");
				}
				
				count = 0;
				isString = false;
				int prev = lIdx + 1;
				ArrayList<reObject> params = new ArrayList<>();
				for(int i = lIdx + 1; i < s.length(); i++){
					if(i == s.length() - 1 || (!isString && count == 0 && s.charAt(i) == ',')){
						reObject res = recursiveCalc(s.substring(prev, i), s, start, end, lineNum);
						if(res != null)
							params.add(res);
						prev = i + 1;
					}
					else if(s.charAt(i) == '"') isString = !isString;
					else if(!isString && (s.charAt(i) == '[' || s.charAt(i) == '{' || s.charAt(i) == '(')) count++;
					else if(!isString && (s.charAt(i) == ']' || s.charAt(i) == '}' || s.charAt(i) == ')')) count--;
				}
				
				if(funcName.equals("eval")){ //special handling for eval() function
					reObject[] arr = new reObject[params.size() + 1];
					for(int i = 0; i < params.size(); i++){
						arr[i + 1] = params.get(i);
					}
					arr[0] = new reNumber(new BigDecimal(lineNum));
					return Functions.eval.apply(arr);
				}else if(func != null && func instanceof reFunction){
					return ((reFunction)func).apply(params.toArray(new reObject[params.size()]));
				}else{
					return Functions.functions.get(funcName).func.apply(params.toArray(new reObject[params.size()]));
				}
			}
		}
		
		//handle inline function calls
		lIdx = s.indexOf('(');
		int rIdx = s.indexOf(')');
		if(lIdx != -1 && rIdx != -1){
			String[] vars = s.substring(lIdx + 1, rIdx).split(",");
			boolean isParams = true;
			for(int i = 0; i < vars.length; i++){
				if(!isVarName(vars[i])){
					isParams = false;
					break;
				}
			}
			if(vars.length == 1 && vars[0].isEmpty()){ //no parameters
				isParams = true;
				vars = new String[0];
			}
			if(isParams && s.startsWith("->", rIdx + 1)){
				ArrayList<Statement> lines = new ArrayList<>();
				lines.add(new ReturnStatement(s.substring(rIdx + 3), lineNum));
				lines.add(new EndStatement());
				return new reFunction("this", lines, new ArrayList<String>(Arrays.asList(vars)), lineNum, lineNum);
			}
		}
		
		//handle ternary (if/else) operations
		count = 0;
		int ifIdx = -1; //if '?' is found
		int elseIdx = -1;
		for(int i = 0; i < s.length(); i++){
			if(!isString && (s.charAt(i) == '[' || s.charAt(i) == '{' || s.charAt(i) == '(')) count++;
			else if(!isString && (s.charAt(i) == ']' || s.charAt(i) == '}' || s.charAt(i) == ')')) count--;
			else if(s.charAt(i) == '"') isString = !isString;
			else if(!isString && count == 0 && s.charAt(i) == '?'){
				ifIdx = i;
			}else if(!isString && count == 0 && ifIdx != -1 && s.startsWith("else", i)){
				elseIdx = i;
			}
		}
		
		if(ifIdx != -1 && elseIdx != -1){
			if(recursiveCalc(s.substring(0, ifIdx), s, start, end, lineNum).toBool() != 0){
				return recursiveCalc(s.substring(ifIdx + 1, elseIdx), s, lineNum, lineNum, lineNum);
			}else{
				return recursiveCalc(s.substring(elseIdx + 4), s, lineNum, lineNum, lineNum);
			}
		}
		
		//handle short circuit operators ("&&" and "||")
		for(int i = precedanceCountShort - 1; i >= 0; i--){
			count = 0;
			isString = false;
			int prev = 0;
			boolean stop = false;
			
			for(int j = 0; j <= s.length(); j++){
				Symbol symbol = null;
				
				if(j < s.length()){
					symbol = getOperatorStart(s.substring(j));
					
					if(s.charAt(j) == '"'){
						isString = !isString;
					}else if(!isString && (s.charAt(j) == '(' || s.charAt(j) == '[' || s.charAt(j) == '{')){
						count++;
					}else if(!isString && (s.charAt(j) == ')' || s.charAt(j) == ']' || s.charAt(j) == '}')){
						count--;
					}
				}
				
				if((j == s.length() && stop) || (!isString && count == 0 && symbol != null &&
						symbol.shortCircuit && symbol.precedance == i)){
					reObject res = recursiveCalc(s.substring(prev, j), s, start, end, lineNum);
					
					if(reversePrecedance.get(i).equals("||") && res.toBool() != 0)
						return new reNumber(BigDecimal.ONE);
					else if(reversePrecedance.get(i).equals("&&") && res.toBool() == 0)
						return new reNumber(BigDecimal.ZERO);
					
					if(j < s.length())
						prev = j + symbol.op.length();
					stop = true;
				}
			}
			
			if(stop){
				if(reversePrecedance.get(i).equals("||")){
					return new reNumber(BigDecimal.ZERO);
				}else{
					return new reNumber(BigDecimal.ONE);
				}
			}
		}
		
		//parse the expression
		//only solve "level 0" items
		//ex: (1 + 2) * 3 -> "(1 + 2)", "*", and "3"
		count = 0;
		isString = false;
		LinkedList<Object> arr = new LinkedList<>();
		arr.add(new StringBuilder());
		for(int i = 0; i < s.length(); i++){
			Symbol symbol = getOperatorStart(s.substring(i));
			
			if(!isString && (s.charAt(i) == '(' || s.charAt(i) == '[' || s.charAt(i) == '{')){
				((StringBuilder)arr.getLast()).append(s.charAt(i));
				count++;
			}else if(!isString && (s.charAt(i) == ')' || s.charAt(i) == ']' || s.charAt(i) == '}')){
				((StringBuilder)arr.getLast()).append(s.charAt(i));
				count--;
			}else if(!isString && count == 0 && symbol != null){
				Object o = arr.removeLast();
				arr.add(((StringBuilder)o).toString());
				arr.add(symbol);
				arr.add(new StringBuilder());
				i += symbol.op.length() - 1;
			}else if(s.charAt(i) == '"'){
				isString = !isString;
				((StringBuilder)arr.getLast()).append(s.charAt(i));
			}else{
				((StringBuilder)arr.getLast()).append(s.charAt(i));
			}
		}
		
		if(!arr.isEmpty()){
			Object o = arr.getLast();
			if(o instanceof StringBuilder){
				arr.removeLast();
				arr.add(((StringBuilder)o).toString());
			}
		}
		
		//separately handle unary operators
		ListIterator<Object> it = arr.listIterator();
		while(it.hasNext()){
			Object o = it.next();
			
			if(o instanceof String && ((String)o).isEmpty() && it.hasNext()){
				Object n = it.next();
				if(n instanceof Symbol){
					if(((Symbol)n).op.equals("-")){ //unary negation
						it.remove();
						it.previous();
						it.remove();
						String str = (String)it.next();
						it.remove();
						it.add("(-1)*" + str); //multiply by -1
					}else if(((Symbol)n).op.equals("!")){ //unary not
						it.remove();
						it.previous();
						it.remove();
						String str = (String)it.next();
						it.remove();
						it.add("1^" + str);
					}
				}
			}
		}
		
		//use recursion to solve deeper level expressions
		//handles order of operations
		if(arr.size() > 1){
			for(int i = 0; i < precedanceCount && arr.size() > 1; i++){
				it = arr.listIterator();
				while(it.hasNext() && arr.size() > 1){
					Object o = it.next();
					if(o instanceof Symbol && ((Symbol)o).precedance == i){
						it.remove();
						Object prev = it.previous();
						it.remove();
						Object next = it.next();
						it.remove();
						
						reObject a = (prev instanceof reObject) ?
								(reObject)prev : recursiveCalc((String)prev, s, start, end, lineNum);
						reObject b = (next instanceof reObject) ?
								(reObject)next : recursiveCalc((String)next, s, start, end, lineNum);
						
						it.add(((Symbol)o).func.apply(new reObject[]{a, b}));
					}
				}
			}
		}else{
			arr.add(recursiveCalc((String)arr.removeFirst(), s, start, end, lineNum));
		}
		
		if(arr.size() == 1 && arr.getFirst() instanceof reObject)
			return (reObject)arr.getFirst();
		else
			throw new IllegalArgumentException("Badly formatted expression: \"" + s + "\"");
	}
	
	@Override
	public String toString(){
		return exp;
	}
}
