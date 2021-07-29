package me.smartygeek.pixelpaint.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class FillTool extends Tool {
	
	private BufferedImage image;
	private int oldColor, newColor;

	public FillTool(int x, int y,int width,int height,Color color,BufferedImage image) {
		super(x, y, width, height, color, true);
		oldColor = image.getRGB(x, y);
		newColor = color.getRGB();
		this.image = image;
	}
	
	public void flood(int x,int y) {
		
		//bounds check
		if(x<0) return;
		if (y<0) return;
		if (x >= this.image.getWidth()) return;
		if (y >= this.image.getHeight()) return;
			
		// check for old color
		if(this.image.getRGB(x, y) != oldColor) return;

		// switch to new color
		image.setRGB(x, y, newColor);
		//this.image.getRaster().setDataElements(x, y, this.image.getColorModel().getDataElements(newColor,null));
		//this.image.getRaster().setDataElements(x, y, this.image.getColorModel().getDataElements(newColor,null));

		// flood the surrounding
		if(checkFlood(x+1,y)) {
			flood(x+1,y);
		}
		if(checkFlood(x,y+1)) {
			flood(x,y+1);
		}
		if (checkFlood(x-1,y)) {
			flood(x-1,y);
		}
		if (checkFlood(x,y-1)) {
			flood(x,y-1);
		}
		/*flood(x-1,y);
		flood(x+1,y);
		flood(x,y-1);
		flood(x,y+1);*/
	}
	
	public boolean checkFlood(int x1, int y1) {
		if(x1<0) return false;
		if (y1<0) return false;
		if (x1 >= this.image.getWidth()) return false;
		if (y1 >= this.image.getHeight()) return false;
		if(this.image.getRGB(x1, y1) != oldColor) return false;
		if (image.getRGB(x1, y1) == newColor)return false;
		return true; 
	}

	public void draw(Graphics2D g) {
		flood(x,y);
	}

}
