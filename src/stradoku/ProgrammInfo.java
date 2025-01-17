/*
 * ProgrammInfo.java ist Teil des Programmes kodelasStradoku
 * Erzeugt am:          15.01.2011 22:16
 * Umgearbeitet am:     03.07.2017 23:00
 * Letzte Änderung:     19.01.2020 00:05
 * 
 * Copyright (C) Konrad Demmel, 2017 - 2020
*/

package stradoku;

/**
 * Zeigt Programminfo
 */
public class ProgrammInfo extends javax.swing.JDialog {

    private final Stradoku mainFrame;

    /** 
     * Konstruktor
     * @param mf Reverenz auf Hauptklasse StradokuAPP
     */
    public ProgrammInfo(Stradoku mf) {
        super(mf, true);
        mainFrame = mf;
        initComponents();
        labelVersion.setText("Version " + mainFrame.getVersion());
        setIconImage(mainFrame.getIconImage());
        setLocationRelativeTo(mainFrame); 
   }

    /**
     * Zeigt das Dialogfenster
     */
    public void zeigeDialog() {
        setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelTitel = new javax.swing.JLabel();
        buttonBeenden = new javax.swing.JButton();
        labelCopyright = new javax.swing.JLabel();
        labelLizenzHinweis = new javax.swing.JLabel();
        labelVersion = new javax.swing.JLabel();

        setTitle("Info zu diesem Programm");
        setModal(true);
        setName(""); // NOI18N
        setResizable(false);

        labelTitel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        labelTitel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        buttonBeenden.setText("Beenden");
        buttonBeenden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBeendenActionPerformed(evt);
            }
        });

        labelCopyright.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelCopyright.setText("<html><p align=center>Copyright © 2017-2020 by Konrad Demmel</p></html>");

        labelLizenzHinweis.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelLizenzHinweis.setText("<html> <center>Dieses Programm ist freie Software. Sie können es unter \nden Bedingungen der GNU General Public License Version 3, \nwie von der Free Software Foundation veröffentlicht, \nverwenden, weitergeben und/oder modifizieren.</center></html>");

        labelVersion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelVersion.setText("Version 1.1");
        labelVersion.setToolTipText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelTitel, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                    .addComponent(labelVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelCopyright, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelLizenzHinweis, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonBeenden))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(labelTitel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelCopyright, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelLizenzHinweis, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addComponent(buttonBeenden)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * Reagiert auf Beenden Button und schließt den Diaoog
     * @param evt Aktionsereignis
     */
    private void buttonBeendenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBeendenActionPerformed
        setVisible(false);
    }//GEN-LAST:event_buttonBeendenActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonBeenden;
    private javax.swing.JLabel labelCopyright;
    private javax.swing.JLabel labelLizenzHinweis;
    private javax.swing.JLabel labelTitel;
    private javax.swing.JLabel labelVersion;
    // End of variables declaration//GEN-END:variables
}
