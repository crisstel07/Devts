package Enemigos;

import java.awt.*;
import java.awt.image.BufferedImage;
import Main.Jugador;
import Main.Animacion;
import java.util.List;

public class Fargano extends EnemigoBase {

    private int offsetXSprite = -120;
    private int offsetYSprite = 0;
    
    private final int RETROCESO_PIXELES = 30;


    // Estados del enemigo
    public enum Estado {
        IDLE, ATACANDO, MUERTO
    }

    private Estado estado = Estado.IDLE;

    private final int RADIO_DETECCION = 300;

    private int vida = 5;

    // Invulnerabilidad
    private boolean invulnerable = false;
    private int tiempoInvulnerable = 0;
    private final int DURACION_INVULNERABLE = 90; // 1.5 segundos si tu juego va a 60fps

    // Animaciones
    private Animacion animIdle;
    private Animacion animAtaque;
    private Animacion animMuerte;

    private Jugador jugador;

    // Constructor
    public Fargano(int x, int y, Jugador jugador) {
        super(x, y, 100);  // Tamaño base (puedes ajustar)
        this.jugador = jugador;

        try {
            animIdle = new Animacion(cargarSprites("idle", 2), 60);
            animAtaque = new Animacion(cargarSprites("Attack", 9), 24);
            animMuerte = new Animacion(cargarSprites("Muerte", 11), 26);
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
        if (!vivo) {
            return;
        }

        if (estado == Estado.MUERTO) {
            animMuerte.actualizar();
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

        // Cambiar a estado ataque si el jugador está cerca
        if (estado == Estado.IDLE && distanciaX < RADIO_DETECCION) {
            estado = Estado.ATACANDO;
        }

        // Actualizar animación del estado actual
        switch (estado) {
            case IDLE ->
                animIdle.actualizar();
            case ATACANDO ->
                animAtaque.actualizar();
        }
    }

    @Override
    public void recibirDano(int cantidad, int direccionEmpuje) {
        if (invulnerable || estado == Estado.MUERTO) {
            return;
        }
        
           // Retroceso
    x += direccionEmpuje +RETROCESO_PIXELES;
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

        if (!vivo) {
            return;
        }

        BufferedImage frame = null;

        switch (estado) {
            case IDLE ->
                frame = animIdle.getFrameActual();
            case ATACANDO ->
                frame = animAtaque.getFrameActual();
            case MUERTO ->
                frame = animMuerte.getFrameActual();
        }

        if (frame != null) {
            int escalaAncho = 270;
            int escalaAlto = 300;

            g.drawImage(
                    frame,
                    x - camaraX + offsetXSprite, // ⬅️ Desplazamiento en X
                    y - escalaAlto + offsetYSprite, // ⬅️ Desplazamiento en Y
                    escalaAncho,
                    escalaAlto,
                    null
            );
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
}
