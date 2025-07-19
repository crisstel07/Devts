package Controles;

import Sonido.GestorAudio;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

//Clase que hereda de KeyListener para que pueda "escuchar" al teclado 
public class Teclado implements KeyListener {

    public static boolean izquierda, derecha, arriba, abajo, saltar, mostrarHitbox;
public boolean curar = false;

    @Override
    public void keyTyped(KeyEvent e) {
    }

    //METODO QUE DICE QUE SI UNA TECLA SE QUEDA PRESIONADA HARÁ CIERTA ACCION
    @Override
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();

        if (tecla == KeyEvent.VK_SPACE) {
            saltar = true;
             GestorAudio.reproducirEfecto("salto");
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
        if (tecla == KeyEvent.VK_E) {
    curar = true;
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
        if (tecla == KeyEvent.VK_E) {
    curar = false;
}


    }
    public void resetear() {
    izquierda = false;
    derecha = false;
    arriba = false;
    abajo = false;
    saltar = false;
    curar = false;
    saltar=false;
}
    
    public static void bloquear(){
          izquierda = false;
    derecha = false;
    arriba = false;
    abajo = false;
    saltar = false;
    saltar=false;
    }


}
