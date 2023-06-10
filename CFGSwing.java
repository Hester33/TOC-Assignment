import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CFGSwing {
    static int np = 0;
    static String[][] grammar;
    static JTextArea outputTextArea, inputTextArea;
    static JTable chartTable;
    static DefaultTableModel chartTableModel;

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("CFG Parser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS)); // Use BoxLayout for left panel

        JButton importButton = new JButton("Import");
        JButton checkButton = new JButton("Check");
        JButton clearButton = new JButton("Clear");

        JPanel rightPanel = new JPanel(new BorderLayout());
        inputTextArea = new JTextArea(5, 20);
        outputTextArea = new JTextArea(10, 30);
        outputTextArea.setEditable(false);

        chartTableModel = new DefaultTableModel();
        chartTable = new JTable(chartTableModel);

        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
        JScrollPane chartScrollPane = new JScrollPane(chartTable);
        chartScrollPane.setPreferredSize(new Dimension(chartScrollPane.getPreferredSize().width, 100));
        chartScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        importButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    importGrammarFromFile(filePath);
                }
            }
        });

        checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parseInputString(inputTextArea.getText().trim());
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

        rightPanel.add(new JLabel("Enter the productions (in the format: variable,rule,terminal):"), BorderLayout.NORTH);
        rightPanel.add(inputScrollPane, BorderLayout.CENTER);
        rightPanel.add(outputScrollPane, BorderLayout.SOUTH);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);
        panel.add(chartScrollPane, BorderLayout.SOUTH);

        frame.getContentPane().add(panel, BorderLayout.NORTH);

        frame.pack();
        frame.setVisible(true);
    }

    private static void importGrammarFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder grammarBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                grammarBuilder.append(line).append("\n");
            }
            inputTextArea.setText(grammarBuilder.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error importing grammar from file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void parseInputString(String input) {
        String[] lines = input.split("\n");
        np = lines.length;
        grammar = new String[np][3];
        outputTextArea.setText("");

        outputTextArea.append("\nEntered Productions:\n");
        for (int i = 0; i < np; i++) {
            outputTextArea.append(lines[i] + "\n");
            String[] parts = lines[i].split(",");
            if (parts.length != 3) {
                JOptionPane.showMessageDialog(null, "Invalid grammar format: " + lines[i], "Error", JOptionPane.ERROR_MESSAGE);
                clearGrammar();
                return;
            }
            for (int j = 0; j < parts.length; j++) {
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
            int columnWidth = Math.max(50, chartTable.getColumnModel().getColumn(i + 1).getPreferredWidth());
            chartTable.getColumnModel().getColumn(i + 1).setPreferredWidth(columnWidth);
            for (int j = i; j >= 0; j--) {
                chartTable.setValueAt(chart[j][i], j, i + 1);
            }
        }

        // Output the result
        outputTextArea.append("\nCYK Chart:\n");
        outputTextArea.append("\nStart symbol: " + start);
        outputTextArea.append("\nString: " + str);
        outputTextArea.append("\nAccepted: " + accepted);
    }

    private static void clearGrammar() {
        inputTextArea.setText("");
        outputTextArea.setText("");
        chartTableModel.setColumnCount(0);
        chartTableModel.setRowCount(0);
    }
}