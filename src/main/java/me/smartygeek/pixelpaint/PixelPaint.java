package me.smartygeek.pixelpaint;

import java.awt.Desktop;
import java.awt.Point;
import java.awt.Desktop.Action;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.intellijthemes.FlatVuesionIJTheme;

public class PixelPaint {
	
	public static JFrame theWindow;
	public static final int WIDTH = 800;
	public static final int HEIGHT = 700;
	private static PaintPanel thePaintPanel;
	public static StartupScreen startupWindow;
	public static JScrollPane scrollPane;
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new FlatVuesionIJTheme());
			UIManager.put( "Button.arc", 10000);
			UIManager.put( "Component.arc", 10000 );
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		} catch (UnsupportedLookAndFeelException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(),"Cannot Start PixelPaint",JOptionPane.ERROR_MESSAGE);
		}
		startupWindow = new StartupScreen();
		if (Desktop.getDesktop().isSupported(Action.APP_REQUEST_FOREGROUND))
			Desktop.getDesktop().requestForeground(true);
		//createNewImage(16,16,12);
		/*theWindow = new JFrame("PixelPaint");
		theWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theWindow.setSize(WIDTH,HEIGHT);
		theWindow.setLocationRelativeTo(null);
		theOptionsWindow = new OptionsWindow();
		thePaintPanel = new PaintPanel(theOptionsWindow);
		theWindow.setResizable(false);
		theWindow.add(thePaintPanel);
		theWindow.setVisible(true);*/
	}
	
	public static Point getWindowLocation() {
		return theWindow.getLocation();
	}
	
	public static void createNewImage(int cols,int rows, int size){
		theWindow = new JFrame("PixelPaint");
		theWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theWindow.setSize(WIDTH,HEIGHT);
		theWindow.setLocationRelativeTo(null);
		thePaintPanel = new PaintPanel(cols,rows,size);
		scrollPane= new JScrollPane(thePaintPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		theWindow.setResizable(false);
		theWindow.getContentPane().add(scrollPane);
		theWindow.setVisible(true);
	}
	public static void createNewImage(int cols,int rows, int size,BufferedImage image){
		theWindow = new JFrame("PixelPaint");
		theWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theWindow.setSize(WIDTH,HEIGHT);
		theWindow.setLocationRelativeTo(null);
		thePaintPanel = new PaintPanel(cols,rows,size,image);
		scrollPane= new JScrollPane(thePaintPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		theWindow.setResizable(false);
		theWindow.getContentPane().add(scrollPane);
		theWindow.setVisible(true);
	}
	

}
