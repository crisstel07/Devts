package Enemigos;

import java.awt.*;
import Main.Jugador;

public class Farg extends EnemigoBase {

    private enum Estado {GENERANDO, SIGUIENDO, MUERTO}
    private Estado estado = Estado.GENERANDO;

    private final int VELOCIDAD = 3;
    private Jugador jugador;
    private Fargano padre;

    private int vida = 2;

    public Farg(int x, int y, Jugador jugador, Fargano padre) {
        super(x, y, 50);
        this.jugador = jugador;
        this.padre = padre;
        // Cargar animaciones
    }

    @Override
    public void actualizar() {
        if (estado == Estado.MUERTO) return;

        if (estado == Estado.GENERANDO) {
            // Cuando termine animación:
            estado = Estado.SIGUIENDO;
        } else if (estado == Estado.SIGUIENDO) {
            int dx = jugador.getX() - x;
            x += dx > 0 ? VELOCIDAD : -VELOCIDAD;
        }
    }

    @Override
    public void recibirDano(int cantidad, int direccionEmpuje) {
        if (estado == Estado.MUERTO) return;

        vida -= cantidad;
        if (vida <= 0) {
            estado = Estado.MUERTO;
            vivo = false;
            padre.estado = Estado.ATACANDO; // Reactivar Fargano
        }
    }

    @Override
    public void dibujar(Graphics g, int camaraX) {
        // Animación según estado
    }

    @Override
    public Rectangle getRect() {
        return new Rectangle(x, y, 50, 50);
    }

    @Override
    public int getVelocidadX() {
        return 0;
    }
}
