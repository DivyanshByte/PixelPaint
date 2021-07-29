package me.smartygeek.pixelpaint;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ColorPickerWindow{
	
	public JFrame window;
	
	private JPanel panel;
	
	//sliders
	public JSlider redSlider, greenSlider, blueSlider;
	
	private JTextField hexField;

	private TitledBorder redBorder,greenBorder,blueBorder,hexBorder;
	
	private ColorListener colorListener;
	
	public static boolean SHOWN = false;
	
	public static int WIDTH = 200;
	
	public static int HEIGHT = 300;
	
	public ColorPickerWindow(Color defaultColor) {
		initializeFields();
		setAttributes();
		addElementsToPanel();
		createWindow();
	}
	
	public void initializeFields() {
		window = new JFrame("Color Picker");
		panel = new JPanel();
		redSlider = new JSlider(0,255,0);
		greenSlider = new JSlider(0,255,0);
		blueSlider = new JSlider(0,255,0);
		hexField = new JTextField(String.format("#%02X%02X%02X", redSlider.getValue(),greenSlider.getValue(), blueSlider.getValue()),15);
		colorListener = new ColorListener();
	}

	public void setAttributes() {
		redBorder = new TitledBorder("Red: ") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override public Border getBorder() {
				return null;
			}
		};
		redSlider.setBorder(redBorder);
		redSlider.setMajorTickSpacing(85);
		redSlider.setValue(0);
		redSlider.setPaintTicks(true);
		redSlider.setPaintLabels(true);
		redSlider.setToolTipText("Red");
		redSlider.addChangeListener(colorListener);
		redSlider.addMouseListener(colorListener);
		
		greenBorder = new TitledBorder("Green: ") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override public Border getBorder() {
				return null;
			}
		};
		greenSlider.setBorder(greenBorder);
		greenSlider.setMajorTickSpacing(85);
		greenSlider.setValue(0);
		greenSlider.setPaintTicks(true);
		greenSlider.setPaintLabels(true);
		greenSlider.setToolTipText("Green");
		greenSlider.addChangeListener(colorListener);
		greenSlider.addMouseListener(colorListener);
		
		blueBorder = new TitledBorder("Blue: ") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override public Border getBorder() {
				return null;
			}
		};
		blueSlider.setBorder(blueBorder);
		blueSlider.setMajorTickSpacing(85);
		blueSlider.setValue(0);
		blueSlider.setPaintTicks(true);
		blueSlider.setPaintLabels(true);
		blueSlider.setToolTipText("Blue");
		blueSlider.addChangeListener(colorListener);
		blueSlider.addMouseListener(colorListener);
		
		hexBorder = new TitledBorder("Hex Format");
		hexField.setBorder(hexBorder);
		hexField.setBackground(getColor());
		hexField.setSize(100, hexField.getHeight());
		hexField.addActionListener(colorListener);
		hexField.setToolTipText("Hex Value");
		
		redBorder.setTitleColor(getForegroundColor(getColor()));
		blueBorder.setTitleColor(getForegroundColor(getColor()));
		greenBorder.setTitleColor(getForegroundColor(getColor()));
		hexBorder.setTitleColor(getForegroundColor(getColor()));
	}
	
	public void addElementsToPanel() {
		panel.add(redSlider);
		panel.add(greenSlider);
		panel.add(blueSlider);
		panel.add(hexField);
		panel.setBackground(new Color(redSlider.getValue(),greenSlider.getValue(),blueSlider.getValue()));
	}
	
	public void createWindow() {
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setSize(WIDTH,HEIGHT);
		
		Point locationPoint = PixelPaint.getWindowLocation();
		locationPoint.setLocation(locationPoint.getX()-WIDTH,locationPoint.getY()+HEIGHT);
		
		window.setLocation(locationPoint);
		window.add(panel);
		window.setVisible(true);
	}
	
	public Color getColor() {
		int red = redSlider.getValue();
		int green =  greenSlider.getValue();
		int blue = blueSlider.getValue();
		Color c = new Color(red,green,blue);
		return c;
	}
	
	public Color getForegroundColor(Color color) {
		int d = 0;
        final double a = 1 - (0.299 * color.getRed() + 0.587
                * color.getGreen() + 0.114 * color.getBlue()) / 255;

        if (a < 0.5)
            d = 0;
        else
            d = 255;

        return new Color(d, d, d);
	}
	
	private class ColorListener implements ChangeListener,ActionListener,MouseListener{

		public void stateChanged(ChangeEvent e) {
			int red = redSlider.getValue();
			int green =  greenSlider.getValue();
			int blue = blueSlider.getValue();
			Color c = new Color(red,green,blue);
			hexField.setText(String.format("#%02X%02X%02X", redSlider.getValue(),greenSlider.getValue(), blueSlider.getValue()));
			panel.setBackground(c);
			hexField.setBackground(c);
			hexField.setForeground(getForegroundColor(c));
			redSlider.setForeground(getForegroundColor(c));
			blueSlider.setForeground(getForegroundColor(c));
			greenSlider.setForeground(getForegroundColor(c));
			redBorder.setTitleColor(getForegroundColor(getColor()));
			blueBorder.setTitleColor(getForegroundColor(getColor()));
			greenBorder.setTitleColor(getForegroundColor(getColor()));
			hexBorder.setTitleColor(getForegroundColor(getColor()));
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == hexField) {
				if (hexField.getText().length() == 7) {
					Color c = Color.decode(hexField.getText());
					redSlider.setValue(c.getRed());
					greenSlider.setValue(c.getGreen());
					blueSlider.setValue(c.getBlue());
				}
				else {
					JOptionPane.showMessageDialog(PixelPaint.theWindow, "Please Enter in RRGGBB Format","Error",JOptionPane.ERROR_MESSAGE);
					int red = redSlider.getValue();
					int green =  greenSlider.getValue();
					int blue = blueSlider.getValue();
					Color c = new Color(red,green,blue);
					hexField.setText(String.format("#%02X%02X%02X", redSlider.getValue(),greenSlider.getValue(), blueSlider.getValue()));
					panel.setBackground(c);
					hexField.setBackground(c);
				}
			}
		}

		
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == redSlider && e.getButton() == MouseEvent.BUTTON3) {
				String value = (String) JOptionPane.showInputDialog(PixelPaint.theWindow,"Enter Red Value: ","Color Chooser",JOptionPane.QUESTION_MESSAGE,null,null,redSlider.getValue());
				if (((value!=null)?value:"").matches("^[+]?\\d+([.]\\d+)?$")) {
                    redSlider.setValue(Integer.parseInt(value));
                } else if (value != null){
                	JOptionPane.showMessageDialog(PixelPaint.theWindow, "Please Enter a Number","Error",JOptionPane.ERROR_MESSAGE);
                }
			}
			else if (e.getSource() == greenSlider && e.getButton() == MouseEvent.BUTTON3) {
				String value = (String) JOptionPane.showInputDialog(PixelPaint.theWindow,"Enter Green Value: ","Color Chooser",JOptionPane.QUESTION_MESSAGE,null,null,greenSlider.getValue());
				if (((value!=null)?value:"").matches("^[+]?\\d+([.]\\d+)?$")) {
                    greenSlider.setValue(Integer.parseInt(value));
                } else if (value != null){
                	JOptionPane.showMessageDialog(PixelPaint.theWindow, "Please Enter a Number","Error",JOptionPane.ERROR_MESSAGE);
                }
			}
			else if (e.getSource() == blueSlider && e.getButton() == MouseEvent.BUTTON3) {
				String value = (String) JOptionPane.showInputDialog(PixelPaint.theWindow,"Enter Blue Value: ","Color Chooser",JOptionPane.QUESTION_MESSAGE,null,null,blueSlider.getValue());
				if (((value!=null)?value:"").matches("^[+]?\\d+([.]\\d+)?$")) {
                    blueSlider.setValue(Integer.parseInt(value));
                } else if (value != null){
                	JOptionPane.showMessageDialog(PixelPaint.theWindow, "Please Enter a Number","Error",JOptionPane.ERROR_MESSAGE);
                }
			}
		}

		
		public void mousePressed(MouseEvent e) {
			
			
		}

		
		public void mouseReleased(MouseEvent e) {
			
			
		}

		
		public void mouseEntered(MouseEvent e) {
			
			
		}

		
		public void mouseExited(MouseEvent e) {
			
		}
	}
}