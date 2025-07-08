package devt.login.view;

// Librerias bases 
import devt.login.components.Message;
import devt.login.components.PanelCover;
import devt.login.components.PanelLoading;
import devt.login.components.PanelLoginAndRegister;
import devt.login.components.PanelVerifyCode;
import devt.login.connection.DBConnection;
import devt.login.model.ModelLogin;
import devt.login.model.ModelUser;
import devt.login.service.ServiceUser;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.swing.*;
import devt.login.view.PanelProfileAndInventory;


// Importa tu clase ApiClient y su clase interna ApiResponse
import devt.login.apiFlask.ApiClient;
import devt.login.apiFlask.ApiClient.ApiResponse;
import com.google.gson.JsonObject;
import devt.login.components.AlphaPanel;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

// MigLayout para organizar componentes de manera facil.
import net.miginfocom.swing.MigLayout;

// Librería para animación (TimingTarget, TimmingTargetAdapter, Animator).
import org.jdesktop.animation.timing.*;

public class LoginBase extends javax.swing.JFrame {

    private FondoPanel fondo; 
    private MigLayout layout; // Layout para posicionar dinámicamente el contenido
    private PanelCover cover; // PAnelCover que se desliza.
    private PanelLoading loading;
    private PanelVerifyCode verifyCode;
    private Integer currentRegisteredUserId;

    private PanelLoginAndRegister loginAndRegister; // Panel del Login y Register
    private Animator animator; // Controlador de animaciones de Trident
    private boolean isLogin;
    private final double addSize = 30;
    private final double coverSize = 45; // Porcentaje del ancho que ocupa el PanelCover
    private final double loginSize = 55;
    private final DecimalFormat df = new DecimalFormat("##0.###", DecimalFormatSymbols.getInstance(Locale.US));
    private ServiceUser service;

     // --- Nuevas variables para el flujo de juego ---
    private JsonObject loggedInUserData; // Datos del usuario logueado (id, nombre_usuario, correo)
    private JsonObject currentCharacterData; // Datos del personaje cargado/creado (id, nombre_personaje, vida, etc.)
    private PanelProfileAndInventory panelProfileAndInventory;  // Instancia del panel de perfil e inventario
    private AlphaPanel overlayPanel;  // Panel para transiciones de fade
 
    public LoginBase() {
        // Estas ActionListeners deben estar aquí porque son parámetros del constructor de PanelLoginAndRegister
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

        // 1. Inicializa loginAndRegister PRIMERO.
        loginAndRegister = new PanelLoginAndRegister(eventRegister, eventLogin);
        initComponents();

        //Se configura laa ventana.
        this.setSize(1365, 767); // Tamaño de la ventana
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Creación y configuración del Fondo del panel.
        fondo = new FondoPanel();
        layout = new MigLayout("fill, insets 0"); // Aplicamos MigLayout al fondoPanel.
        fondo.setLayout(layout);
        this.setContentPane(fondo); // Se establece como panel principal.
        
        cover = new PanelCover(); // Inicializa PanelCover

        // Añade los paneles inicializados al fondo
        fondo.add(cover, "width 45%, pos 0al 0 n 100%");// Posicionamos el panel inicialmente a la izquierda
        fondo.add(loginAndRegister, "width 55%, pos 1al 0 n 100%");

        // --- Inicialización del overlayPanel para transiciones ---
        // Este panel se superpone para crear efectos de desvanecimiento
            // --- Inicialización del overlayPanel para transiciones ---
        // Instanciamos la nueva clase interna que implementa AlphaPanel
        overlayPanel = new OverlayPanelImpl(); 
        
        // Casteamos a JPanel para añadirlo al JLayeredPane y establecer propiedades de JPanel
        // porque JLayeredPane solo acepta componentes Swing (JPanel es un Component)
        this.getLayeredPane().add((JPanel)overlayPanel, JLayeredPane.POPUP_LAYER); // <-- CAMBIO: Añadido (JPanel)
        ((JPanel)overlayPanel).setBounds(0, 0, this.getWidth(), this.getHeight()); // <-- CAMBIO: Añadido (JPanel)
        ((JPanel)overlayPanel).setBackground(java.awt.Color.BLACK); // <-- CAMBIO: Añadido (JPanel)
        ((JPanel)overlayPanel).setOpaque(false); // <-- CAMBIO: Añadido (JPanel)
        ((JPanel)overlayPanel).setVisible(false); // <-- CAMBIO: Añadido (JPanel)
        // --- Fin inicialización overlayPanel ---
        init(); //Se inicializa el contenido visual del PanelCover, para configurar el animador y otros escuchadores de eventos

    }

    // Método que contiene toda la lógica de animación y el listener del botón.
    private void init() {
        loading = new PanelLoading();
        verifyCode = new PanelVerifyCode();
        TimingTarget target = new TimingTargetAdapter() { // Creacion de TimingTarget.
            @Override
            public void timingEvent(float fraction) {
                double fractionCover = isLogin ? 1f - fraction : fraction;
                double fractionLogin = isLogin ? fraction : 1f - fraction;
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
                if (fraction >= 0.5f) {
                    loginAndRegister.showRegister(isLogin);
                }
                fractionCover = Double.valueOf(df.format(fractionCover));
                fractionLogin = Double.valueOf(df.format(fractionLogin));

                layout.setComponentConstraints(cover, "width " + size + "%, pos " + fractionCover + "al 0 n 100%");
                layout.setComponentConstraints(loginAndRegister, "width " + loginSize + "%, pos " + fractionLogin + "al 0 n 100%");
                fondo.revalidate();
            }

            //Se ejecuta cuando termina la animaciòn.
            @Override
            public void end() {
                // Alternamos el estado: si era login, ahora no lo es (y viceversa)
                isLogin = !isLogin;
            }
        };

        animator = new Animator(800, target); // Creacion del animador con duraciòn de 800 milisegundos.
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
        animator.setResolution(0); //Para una animación fluida.
        
        // Añade los paneles de carga y verificación al JLayeredPane del fondo
        fondo.setLayer(loading, JLayeredPane.POPUP_LAYER);
        fondo.setLayer(verifyCode, JLayeredPane.POPUP_LAYER);
        fondo.add(loading, "pos 0 0 100% 100%");
        fondo.add(verifyCode, "pos 0 0 100% 100%");
        
        // Creaciòn de un evento desde el PanelCover que se dispara al presionar su botón
        cover.addEvent(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!animator.isRunning()) { // Inicia la animación si no está corriendo
                    animator.start();
                }
            }
        });
        verifyCode.addEventButtonOK(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // Necesitamos el user_id para la verificación con la API Flask
                // currentRegisteredUserId debe haber sido guardado en el método register()
                if (currentRegisteredUserId == null || currentRegisteredUserId == 0) {
                    showMessage(Message.MessageType.ERROR, "No hay un usuario para verificar. Por favor, regístrate primero.");
                    return;
                }

                String inputCode = verifyCode.getInputCode();
                if (inputCode.isEmpty()) {
                    showMessage(Message.MessageType.ERROR, "Ingresa el código de verificación.");
                    return;
                }
                
                //System.out.println("Java: Intentando verificar con User ID: " + currentRegisteredUserId + " y Código: '" + inputCode + "'");
                loading.setVisible(true); // Mostrar carga mientras se verifica

                new SwingWorker<ApiClient.ApiResponse, Void>() {
                    @Override
                    protected ApiClient.ApiResponse doInBackground() throws Exception {
                        // Llama al método verifyUser de tu ApiClient
                        return ApiClient.verifyUser(currentRegisteredUserId, inputCode);
                    }

                    @Override
                    protected void done() {
                        loading.setVisible(false); // Ocultar carga

                        try {
                            ApiClient.ApiResponse result = get();
                            if (result.success) {
                                showMessage(Message.MessageType.SUCCESS, result.message + " ¡Ingresando!"); // Mensaje más amigable                        verifyCode.setVisible(false); // Ocultar el panel de verificación
                                verifyCode.setVisible(false);
                                // String usernameLoggedIn = result.user.get("nombre_usuario").getAsString();
                                //String emailLoggedIn = result.user.get("correo").getAsString();
                                //int userIdLoggedIn = result.user.get("id").getAsInt();
                                // ModelUser loggedInUser = new ModelUser(userIdLoggedIn, usernameLoggedIn, emailLoggedIn, "PASSWORD_NOT_NEEDED_HERE");

                                //dispose(); // Cerrar la ventana de Login
                                // ViewSystem.main(loggedInUser); // Navegar a la siguiente vista directamente                        
                            
                                  // --- INICIO CAMBIO IMPORTANTE: Después de verificar, intenta cargar/crear personaje ---
                                loadOrCreateCharacterAfterVerification(currentRegisteredUserId);
                                // --- FIN CAMBIO IMPORTANTE ---
                            } else {
                                showMessage(Message.MessageType.ERROR, result.message);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showMessage(Message.MessageType.ERROR, "Error inesperado al verificar: " + ex.getMessage());
                        }
                    }
                }.execute();
            }
        });

    }

    private void register() {
        // Obtener los datos del usuario desde el PanelLoginAndRegister
        // El método getUser() de PanelLoginAndRegister ya hace esto, ¡muy bien!
        ModelUser user = loginAndRegister.getUser();

        // Validación básica
        if (user.getnombre_usuario().isEmpty() || user.getcorreo().isEmpty() || user.getPassword().isEmpty()) {
            showMessage(Message.MessageType.ERROR, "Por favor, llena todos los campos de registro.");
            return;
        }

        // Mostrar la animación de carga
        loading.setVisible(true);

        // Usar SwingWorker para la llamada a la API en segundo plano
        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                // Llama al método registerUser de tu ApiClient
                return ApiClient.registerUser(user.getnombre_usuario(), user.getcorreo(), user.getPassword());
            }

            @Override
            protected void done() {
                // Esto se ejecuta en el EDT (hilo de la UI)
                loading.setVisible(false); // Ocultar la animación de carga

                try {
                    ApiClient.ApiResponse result = get(); // Obtener el resultado de la tarea en segundo plano

                    if (result.success) {
                        showMessage(Message.MessageType.SUCCESS, result.message);
                        // Guardar el user_id retornado por la API Flask para la verificación
                        currentRegisteredUserId = result.user_id;

                        // Ahora, en lugar de enviar correo con ServiceMail, la API Flask ya lo hizo.
                        // Solo necesitamos mostrar el panel de verificación.
                       verifyCode.setVisible(true);
                       verifyCode.putClientProperty("userEmail", user.getcorreo()); // Mostrar email en panel de verificación
                       // notifica al usuario que puede iniciar sesión.  
                        showMessage(Message.MessageType.SUCCESS, "Cuenta verificada. Ahora puedes iniciar sesión.");
                        // Opcional: Puedes pasar el email al panel de verificación para mostrarlo
                       // verifyCode.putClientProperty("userEmail", user.getcorreo());

                    } else {
                        showMessage(Message.MessageType.ERROR, result.message);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace(); // Imprime el stack trace para depuración
                    showMessage(Message.MessageType.ERROR, "Error inesperado al registrar: " + ex.getMessage());
                }
            }
        }.execute(); // Inicia el SwingWorker
    }

    private void login() {
        ModelLogin data = loginAndRegister.getDataLogin();

        if (data.getEmail().isEmpty() || data.getPassword().isEmpty()) {
            showMessage(Message.MessageType.ERROR, "Por favor, ingresa tu correo y contraseña.");
            return;
        }
        loading.setVisible(true); // Mostrar la animación de carga del login
        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                // Llama al método loginUser de tu ApiClient
                return ApiClient.loginUser(data.getEmail(), data.getPassword());
            }

            @Override
            protected void done() {
                loading.setVisible(false); // Ocultar la animación de carga del login
                try {
                    ApiClient.ApiResponse result = get(); // Obtener el resultado
                    if (result.success) {
                        showMessage(Message.MessageType.SUCCESS, result.message);
                        loggedInUserData = result.user; // Guarda los datos del usuario logueado
                        // Aquí, puedes acceder a los datos del usuario logueado
                        // result.user es un JsonObject, puedes acceder a sus propiedades
                        // Por ejemplo: String username = result.user.get("nombre_usuario").getAsString();
                        // int userId = result.user.get("id").getAsInt();

                        // ModelUser userLoggedIn = new ModelUser(userId, username, data.getCorreo(), data.getPassword());
                        // ViewSystem.main(userLoggedIn); // Pasar los datos del usuario a la siguiente vista
                        // Como tu ViewSystem.main() espera un ModelUser, tendrías que construirlo
                        // con la información que viene en result.user.
                        // Adaptamos result.user (JsonObject) a ModelUser
                        //String usernameLoggedIn = result.user.get("nombre_usuario").getAsString();
                        //String emailLoggedIn = result.user.get("correo").getAsString();
                        //int userIdLoggedIn = result.user.get("id").getAsInt();

                       // ModelUser loggedInUser = new ModelUser(userIdLoggedIn, usernameLoggedIn, emailLoggedIn, data.getPassword());

                        //dispose(); // Cerrar la ventana de Login
                        //ViewSystem.main(loggedInUser); // Navegar a la siguiente vista

                       // --- INICIO CAMBIO IMPORTANTE: Llama a la lógica de personaje directamente aquí para login ---
                        loadOrCreateCharacterOnLogin(loggedInUserData.get("id").getAsInt());
                        // --- FIN CAMBIO IMPORTANTE ---
                    } else {
                        showMessage(Message.MessageType.ERROR, result.message);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showMessage(Message.MessageType.ERROR, "Error inesperado al iniciar sesión: " + ex.getMessage());
                }
            }
        }.execute();
    }
   // CREACIÓN DE LOS NUEVOS METODOS A INGRESAR 
    // Nuevo método para cargar o crear el personaje y decidir la siguiente pantalla
    
    // --- NUEVO MÉTODO: Para cargar/crear personaje DESPUÉS de la verificación de REGISTRO ---
    private void loadOrCreateCharacterAfterVerification(int userId) {
        loading.setVisible(true);

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
                    if (result.success && result.character != null) {
                        currentCharacterData = result.character;
                        loggedInUserData = ApiClient.getUserProfile(userId).user; // Asegurarse de tener los datos completos del usuario logueado
                        
                        String characterName = currentCharacterData.has("nombre_personaje") && !currentCharacterData.get("nombre_personaje").isJsonNull()
                                               ? currentCharacterData.get("nombre_personaje").getAsString() : "";

                        // Si el personaje NO tiene nombre, mostrar la pantalla de creación de apodo
                        if (characterName.isEmpty() || characterName.equals("null")) {
                            showCharacterCreationScreen();
                        } else {
                            // Esto no debería pasar en el flujo de registro, pero es una seguridad
                            showMainMenu();
                        }
                    } else {
                        showMessage(Message.MessageType.ERROR, "Error al cargar/crear personaje después de verificación: " + result.message);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showMessage(Message.MessageType.ERROR, "Error inesperado al cargar/crear personaje después de verificación: " + ex.getMessage());
                }
            }
        }.execute();
    }

    // --- NUEVO MÉTODO: Para cargar/crear personaje en el flujo de LOGIN ---
    private void loadOrCreateCharacterOnLogin(int userId) {
        loading.setVisible(true);

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
                    if (result.success && result.character != null) {
                        currentCharacterData = result.character;

                        String characterName = currentCharacterData.has("nombre_personaje") && !currentCharacterData.get("nombre_personaje").isJsonNull()
                                               ? currentCharacterData.get("nombre_personaje").getAsString() : "";

                        // Si el personaje NO tiene nombre, mostrar la pantalla de creación de apodo (esto pasa si el usuario se registró pero nunca puso nombre)
                        if (characterName.isEmpty() || characterName.equals("null")) {
                            showCharacterCreationScreen();
                        } else {
                            // Si el personaje YA tiene nombre, ir directamente al menú principal
                            showMainMenu();
                        }
                    } else {
                        showMessage(Message.MessageType.ERROR, "Error al cargar/crear personaje en login: " + result.message);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showMessage(Message.MessageType.ERROR, "Error inesperado al cargar/crear personaje en login: " + ex.getMessage());
                }
            }
        }.execute();
    }


    // Método para mostrar la pantalla de creación de personaje con transición
    private void showCharacterCreationScreen() {
        if (currentCharacterData == null || !currentCharacterData.has("id")) {
            showMessage(Message.MessageType.ERROR, "Error: Datos de personaje inválidos para la creación.");
            return;
        }
        int characterId = currentCharacterData.get("id").getAsInt();
        
        // Aseguramos que el overlayPanel cubra toda la ventana
        // Casteamos a JPanel porque setBounds, setOpaque, setVisible son métodos de JPanel
        ((JPanel)overlayPanel).setBounds(0, 0, getWidth(), getHeight());
        ((JPanel)overlayPanel).setOpaque(true); // Se vuelve opaco para pintar el fondo
        ((JPanel)overlayPanel).setVisible(true); // Animación de desvanecimiento (Fade In - la pantalla se oscurece)
        Animator fadeInAnimator = new Animator(800, new TimingTargetAdapter() { // 800ms para oscurecer
             @Override
            public void timingEvent(float fraction) {
                overlayPanel.setAlpha(fraction); 
            }


            @Override
            public void end() {
                // --- Una vez completamente oscuro, cambia al CharacterCreationPanel ---
                PanelCharacterCreation creationPanel = new PanelCharacterCreation(characterId);
                
                // Configura el listener para cuando el nombre del personaje es guardado
                creationPanel.addCharacterNameSavedListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Cuando el nombre se guarda, iniciamos la transición al menú principal
                        loading.setVisible(true); // Muestra la pantalla de carga (ya la tienes)
                        // --- INICIO CAMBIO IMPORTANTE: Recargar datos del personaje para asegurar que el nombre esté actualizado ---
                        new SwingWorker<ApiClient.ApiResponse, Void>() {
                            @Override
                            protected ApiClient.ApiResponse doInBackground() throws Exception {
                                // Usar el userId del usuario logueado
                                return ApiClient.getOrCreateCharacterProfile(loggedInUserData.get("id").getAsInt());
                            }
                            @Override
                            protected void done() {
                                loading.setVisible(false);
                                try {
                                    ApiResponse response = get();
                                    if (response.success && response.character != null) {
                                        currentCharacterData = response.character; // Actualizar con el nuevo personaje (ya con nombre)
                                        showMainMenu(); // Ahora sí, ir al menú principal
                                    } else {
                                        showMessage(Message.MessageType.ERROR, "Error al recargar datos del personaje después de nombrar: " + response.message);
                                        // Opcional: Volver a la pantalla de creación de personaje si la recarga falla críticamente
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    showMessage(Message.MessageType.ERROR, "Error inesperado al recargar datos del personaje: " + ex.getMessage());
                                }
                            }
                        }.execute();
                        // --- FIN CAMBIO IMPORTANTE ---
                    }
                });

                // Elimina el contenido actual del JFrame y añade el CharacterCreationPanel
                LoginBase.this.getContentPane().removeAll();
                LoginBase.this.setContentPane(creationPanel);
                creationPanel.setBounds(0, 0, LoginBase.this.getWidth(), LoginBase.this.getHeight());
                LoginBase.this.revalidate();
                LoginBase.this.repaint();

                // Ahora, animación de desvanecimiento (Fade Out - la pantalla se aclara)
                Animator fadeOutAnimator = new Animator(800, new TimingTargetAdapter() { // 800ms para aclarar
                    @Override
                    public void timingEvent(float fraction) {
                        overlayPanel.setAlpha(1.0f - fraction); // Disminuye la opacidad de 1 a 0
                    }

                    @Override
                    public void end() {
                        ((JPanel)overlayPanel).setVisible(false); // Oculta el panel de transición
                        ((JPanel)overlayPanel).setOpaque(false); // Vuelve a hacerlo transparente para futuros usos
                    }
                });
                fadeOutAnimator.setAcceleration(0.5f);
                fadeOutAnimator.setDeceleration(0.5f);
                fadeOutAnimator.setResolution(0);
                fadeOutAnimator.start();
            }
        });
        fadeInAnimator.setAcceleration(0.5f);
        fadeInAnimator.setDeceleration(0.5f);
        fadeInAnimator.setResolution(0);
        fadeInAnimator.start();
    }
    
    // Método para mostrar el menú principal (ViewSystem)
    private void showMainMenu() {
          // Asegúrate de que currentCharacterData esté actualizado con el nombre
        if (loggedInUserData == null || currentCharacterData == null) {
            showMessage(Message.MessageType.ERROR, "Error: No se puede mostrar el menú principal sin datos de usuario/personaje.");
            performLogout(); // Regresar a la pantalla de inicio de sesión si los datos son nulos
            return;
        }

        ViewSystem mainMenuPanel = new ViewSystem(loggedInUserData, currentCharacterData);

        mainMenuPanel.addProfileButtonListener(new ActionListener() {
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

        LoginBase.this.getContentPane().removeAll();
        LoginBase.this.setContentPane(mainMenuPanel);
        mainMenuPanel.setBounds(0, 0, LoginBase.this.getWidth(), LoginBase.this.getHeight());
        LoginBase.this.revalidate();
        LoginBase.this.repaint();
    }
    
    // Método para mostrar la pantalla de perfil e inventario
    private void showProfileScreen() {
        if (loggedInUserData == null || !loggedInUserData.has("id") || currentCharacterData == null || !currentCharacterData.has("id")) {
             showMessage(Message.MessageType.ERROR, "Datos de usuario o personaje no disponibles para el perfil.");
             return;
        }
        int userId = loggedInUserData.get("id").getAsInt();
        int characterId = currentCharacterData.get("id").getAsInt();

        loading.setVisible(true); // Muestra carga mientras se recargan los datos
        new SwingWorker<Map<String, JsonObject>, Void>() {
            @Override
            protected Map<String, JsonObject> doInBackground() throws Exception {
                Map<String, JsonObject> data = new HashMap<>();
                // Recargar datos del personaje (por si cambiaron en el juego)
                ApiResponse profileResponse = ApiClient.getOrCreateCharacterProfile(userId);
                if (profileResponse.success && profileResponse.character != null) {
                    data.put("character", profileResponse.character);
                }
                // Recargar inventario (por si se recogieron ítems en el juego)
                ApiResponse inventoryResponse = ApiClient.getCharacterInventory(characterId);
                if (inventoryResponse.success && inventoryResponse.inventory != null) {
                    data.put("inventory", inventoryResponse.inventory);
                }
                return data;
            }

            @Override
            protected void done() {
                loading.setVisible(false);
                try {
                    Map<String, JsonObject> data = get();
                    if (data != null && data.containsKey("character") && data.containsKey("inventory")) {
                        currentCharacterData = data.get("character"); // Actualizar datos en LoginBase

                        // Si el panel de perfil no ha sido creado, lo creamos
                        if (panelProfileAndInventory == null) {
                            panelProfileAndInventory = new PanelProfileAndInventory(loggedInUserData);
                            panelProfileAndInventory.addLogoutActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    performLogout();
                                }
                            });
                            // Aquí podrías añadir un botón de "Volver al Menú Principal" si lo deseas en ProfileAndInventoryPanel
                            // profileAndInventoryPanel.addBackButtonListener(e -> showMainMenu());
                        }
                        // Cargar/actualizar los datos en el panel de perfil e inventario
                        panelProfileAndInventory.loadData(currentCharacterData, data.get("inventory"));

                        LoginBase.this.getContentPane().removeAll();
                        LoginBase.this.setContentPane(panelProfileAndInventory);
                        panelProfileAndInventory.setBounds(0, 0, LoginBase.this.getWidth(), LoginBase.this.getHeight());
                        LoginBase.this.revalidate();
                        LoginBase.this.repaint();
                    } else {
                        showMessage(Message.MessageType.ERROR, "No se pudieron cargar los datos del perfil/inventario.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(Message.MessageType.ERROR, "Error inesperado al mostrar perfil: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void showMessage(Message.MessageType messageType, String message) {
        Message ms = new Message();
        ms.showMessage(messageType, message);
        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void begin() {
                if (!ms.isShow()) {
                    fondo.add(ms, "pos 0.5al -30", 0); //  Insert to bg fist index 0
                    ms.setVisible(true);
                    fondo.repaint();
                }
            }

            @Override
            public void timingEvent(float fraction) {
                float f;
                if (ms.isShow()) {
                    f = 40 * (1f - fraction);
                } else {
                    f = 40 * fraction;
                }
                layout.setComponentConstraints(ms, "pos 0.5al " + (int) (f - 30));
                fondo.repaint();
                fondo.revalidate();
            }

            @Override
            public void end() {
                if (ms.isShow()) {
                    fondo.remove(ms);
                    fondo.repaint();
                    fondo.revalidate();
                } else {
                    ms.setShow(true);
                }
            }

        };
        Animator animator = new Animator(300, target);
        animator.setResolution(0);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
        animator.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    animator.start();
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
            }
        }).start();
    }

     private void performLogout() {
        // Limpia los datos de sesión del usuario y personaje
        loggedInUserData = null;
        currentCharacterData = null;

        // Vuelve a mostrar el panel de login/registro
        LoginBase.this.getContentPane().removeAll(); // Elimina el contenido actual
        LoginBase.this.setContentPane(fondo); // Vuelve al panel de fondo original
        // Asegúrate de añadir de nuevo los paneles de login y cover al fondo
        fondo.add(cover, "width 45%, pos 0al 0 n 100%");
        fondo.add(loginAndRegister, "width 55%, pos 1al 0 n 100%");
        LoginBase.this.revalidate(); // Revalida el layout
        LoginBase.this.repaint();   // Repinta
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

    public static void main(String args[]) {
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

        try {
            DBConnection.getInstance().connectToDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al conectar a la base de datos: " + e.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Salir si no se puede conectar a la DB
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginBase().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables


class FondoPanel extends JLayeredPane {
    private Image imagen;

    @Override
    public void paint(Graphics g) {
        super.paintComponent(g); // Muy importante: llamar primero al método padre
        imagen = new ImageIcon(getClass().getResource("/devt/login/images/guzz_1.png")).getImage();
        g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        setOpaque(false);
        super.paint(g);
    }
}

// --- ¡CLASE INTERNA PARA EL OVERLAY PANEL! ---
    // Esta clase extiende JPanel e implementa AlphaPanel
    private class OverlayPanelImpl extends JPanel implements AlphaPanel {
        private float alpha = 0.0f;

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }

        @Override
        public void setAlpha(float alpha) {
            this.alpha = alpha;
            repaint();
        }
    }
}