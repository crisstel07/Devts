package Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import Controles.*;
import Sonido.*;
import Escenarios.*;

public class Jugador {

    //1. ATRIBUTOS JUGADOR
    public int x, y;
    private int velocidadX = 0;
    private int vidas; // Esta es la variable que representa la vida del jugador
    private int nivel; // NUEVO: Variable para el nivel del jugador
    private int experiencia; // NUEVO: Variable para la experiencia del jugador
    private final int VELOCIDAD_MOVIMIENTO = 7;
    private final int FUERZA_SALTO = -20;
    private int velocidadY = 0;
    private final int ANCHO = 120;
    private final int ALTO = 130;
    private boolean estaCaminando = false;
    private long ultimoPasoTiempo = 0;
    private long intervaloPaso = 330;
    // Control de cooldown de ataque
    private boolean puedeAtacar = true;
    private long tiempoUltimoAtaque = 0;
    private final long COOLDOWN_ATAQUE = 1000000000L; // 1 segundo en nanosegundos
    // long ahora = System.nanoTime(); // Esta variable local no debe ser un atributo de clase

    //ESTADO DEL JUGADOR
    public enum Estado {
        IDLE, CAMINANDO_DERECHA, CAMINANDO_IZQUIERDA, SALTANDO, ATERRIZANDO, DAÑADO, MUERTO, RENACIENDO
    }
    public static Estado estado = Estado.IDLE;
    private boolean mirandoDerecha = true;

    //ESTADO DEL JUEGO (Este enum es más para PanelJuego, pero si lo usas aquí, está bien)
    public enum EstadoJuego {
        JUGANDO, MURIENDO, FADE_OUT, REINICIANDO
    }
    //VIDA, DAÑO, CARGA
    public boolean invulnerable = false;
    private int timerInvulnerable = 0;
    private int cantidad; // Cantidad de daño recibido
    private int fasesLunares = 1;  // empieza en vacía
    private final int FASES_LUNARES_MAX = 6;
    private boolean cargandoCuracion = false;
    private final int DURACION_INVULNERABLE_DEFAULT = 120;
    private int retrocesoX = 0;
    private final int VIDA_MAXIMA = 5; // MODIFICADO: Vida máxima de 5

    //SPRITES Y HUD
    private BufferedImage[] fasesLunaresSprites;
    private BufferedImage[] gatosIdleSprites;
    private BufferedImage[] gatosPerderSprites;
    private BufferedImage[] gatosRecuperarSprites;
    private Animacion gatosIdleAnim;
    private Animacion gatosPerderAnim;
    private Animacion gatosRecuperarAnim;
    private Animacion curacionAnim;
    private boolean animandoPerderVida = false;
    private boolean animandoRecuperarVida = false;

    // FÍSICA Y GRAVEDAD
    private final double GRAVEDAD = 1;
    private final int SUELO_Y = PanelJuego.ALTO - 150 - ALTO;
    private boolean enSuelo = false;

    // TAMAÑOS Y OFFSETS PARA HITBOX
    private final int OFFSET_HITBOX_X = 25;
    private final int OFFSET_HITBOX_Y = 10;
    private final int HITBOX_ANCHO = ANCHO - 60;
    private final int HITBOX_ALTO = ALTO - 10;

    // LOGICA DE ATAQUES
    private boolean atacarArriba = false;
    private boolean atacarAbajo = false;
    private boolean estaAtacando = false;
    private int blinkTimer = 0;
    //Animaciones de ataque
    private Animacion ataqueNormalAnim;
    private Animacion ataqueArribaAnim;
    private Animacion ataqueAbajoAnim;
    private Animacion ataqueActualAnim = null;
    // Parámetros para dibujar el ataque
    private final double ESCALA_ATAQUE = 0.4;
    private final int OFFSET_DIBUJO_ATAQUE_DERECHA_X = +20;
    private final int OFFSET_DIBUJO_ATAQUE_IZQUIERDA_X = -150;
    private final int OFFSET_DIBUJO_ATAQUE_Y = +5;

    //OFFSETS DE CURACION
    private int curacionOffsetX = 0;
    private int curacionOffsetY = -4;

    //ANIMACIONES DE MOVIMIENTO Y DAÑO
    private Animacion idleAnim;
    private Animacion caminarAnim;
    private Animacion saltoAnim;
    private Animacion danoAnim;
    private Animacion muerteAnim;
    public Animacion renacerAnim;

    // CONSTRUCTOR
    public Jugador() {
        x = 50;
        y = SUELO_Y;
        this.vidas = VIDA_MAXIMA; // MODIFICADO: Inicializa vidas a VIDA_MAXIMA (5)
        this.nivel = 1; // Inicializa el nivel
        this.experiencia = 0; // Inicializa la experiencia
        try {
            cargarAnimaciones();
            cargarHUDSprites();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reiniciar() {
        estado = estado.RENACIENDO;
        this.x = 100;
        this.y = SUELO_Y;
        this.vidas = VIDA_MAXIMA; // Reinicia a la vida máxima (5)
        this.fasesLunares = 1;
        estaAtacando = false;
        cargandoCuracion = false;
        enSuelo = true;
        mirandoDerecha = true;
        velocidadX = 0;
        velocidadY = 0;
        retrocesoX = 0;
        animandoPerderVida = false;
        animandoRecuperarVida = false;
        blinkTimer = 0;
        timerInvulnerable = 0;
        // Reiniciar animaciones
        if (muerteAnim != null) {
            muerteAnim.reiniciar();
        }
        if (idleAnim != null) {
            idleAnim.reiniciar();
        }
        if (caminarAnim != null) {
            caminarAnim.reiniciar();
        }
        if (saltoAnim != null) {
            saltoAnim.reiniciar();
        }
        if (ataqueNormalAnim != null) {
            ataqueNormalAnim.reiniciar();
        }
        if (ataqueArribaAnim != null) {
            ataqueArribaAnim.reiniciar();
        }
        if (ataqueAbajoAnim != null) {
            ataqueAbajoAnim.reiniciar();
        }
        if (danoAnim != null) {
            danoAnim.reiniciar();
        }
        if (curacionAnim != null) {
            curacionAnim.reiniciar();
        }
        if (renacerAnim != null) {
            renacerAnim.reiniciar();
        }
        if (gatosIdleAnim != null) {
            gatosIdleAnim.reiniciar();
        }
        if (gatosPerderAnim != null) {
            gatosPerderAnim.reiniciar();
        }
        if (gatosRecuperarAnim != null) {
            gatosRecuperarAnim.reiniciar();
        }
    }

    // CARGA DE ANIMACIONES DEL JUGADOR
    private void cargarAnimaciones() {
        try {
            idleAnim = new Animacion(cargarSprites("idle", 4), 50, true);
            caminarAnim = new Animacion(cargarSprites("walk", 9), 8, true);
            saltoAnim = new Animacion(cargarSprites("jump", 12), 10, false);
            ataqueNormalAnim = new Animacion(cargarSprites("attack", 7), 4, false);
            ataqueArribaAnim = new Animacion(cargarSprites("Ataquearriba", 5), 4, false);
            ataqueAbajoAnim = new Animacion(cargarSprites("ataqueabajo", 4), 4, false);
            danoAnim = new Animacion(cargarSprites("Dano", 21), 3, false);
            muerteAnim = new Animacion(cargarSprites("MuerteRe", 16), 12, false);
            curacionAnim = new Animacion(cargarSprites("CurarsePerso", 11), 9, false);
            renacerAnim = new Animacion(cargarSprites("Renacer", 10), 5, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //CARGAR LOS SPRITES DEL JUGADOR
    private BufferedImage[] cargarSprites(String nombreAnimacion, int cantidad) throws IOException {
        BufferedImage[] sprites = new BufferedImage[cantidad];
        for (int i = 0; i < cantidad; i++) {
            sprites[i] = ImageIO.read(getClass().getResource("/Graficos/Sprites/Jugador/" + nombreAnimacion + "_" + i + ".png"));
        }
        return sprites;
    }

    //CARGAR LOS SPRITES DEL HUD
    private void cargarHUDSprites() throws IOException {
        // FASES LUNARES
        fasesLunaresSprites = new BufferedImage[5]; // Asumo que tienes 5 sprites para fases lunares (0-4)
        for (int i = 0; i < 5; i++) {
            fasesLunaresSprites[i] = ImageIO.read(getClass().getResource("/Graficos/Sprites/Hud/cargaSuper_" + i + ".png"));
        }
        gatosIdleSprites = cargarSpritesHUD("/Graficos/Sprites/Hud/idleVidas_", 2);
        gatosPerderSprites = cargarSpritesHUD("/Graficos/Sprites/Hud/Pierdevidas_", 4);
        gatosRecuperarSprites = cargarSpritesHUD("/Graficos/Sprites/Hud/Recuperavida_", 4);
        gatosIdleAnim = new Animacion(gatosIdleSprites, 30, true);
        gatosPerderAnim = new Animacion(gatosPerderSprites, 15, false);
        gatosRecuperarAnim = new Animacion(gatosRecuperarSprites, 16, false);
    }

    //CARGAR LAS ANIMACIONES DEL HUD
    private BufferedImage[] cargarSpritesHUD(String basePath, int cantidad) throws IOException {
        BufferedImage[] result = new BufferedImage[cantidad];
        for (int i = 0; i < cantidad; i++) {
            result[i] = ImageIO.read(getClass().getResource(basePath + i + ".png"));
        }
        return result;
    }

    // LÓGICA PRINCIPAL DEL JUGADOR (ACTUALIZACIÓN)
    public void actualizar(boolean izquierda, boolean derecha, boolean arriba, boolean abajo, boolean atacar, boolean saltar, int limiteEscenario) {
        long ahoras = System.nanoTime(); // Variable local para el tiempo actual
        // ESTADO: DAÑADO
        if (estado == Estado.DAÑADO) {
            danoAnim.actualizar();
            
            // Aplica gravedad y retroceso
            velocidadY += GRAVEDAD;
            y += velocidadY;
            if (y >= SUELO_Y) {
                y = SUELO_Y;
                velocidadY = 0;
                enSuelo = true;
            }

            if (retrocesoX != 0) {
                x += retrocesoX;
                retrocesoX += (retrocesoX > 0) ? -5 : 5;
                if (Math.abs(retrocesoX) < 5) {
                    retrocesoX = 0;
                }
            }

            // Cuando la animación de daño termina, cambiar a otro estado
            if (danoAnim.estaTerminada()) {
                estado = IDLEorWalk(false, false);
            }
            return;
        }

        // REGISTRO DE ATAQUES VERTICALES
        atacarArriba = arriba && atacar;
        atacarAbajo = abajo && atacar;

        // SALTO Y ATERRIZAJE
        if (estado == Estado.SALTANDO) {
            saltoAnim.actualizar();

            if (velocidadY > 0 && enSuelo) {
                estado = Estado.ATERRIZANDO;
                saltoAnim.reiniciar();
            }
        } else if (estado == Estado.ATERRIZANDO) {
            saltoAnim.actualizar();
            if (saltoAnim.estaTerminada()) {
                estado = IDLEorWalk(izquierda, derecha);
            }
        }

        // ATAQUE NUEVO
        if (atacar && !estaAtacando) {
            if(puedeAtacar && ahoras - tiempoUltimoAtaque >= COOLDOWN_ATAQUE){
                if (arriba) {
                    estaAtacando = true;
                    GestorAudio.reproducirEfecto("ataque");
                    ataqueActualAnim = ataqueArribaAnim;

                } else if (abajo && !enSuelo) {
                    estaAtacando = true;
                    GestorAudio.reproducirEfecto("ataque");
                    ataqueActualAnim = ataqueAbajoAnim;

                } else {
                    estaAtacando = true;
                    GestorAudio.reproducirEfecto("ataque");
                    ataqueActualAnim = ataqueNormalAnim;

                }
                tiempoUltimoAtaque = ahoras; // Corregido: usar 'ahoras'
                puedeAtacar = false;
                ataqueActualAnim.reiniciar();
                
            }
        }
        if (!puedeAtacar && ahoras - tiempoUltimoAtaque >= COOLDOWN_ATAQUE) {
            puedeAtacar = true;
        }
        // MOVIMIENTO HORIZONTAL
        velocidadX = 0;
        if (izquierda) {
            velocidadX = -VELOCIDAD_MOVIMIENTO;
            mirandoDerecha = false;

        } else if (derecha) {
            velocidadX = VELOCIDAD_MOVIMIENTO;

            mirandoDerecha = true;
        }
        x += velocidadX;
        x = Math.max(0, Math.min(x, limiteEscenario - ANCHO));
        // int limite = PanelJuego.ANCHO; // Esta variable local no es necesaria aquí

        // SALTO
        if (saltar && enSuelo) {
            velocidadY = FUERZA_SALTO;
            enSuelo = false;
            estado = Estado.SALTANDO;
            saltoAnim.reiniciar();
        }

        // GRAVEDAD
        velocidadY += GRAVEDAD;
        y += velocidadY;
        if (y >= SUELO_Y) {
            y = SUELO_Y;
            velocidadY = 0;
            enSuelo = true;
            if (!estaAtacando && estado != Estado.ATERRIZANDO) {
                estado = IDLEorWalk(izquierda, derecha);
            }
        }

        // AJUSTE ESTADO IDLE/CAMINANDO
        if (!estaAtacando && estado != Estado.SALTANDO && estado != Estado.ATERRIZANDO) {
            estado = IDLEorWalk(izquierda, derecha);
        }

        // ANIMACIONES POR ESTADO
        switch (estado) {
            case IDLE ->
                idleAnim.actualizar();
            case CAMINANDO_DERECHA, CAMINANDO_IZQUIERDA ->
                caminarAnim.actualizar();

            case SALTANDO ->
                saltoAnim.actualizar();
            case RENACIENDO -> {
                renacerAnim.actualizar();
                if (renacerAnim.estaTerminada()) {
                    estado = Estado.IDLE;
                }
            }
        } // ANIMACIÓN DE ATAQUE
        if (estaAtacando && ataqueActualAnim != null) {
            ataqueActualAnim.actualizar();
            if (ataqueActualAnim.estaTerminada()) {
                estaAtacando = false;
                ataqueActualAnim = null;
                estado = enSuelo ? IDLEorWalk(izquierda, derecha) : Estado.SALTANDO;
            }
        }

        // RETROCESO TRAS DAÑO
        if (retrocesoX != 0) {
            x += retrocesoX;
            retrocesoX += (retrocesoX > 0) ? -5 : 5;
            if (Math.abs(retrocesoX) < 5) {
                retrocesoX = 0;
            }
        }

        // INVULNERABILIDAD
        if (invulnerable && estado != Estado.DAÑADO) {
            timerInvulnerable--;
            if (timerInvulnerable <= 0) {
                invulnerable = false;
            } else {
                blinkTimer++;
            }
        }

        //ANIMACIONES DEL HUD
        if (animandoPerderVida) {
            gatosPerderAnim.actualizar();
            if (gatosPerderAnim.estaTerminada()) {
                animandoPerderVida = false;
                // La vida se reduce en recibirDaño() o perderVida(), no aquí.
                // Esta parte solo maneja la animación.
            }
        }

        if (animandoRecuperarVida) {
            gatosRecuperarAnim.actualizar();
            if (gatosRecuperarAnim.estaTerminada()) {
                animandoRecuperarVida = false;
                // La vida se incrementa en ganarVida(), no aquí.
                // Esta parte solo maneja la animación.
            }
        }

        gatosIdleAnim.actualizar();

        //MUERTE
        if (vidas <= 0 && estado != Estado.MUERTO) {
            estado = Estado.MUERTO;
            muerteAnim.actualizar();
        }

        // Lógica del sonido de pasos con verificación de null
        estaCaminando = (velocidadX != 0) && enSuelo && !estaAtacando;

        if (GestorAudio.paso != null) { // ¡Protección contra NullPointerException!
            if (estaCaminando) {
                long ahoraMillis = System.currentTimeMillis(); // Usar System.currentTimeMillis() para intervalos de tiempo más largos
                if (ahoraMillis - ultimoPasoTiempo > intervaloPaso) {
                    GestorAudio.paso.reproducir();
                    ultimoPasoTiempo = ahoraMillis;
                }
            } else {
                // MODIFICADO: Usar el método 'estaReproduciendo()' que ahora existe
                if (GestorAudio.paso.estaReproduciendo()) { 
                    GestorAudio.paso.parar();
                }
            }
        }
    }

    //ESTADOS DE MOVIMIENTO
    private Estado IDLEorWalk(boolean izquierda, boolean derecha) {
        if (izquierda) {
            return Estado.CAMINANDO_IZQUIERDA;
        }
        if (derecha) {
            return Estado.CAMINANDO_DERECHA;
        }
        return Estado.IDLE;
    }

    // RECIBIR DAÑO
    public void recibirDaño(int cantidad, int direccionEmpuje) {
        if (invulnerable || estado == Estado.DAÑADO || estado == estado.MUERTO) {
            return;
        }
        GestorAudio.reproducirEfecto("daño");
        this.cantidad = cantidad;
        perderVida(); // Llama a perderVida para reducir la vida y activar animación

        estado = Estado.DAÑADO;
        danoAnim.reiniciar();

        invulnerable = true;
        timerInvulnerable = DURACION_INVULNERABLE_DEFAULT;
        retrocesoX = direccionEmpuje;
    }

    //PERDER VIDAS
    public void perderVida() {
        // La vida se reduce aquí, antes de la animación
        vidas = Math.max(0, vidas - cantidad); // Asegura que la vida no baje de 0
        animandoPerderVida = true;
        gatosPerderAnim.reiniciar();
        System.out.println("Vidas restantes: " + vidas);
    }

    //GANAS FASES LUNARES
    public void ganarFaseLunar() {
        fasesLunares++;
        if (fasesLunares > FASES_LUNARES_MAX) {
            fasesLunares = FASES_LUNARES_MAX;
        }
        System.out.println("Fase Lunar actual: " + fasesLunares);
    }

    //CURARSE
    public boolean Curarse() {
        if (fasesLunares >= 3 && vidas < VIDA_MAXIMA) {
            GestorAudio.reproducirEfecto("curarse");
            fasesLunares = Math.max(1, fasesLunares - 2);  // Nunca menor a 1
            ganarVida(); // Llama a ganarVida para incrementar la vida y activar animación
            System.out.println("Fases lunares tras curar: " + fasesLunares);
            return true;
        } else if (vidas >= VIDA_MAXIMA) {
            System.out.println("Vida llena: no puedes curarte más.");
            return false;
        } else {
            System.out.println("No tienes suficientes fases lunares para curarte.");
            return false;
        }
    }

    //ANIMACION GANAR VIDA
    public void ganarVida() {
        // La vida se incrementa aquí, antes de la animación
        if (vidas < VIDA_MAXIMA) { // Solo ganar vida si no está al máximo
            vidas++;
            animandoRecuperarVida = true;
            gatosRecuperarAnim.reiniciar();
        }
    }

    public void comenzarCuracion() {
        if (!cargandoCuracion && fasesLunares >= 3 && vidas < VIDA_MAXIMA && estado == Estado.IDLE) {
            cargandoCuracion = true;
            Teclado.bloquear(); // Ahora Teclado.bloquear() existe y funciona
            Mouse.resetear();
            curacionAnim.reiniciar();
        }
    }

    public void cancelarCuracion() {
        if (cargandoCuracion) {
            cargandoCuracion = false;
            curacionAnim.reiniciar();
            Teclado.desbloquear(); // MODIFICADO: Ahora Teclado.desbloquear() existe
            // Mouse.resetear(); // No es necesario resetear el mouse al cancelar curación
        }
    }

    public void actualizarCuracion() {
        if (cargandoCuracion) {
            curacionAnim.actualizar();
            Teclado.bloquear(); // Ahora Teclado.bloquear() existe y funciona
            Mouse.resetear();
            if (curacionAnim.estaTerminada()) {
                Curarse(); // ahora sí cura
                cargandoCuracion = false;
                Teclado.desbloquear(); // MODIFICADO: Ahora Teclado.desbloquear() existe
            }
        }
    }

    //RENACER
    public void renacer() {
        estado = Estado.RENACIENDO;
        if (renacerAnim != null) {
            renacerAnim.reiniciar();
            System.out.println("Jugador renaciendo");
        }
    }

    // COLISIONES Y HITBOX
    public Rectangle getRect() {
        return new Rectangle(x + OFFSET_HITBOX_X, y + OFFSET_HITBOX_Y, HITBOX_ANCHO, HITBOX_ALTO);
    }

    public void moverX(int dx) {
        x += dx;
    }

    // ATAQUES HITBOX
    public void generarHitboxAtaque(List<AtaqueHitbox> listaHitboxes) {
        if (estaAtacando && ataqueActualAnim != null) {
            int frameImpacto = getFrameImpactoParaAnimacion(ataqueActualAnim);
            if (ataqueActualAnim.getFrameActualIndex() == frameImpacto) {
                Rectangle hitbox = null;
                if (ataqueActualAnim == ataqueNormalAnim) {
                    int offsetX = mirandoDerecha ? ANCHO - 40 : -115;
                    hitbox = new Rectangle(x + offsetX, y + 10, 150, 120);
                } else if (ataqueActualAnim == ataqueArribaAnim) {
                    int offsetX = +5;
                    int offsetY = -90;
                    hitbox = new Rectangle(x + offsetX, y + offsetY, 100, 120);
                } else if (ataqueActualAnim == ataqueAbajoAnim) {
                    int offsetX = +5;
                    int offsetY = ALTO;
                    hitbox = new Rectangle(x + offsetX, y + offsetY, 100, 120);
                }
                if (hitbox != null) {
                    listaHitboxes.add(new AtaqueHitbox(hitbox, 10));
                }
            }
        }
    }

    private int getFrameImpactoParaAnimacion(Animacion anim) {
        if (anim == ataqueNormalAnim) {
            return 1;
        }
        if (anim == ataqueArribaAnim) {
            return 1;
        }
        if (anim == ataqueAbajoAnim) {
            return 1;
        }
        return 3; // Valor por defecto si no es ninguna de las anteriores
    }

    // DIBUJAR TODO DEL JUGADOR
    public void dibujar(Graphics g, int camaraX) {
        Graphics2D g2 = (Graphics2D) g;
        BufferedImage frameBase = null;
        int anchoDibujo = ANCHO;
        int altoDibujo = ALTO;
        int offsetDibujoX = 0;
        int offsetDibujoY = 0;

        // Selector de frames
        if (estado == estado.RENACIENDO) {
            BufferedImage frameRenacer = renacerAnim.getFrameActual();
            int drawX = x - camaraX + curacionOffsetX;
            int drawY = y + curacionOffsetY;
            if (!mirandoDerecha) {
                drawX += ANCHO;
                g2.drawImage(frameRenacer, drawX, drawY, -ANCHO, ALTO, null);
            } else {
                g2.drawImage(frameRenacer, drawX, drawY, ANCHO, ALTO, null);
            }
            dibujarHUD(g2); // Asegúrate de dibujar el HUD también durante la animación de renacer
            return;
        }
        if (cargandoCuracion) {
            BufferedImage frameCuracion = curacionAnim.getFrameActual();
            int drawX = x - camaraX + curacionOffsetX;
            int drawY = y + curacionOffsetY;
            dibujarHUD(g2); // Dibuja el HUD mientras se cura

            if (!mirandoDerecha) {
                drawX += ANCHO;
                g2.drawImage(frameCuracion, drawX, drawY + 5, -ANCHO, ALTO, null);
            } else {
                g2.drawImage(frameCuracion, drawX, drawY + 5, ANCHO, ALTO, null);
            }
            return;
        }

        if (estado == Estado.DAÑADO) {
            frameBase = danoAnim.getFrameActual();
        } else if (estaAtacando && ataqueActualAnim != null) {
            frameBase = ataqueActualAnim.getFrameActual();
            anchoDibujo = (int) (frameBase.getWidth() * ESCALA_ATAQUE);
            altoDibujo = (int) (frameBase.getHeight() * ESCALA_ATAQUE);
            if (ataqueActualAnim == ataqueAbajoAnim) {
                offsetDibujoY = +5;
            } else if (ataqueActualAnim == ataqueArribaAnim) {
                offsetDibujoY = -70;
            } else {
                offsetDibujoX = mirandoDerecha ? OFFSET_DIBUJO_ATAQUE_DERECHA_X : OFFSET_DIBUJO_ATAQUE_IZQUIERDA_X;
                offsetDibujoY = OFFSET_DIBUJO_ATAQUE_Y;
            }
        } else {
            frameBase = switch (estado) {
                case IDLE -> idleAnim.getFrameActual();
                case CAMINANDO_DERECHA, CAMINANDO_IZQUIERDA -> caminarAnim.getFrameActual();
                case SALTANDO, ATERRIZANDO -> saltoAnim.getFrameActual();
                case MUERTO -> muerteAnim.getFrameActual();
                // case RENACIENDO -> renacerAnim.getFrameActual(); // Ya se maneja al principio del método
                default -> null; // Devolver null si el estado no es manejado aquí
            };
        }

        if (frameBase != null) {
            //Control de transparencia
            if (invulnerable) {
                if ((blinkTimer / 5) % 2 == 0) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f)); // más transparente
                } else {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // normal
                }
            }
            int drawX = x - camaraX + offsetDibujoX;
            int drawY = y + offsetDibujoY;
            if (!mirandoDerecha) {
                drawX += anchoDibujo;
                g2.drawImage(frameBase, drawX, drawY, -anchoDibujo, altoDibujo, null);
            } else {
                g2.drawImage(frameBase, drawX, drawY, anchoDibujo, altoDibujo, null);
            }
            // Restaurar opacidad normal
            if (invulnerable) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        }
        //dibujar el HUD (si no se dibujó ya en curacion/renacer)
        if (!cargandoCuracion && estado != Estado.RENACIENDO) {
            dibujarHUD(g2);
        }
    }//FIN DIBUJAR JUGADOR

    //Dibujar HUD
    private void dibujarHUD(Graphics2D g2) {
        // Fase Lunar (la "redonda")
        // Asegúrate de que fasesLunares esté entre 1 y FASES_LUNARES_MAX (6)
        // El índice del array es 0-4 para 5 sprites
        BufferedImage lunaSprite = fasesLunaresSprites[Math.max(0, Math.min(fasesLunares - 1, fasesLunaresSprites.length - 1))];
        g2.drawImage(lunaSprite, 0, 0, 180, 180, null); // Dibuja en (0,0) con tamaño 180x180

        // Dibujar vidas (los "gatitos")
        // Asumiendo que cada "vida" es un gatito. `vidas` es 5.
        // El bucle dibujará 5 gatitos.
        for (int i = 0; i < vidas; i++) { // Bucle hasta el número actual de vidas
            int xOffset = 140 + i * 75; // Posición X para cada gatito
            BufferedImage frame;
            // Lógica de animación para cada gatito
            // Si se está perdiendo vida y este gatito es el que está desapareciendo
            if (animandoPerderVida && i == vidas) { // Si i es igual a la vida actual, es el que se está "perdiendo"
                frame = gatosPerderAnim.getFrameActual();
            } else if (animandoRecuperarVida && i == vidas -1) { // Si se está recuperando vida y este gatito es el "nuevo"
                frame = gatosRecuperarAnim.getFrameActual();
            } else {
                frame = gatosIdleAnim.getFrameActual(); // Gatito normal
            }
            g2.drawImage(frame, xOffset, 20, 90, 90, null); // Dibuja el gatito
        }
    }//FIN DIBUJAR HUD

    //GETTERS y SETTERS
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) { // Añadido setY para cargar desde la DB
        this.y = y;
    }

    public int getAncho() {
        return ANCHO;
    }

    public int getAlto() {
        return ALTO;
    }

    public int getVida() { // Este getter devuelve el valor de 'vidas'
        return vidas;
    }

    public void setVida(int vidas) { // Este setter actualiza el valor de 'vidas'
        this.vidas = vidas;
    }

    public int getNivel() { // Getter para el nivel
        return nivel;
    }

    public void setNivel(int nivel) { // Setter para el nivel
        this.nivel = nivel;
    }

    public int getExperience() { // Getter para la experiencia
        return experiencia;
    }

    public void setExperience(int experiencia) { // Setter para la experiencia
        this.experiencia = experiencia;
    }

    public int getFasesLunares() { // Getter para las fases lunares
        return fasesLunares;
    }

    public void setFasesLunares(int fasesLunares) { // Setter para las fases lunares
        this.fasesLunares = fasesLunares;
    }

    public boolean estaAtacando() {
        return estaAtacando;
    }

    public boolean esInvulnerable() {
        return invulnerable;
    }

    public void resetearPosicion() {
        x = 50;
    }

    public boolean animacionMuerteTerminada() {
        return muerteAnim.estaTerminada();
    }

    public boolean estaCargandoCuracion() {
        return cargandoCuracion;
    }

    public boolean estaMuerto() {
        if (estado == Estado.MUERTO) {
            invulnerable = true;
        }
        return estado == Estado.MUERTO;
    }

    public void limitarMovimiento(int xMin, int xMax) {
        if (x < xMin) {
            x = xMin;
        }
        if (x + ANCHO > xMax) {
            x = xMax - ANCHO;
        }
    }
}
