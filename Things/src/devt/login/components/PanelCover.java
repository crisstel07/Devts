package devt.login.components;

import devt.login.swing.ButtonOutLine;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat; // Mantenida
import java.text.DecimalFormatSymbols; // Mantenida
import java.util.Locale; // Mantenida
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class PanelCover extends JPanel {

    private final DecimalFormat df = new DecimalFormat("##0.###", DecimalFormatSymbols.getInstance(Locale.US)); // Mantenida
    private ActionListener event;
    private MigLayout layout;
    private JLabel title;
    private JLabel description;
    private JLabel description1;
    private ButtonOutLine button;
    private boolean isLogin; // Mantenida, como en el código de referencia

    public PanelCover() {
        initComponents();
        setOpaque(false);
        layout = new MigLayout("wrap, fill", "[center]", "push[]25[]10[]25[]push");
        setLayout(layout);
        init();
    }

    private void init() {
        title = new JLabel("¡Bienvenido a tu aventura!"); // Texto inicial, se actualizará por login()
        title.setFont(new Font("sansserif", 1, 30));
        title.setForeground(new Color(245, 245, 245));
        add(title);

        description = new JLabel("<html><div style='text-align: center;'>¿Listo para la acción?<br>Tu próxima aventura te espera</div></html>"); // Texto inicial, se actualizará por login()
        description.setForeground(new Color(245, 245, 245));
        add(description);

        description1 = new JLabel("inicia sesión con tu información personal"); // Texto inicial, se actualizará por login()
        description1.setForeground(new Color(245, 245, 245));
        add(description1);

        button = new ButtonOutLine();
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBackground(new Color(255, 255, 255));
        button.setForeground(new Color(255, 255, 255));
        button.setText("REGISTRARSE"); // Texto inicial para que coincida con LoginBase.isLogin=true (invita a registrarse)
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (event != null) {
                    event.actionPerformed(ae);
                }
            }
        });
        add(button, "w 60%, h 40");
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        GradientPaint gra = new GradientPaint(0, 0, new Color(17,64,90), 0, getHeight(), new Color(5,41,61)); // Tus colores de degradado
        g2.setPaint(gra);
        g2.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(grphcs);
    }

    public void addEvent(ActionListener event) {
        this.event = event;
    }

    // Métodos de animación que ahora solo llaman a login() sin mover los textos internamente
    public void registerLeft(double v) {
        v = Double.valueOf(df.format(v));
        login(false); // Llama a login() para cambiar el texto
        // ELIMINADAS las líneas de layout.setComponentConstraints(title, "pad ...")
    }

    public void registerRight(double v) {
        v = Double.valueOf(df.format(v));
        login(false); // Llama a login() para cambiar el texto
        // ELIMINADAS las líneas de layout.setComponentConstraints(title, "pad ...")
    }

    public void loginLeft(double v) {
        v = Double.valueOf(df.format(v));
        login(true); // Llama a login() para cambiar el texto
        // ELIMINADAS las líneas de layout.setComponentConstraints(title, "pad ...")
    }

    public void loginRight(double v) {
        v = Double.valueOf(df.format(v));
        login(true); // Llama a login() para cambiar el texto
        // ELIMINADAS las líneas de layout.setComponentConstraints(title, "pad ...")
    }

    // Este método gestiona el cambio de texto y botón, como en el código de referencia
    public void login(boolean login) {
        if (this.isLogin != login) {
            if (login) {
                // Estado: Panel derecho muestra LOGIN -> Panel izquierdo debe invitar a REGISTRARSE
                title.setText("¡Bienvenido a tu aventura!");
                description.setText("<html><div style='text-align: center;'>¿Listo para la acción?<br>Tu próxima aventura te espera</div></html>");
                description1.setText("Inicia sesión con tu información personal");
                button.setText("REGISTRARSE"); 
            } else {
                // Estado: Panel derecho muestra REGISTRO -> Panel izquierdo debe invitar a INICIAR SESIÓN
                title.setText("¡Oye, crack!");
                description.setText("<html><div style='text-align: center;'>Es momento de insertar tus datos y comenzar la misión<br>¡Prepárate para la locura!</div></html>");
                description1.setText("No olvides tener tu contraseña a la mano");
                button.setText("INICIAR SESIÓN");
            }
            this.isLogin = login;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}