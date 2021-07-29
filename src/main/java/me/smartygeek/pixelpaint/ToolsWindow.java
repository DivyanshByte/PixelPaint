package me.smartygeek.pixelpaint;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import me.smartygeek.pixelpaint.tools.Tools;

public class ToolsWindow{
	
	private JFrame window;
	
	private JPanel panel;
	
	private ArrayList<JButton> toolsButtons;
	
	private JPanel topPanel;
	
	private Listener listener;
	
	public int selectedTool;
	
	static Point compCoords = null;
	
	public final int WIDTH = 100;
	public final int HEIGHT = 300;

	public ToolsWindow() {
		window = new JFrame("Tools");
		toolsButtons = new ArrayList<JButton>();
		panel = new JPanel();
		topPanel = new JPanel((LayoutManager) new FlowLayout(FlowLayout.CENTER));
		selectedTool = Tools.Square.id;
		listener = new Listener();
		addTools();
		addAttributes();
		addElementsToPanel();
		createWindow();
	}
	
	@SuppressWarnings("unused")
	public void addTools(){
		for (Tools t: Tools.values()){
			JButton btn = new JButton();
			btn.setBorder(BorderFactory.createEmptyBorder(10,5, 10, 5));
			this.toolsButtons.add(btn);
		}
		topPanel.setBorder(new EmptyBorder(2,30,2,30));
	}
	
	public void addAttributes(){
		for (JButton btn: this.toolsButtons){
			try {
			    Image img = ImageIO.read(getClass().getResource(Tools.getFromId(this.toolsButtons.indexOf(btn)+1).icon));
			    btn.setIcon(new ImageIcon(img));
			  } catch (Exception ex) {
			    ex.printStackTrace();
			}
			btn.setToolTipText(Tools.getFromId(this.toolsButtons.indexOf(btn)+1).name);
			btn.addActionListener(listener);
		}
        topPanel.addMouseListener(listener);
        topPanel.addMouseMotionListener(listener);
	}
	
	public void addElementsToPanel(){
		topPanel.add(new JLabel("Tools"));
		//panel.add(topPanel);
		for (JButton btn: this.toolsButtons){
			panel.add(btn);
		}
	}
	
	public void createWindow(){
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setSize(WIDTH,HEIGHT);
		Point locationPoint = PixelPaint.getWindowLocation();
		locationPoint.setLocation(locationPoint.getX()-WIDTH,locationPoint.getY());
		window.setLocation(locationPoint);
		window.add(BorderLayout.CENTER,panel);
		window.add(BorderLayout.NORTH,topPanel);
		window.setUndecorated(true);
		window.setBackground(window.getBackground());
		window.setShape(new RoundRectangle2D.Double(0, 0, window.getWidth(), window.getHeight(), 20, 20));
		toolsButtons.get(1).setSelected(true);
		window.setVisible(true);	
	}
	
	
	public class Listener implements ActionListener,MouseListener,MouseMotionListener{
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof JButton){
				try{
					selectedTool = Tools.getFromId(toolsButtons.indexOf(e.getSource())+1).id;
					((JButton)e.getSource()).setSelected(true);
					for (JButton btn: toolsButtons){
						if (btn != e.getSource()){
							btn.setSelected(false);
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
		}
		
		public void mouseReleased(MouseEvent e) {
			if (e.getSource() == topPanel) {
				compCoords = null;
			};
        }

        public void mousePressed(MouseEvent e) {
        	if (e.getSource() == topPanel) compCoords = e.getPoint();
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        	
        }
        public void mouseMoved(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {
        	if (e.getSource() == topPanel){
        		Point currCoords = e.getLocationOnScreen();
        		window.setLocation(currCoords.x - compCoords.x, currCoords.y - compCoords.y);
        	}
        }
	}

}
