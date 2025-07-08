
package devt.login.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import com.google.gson.JsonObject;
import java.awt.event.ActionEvent;

public class ViewSystem extends JPanel { // <-- Extiende JPanel, no JFrame

    private JsonObject loggedInUserData;
    private JsonObject currentCharacterData;

    private JButton btnPlay;
    private JButton btnProfile;
    private JButton btnSettings;
    private JButton btnLogout;

    private ActionListener profileButtonListener;
    private ActionListener logoutButtonListener;

    public ViewSystem(JsonObject userData, JsonObject characterData) {
        this.loggedInUserData = userData;
        this.currentCharacterData = characterData;
        
        setLayout(new GridBagLayout()); // Usa GridBagLayout para un diseño flexible
        setBackground(new Color(15, 15, 15)); // Fondo oscuro
        setBorder(new EmptyBorder(50, 50, 50, 50)); // Padding alrededor del panel

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Espaciado entre componentes
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 0; // Fila 0
        gbc.fill = GridBagConstraints.HORIZONTAL; // Rellena horizontalmente
        gbc.anchor = GridBagConstraints.CENTER; // Centra los componentes

        // Etiqueta de bienvenida
        JLabel welcomeLabel = new JLabel("¡Bienvenido, " + currentCharacterData.get("nombre_personaje").getAsString() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 48));
        welcomeLabel.setForeground(new Color(255, 215, 0)); // Color dorado
        gbc.gridwidth = 1; // Ocupa 1 columna
        add(welcomeLabel, gbc);

        gbc.gridy++; // Pasa a la siguiente fila
        gbc.ipadx = 50; // Padding interno horizontal
        gbc.ipady = 20; // Padding interno vertical
        
        Font buttonFont = new Font("Arial", Font.BOLD, 24);
        Color buttonBg = new Color(50, 100, 150); // Color de fondo de los botones
        Color buttonFg = Color.WHITE; // Color del texto de los botones

        // Botón "Jugar"
        btnPlay = new JButton("Jugar");
        btnPlay.setFont(buttonFont);
        btnPlay.setBackground(buttonBg);
        btnPlay.setForeground(buttonFg);
        btnPlay.setFocusPainted(false); // Quita el borde de foco
        btnPlay.addActionListener(e -> JOptionPane.showMessageDialog(this, "¡Iniciando el juego!", "Jugar", JOptionPane.INFORMATION_MESSAGE));
        add(btnPlay, gbc);

        gbc.gridy++; // Siguiente fila
        // Botón "Perfil"
        btnProfile = new JButton("Perfil");
        btnProfile.setFont(buttonFont);
        btnProfile.setBackground(buttonBg);
        btnProfile.setForeground(buttonFg);
        btnProfile.setFocusPainted(false);
        btnProfile.addActionListener(e -> {
            if (profileButtonListener != null) {
                profileButtonListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "showProfile"));
            }
        });
        add(btnProfile, gbc);

        gbc.gridy++; // Siguiente fila
        // Botón "Configuraciones"
        btnSettings = new JButton("Configuraciones");
        btnSettings.setFont(buttonFont);
        btnSettings.setBackground(buttonBg);
        btnSettings.setForeground(buttonFg);
        btnSettings.setFocusPainted(false);
        btnSettings.addActionListener(e -> JOptionPane.showMessageDialog(this, "Abriendo Configuraciones...", "Configuraciones", JOptionPane.INFORMATION_MESSAGE));
        add(btnSettings, gbc);

        gbc.gridy++; // Siguiente fila
        // Botón "Cerrar Sesión" (con estilo diferente)
        btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setFont(new Font("Arial", Font.BOLD, 20));
        btnLogout.setBackground(new Color(150, 50, 50)); // Rojo
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            if (logoutButtonListener != null) {
                logoutButtonListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "logout"));
            }
        });
        add(btnLogout, gbc);
    }

    /**
     * Añade un ActionListener al botón de Perfil.
     * @param listener El ActionListener a añadir.
     */
    public void addProfileButtonListener(ActionListener listener) {
        this.profileButtonListener = listener;
    }

    /**
     * Añade un ActionListener al botón de Cerrar Sesión.
     * @param listener El ActionListener a añadir.
     */
    public void addLogoutButtonListener(ActionListener listener) {
        this.logoutButtonListener = listener;
    }
}