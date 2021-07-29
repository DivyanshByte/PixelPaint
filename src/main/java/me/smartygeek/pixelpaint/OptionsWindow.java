package me.smartygeek.pixelpaint;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import me.smartygeek.pixelpaint.tools.Tools;

public class OptionsWindow {
	
	// Field
	
	// window
	public JFrame theWindow;
	
	// panel
	private JPanel thePanel;
	
	//sliders
	public JSlider penSizeSlider,eraserSizeSlider;
	
	private JCheckBox fillCheckBox;
	
	//listener
	private OptionsListener theOptionsListener;
	
	// selected tool
	private int currentTool;
	
	public static final int WIDTH = 220;
	
	public static final int HEIGHT = 200;
	
	public OptionsWindow() {
		initializeFields();
		setAttributes();
		addElementsToPanel();
		createWindow();
	}
	
	public void initializeFields() {
		theWindow = new JFrame("PixelPaint Options");
		thePanel = new JPanel();
		penSizeSlider = new JSlider(1,10);
		eraserSizeSlider = new JSlider(1,10);
		theOptionsListener = new OptionsListener();
		currentTool = Tools.SQUARETOOl;
		fillCheckBox = new JCheckBox("Fill Circle",true);
	}
	
	public void setAttributes() {		
		penSizeSlider.setBorder(BorderFactory.createTitledBorder("Tool Size"));
		penSizeSlider.setMajorTickSpacing(1);
		penSizeSlider.setValue(1);
		penSizeSlider.setPaintTicks(true);
		penSizeSlider.setPaintLabels(true);
		penSizeSlider.setSnapToTicks(true);
		penSizeSlider.addChangeListener(theOptionsListener);
		
		eraserSizeSlider.setBorder(BorderFactory.createTitledBorder("Eraser Size"));
		eraserSizeSlider.setMajorTickSpacing(1);
		eraserSizeSlider.setValue(1);
		eraserSizeSlider.setPaintTicks(true);
		eraserSizeSlider.setPaintLabels(true);
		eraserSizeSlider.setSnapToTicks(true);
		eraserSizeSlider.addChangeListener(theOptionsListener);
	}
	
	public void addElementsToPanel() {
		thePanel.add(penSizeSlider);
		thePanel.add(eraserSizeSlider);
		thePanel.add(fillCheckBox);
	}
	
	public void createWindow() {
		theWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theWindow.setResizable(false);
		theWindow.setSize(WIDTH,HEIGHT);
		
		Point locationPoint = PixelPaint.getWindowLocation();
		locationPoint.setLocation(locationPoint.getX()+PixelPaint.WIDTH,locationPoint.getY());
		
		theWindow.setLocation(locationPoint);
		theWindow.add(thePanel);
		theWindow.setVisible(true);
	}
	
	public int getPenSize() {
		return penSizeSlider.getValue();
	}
	
	public int getEraserSize() {
		return eraserSizeSlider.getValue();
	}
	
	public int getCurrentTool() {
		return currentTool;
	}
	
	public boolean isFillColor(){
		return fillCheckBox.isSelected();
	}

	private class OptionsListener implements ChangeListener,ActionListener{

		public void stateChanged(ChangeEvent e) {
		}

		public void actionPerformed(ActionEvent e) {
			
		}
	}
}
