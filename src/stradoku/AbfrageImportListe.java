/**
 * AbfrageImportListe.java ist Teil des Programmes kodelasStradoku
 *
 * Erzeugt am:              14.01.2018 11:32
 * Zuletzt geändert am:     05.03.2020 10:30
 *
 * Copyright (C) Konrad Demmel, 2018 - 2020
 */
package stradoku;

/**
 * Erzeugt einen Abfrage-Dialog für den Import einer Stzr8ts-Liste
 */
public class AbfrageImportListe extends javax.swing.JDialog {

   private int ergebnis;       // Abfragestatus

   /**
    * Konstruktor
    *
    * @param parent Referenz auf Superklasse JDialog
    * @param modal Flag für modale Ausführung
    */
   public AbfrageImportListe(java.awt.Frame parent, boolean modal) {
      super(parent, modal);
      initComponents();
      setLocationRelativeTo(parent);
   }

   /**
    * Zeigt den Dialog.
    *
    * @return Abfragestatus
    */
   public int zeigeDialog() {
      ergebnis = 0;
      setVisible(true);
      return ergebnis;
   }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        buttonAbbrechen = new javax.swing.JButton();
        buttonHinzufuegen = new javax.swing.JButton();
        buttonLoeschen = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Externe Stradoku-Liste importieren");
        setModal(true);
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel1.setText("<html>Sollen die Stradokus der zu importierende Liste der aktuellen Liste hinzugefügt oder soll die aktuelle Liste gelöscht werden?");

        buttonAbbrechen.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N
        buttonAbbrechen.setText("Abbrechen");
        buttonAbbrechen.setMaximumSize(new java.awt.Dimension(87, 23));
        buttonAbbrechen.setMinimumSize(new java.awt.Dimension(87, 23));
        buttonAbbrechen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAbbrechenActionPerformed(evt);
            }
        });

        buttonHinzufuegen.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N
        buttonHinzufuegen.setText("Hinzufügen");
        buttonHinzufuegen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonHinzufuegenActionPerformed(evt);
            }
        });

        buttonLoeschen.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N
        buttonLoeschen.setText("Löschen");
        buttonLoeschen.setMaximumSize(new java.awt.Dimension(87, 23));
        buttonLoeschen.setMinimumSize(new java.awt.Dimension(87, 23));
        buttonLoeschen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLoeschenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonAbbrechen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addComponent(buttonHinzufuegen)
                        .addGap(9, 9, 9)
                        .addComponent(buttonLoeschen, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonAbbrechen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonHinzufuegen)
                    .addComponent(buttonLoeschen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

   /**
    * Reagiert auf Hinzufügen-Button
    *
    * @param evt nicht verwendet
    */
    private void buttonHinzufuegenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonHinzufuegenActionPerformed
       ergebnis = 1;
       setVisible(false);
    }//GEN-LAST:event_buttonHinzufuegenActionPerformed

   /**
    * Reagiert auf Abbrechen-Button
    *
    * @param evt nicht verwendet
    */
    private void buttonAbbrechenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAbbrechenActionPerformed
       ergebnis = 0;
       setVisible(false);
    }//GEN-LAST:event_buttonAbbrechenActionPerformed

   /**
    * Reagiert auf Löschen-Button
    *
    * @param evt nicht verwendet
    */
    private void buttonLoeschenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLoeschenActionPerformed
       ergebnis = 2;
       setVisible(false);
    }//GEN-LAST:event_buttonLoeschenActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAbbrechen;
    private javax.swing.JButton buttonHinzufuegen;
    private javax.swing.JButton buttonLoeschen;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
