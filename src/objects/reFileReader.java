package objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import core.Persistent;

public class reFileReader implements reCloseable{
	private BufferedReader reader;
	public String path;
	private String nextLine = null;
	
	public reFileReader(String path) throws IOException{
		this.path = path;
		Path p = Paths.get(path);
		if(!p.isAbsolute())
			p = Persistent.workingDir.resolve(p);
		this.reader = Files.newBufferedReader(p);
	}
	
	@Override
	public String getType(){
		return "FileReader";
	}
	
	@Override
	public void close() throws IOException{
		reader.close();
	}
	
	public boolean hasNext() throws IOException{
		if(nextLine == null)
			nextLine = reader.readLine();
		return nextLine != null;
	}
	
	public String readline() throws IOException{
		if(nextLine == null){
			String s = reader.readLine();
			if(s == null)
				throw new IOException("End of file!");
			return s;
		}else{
			String res = nextLine;
			nextLine = null;
			return res;
		}
	}
	
	@Override
	public String toString(){
		return "File reader: \"" + path + "\"";
	}
	
	@Override
	public int hashCode(){
		return path.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null)
			return false;
		if(getClass() != o.getClass())
			return false;
		if(!path.equals(((reFileReader)o).path))
			return false;
		return true;
	}
	
	@Override
	public reObject deepClone(){
		return this;
	}

	@Override
	public ArrayList<reObject> getListVal(){
		return new ArrayList<reObject>(Arrays.asList(this));
	}

	@Override
	public int toBool(){
		return 1;
	}
}
