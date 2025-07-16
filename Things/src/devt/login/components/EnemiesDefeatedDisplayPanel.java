package devt.login.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects; // Para Objects.equals en el listener de la lista

public class EnemiesDefeatedDisplayPanel extends JPanel {

    // --- Componentes y Datos de la Clase ---
    // Componentes para el panel izquierdo (lista de enemigos)
    private JList<String> enemiesList; // Declarado como campo de la clase
    private DefaultListModel<String> enemiesListModel; // Declarado como campo de la clase
    // Almacenará los JsonObject completos de cada enemigo, indexados por el orden en la lista
    private List<JsonObject> defeatedEnemiesFullData; // Declarado como campo de la clase

    // Componentes para el panel derecho (detalles del enemigo)
    private JLabel lblEnemyIcon;
    private JLabel lblEnemyName; // Nombre del enemigo en los detalles
    private JTextArea lblEnemyDescription; // Cambiado a JTextArea para descripciones largas
    private JLabel lblHPBase;
    private JLabel lblAttackBase;
    private JLabel lblAttackType; // Nuevo: para tipo_ataque
    private JLabel lblEnemyType; // Nuevo: para tipo_enemigo
    private JLabel lblSpecialAbility; // Nuevo: para habilidad_especial
    private JLabel lblEnemyDefense; // Mantenido como "N/A" si no hay dato directo
    private JLabel lblEnemyExpReward; // Mantenido como "N/A" si no hay dato directo

    // Declarar el JSplitPane como un campo de la clase
    private JSplitPane mainSplitPane;

    public EnemiesDefeatedDisplayPanel() {
        // El panel principal usará un BorderLayout para contener el JSplitPane
        setLayout(new BorderLayout()); 
        
        setBackground(new Color(40, 40, 40));
        setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(new LineBorder(new Color(255, 100, 0), 2), "Enemigos Derrotados", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 18), new Color(255, 100, 0)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        initComponents();
        clearEnemyInfo(); // Limpiar la información al inicio
    }

    private void initComponents() {
        // Crear el JSplitPane
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerSize(10);
        mainSplitPane.setResizeWeight(0.4); // La lista de enemigos ocupa el 40% del ancho
        mainSplitPane.setOpaque(false); // Para que el fondo del padre se vea si es necesario
        mainSplitPane.setBorder(null); // Eliminar el borde por defecto del JSplitPane

        // --- Panel Izquierdo del SplitPane: Lista de Enemigos Derrotados ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(50, 50, 50));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(255, 215, 0), 1), // Borde más delgado
                "Lista de Enemigos", // Título más genérico
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16), // Fuente ligeramente más pequeña
                new Color(255, 215, 0)
        ));

        enemiesListModel = new DefaultListModel<>(); // Inicialización aquí
        enemiesList = new JList<>(enemiesListModel); // Inicialización aquí
        enemiesList.setFont(new Font("Arial", Font.PLAIN, 16));
        enemiesList.setBackground(new Color(60, 60, 60));
        enemiesList.setForeground(Color.WHITE);
        enemiesList.setSelectionBackground(new Color(80, 80, 80));
        enemiesList.setSelectionForeground(new Color(255, 215, 0));
        enemiesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        enemiesList.setBorder(new EmptyBorder(5, 5, 5, 5));
        enemiesList.setFixedCellHeight(30); // Altura fija para cada elemento de la lista

        // Listener para la selección de la lista
        enemiesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Asegura que el evento se dispare solo una vez al soltar el clic
                int selectedIndex = enemiesList.getSelectedIndex();
                if (selectedIndex != -1 && defeatedEnemiesFullData != null && selectedIndex < defeatedEnemiesFullData.size()) {
                    displayEnemyDetails(defeatedEnemiesFullData.get(selectedIndex)); // Pasa el JsonObject directamente
                } else {
                    clearEnemyInfo(); // Limpiar si no hay selección válida
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(enemiesList);
        listScrollPane.setBorder(null); // Eliminar borde del scroll pane
        listScrollPane.getViewport().setBackground(new Color(60, 60, 60)); // Fondo del viewport
        leftPanel.add(listScrollPane, BorderLayout.CENTER);

        mainSplitPane.setLeftComponent(leftPanel);

        // --- Panel Derecho del SplitPane: Detalles del Enemigo Seleccionado ---
        JPanel rightPanel = new JPanel(new GridBagLayout()); // Usar GridBagLayout para los detalles
        rightPanel.setBackground(new Color(50, 50, 50));
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(0, 150, 255), 1), // Borde más delgado
                "Detalles del Enemigo",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                new Color(0, 150, 255)
        ));

        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.insets = new Insets(6, 6, 6, 6); // Espaciado interno
        gbcRight.fill = GridBagConstraints.HORIZONTAL;
        gbcRight.anchor = GridBagConstraints.WEST;

        // Icono del Enemigo
        gbcRight.gridx = 0; gbcRight.gridy = 0; gbcRight.gridwidth = 2; // Ocupa dos columnas
        gbcRight.anchor = GridBagConstraints.CENTER; // Centrar el icono
        lblEnemyIcon = new JLabel();
        lblEnemyIcon.setPreferredSize(new Dimension(100, 100)); // Tamaño fijo para el icono
        lblEnemyIcon.setMinimumSize(new Dimension(100, 100));
        lblEnemyIcon.setMaximumSize(new Dimension(100, 100));
        lblEnemyIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblEnemyIcon.setBorder(new LineBorder(new Color(150, 150, 150), 1));
        rightPanel.add(lblEnemyIcon, gbcRight);

        // Nombre del Enemigo (en los detalles)
        gbcRight.gridy++; gbcRight.gridx = 0; gbcRight.gridwidth = 2;
        lblEnemyName = new JLabel("Selecciona un Enemigo");
        lblEnemyName.setFont(new Font("Arial", Font.BOLD, 20));
        lblEnemyName.setForeground(new Color(255, 215, 0));
        lblEnemyName.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(lblEnemyName, gbcRight);

        // Descripción del Enemigo
        gbcRight.gridy++; gbcRight.gridx = 0; gbcRight.gridwidth = 2;
        lblEnemyDescription = new JTextArea("Información del enemigo aparecerá aquí.");
        lblEnemyDescription.setFont(new Font("Arial", Font.PLAIN, 14));
        lblEnemyDescription.setForeground(new Color(200, 200, 200));
        lblEnemyDescription.setBackground(new Color(50, 50, 50));
        lblEnemyDescription.setLineWrap(true);
        lblEnemyDescription.setWrapStyleWord(true);
        lblEnemyDescription.setEditable(false);
        lblEnemyDescription.setFocusable(false); // No permitir que el usuario interactúe con él
        lblEnemyDescription.setOpaque(false); // Fondo transparente para que se vea el del panel
        JScrollPane descScrollPane = new JScrollPane(lblEnemyDescription);
        descScrollPane.setBorder(null); // Eliminar borde del scroll pane
        descScrollPane.setOpaque(false);
        descScrollPane.getViewport().setOpaque(false);
        descScrollPane.setPreferredSize(new Dimension(300, 80)); // Tamaño para la descripción
        rightPanel.add(descScrollPane, gbcRight);

        // HP Base
        gbcRight.gridy++; gbcRight.gridx = 0; gbcRight.gridwidth = 1;
        JLabel hpBaseLabel = new JLabel("HP Base:");
        hpBaseLabel.setFont(new Font("Arial", Font.BOLD, 14));
        hpBaseLabel.setForeground(Color.WHITE);
        rightPanel.add(hpBaseLabel, gbcRight);
        gbcRight.gridx = 1;
        lblHPBase = new JLabel(); // Solo el JLabel, el texto se pone en displayEnemyDetails
        lblHPBase.setFont(new Font("Arial", Font.PLAIN, 14));
        lblHPBase.setForeground(new Color(180, 180, 180));
        rightPanel.add(lblHPBase, gbcRight);

        // Ataque Base
        gbcRight.gridy++; gbcRight.gridx = 0;
        JLabel attackBaseLabel = new JLabel("Ataque Base:");
        attackBaseLabel.setFont(new Font("Arial", Font.BOLD, 14));
        attackBaseLabel.setForeground(Color.WHITE);
        rightPanel.add(attackBaseLabel, gbcRight);
        gbcRight.gridx = 1;
        lblAttackBase = new JLabel(); // Solo el JLabel
        lblAttackBase.setFont(new Font("Arial", Font.PLAIN, 14));
        lblAttackBase.setForeground(new Color(180, 180, 180));
        rightPanel.add(lblAttackBase, gbcRight);

        // Tipo de Ataque
        gbcRight.gridy++; gbcRight.gridx = 0;
        JLabel attackTypeLabel = new JLabel("Tipo de Ataque:");
        attackTypeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        attackTypeLabel.setForeground(Color.WHITE);
        rightPanel.add(attackTypeLabel, gbcRight);
        gbcRight.gridx = 1;
        lblAttackType = new JLabel(); // Solo el JLabel
        lblAttackType.setFont(new Font("Arial", Font.PLAIN, 14));
        lblAttackType.setForeground(new Color(180, 180, 180));
        rightPanel.add(lblAttackType, gbcRight);

        // Tipo de Enemigo
        gbcRight.gridy++; gbcRight.gridx = 0;
        JLabel enemyTypeLabel = new JLabel("Tipo de Enemigo:");
        enemyTypeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        enemyTypeLabel.setForeground(Color.WHITE);
        rightPanel.add(enemyTypeLabel, gbcRight);
        gbcRight.gridx = 1;
        lblEnemyType = new JLabel(); // Solo el JLabel
        lblEnemyType.setFont(new Font("Arial", Font.PLAIN, 14));
        lblEnemyType.setForeground(new Color(180, 180, 180));
        rightPanel.add(lblEnemyType, gbcRight);

        // Habilidad Especial
        gbcRight.gridy++; gbcRight.gridx = 0; gbcRight.gridwidth = 2;
        JLabel specialAbilityLabel = new JLabel("Habilidad Especial:");
        specialAbilityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        specialAbilityLabel.setForeground(Color.WHITE);
        rightPanel.add(specialAbilityLabel, gbcRight);
        gbcRight.gridy++;
        lblSpecialAbility = new JLabel(); // Solo el JLabel, el texto se pone en displayEnemyDetails
        lblSpecialAbility.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSpecialAbility.setForeground(new Color(180, 180, 180));
        lblSpecialAbility.setVerticalAlignment(SwingConstants.TOP);
        lblSpecialAbility.setPreferredSize(new Dimension(300, 60)); // Espacio para habilidad especial
        rightPanel.add(lblSpecialAbility, gbcRight);
        
        // --- Campos que no están directamente en la tabla Enemigos, se mostrarán como N/A ---
        gbcRight.gridy++; gbcRight.gridx = 0; gbcRight.gridwidth = 1;
        JLabel defenseLabel = new JLabel("Defensa Base:");
        defenseLabel.setFont(new Font("Arial", Font.BOLD, 14));
        defenseLabel.setForeground(Color.WHITE);
        rightPanel.add(defenseLabel, gbcRight);
        gbcRight.gridx = 1;
        lblEnemyDefense = new JLabel(); // Solo el JLabel
        lblEnemyDefense.setFont(new Font("Arial", Font.PLAIN, 14));
        lblEnemyDefense.setForeground(new Color(180, 180, 180));
        rightPanel.add(lblEnemyDefense, gbcRight);

        gbcRight.gridy++; gbcRight.gridx = 0;
        JLabel expRewardLabel = new JLabel("Recompensa EXP:");
        expRewardLabel.setFont(new Font("Arial", Font.BOLD, 14));
        expRewardLabel.setForeground(Color.WHITE);
        rightPanel.add(expRewardLabel, gbcRight);
        gbcRight.gridx = 1;
        lblEnemyExpReward = new JLabel(); // Solo el JLabel
        lblEnemyExpReward.setFont(new Font("Arial", Font.PLAIN, 14));
        lblEnemyExpReward.setForeground(new Color(180, 180, 180));
        rightPanel.add(lblEnemyExpReward, gbcRight);
        // --- FIN de campos no directos ---

        // Añadir un "pegamento" vertical para empujar los componentes hacia arriba
        gbcRight.gridy++; gbcRight.weighty = 1.0; // Este componente ocupará el espacio restante
        rightPanel.add(Box.createVerticalGlue(), gbcRight);

        mainSplitPane.setRightComponent(rightPanel);

        // Añadir el JSplitPane al panel principal
        add(mainSplitPane, BorderLayout.CENTER);
    }

    /**
     * Carga y muestra los datos de los enemigos derrotados en la lista.
     * Este método es llamado desde PanelProfileAndInventory.
     * @param enemiesArray JsonArray con los datos de los enemigos derrotados (incluye detalles de Enemigos).
     */
    public void loadEnemiesDefeatedData(JsonArray enemiesArray) {
        enemiesListModel.clear(); // Limpiar la lista actual
        defeatedEnemiesFullData = new ArrayList<>(); // Reiniciar la lista de datos completos
        clearEnemyInfo(); // Limpiar los detalles del panel derecho

        if (enemiesArray != null && enemiesArray.size() > 0) {
            for (JsonElement enemyElement : enemiesArray) {
                JsonObject enemy = enemyElement.getAsJsonObject();
                defeatedEnemiesFullData.add(enemy); // Guardar el JsonObject completo

                String enemyName = enemy.get("enemy_name").isJsonNull() ? "N/A" : enemy.get("enemy_name").getAsString();
                int defeatedCount = enemy.get("defeated_count").isJsonNull() ? 0 : enemy.get("defeated_count").getAsInt();
                
                // Añadir al modelo de la lista: "Nombre del Enemigo (X derrotas)"
                enemiesListModel.addElement(enemyName + " (x" + defeatedCount + ")");
            }
            // Seleccionar el primer elemento por defecto si hay datos
            enemiesList.setSelectedIndex(0);
        } else {
            enemiesListModel.addElement("No has derrotado enemigos aún.");
        }
    }

    /**
     * Muestra los detalles de un enemigo seleccionado en el panel derecho.
     * @param enemyData JsonObject con los datos completos del enemigo.
     */
    private void displayEnemyDetails(JsonObject enemyData) {
        // Nombre del Enemigo
        lblEnemyName.setText(enemyData.get("enemy_name").isJsonNull() ? "N/A" : enemyData.get("enemy_name").getAsString());

        // Icono del Enemigo
        String iconUrl = enemyData.get("enemy_icon_url").isJsonNull() ? null : enemyData.get("enemy_icon_url").getAsString();
        loadImage(iconUrl, lblEnemyIcon, lblEnemyIcon.getPreferredSize().width, lblEnemyIcon.getPreferredSize().height);

        // Descripción del Enemigo
        lblEnemyDescription.setText(enemyData.get("enemy_description").isJsonNull() ? "Sin descripción." : enemyData.get("enemy_description").getAsString());

        // HP Base (resistencia)
        // CORRECCIÓN: Solo el valor
        lblHPBase.setText(enemyData.get("enemy_hp_base").isJsonNull() ? "N/A" : String.valueOf(enemyData.get("enemy_hp_base").getAsInt()));

        // Ataque Base (daño)
        // CORRECCIÓN: Solo el valor
        lblAttackBase.setText(enemyData.get("enemy_attack_base").isJsonNull() ? "N/A" : String.valueOf(enemyData.get("enemy_attack_base").getAsFloat()));

        // Tipo de Ataque
        // CORRECCIÓN: Solo el valor
        lblAttackType.setText(enemyData.get("enemy_attack_type").isJsonNull() ? "N/A" : enemyData.get("enemy_attack_type").getAsString());

        // Tipo de Enemigo
        // CORRECCIÓN: Solo el valor
        lblEnemyType.setText(enemyData.get("enemy_type").isJsonNull() ? "N/A" : enemyData.get("enemy_type").getAsString());

        // Habilidad Especial
        // CORRECCIÓN: Solo el valor, y usar HTML si es necesario para saltos de línea
        lblSpecialAbility.setText("<html>" + (enemyData.get("enemy_special_ability").isJsonNull() ? "N/A" : enemyData.get("enemy_special_ability").getAsString()) + "</html>");
        
        // Campos que no están en la tabla Enemigos, se muestran como N/A
        // CORRECCIÓN: Solo el valor (que es N/A en este caso)
        lblEnemyDefense.setText("N/A"); // No hay campo directo en tu tabla Enemigos
        lblEnemyExpReward.setText("N/A"); // No hay campo directo en tu tabla Enemigos
    }

    /**
     * Limpia los campos de detalles del enemigo en el panel derecho.
     */
    private void clearEnemyInfo() {
        lblEnemyIcon.setIcon(null);
        lblEnemyName.setText("Selecciona un Enemigo");
        // Para JTextArea, el texto se establece directamente
        lblEnemyDescription.setText("Información del enemigo aparecerá aquí."); 
        // CORRECCIÓN: Dejar los campos vacíos o con un valor por defecto simple
        lblHPBase.setText(""); // O "N/A" si prefieres
        lblAttackBase.setText("");
        lblAttackType.setText("");
        lblEnemyType.setText("");
        lblSpecialAbility.setText(""); // Solo el valor, sin etiquetas HTML
        lblEnemyDefense.setText("");
        lblEnemyExpReward.setText("");
    }
    
    /**
     * Método auxiliar para cargar imágenes desde URL o recursos internos.
     * @param imageUrl La URL o ruta del recurso de la imagen.
     * @param targetLabel El JLabel donde se mostrará la imagen.
     * @param width El ancho deseado para la imagen.
     * @param height La altura deseada para la imagen.
     */
    private void loadImage(String imageUrl, JLabel targetLabel, int width, int height) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            targetLabel.setIcon(null);
            return;
        }
        try {
            URL url;
            // Si la URL es una ruta de recurso interno (ej. /images/enemy.png)
            if (imageUrl.startsWith("/")) { 
                url = getClass().getResource(imageUrl);
            } else { // Si es una URL externa (http/https)
                url = new URL(imageUrl);
            }

            if (url != null) {
                BufferedImage img = ImageIO.read(url);
                if (img != null) {
                    Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    targetLabel.setIcon(new ImageIcon(scaledImg));
                } else {
                    targetLabel.setIcon(null); // No se pudo leer la imagen
                }
            } else {
                targetLabel.setIcon(null); // URL nula
            }
        } catch (IOException e) {
            System.err.println("Error al cargar imagen " + imageUrl + ": " + e.getMessage());
            targetLabel.setIcon(null); // En caso de error, no mostrar icono
        }
    }
}
