package Escenarios;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import Main.*;


public class Muerte extends EscenarioBase {

    private BufferedImage fondo;
    private Suelo suelo;
    private int repeticionesInternas;
    private Jugador jugador;
    public static Rectangle hitboxReintentar;
    public static Rectangle hitboxSalir;

    public Muerte(int repeticiones, Jugador jugador) {
        this.repeticiones = repeticiones;
        this.repeticionesInternas = repeticiones;
        this.jugador = jugador;

        try {
            fondo = ImageIO.read(getClass().getResource("/Graficos/Muertepantalla.png"));
            anchoFondo = fondo.getWidth();
            altoFondo = fondo.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
        suelo = new Suelo("/Graficos/PisoMuerte.png");

        int anchoVentana = 1365;
        int altoVentana = 767;

        int anchoHitbox = 170;
        int altoHitbox = 100;

        hitboxReintentar = new Rectangle(400, 250, anchoHitbox, altoHitbox);

        hitboxSalir = new Rectangle(750, 250, anchoHitbox, altoHitbox);

    }

    @Override
    public void dibujarFondo(Graphics g, int camaraX, int anchoVentana, int altoVentana) {
        for (int i = 0; i < repeticionesInternas; i++) {
            int drawX = i * anchoFondo - camaraX;
            g.drawImage(fondo, drawX, 0, anchoFondo, altoVentana, null);
        }
    }
    @Override
    public void cargarEnemigos(){
    
}
    @Override
    public void dibujarElementos(Graphics g, int camaraX) {
        suelo.dibujar(g, camaraX);
      
    }

    @Override
    public void actualizarEnemigos() {
        // No hay enemigos, pero usamos esto para verificar las zonas
        Rectangle jugadorRect = jugador.getRect();

        if (hitboxReintentar.intersects(jugadorRect)) {
            intentarDeNuevo();
            
        }

        if (hitboxSalir.intersects(jugadorRect)) {
            salirDelJuego();
        }
    }

    private void intentarDeNuevo() {

        // Resetear al escenario previo (ejemplo: nivel 0)
      PanelJuego.iniciarTransicionReintentoEstatico(PanelJuego.nivelPrevioAntesDeMuerte);
      jugador.renacer();
      jugador.invulnerable = false;
    }

    private void salirDelJuego() {
        System.exit(0);
    }

    @Override
    public void reproducirMusica() {
        System.out.println("Reproduciendo música de Noche...");
    }

    @Override
    public boolean permiteSalida() {
        return false;
    }

}
