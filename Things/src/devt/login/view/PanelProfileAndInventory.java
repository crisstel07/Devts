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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import devt.login.apiFlask.ApiClient; // Importar ApiClient
import devt.login.apiFlask.ApiClient.ApiResponse;
import java.awt.image.BufferedImage;

// Importar las nuevas clases de paneles de contenido
import devt.login.components.ProfileStatsDisplayPanel;
import devt.login.components.InventoryDisplayPanel;
import devt.login.components.EnemiesDefeatedDisplayPanel;


public class PanelProfileAndInventory extends javax.swing.JPanel {

    // --- Datos del Usuario y Personaje ---
    private JsonObject currentUserData; // Ahora contendrá la foto_perfil_url del usuario
    private JsonObject currentCharacterData;

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
    // --- ID del usuario actual ---
    private Integer userId; // Nuevo: Para manejar la foto de perfil del usuario

    // --- Botón de Volver ---
    private JButton btnBackToMainMenu;

    // Listener para el botón de volver al menú principal
    private ActionListener backToMenuListener;
    // Listener para el botón de cerrar sesión
    private ActionListener logoutActionListener;

    // ¡NUEVO! Instancia de ApiClient
    private final ApiClient apiClient;

    // Ruta base para las imágenes de perfil de usuario (en el sistema de archivos)
    private static final String USER_PROFILE_IMAGES_DIR;
    static {
        // Obtiene el directorio de inicio del usuario y crea una carpeta oculta para la aplicación
        String userHome = System.getProperty("user.home");
        USER_PROFILE_IMAGES_DIR = userHome + File.separator + ".veilwalker" + File.separator + "profile_images" + File.separator;
        // Asegúrate de que el directorio exista al iniciar la aplicación
        new File(USER_PROFILE_IMAGES_DIR).mkdirs();
    }


    public PanelProfileAndInventory(JsonObject userDataFromLogin) {
        // ¡NUEVO! Inicializar la instancia de ApiClient
        this.apiClient = new ApiClient();

        this.currentUserData = userDataFromLogin;
        
        // Obtener el ID del usuario al inicializar
        if (currentUserData != null && currentUserData.has("id") && !currentUserData.get("id").isJsonNull()) {
            this.userId = currentUserData.get("id").getAsInt();
        } else {
            this.userId = null;
        }
        
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(20, 20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
    }

    // Método para actualizar los datos del USUARIO (llamado desde LoginBase)
    public void updateUserData(JsonObject userData) {
        this.currentUserData = userData;
        if (currentUserData != null && currentUserData.has("id") && !currentUserData.get("id").isJsonNull()) {
            this.userId = currentUserData.get("id").getAsInt();
        } else {
            this.userId = null;
        }
        updateProfileUI(); // Refrescar la UI con los nuevos datos del usuario
    }

    // Método para cargar los datos iniciales del PERSONAJE (llamado desde LoginBase/ViewSystem)
    public void loadData(JsonObject characterData) {
        this.currentCharacterData = characterData;
        
        if (characterData != null && characterData.has("id") && !characterData.get("id").isJsonNull()) {
            this.characterId = characterData.get("id").getAsInt();
        } else {
            this.characterId = null;
        }

        updateProfileUI(); // Llama a updateProfileUI para cargar la foto del usuario y datos del personaje

        profileStatsDisplayPanel.loadCharacterData(characterData);
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
        personalDataPanel.setPreferredSize(new Dimension(350, 600));

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
        lblUsername = new JLabel("Usuario: N/A"); 
        lblUsername.setFont(statFont); lblUsername.setForeground(statColor);
        lblUsername.setAlignmentX(Component.CENTER_ALIGNMENT);
        personalDataPanel.add(lblUsername);
        personalDataPanel.add(Box.createVerticalStrut(5));

        lblEmail = new JLabel("Correo: N/A");
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
        btnLogout.addActionListener(e -> {
            if (logoutActionListener != null) {
                logoutActionListener.actionPerformed(e);
            }
        });
        personalDataPanel.add(btnLogout);
        personalDataPanel.add(Box.createVerticalStrut(10));

        btnAddItemDebug = new JButton("Añadir Ítem (Debug)");
        btnAddItemDebug.setBackground(new Color(50, 100, 200));
        btnAddItemDebug.setForeground(Color.WHITE);
        btnAddItemDebug.setFont(new Font("Arial", Font.PLAIN, 14));
        btnAddItemDebug.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAddItemDebug.addActionListener(e -> addDebugItem());
        personalDataPanel.add(btnAddItemDebug);

        add(personalDataPanel, BorderLayout.WEST);

        // --- Panel Derecho: Contenido Dinámico (Perfil/Inventario/Enemigos) ---
        JPanel mainGameContentPanel = new JPanel(new BorderLayout());
        mainGameContentPanel.setOpaque(false);

        // Panel con CardLayout para las diferentes vistas
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

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
        navigationButtonsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

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

        // --- Botón de Volver al Menú Principal en la navegación ---
        btnBackToMainMenu = new JButton("Volver al Menú");
        btnBackToMainMenu.setFont(buttonFont);
        btnBackToMainMenu.setBackground(new Color(100, 50, 150));
        btnBackToMainMenu.setForeground(Color.WHITE);
        btnBackToMainMenu.addActionListener(e -> {
            if (backToMenuListener != null) {
                backToMenuListener.actionPerformed(e);
            }
        });
        navigationButtonsPanel.add(btnBackToMainMenu);


        mainGameContentPanel.add(navigationButtonsPanel, BorderLayout.SOUTH);
        mainGameContentPanel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(new LineBorder(new Color(0, 150, 255), 2), "Contenido del Juego", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 20), new Color(0, 150, 255)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        add(mainGameContentPanel, BorderLayout.CENTER);

    }

    // --- Métodos de Carga y Actualización de Datos ---
    private void updateProfileUI() {
        if (currentUserData != null) {
            lblUsername.setText("Usuario: " + (currentUserData.has("nombre_usuario") && !currentUserData.get("nombre_usuario").isJsonNull() ? currentUserData.get("nombre_usuario").getAsString() : "N/A")); // ¡MODIFICADO!
            lblEmail.setText("Correo: " + (currentUserData.has("correo") && !currentUserData.get("correo").isJsonNull() ? currentUserData.get("correo").getAsString() : "N/A")); // ¡MODIFICADO!
            
            // Obtener la URL de la foto de perfil del USUARIO
            String photoUrl = currentUserData.has("foto_perfil_url") && !currentUserData.get("foto_perfil_url").isJsonNull()
                                     ? currentUserData.get("foto_perfil_url").getAsString() : "/devt/login/images/profile_images/default_user.png"; // Default si no hay
            
            // Cargar la imagen usando el método modificado
            loadImage(photoUrl, lblProfilePicture, 150, 150);
        } else {
            lblUsername.setText("Usuario: N/A");
            lblEmail.setText("Correo: N/A");
            lblProfilePicture.setIcon(null); // Limpiar foto si no hay datos de usuario
        }

        if (currentCharacterData != null) {
            lblCharacterName.setText("Nombre: " + (currentCharacterData.get("nombre_personaje").isJsonNull() ? "Sin Nombre" : currentCharacterData.get("nombre_personaje").getAsString()));
            lblLevel.setText("Nivel: " + (currentCharacterData.has("nivel") && !currentCharacterData.get("nivel").isJsonNull() ? currentCharacterData.get("nivel").getAsInt() : "N/A"));
            lblHealth.setText("Vida: " + (currentCharacterData.has("vida_actual") && !currentCharacterData.get("vida_actual").isJsonNull() ? currentCharacterData.get("vida_actual").getAsInt() : "N/A")); // ¡MODIFICADO!
            lblEnergy.setText("Energía: " + (currentCharacterData.has("energia") && !currentCharacterData.get("energia").isJsonNull() ? currentCharacterData.get("energia").getAsInt() : "N/A")); // ¡MODIFICADO!
            // La foto de perfil ya no se carga desde currentCharacterData
        } else {
            lblCharacterName.setText("Nombre: N/A");
            lblLevel.setText("Nivel: N/A");
            lblHealth.setText("Vida: N/A");
            lblEnergy.setText("Energía: N/A");
        }
        revalidate(); // Asegura que los cambios en los JLabels se reflejen
        repaint(); // Asegura que se redibuje el panel
    }
    
    // --- Lógica de Interacción con el Usuario ---
    private void selectAndUploadPhoto() {
        if (userId == null) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener el ID de usuario para actualizar la foto.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar foto de perfil");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Asegúrate de que el directorio de destino exista
                File destDir = new File(USER_PROFILE_IMAGES_DIR);
                if (!destDir.exists()) {
                    destDir.mkdirs(); // Crea el directorio si no existe
                }

                // Genera un nombre de archivo único para evitar colisiones
                // Ahora usa el userId en el nombre del archivo
                String fileName = "user_" + userId + "_" + System.currentTimeMillis() + getFileExtension(selectedFile);
                File destFile = new File(destDir, fileName);
                
                // Copia el archivo seleccionado al directorio de destino
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // La URL a guardar en la base de datos es la ruta ABSOLUTA del archivo
                String photoUrlToSave = destFile.getAbsolutePath();
                
                updateUserProfilePhoto(photoUrlToSave); // Actualiza la DB y la UI para el USUARIO
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al copiar la imagen: " + ex.getMessage(), "Error de Archivo", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Imprime el stack trace para depuración
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

    // Nuevo método para actualizar la foto de perfil del USUARIO
    private void updateUserProfilePhoto(String newPhotoUrl) {
        if (userId == null) {
            JOptionPane.showMessageDialog(this, "No hay un usuario cargado para actualizar la foto.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Actualizar el JsonObject localmente y la UI inmediatamente
        // Esto es importante para que la UI se refresque con la nueva URL
        currentUserData.addProperty("foto_perfil_url", newPhotoUrl);
        updateProfileUI(); // Vuelve a llamar a updateProfileUI para recargar la imagen desde la nueva URL

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                // Llama al nuevo método del ApiClient para actualizar la foto del usuario
                // ¡MODIFICADO! Usar la instancia de apiClient
                return apiClient.updateUserProfilePicture(userId, newPhotoUrl); 
            }

            @Override
            protected void done() {
                try {
                    ApiResponse response = get();
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Foto de perfil actualizada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error al actualizar foto: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        
        JTextField nameField = new JTextField(currentName);
        
        Object[] message = {
            "Ingresa el nuevo nombre del personaje:",
            nameField
        };
        
        int option = JOptionPane.showConfirmDialog(
            this,
            message,
            "Editar Nombre",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            String newName = nameField.getText();

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
                        // ¡MODIFICADO! Usar la instancia de apiClient
                        return apiClient.updateCharacterProfile(characterId, updateData);
                    }

                    @Override
                    protected void done() {
                        try {
                            ApiResponse response = get();
                            if (response.isSuccess()) {
                                JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Nombre del personaje actualizado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error al actualizar nombre: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
    }

    private void saveCharacterData() {
        if (characterId == null || currentCharacterData == null) {
            JOptionPane.showMessageDialog(this, "No hay datos de personaje para guardar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                // ¡MODIFICADO! Usar la instancia de apiClient
                return apiClient.updateCharacterProfile(characterId, currentCharacterData);
            }

            @Override
            protected void done() {
                try {
                    ApiResponse response = get();
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Progreso del juego guardado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error al guardar progreso: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                    // ¡MODIFICADO! Usar la instancia de apiClient
                    return apiClient.addItemToInventory(characterId, itemId, quantity);
                }

                @Override
                protected void done() {
                    try {
                        ApiResponse response = get();
                        if (response.isSuccess()) {
                            JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "¡Ítem añadido al inventario!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            // Recargar inventario para ver el cambio
                            reloadInventoryFromApi();
                        } else {
                            JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error al añadir ítem: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                // ¡MODIFICADO! Usar la instancia de apiClient
                return apiClient.getCharacterInventory(characterId);
            }
            @Override
            protected void done() {
                try {
                    ApiResponse response = get();
                    if (response.isSuccess()) {
                        JsonArray inventoryArray = response.getDataAsJsonArray(); // Obtener JsonArray directamente
                        if (inventoryArray != null) {
                           inventoryDisplayPanel.loadInventoryData(inventoryArray); // Pasa el JsonArray directamente
                        } else {
                            System.err.println("Error: La respuesta de inventario fue exitosa pero los datos no son un array JSON o son nulos.");
                            JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error al recargar inventario: Datos inesperados.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        System.err.println("Error al recargar inventario: " + response.getMessage());
                        JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error al recargar inventario: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                // ¡MODIFICADO! Usar la instancia de apiClient
                return apiClient.getEnemiesDefeated(characterId);
            }
            @Override
            protected void done() {
                try {
                    ApiResponse response = get();
                    if (response.isSuccess()) {
                        JsonArray enemiesArray = response.getDataAsJsonArray(); // Obtener JsonArray directamente
                        if (enemiesArray != null) {
                            enemiesDefeatedDisplayPanel.loadEnemiesDefeatedData(enemiesArray);
                        } else {
                            System.err.println("Error: La respuesta de enemigos derrotados fue exitosa pero los datos no son un array JSON o son nulos.");
                            JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error al recargar enemigos derrotados: Datos inesperados.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        System.err.println("Error al recargar enemigos derrotados: " + response.getMessage());
                        JOptionPane.showMessageDialog(PanelProfileAndInventory.this, "Error al recargar enemigos derrotados: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            Image img = null;
            // Intenta cargar como recurso interno del JAR (si empieza con '/')
            if (imageUrl.startsWith("/")) {
                URL resourceUrl = getClass().getResource(imageUrl);
                if (resourceUrl != null) {
                    img = ImageIO.read(resourceUrl);
                }
            } else {
                // Si no es un recurso interno, intenta cargar como archivo del sistema de archivos (ruta absoluta)
                File imageFile = new File(imageUrl);
                if (imageFile.exists() && imageFile.isFile()) {
                    img = ImageIO.read(imageFile);
                }
            }

            if (img != null) {
                Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                targetLabel.setIcon(new ImageIcon(scaledImg));
            } else {
                targetLabel.setIcon(null); // No se pudo leer la imagen
                System.err.println("No se pudo cargar la imagen desde: " + imageUrl);
            }
        } catch (IOException e) {
            System.err.println("Error de E/S al cargar imagen " + imageUrl + ": " + e.getMessage());
            targetLabel.setIcon(null); // En caso de error, no mostrar icono
            e.printStackTrace();
        }
    }

    // Método para añadir un listener al botón "Volver al Menú"
    public void addBackToMainMenuListener(ActionListener listener) {
        this.backToMenuListener = listener;
    }

    // Método para añadir un listener al botón "Cerrar Sesión"
    public void addLogoutActionListener(ActionListener listener) {
        this.logoutActionListener = listener;
    }

}

