package devt.login.view;

// Librerias bases
import devt.login.components.Message;
import devt.login.components.PanelLoading;
import devt.login.components.PanelLoginAndRegister;
import devt.login.components.PanelVerifyCode;
import devt.login.components.PanelCover;
import devt.login.components.PanelForgotPassword;
import devt.login.view.PanelCharacterCreation;
import devt.login.view.PanelProfileAndInventory;
import devt.login.view.SplashScreen;

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
// ¡NUEVO! Importaciones para el cierre de ventana y guardado
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

// Importar la clase de tu juego (ahora será un JLayeredPane)
import Main.VentanaJuego;
import Sonido.Musica;
import devt.login.view.ViewSystem;


// ¡MODIFICADO! LoginBase ahora implementa la interfaz GameNavigationCallback de VentanaJuego
public class LoginBase extends javax.swing.JFrame implements VentanaJuego.GameNavigationCallback {

    private Musica musicaFondoLogin;

    private FondoPanel fondo;
    private MigLayout layout;
    private PanelCover cover;
    private PanelLoading loading;
    private PanelVerifyCode verifyCode;
    private Integer currentRegisteredUserId;

    private PanelLoginAndRegister loginAndRegister;
    private Animator animator;
    private boolean isLogin = true;
    private final double addSize = 30;
    private final double coverSize = 45;
    private final double loginSize = 55;
    private final DecimalFormat df = new DecimalFormat("##0.###", DecimalFormatSymbols.getInstance(Locale.US));

    private JsonObject loggedInUserData;
    private JsonObject currentCharacterData;
    private PanelProfileAndInventory panelProfileAndInventory;
    private PanelCharacterCreation panelCharacterCreation;
    private ViewSystem mainMenuPanel; // Este es tu PanelMenuPrincipal
    private VentanaJuego gamePanel; // ¡MODIFICADO! Ahora es de tipo VentanaJuego

    private AlphaOverlayPanel overlayPanel;

    private PanelForgotPassword forgotPasswordPanel;

    // Instancia única para mostrar mensajes
    private Message messagePanelInstance;

    // ¡NUEVO! Instancia de ApiClient (asegurarse de que esté inicializada)
    private final ApiClient apiClient;

    public LoginBase() {
        // Inicializa ApiClient al inicio
        apiClient = new ApiClient(); // ¡NUEVO! Inicialización de apiClient

        try {
            musicaFondoLogin = new Musica("/Sonido/music.wav");
            musicaFondoLogin.reproducirEnLoop();
            musicaFondoLogin.setVolumen(-15.0f);
            System.out.println("Música de fondo del login iniciada.");
        } catch (Exception e) {
            System.err.println("Error al iniciar la música de fondo del login: " + e.getMessage());
            e.printStackTrace();
        }

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
        ActionListener eventForgotPassword = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showForgotPasswordPanel();
            }
        };

        loginAndRegister = new PanelLoginAndRegister(eventRegister, eventLogin, eventForgotPassword);

        initComponents(); // Llama a initComponents para configurar el JFrame

        this.setSize(1365, 767);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        // ¡MODIFICADO! Manejaremos el cierre de la ventana manualmente para guardar
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        this.setTitle("VEILWALKER - Login");

        fondo = new FondoPanel();
        layout = new MigLayout("fill, insets 0");
        fondo.setLayout(layout);

        // ¡MODIFICADO! Añadir fondo al JLayeredPane del JFrame
        this.getLayeredPane().add(fondo, JLayeredPane.DEFAULT_LAYER);
        fondo.setBounds(0, 0, this.getWidth(), this.getHeight());

        cover = new PanelCover();

        fondo.add(cover, "width " + coverSize + "%, pos " + (isLogin ? "1al" : "0al") + " 0 n 100%");
        fondo.add(loginAndRegister, "width " + loginSize + "%, pos " + (isLogin ? "0al" : "1al") + " 0 n 100%");
        loginAndRegister.showLogin(isLogin);
        cover.login(isLogin);

        overlayPanel = new AlphaOverlayPanel();
        overlayPanel.setVisible(false);
        this.getLayeredPane().add(overlayPanel, JLayeredPane.POPUP_LAYER);
        overlayPanel.setBounds(0, 0, this.getWidth(), this.getHeight());

        loading = new PanelLoading();
        verifyCode = new PanelVerifyCode();
        forgotPasswordPanel = new PanelForgotPassword();

        this.getLayeredPane().add(loading, JLayeredPane.POPUP_LAYER);
        this.getLayeredPane().add(verifyCode, JLayeredPane.POPUP_LAYER);
        this.getLayeredPane().add(forgotPasswordPanel, JLayeredPane.POPUP_LAYER);

        loading.setBounds(0, 0, this.getWidth(), this.getHeight());
        verifyCode.setBounds(0, 0, this.getWidth(), this.getHeight());
        forgotPasswordPanel.setBounds(0, 0, this.getWidth(), this.getHeight());

        loading.setVisible(false);
        verifyCode.setVisible(false);
        forgotPasswordPanel.setVisible(false);

        // Inicializar la instancia única del panel de mensajes
        messagePanelInstance = new Message();

        init();

        // ¡NUEVO! Manejar el cierre de la ventana para guardar datos
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                // Si el juego está activo, guardamos el progreso antes de salir
                if (gamePanel != null && gamePanel.isVisible()) {
                    System.out.println("Cerrando aplicación desde el juego. Guardando...");
                    saveCurrentCharacterData(gamePanel.getPanelJuego().getCurrentCharacterData());
                } else if (currentCharacterData != null) {
                    // Si hay un personaje cargado pero no estamos en el juego (ej. en menú principal o perfil),
                    // guardamos los datos del personaje que tenemos en memoria (que ya deberían estar actualizados
                    // si se editaron en PanelProfileAndInventory y se presionó "Guardar Juego").
                    System.out.println("Cerrando aplicación fuera del juego. Guardando datos del personaje...");
                    saveCurrentCharacterData(currentCharacterData);
                }
                // Finalmente, cerramos la aplicación
                System.exit(0);
            }
        });

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                fondo.setBounds(0, 0, getWidth(), getHeight());
                loading.setBounds(0, 0, getWidth(), getHeight());
                verifyCode.setBounds(0, 0, getWidth(), getHeight());
                forgotPasswordPanel.setBounds(0, 0, getWidth(), getHeight());
                overlayPanel.setBounds(0, 0, getWidth(), getHeight());
                if (messagePanelInstance.isVisible()) {
                    messagePanelInstance.setBounds((getWidth() - messagePanelInstance.getPreferredSize().width) / 2, 10, messagePanelInstance.getPreferredSize().width, messagePanelInstance.getPreferredSize().height);
                }
                if (mainMenuPanel != null) {
                    mainMenuPanel.setBounds(0, 0, getWidth(), getHeight());
                }
                if (panelProfileAndInventory != null) {
                    panelProfileAndInventory.setBounds(0, 0, getWidth(), getHeight());
                }
                if (panelCharacterCreation != null) {
                    panelCharacterCreation.setBounds(0, 0, getWidth(), getHeight());
                }
                // ¡MODIFICADO! Asegurar que gamePanel también se redimensione
                if (gamePanel != null) {
                    gamePanel.setBounds(0, 0, getWidth(), getHeight());
                }
            }
        });
    }

    private void init() {
        cover.addEvent(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!animator.isRunning()) {
                    animator.start();
                }
            }
        });

        forgotPasswordPanel.addEventOK(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                System.out.println("PanelForgotPassword: Botón OK presionado. isShowingEmailInput: " + forgotPasswordPanel.isShowingEmailInput());
                if (forgotPasswordPanel.isShowingEmailInput()) {
                    requestPasswordResetCode();
                } else {
                    resetPasswordWithCode();
                }
            }
        });

        forgotPasswordPanel.addEventCancel(e -> {           
            System.out.println("PanelForgotPassword: Botón CANCELAR presionado.");
            forgotPasswordPanel.setVisible(false);
            overlayPanel.setVisible(false);
            fondo.setVisible(true);
            loginAndRegister.setVisible(true);
            cover.setVisible(true);
            setTitle("VEILWALKER - Login");
            revalidate();
            repaint();
            forgotPasswordPanel.showEmailInput();
        });

        // TU CÓDIGO ORIGINAL DE ANIMACIÓN - NO MODIFICADO
        animator = new Animator(800, new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                double fractionCover;
                double fractionLogin;
                double size = coverSize;

                if (fraction <= 0.5f) {
                    size += fraction * addSize;
                } else {
                    size += addSize - fraction * addSize;
                }

                if (isLogin) {
                    fractionCover = 1f - fraction;
                    fractionLogin = fraction;

                    if (fraction >= 0.5f) {
                        cover.registerRight(fractionCover * 100);
                    } else {
                        cover.loginRight(fractionLogin * 100);
                    }
                } else {
                    fractionCover = fraction;
                    fractionLogin = 1f - fraction;

                    if (fraction <= 0.5f) {
                        cover.registerLeft(fraction * 100);
                    } else {
                        cover.loginLeft((1f - fraction) * 100);
                    }
                }

                fractionCover = Double.valueOf(df.format(fractionCover));
                fractionLogin = Double.valueOf(df.format(fractionLogin));

                if (fraction >= 0.5f) {
                    loginAndRegister.showLogin(!isLogin);
                }

                layout.setComponentConstraints(cover, "width " + df.format(size) + "%, pos " + fractionCover + "al 0 n 100%");
                layout.setComponentConstraints(loginAndRegister, "width " + df.format(loginSize) + "%, pos " + fractionLogin + "al 0 n 100%");

                fondo.revalidate();
            }

            @Override
            public void end() {
                isLogin = !isLogin;
                layout.setComponentConstraints(cover, "width " + df.format(coverSize) + "%, pos " + (isLogin ? "1al" : "0al") + " 0 n 100%");
                layout.setComponentConstraints(loginAndRegister, "width " + df.format(loginSize) + "%, pos " + (isLogin ? "0al" : "1al") + " 0 n 100%");
                fondo.revalidate();
                fondo.repaint();
            }
        });
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
        animator.setResolution(0);

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
                        System.out.println("SwingWorker (verifyCode): Llamando a ApiClient.verifyUser...");
                        return apiClient.verifyUser(email, inputCode); // ¡MODIFICADO! Usar apiClient
                    }

                    @Override
                    protected void done() {
                        loading.setVisible(false);
                        try {
                            ApiClient.ApiResponse result = get();
                            System.out.println("SwingWorker (verifyCode) done. Success: " + result.isSuccess() + ", Message: " + result.getMessage() + ", ErrorCode: " + result.getErrorCode());
                            if (result.isSuccess()) {
                                showMessage(Message.MessageType.SUCCESS, result.getMessage());
                                verifyCode.setVisible(false);
                                verifyCode.clearFields();
                                overlayPanel.setVisible(false);
                                loginAndRegister.showLogin(true);
                                isLogin = true;
                                cover.login(isLogin);

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
                System.out.println("SwingWorker (register): Llamando a ApiClient.registerUser...");
                return apiClient.registerUser(username, email, password); // ¡MODIFICADO! Usar apiClient
            }

            @Override
            protected void done() {
                loading.setVisible(false);
                try {
                    ApiClient.ApiResponse result = get();
                    System.out.println("SwingWorker (register) done. Success: " + result.isSuccess() + ", Message: " + result.getMessage() + ", ErrorCode: " + result.getErrorCode());
                    if (result.isSuccess()) {
                        showMessage(Message.MessageType.SUCCESS, result.getMessage());
                        verifyCode.setEmail(email);
                        verifyCode.setVisible(true);
                        loginAndRegister.clearFields();
                    } else {
                        overlayPanel.setVisible(false);
                        String userMessage = result.getMessage();
                        if (result.getErrorCode() == 409) {
                            userMessage = "Este correo electrónico ya está registrado.";
                        }
                        showMessage(Message.MessageType.ERROR, userMessage);
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
                System.out.println("SwingWorker (login): Llamando a ApiClient.loginUser...");
                return apiClient.loginUser(email, password); // ¡MODIFICADO! Usar apiClient
            }

            @Override
            protected void done() {
                loading.setVisible(false);
                try {
                    ApiClient.ApiResponse result = get();
                    System.out.println("SwingWorker (login) done. Success: " + result.isSuccess() + ", Message: " + result.getMessage() + ", ErrorCode: " + result.getErrorCode());
                    if (result.isSuccess()) {
                        showMessage(Message.MessageType.SUCCESS, result.getMessage());
                        loggedInUserData = result.getDataAsJsonObject();

                        loginAndRegister.clearFields();
                        overlayPanel.setVisible(false);

                        loadOrCreateCharacter();

                    } else {
                        overlayPanel.setVisible(false);
                        String userMessage = result.getMessage();
                        if (result.getErrorCode() == 401) {
                            userMessage = "Credenciales inválidas (correo o contraseña incorrectos).";
                        } else if (result.getErrorCode() == 404) {
                            userMessage = "Usuario no encontrado. Por favor, regístrate.";
                        }
                        showMessage(Message.MessageType.ERROR, userMessage);
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
            showMessage(Message.MessageType.ERROR, "Error: Datos de usuario no disponibles para cargar personaje.");
            performLogout();
            return;
        }
        int userId = loggedInUserData.get("id").getAsInt();

        loading.setVisible(true);
        overlayPanel.setVisible(true);

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                System.out.println("SwingWorker (loadOrCreateCharacter): Llamando a ApiClient.getOrCreateCharacterProfile...");
                return apiClient.getOrCreateCharacterProfile(userId); // ¡MODIFICADO! Usar apiClient
            }

            @Override
            protected void done() {
                loading.setVisible(false);
                try {
                    ApiClient.ApiResponse result = get();
                    System.out.println("SwingWorker (loadOrCreateCharacter) done. Success: " + result.isSuccess() + ", Message: " + result.getMessage() + ", ErrorCode: " + result.getErrorCode());
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
        loading.setVisible(false);
        verifyCode.setVisible(false);
        overlayPanel.setVisible(false);
        if (forgotPasswordPanel != null) {
            forgotPasswordPanel.setVisible(false);
        }
        if (panelCharacterCreation == null) {
            panelCharacterCreation = new devt.login.view.PanelCharacterCreation(userId, new PanelCharacterCreation.MessageDisplayCallback() {
                @Override
                public void showMessage(Message.MessageType type, String message) {
                    LoginBase.this.showMessage(type, message);
                }
            });

            panelCharacterCreation.addCharacterCreatedListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadOrCreateCharacter();
                }
            });
            panelCharacterCreation.addBackToLoginListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    performLogout();
                }
            });
            // ¡MODIFICADO! Añadir a JLayeredPane
            this.getLayeredPane().add(panelCharacterCreation, JLayeredPane.DEFAULT_LAYER);
            panelCharacterCreation.setBounds(0, 0, getWidth(), getHeight());
        } else {
            panelCharacterCreation.setUserId(userId);
            panelCharacterCreation.setMessageCallback(new PanelCharacterCreation.MessageDisplayCallback() {
                @Override
                public void showMessage(Message.MessageType type, String message) {
                    LoginBase.this.showMessage(type, message);
                }
            });
        }

        panelCharacterCreation.setVisible(true);
        setTitle("VEILWALKER - Crea tu Personaje");
        revalidate();
        repaint();
    }

    // ¡MODIFICADO! showMainMenu ahora usa la lógica de ocultar todos los paneles
    // y luego mostrar el menú principal. También es la implementación de la interfaz.
    public void showMainMenu() {
        // Ocultar todos los paneles posibles antes de mostrar el menú principal
        fondo.setVisible(false); // Oculta el fondo del login/registro
        if (panelCharacterCreation != null) { panelCharacterCreation.setVisible(false); }
        if (panelProfileAndInventory != null) { panelProfileAndInventory.setVisible(false); }
        if (gamePanel != null) { gamePanel.setVisible(false); } // ¡IMPORTANTE! Ocultar el juego
        overlayPanel.setVisible(false);
        if (forgotPasswordPanel != null) { forgotPasswordPanel.setVisible(false); }
        loading.setVisible(false); // Asegurarse de que el loading esté oculto
        verifyCode.setVisible(false); // Asegurarse de que el verifyCode esté oculto

        if (mainMenuPanel == null) {
            mainMenuPanel = new devt.login.view.ViewSystem(loggedInUserData, currentCharacterData);

            mainMenuPanel.addPlayButtonListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    overlayPanel.setVisible(true); // Mostrar overlay para la transición
                    Animator fadeToBlackAnimator = new Animator(500, new TimingTargetAdapter() {
                        @Override
                        public void timingEvent(float fraction) {
                            overlayPanel.setAlpha(fraction);
                        }

                        @Override
                        public void end() {
                            SwingUtilities.invokeLater(() -> {
                                mainMenuPanel.setVisible(false);
                                LoginBase.this.setTitle("Cargando Juego...");
                                showMessage(Message.MessageType.INFO, "Cargando juego...");

                                SplashScreen splash = new SplashScreen();
                                splash.startSplash(() -> {
                                    SwingUtilities.invokeLater(() -> {
                                        Animator fadeFromBlackAnimator = new Animator(500, new TimingTargetAdapter() {
                                            @Override
                                            public void timingEvent(float fraction) {
                                                overlayPanel.setAlpha(1.0f - fraction);
                                            }

                                            @Override
                                            public void end() {
                                                SwingUtilities.invokeLater(() -> {
                                                    overlayPanel.setVisible(false);
                                                    // ¡MODIFICADO! Llamar a showGameScreen con los datos del personaje
                                                    LoginBase.this.showGameScreen(currentCharacterData);
                                                    showMessage(Message.MessageType.SUCCESS, "¡Juego iniciado!");
                                                    LoginBase.this.setTitle(Main.VentanaJuego.titulo);
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
                    // Puedes dejar esto vacío si no necesitas una acción al hacer clic en el panel
                }
            });

            // ¡MODIFICADO! Añadir a JLayeredPane
            this.getLayeredPane().add(mainMenuPanel, JLayeredPane.DEFAULT_LAYER);
            mainMenuPanel.setBounds(0, 0, getWidth(), getHeight());

        } else {
            mainMenuPanel.updateUserData(loggedInUserData);
            mainMenuPanel.updateCharacterData(currentCharacterData);
        }

        mainMenuPanel.setVisible(true);
        setTitle("VEILWALKER - Menú Principal");
        revalidate();
        repaint();
    }

    // ¡MODIFICADO! showProfileScreen ahora usa la lógica de ocultar todos los paneles
    // y luego mostrar el perfil. También es la implementación de la interfaz.
    public void showProfileScreen() {
        // Ocultar todos los paneles posibles antes de mostrar el perfil
        fondo.setVisible(false); // Oculta el fondo del login/registro
        if (mainMenuPanel != null) { mainMenuPanel.setVisible(false); }
        if (gamePanel != null) { gamePanel.setVisible(false); } // ¡IMPORTANTE! Ocultar el juego
        if (panelCharacterCreation != null) { panelCharacterCreation.setVisible(false); }
        overlayPanel.setVisible(false);
        if (forgotPasswordPanel != null) { forgotPasswordPanel.setVisible(false); }
        loading.setVisible(false); // Asegurarse de que el loading esté oculto
        verifyCode.setVisible(false); // Asegurarse de que el verifyCode esté oculto

        if (loggedInUserData == null || !loggedInUserData.has("id") || loggedInUserData.get("id").isJsonNull() || currentCharacterData == null || !currentCharacterData.has("id") || currentCharacterData.get("id").isJsonNull()) {
            showMessage(Message.MessageType.ERROR, "Datos de usuario o personaje no disponibles para el perfil.");
            performLogout();
            return;
        }

        if (panelProfileAndInventory == null) {
            panelProfileAndInventory = new devt.login.view.PanelProfileAndInventory(loggedInUserData);

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

            // ¡MODIFICADO! Añadir a JLayeredPane
            this.getLayeredPane().add(panelProfileAndInventory, JLayeredPane.DEFAULT_LAYER);
            panelProfileAndInventory.setBounds(0, 0, getWidth(), getHeight());
        }

        panelProfileAndInventory.loadData(this.currentCharacterData); // Carga los datos del personaje en el panel de perfil
        panelProfileAndInventory.updateUserData(loggedInUserData); // Actualiza los datos del usuario en el perfil

        panelProfileAndInventory.setVisible(true);
        setTitle("VEILWALKER - Perfil e Inventario");
        revalidate();
        repaint();
    }

    // ¡NUEVO! Método para mostrar la pantalla del juego
    // Este método es llamado por el botón "Jugar" en ViewSystem (MainMenu)
    // y también internamente por LoginBase al reanudar el juego desde el splash screen.
    public void showGameScreen(JsonObject characterData) {
        this.currentCharacterData = characterData; // Asegurarse de que el personaje actual esté seteado
        
        // Detener y remover la instancia anterior de VentanaJuego si existe
        if (gamePanel != null) {
            gamePanel.stopGame(); // Detiene el juego anterior si existe
            this.getLayeredPane().remove(gamePanel); // Remueve la instancia anterior
            gamePanel = null; // Limpia la referencia
        }

        // Crear una nueva instancia de VentanaJuego, pasándole el callback y los datos del personaje
        gamePanel = new VentanaJuego(this, characterData); // ¡MODIFICADO!
        // Establece los límites para que ocupe todo el JLayeredPane
        gamePanel.setBounds(0, 0, this.getWidth(), this.getHeight()); 
        this.getLayeredPane().add(gamePanel, JLayeredPane.DEFAULT_LAYER); // Añade en la capa por defecto (inferior)
        
        // Ocultar todos los otros paneles
        fondo.setVisible(false);
        if (mainMenuPanel != null) { mainMenuPanel.setVisible(false); }
        if (panelProfileAndInventory != null) { panelProfileAndInventory.setVisible(false); }
        if (panelCharacterCreation != null) { panelCharacterCreation.setVisible(false); }
        overlayPanel.setVisible(false);
        if (forgotPasswordPanel != null) { forgotPasswordPanel.setVisible(false); }
        loading.setVisible(false);
        verifyCode.setVisible(false);

        gamePanel.setVisible(true); // Hacer visible el panel del juego
        gamePanel.startGame(); // Inicia el hilo del juego y pide foco
        gamePanel.requestFocusInWindow(); // Asegura que VentanaJuego reciba el foco para la tecla ESC
        setTitle(Main.VentanaJuego.titulo); // Actualiza el título de la ventana
        revalidate();
        repaint();
    }

    // ¡NUEVO! Método para mostrar el panel de olvido de contraseña
    private void showForgotPasswordPanel() {
        // Oculta los paneles de login/registro y muestra el de "Olvidé Contraseña"
        fondo.setVisible(false); // Oculta el fondo que contiene login/register/cover
        // cover y loginAndRegister se ocultan porque están dentro de fondo
        
        // Asegurarse de que los paneles de superposición estén ocultos (excepto overlay)
        loading.setVisible(false);
        verifyCode.setVisible(false);
        // Asegurarse de que otros paneles de contenido estén ocultos
        if (mainMenuPanel != null) { mainMenuPanel.setVisible(false); }
        if (panelProfileAndInventory != null) { panelProfileAndInventory.setVisible(false); }
        if (panelCharacterCreation != null) { panelCharacterCreation.setVisible(false); }
        if (gamePanel != null) { gamePanel.setVisible(false); }

        forgotPasswordPanel.showEmailInput(); // Asegura que muestre la vista de email
        forgotPasswordPanel.setVisible(true);
        overlayPanel.setVisible(true); // El overlay debe cubrir toda la pantalla
        setTitle("VEILWALKER - Recuperar Contraseña");
        revalidate();
        repaint();
    }

    private void requestPasswordResetCode() {
        String email = forgotPasswordPanel.getEmail();
        System.out.println("requestPasswordResetCode: Intentando enviar código a " + email);

        if (email.isEmpty()) {
            showMessage(Message.MessageType.ERROR, "Por favor, ingresa tu correo electrónico.");
            System.out.println("requestPasswordResetCode: Email vacío.");
            return;
        }

        loading.setVisible(true);
        overlayPanel.setVisible(true);

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                System.out.println("SwingWorker (requestPasswordResetCode): Llamando a ApiClient.requestPasswordResetCode...");
                return apiClient.requestPasswordResetCode(email);
            }

            @Override
            protected void done() {
                loading.setVisible(false); // Siempre ocultar loading
                try {
                    ApiClient.ApiResponse result = get();
                    System.out.println("SwingWorker (requestPasswordResetCode) done. Success: " + result.isSuccess() + ", Message: " + result.getMessage() + ", ErrorCode: " + result.getErrorCode());
                    if (result.isSuccess()) {
                        showMessage(Message.MessageType.SUCCESS, result.getMessage());
                        forgotPasswordPanel.showCodeInput();
                        // overlayPanel se mantiene visible si showCodeInput lo necesita o se ocultará en el siguiente paso
                    } else {
                        overlayPanel.setVisible(false); // Ocultar overlay en caso de fallo
                        showMessage(Message.MessageType.ERROR, result.getMessage());
                    }
                } catch (Exception ex) {
                    overlayPanel.setVisible(false); // Ocultar overlay en caso de excepción
                    ex.printStackTrace();
                    showMessage(Message.MessageType.ERROR, "Error al solicitar código de restablecimiento: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void resetPasswordWithCode() {
        String email = forgotPasswordPanel.getEmail();
        String code = forgotPasswordPanel.getCode();
        String newPassword = forgotPasswordPanel.getNewPassword();
        String confirmPassword = forgotPasswordPanel.getConfirmPassword();

        System.out.println("resetPasswordWithCode: Intentando restablecer contraseña.");
        System.out.println("Email: " + email + ", Code: " + code + ", NewPass Length: " + newPassword.length());

        if (email.isEmpty() || code.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showMessage(Message.MessageType.ERROR, "Por favor, ingresa el correo, código y la nueva contraseña.");
            System.out.println("resetPasswordWithCode: Campos vacíos.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            showMessage(Message.MessageType.ERROR, "Las contraseñas no coinciden.");
            System.out.println("resetPasswordWithCode: Contraseñas no coinciden.");
            return;
        }
        if (newPassword.length() < 6) {
            showMessage(Message.MessageType.ERROR, "La nueva contraseña debe tener al menos 6 caracteres.");
            System.out.println("resetPasswordWithCode: Contraseña muy corta.");
            return;
        }

        loading.setVisible(true);
        overlayPanel.setVisible(true);

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                System.out.println("SwingWorker (resetPasswordWithCode): Llamando a ApiClient.resetPasswordWithCode...");
                return apiClient.resetPasswordWithCode(email, code, newPassword);
            }

            @Override
            protected void done() {
                loading.setVisible(false); // Siempre ocultar loading
                try {
                    ApiClient.ApiResponse result = get();
                    System.out.println("SwingWorker (resetPasswordWithCode) done. Success: " + result.isSuccess() + ", Message: " + result.getMessage() + ", ErrorCode: " + result.getErrorCode());
                    if (result.isSuccess()) {
                        showMessage(Message.MessageType.SUCCESS, result.getMessage());
                        forgotPasswordPanel.setVisible(false);
                        overlayPanel.setVisible(false); // Ocultar overlay en caso de éxito
                        fondo.setVisible(true);
                        setTitle("VEILWALKER - Login");
                        revalidate();
                        repaint();
                        forgotPasswordPanel.showEmailInput();
                    } else {
                        overlayPanel.setVisible(false); // Ocultar overlay en caso de fallo
                        showMessage(Message.MessageType.ERROR, result.getMessage());
                    }
                } catch (Exception ex) {
                    overlayPanel.setVisible(false); // Ocultar overlay en caso de excepción
                    ex.printStackTrace();
                    showMessage(Message.MessageType.ERROR, "Error al restablecer contraseña: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void showMessage(Message.MessageType messageType, String message) {
        messagePanelInstance.showMessage(messageType, message);
        
        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void begin() {
                // ¡CORRECCIÓN CLAVE AQUÍ! Asegurarse de que el panel se añade SIEMPRE
                // y se hace visible. Se eliminó la condición 'if (!ms.isShow())'
                // que podía causar que el mensaje no apareciera si ya había otro.
                getLayeredPane().add(messagePanelInstance, JLayeredPane.POPUP_LAYER);
                messagePanelInstance.setBounds( (getWidth() - messagePanelInstance.getPreferredSize().width) / 2, 10, messagePanelInstance.getPreferredSize().width, messagePanelInstance.getPreferredSize().height);
                messagePanelInstance.setVisible(true);
                getLayeredPane().repaint();
            }

            @Override
            public void timingEvent(float fraction) {
                float f;
                if (messagePanelInstance.isShow()) {
                    f = 40 * fraction;
                } else {
                    f = 40 * (1f - fraction);
                }
                messagePanelInstance.setLocation((getWidth() - messagePanelInstance.getPreferredSize().width) / 2, (int) (f - 30));
                getLayeredPane().revalidate();
                getLayeredPane().repaint();
            }
            
            @Override
            public void end() {
                if (messagePanelInstance.isShow()) {
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            messagePanelInstance.setShow(false);
                            Animator exitAnimator = new Animator(300, new TimingTargetAdapter() {
                                @Override
                                public void timingEvent(float fraction) {
                                    float f = 40 * (1f - fraction);
                                    messagePanelInstance.setLocation((getWidth() - messagePanelInstance.getPreferredSize().width) / 2, (int) (f - 30));
                                    getLayeredPane().revalidate();
                                    getLayeredPane().repaint();
                                }

                                @Override
                                public void end() {
                                    getLayeredPane().remove(messagePanelInstance);
                                    getLayeredPane().revalidate();
                                    getLayeredPane().repaint();
                                }
                            });
                            exitAnimator.setResolution(0);
                            exitAnimator.setAcceleration(0.5f);
                            exitAnimator.setDeceleration(0.5f);
                            exitAnimator.start();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
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

    // ¡NUEVO! Setter para los datos del usuario logueado
    public void setLoggedInUserData(JsonObject userData) {
        this.loggedInUserData = userData;
        // Pasa los datos del usuario a PanelProfileAndInventory
        if (panelProfileAndInventory != null) {
            // Asegúrate de que PanelProfileAndInventory tenga un método updateUserData
            panelProfileAndInventory.updateUserData(loggedInUserData);
        }
    }

    // ¡NUEVO! Getter para los datos del usuario logueado
    public JsonObject getLoggedInUserData() {
        return loggedInUserData;
    }

    // ¡NUEVO! Setter para los datos del personaje actual
    public void setCurrentCharacterData(JsonObject characterData) {
        this.currentCharacterData = characterData;
        // Si el juego ya está visible, actualiza también sus datos
        if (gamePanel != null && gamePanel.isVisible()) {
            gamePanel.updateCharacterData(characterData);
        }
    }

    // ¡NUEVO! Getter para los datos del personaje actual
    public JsonObject getCurrentCharacterData() {
        return currentCharacterData;
    }

    /**
     * Método para guardar los datos del personaje actual en el servidor.
     * Este método será llamado desde VentanaJuego (a través del callback)
     * y desde el WindowListener al cerrar la aplicación.
     */
    public void saveCurrentCharacterData(JsonObject dataToSave) {
        if (dataToSave == null || !dataToSave.has("id") || dataToSave.get("id").isJsonNull()) {
            System.out.println("No hay datos de personaje válidos (ID faltante) para guardar.");
            return;
        }

        int characterId = dataToSave.get("id").getAsInt();
        System.out.println("Iniciando guardado de progreso para personaje ID: " + characterId);

        new SwingWorker<ApiResponse, Void>() {
            @Override
            protected ApiResponse doInBackground() throws Exception {
                // Llama al método de tu ApiClient para actualizar el perfil del personaje
                return apiClient.updateCharacterProfile(characterId, dataToSave);
            }

            @Override
            protected void done() {
                try {
                    ApiResponse response = get();
                    if (response.isSuccess()) {
                        System.out.println("Progreso del personaje guardado exitosamente.");
                    } else {
                        System.err.println("Error al guardar el progreso del personaje: " + response.getMessage());
                    }
                } catch (Exception e) {
                    System.err.println("Excepción al guardar el progreso: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    // Implementación de la interfaz GameNavigationCallback
    @Override
    public void goToMainMenu() {
        // 1. Guardar el progreso del juego antes de ir al menú principal
        if (gamePanel != null && gamePanel.getPanelJuego() != null) {
            saveCurrentCharacterData(gamePanel.getPanelJuego().getCurrentCharacterData());
        }
        // 2. Detener el juego
        if (gamePanel != null) {
            gamePanel.stopGame();
        }
        // 3. Mostrar el menú principal
        showMainMenu();
    }

    @Override
    public void goToProfile() {
        // 1. Guardar el progreso del juego antes de ir al perfil
        if (gamePanel != null && gamePanel.getPanelJuego() != null) {
            saveCurrentCharacterData(gamePanel.getPanelJuego().getCurrentCharacterData());
        }
        // 2. Detener el juego
        if (gamePanel != null) {
            gamePanel.stopGame();
        }
        // 3. Mostrar la pantalla de perfil
        showProfileScreen();
    }

    private void performLogout() {
        loggedInUserData = null;
        currentCharacterData = null;
        currentRegisteredUserId = null;
        
        // Ocultar todos los paneles de contenido
        if (mainMenuPanel != null) { mainMenuPanel.setVisible(false); }
        if (panelProfileAndInventory != null) { panelProfileAndInventory.setVisible(false); }
        if (panelCharacterCreation != null) { panelCharacterCreation.setVisible(false); }
        if (gamePanel != null) { gamePanel.setVisible(false); } // ¡IMPORTANTE! Ocultar el juego
        
        // Ocultar paneles superpuestos
        loading.setVisible(false);
        verifyCode.setVisible(false);
        forgotPasswordPanel.setVisible(false);
        overlayPanel.setVisible(false);

        // Remover el panel de mensajes si está visible
        if (messagePanelInstance != null && messagePanelInstance.isVisible() && messagePanelInstance.getParent() == getLayeredPane()) {
            getLayeredPane().remove(messagePanelInstance);
        }
        
        // Asegurarse de que el fondo y los paneles de superposición estén en el layered pane y visibles/ocultos según el estado.
        // No se usa removeAll() para evitar tener que re-añadir todo.
        if (fondo.getParent() == null) {
             getLayeredPane().add(fondo, JLayeredPane.DEFAULT_LAYER);
        }
        fondo.setBounds(0, 0, getWidth(), getHeight());
        fondo.setVisible(true);

        if (loading.getParent() == null) { getLayeredPane().add(loading, JLayeredPane.POPUP_LAYER); }
        if (verifyCode.getParent() == null) { getLayeredPane().add(verifyCode, JLayeredPane.POPUP_LAYER); }
        if (overlayPanel.getParent() == null) { getLayeredPane().add(overlayPanel, JLayeredPane.POPUP_LAYER); }
        if (forgotPasswordPanel.getParent() == null) { getLayeredPane().add(forgotPasswordPanel, JLayeredPane.POPUP_LAYER); }

        isLogin = true;
        loginAndRegister.showLogin(true);
        cover.login(isLogin); // Asegurar que el cover muestra el texto correcto para Login
        
        loginAndRegister.clearFields();
        verifyCode.clearFields();
        forgotPasswordPanel.clearFields();
        forgotPasswordPanel.showEmailInput(); // Resetear forgot password panel a la vista de email

        setTitle("VEILWALKER - Login"); // ¡CORREGIDO! Título de la ventana principal
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

   // Clase interna FondoPanel para el fondo de la ventana principal (INTACTA)
    class FondoPanel extends JPanel {
        private Image imagen;

        public FondoPanel() {
            setOpaque(true);
            try {
                URL imageUrl = getClass().getResource("/devt/login/images/guzz2.png");
                if (imageUrl != null) {
                    imagen = new ImageIcon(imageUrl).getImage();
                } else {
                    System.err.println("Error: No se encontró la imagen de fondo en la ruta: /devt/login/images/guzz2.png. Usando color de fondo.");
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
        // Asegúrate de que el LookAndFeel se establezca antes de crear la ventana
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
            LoginBase frame = new LoginBase();
            frame.setVisible(true);
        });
    }
}
