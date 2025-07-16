package devt.login.view;

// Librerias bases
import devt.login.components.Message;
import devt.login.components.PanelLoading;
import devt.login.components.PanelLoginAndRegister;
import devt.login.components.PanelVerifyCode;

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

// Gson (para JSON)
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

// Importa tu clase ApiClient y su clase interna ApiResponse
import devt.login.apiFlask.ApiClient;
import devt.login.apiFlask.ApiClient.ApiResponse;

// Importaciones de tus paneles personalizados
import devt.login.components.PanelCover;
import devt.login.components.AlphaOverlayPanel; // Importación correcta
import java.net.URL;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import org.jdesktop.animation.timing.*;
import org.jdesktop.animation.timing.interpolation.PropertySetter;


public class LoginBase extends javax.swing.JFrame {

    private LoginBase.FondoPanel fondo; // Ahora será un JPanel simple para el fondo
    private MigLayout layout;
    private PanelCover cover;
    private PanelLoading loading;
    private PanelVerifyCode verifyCode;
    private Integer currentRegisteredUserId; // Para el ID del usuario recién registrado

    private PanelLoginAndRegister loginAndRegister;
    private Animator animator;
    private boolean isLogin; // true = login, false = register
    private final double addSize = 30;
    private final double coverSize = 45;
    private final double loginSize = 55;
    private final DecimalFormat df = new DecimalFormat("##0.###", DecimalFormatSymbols.getInstance(Locale.US));

    private JsonObject loggedInUserData; // Datos del usuario logueado (id, nombre_usuario, correo, foto_perfil_url)
    private JsonObject currentCharacterData; // Datos del personaje cargado/creado (id, nombre_personaje, vida, etc.)
    private PanelProfileAndInventory panelProfileAndInventory; // Instancia del panel de perfil e inventario
    private PanelCharacterCreation panelCharacterCreation; // Instancia del panel de creación de personaje
    private ViewSystem mainMenuPanel; // Instancia del panel del menú principal

    private AlphaOverlayPanel overlayPanel; // Overlay para efectos de carga/transición

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

        initComponents(); // Inicializa componentes generados por NetBeans (si usas el diseñador)

        // Configuración de la ventana principal (LoginBase es el JFrame)
        this.setSize(1365, 767);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Juego RPG - Login"); // Título inicial

        // Creación y configuración del Fondo del panel.
        fondo = new LoginBase.FondoPanel(); // Instancia de la clase interna FondoPanel (ahora un JPanel)
        layout = new MigLayout("fill, insets 0");
        fondo.setLayout(layout);
        
        // --- Gestión de Paneles con JLayeredPane ---
        // El FondoPanel se añade a la capa por defecto del JLayeredPane del JFrame
        this.getLayeredPane().add(fondo, JLayeredPane.DEFAULT_LAYER);
        fondo.setBounds(0, 0, this.getWidth(), this.getHeight()); // Asegura que el fondo cubra todo el JFrame

        // 'cover' se inicializa en el método init() que se llama más abajo.
        // Se añade al 'fondo' (que es un JPanel con MigLayout)
        cover = new PanelCover(); // Inicializa PanelCover aquí para que no sea nulo al añadirlo
        fondo.add(cover, "width 45%, pos 0al 0 n 100%");
        fondo.add(loginAndRegister, "width 55%, pos 1al 0 n 100%");

        // --- Inicialización de overlayPanel, loading, verifyCode en POPUP_LAYER ---
        overlayPanel = new AlphaOverlayPanel(); // Instancia de AlphaOverlayPanel
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

        init(); // Inicializa el contenido visual del PanelCover, animador y otros eventos
        
        // Listener para redimensionamiento de ventana para ajustar los paneles superpuestos
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                // Ajusta el tamaño de todos los paneles que se gestionan con setBounds
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
            }
        });
    }

    // Método que contiene toda la lógica de animación y el listener del botón.
    private void init() {
        // El 'cover' ya se inicializa en el constructor.
        // Aquí se le añade el evento del botón.
        cover.addEvent(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!animator.isRunning()) { // Solo inicia la animación si no está corriendo
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
                if (isLogin) { // Si el estado actual es Login, animar hacia Register
                    fractionCover = 1f - fraction;
                    fractionLogin = fraction;
                    if (fraction <= 0.5f) {
                        size += fraction * addSize;
                    } else {
                        size += addSize - fraction * addSize;
                    }
                } else { // Si el estado actual es Register, animar hacia Login
                    fractionCover = fraction;
                    fractionLogin = 1f - fraction;
                    if (fraction <= 0.5f) {
                        size += fraction * addSize;
                    } else {
                        size += addSize - fraction * addSize;
                    }
                }

                // Actualizar la visibilidad de los paneles de login/registro durante la animación
                if (fraction >= 0.5f) {
                    loginAndRegister.showLogin(!isLogin);
                }

                // Actualizar las restricciones de layout para los componentes
                layout.setComponentConstraints(cover, "width " + df.format(size) + "%, pos " + df.format(fractionCover) + "al 0 n 100%");
                layout.setComponentConstraints(loginAndRegister, "width " + df.format(loginSize) + "%, pos " + df.format(fractionLogin) + "al 0 n 100%");
                fondo.revalidate(); // Revalidar el fondo para aplicar los cambios de layout
            }

            @Override
            public void end() {
                // Al finalizar la animación, invertir el estado de isLogin
                isLogin = !isLogin;
            }
        });
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
        animator.setResolution(0); // Para animaciones más suaves
        
        // Listener para el botón OK del panel de verificación de código
        verifyCode.addEventButtonOK(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // Obtener email y código del PanelVerifyCode
                String email = verifyCode.getEmail(); 
                String inputCode = verifyCode.getInputCode();
                
                if (email == null || email.isEmpty() || inputCode.isEmpty()) {
                    showMessage(Message.MessageType.ERROR, "Ingresa el correo y el código de verificación.");
                    return;
                }

                loading.setVisible(true); // Muestra el panel de carga
                overlayPanel.setVisible(true); // Muestra el overlay

                new SwingWorker<ApiClient.ApiResponse, Void>() {
                    @Override
                    protected ApiClient.ApiResponse doInBackground() throws Exception {
                        // Llama al método de la API para verificar el usuario
                        return ApiClient.verifyUser(email, inputCode);
                    }

                    @Override
                    protected void done() {
                        loading.setVisible(false); // Oculta el panel de carga
                        overlayPanel.setVisible(false); // Oculta el overlay
                        try {
                            ApiClient.ApiResponse result = get();
                            if (result.isSuccess()) {
                                showMessage(Message.MessageType.SUCCESS, result.getMessage());
                                verifyCode.setVisible(false); // Oculta el panel de verificación
                                verifyCode.clearFields(); // Limpia los campos de verificación
                                
                                // Después de verificar, redirigimos al login
                                showMessage(Message.MessageType.INFO, "Verificación exitosa. Por favor, inicia sesión.");
                                loginAndRegister.showLogin(true); // Muestra el panel de login
                            } else {
                                showMessage(Message.MessageType.ERROR, result.getMessage());
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showMessage(Message.MessageType.ERROR, "Error al procesar la verificación: " + ex.getMessage());
                        }
                    }
                }.execute();
            }
        });
    }

    private void register() {
        String username = loginAndRegister.getRegisterUsername(); // Asumiendo que existe este getter
        String email = loginAndRegister.getRegisterEmail(); // Asumiendo que existe este getter
        String password = loginAndRegister.getRegisterPassword(); // Asumiendo que existe este getter

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showMessage(Message.MessageType.ERROR, "Por favor, llena todos los campos de registro.");
            return;
        }

        loading.setVisible(true); // Muestra el panel de carga
        overlayPanel.setVisible(true); // Muestra el overlay

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                return ApiClient.registerUser(username, email, password);
            }

            @Override
            protected void done() {
                loading.setVisible(false); // Oculta el panel de carga
                overlayPanel.setVisible(false); // Oculta el overlay

                try {
                    ApiClient.ApiResponse result = get();
                    if (result.isSuccess()) {
                        showMessage(Message.MessageType.SUCCESS, result.getMessage());
                        // No es necesario currentRegisteredUserId si la verificación usa email
                        // currentRegisteredUserId = result.getDataAsJsonObject().get("user_id").getAsInt(); 
                        verifyCode.setEmail(email); // Pasa el email al panel de verificación
                        verifyCode.setVisible(true);
                        overlayPanel.setVisible(true); // Vuelve a mostrar el overlay para verifyCode
                        loginAndRegister.clearFields(); // Limpia los campos de registro
                    } else {
                        showMessage(Message.MessageType.ERROR, result.getMessage());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showMessage(Message.MessageType.ERROR, "Error inesperado al registrar: " + ex.getMessage());
                }
            }
        }.execute();
    }        

    private void login() {
        String email = loginAndRegister.getLoginEmail(); // CAMBIO: Ahora obtiene el email
        String password = loginAndRegister.getLoginPassword(); 

        if (email.isEmpty() || password.isEmpty()) {
            showMessage(Message.MessageType.ERROR, "Por favor, ingresa tu correo y contraseña.");
            return;
        }

        loading.setVisible(true); // Muestra el panel de carga
        overlayPanel.setVisible(true); // Muestra el overlay

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                // ApiClient.loginUser ahora espera email y password
                return ApiClient.loginUser(email, password);
            }

            @Override
            protected void done() {
                loading.setVisible(false); // Oculta el panel de carga
                overlayPanel.setVisible(false); // Oculta el overlay
                try {
                    ApiClient.ApiResponse result = get();
                    if (result.isSuccess()) {
                        showMessage(Message.MessageType.SUCCESS, result.getMessage());
                        loggedInUserData = result.getDataAsJsonObject(); // Guarda los datos del usuario logueado

                        loginAndRegister.clearFields(); // Limpia los campos de login

                        // FLUJO DE LOGIN: Cargar/Crear Personaje y Transición
                        loadOrCreateCharacter();

                    } else {
                        showMessage(Message.MessageType.ERROR, result.getMessage());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showMessage(Message.MessageType.ERROR, "Error inesperado al iniciar sesión: " + ex.getMessage());
                }
            }
        }.execute();
    }

    // Método para cargar o crear el personaje y decidir la siguiente pantalla
    private void loadOrCreateCharacter() {
        if (loggedInUserData == null || !loggedInUserData.has("id") || loggedInUserData.get("id").isJsonNull()) {
            showMessage(Message.MessageType.ERROR, "Error: No se pudo obtener el ID del usuario logueado para cargar personaje.");
            performLogout();
            return;
        }
        int userId = loggedInUserData.get("id").getAsInt(); // Obtener como int

        loading.setVisible(true); // Muestra el panel de carga
        overlayPanel.setVisible(true); // Muestra el overlay

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                // ApiClient.getOrCreateCharacterProfile espera int userId
                return ApiClient.getOrCreateCharacterProfile(userId);
            }

            @Override
            protected void done() {
                loading.setVisible(false); // Oculta el panel de carga
                overlayPanel.setVisible(false); // Oculta el overlay
                try {
                    ApiClient.ApiResponse result = get();
                    if (result.isSuccess()) {
                        // La API Flask devuelve un JsonObject 'character' directamente en 'data'
                        currentCharacterData = result.getDataAsJsonObject(); 
                        if (currentCharacterData != null) {
                            String characterName = currentCharacterData.has("nombre_personaje") && !currentCharacterData.get("nombre_personaje").isJsonNull()
                                ? currentCharacterData.get("nombre_personaje").getAsString() : null;

                            if (characterName == null || characterName.trim().isEmpty() || characterName.equals("None")) { // "None" si la DB lo guarda así
                                showMessage(Message.MessageType.INFO, "No tienes un personaje. ¡Crea uno ahora!");
                                showCharacterCreationScreen(); // Mostrar la pantalla de creación de personaje
                            } else {
                                showMessage(Message.MessageType.INFO, "Personaje cargado: " + characterName);
                                showMainMenu(); // Ir directamente al menú principal
                            }
                        } else {
                            showMessage(Message.MessageType.ERROR, "Error: Datos de personaje nulos en la respuesta.");
                            performLogout();
                        }
                    } else if (result.getErrorCode() == 404) { // Manejo explícito del 404 (No se encontró personaje)
                         showMessage(Message.MessageType.INFO, "No tienes un personaje. ¡Crea uno ahora!");
                         showCharacterCreationScreen();
                    } else {
                        showMessage(Message.MessageType.ERROR, "Error al verificar personaje: " + result.getMessage());
                        performLogout(); // Si hay un error que no es 404, vuelve al login
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showMessage(Message.MessageType.ERROR, "Error inesperado al cargar/crear personaje: " + ex.getMessage());
                    performLogout();
                }
            }
        }.execute();
    }

    // Método para mostrar la pantalla de creación de personaje con transición
    private void showCharacterCreationScreen() {
        if (loggedInUserData == null || !loggedInUserData.has("id") || loggedInUserData.get("id").isJsonNull()) {
            showMessage(Message.MessageType.ERROR, "Error: Datos de usuario no disponibles para la creación de personaje.");
            performLogout();
            return;
        }
        int userId = loggedInUserData.get("id").getAsInt();
        
        // Oculta los paneles de login/registro
        fondo.setVisible(false); // Oculta el fondo que contiene login/register
        
        // Inicializa el panel de creación de personaje si es nulo
        if (panelCharacterCreation == null) {
            panelCharacterCreation = new PanelCharacterCreation(userId); // Pasa el userId
            panelCharacterCreation.addCharacterCreatedListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Cuando el personaje es creado, recarga sus datos y va al menú principal
                    loadOrCreateCharacter(); // Esto recargará el personaje y llamará a showMainMenu
                }
            });
            // Añade el panel de creación de personaje al JLayeredPane
            this.getLayeredPane().add(panelCharacterCreation, JLayeredPane.DEFAULT_LAYER);
            panelCharacterCreation.setBounds(0, 0, getWidth(), getHeight());
        } else {
            // Si ya existe, solo asegúrate de que esté configurado para el usuario actual
            panelCharacterCreation.setUserId(userId); // Asumiendo que PanelCharacterCreation tiene un setUserId
        }

        panelCharacterCreation.setVisible(true); // Hace visible el panel de creación
        setTitle("Juego RPG - Crea tu Personaje");
        revalidate(); // Revalida el JFrame para que los cambios de visibilidad se apliquen
        repaint(); // Repinta el JFrame
    }

    // Método para mostrar el menú principal (ViewSystem)
    private void showMainMenu() {
        // Oculta los paneles anteriores
        fondo.setVisible(false); // Oculta el fondo (login/register)
        if (panelCharacterCreation != null) {
            panelCharacterCreation.setVisible(false);
        }
        if (panelProfileAndInventory != null) {
            panelProfileAndInventory.setVisible(false);
        }

        if (mainMenuPanel == null) {
            mainMenuPanel = new ViewSystem(loggedInUserData, currentCharacterData);
            // Añadir listeners a los botones de ViewSystem
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
            // Agrega el menú principal al JLayeredPane en la capa por defecto
            this.getLayeredPane().add(mainMenuPanel, JLayeredPane.DEFAULT_LAYER);
            mainMenuPanel.setBounds(0, 0, getWidth(), getHeight());
        } else {
            // Si el panel ya existe, actualiza sus datos
            mainMenuPanel.updateUserData(loggedInUserData);
            mainMenuPanel.updateCharacterData(currentCharacterData);
        }

        mainMenuPanel.setVisible(true); // Hace visible el menú principal
        setTitle("Juego RPG - Menú Principal");
        revalidate();
        repaint();
    }
        
    // Método para mostrar la pantalla de perfil e inventario
    private void showProfileScreen() {
        if (loggedInUserData == null || !loggedInUserData.has("id") || loggedInUserData.get("id").isJsonNull() || currentCharacterData == null || !currentCharacterData.has("id") || currentCharacterData.get("id").isJsonNull()) {
            showMessage(Message.MessageType.ERROR, "Datos de usuario o personaje no disponibles para el perfil.");
            performLogout();
            return;
        }

        // Oculta el menú principal
        if (mainMenuPanel != null) {
            mainMenuPanel.setVisible(false);
        }

        if (panelProfileAndInventory == null) {
            // El constructor de PanelProfileAndInventory ahora solo toma loggedInUserData
            panelProfileAndInventory = new PanelProfileAndInventory(loggedInUserData); 
            
            // Añade listener para volver al menú principal
            panelProfileAndInventory.addBackToMainMenuListener(new ActionListener() { 
                @Override
                public void actionPerformed(ActionEvent e) {
                    showMainMenu(); // Llama a showMainMenu para volver
                }
            });
            
            // Añade el listener para el botón de cerrar sesión
            panelProfileAndInventory.addLogoutActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    performLogout(); // Llama a performLogout()
                }
            });

            this.getLayeredPane().add(panelProfileAndInventory, JLayeredPane.DEFAULT_LAYER);
            panelProfileAndInventory.setBounds(0, 0, getWidth(), getHeight());
        }
        
        // Llamar a loadData con el campo de la clase currentCharacterData
        panelProfileAndInventory.loadData(this.currentCharacterData); 

        panelProfileAndInventory.setVisible(true);
        setTitle("Juego RPG - Perfil e Inventario");
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
                    // Asegúrate de que 'fondo' esté visible y sea el contenedor correcto
                    // para añadir el mensaje. Si fondo está oculto, el mensaje no se verá.
                    // Si fondo es un JLayeredPane, se comporta diferente.
                    // Si fondo es un JPanel, debe estar en un JLayeredPane del JFrame.
                    // Asumiendo que fondo es un JPanel y está en el JLayeredPane del JFrame:
                    getLayeredPane().add(ms, JLayeredPane.PALETTE_LAYER); // Añadir a una capa superior
                    ms.setBounds( (getWidth() - ms.getPreferredSize().width) / 2, -30, ms.getPreferredSize().width, ms.getPreferredSize().height);
                    ms.setVisible(true);
                    getLayeredPane().repaint();
                }
            }

            @Override
            public void timingEvent(float fraction) {
                float f;
                if (ms.isShow()) { // Animación de entrada
                    f = 40 * fraction; // Baja desde -30 a 10
                } else { // Animación de salida
                    f = 40 * (1f - fraction); // Sube de 10 a -30
                }
                ms.setLocation((getWidth() - ms.getPreferredSize().width) / 2, (int) (f - 30));
                getLayeredPane().revalidate();
                getLayeredPane().repaint();
            }

            @Override
            public void end() {
                if (ms.isShow()) {
                    // El mensaje está visible y la animación de entrada terminó
                    // Ahora espera 2 segundos antes de iniciar la animación de salida
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            // Inicia la animación de salida (inviertiendo el estado de isShow)
                            ms.setShow(false); // Indica que debe animarse para salir
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
                                    getLayeredPane().remove(ms); // Elimina el mensaje al final de la animación de salida
                                    getLayeredPane().revalidate();
                                    getLayeredPane().repaint();
                                }
                            });
                            exitAnimator.setResolution(0);
                            exitAnimator.setAcceleration(0.5f);
                            exitAnimator.setDeceleration(0.5f);
                            exitAnimator.start();
                        } catch (InterruptedException e) {
                            System.err.println("Error en el hilo del mensaje: " + e.getMessage());
                        }
                    }).start();
                } else {
                    // El mensaje ya no está visible (animación de salida terminó)
                    // No hacer nada aquí, ya se eliminó en el end del exitAnimator
                }
            }
        };
        Animator animator = new Animator(300, target);
        animator.setResolution(0);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
        animator.start(); // Inicia la animación de entrada
    }

    // Método para cerrar sesión
    private void performLogout() {
        // Limpia los datos de sesión del usuario y personaje
        loggedInUserData = null;
        currentCharacterData = null;
        currentRegisteredUserId = null;
        
        // Oculta todos los paneles de juego y auxiliares
        if (mainMenuPanel != null) mainMenuPanel.setVisible(false);
        if (panelProfileAndInventory != null) panelProfileAndInventory.setVisible(false);
        if (panelCharacterCreation != null) panelCharacterCreation.setVisible(false);
        loading.setVisible(false);
        verifyCode.setVisible(false);
        overlayPanel.setVisible(false);

        // Asegúrate de que el fondo y los paneles de login/registro estén en el JLayeredPane
        // y sean visibles.
        // Primero, limpia todos los componentes del JLayeredPane para evitar duplicados
        getLayeredPane().removeAll();
        
        // Vuelve a añadir el fondo y los paneles de login/registro
        getLayeredPane().add(fondo, JLayeredPane.DEFAULT_LAYER);
        fondo.setBounds(0, 0, getWidth(), getHeight());
        fondo.setVisible(true); // Asegúrate de que el fondo sea visible

        // Vuelve a añadir los paneles de carga y verificación a la capa POPUP_LAYER
        getLayeredPane().add(loading, JLayeredPane.POPUP_LAYER);
        getLayeredPane().add(verifyCode, JLayeredPane.POPUP_LAYER);
        getLayeredPane().add(overlayPanel, JLayeredPane.POPUP_LAYER);
        
        // Asegura que el panel de login/registro se muestre correctamente
        // El `fondo` ya los contiene y su `MigLayout` los gestiona.
        // Solo necesitamos asegurarnos de que el estado de `isLogin` sea el correcto para la animación inicial.
        isLogin = true; // Establece el estado para que la animación vaya a login
        loginAndRegister.showLogin(true); // Asegura que el panel de login esté visible

        // Limpiar campos de login/registro
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
    // Ahora es un JPanel simple, no un JLayeredPane, para simplificar la gestión de capas.
    class FondoPanel extends JPanel {
        private Image imagen;

        public FondoPanel() {
            setOpaque(true); // Asegura que el panel dibuje su fondo
            try {
                // Asegúrate de que esta ruta sea correcta para tu imagen de fondo principal
                imagen = new ImageIcon(getClass().getResource("/devt/login/images/guzz_1.png")).getImage();
            } catch (Exception e) {
                System.err.println("Error al cargar imagen de fondo: " + e.getMessage());
                setBackground(new Color(20, 20, 20)); // Fondo de color si la imagen falla
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
        // Establecer el Look and Feel (Nimbus es bueno)
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

        // Ejecutar la aplicación en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new LoginBase().setVisible(true);
        });
    }
}
