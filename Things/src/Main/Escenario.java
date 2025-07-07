package Main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Escenario {
    private BufferedImage fondo;
    private int repeticiones;
    public int anchoFondo;
    private int altoFondo;

    public Escenario(String ruta, int repeticiones) {
        try {
            fondo = ImageIO.read(getClass().getResource(ruta));
            this.repeticiones = repeticiones;
            anchoFondo = fondo.getWidth();
            altoFondo = fondo.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dibujar(Graphics g, int camaraX, int anchoVentana, int altoVentana) {
        for (int i = 0; i < repeticiones; i++) {
            int drawX = i * anchoFondo - camaraX;
            g.drawImage(fondo, drawX, 0, anchoFondo, altoVentana, null);
        }
    }
    
    public int getAnchoTotal() {
    return repeticiones * anchoFondo;
}
}
