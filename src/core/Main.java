package core;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main{
	public static void main(String[] args) throws Exception{
		String srcFile = null;
		for(int i = 0; i < args.length; i++){
			if(args[i].equals("-e") || args[i].equals("--error")){
				System.setErr(new PrintStream(new FileOutputStream(args[++i])));
			}else if(args[i].equals("-i") || args[i].equals("--input")){
				System.setIn(new FileInputStream(args[++i]));
			}else if(args[i].equals("-o") || args[i].equals("--output")){
				System.setOut(new PrintStream(new FileOutputStream(args[++i])));
			}else if(Files.exists(Paths.get(args[i]))){
				srcFile = args[i];
			}
		}
		
		if(srcFile == null)
			System.out.println("Hi! This is the reCall interpreter!");
		else
			Interpreter.run(Interpreter.parse(new FileInputStream(srcFile)), 0, Integer.MAX_VALUE);
	}
}
