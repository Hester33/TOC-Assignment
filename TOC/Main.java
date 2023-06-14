package TOC;
import javax.swing.*;
import java.awt.event.*;

public class Main extends JFrame implements KeyListener {
    Main() {
        // add listener for keyboard input
        addKeyListener(this);
        setFocusable(true);
        JFrame mainFrame = new JFrame();
        Homepage home = new Homepage();
        CFGSwing testing = new CFGSwing(mainFrame);
        JPanel cfgPanel = testing.getPanel(); // Get the CFGSwing panel
//        Help help = new Help();

        // Layout setup

        // Content setup
        JTabbedPane tabbedPane = new JTabbedPane();
        mainFrame.add(tabbedPane);
        tabbedPane.add("Home", home.panel);
        tabbedPane.add("CFG", cfgPanel);
//        tabbedPane.add("Help", help.panel);

        // Window setup
        //mainFrame.setSize(400, 500);
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