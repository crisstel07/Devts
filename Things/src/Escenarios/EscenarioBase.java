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
    private double balanceoAngulo = 0;
private int desplazamientoY = 0;

    public  final List<EnemigoBase> enemigos = new ArrayList<>();
    private List<EnemigoBase> enemigosPendientes = new ArrayList<>();

    public List<EnemigoBase> getEnemigos() {
    synchronized (enemigos) {
        return new ArrayList<>(enemigos);
    }
    }


    public int getAnchoTotal() {
        return repeticiones * anchoFondo;
    }
    public abstract void cargarEnemigos();
    
public void reiniciarEscenario() {
        enemigos.clear();
        enemigosPendientes.clear();
        cargarEnemigos(); // 🔁 vuelve a meter los enemigos
    }
    
    public abstract void dibujarFondo(Graphics g, int camaraX, int anchoVentana, int altoVentana);

    public abstract void dibujarElementos(Graphics g, int camaraX);

    public abstract void reproducirMusica();
    
    public void agregarEnemigo(EnemigoBase enemigo) {
    enemigosPendientes.add(enemigo);
}


    public void actualizarEnemigos() {
   synchronized (enemigos) {
        // Actualizar existentes
        for (EnemigoBase enemigo : enemigos) {
            if (enemigo.estaVivo()) {
                enemigo.actualizar();
            }
        }

        // Agregar los nuevos
        enemigos.addAll(enemigosPendientes);
        enemigosPendientes.clear();
    }
   enemigos.removeIf(e -> !e.estaVivo());
    }

    public void dibujarEnemigos(Graphics g, int camaraX) {
        synchronized (enemigos) {
        for (EnemigoBase enemigo : enemigos) {
            if (enemigo.estaVivo()) {
                enemigo.dibujar(g, camaraX);
            }
        }
    }
}
    public boolean permiteSalida() {
    return true;
}
public int getLimiteEscenario() {
    return getAnchoTotal();  // por defecto, el escenario es tan ancho como sus repeticiones
}

}
