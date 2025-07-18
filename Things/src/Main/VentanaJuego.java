package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import com.google.gson.JsonObject;

public class VentanaJuego extends JLayeredPane {
    
    public static String titulo = "VEILWALKER - DEMO";
    
    private PanelJuego panelJuego;
    private JPanel overlayMenuPanel;
    private GameNavigationCallback callback;
    private JsonObject initialCharacterData;

    private JButton resumeButton;

    public interface GameNavigationCallback {
        void goToMainMenu();
        void goToProfile();
    }

    public VentanaJuego(GameNavigationCallback callback, JsonObject characterData) {
        this.callback = callback;
        this.initialCharacterData = characterData;
        
        setOpaque(true);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(PanelJuego.ANCHO, PanelJuego.ALTO));

        initGamePanel();
        initOverlayMenuPanel();

        setFocusable(true); // ¡IMPORTANTE! VentanaJuego debe ser focusable
        // Desactivar las teclas de navegación de foco por defecto (como TAB)
        setFocusTraversalKeysEnabled(false); 

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("VentanaJuego - Tecla presionada: " + KeyEvent.getKeyText(e.getKeyCode()));
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    togglePause();
                }
            }
        });

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                panelJuego.setBounds(0, 0, getWidth(), getHeight());
                overlayMenuPanel.setBounds(0, 0, getWidth(), getHeight());
            }
        });
        
        // Listener para depurar cambios de foco
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(
            "focusOwner",
            (evt) -> {
                System.out.println("FOCO CAMBIADO A: " + evt.getNewValue());
            }
        );
    }

    private void initGamePanel() {
        panelJuego = new PanelJuego(initialCharacterData);
        panelJuego.setBounds(0, 0, PanelJuego.ANCHO, PanelJuego.ALTO);
        add(panelJuego, JLayeredPane.DEFAULT_LAYER);
        panelJuego.setFocusable(true); // Asegúrate de que PanelJuego también sea focusable
        // Desactivar las teclas de navegación de foco por defecto en PanelJuego
        panelJuego.setFocusTraversalKeysEnabled(false); 
    }

    private void initOverlayMenuPanel() {
        overlayMenuPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 180));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlayMenuPanel.setBounds(0, 0, PanelJuego.ANCHO, PanelJuego.ALTO);
        overlayMenuPanel.setOpaque(false);
        overlayMenuPanel.setVisible(false);
        add(overlayMenuPanel, JLayeredPane.POPUP_LAYER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel titleLabel = new JLabel("Juego Pausado");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        overlayMenuPanel.add(titleLabel, gbc);

        resumeButton = createMenuButton("Reanudar Juego");
        resumeButton.addActionListener(e -> resumeGame());
        overlayMenuPanel.add(resumeButton, gbc);

        JButton profileButton = createMenuButton("Perfil");
        profileButton.addActionListener(e -> {
            if (callback != null) {
                stopGame(); // Detener el juego al cambiar de pantalla
                callback.goToProfile();
            }
        });
        overlayMenuPanel.add(profileButton, gbc);

        JButton mainMenuButton = createMenuButton("Menú Principal");
        mainMenuButton.addActionListener(e -> {
            if (callback != null) {
                stopGame(); // Detener el juego al cambiar de pantalla
                callback.goToMainMenu();
            }
        });
        overlayMenuPanel.add(mainMenuButton, gbc);

        JButton exitButton = createMenuButton("Salir del Juego");
        exitButton.addActionListener(e -> {
            System.exit(0);
        });
        overlayMenuPanel.add(exitButton, gbc);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(50, 50, 50, 200));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(250, 60));
        return button;
    }

    public void startGame() {
        System.out.println("VentanaJuego: Iniciando juego...");
        panelJuego.iniciarJuego(); // Esto ya llama requestFocusInWindow() en PanelJuego
        panelJuego.setGamePaused(false);
        panelJuego.requestFocusInWindow(); // Asegura el foco al PanelJuego
        Controles.Teclado.desbloquear(); // Asegura que el teclado esté desbloqueado al iniciar
        System.out.println("VentanaJuego: Juego iniciado y foco solicitado para PanelJuego.");
    }

    public void stopGame() {
        System.out.println("VentanaJuego: Deteniendo juego...");
        if (panelJuego != null) {
            panelJuego.stopGameThread();
            panelJuego.setGamePaused(false);
            Controles.Teclado.desbloquear(); // Asegura que el teclado se desbloquee al detener
            System.out.println("VentanaJuego: Hilo de juego detenido y teclado desbloqueado.");
        }
        overlayMenuPanel.setVisible(false);
    }

    private void togglePause() {
        boolean isCurrentlyPaused = panelJuego.isGamePaused();
        System.out.println("VentanaJuego: Alternando pausa. Estado actual: " + isCurrentlyPaused);
        panelJuego.setGamePaused(!isCurrentlyPaused);
        overlayMenuPanel.setVisible(!isCurrentlyPaused);

        if (!isCurrentlyPaused) { // Si se acaba de pausar
            System.out.println("VentanaJuego: Juego pausado. Bloqueando teclado y dando foco a botón.");
            if (resumeButton != null) {
                resumeButton.requestFocusInWindow(); // Foco al botón de reanudar
            }
            Controles.Teclado.bloquear(); // Bloquear el teclado del juego al pausar
        } else { // Si se acaba de reanudar
            System.out.println("VentanaJuego: Juego reanudado. Desbloqueando teclado y dando foco a PanelJuego.");
            panelJuego.requestFocusInWindow(); // Devolver foco al juego
            Controles.Teclado.desbloquear(); // Desbloquear el teclado del juego al reanudar
        }
    }

    private void resumeGame() {
        System.out.println("VentanaJuego: Reanudando juego desde botón.");
        togglePause();
    }

    public PanelJuego getPanelJuego() {
        return panelJuego;
    }

    public void updateCharacterData(JsonObject newCharacterData) {
        if (panelJuego != null) {
            panelJuego.setCharacterData(newCharacterData);
        }
    }
}
