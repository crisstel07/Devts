package Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import Sonido.Sonido;

public class Jugador {

    // ============================================================
    // 🟣 1. POSICIÓN Y MOVIMIENTO BÁSICO
    // ============================================================
    private int x, y;
    private int velocidadX = 0;
    private int velocidadY = 0;

    private final int ANCHO = 120;
    private final int ALTO = 130;

    // ============================================================
    // 🟣 2. ESTADO DEL JUGADOR
    // ============================================================
    private enum Estado {
        IDLE,
        CAMINANDO_DERECHA,
        CAMINANDO_IZQUIERDA,
        SALTANDO,
        ATERRIZANDO,
        DAÑADO,
        MUERTO,
        RENACIENDO
    }

    private Estado estado = Estado.IDLE;

    private boolean mirandoDerecha = true;

    // ============================================================
    // 🟣 3. VIDA Y DAÑO
    // ============================================================
    private int vida = 5;
    private boolean invulnerable = false;
    private int timerInvulnerable = 0;

    private int fasesLunares = 1;  // empieza en vacía
    private final int FASES_LUNARES_MAX = 5;

    private boolean cargandoCuracion = false;
    private Animacion curacionAnim;

    // ================================
// 3.5 SPRITES HUD
// ================================
    private BufferedImage[] fasesLunaresSprites;
    private BufferedImage[] gatosIdleSprites;
    private BufferedImage[] gatosPerderSprites;
    private BufferedImage[] gatosRecuperarSprites;

// Estados
    private int vidas = 5;

    private Animacion gatosIdleAnim;
    private Animacion gatosPerderAnim;
    private Animacion gatosRecuperarAnim;
    private boolean animandoPerderVida = false;
    private boolean animandoRecuperarVida = false;

    // Se define automáticamente con la animación de daño
    private final int DURACION_INVULNERABLE_DEFAULT = 120;

    private int retrocesoX = 0;

    // ============================================================
    // 🟣 4. FÍSICA Y GRAVEDAD
    // ============================================================
    private final int VELOCIDAD_MOVIMIENTO = 7;
    private final int FUERZA_SALTO = -20;
    private final double GRAVEDAD = 1;

    private final int SUELO_Y = PanelJuego.ALTO - 150 - ALTO;
    private boolean enSuelo = false;

    // ============================================================
    // 🟣 5. HITBOX DE CUERPO
    // ============================================================
    private final int OFFSET_HITBOX_X = 25;
    private final int OFFSET_HITBOX_Y = 10;
    private final int HITBOX_ANCHO = ANCHO - 60;
    private final int HITBOX_ALTO = ALTO - 10;

    // ============================================================
    // 🟣 6. ATAQUES Y ANIMACIONES DE ATAQUE
    // ============================================================
    private boolean atacarArriba = false;
    private boolean atacarAbajo = false;
    private boolean estaAtacando = false;
    private int blinkTimer = 0;

    private Animacion ataqueNormalAnim;
    private Animacion ataqueArribaAnim;
    private Animacion ataqueAbajoAnim;
    private Animacion ataqueActualAnim = null;

    // Parámetros para dibujar el ataque
    private final double ESCALA_ATAQUE = 0.4;
    private final int OFFSET_DIBUJO_ATAQUE_DERECHA_X = +20;
    private final int OFFSET_DIBUJO_ATAQUE_IZQUIERDA_X = -150;
    private final int OFFSET_DIBUJO_ATAQUE_Y = +5;

    //Offsets de curación
    private int curacionAncho = ANCHO;
    private int curacionAlto = ALTO;
    private int curacionOffsetX = 0;
    private int curacionOffsetY = -4;
    // ============================================================
    // 🟣 7. ANIMACIONES DE MOVIMIENTO Y DAÑO
    // ============================================================
    private Animacion idleAnim;
    private Animacion caminarAnim;
    private Animacion saltoAnim;
    private Animacion danoAnim;
    private Animacion muerteAnim;
    private Animacion renacerAnim;

    // ============================================================
// 🟣 7.5 FASES LUNARES
// ============================================================
    private final int VIDA_MAXIMA = 5;

    // ============================================================
    // 🟣 8. CONSTRUCTOR
    // ============================================================
    public Jugador() {
        x = 50;
        y = SUELO_Y;
        cargarAnimaciones();
        try {
            cargarHUDSprites();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    // 🟣 9. CARGA DE ANIMACIONES
    // ============================================================
    private void cargarAnimaciones() {
        try {
            idleAnim = new Animacion(cargarSprites("idle", 4), 50, true);
            caminarAnim = new Animacion(cargarSprites("walk", 9), 8, true);
            saltoAnim = new Animacion(cargarSprites("jump", 12), 10, false);

            ataqueNormalAnim = new Animacion(cargarSprites("attack", 7), 4, false);
            ataqueArribaAnim = new Animacion(cargarSprites("Ataquearriba", 5), 4, false);
            ataqueAbajoAnim = new Animacion(cargarSprites("ataqueabajo", 4), 4, false);
            danoAnim = new Animacion(cargarSprites("Dano", 21), 3, false);
            muerteAnim = new Animacion(cargarSprites("MuerteRe", 16), 6, false);
            curacionAnim = new Animacion(cargarSprites("CurarsePerso", 11), 6, false);
            renacerAnim = new Animacion(cargarSprites("Renacer", 10), 5, false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage[] cargarSprites(String nombreAnimacion, int cantidad) throws IOException {
        BufferedImage[] sprites = new BufferedImage[cantidad];
        for (int i = 0; i < cantidad; i++) {
            sprites[i] = ImageIO.read(getClass().getResource("/Graficos/Sprites/Jugador/" + nombreAnimacion + "_" + i + ".png"));
        }
        return sprites;
    }

    private void cargarHUDSprites() throws IOException {
        // FASES LUNARES
        fasesLunaresSprites = new BufferedImage[5];
        for (int i = 0; i < 5; i++) {
            fasesLunaresSprites[i] = ImageIO.read(getClass().getResource("/Graficos/Sprites/Hud/cargaSuper_" + i + ".png"));
        }

        // GATOS - animaciones
        gatosIdleSprites = cargarSpritesHUD("/Graficos/Sprites/Hud/idleVidas_", 2);
        gatosPerderSprites = cargarSpritesHUD("/Graficos/Sprites/Hud/Pierdevidas_", 4);
        gatosRecuperarSprites = cargarSpritesHUD("/Graficos/Sprites/Hud/Recuperavida_", 4);

        gatosIdleAnim = new Animacion(gatosIdleSprites, 25, true);
        gatosPerderAnim = new Animacion(gatosPerderSprites, 10, false);
        gatosRecuperarAnim = new Animacion(gatosRecuperarSprites, 10, false);
    }

    private BufferedImage[] cargarSpritesHUD(String basePath, int cantidad) throws IOException {
        BufferedImage[] result = new BufferedImage[cantidad];
        for (int i = 0; i < cantidad; i++) {
            result[i] = ImageIO.read(getClass().getResource(basePath + i + ".png"));
        }
        return result;
    }

    // ============================================================
    // 🟣 10. LÓGICA PRINCIPAL (ACTUALIZACIÓN)
    // ============================================================
    /**
     * Actualiza la lógica del jugador
     */
    public void actualizar(boolean izquierda, boolean derecha, boolean arriba, boolean abajo, boolean atacar, boolean saltar, int limiteEscenario) {

        if (estado == Estado.MUERTO) {
            muerteAnim.actualizar();
            return;
        }

        // ---------------------------
        // ESTADO: DAÑADO
        // ---------------------------
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
        // ---------------------------
        // REGISTRO DE ATAQUES VERTICALES
        // ---------------------------
        atacarArriba = arriba && atacar;
        atacarAbajo = abajo && atacar;

        // ---------------------------
        // SALTO Y ATERRIZAJE
        // ---------------------------
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

        // ---------------------------
        // ATAQUE NUEVO
        // ---------------------------
        if (atacar && !estaAtacando) {
            if (arriba) {
                estaAtacando = true;
                ataqueActualAnim = ataqueArribaAnim;
            } else if (abajo && !enSuelo) {
                estaAtacando = true;
                ataqueActualAnim = ataqueAbajoAnim;
            } else {
                estaAtacando = true;
                ataqueActualAnim = ataqueNormalAnim;
            }
            ataqueActualAnim.reiniciar();
        }

        // ---------------------------
        // MOVIMIENTO HORIZONTAL
        // ---------------------------
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

        // ---------------------------
        // SALTO
        // ---------------------------
        if (saltar && enSuelo) {
            velocidadY = FUERZA_SALTO;
            enSuelo = false;
            estado = Estado.SALTANDO;
            saltoAnim.reiniciar();
        }

        // ---------------------------
        // GRAVEDAD
        // ---------------------------
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

        // ---------------------------
        // AJUSTE ESTADO IDLE/CAMINANDO
        // ---------------------------
        if (!estaAtacando && estado != Estado.SALTANDO && estado != Estado.ATERRIZANDO) {
            estado = IDLEorWalk(izquierda, derecha);
        }

        // ---------------------------
        // ANIMACIONES POR ESTADO
        // ---------------------------
        switch (estado) {
            case IDLE ->
                idleAnim.actualizar();
            case CAMINANDO_DERECHA, CAMINANDO_IZQUIERDA ->
                caminarAnim.actualizar();
            case SALTANDO ->
                saltoAnim.actualizar();
        }

        // ---------------------------
        // ANIMACIÓN DE ATAQUE
        // ---------------------------
        if (estaAtacando && ataqueActualAnim != null) {
            ataqueActualAnim.actualizar();
            if (ataqueActualAnim.estaTerminada()) {
                estaAtacando = false;
                ataqueActualAnim = null;
                estado = enSuelo ? IDLEorWalk(izquierda, derecha) : Estado.SALTANDO;
            }
        }

        // ---------------------------
        // RETROCESO
        // ---------------------------
        if (retrocesoX != 0) {
            x += retrocesoX;
            retrocesoX += (retrocesoX > 0) ? -5 : 5;
            if (Math.abs(retrocesoX) < 5) {
                retrocesoX = 0;
            }
        }

        // ---------------------------
        // INVULNERABILIDAD
        // ---------------------------
        if (invulnerable && estado != Estado.DAÑADO) {
            timerInvulnerable--;
            if (timerInvulnerable <= 0) {
                invulnerable = false;
            } else {
                blinkTimer++;
            }
        }

        //Animaciones del HUD
        if (animandoPerderVida) {
            gatosPerderAnim.actualizar();
            if (gatosPerderAnim.estaTerminada()) {
                animandoPerderVida = false;
                if (vidas > 0) {
                    vidas--;  // REDUCE VIDA AQUÍ AL TERMINAR ANIMACIÓN
                }
            }
        }

        if (animandoRecuperarVida) {
            gatosRecuperarAnim.actualizar();
            if (gatosRecuperarAnim.estaTerminada()) {
                animandoRecuperarVida = false;
                if (vidas < VIDA_MAXIMA) {
                    vidas++;
                }
            }
        }

        gatosIdleAnim.actualizar();

        if (vidas <= 0 && estado != Estado.MUERTO) {
    estado = Estado.MUERTO;
    muerteAnim.reiniciar();
}

        
    }

    private Estado IDLEorWalk(boolean izquierda, boolean derecha) {
        if (izquierda) {
            return Estado.CAMINANDO_IZQUIERDA;
        }
        if (derecha) {
            return Estado.CAMINANDO_DERECHA;
        }
        return Estado.IDLE;
    }

    // ============================================================
    // 🟣 11. RECIBIR DAÑO
    // ============================================================
    public void recibirDaño(int cantidad, int direccionEmpuje) {
        if (invulnerable || estado == Estado.DAÑADO) {
            return;
        }

        perderVida(); // Asegúrate de llamar esto para descontar la vida y verificar muerte

        estado = Estado.DAÑADO;
        danoAnim.reiniciar();

        invulnerable = true;
        timerInvulnerable = DURACION_INVULNERABLE_DEFAULT;
        retrocesoX = direccionEmpuje;
    }

    //GANAS PARA FASES LUNARES
    public void ganarFaseLunar() {
        fasesLunares++;
        if (fasesLunares > FASES_LUNARES_MAX) {
            fasesLunares = FASES_LUNARES_MAX;
        }
        System.out.println("Fase Lunar actual: " + fasesLunares);
    }

    public boolean Curarse() {
        if (fasesLunares >= 3 && vidas < VIDA_MAXIMA) {
            fasesLunares = Math.max(1, fasesLunares - 2);  // Nunca menor a 1
            ganarVida();
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

    public boolean animacionMuerteTerminada() {
        return muerteAnim.estaTerminada();
    }

    public void comenzarCuracion() {
    if (!cargandoCuracion && fasesLunares >= 3 && vidas < VIDA_MAXIMA && estado == Estado.IDLE) {
        cargandoCuracion = true;
        curacionAnim.reiniciar();
    }
}


    public void cancelarCuracion() {
        if (cargandoCuracion) {
            cargandoCuracion = false;
            curacionAnim.reiniciar();
        }
    }

    public void actualizarCuracion() {
        if (cargandoCuracion) {
            curacionAnim.actualizar();
            if (curacionAnim.estaTerminada()) {
                Curarse(); // ahora sí cura
                cargandoCuracion = false;
            }
        }
    }

    public boolean estaCargandoCuracion() {
        return cargandoCuracion;
    }

    public void renacer() {
        if (estado == Estado.MUERTO) {
            estado = Estado.RENACIENDO;
            vida = VIDA_MAXIMA;
            vidas = VIDA_MAXIMA;
            fasesLunares = 1;
            x = 50;
            y = SUELO_Y;
            invulnerable = false;
            retrocesoX = 0;
            renacerAnim.reiniciar();
            // Aquí puedes reiniciar otras animaciones o estados si quieres
        }
    }

    // ============================================================
    // 🟣 12. COLISIONES Y HITBOX
    // ============================================================
    public Rectangle getRect() {
        return new Rectangle(x + OFFSET_HITBOX_X, y + OFFSET_HITBOX_Y, HITBOX_ANCHO, HITBOX_ALTO);
    }
//Repeler hitboxes

    public void moverX(int dx) {
        x += dx;
    }

    // ============================================================
    // 🟣 13. ATAQUES HITBOX
    // ============================================================
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
        return 3; // valor por defecto
    }

    // ============================================================
    // 🟣 14. DIBUJAR
    // ============================================================
    public void dibujar(Graphics g, int camaraX) {
        Graphics2D g2 = (Graphics2D) g;
        BufferedImage frameBase = null;
        int anchoDibujo = ANCHO;
        int altoDibujo = ALTO;
        int offsetDibujoX = 0;
        int offsetDibujoY = 0;

        // ---------------------------
        // SELECCIÓN DEL FRAME
        // ---------------------------
       if (cargandoCuracion) {
    BufferedImage frameCuracion = curacionAnim.getFrameActual();
    int drawX = x - camaraX + curacionOffsetX;
    int drawY = y + curacionOffsetY;

    if (!mirandoDerecha) {
        drawX += ANCHO;
        g2.drawImage(frameCuracion, drawX, drawY, -ANCHO, ALTO, null);
    } else {
        g2.drawImage(frameCuracion, drawX, drawY, ANCHO, ALTO, null);
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
        case IDLE ->
            idleAnim.getFrameActual();
        case CAMINANDO_DERECHA, CAMINANDO_IZQUIERDA ->
            caminarAnim.getFrameActual();
        case SALTANDO, ATERRIZANDO ->
            saltoAnim.getFrameActual();
        case MUERTO ->
            muerteAnim.getFrameActual();
        default ->
            null;
    };
}


        // ---------------------------
        // DIBUJO FINAL
        // ---------------------------
        if (frameBase != null) {
            // Control de transparencia si invulnerable
            if (invulnerable) {
                // Hacemos parpadeo
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

        dibujarHUD(g2);
    }

    //Dibujar HUD
    private void dibujarHUD(Graphics2D g2) {
        // DIBUJAR FASE LUNAR
        BufferedImage lunaSprite = fasesLunaresSprites[Math.max(0, Math.min(fasesLunares - 1, 4))];
        g2.drawImage(lunaSprite, 0, 0, 180, 180, null);

        // DIBUJAR VIDAS / GATOS
        for (int i = 0; i < vidas; i++) {
            int xOffset = 140 + i * 75;
            BufferedImage frame;

            if (animandoPerderVida && i < vidas) {
                frame = gatosPerderAnim.getFrameActual();
            } else if (animandoRecuperarVida && i < vidas) {
                frame = gatosRecuperarAnim.getFrameActual();
            } else {
                frame = gatosIdleAnim.getFrameActual();
            }
            //Gatos
            g2.drawImage(frame, xOffset, 20, 90, 90, null);
        }
    }

    // ============================================================
    // 🟣 15. GETTERS
    // ============================================================
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getAncho() {
        return ANCHO;
    }

    public int getAlto() {
        return ALTO;
    }

    public int getVida() {
        return vida;
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

    public int getFasesLunares() {
        return fasesLunares;
    }

    public void perderVida() {
        if (vidas > 1) {
            vidas--;
            animandoPerderVida = true;
            gatosPerderAnim.reiniciar();
        } else {
            vidas = 0;
            estado = Estado.MUERTO;
            muerteAnim.reiniciar();
            // Aquí deshabilitas input en tu controlador o en PanelJuego
        }
    }

    public boolean estaMuerto() {
        return estado == Estado.MUERTO;
    }

    public void ganarVida() {
        if (vidas < VIDA_MAXIMA && !animandoRecuperarVida) {
            animandoRecuperarVida = true;
            gatosRecuperarAnim.reiniciar();
        }
    }

}
