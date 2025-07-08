
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

        private JTextField txtCharacterName;
        private JButton btnSaveNickname;
        private int characterId;
        private ActionListener characterNameSavedListener;

        public PanelCharacterCreation(int characterId) {
            this.characterId = characterId;
            setLayout(new GridBagLayout());
            setBackground(new Color(10, 10, 10));
            setBorder(new EmptyBorder(50, 50, 50, 50));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel titleLabel = new JLabel("¡Bienvenido, Aventurero!");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
            titleLabel.setForeground(new Color(255, 215, 0));
            gbc.gridwidth = 2;
            add(titleLabel, gbc);

            gbc.gridy++;
            JLabel instructionLabel = new JLabel("Antes de empezar, dale un nombre a tu personaje:");
            instructionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            instructionLabel.setForeground(new Color(200, 200, 200));
            add(instructionLabel, gbc);

            gbc.gridy++;
            txtCharacterName = new JTextField(20);
            txtCharacterName.setFont(new Font("Arial", Font.PLAIN, 18));
            txtCharacterName.setBackground(new Color(50, 50, 50));
            txtCharacterName.setForeground(Color.WHITE);
            txtCharacterName.setCaretColor(Color.WHITE);
            txtCharacterName.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
            add(txtCharacterName, gbc);

            gbc.gridy++;
            btnSaveNickname = new JButton("Guardar Apodo y Jugar");
            btnSaveNickname.setFont(new Font("Arial", Font.BOLD, 20));
            btnSaveNickname.setBackground(new Color(50, 150, 50));
            btnSaveNickname.setForeground(Color.WHITE);
            btnSaveNickname.setFocusPainted(false);
            btnSaveNickname.addActionListener(e -> saveCharacterName());
            add(btnSaveNickname, gbc);
        }

        private void saveCharacterName() {
            String name = txtCharacterName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre del personaje no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            new SwingWorker<ApiClient.ApiResponse, Void>() {
                @Override
                protected ApiClient.ApiResponse doInBackground() throws Exception {
                    JsonObject updateData = new JsonObject();
                    updateData.addProperty("nombre_personaje", name);
                    return ApiClient.updateCharacterProfile(characterId, updateData);
                }

                @Override
                protected void done() {
                    try {
                        ApiResponse response = get();
                        if (response.success) {
                            if (characterNameSavedListener != null) {
                                characterNameSavedListener.actionPerformed(new ActionEvent(PanelCharacterCreation.this, ActionEvent.ACTION_PERFORMED, "nameSaved"));
                            }
                        } else {
                            JOptionPane.showMessageDialog(PanelCharacterCreation.this, "Error al guardar el nombre: " + response.message, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(PanelCharacterCreation.this, "Error inesperado al guardar el nombre: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }

        public void addCharacterNameSavedListener(ActionListener listener) {
            this.characterNameSavedListener = listener;
        }
    }
    