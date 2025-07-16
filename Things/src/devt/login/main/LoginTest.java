
package devt.login.main;

import devt.login.view.LoginBase;
import javax.swing.SwingUtilities;

public class LoginTest {
    public static void main(String[] args) {
        //Asegura que el formulario se abra en el hilo de eventos de Swing
        //Evita errores de interfaz.
        SwingUtilities.invokeLater(() -> { 
            new LoginBase().setVisible(true);
        });
    }
}
