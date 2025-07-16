
package devt.login.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import com.google.gson.JsonObject;

public class ProfileStatsDisplayPanel extends JPanel {

    private JLabel lblCharacterNameFull;
    private JLabel lblLevelFull;
    private JLabel lblExperience;
    private JLabel lblHealthFull;
    private JLabel lblEnergyFull;
    private JLabel lblPlayTime;
    private JLabel lblLastSave;

    public ProfileStatsDisplayPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(40, 40, 40));
        setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(new LineBorder(new Color(0, 180, 255), 2), "Estadísticas Detalladas del Personaje", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 18), new Color(0, 180, 255)),
            new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font valueFont = new Font("Arial", Font.PLAIN, 16);
        Color labelColor = new Color(220, 220, 220);
        Color valueColor = new Color(255, 255, 255);

        int row = 0;

        // Nombre del Personaje
        gbc.gridx = 0; gbc.gridy = row;
        JLabel nameLabel = new JLabel("Nombre:");
        nameLabel.setFont(labelFont); nameLabel.setForeground(labelColor);
        add(nameLabel, gbc);

        gbc.gridx = 1;
        lblCharacterNameFull = new JLabel("Cargando...");
        lblCharacterNameFull.setFont(valueFont); lblCharacterNameFull.setForeground(valueColor);
        add(lblCharacterNameFull, gbc);
        row++;

        // Nivel
        gbc.gridx = 0; gbc.gridy = row;
        JLabel levelLabel = new JLabel("Nivel:");
        levelLabel.setFont(labelFont); levelLabel.setForeground(labelColor);
        add(levelLabel, gbc);

        gbc.gridx = 1;
        lblLevelFull = new JLabel("Cargando...");
        lblLevelFull.setFont(valueFont); lblLevelFull.setForeground(valueColor);
        add(lblLevelFull, gbc);
        row++;

        // Experiencia
        gbc.gridx = 0; gbc.gridy = row;
        JLabel expLabel = new JLabel("Experiencia:");
        expLabel.setFont(labelFont); expLabel.setForeground(labelColor);
        add(expLabel, gbc);

        gbc.gridx = 1;
        lblExperience = new JLabel("Cargando...");
        lblExperience.setFont(valueFont); lblExperience.setForeground(valueColor);
        add(lblExperience, gbc);
        row++;

        // Vida
        gbc.gridx = 0; gbc.gridy = row;
        JLabel healthLabel = new JLabel("Vida Actual:");
        healthLabel.setFont(labelFont); healthLabel.setForeground(labelColor);
        add(healthLabel, gbc);

        gbc.gridx = 1;
        lblHealthFull = new JLabel("Cargando...");
        lblHealthFull.setFont(valueFont); lblHealthFull.setForeground(valueColor);
        add(lblHealthFull, gbc);
        row++;

        // Energía
        gbc.gridx = 0; gbc.gridy = row;
        JLabel energyLabel = new JLabel("Energía:");
        energyLabel.setFont(labelFont); energyLabel.setForeground(labelColor);
        add(energyLabel, gbc);

        gbc.gridx = 1;
        lblEnergyFull = new JLabel("Cargando...");
        lblEnergyFull.setFont(valueFont); lblEnergyFull.setForeground(valueColor);
        add(lblEnergyFull, gbc);
        row++;

        // Tiempo de Juego
        gbc.gridx = 0; gbc.gridy = row;
        JLabel playtimeLabel = new JLabel("Tiempo de Juego:");
        playtimeLabel.setFont(labelFont); playtimeLabel.setForeground(labelColor);
        add(playtimeLabel, gbc);

        gbc.gridx = 1;
        lblPlayTime = new JLabel("Cargando...");
        lblPlayTime.setFont(valueFont); lblPlayTime.setForeground(valueColor);
        add(lblPlayTime, gbc);
        row++;

        // Último Punto de Guardado
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lastSaveLabel = new JLabel("Último Guardado:");
        lastSaveLabel.setFont(labelFont); lastSaveLabel.setForeground(labelColor);
        add(lastSaveLabel, gbc);

        gbc.gridx = 1;
        lblLastSave = new JLabel("Cargando...");
        lblLastSave.setFont(valueFont); lblLastSave.setForeground(valueColor);
        add(lblLastSave, gbc);
        row++;
    }

    public void loadCharacterData(JsonObject characterData) {
        if (characterData != null) {
            lblCharacterNameFull.setText(characterData.get("nombre_personaje").getAsString());
            lblLevelFull.setText(String.valueOf(characterData.get("nivel").getAsInt()));
            lblExperience.setText(String.valueOf(characterData.get("experiencia").getAsInt()));
            lblHealthFull.setText(String.valueOf(characterData.get("vida_actual").getAsInt()));
            lblEnergyFull.setText(String.valueOf(characterData.get("energia").getAsInt()));
            lblPlayTime.setText(characterData.has("tiempo_juego") && !characterData.get("tiempo_juego").isJsonNull() ? characterData.get("tiempo_juego").getAsString() : "00:00:00");
            lblLastSave.setText(characterData.has("last_save_point") && !characterData.get("last_save_point").isJsonNull() ? characterData.get("last_save_point").getAsString() : "N/A");
        } else {
            lblCharacterNameFull.setText("N/A");
            lblLevelFull.setText("N/A");
            lblExperience.setText("N/A");
            lblHealthFull.setText("N/A");
            lblEnergyFull.setText("N/A");
            lblPlayTime.setText("N/A");
            lblLastSave.setText("N/A");
        }
    }
}
