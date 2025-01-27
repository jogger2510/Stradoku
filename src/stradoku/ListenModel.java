/**
 * ListModel.java ist Teil des Programmes kodelasStradoku
 * 
 * Erzeugt am:                  17.10.2011 17:41
 * Letzte Änderung:             20.06.2022 23:35
 *
 * Copyright (C) Konrad Demmel, 2010 - 2022
 */

package stradoku;

import java.io.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Diese Klasse definiert die Struktur der Tabelle und stellt die Schnittstelle
 * zwischen den Daten und den Anzeige-Methoden dar.
 */
public class ListenModel extends DefaultTableModel {

    private final Spalte selekt = new Spalte("selekt.", 20);
    private final Spalte num = new Spalte("ANr.", 20);
    private final Spalte aufgabe = new Spalte("Aufgabe", 350);
    private final Spalte level = new Spalte("Level", 8);
    private final Spalte frei = new Spalte("frei", 8);
    private final Spalte bemerkung = new Spalte("Bemerkung", 200);
    private final Spalte header[] = {
                                selekt, num, aufgabe, level, frei, bemerkung};
    private final ArrayList<Object> liste;
    private boolean geaendert;
    private boolean msg_zeigen = true;
    private final String userDir;
    private final String listDatei = "stradoku.lst";
    private final Stradoku mainFrame;

    /**
     * Konstruktor
     * @param mf Verweis auf das Hauptfenster der Anwendung
     */
    @SuppressWarnings({"CollectionWithoutInitialCapacity"})
    public ListenModel(Stradoku mf) {
        mainFrame = mf;
        userDir = mainFrame.getHomePath();
        liste = new ArrayList<>();
        initDaten();
        geaendert = false;
    }

    /**
     * Leitet die Übernahme von Stradokus aus einer Listendatei ein
     */
    private void initDaten() {
        File lf = new File(userDir, listDatei);
        if (lf.canRead() && lf.length() > 0) {
            datenLaden(userDir, listDatei);
        } else {
            setDefaultData();
        }
    }

    /*
     * Damit wird in der ersten Spalte ein Kontrollkästchen an Stelle von 
     * Text (true / false) dargestellt
     */
    @Override
    public Class getColumnClass(int i) {
        return getValueAt(0, i).getClass();
    }

    /**
     * Liest die aktuelle Stradoku-Liste ein
     * @param pfad Quell-Pfad
     * @param datei Dateiname
     */
    public void datenLaden(String pfad, String datei) {
        try {
            String fileName = pfad + File.separator + datei;
            InputStreamReader reader
                    = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
            try (BufferedReader br = new BufferedReader(reader)) {
                br.readLine();                          // erste Zeile mit dem Listenkopf überlesen
                String alle = " \t \t \t \t \t \t";     // Restaurieren einer Zeile
                do {
                    String sLine = br.readLine();
                    if (sLine == null || sLine.isEmpty()) {
                        break;
                    }
                    String buffer[] = alle.split("\t");
                    String[] tmp = sLine.split("\t");
                    System.arraycopy(tmp, 0, buffer, 0, tmp.length);
                    if (buffer[4].equals(" ")) {
                        buffer[4] = "";
                    }
                    liste.add(new Datensatz(false,
                            Util.getNum(buffer[0]), buffer[1], buffer[2],
                            buffer[3], buffer[4]));
                } while (true);
            }
        } catch (IOException ex) {
        }
    }

    /**
     * Speichert die aktuelle Stradoku-Liste
     * @param pfad Ziel-Pfad
     * @param datei Dateiname
     */
    public void datenSpeichern(String pfad, String datei) {
        if (geaendert) {
            int s = 1;
            int z = 0;
            BufferedWriter bw = null;
            try {
                String fileName = pfad + File.separator;
                OutputStreamWriter writer = new OutputStreamWriter(
                        new FileOutputStream(fileName + datei), "UTF-8");
                bw = new BufferedWriter(writer);
                try {
                    bw.write("Stradoku-Liste\r\n");
                    String sLine = "";
                    String eintrag;
                    while (z < getRowCount()) {
                        while (s < getColumnCount()) {
                            eintrag = "" + getValueAt(z, s);
                            if (eintrag.length() == 0) {
                                eintrag = " ";
                            } else if (s == 1 && eintrag.length() < 4) {
                                if (eintrag.length() == 1) {
                                    eintrag = "000" + eintrag;
                                } else if (eintrag.length() == 2) {
                                    eintrag = "00" + eintrag;
                                } else if (eintrag.length() == 3) {
                                    eintrag = "0" + eintrag;
                                }
                            }
                            sLine += eintrag + "\t";
                            s++;
                        }
                        bw.write(sLine + "\r\n");
                        sLine = "";
                        z++;
                        s = 1;
                    }
                    bw.flush();
                } catch (IOException ex) {
                }
            } catch (IOException ex) {
            } finally {
                try {
                    bw.close();
                } catch (Exception e) {
                }
            }
            geaendert = false;
        }
    }

    /**
     * Wird aufgerufen, wenn keine Liste gefunden wird und erstellt eine leere
     * Liste.
     */
    public void setDefaultData() {
        liste.removeAll(liste);
        liste.add(new Datensatz(false, 1, "", "", "", ""));
    }

    /**
     * Fügt einen Datensatz für ein Stradoku hinzu.
     * @param afg Stradoku-Aufgabe
     * @param lvl Level der Aufgabe
     * @param lz Anzahl der leeren Zellen
     * @param bmk Bemerkung
     * @return Nummer der Zeile, in welcher das Stradoku eingefügt wurde
     */
    public int addStradoku(String afg, String lvl, int lz, String bmk) {
        int pos = -1;
        // alle Einträge drchlaufen
        for (int z = 0; z < getRowCount(); z++) {
            // neuer Eintrag mit vorhandenem identisch
            if (afg.equals(getValueAt(z, 2))) {
                // kein Serieneintrag
                if (msg_zeigen) {
                    JOptionPane.showMessageDialog(mainFrame,
                        "Dieses Stradoku ist in der Liste bereits als Aufgabe"
                        + " Nummer \"" + getValueAt(z, 1) + "\" gespeichert.",
                        "Hinweis", 1);
                }
                return pos;
            }
        }
        // nächste Position für Eintrag suchen
        for (int z = 0; z < getRowCount(); z++) {
            if (getValueAt(z, 2).toString().length() < 81) {
                pos = z;
                break;
            }
        }
        // eventuell vorhandene Bemerkung übernehmen
        bmk = getValueAt(pos, 5).toString();       
        if (getRowCount() == 1 && pos == 0) {
            setValueAt("", pos, 5);
        }
        if (pos != -1) {
            setValueAt(false, pos, 0);
            setValueAt(pos + 1, pos, 1);
            setValueAt(afg, pos, 2);
            setValueAt(lvl, pos, 3);
            setValueAt("" + lz, pos, 4);
            setValueAt(bmk, pos, 5);
            pos++;
        } else {
            pos = getRowCount() + 1;
            liste.add(new Datensatz(false, pos, afg, lvl, "" + lz, bmk));
        }
        fireTableDataChanged();
        geaendert = true;
        return pos;
    }

    /**
     * Überschreibt die entsprechende Methode des DefaultTableModel
     * @return Anzahl der Datensätze in der Liste
     */
    @Override
    public int getRowCount() {
        return liste == null ? 0 : liste.size();
    }

    /**
     * Abfrage der Spaltenzahl für die Liste
     * @return Anzahl der Spalten
     */
    @Override
    public int getColumnCount() {
        return header.length;
    }

    /**
     * Abfrage der Kopfzeileneinträge
     * @param spalte Spalte, für die Abfrage
     * @return Spaltenbezeichnung
     */
    @Override
    public String getColumnName(int spalte) {
        return header[spalte].getSpaltenKopf();
    }

    /**
     * Abfrage, ob Zelle edidierbar ist
     * @param nRow Zeile für die Abfrage
     * @param nCol Spalte für die Abfrage
     * @return true, wenn editierbar, sonst false 
     */
    @Override
    public boolean isCellEditable(int nRow, int nCol) {
        return nCol <= 0 || nCol >= 5;
    }

    /**
     * Abfrage eines Zellenwertes
     * @param nRow Zeile für Abfrage
     * @param nCol Spalte für Abfrage
     * @return Zellenwert
     */
    @Override
    public Object getValueAt(int nRow, int nCol) {
        if (nRow < 0) {
            return "";
        }
        try {
            Datensatz row = (Datensatz) liste.get(nRow);
            switch (nCol) {
                case 0:
                    return row.isSelected();
                case 1:
                    return row.getNummer();
                case 2:
                    return row.getAufgabe();
                case 3:
                    return row.getLevel();
                case 4:
                    return row.getVorgaben();
                case 5:
                    return row.getBemerkung();
            }
        } catch (Exception pe) {
            return "";
        }        
        return "";
    }

    /**
     * Setzt den Wert für eine Zelle
     * @param eintrag zu setzender Wert
     * @param zeile Zeilenindex der Zelle 
     * @param spalte Spaltenindex der Zelle
     */
    @Override
    public void setValueAt(Object eintrag, int zeile, int spalte) {
        Datensatz aktZeile = (Datensatz) liste.get(zeile);
        switch (spalte) {
            case 0:
                aktZeile.setSelected((boolean) (Boolean) eintrag);
                break;
            case 1:
                aktZeile.setNummer((Integer) eintrag);
                break;
            case 2:
                aktZeile.setAufgabe(eintrag.toString());
                break;
            case 3:
                aktZeile.setLevel(eintrag.toString());
                break;
            case 4:
                aktZeile.setVorgaben(eintrag.toString());
                break;
            case 5:
                aktZeile.setBemerkung(eintrag.toString());
                break;
        }
        geaendert = true;
        this.fireTableDataChanged();
    }

    /**
     * Abfrage nach Listenbezeichnung
     * @return Listenbezeichnung
     */
    public String getTitle() {
        return "Stradoku-Liste";
    }

    /**
     * Gibt die Breite einer Spalte zurück.
     * @param spalte Spalte für die die Breite zurückzugeben ist.
     * @return Spaltenbreite
     */
    public int getSpaltenBreite(int spalte) {
        return header[spalte].getSpaltenBreite();
    }

    /**
     * Gibt die Anzahl der selektierten Datensätze zurück.
     * @return Anzahl
     */
    public int getSelektStradoku() {
        int anz = 0;
        for (int i = 0; i < getRowCount(); i++) {
            if ((Boolean) getValueAt(i, 0)) {
                anz++;
            }
        }
        return anz;
    }

    /**
     * Entfernt für alle Datensätze die Selektierung.
     */
    public void deselektPrintSelekt() {
        for (int i = 0; i < getRowCount(); i++) {
            setValueAt((Boolean) false, i, 0);
        }
    }

    /**
     * Entfernt den Eintrag für eine Stradokuaufgabe aus der Liste. Die Zeile
     * selbst bleibt als leere Zeile erhalten.
     * @param nr Index-Nummer der zu entfernenden Aufgabe
     */
    public void deleteStradoku(int nr) {
        setValueAt(false, nr, 0);
        for (int s = 2; s <= 4; s++) {
            setValueAt("", nr, s);
        }
    }

    /**
     * Entfernt einen Datensatz.
     * @param z Index für zu entfernenden Datensatz
     */
    public void deleteStradokuZeile(int z) {
        liste.remove(z);
    }

    /**
     * Nummeriert alle Datensätze neu.
     */
    public void neuNummerieren() {
        for (int z = 0, zNr = 1; z < getRowCount(); z++, zNr++) {
            setValueAt(zNr, z, 1);
            setValueAt(false, z, 0);
        }
    }

    /**
     * Setzt Flag für Änderung der Liste.
     */
    public void setGeaendert() {
        geaendert = true;
    }

    /**
     * Legt fest, ob beim Versuch, ein bereits in der Liste befindliche
     * Stradoku-Aufgabe noch einmal hinzuzufügen, eine Meldung ausgegeben werden
     * soll.
     * @param zeigen true wenn Meldung ausgegeben werden soll, sonst false
     */
    public void setMsg_vorhanden(boolean zeigen) {
        msg_zeigen = zeigen;
    }
}

/**
 * Diese Klasse kapselt die Daten für einen Datensatz.
 */
class Datensatz {
    private boolean selected;
    private int nummer;
    private String aufgabe;
    private String vorgaben;
    private String level;
    private String bemerkung;

    /**
     * Konstruktor
     * @param ps Flag für Print-Selekt
     * @param nr laufende Nummer für Stradoku-Aufgabe
     * @param afg Stradoku-Aufgabe
     * @param lev Level der Stradoku-Aufgabe
     * @param vor Anzahl der Vorgabewerte
     * @param bem Bemerkung
     */
    Datensatz(
            boolean ps, 
            int nr, 
            String afg, 
            String lev, 
            String vor, 
            String bem) {
        selected = ps;
        nummer = nr;
        aufgabe = afg;
        level = lev;
        vorgaben = vor;
        bemerkung = bem;
    }

    /**
     * Abfrage auf Selection eines Datensatzes
     * @return true wenn selektiert
     */
    public boolean isSelected() {
        return (Boolean) selected;
    }

    /**
     * Setzt Selektion eines Datensatzes
     * @param sel zu setzender Wert
     */
    public void setSelected(boolean sel) {
        selected = sel;
    }

    /**
     * Abfrage der Nummer eines Datensatzes
     * @return Nummer des Datensatzes
     */
    public int getNummer() {
        return (Integer) nummer;
    }

    /**
     * Neue Nummer für Datensatz eintragen.
     * @param nr zu setzender Zeilenindex
     */
    public void setNummer(int nr) {
        this.nummer = nr;
    }

    /**
     * Aufgabe für Datensatz zurückgeben
     * @return Stradoku Aufgabe
     */
    public String getAufgabe() {
        return aufgabe;
    }

    /**
     * Für Datensatz Aufgabe eintragen
     * @param aufgabe Stradoku-Aufgabe
     */
    public void setAufgabe(String aufgabe) {
        this.aufgabe = aufgabe;
    }

    /**
     * Vorgaben für Datensatz zurückgeben.
     * @return Anzahl der Vorgabezellen
     */
    public String getVorgaben() {
        String sb = vorgaben;
        return sb;
    }

    /** Vorgaben für Datensatz eintragen.
     * @param vorgaben Anzahl der Vorgabezellen
     */
    public void setVorgaben(String vorgaben) {
        this.vorgaben = vorgaben;
    }

    /**
     * Level für Datensatz zurückgeben.
     * @return Level
     */
    public String getLevel() {
        return level;
    }

    /**
     * Level für Datensatz eintragen.
     * @param lvl Level
     */
    public void setLevel(String lvl) {
        level = lvl;
    }

    /**
     * Bemerkung zurückgeben.
     * @return Bemerkung
     */
    public String getBemerkung() {
        String sb = bemerkung;
        return sb;
    }

    /**
     * Bemerkung für Datensatz eintragen.
     * @param bemerkung Bemerkung
     */
    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

}

/**
 * Diese Klasse kapselt die Formatdaten Spaltenbezeichnung und Spaltenbreite für
 * eine Spalte.
 */
class Spalte {

    private String spaltenKopf;
    private int spaltenBreite;

    /**
     * Konstruktor
     * @param title Bezeichnung im Spaltenkopf für die Spalte
     * @param width Breite der Spalte
     */
    Spalte(String title, int width) {
        spaltenKopf = title;
        spaltenBreite = width;
    }

    /**
     * Abfrage des Spaltenkopfes
     * @return SpaltenKopf
     */
    public String getSpaltenKopf() {
        return spaltenKopf;
    }

    /**
     * Setzt den Spaltenkopf
     * @param spaltenKopf Beschreibung des Spaltenkopfes
     */
    public void setSpaltenKopf(String spaltenKopf) {
        this.spaltenKopf = spaltenKopf;
    }

    /**
     * Abfrage der Spaltenbreite
     * @return spaltenBreite
     */
    public int getSpaltenBreite() {
        return spaltenBreite;
    }

    /**
     * Setzt Spaltenbreite
     * @param spaltenBreite zu setzende breite
     */
    public void setSpaltenBreite(int spaltenBreite) {
        this.spaltenBreite = spaltenBreite;
    }
}
