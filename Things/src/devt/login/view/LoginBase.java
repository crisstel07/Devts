package devt.login.view;

// Librerias bases
import devt.login.components.Message;
// import devt.login.components.FondoPanel; // Esta línea ya no es necesaria si FondoPanel es una clase interna
import devt.login.components.PanelLoading;
import devt.login.components.PanelLoginAndRegister;
import devt.login.components.PanelVerifyCode;
import devt.login.connection.DBConnection;
import devt.login.model.ModelLogin;
import devt.login.model.ModelUser;

// Librerias de Java de AWT Y Swing
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.miginfocom.swing.MigLayout;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

// Gson (para JSON)
import com.google.gson.JsonObject;

// Importa tu clase ApiClient y su clase interna ApiResponse
import devt.login.apiFlask.ApiClient;
import devt.login.apiFlask.ApiClient.ApiResponse;

// Importaciones de tus paneles personalizados
import devt.login.components.PanelCover;
// Librería para animación (TimingTarget, TimmingTargetAdapter, Animator).
import org.jdesktop.animation.timing.*;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

// Importa la interfaz AlphaPanel y la clase AlphaOverlayPanel
import devt.login.components.AlphaPanel; // Interfaz
import devt.login.components.AlphaOverlayPanel; // Clase concreta que implementa AlphaPanel

public class LoginBase extends javax.swing.JFrame {

    private FondoPanel fondo; // Ahora se refiere a la clase interna FondoPanel
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

    private JsonObject loggedInUserData; // Datos del usuario logueado (id, nombre_usuario, correo)
    private JsonObject currentCharacterData; // Datos del personaje cargado/creado (id, nombre_personaje, vida, etc.)
    private PanelProfileAndInventory panelProfileAndInventory; // Instancia del panel de perfil e inventario
    private AlphaOverlayPanel overlayPanel; // Declarado como la clase concreta AlphaOverlayPanel

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

        // Configuración de la ventana principal
        this.setSize(1365, 767);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Creación y configuración del Fondo del panel.
        fondo = new FondoPanel(); // Instancia de la clase interna FondoPanel
        layout = new MigLayout("fill, insets 0");
        fondo.setLayout(layout);
        this.setContentPane(fondo); // Establece el FondoPanel como el content pane principal

        cover = new PanelCover(); // Inicializa PanelCover
        
        // Añade los paneles inicializados al fondo (content pane)
        fondo.add(cover, "width 45%, pos 0al 0 n 100%");
        fondo.add(loginAndRegister, "width 55%, pos 1al 0 n 100%");

        // --- Inicialización del overlayPanel para transiciones ---
        // Instanciamos la clase AlphaOverlayPanel
        overlayPanel = new AlphaOverlayPanel(); 
        overlayPanel.setVisible(false); // Inicialmente invisible
        // Añade el overlayPanel al JLayeredPane de la ventana para que esté por encima de otros paneles
        this.getLayeredPane().add(overlayPanel, JLayeredPane.POPUP_LAYER);
        overlayPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
        // --- Fin inicialización overlayPanel ---

        init(); // Inicializa el contenido visual del PanelCover, animador y otros eventos
    }

    // Método que contiene toda la lógica de animación y el listener del botón.
    private void init() {
        loading = new PanelLoading();
        verifyCode = new PanelVerifyCode();
        TimingTarget target = new TimingTargetAdapter() {
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

            @Override
            public void end() {
                isLogin = !isLogin;
            }
        };

        animator = new Animator(800, target);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
        animator.setResolution(0);
        
        // Añade los paneles de carga y verificación al JLayeredPane del fondo
        fondo.setLayer(loading, JLayeredPane.POPUP_LAYER);
        fondo.setLayer(verifyCode, JLayeredPane.POPUP_LAYER);
        fondo.add(loading, "pos 0 0 100% 100%");
        fondo.add(verifyCode, "pos 0 0 100% 100%");

        cover.addEvent(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!animator.isRunning()) {
                    animator.start();
                }
            }
        });

        verifyCode.addEventButtonOK(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (currentRegisteredUserId == null || currentRegisteredUserId == 0) {
                    showMessage(Message.MessageType.ERROR, "No hay un usuario para verificar. Por favor, regístrate primero.");
                    return;
                }

                String inputCode = verifyCode.getInputCode();
                if (inputCode.isEmpty()) {
                    showMessage(Message.MessageType.ERROR, "Ingresa el código de verificación.");
                    return;
                }

                loading.setVisible(true);

                new SwingWorker<ApiClient.ApiResponse, Void>() {
                    @Override
                    protected ApiClient.ApiResponse doInBackground() throws Exception {
                        return ApiClient.verifyUser(currentRegisteredUserId, inputCode);
                    }

                    @Override
                    protected void done() {
                        loading.setVisible(false);
                        try {
                            ApiClient.ApiResponse result = get();
                            if (result.success) {
                                showMessage(Message.MessageType.SUCCESS, result.message);
                                verifyCode.setVisible(false);
                                
                                // --- CAMBIO CLAVE: Después de verificar, intentar iniciar sesión automáticamente ---
                                // Esto es necesario porque la API /verify no devuelve los datos completos del usuario.
                                // Al hacer login, se obtienen los datos del usuario y se inicia el flujo del personaje.
                                ModelLogin tempLoginData = new ModelLogin();
                                tempLoginData.setEmail((String) verifyCode.getClientProperty("userEmail")); // Recuperar el email usado en el registro
                                // IMPORTANTE: Si la contraseña no se guarda en ningún lado después del registro,
                                // necesitarías que el usuario la reingrese o que la API de verificación devuelva
                                // un token de sesión para evitar pedirla de nuevo.
                                // Por ahora, asumimos que la contraseña se puede recuperar del campo de login/registro
                                // o que el usuario la reingresará si es necesario.
                                // Para esta implementación, se asume que la contraseña está disponible en loginAndRegister.getDataLogin().getPassword()
                                // o que el ModelUser del registro inicial tiene la contraseña.
                                // Si el usuario no ha ingresado la contraseña en el campo de login (porque está en la pantalla de verificación),
                                // esta línea podría causar un NPE o una contraseña vacía.
                                // Una solución robusta sería pedir al usuario que reingrese la contraseña para el login
                                // o que la API de verificación devuelva un token de sesión.
                                // Para mantener el flujo, usaremos la contraseña del ModelUser que se usó para el registro.
                                tempLoginData.setPassword(loginAndRegister.getUser().getPassword()); 

                                new SwingWorker<ApiClient.ApiResponse, Void>() {
                                    @Override
                                    protected ApiClient.ApiResponse doInBackground() throws Exception {
                                        return ApiClient.loginUser(tempLoginData.getEmail(), tempLoginData.getPassword());
                                    }
                                    @Override
                                    protected void done() {
                                        try {
                                            ApiClient.ApiResponse loginAfterVerifyResult = get();
                                            if (loginAfterVerifyResult.success && loginAfterVerifyResult.user != null) {
                                                loggedInUserData = loginAfterVerifyResult.user; // Establecer loggedInUserData
                                                loadOrCreateCharacter(); // Continuar con el flujo de personaje
                                            } else {
                                                showMessage(Message.MessageType.ERROR, "Error al iniciar sesión automáticamente después de la verificación: " + loginAfterVerifyResult.message);
                                                performLogout(); // Si no se puede loguear, cerrar sesión
                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            showMessage(Message.MessageType.ERROR, "Error inesperado al intentar iniciar sesión después de la verificación: " + ex.getMessage());
                                            performLogout();
                                        }
                                    }
                                }.execute();
                                // --- FIN CAMBIO CLAVE ---

                            } else {
                                showMessage(Message.MessageType.ERROR, result.message);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace(); // Imprime el stack trace para depuración
                            showMessage(Message.MessageType.ERROR, "Error al procesar la verificación: " + ex.getMessage());
                        }
                    }
                }.execute();
            }
        });
    }

    private void register() {
        ModelUser user = loginAndRegister.getUser();

        if (user.getnombre_usuario().isEmpty() || user.getcorreo().isEmpty() || user.getPassword().isEmpty()) {
            showMessage(Message.MessageType.ERROR, "Por favor, llena todos los campos de registro.");
            return;
        }

        loading.setVisible(true);

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                return ApiClient.registerUser(user.getnombre_usuario(), user.getcorreo(), user.getPassword());
            }

            @Override
            protected void done() {
                loading.setVisible(false);

                try {
                    ApiClient.ApiResponse result = get();

                    if (result.success) {
                        showMessage(Message.MessageType.SUCCESS, result.message);
                        currentRegisteredUserId = result.user_id; // Guarda el ID del usuario registrado
                        verifyCode.setVisible(true);
                        verifyCode.putClientProperty("userEmail", user.getcorreo()); // Mostrar email en panel de verificación
                        // Es importante que la contraseña también se guarde o se recupere para el auto-login después de la verificación
                        // Aquí, la contraseña se asume que está en ModelUser que se pasó al registro.
                    } else {
                        showMessage(Message.MessageType.ERROR, result.message);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace(); // Imprime el stack trace para depuración
                    showMessage(Message.MessageType.ERROR, "Error inesperado al registrar: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void login() {
        ModelLogin data = loginAndRegister.getDataLogin();

        if (data.getEmail().isEmpty() || data.getPassword().isEmpty()) {
            showMessage(Message.MessageType.ERROR, "Por favor, ingresa tu correo y contraseña.");
            return;
        }

        loading.setVisible(true); // Muestra la pantalla de carga del login

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                return ApiClient.loginUser(data.getEmail(), data.getPassword());
            }

            @Override
            protected void done() {
                loading.setVisible(false); // Oculta la carga del login
                try {
                    ApiClient.ApiResponse result = get();
                    if (result.success) {
                        showMessage(Message.MessageType.SUCCESS, result.message);
                        loggedInUserData = result.user; // Guarda los datos del usuario logueado

                        // --- FLUJO DE LOGIN: Cargar/Crear Personaje y Transición ---
                        loadOrCreateCharacter(); // Llama a este método para decidir si va a apodo o menú

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

    // Método para cargar o crear el personaje y decidir la siguiente pantalla
    private void loadOrCreateCharacter() {
        if (loggedInUserData == null || !loggedInUserData.has("id")) {
            showMessage(Message.MessageType.ERROR, "Error: No se pudo obtener el ID del usuario logueado.");
            return;
        }
        int userId = loggedInUserData.get("id").getAsInt();

        loading.setVisible(true); // Muestra la pantalla de carga mientras se carga/crea el personaje

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                return ApiClient.getOrCreateCharacterProfile(userId);
            }

            @Override
            protected void done() {
                loading.setVisible(false); // Oculta la carga
                try {
                    ApiClient.ApiResponse result = get();
                    if (result.success && result.character != null) {
                        currentCharacterData = result.character; // Guarda los datos del personaje

                        // Verificar si el personaje ya tiene un nombre (si es NULL o vacío)
                        String characterName = "";
                        if (currentCharacterData.has("nombre_personaje") && !currentCharacterData.get("nombre_personaje").isJsonNull()) {
                            characterName = currentCharacterData.get("nombre_personaje").getAsString();
                        }

                        if (characterName.trim().isEmpty()) { // Usamos trim().isEmpty() para robustez
                            showCharacterCreationScreen(); // Mostrar la pantalla de creación de apodo
                        } else {
                            showMainMenu(); // Ir directamente al menú principal
                        }
                    } else {
                        showMessage(Message.MessageType.ERROR, "Error al cargar/crear personaje: " + result.message);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showMessage(Message.MessageType.ERROR, "Error inesperado al cargar/crear personaje: " + ex.getMessage());
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
        overlayPanel.setBounds(0, 0, getWidth(), getHeight());
        overlayPanel.setOpaque(true); // Se vuelve opaco para pintar el fondo
        overlayPanel.setVisible(true); // Hace visible el overlay para el fade-in

        // Animación de desvanecimiento (Fade In - la pantalla se oscurece)
        Animator fadeInAnimator = new Animator(800, new TimingTargetAdapter() { // 800ms para oscurecer
            @Override
            public void timingEvent(float fraction) {
                overlayPanel.setAlpha(fraction); // Aumenta la opacidad de 0 a 1
            }

            @Override
            public void end() {
                // --- Una vez completamente oscuro, cambia al CharacterCreationPanel ---
                PanelCharacterCreation creationPanel = new PanelCharacterCreation(characterId);
                
                // Configura el listener para cuando el nombre del personaje es guardado
                creationPanel.addCharacterNameSavedListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        loading.setVisible(true); // Muestra la pantalla de carga
                        // --- INICIO CAMBIO CLAVE: Recargar datos del personaje después de guardar el apodo ---
                        new SwingWorker<ApiClient.ApiResponse, Void>() {
                            @Override
                            protected ApiClient.ApiResponse doInBackground() throws Exception {
                                // Asegurarse de usar el userId del usuario logueado para recargar el personaje
                                return ApiClient.getOrCreateCharacterProfile(loggedInUserData.get("id").getAsInt());
                            }
                            @Override
                            protected void done() {
                                loading.setVisible(false); // Oculta la carga
                                try {
                                    ApiResponse response = get();
                                    if (response.success && response.character != null) {
                                        currentCharacterData = response.character; // Actualizar con el nuevo personaje (ya con nombre)
                                        showMainMenu(); // Ahora sí, ir al menú principal
                                    } else {
                                        showMessage(Message.MessageType.ERROR, "Error al recargar datos del personaje después de nombrar: " + response.message);
                                        // Si falla la recarga, podríamos volver al login o intentar de nuevo
                                        performLogout(); // Opción de seguridad: cerrar sesión
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    showMessage(Message.MessageType.ERROR, "Error inesperado al recargar datos del personaje: " + ex.getMessage());
                                }
                            }
                        }.execute();
                        // --- FIN CAMBIO CLAVE ---
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
                        overlayPanel.setVisible(false); // Oculta el panel de transición
                        overlayPanel.setOpaque(false); // Vuelve a hacerlo transparente para futuros usos
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

                        if (panelProfileAndInventory == null) {
                            panelProfileAndInventory = new PanelProfileAndInventory(loggedInUserData);
                            panelProfileAndInventory.addLogoutActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    performLogout();
                                }
                            });
                            // Aquí podrías añadir un botón de "Volver al Menú Principal" si lo deseas en ProfileAndInventoryPanel
                            // panelProfileAndInventory.addBackButtonListener(e -> showMainMenu());
                        }
                        panelProfileAndInventory.loadData(currentCharacterData, data.get("inventory")); // Cargar/actualizar datos en el panel

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
                    fondo.add(ms, "pos 0.5al -30", 0);
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
// Clase interna FondoPanel para el fondo de la ventana principal
// Si prefieres que sea una clase separada, muévela a su propio archivo .java
// en el paquete devt.login.components y añade su importación.
// Clase interna FondoPanel para el fondo de la ventana principal
    // Se mantiene aquí como clase interna, como lo tenías originalmente.
    class FondoPanel extends JLayeredPane {
        private Image imagen;

        public FondoPanel() {
            // Llama a setOpaque(false) en el constructor para que el fondo sea transparente
            // si la imagen no cubre todo o si quieres que se vea algo debajo.
            // Si la imagen SIEMPRE cubre todo el panel, puedes dejarlo opaco (true)
            // y solo dibujar la imagen. Para un fondo completo, lo más común es dejarlo opaco.
            setOpaque(true); // Se establece como opaco por defecto para el fondo
        }

        @Override
        protected void paintComponent(Graphics g) { // Usa paintComponent para dibujo personalizado
            super.paintComponent(g); // Muy importante: llamar primero al método padre
            // Asegúrate de que la ruta de la imagen sea correcta y la imagen exista en src/main/resources/devt/login/images/
            imagen = new ImageIcon(getClass().getResource("/devt/login/images/guzz_1.png")).getImage();
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
            // super.paint(g); // <-- ¡ESTA LÍNEA SE ELIMINA! No se llama a super.paint(g) dentro de paintComponent
        }
    }
}
