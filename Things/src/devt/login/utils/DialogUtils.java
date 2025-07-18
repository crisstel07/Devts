package devt.login.utils; // Asegúrate de que el paquete sea correcto

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URL; // Necesario para cargar imágenes desde recursos

public class DialogUtils {

    // Enum para tipos de mensaje con colores y rutas de iconos
    public enum MessageType {
        SUCCESS(new Color(50, 168, 82), "/devt/login/images/success.png"),
        ERROR(new Color(220, 53, 69), "/devt/login/images/error.png"),
        INFO(new Color(0, 123, 255), "/devt/login/images/info.png"),
        WARNING(new Color(255, 193, 7), "/devt/login/images/warning.png");

        private final Color color;
        private final String iconPath;

        MessageType(Color color, String iconPath) {
            this.color = color;
            this.iconPath = iconPath;
        }

        public Color getColor() {
            return color;
        }

        public String getIconPath() {
            return iconPath;
        }
    }

    /**
     * Muestra un diálogo de mensaje personalizado flotante que se cierra automáticamente.
     * Este método NO INTERFIERE con el LayeredPane de tu JFrame principal.
     * @param parentComponent El componente padre para el diálogo (usualmente el JFrame principal o un JPanel).
     * Se usa para posicionar el diálogo.
     * @param type El tipo de mensaje (SUCCESS, ERROR, INFO, WARNING).
     * @param message El texto del mensaje a mostrar.
     * @param durationMs La duración en milisegundos antes de que el diálogo se cierre automáticamente (ej. 2000 para 2 segundos).
     */
    public static void showFloatingMessage(java.awt.Component parentComponent, MessageType type, String message, int durationMs) {
        // Asegurarse de que se ejecuta en el Event Dispatch Thread (EDT)
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> showFloatingMessage(parentComponent, type, message, durationMs));
            return;
        }

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parentComponent));
        dialog.setUndecorated(true); // Sin bordes ni barra de título
        dialog.setAlwaysOnTop(true); // Siempre encima de otras ventanas
        dialog.setFocusableWindowState(false); // No roba el foco de la ventana principal

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(type.getColor()); // Color de fondo según el tipo de mensaje
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(245, 245, 245), 1), // Borde claro
            BorderFactory.createEmptyBorder(10, 15, 10, 15) // Padding interno
        ));
        panel.setPreferredSize(new Dimension(350, 60)); // Tamaño fijo para el mensaje

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5); // Espaciado entre componentes
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        // Icono
        JLabel iconLabel = new JLabel();
        URL iconUrl = DialogUtils.class.getResource(type.getIconPath());
        if (iconUrl != null) {
            ImageIcon originalIcon = new ImageIcon(iconUrl);
            Image scaledImage = originalIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            System.err.println("Advertencia: No se encontró el icono para el tipo de mensaje: " + type.name() + " en " + type.getIconPath());
        }
        panel.add(iconLabel, gbc);

        // Mensaje de texto
        gbc.gridx = 1;
        gbc.weightx = 1.0; // Para que el texto ocupe el espacio restante
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14)); // Fuente negrita para mayor visibilidad
        messageLabel.setForeground(Color.WHITE); // Texto blanco
        panel.add(messageLabel, gbc);

        dialog.setContentPane(panel);
        dialog.pack();

        // Posicionar el diálogo en la parte superior central de la ventana padre
        int x = parentComponent.getX() + (parentComponent.getWidth() - dialog.getWidth()) / 2;
        int y = parentComponent.getY() + 30; // Un poco más abajo del borde superior
        dialog.setLocation(x, y);

        dialog.setVisible(true);

        // Temporizador para cerrar el diálogo automáticamente
        Timer timer = new Timer(durationMs, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose(); // Cierra el diálogo
            }
        });
        timer.setRepeats(false); // Solo se ejecuta una vez
        timer.start();
    }
}
