package Enemigos;

import java.awt.*;

public abstract class EnemigoBase {

    protected int x, y;
    protected int ancho, alto;
    protected int vida;
    protected boolean vivo = true;
    protected int rangoAtaque = 20;
   

    public EnemigoBase(int x, int y, int vida) {
        this.x = x;
        this.y = y;
        this.vida = vida;
        this.ancho = 120;  // Puedes ajustar
        this.alto = 100;
    }
    
    
    public abstract int getVelocidadX();
    
    
     public boolean estaVivo() {
        return vivo;
    }
    
    // Para moverse o hacer AI
    public abstract void actualizar();

    // Para dibujarse
    public abstract void dibujar(Graphics g, int camaraX);
    
    

     public abstract void recibirDano(int cantidad, int direccionEmpuje);
      public Rectangle getRect() {
        return new Rectangle(x, y, ancho, alto);
    }
    

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }
    

    
}
