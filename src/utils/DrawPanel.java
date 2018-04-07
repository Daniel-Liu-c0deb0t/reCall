package utils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;

import javax.swing.JPanel;

import objects.reFunction;
import objects.reList;
import objects.reNumber;
import objects.reObject;

@SuppressWarnings("serial")
public class DrawPanel extends JPanel{
	public reFunction func;
	public BufferedImage img;
	public int scale;
	
	public DrawPanel(int width, int height, int scale, reFunction func){
		this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		this.func = func;
		this.scale = scale;
		this.setPreferredSize(new Dimension(width * scale, height * scale));
	}
	
	public void draw(){
		for(int i = 0; i < img.getWidth(); i++){
			for(int j = 0; j < img.getHeight(); j++){
				reList list = (reList)func.apply(new reObject[]{new reNumber(new BigDecimal(i)), new reNumber(new BigDecimal(j))});
				img.setRGB(i, j, Utils.getRGB(list.val));
			}
			if(i % 2 == 0)
				repaint();
		}
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(img, 0, 0, img.getWidth() * scale, img.getHeight() * scale,
				0, 0, img.getWidth(), img.getHeight(), null);
		g.dispose();
	}
}
