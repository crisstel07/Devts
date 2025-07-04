package Escenarios;
import Main.Suelo;
import java.awt.*;

public abstract class EscenarioBase {

    protected int repeticiones;
    protected int anchoFondo;
    protected int altoFondo;
    protected Suelo suelo;

    /**
     * El ancho total del escenario, importante para la cámara y límites del jugador.
     */
    public int getAnchoTotal() {
        return repeticiones * anchoFondo;
    }

    /**
     * Método que dibuja el fondo del escenario
     */
    public abstract void dibujarFondo(Graphics g, int camaraX, int anchoVentana, int altoVentana);

    /**
     * Método para dibujar suelo, plataformas, enemigos, etc.
     */
    public abstract void dibujarElementos(Graphics g, int camaraX);

    public Suelo getSuelo() {
        return suelo;
    }
    
    
    /**
     * Método para reproducir música de fondo (opcional)
     */
    public abstract void reproducirMusica();

}
