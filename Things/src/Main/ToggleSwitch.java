package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ToggleSwitch extends JPanel {
    private boolean estado = true;
    private String iconoTexto = " ";

    public ToggleSwitch(String icono) {
        this.iconoTexto = icono;
        setPreferredSize(new Dimension(110, 36));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                estado = !estado;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fondo = estado ? new Color(96, 96, 100) : new Color(36, 38, 42);
        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

        int deslizadorX = estado ? getWidth() - 44 : 4;
        g2.setColor(new Color(230, 230, 230));
        g2.fillRoundRect(deslizadorX, 4, 40, getHeight() - 8, 20, 20);

        g2.setFont(new Font("Dialog", Font.PLAIN, 16));
        g2.setColor(new Color(36, 38, 42));
        g2.drawString(iconoTexto, 12, 22);

        g2.setFont(new Font("Press Start 2P", Font.PLAIN, 10));
        String texto = estado ? "ON" : "OFF";
        FontMetrics fm = g2.getFontMetrics();
        int tx = deslizadorX + (40 - fm.stringWidth(texto)) / 2;
        int ty = (getHeight() + fm.getAscent()) / 2 - 6;
        g2.drawString(texto, tx, ty);

        g2.dispose();
    }

    public boolean isOn() {
        return estado;
    }
}


