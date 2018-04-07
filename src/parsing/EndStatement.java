package parsing;

import objects.reObject;

public class EndStatement implements Statement{
	@Override
	public reObject calc(int start, int end){
		return null;
	}
	
	@Override
	public int getLineNum(){
		return -1;
	}
}
