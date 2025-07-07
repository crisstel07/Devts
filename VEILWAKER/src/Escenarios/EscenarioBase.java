package Escenarios;

import Enemigos.EnemigoBase;
import java.awt.Graphics;
import java.util.List;
import java.util.ArrayList;

public abstract class EscenarioBase {

    protected int repeticiones;
    protected int anchoFondo;
    protected int altoFondo;
    protected Suelo suelo;

    protected List<EnemigoBase> enemigos = new ArrayList<>();

    public List<EnemigoBase> getEnemigos() {
        return enemigos;
    }

    public int getAnchoTotal() {
        return repeticiones * anchoFondo;
    }

    public abstract void dibujarFondo(Graphics g, int camaraX, int anchoVentana, int altoVentana);

    public abstract void dibujarElementos(Graphics g, int camaraX);

    public abstract void reproducirMusica();

    public void actualizarEnemigos() {
        for (EnemigoBase enemigo : enemigos) {
            if (enemigo.estaVivo()) {
                enemigo.actualizar();
            }
        }
    }

    public void dibujarEnemigos(Graphics g, int camaraX) {
        for (EnemigoBase enemigo : enemigos) {
            if (enemigo.estaVivo()) {
                enemigo.dibujar(g, camaraX);
            }
        }
    }
}
