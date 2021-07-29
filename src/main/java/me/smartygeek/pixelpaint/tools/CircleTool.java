package me.smartygeek.pixelpaint.tools;

import java.awt.Color;
import java.awt.Graphics2D;

public class CircleTool extends Tool {

	public CircleTool(int x, int y,int width,int height,Color color,boolean fill) {
		super(x, y, width, height, color, fill);
	}

	public void draw(Graphics2D g) {
		if (fill) g.fillOval(x, y, width, height); 
		else g.drawOval(x, y, width, height);
	}

}
