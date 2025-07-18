package Main;

import Enemigos.*;
import Controles.Teclado;
import Controles.Mouse;
import javax.swing.*;
import java.awt.*;
import Escenarios.*;
import java.util.ArrayList;
import Sonido.*;
import com.google.gson.JsonObject; // Importar JsonObject

public class PanelJuego extends JPanel implements Runnable {
    public static final int ANCHO = 1365;
    public static final int ALTO = 767;

    private Thread gameThread;
    private volatile boolean isGamePaused = false; // Estado de pausa del juego
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

    // Estados
    // Cámara
    private int camaraX = 0;
    private int anchoEscenario;

    // Clases del juego
    private Jugador jugador;
    private Mouse mouse;
    private Teclado teclado;
    private java.util.List<AtaqueHitbox> hitboxesDeAtaque = new ArrayList<>();

    // Hitbox
    private boolean mostrarHitboxes = false;

    // Particulas
    private java.util.List<ParticulasGolpe> particulasGolpe = new ArrayList<>();

    // Referencia a los datos del personaje como JsonObject
    private JsonObject characterData;

    // Instancia de GestorAudio para asegurar que su constructor se llame
    private GestorAudio gestorAudio; 

    // CONSTRUCTOR
    public PanelJuego(JsonObject initialCharacterData) { // MODIFICADO: Ahora acepta JsonObject
        instanciaGlobal = this;
        this.setPreferredSize(new Dimension(ANCHO, ALTO));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        // this.requestFocusInWindow(); // Se llamará desde VentanaJuego.startGame()

        this.characterData = initialCharacterData; // Asigna los datos iniciales del personaje
        
        jugador = new Jugador(); // La vida inicial de 5 se establece en el constructor de Jugador
        
        mouse = new Mouse();
        teclado = new Teclado();

        niveles = new ArrayList<>();
        niveles.add(new Dia(1, jugador));
        niveles.add(new Tarde(1, jugador));
        niveles.add(new Noche(1, jugador));
        niveles.add(new Muerte(1, jugador));

        // Inicializa el nivel actual del juego según los datos del personaje si existen, o el nivel 0 por defecto
        if (characterData != null && characterData.has("current_level") && !characterData.get("current_level").isJsonNull()) {
            nivelActual = characterData.get("current_level").getAsInt();
        } else {
            nivelActual = 0;
        }
        escenario = niveles.get(nivelActual);
        anchoEscenario = escenario.getAnchoTotal();

        this.addMouseListener(mouse);
        this.addKeyListener(teclado);
        
        // ¡IMPORTANTE! Crear una instancia de GestorAudio aquí
        // Esto asegura que el constructor de GestorAudio se ejecute,
        // inicializando las variables estáticas como 'paso'.
        gestorAudio = new GestorAudio(); 
        // Si quieres que la música del juego empiece aquí:
        // gestorAudio.reproducirMusica("/Sonido/music.wav"); 
    }

    // Setter para el estado de pausa del juego
    public void setGamePaused(boolean paused) {
        this.isGamePaused = paused;
        System.out.println("Juego pausado: " + paused);
        if (paused) {
            limpiarEntrada();
        }
    }

    // Getter para el estado de pausa
    public boolean isGamePaused() {
        return isGamePaused;
    }

    // Método para detener el hilo del juego de forma segura
    public void stopGameThread() {
        if (gameThread != null) {
            Thread dummyThread = gameThread;
            gameThread = null; // Señala al hilo para que termine su bucle
            dummyThread.interrupt(); // Interrumpe el hilo para sacarlo de un posible sleep/wait
            try {
                dummyThread.join(); // Espera a que el hilo termine completamente
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restaura el estado de interrupción
                System.err.println("Hilo de juego interrumpido al detener.");
            }
        }
    }

    // INICAR EL JUEGO
    public void iniciarJuego() {
        isGamePaused = false;
        if (gameThread == null || !gameThread.isAlive()) { // Solo crear e iniciar si no está ya corriendo
            gameThread = new Thread(this);
            gameThread.start();
        }
        // Cargar datos del personaje desde el JsonObject
        if (characterData != null && jugador != null) {
            if (characterData.has("x_position") && !characterData.get("x_position").isJsonNull()) {
                jugador.setX(characterData.get("x_position").getAsInt());
            }
            if (characterData.has("y_position") && !characterData.get("y_position").isJsonNull()) {
                jugador.setY(characterData.get("y_position").getAsInt());
            }
            // ¡IMPORTANTE! La línea para cargar vida_actual desde la DB está COMENTADA/ELIMINADA aquí
            // para que la vida SIEMPRE inicie en 5 (o lo que esté en Jugador.java).
            // if (characterData.has("vida_actual") && !characterData.get("vida_actual").isJsonNull()) {
            //     jugador.setVida(characterData.get("vida_actual").getAsInt());
            // }
            if (characterData.has("energia") && !characterData.get("energia").isJsonNull()) {
                jugador.setFasesLunares(characterData.get("energia").getAsInt());
            }
            if (characterData.has("experiencia") && !characterData.get("experiencia").isJsonNull()) {
                jugador.setExperience(characterData.get("experiencia").getAsInt());
            }
            if (characterData.has("nivel") && !characterData.get("nivel").isJsonNull()) {
                jugador.setNivel(characterData.get("nivel").getAsInt());
            }
            
            // Actualizar el escenario si el nivel ha cambiado al cargar
            if (characterData.has("current_level") && !characterData.get("current_level").isJsonNull()) {
                int loadedLevel = characterData.get("current_level").getAsInt();
                if (nivelActual != loadedLevel && loadedLevel >= 0 && loadedLevel < niveles.size()) {
                    nivelActual = loadedLevel;
                    escenario = niveles.get(nivelActual);
                    escenario.reiniciarEscenario();
                    anchoEscenario = escenario.getAnchoTotal();
                }
            }
        }
        this.requestFocusInWindow(); // Asegura que PanelJuego reciba el foco al iniciar
    }

    // METODO RUN: Bucle principal del juego
    @Override
    public void run() {
        int FPS = 60;
        double intervalo = 1_000_000_000 / FPS;
        double delta = 0;
        long tiempoAnterior = System.nanoTime();

        while (gameThread != null) { // El bucle continúa mientras gameThread no sea nulo
            long tiempoActual = System.nanoTime();
            delta += (tiempoActual - tiempoAnterior) / intervalo;
            tiempoAnterior = tiempoActual;

            if (delta >= 1) {
                if (!isGamePaused) { // ¡Solo actualiza la lógica del juego si NO está pausado!
                    actualizar();
                }
                repaint(); // Siempre repinta para mostrar el menú de pausa o el juego
                delta--;
            }
        }
    }

    public void limpiarEntrada() {
        teclado.resetear();
        mouse.resetear();
        Controles.Teclado.desbloquear(); // ¡NUEVO! Asegura que el teclado se desbloquee al limpiar la entrada
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
        opacidadTransicion = 0; // Resetear opacidad para un nuevo fade
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
        nivelActual = 3;
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

        // Manejo de curación (solo si el juego NO está pausado)
        if (!isGamePaused) { 
            if (teclado.curar) {
                if (!jugador.estaCargandoCuracion()) {
                    jugador.comenzarCuracion();
                }
            } else {
                jugador.cancelarCuracion();
            }
        }

        jugador.actualizarCuracion();

        // ✅ Actualizar al jugador (mueve, salta, ataca, animaciones) (solo si el juego NO está pausado)
        if (!isGamePaused) {
            jugador.actualizar(
                    teclado.izquierda, teclado.derecha, teclado.arriba, teclado.abajo,
                    mouse.atacar, teclado.saltar,
                    anchoEscenario + 300
            );
        } else {
            // Si el juego está pausado, actualiza al jugador sin entrada de movimiento/ataque
            // para que las animaciones de idle o muerte sigan reproduciéndose si es necesario.
            jugador.actualizar(false, false, false, false, false, false, anchoEscenario + 300);
        }
        
        // ¡NUEVO! Actualizar CharacterData con el estado actual del jugador para el guardado
        updateCharacterDataFromPlayer();

        // ✅ Manejar transiciones de nivel
        // ✅ Ajustar la cámara
        actualizarCamara();

        // ✅ Cambiar de nivel si llegó al final
        if (jugador.getX() >= anchoEscenario + 100 && !enTransicionNivel && !enTransicionMuerte && escenario.permiteSalida()) {
            iniciarTransicionNivel();
        }

        escenario.actualizarEnemigos();

        for (EnemigoBase enemigo : escenario.getEnemigos()) {
            resolverEmpuje(jugador, enemigo);
        }

        verificarDañoPorColision();

        jugador.generarHitboxAtaque(hitboxesDeAtaque);

        verificarDañoCuerpoACuerpo();

        actualizarHitboxes();

        verificarDañoPorHitboxes();

        actualizarParticulas();

        mostrarHitboxes = teclado.mostrarHitbox;
        
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

    // ¡NUEVO! Método para actualizar el objeto CharacterData con el estado actual del jugador
    private void updateCharacterDataFromPlayer() {
        if (characterData != null && jugador != null) {
            // Asegúrate de que tu clase Jugador tenga getters para estos atributos
            characterData.addProperty("x_position", jugador.getX());
            characterData.addProperty("y_position", jugador.getY());
            characterData.addProperty("vida_actual", jugador.getVida()); // Usar getVida()
            characterData.addProperty("energia", jugador.getFasesLunares()); // Usar getFasesLunares()
            characterData.addProperty("experiencia", jugador.getExperience()); // Guardar experiencia
            characterData.addProperty("nivel", jugador.getNivel()); // Guardar nivel del jugador
            characterData.addProperty("current_level", nivelActual); // Guardar el nivel actual del escenario
        }
    }

    // ¡NUEVO! Método para obtener el CharacterData actual del juego para guardarlo
    public JsonObject getCurrentCharacterData() {
        // Asegúrate de que characterData siempre esté actualizado antes de devolverlo
        updateCharacterDataFromPlayer(); // Llama a este método para sincronizar
        return characterData;
    }

    // ¡NUEVO! Setter para actualizar el CharacterData si se cambia externamente (ej. al cargar un nuevo juego)
    public void setCharacterData(JsonObject newCharacterData) {
        this.characterData = newCharacterData;
        // También actualiza el jugador y el nivel si es necesario
        if (jugador != null && newCharacterData != null) {
            if (newCharacterData.has("x_position") && !newCharacterData.get("x_position").isJsonNull()) {
                jugador.setX(newCharacterData.get("x_position").getAsInt());
            }
            if (newCharacterData.has("y_position") && !newCharacterData.get("y_position").isJsonNull()) {
                jugador.setY(newCharacterData.get("y_position").getAsInt());
            }
            // ¡IMPORTANTE! La línea para cargar vida_actual desde la DB está COMENTADA/ELIMINADA aquí
            // para que la vida SIEMPRE inicie en 5 (o lo que esté en Jugador.java).
            // if (newCharacterData.has("vida_actual") && !newCharacterData.get("vida_actual").isJsonNull()) {
            //     jugador.setVida(newCharacterData.get("vida_actual").getAsInt());
            // }
            if (newCharacterData.has("energia") && !newCharacterData.get("energia").isJsonNull()) {
                jugador.setFasesLunares(newCharacterData.get("energia").getAsInt());
            }
            if (newCharacterData.has("experiencia") && !newCharacterData.get("experiencia").isJsonNull()) {
                jugador.setExperience(newCharacterData.get("experiencia").getAsInt());
            }
            if (newCharacterData.has("nivel") && !newCharacterData.get("nivel").isJsonNull()) {
                jugador.setNivel(newCharacterData.get("nivel").getAsInt());
            }
        }
        if (newCharacterData.has("current_level") && !newCharacterData.get("current_level").isJsonNull()) {
            int newLevel = newCharacterData.get("current_level").getAsInt();
            if (nivelActual != newLevel && newLevel >= 0 && newLevel < niveles.size()) {
                nivelActual = newLevel;
                escenario = niveles.get(nivelActual);
                escenario.reiniciarEscenario();
                anchoEscenario = escenario.getAnchoTotal();
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
                    GestorAudio.reproducirEfecto("pegar"); // Asegúrate de que este efecto exista
                    boolean murio = enemigo.recibirDano(1, direccionEmpuje);
                    if (murio) {
                        jugador.ganarFaseLunar();
                    }

                    generarParticulasGolpe(enemigo.getX(), enemigo.getY());
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
                    GestorAudio.reproducirEfecto("pegar"); // Asegúrate de que este efecto exista
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
        jugador.dibujar(g, camaraX); // Jugador dibuja su propio HUD
        
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

