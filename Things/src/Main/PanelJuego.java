package Main;

import Enemigos.*;
import Controles.Teclado;
import Controles.Mouse;
import javax.swing.*;
import java.awt.*;
import Escenarios.*;
import java.util.ArrayList;

public class PanelJuego extends JPanel implements Runnable {

    public static final int ANCHO = 1365;
    public static final int ALTO = 767;

    private Thread gameThread;

    // Escenarios
    private java.util.List<EscenarioBase> niveles;
    private int nivelActual;
    private EscenarioBase escenario;

    // Transiciones
    private boolean enTransicion = false;
    private boolean faseFadeOut = true;
    private int opacidadTransicion = 0;
    private EscenarioBase proximoEscenario = null;

    // Cámara
    private int camaraX = 0;
    private int anchoEscenario;

    // Clases del juego
    private Jugador jugador;
    private Mouse mouse;
    private Teclado teclado;
    private java.util.List<AtaqueHitbox> hitboxesDeAtaque = new ArrayList<>();

    //HItbox
    private boolean mostrarHitboxes = false;

    //Particulas
    private java.util.List<ParticulasGolpe> particulasGolpe = new ArrayList<>();

    //CONSTRUCTOR
    public PanelJuego() {
        this.setPreferredSize(new Dimension(ANCHO, ALTO));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        jugador = new Jugador();
        mouse = new Mouse();
        teclado = new Teclado();

        niveles = new ArrayList<>();
        //Manejo de Niveles
        niveles.add(new Dia(1, jugador));
        niveles.add(new Noche(1, jugador));
        niveles.add(new NocheOjos(1, jugador));

        nivelActual = 0;
        escenario = niveles.get(nivelActual);
        anchoEscenario = escenario.getAnchoTotal();

        this.addMouseListener(mouse);
        this.addKeyListener(teclado);
    }

    //INICAR EL JUEGO
    public void iniciarJuego() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    //METODO RUN 
    @Override
    public void run() {
        int FPS = 60;
        double intervalo = 1_000_000_000 / FPS;
        double delta = 0;
        long tiempoAnterior = System.nanoTime();

        while (gameThread != null) {
            long tiempoActual = System.nanoTime();
            delta += (tiempoActual - tiempoAnterior) / intervalo;
            tiempoAnterior = tiempoActual;

            if (delta >= 1) {
                actualizar();
                repaint();
                delta--;
            }
        }
    }

    //MANEJAR TRANSICIONES 
    private void iniciarTransicion() {
        if (nivelActual + 1 < niveles.size()) {
            enTransicion = true;
            faseFadeOut = true;
            opacidadTransicion = 0;
            proximoEscenario = niveles.get(nivelActual + 1);
        } else {
            System.out.println("¡Fin del juego o niveles!");
        }
    }

    // Actualizar lógica del juego
    public void actualizar() {

        // ✅ Actualizar jugador (siempre se actualiza para movimiento, gravedad, etc.)
        jugador.actualizar(
                teclado.izquierda, teclado.derecha, teclado.arriba, teclado.abajo,
                mouse.atacar, teclado.saltar,
                anchoEscenario + 300 // Permite salir 300 px fuera
        );

        // ✅ Manejar transiciones de nivel (fade in/out)
        if (enTransicion) {
            if (faseFadeOut) {
                opacidadTransicion += 2;
                if (opacidadTransicion >= 255) {
                    opacidadTransicion = 255;

                    nivelActual++;
                    escenario = proximoEscenario;
                    anchoEscenario = escenario.getAnchoTotal();
                    jugador.resetearPosicion();
                    escenario.reproducirMusica();
                    faseFadeOut = false;
                    camaraX = 0;
                }
            } else {
                opacidadTransicion -= 2;
                if (opacidadTransicion <= 0) {
                    opacidadTransicion = 0;
                    enTransicion = false;
                }
            }
            return; // Durante transición negra, no procesamos nada más
        }

        // ✅ Ajustar cámara
        int mitadPantalla = ANCHO / 2;
        int jugadorX = jugador.getX();
        if (jugadorX < mitadPantalla) {
            camaraX = 0;
        } else if (jugadorX <= anchoEscenario - mitadPantalla) {
            camaraX = jugadorX - mitadPantalla;
        } else {
            camaraX = anchoEscenario - ANCHO;
        }

        // ✅ Checar si el jugador llegó al final del escenario
        if (jugador.getX() >= anchoEscenario + 100) {
            iniciarTransicion();
        }

       escenario.actualizarEnemigos(); // ✅ primero actualiza
       // ✅ Resolver empuje entre jugador y enemigos
for (EnemigoBase enemigo : escenario.getEnemigos()) {
    resolverEmpuje(jugador, enemigo);
}

for (EnemigoBase enemigo : escenario.getEnemigos()) {
    if (enemigo.estaVivo() && enemigo.getRect().intersects(jugador.getRect()) && !jugador.esInvulnerable()) {
        int direccionEmpuje = (enemigo.getVelocidadX() > 0) ? 40 : -40;
       if (enemigo instanceof Fargo) {
    jugador.recibirDaño(2, direccionEmpuje);
} else {
    jugador.recibirDaño(1, direccionEmpuje);
}
    }
}


        // ✅ Generar nueva hitbox de ataque si corresponde
        jugador.generarHitboxAtaque(hitboxesDeAtaque);

        // ✅ Daño directo al enemigo por cuerpo a cuerpo mientras atacas
        if (jugador.estaAtacando()) {
            for (EnemigoBase enemigo : escenario.getEnemigos()) {
                if (enemigo.estaVivo() && jugador.getRect().intersects(enemigo.getRect())) {
                    int direccionEmpuje = (jugador.getX() < enemigo.getX()) ? +60 : -60;
                    enemigo.recibirDano(1, direccionEmpuje);

                }
            }
        }

        // ✅ Actualizar y limpiar hitboxes expiradas
        for (int i = hitboxesDeAtaque.size() - 1; i >= 0; i--) {
            AtaqueHitbox hb = hitboxesDeAtaque.get(i);
            hb.actualizar();
            if (!hb.estaActiva()) {
                hitboxesDeAtaque.remove(i);
            }
        }

        // ✅ Aplicar daño a enemigos que colisionan con hitboxes activas
        for (EnemigoBase enemigo : escenario.getEnemigos()) {
            if (!enemigo.estaVivo()) {
                continue;
            }
            for (AtaqueHitbox hb : hitboxesDeAtaque) {
                if (hb.getRect().intersects(enemigo.getRect())) {
                    int direccionEmpuje = (jugador.getX() < enemigo.getX()) ? +50 : -50;
                    // Calculamos el centro del enemigo para ubicar la partícula
                    int offsetVertical = -200;
                    int centroX = enemigo.getX() + enemigo.getAncho() / 2 - ParticulasGolpe.PARTICULA_ANCHO / 2;
                    int centroY = enemigo.getY() + enemigo.getAlto() / 2 - ParticulasGolpe.PARTICULA_ALTO / 2 + offsetVertical;

                    particulasGolpe.add(new ParticulasGolpe(centroX, centroY));

                    enemigo.recibirDano(1, direccionEmpuje);
                }
            }
        }

        for (int i = particulasGolpe.size() - 1; i >= 0; i--) {
            ParticulasGolpe p = particulasGolpe.get(i);
            p.actualizar();
            if (!p.estaViva()) {
                particulasGolpe.remove(i);
            }
        }

        // ✅ Manejar mostrar hitboxes
        mostrarHitboxes = teclado.mostrarHitbox;
    }
    
    private void resolverEmpuje(Jugador jugador, EnemigoBase enemigo) {
    Rectangle jugadorRect = jugador.getRect();
    Rectangle enemigoRect = enemigo.getRect();

    if (!jugadorRect.intersects(enemigoRect)) return;

    int overlapX = Math.min(
        jugadorRect.x + jugadorRect.width - enemigoRect.x,
        enemigoRect.x + enemigoRect.width - jugadorRect.x
    );

    if (overlapX <= 0) return;

    // Separar a ambos a la mitad
    if (jugador.getX() < enemigo.getX()) {
        jugador.moverX(-overlapX / 2);
        enemigo.moverX(+overlapX / 2);
    } else {
        jugador.moverX(+overlapX / 2);
        enemigo.moverX(-overlapX / 2);
    }
}



    // -----------------------------------------------------------------
    // DIBUJAR 
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int camaraX = this.camaraX;

        escenario.dibujarFondo(g, camaraX, ANCHO, ALTO);

        jugador.dibujar(g, camaraX);

        escenario.dibujarElementos(g, camaraX);

        if (enTransicion) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(0, 0, 0, opacidadTransicion));
            g2.fillRect(0, 0, ANCHO, ALTO);
        }

        escenario.dibujarEnemigos(g, camaraX);
        for (ParticulasGolpe p : particulasGolpe) {
            p.dibujar(g, camaraX);
        }
        if (mostrarHitboxes) {
            // HITBOX DEL JUGADOR
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.RED);
            Rectangle rJugador = jugador.getRect();
            g2d.drawRect(rJugador.x - camaraX, rJugador.y, rJugador.width, rJugador.height);

            // HITBOX DE ENEMIGOS
            g2d.setColor(Color.BLUE);
            for (EnemigoBase enemigo : escenario.getEnemigos()) {
                if (enemigo.estaVivo()) {
                    Rectangle rEnemigo = enemigo.getRect();
                    g2d.drawRect(rEnemigo.x - camaraX, rEnemigo.y, rEnemigo.width, rEnemigo.height);
                }
            }
            for (AtaqueHitbox hb : hitboxesDeAtaque) {
                hb.dibujar(g, camaraX);
            }
            // HITBOX BALAS FARGANO
            g2d.setColor(Color.ORANGE);
            for (EnemigoBase enemigo : escenario.getEnemigos()) {
                if (enemigo instanceof Fargano) {
                    Fargano fargano = (Fargano) enemigo;
                    for (BalaFargano b : fargano.getBalas()) {
                        if (b.isActiva()) {
                            Rectangle rBala = b.getRect();
                            g2d.drawRect(rBala.x - camaraX, rBala.y, rBala.width, rBala.height);
                        }
                    }
                }
            }

        }

    }
}
