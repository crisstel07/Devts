package Main;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Objects;

public class ParticulasGolpe {

    private int x, y;
    private static BufferedImage[] FRAMES_COMPARTIDOS;
    private int frameActual = 0;
    private int contadorFrame = 0;
    private int velocidadAnimacion = 3;  // Ajusté a más rápido
    public static final int PARTICULA_ANCHO = 300;
    public static final int PARTICULA_ALTO = 300;


    private boolean viva = true;

    public ParticulasGolpe(int x, int y) {
        this.x = x;
        this.y = y;
        cargarFramesCompartidosSiNecesario();
    }

    private void cargarFramesCompartidosSiNecesario() {
        if (FRAMES_COMPARTIDOS != null) return; // Ya estaban cargados

        FRAMES_COMPARTIDOS = new BufferedImage[6];
        try {
            for (int i = 0; i < FRAMES_COMPARTIDOS.length; i++) {
                FRAMES_COMPARTIDOS[i] = ImageIO.read(
                    Objects.requireNonNull(getClass().getResource(
                        "/Graficos/Particulas/golpear_" + i + ".png"))
                );
                System.out.println("Cargado frame: Golpear_" + i);
            }
        } catch (Exception e) {
            System.out.println("❌ Error cargando frames de partículas:");
            e.printStackTrace();
            System.out.println(getClass().getResource(
    "/Graficos/Sprites/Particulas/Golpear_0.png"));

        }
    }

    public void actualizar() {
        contadorFrame++;
        if (contadorFrame >= velocidadAnimacion) {
            contadorFrame = 0;
            frameActual++;
            if (frameActual >= FRAMES_COMPARTIDOS.length) {
                viva = false;
            }
        }
    }

    public void dibujar(Graphics g, int camaraX) {
    if (viva && frameActual < FRAMES_COMPARTIDOS.length) {
        g.drawImage(FRAMES_COMPARTIDOS[frameActual], x - camaraX, y, PARTICULA_ANCHO, PARTICULA_ALTO, null);
    }
}


    public boolean estaViva() {
        return viva;
    }
}
