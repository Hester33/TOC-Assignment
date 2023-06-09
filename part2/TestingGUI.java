package part2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeSet;

public class TestingGUI {
    private JFrame frame;
    private JTextField grammarFileField;
    private JTextField wordField;
    private JTable table;

    public static String word;    private String startingSymbol;
    private ArrayList<String> terminals;
    private ArrayList<String> nonTerminals;
    private HashMap<String, ArrayList<String>> grammar;

    public TestingGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("CYK Algorithm");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 200);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 5, 5);

        // Grammar file label
        JLabel grammarFileLabel = new JLabel("Grammar File:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(grammarFileLabel, constraints);

        // Grammar file text field
        grammarFileField = new JTextField();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        panel.add(grammarFileField, constraints);

        // Word label
        JLabel wordLabel = new JLabel("Word:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0.0;
        panel.add(wordLabel, constraints);

        // Word text field
        wordField = new JTextField();
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 1.0;
        panel.add(wordField, constraints);

        // Run button
        JButton runButton = new JButton("Run CYK Algorithm");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        panel.add(runButton, constraints);

        frame.getContentPane().add(panel);

        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String grammarFile = grammarFileField.getText();
                String word = wordField.getText();

                if (grammarFile.isEmpty() || word.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Grammar file and word fields cannot be empty.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    runCYKAlgorithm(grammarFile, word);
                }
            }
        });

        frame.setVisible(true);
    }

    private void displayResult(String[][] cykTable) {
        String[] columnNames = new String[cykTable[0].length];
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = Integer.toString(i + 1);
        }

        DefaultTableModel model = new DefaultTableModel(cykTable, columnNames);
        table = new JTable(model);
        table.setEnabled(false);

        JFrame resultFrame = new JFrame();
        resultFrame.setTitle("CYK Algorithm Result");
        resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultFrame.setLayout(new BorderLayout());
        resultFrame.add(new JScrollPane(table));
        resultFrame.pack();
        resultFrame.setVisible(true);
    }

    private void runCYKAlgorithm(String grammarFile, String word) {
        TestingGUI.word = word;
        terminals = new ArrayList<>();
        nonTerminals = new ArrayList<>();
        grammar = new HashMap<>();

        parseGrammar(grammarFile);
        String[][] cykTable = createCYKTable();
        doCYK(cykTable);
        displayResult(cykTable);
    }

    private void parseGrammar(String grammarFile) {
        try {
            Scanner input = new Scanner(new File(grammarFile));
            ArrayList<String> tmp = new ArrayList<>();
            int line = 2;

            word = getWord();

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
                grammar.put(leftSide, new ArrayList<>(tmp));
                tmp.clear();
            }

            input.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(frame, "Grammar file not found: " + grammarFile,
                    "File Not Found", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getWord() {
        return wordField.getText();
    }
//    
//    private String[][] createCYKTable() {
//        int length = word.length();
//        String[][] cykTable = new String[length + 1][length];
//
//        for (int i = 0; i < length; i++) {
//            cykTable[i + 1][0] = Character.toString(word.charAt(i));
//        }
//
//        return cykTable;
//    }


    public static String[][] createCYKTable (){
        //int length = isTokenWord ? toArray(word).length : word.length();
        int length = word.length();

        String[][] cykTable = new String[length + 1][];
        cykTable[0] = new String[length];
        for(int i = 1; i < cykTable.length; i++){
            cykTable[i] = new String[length - (i - 1)];
        }
        for(int i = 1; i < cykTable.length; i++){
            for(int j = 0; j < cykTable[i].length; j++){
                cykTable[i][j] = "";
            }
        }
        return cykTable;
    }
    private String[][] doCYK(String[][] cykTable) {
        int length = word.length();
      //Step 1: Fill header row
        for (int i = 0; i < cykTable[0].length; i++) {
            cykTable[0][i] = manageWord(word, i);
        }
      //Step 2: Get productions for terminals
        for (int i = 0; i < cykTable[1].length; i++) {
        	String[] validCombinations = checkIfProduces(new String[] {cykTable[0][i]});
            cykTable[1][i] = toString(validCombinations);
        }

        if (length <= 1) {
            return cykTable;
        }
      //Step 3: Get productions for sub words with the length of 2
        for (int i = 0; i < cykTable[2].length - 1; i++) {
            String[] downwards = toArray(cykTable[1][i]);
            String[] diagonal = toArray(cykTable[1][i + 1]);
            String[] validCombinations = checkIfProduces(getAllCombinations(downwards, diagonal));
            cykTable[2][i] = toString(validCombinations);
        }

        if (length <= 2) {
            return cykTable;
        }
        
      //Step 4: Get productions for sub words with the length of 2
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

    private String[] checkIfProduces(String[] toCheck) {
        ArrayList<String> storage = new ArrayList<>();
        for (String s : grammar.keySet()) {
            for (String t : toCheck) {
                if (grammar.get(s).contains(t)) {
                    storage.add(s);
                }
            }
        }
        return storage.toArray(new String[0]);
    }

    private String[] getAllCombinations(String[] array1, String[] array2) {
        ArrayList<String> combinations = new ArrayList<>();
        for (String s1 : array1) {
            for (String s2 : array2) {
                combinations.add(s1 + s2);
            }
        }
        return combinations.toArray(new String[0]);
    }

//    public static String toString(String[] input) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < input.length; i++) {
//            sb.append(input[i]);
//            if (i != input.length - 1) {
//                sb.append(",");
//            }
//        }
//        return sb.toString();
//    }
    
    public static String toString(String[] input){
        return Arrays.toString(input).replaceAll("[\\[\\]\\,]", "");
    }

    public static String manageWord(String word, int position){
        //if(!isTokenWord){ return Character.toString(word.charAt(position)); }
        //return toArray(word)[position];
    	return Character.toString(word.charAt(position));
    }

    private String[] toArray(String str) {
        return str.split("\\s");
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new TestingGUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
