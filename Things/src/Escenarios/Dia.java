package Escenarios;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import Main.Jugador;
import Enemigos.*;

public class Dia extends EscenarioBase {
    private BufferedImage fondo;
    private Suelo suelo;
    private Cielo cielo;
    private int repeticionesInternas;
    private Jugador jugador;

    public Dia(int repeticiones, Jugador jugador) {
        this.repeticiones = repeticiones;
        this.repeticionesInternas = repeticiones;
        this.jugador = jugador;

        try {
            fondo = ImageIO.read(getClass().getResource("/Graficos/FondoDia.png"));
            anchoFondo = fondo.getWidth();
            altoFondo = fondo.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
        suelo = new Suelo("/Graficos/SueloDia.png");
        cielo = new Cielo("/Graficos/CieloDia.png");
        // 👇️ Aquí defines los enemigos SOLO de este escenario
         cargarEnemigos();
       
    }
@Override
    public void cargarEnemigos() {
        enemigos.clear();
         enemigos.add(new Fargano(1050, 617, jugador, this));
        enemigos.add(new Darker(2000, 617, jugador));
         enemigos.add(new Darker(2500, 617, jugador));
         enemigos.add(new Fargano(2900, 617, jugador, this));
         
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
        cielo.actualizar();
        cielo.dibujar(g);
    }

    @Override
    public void reproducirMusica() {
        System.out.println("Reproduciendo música de Noche...");
    }
    
    
}
