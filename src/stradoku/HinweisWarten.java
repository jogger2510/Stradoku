/**
 * HinweisWarten ist Teil des Programmes kodelasStradoku
 * 
 * Erzeugt am:                  28.08.2010 23:53
 * Zuletzt geändert am:         09.02.2020 22:35
 * 
 * Copyright (C) Konrad Demmel, 2010 - 2021
 */

package stradoku;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/**
 * Warten-Dialog mit Fortschrittsanzeige
 */
public class HinweisWarten extends JDialog {

    private static final long serialVersionUID = 1L;
    
    /**
     * Konstruktor
     * @param parent Superklasse
     */
    public HinweisWarten(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        setModal(true);
        setLocationRelativeTo(parent); 
    }

    /**
     * Zeigt das Warten-Fenster.
     * @param msg anzuzeigender text 
     */
    public void zeigeHinweis(String msg){
        labelText.setText(msg);
    }
    
    /**
     * Aktualisiert den Fortschritt.
     * @param value  Wert für den Fortschritt zwischen 0 und 100
     */
    public void setFortschritt(int value) {
        SwingUtilities.invokeLater( new Runnable(){
            @Override
            public void run() {
                try{
                    fortschrittsAnzeige.setValue(value);
                }
                catch( NullPointerException ex ){
                    // mache nichts
                }
            }
        });        
    }     

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelText = new javax.swing.JLabel();
        fortschrittsAnzeige = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Hinweis");
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setName("dialogWarten"); // NOI18N
        setResizable(false);

        labelText.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        labelText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelText.setText("<html><center><b>Importiere Stradoku-Aufgaben.<br><br>Bitte solange warten.</b></center></html>");
        labelText.setMaximumSize(new java.awt.Dimension(116, 45));

        fortschrittsAnzeige.setPreferredSize(new java.awt.Dimension(146, 20));
        fortschrittsAnzeige.setRequestFocusEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelText, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(fortschrittsAnzeige, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(19, 19, 19))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(labelText, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(fortschrittsAnzeige, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JProgressBar fortschrittsAnzeige;
    private javax.swing.JLabel labelText;
    // End of variables declaration//GEN-END:variables

}
