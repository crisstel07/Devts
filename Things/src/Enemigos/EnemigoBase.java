package Enemigos;

import java.awt.*;

public abstract class EnemigoBase {

    protected int x, y;
    protected int ancho, alto;
    protected int vida;
    protected boolean vivo = true;

    // Invulnerabilidad
    protected boolean invulnerable = false;
    protected int timerInvulnerable = 0;
    protected final int DURACION_INVULNERABLE = 90; // 1.5 segundos a 60fps
    

    // Retroceso
    protected int retrocesoX = 0;

    public EnemigoBase(int x, int y, int vida) {
        this.x = x;
        this.y = y;
        this.vida = vida;
        this.ancho = 120;
        this.alto = 100;
    }

    public boolean estaVivo() {
        return vivo;
    }

    public boolean esInvulnerable() {
        return invulnerable;
    }

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

    public abstract int getVelocidadX();

    // ⚡️ Nuevo: llamada desde PanelJuego
    public void recibirDano(int cantidad, int direccionEmpuje) {
        if (!vivo || invulnerable) return;

        vida -= cantidad;
        if (vida <= 0) {
            vivo = false;
            return;
        }

        // activar invulnerabilidad
        invulnerable = true;
        timerInvulnerable = DURACION_INVULNERABLE;

        // aplicar retroceso
        retrocesoX = direccionEmpuje > 0 ? 40 : -40;
    }

    // ⚡️ Todos tus enemigos DEBEN llamar super.actualizar() para esto:
    protected void actualizarBase() {
        // Invulnerabilidad
        if (invulnerable) {
            timerInvulnerable--;
            if (timerInvulnerable <= 0) {
                invulnerable = false;
            }
        }

        // Retroceso
        if (retrocesoX != 0) {
            x += retrocesoX;
            if (retrocesoX > 0) {
                retrocesoX -= 5;
                if (retrocesoX < 0) retrocesoX = 0;
            } else {
                retrocesoX += 5;
                if (retrocesoX > 0) retrocesoX = 0;
            }
        }
    }

    // ➜ obligatorio en cada subclase
    public abstract void actualizar();

    public abstract void dibujar(Graphics g, int camaraX);
}
