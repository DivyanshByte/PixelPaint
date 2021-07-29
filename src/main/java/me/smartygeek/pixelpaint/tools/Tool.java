package me.smartygeek.pixelpaint.tools;

import java.awt.Color;
import java.awt.Graphics2D;

public abstract class Tool {
	
	protected int x,y,width,height;
	protected Color color;
	protected boolean fill;
	
	public Tool(int x, int y,int width,int height,Color color,boolean fill) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
		this.fill = fill;
	}
	
	public void setAttributes(Graphics2D g) {
		g.setColor(color);
	}
	
	public abstract void draw(Graphics2D g);

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isFill() {
		return fill;
	}

	public void setFill(boolean fill) {
		this.fill = fill;
	}
	
	

}

