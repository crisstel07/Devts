package Main;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class PanelCréditos extends JPanel {

    private int yTexto = 768;  // Posición inicial de los créditos (fuera de la vista)
    private Timer timer;  // Timer para la animación de los créditos
    private List<String[]> creditos;  // Lista de los créditos (rol, nombre)
    private Font fuentePixel;  // Fuente personalizada para los créditos

    // Constructor del panel de créditos
    public PanelCréditos(Font fuentePixel) {
        this.fuentePixel = fuentePixel;

        // Establecer el layout del panel y el color de fondo
        setLayout(null);
        setOpaque(true);
        setBackground(Color.BLACK);
        setBounds(0, 0, 1366, 768);

        // Definir los créditos
        creditos = Arrays.asList(
            new String[]{"Coordinador General", "Esrón Emmanuel Pineda Pérez"},
            new String[]{"Programación de videojuego", "Esrón Emmanuel Pineda Pérez"},
            new String[]{"Arte Pixel", "Andrea Elizabeth Miranda catalán"},
            new String[]{"Música", "Jhpz"},
            new String[]{"Narrativa", "Cristel Jimena Orozco Porras"},
            new String[]{"Diseño Interz", "Deysi Esmeralda Muhún Raxa"},
            new String[]{"Login", "Cristel Jimena Orozco Porras"},
            new String[]{"Gracias por jugar", ""},
            new String[]{"", ""},
            new String[]{"", ""}
        );

        iniciarAnimacion();  // Iniciar la animación del movimiento hacia arriba
    }

    // Iniciar el Timer para mover los créditos hacia arriba
    private void iniciarAnimacion() {
        timer = new Timer(30, e -> {
            yTexto -= 1;  // Mover los créditos hacia arriba

            // Si los créditos han salido completamente de la pantalla, reiniciar la posición
            if (yTexto + creditos.size() * 40 < -300) {
                yTexto = 768;  // Volver a la posición inicial
            }

            repaint();  // Redibujar el panel
        });

        timer.start();  // Iniciar el Timer
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Crear un Graphics2D para personalizar el dibujo
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setFont(fuentePixel.deriveFont(18f));
        g2.setColor(Color.WHITE);

        int frameWidth = getWidth();  // Ancho del panel
        int centroX = frameWidth / 2;  // Centro horizontal del panel
        int alturaLinea = 40;  // Espacio entre cada línea de texto
        int yActual = yTexto;  // Posición vertical de los créditos

        // Dibujar los créditos
        for (String[] linea : creditos) {
            String rol = linea[0];
            String nombre = linea[1];

            // Calcular el ancho de cada texto
            FontMetrics fm = g2.getFontMetrics();
            int anchoRol = fm.stringWidth(rol);
            int anchoNombre = fm.stringWidth(nombre);
            int espacio = 40;  // Espacio entre la columna de rol y nombre

            // Calcular las posiciones de los textos (centrados)
            int xRol = centroX - espacio - anchoRol;
            int xNombre = centroX + espacio;

            // Dibujar los textos solo si están dentro del área visible
            if (yActual > -alturaLinea && yActual < getHeight() + alturaLinea) {
                if (!rol.isEmpty()) g2.drawString(rol, xRol, yActual);
                if (!nombre.isEmpty()) g2.drawString(nombre, xNombre, yActual);
            }

            yActual += alturaLinea;  // Mover la posición para la siguiente línea
        }

        g2.dispose();  // Liberar recursos gráficos
    }
}
