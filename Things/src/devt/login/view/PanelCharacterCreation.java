package devt.login.view;

import com.google.gson.JsonObject;
import devt.login.apiFlask.ApiClient; // Importar ApiClient
import devt.login.apiFlask.ApiClient.ApiResponse;
import devt.login.components.Message;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.EventListenerList;

public class PanelCharacterCreation extends JPanel {

    private int userId;
    private JTextField txtCharacterName;
    private JButton btnCreateCharacter;
    private JButton btnBackToLogin;
    private MessageDisplayCallback messageCallback;

    // ¡NUEVO! Instancia de ApiClient
    private final ApiClient apiClient;

    public PanelCharacterCreation(int userId, MessageDisplayCallback messageCallback) {
        // ¡NUEVO! Inicializar la instancia de ApiClient
        this.apiClient = new ApiClient();

        this.userId = userId;
        this.messageCallback = messageCallback;
        initComponents();
    }

    // Setter para actualizar el userId si el panel se reutiliza
    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Setter para actualizar el callback de mensajes
    public void setMessageCallback(MessageDisplayCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(new Color(20, 20, 20));
        setBorder(new EmptyBorder(50, 50, 50, 50));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(30, 30, 30));
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(new LineBorder(new Color(0, 150, 255), 2), "Crear Personaje", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 24), new Color(0, 150, 255)),
                new EmptyBorder(30, 30, 30, 30)
        ));

        JLabel lblTitle = new JLabel("¡Bienvenido, Aventurero!");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 30));
        lblTitle.setForeground(new Color(255, 215, 0));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lblTitle);
        contentPanel.add(Box.createVerticalStrut(20));

        JLabel lblInstructions = new JLabel("Para comenzar tu viaje, dale un nombre a tu personaje:");
        lblInstructions.setFont(new Font("Arial", Font.PLAIN, 18));
        lblInstructions.setForeground(Color.WHITE);
        lblInstructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lblInstructions);
        contentPanel.add(Box.createVerticalStrut(30));

        txtCharacterName = new JTextField(20);
        txtCharacterName.setFont(new Font("Arial", Font.PLAIN, 20));
        txtCharacterName.setMaximumSize(new Dimension(300, 40));
        txtCharacterName.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(txtCharacterName);
        contentPanel.add(Box.createVerticalStrut(30));

        btnCreateCharacter = new JButton("Crear Personaje");
        btnCreateCharacter.setFont(new Font("Arial", Font.BOLD, 22));
        btnCreateCharacter.setBackground(new Color(50, 150, 50));
        btnCreateCharacter.setForeground(Color.WHITE);
        btnCreateCharacter.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCreateCharacter.addActionListener(e -> createCharacter());
        contentPanel.add(btnCreateCharacter);
        contentPanel.add(Box.createVerticalStrut(20));

        btnBackToLogin = new JButton("Volver al Login");
        btnBackToLogin.setFont(new Font("Arial", Font.PLAIN, 16));
        btnBackToLogin.setBackground(new Color(70, 70, 70));
        btnBackToLogin.setForeground(Color.WHITE);
        btnBackToLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Este listener se añade externamente en LoginBase
        contentPanel.add(btnBackToLogin);

        add(contentPanel, new GridBagConstraints());
    }

    private void createCharacter() {
        String characterName = txtCharacterName.getText().trim();

        if (characterName.isEmpty()) {
            if (messageCallback != null) {
                messageCallback.showMessage(Message.MessageType.ERROR, "El nombre del personaje no puede estar vacío.");
            }
            return;
        }

        // Deshabilitar el botón para evitar múltiples clics
        btnCreateCharacter.setEnabled(false);

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                // ¡MODIFICADO! Llamar a getOrCreateCharacterProfile en lugar de createCharacter
                // Este endpoint en Flask ya crea el personaje si no existe.
                // Asegúrate de que tu Flask maneje la creación con user_id y nombre_personaje
                // si el personaje no existe.
                return apiClient.getOrCreateCharacterProfile(userId); // ¡MODIFICADO! Usar la instancia de apiClient
            }

            @Override
            protected void done() {
                btnCreateCharacter.setEnabled(true); // Habilitar el botón de nuevo
                try {
                    ApiResponse response = get();
                    if (response.isSuccess()) {
                        JsonObject characterData = response.getDataAsJsonObject();
                        if (characterData != null) {
                            // Si el personaje se creó con éxito, ahora lo actualizamos con el nombre
                            updateCharacterNameOnServer(characterData.get("id").getAsInt(), characterName);
                        } else {
                            if (messageCallback != null) {
                                messageCallback.showMessage(Message.MessageType.ERROR, "Error: Datos de personaje nulos después de la creación.");
                            }
                        }
                    } else {
                        if (messageCallback != null) {
                            messageCallback.showMessage(Message.MessageType.ERROR, "Error al crear personaje: " + response.getMessage());
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (messageCallback != null) {
                        messageCallback.showMessage(Message.MessageType.ERROR, "Error inesperado al crear personaje: " + ex.getMessage());
                    }
                }
            }
        }.execute();
    }

    private void updateCharacterNameOnServer(int characterId, String newName) {
        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                JsonObject updateData = new JsonObject();
                updateData.addProperty("nombre_personaje", newName);
                // ¡MODIFICADO! Usar la instancia de apiClient
                return apiClient.updateCharacterProfile(characterId, updateData);
            }

            @Override
            protected void done() {
                try {
                    ApiResponse response = get();
                    if (response.isSuccess()) {
                        if (messageCallback != null) {
                            messageCallback.showMessage(Message.MessageType.SUCCESS, "¡Personaje '" + newName + "' creado y nombre actualizado!");
                        }
                        // Notificar a LoginBase que el personaje fue creado/actualizado
                        fireCharacterCreatedEvent();
                    } else {
                        if (messageCallback != null) {
                            messageCallback.showMessage(Message.MessageType.ERROR, "Error al actualizar nombre del personaje: " + response.getMessage());
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (messageCallback != null) {
                        messageCallback.showMessage(Message.MessageType.ERROR, "Error inesperado al actualizar nombre: " + ex.getMessage());
                    }
                }
            }
        }.execute();
    }


    // --- Callbacks y Listeners ---
    public interface MessageDisplayCallback {
        void showMessage(Message.MessageType type, String message);
    }

    private final EventListenerList listenerList = new EventListenerList();
    private static final String CHARACTER_CREATED = "characterCreated";
    private static final String BACK_TO_LOGIN = "backToLogin";

    public void addCharacterCreatedListener(ActionListener listener) {
        listenerList.add(ActionListener.class, listener);
    }

    public void addBackToLoginListener(ActionListener listener) {
        btnBackToLogin.addActionListener(listener); // Adjuntar directamente al botón
    }

    protected void fireCharacterCreatedEvent() {
        ActionListener[] listeners = listenerList.getListeners(ActionListener.class);
        for (ActionListener listener : listeners) {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, CHARACTER_CREATED));
        }
    }
}

