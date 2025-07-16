
package devt.login.components;


import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color; // Importa Color

public class AlphaOverlayPanel extends JPanel {
 
    private float alpha = 0.0f;

    public AlphaOverlayPanel() {
        setOpaque(false); // Importante para que AlphaComposite funcione correctamente
        setBackground(Color.BLACK); // Color de fondo por defecto para la superposición
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        repaint();
    }
}
