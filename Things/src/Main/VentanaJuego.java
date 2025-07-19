package Main;

import javax.swing.*;
import java.awt.*; // Necesario para Graphics, etc.

// Creamos la clase que hereda de JPanel
public class VentanaJuego extends JPanel {
    // Ya no necesita un título de ventana aquí, lo manejará el JFrame principal
    public static String titulo = "VEILWAKER - DEMO"; 
    private PanelJuego panelJuego; // Mantener una referencia al PanelJuego

    // Constructor de la clase
    public VentanaJuego() {
        // Un JPanel no tiene título, tamaño de ventana, ni operación de cierre.
        // Estas propiedades son del JFrame que lo contiene.
        // this.setTitle(titulo); // Eliminado
        // this.setSize(1365, 767); // Eliminado
        // this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Eliminado
        // this.setResizable(false); // Eliminado
        // this.setLocationRelativeTo(null); // Eliminado
        // this.setVisible(true); // Eliminado

        // Establecer un layout para VentanaJuego si es necesario.
        // Por defecto, JPanel usa FlowLayout. Si PanelJuego debe ocupar todo, usa BorderLayout.
        setLayout(new BorderLayout()); 
        
        // Agregamos al constructor el PanelJuego
        panelJuego = new PanelJuego(); // Inicializar PanelJuego
        this.add(panelJuego, BorderLayout.CENTER); // Añadir PanelJuego para que ocupe todo el espacio
        
        // No llamar pack() o setVisible(true) aquí, ya que es un JPanel.
        // Se harán en el método startGame() que será llamado por el JFrame principal.
    }

    // Método para inicializar la lógica del juego y solicitar el enfoque
    public void startGame() {
        // No llamar pack() o setVisible(true) aquí, ya que el JPanel ya estará en el JFrame principal.
        
        // Le damos inicio al metodo iniciarJuego que inicia el metodo run que el mismo inicia el metodo Actualizar y repaint
        panelJuego.iniciarJuego();
        // Forzamos a que tome enfoque en el panel para que detecte el teclado
        panelJuego.requestFocusInWindow(); // Usar requestFocusInWindow() para mayor fiabilidad
    }

    // Opcional: Puedes añadir un paintComponent si VentanaJuego tiene su propio fondo o elementos
    // @Override
    // protected void paintComponent(Graphics g) {
    //     super.paintComponent(g);
    //     // Dibuja aquí el fondo o elementos de VentanaJuego si los tiene
    // }
}