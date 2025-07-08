
package devt.login.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import devt.login.apiFlask.ApiClient;
import devt.login.apiFlask.ApiClient.ApiResponse;
import java.awt.image.BufferedImage;

public class PanelProfileAndInventory extends javax.swing.JPanel {
   
     // --- Datos del Usuario y Personaje ---
    private JsonObject currentUserData;
    private JsonObject currentCharacterData;
    private JsonObject currentInventoryData;

    // --- Componentes del Panel de Perfil ---
    private JLabel lblProfilePicture;
    private JLabel lblCharacterName;
    private JLabel lblLevel;
    private JLabel lblHealth;
    private JLabel lblEnergy;
    private JLabel lblUsername;
    private JLabel lblEmail;
    private JButton btnChangePhoto;
    private JButton btnEditName;
    private JButton btnSaveGame;
    private JButton btnLogout;
    private JButton btnAddItemDebug;

    // --- Componentes del Panel de Inventario ---
    private JPanel inventoryGridPanel;
    private JLabel[] itemSlots;
    private Map<Integer, JsonObject> inventorySlotDetails;

    // Componentes del panel de información del ítem
    private JPanel itemInfoPanel;
    private JLabel infoItemIcon;
    private JLabel infoItemName;
    private JTextArea infoItemDescription;
    private JLabel infoItemType;
    private JLabel infoItemEffect;
    private JLabel infoItemCount;

    // --- ID del personaje actual ---
    private Integer characterId;
    
     public PanelProfileAndInventory (JsonObject userDataFromLogin) {
        this.currentUserData = userDataFromLogin;
        this.inventorySlotDetails = new HashMap<>();
        
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(20, 20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        // Cargar datos del personaje y inventario después de que los componentes estén listos
        // Esto se hará en LoginBase al llamar a showProfileScreen()
    }

    // Método para cargar los datos iniciales (llamado desde LoginBase)
    public void loadData(JsonObject characterData, JsonObject inventoryData) {
        this.currentCharacterData = characterData;
        this.currentInventoryData = inventoryData;
        if (characterData != null && characterData.has("id")) {
            this.characterId = characterData.get("id").getAsInt();
        }
        updateProfileUI();
        updateInventoryUI();
    }
    
     private void initComponents() {
        // --- Panel Izquierdo: Datos Personales (Perfil) ---
        JPanel personalDataPanel = new JPanel();
        personalDataPanel.setLayout(new BoxLayout(personalDataPanel, BoxLayout.Y_AXIS));
        personalDataPanel.setBackground(new Color(30, 30, 30));
        personalDataPanel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(new LineBorder(new Color(255, 215, 0), 2), "Perfil del Personaje", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 20), new Color(255, 215, 0)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        personalDataPanel.setPreferredSize(new Dimension(350, 600));

        lblProfilePicture = new JLabel();
        lblProfilePicture.setPreferredSize(new Dimension(150, 150));
        lblProfilePicture.setMinimumSize(new Dimension(150, 150));
        lblProfilePicture.setMaximumSize(new Dimension(150, 150));
        lblProfilePicture.setBorder(new LineBorder(new Color(150, 150, 150), 2));
        lblProfilePicture.setAlignmentX(Component.CENTER_ALIGNMENT);
        personalDataPanel.add(Box.createVerticalStrut(10));
        personalDataPanel.add(lblProfilePicture);
        personalDataPanel.add(Box.createVerticalStrut(10));

        btnChangePhoto = new JButton("Cambiar Foto");
        btnChangePhoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnChangePhoto.setBackground(new Color(70, 70, 70));
        btnChangePhoto.setForeground(Color.WHITE);
        btnChangePhoto.setFont(new Font("Arial", Font.PLAIN, 16));
        btnChangePhoto.addActionListener(e -> selectAndUploadPhoto());
        personalDataPanel.add(btnChangePhoto);
        personalDataPanel.add(Box.createVerticalStrut(20));

        JPanel nameEditPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        nameEditPanel.setOpaque(false);
        lblCharacterName = new JLabel("Nombre: Cargando...");
        lblCharacterName.setFont(new Font("Arial", Font.BOLD, 24));
        lblCharacterName.setForeground(new Color(255, 255, 255));
        nameEditPanel.add(lblCharacterName);
        btnEditName = new JButton("Editar");
        btnEditName.setBackground(new Color(70, 70, 70));
        btnEditName.setForeground(Color.WHITE);
        btnEditName.setFont(new Font("Arial", Font.PLAIN, 14));
        btnEditName.addActionListener(e -> editCharacterName());
        nameEditPanel.add(btnEditName);
        personalDataPanel.add(nameEditPanel);
        personalDataPanel.add(Box.createVerticalStrut(10));

        Font statFont = new Font("Arial", Font.PLAIN, 18);
        Color statColor = new Color(200, 200, 200);

        lblLevel = new JLabel("Nivel: ");
        lblLevel.setFont(statFont); lblLevel.setForeground(statColor);
        lblLevel.setAlignmentX(Component.CENTER_ALIGNMENT);
        personalDataPanel.add(lblLevel);
        personalDataPanel.add(Box.createVerticalStrut(5));

        lblHealth = new JLabel("Vida: ");
        lblHealth.setFont(statFont); lblHealth.setForeground(statColor);
        lblHealth.setAlignmentX(Component.CENTER_ALIGNMENT);
        personalDataPanel.add(lblHealth);
        personalDataPanel.add(Box.createVerticalStrut(5));

        lblEnergy = new JLabel("Energía: ");
        lblEnergy.setFont(statFont); lblEnergy.setForeground(statColor);
        lblEnergy.setAlignmentX(Component.CENTER_ALIGNMENT);
        personalDataPanel.add(lblEnergy);
        personalDataPanel.add(Box.createVerticalStrut(20));

        lblUsername = new JLabel("Usuario: " + currentUserData.get("nombre_usuario").getAsString());
        lblUsername.setFont(statFont); lblUsername.setForeground(statColor);
        lblUsername.setAlignmentX(Component.CENTER_ALIGNMENT);
        personalDataPanel.add(lblUsername);
        personalDataPanel.add(Box.createVerticalStrut(5));

        lblEmail = new JLabel("Correo: " + currentUserData.get("correo").getAsString());
        lblEmail.setFont(statFont); lblEmail.setForeground(statColor);
        lblEmail.setAlignmentX(Component.CENTER_ALIGNMENT);
        personalDataPanel.add(lblEmail);
        personalDataPanel.add(Box.createVerticalStrut(20));

        btnSaveGame = new JButton("Guardar Juego");
        btnSaveGame.setBackground(new Color(50, 150, 50));
        btnSaveGame.setForeground(Color.WHITE);
        btnSaveGame.setFont(new Font("Arial", Font.BOLD, 18));
        btnSaveGame.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSaveGame.addActionListener(e -> saveCharacterData());
        personalDataPanel.add(btnSaveGame);
        personalDataPanel.add(Box.createVerticalStrut(10));

        btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setBackground(new Color(200, 50, 50));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 18));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        personalDataPanel.add(btnLogout);
        personalDataPanel.add(Box.createVerticalStrut(10));

        btnAddItemDebug = new JButton("Añadir Poción Debug");
        btnAddItemDebug.setBackground(new Color(50, 100, 200));
        btnAddItemDebug.setForeground(Color.WHITE);
        btnAddItemDebug.setFont(new Font("Arial", Font.PLAIN, 14));
        btnAddItemDebug.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAddItemDebug.addActionListener(e -> addDebugItem());
        personalDataPanel.add(btnAddItemDebug);

        add(personalDataPanel, BorderLayout.WEST);

        // --- Panel Derecho: Inventario ---
        JSplitPane inventorySplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        inventorySplitPane.setDividerSize(10);
        inventorySplitPane.setResizeWeight(0.6);
        inventorySplitPane.setOpaque(false);
        inventorySplitPane.setBackground(new Color(20, 20, 20));

        inventoryGridPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        inventoryGridPanel.setBackground(new Color(30, 30, 30));
        inventoryGridPanel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(new LineBorder(new Color(255, 215, 0), 2), "Inventario", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 20), new Color(255, 215, 0)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        itemSlots = new JLabel[9];
        for (int i = 0; i < 9; i++) {
            JLabel slot = new JLabel();
            slot.setPreferredSize(new Dimension(80, 80));
            slot.setBorder(new LineBorder(new Color(100, 100, 100), 2));
            slot.setOpaque(true);
            slot.setBackground(new Color(60, 60, 60));
            slot.setHorizontalAlignment(JLabel.CENTER);
            slot.setVerticalAlignment(JLabel.CENTER);
            slot.setCursor(new Cursor(Cursor.HAND_CURSOR));
            final int slotNumber = i + 1;

            slot.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    displayItemInfo(slotNumber);
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    slot.setBorder(new LineBorder(new Color(255, 215, 0), 2));
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    slot.setBorder(new LineBorder(new Color(100, 100, 100), 2));
                }
            });
            itemSlots[i] = slot;
            inventoryGridPanel.add(slot);
        }
        inventorySplitPane.setLeftComponent(inventoryGridPanel);

        itemInfoPanel = new JPanel();
        itemInfoPanel.setLayout(new BorderLayout(10, 10));
        itemInfoPanel.setBackground(new Color(40, 40, 40));
        itemInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(new LineBorder(new Color(255, 215, 0), 2), "Detalles del Ítem", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 20), new Color(255, 215, 0)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel topInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        topInfoPanel.setOpaque(false);
        infoItemIcon = new JLabel();
        infoItemIcon.setPreferredSize(new Dimension(64, 64));
        infoItemIcon.setBorder(new LineBorder(new Color(150, 150, 150), 1));
        topInfoPanel.add(infoItemIcon);
        infoItemName = new JLabel("Selecciona un ítem");
        infoItemName.setFont(new Font("Arial", Font.BOLD, 22));
        infoItemName.setForeground(new Color(255, 215, 0));
        topInfoPanel.add(infoItemName);
        itemInfoPanel.add(topInfoPanel, BorderLayout.NORTH);

        infoItemDescription = new JTextArea("Información del ítem aparecerá aquí.");
        infoItemDescription.setFont(new Font("Arial", Font.PLAIN, 14));
        infoItemDescription.setForeground(new Color(200, 200, 200));
        infoItemDescription.setBackground(new Color(40, 40, 40));
        infoItemDescription.setLineWrap(true);
        infoItemDescription.setWrapStyleWord(true);
        infoItemDescription.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(infoItemDescription);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        itemInfoPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomInfoPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        bottomInfoPanel.setOpaque(false);
        infoItemType = new JLabel("Tipo: ");
        infoItemEffect = new JLabel("Efecto: ");
        infoItemCount = new JLabel("Cantidad: ");
        Font detailFont = new Font("Arial", Font.PLAIN, 16);
        Color detailColor = new Color(200, 200, 200);

        infoItemType.setFont(detailFont); infoItemType.setForeground(detailColor);
        infoItemEffect.setFont(detailFont); infoItemEffect.setForeground(detailColor);
        infoItemCount.setFont(detailFont); infoItemCount.setForeground(detailColor);

        bottomInfoPanel.add(infoItemType);
        bottomInfoPanel.add(infoItemEffect);
        bottomInfoPanel.add(infoItemCount);
        itemInfoPanel.add(bottomInfoPanel, BorderLayout.SOUTH);

        inventorySplitPane.setRightComponent(itemInfoPanel);
        add(inventorySplitPane, BorderLayout.CENTER);
    }

    // --- Métodos de Carga y Actualización de Datos ---

    private void updateProfileUI() {
        if (currentCharacterData != null) {
            lblCharacterName.setText("Nombre: " + currentCharacterData.get("nombre_personaje").getAsString());
            lblLevel.setText("Nivel: " + currentCharacterData.get("nivel").getAsInt());
            lblHealth.setText("Vida: " + currentCharacterData.get("vida_actual").getAsInt());
            lblEnergy.setText("Energía: " + currentCharacterData.get("energia").getAsInt());
            
            String photoUrl = currentCharacterData.has("foto_perfil_url") && !currentCharacterData.get("foto_perfil_url").isJsonNull()
                              ? currentCharacterData.get("foto_perfil_url").getAsString() : "";
            loadImage(photoUrl, lblProfilePicture, 150, 150);
        }
    }

    public void updateInventoryUI() { // Public para que LoginBase pueda llamarlo
        // Limpiar slots antes de actualizar
        for (JLabel slot : itemSlots) {
            slot.setIcon(null);
            slot.setText("");
            slot.setBackground(new Color(60, 60, 60)); // Restablecer color de fondo
        }
        inventorySlotDetails.clear();
        clearItemInfo(); // Limpiar panel de detalles

        if (currentInventoryData != null && currentInventoryData.has("inventory") && currentInventoryData.get("inventory").isJsonArray()) {
            JsonArray inventoryArray = currentInventoryData.getAsJsonArray("inventory");
            for (int i = 0; i < inventoryArray.size(); i++) {
                JsonObject itemEntry = inventoryArray.get(i).getAsJsonObject();
                int slotNumber = itemEntry.get("slot").getAsInt();
                int quantity = itemEntry.get("cantidad").getAsInt();
                JsonObject itemDetails = itemEntry.getAsJsonObject("item_details");

                if (slotNumber >= 1 && slotNumber <= 9) {
                    inventorySlotDetails.put(slotNumber, itemEntry); // Guardar el objeto completo para info
                    JLabel targetSlot = itemSlots[slotNumber - 1];
                    
                    String iconUrl = itemDetails.has("icono_url") && !itemDetails.get("icono_url").isJsonNull()
                                     ? itemDetails.get("icono_url").getAsString() : "";
                    loadImage(iconUrl, targetSlot, 60, 60);

                    if (itemDetails.has("apilable") && itemDetails.get("apilable").getAsBoolean() && quantity > 1) {
                         targetSlot.setText("x" + quantity);
                         targetSlot.setHorizontalTextPosition(JLabel.RIGHT);
                         targetSlot.setVerticalTextPosition(JLabel.BOTTOM);
                         targetSlot.setForeground(Color.WHITE);
                         targetSlot.setFont(new Font("Arial", Font.BOLD, 12));
                    } else {
                        targetSlot.setText("");
                    }
                    targetSlot.setBackground(new Color(80, 80, 80)); // Slot ocupado
                }
            }
        }
    }

    private void displayItemInfo(int slotNumber) {
        JsonObject itemEntry = inventorySlotDetails.get(slotNumber);
        if (itemEntry != null) {
            JsonObject itemDetails = itemEntry.getAsJsonObject("item_details");
            int quantity = itemEntry.get("cantidad").getAsInt();

            infoItemName.setText(itemDetails.get("nombre").getAsString());
            infoItemDescription.setText(itemDetails.get("descripcion").getAsString());
            infoItemType.setText("Tipo: " + itemDetails.get("tipo").getAsString());
            
            String effect = itemDetails.has("efecto") && !itemDetails.get("efecto").isJsonNull() ? itemDetails.get("efecto").getAsString() : "Ninguno";
            String effectValue = itemDetails.has("valor_efecto") && !itemDetails.get("valor_efecto").isJsonNull() ? String.valueOf(itemDetails.get("valor_efecto").getAsInt()) : "";
            infoItemEffect.setText("Efecto: " + effect + (effectValue.isEmpty() ? "" : " (" + effectValue + ")"));
            infoItemCount.setText("Cantidad: " + quantity);
            
            String iconUrl = itemDetails.has("icono_url") && !itemDetails.get("icono_url").isJsonNull()
                             ? itemDetails.get("icono_url").getAsString() : "";
            loadImage(iconUrl, infoItemIcon, 64, 64);

        } else {
            clearItemInfo();
        }
    }

    private void clearItemInfo() {
        infoItemIcon.setIcon(null);
        infoItemName.setText("Selecciona un ítem");
        infoItemDescription.setText("Información del ítem aparecerá aquí.");
        infoItemType.setText("Tipo: ");
        infoItemEffect.setText("Efecto: ");
        infoItemCount.setText("Cantidad: ");
    }

    // --- Lógica de Interacción con el Usuario ---

    private void selectAndUploadPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar foto de perfil");
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                File destDir = new File("src/main/resources/profile_images/");
                if (!destDir.exists()) destDir.mkdirs();

                String fileName = "char_" + characterId + "_" + System.currentTimeMillis() + getFileExtension(selectedFile);
                File destFile = new File(destDir, fileName);
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                String photoUrl = "/profile_images/" + fileName;
                
                updateCharacterPhoto(photoUrl);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al copiar la imagen: " + ex.getMessage(), "Error de Archivo", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return name.substring(lastDotIndex);
    }

    private void updateCharacterPhoto(String newPhotoUrl) {
        if (characterId == null) {
            JOptionPane.showMessageDialog(this, "No hay un personaje cargado para actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        currentCharacterData.addProperty("foto_perfil_url", newPhotoUrl);
        updateProfileUI();

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                JsonObject updateData = new JsonObject();
                updateData.addProperty("foto_perfil_url", newPhotoUrl);
                return ApiClient.updateCharacterProfile(characterId, updateData);
            }

            @Override
            protected void done() {
                try {
                    ApiResponse response = get();
                    if (response.success) {
                        JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Foto de perfil actualizada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error al actualizar foto: " + response.message, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error inesperado al actualizar foto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void editCharacterName() {
        String currentName = currentCharacterData != null && currentCharacterData.has("nombre_personaje") && !currentCharacterData.get("nombre_personaje").isJsonNull()
                             ? currentCharacterData.get("nombre_personaje").getAsString() : "";
        String newName = JOptionPane.showInputDialog(this, "Ingresa el nuevo nombre del personaje:", "Editar Nombre", JOptionPane.PLAIN_MESSAGE, null, null, currentName).toString();

        if (newName != null && !newName.trim().isEmpty() && !newName.equals(currentName)) {
            if (characterId == null) {
                JOptionPane.showMessageDialog(this, "No hay un personaje cargado para actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            currentCharacterData.addProperty("nombre_personaje", newName.trim());
            updateProfileUI();

            new SwingWorker<ApiClient.ApiResponse, Void>() {
                @Override
                protected ApiClient.ApiResponse doInBackground() throws Exception {
                    JsonObject updateData = new JsonObject();
                    updateData.addProperty("nombre_personaje", newName.trim());
                    return ApiClient.updateCharacterProfile(characterId, updateData);
                }

                @Override
                protected void done() {
                    try {
                        ApiResponse response = get();
                        if (response.success) {
                            JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Nombre del personaje actualizado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error al actualizar nombre: " + response.message, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error inesperado al actualizar nombre: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    private void saveCharacterData() {
        if (characterId == null || currentCharacterData == null) {
            JOptionPane.showMessageDialog(this, "No hay datos de personaje para guardar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Aquí, podrías obtener los datos actuales del juego (vida, energía, posición)
        // y actualizarlos en currentCharacterData antes de enviarlos.
        // Por ejemplo: currentCharacterData.addProperty("vida_actual", game.getPlayerHealth());

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                return ApiClient.updateCharacterProfile(characterId, currentCharacterData);
            }

            @Override
            protected void done() {
                try {
                    ApiResponse response = get();
                    if (response.success) {
                        JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Progreso del juego guardado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error al guardar progreso: " + response.message, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error inesperado al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void addDebugItem() {
        if (characterId == null) {
            JOptionPane.showMessageDialog(this, "No hay personaje para añadir ítems.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ID de un ítem de ejemplo (Poción de Vida). Asegúrate de que este ID exista en tu tabla 'Items'
        // ¡CAMBIA ESTO al ID real de una poción en tu DB! (Ej. 1 para 'Poción de Vida Pequeña')
        String itemIdStr = JOptionPane.showInputDialog(this, "Ingresa el ID del ítem a añadir:", "Añadir Ítem", JOptionPane.PLAIN_MESSAGE);
        if (itemIdStr == null || itemIdStr.trim().isEmpty()) return;
        
        try {
            int itemId = Integer.parseInt(itemIdStr.trim());
            int quantity = 1; // Puedes hacer que esto también sea configurable

            new SwingWorker<ApiClient.ApiResponse, Void>() {
                @Override
                protected ApiClient.ApiResponse doInBackground() throws Exception {
                    return ApiClient.addItemToInventory(characterId, itemId, quantity);
                }

                @Override
                protected void done() {
                    try {
                        ApiResponse response = get();
                        if (response.success) {
                            JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "¡Ítem añadido al inventario!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            // Recargar inventario para ver el cambio
                            // Esto requiere volver a llamar a ApiClient.getCharacterInventory
                            // y luego updateInventoryUI.
                            // Para simplificar, puedes recargar todo el perfil y el inventario.
                            // Esto se haría llamando a un método en LoginBase para recargar showProfileScreen()
                            // O, si ProfileAndInventoryPanel maneja su propia recarga:
                            // loadCharacterAndInventoryData(); // Si este panel tuviera ese método público
                            // Por ahora, solo actualiza la UI si la respuesta de Flask te da el inventario actualizado.
                            // La API de Flask para add_item_to_inventory devuelve 'slot', no el inventario completo.
                            // Por lo tanto, necesitamos recargar el inventario completo.
                            reloadInventoryFromApi();

                        } else {
                            JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error al añadir ítem: " + response.message, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error inesperado al añadir ítem: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID de ítem inválido. Debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reloadInventoryFromApi() {
        if (characterId == null) return;
        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                return ApiClient.getCharacterInventory(characterId);
            }
            @Override
            protected void done() {
                try {
                    ApiResponse response = get();
                    if (response.success && response.inventory != null) {
                        currentInventoryData = response.inventory;
                        updateInventoryUI();
                    } else {
                        System.err.println("Error al recargar inventario: " + response.message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    // Método auxiliar para cargar imágenes
    private void loadImage(String imageUrl, JLabel targetLabel, int width, int height) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            targetLabel.setIcon(null);
            return;
        }
        try {
            URL url;
            if (imageUrl.startsWith("/")) { // Si es una ruta de recurso interno
                url = getClass().getResource(imageUrl);
            } else { // Si es una URL externa o ruta de archivo absoluta
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

    // Método para que LoginBase/ViewSystem pueda añadir un listener al botón de Logout
    public void addLogoutActionListener(ActionListener listener) {
        btnLogout.addActionListener(listener);
    }
}
    
    

