package parsing;

import objects.reObject;

public interface Statement{
	public reObject calc(int start, int end);
	public int getLineNum();
}
