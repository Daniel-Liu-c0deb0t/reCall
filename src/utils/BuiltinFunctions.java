package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import core.Persistent;

import static core.Persistent.*;

import objects.reCloseable;
import objects.reFileReader;
import objects.reFileWriter;
import objects.reFunction;
import objects.reList;
import objects.reMap;
import objects.reNumber;
import objects.reObject;
import objects.reString;
import objects.reWindow;
import parsing.Expression;

public class BuiltinFunctions{
	public static Function add = (params) -> {
		if(params[0] instanceof reNumber && params[1] instanceof reNumber){
			return new reNumber(((reNumber)params[0]).val.add(((reNumber)params[1]).val, defaultMath));
		}else if(params[0] instanceof reList && params[1] instanceof reList){
			ArrayList<reObject> res = new ArrayList<>(params[0].getListVal());
			res.addAll(params[1].getListVal());
			return new reList(res);
		}else if(params[0] instanceof reMap && params[1] instanceof reMap){
			HashMap<reObject, reObject> res = new HashMap<>(((reMap)params[0]).val);
			res.putAll(((reMap)params[1]).val);
			return new reMap(res);
		}else if(params[0] instanceof reString || params[1] instanceof reString){
			return new reString(params[0].toString() + params[1].toString());
		}
		
		throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
	};
	
	public static Function subtract = (params) -> {
		if(params[0] instanceof reNumber && params[1] instanceof reNumber){
			return new reNumber(((reNumber)params[0]).val.subtract(((reNumber)params[1]).val, defaultMath));
		}else if(params[0] instanceof reList && params[1] instanceof reList){
			ArrayList<reObject> res = new ArrayList<>(params[0].getListVal());
			res.removeAll(params[1].getListVal());
			return new reList(res);
		}else if(params[0] instanceof reMap && params[1] instanceof reMap){
			HashMap<reObject, reObject> res = new HashMap<>(((reMap)params[0]).val);
			for(reObject o : ((reMap)params[1]).val.keySet()){
				res.remove(o);
			}
			return new reMap(res);
		}
		
		throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
	};
	
	public static Function multiply = (params) -> {
		if(params[0] instanceof reNumber && params[1] instanceof reNumber){
			return new reNumber(((reNumber)params[0]).val.multiply(((reNumber)params[1]).val, defaultMath));
		}else if((params[0] instanceof reNumber && params[1] instanceof reList) ||
				params[1] instanceof reNumber && params[0] instanceof reList){
			if(params[1] instanceof reNumber && params[0] instanceof reList)
				Utils.swap(params, 0, 1);
			
			ArrayList<reObject> res = new ArrayList<>();
			
			for(int i = 0; i < ((reNumber)params[0]).val.intValueExact(); i++){
				ArrayList<reObject> val = params[1].getListVal();
				for(reObject o : val){
					res.add(o.deepClone());
				}
			}
			
			return new reList(res);
		}else if((params[0] instanceof reNumber && params[1] instanceof reString) ||
				params[1] instanceof reNumber && params[0] instanceof reString){
			if(params[1] instanceof reNumber && params[0] instanceof reString)
				Utils.swap(params, 0, 1);
			
			StringBuilder res = new StringBuilder();
			
			for(int i = 0; i < ((reNumber)params[0]).val.intValueExact(); i++){
				res.append(params[1].toString());
			}
			
			return new reString(res.toString());
		}else if(params[0] instanceof reList && params[1] instanceof reList){ //cartesian product
			ArrayList<reObject> a = params[0].getListVal();
			ArrayList<reObject> b = params[1].getListVal();
			ArrayList<reObject> res = new ArrayList<>();
			
			for(reObject o1 : a){
				for(reObject o2 : b){
					res.add(new reList(new ArrayList<reObject>(Arrays.asList(o1, o2))));
				}
			}
			
			return new reList(res);
		}
		
		throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
	};
	
	public static Function divide = (params) -> {
		if(params[0] instanceof reNumber && params[1] instanceof reNumber){
			return new reNumber(((reNumber)params[0]).val.divide(((reNumber)params[1]).val, defaultMath));
		}else if(params[0] instanceof reString && params[1] instanceof reString){
			String[] s = params[0].toString().split(((reString)params[1]).isRegex ?
					params[1].toString() : Pattern.quote(params[1].toString()));
			ArrayList<reObject> res = new ArrayList<>();
			
			for(int i = 0; i < s.length; i++){
				res.add(new reString(s[i]));
			}
			
			return new reList(res);
		}
		
		throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
	};
	
	public static Function floorDivide = (params) -> {
		if(params[0] instanceof reNumber && params[1] instanceof reNumber){
			return new reNumber(((reNumber)params[0]).val.divideToIntegralValue(((reNumber)params[1]).val, defaultMath));
		}
		
		throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
	};
	
	public static Function mod = (params) -> {
		if(params[0] instanceof reNumber && params[1] instanceof reNumber){
			return new reNumber(((reNumber)params[0]).val.remainder(((reNumber)params[1]).val, defaultMath));
		}else if(params[0] instanceof reString && params[1] instanceof reList){
			ArrayList<reObject> arr = params[1].getListVal();
			Object[] res = new Object[arr.size()];
			
			for(int i = 0; i < arr.size(); i++){
				reObject o = arr.get(i);
				if(o instanceof reNumber){
					res[i] = ((reNumber)o).val;
				}else{
					res[i] = o.toString();
				}
			}
			
			return new reString(String.format(params[0].toString(), res));
		}
		
		throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
	};
	
	public static Function pow = (params) -> {
		if(params[0] instanceof reNumber && params[1] instanceof reNumber){
			BigDecimal a = ((reNumber)params[0]).val;
			BigDecimal b = ((reNumber)params[1]).val;
			boolean negative = false;
			
			if(a.signum() < 0 && b.abs().compareTo(BigDecimal.ONE) < 0)
				throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
			
			if(b.compareTo(BigDecimal.ZERO) == 0)
				return new reNumber(BigDecimal.ONE);
			
			if(b.signum() < 0){
				b = b.negate();
				negative = true;
			}
			
			//use a^(x + y) = a^x * a^y
			BigDecimal remB = b.remainder(BigDecimal.ONE, defaultMath); //get the decimal part
			BigDecimal intB = b.subtract(remB, defaultMath);
			BigDecimal res = BigDecimal.ONE;
			
			res = Utils.intPow(a, intB.toBigIntegerExact());
			if(remB.compareTo(BigDecimal.ZERO) != 0)
				res = res.multiply(BigDecimal.valueOf(Math.pow(a.doubleValue(), remB.doubleValue())), defaultMath);
			
			if(negative)
				res = BigDecimal.ONE.divide(res, defaultMath);
			
			return new reNumber(res);
		}
		
		throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
	};
	
	public static Function not = (params) -> {
		return new reNumber(params[1].toBool() == 0 ? BigDecimal.ONE : BigDecimal.ZERO);
	};
	
	public static Function cmpEq = (params) -> {
		if(params[0].equals(params[1])){
			return new reNumber(BigDecimal.ONE);
		}else{
			return new reNumber(BigDecimal.ZERO);
		}
	};
	
	public static Function cmpNotEq = (params) -> {
		if(!params[0].equals(params[1])){
			return new reNumber(BigDecimal.ONE);
		}else{
			return new reNumber(BigDecimal.ZERO);
		}
	};
	
	public static Function cmpGreater = (params) -> {
		if(Utils.compare(params[0], params[1]) > 0){
			return new reNumber(BigDecimal.ONE);
		}else{
			return new reNumber(BigDecimal.ZERO);
		}
	};
	
	public static Function cmpLess = (params) -> {
		if(Utils.compare(params[0], params[1]) < 0){
			return new reNumber(BigDecimal.ONE);
		}else{
			return new reNumber(BigDecimal.ZERO);
		}
	};
	
	public static Function cmpGreaterEq = (params) -> {
		if(Utils.compare(params[0], params[1]) >= 0){
			return new reNumber(BigDecimal.ONE);
		}else{
			return new reNumber(BigDecimal.ZERO);
		}
	};
	
	public static Function cmpLessEq = (params) -> {
		if(Utils.compare(params[0], params[1]) <= 0){
			return new reNumber(BigDecimal.ONE);
		}else{
			return new reNumber(BigDecimal.ZERO);
		}
	};
	
	public static Function instanceOf = (params) -> {
		if(params[0] instanceof reList && params[1] instanceof reList){
			ArrayList<reObject> a = params[0].getListVal();
			ArrayList<reObject> b = params[1].getListVal();
			boolean res = true;
			for(int i = 0; i < a.size(); i++){
				if(!a.get(i).getType().equals(b.get(i).getType())){
					res = false;
					break;
				}
			}
			return new reNumber(res ? BigDecimal.ONE : BigDecimal.ZERO);
		}else{
			if(params[0].getType().equals(params[1].getType())){
				return new reNumber(BigDecimal.ONE);
			}else{
				return new reNumber(BigDecimal.ZERO);
			}
		}
	};
	
	public static Function notInstanceOf = (params) -> {
		if(params[0] instanceof reList && params[1] instanceof reList){
			ArrayList<reObject> a = params[0].getListVal();
			ArrayList<reObject> b = params[1].getListVal();
			boolean res = true;
			for(int i = 0; i < a.size(); i++){
				if(!a.get(i).getType().equals(b.get(i).getType())){
					res = false;
					break;
				}
			}
			return new reNumber(res ? BigDecimal.ZERO : BigDecimal.ONE);
		}else{
			if(params[0].getType().equals(params[1].getType())){
				return new reNumber(BigDecimal.ZERO);
			}else{
				return new reNumber(BigDecimal.ONE);
			}
		}
	};
	
	public static Function consecutiveList = (params) -> {
		BigDecimal a = ((reNumber)params[0]).val;
		BigDecimal b = ((reNumber)params[1]).val;
		BigDecimal diff = new BigDecimal(b.subtract(a, defaultMath).signum());
		ArrayList<reObject> res = new ArrayList<>();
		while(diff.signum() < 0 ? a.compareTo(b) > 0 : a.compareTo(b) < 0){
			res.add(new reNumber(a));
			a = a.add(diff, defaultMath);
		}
		return new reList(res);
	};
	
	public static Function sum = (params) -> {
		BigDecimal res = new BigDecimal(0);
		for(reObject o : params){
			if(o instanceof reNumber){
				res = res.add(((reNumber)o).val, defaultMath);
			}else if(o instanceof reList){
				ArrayList<reObject> list = o.getListVal();
				res = res.add(((reNumber)BuiltinFunctions.sum.apply(list.toArray(new reObject[list.size()]))).val, defaultMath);
			}else{
				throw new IllegalArgumentException("Bad argument: \"" + o.toString() + "\"");
			}
		}
		return new reNumber(res);
	};
	
	public static Function max = (params) -> {
		BigDecimal res = null;
		for(reObject o : params){
			if(o instanceof reNumber){
				if(res == null)
					res = ((reNumber)o).val;
				else
					res = res.max(((reNumber)o).val);
			}else if(o instanceof reList){
				ArrayList<reObject> list = o.getListVal();
				BigDecimal temp = ((reNumber)BuiltinFunctions.max.apply(list.toArray(new reObject[list.size()]))).val;
				if(res == null)
					res = temp;
				else
					res = res.max(temp);
			}else{
				throw new IllegalArgumentException("Bad argument: \"" + o.toString() + "\"");
			}
		}
		return new reNumber(res);
	};
	
	public static Function argMax = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reList){
			ArrayList<reObject> arr = params[0].getListVal();
			BigDecimal res = null;
			int idx = -1;
			for(int i = 0; i < arr.size(); i++){
				if(res == null || ((reNumber)arr.get(i)).val.compareTo(res) > 0){
					res = ((reNumber)arr.get(i)).val;
					idx = i;
				}
			}
			return new reNumber(new BigDecimal(idx));
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function min = (params) -> {
		BigDecimal res = null;
		for(reObject o : params){
			if(o instanceof reNumber){
				if(res == null)
					res = ((reNumber)o).val;
				else
					res = res.min(((reNumber)o).val);
			}else if(o instanceof reList){
				ArrayList<reObject> list = o.getListVal();
				BigDecimal temp = ((reNumber)BuiltinFunctions.min.apply(list.toArray(new reObject[list.size()]))).val;
				if(res == null)
					res = temp;
				else
					res = res.min(temp);
			}else{
				throw new IllegalArgumentException("Bad argument: \"" + o.toString() + "\"");
			}
		}
		return new reNumber(res);
	};
	
	public static Function argMin = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reList){
			ArrayList<reObject> arr = params[0].getListVal();
			BigDecimal res = null;
			int idx = -1;
			for(int i = 0; i < arr.size(); i++){
				if(res == null || ((reNumber)arr.get(i)).val.compareTo(res) < 0){
					res = ((reNumber)arr.get(i)).val;
					idx = i;
				}
			}
			return new reNumber(new BigDecimal(idx));
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function abs = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reNumber){
			return new reNumber(((reNumber)params[0]).val.abs());
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function log = (params) -> { //log a base b for b, a
		if(params.length != 2)
			throw new IllegalArgumentException("Only 2 parameter(s) allowed!");
		
		if(params[0] instanceof reNumber && params[1] instanceof reNumber){
			BigDecimal a = ((reNumber)params[1]).val;
			BigDecimal b = ((reNumber)params[0]).val;
			
			//use log(x * y) = log(x) + log(y)
			int expA = a.precision() - a.scale() - 1;
			double valA = a.movePointLeft(expA).doubleValue(); //should fit within a double
			BigDecimal log10A = BigDecimal.valueOf(Math.log10(valA) + expA);
			int expB = b.precision() - b.scale() - 1;
			double valB = b.movePointLeft(expB).doubleValue(); //should fit within a double
			BigDecimal log10B = BigDecimal.valueOf(Math.log10(valB) + expB);
			
			return new reNumber(log10A.divide(log10B, defaultMath));
		}else{
			throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
		}
	};
	
	private static BigDecimal two = new BigDecimal(2);
	
	public static Function sin = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reNumber){
			return new reNumber(BigDecimal.valueOf(Math.sin(
					((reNumber)params[0]).val.remainder(constPI.multiply(two, defaultMath)).doubleValue())));
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function cos = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reNumber){
			return new reNumber(BigDecimal.valueOf(Math.cos(
					((reNumber)params[0]).val.remainder(constPI.multiply(two, defaultMath)).doubleValue())));
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function tan = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reNumber){
			return new reNumber(BigDecimal.valueOf(Math.tan(
					((reNumber)params[0]).val.remainder(constPI.multiply(two, defaultMath)).doubleValue())));
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function asin = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reNumber){
			return new reNumber(BigDecimal.valueOf(Math.asin(((reNumber)params[0]).val.doubleValue())));
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function acos = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reNumber){
			return new reNumber(BigDecimal.valueOf(Math.acos(((reNumber)params[0]).val.doubleValue())));
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function atan = (params) -> {
		if(params.length == 1){
			if(params[0] instanceof reNumber){
				return new reNumber(BigDecimal.valueOf(Math.atan(((reNumber)params[0]).val.doubleValue())));
			}else{
				throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
			}
		}else if(params.length == 2){
			if(params[0] instanceof reNumber && params[1] instanceof reNumber){
				return new reNumber(BigDecimal.valueOf(Math.atan2(((reNumber)params[0]).val.doubleValue(),
						((reNumber)params[1]).val.doubleValue())));
			}else{
				throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
			}
		}else{
			throw new IllegalArgumentException("Only 1 or 2 parameter(s) allowed!");
		}
	};
	
	public static Function println = (params) -> {
		if(params.length >= 1 && params[0] instanceof reFileWriter){
			try{
				((reFileWriter)params[0]).println(Utils.join(params, " ", 1, false));
			}catch(IOException e){
				Utils.handleException(e, "An error occured while writing to file!");
			}
		}else{
			if(params.length == 3 && params[0] instanceof reString &&
					params[1] instanceof reString && params[2] instanceof reWindow){
				Path p = Paths.get(params[0].toString());
				if(!p.isAbsolute())
					p = Persistent.workingDir.resolve(p);
				try{
					ImageIO.write(((reWindow)params[2]).p.img, params[1].toString(), p.toFile());
				}catch(IOException e){
					e.printStackTrace();
				}
			}else{
				System.out.println(Utils.join(params, " ", 0, false));
			}
		}
		return null;
	};
	
	public static Function createFileReader = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reString){
			try{
				return new reFileReader(params[0].toString());
			}catch(IOException e){
				Utils.handleException(e, "An error occured while creating file reader!");
			}
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
		
		return null;
	};
	
	public static Function createFileWriter = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reString){
			try{
				return new reFileWriter(params[0].toString());
			}catch(IOException e){
				Utils.handleException(e, "An error occured while creating file writer!");
			}
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
		
		return null;
	};
	
	public static Function closeFileIO = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reCloseable){
			try{
				((reCloseable)params[0]).close();
			}catch(IOException e){
				Utils.handleException(e, "An error occured while closing the file reader or writer!");
			}
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
		
		return null;
	};
	
	public static Function flushFileIO = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reFileWriter){
			try{
				((reFileWriter)params[0]).flush();
			}catch(IOException e){
				Utils.handleException(e, "An error occured while flushing the file writer!");
			}
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
		
		return null;
	};
	
	public static Function deleteFile = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reString){
			Path p = Paths.get(params[0].toString());
			if(!p.isAbsolute())
				p = Persistent.workingDir.resolve(p);
			try{
				Files.deleteIfExists(p);
			}catch(IOException e){
				Utils.handleException(e, "An error occured while deleting a file!");
			}
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
		
		return null;
	};
	
	public static Function readLine = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reString){
			System.out.print(params[0].toString());
			try{
				return new reString(in.readLine());
			}catch(IOException e){
				Utils.handleException(e, "An error occured while reading user input!");
			}
		}else if(params[0] instanceof reFileReader){
			try{
				return new reString(((reFileReader)params[0]).readline());
			}catch(IOException e){
				Utils.handleException(e, "An error occured while reading from file!");
			}
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
		
		return null;
	};
	
	public static Function hasNextLine = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reFileReader){
			try{
				return new reNumber(((reFileReader)params[0]).hasNext() ? BigDecimal.ONE : BigDecimal.ZERO);
			}catch(IOException e){
				Utils.handleException(e, "An error occured while reading from file!");
			}
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
		
		return null;
	};
	
	public static Function toNumber = (params) -> {
		if(params.length == 1){
			if(params[0] instanceof reString){
				return new reNumber(((reString)params[0]).val);
			}else if(params[0] instanceof reNumber){
				return new reNumber(((reNumber)params[0]).val);
			}else{
				return new reNumber(new BigDecimal(params[0].toBool()));
			}
		}else if(params.length == 2){
			if(params[0] instanceof reString && params[1] instanceof reNumber){
				return new reNumber(new BigDecimal(
						new BigInteger(((reString)params[0]).val, ((reNumber)params[1]).val.intValueExact()), defaultMath));
			}else{
				throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
			}
		}else{
			throw new IllegalArgumentException("Only 1 or 2 parameter(s) allowed!");
		}
	};
	
	public static Function toString = (params) -> {
		if(params.length == 1){
			return new reString(params[0].toString());
		}else if(params.length == 2){
			if(params[0] instanceof reNumber && params[1] instanceof reNumber){
				return new reString(
						((reNumber)params[0]).val.toBigIntegerExact().toString(((reNumber)params[1]).val.intValueExact()));
			}else{
				throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
			}
		}else{
			throw new IllegalArgumentException("Only 1 or 2 parameter(s) allowed!");
		}
	};
	
	public static Function count = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reList){
			HashMap<reObject, Integer> map = new HashMap<>();
			for(reObject o : params[0].getListVal()){
				map.put(o, map.getOrDefault(o, 0) + 1);
			}
			
			HashMap<reObject, reObject> res = new HashMap<>();
			for(reObject o : map.keySet()){
				res.put(o, new reNumber(new BigDecimal(map.get(o))));
			}
			
			return new reMap(res);
		}else if(params[0] instanceof reString){
			HashMap<Character, Integer> map = new HashMap<>();
			for(char c : params[0].toString().toCharArray()){
				map.put(c, map.getOrDefault(c, 0) + 1);
			}
			
			HashMap<reObject, reObject> res = new HashMap<>();
			for(char c : map.keySet()){
				res.put(new reString(c + ""), new reNumber(new BigDecimal(map.get(c))));
			}
			
			return new reMap(res);
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function charToNum = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reString && ((reString)params[0]).val.length() == 1){
			return new reNumber(new BigDecimal((int)((reString)params[0]).val.charAt(0)));
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function numToChar = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reNumber){
			return new reString(((char)((reNumber)params[0]).val.intValueExact()) + "");
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function toUpperCase = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reString){
			return new reString(((reString)params[0]).val.toUpperCase());
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function toLowerCase = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reString){
			return new reString(((reString)params[0]).val.toLowerCase());
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function keyList = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reMap){
			return new reList(new ArrayList<reObject>(((reMap)params[0]).val.keySet()));
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function valList = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reMap){
			return new reList(new ArrayList<reObject>(((reMap)params[0]).val.values()));
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function listToMap = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reList){
			HashMap<reObject, reObject> res = new HashMap<>();
			ArrayList<reObject> arr = params[0].getListVal();
			
			for(reObject o : arr){
				ArrayList<reObject> l = ((reList)o).getListVal();
				res.put(l.get(0), l.get(1));
			}
			
			return new reMap(res);
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function randomInt = (params) -> {
		if(params.length != 2)
			throw new IllegalArgumentException("Only 2 parameter(s) allowed!");
		
		if(params[0] instanceof reNumber && params[1] instanceof reNumber){
			BigDecimal min = ((reNumber)params[0]).val.min(((reNumber)params[1]).val);
			BigDecimal max = ((reNumber)params[0]).val.max(((reNumber)params[1]).val);
			BigDecimal rand = min.add(max.subtract(min, defaultMath).multiply(
					BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble()), defaultMath), defaultMath);
			
			return new reNumber(rand.setScale(0, RoundingMode.FLOOR));
		}else{
			throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
		}
	};
	
	public static Function randomFloat = (params) -> {
		if(params.length != 2)
			throw new IllegalArgumentException("Only 2 parameter(s) allowed!");
		
		if(params[0] instanceof reNumber && params[1] instanceof reNumber){
			BigDecimal min = ((reNumber)params[0]).val.min(((reNumber)params[1]).val);
			BigDecimal max = ((reNumber)params[0]).val.max(((reNumber)params[1]).val);
			BigDecimal rand = min.add(max.subtract(min, defaultMath).multiply(
					BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble()), defaultMath), defaultMath);
			
			return new reNumber(rand);
		}else{
			throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
		}
	};
	
	public static Function shuffleList = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reList){
			ArrayList<reObject> res = new ArrayList<>(params[0].getListVal());
			Collections.shuffle(res, ThreadLocalRandom.current());
			return new reList(res);
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function sortList = (params) -> {
		if(params.length == 1){
			if(params[0] instanceof reList){
				ArrayList<reObject> res = new ArrayList<>(params[0].getListVal());
				Collections.sort(res, (a, b) -> Utils.compare(a, b));
				return new reList(res);
			}else{
				throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
			}
		}else if(params.length == 2){
			if(params[0] instanceof reList && params[1] instanceof reFunction){
				ArrayList<reObject> res = new ArrayList<>(params[0].getListVal());
				Collections.sort(res, (a, b) -> ((reNumber)((reFunction)params[1]).apply(null, new reObject[]{a, b})).val.signum());
				return new reList(res);
			}else{
				throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
			}
		}else{
			throw new IllegalArgumentException("Only 1 or 2 parameter(s) allowed!");
		}
	};
	
	public static Function nextPermutation = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reList){
			ArrayList<reObject> res = new ArrayList<>(params[0].getListVal());
			
			int first = -1;
			for(int i = res.size() - 2; i >= 0; i--){
				if(Utils.compare(res.get(i), res.get(i + 1)) < 0){
					first = i;
					break;
				}
			}
			
			if(first == -1)
				return new reList(new ArrayList<reObject>());
			
			int swap = res.size() - 1;
			while(Utils.compare(res.get(first), res.get(swap)) >= 0){
				swap--;
			}
			Utils.swap(res, first++, swap);
			swap = res.size() - 1;
			
			while(first < swap){
				Utils.swap(res, first++, swap--);
			}
			
			return new reList(res);
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function maskList = (params) -> {
		if(params.length != 2)
			throw new IllegalArgumentException("Only 2 parameter(s) allowed!");
		
		if(params[0] instanceof reList && params[1] instanceof reNumber){
			ArrayList<reObject> res = new ArrayList<>();
			ArrayList<reObject> list = params[0].getListVal();
			BigInteger mask = ((reNumber)params[1]).val.toBigIntegerExact();
			
			for(int i = 0; i < list.size(); i++){
				if(mask.testBit(i)){
					res.add(list.get(i));
				}
			}
			
			return new reList(res);
		}else{
			throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
		}
	};
	
	public static Function popList = (params) -> {
		if(params.length == 1){
			if(params[0] instanceof reList){
				ArrayList<reObject> res = new ArrayList<>(params[0].getListVal());
				res.remove(res.size() - 1);
				return new reList(res);
			}else{
				throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
			}
		}else if(params.length == 2){
			if(params[0] instanceof reList && params[1] instanceof reNumber){
				ArrayList<reObject> res = new ArrayList<>(params[0].getListVal());
				int idx = ((reNumber)params[1]).val.intValueExact();
				res.remove(idx < 0 ? res.size() + idx : idx);
				return new reList(res);
			}else{
				throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
			}
		}else{
			throw new IllegalArgumentException("Only 1 or 2 parameter(s) allowed!");
		}
	};
	
	public static Function contains = (params) -> {
		if(params.length != 2)
			throw new IllegalArgumentException("Only 2 parameter(s) allowed!");
		
		if(params[0] instanceof reList){
			return new reNumber(params[0].getListVal().contains(params[1]) ? BigDecimal.ONE : BigDecimal.ZERO);
		}else if(params[0] instanceof reMap){
			return new reNumber(((reMap)params[0]).val.containsKey(params[1]) ? BigDecimal.ONE : BigDecimal.ZERO);
		}else if(params[0] instanceof reString && params[1] instanceof reString){
			return new reNumber(params[0].toString().contains(params[1].toString()) ? BigDecimal.ONE : BigDecimal.ZERO);
		}else{
			throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
		}
	};
	
	public static Function indexOf = (params) -> {
		if(params.length != 2)
			throw new IllegalArgumentException("Only 2 parameter(s) allowed!");
		
		if(params[0] instanceof reList){
			return new reNumber(new BigDecimal(params[0].getListVal().indexOf(params[1])));
		}else if(params[0] instanceof reString && params[1] instanceof reString){
			return new reNumber(new BigDecimal(params[0].toString().indexOf(params[1].toString())));
		}else{
			throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
		}
	};
	
	public static Function length = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reString){
			return new reNumber(new BigDecimal(params[0].toString().length()));
		}else if(params[0] instanceof reList){
			return new reNumber(new BigDecimal(params[0].getListVal().size()));
		}else if(params[0] instanceof reMap){
			return new reNumber(new BigDecimal(((reMap)params[0]).val.size()));
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function map = (params) -> {
		if(params.length != 2)
			throw new IllegalArgumentException("Only 2 parameter(s) allowed!");
		
		if(params[0] instanceof reList && params[1] instanceof reFunction){
			ArrayList<reObject> res = new ArrayList<>();
			ArrayList<reObject> val = params[0].getListVal();
			for(int i = 0; i < val.size(); i++){
				reObject o = ((reFunction)params[1]).apply(null, new reObject[]{new reNumber(new BigDecimal(i)), val.get(i)});
				if(o != null)
					res.add(o);
			}
			return new reList(res);
		}else{
			throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
		}
	};
	
	public static Function deepMap = (params) -> {
		if(params.length != 2)
			throw new IllegalArgumentException("Only 2 parameter(s) allowed!");
		
		if(params[0] instanceof reList && params[1] instanceof reFunction){
			ArrayList<reObject> res = new ArrayList<>();
			ArrayList<reObject> val = params[0].getListVal();
			for(reObject o : val){
				reObject a = null;
				if(o instanceof reList)
					a = BuiltinFunctions.deepMap.apply(new reObject[]{o, params[1]});
				else
					a = ((reFunction)params[1]).apply(null, new reObject[]{o});
				if(a != null)
					res.add(a);
			}
			return new reList(res);
		}else{
			throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
		}
	};
	
	public static Function filter = (params) -> {
		if(params.length != 2)
			throw new IllegalArgumentException("Only 2 parameter(s) allowed!");
		
		if(params[0] instanceof reList && params[1] instanceof reFunction){
			ArrayList<reObject> res = new ArrayList<>();
			ArrayList<reObject> val = params[0].getListVal();
			for(int i = 0; i < val.size(); i++){
				if(((reFunction)params[1]).apply(null,
						new reObject[]{new reNumber(new BigDecimal(i)), val.get(i)}).toBool() != 0)
					res.add(val.get(i));
			}
			return new reList(res);
		}else{
			throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
		}
	};
	
	public static Function reduce = (params) -> {
		if(params.length == 2){
			if(params[0] instanceof reList && params[1] instanceof reFunction){
				reObject res = null;
				ArrayList<reObject> val = params[0].getListVal();
				for(reObject o : val){
					if(res == null)
						res = o;
					else
						res = ((reFunction)params[1]).apply(null, new reObject[]{res, o});
				}
				return res;
			}else{
				throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
			}
		}else if(params.length == 3){
			if(params[1] instanceof reList && params[2] instanceof reFunction){
				reObject res = params[0];
				ArrayList<reObject> val = params[1].getListVal();
				for(reObject o : val){
					res = ((reFunction)params[2]).apply(null, new reObject[]{res, o});
				}
				return res;
			}else{
				throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
			}
		}else{
			throw new IllegalArgumentException("Only 2 or 3 parameter(s) allowed!");
		}
	};
	
	public static Function deepReduce = (params) -> {
		if(params.length == 2){
			if(params[0] instanceof reList && params[1] instanceof reFunction){
				reObject res = null;
				ArrayList<reObject> val = params[0].getListVal();
				for(reObject o : val){
					reObject c = null;
					if(o instanceof reList)
						c = BuiltinFunctions.deepReduce.apply(new reObject[]{o, params[1]});
					else
						c = o;
					if(res == null)
						res = c;
					else
						res = ((reFunction)params[1]).apply(null, new reObject[]{res, c});
				}
				return res;
			}else{
				throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
			}
		}else if(params.length == 3){
			if(params[1] instanceof reList && params[2] instanceof reFunction){
				reObject res = params[0];
				ArrayList<reObject> val = params[1].getListVal();
				for(reObject o : val){
					if(o instanceof reList)
						res = BuiltinFunctions.deepReduce.apply(new reObject[]{res, o, params[2]});
					else
						res = ((reFunction)params[2]).apply(null, new reObject[]{res, o});
				}
				return res;
			}else{
				throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
			}
		}else{
			throw new IllegalArgumentException("Only 2 or 3 parameter(s) allowed!");
		}
	};
	
	public static Function flatten = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reList){
			ArrayList<reObject> res = new ArrayList<>();
			ArrayList<reObject> val = params[0].getListVal();
			for(reObject o : val){
				if(o instanceof reList)
					res.addAll(((reList)BuiltinFunctions.flatten.apply(new reObject[]{o})).getListVal());
				else
					res.add(o);
			}
			return new reList(res);
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function generate = (params) -> {
		if(params.length == 2){
			if(params[0] instanceof reNumber && params[1] instanceof reFunction){
				reObject curr = null;
				int n = ((reNumber)params[0]).val.intValueExact(); //should fit!
				for(int i = 0; i < n; i++){
					curr = ((reFunction)params[1]).apply(null,
							new reObject[]{new reNumber(new BigDecimal(i))});
				}
				return curr;
			}else if(params[0] instanceof reFunction && params[1] instanceof reFunction){
				reObject curr = null;
				int i = 0;
				while(((reFunction)params[0]).apply(null,
						new reObject[]{new reNumber(new BigDecimal(i))}).toBool() != 0){
					curr = ((reFunction)params[1]).apply(null,
							new reObject[]{new reNumber(new BigDecimal(i))});
					i++;
				}
				return curr;
			}else{
				throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
			}
		}else if(params.length == 3){
			if(params[1] instanceof reNumber && params[2] instanceof reFunction){
				reObject curr = params[0];
				int n = ((reNumber)params[1]).val.intValueExact(); //should fit!
				for(int i = 1; i < n; i++){
					curr = ((reFunction)params[2]).apply(null,
							new reObject[]{new reNumber(new BigDecimal(i)), curr});
				}
				return curr;
			}else if(params[1] instanceof reFunction && params[2] instanceof reFunction){
				reObject curr = params[0];
				int i = 1;
				while(((reFunction)params[1]).apply(null,
						new reObject[]{new reNumber(new BigDecimal(i)), curr}).toBool() != 0){
					curr = ((reFunction)params[2]).apply(null,
							new reObject[]{new reNumber(new BigDecimal(i)), curr});
					i++;
				}
				return curr;
			}else{
				throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
			}
		}else{
			throw new IllegalArgumentException("Only 2 or 3 parameter(s) allowed!");
		}
	};
	
	public static Function generateList = (params) -> {
		if(params.length == 2){
			if(params[0] instanceof reNumber && params[1] instanceof reFunction){
				ArrayList<reObject> res = new ArrayList<>();
				int n = ((reNumber)params[0]).val.intValueExact(); //should fit!
				for(int i = 0; i < n; i++){
					res.add(((reFunction)params[1]).apply(null,
							new reObject[]{new reNumber(new BigDecimal(i))}));
				}
				return new reList(res);
			}else if(params[0] instanceof reFunction && params[1] instanceof reFunction){
				ArrayList<reObject> res = new ArrayList<>();
				int i = 0;
				while(((reFunction)params[0]).apply(null,
						new reObject[]{new reNumber(new BigDecimal(i))}).toBool() != 0){
					res.add(((reFunction)params[1]).apply(null,
							new reObject[]{new reNumber(new BigDecimal(i))}));
					i++;
				}
				return new reList(res);
			}else{
				throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
			}
		}else if(params.length == 3){
			if(params[1] instanceof reNumber && params[2] instanceof reFunction){
				ArrayList<reObject> res = new ArrayList<>();
				res.add(params[0]);
				int n = ((reNumber)params[1]).val.intValueExact(); //should fit!
				for(int i = 1; i < n; i++){
					res.add(((reFunction)params[2]).apply(null,
							new reObject[]{new reNumber(new BigDecimal(i)), res.get(res.size() - 1)}));
				}
				return new reList(res);
			}else if(params[1] instanceof reFunction && params[2] instanceof reFunction){
				ArrayList<reObject> res = new ArrayList<>();
				res.add(params[0]);
				int i = 1;
				while(((reFunction)params[1]).apply(null,
						new reObject[]{new reNumber(new BigDecimal(i)), res.get(res.size() - 1)}).toBool() != 0){
					res.add(((reFunction)params[2]).apply(null,
							new reObject[]{new reNumber(new BigDecimal(i)), res.get(res.size() - 1)}));
					i++;
				}
				return new reList(res);
			}else{
				throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
			}
		}else{
			throw new IllegalArgumentException("Only 2 or 3 parameter(s) allowed!");
		}
	};
	
	public static Function round = (params) -> {
		if(params.length != 2)
			throw new IllegalArgumentException("Only 2 parameter(s) allowed!");
		
		if(params[0] instanceof reNumber && params[1] instanceof reNumber){
			return new reNumber(((reNumber)params[0]).val.setScale(((reNumber)params[1]).val.intValueExact(), defaultRounding));
		}else{
			throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
		}
	};
	
	public static Function zip = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reList){
			ArrayList<reObject> res = new ArrayList<>();
			ArrayList<reObject> val = params[0].getListVal();
			int max = 0;
			
			for(reObject o : val){
				if(o instanceof reList){
					max = Math.max(max, ((reList)o).getListVal().size());
				}else{
					throw new IllegalArgumentException("Bad argument!");
				}
			}
			
			for(int i = 0; i < max; i++){
				res.add(new reList(new ArrayList<reObject>()));
			}
			
			for(reObject o : val){
				ArrayList<reObject> arr = ((reList)o).getListVal();
				for(int i = 0; i < arr.size(); i++){
					((reList)res.get(i)).val.add(arr.get(i));
				}
			}
			
			return new reList(res);
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function createWindow = (params) -> {
		if(params.length != 5)
			throw new IllegalArgumentException("Only 4 parameter(s) allowed!");
		
		if(params[0] instanceof reString && params[1] instanceof reNumber &&
				params[2] instanceof reNumber && params[3] instanceof reNumber && params[4] instanceof reFunction){
			return new reWindow(params[0].toString(), ((reNumber)params[1]).val.intValueExact(),
					((reNumber)params[2]).val.intValueExact(), ((reNumber)params[3]).val.intValueExact(), (reFunction)params[4]);
		}else{
			throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
		}
	};
	
	public static Function refreshWindow = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reWindow){
			((reWindow)params[0]).refresh();
			return null;
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function eval = (params) -> {
		if(params.length != 2) //eval has one extra parameter
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[1] instanceof reString){ //the first argument is only used to pass along the line number
			int line = ((reNumber)params[0]).val.intValueExact();
			return Expression.recursiveCalc(Utils.removeSpaces(params[1].toString()), null, line, line, line);
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + Utils.join(params, ", ", 1, true) + "\"");
		}
	};
	
	public static Function regex = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reString){
			reString res = (reString)((reString)params[0]).deepClone();
			res.isRegex = true;
			return res;
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function replace = (params) -> {
		if(params.length != 3)
			throw new IllegalArgumentException("Only 3 parameter(s) allowed!");
		
		if(params[0] instanceof reString && params[1] instanceof reString && params[2] instanceof reString){
			reString before = (reString)params[1];
			if(before.isRegex){
				return new reString(params[0].toString().replaceAll(before.toString(), params[2].toString()));
			}else{
				return new reString(params[0].toString().replace(before.toString(), params[2].toString()));
			}
		}else{
			throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
		}
	};
	
	public static Function matchGroups = (params) -> {
		if(params.length != 2)
			throw new IllegalArgumentException("Only 2 parameter(s) allowed!");
		
		if(params[0] instanceof reString && params[1] instanceof reString){
			reString s1 = (reString)params[0];
			reString s2 = (reString)params[1];
			
			if((s1.isRegex || s2.isRegex) && (!s1.isRegex || !s2.isRegex)){ //only one string is regex
				String regex = s1.isRegex ? s1.val : s2.val;
				String text = s1.isRegex ? s2.val : s1.val;
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(text);
				
				if(m.matches()){
					ArrayList<reObject> res = new ArrayList<>();
					for(int i = 0; i < m.groupCount(); i++){
						ArrayList<reObject> arr = new ArrayList<>();
						arr.add(new reString(m.group(i + 1)));
						arr.add(new reNumber(new BigDecimal(m.start(i + 1))));
						arr.add(new reNumber(new BigDecimal(m.end(i + 1))));
						res.add(new reList(arr));
					}
					return new reList(res);
				}else{
					return new reList(new ArrayList<reObject>());
				}
			}else{
				throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
			}
		}else{
			throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
		}
	};
	
	public static Function find = (params) -> {
		if(params.length != 2)
			throw new IllegalArgumentException("Only 2 parameter(s) allowed!");
		
		if(params[0] instanceof reString && params[1] instanceof reString){
			reString s1 = (reString)params[0];
			reString s2 = (reString)params[1];
			
			if((s1.isRegex || s2.isRegex) && (!s1.isRegex || !s2.isRegex)){ //only one string is regex
				String regex = s1.isRegex ? s1.val : s2.val;
				String text = s1.isRegex ? s2.val : s1.val;
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(text);
				ArrayList<reObject> res = new ArrayList<>();
				
				while(m.find()){
					ArrayList<reObject> arr = new ArrayList<>();
					arr.add(new reString(m.group()));
					arr.add(new reNumber(new BigDecimal(m.start())));
					arr.add(new reNumber(new BigDecimal(m.end())));
					res.add(new reList(arr));
				}
				
				return new reList(res);
			}else{
				throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
			}
		}else{
			throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
		}
	};
	
	public static Function reverse = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reList){
			ArrayList<reObject> arr = params[0].getListVal();
			ArrayList<reObject> res = new ArrayList<>();
			
			for(int i = arr.size() - 1; i >= 0; i--){
				res.add(arr.get(i));
			}
			
			return new reList(res);
		}else if(params[0] instanceof reString){
			String s = params[0].toString();
			char[] res = new char[s.length()];
			
			for(int i = 0; i < s.length(); i++){
				res[i] = s.charAt(s.length() - 1 - i);
			}
			
			return new reString(new String(res));
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function set = (params) -> {
		if(params.length != 3)
			throw new IllegalArgumentException("Only 3 parameter(s) allowed!");
		
		if(params[0] instanceof reList && params[1] instanceof reNumber){
			ArrayList<reObject> res = new ArrayList<>(params[0].getListVal());
			int idx = ((reNumber)params[1]).val.intValueExact();
			res.set(idx < 0 ? res.size() + idx : idx, params[2]);
			return new reList(res);
		}else if(params[0] instanceof reMap){
			HashMap<reObject, reObject> res = new HashMap<>(((reMap)params[0]).val);
			res.put(params[1], params[2]);
			return new reMap(res);
		}else{
			throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
		}
	};
	
	public static Function pushList = (params) -> {
		if(params.length == 2){
			if(params[0] instanceof reList){
				ArrayList<reObject> res = new ArrayList<>(params[0].getListVal());
				res.add(params[1]);
				return new reList(res);
			}else{
				throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
			}
		}else if(params.length == 3){
			if(params[0] instanceof reList && params[1] instanceof reNumber){
				ArrayList<reObject> res = new ArrayList<>(params[0].getListVal());
				int idx = ((reNumber)params[1]).val.intValueExact();
				res.add(idx < 0 ? res.size() + idx : idx, params[2]);
				return new reList(res);
			}else{
				throw new IllegalArgumentException("Bad arguments: \"" + Utils.join(params, ", ", 0, true) + "\"");
			}
		}else{
			throw new IllegalArgumentException("Only 2 or 3 parameter(s) allowed!");
		}
	};
	
	public static Function currTime = (params) -> {
		if(params.length != 0)
			throw new IllegalArgumentException("No parameters allowed!");
		
		return new reNumber(new BigDecimal(System.currentTimeMillis(), defaultMath));
	};
	
	public static Function formatDuration = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reNumber){
			long t = ((reNumber)params[0]).val.longValueExact();
			long h = TimeUnit.MILLISECONDS.toHours(t);
			long m = TimeUnit.MILLISECONDS.toMinutes(t) % 60;
			long s = TimeUnit.MILLISECONDS.toSeconds(t) % 60;
			long ms = TimeUnit.MILLISECONDS.toMillis(t) % 1000;
			return new reString(String.format("%02d:%02d:%02d.%03d", h, m, s, ms));
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function formatDate = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reNumber){
			long t = ((reNumber)params[0]).val.longValueExact();
			return new reString(LocalDateTime.ofInstant(Instant.ofEpochMilli(t), ZoneId.systemDefault()).format(
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static Function executeCmd = (params) -> {
		if(params.length != 1)
			throw new IllegalArgumentException("Only 1 parameter(s) allowed!");
		
		if(params[0] instanceof reString){
			Runtime r = Runtime.getRuntime();
			try{
				Process p = r.exec(params[0].toString());
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while((line = reader.readLine()) != null){
					System.out.println(line);
				}
				reader.close();
			}catch(IOException e){
				Utils.handleException(e, "An error occured while executing a command!");
			}
			return null;
		}else{
			throw new IllegalArgumentException("Bad argument: \"" + params[0].toString() + "\"");
		}
	};
	
	public static HashMap<String, DefaultFunction> functions = new HashMap<>();
	
	static{
		functions.put("currTimeMS", new DefaultFunction(currTime, "current time in milliseconds"));
		functions.put("msToDuration", new DefaultFunction(formatDuration, "converts milliseconds to a duration string"));
		functions.put("msToDate", new DefaultFunction(formatDate, "converts milliseconds to a date string"));
		
		functions.put("num", new DefaultFunction(toNumber, "casts to a number"));
		functions.put("round", new DefaultFunction(round, "rounds a number"));
		
		functions.put("str", new DefaultFunction(toString, "casts to a string"));
		functions.put("charToNum", new DefaultFunction(charToNum, "casts a character to an integer"));
		functions.put("numToChar", new DefaultFunction(numToChar, "casts an integer to a character"));
		functions.put("uppercase", new DefaultFunction(toUpperCase, "converts a string to uppercase"));
		functions.put("lowercase", new DefaultFunction(toLowerCase, "converts a string to lowercase"));
		
		functions.put("max", new DefaultFunction(max, "calculates the max of all input parameters"));
		functions.put("min", new DefaultFunction(min, "calculates the min of all input parameters"));
		functions.put("argMax", new DefaultFunction(argMax, "calculates the index of the max in the array"));
		functions.put("argMin", new DefaultFunction(argMin, "calculates the index of the min in the array"));
		functions.put("sum", new DefaultFunction(sum, "calculates the sum of all input parameters"));
		functions.put("abs", new DefaultFunction(abs, "calculates the absolute value of the input"));
		functions.put("log", new DefaultFunction(log, "calculates the log of base b of a, for parameters b, a"));
		functions.put("sin", new DefaultFunction(sin, "calculates sin"));
		functions.put("cos", new DefaultFunction(cos, "calculates cos"));
		functions.put("tan", new DefaultFunction(tan, "calculates tan"));
		functions.put("asin", new DefaultFunction(asin, "calculates arcsin"));
		functions.put("acos", new DefaultFunction(acos, "calculates arccos"));
		functions.put("atan", new DefaultFunction(atan, "calculates arctan"));
		
		functions.put("contains", new DefaultFunction(contains, "checks if the string, list, or map contains some value"));
		functions.put("indexOf", new DefaultFunction(indexOf, "gets the index of some value in the string or list, or returns -1"));
		functions.put("count", new DefaultFunction(count, "creates a map of each distinct element and the number of times it appears in the list"));
		functions.put("keyList", new DefaultFunction(keyList, "creates a list containing all of the keys in a map"));
		functions.put("valList", new DefaultFunction(valList, "creates a list containing all of the values in a map"));
		functions.put("listToMap", new DefaultFunction(listToMap, "creates a map from a list"));
		
		functions.put("randInt", new DefaultFunction(randomInt, "returns a random integer"));
		functions.put("randFloat", new DefaultFunction(randomFloat, "returns a random float"));
		functions.put("shuffle", new DefaultFunction(shuffleList, "shuffles a list"));
		
		functions.put("len", new DefaultFunction(length, "length of a list, map, or string"));
		functions.put("set", new DefaultFunction(set, "sets an item in a list or map"));
		functions.put("push", new DefaultFunction(pushList, "add an item to a list"));
		functions.put("pop", new DefaultFunction(popList, "removes an item from the list"));
		functions.put("reverse", new DefaultFunction(reverse, "reverses a string or a list"));
		functions.put("sort", new DefaultFunction(sortList, "sorts the list"));
		functions.put("nextPerm", new DefaultFunction(nextPermutation, "gets next lexicographically larger permutation"));
		functions.put("maskList", new DefaultFunction(maskList, "creates a sublist of a list using an integer mask"));
		functions.put("map", new DefaultFunction(map, "maps a function to each item in the list"));
		functions.put("deepMap", new DefaultFunction(deepMap, "maps a function to each item in a nested list"));
		functions.put("filter", new DefaultFunction(filter, "use a function to determine whether to keep each item or not"));
		functions.put("reduce", new DefaultFunction(reduce, "calculate some function for all items"));
		functions.put("deepReduce", new DefaultFunction(deepReduce, "calculate some function for all items in a nested list"));
		functions.put("flatten", new DefaultFunction(flatten, "flatten a nested list"));
		functions.put("generate", new DefaultFunction(generate, "generates a value using the parameter function(s)"));
		functions.put("generateList", new DefaultFunction(generateList, "generates a list using the parameter function(s)"));
		functions.put("zip", new DefaultFunction(zip, "take one of every list, per list item (transpose)"));
		
		functions.put("newFileReader", new DefaultFunction(createFileReader, "creates a file reader"));
		functions.put("newFileWriter", new DefaultFunction(createFileWriter, "creates a file writer"));
		functions.put("close", new DefaultFunction(closeFileIO, "closes a file reader or file writer"));
		functions.put("flush", new DefaultFunction(flushFileIO, "flushes a file writer"));
		functions.put("delete", new DefaultFunction(deleteFile, "deletes a file"));
		functions.put("write", new DefaultFunction(println, "writes/prints output"));
		functions.put("read", new DefaultFunction(readLine, "reads input"));
		functions.put("hasNext", new DefaultFunction(hasNextLine, "checks if the file has a next line"));
		
		functions.put("newWindow", new DefaultFunction(createWindow, "creates a new drawable window"));
		functions.put("refresh", new DefaultFunction(refreshWindow, "refreshes/redraws a window"));
		
		//the function, eval(), is not listed here!
		//it is separately handled!
		functions.put("exec", new DefaultFunction(executeCmd, "executes a command-line command"));
		
		functions.put("regex", new DefaultFunction(regex, "converts a string to a regex"));
		functions.put("replaceAll", new DefaultFunction(replace, "replaces all of occurrences of one string (can be regex) with another"));
		functions.put("matchGroups", new DefaultFunction(matchGroups, "creates a list of captured groups if matching"));
		functions.put("findAll", new DefaultFunction(find, "creates a list of all occurrences of a regex in a string"));
	}
	
	public static class DefaultFunction{
		public Function func;
		public String desc;
		
		public DefaultFunction(Function func, String desc){
			this.func = func;
			this.desc = desc;
		}
	}
	
	public static interface Function{
		public reObject apply(reObject[] params);
	}
}
