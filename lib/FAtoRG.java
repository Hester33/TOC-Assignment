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
import java.util.*;

public class FAtoRG {
    JPanel panel = new JPanel();

    private JPanel button_panel;
    private JButton import_button = new JButton("Import");
    private JButton clear_button = new JButton("Clear");
    private JButton generate_button = new JButton("Generate Output");
    
    private JPanel input_panel;
    private static JTextArea input_box;
    private JLabel input_label;
    private JScrollPane input_scroll;
    private JFileChooser input_file_browser;
    
    private JPanel output_panel;
    private static JTextArea output_box;
    private JLabel output_label;
    private JScrollPane output_scroll;

    private static boolean has_imported = false;
    private static Set<String> states;
    private static Set<String> alphabets;
    private static String start_state;
    private static Set<String> final_states;
    private static java.util.List<String> test_strings;
    private static Map<String, Map<String, String>> transition_map;
    private static final String EPSILON = "e";
    private static final String OR = " | ";
    private static final String ARROW = " -> ";
    private static final String OPEN_BRACKET = "{ ";
    private static final String CLOSE_BRACKET = " }";

    FAtoRG() {
        this.panel = new JPanel();
        this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
        
        button_panel = new JPanel();
        button_panel.setLayout(new BoxLayout(button_panel, BoxLayout.LINE_AXIS));
        button_panel.add(import_button);
        button_panel.add(Box.createHorizontalStrut(5));
        button_panel.add(clear_button);
        button_panel.add(Box.createHorizontalStrut(5));
        button_panel.add(generate_button);
        button_panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        input_panel = new JPanel();
        input_panel.setLayout(new BoxLayout(input_panel, BoxLayout.X_AXIS));
        input_box = new JTextArea(10,30);
        input_box.setLineWrap(true);
        input_box.setWrapStyleWord(true);
        input_scroll = new JScrollPane(input_box);
        input_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        input_label = new JLabel("Input");
        input_panel.add(input_label);
        input_panel.add(Box.createHorizontalStrut(30));
        input_panel.add(input_scroll);
        input_panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        output_panel = new JPanel();
        output_panel.setLayout(new BoxLayout(output_panel, BoxLayout.X_AXIS));
        output_box = new JTextArea(10, 30);
        output_box.setLineWrap(true);
        output_box.setWrapStyleWord(true);
        output_box.setEditable(false);
        output_scroll = new JScrollPane(output_box);
        output_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        output_label = new JLabel("Output");
        output_panel.add(output_label);
        output_panel.add(Box.createHorizontalStrut(20));
        output_panel.add(output_scroll);
        output_panel.add(Box.createRigidArea(new Dimension(0, 5)));

        this.panel.add(button_panel);
        this.panel.add(input_panel);
        this.panel.add(output_panel);

        import_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!has_imported) {
                    input_file_browser = new JFileChooser();
                    
                    int return_value = input_file_browser.showOpenDialog(null);
                    if (return_value == JFileChooser.APPROVE_OPTION) {
                        File file_path = input_file_browser.getSelectedFile();
                        import_from_file(file_path);
                        String states_string = string_builder(states);
                        String alphabets_string = string_builder(alphabets);
                        String final_states_string = string_builder(final_states);

                        input_box.append(states_string.toString() + "\n");
                        input_box.append(alphabets_string.toString() + "\n");
                        input_box.append(String.format("P0 = %s\n", start_state));
                        input_box.append(final_states_string.toString() + "\n");

                        input_box.append("\nStrings to be checked:\n");
                        for (int i = 0; i < test_strings.size(); i++) {
                            input_box.append(test_strings.get(i) + "\n");
                        }

                        has_imported = true;
                    }
                }
            }
        });

        clear_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clear_io();
            }
        });

        generate_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String data = input_box.getText().trim();
                
                if (has_imported) {
                    Map<String, String> regular_grammar = rg_conversion();
                    
                    output_box.append("Regular Grammar:\n");

                    for (String state : regular_grammar.keySet()) {
                        output_box.append(String.format("%s %s %s\n", state, ARROW, regular_grammar.get(state)));
                    }

                    output_box.append("\nThe results after checking the strings are:\n");
                    for (String test_string : test_strings) {
                        boolean isAccepted = check_string(test_string.trim(), regular_grammar);
                        output_box.append(test_string + ": " + (isAccepted ? "OK\n" : "NO\n")); 
                    }
                }

                else if (!data.equals("")) {
                    parse_manual_input(input_box.getText().trim());
                }
            }
        });
    }

    private static void import_from_file(File file_path) {
        try {
            Scanner input = new Scanner(file_path);

            String imported_states = input.nextLine();
            states = new HashSet<>(Arrays.asList(imported_states.split(",")));

            String imported_alphabets = input.nextLine();
            alphabets = new HashSet<>(Arrays.asList(imported_alphabets.split(",")));

            start_state = input.nextLine();

            String imported_final_states = input.nextLine();
            final_states = new HashSet<>(Arrays.asList(imported_final_states.split(",")));
            
            String imported_transition;
            transition_map = new HashMap<>();
            while (true) {
                imported_transition = input.nextLine();

                if (imported_transition.equalsIgnoreCase("done")) {
                    break;
                }

                String[] transitionParts = imported_transition.split(",");
                String currentState = transitionParts[0].trim();
                String inputSymbol = transitionParts[1].trim();
                String nextState = transitionParts[2].trim();

                transition_map.putIfAbsent(currentState, new HashMap<>());
                transition_map.get(currentState).put(inputSymbol, nextState);
            }

            String imported_test_strings = input.nextLine();
            test_strings = Arrays.asList(imported_test_strings.split(","));
        }

        catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "File not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static Map<String, String> rg_conversion() {
        Map<String, String> regular_grammar = new HashMap<>();

        for (String state : states) {
            StringBuilder sb = new StringBuilder();

            for (String alphabet : alphabets) {
                if (transition_map.containsKey(state) && transition_map.get(state).containsKey(alphabet)) {
                    sb.append(alphabet).append(transition_map.get(state).get(alphabet)).append(OR);
                }
            }

            if (transition_map.containsKey(state) && transition_map.get(state).containsKey("")) {
                sb.append(transition_map.get(state).get("")).append(OR);
            }

            if (final_states.contains(state)) {
                sb.append(EPSILON).append(OR);
            }

            if (sb.length() > 0) {
                sb.setLength(sb.length() - OR.length());
                regular_grammar.put(state, sb.toString());
            }
        }

        return regular_grammar;
    }

    private static boolean check_string(String input, Map<String, String> regular_grammar) {
        Set<String> current_states = new HashSet<>();
        current_states.add(start_state);

        if (input.isEmpty())
            return final_states.contains(start_state);

        for (char c : input.toCharArray()) {
            Set<String> future_states = new HashSet<>();
            for (String state : current_states) {
                if (regular_grammar.containsKey(start_state)) {
                    String[] productions = regular_grammar.get(state).split("\\" + OR);
                    for (String production : productions) {
                        if (production.charAt(0) == c) {
                            future_states.add(production.substring(1));
                        }

                        else if (production.equals(EPSILON) && alphabets.isEmpty()) {
                            future_states.add(state);
                        }
                    }
                }
            }

            current_states = future_states;

            if (current_states.isEmpty()) {
                return false;
            }
        }

        for (String state : current_states) {
            if (final_states.contains(state))
                return true;

            if (final_states.contains(state) == current_states.contains(state))
                return true;
        }
        
        return false;
    }

    private static void clear_io() {
        input_box.setText("");
        output_box.setText("");
        states = null;
        alphabets = null;
        start_state = null;
        final_states = null;
        test_strings = null;
        transition_map = null;
        has_imported = false;
    }

    public static String string_builder (Set<String> target_states) {
        StringBuilder string = new StringBuilder();
        string.append(String.format("F = ")).append(OPEN_BRACKET);
        for (String state : target_states) {
            string.append(String.format("%s, ", state));
        }

        string.deleteCharAt(string.length() - 1);
        string.deleteCharAt(string.length() - 1);
        string.append(CLOSE_BRACKET);
        
        return string.toString();
    }

    public static void parse_manual_input(String input) {
        String[] imported_lines = input.split("\n");

        String imported_states = imported_lines[0];
        states = new HashSet<>(Arrays.asList(imported_states.split(",")));

        String imported_alphabets = imported_lines[1];
        alphabets = new HashSet<>(Arrays.asList(imported_alphabets.split(",")));

        start_state = imported_lines[2];

        String imported_final_states = imported_lines[3];
        final_states = new HashSet<>(Arrays.asList(imported_final_states.split(",")));
    
        String imported_transition;
        int transition_table_start = 4;
        int transition_table_end = imported_lines.length - 2;
        transition_map = new HashMap<>();
        for (int i = transition_table_start; i < transition_table_end; i++) {
            imported_transition = imported_lines[i];

            if (imported_transition.equalsIgnoreCase("done")) {
                break;
            }

            String[] transitionParts = imported_transition.split(",");
            String currentState = transitionParts[0].trim();
            String inputSymbol = transitionParts[1].trim();
            String nextState = transitionParts[2].trim();

            transition_map.putIfAbsent(currentState, new HashMap<>());
            transition_map.get(currentState).put(inputSymbol, nextState);
        }

        String imported_test_strings = imported_lines[imported_lines.length - 1];
        test_strings = Arrays.asList(imported_test_strings.split(","));

        Map<String, String> regular_grammar = rg_conversion();
                    
        output_box.append("Regular Grammar:\n");

        for (String state : regular_grammar.keySet()) {
            output_box.append(String.format("%s %s %s\n", state, ARROW, regular_grammar.get(state)));
        }

        output_box.append("\nThe results after checking the strings are:\n");
        for (String test_string : test_strings) {
            boolean isAccepted = check_string(test_string.trim(), regular_grammar);
            output_box.append(test_string + ": " + (isAccepted ? "OK\n" : "NO\n")); 
        }
    }
}
