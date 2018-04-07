package objects;

import java.io.IOException;

public interface reCloseable extends reObject{
	public void close() throws IOException;
}
