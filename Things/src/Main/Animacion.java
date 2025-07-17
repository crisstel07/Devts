package Main;

import java.awt.image.BufferedImage;

public class Animacion {
    private BufferedImage[] frames;
    private int frameActual;
    private int velocidad;
    private int contador;
    private boolean loop;

    public Animacion(BufferedImage[] frames, int velocidad, boolean loop) {
        this.frames = frames;
        this.velocidad = velocidad;
        this.loop = loop;
        this.frameActual = 0;
        this.contador = 0;
    }

    public void actualizar() {
        if (estaTerminada() && !loop) return;

        contador++;
        if (contador >= velocidad) {
            contador = 0;
            frameActual++;

            if (loop) {
                frameActual %= frames.length;
            } else if (frameActual >= frames.length) {
                frameActual = frames.length - 1; // Se queda en el último frame
            }
        }
    }

    public void reiniciar() {
        frameActual = 0;
        contador = 0;
    }

    public BufferedImage getFrameActual() {
        return frames[frameActual];
    }

    public boolean estaTerminada() {
        return frameActual == frames.length - 1 && contador == 0;
    }

    public int getFrameActualIndex() {
        return frameActual;
    }

    public int getCantidadFrames() {
        return frames.length;
    }

    public int getDuracionEnFrames() {
        return frames.length * velocidad;
    }
}
