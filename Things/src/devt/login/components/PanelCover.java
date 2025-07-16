package devt.login.components;
//Dibuja elementos graficos como lineas, rectangulos, imagenes, etc.
import devt.login.swing.ButtonOutLine;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

public class PanelCover extends javax.swing.JPanel {

    private final DecimalFormat df = new DecimalFormat("##0.###", DecimalFormatSymbols.getInstance(Locale.US));
    private ActionListener event;
    private MigLayout layout;
    private JLabel title;
    private JLabel description;
    private JLabel description1;
    private ButtonOutLine button;
    private boolean isLogin; // Esta variable controla el contenido del PanelCover
    
    public PanelCover() {
        initComponents();
        setOpaque(false); // indica cuando un componet es trasnparente o no opaco.
        layout = new MigLayout("wrap, fill", "[center]", "push[]25[]10[]25[]push");
        setLayout(layout);
        init(); 
        // Establece el contenido inicial del PanelCover para que coincida con el estado inicial de LoginBase (Login)
        login(true); // Esto mostrará "¡Oye, crack!" en el PanelCover al inicio
    }

    private void init(){
        title = new JLabel("¡Bienvenido a tu aventura!"); // El texto inicial se sobrescribirá por login(true)
        title.setFont(new Font("sansserif", 1, 30));
        title.setForeground(new Color(245, 245, 245));
        add(title);
        description = new JLabel("<html><div style='text-align: center;'>¿Listo para la acción?<br>Tu próxima aventura te espera</div></html>"); // El texto inicial se sobrescribirá
        description.setForeground(new Color(245, 245, 245));
        add(description);
        description1 = new JLabel("inicia sesión con tu información personal"); // El texto inicial se sobrescribirá
        description1.setForeground(new Color(245, 245, 245));
        add(description1);
        button = new ButtonOutLine();
        button.setBorderPainted(false); 
        button.setContentAreaFilled(false); 
        button.setFocusPainted(false);  
        button.setOpaque(false);
        button.setBackground(new Color(255, 255, 255));
        button.setForeground(new Color(255, 255, 255));
        button.setText("INICIAR SESIÓN"); // Este texto se mantendrá constante según tus indicaciones
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                event.actionPerformed(ae);
            }
        });
        add(button, "w 60%, h 40");
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
 
     @Override
    protected void paintComponent (Graphics grphcs){
        Graphics2D g2 = (Graphics2D) grphcs;
        GradientPaint gra = new GradientPaint(0,0, new Color(68,148,125), 0, getHeight(), new Color(70,153,131));
        g2.setPaint(gra);
        g2.fillRect(0, 0,getWidth(),getHeight());
        super.paintComponent(grphcs); 
        
    }
      
    public void addEvent(ActionListener event) {
        this.event = event;
    }
    
    public void registerLeft(double v) {
        v = Double.valueOf(df.format(v));
        login(false); // <--- Esta llamada es la que tu código original tiene
        layout.setComponentConstraints(title, "pad 0 -" + v + "% 0 0");
        layout.setComponentConstraints(description, "pad 0 -" + v + "% 0 0");
        layout.setComponentConstraints(description1, "pad 0 -" + v + "% 0 0");
        layout.setComponentConstraints(button, "pad 0 -" + v + "% 0 0"); // Añadido para animar el botón también
        revalidate();
        repaint();
    }
      
    public void registerRight(double v) {
        v = Double.valueOf(df.format(v));
        login(false); // <--- Esta llamada es la que tu código original tiene
        layout.setComponentConstraints(title, "pad 0 -" + v + "% 0 0");
        layout.setComponentConstraints(description, "pad 0 -" + v + "% 0 0");
        layout.setComponentConstraints(description1, "pad 0 -" + v + "% 0 0");
        layout.setComponentConstraints(button, "pad 0 -" + v + "% 0 0"); // Añadido
        revalidate();
        repaint();
    }

    public void loginLeft(double v) {
        v = Double.valueOf(df.format(v));
        login(true); // <--- Esta llamada es la que tu código original tiene
        layout.setComponentConstraints(title, "pad 0 " + v + "% 0 " + v + "%");
        layout.setComponentConstraints(description, "pad 0 " + v + "% 0 " + v + "%");
        layout.setComponentConstraints(description1, "pad 0 " + v + "% 0 " + v + "%");
        layout.setComponentConstraints(button, "pad 0 " + v + "% 0 " + v + "%"); // Añadido
        revalidate();
        repaint();
    }

    public void loginRight(double v) {
        v = Double.valueOf(df.format(v));
        login(true); // <--- Esta llamada es la que tu código original tiene
        layout.setComponentConstraints(title, "pad 0 " + v + "% 0 " + v + "%");
        layout.setComponentConstraints(description, "pad 0 " + v + "% 0 " + v + "%");
        layout.setComponentConstraints(description1, "pad 0 " + v + "% 0 " + v + "%");
        layout.setComponentConstraints(button, "pad 0 " + v + "% 0 " + v + "%"); // Añadido
        revalidate();
        repaint();
    }
    
    // Este método cambia el contenido de texto del PanelCover
    // 'login': true para el contenido de "¡Oye, crack!" (cuando el panel derecho es Login)
    //          false para el contenido de "¡Bienvenido a tu aventura!" (cuando el panel derecho es Registro)
    public void login(boolean login){ 
        if (this.isLogin != login) {
            if (login) { // Si 'login' es true, significa que el panel DERECHO es LOGIN.
                         // Por lo tanto, el PanelCover (izquierda) debe mostrar el contenido para el estado LOGIN.
                title.setText("¡Oye, crack!");
                description.setText("<html><div style='text-align: center;'>Es momento de insertar tus datos y comenzar la misión<br>¡Prepárate para la locura!</div></html>");
                description1.setText("No olvides tener tu contraseña a la mano"); 
                button.setText("INICIAR SESIÓN"); // Siempre "INICIAR SESIÓN" según tus imágenes
            } else { // Si 'login' es false, significa que el panel DERECHO es REGISTRO.
                      // Por lo tanto, el PanelCover (izquierda) debe mostrar el contenido para el estado REGISTRO.
                title.setText("¡Bienvenido a tu aventura!");
                description.setText("<html><div style='text-align: center;'>¿Listo para la acción?<br>Tu próxima aventura te espera</div></html>");
                description1.setText("inicia sesión con tu información personal");
                button.setText("INICIAR SESIÓN"); // Siempre "INICIAR SESIÓN" según tus imágenes
            }
            this.isLogin = login;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
