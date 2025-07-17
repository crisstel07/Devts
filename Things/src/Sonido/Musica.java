package Sonido;

import javax.sound.sampled.*;
import java.net.URL;

public class Musica {
    private Clip clip;
    private int frameActual = 0;
    private FloatControl controlVolumen;
    private boolean silenciado = false;
    private float volumenAnterior = 0f;

    public Musica(String ruta) {
        try {
            URL url = getClass().getResource(ruta);
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(ais);

            // Inicializa el control de volumen
            controlVolumen = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reproducirEnLoop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void parar() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void reiniciar() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public boolean estaReproduciendose() {
        return clip != null && clip.isRunning();
    }

    public void pausar() {
        if (clip != null && clip.isRunning()) {
            frameActual = clip.getFramePosition();
            clip.stop();
        }
    }

    public void reanudar() {
        if (clip != null && !clip.isRunning()) {
            clip.setFramePosition(frameActual);
            clip.start();
        }
    }

    public void setVolumen(float decibel) {
        if (controlVolumen != null) {
            // Ejemplo: -80.0f = silencio, 0.0f = volumen normal
            controlVolumen.setValue(decibel);
        }
    }

    public void silenciar() {
        if (!silenciado && controlVolumen != null) {
            volumenAnterior = controlVolumen.getValue();
            controlVolumen.setValue(-80.0f); // Silencio total
            silenciado = true;
        }
    }

    public void desSilenciar() {
        if (silenciado && controlVolumen != null) {
            controlVolumen.setValue(volumenAnterior);
            silenciado = false;
        }
    }
}
