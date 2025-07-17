package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.ArrayList;

public class PanelAyuda extends JPanel {

    private final Font fuentePixel;
    private final HashMap<String, String> preguntasYRespuestas = new HashMap<>();
    private final ArrayList<JPanel> bloques = new ArrayList<>();
    private final int anchoPanel = 513;
    private final int altoPanel = 565;

    public PanelAyuda(Font fuentePixel) {
        this.fuentePixel = fuentePixel;
        setLayout(null);
        setOpaque(false);

        int anchoVentana = 1366;
        int altoVentana = 768;
        int x = ((anchoVentana - anchoPanel) / 2) - 100;
        int y = (altoVentana - altoPanel) / 2 - 30;
        setBounds(x, y, anchoPanel, altoPanel);

        Image fondoOriginal = new ImageIcon(getClass().getResource("/img/fondo_ayuda.png")).getImage();
        Image fondoEscalado = fondoOriginal.getScaledInstance(anchoPanel, altoPanel, Image.SCALE_SMOOTH);
        JLabel fondo = new JLabel(new ImageIcon(fondoEscalado));
        fondo.setLayout(null);
        fondo.setBounds(0, 0, anchoPanel, altoPanel);
        add(fondo);

        JLabel lblTitulo = new JLabel("SISTEMA DE AYUDA");
        lblTitulo.setFont(fuentePixel.deriveFont(17f));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(0, 123, anchoPanel, 28); // más abajo
        fondo.add(lblTitulo);

        JPanel contenedor = new JPanel(null);
        contenedor.setOpaque(false);
        contenedor.setBounds(54, 175, 420, 340); // más abajo aún
        fondo.add(contenedor);

        JButton btnCerrar = crearBoton("          Cerrar", "/img/i.png", 13f);
        btnCerrar.setBounds((anchoPanel - 240) / 2, 500, 240, 45);
        btnCerrar.addActionListener(e -> setVisible(false));
        fondo.add(btnCerrar);

        cargarPreguntas();
        generarBloques(contenedor);
    }

    private void cargarPreguntas() {
        preguntasYRespuestas.put(">>¿Qué es VEILWAKER?",
                "Una experiencia narrativa donde recorres vagones y descubres secretos entre mundos.");
        preguntasYRespuestas.put(">>¿Cómo interactúo con el<br>tren?",
                "Acércate y presiona la tecla D. Para avanzar.");
        preguntasYRespuestas.put(">>¿Cuáles son los controles<br>básicos?",
                "Usa la tecla D para moverte. Barra espaciadora para saltar. Click derecho para atacar.");
    }

    private void generarBloques(JPanel contenedor) {
        int y = 0;

        for (String pregunta : preguntasYRespuestas.keySet()) {
            JPanel bloque = new JPanel(null);
            bloque.setBounds(0, y, 420, 125);
            bloque.setOpaque(false);

            float fuente = pregunta.length() > 40 ? 11f : 13f;
            JButton btnPregunta = crearBoton(pregunta, "/img/i.png", fuente);
            btnPregunta.setBounds(0, 0, 420, 52);

            JTextArea areaRespuesta = new JTextArea();
            areaRespuesta.setFont(fuentePixel.deriveFont(12f));
            areaRespuesta.setForeground(Color.WHITE);
            areaRespuesta.setBackground(new Color(0, 0, 0, 130));
            areaRespuesta.setLineWrap(true);
            areaRespuesta.setWrapStyleWord(true);
            areaRespuesta.setEditable(false);
            areaRespuesta.setBounds(0, 57, 420, 75);
            areaRespuesta.setVisible(false);

            btnPregunta.addActionListener(e -> {
                boolean visible = areaRespuesta.isVisible();

                for (JPanel panel : bloques) {
                    for (Component comp : panel.getComponents()) {
                        if (comp instanceof JTextArea txt) {
                            txt.setVisible(false);
                        }
                    }
                }

                if (!visible) {
                    areaRespuesta.setText("");
                    areaRespuesta.setVisible(true);
                    String texto = preguntasYRespuestas.get(pregunta).replace("<br>", " ");
                    final int[] index = {0};
                    Timer timer = new Timer(30, null);
                    timer.addActionListener(ev -> {
                        if (index[0] < texto.length()) {
                            areaRespuesta.append(String.valueOf(texto.charAt(index[0])));
                            index[0]++;
                        } else {
                            timer.stop();
                        }
                    });
                    timer.start();
                } else {
                    areaRespuesta.setVisible(false);
                }

                int nuevaY = 0;
                for (JPanel panel : bloques) {
                    boolean activo = false;
                    for (Component comp : panel.getComponents()) {
                        if (comp instanceof JTextArea txt && txt.isVisible()) {
                            activo = true;
                            break;
                        }
                    }
                    panel.setBounds(0, nuevaY, 420, activo ? 132 : 62);
                    nuevaY += activo ? 132 : 54; // espaciado más bajo
                }

                contenedor.revalidate();
                contenedor.repaint();
            });

            bloque.add(btnPregunta);
            bloque.add(areaRespuesta);
            contenedor.add(bloque);
            bloques.add(bloque);
            y += 54; // espaciado aún más compacto
        }
    }

    private JButton crearBoton(String texto, String rutaIcono, float fuente) {
        Color fondo = new Color(0x1A1A1A);
        Color hover = new Color(0x2A2A2A);

        JButton boton = new JButton("<html><body style='width:390px;text-align:left;'>" + texto + "</body></html>");
        boton.setForeground(Color.WHITE);
        boton.setBackground(fondo);
        boton.setFocusPainted(false);
        boton.setFont(fuentePixel.deriveFont(fuente));
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setIconTextGap(10);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setMargin(new Insets(0, 20, 0, 10));
        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);

        try {
            Image img = new ImageIcon(getClass().getResource(rutaIcono))
                    .getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH);
            boton.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            System.out.println("Ícono no encontrado: " + rutaIcono);
        }

        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                boton.setBackground(hover);
            }

            public void mouseExited(MouseEvent evt) {
                boton.setBackground(fondo);
            }
        });

        return boton;
    }
}
