/**
 * ImportStradoku.java ist Teil des Programmes kodelasStradoku
 * Erzeugt am                   04.07.2017 12:00
 * Zuletzt geändert:            24.01.2024 12:30
 * 
 * Copyright (C) Konrad Demmel, 2021 - 2024
*/

package stradoku;

import java.awt.HeadlessException;
import java.io.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Übernimt ein als Datei gespeichertes Stradoku
 */
public class ImportStradoku implements GlobaleObjekte {
    private final Stradoku strApp;
    private final StradokuOrg str;
    private final StradokuBoard strBoard;
    private String aktDir;
    private String userDir;
    private String strOrdner;
    private String strName;

    /**
     * Konstruktor
     * @param mf Referenz auf Hauptklasse
     */
    public ImportStradoku(Stradoku mf) {
        strApp = mf;
        str = strApp.getStradokuKlasse();
        strBoard = strApp.getStradokuBoard();
    }

    /**
     * Gespeichertes Stradoku über Dateidialog öffnen
     * @return true wenn Öffnen erfolgreich, sonst false
     */
    public boolean strLaden() {
        aktDir = strApp.getFilePath();
        userDir = strApp.getHomePath();
        strOrdner = strApp.getStrFolder();        boolean geladen = false;
        if (aktDir == null) {
            aktDir = userDir + strOrdner;
            File uDir = new File(aktDir);
            if (!uDir.exists()) {
                new File(userDir + File.separator + "Aufgaben").mkdirs();
            }
        }
        JFileChooser fc = new JFileChooser(aktDir);                       
        fc.setAcceptAllFileFilterUsed(false);                             
        fc.setFileFilter(new StrBinFilter());
        fc.setFileFilter(new StrBinFilter());                             
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);                 
        int returnVal = fc.showOpenDialog(strApp);                        
        if (returnVal == JFileChooser.APPROVE_OPTION) {                   
            try {                                                         
                String aktPfad = fc.getSelectedFile().getPath();          
                aktDir = aktPfad.substring(0,
                        aktPfad.lastIndexOf(File.separator));
                strApp.setFilePath(aktDir);
                File sf = new File(aktPfad);
                strName = sf.getName();                                   
                if (strName.toLowerCase().endsWith(".str")) {
                    if (strBinLaden(aktPfad)) {
                        strApp.setStatusBarHinweis(
                                "Gespeichertes Stradoku geladen.", false);
                    } else {
                        JOptionPane.showMessageDialog(strApp,
                                "Die gewählte Datei ist keine"
                                + " gültige Stradoku-Datei!");
                        return false; 
                    }
                }
                geladen = true;
                strApp.setStrName(aktPfad);
                strApp.setTitel();
            }
            catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(strApp, "Eingabefehler");
            }
        }
        return geladen;
    }

    /**
     * Binär gespeichertes Stradoku direkt öffnen
     * @param pfad Verzeichnis für das zu öffnende Stradoku
     * @return true wenn Öffnen erfolgreich, sonst false
     */
    public boolean strBinLaden(String pfad) {
        DataInputStream strm;
        File fp = new File(pfad);        
        if (!fp.exists())
            return false;
        int[] tmp = new int[81];        
        try {
            strm = new DataInputStream(new FileInputStream(pfad));
            long len = fp.length();         // Längenvergleich wegen VerlaufSize (820/800/640)
            for (int i = 0; i < 81; i++)
                tmp[i] = strm.readInt();
            str.setStradoku(tmp);
            for (int i = 0; i < 81; i++)
                tmp[i] = strm.readInt();
            str.setLoesung(tmp);            
            str.setSavePosNdx(strm.readInt());
            int tmpSize = str.getVerlaufSize();
            if (len < 3980 && len > 3260) {                               // neu = 3980
                tmpSize = 800;                             // Verlaufssize bis Level 4.3.2
            } else if (len <= 3260) {
                tmpSize = 640;
            }
            str.setVerlaufSize(tmpSize);
            tmp = new int[tmpSize];
            for (int i = 0; i < tmpSize; i++)
                tmp[i] = strm.readInt();
            str.setVerlauf(tmp);
            str.setVerlaufsNdx(strm.readInt());
            str.setLetzterVerlaufsNdx(strm.readInt());
            str.setGeloestNdx(strm.readInt());
            str.setLoesungsZellen(strm.readInt());
            str.setFreiZellen(strm.readInt());
            strBoard.setSelect(strm.readInt());
            int level = strm.readInt();  
            str.setLevel(level);
            strApp.setLevel(level);            
            strApp.setFehlerFreieZellen(strm.readInt());
            int filter = strm.readInt();
            str.setFilterKnd(filter);                         
            int flags = strm.readInt();
            if ((flags & ZEIGE_KND) == ZEIGE_KND) {
                strApp.setKndModus(1, true);
            } else if ((flags & ZEIGE_NOTIZ) == ZEIGE_NOTIZ) {
                strApp.setKndModus(2, true);
            } else {
                strApp.setKndModus(0, true);
            }
            strApp.setKndAnzeigeMod((flags & PUNKT_KND) == PUNKT_KND);
            str.setGeloest((flags & GELOEST) == GELOEST);
            strApp.set_usedKListe((flags & USED_KLISTE) == USED_KLISTE);
            strApp.set_usedKndFi((flags & USED_FILTER) == USED_FILTER);
            strApp.set_usedNotizen((flags & USED_NOTIZEN) == USED_NOTIZEN);
            strApp.set_usedTestmod((flags & USED_TESTMOD) == USED_TESTMOD);
            str.setGestartet((flags & GESTARTET) == GESTARTET);
            boolean start, links, rechts, aktuell, ende;
            start = (flags & NAVI_START) == NAVI_START;
            links = (flags & NAVI_ZURUECK) == NAVI_ZURUECK;
            rechts = (flags & NAVI_VOR) == NAVI_VOR;
            aktuell = (flags & NAVI_AKTPOS) == NAVI_AKTPOS;
            ende = (flags & NAVI_GELOEST) == NAVI_GELOEST;
            strApp.setNaviStatus(start, links, rechts, aktuell, ende);                
            long loesungsZeit;
            try {
                loesungsZeit = strm.readLong();
                if (flags == -1)
                    str.setGeloest(strm.readBoolean());
            }
            catch(EOFException e) {
                loesungsZeit = 0;
            } 
            strApp.setStartZeit(loesungsZeit);
            if (!ende) {
                str.showLoesung();
            }            
            strm.close();
        } catch (IOException iox) {
            JOptionPane.showMessageDialog(strApp,
                    "Beim Laden dieser Stradoku-Aufgabe "
                    + "ist ein unbekannter Fehler aufgetreten.",
                    "Hinweis", 1);
            strApp.setStatusBarHinweis("Lösungsmodus", false);
            return false;
        }
        str.setAufgabe();
        return true;
    }

}
