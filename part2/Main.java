package part2;

import javax.swing.*;
import java.awt.event.*;

public class Main extends JFrame implements KeyListener {
    Main() {
        // add listener for keyboard input
        addKeyListener(this);
        setFocusable(true);
        JFrame mainFrame = new JFrame();
        HomePage home = new HomePage();

        CYK cyk = new CYK();
        part_one_gui fa_to_rg_gui = new part_one_gui();
        Help help = new Help();


        // Layout setup

        // Content setup
        JTabbedPane tabbedPane = new JTabbedPane();
        mainFrame.add(tabbedPane);
        tabbedPane.add("Home", home.panel);

        tabbedPane.add("CYK", cyk.panel);
        tabbedPane.add("FA to RG", fa_to_rg_gui.panel);
        tabbedPane.add("Help", help.panel);


        // Window setup
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        mainFrame.setTitle("TOC Assignment");
    }

    public static void main(String[] args) {
        new Main();
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        // Close program on esc key pressed
        if (e.getKeyCode() == 27) // code: 27 for Escape key
            System.exit(0);
    }

    public void keyTyped(KeyEvent e) {
    }
}

