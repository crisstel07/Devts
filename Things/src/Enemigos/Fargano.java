package Enemigos;

import java.awt.*;
import java.awt.image.BufferedImage;
import Main.Jugador;
import Main.Animacion;
import java.util.ArrayList;
import java.util.List;
import Escenarios.*;

public class Fargano extends EnemigoBase {

    private EscenarioBase escenario;
    private Jugador jugador;

    private final int RETROCESO_PIXELES = 30;
    private final int RADIO_DETECCION = 500;
    private int vida = 5;

    private boolean invulnerable = false;
    private int tiempoInvulnerable = 0;
    private final int DURACION_INVULNERABLE = 60; // 1.5 segundos si el juego va a 60fps

    private boolean fargGenerado = false;
    private boolean yaDisparoEnEsteAtaque = false;

    private Animacion animIdle;
    private Animacion animAtaque;
    private Animacion animMuerte;

    private Estado estado = Estado.IDLE;

    private List<BalaFargano> balas = new ArrayList<>();

    // Desplazamiento para dibujar sprite
    private int offsetXSprite = -120;
    private int offsetYSprite = 0;

    public enum Estado {
        IDLE, ATACANDO, MUERTO
    }

    public Fargano(int x, int y, Jugador jugador, EscenarioBase escenario) {
        super(x, y, 100);
        this.jugador = jugador;
        this.escenario = escenario;

        try {
            animIdle = new Animacion(cargarSprites("idle", 2), 60);
            animAtaque = new Animacion(cargarSprites("Attack", 9), 8);
            animMuerte = new Animacion(cargarSprites("Muerte", 11), 12);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage[] cargarSprites(String baseNombre, int cantidad) throws Exception {
        BufferedImage[] frames = new BufferedImage[cantidad];
        for (int i = 0; i < cantidad; i++) {
            frames[i] = javax.imageio.ImageIO.read(
                getClass().getResourceAsStream("/Graficos/Sprites/Enemigos/Fargano/" + baseNombre + "_" + i + ".png")
            );
        }
        return frames;
    }

    @Override
    public void actualizar() {
        if (!vivo) return;

        if (estado == Estado.MUERTO) {
            animMuerte.actualizar();

            // Aquí generamos al Farg mientras está la animación de muerte
            if (!fargGenerado && animMuerte.getFrameActualIndex() >= animMuerte.getCantidadFrames() / 2) {
                generarFarg();
                fargGenerado = true;
            }

            // Cuando termina la animación marcamos muerto
            if (animMuerte.estaTerminada()) {
                vivo = false;
            }

            return;
        }

        // Reducir invulnerabilidad
        if (invulnerable) {
            tiempoInvulnerable--;
            if (tiempoInvulnerable <= 0) {
                invulnerable = false;
            }
        }

        int distanciaX = Math.abs(jugador.getX() - this.x);

        if (estado == Estado.IDLE && distanciaX < RADIO_DETECCION) {
            estado = Estado.ATACANDO;
        }

        switch (estado) {
            case IDLE -> animIdle.actualizar();
            case ATACANDO -> {
                animAtaque.actualizar();

                // Dispara en el último frame
                if (!yaDisparoEnEsteAtaque && animAtaque.getFrameActualIndex() == animAtaque.getCantidadFrames() - 1) {
                    disparar();
                    yaDisparoEnEsteAtaque = true;
                }

                if (animAtaque.estaTerminada()) {
                    animAtaque.reiniciar();
                    yaDisparoEnEsteAtaque = false;
                    estado = Estado.IDLE;
                }
            }
        }

        // Actualizar balas
        for (int i = 0; i < balas.size(); i++) {
            BalaFargano b = balas.get(i);
            b.actualizar();
            if (!b.isActiva()) {
                balas.remove(i);
                i--;
            }
        }
    }

    public void disparar() {
        int direccion = jugador.getX() >= this.x ? 1 : -1;
        int xBala = this.x + (direccion == 1 ? 100 : -100);
        int yBala = this.y - 130;

        balas.add(new BalaFargano(xBala, yBala, direccion, jugador));
    }

    private void generarFarg() {
        // Ajusta la posición si quieres
        Farg nuevoFarg = new Farg(this.x + 50, this.y, jugador, escenario);
        escenario.agregarEnemigo(nuevoFarg);
    }

    @Override
    public void recibirDano(int cantidad, int direccionEmpuje) {
        if (invulnerable || estado == Estado.MUERTO) return;

        x += direccionEmpuje + RETROCESO_PIXELES;
        vida -= cantidad;
        invulnerable = true;
        tiempoInvulnerable = DURACION_INVULNERABLE;

        if (vida <= 0) {
            estado = Estado.MUERTO;
            animMuerte.reiniciar();
        }
    }

    @Override
    public void dibujar(Graphics g, int camaraX) {
        if (!vivo) return;

        BufferedImage frame = switch (estado) {
            case IDLE -> animIdle.getFrameActual();
            case ATACANDO -> animAtaque.getFrameActual();
            case MUERTO -> animMuerte.getFrameActual();
        };

        if (frame != null) {
            int escalaAncho = 270;
            int escalaAlto = 300;

            g.drawImage(
                frame,
                x - camaraX + offsetXSprite,
                y - escalaAlto + offsetYSprite,
                escalaAncho,
                escalaAlto,
                null
            );
        }

        for (BalaFargano b : balas) {
            b.dibujar(g, camaraX);
        }
    }

    @Override
    public Rectangle getRect() {
        return new Rectangle(x, y - 270, 140, 270);
    }

    @Override
    public int getVelocidadX() {
        return 0;
    }

    public List<BalaFargano> getBalas() {
        return balas;
    }
}
