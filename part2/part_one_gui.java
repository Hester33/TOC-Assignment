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
import java.util.*;

public class part_one_gui {
    JPanel panel = new JPanel();

    private JPanel button_panel;
    private JButton import_button = new JButton("Import");
    private JButton clear_button = new JButton("Clear");
    private JButton generate_button = new JButton("Generate RG");
    
    private JPanel input_panel;
    private static JTextArea input_box;
    private JLabel input_label;
    private JScrollPane input_scroll;
    private JFileChooser input_file_browser;
    
    private JPanel output_panel;
    private JTextArea output_box;
    private JLabel output_label;
    private JScrollPane output_scroll;

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

    part_one_gui() {
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
                input_file_browser = new JFileChooser();
                
                int return_value = input_file_browser.showOpenDialog(null);
                if (return_value == JFileChooser.APPROVE_OPTION) {
                    File file_path = input_file_browser.getSelectedFile();
                    import_from_file(file_path);
                }

                StringBuilder states_string = new StringBuilder();
                states_string.append("Q = ").append(OPEN_BRACKET);
                for (String state : states) {
                    states_string.append(String.format("%s, ", state));
                }
                states_string.deleteCharAt(states_string.length() - 1);
                states_string.deleteCharAt(states_string.length() - 1);
                states_string.append(CLOSE_BRACKET);

                StringBuilder alphabets_string = new StringBuilder();
                alphabets_string.append("E = ").append(OPEN_BRACKET);
                for (String alphabet : alphabets) {
                    alphabets_string.append(String.format("%s, ", alphabet));
                }
                alphabets_string.deleteCharAt(alphabets_string.length() - 1);
                alphabets_string.deleteCharAt(alphabets_string.length() - 1);
                alphabets_string.append(CLOSE_BRACKET);

                StringBuilder final_states_string = new StringBuilder();
                final_states_string.append(String.format("F = ")).append(OPEN_BRACKET);
                for (String final_state : final_states) {
                    final_states_string.append(String.format("%s, ", final_state));
                }
                final_states_string.deleteCharAt(final_states_string.length() - 1);
                final_states_string.deleteCharAt(final_states_string.length() - 1);
                final_states_string.append(CLOSE_BRACKET);

                input_box.append(states_string.toString() + "\n");
                input_box.append(alphabets_string.toString() + "\n");
                input_box.append(String.format("P0 = %s\n", start_state));
                input_box.append(final_states_string.toString() + "\n");

                input_box.append("\nStrings to be checked:\n");
                for (int i = 0; i < test_strings.size(); i++) {
                    input_box.append(test_strings.get(i) + "\n");
                }
            }
        });

        clear_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });

        generate_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                output_box.append("Regular Grammar:\n");

                Map<String, String> regular_grammar = rg_conversion();

                for (String state : regular_grammar.keySet()) {
                    output_box.append(String.format("%s %s %s\n", state, ARROW, regular_grammar.get(state)));
                }

                output_box.append("\nThe results after checking the strings are:\n");
                for (String test_string : test_strings) {
                    boolean isAccepted = check_string(test_string.trim(), regular_grammar);
                    output_box.append(test_string + ": " + (isAccepted ? "OK\n" : "NO\n")); 
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

    /*private static Set<String> states;
    private static Set<String> alphabets;
    private static String start_state;
    private static Set<String> final_states;
    private static java.util.List<String> test_strings;
    private static Map<String, Map<String, String>> transition_map;*/

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
}