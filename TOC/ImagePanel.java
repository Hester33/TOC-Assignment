package TOC;
import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private Image image;

    public ImagePanel(String fileName) {
        this.image = Toolkit.getDefaultToolkit().getImage(fileName);
    }

    public void paint(Graphics g) {
        g.drawImage(this.image, 0, 0, this.getWidth(), this.getHeight(), this);
    }
}
