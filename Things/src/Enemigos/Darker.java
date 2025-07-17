package Enemigos;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import Main.Jugador;
import java.util.Objects;
import Main.Animacion;
import Sonido.Sonido;

public class Darker extends EnemigoBase {

    private int offsetXIdle = -100;
private int offsetYIdle = -300;

private int offsetXGenerar = -100;
private int offsetYGenerar = -320;

private int offsetXCaminar = -100;
private int offsetYCaminar = -320;


// Invulnerabilidad
private boolean invulnerable = false;
private int tiempoInvulnerable = 0;
private final int DURACION_INVULNERABLE = 40;

// Retroceso
private int retrocesoX = 0;

  
    
    private enum Estado {
        IDLE, GENERANDO, SIGUIENDO
    }

    private Estado estado = Estado.IDLE;
    private Jugador jugador;

    private Animacion animIdle;
    private Animacion animGenerar;
    private Animacion animCaminar;
    private Animacion animacionMuerte;
private boolean estaMuriendo = false;

    private boolean mirandoDerecha = true;
    private final int RADIO_DETECCION = 300;
    private final int VELOCIDAD = 5;
    private int velocidadX = 0;

    private int vida = 4;
    private boolean puedeAtacar = true;
    private int cooldownAtaque = 60;
    private int temporizadorAtaque = 0;

    public Darker(int x, int y, Jugador jugador) {
        super(x, y, 70); // tamaño base, lo puedes ajustar
        this.jugador = jugador;
        cargarAnimaciones();
    }

    private void cargarAnimaciones() {
        try {
            animIdle = new Animacion(cargarSprites("idle", 2), 200, true);
            animGenerar = new Animacion(cargarSprites("generation", 6), 9, false);
            animCaminar = new Animacion(cargarSprites("avanzar", 5), 24, true);
            animacionMuerte = new Animacion(cargarSprites("muerte", 9), 10, false);  // Usa tus propios nombres y cantidad de frames

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage[] cargarSprites(String baseNombre, int cantidad) throws IOException {
        BufferedImage[] frames = new BufferedImage[cantidad];
        for (int i = 0; i < cantidad; i++) {
            frames[i] = ImageIO.read(Objects.requireNonNull(getClass().getResource("/Graficos/Sprites/Enemigos/Darker/" + baseNombre + "_" + i + ".png")));
        }
        return frames;
    }

    @Override
    public void actualizar() {
        // Aplica retroceso
if (retrocesoX != 0) {
    x += retrocesoX;

    if (retrocesoX > 0) {
        retrocesoX -= 12;
        if (retrocesoX < 0) retrocesoX = 0;
    } else {
        retrocesoX += 12;
        if (retrocesoX > 0) retrocesoX = 0;
    }
}

// Contador de invulnerabilidad
if (invulnerable) {
    tiempoInvulnerable--;
    if (tiempoInvulnerable <= 0) {
        invulnerable = false;
    }
}
        
   if (estaMuriendo) {
    animacionMuerte.actualizar();
    
    if (animacionMuerte.estaTerminada()) {
        vivo = false;
    }
    return;  // No realiza más acciones si está muriendo
}

        int distanciaX = Math.abs(jugador.getX() - this.x);

        switch (estado) {
            case IDLE -> {
                animIdle.actualizar();
                if (distanciaX < RADIO_DETECCION) {
                    estado = Estado.GENERANDO;
                    animGenerar.reiniciar();
                }
            }

            case GENERANDO -> {
                animGenerar.actualizar();
                if (animGenerar.estaTerminada()) {
                    estado = Estado.SIGUIENDO;
                }
            }

            case SIGUIENDO -> {
                animCaminar.actualizar();
                if (jugador.getX() > this.x) {
                    velocidadX = VELOCIDAD;
                    mirandoDerecha = true;
                } else {
                    velocidadX = -VELOCIDAD;
                    mirandoDerecha = false;
                }
                x += velocidadX;
            }
        }

        // Ataque si colisiona
        if (temporizadorAtaque > 0) temporizadorAtaque--;

        Rectangle rectEnemigo = getRect();
        Rectangle rectJugador = jugador.getRect();
        if (rectEnemigo.intersects(rectJugador)) {
            atacar();
        }
    }

    private void atacar() {
        if (temporizadorAtaque <= 0) {
            jugador.recibirDaño(1, mirandoDerecha ? 15 : -15);
            temporizadorAtaque = cooldownAtaque;
        }
    }

  @Override
public void dibujar(Graphics g, int camaraX) {
    if (!vivo && !estaMuriendo) return;

Graphics2D g2 = (Graphics2D) g;
BufferedImage frame;

if (estaMuriendo && animacionMuerte != null) {
    frame = animacionMuerte.getFrameActual();
} else {
    frame = switch (estado) {
        case IDLE -> animIdle.getFrameActual();
        case GENERANDO -> animGenerar.getFrameActual();
        case SIGUIENDO -> animCaminar.getFrameActual();
    };
}
    int anchoDibujo = frame.getWidth();
    int altoDibujo = frame.getHeight();

    // Define los offsets según el estado
    int offsetX = 0;
    int offsetY = 0;
    switch (estado) {
        case IDLE -> {
            offsetX = offsetXIdle;
            offsetY = offsetYIdle;
        }
        case GENERANDO -> {
            offsetX = offsetXGenerar;
            offsetY = offsetYGenerar;
        }
        case SIGUIENDO -> {
            offsetX = offsetXCaminar;
            offsetY = offsetYCaminar;
        }
    }

    // Aplica la cámara y offset
    int drawX = x - camaraX + offsetX;
    int drawY = y + offsetY;

    // Flip si mira izquierda
    if (!mirandoDerecha) {
        g2.drawImage(frame, drawX + anchoDibujo, drawY, -anchoDibujo, altoDibujo, null);
    } else {
        g2.drawImage(frame, drawX, drawY, anchoDibujo, altoDibujo, null);
    }
}

    @Override
    public Rectangle getRect() {
        return new Rectangle(x, 380, 130, 200); // Puedes ajustar el tamaño de la hitbox
    }

    public void recibirDano(int cantidad) {
   // Por compatibilidad con EnemigoBase
    recibirDano(cantidad, 0);
}

public boolean recibirDano(int cantidad, int direccionEmpuje) {
    if (invulnerable || estaMuriendo || !vivo) return false;

    vida -= cantidad;

    if (vida <= 0) {
        estaMuriendo = true;
        animacionMuerte.reiniciar();
        velocidadX = 0;
        return true;
    }

    // Activar invulnerabilidad y retroceso
    invulnerable = true;
    tiempoInvulnerable = DURACION_INVULNERABLE;
    retrocesoX = direccionEmpuje;
    return false;
    }

    public int getVelocidadX() {
        return velocidadX;
    }
}
