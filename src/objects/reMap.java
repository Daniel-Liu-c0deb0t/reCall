package objects;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class reMap implements reArrayAccessible{
	public HashMap<reObject, reObject> val;
	
	public reMap(HashMap<reObject, reObject> val){
		this.val = val;
	}
	
	@Override
	public String getType(){
		return "Map";
	}
	
	@Override
	public int toBool(){
		return val.isEmpty() ? 0 : 1;
	}
	
	@Override
	public String toString(){
		ArrayList<String> pairs = new ArrayList<>();
		for(reObject key : val.keySet()){
			String keyStr = key instanceof reString ? ("\"" + key.toString() + "\"") : key.toString();
			String valStr = val.get(key) instanceof reString ?
					("\"" + val.get(key).toString() + "\"") : val.get(key).toString();
			pairs.add(keyStr + ": " + valStr);
		}
		return "{" + String.join(", ", pairs) + "}";
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
		if(!val.equals(((reMap)o).val))
			return false;
		return true;
	}
	
	@Override
	public ArrayList<reObject> getListVal(){
		return new ArrayList<reObject>(Arrays.asList(this));
	}
	
	@Override
	public reObject deepClone(){
		HashMap<reObject, reObject> res = new HashMap<>();
		for(reObject key : val.keySet()){
			res.put(key.deepClone(), val.get(key).deepClone());
		}
		return new reMap(res);
	}
	
	@Override
	public reObject get(ArrayList<reObject> list){
		if(list.size() != 1)
			throw new IllegalArgumentException("Only one parameter allowed for map get!");
		
		if(!val.containsKey(list.get(0)))
			val.put(list.get(0), new reNumber(BigDecimal.ZERO));
		
		return val.get(list.get(0));
	}
	
	@Override
	public void set(reObject i, reObject o){
		val.put(i, o);
	}
}
