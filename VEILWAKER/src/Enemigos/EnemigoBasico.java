package Enemigos;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import Main.Jugador;

public class EnemigoBasico extends EnemigoBase {

    private BufferedImage sprite;
    private int velocidad = 3;
    private Jugador jugador;
    private int vida = 2;
private boolean puedeAtacar = true;
private int cooldownAtaque = 60; // 1 segundo si FPS = 60
private int temporizadorAtaque = 0;
private int velocidadX = 0;

    public EnemigoBasico(int x, int y, Jugador jugador) {
        super(x, y, 20);
         this.jugador = jugador;
        cargarSprite();
    }

    private void cargarSprite() {
        try {
            sprite = ImageIO.read(getClass().getResource("/Graficos/Sprites/Enemigos/bandana.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //RECIBIR DAÑO DEL JUGADOR
    public void recibirDano(int cantidad) {
    vida -= cantidad;
    System.out.println("Enemigo recibió dano. Vida: " + vida);
    if (vida <= 0) {
        vivo = false;
        System.out.println("Enemigo derrotado");
    }
}

    @Override
    public void actualizar() {
       if (!vivo) return;

    // Movimiento hacia el jugador
    if (jugador.getX() > this.x) {
    velocidadX = velocidad;
    x += velocidad;
} else if (jugador.getX() < this.x) {
    velocidadX = -velocidad;
    x -= velocidad;
} else {
    velocidadX = 0;
}

    // Cooldown de ataque
    if (temporizadorAtaque > 0) {
        temporizadorAtaque--;
    }

    // Ataque si está cerca
    Rectangle rectEnemigo = getRect();
    Rectangle rectJugador = jugador.getRect();

    if (rectEnemigo.intersects(rectJugador)) {
        atacar();
    }
    }
    
    private void atacar() {
    if (temporizadorAtaque <= 0) {
        jugador.recibirDaño(1,10);
        temporizadorAtaque = cooldownAtaque;
    }
}
        
    @Override
public Rectangle getRect() {
    return new Rectangle(x, y, ancho, alto);
}

public int getVelocidadX() {
    return this.velocidadX;
}


    @Override
    public void dibujar(Graphics g, int camaraX) {
        if (vivo && sprite != null) {
            g.drawImage(sprite, x - camaraX, y, ancho, alto, null);
        } else if (vivo) {
            // En caso de que no haya sprite
            g.setColor(Color.RED);
            g.fillRect(x - camaraX, y, ancho, alto);
        }
    }
}
