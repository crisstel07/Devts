package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import com.google.gson.JsonObject;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.InputStream;

// Importaciones para los paneles de tu amiga (asegúrate de que estas clases existan en sus respectivos paquetes)
import Main.PanelMenuLateral; 
import Main.PanelAyuda; 
import Main.PanelCréditos; 
import devt.login.components.ProfileStatsDisplayPanel;

// Importa tu ViewSystem existente
import devt.login.view.ViewSystem; 

// La clase principal del juego que ahora extiende JPanel y gestiona todo
public class VentanaJuego extends JPanel { // ¡Ahora es un JPanel que lo contiene todo!

    // Dimensiones de la ventana (necesarias para posicionar elementos)
    public static final int ANCHO = 1365;
    public static final int ALTO = 767;

    // Panel principal que usará CardLayout para cambiar entre pantallas (menú, juego, perfil)
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Paneles de contenido principal (ahora son "tarjetas" en el CardLayout)
    private ViewSystem mainMenuPanel; // Tu ViewSystem como menú principal
    private PanelJuego panelJuego;       // El panel donde se ejecuta la lógica del juego
    private ProfileStatsDisplayPanel profilePanel;   // Tu panel de perfil

    // Paneles y componentes para overlays (menú lateral, ayuda, créditos, pausa)
    private JLayeredPane layeredPane; // Usamos un JLayeredPane interno para los overlays
    private PanelMenuLateral panelMenu;
    private JButton btnTuercaFlotante;
    private PanelAyuda panelAyuda;
    private PanelCréditos panelCreditos;
    private JPanel overlayMenuPanel; // Panel para el menú de pausa

    // Dimensiones y posiciones para el menú lateral
    private final int ANCHO_MENU = 270;
    private final int MARGEN_DERECHO = 13;
    private final int POS_MENU_ABIERTO_X = ANCHO - ANCHO_MENU - MARGEN_DERECHO;
    private final int POS_MENU_CERRADO_X = ANCHO + MARGEN_DERECHO;

    // Referencia al botón de reanudar para darle foco en el menú de pausa
    private JButton resumeButton;

    // Datos del usuario y personaje que se pasan desde LoginBase
    private JsonObject loggedInUserData;
    private JsonObject currentCharacterData;

    // Referencia al JFrame padre (LoginBase) para acciones como cerrar la aplicación
    private JFrame parentJFrame; 

    // Constructor de la clase: Ahora acepta los datos del usuario y del personaje, y el JFrame padre
    public VentanaJuego(JsonObject userData, JsonObject characterData, JFrame parentFrame) {
        this.loggedInUserData = userData;
        this.currentCharacterData = characterData;
        this.parentJFrame = parentFrame; // Guarda la referencia al JFrame padre

        // Configuración del JPanel principal (VentanaJuego)
        this.setPreferredSize(new Dimension(ANCHO, ALTO));
        this.setLayout(new BorderLayout()); // Usamos BorderLayout para organizar el layeredPane
        this.setFocusable(true); // Permite que el panel reciba el foco para eventos de teclado
        this.setFocusTraversalKeysEnabled(false); // Desactiva teclas de navegación de foco

        // Inicializar JLayeredPane para superponer paneles
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(ANCHO, ALTO));
        layeredPane.setLayout(null); // Usamos layout nulo para posicionar manualmente los componentes
        this.add(layeredPane, BorderLayout.CENTER); // Añade el layeredPane al centro del JPanel

        // Inicializar CardLayout y contentPanel (este contendrá las pantallas principales)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBounds(0, 0, ANCHO, ALTO);
        layeredPane.add(contentPanel, JLayeredPane.DEFAULT_LAYER); // contentPanel en la capa base

        // 1. Inicializar y añadir tu ViewSystem como la "tarjeta" del menú principal
        // Le pasamos los datos del usuario y personaje, y una referencia a esta VentanaJuego (JPanel)
        mainMenuPanel = new ViewSystem(loggedInUserData, currentCharacterData, this); 
        contentPanel.add(mainMenuPanel, "MainMenu"); // Añade al CardLayout con nombre "MainMenu"

        // 2. Inicializar y añadir el PanelJuego como una "tarjeta"
        panelJuego = new PanelJuego(currentCharacterData); // Pasa los datos iniciales del personaje
        contentPanel.add(panelJuego, "Game"); // Añade al CardLayout con nombre "Game"

        // 3. Inicializar y añadir el Panel de Perfil como una "tarjeta"
        profilePanel = new ProfileStatsDisplayPanel(this); // Pasa esta VentanaJuego (JPanel) para que el perfil pueda interactuar
        contentPanel.add(profilePanel, "Profile"); // Añade al CardLayout con nombre "Profile"

        // 4. Inicializar y añadir el panel del menú de pausa (se superpone en JLayeredPane)
        initOverlayMenuPanel(layeredPane);

        // 5. Inicializar y añadir los paneles de tu amiga (menú lateral, ayuda, créditos)
        initFriendPanels(layeredPane);

        // Configurar KeyListener para este JPanel principal para la pausa
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("VentanaJuego (JPanel) - Tecla presionada: " + KeyEvent.getKeyText(e.getKeyCode()));
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    togglePause(); // Llama al método para pausar/reanudar
                }
            }
        });

        // Listener para depurar cambios de foco a nivel global (muy útil para el teclado)
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(
            "focusOwner",
            new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    System.out.println("--- FOCO CAMBIADO A: " + evt.getNewValue());
                }
            }
        );

        // Listener para redimensionamiento (asegura que los paneles se ajusten)
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                // Ajusta el contentPanel y los overlays al tamaño actual del JPanel
                contentPanel.setBounds(0, 0, getWidth(), getHeight());
                layeredPane.setBounds(0, 0, getWidth(), getHeight()); // Asegura que el layeredPane ocupe todo
                overlayMenuPanel.setBounds(0, 0, getWidth(), getHeight());
                panelAyuda.setBounds(0, 0, getWidth(), getHeight());
                panelCreditos.setBounds(0, 0, getWidth(), getHeight());
                panelMenu.setBounds(panelMenu.getX(), 0, ANCHO_MENU, getHeight()); // Ajusta altura del menú lateral
            }
        });

        System.out.println("VentanaJuego (JPanel): Constructor finalizado.");

        // Mostrar el menú principal al inicio
        showMainMenu();
    }

    // Método para inicializar el panel del menú de pausa
    private void initOverlayMenuPanel(JLayeredPane capas) {
        JPanel overlay = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 180)); // Fondo semi-transparente oscuro
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setBounds(0, 0, ANCHO, ALTO);
        overlay.setOpaque(false); // Permitir que paintComponent dibuje la transparencia
        overlay.setVisible(false); // Invisible por defecto
        capas.add(overlay, JLayeredPane.POPUP_LAYER); // Capa para popups/menús
        this.overlayMenuPanel = overlay; // Asigna a la variable de instancia

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Cada componente en una nueva fila
        gbc.fill = GridBagConstraints.HORIZONTAL; // Rellena horizontalmente
        gbc.insets = new Insets(10, 0, 10, 0); // Espaciado entre botones

        JLabel titleLabel = new JLabel("Juego Pausado");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        overlay.add(titleLabel, gbc);

        resumeButton = createMenuButton("Reanudar Juego");
        resumeButton.addActionListener(e -> resumeGame());
        overlay.add(resumeButton, gbc);

        JButton profileButton = createMenuButton("Perfil");
        profileButton.addActionListener(e -> {
            showProfile(); // Ahora lo maneja esta misma VentanaJuego (JPanel)
        });
        overlay.add(profileButton, gbc);

        JButton mainMenuButton = createMenuButton("Menú Principal");
        mainMenuButton.addActionListener(e -> {
            showMainMenu(); // Ahora lo maneja esta misma VentanaJuego (JPanel)
        });
        overlay.add(mainMenuButton, gbc);

        JButton exitButton = createMenuButton("Salir del Juego");
        exitButton.addActionListener(e -> {
            // Como VentanaJuego es un JPanel, la salida final la maneja el JFrame padre (LoginBase)
            // o directamente System.exit(0) si no se espera volver a LoginBase.
            // Para este caso, System.exit(0) es lo más directo.
            System.exit(0); 
        });
        overlay.add(exitButton, gbc);
        System.out.println("VentanaJuego (JPanel): Menú de pausa inicializado y añadido.");
    }

    // Método para inicializar los paneles de tu amiga
    private void initFriendPanels(JLayeredPane capas) {
        // Cargar la fuente para los créditos (debe ser la misma fuente pixelada)
        Font fuentePixel = null;
        try {
            InputStream is = getClass().getResourceAsStream("/img/PressStart2P-Regular.ttf");
            if (is != null) {
                fuentePixel = Font.createFont(Font.TRUETYPE_FONT, is);
            } else {
                System.err.println("Advertencia: No se encontró el recurso de fuente /img/PressStart2P-Regular.ttf en PanelMenuLateral.");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar fuente pixelada en PanelMenuLateral: " + e.getMessage());
            e.printStackTrace();
        }

        // Panel de configuración lateral
        panelMenu = new PanelMenuLateral();
        panelMenu.setBounds(POS_MENU_CERRADO_X, 0, ANCHO_MENU, ALTO);
        panelMenu.setVisible(false); // Oculto inicialmente
        capas.add(panelMenu, JLayeredPane.PALETTE_LAYER); // Una capa para herramientas/menús secundarios

        // Botón flotante tipo tuerca para abrir el menú lateral
        btnTuercaFlotante = new JButton();
        btnTuercaFlotante.setBounds(ANCHO - 50 - MARGEN_DERECHO, 10, 40, 40);
        panelMenu.estilizarBotonIcono(btnTuercaFlotante, "/img/confi.png"); // Aplica estilo personalizado
        btnTuercaFlotante.setVisible(true);
        btnTuercaFlotante.addActionListener(e -> abrirMenu()); // Acción de abrir menú
        capas.add(btnTuercaFlotante, JLayeredPane.MODAL_LAYER); // Capa para elementos interactivos flotantes

        // Conectamos el botón flotante al menú para control externo
        panelMenu.setControlExternamente(btnTuercaFlotante);

        // Creamos el PanelAyuda y lo guardamos como atributo
        panelAyuda = new PanelAyuda(fuentePixel); // Pasa la fuente
        panelAyuda.setBounds(0, 0, ANCHO, ALTO); // Ocupa toda la ventana
        panelAyuda.setVisible(false);
        capas.add(panelAyuda, JLayeredPane.PALETTE_LAYER);
        panelMenu.setPanelAyudaExternamente(panelAyuda); // Enlazamos panel de ayuda

        // Crear e insertar el panel de créditos
        panelCreditos = new PanelCréditos(fuentePixel); // Pasa la fuente
        panelCreditos.setBounds(0, 0, ANCHO, ALTO); // Ocupa toda la ventana
        panelCreditos.setVisible(false); // Inicialmente oculto
        capas.add(panelCreditos, JLayeredPane.PALETTE_LAYER);
        
        // ¡IMPORTANTE! Pasa la referencia de esta VentanaJuego (JPanel) a los paneles de tu amiga
        // para que puedan llamar a los métodos de mostrar/ocultar paneles en esta VentanaJuego.
        panelMenu.setVentanaJuego(this); 
        panelAyuda.setVentanaJuego(this);
      panelCreditos.setVentanaJuego(this);
        
        System.out.println("VentanaJuego (JPanel): Paneles de amiga inicializados y añadidos.");
    }

    // Método auxiliar para crear botones de menú con estilo
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

    // --- Métodos para cambiar de pantalla ---

    public void showMainMenu() {
        System.out.println("VentanaJuego (JPanel): Mostrando Menú Principal (ViewSystem).");
        cardLayout.show(contentPanel, "MainMenu");
        // Detener el juego si estaba corriendo
        if (panelJuego != null) {
            panelJuego.stopGameThread();
            panelJuego.setGamePaused(true); // Asegura que el juego esté pausado si se vuelve al menú
        }
        // Ocultar overlays
        overlayMenuPanel.setVisible(false);
        panelMenu.setVisible(false);
        panelAyuda.setVisible(false);
        panelCreditos.setVisible(false);
        btnTuercaFlotante.setVisible(true); // Asegura que el botón de tuerca esté visible en el menú principal
        
        // Asegurar que el foco esté en el MainMenuPanel (ViewSystem)
        mainMenuPanel.requestFocusInWindow();
        Controles.Teclado.desbloquear(); // Desbloquear teclado para el menú
    }

    // Método para iniciar la pantalla de juego
    public void startGameScreen() { 
        System.out.println("VentanaJuego (JPanel): Iniciando pantalla de juego.");
        cardLayout.show(contentPanel, "Game");
        if (panelJuego != null) {
            panelJuego.iniciarJuego(); // Inicia el hilo del juego en PanelJuego y pide foco
            panelJuego.setGamePaused(false); // Asegura que el juego no esté pausado al iniciar
            
            // Asegura que este JPanel tenga el foco y luego PanelJuego
            this.requestFocusInWindow(); 
            panelJuego.requestFocusInWindow(); 
        }

        Controles.Teclado.desbloquear(); // Asegura que el teclado esté desbloqueado al iniciar el juego
        System.out.println("VentanaJuego (JPanel): Pantalla de juego iniciada, foco solicitado para PanelJuego, teclado desbloqueado.");
    }

    public void showProfile() {
        System.out.println("VentanaJuego (JPanel): Mostrando Perfil.");
        cardLayout.show(contentPanel, "Profile");
        // Detener el juego si estaba corriendo
        if (panelJuego != null) {
            panelJuego.stopGameThread();
            panelJuego.setGamePaused(true);
        }
        // Ocultar overlays
        overlayMenuPanel.setVisible(false);
        panelMenu.setVisible(false);
        panelAyuda.setVisible(false);
        panelCreditos.setVisible(false);
        btnTuercaFlotante.setVisible(true); // Asegura que el botón de tuerca esté visible en el perfil
        
        // Asegurar que el foco esté en el ProfilePanel
        profilePanel.requestFocusInWindow();
        Controles.Teclado.desbloquear(); // Desbloquear teclado para el perfil
    }
    
    // Nuevo método para mostrar el menú de configuraciones (PanelMenuLateral)
    public void showSettingsMenu() {
        System.out.println("VentanaJuego (JPanel): Mostrando menú de configuraciones (PanelMenuLateral).");
        // Si el juego está pausado por el menú ESC, no abrir el menú lateral
        if (panelJuego.isGamePaused() && overlayMenuPanel.isVisible()) { // Verifica si el menú de pausa está activo
            System.out.println("VentanaJuego (JPanel): Juego pausado por ESC, no se abre el menú lateral.");
            return;
        }
        abrirMenu(); // Llama al método existente para deslizar el menú lateral
    }

    // Método para detener el juego completamente (al cerrar la aplicación o ir a una pantalla de login externa)
    public void stopApplication() {
        System.out.println("VentanaJuego (JPanel): Deteniendo aplicación...");
        if (panelJuego != null) {
            panelJuego.stopGameThread(); // Detiene el hilo del juego
            panelJuego.setGamePaused(false); // Asegurarse de que no esté pausado al salir
            Controles.Teclado.desbloquear(); // Asegura que el teclado se desbloquee
            System.out.println("VentanaJuego (JPanel): Hilo de juego detenido y teclado desbloqueado.");
        }
        // Ocultar todos los overlays
        overlayMenuPanel.setVisible(false);
        panelMenu.setVisible(false);
        panelAyuda.setVisible(false);
        panelCreditos.setVisible(false);
        btnTuercaFlotante.setVisible(true);
        
        // Si se necesita volver a LoginBase, se puede llamar a un método en parentJFrame
        // Por ejemplo: if (parentJFrame instanceof LoginBase) ((LoginBase) parentJFrame).goToLoginScreen();
    }

    // Método para alternar el estado de pausa (para tecla ESC)
    private void togglePause() {
        // Solo pausar si la pantalla actual es el juego
        if (cardLayout.getLayoutComponent(contentPanel, "Game") == panelJuego && panelJuego.isVisible()) {
            boolean isCurrentlyPaused = panelJuego.isGamePaused(); // Obtiene el estado actual de pausa de PanelJuego
            System.out.println("VentanaJuego (JPanel): Alternando pausa. Estado actual: " + isCurrentlyPaused);

            if (!isCurrentlyPaused) { // Si se va a pausar
                panelJuego.setGamePaused(true); // Pausa el juego
                overlayMenuPanel.setVisible(true); // Muestra el menú de pausa
                // Ocultar menú lateral si está abierto
                if (panelMenu.isVisible()) {
                    panelMenu.setVisible(false);
                    btnTuercaFlotante.setVisible(true);
                }
                // Ocultar paneles de ayuda/créditos si están abiertos
                panelAyuda.setVisible(false);
                panelCreditos.setVisible(false);

                System.out.println("VentanaJuego (JPanel): Juego pausado. Bloqueando teclado y dando foco a botón.");
                if (resumeButton != null) {
                    resumeButton.requestFocusInWindow(); // ¡IMPORTANTE! Dar foco al botón de reanudar
                }
                Controles.Teclado.bloquear(); // ¡CRÍTICO! Bloquear el teclado del juego
            } else { // Si se va a reanudar
                panelJuego.setGamePaused(false); // Reanuda el juego
                overlayMenuPanel.setVisible(false); // Oculta el menú de pausa

                System.out.println("VentanaJuego (JPanel): Juego reanudado. Desbloqueando teclado y dando foco a PanelJuego.");
                panelJuego.requestFocusInWindow(); // ¡CRÍTICO! Devolver foco al juego
                Controles.Teclado.desbloquear(); // ¡CRÍTICO! Desbloquear el teclado del juego
            }
        } else {
            System.out.println("VentanaJuego (JPanel): ESC presionado, pero el juego no está activo o visible para pausar.");
        }
    }

    // Método para reanudar el juego desde el menú de pausa
    private void resumeGame() {
        System.out.println("VentanaJuego (JPanel): Reanudando juego desde botón.");
        togglePause(); // Simplemente invierte el estado de pausa (oculta el menú y reanuda el juego)
    }

    // Métodos para el menú lateral de tu amiga
    private void abrirMenu() {
        // Si el juego está pausado por el menú ESC, no abrir el menú lateral
        if (panelJuego.isGamePaused() && overlayMenuPanel.isVisible()) { // Verifica si el menú de pausa está activo
            System.out.println("VentanaJuego (JPanel): Intentando abrir menú lateral mientras juego está pausado por ESC. Ignorando.");
            return;
        }
        System.out.println("VentanaJuego (JPanel): Abriendo menú lateral.");
        btnTuercaFlotante.setVisible(false); // Oculta botón cuando se abre el menú
        deslizarMenu(true); // Inicia animación para mostrar menú
        Controles.Teclado.bloquear(); // Bloquear teclado mientras el menú lateral está abierto
    }

    // Método para cerrar el menú lateral con animación
    public void cerrarMenu() {
        System.out.println("VentanaJuego (JPanel): Cerrando menú lateral.");
        deslizarMenu(false); // Inicia animación para ocultar menú
        // Regresa el enfoque al panel principal activo
        Component currentCard = null;
        for (Component comp : contentPanel.getComponents()) {
            if (comp.isVisible() && cardLayout.getLayoutComponent(contentPanel, "") == comp) { // Check if it's the currently displayed card
                currentCard = comp;
                break;
            }
        }

        if (currentCard instanceof JPanel) {
            ((JPanel) currentCard).requestFocusInWindow();
        } else {
            // Fallback si no se encuentra la tarjeta activa o no es un JPanel
            this.requestFocusInWindow();
        }
        Controles.Teclado.desbloquear(); // Desbloquear teclado al cerrar el menú lateral
    }

    // Animación tipo slide para abrir o cerrar menú lateral
    private void deslizarMenu(boolean abrir) {
        int inicio = abrir ? POS_MENU_CERRADO_X : POS_MENU_ABIERTO_X;
        int fin = abrir ? POS_MENU_ABIERTO_X : POS_MENU_CERRADO_X;
        int paso = abrir ? -10 : 10;

        Timer timer = new Timer(10, null);
        timer.addActionListener(e -> {
            int x = panelMenu.getX();
            if ((abrir && x <= fin) || (!abrir && x >= fin)) {
                panelMenu.setBounds(fin, 0, ANCHO_MENU, ALTO);
                timer.stop();
                if (!abrir) btnTuercaFlotante.setVisible(true);
                return;
            }
            panelMenu.setBounds(x + paso, 0, ANCHO_MENU, ALTO);
            panelMenu.repaint();
        });
        timer.start();
        panelMenu.setVisible(true);
    }

    // Mostrar el panel de créditos y ocultar el menú lateral
    public void mostrarCreditos() {
        System.out.println("VentanaJuego (JPanel): Mostrando créditos.");
        panelCreditos.setVisible(true);
        panelMenu.setVisible(false);
        panelAyuda.setVisible(false); // Asegurarse de ocultar ayuda también
        Controles.Teclado.bloquear(); // Bloquear teclado mientras se ven los créditos
        panelCreditos.requestFocusInWindow(); // Dar foco a los créditos
    }

    // Método para ocultar los créditos
    public void ocultarCreditos() {
        System.out.println("VentanaJuego (JPanel): Ocultando créditos.");
        panelCreditos.setVisible(false);
        panelMenu.setVisible(true); // Vuelve a mostrar el menú lateral
        Controles.Teclado.desbloquear(); // Desbloquear teclado al ocultar créditos
        panelMenu.requestFocusInWindow(); // Dar foco al menú lateral
    }

    // Mostrar el panel de ayuda y ocultar el menú lateral
    public void mostrarAyuda() {
        System.out.println("VentanaJuego (JPanel): Mostrando ayuda.");
        panelAyuda.setVisible(true);
        panelMenu.setVisible(false);
        panelCreditos.setVisible(false); // Asegurarse de ocultar créditos también
        Controles.Teclado.bloquear(); // Bloquear teclado mientras se ve la ayuda
        panelAyuda.requestFocusInWindow(); // Dar foco a la ayuda
    }

    // Método para ocultar la ayuda
    public void ocultarAyuda() {
        System.out.println("VentanaJuego (JPanel): Ocultando ayuda.");
        panelAyuda.setVisible(false);
        panelMenu.setVisible(true); // Vuelve a mostrar el menú lateral
        Controles.Teclado.desbloquear(); // Desbloquear teclado al ocultar ayuda
        panelMenu.requestFocusInWindow(); // Dar foco al menú lateral
    }

    // Panel visual de niebla que se activa al cerrar sesión
    public void mostrarNieblaDeDesconexion() {
        System.out.println("VentanaJuego (JPanel): Mostrando niebla de desconexión.");
        JPanel panelFalla = new JPanel() {
            float alpha = 0f;
            Timer timer;
            int glitchOffset = 0;
            final int velocidadAnimacion = 25;
            final int brilloMaximo = 210;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

                int width = getWidth();
                int height = getHeight();

                GradientPaint niebla = new GradientPaint(
                    0, height, new Color(brilloMaximo, brilloMaximo, brilloMaximo, 210),
                    0, 0, new Color(brilloMaximo, brilloMaximo, brilloMaximo, 60)
                );
                g2.setPaint(niebla);
                g2.fillRect(0, 0, width, height);

                g2.setColor(new Color(255, 255, 255, 50));
                for (int i = 0; i < height; i += 8) {
                    int desvio = (int) (Math.random() * 30 - 15);
                    g2.fillRect(desvio, i + glitchOffset % 6, width, 2);
                }

                g2.dispose();
            }

            { // Bloque de inicialización de instancia
                setOpaque(false);
                setLayout(null);
                setBounds(0, 0, ANCHO, ALTO);

                JLabel mensaje = new JLabel("PRÓXIMA PARADA:" + "REALIDAD");
                // Asegúrate de que la fuente "Press Start 2P" esté cargada y disponible
                // O usa una fuente por defecto si no se carga
                Font defaultFont = new Font("Arial", Font.BOLD, 18);
                try {
                    InputStream is = getClass().getResourceAsStream("/img/PressStart2P-Regular.ttf");
                    if (is != null) {
                        defaultFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.BOLD, 18);
                    } else {
                        System.err.println("Advertencia: No se encontró el recurso de fuente /img/PressStart2P-Regular.ttf para el mensaje de desconexión.");
                    }
                } catch (Exception e) {
                    System.err.println("Error al cargar fuente para mensaje de desconexión: " + e.getMessage());
                }
                mensaje.setFont(defaultFont);
                mensaje.setForeground(new Color(60, 60, 60));
                mensaje.setHorizontalAlignment(SwingConstants.CENTER);
                mensaje.setBounds(0, ALTO / 2 + 20, ANCHO, 40);
                add(mensaje);

                timer = new Timer(velocidadAnimacion, evt -> {
                    alpha += 0.035f;
                    glitchOffset += 2;
                    if (alpha >= 1f) {
                        ((Timer) evt.getSource()).stop();
                        // Aquí llamamos a System.exit(0) para cerrar la aplicación
                        // ya que VentanaJuego es el panel principal de la lógica del juego
                        // y el "cerrar sesión" implica salir completamente.
                        new Timer(1500, e -> System.exit(0)).start();
                    }
                    repaint();
                });
                timer.start();
            }
        };

        // Agregamos la niebla como capa superior en este JPanel
        layeredPane.add(panelFalla, JLayeredPane.DRAG_LAYER);
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    // Getter para obtener la instancia de PanelJuego (necesario para guardar datos)
    public PanelJuego getPanelJuego() {
        return panelJuego;
    }

    // Método para actualizar los datos del personaje en PanelJuego
    public void updateCharacterData(JsonObject newCharacterData) {
        if (panelJuego != null) {
            panelJuego.setCharacterData(newCharacterData); // Pasa los nuevos datos al PanelJuego
        }
    }
}
