package objects;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class reFileWriter implements reCloseable{
	private BufferedWriter writer;
	public String path;
	
	public reFileWriter(String path) throws IOException{
		this.path = path;
		this.writer = Files.newBufferedWriter(Paths.get(path));
	}
	
	@Override
	public void close() throws IOException{
		writer.close();
	}
	
	public void println(String s) throws IOException{
		writer.write(s);
		writer.newLine();
	}
	
	@Override
	public String toString(){
		return "File writer: \"" + path + "\"";
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
		if(!path.equals(((reFileWriter)o).path))
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
