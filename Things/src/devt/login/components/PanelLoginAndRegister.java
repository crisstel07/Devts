package devt.login.components;

import devt.login.swing.Button;
import devt.login.swing.MyPasswordField;
import devt.login.swing.MyTextField;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import java.awt.CardLayout; // ¡IMPORTANTE: Importar CardLayout!

public class PanelLoginAndRegister extends javax.swing.JLayeredPane {

    // Campos para el panel de Registro
    private MyTextField txtRegisterUsername;
    private MyTextField txtRegisterEmail;
    private MyPasswordField txtRegisterPassword;
    // private MyPasswordField txtRegisterConfirmPassword; // Si se necesita

    // Campos para el panel de Login
    private MyTextField txtLoginEmail;
    private MyPasswordField txtLoginPassword;

    private CardLayout cardLayout; // <-- ¡NUEVA VARIABLE PARA ALMACENAR EL CARDLAYOUT!

    // ¡NUEVO! Variables para ActionListeners
    private ActionListener eventRegister;
    private ActionListener eventLogin;
    private ActionListener eventForgetPassword; // <-- ¡NUEVA VARIABLE PARA EL LISTENER DE OLVIDAR CONTRASEÑA!

    // Constructor actualizado para aceptar el nuevo ActionListener
    public PanelLoginAndRegister(ActionListener eventRegister, ActionListener eventLogin, ActionListener eventForgetPassword) {
        // Asigna los ActionListeners pasados al constructor a las variables de instancia
        this.eventRegister = eventRegister;
        this.eventLogin = eventLogin;
        this.eventForgetPassword = eventForgetPassword; // <-- ¡ASIGNA EL NUEVO LISTENER!

        initComponents(); // Inicializa los JPanels internos (login, register) y el CardLayout
        this.cardLayout = (CardLayout) this.getLayout(); // Obtiene la instancia de CardLayout establecida por initComponents()

        setOpaque(false); // Asegura que este JLayeredPane sea transparente para ver el fondo

        // Configura los paneles de login y registro, pasando los ActionListeners correctos
        initRegister(this.eventRegister); // Usa this.eventRegister
        initLogin(this.eventLogin);       // Usa this.eventLogin

        // Asegúrate de que los paneles internos sean transparentes si es necesario para tu diseño
        login.setOpaque(false);
        register.setOpaque(false);

        cardLayout.show(this, "card3"); // Muestra el panel de Login ("card3") al inicio
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
        register.setLayout(new MigLayout("wrap", "push[center]push", "push[]25[]10[]10[]25[]push"));

        JLabel label = new JLabel("Crea una Cuenta");
        label.setFont(new Font("sansserif", 1, 30));
        label.setForeground(new Color(17, 64, 90)); // Tu color personalizado
        register.add(label);

        txtRegisterUsername = new MyTextField();
        txtRegisterUsername.setPrefixIcon(new ImageIcon(getClass().getResource("/devt/login/images/user.png"))); // Tu ruta de imagen
        txtRegisterUsername.setHint("Nombre de Usuario");
        register.add(txtRegisterUsername, "w 60%");

        txtRegisterEmail = new MyTextField();
        txtRegisterEmail.setPrefixIcon(new ImageIcon(getClass().getResource("/devt/login/images/mail.png"))); // Tu ruta de imagen
        txtRegisterEmail.setHint("Email");
        register.add(txtRegisterEmail, "w 60%");

        txtRegisterPassword = new MyPasswordField();
        txtRegisterPassword.setPrefixIcon(new ImageIcon(getClass().getResource("/devt/login/images/pass.png"))); // Tu ruta de imagen
        txtRegisterPassword.setHint("Contraseña");
        register.add(txtRegisterPassword, "w 60%");

        // Si necesitas un campo de confirmar contraseña, añádelo aquí:
        // txtRegisterConfirmPassword = new MyPasswordField();
        // txtRegisterConfirmPassword.setPrefixIcon(new ImageIcon(getClass().getResource("/devt/login/images/pass.png")));
        // txtRegisterConfirmPassword.setHint("Confirmar Contraseña");
        // register.add(txtRegisterConfirmPassword, "w 60%");

        Button cmd = new Button();
        cmd.setBackground(new Color(17, 64, 90)); // Tu color personalizado
        cmd.setForeground(new Color(250, 250, 250));
        cmd.addActionListener(eventRegister); // Listener para el botón de registro
        cmd.setText("REGISTRARSE");
        register.add(cmd, "w 40%, h 40");
    }

    private void initLogin(ActionListener eventLogin) {
        login.setLayout(new MigLayout("wrap", "push[center]push", "push[]25[]10[]10[]25[]push"));

        JLabel label = new JLabel("Iniciar Sesión"); // Tu texto personalizado
        label.setFont(new Font("sansserif", 1, 30));
        label.setForeground(new Color(17, 64, 90)); // Tu color personalizado
        login.add(label);

        txtLoginEmail = new MyTextField();
        txtLoginEmail.setPrefixIcon(new ImageIcon(getClass().getResource("/devt/login/images/mail.png"))); // Tu ruta de imagen
        txtLoginEmail.setHint("Email");
        login.add(txtLoginEmail, "w 60%");

        txtLoginPassword = new MyPasswordField();
        txtLoginPassword.setPrefixIcon(new ImageIcon(getClass().getResource("/devt/login/images/pass.png"))); // Tu ruta de imagen
        txtLoginPassword.setHint("Contraseña");
        login.add(txtLoginPassword, "w 60%");

        JButton cmdForget = new JButton("¿ Olvidaste tu contraseña ?"); // Tu texto personalizado
        cmdForget.setForeground(new Color(100, 100, 100));
        cmdForget.setFont(new Font("sansserif", 1, 12));
        cmdForget.setContentAreaFilled(false);
        cmdForget.setBorderPainted(false); // Asegúrate de que esto esté aquí si quieres sin borde
        cmdForget.setFocusPainted(false);
        cmdForget.setOpaque(false);
        cmdForget.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // ¡NUEVO! Asigna el ActionListener para el botón de "Olvidaste tu contraseña"
        if (eventForgetPassword != null) { // Asegúrate de que el listener no sea nulo
            cmdForget.addActionListener(eventForgetPassword);
        }
        login.add(cmdForget);

        Button cmd = new Button();
        cmd.setBackground(new Color(17, 64, 90)); // Tu color personalizado
        cmd.setForeground(new Color(250, 250, 250));
        cmd.addActionListener(eventLogin); // Listener para el botón de login
        cmd.setText("INICIAR SESIÓN"); // Tu texto personalizado
        login.add(cmd, "w 40%, h 40");
    }

    /**
     * Muestra el panel de login u oculta el de registro, o viceversa.
     * Esto se usa para las transiciones animadas desde LoginBase.
     * @param showLogin Si es true, muestra el panel de login ("card3"); si es false, muestra el de registro ("card2").
     */
    public void showLogin(boolean showLogin) {
        if (showLogin) {
            cardLayout.show(this, "card3"); // Muestra el panel de Login ("card3")
        } else {
            cardLayout.show(this, "card2"); // Muestra el panel de Registro ("card2")
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

    // ¡NUEVO! Método para que LoginBase pueda establecer el listener para el botón de "Olvidaste contraseña"
    // Ya no se necesita esta función si el ActionListener se pasa directamente en el constructor.
    // public void addEventForgetPassword(ActionListener event) {
    //     this.eventForgetPassword = event;
    // }

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
