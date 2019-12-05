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
 *  -t Center, no justification
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
 * @author Jonathan Cahal<br>
 * Tingyu Luo<br>
 * Anna McDonald<br>
 * Ruijun Yang<br>
 *  
 * @since 1.0.0
 * @version 2.3.0
 *  
 * @param inputFile
 * @param output
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
import java.util.ArrayList;
import java.util.regex.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.BorderFactory;

public class Formatter {
	private JFrame programFrame;
	private JPanel sidePanel;
	private JPanel actionPanel;
	private JPanel errorPanel;
	private JPanel previewPanel;
	private JTextArea errorLogTextArea;
	private JTextArea previewTextArea;
	private JScrollPane errorLogScroll;
	private JScrollPane previewScroll;
	private File inputFile = null;
	final JFileChooser fileChooser = new JFileChooser("~/"); // ~/ is home dir in Unix. Needs to be tested in Windows. 
	
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
		fileChooser.setFileFilter(new FileNameExtensionFilter("TXT files only", "txt"));
		guiCreateFrame();
		guiLinkActions();
	}
	
	/**
	 * preview(File file) - opens and parses file and sends sections to format()
	 * 
	 * @since 2.0.0
	 * @version 2.0.0
	 * 
	 * @param file FileReader
	 * 
	 */
	private void preview(File file) {
		previewTextArea.setText("");
		
		try {
			FileReader reader = new FileReader(file);
			
			try {
				int c = reader.read();
				String fileString = "";
				while(c != -1) {
					fileString += (char)c;
					c = reader.read();
				}
				Pattern pattern = Pattern.compile("(.+\\R)");
				Matcher matcher = pattern.matcher(fileString);
				ArrayList<Character> flags = new ArrayList<Character>();
				ArrayList<String> lines = new ArrayList<String>();
				Settings settings = new Settings();

				while(matcher.find()) {
					String line = matcher.group(1);
					lines.add(line);
				}
				String paragraph = "";
				
				for(int i = 0; i < lines.size(); i++) {
					
					String line = lines.get(i);
					
					if(line.charAt(0) == '-') {
						
						if(i > 1 && !paragraph.isEmpty()) {
							flags.forEach(flag -> settings.updateSetting(flag));
							format(paragraph, settings);
							flags.clear();
							paragraph = "";
						}
						flags.add(line.charAt(1));
						
					} else {
						paragraph += line;
					}
				}
				
				// format the last paragraph
				flags.forEach(flag -> settings.updateSetting(flag));
				format(paragraph, settings);
				
				// print sections to preview, for debug
				// sections.forEach(section -> previewTextArea.append(section.toString()));
				reader.close();
				
			} catch(IOException error) { errorLogTextArea.setText(error.toString()); }
		} catch(FileNotFoundException error) { errorLogTextArea.setText(error.toString()); }
			
	}
	
	/**
	 * parse() - logic moved to the preview function.
	 * 
	 * @return ArrayList<Section>
	 * @param file FileReader
	 * 
	 * @author Jonathan Cahal<br>
	 * Tingyu Luo<br>
	 * Anna McDonald<br>
	 * Ruijun Yang<br>
	 * 
	 * @since 2.0.0
	 * @version 1.0.0
	 * 
	 * @deprecated
	 */
	private ArrayList<Section> parse(FileReader file) {
		return new ArrayList<Section>();
	}
	
	/**
	 * format(String paragraph, Settings settings) - takes an input file, sections it and formats the sections<br>
	 * 	based on format flags provided to that section.
	 * 
	 * @return void
	 * @param paragraph String
	 * @param Settings settings
	 * 
	 * @author Jonathan Cahal<br>
	 * Tingyu Luo<br>
	 * Anna McDonald<br>
	 * Ruijun Yang<br>
	 * 
	 * @since 1.2.0
	 * @version 2.2.0
	 * 
	 */
	private void format(String paragraph, Settings settings) {
		String newLine = "";
		ArrayList<String> lines = new ArrayList<String>();
		int lineSize = 80;
		
		paragraph = paragraph.replace('\n', ' '); // replace all new line chars
		
		if(settings.oneColumn) {
			
			if(settings.indented) 
			{
				paragraph = padding(5) + paragraph;
				
			} 
			else if(settings.blockIndented) {
				lineSize = 70;
			}
			
			while(paragraph.length() > lineSize) 
			{ // set line breaks
				int i = lineSize - 1;
				
				if(!Character.isWhitespace(paragraph.charAt(i))) 
				{ // does line end on a space " "? 
					while(!Character.isWhitespace(paragraph.charAt(i))) 
					{ // find the closest space 
						i--;
					}
				}
				
				newLine = paragraph.substring(0, i); // make and add a line
				
				if(settings.rightJustified) {
					lines.add(padding(lineSize - newLine.length()) + newLine);
					
				} else if(settings.centered) {
					String padding = padding((lineSize - newLine.length()) / 2);
					lines.add(String.format("%s%s%s", padding, newLine, padding));
					
				} else if(settings.centerJustified) {
					String padding = padding(lineSize - newLine.length());
					
					// find spaces
					ArrayList<Integer> spaceIndexes = new ArrayList<Integer>();
					
					for(int j = 0; j < newLine.length(); j++) {
						if(!Character.isWhitespace(newLine.charAt(j))) {
							spaceIndexes.add(j);
						}
					}
					StringBuilder tmpString = new StringBuilder(newLine);
					
					for(int j = 0; j <  padding.length(); j++) {
						tmpString.insert(spaceIndexes.get(j), " ");
					}
					
					newLine = tmpString.toString();
					
				}
				else 
				{	
					lines.add(newLine);
				}		
				
				paragraph = paragraph.substring(i + 1);	// trim line off newLine
			}
			
			lines.add(paragraph); // add what's left of the paragraph
			
		} 
		else if(settings.twoColumn) 
		{
			lineSize = 35;
			String leftParagraph;
			String rightParagraph;
			
			
			if(!Character.isWhitespace(paragraph.charAt(paragraph.length() / 2))) 
			{
				int i = paragraph.length() / 2;
				
				while(!Character.isWhitespace(paragraph.charAt(i))) 
				{ // find the closest space 
					i--;
				}
				
				leftParagraph = paragraph.substring(0, (i));
				rightParagraph = paragraph.substring(i + 1, paragraph.length());
			} 
			else 
			{
				leftParagraph = paragraph.substring(0, (paragraph.length() / 2));
				rightParagraph = paragraph.substring((paragraph.length() / 2), paragraph.length());
			}
			
			while(leftParagraph.length() > lineSize) 
			{ // set line breaks
				
				if(!Character.isWhitespace(leftParagraph.charAt(lineSize - 1))) 
				{ // does line end on a space " "? 
					int i = lineSize - 1;
					while(!Character.isWhitespace(leftParagraph.charAt(i))) 
					{ // find the closest space 
						i--;
					}
					
					newLine = leftParagraph.substring(0, i); // make and add a line
					
					lines.add(newLine + padding(45 - newLine.length())); // include column gutters
					leftParagraph = leftParagraph.substring(i + 1);	// trim line off paragraph
				} 
				else 
				{
					newLine = leftParagraph.substring(0, lineSize);
					lines.add(newLine + padding(9));
					leftParagraph = leftParagraph.substring(lineSize + 1);
				}
			}
			int length=(45-leftParagraph.length());
			if(settings.centered)
			{
				lines.add(padding((45-leftParagraph.length())/2));
				length=length/2;
				lines.add(leftParagraph + padding(length)); // add what's left of the paragraph
			}
			else if(settings.rightJustified)
			{
				lines.add(padding(length)+leftParagraph); // add what's left of the paragraph
			}
			else if(settings.indented)
			{
				length=length-5;
				lines.add(padding(5)+leftParagraph+padding(length));
			}
			else
			{
				lines.add(leftParagraph + padding(length)); // add what's left of the paragraph
			}
			int j = 0;
			while(rightParagraph.length() > lineSize) { // set line breaks
				
				
				if(!Character.isWhitespace(rightParagraph.charAt(lineSize - 1))) { // does line end on a space " "? 
					int i = lineSize - 1;
					while(!Character.isWhitespace(rightParagraph.charAt(i))) { // find the closest space 
						i--;
					}
					
					newLine = rightParagraph.substring(0, i); // make a line
					lines.set(j, lines.get(j) + newLine); // add it to the previous one
					rightParagraph = rightParagraph.substring(i + 1);	// trim line off paragraph
				} else {
					newLine = rightParagraph.substring(0, lineSize);
					lines.set(j, lines.get(j) + newLine);
					rightParagraph = rightParagraph.substring(lineSize + 1);
				}
				
				j++;
			}
			
			length=(45-rightParagraph.length());
			if(settings.centered)
			{
				lines.add(padding((45-rightParagraph.length())/2));
				length=length/2;
				lines.add(rightParagraph + padding(length)); // add what's right of the paragraph
			}
			else if(settings.rightJustified)
			{
				lines.add(padding(length)+rightParagraph); // add what's right of the paragraph
			}
			else if(settings.indented)
			{
				length=length-5;
				lines.add(padding(5)+rightParagraph+padding(length));
			}
			else
			{
				lines.add(rightParagraph + padding(length)); // add what's right of the paragraph
			}
			
		}
		
		if(settings.blankLine) {
			previewTextArea.append("\n");
		}
		
		if(settings.blockIndented) {
			lines.forEach(line -> previewTextArea.append(padding(10) + line + "\n"));
		} else if(settings.doubleSpaced) {
			lines.forEach(line -> previewTextArea.append(line + "\n\n"));
		} else {
			lines.forEach(line -> previewTextArea.append(line + "\n"));
		}
		
	}
	
	/**
	 * padding() - makes a string of whitespace at the given size
	 * 
	 * @param size int
	 * @return String
	 * 
	 * * @author Jonathan Cahal<br>
	 * Tingyu Luo<br>
	 * Anna McDonald<br>
	 * Ruijun Yang<br>
	 * 
	 * @since 2.0.0
	 * @version 1.0.0
	 */
	private String padding(int size) {
		String padding = "";
		while(padding.length() < size) {
			padding += " ";
		}
		
		return padding;
	}
	
	/**
	 * createGUI() - Creates GUI for the Formatter class
	 * 
	 * @return void
	 * 
	 * @author Jonathan Cahal<br>
	 * Tingyu Luo<br>
	 * Anna McDonald<br>
	 * Ruijun Yang<br>
	 * 
	 * @since 1.1.0
	 * @version 1.2.0 +Srollability to error log and preview
	 */
	private void guiCreateFrame() {
		
		programFrame = new JFrame("Formatter");
		
		programFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // End program on frame close
		programFrame.setBounds(100, 100, 670, 400);
		
			
		sidePanel = new JPanel(new GridLayout(2,1));
		actionPanel = new JPanel(new GridLayout(3,1));
		errorPanel = new JPanel(new GridLayout(1,1));
		previewPanel = new JPanel(new GridLayout(1,1));
		errorLogTextArea = new JTextArea();
		previewTextArea = new JTextArea();
		errorLogScroll = new JScrollPane(errorLogTextArea);
		previewScroll = new JScrollPane(previewTextArea);
		
		errorLogTextArea.setEditable(false);
		errorLogTextArea.setText("No Errors");
		
		previewTextArea.setEditable(false);
		previewTextArea.setText("No preview available, choose a file to continue.");
		
		
		actionPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
		errorPanel.setBorder(BorderFactory.createTitledBorder("Error Log"));
		previewPanel.setBorder(BorderFactory.createTitledBorder("File Preview"));
		
		errorPanel.add(errorLogScroll);
		previewPanel.add(previewScroll);
		
		sidePanel.add(actionPanel);
		sidePanel.add(errorPanel);
		
		programFrame.setLayout(new BorderLayout());
		programFrame.add(sidePanel, BorderLayout.WEST);
		programFrame.add(previewPanel, BorderLayout.CENTER);
		
		programFrame.setVisible(true);
		
	}
	
	/**
	 * guiLinkActions() - Creates buttons on GUI and links events to ActionController
	 * 
	 * @return void
	 * 
	 * @author Jonathan Cahal <br>
	 * Tingyu Luo<br>
	 * Anna McDonald<br>
	 * Ruijun Yang<br>
	 * 
	 * @since 1.1.0
	 * @version 1.1.0 run() renamed to guiLinkActions()
	 */
	
	private void guiLinkActions() {
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
	 * ActionController - decides what to do for each specific action.
	 * 
	 * @author Jonathan Cahal <br>
	 * Tingyu Luo<br>
	 * Anna McDonald<br>
	 * Ruijun Yang<br>
	 * 
	 * @since 1.1.0
	 * @version 1.2.0
	 */
	
	private class ActionController implements ActionListener {
		public void actionPerformed(ActionEvent e) {
	         String command = e.getActionCommand();  
	         
	         if( command.equals("chooseFile"))  {
	            
	        	fileChooser.showOpenDialog(programFrame.getParent());
	        	
	            if(fileChooser.getSelectedFile().exists()) {
	            	
	            	inputFile = fileChooser.getSelectedFile();
	            	previewTextArea.setText("Preview available, click 'Preview' to continue.");
	            
	            } 
	            else {
	            	
	            	errorLogTextArea.setText("No file choosen");
	            	
	            }
	            
	            
	         } else if(command.contentEquals("preview")) {
	        	
	        	 preview(inputFile);
	        	 
	         } else if(command.contentEquals("saveAs")) {
	        	 
	        	 if (inputFile != null) {
	        		 fileChooser.showSaveDialog(programFrame.getParent());
	        	 } else {
	        		 errorLogTextArea.setText("No file choosen");
	        	 }
	        	 
	         }
	      }		
		
	}
	
	/**
	 * Settings holds all the format settings for Formatter to use
	 * 
	 * @author Jonathan Cahal <br>
	 * Tingyu Luo<br>
	 * Anna McDonald<br>
	 * Ruijun Yang<br>
	 *
	 * @since 1.2.0
	 * @version 2.0.0
	 * 
	 
	 */
	private class Settings {
		private boolean leftJustified = true;
		private boolean rightJustified = false;
		private boolean centerJustified = false;
		private boolean centered = false;
		private boolean singleSpaced = true;
		private boolean doubleSpaced = false;
		private boolean indented = false;
		private boolean blockIndented = false;
		private boolean oneColumn = true;
		private boolean twoColumn = false;
		private boolean blankLine = false;
		private int lineSize = 80;
		private ArrayList<Character> flags = new ArrayList<Character>();
		
		
		/**
		 * Default constructor for the Settings class
		 * 
		 * @since 1.0.0
		 * @version 2.0.0
		 */
		public Settings() {
			// nothing, defaults set in member initializations
		}

		/**
		 * Parameterized constructor for the Settings class
		 * 
		 * @since 1.0.0
		 * @version 2.0.0
		 * 
		 * @param
		 */
		public Settings(ArrayList<Character> flags) {
			this.flags = flags;
			flags.forEach((flag) -> updateSetting(flag));
		}
		
		public ArrayList<Character> getFlags() {
			return flags;
		}
		
		public void updateSetting(Character flag) {
			switch(flag) {
				case 'l': {
					leftJustified = true;
					rightJustified = false;
					centerJustified = false;
					centered = false;
					
					break;
				}
				case 'r': {
					leftJustified = false;
					rightJustified = true;
					centerJustified = false;
					centered = false;
					indented = false;
					blockIndented = false;
					oneColumn = true;
					twoColumn = false;
					
					break;
				}
				case 'c': {
					leftJustified = false;
					rightJustified = false;
					centerJustified = true;
					centered = false;
					indented = false;
					blockIndented = false;
					oneColumn = true;
					twoColumn = false;
					
					break;
				}
				case 't': {
					leftJustified = false;
					rightJustified = false;
					centerJustified = false;
					centered = true;
					indented = false;
					blockIndented = false;
					oneColumn = true;
					twoColumn = false;
					
					break;
				}
				case 'd': {
					singleSpaced = false;
					doubleSpaced = true;
					
					break;
				}
				case 's': {
					singleSpaced = true;
					doubleSpaced = false;
					
					break;
				}
				case 'i': {
					indented = true;
					blockIndented = false;
					rightJustified = false;
					centerJustified = false;
					centered = false;
					oneColumn = true;
					twoColumn = false;
					
					break;
				}
				case 'b': {
					blockIndented = true;
					indented = false;
					rightJustified = false;
					centerJustified = false;
					centered = false;
					oneColumn = true;
					twoColumn = false;
					
					break;
				}
				case '1': {
					oneColumn = true;
					twoColumn = false;
					
					break;
				}
				case '2': {
					twoColumn = true;
					oneColumn = false;
					indented = false;
					blockIndented = false;
					rightJustified = false;
					centerJustified = false;
					centered = false;
					
					break;
				}
				case 'e': {
					blankLine = true;
					
					break;
				}
				case 'h': {
					indented = false;
					blockIndented = false;
					
					break;
				}
			
				
			}
		}
		
		public String toString() {
			return  "leftJustified: " + leftJustified + "\n" +
					"rightJustified: "  + rightJustified + "\n" +
					"centerJustified: " + centerJustified + "\n" +
					"centered: " + centered + "\n" +
					"singleSpaced: " + singleSpaced + "\n" +
					"doubleSpaced: " + doubleSpaced + "\n" +
					"indented: " + indented + "\n" +
					"blockIndented: " + blockIndented + "\n" +
					"oneColumn: " + oneColumn + "\n" +
					"twoColumn: " + twoColumn + "\n" +
					"blankLine: " + blankLine + "\n";
		}
		
	}
	
	/**
	 * Section class
	 * 
	 * @author JonathanCahal
	 *
	 * @since 2.0.0
	 * @version 1.0.0
	 * @deprecated
	 * 
	 */
	private class Section {
		private String paragraph;

		private Settings settings;
		
		public Section() {
			this("", new Settings());
		}
		
		public Section(String paragraph, Settings settings) {
			setParagraph(paragraph);
			setSettings(settings);
		}
		
		public void setParagraph(String paragraph) {
			this.paragraph = paragraph;
		}
		
		public void setSettings(Settings settings) {
			this.settings = settings;
		}

		public String getParagraph() {
			return paragraph;
		}
		
		public Settings getSettings() {
			return settings;
		}
		
		public String toString() {
			return settings.toString() + paragraph;
		}
	}
}
