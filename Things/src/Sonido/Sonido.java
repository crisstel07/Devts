package Sonido;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class Sonido {
    private Clip clip;
    private FloatControl controlVolumen;
    private boolean silenciado = false;
    private float volumenAnterior = 0f;

    public Sonido(String ruta) {
        try {
            URL url = getClass().getResource(ruta);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            // Inicializa el control de volumen si existe
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                controlVolumen = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            }

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void reproducir() {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void loop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void parar() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public boolean estaReproduciendose() {
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
            controlVolumen.setValue(decibelios);
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
