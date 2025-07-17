package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class PanelMenuLateral extends JPanel {

    private JPanel panelMenu;
    private JButton editarBtn, cerrarBtn, tuercaLateral;
    private boolean subMenuActivo = false;
    private PanelAyuda panelAyudaReferencia;
    private PanelCréditos panelCreditosReferencia; // Referencia a panel de Créditos
    private final int ANCHO_MENU = 270;
    private final int ALTURA_BOTON = 60;
    private final int ESPACIADO = 20;
    private final int MARGEN_SUPERIOR = 100;

    private ArrayList<JButton> botonesMenu = new ArrayList<>();
    Font fuentePixel;

    private JButton controlExterno;

    public PanelMenuLateral() {
        setLayout(null);
        setOpaque(false);
        cargarFuentePixel(); // Carga la fuente desde recursos correctamente

        this.setBounds(0, 0, ANCHO_MENU, 767);
        this.setPreferredSize(new Dimension(ANCHO_MENU, 767));

        panelMenu = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    URL ruta = getClass().getResource("/img/fondo_menu.png");
                    Image bg = new ImageIcon(ruta).getImage();
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    g.setColor(new Color(28, 28, 30));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        panelMenu.setOpaque(false);
        panelMenu.setBounds(0, 0, ANCHO_MENU, 767);
        panelMenu.setPreferredSize(new Dimension(ANCHO_MENU, 767));
        add(panelMenu);

        tuercaLateral = new JButton();
        tuercaLateral.setBounds(ANCHO_MENU -  55, 10, 40, 40);
        estilizarBotonIcono(tuercaLateral, "/img/confi.png");
        tuercaLateral.addActionListener(e -> cerrarMenuDesdePadre());
        panelMenu.add(tuercaLateral);

        int y = MARGEN_SUPERIOR;

        JButton btnUsuario = crearBoton("Usuario", "/img/user.png", y);
        btnUsuario.addActionListener(e -> toggleSubMenu());
        panelMenu.add(btnUsuario);
        botonesMenu.add(btnUsuario);
        y += ALTURA_BOTON + ESPACIADO;

        editarBtn = crearBoton("   Editar Nombre", "/img/i.png", y);
        cerrarBtn = crearBoton("   Cerrar sesión", "/img/i.png", y + ALTURA_BOTON + 10);
        editarBtn.setVisible(false);
        cerrarBtn.setVisible(false);
        editarBtn.addActionListener(e -> mostrarPanelEditarNombre());
        cerrarBtn.addActionListener(e -> showLogoutDialog());
        panelMenu.add(editarBtn);
        panelMenu.add(cerrarBtn);

        JButton btnSonido = crearBoton("Sonido", "/img/soni.png", y);
        btnSonido.addActionListener(e -> showSoundDialog());
        panelMenu.add(btnSonido);
        botonesMenu.add(btnSonido);
        y += ALTURA_BOTON + ESPACIADO;

        // Botón de Ayuda
        JButton btnAyuda = crearBoton("Ayuda", "/img/ayuda.png", y);
        btnAyuda.addActionListener(e -> {
            if (panelAyudaReferencia != null) {
                boolean visible = panelAyudaReferencia.isVisible();
                panelAyudaReferencia.setVisible(!visible);
            }
        });
        panelMenu.add(btnAyuda);
        botonesMenu.add(btnAyuda);
        y += ALTURA_BOTON + ESPACIADO;

        // Botón de Créditos
        JButton btnCreditos = crearBoton("Créditos", "/img/creditos.png", y);
        btnCreditos.addActionListener(e -> {
            if (panelCreditosReferencia != null) {
                boolean visible = panelCreditosReferencia.isVisible();
                panelCreditosReferencia.setVisible(!visible); // Cambiar visibilidad
            }
        });
        panelMenu.add(btnCreditos);
        botonesMenu.add(btnCreditos);
        y += ALTURA_BOTON + ESPACIADO;

        actualizarPosiciones();
    }

    public void setControlExternamente(JButton btnTuerca) {
        this.controlExterno = btnTuerca;
    }
    
    // Método para enlazar el PanelAyuda desde la ventana principal
    public void setPanelAyudaExternamente(PanelAyuda panelAyuda) {
        this.panelAyudaReferencia = panelAyuda;
    }

    // Método para enlazar el PanelCréditos desde la ventana principal
    public void setPanelCreditosExternamente(PanelCréditos panelCreditos) {
        this.panelCreditosReferencia = panelCreditos;
    }

    public void estilizarBotonIcono(JButton btn, String rutaIcono) {
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        try {
            URL ruta = getClass().getResource(rutaIcono);
            Image img = new ImageIcon(ruta).getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            System.out.println("No se pudo cargar el ícono: " + rutaIcono);
        }
    }

    private JButton crearBoton(String texto, String rutaIcono, int y) {
        Color colorNormal = new Color(0x5A6673);
        Color colorHover = new Color(0x6A7684);
        Color bordeNegro = new Color(0, 0, 0, 100);

        JButton boton = new JButton(texto);
        boton.setBounds(20, y, ANCHO_MENU - 50, ALTURA_BOTON);
        boton.setForeground(Color.WHITE);
        boton.setBackground(colorNormal);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(bordeNegro, 5));
        boton.setFont(fuentePixel);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setHorizontalTextPosition(SwingConstants.RIGHT);
        boton.setVerticalTextPosition(SwingConstants.CENTER);
        boton.setIconTextGap(10);
        boton.setMargin(new Insets(0, 20, 0, 10));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        try {
            URL ruta = getClass().getResource(rutaIcono);
            Image img = new ImageIcon(ruta).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            boton.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            System.out.println("No se encontró el ícono: " + rutaIcono);
        }

        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                boton.setBackground(colorHover);
            }
            public void mouseExited(MouseEvent evt) {
                boton.setBackground(colorNormal);
            }
        });

        return boton;
    }

    private void actualizarPosiciones() {
        int y = MARGEN_SUPERIOR;
        int margenIzquierdo = 20;
        int margenDerecho = 30;
        int anchoBoton = ANCHO_MENU - margenIzquierdo - margenDerecho;

        botonesMenu.get(0).setBounds(margenIzquierdo, y, anchoBoton, ALTURA_BOTON);
        y += ALTURA_BOTON + ESPACIADO;

        if (subMenuActivo) {
            editarBtn.setBounds(margenIzquierdo, y, anchoBoton, ALTURA_BOTON);
            y += ALTURA_BOTON + 10;
            cerrarBtn.setBounds(margenIzquierdo, y, anchoBoton, ALTURA_BOTON);
            y += ALTURA_BOTON + ESPACIADO;
        }

        for (int i = 1; i < botonesMenu.size(); i++) {
            botonesMenu.get(i).setBounds(margenIzquierdo, y, anchoBoton, ALTURA_BOTON);
            y += ALTURA_BOTON + ESPACIADO;
        }

        panelMenu.repaint();
        panelMenu.revalidate();
    }

    private void toggleSubMenu() {
        subMenuActivo = !subMenuActivo;
        editarBtn.setVisible(subMenuActivo);
        cerrarBtn.setVisible(subMenuActivo);
        actualizarPosiciones();
    }

    private void cerrarMenuDesdePadre() {
        if (getTopLevelAncestor() instanceof VentanaJuego ventana) {
            ventana.cerrarMenu();
        }
    }

    private void mostrarPanelEditarNombre() {
        // Faltaría implementar
    }

    private JButton createDialogButton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(fuentePixel.deriveFont(14f));
        btn.setBackground(new Color(40, 40, 45));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Cierre sesión
    private void showLogoutDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog((Frame) parent, true);
        dialog.setUndecorated(true);

        int anchoDialogo = 520;
        int altoDialogo = 320;
        dialog.setSize(anchoDialogo, altoDialogo);
        dialog.setLocationRelativeTo(parent);

        JPanel p = new JPanel(null);
        p.setBackground(new Color(26, 28, 34));
        dialog.setContentPane(p);

        // Título principal
        JLabel lblTitle = new JLabel("¿Deseas salir?");
        lblTitle.setFont(fuentePixel.deriveFont(18f));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(0, 20, anchoDialogo, 30);
        p.add(lblTitle);

        // Ícono pixelado de cierre de sesión (escalado a 96x96)
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/img/icono_salida_pixel.png"));
        Image imagenEscalada = originalIcon.getImage().getScaledInstance(90, 90, Image.SCALE_DEFAULT); // Mantiene nitidez pixel art
        ImageIcon iconoSalida = new ImageIcon(imagenEscalada);

        JLabel lblIcono = new JLabel(iconoSalida);
        lblIcono.setBounds((anchoDialogo - 96) / 2, 60, 96, 96); // Centrado horizontal
        p.add(lblIcono);

        // Botón "Salir"
        JButton btnCerrarSesion = createDialogButton("Salir");
        btnCerrarSesion.setBounds(80, 170, 150, 50);
        btnCerrarSesion.addActionListener(e -> {
            dialog.dispose();
            if (getTopLevelAncestor() instanceof VentanaJuego ventana) {
                ventana.cerrarMenu();
                ventana.mostrarNieblaDeDesconexion();
            }
        });
        p.add(btnCerrarSesion);

        // Botón "Cancelar"
        JButton btnCancelar = createDialogButton("Cancelar");
        btnCancelar.setBounds(290, 170, 150, 50);
        btnCancelar.addActionListener(e -> dialog.dispose());
        p.add(btnCancelar);

        // Texto debajo de los botones
        JLabel lblSub = new JLabel("Puedes volver cuando quieras.");
        lblSub.setFont(fuentePixel.deriveFont(10f));
        lblSub.setForeground(Color.LIGHT_GRAY);
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);
        lblSub.setBounds(0, 240, anchoDialogo, 20);
        p.add(lblSub);

        dialog.setVisible(true);
    }

    // CONFIGURACION DE SONIDO
    private void showSoundDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog((Frame) parent, true);
        dialog.setUndecorated(true);

        int anchoDialogo = 520;
        int altoDialogo = 320;
        dialog.setSize(anchoDialogo, altoDialogo);
        dialog.setLocationRelativeTo(parent);

        JPanel p = new JPanel(null);
        p.setBackground(new Color(26, 28, 34));
        dialog.setContentPane(p);

        // Base para bloques centrados (total 260px)
        int centroBloqueX = (anchoDialogo - 260) / 2;

        // Título centrado
        JLabel lblTitle = new JLabel("CONFIGURACIÓN DE SONIDO");
        lblTitle.setFont(fuentePixel.deriveFont(16f));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(0, 50, anchoDialogo, 30);
        p.add(lblTitle);

        // 🎵 Música (95px de etiqueta, 160px de slider)
        JLabel lblMusica = new JLabel("Música:");
        lblMusica.setFont(fuentePixel.deriveFont(12f));
        lblMusica.setForeground(Color.WHITE);
        lblMusica.setHorizontalAlignment(SwingConstants.RIGHT);
        lblMusica.setBounds(centroBloqueX, 100, 95, 20);
        p.add(lblMusica);

        JSlider sliderMusica = new JSlider(0, 100, 80);
        sliderMusica.setBounds(centroBloqueX + 100, 100, 160, 20);
        sliderMusica.setBackground(new Color(26, 28, 34));
        sliderMusica.setForeground(Color.GREEN);
        p.add(sliderMusica);

        //  Efectos (mismo ancho de bloque)
        JLabel lblEfectos = new JLabel("Sonido:");
        lblEfectos.setFont(fuentePixel.deriveFont(12f));
        lblEfectos.setForeground(Color.WHITE);
        lblEfectos.setHorizontalAlignment(SwingConstants.RIGHT);
        lblEfectos.setBounds(centroBloqueX, 140, 95, 20);
        p.add(lblEfectos);
        
        JSlider sliderEfectos = new JSlider(0, 100, 60);
        sliderEfectos.setBounds(centroBloqueX + 100, 140, 160, 20);
        sliderEfectos.setBackground(new Color(26, 28, 34));
        sliderEfectos.setForeground(Color.GREEN);
        p.add(sliderEfectos);

        // ️ Switches centrados debajo
        int switchAncho = 110;
        int espacioEntre = 30;
        int switchesTotal = switchAncho * 2 + espacioEntre;
        int switchesX = (anchoDialogo - switchesTotal) / 2;

        ToggleSwitch switchMusica = new ToggleSwitch("🎵");
        switchMusica.setBounds(switchesX, 180, switchAncho, 36);
        p.add(switchMusica);

        ToggleSwitch switchEfectos = new ToggleSwitch("🔊");
        switchEfectos.setBounds(switchesX + switchAncho + espacioEntre, 180, switchAncho, 36);
        p.add(switchEfectos);

        //  Botón OK centrado
        JButton btnOk = createDialogButton("Ok");
        btnOk.setBounds((anchoDialogo - 120) / 2, 240, 120, 40);
        btnOk.addActionListener(e -> dialog.dispose());
        p.add(btnOk);

        dialog.setVisible(true);
    }

    private void cargarFuentePixel() {
        try {
            InputStream is = getClass().getResourceAsStream("/img/PressStart2P-Regular.ttf");

            if (is == null) {
                System.out.println("️ No se encontró la fuente. Verifica que esté dentro de src/img/");
                fuentePixel = new Font("Monospaced", Font.PLAIN, 12);
                return;
            }

            fuentePixel = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(12f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fuentePixel);
            System.out.println(" Fuente cargada correctamente: " + fuentePixel.getFontName());
        } catch (Exception e) {
            System.out.println(" Error al cargar la fuente:");
            e.printStackTrace();
            fuentePixel = new Font("Monospaced", Font.PLAIN, 12);
        }
    }
}
