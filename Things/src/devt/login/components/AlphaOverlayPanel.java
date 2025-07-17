package devt.login.components;

import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class AlphaOverlayPanel extends JPanel {

    private float alpha = 0.0f; // 0.0f es completamente transparente, 1.0f es completamente opaco

    public AlphaOverlayPanel() {
        setOpaque(false); // Es importante que el panel no sea opaco para que se pueda ver a través de él
        setBackground(Color.BLACK); // El color del fundido será negro
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        // Asegurarse de que el valor de alpha esté entre 0.0f y 1.0f
        if (alpha < 0.0f) {
            alpha = 0.0f;
        } else if (alpha > 1.0f) {
            alpha = 1.0f;
        }
        this.alpha = alpha;
        repaint(); // Vuelve a dibujar el panel para aplicar el nuevo valor de alpha
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create(); // Crea una copia del contexto gráfico

        // Aplica el AlphaComposite para controlar la transparencia
        // Tu versión: g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
        // Ambas son válidas, mantendré la que te di para consistencia.
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Dibuja un rectángulo del tamaño del panel con el color de fondo (negro)
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.dispose(); // Libera los recursos gráficos
    }
}
