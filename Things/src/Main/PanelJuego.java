package Main;

import Enemigos.*;
import Controles.Teclado;
import Controles.Mouse;
import javax.swing.*;
import java.awt.*;
import Escenarios.*;
import java.util.ArrayList;
import Sonido.*;

public class PanelJuego extends JPanel implements Runnable {
    public static final int ANCHO = 1365;
    public static final int ALTO = 767;

    private Thread gameThread;
    private static PanelJuego instanciaGlobal;
    public static int nivelPrevioAntesDeMuerte;
    private Jugador.EstadoJuego estadoJuego = Jugador.EstadoJuego.JUGANDO;
    private int nivelReintentoDestino = 0;
    private boolean enTransicionReintento = false;

    // Escenarios
    private java.util.List<EscenarioBase> niveles;
    public static int nivelActual;
    private EscenarioBase escenario;
    private double balanceoAngulo = 0;
private int desplazamientoY = 0;
private long ultimoBalanceo = 0;
private boolean enBalanceo = false;
private double anguloBalanceo = 0;
private int offsetYBalanceo = 0;

    // Transiciones
    private boolean faseFadeOut = true;
    private int opacidadTransicion = 0;

    private boolean enTransicionNivel = false;
    private boolean enTransicionMuerte = false;

    //Estados
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
        instanciaGlobal = this;
        this.setPreferredSize(new Dimension(ANCHO, ALTO));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        jugador = new Jugador();
        mouse = new Mouse();
        teclado = new Teclado();

        niveles = new ArrayList<>();
        //Manejo de Niveles
      //  niveles.add(new Final(1,jugador));
        niveles.add(new Dia(1, jugador));
        niveles.add(new Tarde(1, jugador));
        niveles.add(new Noche(1, jugador));
        niveles.add(new Muerte(1, jugador));

        nivelActual = 0;
        escenario = niveles.get(nivelActual);
        anchoEscenario = escenario.getAnchoTotal();

        this.addMouseListener(mouse);
        this.addKeyListener(teclado);
        GestorAudio gestorAudio = new GestorAudio();
        gestorAudio.reproducirMusica("/Sonido/music.wav");

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

    public void limpiarEntrada() {
        teclado.resetear();
    }

    public static void cambiarNivelEstatico(int nuevoNivel) {
        if (instanciaGlobal != null) {
            instanciaGlobal.iniciarTransicionReintento(nuevoNivel);
        }
    }

    private void resetearTransiciones() {
        enTransicionNivel = false;
        enTransicionMuerte = false;
        enTransicionReintento = false;

    }

    public void iniciarTransicionReintento(int nivelDestino) {
        resetearTransiciones();
        nivelReintentoDestino = nivelDestino;
        faseFadeOut = true;
        enTransicionReintento = true;
    }

    private void cambiarANivel(int nuevoNivel) {
        nivelActual = nuevoNivel;
        escenario = niveles.get(nivelActual);
        escenario.reiniciarEscenario();
        anchoEscenario = escenario.getAnchoTotal();
        jugador.reiniciar();
        jugador.renacer();
        limpiarEntrada();
        resetearTransiciones();
    }

    public static void iniciarTransicionReintentoEstatico(int nivelDestino) {
        if (instanciaGlobal != null) {
            instanciaGlobal.iniciarTransicionReintento(nivelDestino);
           
        }
    }

    private void iniciarFade() {
        faseFadeOut = true;
        opacidadTransicion = 0;
    }

    private void iniciarTransicionNivel() {
        enTransicionNivel = true;
        iniciarFade();
    }

    private void iniciarTransicionMuerte() {
        enTransicionMuerte = true;
        jugador.renacer();
        iniciarFade();

    }

    private void cambiarANivelSiguiente() {
        if (nivelActual + 1 < niveles.size()) {
            nivelActual++;
            escenario = niveles.get(nivelActual);
            anchoEscenario = escenario.getAnchoTotal();
            jugador.resetearPosicion();

            System.out.println("Nivel cambiado a: " + nivelActual);
            resetearTransiciones();
        } else {
            System.out.println("¡Fin del juego o niveles!");
        }

        enTransicionNivel = false;
    }

    private void cambiarANivelMuerte() {
        nivelPrevioAntesDeMuerte = nivelActual;
        nivelActual = 3;  // índice de tu escenario Muerte
        escenario = niveles.get(nivelActual);
        anchoEscenario = escenario.getAnchoTotal();
        jugador.x = 650;
        jugador.renacer();
        limpiarEntrada();
        resetearTransiciones();

    }

    // ACTUALIZAR LOGICA DEL JUEGO
    public void actualizar() {
        // ✅ Verificar muerte del jugador
        if (jugador.estaMuerto() && nivelActual != 3) {
            jugador.actualizar(false, false, false, false, false, false, anchoEscenario + 300);
            teclado.resetear();
            mouse.resetear();
           

            // Esperar que termine animación de muerte y reiniciar
            if (jugador.animacionMuerteTerminada() && !enTransicionMuerte && !enTransicionNivel && opacidadTransicion == 0) {
                iniciarTransicionMuerte();
            }

        }

        if (faseFadeOut) {
            opacidadTransicion += 5;
            if (opacidadTransicion >= 255) {
                opacidadTransicion = 255;
                faseFadeOut = false;

                if (enTransicionNivel) {
                    cambiarANivelSiguiente();
                } else if (enTransicionMuerte) {
                    cambiarANivelMuerte();
                } else if (enTransicionReintento) {
                    cambiarANivel(nivelReintentoDestino);
                    enTransicionReintento = false;
                }
            }
        } else if (opacidadTransicion > 0) {
            opacidadTransicion -= 5;
        }

        // Manejo de curación
        if (teclado.curar) {
            if (!jugador.estaCargandoCuracion()) {
                jugador.comenzarCuracion();
            }
        } else {
            jugador.cancelarCuracion();
        }

        jugador.actualizarCuracion();

        // ✅ Actualizar al jugador (mueve, salta, ataca, animaciones)
        jugador.actualizar(
                teclado.izquierda, teclado.derecha, teclado.arriba, teclado.abajo,
                mouse.atacar, teclado.saltar,
                anchoEscenario + 300
        );
      

        // ✅ Manejar transiciones de nivel
        // ✅ Ajustar la cámara
        actualizarCamara();

        // ✅ Cambiar de nivel si llegó al final
        if (jugador.getX() >= anchoEscenario + 100 && !enTransicionNivel && !enTransicionMuerte && escenario.permiteSalida()) {
            iniciarTransicionNivel();
        }

        // ✅ Actualizar enemigos
        escenario.actualizarEnemigos();

        // ✅ Resolver colisión (empuje) con enemigos
        for (EnemigoBase enemigo : escenario.getEnemigos()) {
            resolverEmpuje(jugador, enemigo);
        }

        // ✅ Verificar daño por colisión con enemigos
        verificarDañoPorColision();

        // ✅ Generar nueva hitbox de ataque si corresponde
        jugador.generarHitboxAtaque(hitboxesDeAtaque);

        // ✅ Daño cuerpo a cuerpo directo
        verificarDañoCuerpoACuerpo();

        // ✅ Actualizar y limpiar hitboxes expiradas
        actualizarHitboxes();

        // ✅ Aplicar daño a enemigos por hitboxes activas
        verificarDañoPorHitboxes();

        // ✅ Actualizar partículas
        actualizarParticulas();

        // ✅ Mostrar hitboxes si está activado
        mostrarHitboxes = teclado.mostrarHitbox;
        
    //    if (nivelActual ==0) {
    //jugador.limitarMovimiento(0, ANCHO);
    //}

    long ahora = System.currentTimeMillis();
if (ahora - ultimoBalanceo > 6000 && !enBalanceo) { // cada 6 segundos
    enBalanceo = true;
    anguloBalanceo = 0;
    ultimoBalanceo = ahora;
}

// Si está en balanceo
if (enBalanceo) {
    anguloBalanceo += 0.1;
    offsetYBalanceo = (int)(Math.sin(anguloBalanceo) * 4);

    if (anguloBalanceo > Math.PI) {
        enBalanceo = false;
        offsetYBalanceo = 0;
    }
}
    }

    private void actualizarCamara() {
        int mitadPantalla = ANCHO / 2;
        int jugadorX = jugador.getX();
        if (jugadorX < mitadPantalla) {
            camaraX = 0;
        } else if (jugadorX <= anchoEscenario - mitadPantalla) {
            camaraX = jugadorX - mitadPantalla;
        } else {
            camaraX = anchoEscenario - ANCHO;
        }
    }

    private void verificarDañoPorColision() {
        for (EnemigoBase enemigo : escenario.getEnemigos()) {
            if (enemigo.estaVivo() && enemigo.getRect().intersects(jugador.getRect()) && !jugador.esInvulnerable()) {
                int direccionEmpuje = (enemigo.getVelocidadX() > 0) ? 40 : -40;

                if (enemigo instanceof Fargo) {
                    jugador.recibirDaño(2, direccionEmpuje);
                } else {
                    jugador.recibirDaño(1, direccionEmpuje);
                }

                generarParticulasGolpe(jugador.getX(), jugador.getY());
            }
        }
    }

    private void verificarDañoCuerpoACuerpo() {
        if (jugador.estaAtacando()) {
            for (EnemigoBase enemigo : escenario.getEnemigos()) {
                if (enemigo.estaVivo() && jugador.getRect().intersects(enemigo.getRect())) {
                    int direccionEmpuje = (jugador.getX() < enemigo.getX()) ? +60 : -60;
                    enemigo.recibirDano(1, direccionEmpuje);
                }
            }
        }
    }

    private void actualizarHitboxes() {
        for (int i = hitboxesDeAtaque.size() - 1; i >= 0; i--) {
            AtaqueHitbox hb = hitboxesDeAtaque.get(i);
            hb.actualizar();
            if (!hb.estaActiva()) {
                hitboxesDeAtaque.remove(i);
            }
        }
    }

    private void verificarDañoPorHitboxes() {
        for (EnemigoBase enemigo : escenario.getEnemigos()) {
            if (!enemigo.estaVivo()) {
                continue;
            }

            for (AtaqueHitbox hb : hitboxesDeAtaque) {
                if (hb.getRect().intersects(enemigo.getRect())) {
                    int direccionEmpuje = (jugador.getX() < enemigo.getX()) ? +50 : -50;
                       GestorAudio.reproducirEfecto("pegar");
                    boolean murio = enemigo.recibirDano(1, direccionEmpuje);
                    if (murio) {
                        jugador.ganarFaseLunar();
                    }

                    generarParticulasGolpe(enemigo.getX(), enemigo.getY());
                }
            }
        }
    }

    private void actualizarParticulas() {
        for (int i = particulasGolpe.size() - 1; i >= 0; i--) {
            ParticulasGolpe p = particulasGolpe.get(i);
            p.actualizar();
            if (!p.estaViva()) {
                particulasGolpe.remove(i);
            }
        }
    }

    private void generarParticulasGolpe(int x, int y) {
        int offsetVertical = -200;
        int centroX = x + jugador.getAncho() / 2 - ParticulasGolpe.PARTICULA_ANCHO / 2;
        int centroY = y + jugador.getAlto() / 2 - ParticulasGolpe.PARTICULA_ALTO / 2 + offsetVertical;
        particulasGolpe.add(new ParticulasGolpe(centroX, centroY));
    }

    private void resolverEmpuje(Jugador jugador, EnemigoBase enemigo) {
        Rectangle jugadorRect = jugador.getRect();
        Rectangle enemigoRect = enemigo.getRect();

        if (!jugadorRect.intersects(enemigoRect)) {
            return;
        }

        int overlapX = Math.min(
                jugadorRect.x + jugadorRect.width - enemigoRect.x,
                enemigoRect.x + enemigoRect.width - jugadorRect.x
        );

        if (overlapX <= 0) {
            return;
        }

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

    // 🌀 Aplicar el balanceo vertical
    g.translate(0, offsetYBalanceo);

    int camaraX = this.camaraX;

    escenario.dibujarFondo(g, camaraX, ANCHO, ALTO);
     escenario.dibujarElementos(g, camaraX);
    jugador.dibujar(g, camaraX);
   
    escenario.dibujarEnemigos(g, camaraX);
    for (ParticulasGolpe p : particulasGolpe) {
        p.dibujar(g, camaraX);
    }

    if (faseFadeOut || opacidadTransicion > 0) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0, 0, 0, opacidadTransicion));
        g2.fillRect(0, 0, ANCHO, ALTO);
    }

    if (mostrarHitboxes) {
        // Dibujo de hitboxes (ya con el balanceo aplicado)
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);
        Rectangle rJugador = jugador.getRect();
        g2d.drawRect(rJugador.x - camaraX, rJugador.y, rJugador.width, rJugador.height);

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

        g2d.draw(Muerte.hitboxReintentar);
        g2d.draw(Muerte.hitboxSalir);

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

    // 🔁 Revertir el translate para evitar afectar componentes futuros
    g.translate(0, -offsetYBalanceo);
}

}
