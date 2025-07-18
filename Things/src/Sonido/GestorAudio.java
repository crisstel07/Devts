package Sonido;

// Asegúrate de que estas importaciones estén presentes si Sonido.java las necesita
// import javax.sound.sampled.Clip; 
// import javax.sound.sampled.FloatControl;

public class GestorAudio {
    // Música por escenario
    private Musica musicaActual;

    // Efectos pre-cargados
    public static Sonido salto;
    public static Sonido ataque;
    public static Sonido paso; // Asegúrate de que esta línea exista
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
        // ¡MODIFICADO! Ruta correcta al archivo "Pasos.wav" dentro de Sonido.Jugador
        paso = new Sonido("/Sonido/Jugador/Pasos.wav"); 
        daño = new Sonido("/Sonido/Jugador/dano.wav");
        curarse = new Sonido("/Sonido/Jugador/Curarse.wav");
        pegar = new Sonido("/Sonido/Jugador/golpe2.wav");
        
        //Precargar efecto Fargano
        ataqueF = new Sonido("/Sonido/Enemigos/Fargano/ataque.wav");
        muerteF = new Sonido("/Sonido/Enemigos/Fargano/muerte.wav");
        generacionF = new Sonido("/Sonido/Enemigos/Fargano/Fargo.wav");
        muertef = new Sonido("/Sonido/Enemigos/Fargano/muertef.wav");
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
        if (salto != null) salto.silenciar();
        if (ataque != null) ataque.silenciar();
        if (paso != null) paso.silenciar(); // Protección contra null
        if (daño != null) daño.silenciar();
        if (curarse != null) curarse.silenciar();
        if (pegar != null) pegar.silenciar(); // Añadido
        if (ataqueF != null) ataqueF.silenciar(); // Añadido
        if (muerteF != null) muerteF.silenciar(); // Añadido
        if (generacionF != null) generacionF.silenciar(); // Añadido
        if (muertef != null) muertef.silenciar(); // Añadido
    }

    public void desSilenciarTodo() {
        if (musicaActual != null) musicaActual.desSilenciar();
        if (salto != null) salto.desSilenciar();
        if (ataque != null) ataque.desSilenciar();
        if (paso != null) paso.desSilenciar(); // Protección contra null
        if (daño != null) daño.desSilenciar();
        if (curarse != null) curarse.desSilenciar();
        if (pegar != null) pegar.desSilenciar(); // Añadido
        if (ataqueF != null) ataqueF.desSilenciar(); // Añadido
        if (muerteF != null) muerteF.desSilenciar(); // Añadido
        if (generacionF != null) generacionF.desSilenciar(); // Añadido
        if (muertef != null) muertef.desSilenciar(); // Añadido
    }

    public void setVolumenMusica(float db) {
        if (musicaActual != null) musicaActual.setVolumen(db);
    }

    public void setVolumenEfectos(float db) {
        if (salto != null) salto.setVolumen(db);
        if (ataque != null) ataque.setVolumen(db);
        if (paso != null) paso.setVolumen(db); // Protección contra null
        if (daño != null) daño.setVolumen(db);
        if (curarse != null) curarse.setVolumen(db);
        if (pegar != null) pegar.setVolumen(db); // Añadido
        if (ataqueF != null) ataqueF.setVolumen(db); // Añadido
        if (muerteF != null) muerteF.setVolumen(db); // Añadido
        if (generacionF != null) generacionF.setVolumen(db); // Añadido
        if (muertef != null) muertef.setVolumen(db); // Añadido
    }
    
    public static void reproducirEfecto(String nombre) {
        switch (nombre) {
            case "salto" -> { if (salto != null) salto.reproducir(); }
            case "ataque" -> { if (ataque != null) ataque.reproducir(); }
            case "paso" -> { if (paso != null) paso.reproducir(); } // Protección contra null
            case "daño" -> { if (daño != null) daño.reproducir(); }
            case "curarse"-> { if (curarse != null) curarse.reproducir(); }
            case "pegar"-> { if (pegar != null) pegar.reproducir(); }
            default -> System.err.println("Efecto de sonido no reconocido: " + nombre); // Manejo de caso no encontrado
        }
    }
    
    public static void reproducirEfectoFargano(String nombre) {
        switch (nombre) {
            case "MuerteF" -> { if (muerteF != null) muerteF.reproducir(); }
            case "ataqueF" -> { if (ataqueF != null) ataqueF.reproducir(); }
            case "GeneracionF" -> { if (generacionF != null) generacionF.reproducir(); }
            case "muertef" -> { if (muertef != null) muertef.reproducir(); }
            default -> System.err.println("Efecto de sonido de Fargano no reconocido: " + nombre); // Manejo de caso no encontrado
        }
    }
}
