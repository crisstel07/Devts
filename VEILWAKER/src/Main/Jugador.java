package Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class Jugador {

    // Posición y movimiento
    private int x, y;
    private int velocidadX = 0;
    private int velocidadY = 0;

    //Vida y daño
    private int vida = 5;

    // Tamaño del sprite en pantalla
    private final int ANCHO = 120;
    private final int ALTO = 130;

    // Físicas
    private final int VELOCIDAD_MOVIMIENTO = 7;  //Velocidad normal  7
    private final int FUERZA_SALTO = -20;               //-20
    private final double GRAVEDAD = 1;                  // 1

    // Suelo
    private final int SUELO_Y = PanelJuego.ALTO - 150 - ALTO;
    private boolean enSuelo = false;

    // Estado del jugador
    private boolean mirandoDerecha = true;
    private boolean atacarArriba = false;
    private boolean atacarAbajo = false;

    private Animacion ataqueNormalAnim;
    private Animacion ataqueArribaAnim;
    private Animacion ataqueAbajoAnim;

    private boolean estaAtacando = false;
    private Animacion ataqueActualAnim = null;
    
    //HItbox
    private final int OFFSET_HITBOX_X = 25;
private final int OFFSET_HITBOX_Y = 10;
private final int HITBOX_ANCHO = ANCHO-60;
private final int HITBOX_ALTO = ALTO-10;

//Retroceso y vulnerabilidad 
private boolean invulnerable = false;
private int timerInvulnerable = 0;
private final int DURACION_INVULNERABLE = 90; // 1 segundo si FPS=60

private int retrocesoX = 0;


 // Tamaño de los sprites de ataque (guardamos tamaño real)
    private int anchoAtaqueNormal;
    private int altoAtaqueNormal;

    // Si quieres, también puedes hacer para ataque arriba y abajo cuando los cargues
    // private int anchoAtaqueArriba;
    // private int altoAtaqueArriba;
    // private int anchoAtaqueAbajo;
    // private int altoAtaqueAbajo;

    // Offsets para dibujar animaciones de ataque (ajusta estos valores según lo que necesites)
  private final int OFFSET_DIBUJO_ATAQUE_DERECHA_X = +20;
private final int OFFSET_DIBUJO_ATAQUE_IZQUIERDA_X = -150; 
    private final int OFFSET_DIBUJO_ATAQUE_Y = +5;
    private final double ESCALA_ATAQUE = 0.4; // controla el tamaño del sprite de ataque
    


    // Enum de estados
    private enum Estado {
        IDLE, CAMINANDO_DERECHA, CAMINANDO_IZQUIERDA, SALTANDO, ATERRIZANDO
    }

    private Estado estado = Estado.IDLE;

    // Animaciones
    private Animacion idleAnim;
    private Animacion caminarAnim;
    private Animacion saltoAnim;
    
        

    // Constructor
    public Jugador() {
        x = 50;
        y = SUELO_Y;
        cargarAnimaciones();
    }

    /**
     * Carga todas las animaciones con su velocidad y frames específicos
     */
    private void cargarAnimaciones() {
        try {
            // Idle (2 frames, más lento)
            idleAnim = new Animacion(cargarSprites("idle", 4), 50);

            // Caminar (7 frames)
            caminarAnim = new Animacion(cargarSprites("walk", 9), 8);

            // Salto (5 frames, lento para verse bien)
            saltoAnim = new Animacion(cargarSprites("jump", 12), 10);

            // Ataque Normal (6 frames, rápido)
            BufferedImage[] spritesAtaqueNormal = cargarSprites("attack", 8);
            ataqueNormalAnim = new Animacion(spritesAtaqueNormal, 7);
            anchoAtaqueNormal = spritesAtaqueNormal[0].getWidth();
            altoAtaqueNormal = spritesAtaqueNormal[0].getHeight();

            // Ataque Arriba (si lo usas)
            // BufferedImage[] spritesAtaqueArriba = cargarSprites("attack_up", 6);
            // ataqueArribaAnim = new Animacion(spritesAtaqueArriba, 10);
            // anchoAtaqueArriba = spritesAtaqueArriba[0].getWidth();
            // altoAtaqueArriba = spritesAtaqueArriba[0].getHeight();

            // Ataque Abajo (si lo usas)
            // BufferedImage[] spritesAtaqueAbajo = cargarSprites("attack_down", 6);
            // ataqueAbajoAnim = new Animacion(spritesAtaqueAbajo, 10);
            // anchoAtaqueAbajo = spritesAtaqueAbajo[0].getWidth();
            // altoAtaqueAbajo = spritesAtaqueAbajo[0].getHeight();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método auxiliar para cargar sprites de una carpeta
     */
    private BufferedImage[] cargarSprites(String nombreAnimacion, int cantidad) throws IOException {
        BufferedImage[] sprites = new BufferedImage[cantidad];
        for (int i = 0; i < cantidad; i++) {
            sprites[i] = ImageIO.read(getClass().getResource("/Graficos/Sprites/Jugador/" + nombreAnimacion + "_" + i + ".png"));

        }
        return sprites;
    }

    /**
     * Actualiza la lógica del jugador
     */
    public void actualizar(boolean izquierda, boolean derecha, boolean arriba, boolean abajo, boolean atacar, boolean saltar, int limiteEscenario) {
        atacarArriba = arriba && atacar;
        atacarAbajo = abajo && atacar;

        if (estado == Estado.SALTANDO) {
            saltoAnim.actualizar();

            // Si ya estamos cayendo (velocidadY positiva)
            if (velocidadY > 0 && enSuelo) {
                // Pasamos a estado ATERRIZANDO
                estado = Estado.ATERRIZANDO;
                saltoAnim.reiniciar();
            }
        } else if (estado == Estado.ATERRIZANDO) {
            saltoAnim.actualizar();

            if (saltoAnim.estaTerminada()) {
                estado = IDLEorWalk(izquierda, derecha);
            }
        }  //------------------------------------------------------- 

        // 
        if (atacar && !estaAtacando) {
            if (arriba) {
                estaAtacando = true;
                ataqueActualAnim = ataqueArribaAnim;
                ataqueActualAnim.reiniciar();
            } else if (abajo && !enSuelo) {
                estaAtacando = true;
                ataqueActualAnim = ataqueAbajoAnim;
                ataqueActualAnim.reiniciar();
            } else {
                estaAtacando = true;
                ataqueActualAnim = ataqueNormalAnim;
                ataqueActualAnim.reiniciar();
            }
        }
        velocidadX = 0;
        if (izquierda) {
            velocidadX = -VELOCIDAD_MOVIMIENTO;
            mirandoDerecha = false;
        } else if (derecha) {
            velocidadX = VELOCIDAD_MOVIMIENTO;
            mirandoDerecha = true;
        }

        x += velocidadX;

        // Limitar posición
        x = Math.max(0, Math.min(x, limiteEscenario - ANCHO));

        // Salto
        if (saltar && enSuelo) {
            velocidadY = FUERZA_SALTO;
            enSuelo = false;
            estado = Estado.SALTANDO;
            saltoAnim.reiniciar();
        }

        // Gravedad
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

        // Después de actualizar posición y gravedad
        if (!estaAtacando && estado != Estado.SALTANDO && estado != Estado.ATERRIZANDO) {
            estado = IDLEorWalk(izquierda, derecha);
        }
        // Actualizar animaciones según estado
        switch (estado) {
            case IDLE ->
                idleAnim.actualizar();
            case CAMINANDO_DERECHA, CAMINANDO_IZQUIERDA ->
                caminarAnim.actualizar();
            case SALTANDO ->
                saltoAnim.actualizar();
        }
        // 6️⃣ Ataque (si está atacando)
        if (estaAtacando && ataqueActualAnim != null) {
            ataqueActualAnim.actualizar();
            if (ataqueActualAnim.estaTerminada()) {
                estaAtacando = false;
                ataqueActualAnim = null;
                // Vuelve al estado correcto
                if (enSuelo) {
                    estado = IDLEorWalk(izquierda, derecha);
                } else {
                    estado = Estado.SALTANDO;
                }
            } else {
                // Mantén estado ATACANDO, pero sigue cayendo/moviéndose
                if (!enSuelo) {
                    estado = Estado.SALTANDO;
                }
            }
        }
        
        // Aplicar retroceso si está activo
if (retrocesoX != 0) {
    x += retrocesoX;

    // Reducir retrocesoX poco a poco para detener el retroceso suavemente
    if (retrocesoX > 0) {
        retrocesoX -= 5;  // Reducir 5 pixeles por frame
        if (retrocesoX < 0) retrocesoX = 0;
    } else {
        retrocesoX += 5;  // Aumentar 5 pixeles (porque es negativo) para acercarse a cero
        if (retrocesoX > 0) retrocesoX = 0;
    }
}
x = Math.max(0, Math.min(x, limiteEscenario - ANCHO));

// Manejar invulnerabilidad
if (invulnerable) {
    timerInvulnerable--;
    if (timerInvulnerable <= 0) {
        invulnerable = false;
    }
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

    public int getX() {
        return x;
    }

    public void resetearPosicion() {
        x = 50;
    }

    public int getAncho() {
        return ANCHO;
    }

    //VIDA Y DAÑO
    public int getVida() {
        return vida;
    }

    public void recibirDaño(int cantidad, int direccionEmpuje) {
        if (invulnerable) return;

    vida -= cantidad;
    System.out.println("Jugador recibió daño. Vida: " + vida);
    if (vida <= 0) {
        System.out.println("Jugador derrotado");
        // Puedes implementar respawn o game over
    }

    // Activar invulnerabilidad
    invulnerable = true;
    timerInvulnerable = DURACION_INVULNERABLE;

    // Aplicar retroceso
    retrocesoX = direccionEmpuje;
    }

    //COLISION DE JUGADOR
    public Rectangle getRect() {
    return new Rectangle(x +OFFSET_HITBOX_X, y+OFFSET_HITBOX_Y, HITBOX_ANCHO, HITBOX_ALTO);
}

public boolean estaAtacando() {
    return estaAtacando;
}

public boolean esInvulnerable() {
    return invulnerable;
}
    
//CONTROL DE HITBOX DE ATAQUE
    public void generarHitboxAtaque(List<AtaqueHitbox> listaHitboxes) {
    if (estaAtacando && ataqueActualAnim != null && ataqueActualAnim.getFrameActual() != null) {
        // Solo la primera vez al iniciar ataque
        if (ataqueActualAnim.getFrameActualIndex() == 3) {
            int offsetX = mirandoDerecha ? ANCHO-40 : -105;
            Rectangle hitbox = new Rectangle(x + offsetX, y + 10, 150,120);
            listaHitboxes.add(new AtaqueHitbox(hitbox, 10)); // 20 frames 
        }
    }
}
    
    //DIBUJA TODO RELACIONADO AL JUGADOR
   public void dibujar(Graphics g, int camaraX) {
    Graphics2D g2 = (Graphics2D) g;

    // Elegir frame a dibujar
    BufferedImage frameBase = null;
    
    int anchoDibujo = ANCHO;
    int altoDibujo = ALTO;
    int offsetDibujoX = 0;
    int offsetDibujoY = 0;

    if (estaAtacando && ataqueActualAnim != null) {
    frameBase = ataqueActualAnim.getFrameActual();

    anchoDibujo = (int)(frameBase.getWidth() * ESCALA_ATAQUE);
    altoDibujo = (int)(frameBase.getHeight() * ESCALA_ATAQUE);

    offsetDibujoX = mirandoDerecha ? OFFSET_DIBUJO_ATAQUE_DERECHA_X : OFFSET_DIBUJO_ATAQUE_IZQUIERDA_X;
    offsetDibujoY = OFFSET_DIBUJO_ATAQUE_Y;
    } else {
        // Animaciones normales: idle, caminar, salto con tamaño fijo
        frameBase = switch (estado) {
            case IDLE -> idleAnim.getFrameActual();
            case CAMINANDO_DERECHA, CAMINANDO_IZQUIERDA -> caminarAnim.getFrameActual();
            case SALTANDO, ATERRIZANDO -> saltoAnim.getFrameActual();
        };
    }

    
    if (frameBase != null) {
    int drawX = x - camaraX + offsetDibujoX;
    int drawY = y + offsetDibujoY;

    if (!mirandoDerecha) {
        // Flip horizontal manteniendo punto de referencia
        g2.drawImage(frameBase,
            drawX + anchoDibujo, drawY,
            -anchoDibujo, altoDibujo,
            null);
    } else {
        g2.drawImage(frameBase,
            drawX, drawY,
            anchoDibujo, altoDibujo,
            null);
    }
}
}

    }

