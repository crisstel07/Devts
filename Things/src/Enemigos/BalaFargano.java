package Enemigos;

import java.awt.*;
import java.awt.image.BufferedImage;
import Main.Jugador;

public class BalaFargano {

    private int x, y;
    private int velocidadX = 8;
    private int direccion; // -1 o 1
    private boolean activa = true;

    private Jugador jugador;
    private BufferedImage sprite; // Carga tu sprite

    public BalaFargano(int x, int y, int direccion, Jugador jugador) {
        this.x = x;
        this.y = y;
        this.direccion = direccion;
        this.jugador = jugador;
        
        
    }

    public void actualizar() {
        if (!activa) return;

        x += velocidadX * direccion;

        Rectangle r = new Rectangle(x, y, 20, 20); // Tamaño hitbox bala
        if (r.intersects(jugador.getRect())) {
            jugador.recibirDaño(1, direccion * 10);
            activa = false;
        }
    }

    public void dibujar(Graphics g, int camaraX) {
        if (!activa) return;
        g.drawImage(sprite, x - camaraX, y, null);
    }

    public boolean isActiva() {
        return activa;
    }
}
