package Escenarios;

import Main.PanelJuego;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Cielo {
    private int y;
    private BufferedImage sprite;
    private float offsetX = 0;  // desplazamiento horizontal actual, float para suavidad

    public Cielo(String rutaImagen) {
        this.y = PanelJuego.ALTO - 770;
        try {
            sprite = ImageIO.read(getClass().getResource(rutaImagen));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para actualizar el desplazamiento, debe llamarse en el ciclo de actualización del juego
    public void actualizar() {
        offsetX += 0.1f;  // velocidad de desplazamiento hacia la izquierda (ajusta este valor)
        if (offsetX > sprite.getWidth()) {
            offsetX -= sprite.getWidth();  // loop para que no crezca infinito
        }
    }

    public void dibujar(Graphics g) {
        if (sprite != null) {
            int anchoBloque = sprite.getWidth();
            int inicioBloque = -(int)offsetX;  // empieza desde el offset negativo para dar la ilusión de movimiento hacia la izquierda

            // Dibuja los bloques necesarios para cubrir la pantalla (añade uno más para evitar huecos)
            for (int x = inicioBloque; x < PanelJuego.ANCHO; x += anchoBloque) {
                g.drawImage(sprite, x, y, null);
            }
        } else {
            // Dibuja fondo de respaldo
            g.setColor(Color.cyan);
            g.fillRect(0, y, PanelJuego.ANCHO, 150);
        }
    }

    public int getY() {
        return y;
    }
}
