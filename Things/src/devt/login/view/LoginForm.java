package devt.login.view;

// Librerias bases 
import devt.login.components.PanelCover;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

// MigLayout para organizar componentes de manera facil.
import net.miginfocom.swing.MigLayout;

// Librería para animación (TimingTarget, TimmingTargetAdapter, Animator).
import org.jdesktop.animation.timing.*;

public class LoginForm extends javax.swing.JFrame {

    private MigLayout layout; // Layout para posicionar dinámicamente el contenido
    private PanelCover cover; // PAnelCover que se desliza.
    private Animator animator; // Controlador de animaciones de Trident
    private boolean isLogin = false;
    private final double coverSize = 40; // Porcentaje del ancho que ocupa el PanelCover
    private FondoPanel fondo; // Variable local para acceder desde cualquier metodo
    
    
    public LoginForm() {
        initComponents();

        // Creación del Fondo en un panel.
        fondo = new FondoPanel();
        layout = new MigLayout("fill, insets 0"); // Aplicamos MigLayout al fondoPanel.
        fondo.setLayout(layout);
        this.setContentPane(fondo); //Se establece como panel principal.

        cover = new PanelCover(); // Instancia del panel que se moverá
        fondo.add(cover, "width 40%, pos 0al 0 n 100%");// Posicionamos el panel inicialmente a la izquierda

        //Se inicializa laa ventana.
        this.setSize(1365, 767); // Tamaño de la ventana
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Se inicializa el contenido visual del PanelCover
        init();
    }

    // Método que contiene toda la lógica de animación y el listener del botón.
    private void init() {

        TimingTarget target = new TimingTargetAdapter() { // Creacion de TimingTarget.
            @Override
            public void timingEvent(float fraction) {
                double fractionCover = isLogin ? 1f - fraction : fraction;
                layout.setComponentConstraints(cover, "width 40%, pos " + fractionCover + "al 0 n 100%");
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
        animator.setResolution(10); // Suavidad de la animación

        // Creaciòn de un evento desde el PanelCover que se dispara al presionar su botón
        cover.addEvent(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!animator.isRunning()) { // Inicia la animación si no está corriendo
                    animator.start();
                }
            }
        });
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
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginForm().setVisible(true);
            }
            
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

class FondoPanel extends JPanel {

    private Image imagen;

    @Override
    public void paint(Graphics g) {
        imagen = new ImageIcon(getClass().getResource("/devt/login/images/Guzz.png")).getImage();
        g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        setOpaque(false);
        super.paint(g);
    }
}
