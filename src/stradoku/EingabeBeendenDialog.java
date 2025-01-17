/**
 * EingabeBeendenDialog.java ist Teil des Programmes kodelasStradoku
 * 
 * Erzeugt am:          11.03.2010 18:59
 * Zuletzt geändert:    05.03.2020 10:30
 * 
 * Copyright (C) Konrad Demmel, 2010 - 2021
*/

package stradoku;

/**
 * Zeigt einen Dialog für die Beendigung des Bearbeitungsmodus.
 */
public class EingabeBeendenDialog extends javax.swing.JDialog {

    public final int UBERARBEITEN = 1;
    public final int UEBERNAHME = 2;
    public final int ABBRUCH = 3;
    private int ergebnis;

    /** Konstruktor erzeugt einen neuen SdkEingabeBeendenFehlerDialog
     * @param parent - Referenz auf Hauptklasse StradokuApp
     */
    public EingabeBeendenDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        setLocationRelativeTo(parent); 
    }

    /**
     * Erzeugt einen Dialog für die Beendigung des Bearbeitungsmodus.
     * 
     * @param begruendung - anzuzeigender Text
     * @param mod - Flag für Anzeigemuster
     * @return - Abfrageergebnis
     */
    public int zeigeDialog(String begruendung, boolean mod){
        if (mod) {
            frage_Lbl.setText("<html>Soll diese Aufgabe noch einmal " +
                    "überarbeitet, übernommen oder die Eingabe " +
                    "abgebrochen werden?");
            ueberschrift_Lbl.setText("Gültige Eingabe");
        }
        else {
            frage_Lbl.setText("<html>Soll diese Aufgabe noch einmal " +
                    "überarbeitet oder die Eingabe abgebrochen werden?");
            ueberschrift_Lbl.setText("Ungültige Eingabe");
        }
        begruendung_Lbl.setText(begruendung);
        uebernehmenButton.setVisible(mod);
        ergebnis = ABBRUCH;
        setVisible(true);
        return ergebnis;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        ueberarbeitenButton = new javax.swing.JButton();
        beendenButton = new javax.swing.JButton();
        ueberschrift_Lbl = new javax.swing.JLabel();
        begruendung_Lbl = new javax.swing.JLabel();
        frage_Lbl = new javax.swing.JLabel();
        uebernehmenButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bearbeitungsmodus verlassen");
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);
        setName("dialogEingabefehler"); // NOI18N
        setResizable(false);

        ueberarbeitenButton.setText("Überarbeiten");
        ueberarbeitenButton.setActionCommand("1");
        buttonGroup.add(ueberarbeitenButton);
        ueberarbeitenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonGroupAuswertung(evt);
            }
        });

        beendenButton.setText("Abbrechen");
        beendenButton.setActionCommand("3");
        buttonGroup.add(beendenButton);
        beendenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonGroupAuswertung(evt);
            }
        });

        ueberschrift_Lbl.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        ueberschrift_Lbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ueberschrift_Lbl.setText("Eingabe beendet");

        begruendung_Lbl.setText("<html>");
        begruendung_Lbl.setMaximumSize(new java.awt.Dimension(380, 28));
        begruendung_Lbl.setMinimumSize(new java.awt.Dimension(380, 28));
        begruendung_Lbl.setPreferredSize(new java.awt.Dimension(380, 28));

        frage_Lbl.setText("<html>Soll diese Aufgabe noch einmal überarbeitet, übernommen oder die Eingabe abgebrochen werden?");
        frage_Lbl.setMaximumSize(new java.awt.Dimension(380, 28));
        frage_Lbl.setPreferredSize(new java.awt.Dimension(380, 28));

        uebernehmenButton.setText("Übernehmen");
        uebernehmenButton.setActionCommand("2");
        buttonGroup.add(uebernehmenButton);
        uebernehmenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonGroupAuswertung(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(ueberschrift_Lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(ueberarbeitenButton)
                                .addGap(7, 7, 7)
                                .addComponent(uebernehmenButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(beendenButton)))
                        .addGap(55, 55, 55))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(frage_Lbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(begruendung_Lbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {beendenButton, ueberarbeitenButton, uebernehmenButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(ueberschrift_Lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(begruendung_Lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(frage_Lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(beendenButton)
                    .addComponent(uebernehmenButton)
                    .addComponent(ueberarbeitenButton))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {beendenButton, ueberarbeitenButton, uebernehmenButton});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Wertet die Eingabe aus und übergibt das Ergebnis.
     * @param evt Ergebnis der Eingabe durch Gruppenauswertung
     */
    private void ButtonGroupAuswertung(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonGroupAuswertung
        ergebnis = (Integer.parseInt(evt.getActionCommand()));
        setVisible(false);
    }//GEN-LAST:event_ButtonGroupAuswertung

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton beendenButton;
    private javax.swing.JLabel begruendung_Lbl;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JLabel frage_Lbl;
    private javax.swing.JButton ueberarbeitenButton;
    private javax.swing.JButton uebernehmenButton;
    private javax.swing.JLabel ueberschrift_Lbl;
    // End of variables declaration//GEN-END:variables
}
