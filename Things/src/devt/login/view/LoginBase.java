package devt.login.view;

// Librerias bases
import devt.login.components.Message;
import devt.login.components.PanelLoading;
import devt.login.components.PanelLoginAndRegister;
import devt.login.components.PanelVerifyCode;
import devt.login.components.PanelCover; 

// Librerias de Java de AWT Y Swing
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import java.net.URL; 

// Gson (para JSON)
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

// Importa tu clase ApiClient y su clase interna ApiResponse
import devt.login.apiFlask.ApiClient;
import devt.login.apiFlask.ApiClient.ApiResponse;

// Importaciones de tus paneles personalizados
import devt.login.components.AlphaOverlayPanel; 
import org.jdesktop.animation.timing.*;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

// Importar la clase de tu juego (ahora será un JPanel)
import Main.VentanaJuego; 


public class LoginBase extends javax.swing.JFrame {

    private FondoPanel fondo; 
    private MigLayout layout;
    private PanelCover cover;
    private PanelLoading loading;
    private PanelVerifyCode verifyCode;
    private Integer currentRegisteredUserId; 

    private PanelLoginAndRegister loginAndRegister;
    private Animator animator;
    private boolean isLogin = true; // true = panel derecho muestra Login, false = panel derecho muestra Register
    private final double addSize = 30;
    private final double coverSize = 45;
    private final double loginSize = 55;
    private final DecimalFormat df = new DecimalFormat("##0.###", DecimalFormatSymbols.getInstance(Locale.US));

    private JsonObject loggedInUserData; 
    private JsonObject currentCharacterData; 
    private PanelProfileAndInventory panelProfileAndInventory; 
    private PanelCharacterCreation panelCharacterCreation; 
    private ViewSystem mainMenuPanel; // Instancia del panel del menú principal
    private VentanaJuego gamePanel; // Nueva instancia para el panel del juego

    private AlphaOverlayPanel overlayPanel; 

    public LoginBase() {
        // Inicialización de ActionListeners (necesarios para PanelLoginAndRegister)
        ActionListener eventRegister = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        };
        ActionListener eventLogin = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                login();
            }
        };

        // 1. Inicializa loginAndRegister PRIMERO
        loginAndRegister = new PanelLoginAndRegister(eventRegister, eventLogin);

        initComponents(); 

        // Configuración de la ventana principal (LoginBase es el JFrame)
        this.setSize(1365, 767);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Juego RPG - Login"); 

        // Creación y configuración del Fondo del panel.
        fondo = new FondoPanel(); 
        layout = new MigLayout("fill, insets 0");
        fondo.setLayout(layout);
        
        // --- Gestión de Paneles con JLayeredPane ---
        this.getLayeredPane().add(fondo, JLayeredPane.DEFAULT_LAYER);
        fondo.setBounds(0, 0, this.getWidth(), this.getHeight()); 

        cover = new PanelCover(); 
        fondo.add(cover, "width 45%, pos 0al 0 n 100%");
        fondo.add(loginAndRegister, "width 55%, pos 1al 0 n 100%");

        // --- Inicialización de overlayPanel, loading, verifyCode en POPUP_LAYER ---
        overlayPanel = new AlphaOverlayPanel(); 
        overlayPanel.setVisible(false); 
        this.getLayeredPane().add(overlayPanel, JLayeredPane.POPUP_LAYER);
        overlayPanel.setBounds(0, 0, this.getWidth(), this.getHeight());

        loading = new PanelLoading();
        verifyCode = new PanelVerifyCode(); 
        this.getLayeredPane().add(loading, JLayeredPane.POPUP_LAYER);
        this.getLayeredPane().add(verifyCode, JLayeredPane.POPUP_LAYER);
        loading.setBounds(0, 0, this.getWidth(), this.getHeight());
        verifyCode.setBounds(0, 0, this.getWidth(), this.getHeight());
        loading.setVisible(false);
        verifyCode.setVisible(false);
        // --- Fin inicialización de paneles superpuestos ---

        init(); 
        
        // Listener para redimensionamiento de ventana para ajustar los paneles superpuestos
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                fondo.setBounds(0, 0, getWidth(), getHeight());
                loading.setBounds(0, 0, getWidth(), getHeight());
                verifyCode.setBounds(0, 0, getWidth(), getHeight());
                overlayPanel.setBounds(0, 0, getWidth(), getHeight());
                if (mainMenuPanel != null) {
                    mainMenuPanel.setBounds(0, 0, getWidth(), getHeight());
                }
                if (panelProfileAndInventory != null) {
                    panelProfileAndInventory.setBounds(0, 0, getWidth(), getHeight());
                }
                if (panelCharacterCreation != null) {
                    panelCharacterCreation.setBounds(0, 0, getWidth(), getHeight());
                }
                if (gamePanel != null) { // Asegurarse de que el panel del juego también se redimensione
                    gamePanel.setBounds(0, 0, getWidth(), getHeight());
                }
            }
        });
    }

    // Método que contiene toda la lógica de animación y el listener del botón.
    private void init() {
        cover.addEvent(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!animator.isRunning()) { 
                    animator.start();
                }
            }
        });

        // Inicializar animador
        animator = new Animator(800, new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                double fractionCover;
                double fractionLogin;
                double size = coverSize;

                if (isLogin) { 
                    fractionCover = 1f - fraction;
                    fractionLogin = fraction;
                    if (fraction <= 0.5f) {
                        size += fraction * addSize;
                    } else {
                        size += addSize - fraction * addSize;
                    }
                    cover.registerLeft(fraction); 
                } else { 
                    fractionCover = fraction;
                    fractionLogin = 1f - fraction;
                    if (fraction <= 0.5f) {
                        size += fraction * addSize;
                    } else {
                        size += addSize - fraction * addSize;
                    }
                    cover.loginRight(fraction); 
                }
                
                if (fraction >= 0.5f && fraction - 0.01f < 0.5f) { 
                    loginAndRegister.showLogin(!isLogin); 
                }

                layout.setComponentConstraints(cover, "width " + df.format(size) + "%, pos " + df.format(fractionCover) + "al 0 n 100%");
                layout.setComponentConstraints(loginAndRegister, "width " + df.format(loginSize) + "%, pos " + df.format(fractionLogin) + "al 0 n 100%");
                fondo.revalidate(); 
            }

            @Override
            public void end() {
                isLogin = !isLogin; 
                if (isLogin) { 
                    cover.loginRight(1); 
                } else { 
                    cover.registerLeft(1); 
                }
                fondo.revalidate(); 
                fondo.repaint(); 
            }
        });
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
        animator.setResolution(0); 
        
        // Listener para el botón OK del panel de verificación de código
        verifyCode.addEventButtonOK(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String email = verifyCode.getEmail(); 
                String inputCode = verifyCode.getInputCode();
                
                if (email == null || email.isEmpty() || inputCode.isEmpty()) {
                    showMessage(Message.MessageType.ERROR, "Ingresa el correo y el código de verificación.");
                    return;
                }

                loading.setVisible(true); 
                overlayPanel.setVisible(true); 

                new SwingWorker<ApiClient.ApiResponse, Void>() {
                    @Override
                    protected ApiClient.ApiResponse doInBackground() throws Exception {
                        return ApiClient.verifyUser(email, inputCode);
                    }

                    @Override
                            protected void done() {
                                loading.setVisible(false); 
                                try {
                                    ApiClient.ApiResponse result = get();
                                    if (result.isSuccess()) {
                                        showMessage(Message.MessageType.SUCCESS, result.getMessage());
                                        verifyCode.setVisible(false); 
                                        verifyCode.clearFields(); 

                                        loginAndRegister.showLogin(true);
                                        cover.login(true); 
                                        isLogin = true;

                                    } else {
                                        overlayPanel.setVisible(false);
                                        showMessage(Message.MessageType.ERROR, result.getMessage());
                                    }
                                } catch (Exception ex) {
                                    overlayPanel.setVisible(false);
                                    ex.printStackTrace();
                                    showMessage(Message.MessageType.ERROR, "Error al procesar la verificación: " + ex.getMessage());
                                }
                            }
                }.execute();
            }
        });
    }

    private void register() {
        String username = loginAndRegister.getRegisterUsername(); 
        String email = loginAndRegister.getRegisterEmail(); 
        String password = loginAndRegister.getRegisterPassword(); 

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showMessage(Message.MessageType.ERROR, "Por favor, llena todos los campos de registro.");
            return;
        }

        loading.setVisible(true); 
        overlayPanel.setVisible(true); 

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                return ApiClient.registerUser(username, email, password);
            }

            @Override
            protected void done() {
                loading.setVisible(false); 
                try {
                    ApiClient.ApiResponse result = get();
                    if (result.isSuccess()) {
                        showMessage(Message.MessageType.SUCCESS, result.getMessage());
                        verifyCode.setEmail(email); 
                        verifyCode.setVisible(true); 
                        loginAndRegister.clearFields(); 
                    } else {
                        overlayPanel.setVisible(false); 
                        showMessage(Message.MessageType.ERROR, result.getMessage());
                    }
                } catch (Exception ex) {
                    overlayPanel.setVisible(false); 
                    ex.printStackTrace();
                    showMessage(Message.MessageType.ERROR, "Error inesperado al registrar: " + ex.getMessage());
                }
            }
        }.execute();
    }        

    private void login() {
        String email = loginAndRegister.getLoginEmail(); 
        String password = loginAndRegister.getLoginPassword(); 

        if (email.isEmpty() || password.isEmpty()) {
            showMessage(Message.MessageType.ERROR, "Por favor, ingresa tu correo y contraseña.");
            return;
        }

        loading.setVisible(true); 
        overlayPanel.setVisible(true); 

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                return ApiClient.loginUser(email, password);
            }

            @Override
            protected void done() {
                loading.setVisible(false); 
                try {
                    ApiClient.ApiResponse result = get();
                    if (result.isSuccess()) {
                        showMessage(Message.MessageType.SUCCESS, result.getMessage());
                        loggedInUserData = result.getDataAsJsonObject(); 

                        loginAndRegister.clearFields(); 

                        loadOrCreateCharacter();

                    } else {
                        overlayPanel.setVisible(false); 
                        showMessage(Message.MessageType.ERROR, result.getMessage());
                    }
                } catch (Exception ex) {
                    overlayPanel.setVisible(false); 
                    ex.printStackTrace();
                    showMessage(Message.MessageType.ERROR, "Error inesperado al iniciar sesión: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void loadOrCreateCharacter() {
        if (loggedInUserData == null || !loggedInUserData.has("id") || loggedInUserData.get("id").isJsonNull()) {
            showMessage(Message.MessageType.ERROR, "Error: No se pudo obtener el ID del usuario logueado para cargar personaje.");
            performLogout();
            return;
        }
        int userId = loggedInUserData.get("id").getAsInt(); 

        loading.setVisible(true); 
        overlayPanel.setVisible(true); 

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                return ApiClient.getOrCreateCharacterProfile(userId);
            }

            @Override
            protected void done() {
                loading.setVisible(false); 
                try {
                    ApiClient.ApiResponse result = get();
                    if (result.isSuccess()) {
                        currentCharacterData = result.getDataAsJsonObject(); 
                        if (currentCharacterData != null) {
                            String characterName = currentCharacterData.has("nombre_personaje") && !currentCharacterData.get("nombre_personaje").isJsonNull()
                                ? currentCharacterData.get("nombre_personaje").getAsString() : null;

                            if (characterName == null || characterName.trim().isEmpty() || characterName.equals("None")) { 
                                showMessage(Message.MessageType.INFO, "No tienes un personaje. ¡Crea uno ahora!");
                                showCharacterCreationScreen(); 
                            } else {
                                showMessage(Message.MessageType.INFO, "Personaje cargado: " + characterName);
                                showMainMenu(); 
                            }
                        } else {
                            showMessage(Message.MessageType.ERROR, "Error: Datos de personaje nulos en la respuesta.");
                            performLogout();
                        }
                    } else if (result.getErrorCode() == 404) { 
                         showMessage(Message.MessageType.INFO, "No tienes un personaje. ¡Crea uno ahora!");
                         showCharacterCreationScreen();
                    } else {
                        overlayPanel.setVisible(false); 
                        showMessage(Message.MessageType.ERROR, "Error al verificar personaje: " + result.getMessage());
                        performLogout(); 
                    }
                } catch (Exception ex) {
                    overlayPanel.setVisible(false); 
                    ex.printStackTrace();
                    showMessage(Message.MessageType.ERROR, "Error inesperado al cargar/crear personaje: " + ex.getMessage());
                    performLogout();
                }
            }
        }.execute();
    }

    private void showCharacterCreationScreen() {
        if (loggedInUserData == null || !loggedInUserData.has("id") || loggedInUserData.get("id").isJsonNull()) {
            showMessage(Message.MessageType.ERROR, "Error: Datos de usuario no disponibles para la creación de personaje.");
            performLogout();
            return;
        }
        int userId = loggedInUserData.get("id").getAsInt();
        
        fondo.setVisible(false); 
        
        if (panelCharacterCreation == null) {
            panelCharacterCreation = new PanelCharacterCreation(userId); 
            panelCharacterCreation.addCharacterCreatedListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadOrCreateCharacter(); 
                }
            });
            this.getLayeredPane().add(panelCharacterCreation, JLayeredPane.DEFAULT_LAYER);
            panelCharacterCreation.setBounds(0, 0, getWidth(), getHeight());
        } else {
            panelCharacterCreation.setUserId(userId); 
        }

        panelCharacterCreation.setVisible(true); 
        setTitle("JVEILWALKER - Crea tu Personaje");
        revalidate(); 
        repaint(); 
    }

    private void showMainMenu() {
        fondo.setVisible(false); 
        if (panelCharacterCreation != null) {
            panelCharacterCreation.setVisible(false);
        }
        if (panelProfileAndInventory != null) {
            panelProfileAndInventory.setVisible(false);
        }
        if (gamePanel != null) { // Asegurarse de ocultar el panel del juego si está visible
            gamePanel.setVisible(false);
        }

        if (mainMenuPanel == null) {
            mainMenuPanel = new ViewSystem(loggedInUserData, currentCharacterData);
            
            // AÑADIR LISTENER AL BOTÓN "JUGAR" DE ViewSystem
            mainMenuPanel.addPlayButtonListener(new ActionListener() { 
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 1. Iniciar fundido a negro
                    overlayPanel.setVisible(true); 
                    Animator fadeToBlackAnimator = new Animator(500, new TimingTargetAdapter() { 
                        @Override
                        public void timingEvent(float fraction) {
                            overlayPanel.setAlpha(fraction); 
                        }

                        @Override
                        public void end() {
                            // Cuando el fundido a negro ha terminado
                            SwingUtilities.invokeLater(() -> {
                                mainMenuPanel.setVisible(false); 
                                LoginBase.this.setTitle("Cargando Juego..."); 

                                // 2. Mostrar la SplashScreen
                                SplashScreen splash = new SplashScreen();
                                splash.startSplash(() -> {
                                    // Este código se ejecuta cuando la SplashScreen ha terminado y se ha cerrado
                                    SwingUtilities.invokeLater(() -> {
                                        // 3. Iniciar fundido de negro a transparente
                                        Animator fadeFromBlackAnimator = new Animator(500, new TimingTargetAdapter() { 
                                            @Override
                                            public void timingEvent(float fraction) {
                                                overlayPanel.setAlpha(1.0f - fraction); 
                                            }

                                            @Override
                                            public void end() {
                                                // Cuando el fundido de salida ha terminado
                                                SwingUtilities.invokeLater(() -> {
                                                    overlayPanel.setVisible(false); // Ocultar el overlay
                                                    
                                                    // *** AQUÍ ES DONDE SE INICIA VentanaJuego (ahora como JPanel) ***
                                                    // LoginBase.this.dispose(); // YA NO SE CIERRA EL JFrame PRINCIPAL
                                                    
                                                    if (gamePanel == null) {
                                                        gamePanel = new VentanaJuego();
                                                        // Añadir el panel del juego al JLayeredPane en la capa DEFAULT
                                                        LoginBase.this.getLayeredPane().add(gamePanel, JLayeredPane.DEFAULT_LAYER);
                                                        gamePanel.setBounds(0, 0, LoginBase.this.getWidth(), LoginBase.this.getHeight());
                                                    }
                                                    gamePanel.setVisible(true); // Hacer visible el panel del juego
                                                    gamePanel.startGame(); // Inicia la lógica interna del juego y pide enfoque
                                                    
                                                    LoginBase.this.setTitle(VentanaJuego.titulo); // Establecer el título del juego
                                                    LoginBase.this.revalidate();
                                                    LoginBase.this.repaint();
                                                });
                                            }
                                        });
                                        fadeFromBlackAnimator.setResolution(0);
                                        fadeFromBlackAnimator.setAcceleration(0.5f);
                                        fadeFromBlackAnimator.setDeceleration(0.5f);
                                        fadeFromBlackAnimator.start();
                                    });
                                });
                            });
                        }
                    });
                    fadeToBlackAnimator.setResolution(0);
                    fadeToBlackAnimator.setAcceleration(0.5f);
                    fadeToBlackAnimator.setDeceleration(0.5f);
                    fadeToBlackAnimator.start();
                }
            });

            mainMenuPanel.addShowProfileListener(new ActionListener() { 
                @Override
                public void actionPerformed(ActionEvent e) {
                    showProfileScreen();
                }
            });
            mainMenuPanel.addLogoutButtonListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    performLogout();
                }
            });
            mainMenuPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                }
            });

            this.getLayeredPane().add(mainMenuPanel, JLayeredPane.DEFAULT_LAYER);
            mainMenuPanel.setBounds(0, 0, getWidth(), getHeight());

        } else {
            mainMenuPanel.updateUserData(loggedInUserData);
            mainMenuPanel.updateCharacterData(currentCharacterData);
        }

        mainMenuPanel.setVisible(true); 
        setTitle("VEILKALWER - Menú Principal");
        revalidate();
        repaint();
    }
        
    private void showProfileScreen() {
        if (loggedInUserData == null || !loggedInUserData.has("id") || loggedInUserData.get("id").isJsonNull() || currentCharacterData == null || !currentCharacterData.has("id") || currentCharacterData.get("id").isJsonNull()) {
            showMessage(Message.MessageType.ERROR, "Datos de usuario o personaje no disponibles para el perfil.");
            performLogout();
            return;
        }

        if (mainMenuPanel != null) {
            mainMenuPanel.setVisible(false);
        }
        if (gamePanel != null) { // Asegurarse de ocultar el panel del juego si está visible
            gamePanel.setVisible(false);
        }

        if (panelProfileAndInventory == null) {
            panelProfileAndInventory = new PanelProfileAndInventory(loggedInUserData); 
            
            panelProfileAndInventory.addBackToMainMenuListener(new ActionListener() { 
                @Override
                public void actionPerformed(ActionEvent e) {
                    showMainMenu(); 
                }
            });
            
            panelProfileAndInventory.addLogoutActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    performLogout(); 
                }
            });

            this.getLayeredPane().add(panelProfileAndInventory, JLayeredPane.DEFAULT_LAYER);
            panelProfileAndInventory.setBounds(0, 0, getWidth(), getHeight());
        }
        
        panelProfileAndInventory.loadData(this.currentCharacterData); 

        panelProfileAndInventory.setVisible(true);
        setTitle("VEILWALWER - Perfil e Inventario");
        revalidate();
        repaint();
    }


    private void showMessage(Message.MessageType messageType, String message) {
        Message ms = new Message();
        ms.showMessage(messageType, message);
        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void begin() {
                if (!ms.isShow()) {
                    getLayeredPane().add(ms, JLayeredPane.POPUP_LAYER); 
                    ms.setBounds( (getWidth() - ms.getPreferredSize().width) / 2, 10, ms.getPreferredSize().width, ms.getPreferredSize().height); 
                    ms.setVisible(true);
                    getLayeredPane().repaint();
                }
            }

            @Override
            public void timingEvent(float fraction) {
                float f;
                if (ms.isShow()) { 
                    f = 40 * fraction; 
                } else { 
                    f = 40 * (1f - fraction); 
                }
                ms.setLocation((getWidth() - ms.getPreferredSize().width) / 2, (int) (f - 30));
                getLayeredPane().revalidate();
                getLayeredPane().repaint();
            }

            @Override
            public void end() {
                if (ms.isShow()) {
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            ms.setShow(false); 
                            Animator exitAnimator = new Animator(300, new TimingTargetAdapter() {
                                @Override
                                public void timingEvent(float fraction) {
                                    float f = 40 * (1f - fraction);
                                    ms.setLocation((getWidth() - ms.getPreferredSize().width) / 2, (int) (f - 30));
                                    getLayeredPane().revalidate();
                                    getLayeredPane().repaint();
                                }
                                @Override
                                public void end() {
                                    getLayeredPane().remove(ms); 
                                    getLayeredPane().revalidate();
                                    getLayeredPane().repaint();
                                }
                            });
                            exitAnimator.setResolution(0);
                            exitAnimator.setAcceleration(0.5f);
                            exitAnimator.setDeceleration(0.5f);
                            exitAnimator.start();
                        } catch (InterruptedException e) {
                        }
                    }).start();
                } 
            }
        };
        Animator animator = new Animator(300, target);
        animator.setResolution(0);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
        animator.start(); 
    }

    private void performLogout() {
        loggedInUserData = null;
        currentCharacterData = null;
        currentRegisteredUserId = null;
        
        if (mainMenuPanel != null) {
            mainMenuPanel.setVisible(false);
        }
        if (panelProfileAndInventory != null) {
            panelProfileAndInventory.setVisible(false);
        }
        if (panelCharacterCreation != null) {
            panelCharacterCreation.setVisible(false);
        }
        if (gamePanel != null) { // Asegurarse de ocultar el panel del juego si está visible
            gamePanel.setVisible(false);
        }
        loading.setVisible(false);
        verifyCode.setVisible(false);
        overlayPanel.setVisible(false);

        getLayeredPane().removeAll();
        
        getLayeredPane().add(fondo, JLayeredPane.DEFAULT_LAYER);
        fondo.setBounds(0, 0, getWidth(), getHeight());
        fondo.setVisible(true); 

        getLayeredPane().add(loading, JLayeredPane.POPUP_LAYER);
        getLayeredPane().add(verifyCode, JLayeredPane.POPUP_LAYER);
        getLayeredPane().add(overlayPanel, JLayeredPane.POPUP_LAYER);
        
        isLogin = true; 
        loginAndRegister.showLogin(true); 
        cover.login(true); 

        loginAndRegister.clearFields();
        verifyCode.clearFields(); 
        
        setTitle("Juego RPG - Login");
        revalidate();
        repaint();    
        showMessage(Message.MessageType.INFO, "Sesión cerrada exitosamente.");
    }

    // EL initComponents()  PARA UN JFRAME
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

     // Clase interna FondoPanel para el fondo de la ventana principal
    class FondoPanel extends JPanel {
        private Image imagen;

        public FondoPanel() {
            setOpaque(true); 
            try {
                URL imageUrl = getClass().getResource("/devt/login/images/guzz_1.png"); 
                if (imageUrl != null) { 
                    imagen = new ImageIcon(imageUrl).getImage();
                } else {
                    System.err.println("Error: No se encontró la imagen de fondo en la ruta: /devt/login/images/guzz_1.png");
                    setBackground(new Color(20, 20, 20)); 
                }
            } catch (Exception e) {
                System.err.println("Error al cargar imagen de fondo: " + e.getMessage());
                    setBackground(new Color(20, 20, 20)); 
                }
            }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagen != null) {
                g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginBase.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        SwingUtilities.invokeLater(() -> {
            new LoginBase().setVisible(true);
        });
    }
}
