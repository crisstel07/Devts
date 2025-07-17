
package Escenarios;

import Main.PanelJuego;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Suelo {
    private int y;
    private BufferedImage sprite;

    public Suelo(String rutaImagen) {
    this.y = PanelJuego.ALTO - 150;

    try {
        sprite = ImageIO.read(getClass().getResource(rutaImagen));
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    public void dibujar(Graphics g, int offsetX) {
        if (sprite != null) {
            // Calcula cuántos bloques se necesitan para cubrir la pantalla
            int anchoBloque = sprite.getWidth();
          int inicioBloque = (offsetX / anchoBloque) * anchoBloque - anchoBloque;
for (int i = inicioBloque; i < offsetX + PanelJuego.ANCHO; i += anchoBloque) {
    g.drawImage(sprite, i - offsetX, y, null);
}
        } else {
            // Dibuja suelo de respaldo
            g.setColor(Color.GRAY);
            g.fillRect(0, y, PanelJuego.ANCHO, 150);
        }
    }

    public int getY() {
        return y;
    }
}
