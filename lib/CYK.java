package lib;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeSet;

public class CYK {
	 JPanel panel = new JPanel();
	
    static JTextArea outputTextArea, inputTextArea;
    private static JTextField stringField;
    static JTable table;
    static DefaultTableModel model;
    
    private static String startingSymbol;
    private static String string;
    private static ArrayList<String> terminals;
    private static ArrayList<String> nonTerminals;
    private static HashMap<String, ArrayList<String>> grammars;
    private static String[][] cykTable= new String[0][0];
    private static String[] columnNames;
    
    private static boolean checkImport=false;
    
    CYK(){

    this.panel = new JPanel(new BorderLayout(10,10));

    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS)); // Use BoxLayout for left panel

    JButton importButton = new JButton("Import");
    JButton checkButton = new JButton("Check");
    JButton clearButton = new JButton("Clear");

    JPanel rightPanel = new JPanel(new BorderLayout());
    JPanel p = new JPanel(new BorderLayout());
    JPanel p1 = new JPanel();

    inputTextArea = new JTextArea(6, 20);
    JLabel stringLabel = new JLabel("String:");
    stringField = new JTextField(10);
    outputTextArea = new JTextArea(10, 30);
    outputTextArea.setEditable(false);

    
    model = new DefaultTableModel();
    table = new JTable(model);
    JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
    JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
    JScrollPane chartScrollPane = new JScrollPane(table);
    chartScrollPane.setPreferredSize(new Dimension(chartScrollPane.getPreferredSize().height, 200));
    chartScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    importButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File filePath = fileChooser.getSelectedFile();
            	importGrammarFromFile(filePath);
            	checkImport=true;
            }
        }
    });

    checkButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        	string =  stringField.getText();
        	if(!checkImport) {
        		getInput(inputTextArea.getText().trim());
        	}
        	createCYKTable();
    		implementCYK();
            displayResult();
            checkImport=false;
        }
    });

    clearButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            clearGrammar();
        }
    });

    leftPanel.add(importButton);
    leftPanel.add(Box.createVerticalStrut(10)); // Add vertical strut for spacing
    leftPanel.add(checkButton);
    leftPanel.add(Box.createVerticalStrut(10)); // Add vertical strut for spacing
    leftPanel.add(clearButton);

    p.add(new JLabel("Enter the productions (Format: startingSymbol, terminal, non-terminal, grammar):"), BorderLayout.NORTH);
    p.add(inputScrollPane, BorderLayout.CENTER);
    p1.setLayout(new FlowLayout(FlowLayout.LEFT));
    p1.add(stringLabel);
    p1.add(stringField);
    rightPanel.add(p, BorderLayout.NORTH);
    rightPanel.add(p1, BorderLayout.CENTER);
    rightPanel.add(outputScrollPane, BorderLayout.SOUTH);
    
    this.panel.add(leftPanel, BorderLayout.WEST);
    this.panel.add(rightPanel, BorderLayout.CENTER);
    this.panel.add(chartScrollPane, BorderLayout.SOUTH);
}

//--------methods for get user input--------//
private static void getInput(String input) {
	//get the text from text area
	String[] lines = input.split("\n");
	ArrayList<String> tmp = new ArrayList<>();
	int line = 2;

	terminals = new ArrayList<>();
	nonTerminals = new ArrayList<>();
	grammars = new HashMap<>();
	
	startingSymbol = lines[0];
	
	int i=1;
	while (line <= 3) {
	    tmp.addAll(Arrays.asList(toArray(lines[i])));
	    if (line == 2) {
	        terminals.addAll(tmp);
	    }
	    if (line == 3) {
	        nonTerminals.addAll(tmp);
	    }
	    tmp.clear();
	    line++;
	    i++;
	}

	i=3;
	while (i < lines.length) {
	    tmp.addAll(Arrays.asList(toArray(lines[i])));
	    String leftSide = tmp.get(0);
	    tmp.remove(0);
	    grammars.put(leftSide, new ArrayList<>(tmp));
	    tmp.clear();
	    i++;
		}
	}


//--------methods for import grammar file--------//
    private static void importGrammarFromFile(File filePath) {
    	 try {
    		 Scanner input = new Scanner(filePath);
    		 ArrayList<String> tmp = new ArrayList<>();
             int line = 2;

             terminals = new ArrayList<>();
             nonTerminals = new ArrayList<>();
             grammars = new HashMap<>();
             startingSymbol = input.next();
             input.nextLine();

             while (input.hasNextLine() && line <= 3) {
                 tmp.addAll(Arrays.asList(toArray(input.nextLine())));
                 if (line == 2) {
                     terminals.addAll(tmp);
                 }
                 if (line == 3) {
                     nonTerminals.addAll(tmp);
                 }
                 tmp.clear();
                 line++;
             }

             while (input.hasNextLine()) {
                 tmp.addAll(Arrays.asList(toArray(input.nextLine())));
                 String leftSide = tmp.get(0);
                 tmp.remove(0);
                 grammars.put(leftSide, new ArrayList<>(tmp));
                 tmp.clear();
             }
             input.close();
             inputTextArea.setText("");
             inputTextArea.append(startingSymbol);
             inputTextArea.append("\n"+terminals.toString().replaceAll("[\\[\\]\\,]", ""));
             inputTextArea.append("\n"+nonTerminals.toString().replaceAll("[\\[\\]\\,]", "")+"\n");
         	for(String s: grammars.keySet()){
         		inputTextArea.append(s +" "+ grammars.get(s).toString().replaceAll("[\\[\\]\\,]", ""));
         		inputTextArea.append("\n");
         	}
             
         } catch (FileNotFoundException e) {
             JOptionPane.showMessageDialog(null, "Grammar file not found: " + filePath,
                     "File Not Found", JOptionPane.ERROR_MESSAGE);
         }
    }


    private static void createCYKTable (){
        int length = string.length();

        cykTable = new String[length + 1][];
        cykTable[0] = new String[length];
        for(int i = 1; i < cykTable.length; i++){
            cykTable[i] = new String[length - (i - 1)];
        }
        for(int i = 1; i < cykTable.length; i++){
            for(int j = 0; j < cykTable[i].length; j++){
                cykTable[i][j] = "";
            }
        }
    }
    
    private static String[][] implementCYK() {
        int length = string.length();
      //Step 1: Fill header row
        for (int i = 0; i < cykTable[0].length; i++) {
            cykTable[0][i] = managestring(string, i);
        }
      //Step 2: Get productions for terminals
        for (int i = 0; i < length; i++) {
        	String[] validCombinations = checkIfProduces(new String[] {cykTable[0][i]});
            cykTable[1][i] = toString(validCombinations);
        }

        if (length <= 1) {
            return cykTable;
        }
      //Step 3: Get productions for sub strings with the length of 2
        for (int i = 0; i < cykTable[2].length - 1; i++) {
            String[] downwards = toArray(cykTable[1][i]);
            String[] diagonal = toArray(cykTable[1][i + 1]);
            String[] validCombinations = checkIfProduces(getAllCombinations(downwards, diagonal));
            cykTable[2][i] = toString(validCombinations);
        }

        if (length <= 2) {
            return cykTable;
        }
      //Step 4: Get productions for sub strings with the length of 3
        TreeSet<String> currentValues = new TreeSet<String>();

        for(int i = 3; i < cykTable.length; i++){
            for(int j = 0; j < cykTable[i].length; j++){
                for(int compareFrom = 1; compareFrom < i; compareFrom++){
                    String[] downwards = cykTable[compareFrom][j].split("\\s");
                    String[] diagonal = cykTable[i-compareFrom][j+compareFrom].split("\\s");
                    String[] combinations = getAllCombinations(downwards, diagonal);
                    String[] validCombinations = checkIfProduces(combinations);
                    if(cykTable[i][j].isEmpty()){
                        cykTable[i][j] = toString(validCombinations);
                    }else{
                        String[] oldValues = toArray(cykTable[i][j]);
                        ArrayList<String> newValues = new ArrayList<String>(Arrays.asList(oldValues));
                        newValues.addAll(Arrays.asList(validCombinations));
                        currentValues.addAll(newValues);
                        cykTable[i][j] = toString(currentValues.toArray(new String[currentValues.size()]));
                    }
                }
                currentValues.clear();
            }
        }
		return cykTable;
    }
    
    private static void displayResult() {
    	//print string and grammar
    	outputTextArea.setText("");
    	outputTextArea.append("String: " + string);
     	String g = "\nG = (" + terminals.toString().replace("[", "{").replace("]", "}") 
                 + ", " + nonTerminals.toString().replace("[", "{").replace("]", "}")
                 + ", P, " + startingSymbol + ")\n\nWith Productions P as: \n";
     	outputTextArea.append(g);
     	for(String s: grammars.keySet()){
     		outputTextArea.append(s + " -> " + grammars.get(s).toString().replaceAll("[\\[\\]\\,]", "").replaceAll("\\s", " |"));
     		outputTextArea.append("\n");
     	}
     	
     	
        //Evaluate if the Starting Symbol exist in the last cell of CYK table.
     	  String str;
          if(cykTable[cykTable.length-1][cykTable[cykTable.length-1].length-1].contains(startingSymbol)){
        	  str = "The string \"" + string + "\" is accepted by the grammar.";
          }else{
              str = "The string \"" + string + "\" is not accepted by the grammar.";
          }
          outputTextArea.append("\n"+str);
        
        columnNames = new String[cykTable[0].length];
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = Integer.toString(i + 1);
        }

        //CYK Table
         model = new DefaultTableModel(cykTable, columnNames);
        table.setModel(model);
    }

    private static String[] checkIfProduces(String[] toCheck) {
        ArrayList<String> storage = new ArrayList<>();
        for (String s : grammars.keySet()) {
            for (String t : toCheck) {
                if (grammars.get(s).contains(t)) {
                    storage.add(s);
                }
            }
        }
        return storage.toArray(new String[0]);
    }

 // Generates all possible combinations out of the two strings passed
    private static String[] getAllCombinations(String[] array1, String[] array2) {
        ArrayList<String> combinations = new ArrayList<>();
        for (String s1 : array1) {
            for (String s2 : array2) {
                combinations.add(s1 + s2);
            }
        }
        return combinations.toArray(new String[0]);
    }

    public static String toString(String[] input){
        return Arrays.toString(input).replaceAll("[\\[\\]\\,]", "");
    }

    public static String managestring(String string, int position){
    	return Character.toString(string.charAt(position));
    }
    
    //Splits a string into an array of strings by whitespace
    private static String[] toArray(String str) {
        return str.split("\\s");
    }

    private static void clearGrammar() {
        inputTextArea.setText("");
        stringField.setText("");
        outputTextArea.setText("");
        model.setRowCount(0);
        model.setColumnCount(0);
    }
}