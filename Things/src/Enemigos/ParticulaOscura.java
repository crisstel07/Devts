package Enemigos;

import java.awt.*;
import Main.Jugador;

public class ParticulaOscura extends EnemigoBase {

    private int duracion = 60;
    private Jugador jugador;

    public ParticulaOscura(int x, int y) {
        super(x, y, 20);
        // Carga sprite si quieres
    }

    @Override
    public void actualizar() {
        duracion--;
        if (duracion <= 0) {
            vivo = false;
        }
    }

    @Override
    public void recibirDano(int cantidad, int direccionEmpuje) {
        // No se destruyen
    }

    @Override
    public void dibujar(Graphics g, int camaraX) {
        // Dibuja la partícula
    }

    @Override
    public Rectangle getRect() {
        return new Rectangle(x, y, 20, 20);
    }

    @Override
    public int getVelocidadX() {
        return 0;
    }
}
