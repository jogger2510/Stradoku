/**
 * AbfrageSerieErzeugen.java ist Teil des Programmes kodelasStradoku
 *
 * Erzeugt am:              11.03.2010 18:59
 * Letzte Änderung:         14.01.2021 13:00
 *
 * Copyright (C) Konrad Demmel, 2010 - 2021
 */
package stradoku;

import java.awt.event.*;

/**
 * Stellt Abfrage f+r die Erzeugung einer Stradoku Serie bereit. Abgefragt werden der Level und die
 * Anzahl der Stradokus.
 */
public class AbfrageSerieErzeugen extends javax.swing.JDialog {

   private boolean escaped;
   private final int maxAufgaben;
   public String anzeige;

   /**
    * Konstruktor
    *
    * @param parent Referenz zur aufrufenden Klasse
    * @param max Obergrenze für die Anzahl der zu erzeugenden Aufgaben
    * @param modal bestimmt die Fensterdarstellung
    */
   public AbfrageSerieErzeugen(java.awt.Frame parent, int max, boolean modal) {
      super(parent, modal);
      initComponents();
      setLocationRelativeTo(parent);
      maxAufgaben = max;
      anzeige = "<html>Wie viele Aufgaben mit dem aktuell eingestellten "
            + "Level (maximal " + maxAufgaben + " ) "
            + "sollen erzeugt und in der Liste gespeichert werden?";
      anzeigeLabel.setText(anzeige);
   }

   /**
    * Startet das Dialogfenster.
    *
   * @param level aktueller Level
   * @return Anzahl der zu erstellenden Aufgaben
    */

   public int zeigeDialog(int level) {
    auswahlLevel.setText("" + level);
    eingabeFeld.setText("");
    escaped = false;
    setVisible(true);
    return escaped ? 0 : Util.getNum(eingabeFeld.getText());
 }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        anzeigeLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        eingabeFeld = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        buttonAbbrechen = new javax.swing.JButton();
        buttonOK = new javax.swing.JButton();
        auswahlLevel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Stradoku-Serie generieren");
        setModal(true);
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);
        setResizable(false);

        anzeigeLabel.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        jLabel2.setText("Aktueller Level:");

        eingabeFeld.setFont(new java.awt.Font("DialogInput", 0, 13)); // NOI18N
        eingabeFeld.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        eingabeFeld.setText("0");
        eingabeFeld.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                eingabeFeldKeyPressed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        jLabel3.setText("Anzahl der zu erzeugenden Aufgaben:");

        buttonAbbrechen.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N
        buttonAbbrechen.setText("Abbrechen");
        buttonAbbrechen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAbbrechenActionPerformed(evt);
            }
        });

        buttonOK.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N
        buttonOK.setText("Erzeugen");
        buttonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOKActionPerformed(evt);
            }
        });

        auswahlLevel.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        auswahlLevel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        auswahlLevel.setText("3");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(anzeigeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(buttonAbbrechen))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(auswahlLevel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(buttonOK, javax.swing.GroupLayout.PREFERRED_SIZE, 85, Short.MAX_VALUE)
                            .addComponent(eingabeFeld))))
                .addGap(31, 31, 31))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonAbbrechen, buttonOK});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(anzeigeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(buttonAbbrechen))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(auswahlLevel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(eingabeFeld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(buttonOK)))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {auswahlLevel, jLabel2});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {eingabeFeld, jLabel3});

        pack();
    }// </editor-fold>//GEN-END:initComponents

   /**
    * Reagiert auf OK-Button
    *
    * @param evt nicht verwendet
    */
    private void buttonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOKActionPerformed
       setVisible(false);
    }//GEN-LAST:event_buttonOKActionPerformed

   /**
    * Reagiert auf Abbrechen-Button
    *
    * @param evt nicht verwendet
    */
    private void buttonAbbrechenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAbbrechenActionPerformed
        escaped = true;
        setVisible(false);
    }//GEN-LAST:event_buttonAbbrechenActionPerformed

   /**
    * Reagiert auf die Esc- und Eingabe-Taste
    *
    * @param evt Eingabe-Taste
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
    private javax.swing.JLabel anzeigeLabel;
    private javax.swing.JLabel auswahlLevel;
    private javax.swing.JButton buttonAbbrechen;
    private javax.swing.JButton buttonOK;
    private javax.swing.JTextField eingabeFeld;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}
