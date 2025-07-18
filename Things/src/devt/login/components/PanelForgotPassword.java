package devt.login.components;

import devt.login.swing.Button;
import devt.login.swing.MyTextField;
import devt.login.swing.MyPasswordField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.ImageIcon;
import java.net.URL;
import java.awt.Graphics;
import java.awt.Image;

public class PanelForgotPassword extends JPanel {

    private MyTextField txtEmail;
    private MyTextField txtCode;
    private MyPasswordField txtNewPassword;
    private MyPasswordField txtConfirmPassword;

    private Button cmdMainAction;
    private Button cmdBackToLogin;

    private JLabel lblTitle;
    private JLabel lblDescription;

    private ActionListener eventOKAction;
    private ActionListener eventCancelAction;

    private boolean isEmailInputView = true;

    private FondoRestContraPanel fondoPanel; // Nuevo panel para el fondo

    public PanelForgotPassword() {
        setOpaque(false); // Este panel principal será transparente para que se vea el fondo
        setLayout(new BorderLayout()); // Usamos BorderLayout para añadir el fondoPanel

        fondoPanel = new FondoRestContraPanel();
        // Usamos MigLayout en el fondoPanel para centrar los componentes
        // "wrap" para que cada componente vaya en una nueva fila.
        // "fillx" para que los componentes se expandan horizontalmente.
        // "align center" para centrar los componentes horizontalmente.
        // "gap 0 0" para reducir el espacio entre filas y columnas.
        // "push" en las filas para empujar el contenido al centro verticalmente.
        fondoPanel.setLayout(new MigLayout("wrap, fillx, alignx center, insets 0", "[center, grow]", "push[]10[]10[]10[]10[]10[]20[]10[]push"));
        add(fondoPanel, BorderLayout.CENTER); // Añadimos el fondoPanel al centro

        initUI();
        showEmailInput();
    }

    private void initUI() {
        lblTitle = new JLabel("Restablecer Contraseña");
        lblTitle.setFont(new Font("sansserif", 1, 30));
        lblTitle.setForeground(new Color(6,96,106));
        fondoPanel.add(lblTitle, "align center, gaptop 50"); // Centrado y un poco de espacio superior

        lblDescription = new JLabel("<html><div style='text-align: center;'>Ingresa tu correo electrónico para<br>enviarte un código de verificación.</div></html>");
        lblDescription.setForeground(new Color(100, 100, 100));
        lblDescription.setFont(new Font("sansserif", 0, 14));
        fondoPanel.add(lblDescription, "align center, gapbottom 20"); // Centrado y espacio inferior

        // Inicialización de campos
        txtEmail = new MyTextField();
        URL mailIconUrl = getClass().getResource("/devt/login/images/mail.png");
        if (mailIconUrl != null) {
            txtEmail.setPrefixIcon(new ImageIcon(mailIconUrl));
        } else {
            System.err.println("Advertencia: No se encontró el icono de mail en /devt/login/images/mail.png");
        }
        txtEmail.setHint("Correo Electrónico");
        fondoPanel.add(txtEmail, "w 60%"); // Ancho relativo para el campo

        txtCode = new MyTextField();
        URL keyIconUrl = getClass().getResource("/devt/login/images/Key.gif");
        if (keyIconUrl != null) {
            txtCode.setPrefixIcon(new ImageIcon(keyIconUrl));
        } else {
            System.err.println("Advertencia: No se encontró el icono de llave en /devt/login/images/Key.gif");
        }
        txtCode.setHint("Código de Verificación");
        fondoPanel.add(txtCode, "w 60%");

        txtNewPassword = new MyPasswordField();
        URL passIconUrl = getClass().getResource("/devt/login/images/pass.png");
        if (passIconUrl != null) {
            txtNewPassword.setPrefixIcon(new ImageIcon(passIconUrl));
        } else {
            System.err.println("Advertencia: No se encontró el icono de contraseña en /devt/login/images/pass.png");
        }
        txtNewPassword.setHint("Nueva Contraseña");
        fondoPanel.add(txtNewPassword, "w 60%");

        txtConfirmPassword = new MyPasswordField();
        if (passIconUrl != null) { // Reutiliza el mismo URL y ImageIcon si ya se cargó
            txtConfirmPassword.setPrefixIcon(new ImageIcon(passIconUrl));
        } else {
            System.err.println("Advertencia: No se encontró el icono de contraseña en /devt/login/images/pass.png");
        }
        txtConfirmPassword.setHint("Confirmar Contraseña");
        fondoPanel.add(txtConfirmPassword, "w 60%");

        // Botones
        cmdMainAction = new Button();
        cmdMainAction.setBackground(new Color(6,96,106));
        cmdMainAction.setForeground(new Color(250, 250, 250));
        cmdMainAction.setText("ENVIAR CÓDIGO"); // Texto inicial
        cmdMainAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (eventOKAction != null) {
                    eventOKAction.actionPerformed(ae);
                }
            }
        });
        fondoPanel.add(cmdMainAction, "w 40%, h 40, gaptop 20"); // Ancho relativo, altura fija, espacio superior

        cmdBackToLogin = new Button();
        cmdBackToLogin.setBackground(new Color(200, 70, 70));
        cmdBackToLogin.setForeground(new Color(250, 250, 250));
        cmdBackToLogin.setText("VOLVER AL LOGIN");
        cmdBackToLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (eventCancelAction != null) {
                    eventCancelAction.actionPerformed(ae);
                }
            }
        });
        fondoPanel.add(cmdBackToLogin, "w 40%, h 40, gapbottom 50"); // Ancho relativo, altura fija, espacio inferior
    }

    // Métodos para asignar los ActionListeners que LoginBase llamará
    public void addEventOK(ActionListener event) {
        this.eventOKAction = event;
    }

    public void addEventCancel(ActionListener event) {
        this.eventCancelAction = event;
    }

    // Getters para los campos de texto
    public String getEmail() {
        return txtEmail.getText().trim();
    }
    
    public String getCode() {
        return txtCode.getText().trim();
    }
    
    public String getNewPassword() {
        return String.valueOf(txtNewPassword.getPassword());
    }
    
    public String getConfirmPassword() {
        return String.valueOf(txtConfirmPassword.getPassword());
    }
    
    public void setEmail(String email) {
        txtEmail.setText(email);
    }

    // Métodos para cambiar entre las vistas del panel
    public boolean isShowingEmailInput() {
        return isEmailInputView;
    }
    
    public void showEmailInput() {
        isEmailInputView = true;
        lblDescription.setText("<html><div style='text-align: center;'>Ingresa tu correo electrónico para<br>enviarte un código de verificación.</div></html>");
        cmdMainAction.setText("ENVIAR CÓDIGO");

        txtEmail.setVisible(true);
        txtCode.setVisible(false);
        txtNewPassword.setVisible(false);
        txtConfirmPassword.setVisible(false);
        
        clearFields(); // Limpiar campos al cambiar de vista
        revalidate();
        repaint();
    }
    
    public void showCodeInput() {
        isEmailInputView = false;
        lblDescription.setText("<html><div style='text-align: center;'>Ingresa el código de verificación<br>y tu nueva contraseña.</div></html>");
        cmdMainAction.setText("RESTABLECER CONTRASEÑA");

        txtEmail.setVisible(false);
        txtCode.setVisible(true);
        txtNewPassword.setVisible(true);
        txtConfirmPassword.setVisible(true);
        
        // No limpiamos el email aquí, solo el código y la nueva contraseña
        txtCode.setText("");
        txtNewPassword.setText("");
        txtConfirmPassword.setText("");
        revalidate();
        repaint();
    }
    
    // Limpia todos los campos de ambas vistas
    public void clearFields() {
        txtEmail.setText("");
        txtCode.setText("");
        txtNewPassword.setText("");
        txtConfirmPassword.setText("");
    }

    // Clase interna para el fondo con imagen
    private class FondoRestContraPanel extends JPanel {
        private Image imagen;

        public FondoRestContraPanel() {
            setOpaque(true); // Asegurarse de que el panel es opaco para pintar el fondo
            try {
                // Cargar la imagen de fondo
                URL imageUrl = getClass().getResource("/devt/login/images/RestContra.png");
                if (imageUrl != null) {
                    imagen = new ImageIcon(imageUrl).getImage();
                } else {
                    System.err.println("Error: No se encontró la imagen de fondo en la ruta: /devt/login/images/RestContra.png. Usando color de fondo.");
                    setBackground(new Color(30, 30, 30)); // Color de respaldo oscuro
                }
            } catch (Exception e) {
                System.err.println("Error al cargar imagen de fondo: " + e.getMessage());
                setBackground(new Color(30, 30, 30)); // Color de respaldo oscuro
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagen != null) {
                // Dibujar la imagen escalada para llenar el panel
                g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
            } else {
                // Si no hay imagen, rellenar con el color de fondo
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
}
