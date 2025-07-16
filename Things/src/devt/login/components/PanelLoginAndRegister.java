package devt.login.components;

import devt.login.swing.Button;
import devt.login.swing.MyPasswordField;
import devt.login.swing.MyTextField;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint; 
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class PanelLoginAndRegister extends javax.swing.JLayeredPane {

    // Campos para el panel de Registro
    private MyTextField txtRegisterUsername; 
    private MyTextField txtRegisterEmail; 
    private MyPasswordField txtRegisterPassword; 
    // private MyPasswordField txtRegisterConfirmPassword; // Si se necesita

    // Campos para el panel de Login
    private MyTextField txtLoginEmail; 
    private MyPasswordField txtLoginPassword; 
    
    public PanelLoginAndRegister(ActionListener eventRegister, ActionListener eventLogin) {
        initComponents(); // Inicializa los JPanels internos (login, register)
        setOpaque(false); // Asegura que este JLayeredPane sea transparente para ver el fondo
        
        // Configura los paneles de login y registro
        // CORRECCIÓN: Usar las variables renombradas
        initRegister(eventRegister);
        initLogin(eventLogin);
        
        // Establece el panel visible inicialmente (Login o Register)
        // Para que coincida con LoginBase, que arranca en Register y anima a Login
        // CORRECCIÓN: Usar las variables renombradas
        login.setVisible(false); // Empieza oculto para que la animación lo revele
        register.setVisible(true); // Empieza visible

        // CORRECCIÓN: Usar las variables renombradas
        login.setOpaque(false);
        register.setOpaque(false);
    }
    
    // --- Getters para los campos de registro (usados por LoginBase) ---
    public String getRegisterUsername() {
        return txtRegisterUsername.getText().trim();
    }

    public String getRegisterEmail() {
        return txtRegisterEmail.getText().trim();
    }

    public String getRegisterPassword() {
        return String.valueOf(txtRegisterPassword.getPassword());
    }
    // Si tienes un campo de confirmación:
    // public String getRegisterConfirmPassword() {
    //     return String.valueOf(txtRegisterConfirmPassword.getPassword());
    // }

    // --- Getters para los campos de login (usados por LoginBase) ---
    public String getLoginEmail() {
        return txtLoginEmail.getText().trim();
    }

    public String getLoginPassword() {
        return String.valueOf(txtLoginPassword.getPassword());
    }

    private void initRegister(ActionListener eventRegister) {
        // CORRECCIÓN: Usar la variable renombrada
        register.setLayout(new MigLayout("wrap", "push[center]push", "push[]25[]10[]10[]25[]push"));
        
        JLabel label = new JLabel("Crea una Cuenta");
        label.setFont(new Font("sansserif", 1, 30));
        label.setForeground(new Color(68, 148, 125));
        // CORRECCIÓN: Usar la variable renombrada
        register.add(label);
        
        txtRegisterUsername = new MyTextField();
        txtRegisterUsername.setPrefixIcon(new ImageIcon(getClass().getResource("/devt/login/images/user.png")));
        txtRegisterUsername.setHint("Nombre de Usuario");
        // CORRECCIÓN: Usar la variable renombrada
        register.add(txtRegisterUsername, "w 60%");
        
        txtRegisterEmail = new MyTextField();
        txtRegisterEmail.setPrefixIcon(new ImageIcon(getClass().getResource("/devt/login/images/mail.png")));
        txtRegisterEmail.setHint("Email");
        // CORRECCIÓN: Usar la variable renombrada
        register.add(txtRegisterEmail, "w 60%");
        
        txtRegisterPassword = new MyPasswordField();
        txtRegisterPassword.setPrefixIcon(new ImageIcon(getClass().getResource("/devt/login/images/pass.png")));
        txtRegisterPassword.setHint("Contraseña");
        // CORRECCIÓN: Usar la variable renombrada
        register.add(txtRegisterPassword, "w 60%");
        
        // Si necesitas un campo de confirmar contraseña, añádelo aquí:
        // txtRegisterConfirmPassword = new MyPasswordField();
        // txtRegisterConfirmPassword.setPrefixIcon(new ImageIcon(getClass().getResource("/devt/login/images/pass.png")));
        // txtRegisterConfirmPassword.setHint("Confirmar Contraseña");
        // register.add(txtRegisterConfirmPassword, "w 60%");
        
        Button cmd = new Button();
        cmd.setBackground(new Color(68, 148, 125));
        cmd.setForeground(new Color(250, 250, 250));
        cmd.addActionListener(eventRegister); 
        cmd.setText("REGISTRARSE");
        // CORRECCIÓN: Usar la variable renombrada
        register.add(cmd, "w 40%, h 40");
    }
    
    private void initLogin(ActionListener eventLogin) {
        // CORRECCIÓN: Usar la variable renombrada
        login.setLayout(new MigLayout("wrap", "push[center]push", "push[]25[]10[]10[]25[]push"));
        
        JLabel label = new JLabel("Iniciar Sesión");
        label.setFont(new Font("sansserif", 1, 30));
        label.setForeground(new Color(68, 148, 125));
        // CORRECCIÓN: Usar la variable renombrada
        login.add(label);
        
        txtLoginEmail = new MyTextField();
        txtLoginEmail.setPrefixIcon(new ImageIcon(getClass().getResource("/devt/login/images/mail.png")));
        txtLoginEmail.setHint("Email");
        // CORRECCIÓN: Usar la variable renombrada
        login.add(txtLoginEmail, "w 60%");
        
        txtLoginPassword = new MyPasswordField();
        txtLoginPassword.setPrefixIcon(new ImageIcon(getClass().getResource("/devt/login/images/pass.png")));
        txtLoginPassword.setHint("Contraseña");
        // CORRECCIÓN: Usar la variable renombrada
        login.add(txtLoginPassword, "w 60%");
        
        JButton cmdForget = new JButton("¿ Olvidaste tu contraseña ?");
        cmdForget.setForeground(new Color(100, 100, 100));
        cmdForget.setFont(new Font("sansserif", 1, 12));
        cmdForget.setContentAreaFilled(false);
        cmdForget.setBorderPainted(false);
        cmdForget.setFocusPainted(false);
        cmdForget.setOpaque(false);
        cmdForget.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // CORRECCIÓN: Usar la variable renombrada
        login.add(cmdForget);
        
        Button cmd = new Button();
        cmd.setBackground(new Color(68, 148, 125));
        cmd.setForeground(new Color(250, 250, 250));
        cmd.addActionListener(eventLogin); 
        cmd.setText("INICIAR SESIÓN");
        // CORRECCIÓN: Usar la variable renombrada
        login.add(cmd, "w 40%, h 40");
    }
    
    /**
     * Muestra el panel de registro u oculta el de login, o viceversa.
     * Esto se usa para las transiciones animadas desde LoginBase.
     * @param showLogin Si es true, muestra el panel de login; si es false, muestra el de registro.
     */
    public void showLogin(boolean showLogin){
        if (showLogin) {
            // CORRECCIÓN: Usar las variables renombradas
            login.setVisible(true);
            register.setVisible(false);
        } else {
            // CORRECCIÓN: Usar las variables renombradas
            login.setVisible(false);
            register.setVisible(true);
        }
        clearFields(); // Limpia los campos al cambiar de vista
    }
    
    /**
     * Limpia todos los campos de texto en los paneles de login y registro.
     */
    public void clearFields() {
        if (txtRegisterUsername != null) txtRegisterUsername.setText("");
        if (txtRegisterEmail != null) txtRegisterEmail.setText("");
        if (txtRegisterPassword != null) txtRegisterPassword.setText("");
        // if (txtRegisterConfirmPassword != null) txtRegisterConfirmPassword.setText("");
        if (txtLoginEmail != null) txtLoginEmail.setText("");
        if (txtLoginPassword != null) txtLoginPassword.setText("");
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
