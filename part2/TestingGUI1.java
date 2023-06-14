package part2;

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

public class TestingGUI1 {
	 JPanel panel = new JPanel();
	
    static int np = 0;
    static String[][] grammar;
    static JTextArea outputTextArea, inputTextArea;
    private static JTextField stringField;
    static JTable chartTable;
    static DefaultTableModel chartTableModel;
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
    
TestingGUI1(){

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
    // string label
    JLabel stringLabel = new JLabel("String:");
    // string text field
    stringField = new JTextField(10);
    outputTextArea = new JTextArea(10, 30);
    outputTextArea.setEditable(false);

//    chartTableModel = new DefaultTableModel();
//    chartTable = new JTable(chartTableModel);
    
     model = new DefaultTableModel();
    table = new JTable(model);
    JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
    JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
    JScrollPane chartScrollPane = new JScrollPane(table);
    //JScrollPane chartScrollPane = new JScrollPane(chartTable);
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
        	if(checkImport) {
        		createCYKTable();
        		implementCYK();
                displayResult();
        	}
        	else {
        		parseInputString(inputTextArea.getText().trim());
        	}
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

    //p.setLayout(new GridLayout(2,1));
    p.add(new JLabel("Enter the productions (in the format: variable,rule,terminal):"), BorderLayout.NORTH);
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
         	for(String s: grammars.keySet()){
         		inputTextArea.append(s + " -> " + grammars.get(s).toString().replaceAll("[\\[\\]\\,]", "").replaceAll("\\s", " |"));
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
    	outputTextArea.append("string: " + string);
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

  //--------methods for user input grammar--------//
    private static void parseInputString(String input) {
    	
    	outputTextArea.setText("");
        String[] lines = input.split("\n");
        np = lines.length;
        grammar = new String[np][3];
        outputTextArea.setText("");

        outputTextArea.append("\nEntered Productions:\n");
        for (int i = 0; i < np; i++) {
            outputTextArea.append(lines[i] + "\n");
            String[] parts = lines[i].split(",");
            for (int j = 0; j < 3; j++) {
                grammar[i][j] = parts[j].trim();
            }
        }

        String start = grammar[0][0];
        String str = JOptionPane.showInputDialog(null, "Enter the string to be checked:");

        if (str == null || str.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No string entered.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int len = str.length();
        String[][] chart = new String[len][len];

        // Initialize the chart with empty strings
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                chart[i][j] = "";
            }
        }

        // Fill the diagonal of the chart
        for (int i = 0; i < len; i++) {
            String terminal = String.valueOf(str.charAt(i));
            for (int j = 0; j < np; j++) {
                if (grammar[j][2].equals(terminal)) {
                    chart[i][i] += grammar[j][0];
                }
            }
        }

        // Fill the rest of the chart
        for (int l = 2; l <= len; l++) {
            for (int i = 0; i <= len - l; i++) {
                int j = i + l - 1;
                for (int k = i; k < j; k++) {
                    chart[i][j] += combinat(chart[i][k], chart[k + 1][j]);
                }
            }
        }

        // Check if the start symbol is present in the top-right cell of the chart
        boolean accepted = chart[0][len - 1].contains(start);

        // Update the CYK chart table
        String[] columnNames = new String[len + 1];
        columnNames[0] = "";
        for (int i = 0; i < len; i++) {
            columnNames[i + 1] = String.valueOf(str.charAt(i));
        }
        chartTableModel.setColumnIdentifiers(columnNames);
        chartTableModel.setRowCount(len);
        for (int i = 0; i < len; i++) {
            int columnWidth = Math.max(30, chartTable.getParent().getWidth() / (len + 1)); // Calculate column width
            chartTable.getColumnModel().getColumn(i + 1).setPreferredWidth(columnWidth);
            chartTable.getColumnModel().getColumn(i + 1).setMaxWidth(columnWidth);
            chartTable.getColumnModel().getColumn(i + 1).setResizable(false);
            chartTable.setValueAt(str.charAt(i), i, 0);
            for (int j = 0; j < len - i; j++) {
                chartTable.setValueAt(chart[j][j + i], j, i + 1);
            }
        }

        // Print the result
        if (accepted) {
            outputTextArea.append("\nString \"" + str + "\" is accepted by the grammar.");
        } else {
            outputTextArea.append("\nString \"" + str + "\" is not accepted by the grammar.");
        }
    }
    
    // Checks if the passed string can be derived from the grammar
    static boolean check(String a) {
        boolean found = false;
        for (int i = 0; i < np; i++) {
            if (grammar[i][1].equals(a)) {
                found = true;
                break;
            }
        }
        return found;
    }

    // Makes all possible combinations out of the two strings passed
    static String combinat(String a, String b) {
        String to_ret = "";
        for (int i = 0; i < a.length(); i++) {
            for (int j = 0; j < b.length(); j++) {
                String temp = "" + a.charAt(i) + b.charAt(j);
                if (check(temp)) {
                    to_ret += grammar[0][0];
                }
            }
        }
        return to_ret;
    }

    private static void clearGrammar() {
        np = 0;
        grammar = null;
        checkImport=false;
        inputTextArea.setText("");
        stringField.setText("");
        outputTextArea.setText("");
        //chartTableModel.setRowCount(0);
        model.setRowCount(0);
        model.setColumnCount(0);
    }
}