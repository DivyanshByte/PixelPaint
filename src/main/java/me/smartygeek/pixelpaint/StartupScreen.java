package me.smartygeek.pixelpaint;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Desktop.Action;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.io.FilenameUtils;

public class StartupScreen {
	
	public JFrame window;
	
	private JPanel panel;
	
	private JButton createNewBtn,openBtn;
	
	private JLabel createNewLabel,openLabel;
	
	private GridBagConstraints gbc;
	
	private Listener listener;

	public StartupScreen() {
		window = new JFrame("Welcome to PixelPaint");
		panel = new JPanel();
		createNewBtn = new JButton();
		openBtn = new JButton();
		createNewLabel = new JLabel("");
		openLabel = new JLabel("");
		listener = new Listener();
		setAttributes();
		addElementsToPanel();
		createWindow();
	}

	public void setAttributes() {
		panel.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		
		
		try {
			createNewBtn.setIcon(new ImageIcon(ImageIO.read(PixelPaint.class.getResource("/NewImage.png"))));
			openBtn.setIcon(new ImageIcon(ImageIO.read(PixelPaint.class.getResource("/OpenFile.png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		createNewBtn.setBorder(BorderFactory.createEmptyBorder());
		openBtn.setBorder(BorderFactory.createEmptyBorder());
		
		createNewBtn.addActionListener(listener);
		openBtn.addActionListener(listener);
		createNewLabel.addMouseListener(listener);
		openLabel.addMouseListener(listener);
	}
	
	public void addElementsToPanel() {
		// gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.CENTER;
		panel.add(createNewBtn,gbc);
		
		gbc.insets = new Insets(0,20, 0, 0);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		panel.add(createNewLabel,gbc);
		
		gbc.insets = new Insets(10,0, 0, 0);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		panel.add(openBtn,gbc);
		
		gbc.insets = new Insets(10,15, 0, 0);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		panel.add(openLabel,gbc);
	}
	
	public void createWindow() {
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setSize(250,200);
		window.setLocationRelativeTo(null);
		window.add(panel);
		window.setAutoRequestFocus(true);
		window.setVisible(true);
		if (Desktop.getDesktop().isSupported(Action.APP_OPEN_FILE))
			Desktop.getDesktop().setOpenFileHandler(e -> openFile(e.getFiles().get(0)));
		
		String createNewText = "Create new Image";
		String openText = "Open Existing File";
		
		new Thread(new Runnable() {
			
			int createNewIndex = 0;
			int openIndex = 0;

			@Override
			public void run() {
				try {
					Thread.sleep(30L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				createNewLabel.setText(createNewText.substring(0,createNewIndex>16?16:createNewIndex));
				openLabel.setText(openText.substring(0, openIndex>18?18:openIndex));
				createNewIndex++;
				openIndex++;
				if (createNewIndex <= 16 || openIndex <= 18)
					run();
			}
			
		}).start();
	}
	
	public void createNewImage() {
		JDialog dialog = new JDialog(window, "Create New Image");
		dialog.setSize(300,200);
		dialog.setLocationRelativeTo(window);
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
								PixelPaint.createNewImage(cols, rows, size);
								window.dispose();
							}
							dialog.dispose();
						}
					}
				}
	    	});
	    	id++;
	    }
		// dialog.dispose();
		/*Object selectedValue = optionPane.getValue();
		System.out.println(selectedValue);
		if (selectedValue != null){
			int cols = ((InputField) colsField).getNumber();
			int rows = ((InputField) rowsField).getNumber();
			int size = ((InputField) sizeField).getNumber();
		}*/
	}
	
	
	public static class InputField extends JTextField{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@Override
	    public void processKeyEvent(KeyEvent ev) {
			if (ev.getKeyCode() != KeyEvent.VK_ENTER){
				super.processKeyEvent(ev);
			}
			else{
				ev.consume();
			}
	        this.setText(getText().replaceAll("[^0-9]", ""));
	        return;
	    }

	    /**
	     * As the user is not even able to enter a dot ("."), only integers (whole numbers) may be entered.
	     */
		public int getNumber() {
	        int result = -1;
	        String text = getText();
	        if (text != null && !"".equals(text)) {
	            result = Integer.valueOf(text);
	        }
	        return result;
	    }
		
	}
	
	public void openImage() {
		FileDialog filechooser = new FileDialog(window,"Open Image",FileDialog.LOAD);
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
					PixelPaint.createNewImage(cols,rows,size,image);
					window.dispose();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void openFile(File file){
		window.dispose();
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
				PixelPaint.createNewImage(cols,rows,size,image);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public class Listener implements ActionListener, MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getSource() == createNewLabel) {
				createNewImage();
			}
			else if(e.getSource() == openLabel){
				openImage();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
			
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (e.getSource() == createNewBtn) {
				createNewImage();
			}
			else if(e.getSource() == openBtn){
				openImage();
			}
		}
		
	}

}
