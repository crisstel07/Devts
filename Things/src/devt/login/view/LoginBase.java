package devt.login.view;

// Librerias bases 
import devt.login.components.Message;
import devt.login.components.PanelCover;
import devt.login.components.PanelLoading;
import devt.login.components.PanelLoginAndRegister;
import devt.login.components.PanelVerifyCode;
import devt.login.connection.DBConnection;
import devt.login.model.ModelLogin;
import devt.login.model.ModelMessage;
import devt.login.model.ModelUser;
import devt.login.service.ServiceUser;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.miginfocom.swing.MigLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.swing.*;

// Importa tu clase ApiClient y su clase interna ApiResponse
import devt.login.apiFlask.ApiClient;
import devt.login.apiFlask.ApiClient.ApiResponse;

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

        loginAndRegister = new PanelLoginAndRegister(eventRegister, eventLogin);
        initComponents();

        //Se inicializa laa ventana.
        this.setSize(1365, 767); // Tamaño de la ventana
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Creación del Fondo del panel.
        fondo = new FondoPanel();
        layout = new MigLayout("fill, insets 0"); // Aplicamos MigLayout al fondoPanel.
        fondo.setLayout(layout);
        this.setContentPane(fondo); // Se establece como panel principal.
        cover = new PanelCover(); // Inicializa PanelCover

        // Añade los paneles inicializados al fondo
        fondo.add(cover, "width 45%, pos 0al 0 n 100%");// Posicionamos el panel inicialmente a la izquierda
        fondo.add(loginAndRegister, "width 55%, pos 1al 0 n 100%");

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
                
                System.out.println("Java: Intentando verificar con User ID: " + currentRegisteredUserId + " y Código: '" + inputCode + "'");
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
                                String usernameLoggedIn = result.user.get("nombre_usuario").getAsString();
                                String emailLoggedIn = result.user.get("correo").getAsString();
                                int userIdLoggedIn = result.user.get("id").getAsInt();
                                ModelUser loggedInUser = new ModelUser(userIdLoggedIn, usernameLoggedIn, emailLoggedIn, "PASSWORD_NOT_NEEDED_HERE");

                                dispose(); // Cerrar la ventana de Login
                                ViewSystem.main(loggedInUser); // Navegar a la siguiente vista directamente                        
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
                       // verifyCode.setVisible(true);
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

        loading.setVisible(true); // Mostrar la animación de carga

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                // Llama al método loginUser de tu ApiClient
                return ApiClient.loginUser(data.getEmail(), data.getPassword());
            }

            @Override
            protected void done() {
                loading.setVisible(false); // Ocultar la animación de carga

                try {
                    ApiClient.ApiResponse result = get(); // Obtener el resultado
                    if (result.success) {
                        showMessage(Message.MessageType.SUCCESS, result.message);
                        // Aquí, puedes acceder a los datos del usuario logueado
                        // result.user es un JsonObject, puedes acceder a sus propiedades
                        // Por ejemplo: String username = result.user.get("nombre_usuario").getAsString();
                        // int userId = result.user.get("id").getAsInt();

                        // ModelUser userLoggedIn = new ModelUser(userId, username, data.getCorreo(), data.getPassword());
                        // ViewSystem.main(userLoggedIn); // Pasar los datos del usuario a la siguiente vista
                        // Como tu ViewSystem.main() espera un ModelUser, tendrías que construirlo
                        // con la información que viene en result.user.
                        // Adaptamos result.user (JsonObject) a ModelUser
                        String usernameLoggedIn = result.user.get("nombre_usuario").getAsString();
                        String emailLoggedIn = result.user.get("correo").getAsString();
                        int userIdLoggedIn = result.user.get("id").getAsInt();

                        ModelUser loggedInUser = new ModelUser(userIdLoggedIn, usernameLoggedIn, emailLoggedIn, data.getPassword());

                        dispose(); // Cerrar la ventana de Login
                        ViewSystem.main(loggedInUser); // Navegar a la siguiente vista

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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginBase.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginBase.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginBase.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginBase.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        /* Create and display the form */

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginBase.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginBase.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginBase.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginBase.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        try {
            DBConnection.getInstance().connectToDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginBase().setVisible(true);
            }

        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

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
