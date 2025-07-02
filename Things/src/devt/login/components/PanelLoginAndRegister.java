
package devt.login.components;
//Dibuja elementos graficos como lineas, rectangulos, imagenes, etc.
import devt.login.model.ModelUser;
import devt.login.swing.Button;
import devt.login.swing.MyPasswordField;
import devt.login.swing.MyTextField;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint; // Pinta el fondo
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class PanelLoginAndRegister extends javax.swing.JLayeredPane {

    private ModelUser user;
    public ModelUser getUser() {
        return user;
    }  
     
    public PanelLoginAndRegister(ActionListener eventRegister) {
        initComponents();
        setOpaque(false); // indica cuando un componet es trasnparente o no opaco.
         
        initRegister(eventRegister);
        initLogin();
        login.setVisible(false);
        register.setVisible(true);
        
        // Paneles trasnparentes (login and register)
        login.setOpaque(false);
        register.setOpaque(false);
        
    }

    private void initRegister(ActionListener eventRegister){
        // Titutlo
        register.setLayout(new  MigLayout("wrap", "push[center]push", "push[]25[]10[]10[]25[]push"));
        
        //Registro
        JLabel label = new JLabel("Crea una Cuenta");
        label.setFont(new Font("sansserif", 1, 30));
        label.setForeground(new Color(68,148,125));
        register.add(label);
        MyTextField txtUser = new  MyTextField();
        txtUser.setPrefixIcon(new ImageIcon(getClass().getResource("/devt/login/images/user.png")));
        txtUser.setHint("Nombre");
        register.add(txtUser, "w 60%");
        
        //Email
        MyTextField txtEmail = new MyTextField();
        txtEmail.setPrefixIcon(new ImageIcon(getClass().getResource("/devt/login/images/mail.png")));
        txtEmail.setHint("Email");
        register.add(txtEmail, "w 60%");
        
        //Password
        MyPasswordField txtPass = new MyPasswordField();
        txtPass.setPrefixIcon(new ImageIcon(getClass().getResource("/devt/login/images/pass.png")));
        txtPass.setHint("Contraseña");
        register.add(txtPass, "w 60%");
        Button cmd = new Button();
        cmd.setBackground(new Color(68,148,125));
        cmd.setForeground(new Color(250, 250, 250));
        cmd.addActionListener(eventRegister);
        cmd.setText("REGISTRARSE");
        register.add(cmd, "w 40%, h 40");
         cmd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String userName = txtUser.getText().trim();
                String email = txtEmail.getText().trim();
                String password = String.valueOf(txtPass.getPassword());
                user = new ModelUser(0, userName, email, password);
            }
        });
    }
    
    private void initLogin (){
     // Tutulo
     login.setLayout(new MigLayout("wrap", "push[center]push", "push[]25[]10[]10[]25[]push"));
     
     //Ingreso  
     JLabel label = new JLabel("Iniciar Sesión");
        label.setFont(new Font("sansserif", 1, 30));
        label.setForeground(new Color(68,148,125));
        login.add(label);
        MyTextField txtEmail = new MyTextField();
        txtEmail.setPrefixIcon(new ImageIcon(getClass().getResource("/devt/login/images/mail.png")));
        txtEmail.setHint("Email");
        login.add(txtEmail, "w 60%");
        
        //Password
        MyPasswordField txtPass = new MyPasswordField();
        txtPass.setPrefixIcon(new ImageIcon(getClass().getResource("/devt/login/images/pass.png")));
        txtPass.setHint("Contraseña");
        login.add(txtPass, "w 60%");
        JButton cmdForget = new JButton("¿ Olvidaste tu contraseña ?");
        cmdForget.setForeground(new Color(100, 100, 100));
        cmdForget.setFont(new Font("sansserif", 1, 12));
        cmdForget.setContentAreaFilled(false); // No pinta el fondo
        cmdForget.setBorderPainted(false); // No dibuja el borde
        cmdForget.setFocusPainted(false); // Quita el efecto de focus (clic)
        cmdForget.setOpaque(false);  //Se asegura de opacar el button.
        cmdForget.setCursor(new Cursor(Cursor.HAND_CURSOR));
        login.add(cmdForget);
        Button cmd = new Button();
        cmd.setBackground(new Color(68,148,125));
        cmd.setForeground(new Color(250, 250, 250));
        cmd.setText("INICIAR SESIÓN");
        login.add(cmd, "w 40%, h 40");
        
    }
    
    public void showRegister(boolean show){
         if (show) {
            register.setVisible(true);
            login.setVisible(false);
        } else {
            register.setVisible(false);
            login.setVisible(true);
        }  
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        login = new javax.swing.JPanel();
        register = new javax.swing.JPanel();

        setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout loginLayout = new javax.swing.GroupLayout(login);
        login.setLayout(loginLayout);
        loginLayout.setHorizontalGroup(
            loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        loginLayout.setVerticalGroup(
            loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        add(login, "card3");

        javax.swing.GroupLayout registerLayout = new javax.swing.GroupLayout(register);
        register.setLayout(registerLayout);
        registerLayout.setHorizontalGroup(
            registerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        registerLayout.setVerticalGroup(
            registerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        add(register, "card2");
    }// </editor-fold>//GEN-END:initComponents

  
     
   
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel login;
    private javax.swing.JPanel register;
    // End of variables declaration//GEN-END:variables
}
