package cse360teamproject;

/**
 * Takes a text file input and outputs a formatted version of text
 * 	based on a selection of formatting flags.<br>
 * 
 * Default format
 * 	- 80 chars/line
 * 	- Left justified
 * 	- Single spaced
 * 
 * Formatting flags - One per line prefixed with a "-"
 * 	-r Right justified
 * 	-c Center justified
 * 	-l Left justified
 * 	
 *  -d double spaced
 * 	-s Single spaced
 * 	
 *  -i Indent (First line, 5 spaces, only w/ left justified)
 *  -b Indent multiple lines (Each line until removed or EOF, 10 spaces)
 *  -n Remove indentation
 *  
 *  -2 Two columns (35 chars / 10 spaces / 35 chars)
 *  -1 One column
 *  
 *  -e Blank line
 *  
 * @author Jonathan Cahal <br>
 * Tingyu Luo<br>
 * Anna McDonald<br>
 * Ruijun Yang<br>
 *  
 * @since 1.0.0
 * @version 1.1.1
 *  
 * @param inputFile
 * @param programFrame
 * @param sidePanel
 * @param actionPanel
 * @param errorPanel
 * @param previewPanel
 * @param errorLogTextArea
 * @param previewTextArea
 * @param fileChooser
 */

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.BorderFactory;

public class Formatter {
	
	private JFrame programFrame;
	private JPanel sidePanel;
	private JPanel actionPanel;
	private JPanel errorPanel;
	private JPanel previewPanel;
	private JTextArea errorLogTextArea;
	private JTextArea previewTextArea;
	private File inputFile = null;
	final JFileChooser fileChooser = new JFileChooser("~/");
	
	
	public static void main(String[] args) {
		Formatter formatter = new Formatter();
		
	}
	
	/**
	 * Default constructor, creates GUI and runs
	 * 
	 * @since 1.0.0
	 * @version 1.1.0
	 */
	public Formatter() {
		createGUI();
		run();
		
	}
	
	/**
	 * createGUI() - Creates GUI for the Formatter class
	 * 
	 * @return void
	 * 
	 * @author Jonathan Cahal <br>
	 * Tingyu Luo<br>
	 * Anna McDonald<br>
	 * Ruijun Yang<br>
	 * 
	 * @since 1.1.0
	 * @version 1.0.0 
	 */
	private void createGUI() {
		
		programFrame = new JFrame("Formatter");
		
		programFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // End program on frame close
		programFrame.setBounds(100, 100, 650, 400);
		
			
		sidePanel = new JPanel(new GridLayout(2,1));
		actionPanel = new JPanel(new GridLayout(3,1));
		errorPanel = new JPanel(new GridLayout(1,1));
		previewPanel = new JPanel(new GridLayout(1,1));
		errorLogTextArea = new JTextArea();
		previewTextArea = new JTextArea(); 
		
		errorLogTextArea.setEditable(false);
		errorLogTextArea.setText("No Errors");
		
		previewTextArea.setEditable(false);
		previewTextArea.setText("No preview available, choose a file to continue.");
		
		
		actionPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
		errorPanel.setBorder(BorderFactory.createTitledBorder("Error Log"));
		previewPanel.setBorder(BorderFactory.createTitledBorder("File Preview"));
		
		errorPanel.add(errorLogTextArea);
		
		previewPanel.add(previewTextArea);
		
		sidePanel.add(actionPanel);
		sidePanel.add(errorPanel);
		
		programFrame.setLayout(new BorderLayout());
		programFrame.add(sidePanel, BorderLayout.WEST);
		programFrame.add(previewPanel, BorderLayout.CENTER);
		
		programFrame.setVisible(true);
		
	}
	
	/**
	 * run() - Creates buttons on GUI and links events to ActionController
	 * 
	 * @return void
	 * 
	 * @author Jonathan Cahal <br>
	 * Tingyu Luo<br>
	 * Anna McDonald<br>
	 * Ruijun Yang<br>
	 * 
	 * @since 1.1.0
	 * @version 1.0.0
	 */
	
	private void run() {
		JButton chooseFileButton = new JButton("Choose File");
		JButton previewButton = new JButton("Preview");
		JButton saveAsButton = new JButton("Save As");
		
		chooseFileButton.setActionCommand("chooseFile");
		previewButton.setActionCommand("preview");
		saveAsButton.setActionCommand("saveAs");
		
		chooseFileButton.addActionListener(new ActionController());
		previewButton.addActionListener(new ActionController());
		saveAsButton.addActionListener(new ActionController());
		
		actionPanel.add(chooseFileButton);
		actionPanel.add(previewButton);
		actionPanel.add(saveAsButton);
		
		programFrame.setVisible(true);
	}
	
	/**
	 * ActionController decides what to do for each specific action.
	 * 
	 * @author Jonathan Cahal <br>
	 * Tingyu Luo<br>
	 * Anna McDonald<br>
	 * Ruijun Yang<br>
	 * 
	 * @since 1.1.0
	 * @version 1.0.1
	 */
	
	private class ActionController implements ActionListener {
		public void actionPerformed(ActionEvent e) {
	         String command = e.getActionCommand();  
	         
	         if( command.equals("chooseFile"))  {
	            
	        	fileChooser.showOpenDialog(programFrame.getParent());
	            inputFile = fileChooser.getSelectedFile();
	            
	            
	         } else if(command.contentEquals("preview")) {
	        	
	        	previewTextArea.setText("     Lorem  ipsum  dolor  sit  amet, consectetur adipiscing elit, sed do eiusmod" + "\n" + 
	        							"\n" +
	        							"tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam," + "\n" + 
	        							"\n" +
	        							"quis nostrud exercitation");
	        	 
	         } else if(command.contentEquals("saveAs")) {
	        	 
	        	 if (inputFile != null) {
	        		 fileChooser.showSaveDialog(programFrame.getParent());
	        	 } else {
	        		 errorLogTextArea.setText("No file choosen");
	        	 }
	        	 
	         }
	      }		
		
	}
}
