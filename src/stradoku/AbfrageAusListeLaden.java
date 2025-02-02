/**
 * AbfrageAusListeLaden.java ist Teil des Programmes kodelasStradoku
 *
 * Erzeugt am:          11.03.2010 18:59
 * Zuletzt geändert:    05.03.2020 10:30
 *
 * Copyright (C) Konrad Demmel, 2010 - 2024
 */
package stradoku;

import java.awt.event.*;

/**
 * Erzeugt Dialog für die Abfrage, wenn ein Stradoku aus der internen Liste für das Stradoku-Feld
 * übernommen werden soll.
 */
public class AbfrageAusListeLaden extends javax.swing.JDialog {

    private boolean escaped;

   /**
    * Konstruktor
    *
    * @param mainFrame Referenz auf Hauptklasse
    * @param modal Flag für die Anzeige des Dialogfensters, true wenn modal
    */
   public AbfrageAusListeLaden(Stradoku mainFrame, boolean modal) {
      super(mainFrame, modal);
      initComponents();
      setIconImage(mainFrame.getIconImage());
      setLocationRelativeTo(mainFrame);
   }

   /**
    * Zeigt Dialog für die Abfrage, welches Stradoku aus der Liste übernommen werden soll.
    *
    * @param anzahl Anzahl der Stradoku in der Liste übergeben..
    * @return die Listenposition für das gewünschte Stradoku
    */
   public int zeigeDialog(int anzahl) {
      labelVorhanden.setText("" + anzahl);
      eingabeFeld.setText("");
      escaped = false;
      setVisible(true);
      return escaped ? 0 : Util.getNum(eingabeFeld.getText());
   }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel_Auftrag = new javax.swing.JLabel();
        jLabel_FrageAnzahl = new javax.swing.JLabel();
        eingabeFeld = new javax.swing.JTextField();
        jLabel_FrageNummer = new javax.swing.JLabel();
        buttonAbbrechen = new javax.swing.JButton();
        buttonOK = new javax.swing.JButton();
        labelVorhanden = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Aufgabe aus Liste übernehmen");
        setModal(true);
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);
        setResizable(false);

        jLabel_Auftrag.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel_Auftrag.setText("<html> Geben Sie die Nummer der Aufgabe ein, die aus der Liste übernommen werden soll.");

        jLabel_FrageAnzahl.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        jLabel_FrageAnzahl.setText("Anzahl der Aufgaben in der Liste:");

        eingabeFeld.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        eingabeFeld.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        eingabeFeld.setText("0");
        eingabeFeld.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                eingabeFeldKeyPressed(evt);
            }
        });

        jLabel_FrageNummer.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        jLabel_FrageNummer.setText("Nummer der Aufgabe:");

        buttonAbbrechen.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        buttonAbbrechen.setText("Abbrechen");
        buttonAbbrechen.setSelected(true);
        buttonAbbrechen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAbbrechenActionPerformed(evt);
            }
        });

        buttonOK.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        buttonOK.setText("Übernehmen");
        buttonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOKActionPerformed(evt);
            }
        });

        labelVorhanden.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        labelVorhanden.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelVorhanden.setText("55");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel_Auftrag, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel_FrageNummer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel_FrageAnzahl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelVorhanden, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(buttonAbbrechen)
                                .addGap(63, 63, 63)
                                .addComponent(buttonOK))
                            .addComponent(eingabeFeld, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonAbbrechen, buttonOK});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel_Auftrag, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel_FrageAnzahl, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel_FrageNummer))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelVorhanden)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(eingabeFeld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonAbbrechen)
                    .addComponent(buttonOK))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel_FrageAnzahl, labelVorhanden});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {eingabeFeld, jLabel_FrageNummer});

        pack();
    }// </editor-fold>//GEN-END:initComponents

   /**
    * Behandlung wenn Ok-Button gedrückt wurde.
    *
    * @param evt nicht ausgewertet
    */
    private void buttonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOKActionPerformed
       setVisible(false);
    }//GEN-LAST:event_buttonOKActionPerformed

   /**
    * Behandlung wenn Abbrechen-Button gedrückt wurde.
    *
    * @param evt nicht ausgewertet
    */
    private void buttonAbbrechenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAbbrechenActionPerformed
        escaped = true;
       setVisible(false);
    }//GEN-LAST:event_buttonAbbrechenActionPerformed

   /**
    * Behandelt die Eingabe über Esc- und Enter-Tasten.
    *
    * @param evt KeyCode der beiden zu behandelnden Tasten
    */
    private void eingabeFeldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_eingabeFeldKeyPressed
       int taste = evt.getKeyCode();
       switch (taste) {
          case KeyEvent.VK_ENTER:
             setVisible(false);
             break;
          case KeyEvent.VK_ESCAPE:
          escaped = true;
          setVisible(false);
       }
    }//GEN-LAST:event_eingabeFeldKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAbbrechen;
    private javax.swing.JButton buttonOK;
    private javax.swing.JTextField eingabeFeld;
    private javax.swing.JLabel jLabel_Auftrag;
    private javax.swing.JLabel jLabel_FrageAnzahl;
    private javax.swing.JLabel jLabel_FrageNummer;
    private javax.swing.JLabel labelVorhanden;
    // End of variables declaration//GEN-END:variables
}
