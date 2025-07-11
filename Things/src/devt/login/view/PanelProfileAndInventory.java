package devt.login.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import com.google.gson.JsonObject;
import devt.login.apiFlask.ApiClient;
import devt.login.apiFlask.ApiClient.ApiResponse;
import java.awt.image.BufferedImage;

// Importar las nuevas clases de paneles de contenido
// Asegúrate de que estas clases estén en el mismo paquete (devt.login.view)
// o ajusta la importación si las colocas en otro subpaquete.
import devt.login.components.ProfileStatsDisplayPanel;
import devt.login.components.InventoryDisplayPanel;
import devt.login.components.EnemiesDefeatedDisplayPanel;

public class PanelProfileAndInventory extends javax.swing.JPanel {

    // --- Datos del Usuario y Personaje ---
    private JsonObject currentUserData;
    private JsonObject currentCharacterData;
    // currentInventoryData ya no se mantiene aquí directamente, se pasa al sub-panel
    // private JsonObject currentInventoryData; 

    // --- Componentes del Panel Izquierdo (Perfil Básico) ---
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
    private JButton btnAddItemDebug; // Botón de depuración para añadir ítems

    // --- Componentes del Panel Derecho (Contenido Dinámico) ---
    private JPanel cardPanel; // Panel que usa CardLayout para cambiar vistas
    private CardLayout cardLayout; // Layout para gestionar las vistas
    private ProfileStatsDisplayPanel profileStatsDisplayPanel;
    private InventoryDisplayPanel inventoryDisplayPanel;
    private EnemiesDefeatedDisplayPanel enemiesDefeatedDisplayPanel;

    // --- ID del personaje actual ---
    private Integer characterId;

    public PanelProfileAndInventory(JsonObject userDataFromLogin) {
        this.currentUserData = userDataFromLogin;
        // inventorySlotDetails ya no es necesario aquí, se mueve a InventoryDisplayPanel
        // this.inventorySlotDetails = new HashMap<>(); 
        
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(20, 20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
    }

    // Método para cargar los datos iniciales (llamado desde LoginBase)
    public void loadData(JsonObject characterData, JsonObject inventoryData) {
        this.currentCharacterData = characterData;
        
        if (characterData != null && characterData.has("id")) {
            this.characterId = characterData.get("id").getAsInt();
        }

        // Actualizar la UI del panel de perfil básico
        updateProfileUI();

        // Cargar datos en los paneles de contenido dinámico
        profileStatsDisplayPanel.loadCharacterData(characterData);
        inventoryDisplayPanel.loadInventoryData(inventoryData);
        // enemiesDefeatedDisplayPanel.loadEnemiesDefeatedData(characterId); // Se cargará cuando se muestre el panel
    }

    private void initComponents() {
        // --- Panel Izquierdo: Datos Personales (Perfil Básico) ---
        JPanel personalDataPanel = new JPanel();
        personalDataPanel.setLayout(new BoxLayout(personalDataPanel, BoxLayout.Y_AXIS));
        personalDataPanel.setBackground(new Color(30, 30, 30));
        personalDataPanel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(new LineBorder(new Color(255, 215, 0), 2), "Perfil del Usuario", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 20), new Color(255, 215, 0)),
                new EmptyBorder(20, 20, 20, 20)
        ));
        personalDataPanel.setPreferredSize(new Dimension(350, 600)); // Ancho fijo para el panel izquierdo

        // Foto de perfil
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

        // Nombre del personaje y botón de edición
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

        // Estadísticas básicas del personaje
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

        // Datos del usuario (del login)
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

        // Botones de acción
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

        btnAddItemDebug = new JButton("Añadir Ítem (Debug)"); // Renombrado para claridad
        btnAddItemDebug.setBackground(new Color(50, 100, 200));
        btnAddItemDebug.setForeground(Color.WHITE);
        btnAddItemDebug.setFont(new Font("Arial", Font.PLAIN, 14));
        btnAddItemDebug.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAddItemDebug.addActionListener(e -> addDebugItem());
        personalDataPanel.add(btnAddItemDebug);

        add(personalDataPanel, BorderLayout.WEST); // Añadir el panel izquierdo al BorderLayout principal

        // --- Panel Derecho: Contenido Dinámico (Perfil/Inventario/Enemigos) ---
        JPanel mainGameContentPanel = new JPanel(new BorderLayout());
        mainGameContentPanel.setOpaque(false); // Para que se vea el fondo del PanelProfileAndInventory

        // Panel con CardLayout para las diferentes vistas
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false); // Importante para que los paneles internos manejen su propio fondo

        // Inicializar los paneles de contenido
        profileStatsDisplayPanel = new ProfileStatsDisplayPanel();
        inventoryDisplayPanel = new InventoryDisplayPanel();
        enemiesDefeatedDisplayPanel = new EnemiesDefeatedDisplayPanel();

        // Añadir paneles al CardLayout
        cardPanel.add(profileStatsDisplayPanel, "PROFILE_STATS");
        cardPanel.add(inventoryDisplayPanel, "INVENTORY");
        cardPanel.add(enemiesDefeatedDisplayPanel, "ENEMIES_DEFEATED");

        mainGameContentPanel.add(cardPanel, BorderLayout.CENTER);

        // Panel para los botones de navegación en la parte inferior
        JPanel navigationButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        navigationButtonsPanel.setBackground(new Color(30, 30, 30));
        navigationButtonsPanel.setBorder(new EmptyBorder(10, 0, 0, 0)); // Espacio superior

        JButton btnShowProfileStats = new JButton("Perfil & Progreso");
        JButton btnShowInventory = new JButton("Inventario");
        JButton btnShowEnemies = new JButton("Enemigos Derrotados");

        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        Color buttonBgColor = new Color(70, 70, 70);
        Color buttonFgColor = Color.WHITE;

        btnShowProfileStats.setFont(buttonFont); btnShowProfileStats.setBackground(buttonBgColor); btnShowProfileStats.setForeground(buttonFgColor);
        btnShowInventory.setFont(buttonFont); btnShowInventory.setBackground(buttonBgColor); btnShowInventory.setForeground(buttonFgColor);
        btnShowEnemies.setFont(buttonFont); btnShowEnemies.setBackground(buttonBgColor); btnShowEnemies.setForeground(buttonFgColor);

        // Listeners para cambiar las tarjetas
        btnShowProfileStats.addActionListener(e -> {
            cardLayout.show(cardPanel, "PROFILE_STATS");
            profileStatsDisplayPanel.loadCharacterData(currentCharacterData); // Recargar datos al mostrar
        });
        btnShowInventory.addActionListener(e -> {
            cardLayout.show(cardPanel, "INVENTORY");
            // Recargar inventario al mostrar (se necesita el characterId)
            if (characterId != null) {
                reloadInventoryFromApi(); // Este método ya llama a inventoryDisplayPanel.loadInventoryData
            }
        });
        btnShowEnemies.addActionListener(e -> {
            cardLayout.show(cardPanel, "ENEMIES_DEFEATED");
            // Recargar enemigos derrotados al mostrar
            if (characterId != null) {
                reloadEnemiesDefeatedFromApi(); // Nuevo método para cargar enemigos
            }
        });

        navigationButtonsPanel.add(btnShowProfileStats);
        navigationButtonsPanel.add(btnShowInventory);
        navigationButtonsPanel.add(btnShowEnemies);

        mainGameContentPanel.add(navigationButtonsPanel, BorderLayout.SOUTH); // Botones abajo
        mainGameContentPanel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(new LineBorder(new Color(0, 150, 255), 2), "Contenido del Juego", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 20), new Color(0, 150, 255)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        add(mainGameContentPanel, BorderLayout.CENTER); // Añadir el panel derecho al BorderLayout principal
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

                String photoUrl = "/profile_images/" + fileName; // Ruta relativa para recursos internos
                
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
        
        // Actualizar el JsonObject localmente y la UI inmediatamente
        currentCharacterData.addProperty("foto_perfil_url", newPhotoUrl);
        updateProfileUI(); // Actualiza la imagen en el panel izquierdo

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
        
        String newName = JOptionPane.showInputDialog(this, "Ingresa el nuevo nombre del personaje:", "Editar Nombre", JOptionPane.PLAIN_MESSAGE, null, null, currentName);

        if (newName != null && !newName.trim().isEmpty() && !newName.trim().equals(currentName)) {
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
        } else if (newName != null && newName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del personaje no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveCharacterData() {
        if (characterId == null || currentCharacterData == null) {
            JOptionPane.showMessageDialog(this, "No hay datos de personaje para guardar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                // Envía el JsonObject completo del personaje para actualizar todos los campos
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
                            reloadInventoryFromApi(); // Llama al método para recargar el inventario
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

    // Método para recargar el inventario desde la API y actualizar el sub-panel
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
                        inventoryDisplayPanel.loadInventoryData(response.inventory);
                    } else {
                        System.err.println("Error al recargar inventario: " + response.message);
                        JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error al recargar inventario: " + response.message, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error inesperado al recargar inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    // Nuevo método para recargar enemigos derrotados desde la API y actualizar el sub-panel
    private void reloadEnemiesDefeatedFromApi() {
        if (characterId == null) return;
        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                // Asegúrate de que ApiClient tenga este método
                return ApiClient.getEnemiesDefeated(characterId); 
            }
            @Override
            protected void done() {
                try {
                    ApiResponse response = get();
                    if (response.success && response.enemies_defeated != null) { // Asume que la API devuelve 'enemies_defeated'
                        enemiesDefeatedDisplayPanel.loadEnemiesDefeatedData(response.enemies_defeated);
                    } else {
                        System.err.println("Error al recargar enemigos derrotados: " + response.message);
                        JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error al recargar enemigos derrotados: " + response.message, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error inesperado al recargar enemigos derrotados: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            // Si la URL es una ruta de recurso interno (ej. /profile_images/imagen.png)
            if (imageUrl.startsWith("/")) {
                url = getClass().getResource(imageUrl);
            } else { // Si es una URL externa (http/https) o ruta de archivo absoluta
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

    // Método para que LoginBase/ViewSystem pueda añadir un listener al botón de Logout
    public void addLogoutActionListener(ActionListener listener) {
        btnLogout.addActionListener(listener);
    }
}

    
    

