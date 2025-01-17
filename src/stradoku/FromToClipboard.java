/**
 * FromToClipboard.java ist Teil des Programmes kodelasStradoku

 * Erstellt am:                 27.01.2021 22:45
 * Letzte Änderung:             04.01.2024 14:00
 * 
 * Copyright (C) Konrad Demmel, 2021
 */

package stradoku;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author Konrad
 */
public class FromToClipboard {
    
    private static Stradoku strApp;
    private static StradokuOrg strOrg;
    private static StradokuBoard strBoard;
    
    /** Konstruktor
     * @param mFrame Referenz zum Hauptfenster
     * @param strKlasse Referenz zur StradokuOrg-Klasse
     * @param board Referenz zur Klasse StradokuBoard
     */
    public FromToClipboard(Stradoku mFrame, 
            StradokuOrg strKlasse, StradokuBoard board) {
        strApp = mFrame;
        strOrg = strKlasse;
        strBoard = board;
    }    
    
    /**
     * Übergibt das aktuelle Stradoku der Zwischenablage.
     * @param intern bestimmt das Format (siehe MakeStradokuString())
     * @param anzeige True für Übernahme in Statuszeile angezeigen
     */
    public void strToClipboard(boolean intern, boolean anzeige) {
        MakeStradokuString exp = new MakeStradokuString(strOrg);
        String clipStr = exp.getStradokuString(intern);
        try {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection content = new StringSelection(clipStr);
            clip.setContents(content, null);
            if (anzeige) {
                strApp.statusBarHinweis.setText(
                        "Aktuelles Stradoku in Zwischenablage kopiert.");
            }
        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(strApp,
                    "Es ist ein Fehler aufgetreten. \n\n"
                    + ex.getMessage(),
                    "Hinweis", 1);
        }
    }

    /**
     * Übernimmt von der Zwischenablage ein Stradoku und veranlasst, dass dieses
     * als aktuelles Stradoku angezeigt wird.
     * @return false wenn Fehler aufgetreten ist, sonst true
     */
    public boolean strFromClipboard() {
        boolean erfolg = true;
        try {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable clipboardContent = clip.getContents(this);
            if ((clipboardContent != null)
                    && (clipboardContent.isDataFlavorSupported(
                            DataFlavor.stringFlavor))) {
                String stradoku = (String) clipboardContent.getTransferData(
                        DataFlavor.stringFlavor);
                if (strOrg.importStradokuString(stradoku, true)) {
                    strApp.setStrPath("unbenannt");
                    strApp.setTitle(strApp.getAppName() + " - " + "unbenannt");
                    strApp.setFehlerFreieZellen(0);
                    strApp.resetKandAnzeige(true);
                    strApp.set_usedKListe(false);
                    strApp.set_usedNotizen(false);
                    strApp.set_geaendert(false);
                    strApp.setStartZeit(0);
                    strApp.labelHinweisfeld.setText("");
                    strOrg.setFilterKnd(0);
                    strBoard.requestFocusInWindow();
                    strApp.statusBarHinweis.setText(
                            "Stradoku aus Zwischenablage übernonnen.");
                } else {
                    strApp.statusBarHinweis.setText(
                            "Kein gültiger Stradoku-String aus Zwischenablage übergeben.");
                    erfolg = false;
                }
            }
        } catch (HeadlessException
                | UnsupportedFlavorException | IOException ex) {
            return false;
        }
        return erfolg;
    }    
    
    /**
     * Kopiert das Stradoku-Feld in die Zwischenablage
     * @param rahmen  true wenn Übernahme des Feldes mit Rahmen, sonst false 
     */
    public void imgToClipboard(boolean rahmen) {
        Component c = strApp.getStradokuFeld();
        int q = c.getWidth();
        int r = StradokuBoard.getStrBorder();
        String mo = " mit ";
        BufferedImage img = new BufferedImage(q, q, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        c.paintAll(g);
        g.dispose();
        try {
            if (!rahmen) {
                img = img.getSubimage(r, r, q-2*r, q-2*r);
                mo = " ohne ";
            }
            ImageSelection imgSel = new ImageSelection(img);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);            
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(strApp,
                    "Bei der Übernahme in die Zwischenablage "
                    + "ist ein unbekannter Fehler aufgetreten.",
                    "Hinweis", 1);
            return;
        }
        strApp.setStatusBarHinweis(
                "Stradoku als Bildatei"+mo+"Rand in die Zwischenablage kopiert.", false);        
    }
    
    /**
     * Diese Klasse wird verwendet, um ein Bild in der Zwischenablage zu speichern.
     * Fundstelle: https://alvinalexander.com/java/java-copy-image-to-clipboard-example/
     */
    static class ImageSelection implements Transferable {

        private final Image image;

        public ImageSelection(Image image) {
            this.image = image;
        }

        // Returns supported flavors
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        // Returns true if flavor is supported
        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        // Returns image
        @Override
        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException, IOException {
            if (!DataFlavor.imageFlavor.equals(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return image;
        }
    }
    
}
