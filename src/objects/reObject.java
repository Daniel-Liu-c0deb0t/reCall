package objects;

import java.util.ArrayList;

public interface reObject{
	public reObject deepClone();
	public ArrayList<reObject> getListVal();
	public int toBool();
}
