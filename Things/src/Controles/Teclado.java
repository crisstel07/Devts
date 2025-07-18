package Controles;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import Sonido.GestorAudio; // Asegúrate de que esta importación esté presente

//Clase que hereda de KeyListener para que pueda "escuchar" al teclado
public class Teclado implements KeyListener {

    public boolean izquierda, derecha, arriba, abajo, saltar, mostrarHitbox;
    public boolean curar = false;
    public boolean atacar = false; // Añadido: Asumo que 'atacar' también se controla por teclado o mouse

    // ¡NUEVO! Variable estática para controlar si la entrada del teclado está bloqueada
    private static boolean bloqueado = false;

    @Override
    public void keyTyped(KeyEvent e) {
        // No se usa para la lógica de movimiento
    }

    // METODO QUE DICE QUE SI UNA TECLA SE QUEDA PRESIONADA HARÁ CIERTA ACCION
    @Override
    public void keyPressed(KeyEvent e) {
        if (bloqueado) { // Si el teclado está bloqueado, ignorar la entrada
            return;
        }

        int codigo = e.getKeyCode();

        switch (codigo) {
            case KeyEvent.VK_A -> izquierda = true;
            case KeyEvent.VK_D -> derecha = true;
            case KeyEvent.VK_W -> arriba = true;
            case KeyEvent.VK_S -> abajo = true;
            case KeyEvent.VK_SPACE -> {
                saltar = true;
                // GestorAudio.reproducirEfecto("salto"); // Si el salto tiene sonido, asegúrate de que se reproduzca aquí
            }
            case KeyEvent.VK_E -> curar = true; // Tecla 'E' para curar
            case KeyEvent.VK_H -> mostrarHitbox = !mostrarHitbox; // Alternar mostrar hitbox con 'H'
            // Puedes añadir una tecla para atacar aquí si es controlada por teclado
            // case KeyEvent.VK_J -> atacar = true; 
        }
    }

    // METODO QUE DICE QUE SI SE SUELTA UNA TECLA HARÁ OTRA FUNCIÓN
    @Override
    public void keyReleased(KeyEvent e) {
        // No necesitamos verificar 'bloqueado' aquí, ya que el estado se reseteará
        // independientemente de si la entrada fue procesada o no.
        int codigo = e.getKeyCode();

        switch (codigo) {
            case KeyEvent.VK_A -> izquierda = false;
            case KeyEvent.VK_D -> derecha = false;
            case KeyEvent.VK_W -> arriba = false;
            case KeyEvent.VK_S -> abajo = false;
            case KeyEvent.VK_SPACE -> saltar = false;
            case KeyEvent.VK_E -> curar = false; // Liberar la tecla 'E'
            // case KeyEvent.VK_J -> atacar = false; // Si tienes tecla de ataque
        }
    }

    public void resetear() {
        izquierda = false;
        derecha = false;
        arriba = false;
        abajo = false;
        saltar = false;
        atacar = false; // Asegúrate de que 'atacar' también se resetea si es necesario
        curar = false;
        // mostrarHitbox no se resetea aquí, ya que es un toggle
    }
    
    // ¡NUEVO MÉTODO! Para bloquear la entrada del teclado
    public static void bloquear() {
        bloqueado = true;
        // Opcional: También puedes resetear todas las teclas al bloquear
        // izquierda = false;
        // derecha = false;
        // ...
    }

    // ¡NUEVO MÉTODO! Para desbloquear la entrada del teclado
    public static void desbloquear() {
        bloqueado = false;
    }
}
