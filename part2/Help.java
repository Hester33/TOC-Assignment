package part2;

import javax.swing.*;

public class Help {
    JPanel panel = new JPanel();

    Help() {
    	
    	JLabel cyk = new JLabel(" CYK:");
    	cyk.setSize(18, 18);
    	
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentY(0);
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

