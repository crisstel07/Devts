package Controles;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

//Clase que hereda de KeyListener para que pueda "escuchar" al teclado 
public class Teclado implements KeyListener {

    public boolean izquierda, derecha, arriba, abajo, saltar, mostrarHitbox;

    @Override
    public void keyTyped(KeyEvent e) {
    }

    //METODO QUE DICE QUE SI UNA TECLA SE QUEDA PRESIONADA HARÁ CIERTA ACCION
    @Override
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();

        if (tecla == KeyEvent.VK_SPACE) {
            saltar = true;
        }

        if (tecla == KeyEvent.VK_A) {
            izquierda = true;
        }
        if (tecla == KeyEvent.VK_D) {
            derecha = true;
        }
        if (tecla == KeyEvent.VK_W) {
            arriba = true;
        }
        if (tecla == KeyEvent.VK_S) {
            abajo = true;
        }
        if (tecla == KeyEvent.VK_P) {
            mostrarHitbox = !mostrarHitbox;
        }

    }

    //METODO QUE DICE QUE SI SE SUELTA UNA TECLA HARÁ OTRA FUNCIÓN
    @Override
    public void keyReleased(KeyEvent e) {
        int tecla = e.getKeyCode();

        if (tecla == KeyEvent.VK_SPACE) {
            saltar = false;
        }

        if (tecla == KeyEvent.VK_A) {
            izquierda = false;
        }
        if (tecla == KeyEvent.VK_D) {
            derecha = false;
        }
        if (tecla == KeyEvent.VK_W) {
            arriba = false;
        }
        if (tecla == KeyEvent.VK_S) {
            abajo = false;
        }

    }

}
