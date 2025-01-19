/**
 * UpdateDialog.java ist Teil des Programmes kodelasStradoku
 * Erzeugt am:                  01.12.2011 18:43
 * Letzte Änderung:             05.08.2023 13:35
 * 
 * Copyright (C) Konrad Demmel, 2010 - 2023
*/

package stradoku;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

/**
 * Gibt den Update-Dialog aus, wenn es ein Update gibt.
 */
public class UpdateDialog extends JDialog {

    private HtmlLabel labelTxt;
    private JButton okButton;
    private final Stradoku mainFrame;
    private final String version;

    /** Konstruktor
     * @param parent Referenz auf Hauptklasse StradokuApp
     * @param vers aktuelle Programmversion
     */
    public UpdateDialog(Stradoku parent, String vers) {
        super(parent, false);
        version = vers;
        mainFrame = parent;
        initComponents();
    }

    /**
     * Zeigt den Dialog.
     */
    public void zeigeDialog() {
        Point pos = mainFrame.getLocation();
        pos.x += (mainFrame.getWidth() / 2) - (getWidth() / 2);
        pos.y += (mainFrame.getHeight() / 2) - (getHeight() / 2);
        setIconImage(mainFrame.getIconImage());
        setLocation(pos);
        setVisible(true);
    }

    /**
     * Dialog beenden
     * @param evt Ok-Button wurde gedrückt
     */
    private void buttonAbbrechenActionPerformed(ActionEvent evt) {
        setVisible(false);
        dispose();
    }

    /**
     * Initialisiert den Dialog
     */
    @SuppressWarnings({"Convert2Lambda"})
    private void initComponents() {

        labelTxt = new HtmlLabel(this, 
                "<html><center><b>Sie haben von Stradoku die " +
                "Version " + version + ".</b><br><br>Es gibt eine neuere Version, " +
                "die <b><a href=\"https://github.com/jogger2510/Stradoku/Stradoku.zip\">hier</a></b> " + 
                "heruntergeladen werden kann.</center>");
        okButton = new JButton();
        okButton.setText("Ok");
        setTitle("Update.Abfrage");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(labelTxt, 
                            GroupLayout.PREFERRED_SIZE, 293, GroupLayout.PREFERRED_SIZE)
                    .addComponent(okButton))
                .addGap(28, 28, 28))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(labelTxt, GroupLayout.DEFAULT_SIZE, 3, Short.MAX_VALUE)
                .addGap(20, 20, 20)
                .addComponent(okButton)
                .addGap(80, 80, 80))
        );
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                buttonAbbrechenActionPerformed(evt);
            }
        });
        pack();
    }
}
