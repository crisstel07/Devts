package Escenarios;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import Main.Suelo;


public class Dia extends EscenarioBase {
    private BufferedImage fondo;
    private Suelo suelo;
    private int repeticionesInternas;

    public Dia(int repeticiones) {
        this.repeticiones = repeticiones;
        this.repeticionesInternas = repeticiones;
        try {
            fondo = ImageIO.read(getClass().getResource("/Graficos/fondotrendia.png"));
            anchoFondo = fondo.getWidth();
            altoFondo = fondo.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
        suelo = new Suelo("/Graficos/suelos.png");
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
        // Aquí podrías cargar música
        System.out.println("Reproduciendo música de Día...");
    }
}
