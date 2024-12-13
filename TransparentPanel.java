import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

class TransparentPanel extends JPanel {

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        // Establecer transparencia total (0.0f significa completamente transparente)
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }


    public boolean isOpaque() {
        return false; // Asegurarte de que se considere transparente
    }
}
