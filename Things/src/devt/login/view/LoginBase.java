package devt.login.view;

// Librerias bases 
import devt.login.components.Message;
import devt.login.components.PanelCover;
import devt.login.components.PanelLoading;
import devt.login.components.PanelLoginAndRegister;
import devt.login.components.PanelVerifyCode;
import devt.login.connection.DBConnection;
import devt.login.model.ModelMessage;
import devt.login.model.ModelUser;
import devt.login.service.ServiceMail;
import devt.login.service.ServiceUser;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.miginfocom.swing.MigLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.swing.*;

// MigLayout para organizar componentes de manera facil.
import net.miginfocom.swing.MigLayout;

// Librería para animación (TimingTarget, TimmingTargetAdapter, Animator).
import org.jdesktop.animation.timing.*;

public class LoginBase extends javax.swing.JFrame {

    private FondoPanel fondo; // Variable local para acceder desde cualquier metodo
    private MigLayout layout; // Layout para posicionar dinámicamente el contenido
    private PanelCover cover; // PAnelCover que se desliza.
    private PanelLoading loading;
    private PanelVerifyCode verifyCode;
    
    private PanelLoginAndRegister loginAndRegister; // Panel del Login y Register
    private Animator animator; // Controlador de animaciones de Trident
    private boolean isLogin = false;
    private final double addSize = 30;
    private final double coverSize = 45; // Porcentaje del ancho que ocupa el PanelCover
    private final double loginSize = 55;
    private final DecimalFormat df = new DecimalFormat("##0.###");
    private ServiceUser service;
    
    public LoginBase() {
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
        this.setContentPane(fondo); //Se establece como panel principal.
        cover = new PanelCover(); // Instancia del panel que se moverá
        ActionListener eventRegister = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        };
                
        loginAndRegister = new PanelLoginAndRegister(eventRegister);// Instancia del panel LoginAndRegister.
        fondo.add(cover, "width 45%, pos 0al 0 n 100%");// Posicionamos el panel inicialmente a la izquierda
        fondo.add(loginAndRegister, "width 55%, pos 1al 0 n 100%");
        
        init(); //Se inicializa el contenido visual del PanelCover

    }

    // Método que contiene toda la lógica de animación y el listener del botón.
    private void init() {
        service = new ServiceUser();
        loading = new PanelLoading();
        verifyCode = new PanelVerifyCode();
        TimingTarget target = new TimingTargetAdapter() { // Creacion de TimingTarget.
            @Override
            public void timingEvent(float fraction) {
                double fractionCover = isLogin ? 1f - fraction : fraction;
                double fractionLogin = isLogin ? fraction : 1f - fraction;
                double size = coverSize;
                if(fraction <= 0.5f){
                    size += fraction * addSize;
                }else{
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
                fractionCover = Double.valueOf(df.format(fractionCover ));
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
        try {
            ModelUser user = loginAndRegister.getUser();
            String inputCode = verifyCode.getInputCode();
            if (inputCode.equals(user.getVerifyCode())) {
                service.insertUser(user);  // Solo aquí se inserta al usuario
                showMessage(Message.MessageType.SUCCESS, "Registration successful");
                verifyCode.setVisible(false);
            } else {
                showMessage(Message.MessageType.ERROR, "Incorrect verification code");
            }
        } catch (SQLException e) {
            showMessage(Message.MessageType.ERROR, "Error saving user");
            e.printStackTrace();
        }
    }
});
    }
    

   private void register() {
    ModelUser user = loginAndRegister.getUser();
    try {
        if (service.checkDuplicateUser(user.getnombre_usuario())) {
            showMessage(Message.MessageType.ERROR, "User name already exists");
        } else if (service.checkDuplicateEmail(user.getcorreo())) {
            showMessage(Message.MessageType.ERROR, "Email already exists");
        } else {
            String code = service.generateCode();
            user.setVerifyCode(code);
            sendMain(user); // Envía correo
        }
    } catch (SQLException e) {
        e.printStackTrace();  // <- Esto imprimirá la traza completa del error
        showMessage(Message.MessageType.ERROR, "Error Register");
    }
}


     private void sendMain(ModelUser user) {
    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                loading.setVisible(true);
                ModelMessage ms = new ServiceMail().sendMain(user.getcorreo(), user.getVerifyCode());
                if (ms.isSuccess()) {
                    loading.setVisible(false);
                    verifyCode.setVisible(true);
                } else {
                    loading.setVisible(false);
                    showMessage(Message.MessageType.ERROR, ms.getMessage());
                }
            } catch (Exception ex) {
                ex.printStackTrace(); // Imprime cualquier excepción en el envío
                loading.setVisible(false);
                showMessage(Message.MessageType.ERROR, "Error sending mail");
            }
        }
    }).start();
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
