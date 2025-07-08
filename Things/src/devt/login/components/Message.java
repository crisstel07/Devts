package devt.login.components;

import java.awt.AlphaComposite;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.FontMetrics;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;


public class Message extends javax.swing.JPanel {
    
    private JLabel lblMessage; 
    private MessageType messageType; // Necesitas esta variable
    private boolean show; // Necesitas esta variable para la animación en LoginBase

    public Message() {
        // Asumo que tienes un layout y añades lblMessage aquí
        // Por ejemplo:
        setLayout(new java.awt.BorderLayout());
        lblMessage = new JLabel();
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        lblMessage.setForeground(Color.WHITE); // Color del texto
        lblMessage.setFont(new java.awt.Font("sansserif", 1, 14)); // Fuente del texto
        add(lblMessage, java.awt.BorderLayout.CENTER);

        setOpaque(false);
        setVisible(false);
    }

    // Tu método showMessage actual, modificado para incluir INFO
    public void showMessage(MessageType messageType, String message) {
        this.messageType = messageType; // Guarda el tipo de mensaje
        lblMessage.setText(message);
        if (messageType == MessageType.SUCCESS) {
            lblMessage.setIcon(new ImageIcon(getClass().getResource("/devt/login/images/success.png")));
            setBackground(MessageType.SUCCESS.getColor()); // Establece el color de fondo del JPanel
        } else if (messageType == MessageType.ERROR) { // Añadir else if para ERROR
            lblMessage.setIcon(new ImageIcon(getClass().getResource("/devt/login/images/error.png")));
            setBackground(MessageType.ERROR.getColor()); // Establece el color de fondo del JPanel
        } else if (messageType == MessageType.INFO) { // ¡NUEVO! Para el tipo INFO
            lblMessage.setIcon(new ImageIcon(getClass().getResource("/devt/login/images/info.png"))); // Necesitas un ícono info.png
            setBackground(MessageType.INFO.getColor()); // Establece el color de fondo del JPanel
        }
        this.show = false; // Se establece en false al inicio de la animación de aparición
        setVisible(true);
    }
    
     public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }
   

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblMessage = new javax.swing.JLabel();

        lblMessage.setFont(new java.awt.Font("Segoe UI Emoji", 0, 14)); // NOI18N
        lblMessage.setForeground(new java.awt.Color(255, 255, 255));
        lblMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMessage.setText("Message");
        lblMessage.setPreferredSize(new java.awt.Dimension(300, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

     @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        if (messageType == MessageType.SUCCESS) {
            g2.setColor(new Color(15, 174, 37));
        } else {
            g2.setColor(new Color(240, 52, 53));
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setColor(new Color(245, 245, 245));
        g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        super.paintComponent(grphcs);
    }

   public static enum MessageType {
        SUCCESS(new Color(50, 168, 82)), // Verde para éxito
        ERROR(new Color(220, 53, 69)),   // Rojo para error
        INFO(new Color(0, 123, 255));    // Azul para información (¡NUEVO!)

        private final Color color;

        private MessageType(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblMessage;
    // End of variables declaration//GEN-END:variables
}
}
