package Sonido;

public class GestorAudio {
    // Música por escenario
    private Musica musicaActual;

    // Efectos pre-cargados
    public static Sonido salto;
    public static Sonido ataque;
    public static Sonido paso;
    public static Sonido daño;
    public static Sonido curarse;
    public static Sonido pegar;
    public static Sonido ataqueF;
    public static Sonido muerteF;
    public static Sonido generacionF;
    public static Sonido muertef;
    public GestorAudio() {
        // Precargar efectos Jugador
        salto = new Sonido("/Sonido/Jugador/salto.wav");
        ataque = new Sonido("/Sonido/Jugador/golpe1.wav");
        paso = new Sonido("/Sonido/Jugador/Pasos.wav");
        daño = new Sonido("/Sonido/Jugador/dano.wav");
        curarse = new Sonido("/Sonido/Jugador/Curarse.wav");
        pegar = new Sonido("/Sonido/Jugador/golpe2.wav");
        
        //Precargar efecto Fargano
        ataqueF =new Sonido("/Sonido/Enemigos/Fargano/ataque.wav");
        muerteF = new Sonido("/Sonido/Enemigos/Fargano/muerte.wav");
        generacionF = new Sonido("/Sonido/Enemigos/Fargano/Fargo.wav");
        muertef= new Sonido("/Sonido/Enemigos/Fargano/muertef.wav");
    }

    // Cambiar música de escenario
    public void reproducirMusica(String ruta) {
        if (musicaActual != null) {
            musicaActual.parar();
        }
        musicaActual = new Musica(ruta);
        musicaActual.reproducirEnLoop();
    }

    public void silenciarTodo() {
        if (musicaActual != null) musicaActual.silenciar();
        salto.silenciar();
        ataque.silenciar();
        paso.silenciar();
        daño.silenciar();
        curarse.silenciar();
    }

    public void desSilenciarTodo() {
        if (musicaActual != null) musicaActual.desSilenciar();
        salto.desSilenciar();
        ataque.desSilenciar();
        paso.desSilenciar();
        daño.desSilenciar();
        curarse.desSilenciar();
    }

    public void setVolumenMusica(float db) {
        if (musicaActual != null) musicaActual.setVolumen(db);
    }

    public void setVolumenEfectos(float db) {
        salto.setVolumen(db);
        ataque.setVolumen(db);
        paso.setVolumen(db);
        daño.setVolumen(db);
        curarse.setVolumen(db);
    }
    
    public static void reproducirEfecto(String nombre) {
    switch (nombre) {
        case "salto" -> salto.reproducir();
        case "ataque" -> ataque.reproducir();
        case "paso" -> paso.reproducir();
        case "daño" -> daño.reproducir();
        case "curarse"-> curarse.reproducir();
        case "pegar"->pegar.reproducir();
    }
}
    
    public static void reproducirEfectoFargano(String nombre) {
    switch (nombre) {
        case "MuerteF" -> muerteF.reproducir();
        case "ataqueF" -> ataqueF.reproducir();
        case "GeneracionF" -> generacionF.reproducir();
        case "muertef" -> muertef.reproducir();
    }
}
}
