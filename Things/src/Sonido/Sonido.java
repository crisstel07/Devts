package Sonido;

import javax.sound.sampled.*;
import java.io.BufferedInputStream; // Necesario para BufferedInputStream
import java.io.IOException;
import java.io.InputStream; // Necesario para InputStream
import java.net.URL;

public class Sonido {
    private Clip clip;
    private FloatControl controlVolumen;
    private boolean silenciado = false;
    private float volumenAnterior = 0f;

    public Sonido(String ruta) {
        try {
            // Usar getClass().getResourceAsStream para recursos dentro del JAR
            InputStream audioSrc = getClass().getResourceAsStream(ruta);
            if (audioSrc == null) {
                System.err.println("Error: Archivo de sonido no encontrado en la ruta: " + ruta);
                return; // Salir si el archivo no se encuentra
            }
            // BufferedInputStream es crucial para que AudioSystem pueda leer correctamente
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
            
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            // Inicializa el control de volumen si existe
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                controlVolumen = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            }

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error al cargar el sonido " + ruta + ": " + e.getMessage());
            e.printStackTrace();
            clip = null; // Asegurarse de que el clip sea null si hay un error
        }
    }

    public void reproducir() {
        if (clip != null) {
            clip.setFramePosition(0); // Reinicia desde el principio
            clip.start();
        }
    }

    // MODIFICADO: Renombrado de 'loop()' a 'reproducirEnLoop()' para consistencia
    public void reproducirEnLoop() {
        if (clip != null) {
            clip.setFramePosition(0); // Reinicia desde el principio
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void parar() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    // MODIFICADO: Renombrado de 'estaReproduciendose()' a 'estaReproduciendo()'
    public boolean estaReproduciendo() {
        return clip != null && clip.isRunning();
    }

    // -------------------------
    // 🎚️ Volumen
    // -------------------------

    /**
     * Cambia el volumen en decibelios.
     * Ejemplo: 0.0f es volumen normal, -10.0f más bajo, -80.0f es silencio total.
     */
    public void setVolumen(float decibelios) {
        if (controlVolumen != null) {
            // Asegurarse de que el valor esté dentro del rango permitido por el control
            float min = controlVolumen.getMinimum();
            float max = controlVolumen.getMaximum();
            float clampedDb = Math.max(min, Math.min(max, decibelios));
            controlVolumen.setValue(clampedDb);
        }
    }

    /**
     * Silencia el sonido, guardando volumen previo.
     */
    public void silenciar() {
        if (!silenciado && controlVolumen != null) {
            volumenAnterior = controlVolumen.getValue();
            controlVolumen.setValue(controlVolumen.getMinimum());
            silenciado = true;
        }
    }

    /**
     * Quita el silencio y restaura el volumen previo.
     */
    public void desSilenciar() {
        if (silenciado && controlVolumen != null) {
            controlVolumen.setValue(volumenAnterior);
            silenciado = false;
        }
    }
}
