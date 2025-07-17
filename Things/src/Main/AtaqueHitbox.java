package Main;

import java.awt.*;

public class AtaqueHitbox {
    private Rectangle rect;
    private int framesRestantes;

    public AtaqueHitbox(Rectangle rect, int duracionFrames) {
        this.rect = rect;
        this.framesRestantes = duracionFrames;
    }

    public void actualizar() {
        framesRestantes--;
    }

    public boolean estaActiva() {
        return framesRestantes > 0;
    }

    public Rectangle getRect() {
        return rect;
    }

    public void dibujar(Graphics g, int camaraX) {
        g.setColor(new Color(0, 255, 0, 100));
        g.fillRect(rect.x - camaraX, rect.y, rect.width, rect.height);
    }
}
