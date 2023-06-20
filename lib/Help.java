package lib;

import javax.swing.*;

public class Help {
    JPanel panel = new JPanel();

    Help() {
        JLabel fa_to_rg = new JLabel(" Fa to RG:");
        fa_to_rg.setSize(18, 18);

    	JLabel cyk = new JLabel(" CYK:");
    	cyk.setSize(18, 18);
    	
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentY(0);

        panel.add(fa_to_rg);
        panel.add(new JLabel("  1. Import a text file."));
        panel.add(new JLabel("              OR"));
        panel.add(new JLabel("  1. Type in the input box."));
        panel.add(new JLabel("     -  States (use ',' to separate the states)"));
        panel.add(new JLabel("     -  Alphabets (use ',' to seperate the alphabet)"));
        panel.add(new JLabel("     -  Initial state"));
        panel.add(new JLabel("     -  Final states (use ',' to separate the final states)"));
        panel.add(new JLabel("     -  Transition table start (use ',' to separate the present state, input, next state)"));
        panel.add(new JLabel("         eg. A,1,B"));
        panel.add(new JLabel("     -  The rest of the transition table (use same syntax as fifth line)"));
        panel.add(new JLabel("     -  Transition table end (use 'Done' to signal the end of the transition table)"));
        panel.add(new JLabel("     -  Strings to be checked for validity (use ',' to separate the strings)"));
        panel.add(new JLabel("  3. Click the \"Generate Output\" button"));
        panel.add(new JLabel("  4. The results will be displayed in the output field"));

        panel.add(new JLabel(" "));

        panel.add(cyk);
        panel.add(new JLabel("  1. Import a text file."));
        panel.add(new JLabel("              OR"));
        panel.add(new JLabel("  1. Type in the input box."));
        panel.add(new JLabel("     -  First line: Starting symbol (eg. S)"));
        panel.add(new JLabel("     -  Second line: Terminals (use 'space' to seperate the alphabet)"));
        panel.add(new JLabel("     -  Third line: Non-terminals (use 'space' to seperate the variables)"));
        panel.add(new JLabel("     -  Fourth line and all following lines: Production rules (use 'space' to represent -> and \"|\")"));
        panel.add(new JLabel("         eg. A a YE XC (A -> a|YE|XC)"));
        panel.add(new JLabel("  2. Enter the String to be tested"));
        panel.add(new JLabel("  3. Press on \"Check\" button"));
        panel.add(new JLabel("  4. The result and CYK table will display in the output field"));
    }
}

