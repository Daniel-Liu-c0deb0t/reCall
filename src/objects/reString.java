package objects;

import java.util.ArrayList;
import java.util.Arrays;

public class reString implements reArrayAccessible, Comparable<reString>{
	public String val;
	public boolean isRegex;
	
	public reString(String val){
		val = val.replace("\\n", "\n"); //handle newlines
		val = val.replace("\\\n", "\\n"); //handle escaped backslashes
		this.val = val;
	}
	
	@Override
	public String getType(){
		return "String";
	}
	
	@Override
	public int toBool(){
		return val.isEmpty() ? 0 : 1;
	}
	
	@Override
	public String toString(){
		return val;
	}
	
	@Override
	public int hashCode(){
		return val.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null)
			return false;
		if(getClass() != o.getClass())
			return false;
		
		reString s = (reString)o;
		if((isRegex || s.isRegex) && (!isRegex || !s.isRegex)){ //only one can be a regex
			String regex = isRegex ? val : s.val;
			String text = isRegex ? s.val : val;
			
			if(!text.matches(regex)){
				return false;
			}
		}else if(!val.equals(o.toString())){
			return false;
		}
		
		return true;
	}
	
	@Override
	public int compareTo(reString o){
		return val.compareTo(o.val);
	}
	
	@Override
	public ArrayList<reObject> getListVal(){
		return new ArrayList<reObject>(Arrays.asList(this));
	}
	
	@Override
	public reObject deepClone(){
		return new reString(val);
	}
	
	@Override
	public reObject get(ArrayList<reObject> list){
		ArrayList<Integer> arr = new ArrayList<>();
		for(reObject o : list){
			if(o == null)
				arr.add(null);
			else
				arr.add(((reNumber)o).val.intValueExact());
		}
		
		if(arr.size() > 0 && arr.get(0) == null) arr.set(0, 0);
		if(arr.size() > 1 && arr.get(1) == null) arr.set(1, val.length());
		if(arr.size() > 2 && arr.get(2) == null) arr.set(2, 1);
		
		for(int i = 0; i < arr.size(); i++){
			if(arr.get(i) < 0){
				arr.set(i, arr.get(i) + val.length());
			}
		}
		if(arr.size() > 1){
			int temp = arr.get(0);
			arr.set(0, Math.min(arr.get(0), arr.get(1)));
			arr.set(1, Math.max(temp, arr.get(1)));
		}
		
		if(arr.size() == 1){
			return new reString(val.charAt(arr.get(0)) + "");
		}else if(arr.size() == 2){
			return new reString(val.substring(arr.get(0), arr.get(1)));
		}else if(arr.size() == 3){
			StringBuilder res = new StringBuilder();
			for(int i = arr.get(0); i < arr.get(1); i += arr.get(2)){
				res.append(val.charAt(i));
			}
			return new reString(res.toString());
		}else{
			throw new IllegalArgumentException("Maximum of three parameters allowed for string slice!");
		}
	}
	
	@Override
	public void set(reObject i, reObject o){
		throw new UnsupportedOperationException("Strings cannot be modified!");
	}
}
