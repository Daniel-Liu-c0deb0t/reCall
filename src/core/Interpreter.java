package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.TreeMap;

import objects.reNumber;
import objects.reObject;
import parsing.ClassStatement;
import parsing.ElseStatement;
import parsing.EndStatement;
import parsing.Expression;
import parsing.FunctionStatement;
import parsing.IfStatement;
import parsing.ReturnStatement;
import parsing.SetStatement;
import parsing.Statement;
import utils.Operators;
import utils.Operators.Symbol;

import static utils.Utils.*;
import static core.Persistent.*;

public class Interpreter{
	public static ArrayList<Statement> parse(InputStream srcStream){
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
			@Override
			public void uncaughtException(Thread t, Throwable e){
				handleException(e, null);
			}
		});
		
		src = new BufferedReader(new InputStreamReader(srcStream));
		in = new BufferedReader(new InputStreamReader(System.in));
		
		ArrayDeque<ArrayList<Statement>> prog = new ArrayDeque<>();
		prog.push(new ArrayList<Statement>());
		ArrayDeque<Integer> indentsIf = new ArrayDeque<>();
		ArrayDeque<Statement> elifs = new ArrayDeque<>();
		ArrayDeque<Integer> indentsFunc = new ArrayDeque<>();
		String line = null;
		int lineNum = 0;
		
		try{
			while((line = src.readLine()) != null){
				lineNum++;
				progLine = lineNum;
				
				int indent = countLeftSpaces(line);
				line = removeSpaces(line);
				
				boolean isString = false;
				for(int i = 0; i < line.length(); i++){ //search for '#'
					if(line.charAt(i) == '"') isString = !isString;
					else if(!isString && line.charAt(i) == '#'){
						line = line.substring(0, i);
						break;
					}
				}
				
				if(line.isEmpty()){
					continue;
				}
				
				StringBuilder builder = new StringBuilder(line);
				while(builder.charAt(builder.length() - 1) == '\\'){ //handle multi-line statements
					builder.deleteCharAt(builder.length() - 1);
					String s = removeSpaces(src.readLine());
					isString = false;
					
					for(int i = 0; i < s.length(); i++){ //search for '#'
						if(s.charAt(i) == '"') isString = !isString;
						else if(!isString && s.charAt(i) == '#'){
							s = s.substring(0, i);
							break;
						}
					}
					
					builder.append(s);
					lineNum++;
				}
				line = builder.toString();
				
				int idxEq = -1;
				int count = 0;
				isString = false;
				
				for(int i = 0; i < line.length(); i++){ //search for '='
					if(!isString && (line.charAt(i) == '(' || line.charAt(i) == '[' || line.charAt(i) == '{')) count++;
					else if(!isString && (line.charAt(i) == ')' || line.charAt(i) == ']' || line.charAt(i) == '}')) count--;
					else if(line.charAt(i) == '"') isString = !isString;
					else if(!isString && count == 0 && line.charAt(i) == '='){
						idxEq = i;
						break;
					}
				}
				Symbol s = null;
				if(idxEq != -1)
					s = Operators.getOperatorEnd(line.substring(0, idxEq));
				
				//try to check for if/else and function def endings
				while((!indentsIf.isEmpty() && indent <= indentsIf.peek()) ||
						(!indentsFunc.isEmpty() && indent <= indentsFunc.peek())){
					if(!indentsFunc.isEmpty() && indent <= indentsFunc.peek() &&
							!indentsIf.isEmpty() && indent <= indentsIf.peek()){
						if(indentsIf.peek() < indentsFunc.peek()){
							int end;
							if(prog.peek().get(prog.peek().size() - 1) instanceof FunctionStatement)
								end = ((FunctionStatement)prog.peek().get(prog.peek().size() - 1)).end;
							else
								end = getEnd(prog.peek());
							prog.peek().add(new EndStatement());
							ArrayList<Statement> res = prog.pop();
							((FunctionStatement)prog.peek().get(prog.peek().size() - 1)).lines = res;
							((FunctionStatement)prog.peek().get(prog.peek().size() - 1)).end = end;
							indentsFunc.pop();
						}else{
							int end;
							if(prog.peek().get(prog.peek().size() - 1) instanceof FunctionStatement)
								end = ((FunctionStatement)prog.peek().get(prog.peek().size() - 1)).end;
							else
								end = getEnd(prog.peek());
							if(elifs.peek() instanceof IfStatement)
								((IfStatement)elifs.peek()).end = end;
							else
								((ElseStatement)elifs.peek()).end = end;
							prog.peek().add(new EndStatement());
							indentsIf.pop();
						}
					}else if(!indentsIf.isEmpty() && indent <= indentsIf.peek()){
						int end;
						if(prog.peek().get(prog.peek().size() - 1) instanceof FunctionStatement)
							end = ((FunctionStatement)prog.peek().get(prog.peek().size() - 1)).end;
						else
							end = getEnd(prog.peek());
						if(elifs.peek() instanceof IfStatement)
							((IfStatement)elifs.peek()).end = end;
						else
							((ElseStatement)elifs.peek()).end = end;
						prog.peek().add(new EndStatement());
						indentsIf.pop();
					}else{
						int end;
						if(prog.peek().get(prog.peek().size() - 1) instanceof FunctionStatement)
							end = ((FunctionStatement)prog.peek().get(prog.peek().size() - 1)).end;
						else
							end = getEnd(prog.peek());
						prog.peek().add(new EndStatement());
						ArrayList<Statement> res = prog.pop();
						((FunctionStatement)prog.peek().get(prog.peek().size() - 1)).lines = res;
						((FunctionStatement)prog.peek().get(prog.peek().size() - 1)).end = end;
						indentsFunc.pop();
					}
				}
				
				if(line.startsWith("return")){
					prog.peek().add(new ReturnStatement(line.substring(6, line.length()), lineNum));
				}else if(line.endsWith("?")){
					//if(expression)
					IfStatement ifStatement = new IfStatement(line.substring(0, line.length() - 1), lineNum, indent);
					prog.peek().add(ifStatement);
					indentsIf.push(indent);
					elifs.push(ifStatement);
				}else if(line.startsWith("else") && line.endsWith("?")){
					ElseStatement elseStatement = new ElseStatement(line.substring(4, line.length() - 1), lineNum, indent);
					prog.peek().add(elseStatement);
					indentsIf.push(indent);
					elifs.push(elseStatement);
				}else if(line.startsWith("else")){
					ElseStatement elseStatement = new ElseStatement(lineNum, indent);
					prog.peek().add(elseStatement);
					indentsIf.push(indent);
					elifs.push(elseStatement);
				}else if(idxEq != -1 && Operators.getOperatorStart(line.substring(idxEq)) == null
						&& Operators.getOperatorEnd(line.substring(0, idxEq + 1)) == null){
					//variable = expression
					String var = line.substring(0, idxEq);
					String exp = line.substring(idxEq + 1);
					
					if(exp.endsWith(")->")){
						prog.peek().add(
								new FunctionStatement(var, exp.substring(1, exp.length() - 3), lineNum));
						prog.push(new ArrayList<Statement>());
						indentsFunc.push(indent);
					}else if(exp.startsWith("class->")){
						prog.peek().add(new ClassStatement(var, exp.substring(7), lineNum));
					}else{
						if(s != null){
							if(s.beforeEq){
								var = var.substring(0, var.length() - s.op.length());
								exp = var + s.op + "(" + exp + ")";
							}else{
								throw new IllegalArgumentException("The operator, \"" + s.op + "\", cannot be placed before an equals sign!");
							}
						}
						prog.peek().add(new SetStatement(var, exp, lineNum));
					}
				}else{
					//just expression
					prog.peek().add(new Expression(line, lineNum));
				}
			}
		}catch(IOException e){
			handleException(e, "An error occured while reading the source file!");
		}
		
		while(!indentsIf.isEmpty() || !indentsFunc.isEmpty()){
			if(!indentsFunc.isEmpty() && !indentsIf.isEmpty()){
				if(indentsIf.peek() < indentsFunc.peek()){
					int end;
					if(prog.peek().get(prog.peek().size() - 1) instanceof FunctionStatement)
						end = ((FunctionStatement)prog.peek().get(prog.peek().size() - 1)).end;
					else
						end = getEnd(prog.peek());
					prog.peek().add(new EndStatement());
					ArrayList<Statement> res = prog.pop();
					((FunctionStatement)prog.peek().get(prog.peek().size() - 1)).lines = res;
					((FunctionStatement)prog.peek().get(prog.peek().size() - 1)).end = end;
					indentsFunc.pop();
				}else{
					int end;
					if(prog.peek().get(prog.peek().size() - 1) instanceof FunctionStatement)
						end = ((FunctionStatement)prog.peek().get(prog.peek().size() - 1)).end;
					else
						end = getEnd(prog.peek());
					if(elifs.peek() instanceof IfStatement)
						((IfStatement)elifs.peek()).end = end;
					else
						((ElseStatement)elifs.peek()).end = end;
					prog.peek().add(new EndStatement());
					indentsIf.pop();
				}
			}else if(!indentsIf.isEmpty()){
				int end;
				if(prog.peek().get(prog.peek().size() - 1) instanceof FunctionStatement)
					end = ((FunctionStatement)prog.peek().get(prog.peek().size() - 1)).end;
				else
					end = getEnd(prog.peek());
				if(elifs.peek() instanceof IfStatement)
					((IfStatement)elifs.peek()).end = end;
				else
					((ElseStatement)elifs.peek()).end = end;
				prog.peek().add(new EndStatement());
				indentsIf.pop();
			}else{
				int end;
				if(prog.peek().get(prog.peek().size() - 1) instanceof FunctionStatement)
					end = ((FunctionStatement)prog.peek().get(prog.peek().size() - 1)).end;
				else
					end = getEnd(prog.peek());
				prog.peek().add(new EndStatement());
				ArrayList<Statement> res = prog.pop();
				((FunctionStatement)prog.peek().get(prog.peek().size() - 1)).lines = res;
				((FunctionStatement)prog.peek().get(prog.peek().size() - 1)).end = end;
				indentsFunc.pop();
			}
		}
		prog.peek().add(new EndStatement());
		
		return prog.pop();
	}
	
	//there should already be an empty map in the stack before running this!
	//this function will pop that stack
	public static reObject run(ArrayList<Statement> curr, int start, int end){
		int initialSize = stack.size();
		int idx = 0;
		TreeMap<Integer, Boolean> indents = new TreeMap<>();
		ArrayDeque<Boolean> shouldRun = new ArrayDeque<>();
		shouldRun.push(true);
		
		while(stack.size() >= initialSize){
			Statement line = curr.get(idx);
			progLine = line.getLineNum();
			
			start = stack.peek().start;
			end = stack.peek().end;
			
			if(line instanceof IfStatement){
				if(shouldRun.peek() &&
						((reNumber)line.calc(start, end)).val.compareTo(BigDecimal.ZERO) != 0){
					stack.push(new StackItem(((IfStatement)line).start, ((IfStatement)line).end));
					shouldRun.push(true);
					indents.put(((IfStatement)line).indent, true);
				}else{
					shouldRun.push(false);
					indents.put(((IfStatement)line).indent, false);
				}
			}else if(line instanceof ElseStatement){
				if(shouldRun.peek() && !indents.get(((ElseStatement)line).indent) &&
						((reNumber)line.calc(start, end)).val.compareTo(BigDecimal.ZERO) != 0){
					stack.push(new StackItem(((ElseStatement)line).start, ((ElseStatement)line).end));
					shouldRun.push(true);
					indents.put(((ElseStatement)line).indent, true);
				}else{
					shouldRun.push(false);
				}
			}else if(shouldRun.peek() && line instanceof SetStatement){
				line.calc(start, end);
			}else if(shouldRun.peek() && line instanceof Expression){
				line.calc(start, end);
			}else if(shouldRun.peek() && line instanceof ReturnStatement){
				reObject res = line.calc(start, end);
				while(!shouldRun.isEmpty()){
					if(shouldRun.pop())
						stack.pop();
				}
				return res;
			}else if(shouldRun.peek() && line instanceof FunctionStatement){
				line.calc(start, end);
			}else if(shouldRun.peek() && line instanceof ClassStatement){
				line.calc(start, end);
			}else if(line instanceof EndStatement){
				if(shouldRun.peek())
					stack.pop();
				shouldRun.pop();
			}
			
			idx++;
		}
		
		return null;
	}
}
