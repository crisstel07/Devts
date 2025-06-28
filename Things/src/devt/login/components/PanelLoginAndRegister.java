
package devt.login.components;
//Dibuja elementos graficos como lineas, rectangulos, imagenes, etc.
import java.awt.Color;
import java.awt.GradientPaint; // Pinta el fondo
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;

public class PanelLoginAndRegister extends javax.swing.JPanel {

    private ActionListener event;
    
    public PanelLoginAndRegister() {
        initComponents();
        setOpaque(false); // indica cuando un componet es trasnparente o no opaco.
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton2 = new javax.swing.JButton();

        jButton2.setText("Test Animation");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(155, 155, 155)
                .addComponent(jButton2)
                .addContainerGap(135, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(215, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(62, 62, 62))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (event != null) {
            event.actionPerformed(evt);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    @Override
    // Fondo Semi-Transparenre (Estilo vidrio)
    protected void paintComponent (Graphics grphcs){
        Graphics2D g2 = (Graphics2D) grphcs;
        Color color1 = new Color(1,14,42, 100);
        Color color2 = new Color(1,14,42, 100);
        GradientPaint gra = new GradientPaint(0,0,color1, 0, getHeight(), color2);
        g2.setPaint(gra);
        g2.fillRect(0, 0,getWidth(),getHeight());
        super.paintComponent(grphcs); //Para cambiar el cuerpo de los métodos generados, seleccione Herramientas/Plantillas

    }
     
     public void addEvent(ActionListener event) {
        this.event = event;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    // End of variables declaration//GEN-END:variables
}
