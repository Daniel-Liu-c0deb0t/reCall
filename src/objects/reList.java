package objects;

import java.util.ArrayList;

import utils.Utils;

public class reList implements reArrayAccessible, Comparable<reList>{
	public ArrayList<reObject> val;
	
	public reList(ArrayList<reObject> val){
		this.val = val;
	}
	
	@Override
	public String getType(){
		return "List";
	}
	
	@Override
	public int toBool(){
		return val.isEmpty() ? 0 : 1;
	}
	
	@Override
	public String toString(){
		ArrayList<String> s = new ArrayList<>();
		for(reObject o : val){
			s.add((o instanceof reString) ? ("\"" + o.toString() + "\"") : o.toString());
		}
		return "[" + String.join(", ", s) + "]";
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
		if(!val.equals(((reObject)o).getListVal()))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(reList o){
		for(int i = 0; i < val.size() && i < o.val.size(); i++){
			if(val.get(i).equals(o.val.get(i))){
				continue;
			}
			return Utils.compare(val.get(i), o.val.get(i));
		}
		
		if(val.size() > o.val.size())
			return 1;
		else if(val.size() < o.val.size())
			return -1;
		else
			return 0;
	}
	
	@Override
	public ArrayList<reObject> getListVal(){
		return val;
	}
	
	@Override
	public reObject deepClone(){
		ArrayList<reObject> res = new ArrayList<>();
		for(int i = 0; i < val.size(); i++){
			res.add(val.get(i).deepClone());
		}
		return new reList(res);
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
		
		for(int i = 0; i < Math.min(2, arr.size()); i++){
			if(arr.get(i) != null && arr.get(i) < 0){
				arr.set(i, arr.get(i) + val.size());
			}
		}
		
		if(arr.size() > 0 && arr.get(0) == null)
			arr.set(0, arr.size() > 2 && arr.get(2) != null && arr.get(2) < 0 ? val.size() - 1 : 0);
		if(arr.size() > 1 && arr.get(1) == null)
			arr.set(1, arr.size() > 2 && arr.get(2) != null && arr.get(2) < 0 ? -1 : val.size());
		if(arr.size() > 2 && arr.get(2) == null)
			arr.set(2, 1);
		
		if(arr.size() == 1){
			return val.get(arr.get(0));
		}else if(arr.size() == 2){
			ArrayList<reObject> res = new ArrayList<>();
			for(int i = arr.get(0); i < arr.get(1); i++){
				res.add(val.get(i));
			}
			return new reList(res);
		}else if(arr.size() == 3){
			ArrayList<reObject> res = new ArrayList<>();
			for(int i = arr.get(0); arr.get(2) < 0 ? i > arr.get(1) : i < arr.get(1); i += arr.get(2)){
				res.add(val.get(i));
			}
			return new reList(res);
		}else{
			throw new IllegalArgumentException("Maximum of three parameters allowed for list slice!");
		}
	}
	
	@Override
	public void set(reObject i, reObject o){
		val.set(((reNumber)i).val.intValueExact(), o);
	}
}
