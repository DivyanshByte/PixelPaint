package me.smartygeek.pixelpaint.tools;

import java.awt.Color;
import java.awt.Graphics2D;

public class EraserTool extends Tool {

	public EraserTool(int x, int y,int width,int height,Color color) {
		super(x, y, width, height, color, false);
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.fillRect(x, y, width, height);
	}

}
