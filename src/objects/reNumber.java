package objects;

import static core.Persistent.defaultMath;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

public class reNumber implements reObject, Comparable<reNumber>{
	public BigDecimal val;
	
	public reNumber(BigDecimal val){
		this.val = val.stripTrailingZeros();
	}
	
	public reNumber(String val){
		this.val = parseInt(val).stripTrailingZeros();
	}
	
	public static boolean isNumber(String s){
		try{
			new BigDecimal(s);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public static boolean isInt(reNumber o){
		return o.val.signum() == 0 || o.val.scale() <= 0 || o.val.stripTrailingZeros().scale() <= 0;
	}
	
	@Override
	public String getType(){
		return "Number";
	}
	
	@Override
	public int toBool(){
		return val.signum() == 0 ? 0 : 1;
	}
	
	@Override
	public int hashCode(){
		return val.hashCode();
	}
	
	@Override
	public String toString(){
		return val.toPlainString();
	}
	
	@Override
	public int compareTo(reNumber o){
		return val.compareTo(o.val);
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null)
			return false;
		if(getClass() != o.getClass())
			return false;
		if(val.compareTo(((reNumber)o).val) != 0)
			return false;
		return true;
	}
	
	@Override
	public ArrayList<reObject> getListVal(){
		return new ArrayList<reObject>(Arrays.asList(this));
	}
	
	@Override
	public reObject deepClone(){
		return new reNumber(val);
	}
	
	public static BigDecimal parseInt(String val){
		return new BigDecimal(val, defaultMath);
	}
}
