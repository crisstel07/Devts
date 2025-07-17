package Main;

import javax.swing.*;

//Creamos la clase que hereda de JFrame
public class VentanaJuego extends JFrame {
public static String titulo = "VEILWAKER - DEMO";
    //Constructor de la clase
    public VentanaJuego() {
        //Titulo de la ventana
        
        this.setTitle(titulo);
        //Tamaño de la ventana
        this.setSize(1365, 767);
        //Operacion para cerrar el JFrame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Que el usuario no pueda alterar el tamaño de la ventana
        this.setResizable(false);
        //Que la pestaña que se inicialice sea en el centro de la pantallla
        this.setLocationRelativeTo(null);
        //Que el JFrame sea visible
        this.setVisible(true);
        

        //Agregamos al constructor el PanelJuego para que pueda ser llamado por la clase Main
        PanelJuego panelJuego = new PanelJuego();
        this.add(panelJuego);
        this.pack();  //Ajusta la ventana al panel
        this.setVisible(true);

        //le damos iniciao al metodo iniciarJuego que inicia el metodo run que el mismo inicia el metodo Actualizar y repaint
        panelJuego.iniciarJuego();
        //Forzamos a que tome enfoque en el panel para que detecte el teclado
        panelJuego.requestFocus();

    }
}
