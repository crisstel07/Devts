
package devt.login.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InventoryDisplayPanel extends JPanel {

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

    // Declarar el JSplitPane como un campo de la clase
    private JSplitPane mainSplitPane;

    public InventoryDisplayPanel() {
        this.inventorySlotDetails = new HashMap<>();
        // El panel principal usará un BorderLayout para contener el JSplitPane
        setLayout(new BorderLayout()); 
        
        setBackground(new Color(40, 40, 40));
        setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(new LineBorder(new Color(0, 180, 255), 2), "Inventario del Personaje", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 18), new Color(0, 180, 255)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        initComponents();
        clearItemInfo();
    }

    private void initComponents() {
        // Crear el JSplitPane
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerSize(10);
        mainSplitPane.setResizeWeight(0.6); // El inventario ocupa más espacio
        mainSplitPane.setOpaque(false); // Para que el fondo del padre se vea si es necesario

        // --- Panel Izquierdo del SplitPane: Cuadrícula del Inventario ---
        inventoryGridPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        inventoryGridPanel.setBackground(new Color(50, 50, 50));
        inventoryGridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

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
        mainSplitPane.setLeftComponent(inventoryGridPanel); // Añadir al split pane

        // --- Panel Derecho del SplitPane: Detalles del Ítem Seleccionado ---
        itemInfoPanel = new JPanel();
        itemInfoPanel.setLayout(new BorderLayout(10, 10));
        itemInfoPanel.setBackground(new Color(50, 50, 50));
        itemInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(new LineBorder(new Color(255, 215, 0), 2), "Detalles del Ítem", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 16), new Color(255, 215, 0)),
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
        infoItemDescription.setBackground(new Color(50, 50, 50));
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

        mainSplitPane.setRightComponent(itemInfoPanel); // Añadir al split pane

        // Añadir el JSplitPane al panel principal
        add(mainSplitPane, BorderLayout.CENTER);
    }

    public void loadInventoryData(JsonObject inventoryData) {
        for (JLabel slot : itemSlots) {
            slot.setIcon(null);
            slot.setText("");
            slot.setBackground(new Color(60, 60, 60));
        }
        inventorySlotDetails.clear();
        clearItemInfo();

        if (inventoryData != null && inventoryData.has("inventory") && inventoryData.get("inventory").isJsonArray()) {
            JsonArray inventoryArray = inventoryData.getAsJsonArray("inventory");
            for (int i = 0; i < inventoryArray.size(); i++) {
                JsonObject itemEntry = inventoryArray.get(i).getAsJsonObject();
                int slotNumber = itemEntry.get("slot").getAsInt();
                int quantity = itemEntry.get("cantidad").getAsInt();
                JsonObject itemDetails = itemEntry.getAsJsonObject("item_details");

                if (slotNumber >= 1 && slotNumber <= 9) {
                    inventorySlotDetails.put(slotNumber, itemEntry);
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
                    targetSlot.setBackground(new Color(80, 80, 80));
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
