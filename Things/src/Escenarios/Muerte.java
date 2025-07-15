package Escenarios;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import Main.*;
import Enemigos.*;
import Controles.*;

public class Muerte extends EscenarioBase {
    private BufferedImage fondo;
    private Suelo suelo;
    private int repeticionesInternas;
    private Jugador jugador;
    private Rectangle hitboxReintentar;
private Rectangle hitboxSalir;



    public Muerte(int repeticiones, Jugador jugador) {
        this.repeticiones = repeticiones;
        this.repeticionesInternas = repeticiones;
        this.jugador = jugador;

        try {
            fondo = ImageIO.read(getClass().getResource("/Graficos/Muerte.png"));
            anchoFondo = fondo.getWidth();
            altoFondo = fondo.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
        suelo = new Suelo("/Graficos/suelomuerte.png");

        int anchoVentana = 1365;
int altoVentana = 767;

int anchoHitbox = 200;
int altoHitbox = 150;

hitboxReintentar = new Rectangle(
    anchoVentana / 4 - anchoHitbox / 2,
    altoVentana / 3 - altoHitbox / 2,
    anchoHitbox,
    altoHitbox
);

hitboxSalir = new Rectangle(
    (anchoVentana * 3) / 4 - anchoHitbox / 2,
    altoVentana / 3 - altoHitbox / 2,
    anchoHitbox,
    altoHitbox
);

        
    }

    
    @Override
    public void dibujarFondo(Graphics g, int camaraX, int anchoVentana, int altoVentana) {
        for (int i = 0; i < repeticionesInternas; i++) {
            int drawX = i * anchoFondo - camaraX;
            g.drawImage(fondo, drawX, 0, anchoFondo, altoVentana, null);
        }
    }

   @Override
public void dibujarElementos(Graphics g, int camaraX) {
    suelo.dibujar(g, camaraX);

    // Para depurar: dibuja las hitboxes
    Graphics2D g2d = (Graphics2D) g;
    g2d.setColor(Color.RED);
    g2d.draw(hitboxReintentar);
    g2d.setColor(Color.BLUE);
    g2d.draw(hitboxSalir);
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
    javax.swing.JOptionPane.showMessageDialog(null, "Intentando de nuevo");
    
    // Resetear al escenario previo (ejemplo: nivel 0)
    jugador.resetearPosicion();
    jugador.reiniciar();
   
    

    // Asumimos PanelJuego tiene un método para cambiar nivel actual
    PanelJuego.cambiarNivelEstatico(PanelJuego.nivelPrevioAntesDeMuerte);  // Supondré que lo haremos
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
