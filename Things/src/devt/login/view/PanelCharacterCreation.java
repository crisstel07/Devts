package devt.login.view; // ¡IMPORTANTE! Asegúrate de que esta ruta sea la correcta para tu archivo.
                         // Si está en 'devt.login.components', cambia esta línea.

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import com.google.gson.JsonObject;
import devt.login.apiFlask.ApiClient;
import devt.login.apiFlask.ApiClient.ApiResponse;
import java.awt.event.ActionEvent;
import devt.login.components.Message; // Importar tu clase Message

public class PanelCharacterCreation extends JPanel {

    private int userId;
    private JTextField txtCharacterName;
    private JButton btnConfirmName;
    private JLabel lblMessage;
    private JButton btnBackToLogin;

    private ActionListener characterCreatedListener;
    private ActionListener backToLoginListener;

    // ¡NUEVA INTERFAZ! Para que LoginBase pueda implementar cómo mostrar mensajes
    // Esta interfaz permite que PanelCharacterCreation "llame de vuelta" a LoginBase para mostrar un Message.
    public interface MessageDisplayCallback {
        void showMessage(Message.MessageType type, String message);
    }

    private MessageDisplayCallback messageCallback; // Instancia del callback para mostrar mensajes

    // ¡CONSTRUCTOR MODIFICADO! Ahora acepta el callback de mensajes
    public PanelCharacterCreation(int userId, MessageDisplayCallback callback) {
        this.userId = userId;
        this.messageCallback = callback; // Guarda la referencia al método showMessage de LoginBase
        initUI();
    }

    // Constructor alternativo (si se usa, el callback será null, cuidado al llamar showMessage)
    public PanelCharacterCreation(int userId) {
        this(userId, null); // Llama al constructor principal con un callback nulo por defecto
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // ¡NUEVO! Setter para el callback, útil si el panel se reutiliza o se inicializa sin él.
    public void setMessageCallback(MessageDisplayCallback callback) {
        this.messageCallback = callback;
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(25, 25, 25));
        setBorder(new EmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("¡Bienvenido, Aventurero!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(new Color(255, 215, 0));
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridy++;
        lblMessage = new JLabel("Antes de empezar, dale un nombre a tu personaje:");
        lblMessage.setFont(new Font("Arial", Font.PLAIN, 18));
        lblMessage.setForeground(new Color(200, 200, 200));
        add(lblMessage, gbc);

        gbc.gridy++;
        txtCharacterName = new JTextField(20);
        txtCharacterName.setFont(new Font("Arial", Font.PLAIN, 20));
        txtCharacterName.setBackground(new Color(50, 50, 50));
        txtCharacterName.setForeground(Color.WHITE);
        txtCharacterName.setCaretColor(Color.WHITE);
        txtCharacterName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        add(txtCharacterName, gbc);

        gbc.gridy++;
        btnConfirmName = new JButton("Confirmar Nombre");
        btnConfirmName.setFont(new Font("Arial", Font.BOLD, 22));
        btnConfirmName.setBackground(new Color(50, 150, 50));
        btnConfirmName.setForeground(Color.WHITE);
        btnConfirmName.setFocusPainted(false);
        btnConfirmName.addActionListener(e -> createOrUpdateCharacterName());
        add(btnConfirmName, gbc);

        gbc.gridy++;
        btnBackToLogin = new JButton("Volver al Login");
        btnBackToLogin.setFont(new Font("Arial", Font.BOLD, 18));
        btnBackToLogin.setBackground(new Color(150, 50, 50));
        btnBackToLogin.setForeground(Color.WHITE);
        btnBackToLogin.setFocusPainted(false);
        btnBackToLogin.addActionListener(e -> {
            if (backToLoginListener != null) {
                backToLoginListener.actionPerformed(e);
            }
        });
        add(btnBackToLogin, gbc);
    }

    private void createOrUpdateCharacterName() {
        String newName = txtCharacterName.getText().trim();

        if (newName.isEmpty()) {
            // ¡MODIFICADO! Usar el callback para mostrar el mensaje de error
            if (messageCallback != null) {
                messageCallback.showMessage(Message.MessageType.ERROR, "El nombre del personaje no puede estar vacío.");
            }
            return;
        }

        btnConfirmName.setEnabled(false);
        btnBackToLogin.setEnabled(false);
        lblMessage.setText("Procesando...");
        lblMessage.setForeground(new Color(0, 150, 255));

        new SwingWorker<ApiResponse, Void>() {
            @Override
            protected ApiResponse doInBackground() throws Exception {
                ApiResponse getOrCreateResponse = ApiClient.getOrCreateCharacterProfile(userId);
                
                if (!getOrCreateResponse.isSuccess() || getOrCreateResponse.getDataAsJsonObject() == null) {
                    return new ApiResponse(false, "Error al obtener/crear personaje: " + getOrCreateResponse.getMessage(), null, getOrCreateResponse.getErrorCode());
                }

                JsonObject characterData = getOrCreateResponse.getDataAsJsonObject();
                int characterId = characterData.get("id").getAsInt();
                
                String existingName = characterData.has("nombre_personaje") && !characterData.get("nombre_personaje").isJsonNull()
                                             ? characterData.get("nombre_personaje").getAsString() : null;
                
                if (existingName != null && !existingName.equals("None") && !existingName.isEmpty() && !existingName.equals(newName)) {
                    // ¡MODIFICADO! Usar el callback para mostrar el mensaje de conflicto
                    if (messageCallback != null) {
                        messageCallback.showMessage(Message.MessageType.WARNING, "Este personaje ya tiene un nombre asignado: " + existingName);
                    }
                    return new ApiResponse(false, "Este personaje ya tiene un nombre asignado.", null, 409);
                }

                JsonObject updateData = new JsonObject();
                updateData.addProperty("nombre_personaje", newName);
                return ApiClient.updateCharacterProfile(characterId, updateData);
            }

            @Override
            protected void done() {
                btnConfirmName.setEnabled(true);
                btnBackToLogin.setEnabled(true);
                try {
                    ApiResponse result = get();
                    if (result.isSuccess()) {
                        // ¡MODIFICADO! Usar el callback para mostrar el mensaje de éxito
                        if (messageCallback != null) {
                            messageCallback.showMessage(Message.MessageType.SUCCESS, "Nombre '" + newName + "' guardado exitosamente!");
                        }
                        lblMessage.setText("Nombre guardado. ¡Preparando el juego!");
                        lblMessage.setForeground(new Color(50, 150, 50));

                        if (characterCreatedListener != null) {
                            characterCreatedListener.actionPerformed(new ActionEvent(PanelCharacterCreation.this, ActionEvent.ACTION_PERFORMED, "characterCreated"));
                        }
                    } else {
                        // ¡MODIFICADO! Usar el callback para mostrar el mensaje de error
                        if (messageCallback != null) {
                            messageCallback.showMessage(Message.MessageType.ERROR, "Error al guardar el nombre: " + result.getMessage());
                        }
                        lblMessage.setText("Error: " + result.getMessage());
                        lblMessage.setForeground(new Color(200, 50, 50));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    // ¡MODIFICADO! Usar el callback para mostrar el mensaje de error inesperado
                    if (messageCallback != null) {
                        messageCallback.showMessage(Message.MessageType.ERROR, "Error inesperado: " + ex.getMessage());
                    }
                    lblMessage.setText("Error inesperado.");
                    lblMessage.setForeground(new Color(200, 50, 50));
                }
            }
        }.execute();
    }

    public void addCharacterCreatedListener(ActionListener listener) {
        this.characterCreatedListener = listener;
    }

    public void addBackToLoginListener(ActionListener listener) {
        this.backToLoginListener = listener;
    }
}
