package Enemigos;

import java.awt.*;
import java.util.List;
import Main.Jugador;

public class Fargo extends EnemigoBase {

    private enum Estado {GENERANDO, SIGUIENDO, MUERTO}
    private Estado estado = Estado.GENERANDO;

    private final int VELOCIDAD = 4;
    private Jugador jugador;
    private List<EnemigoBase> enemigos;

    private int vida = 3;

    private int temporizadorAutodestruccion = 300;

    public Fargo(int x, int y, Jugador jugador, List<EnemigoBase> enemigos) {
        super(x, y, 50);
        this.jugador = jugador;
        this.enemigos = enemigos;
        // Cargar animaciones
    }

    @Override
    public void actualizar() {
        if (estado == Estado.MUERTO) return;

        if (estado == Estado.GENERANDO) {
            estado = Estado.SIGUIENDO;
        } else if (estado == Estado.SIGUIENDO) {
            int dx = jugador.getX() - x;
            x += dx > 0 ? VELOCIDAD : -VELOCIDAD;

            temporizadorAutodestruccion--;
            if (temporizadorAutodestruccion <= 0) {
                explotar();
                vivo = false;
                estado = Estado.MUERTO;
            }
        }
    }

    @Override
    public void recibirDano(int cantidad, int direccionEmpuje) {
        if (estado == Estado.MUERTO) return;

        vida -= cantidad;
        if (vida <= 0) {
            vivo = false;
            estado = Estado.MUERTO;
            // No explota si fue destruido por jugador
        }
    }

    private void explotar() {
        for (int i = 0; i < 5; i++) {
            ParticulaOscura p = new ParticulaOscura(x, y);
            enemigos.add(p); // Si manejas partículas en otro lado, cambia esta lista
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
