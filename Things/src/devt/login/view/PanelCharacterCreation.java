package devt.login.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import com.google.gson.JsonObject;
import devt.login.apiFlask.ApiClient;
import devt.login.apiFlask.ApiClient.ApiResponse;
import java.awt.event.ActionEvent;

public class PanelCharacterCreation extends JPanel {

    private int userId; // El ID del usuario que está creando/nombrando el personaje
    private JTextField txtCharacterName;
    private JButton btnConfirmName; // Renombrado para mayor claridad
    private JLabel lblMessage;

    // Listener para notificar cuando el personaje ha sido creado/nombrado
    private ActionListener characterCreatedListener; // Mantener este nombre para LoginBase

    public PanelCharacterCreation(int userId) {
        this.userId = userId;
        initUI();
    }

    // Método para actualizar el userId si el panel se reutiliza (aunque en este flujo no debería ser necesario)
    public void setUserId(int userId) {
        this.userId = userId;
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(25, 25, 25)); // Fondo oscuro
        setBorder(new EmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("¡Bienvenido, Aventurero!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(new Color(255, 215, 0)); // Dorado
        gbc.gridwidth = 2; // Ocupa dos columnas
        add(titleLabel, gbc);

        gbc.gridy++;
        lblMessage = new JLabel("Antes de empezar, dale un nombre a tu personaje:");
        lblMessage.setFont(new Font("Arial", Font.PLAIN, 18));
        lblMessage.setForeground(new Color(200, 200, 200)); // Gris claro
        add(lblMessage, gbc);

        gbc.gridy++;
        txtCharacterName = new JTextField(20);
        txtCharacterName.setFont(new Font("Arial", Font.PLAIN, 20));
        txtCharacterName.setBackground(new Color(50, 50, 50));
        txtCharacterName.setForeground(Color.WHITE);
        txtCharacterName.setCaretColor(Color.WHITE); // Color del cursor
        txtCharacterName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        add(txtCharacterName, gbc);

        gbc.gridy++;
        btnConfirmName = new JButton("Confirmar Nombre"); // Renombrado
        btnConfirmName.setFont(new Font("Arial", Font.BOLD, 22));
        btnConfirmName.setBackground(new Color(50, 150, 50)); // Verde
        btnConfirmName.setForeground(Color.WHITE);
        btnConfirmName.setFocusPainted(false);
        btnConfirmName.addActionListener(e -> createOrUpdateCharacterName()); // Llamar al nuevo método
        add(btnConfirmName, gbc);
    }

    /**
     * Crea o actualiza el nombre del personaje llamando a la API.
     */
    private void createOrUpdateCharacterName() {
        String newName = txtCharacterName.getText().trim();

        if (newName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del personaje no puede estar vacío.", "Error de Nombre", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Deshabilitar el botón para evitar múltiples clics
        btnConfirmName.setEnabled(false);
        lblMessage.setText("Procesando...");
        lblMessage.setForeground(new Color(0, 150, 255)); // Azul

        new SwingWorker<ApiResponse, Void>() {
            @Override
            protected ApiResponse doInBackground() throws Exception {
                // Paso 1: Obtener o crear el personaje para este usuario
                ApiResponse getOrCreateResponse = ApiClient.getOrCreateCharacterProfile(userId);
                
                if (!getOrCreateResponse.isSuccess() || getOrCreateResponse.getDataAsJsonObject() == null) {
                    return new ApiResponse(false, "Error al obtener/crear personaje: " + getOrCreateResponse.getMessage(), null, getOrCreateResponse.getErrorCode());
                }

                JsonObject characterData = getOrCreateResponse.getDataAsJsonObject();
                int characterId = characterData.get("id").getAsInt();
                
                // Verificar si el personaje ya tiene un nombre asignado
                String existingName = characterData.has("nombre_personaje") && !characterData.get("nombre_personaje").isJsonNull()
                                        ? characterData.get("nombre_personaje").getAsString() : null;
                
                // Si ya tiene un nombre y no es "None" (que indica un nombre por defecto de la DB)
                // y el nombre existente no es el mismo que el nuevo nombre que se intenta poner
                if (existingName != null && !existingName.equals("None") && !existingName.isEmpty() && !existingName.equals(newName)) {
                    // Si el personaje ya tiene un nombre válido y se intenta cambiar,
                    // podríamos añadir una lógica para preguntar al usuario o simplemente
                    // retornar un error aquí si este panel es SOLO para la creación inicial.
                    // Por ahora, si ya tiene un nombre y no es el que el usuario quiere poner,
                    // y no es el nombre por defecto "None", lo consideramos un error.
                    // Si permites la edición de nombre desde este panel, esta lógica debe cambiar.
                    return new ApiResponse(false, "Este personaje ya tiene un nombre asignado.", null, 409); // Conflict
                }

                // Paso 2: Actualizar el nombre del personaje
                JsonObject updateData = new JsonObject();
                updateData.addProperty("nombre_personaje", newName);
                return ApiClient.updateCharacterProfile(characterId, updateData);
            }

            @Override
            protected void done() {
                btnConfirmName.setEnabled(true); // Habilitar el botón de nuevo
                try {
                    ApiResponse result = get();
                    if (result.isSuccess()) {
                        JOptionPane.showMessageDialog(PanelCharacterCreation.this, "Nombre '" + newName + "' guardado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        lblMessage.setText("Nombre guardado. ¡Preparando el juego!");
                        lblMessage.setForeground(new Color(50, 150, 50)); // Verde

                        // Notificar al listener que el personaje ha sido creado/nombrado
                        if (characterCreatedListener != null) {
                            characterCreatedListener.actionPerformed(new ActionEvent(PanelCharacterCreation.this, ActionEvent.ACTION_PERFORMED, "characterCreated"));
                        }
                    } else {
                        JOptionPane.showMessageDialog(PanelCharacterCreation.this, "Error al guardar el nombre: " + result.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        lblMessage.setText("Error: " + result.getMessage());
                        lblMessage.setForeground(new Color(200, 50, 50)); // Rojo
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PanelCharacterCreation.this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    lblMessage.setText("Error inesperado.");
                    lblMessage.setForeground(new Color(200, 50, 50)); // Rojo
                }
            }
        }.execute();
    }

    /**
     * Añade un listener que se activará cuando el nombre del personaje sea guardado exitosamente.
     * @param listener El ActionListener a añadir.
     */
    public void addCharacterCreatedListener(ActionListener listener) { // Renombrado para consistencia
        this.characterCreatedListener = listener;
    }
}
