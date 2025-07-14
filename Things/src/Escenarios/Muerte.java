package Escenarios;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import Main.Jugador;
import Enemigos.*;

public class Muerte extends EscenarioBase {
    private BufferedImage fondo;
    private Suelo suelo;
    private int repeticionesInternas;
    private Jugador jugador;

    public Muerte(int repeticiones, Jugador jugador) {
        this.repeticiones = repeticiones;
        this.repeticionesInternas = repeticiones;
        this.jugador = jugador;

        try {
            fondo = ImageIO.read(getClass().getResource("/Graficos/fondotrendia.png"));
            anchoFondo = fondo.getWidth();
            altoFondo = fondo.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
        suelo = new Suelo("/Graficos/SueloDia.png");

        // 👇️ Aquí defines los enemigos SOLO de este escenario
         
        enemigos.add(new Darker(2000, 617, jugador));
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
        System.out.println("Reproduciendo música de Noche...");
    }
    
    
}
