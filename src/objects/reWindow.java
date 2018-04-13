package objects;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;

import utils.DrawPanel;

public class reWindow implements reObject{
	public JFrame frame;
	public DrawPanel p;
	
	public reWindow(String title, int width, int height, int scale, reFunction func){
		frame = new JFrame(title);
		
		p = new DrawPanel(width, height, scale, func);
		frame.add(p);
		
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		refresh();
	}
	
	public void refresh(){
		p.draw();
		frame.repaint();
	}
	
	@Override
	public String getType(){
		return "Window";
	}
	
	@Override
	public String toString(){
		return "Window: \"" + frame.getTitle() + "\"";
	}
	
	@Override
	public int hashCode(){
		return frame.getTitle().hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null)
			return false;
		if(getClass() != o.getClass())
			return false;
		if(!frame.equals(((reWindow)o).frame))
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
