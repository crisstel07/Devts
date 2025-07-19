package Main;

import javax.swing.*;
<<<<<<< HEAD
import java.awt.*; // Necesario para Graphics, etc.

// Creamos la clase que hereda de JPanel
public class VentanaJuego extends JPanel {
    // Ya no necesita un título de ventana aquí, lo manejará el JFrame principal
    public static String titulo = "VEILWAKER - DEMO"; 
    private PanelJuego panelJuego; // Mantener una referencia al PanelJuego

    // Constructor de la clase
    public VentanaJuego() {
        // Un JPanel no tiene título, tamaño de ventana, ni operación de cierre.
        // Estas propiedades son del JFrame que lo contiene.
        // this.setTitle(titulo); // Eliminado
        // this.setSize(1365, 767); // Eliminado
        // this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Eliminado
        // this.setResizable(false); // Eliminado
        // this.setLocationRelativeTo(null); // Eliminado
        // this.setVisible(true); // Eliminado

        // Establecer un layout para VentanaJuego si es necesario.
        // Por defecto, JPanel usa FlowLayout. Si PanelJuego debe ocupar todo, usa BorderLayout.
        setLayout(new BorderLayout()); 
        
        // Agregamos al constructor el PanelJuego
        panelJuego = new PanelJuego(); // Inicializar PanelJuego
        this.add(panelJuego, BorderLayout.CENTER); // Añadir PanelJuego para que ocupe todo el espacio
        
        // No llamar pack() o setVisible(true) aquí, ya que es un JPanel.
        // Se harán en el método startGame() que será llamado por el JFrame principal.
    }

    // Método para inicializar la lógica del juego y solicitar el enfoque
    public void startGame() {
        // No llamar pack() o setVisible(true) aquí, ya que el JPanel ya estará en el JFrame principal.
        
        // Le damos inicio al metodo iniciarJuego que inicia el metodo run que el mismo inicia el metodo Actualizar y repaint
        panelJuego.iniciarJuego();
        // Forzamos a que tome enfoque en el panel para que detecte el teclado
        panelJuego.requestFocusInWindow(); // Usar requestFocusInWindow() para mayor fiabilidad
    }

    // Opcional: Puedes añadir un paintComponent si VentanaJuego tiene su propio fondo o elementos
    // @Override
    // protected void paintComponent(Graphics g) {
    //     super.paintComponent(g);
    //     // Dibuja aquí el fondo o elementos de VentanaJuego si los tiene
    // }
}
=======
import java.awt.*;
import java.io.InputStream;

public class VentanaJuego extends JFrame {

    private PanelMenuLateral panelMenu;
    private JButton btnTuercaFlotante;
    private PanelAyuda panelAyuda; // Panel del sistema de ayuda
    private PanelCréditos panelCreditos; // Panel de los créditos

    private final int ANCHO_VENTANA = 1365;
    private final int ANCHO_MENU = 270;
    private final int ALTURA_VENTANA = 767;

    private final int MARGEN_DERECHO = 13; // Margen visual entre el menú y el borde
    private final int POS_MENU_ABIERTO_X = ANCHO_VENTANA - ANCHO_MENU - MARGEN_DERECHO;
    private final int POS_MENU_CERRADO_X = ANCHO_VENTANA + MARGEN_DERECHO;

    // Constructor de la clase
    public VentanaJuego() {
        // Titulo de la ventana
        this.setTitle("VEILWAKER");
        // Tamaño de la ventana
        this.setSize(ANCHO_VENTANA, ALTURA_VENTANA);
        // Operación para cerrar el JFrame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Que el usuario no pueda alterar el tamaño de la ventana
        this.setResizable(false);
        // Que la pestaña que se inicialice sea en el centro de la pantalla
        this.setLocationRelativeTo(null);

        // ==================== CAPA PRINCIPAL ===================== //

        // Usamos capas para añadir múltiples paneles sin que se tapen
        JLayeredPane capas = getLayeredPane();
        capas.setLayout(null);

        // Creamos y agregamos el panel del juego
        PanelJuego panelJuego = new PanelJuego();
        panelJuego.setBounds(0, 0, ANCHO_VENTANA, ALTURA_VENTANA);
        capas.add(panelJuego, JLayeredPane.DEFAULT_LAYER);

        panelJuego.iniciarJuego();      // Inicia lógica del juego
        panelJuego.requestFocus();      // Enfoca el panel para uso de teclado

        // ==================== PANEL DE MENÚ LATERAL ===================== //

        // Panel de configuración lateral
        panelMenu = new PanelMenuLateral();
        panelMenu.setBounds(POS_MENU_CERRADO_X, 0, ANCHO_MENU, ALTURA_VENTANA);
        panelMenu.setVisible(false); // Oculto inicialmente
        capas.add(panelMenu, JLayeredPane.PALETTE_LAYER);

        // Botón flotante tipo tuerca para abrir el menú lateral
        btnTuercaFlotante = new JButton();
        btnTuercaFlotante.setBounds(ANCHO_VENTANA - 50 - MARGEN_DERECHO, 10, 40, 40);
        panelMenu.estilizarBotonIcono(btnTuercaFlotante, "/img/confi.png"); // Aplica estilo personalizado
        btnTuercaFlotante.setVisible(true);
        btnTuercaFlotante.addActionListener(e -> abrirMenu()); // Acción de abrir menú
        capas.add(btnTuercaFlotante, JLayeredPane.MODAL_LAYER);

        // Conectamos el botón flotante al menú para control externo
        panelMenu.setControlExternamente(btnTuercaFlotante);

        // ==================== PANEL DE AYUDA ===================== //

        // Creamos el PanelAyuda y lo guardamos como atributo
        panelAyuda = new PanelAyuda(panelMenu.fuentePixel);
        panelAyuda.setVisible(false);
        capas.add(panelAyuda, JLayeredPane.PALETTE_LAYER);
        panelMenu.setPanelAyudaExternamente(panelAyuda); // Enlazamos panel de ayuda

        // ==================== PANEL DE CRÉDITOS ===================== //

        // Cargar la fuente para los créditos (debe ser la misma fuente pixelada)
        Font fuentePixel = null;
        try {
            InputStream is = getClass().getResourceAsStream("/img/PressStart2P-Regular.ttf");
            fuentePixel = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Crear e insertar el panel de créditos
        panelCreditos = new PanelCréditos(fuentePixel);
        panelCreditos.setVisible(false);  // Inicialmente oculto
        capas.add(panelCreditos, JLayeredPane.PALETTE_LAYER);

        // ==================== MOSTRAR VENTANA ===================== //

        // Hacemos visible la ventana principal
        this.setVisible(true);
    }

    // Método para abrir el menú lateral con animación
    private void abrirMenu() {
        btnTuercaFlotante.setVisible(false); // Oculta botón cuando se abre el menú
        deslizarMenu(true); // Inicia animación para mostrar menú
    }

    // Método para cerrar el menú lateral con animación
    public void cerrarMenu() {
        deslizarMenu(false); // Inicia animación para ocultar menú

        // Regresa el enfoque al panel principal del juego
        for (Component comp : getLayeredPane().getComponentsInLayer(JLayeredPane.DEFAULT_LAYER)) {
            if (comp instanceof PanelJuego juego) {
                juego.requestFocusInWindow();
                break;
            }
        }
    }

    // Animación tipo slide para abrir o cerrar menú lateral
    private void deslizarMenu(boolean abrir) {
        int inicio = abrir ? POS_MENU_CERRADO_X : POS_MENU_ABIERTO_X;
        int fin = abrir ? POS_MENU_ABIERTO_X : POS_MENU_CERRADO_X;
        int paso = abrir ? -10 : 10;

        Timer timer = new Timer(10, null);
        timer.addActionListener(e -> {
            int x = panelMenu.getX();
            if ((abrir && x <= fin) || (!abrir && x >= fin)) {
                panelMenu.setBounds(fin, 0, ANCHO_MENU, ALTURA_VENTANA);
                timer.stop();
                if (!abrir) btnTuercaFlotante.setVisible(true);
                return;
            }
            panelMenu.setBounds(x + paso, 0, ANCHO_MENU, ALTURA_VENTANA);
            panelMenu.repaint();
        });
        timer.start();
        panelMenu.setVisible(true);
    }

    // Mostrar el panel de créditos y ocultar el menú lateral
    public void mostrarCreditos() {
        panelCreditos.setVisible(true);  // Muestra el panel de créditos
        panelMenu.setVisible(false);    // Oculta el menú lateral si lo deseas
    }

    // Método para ocultar los créditos
    public void ocultarCreditos() {
        panelCreditos.setVisible(false); // Oculta el panel de créditos
        panelMenu.setVisible(true);      // Vuelve a mostrar el menú lateral
    }

    

    // Panel visual de niebla que se activa al cerrar sesión
    public void mostrarNieblaDeDesconexion() {
        JPanel panelFalla = new JPanel() {
            float alpha = 0f;
            Timer timer;
            int glitchOffset = 0;
            final int velocidadAnimacion = 25;
            final int brilloMaximo = 210;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

                int width = getWidth();
                int height = getHeight();

                // Efecto de niebla con gradiente
                GradientPaint niebla = new GradientPaint(
                    0, height, new Color(brilloMaximo, brilloMaximo, brilloMaximo, 210),
                    0, 0, new Color(brilloMaximo, brilloMaximo, brilloMaximo, 60)
                );
                g2.setPaint(niebla);
                g2.fillRect(0, 0, width, height);

                // Líneas glitch para dar efecto distorsionado
                g2.setColor(new Color(255, 255, 255, 50));
                for (int i = 0; i < height; i += 8) {
                    int desvio = (int) (Math.random() * 30 - 15);
                    g2.fillRect(desvio, i + glitchOffset % 6, width, 2);
                }

                g2.dispose();
            }

            {
                setOpaque(false);
                setLayout(null);
                setBounds(0, 0, ANCHO_VENTANA, ALTURA_VENTANA);

                JLabel mensaje = new JLabel("PRÓXIMA PARADA:" + "REALIDAD");
                mensaje.setFont(new Font("Press Start 2P", Font.BOLD, 18));
                mensaje.setForeground(new Color(60, 60, 60));
                mensaje.setHorizontalAlignment(SwingConstants.CENTER);
                mensaje.setBounds(0, ALTURA_VENTANA / 2 + 20, ANCHO_VENTANA, 40);
                add(mensaje);

                // Animación para mostrar niebla y luego cerrar programa
                timer = new Timer(velocidadAnimacion, evt -> {
                    alpha += 0.035f;
                    glitchOffset += 2;
                    if (alpha >= 1f) {
                        ((Timer) evt.getSource()).stop();
                        new Timer(1500, e -> System.exit(0)).start();
                    }
                    repaint();
                });
                timer.start();
            }
        };

        // Agregamos la niebla como capa superior
        getLayeredPane().add(panelFalla, JLayeredPane.DRAG_LAYER);
        getLayeredPane().revalidate();
        getLayeredPane().repaint();
    }
}
>>>>>>> Deysi
