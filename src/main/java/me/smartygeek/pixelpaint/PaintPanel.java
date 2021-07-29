package me.smartygeek.pixelpaint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.TooManyListenersException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.io.FilenameUtils;

import me.smartygeek.pixelpaint.StartupScreen.InputField;
import me.smartygeek.pixelpaint.tools.CircleTool;
import me.smartygeek.pixelpaint.tools.EraserTool;
import me.smartygeek.pixelpaint.tools.FillTool;
import me.smartygeek.pixelpaint.tools.SquareTool;
import me.smartygeek.pixelpaint.tools.Tool;
import me.smartygeek.pixelpaint.tools.Tools;

public class PaintPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Listener theListener;
	private OptionsWindow optionsWindow;
	private Graphics2D g2;
	private BufferedImage image;
	private ColorPickerWindow cPicker;
	private ToolsWindow tWindow;

	private int cols, rows, size;
	
	private PopupMenu popup;
	private MenuItem saveitem,newitem,openitem;
	private MenuListener menuListener;
	
	private int hoverx = -1,hovery=-1,hovercells=0;

	// tool attributes
	private Color toolColor;
	private int toolX, toolY, toolWidth, toolHeight;
	private List<Tool> strokes;
	
	private DropTarget dropTarget;
    private DropTargetHandler dropTargetHandler;

	public PaintPanel(int cols, int rows, int size) {
		this.cols = cols;
		this.rows = rows;
		this.size = size;
		this.image = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_ARGB);
		this.g2 = (Graphics2D) image.getGraphics();
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, image.getWidth(), image.getHeight());
		initFields();
	}
	public PaintPanel(int cols, int rows, int size,BufferedImage image1) {
		this.cols = cols;
		this.rows = rows;
		this.size = size;
		this.image = image1;
		this.g2 = (Graphics2D) image.getGraphics();
		initFields();
	}
	
	private void initFields(){
		this.optionsWindow = new OptionsWindow();
		this.cPicker = new ColorPickerWindow(toolColor);
		this.tWindow = new ToolsWindow();
		this.theListener = new Listener();
		this.addMouseListener(theListener);
		this.addMouseMotionListener(theListener);
		this.addMouseWheelListener(theListener);
		this.strokes = new ArrayList<Tool>();
		this.setPreferredSize(new Dimension(cols*size, rows*size));
		menuListener = new MenuListener();
		popup = new PopupMenu();
		saveitem = new MenuItem("Save Image");
		newitem = new MenuItem("New Image");
		openitem = new MenuItem("Open Image");
		popup.add(saveitem);
		popup.add(newitem);
		popup.add(openitem);
		add(popup);
		saveitem.addActionListener(menuListener);
		newitem.addActionListener(menuListener);
		openitem.addActionListener(menuListener);
		draw();
		repaint();
	}

	public void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0,cols*size, rows*size, null);
		int cellHeight = size;
	    int cellWidth = size;
	    for (int y = 0; y < rows*size; y += cellHeight) {
	        for (int x = 0; x < cols*size; x += cellWidth){
	        	if(hovery == y/size && hoverx == x/size){
	        		g.setColor(new Color(0,0,255,100));
	        		int hoverwidth = cellWidth*hovercells;
	        		int hoverheight= cellHeight*hovercells;
	        		if (x+(cellWidth*hovercells)>cols*size){
	        			for (int hv=hovercells;hv>0;hv--){
	        				if (x+(cellWidth*hv)<=cols*size){
	        					hoverwidth = cellWidth*hv;
	        					break;
	        				}
	        				continue;
	        			}
	        		}
	        		if (y+(cellHeight*hovercells)>rows*size){
	        			for (int hv=hovercells;hv>0;hv--){
	        				if (y+(cellHeight*hv)<=rows*size){
	        					hoverheight = cellHeight*hv;
	        					break;
	        				}
	        				continue;
	        			}
	        		}
	        		g.fillRect(x, y, hoverwidth, hoverheight);
	        	}
	        	else{
		        	g.setColor(Color.GRAY);
		            g.drawRect(x, y, cellWidth, cellHeight);
	        	}
	        }
	    }
	}

	public void draw() {
		drawStrokes();
	}

	public void drawStrokes() {
		for (Tool t : strokes) {
			t.setAttributes(g2);
			t.draw(g2);
		}
	}

	public void addStroke(MouseEvent e) {
		if (e.getX() <= cols * size && e.getY() <= rows * size) {
			toolX = e.getX() / size;
			toolY = e.getY() / size;
			toolWidth = optionsWindow.getPenSize();
			toolHeight = optionsWindow.getPenSize();
			toolColor = cPicker.getColor();
			if (tWindow.selectedTool == Tools.CIRCLETOOL) {
				strokes.add(new CircleTool(toolX, toolY, toolWidth + 2, toolHeight + 2, toolColor,
						optionsWindow.isFillColor()));
			} else if (tWindow.selectedTool == Tools.SQUARETOOl) {
				strokes.add(new SquareTool(toolX, toolY, toolWidth, toolHeight, toolColor));
			} else if (tWindow.selectedTool == Tools.FILLTOOL) {
				strokes.add(new FillTool(toolX, toolY, toolWidth, toolHeight, toolColor, image));
			} else if (tWindow.selectedTool == Tools.ERASERTOOL) {
				strokes.add(new EraserTool(toolX, toolY, optionsWindow.getEraserSize(), optionsWindow.getEraserSize(),toolColor));
			} else if(tWindow.selectedTool == Tools.EYEDROPPERTOOL){
				cPicker.redSlider.setValue(new Color(image.getRGB(toolX, toolY)).getRed());
				cPicker.greenSlider.setValue(new Color(image.getRGB(toolX, toolY)).getGreen());
				cPicker.blueSlider.setValue(new Color(image.getRGB(toolX, toolY)).getBlue());
			}
		}
		repaint();
		draw();
	}
	
	public void hoverCell(MouseEvent e){
		if (e.getX() <= cols * size && e.getY() <= rows * size) {
			hoverx = e.getX()/size;
			hovery = e.getY()/size;
			if (tWindow.selectedTool == Tools.ERASERTOOL){
				hovercells = optionsWindow.getEraserSize();
			}
			else{
				hovercells = optionsWindow.getPenSize();
			}
		}
		else{
			hoverx = -1;
			hovery = -1;
			hovercells = 0;
		}
		repaint();
		draw();
	}

	protected DropTarget getMyDropTarget() {
		if (dropTarget == null) {
			dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, null);
		}
		return dropTarget;
	}

	protected DropTargetHandler getDropTargetHandler() {
		if (dropTargetHandler == null) {
			dropTargetHandler = new DropTargetHandler();
		}
		return dropTargetHandler;
	}

	@Override
	public void addNotify() {
		super.addNotify();
		try {
			getMyDropTarget().addDropTargetListener(getDropTargetHandler());
		} catch (TooManyListenersException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		getMyDropTarget().removeDropTargetListener(getDropTargetHandler());
	}

	protected void importFiles(final List<File> files) throws Exception{
		File file = null;
		for (File f: files){
			if (FilenameUtils.getExtension(f.getName()).equals("ppa")){
				file = f;
				break;
			}
		}
		if (file == null || file.isDirectory()){
			throw new Exception("Invalid File Type");
		}
		if (file.exists()){
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = "";
				int row = 0;
				int[][] pixels = null;
				int cols = 0,rows = 0,size = 0;
				while((line = reader.readLine()) != null)
				{
					if(line.contains(";")){
						String values[] = line.split(";");
						cols = Integer.parseInt(values[0]);
						rows = Integer.parseInt(values[1]);
						size = Integer.parseInt(values[2]);
						pixels = new int[rows][cols];
					}
					else{
						String[] cols1 = line.split(",");
						int col = 0;
						for(String  c : cols1)
						{
							pixels[row][col] = Integer.parseInt(c);
							col++;
						}
						row++;
					}
				}
				reader.close();
				BufferedImage image = new BufferedImage(cols,rows,BufferedImage.TYPE_INT_ARGB);
			    for (int i = 0; i < rows; i++) {
			        for (int j = 0; j < cols; j++) {
			            int pixel=pixels[i][j];
			            image.setRGB(j, i, pixel);
			        }
			    }
			    PaintPanel.this.cols = cols;
				PaintPanel.this.rows = rows;
				PaintPanel.this.size = size;
				PaintPanel.this.image = image;
				PaintPanel.this.g2 = (Graphics2D) image.getGraphics();
				PaintPanel.this.strokes = new ArrayList<Tool>();
				PaintPanel.this.setPreferredSize(new Dimension(cols*size, rows*size));
				PaintPanel.this.repaint();
				PaintPanel.this.draw();
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	protected class DropTargetHandler implements DropTargetListener {

		protected void processDrag(DropTargetDragEvent dtde) {
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dtde.acceptDrag(DnDConstants.ACTION_COPY);
			} else {
				dtde.rejectDrag();
			}
		}

		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
			processDrag(dtde);
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			processDrag(dtde);
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent dtde) {
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
		}

		@Override
		public void drop(DropTargetDropEvent dtde) {

			Transferable transferable = dtde.getTransferable();
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dtde.acceptDrop(dtde.getDropAction());
				try {

					@SuppressWarnings("unchecked")
					List<File> transferData = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
					if (transferData != null && transferData.size() > 0) {
						importFiles(transferData);
						dtde.dropComplete(true);
					}

				} catch (Exception ex) {
					//ex.printStackTrace();
				}
			} else {
				dtde.rejectDrop();
			}
		}
	}


	private class Listener implements MouseListener, MouseMotionListener,MouseWheelListener{

		public void mouseClicked(MouseEvent e) {
			
		}

		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1)
				addStroke(e);
			hoverCell(e);
		}

		public void mouseReleased(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON3){
				popup.show(PaintPanel.this, e.getX(), e.getY());
			}
		}

		public void mouseEntered(MouseEvent e) {
			hoverCell(e);
		}

		public void mouseExited(MouseEvent e) {
			hoverCell(e);
		}

		public void mouseDragged(MouseEvent e) {
			addStroke(e);
			hoverCell(e);
		}

		public void mouseMoved(MouseEvent e) {
			hoverCell(e);
		}

		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.isShiftDown()){
				optionsWindow.penSizeSlider.setValue(optionsWindow.penSizeSlider.getValue()+(e.getWheelRotation()));
				hoverCell(e);
			}
			if (e.isAltDown()){
				optionsWindow.eraserSizeSlider.setValue(optionsWindow.eraserSizeSlider.getValue()+(e.getWheelRotation()));
				hoverCell(e);
			}
		}
	}
	
	public class MenuListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == saveitem){
				// System.out.println(getParent().getParent().getParent().getParent().getParent().getParent().getClass().getName());
				JDialog saveDialog = new JDialog(null,"Save Image",null);
				JPanel panel = new JPanel();
				JPanel topPanel = new JPanel((LayoutManager) new FlowLayout(FlowLayout.CENTER));
				JPanel bottomPanel = new JPanel((LayoutManager) new FlowLayout(FlowLayout.CENTER));
				
				
				DialogListener dlistener = new DialogListener(topPanel,saveDialog);
				topPanel.addMouseListener(dlistener);
		        topPanel.addMouseMotionListener(dlistener);
		        
		        JComboBox<String> exportType = new JComboBox<String>();
		        exportType.addItem("Image (.png)");
		        exportType.addItem("PixelPaint Art (.ppa)");
		        exportType.setSelectedIndex(1);
		        exportType.setBorder(BorderFactory.createTitledBorder("Export File Type"));
		        JTextField sizeField = new StartupScreen.InputField();
		        sizeField.setColumns(10);
		        sizeField.setText(String.valueOf(size));
		        sizeField.setBorder(BorderFactory.createTitledBorder("Export Cell Size"));
		        JCheckBox convertAlpha = new JCheckBox("<html>Convert White <br/> to Transparent<br/> (Only For PNG) </html>");
		        convertAlpha.setSelected(false);
		        panel.add(exportType);
		        panel.add(sizeField);
		        panel.add(convertAlpha);
		        
		        topPanel.add(new JLabel("Save Image"));
		        JButton saveBtn = new JButton("Save");
		        saveBtn.setBackground(Color.GREEN);
		        saveBtn.setForeground(Color.white);
		        saveBtn.setMargin(new Insets(5,5,5,5));
		        saveBtn.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						int exportT = exportType.getSelectedIndex();
						int sizeE = ((StartupScreen.InputField)sizeField).getNumber();
						if (sizeE != -1){
							FileDialog filechooser = new FileDialog(PixelPaint.theWindow,"Save Image",FileDialog.SAVE);
							if (exportT ==0){
								filechooser.setFile("Art.png");
								filechooser.setFilenameFilter((dir, name) -> FilenameUtils.getExtension(name)=="png");
							}
							else if(exportT == 1){
								filechooser.setFile("Art.ppa");
								filechooser.setFilenameFilter((dir, name) -> FilenameUtils.getExtension(name)=="ppa");
							}
							filechooser.setVisible(true);
							String sFile = (filechooser.getDirectory()==null?"":filechooser.getDirectory()) + (filechooser.getFile()==null?"":filechooser.getFile());
							if (sFile != ""){
								File selectedFile = new File(sFile);
								if (!selectedFile.exists()){
									try {
										selectedFile.createNewFile();
									} catch (IOException e1) {
										JOptionPane.showMessageDialog(PixelPaint.theWindow, e1.getMessage(),"Cannot Export Image", JOptionPane.ERROR_MESSAGE);
									}
								}
								BufferedImage tImage;
								if (convertAlpha.isSelected()){
									tImage = TransformColorToTransparency(image,new Color(255,255,255));
								}
								else{
									tImage = image;
								}
								if (exportT == 0){
									try {
										BufferedImage exportedImage = new BufferedImage(cols*sizeE,rows*sizeE,BufferedImage.TYPE_INT_ARGB);
										Graphics2D eg2d = exportedImage.createGraphics();
										eg2d.drawImage(tImage, 0,0, cols*sizeE, rows*sizeE, null);
										eg2d.dispose();
										ImageIO.write(exportedImage, "png", selectedFile);
									} catch (IOException e1) {
										JOptionPane.showMessageDialog(PixelPaint.theWindow, e1.getMessage(),"Cannot Export Image", JOptionPane.ERROR_MESSAGE);
									}
								}
								else if(exportT == 1){
									try {
										int[][] pixels = new int[rows][cols];

										for (int row = 0; row < rows; row++) {
											for (int col = 0; col < cols; col++) {
												pixels[row][col] = image.getRGB(col, row);
											}
										}
										
										StringBuilder builder = new StringBuilder();
										builder.append(cols+";"+rows+";"+size+";\n");
										for(int i = 0; i < rows; i++)//for each row
										{
											for(int j = 0; j < cols; j++)//for each column
											{
												builder.append(pixels[i][j]+"");//append to the output string
												if(j < cols-1){
													builder.append(",");
												}
											}
											builder.append("\n");//append new line at the end of the row
										}
										BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));
										writer.write(builder.toString());//save the string representation of the board
										writer.close();
									} catch (IOException e1) {
										JOptionPane.showMessageDialog(PixelPaint.theWindow, e1.getMessage(),"Cannot Save Image", JOptionPane.ERROR_MESSAGE);
									}
									
								}
								if (exportT ==1){
									Desktop.getDesktop().browseFileDirectory(selectedFile);
								}
								else if (exportT == 0){
									try {
										Desktop.getDesktop().open(selectedFile);
									} catch (IOException e1) {
										e1.printStackTrace();
									}
								}
							}
							
						}
						saveDialog.dispose();
					}
		        	
		        });
		        JButton cancelBtn = new JButton("Cancel");
		        cancelBtn.setBackground(Color.red);
		        cancelBtn.setForeground(Color.white);
		        cancelBtn.setMargin(new Insets(5,5,5,5));
		        cancelBtn.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						saveDialog.dispose();
					}
		        	
		        });
		        bottomPanel.add(saveBtn);
		        bottomPanel.add(cancelBtn);
		        
		        
				saveDialog.setSize(18, 20);
				saveDialog.setUndecorated(true);
				saveDialog.add(BorderLayout.CENTER,panel);
				saveDialog.add(BorderLayout.NORTH,topPanel);
				saveDialog.add(BorderLayout.SOUTH,bottomPanel);
				saveDialog.setLocationRelativeTo(PaintPanel.this);
				Point locationPoint = PixelPaint.getWindowLocation();
				locationPoint.setLocation(locationPoint.getX()+PixelPaint.WIDTH/2-100,locationPoint.getY()+25);
				saveDialog.setLocation(locationPoint);
				saveDialog.setShape(new RoundRectangle2D.Double(0, 0, saveDialog.getWidth(), saveDialog.getHeight(), 30, 30));
				saveDialog.setVisible(true);
				new Thread(new Runnable(){
					int width = 18;
					int height = 20;
					@Override
					public void run() {
						saveDialog.setSize(width, height);
						saveDialog.setShape(new RoundRectangle2D.Double(0, 0, saveDialog.getWidth(), saveDialog.getHeight(), 30, 30));
						if (width<200&& height<240){
							width += 18;
							height += 20;
							try {
								Thread.sleep(10L);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							run();
						}
					}
				}).start();
			}
			else if(e.getSource() == newitem){
				int surenew = JOptionPane.OK_OPTION;
				if (PaintPanel.this.strokes.size() >0)
					surenew = JOptionPane.showConfirmDialog(PixelPaint.theWindow, "Are You Sure You Want To Lose Your Existing Image? ","New Image",JOptionPane.OK_CANCEL_OPTION);
				if (surenew == JOptionPane.OK_OPTION){
					JDialog dialog = new JDialog(PixelPaint.theWindow, "Create New Image");
					dialog.setSize(300,200);
					dialog.setLocationRelativeTo(PixelPaint.theWindow);
					dialog.setResizable(false);
					dialog.setUndecorated(true);
					
					JPanel mainPanel = new JPanel(new BorderLayout());
					JPanel inputPanel = new JPanel(new GridBagLayout());
					
					JOptionPane optionPane = new JOptionPane("",JOptionPane.PLAIN_MESSAGE,JOptionPane.OK_CANCEL_OPTION,null,null,null);
				    JLabel rowsLabel = new JLabel("Rows: ");
				    JLabel colsLabel = new JLabel("Columns: ");
				    JLabel sizeLabel = new JLabel("Pixel Size: ");
				    JTextField rowsField = new InputField();
				    JTextField colsField = new InputField();
				    JTextField sizeField = new InputField();
				    
					GridBagConstraints gbc2 = new GridBagConstraints();
				    gbc2.fill = GridBagConstraints.HORIZONTAL;
				    gbc2.gridx = 0;
				    gbc2.gridy = 0;
				    gbc2.insets = new Insets(10,0,0,0);
				    gbc2.anchor = GridBagConstraints.WEST;
				    inputPanel.add(rowsLabel,gbc2);
				    gbc2.fill = GridBagConstraints.HORIZONTAL;
				    gbc2.gridx = 0;
				    gbc2.gridy = 1;
				    gbc2.insets = new Insets(10,0,0,0);
				    gbc2.anchor = GridBagConstraints.WEST;
				    inputPanel.add(colsLabel,gbc2);
				    gbc2.fill = GridBagConstraints.HORIZONTAL;
				    gbc2.gridx = 0;
				    gbc2.gridy = 2;
				    gbc2.insets = new Insets(10,0,0,0);
				    gbc2.anchor = GridBagConstraints.WEST;
				    inputPanel.add(sizeLabel,gbc2);
				    gbc2.gridx = 1;
				    gbc2.gridy = 0;
				    gbc2.insets = new Insets(10,10,0,0);
				    gbc2.ipadx = 50;
				    inputPanel.add(rowsField,gbc2);
				    gbc2.gridx = 1;
				    gbc2.gridy = 1;
				    gbc2.insets = new Insets(10,10,0,0);
				    gbc2.ipadx = 50;
				    inputPanel.add(colsField,gbc2);
				    gbc2.gridx = 1;
				    gbc2.gridy = 2;
				    gbc2.insets = new Insets(10,10,0,0);
				    gbc2.ipadx = 50;
				    inputPanel.add(sizeField,gbc2);
				    mainPanel.add(optionPane,BorderLayout.SOUTH);
				    mainPanel.add(inputPanel,BorderLayout.NORTH);
				    
					dialog.getContentPane().add(mainPanel);
					dialog.setVisible(true);
					
					int id = 1;
				    for (Component cp: ((JPanel)optionPane.getComponents()[1]).getComponents()){
				    	((JButton)cp).putClientProperty("uid","NewImage"+String.valueOf(id));
				    	((JButton)cp).addActionListener(new ActionListener(){
	
							@Override
							public void actionPerformed(ActionEvent e) {
								if (e.getSource() instanceof JButton & ((JButton)e.getSource()).getClientProperty("uid")!=null && ((JButton)e.getSource()).getClientProperty("uid").toString().contains("NewImage")){
									String uid = ((JButton)e.getSource()).getClientProperty("uid").toString();
									uid = uid.replace("NewImage", "");
									int bid = Integer.valueOf(uid);
									if (bid == 2){
										dialog.dispose();
									}
									else if(bid == 1){
										int cols = ((InputField) colsField).getNumber();
										int rows = ((InputField) rowsField).getNumber();
										int size = ((InputField) sizeField).getNumber();
										if (cols != -1 && rows != -1 && size != -1){
											PaintPanel.this.cols = cols;
											PaintPanel.this.rows = rows;
											PaintPanel.this.size = size;
											PaintPanel.this.image = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_ARGB);
											PaintPanel.this.g2 = (Graphics2D) image.getGraphics();
											g2.setColor(Color.WHITE);
											g2.fillRect(0, 0, image.getWidth(), image.getHeight());
											PaintPanel.this.strokes = new ArrayList<Tool>();
											PaintPanel.this.setPreferredSize(new Dimension(cols*size, rows*size));
											PaintPanel.this.repaint();
											PaintPanel.this.draw();
											PixelPaint.scrollPane.getVerticalScrollBar().setValue(0);
											PixelPaint.scrollPane.getHorizontalScrollBar().setValue(0);
										}
										dialog.dispose();
									}
								}
							}
				    	});
				    	id++;
				    }
				}
			}
			else if(e.getSource() == openitem){
				int surenew = JOptionPane.OK_OPTION;
				if (PaintPanel.this.strokes.size() >0)
					surenew = JOptionPane.showConfirmDialog(PixelPaint.theWindow, "Are You Sure You Want To Lose Your Existing Image? ","Open Image",JOptionPane.OK_CANCEL_OPTION);
				if (surenew == JOptionPane.OK_OPTION){
					FileDialog filechooser = new FileDialog(PixelPaint.theWindow,"Open Image",FileDialog.LOAD);
					filechooser.setFilenameFilter((dir, name) -> FilenameUtils.getExtension(name)=="ppa");
					filechooser.setVisible(true);
					String sFile = (filechooser.getDirectory()==null?"":filechooser.getDirectory()) + (filechooser.getFile()==null?"":filechooser.getFile());
					if (sFile != ""){
						File file = new File(sFile);
						if (file.exists()){
							try {
								BufferedReader reader = new BufferedReader(new FileReader(file));
								String line = "";
								int row = 0;
								int[][] pixels = null;
								int cols = 0,rows = 0,size = 0;
								while((line = reader.readLine()) != null)
								{
									if(line.contains(";")){
										String values[] = line.split(";");
										cols = Integer.parseInt(values[0]);
										rows = Integer.parseInt(values[1]);
										size = Integer.parseInt(values[2]);
										pixels = new int[rows][cols];
									}
									else{
										String[] cols1 = line.split(",");
										int col = 0;
										for(String  c : cols1)
										{
											pixels[row][col] = Integer.parseInt(c);
											col++;
										}
										row++;
									}
								}
								reader.close();
								BufferedImage image = new BufferedImage(cols,rows,BufferedImage.TYPE_INT_ARGB);
							    for (int i = 0; i < rows; i++) {
							        for (int j = 0; j < cols; j++) {
							            int pixel=pixels[i][j];
							            image.setRGB(j, i, pixel);
							        }
							    }
							    PaintPanel.this.cols = cols;
								PaintPanel.this.rows = rows;
								PaintPanel.this.size = size;
								PaintPanel.this.image = image;
								PaintPanel.this.g2 = (Graphics2D) image.getGraphics();
								PaintPanel.this.strokes = new ArrayList<Tool>();
								PaintPanel.this.setPreferredSize(new Dimension(cols*size, rows*size));
								PaintPanel.this.repaint();
								PaintPanel.this.draw();
								PixelPaint.scrollPane.getVerticalScrollBar().setValue(0);
								PixelPaint.scrollPane.getHorizontalScrollBar().setValue(0);
								
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				}
			}
		}
		
		private BufferedImage TransformColorToTransparency(BufferedImage image, Color c)
		  {
		    // Primitive test, just an example
			final int r1 = c.getRed();
			final int g1 = c.getGreen();
			final int b1 = c.getBlue();
		    ImageFilter filter = new RGBImageFilter()
		    {
		      public final int filterRGB(int x, int y, int rgb)
		      {
		        int r = (rgb & 0xFF0000) >> 16;
		        int g = (rgb & 0xFF00) >> 8;
		        int b = rgb & 0xFF;
		        if (r == r1 &&
		            g == g1 &&
		            b == b1)
		        {
		          // Set fully transparent but keep color
		          return rgb & 0xFFFFFF;
		        }
		        return rgb;
		      }
		    };

		    ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
		    BufferedImage result = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_ARGB);
		    Graphics2D g2 = result.createGraphics();
		    g2.drawImage(Toolkit.getDefaultToolkit().createImage(ip), 0,0, null);
		    g2.dispose();
		      return result;
		  }
		
		
		public class DialogListener implements MouseListener, MouseMotionListener{
			
			JPanel topPanel;
			Point saveWindowCoords = null;
			JDialog dialog;
			public DialogListener(JPanel topPanel,JDialog dialog){
				this.topPanel = topPanel;
				this.dialog = dialog;
			}
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getSource() == topPanel) saveWindowCoords= e.getPoint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				saveWindowCoords = null;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				if (e.getSource() == topPanel){
	        		Point currCoords = e.getLocationOnScreen();
	        		dialog.setLocation(currCoords.x - saveWindowCoords.x, currCoords.y - saveWindowCoords.y);
	        	}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				
			}
		}
	}
	
	public class UndoList<T> extends Stack<T> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final int maxSize;

		public UndoList(int size) {
			super();
			this.maxSize = size;
		}
		@Override
		public T push(T object) {
			while (this.size() > maxSize) {
				this.remove(0);
			}
			return super.push((T) object);
		}
	}

}
