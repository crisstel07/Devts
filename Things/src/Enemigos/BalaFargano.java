package Enemigos;

import Main.Animacion;
import java.awt.*;
import java.awt.image.BufferedImage;
import Main.Jugador;

public class BalaFargano {

    private int x, y;
    private int velocidadX = 8;
    private int direccion; // -1 o 1
    private boolean activa = true;
    
    //Hitbox tamaño
    

private final int anchoDibujo = 64; // Cambia a lo que desees (ej. 64 píxeles de ancho)
private final int altoDibujo = 64;  // Alto del sprite dibujado

private final int offsetXSprite = +60; // Corrección horizontal si el sprite está desalineado
private final int offsetYSprite = -10; // Corrección vertical si quieres subir o bajar el sprite

private final int offsetX = 60;
    
    

    private Jugador jugador;
    private BufferedImage sprite; // Carga tu sprite
    private  Animacion animavance;

    public BalaFargano(int x, int y, int direccion, Jugador jugador) {
        this.x = x;
        this.y = y;
        this.direccion = direccion;
        this.jugador = jugador;
        
         try {
            animavance = new Animacion(cargarSprites("Bala", 5), 15, false);
            
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
        animavance.actualizar();
      BufferedImage frame = animavance.getFrameActual();
      g.drawImage(frame, x - camaraX + offsetXSprite, y + offsetYSprite, anchoDibujo, altoDibujo, null);

      
    }

    public boolean isActiva() {
        return activa;
    }
    
    public Rectangle getRect() {
    return new Rectangle(x+offsetX, y, 50, 30);
}

}
