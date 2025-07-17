package Escenarios;

import Enemigos.Darker;
import Main.Jugador;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

// Clase NocheOjos
public class Noche extends EscenarioBase {
    private BufferedImage fondo;
    private Suelo suelo;
    private Cielo cielo;
    private int repeticionesInternas;
     private Jugador jugador;

    public Noche(int repeticiones, Jugador jugador) {
        this.repeticiones = repeticiones;
        this.repeticionesInternas = repeticiones;
         this.jugador = jugador;
          
        try {
            fondo = ImageIO.read(getClass().getResource("/Graficos/FondoNoche.png"));
            anchoFondo = fondo.getWidth();
            altoFondo = fondo.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
        suelo = new Suelo("/Graficos/SueloNoche.png");
        cielo = new Cielo("/Graficos/CieloNoche.png");
       cargarEnemigos();
    }
@Override
    public void cargarEnemigos() {
            // 👇️ Aquí defines los enemigos SOLO de este escenario
        enemigos.clear();
        enemigos.add(new Darker(900, 617, jugador));
        enemigos.add(new Darker(1500, 617, jugador));
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
        cielo.actualizar();
        cielo.dibujar(g);
    }

    @Override
    public void reproducirMusica() {
        System.out.println("Reproduciendo música de NocheOjos...");
    }
}
