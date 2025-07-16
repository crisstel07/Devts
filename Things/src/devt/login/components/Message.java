package devt.login.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D; // Importar para rectángulos redondeados
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Message extends JPanel {
    
    // Atributos de la clase Message
    private MessageType messageType;
    private boolean show;

    public Message() {
        // Establecer un tamaño preferido para el panel Message
        // Esto ayuda a LoginBase a calcular su posición y tamaño correctamente
        setPreferredSize(new Dimension(300, 40)); // Ancho 300, Alto 40 (ajustable)
        
        initComponents(); 
        
        // Configuración inicial del panel
        setOpaque(false); // Hace que el panel sea transparente por defecto
        setVisible(false); // Oculta el panel por defecto
    }

    /**
     * Muestra un mensaje con un tipo y texto específicos.
     * @param messageType El tipo de mensaje (SUCCESS, ERROR, INFO, WARNING).
     * @param message El texto del mensaje a mostrar.
     */
    public void showMessage(MessageType messageType, String message) {
        this.messageType = messageType; // Guarda el tipo de mensaje para paintComponent
        lblMessage.setText(message);
        lblMessage.setForeground(Color.WHITE); // Asegura que el texto siempre sea blanco

        // Escalar y establecer el icono según el tipo de mensaje
        ImageIcon originalIcon = null;
        if (messageType == MessageType.SUCCESS) {
            originalIcon = new ImageIcon(getClass().getResource("/devt/login/images/success.png"));
        } else if (messageType == MessageType.ERROR) {
            originalIcon = new ImageIcon(getClass().getResource("/devt/login/images/error.png"));
        } else if (messageType == MessageType.INFO) {
            originalIcon = new ImageIcon(getClass().getResource("/devt/login/images/info.png"));
        } else if (messageType == MessageType.WARNING) {
            originalIcon = new ImageIcon(getClass().getResource("/devt/login/images/warning.png")); // Asumo que tienes un warning.png
        }
        
        if (originalIcon != null) {
            // Escalar la imagen a un tamaño pequeño, por ejemplo 24x24 píxeles
            Image scaledImage = originalIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            lblMessage.setIcon(new ImageIcon(scaledImage));
        } else {
            lblMessage.setIcon(null); // No hay icono si no se encuentra o tipo desconocido
        }
        
        this.show = true; // Se establece en true al inicio de la animación de aparición
        setVisible(true); // Hace visible el panel
        revalidate(); // Revalida el layout para que el tamaño del JLabel se ajuste
        repaint(); // Asegura que paintComponent se llame para dibujar el fondo correcto
    }
    
    /**
     * Obtiene el estado actual de visibilidad de la animación.
     * @return true si el mensaje se está mostrando, false en caso contrario.
     */
    public boolean isShow() {
        return show;
    }

    /**
     * Establece el estado de visibilidad para la animación.
     * @param show true para mostrar, false para ocultar.
     */
    public void setShow(boolean show) {
        this.show = show;
    }
    
    /**
     * Método generado por el diseñador de NetBeans para inicializar componentes.
     * No modificar manualmente este bloque.
     */
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

   /**
     * Método para dibujar el componente. Se encarga de pintar el fondo y el borde.
     * @param grphcs El contexto gráfico.
     */
    /**
     * Método para dibujar el componente. Se encarga de pintar el fondo y el borde.
     * @param grphcs El contexto gráfico.
     */
    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        // Habilita el anti-aliasing para un dibujo más suave
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Usar el color definido en el enum para el fondo
        if (messageType != null) {
            g2.setColor(messageType.getColor());
        } else {
            g2.setColor(Color.GRAY); // Color por defecto si messageType es nulo
        }
        
        // Establece la composición para la transparencia del fondo
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f)); // 90% de opacidad
        
        // Dibuja un rectángulo redondeado para el fondo del mensaje
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        
        // Restaura la composición para que el texto no sea transparente
        g2.setComposite(AlphaComposite.SrcOver);
        
        g2.setColor(new Color(245, 245, 245)); // Color del borde (gris claro)
        // Dibuja el borde redondeado
        g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
        
        super.paintComponent(grphcs); // Llama al método de la superclase para dibujar los componentes hijos (lblMessage)
    }

    /**
     * Enumeración para definir los tipos de mensajes y sus colores asociados.
     */
    public static enum MessageType {
        SUCCESS(new Color(50, 168, 82)),      // Verde para éxito
        ERROR(new Color(220, 53, 69)),        // Rojo para error
        INFO(new Color(0, 123, 255)),         // Azul para información
        WARNING(new Color(255, 193, 7));      // Amarillo/Naranja para advertencia
        
        private final Color color; // Campo para almacenar el color

        // Constructor del enum
        private MessageType(Color color) {
            this.color = color;
        }

        // Método para obtener el color asociado al tipo de mensaje
        public Color getColor() {
            return color;
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblMessage;
    // End of variables declaration//GEN-END:variables
}
