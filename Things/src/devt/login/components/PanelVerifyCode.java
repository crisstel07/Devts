
package devt.login.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;


public class PanelVerifyCode extends javax.swing.JPanel {

    public PanelVerifyCode() {
        initComponents();
        setOpaque(false);
        setFocusCycleRoot(true);
        super.setVisible(false);
        addMouseListener(new MouseAdapter() {
        });  
    }
  
    @Override
    public void setVisible(boolean bln) {
        super.setVisible(bln);
        if (bln) {
            txtCode.grabFocus();
            txtCode.setText("");
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelRound1 = new devt.login.swing.PanelRound();
        panelRound2 = new devt.login.swing.PanelRound();
        panelRound3 = new devt.login.swing.PanelRound();
        panelRound4 = new devt.login.swing.PanelRound();
        panelRound5 = new devt.login.swing.PanelRound();
        panelRound6 = new devt.login.swing.PanelRound();
        panelRound7 = new devt.login.swing.PanelRound();
        panelRound8 = new devt.login.swing.PanelRound();
        panelRound9 = new devt.login.swing.PanelRound();
        panelRound10 = new devt.login.swing.PanelRound();
        panelRound11 = new devt.login.swing.PanelRound();
        panelRound12 = new devt.login.swing.PanelRound();
        panelRound13 = new devt.login.swing.PanelRound();
        panelRound14 = new devt.login.swing.PanelRound();
        panelRound15 = new devt.login.swing.PanelRound();
        txtCode = new devt.login.swing.MyTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cmdCancel = new devt.login.swing.ButtonOutLine();
        cmdOK = new devt.login.swing.ButtonOutLine();

        panelRound15.setToolTipText("");

        txtCode.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodeActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Verify Code");

        jLabel2.setText("Revisa tu mail para obtener el codigo de verificaciòn");

        cmdCancel.setBackground(javax.swing.UIManager.getDefaults().getColor("InternalFrame.closePressedBackground"));
        cmdCancel.setText("Cancel");
        cmdCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCancelActionPerformed(evt);
            }
        });

        cmdOK.setBackground(javax.swing.UIManager.getDefaults().getColor("Actions.Green"));
        cmdOK.setText("OK");

        javax.swing.GroupLayout panelRound15Layout = new javax.swing.GroupLayout(panelRound15);
        panelRound15.setLayout(panelRound15Layout);
        panelRound15Layout.setHorizontalGroup(
            panelRound15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound15Layout.createSequentialGroup()
                .addGap(90, 90, 90)
                .addGroup(panelRound15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCode, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(90, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound15Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cmdOK, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cmdCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(120, 120, 120))
        );
        panelRound15Layout.setVerticalGroup(
            panelRound15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound15Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelRound15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdOK, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(panelRound15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(42, Short.MAX_VALUE)
                .addComponent(panelRound15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodeActionPerformed

    private void cmdCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCancelActionPerformed
    setVisible(false);
    }//GEN-LAST:event_cmdCancelActionPerformed

    @Override
    protected void paintComponent(Graphics grphcs) {
         Graphics2D g2 = (Graphics2D) grphcs;
        g2.setColor(new Color(50, 50, 50 ));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setComposite(AlphaComposite.SrcOver);
        super.paintComponent(grphcs); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }
    public String getInputCode() {
        return txtCode.getText().trim();
    }

    public void addEventButtonOK(ActionListener event) {
        cmdOK.addActionListener(event);
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private devt.login.swing.ButtonOutLine cmdCancel;
    private devt.login.swing.ButtonOutLine cmdOK;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private devt.login.swing.PanelRound panelRound1;
    private devt.login.swing.PanelRound panelRound10;
    private devt.login.swing.PanelRound panelRound11;
    private devt.login.swing.PanelRound panelRound12;
    private devt.login.swing.PanelRound panelRound13;
    private devt.login.swing.PanelRound panelRound14;
    private devt.login.swing.PanelRound panelRound15;
    private devt.login.swing.PanelRound panelRound2;
    private devt.login.swing.PanelRound panelRound3;
    private devt.login.swing.PanelRound panelRound4;
    private devt.login.swing.PanelRound panelRound5;
    private devt.login.swing.PanelRound panelRound6;
    private devt.login.swing.PanelRound panelRound7;
    private devt.login.swing.PanelRound panelRound8;
    private devt.login.swing.PanelRound panelRound9;
    private devt.login.swing.MyTextField txtCode;
    // End of variables declaration//GEN-END:variables
}
