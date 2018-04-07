package objects;

import java.util.ArrayList;

public interface reArrayAccessible extends reObject{
	public reObject get(ArrayList<reObject> list);
	public void set(reObject i, reObject o);
}
