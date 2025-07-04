package Main;

import java.awt.image.BufferedImage;

public class Animacion {
    private BufferedImage[] frames;
    private int frameActual;
    private int velocidad; // frames para cambiar
    private int contador;

    /**
     * Constructor
     * @param frames Arreglo de imágenes para la animación
     * @param velocidad Velocidad de animación (menor = más rápido)
     */
    public Animacion(BufferedImage[] frames, int velocidad) {
        this.frames = frames;
        this.velocidad = velocidad;
        this.frameActual = 0;
        this.contador = 0;
    }

    /**
     * Actualiza el frame de la animación
     */
    public void actualizar() {
        contador++;
        if (contador >= velocidad) {
            frameActual = (frameActual + 1) % frames.length;
            contador = 0;
        }
    }

    /**
     * Reinicia la animación (para ataques, saltos, etc.)
     */
    public void reiniciar() {
        frameActual = 0;
        contador = 0;
    }

    /**
     * Devuelve el frame actual para dibujar
     * @return BufferedImage del frame actual
     */
    public BufferedImage getFrameActual() {
        return frames[frameActual];
    }

    /**
     * Saber si terminó (para ataques)
     */
    public boolean estaTerminada() {
        return frameActual == frames.length - 1;
    }
    
    public int getFrameActualIndex() {
    return frameActual;
}
}
