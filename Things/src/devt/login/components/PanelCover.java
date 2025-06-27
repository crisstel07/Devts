
package devt.login.components;
//Dibuja elementos graficos como lineas, rectangulos, imagenes, etc.
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class PanelCover extends javax.swing.JPanel {

    public PanelCover() {
        initComponents();
        setOpaque(false); // indica cuando un componet es trasnparente o no opaco.
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    
    @Override
    protected void paintComponent (Graphics grphcs){
        Graphics2D g2 = (Graphics2D) grphcs;
        GradientPaint gra = new GradientPaint(0,0, new Color(3,91,126), 0, getHeight(), new Color(1,28,64));
        g2.setPaint(gra);
        g2.fillRect(0, 0,getWidth(),getHeight());
        super.paintComponent(grphcs); //Para cambiar el cuerpo de los métodos generados, seleccione Herramientas/Plantillas
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
