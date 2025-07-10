package Enemigos;

import java.awt.*;
import java.awt.image.BufferedImage;
import Main.Animacion;
import Main.Jugador;
import Escenarios.*;

public class Farg extends EnemigoBase {

    private Jugador jugador;
    private int vida = 2;
    private boolean invulnerable = false;
    private int tiempoInvulnerable = 0;
    private final int DURACION_INVULNERABLE = 60;
    private final int RETROCESO_PIXELES = 30;
    private int velocidadX = 2;

    private boolean mirandoDerecha = true;

    private Animacion muerte, ataque, aparicion;
    private Estado estado = Estado.APARICION;

    private EscenarioBase escenario;
    //Offset de sprite
    private int offsetX = 10;
private int offsetY = 70;
//Tamaño de sprites
private int spriteWidth = 70;
private int spriteHeight = 70   ;

    public enum Estado {
        APARICION, MUERTE, ATAQUE
    }

    public Farg(int x, int y, Jugador jugador, EscenarioBase escenario) {
        super(x, y, 40); // ajusta el tamaño
        this.jugador = jugador;
        this.escenario = escenario;

        try {
            muerte = new Animacion(cargarSprites("Muerte", 7), 16);
            ataque = new Animacion(cargarSprites("Seguir", 6), 18);
            aparicion = new Animacion(cargarSprites("Fargeneration", 13), 7);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage[] cargarSprites(String baseNombre, int cantidad) throws Exception {
        BufferedImage[] frames = new BufferedImage[cantidad];
        for (int i = 0; i < cantidad; i++) {
            frames[i] = javax.imageio.ImageIO.read(
                    getClass().getResourceAsStream("/Graficos/Sprites/Enemigos/Fargano/Farg/" + baseNombre + "_" + i + ".png")
            );
        }
        return frames;
    }

    @Override
    public void actualizar() {

        if (invulnerable) {
            tiempoInvulnerable--;
            if (tiempoInvulnerable <= 0) {
                invulnerable = false;
            }
        }
        switch (estado) {
            case APARICION -> {
                aparicion.actualizar();
                if (aparicion.estaTerminada()) {
                    estado = Estado.ATAQUE;
                }
            }
            case MUERTE ->{
    muerte.actualizar();
    if (muerte.estaTerminada()) {
        vivo = false;
    }
}
            case ATAQUE -> {
                ataque.actualizar();
                moverHaciaJugador();
            }
        }
    }

    private void moverHaciaJugador() {
        if (jugador == null) {
            return;
        }
        if (jugador.getX() < this.x) {
            this.x -= velocidadX;
            mirandoDerecha = false;
        } else {
            this.x += velocidadX;
            mirandoDerecha = true;
        }
    }

    @Override
    public void recibirDano(int cantidad, int direccionEmpuje) {
        if (invulnerable || estado == Estado.MUERTE) {
            return;
        }

        this.x += direccionEmpuje + RETROCESO_PIXELES;
        vida -= cantidad;
        invulnerable = true;
        tiempoInvulnerable = DURACION_INVULNERABLE;

        if (vida <= 0) {
            estado = Estado.MUERTE;
            muerte.reiniciar();
        }
    }

    @Override
public void dibujar(Graphics g, int camaraX) {
    BufferedImage frameActual = switch (estado) {
        case APARICION -> aparicion.getFrameActual();
        case MUERTE -> muerte.getFrameActual();
        case ATAQUE -> ataque.getFrameActual();
    };

    if (frameActual == null) return;

    Graphics2D g2 = (Graphics2D) g;

    int dibujarX = x - camaraX-offsetX;
    int dibujarY = y-offsetY;

    if (mirandoDerecha) {
        g2.drawImage(frameActual, dibujarX, dibujarY, spriteWidth, spriteHeight, null);
    } else {
        g2.drawImage(frameActual,
                dibujarX + spriteWidth, dibujarY,
                dibujarX, dibujarY + spriteHeight,
                0, 0, frameActual.getWidth(), frameActual.getHeight(),
                null);
    }
}

    @Override
    public Rectangle getRect() {
        return new Rectangle(x, y - 60, 60, 60);
    }

    @Override
    public int getVelocidadX() {
        return 0;
    }
}
