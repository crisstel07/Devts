package Escenarios;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import Main.Jugador;
import Enemigos.JefeFinal.Umbrale;

public class Final extends EscenarioBase {
    private BufferedImage fondo;
    private Suelo suelo;
    private int repeticionesInternas;
    private Jugador jugador;



    public Final(int repeticiones, Jugador jugador) {
        this.repeticiones = repeticiones;
        this.repeticionesInternas = repeticiones;
        this.jugador = jugador;
     

        try {
            fondo = ImageIO.read(getClass().getResource("/Graficos/Muerte.png"));
            anchoFondo = fondo.getWidth();
            altoFondo = fondo.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
        suelo = new Suelo("/Graficos/PisoMuerte.png");

        // 👇️ Aquí defines los enemigos SOLO de este escenario
         cargarEnemigos();
       
    }
@Override
    public void cargarEnemigos() {
        enemigos.clear();
        enemigos.add(new Umbrale(900, 617, jugador));
         
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
    @Override
public int getLimiteEscenario() {
    return 1365;  // límite fijo para la arena del boss
}

    
}
