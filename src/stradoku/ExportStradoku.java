/*
 * ExportStradoku.java ist Teil des Programmes kodelasStradoku
 *
 * Erzeugt am:                  04.07.2017 12:00
 * Zuletzt geändert:            24.01.2024 12:30
 *
 * Copyright (C) Konrad Demmel, 2019-2024
 */

package stradoku;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Aufgabe: aktuelles Stradoku als Binär- oder Textdatei speichern
 */
public class ExportStradoku implements GlobaleObjekte {

    private final Stradoku strApp;
    private final StradokuOrg str;
    private final StradokuBoard strBoard;
    private String filePath;
    private String strOrdner;
    private String strName;

    /**
     * Konstruktor
     * @param mf Referenz auf Hauptklasse
     */
    public ExportStradoku(Stradoku mf) {
        strApp = mf;
        str = strApp.getStradokuKlasse();
        strBoard = strApp.getStradokuBoard();
    }

    /**
     * Speichert das aktuelle Stradoku erstmalig im Binär-Format. 
     * Der Anwender entscheidet über den Dateidialog über das Format.
     */
    public void exportStradoku() {
        String tmpDir = strApp.getHomePath();
        filePath = strApp.getFilePath();
        strOrdner = strApp.getStrFolder();
        if (filePath == null
                || filePath.substring(0, filePath.lastIndexOf(File.separatorChar))
                        .equals(tmpDir)) {
            filePath = tmpDir + strOrdner + File.separator;
        }
        File fp = new File(filePath);
        if (!fp.exists()) {
            new File(filePath).mkdirs();
        }
        strName = strApp.getStrName();
        JFileChooser fc = new JFileChooser(filePath);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new StrBinFilter());
        fc.setSelectedFile(new File(strName));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showSaveDialog(strApp);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String pfad = fc.getSelectedFile().getPath();
                if (!pfad.endsWith(".str")) {
                    pfad += ".str";
                }
                File sf = new File(pfad);
                if (sf.exists()) {
                    strName = pfad.substring(
                            pfad.lastIndexOf(File.separatorChar) + 1);
                    String[] yesNoOptions = {"Ja", "Nein"};
                    int op = JOptionPane.showOptionDialog(strApp,
                            "Die Datei '" + strName
                            + "' ist bereits vorhanden. "
                            + "Soll sie überschrieben werden?",
                            "Sicherheitsabfrage", // Titel
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null, yesNoOptions, yesNoOptions[1]);
                    if (op == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
                strBinSpeichern(pfad);
                strApp.setFilePath(pfad);
                strApp.setStrName(pfad);
                strApp.setTitel();
                strApp.setStatusBarHinweis("Stradoku gespeichert", false);
            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(strApp, "Eingabefehler");
            }
        }
    }

    /**
     * Stradoku binär als Folgespeicherung oder für exportStradoku() speichern.
     * @param pfad Speicherort
     */
    public void strBinSpeichern(String pfad) {
        DataOutputStream strm;
        int[] tmp;
        try {
            strm = new DataOutputStream(new FileOutputStream(pfad));
            tmp = str.getAktStradoku();
            for (int i = 0; i < 81; i++) {
                strm.writeInt(tmp[i]);
            }
            tmp = str.getLoesung();
            for (int i = 0; i < 81; i++) {
                strm.writeInt(tmp[i]);
            }
            strm.writeInt(str.getSavePosNdx());
            tmp = str.getVerlauf();
            for (int i = 0; i < tmp.length; i++) {
                strm.writeInt(tmp[i]);
            }
            strm.writeInt(str.getVerlaufsNdx());
            strm.writeInt(str.getLetzterVerlaufsNdx());
            strm.writeInt(str.getGeloestNdx());
            strm.writeInt(str.getLoesungsZellen());
            strm.writeInt(str.getFreiZellen(true));
            strm.writeInt(strBoard.getSelect());
            strm.writeInt(str.getLevel());
            strm.writeInt(strApp.getFehler());
            strm.writeInt(str.getFilterKnd());  
            int flags = 0;
            int kmod = strApp.getKndModus();
            if (kmod == 1) {
                flags |= ZEIGE_KND;
            } else if (kmod == 2) {
                flags |= ZEIGE_NOTIZ;
            }
            if (strApp.getKndAnzeigeMod()) {
                flags |= PUNKT_KND;
            }
            if (str.getGeloest()) {
                flags |= GELOEST;
            }
            if (strApp.getNaviStart()) {
                flags |= NAVI_START;
            }
            if (strApp.getNaviZurueck()) {
                flags |= NAVI_ZURUECK;
            }
            if (strApp.getNaviVor()) {
                flags |= NAVI_VOR;
            }
            if (strApp.getNaviAktPos()) {
                flags |= NAVI_AKTPOS;
            }
            if (strApp.getNaviGeloest()) {
                flags |= NAVI_GELOEST;
            }
            if (strApp.get_usedNotizen()) {
                flags |= USED_NOTIZEN;
            }
            if (strApp.get_usedKListe()) {
                flags |= USED_KLISTE;
            }
            if (strApp.get_usedKndFi()) {
                flags |= USED_FILTER;
            }
            if (strApp.get_usedTestmod()) {
                flags |= USED_TESTMOD;
            }
            if (str.getGestartet()) {
                flags |= GESTARTET;
            }
            strm.writeInt(flags);
            strm.writeLong(strApp.getLoesungsZeit());
            strm.flush();
            strm.close();
            strApp.repaint();
        } catch (IOException iox) {
            JOptionPane.showMessageDialog(strApp, // dann Meldung ausgeben
                    "Beim Speichern dieser Stradoku-Aufgabe "
                    + "ist ein unbekannter Fehler aufgetreten.",
                    "Hinweis", 1);
        }
    }

    /**
     * Speichert aktuelles Stradoku als PNG-Bilddatei.
     * @param name vorgeschlagener Neme
     * @param rand true wenn Ausgabe mit Rand erfolgen soll, bei false ohne Rand
     * @throws IOException Ein- und Ausgabefehler behandel
     */
    public void strBildSpeichern(String name, boolean rand) throws IOException {

        String pngDir = strApp.getPngPfad();
        File fp = new File(pngDir);
        if (!fp.exists()) {
            new File(pngDir).mkdir();
        }
        if (name.indexOf('.') >= 0) {
            strName = name.substring(name.lastIndexOf(File.separator) + 1, 
                    name.lastIndexOf("."));
        } else {
            strName = name;
        }
        JFileChooser fc = new JFileChooser(pngDir);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new StrPngFilter());
        fc.setSelectedFile(new File(strName));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showSaveDialog(strApp);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                pngDir = fc.getSelectedFile().getPath();
                if (!pngDir.endsWith(".png")) {
                    javax.swing.filechooser.FileFilter ff
                            = fc.getFileFilter();
                    String des = ff.getDescription();
                    if (des.endsWith("(*.png)")) {
                        pngDir += ".png";
                    } else {
                        JOptionPane.showMessageDialog(strApp,
                                "Es wurde keine Dateinamenserweiterung "
                                + "eingegeben.\n"
                                + "Daher ist eine Speicherung nicht möglich.\n");
                        return;
                    }
                }
                File sf = new File(pngDir);
                if (sf.exists()) {
                    String neuerName = pngDir.substring(
                            pngDir.lastIndexOf(File.separatorChar) + 1);
                    String[] yesNoOptions = {"Ja", "Nein"};
                    int op = JOptionPane.showOptionDialog(strApp,
                            "Die Datei '" + neuerName
                            + "' ist bereits vorhanden. "
                            + "Soll sie überschrieben werden?",
                            "Sicherheitsabfrage", // Titel
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null, yesNoOptions, yesNoOptions[1]);
                    if (op == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(strApp, "Eingabefehler");
                return;
            }
        }
        Component c = strApp.getStradokuFeld();
        int w = c.getWidth();
        int r = StradokuBoard.getStrBorder();
        BufferedImage img = new BufferedImage(w, w, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        c.paintAll(g);
        g.dispose();
        try {
            if (rand) {
                ImageIO.write(img, "png", new File(pngDir));
            } else {
                ImageIO.write(img.getSubimage(r, r, w-2*r, w-2*r),
                        "png", new File(pngDir));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(strApp,
                    "Beim Speichern der Bild-Datei ist ein "
                    + "unbekannter Fehler aufgetreten.",
                    "Hinweis", 1);
            return;
        }
        strApp.setStatusBarHinweis("Stradoku als PNG-Bildatei gespeichert", false);
    }
    
}
