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
 * @version 2.1.0 +Srollability to error log and preview
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
	private JScrollPane errorLogScroll;
	private JScrollPane previewScroll;
	private File inputFile = null;
	private String output = "";
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
		guiCreateFrame();
		guiLinkActions();
	}
	
	/**
	 * Preview
	 * 
	 * @since 2.0.0
	 * @version 1.0.0
	 * 
	 * @param file FileReader
	 * 
	 */
	private void preview(FileReader file) {
		ArrayList<Section> sections = parse(file);
		
		sections.forEach(section -> format(section));
			
	}
	
	/**
	 * Parse
	 * 
	 * @since 2.0.0
	 * @version 1.0.0
	 * 
	 * @param file FileReader
	 */
	private ArrayList<Section> parse(FileReader file) {
		ArrayList<Section> sections = new ArrayList<Section>();
		boolean parsingFlags = false;
		boolean parsingParagraph = false;
		ArrayList<Character> parsedFlags;
		String parsedParagraph;
		int currentChar = -1; // init to empty file
		
		// First, try to read file. If IOException is thrown, catch it and display error.
		try {
			
			currentChar = file.read();
			previewTextArea.setText("");
			
			// While !EOF, parse.
			while(currentChar != -1) {
				// Assume we are parsing flags.
				parsingFlags = true;
				parsedFlags = new ArrayList<Character>();
				
				while(parsingFlags) {
					// If current char is '-', assume flags. Else, switch to paragraph.
					if((char)currentChar == '-') {
						currentChar = file.read(); // assuming this is a flag
						parsedFlags.add((Character)(char)currentChar); // double cast needed for int ~> Character
						currentChar = file.read(); // assume this is a '\n'
						currentChar = file.read(); // assume this is a '-'
						
					} else {
						
						// move on to parsing paragraph
						parsingFlags = false;
						parsingParagraph = true;
					}
				} // end parsing flags
				
				parsedParagraph = ""; // (re)init paragraph
				
				while(parsingParagraph) {
					
					if((char)currentChar == '\n') {
						parsedParagraph += (char)currentChar; // put '\n' into paragraph
						currentChar = file.read();
						
					} else if((char)currentChar == '-') {
						
						parsingParagraph = false;
						sections.add(new Section(parsedParagraph, new Settings(parsedFlags))); // create and add section
					} else if(currentChar == -1) {
						
						parsingFlags = false;
						parsingParagraph = false;
						sections.add(new Section(parsedParagraph, new Settings(parsedFlags))); // create and add section
					} else {
						
						parsedParagraph += (char)currentChar;
						currentChar = file.read();
					}
				}// end parsing paragraph
			} // end parsing file
		} catch(IOException error) { errorLogTextArea.setText(error.toString()); }
		
		return sections; // return
	}
	
	/**
	 * format(Settings settings, String pargraph) - takes an input file, sections it and formats the sections<br>
	 * 	based on format flags provided to that section.
	 * 
	 * @return void
	 * 
	 * @author Jonathan Cahal<br>
	 * Tingyu Luo<br>
	 * Anna McDonald<br>
	 * Ruijun Yang<br>
	 * 
	 * @since 1.2.0
	 * @version 2.0.0
	 * 
	 * @param section Section
	 */
	private void format(Section section) {
		Settings settings = section.settings;
		StringBuilder paragraph = new StringBuilder(section.paragraph);
		StringBuilder line = new StringBuilder();
		ArrayList<String> lines = new ArrayList<String>();
		Character currentChar;
		int lineStartIndex = 0;
		int lastWhitespace = 81;
		int lineSize = 80;
		
		if(settings.oneColumn) {
			
			if(settings.indented) {
				paragraph.insert(0, "     ");
				
			} else if(settings.blockIndented) {
				lineSize = 70;
			}
			
			for(int i = 0; i < paragraph.length() - 1; i++) {
				
				currentChar = paragraph.charAt(i);
				if(Character.isWhitespace(currentChar)) {
					lastWhitespace = i;
				} else if(currentChar == '\n') {
					paragraph.setCharAt(i, ' ');
				} 
				
				if(i % lineSize == 0) {
					if(lastWhitespace < i) {
						if(settings.rightJustified) {
							for(int j = 0; j < i - lastWhitespace; j++ ) {
								line.insert(0, " ");
							}
						} 
						
						lines.add(paragraph.substring(lineStartIndex, lastWhitespace));
						lineStartIndex = lastWhitespace + 1;
					} else {
						lines.add(paragraph.substring(lineStartIndex, i));
						lineStartIndex = i + 1;
					}
				}
			}
		} else if(settings.twoColumn) {

		}
		
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
		programFrame.setBounds(100, 100, 650, 400);
		
			
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
	 * ActionController decides what to do for each specific action.
	 * 
	 * @author Jonathan Cahal <br>
	 * Tingyu Luo<br>
	 * Anna McDonald<br>
	 * Ruijun Yang<br>
	 * 
	 * @since 1.1.0
	 * @version 1.0.3 +IOException handledd
	 */
	
	private class ActionController implements ActionListener {
		public void actionPerformed(ActionEvent e) {
	         String command = e.getActionCommand();  
	         
	         if( command.equals("chooseFile"))  {
	            
	        	fileChooser.showOpenDialog(programFrame.getParent());
	        	
	            if(fileChooser.getSelectedFile().exists()) {
	            	
	            	inputFile = fileChooser.getSelectedFile();
	            
	            } else {
	            	
	            	errorLogTextArea.setText("No file choosen");
	            	
	            }
	            
	            
	         } else if(command.contentEquals("preview")) {
	        	
	        	 try {
	        		 
	        		FileReader readFile = new FileReader(inputFile);
	        		preview(readFile);
	        		readFile.close();
	        		
	        	 } catch(IOException error) { errorLogTextArea.setText(error.toString()); }
	        	 
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
			flags.forEach((flag) -> updateSetting(flag));
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
					
					break;
				}
				case 'c': {
					leftJustified = false;
					rightJustified = false;
					centerJustified = true;
					centered = false;
					
					break;
				}
				case 't': {
					leftJustified = false;
					rightJustified = false;
					centerJustified = false;
					centered = true;
					
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
					
					break;
				}
				case 'b': {
					blockIndented = true;
					indented = false;
					
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
	
	private class Word {
		private int startIndex;
		private String text;
		private int length;
		
		public word(int startIndex, String text, int length) {
			this.startIndex = startIndex;
			this.text = text;
			this.length = length;
		}
	}
}
