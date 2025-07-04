package Escenarios;
import Main.Suelo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

// Clase NocheOjos
public class NocheOjos extends EscenarioBase {
    private BufferedImage fondo;
    private Suelo suelo;
    private int repeticionesInternas;

    public NocheOjos(int repeticiones) {
        this.repeticiones = repeticiones;
        this.repeticionesInternas = repeticiones;
        try {
            fondo = ImageIO.read(getClass().getResource("/Graficos/FondoTren2.png"));
            anchoFondo = fondo.getWidth();
            altoFondo = fondo.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
        suelo = new Suelo("/Graficos/suelo.png");
    }

    @Override
    public void dibujarFondo(Graphics g, int camaraX, int anchoVentana, int altoVentana) {
        for (int i = 0; i < repeticionesInternas; i++) {
            int drawX = i * anchoFondo - camaraX;
            g.drawImage(fondo, drawX, 0, anchoFondo, altoVentana, null);
        }
    }

    @Override
    public void dibujarElementos(Graphics g, int camaraX) {
        suelo.dibujar(g, camaraX);
    }

    @Override
    public void reproducirMusica() {
        System.out.println("Reproduciendo música de NocheOjos...");
    }
}
