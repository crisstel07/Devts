package devt.login.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import com.google.gson.JsonObject;

public class ViewSystem extends JPanel {

    private JsonObject loggedInUserData;
    private JsonObject currentCharacterData;

    private JLabel welcomeLabel;
    private JButton btnPlay;
    private JButton btnProfile;
    private JButton btnSettings;
    private JButton btnLogout;

    private Image backgroundImage;
    private Font newboroughFont; // Variable para la fuente Newborough
    private Font pixelateFont;   // Variable para la fuente Pixelate

    public ViewSystem(JsonObject userData, JsonObject characterData) {
        this.loggedInUserData = userData;
        this.currentCharacterData = characterData;
        
        // Cargar las fuentes personalizadas
        // ¡IMPORTANTE! Asegúrate de que los archivos sean .ttf o .otf y que los nombres coincidan exactamente.
        // Renombra tus archivos de fuente a "Newborough.ttf" y "pixelate.ttf" en la carpeta fonts
        newboroughFont = loadCustomFont("PlayfairDisplay-Italic.ttf", 90f); // Tamaño para que resalte más
        pixelateFont = loadCustomFont("pixelates.ttf", 20f);     // Tamaño para botones

        // Diagnóstico de fuentes después de la carga
        System.out.println("--- Diagnóstico de Fuentes ---");
        System.out.println("Newborough Font cargada: " + (newboroughFont != null ? newboroughFont.getFontName() : "NULL"));
        System.out.println("Pixelate Font cargada: " + (pixelateFont != null ? pixelateFont.getFontName() : "NULL"));
        System.out.println("--- Fin Diagnóstico ---");


        // Cargar la imagen de fondo
        try {
            URL imageUrl = getClass().getResource("/devt/login/images/backup.png"); // Usando "backup.png" como indicaste
            if (imageUrl != null) {
                backgroundImage = new ImageIcon(imageUrl).getImage();
            } else {
                System.err.println("Error: No se encontró la imagen de fondo en la ruta: /devt/login/images/backup.png");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen de fondo del menú principal: " + e.getMessage());
        }

        setLayout(new GridBagLayout());
        setOpaque(false);
        // Ajustado el padding superior del panel para bajar todo el contenido un poco
        setBorder(new EmptyBorder(50, 50, 50, 50)); // Top, Left, Bottom, Right

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Etiqueta de bienvenida - Usando Newborough
        welcomeLabel = new JLabel();
        welcomeLabel.setFont(newboroughFont); 
        welcomeLabel.setForeground(new Color(255, 215, 0)); // Color dorado
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER); // Centrar el texto
        // Espaciado solo para la bienvenida, para separarla de los botones
        gbc.insets = new Insets(0, 0, 30, 0); // Top, Left, Bottom, Right - Espacio debajo del título
        gbc.weighty = 0; // Este componente no absorbe espacio vertical
        gbc.gridwidth = 1;
        add(welcomeLabel, gbc);

        // --- Pegamento vertical para empujar los botones hacia abajo ---
        gbc.gridy++;
        // CAMBIO AQUÍ: Reducido de 0.7 a 0.6 para subir los botones
        gbc.weighty = 0.4; // Este componente absorberá el 60% del espacio vertical disponible
        gbc.insets = new Insets(0,0,0,0); // Sin insets para el pegamento
        add(Box.createVerticalGlue(), gbc);
        // --- FIN PEGAMENTO ---

        // Reiniciar insets y weighty para los botones para unirlos al máximo
        gbc.insets = new Insets(0, 0, 0, 0); // Espaciado vertical CERO
        gbc.weighty = 0; // Los botones no tienen peso vertical
        gbc.gridy++; // Pasa a la siguiente fila (después del pegamento)
        gbc.ipadx = 60; // Padding interno horizontal aumentado para botones más anchos
        gbc.ipady = 10; // Padding interno vertical reducido

        // Botones - Usando Pixelate
        btnPlay = createStyledButton("Jugar", pixelateFont);
        btnPlay.addActionListener(e -> JOptionPane.showMessageDialog(this, "¡Iniciando el juego!", "Jugar", JOptionPane.INFORMATION_MESSAGE));
        add(btnPlay, gbc);

        gbc.gridy++; // Siguiente fila
        btnProfile = createStyledButton("Perfil", pixelateFont);
        add(btnProfile, gbc);

        gbc.gridy++; // Siguiente fila
        btnSettings = createStyledButton("Configuraciones", pixelateFont);
        btnSettings.addActionListener(e -> JOptionPane.showMessageDialog(this, "Abriendo Configuraciones...", "Configuraciones", JOptionPane.INFORMATION_MESSAGE));
        add(btnSettings, gbc);

        gbc.gridy++; // Siguiente fila
        btnLogout = createStyledButton("Cerrar Sesión", pixelateFont); 
        add(btnLogout, gbc);
        
        // Añadir un "pegamento" vertical al final para empujar todo hacia arriba si hay espacio
        gbc.gridy++;
        // CAMBIO AQUÍ: Aumentado de 0.3 a 0.4 para subir los botones
        gbc.weighty = 0.4; // Este componente absorberá el 40% del espacio restante
        add(Box.createVerticalGlue(), gbc);

        updateWelcomeMessage();
    }

    /**
     * Método auxiliar para crear botones con el estilo deseado (transparente, borde, rollover).
     */
    private JButton createStyledButton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);

        LineBorder defaultBorder = new LineBorder(new Color(100, 100, 100, 100), 2, true);
        LineBorder hoverBorder = new LineBorder(new Color(255, 215, 0, 200), 2, true);
        button.setBorder(defaultBorder);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBorder(hoverBorder);
                button.setForeground(new Color(255, 215, 0));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBorder(defaultBorder);
                button.setForeground(Color.WHITE);
            }
        });
        return button;
    }

    /**
     * Carga una fuente personalizada desde los recursos del proyecto.
     * @param fontFileName El nombre del archivo de la fuente (ej. "Newborough.ttf").
     * @param defaultSize El tamaño por defecto a aplicar si la fuente se carga.
     * @return La fuente cargada o una fuente de respaldo si hay un error.
     */
    private Font loadCustomFont(String fontFileName, float defaultSize) {
        try {
            URL fontUrl = getClass().getResource("/devt/login/fonts/" + fontFileName);
            if (fontUrl != null) {
                Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(customFont);
                System.out.println("DEBUG: Fuente cargada exitosamente: " + fontFileName + " (Family: " + customFont.getFamily() + ", Name: " + customFont.getFontName() + ")");
                return customFont.deriveFont(defaultSize); // Aplica el tamaño por defecto
            } else {
                System.err.println("ERROR: No se encontró la fuente " + fontFileName + " en /devt/login/fonts/. Usando fuente de respaldo (Dialog Bold).");
                return new Font("Dialog", Font.BOLD, (int) defaultSize); // Fallback a Dialog Bold para que resalte
            }
        } catch (FontFormatException | IOException e) {
            System.err.println("ERROR al cargar la fuente " + fontFileName + ": " + e.getMessage() + ". Usando fuente de respaldo (Dialog Bold).");
            return new Font("Dialog", Font.BOLD, (int) defaultSize); // Fallback a Dialog Bold
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(15, 15, 15));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public void updateUserData(JsonObject userData) {
        this.loggedInUserData = userData;
        updateWelcomeMessage();
    }

    public void updateCharacterData(JsonObject characterData) {
        this.currentCharacterData = characterData;
        updateWelcomeMessage();
    }

    private void updateWelcomeMessage() {
        String username = loggedInUserData != null && loggedInUserData.has("nombre_usuario") && !loggedInUserData.get("nombre_usuario").isJsonNull() ? loggedInUserData.get("nombre_usuario").getAsString() : "Usuario";
        String characterName = currentCharacterData != null && currentCharacterData.has("nombre_personaje") && !currentCharacterData.get("nombre_personaje").isJsonNull() ? currentCharacterData.get("nombre_personaje").getAsString() : "Personaje";
        String welcomeText = "¡Bienvenido, " + username + " (" + characterName + ")!";
        welcomeLabel.setText(welcomeText);
        System.out.println("DEBUG: Texto de bienvenida establecido: '" + welcomeText + "' con fuente: " + welcomeLabel.getFont().getFontName() + ", tamaño: " + welcomeLabel.getFont().getSize());
        // Forzar revalidación y repintado para asegurar que los cambios de fuente/texto se apliquen
        welcomeLabel.revalidate();
        welcomeLabel.repaint();
    }

    public void addShowProfileListener(ActionListener listener) {
        this.btnProfile.addActionListener(listener);
    }

    public void addLogoutButtonListener(ActionListener listener) {
        this.btnLogout.addActionListener(listener);
    }
    
    public void addPlayButtonListener(ActionListener listener) {
        this.btnPlay.addActionListener(listener);
    }
    
    public void addSettingsButtonListener(ActionListener listener) {
        this.btnSettings.addActionListener(listener);
    }
}
