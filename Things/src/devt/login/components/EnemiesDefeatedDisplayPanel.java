
package devt.login.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.URL;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class EnemiesDefeatedDisplayPanel extends JPanel {

    private JList<String> enemiesList;
    private DefaultListModel<String> enemiesListModel;
    private Map<String, JsonObject> enemyDetailsMap;
    
    // Componentes del panel de información del enemigo
    private JPanel enemyInfoPanel;
    private JLabel infoEnemyIcon;
    private JLabel infoEnemyName;
    private JTextArea infoEnemyDescription;
    private JLabel infoEnemyHP;
    private JLabel infoEnemyAttack;
    private JLabel infoEnemyDefense;
    private JLabel infoEnemyExpReward;
    private JLabel infoEnemyCount;

    // Declarar el JSplitPane como un campo de la clase
    private JSplitPane mainSplitPane;

    public EnemiesDefeatedDisplayPanel() {
        this.enemyDetailsMap = new HashMap<>();
        // El panel principal usará un BorderLayout para contener el JSplitPane
        setLayout(new BorderLayout()); 
        
        setBackground(new Color(40, 40, 40));
        setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(new LineBorder(new Color(255, 100, 0), 2), "Enemigos Derrotados", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 18), new Color(255, 100, 0)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        initComponents();
        clearEnemyInfo();
    }

    private void initComponents() {
        // Crear el JSplitPane
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerSize(10);
        mainSplitPane.setResizeWeight(0.4); // La lista de enemigos ocupa menos espacio
        mainSplitPane.setOpaque(false); // Para que el fondo del padre se vea si es necesario

        // --- Panel Izquierdo del SplitPane: Lista de Enemigos Derrotados ---
        enemiesListModel = new DefaultListModel<>();
        enemiesList = new JList<>(enemiesListModel);
        enemiesList.setFont(new Font("Arial", Font.PLAIN, 16));
        enemiesList.setBackground(new Color(50, 50, 50));
        enemiesList.setForeground(Color.WHITE);
        enemiesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        enemiesList.setBorder(new EmptyBorder(5, 5, 5, 5));

        enemiesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedEnemyName = enemiesList.getSelectedValue();
                if (selectedEnemyName != null) {
                    displayEnemyInfo(selectedEnemyName);
                } else {
                    clearEnemyInfo();
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(enemiesList);
        listScrollPane.setBorder(new LineBorder(new Color(100, 100, 100), 1));
        listScrollPane.getViewport().setBackground(new Color(50, 50, 50));

        mainSplitPane.setLeftComponent(listScrollPane);

        // --- Panel Derecho del SplitPane: Detalles del Enemigo Seleccionado ---
        enemyInfoPanel = new JPanel();
        enemyInfoPanel.setLayout(new BorderLayout(10, 10));
        enemyInfoPanel.setBackground(new Color(50, 50, 50));
        enemyInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(new LineBorder(new Color(255, 100, 0), 2), "Detalles del Enemigo", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 16), new Color(255, 100, 0)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel topInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        topInfoPanel.setOpaque(false);
        infoEnemyIcon = new JLabel();
        infoEnemyIcon.setPreferredSize(new Dimension(64, 64));
        infoEnemyIcon.setBorder(new LineBorder(new Color(150, 150, 150), 1));
        topInfoPanel.add(infoEnemyIcon);
        infoEnemyName = new JLabel("Selecciona un enemigo");
        infoEnemyName.setFont(new Font("Arial", Font.BOLD, 22));
        infoEnemyName.setForeground(new Color(255, 100, 0));
        topInfoPanel.add(infoEnemyName);
        enemyInfoPanel.add(topInfoPanel, BorderLayout.NORTH);

        infoEnemyDescription = new JTextArea("Información del enemigo aparecerá aquí.");
        infoEnemyDescription.setFont(new Font("Arial", Font.PLAIN, 14));
        infoEnemyDescription.setForeground(new Color(200, 200, 200));
        infoEnemyDescription.setBackground(new Color(50, 50, 50));
        infoEnemyDescription.setLineWrap(true);
        infoEnemyDescription.setWrapStyleWord(true);
        infoEnemyDescription.setEditable(false);
        JScrollPane descScrollPane = new JScrollPane(infoEnemyDescription);
        descScrollPane.setBorder(null);
        descScrollPane.setOpaque(false);
        descScrollPane.getViewport().setOpaque(false);
        enemyInfoPanel.add(descScrollPane, BorderLayout.CENTER);

        JPanel bottomInfoPanel = new JPanel(new GridLayout(4, 1, 0, 5));
        bottomInfoPanel.setOpaque(false);
        infoEnemyHP = new JLabel("HP Base: ");
        infoEnemyAttack = new JLabel("Ataque Base: ");
        infoEnemyDefense = new JLabel("Defensa Base: ");
        infoEnemyExpReward = new JLabel("Recompensa EXP: ");
        infoEnemyCount = new JLabel("Derrotados: ");

        Font detailFont = new Font("Arial", Font.PLAIN, 16);
        Color detailColor = new Color(200, 200, 200);

        infoEnemyHP.setFont(detailFont); infoEnemyHP.setForeground(detailColor);
        infoEnemyAttack.setFont(detailFont); infoEnemyAttack.setForeground(detailColor);
        infoEnemyDefense.setFont(detailFont); infoEnemyDefense.setForeground(detailColor);
        infoEnemyExpReward.setFont(detailFont); infoEnemyExpReward.setForeground(detailColor);
        infoEnemyCount.setFont(detailFont); infoEnemyCount.setForeground(detailColor);

        bottomInfoPanel.add(infoEnemyHP);
        bottomInfoPanel.add(infoEnemyAttack);
        bottomInfoPanel.add(infoEnemyDefense);
        bottomInfoPanel.add(infoEnemyExpReward);
        bottomInfoPanel.add(infoEnemyCount);
        enemyInfoPanel.add(bottomInfoPanel, BorderLayout.SOUTH);

        mainSplitPane.setRightComponent(enemyInfoPanel);

        // Añadir el JSplitPane al panel principal
        add(mainSplitPane, BorderLayout.CENTER);
    }

    public void loadEnemiesDefeatedData(JsonArray enemiesDefeatedData) {
        enemiesListModel.clear();
        enemyDetailsMap.clear();
        clearEnemyInfo();

        if (enemiesDefeatedData != null) {
            for (int i = 0; i < enemiesDefeatedData.size(); i++) {
                JsonObject enemyEntry = enemiesDefeatedData.get(i).getAsJsonObject();
                JsonObject enemyDetails = enemyEntry.getAsJsonObject("enemigo_details");
                int defeatedCount = enemyEntry.get("cantidad_derrotados").getAsInt();
                
                String enemyName = enemyDetails.get("nombre").getAsString();
                enemiesListModel.addElement(enemyName + " (x" + defeatedCount + ")");
                enemyDetailsMap.put(enemyName, enemyEntry);
            }
        }
        if (enemiesListModel.isEmpty()) {
            enemiesListModel.addElement("No has derrotado enemigos aún.");
        }
    }

    private void displayEnemyInfo(String selectedEnemyListName) {
        if (selectedEnemyListName.equals("No has derrotado enemigos aún.")) {
            clearEnemyInfo();
            return;
        }

        String enemyNameKey = selectedEnemyListName.split(" \\(x")[0];
        JsonObject enemyEntry = enemyDetailsMap.get(enemyNameKey);

        if (enemyEntry != null) {
            JsonObject enemyDetails = enemyEntry.getAsJsonObject("enemigo_details");
            int defeatedCount = enemyEntry.get("cantidad_derrotados").getAsInt();

            infoEnemyName.setText(enemyDetails.get("nombre").getAsString());
            infoEnemyDescription.setText(enemyDetails.get("descripcion").getAsString());
            infoEnemyHP.setText("HP Base: " + enemyDetails.get("hp_base").getAsInt());
            infoEnemyAttack.setText("Ataque Base: " + enemyDetails.get("ataque_base").getAsInt());
            infoEnemyDefense.setText("Defensa Base: " + enemyDetails.get("defensa_base").getAsInt());
            infoEnemyExpReward.setText("Recompensa EXP: " + enemyDetails.get("recompensa_exp").getAsInt());
            infoEnemyCount.setText("Derrotados: " + defeatedCount);
            
            String iconUrl = enemyDetails.has("icono_url") && !enemyDetails.get("icono_url").isJsonNull()
                                     ? enemyDetails.get("icono_url").getAsString() : "";
            loadImage(iconUrl, infoEnemyIcon, 64, 64);
        } else {
            clearEnemyInfo();
        }
    }

    private void clearEnemyInfo() {
        infoEnemyIcon.setIcon(null);
        infoEnemyName.setText("Selecciona un enemigo");
        infoEnemyDescription.setText("Información del enemigo aparecerá aquí.");
        infoEnemyHP.setText("HP Base: ");
        infoEnemyAttack.setText("Ataque Base: ");
        infoEnemyDefense.setText("Defensa Base: ");
        infoEnemyExpReward.setText("Recompensa EXP: ");
        infoEnemyCount.setText("Derrotados: ");
    }
    
    private void loadImage(String imageUrl, JLabel targetLabel, int width, int height) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            targetLabel.setIcon(null);
            return;
        }
        try {
            URL url;
            if (imageUrl.startsWith("/")) {
                url = getClass().getResource(imageUrl);
            } else {
                url = new URL(imageUrl);
            }

            if (url != null) {
                BufferedImage img = ImageIO.read(url);
                if (img != null) {
                    Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    targetLabel.setIcon(new ImageIcon(scaledImg));
                } else {
                    targetLabel.setIcon(null);
                }
            } else {
                targetLabel.setIcon(null);
            }
        } catch (IOException e) {
            System.err.println("Error al cargar imagen " + imageUrl + ": " + e.getMessage());
            targetLabel.setIcon(null);
        }
    }
}
