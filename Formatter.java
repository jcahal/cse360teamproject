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
 *  @author Jonathan Cahal <br>
 *  Tingyu Luo<br>
 *  Anna McDonald<br>
 *  Ruijun Yang<br>
 *  
 *  @since 1.0.0
 *  @version 1.0.0
 *  
 *	@param inputFile
 *	@param gui
 */

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.BorderFactory;

public class Formatter {
	
	private FileInputStream inputFile = null;
	private GUI gui = null;
	
	
	public static void main(String[] args) {
		Formatter program = new Formatter();
	}
	
	/**
	 * Default constructor, prepares GUI
	 * 
	 * @since 1.0.0
	 * @version 1.0.0
	 */
	public Formatter() {
		gui = new GUI();
		
	}
	
	/**
	 * GUI class for the Formatter
	 * 
	 * @author Jonathan Cahal <br>
	 * Tingyu Luo<br>
	 * Anna McDonald<br>
	 * Ruijun Yang<br>
	 * 
	 * @since 1.0.0
	 * @version 1.0.0
	 * 
	 * @param sidePanel
	 * @param actionPanel
	 * @param errorPanel
	 * @param previewPanel
	 * @param chooseFileButton
	 * @param previewButton
	 * @param saveAsButton
	 * @param errorLogTextArea
	 * @param previewTextArea
	 *
	 */
	private class GUI extends JFrame {
		
		private JPanel sidePanel;
		private JPanel actionPanel;
		private JPanel errorPanel;
		private JPanel previewPanel;
		private JButton chooseFileButton;
		private JButton previewButton;
		private JButton saveAsButton;
		private JTextArea errorLogTextArea;
		private JTextArea previewTextArea;
		
		public GUI() {
			
			sidePanel = new JPanel(new GridLayout(2,1));
			actionPanel = new JPanel(new GridLayout(3,1));
			errorPanel = new JPanel(new GridLayout(1,1));
			previewPanel = new JPanel(new GridLayout(1,1));
			chooseFileButton = new JButton("Choose File");
			previewButton = new JButton("Preview");
			saveAsButton = new JButton("Save As");
			errorLogTextArea = new JTextArea();
			errorLogTextArea.setEditable(false);
			errorLogTextArea.setText("No Errors");
			previewTextArea = new JTextArea(); 
			previewTextArea.setEditable(false);
			previewTextArea.setText("No preview available, choose a file to continue.");
			
			actionPanel.add(chooseFileButton);
			actionPanel.add(previewButton);
			actionPanel.add(saveAsButton);
			actionPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
			errorPanel.add(errorLogTextArea);
			errorPanel.setBorder(BorderFactory.createTitledBorder("Error Log"));
			previewPanel.add(previewTextArea);
			previewPanel.setBorder(BorderFactory.createTitledBorder("File Preview"));
			sidePanel.add(actionPanel);
			sidePanel.add(errorPanel);
			
			setTitle("Formatter");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 600, 400);
			setLayout(new BorderLayout());
			add(sidePanel, BorderLayout.WEST);
			add(previewPanel, BorderLayout.CENTER);
			
			setVisible(true);
		}
		
	}
}
