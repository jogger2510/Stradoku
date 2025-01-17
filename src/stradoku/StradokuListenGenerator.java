/**
 * StradokuListenGenerator.java ist Teil des Programmes kodelasStradoku
 * 
 * Erzeugt am:                  18.08.2018 08:50
 * Letzte Änderung:             08.11.2018 15:00         
 *
 * Copyright (C) Konrad Demmel, 2018 - 2020
 */

package stradoku;

/**
 * Erzeugt für ein Stradoku in einem eindimensionalen Array ein 
 * zweidimensionales Array und eine Liste aller Bereiche.
 */
public class StradokuListenGenerator implements GlobaleObjekte {
    
    private final int[][] stradokuFeld;
    private final StrBereich[] strListe;

    /**
     * Konstruktor
     * @param aktStr eindimensionales Array mit einer StrBereich-Aufgabe
     */
    public StradokuListenGenerator(int[] aktStr) {
        stradokuFeld = new int[9][9];
        strListe = new StrBereich[48];
        initAuswertung(aktStr);
    }  
    
    /**
     * Initialisiert das zweidimensionale Array für das Stradokufeld.
     * @param aktStr Referenz auf aktuelles StrBereich Array
     */
    private void initAuswertung(int[] aktStr) {
        int z, s, lp;
        boolean isStradoku;
        for (int i = 0; i < strListe.length; i++) {
            strListe[i] = new StrBereich(0);
        }
        // aktuelles eindimensionales Stradoku (aktStr) als zweidimensionales 
        // Stradoku (stradokuFeld) übernehmen
        for (z = 0; z < 9; z++) {
            for (s = 0; s < 9; s++) {
                stradokuFeld[z][s] = aktStr[z * 9 + s];
            }
        }
        int i = 0;
        // StrBereich suchen und auflisten, erst in den Zeilen
        for (z = 0; z < 9; z++) {
            isStradoku = false;
            lp = 0;
            for (s = 0; s < 9; s++) {
                // Sperrzelle
                if ((stradokuFeld[z][s] & SZELLE) == SZELLE) {
                    // eine StrBereich ist offen
                    if (isStradoku) {
                        // StrBereich ist zu Ende
                        isStradoku = false;
                        strListe[i].lzPos.z = z;
                        strListe[i].lzPos.s = s - 1;
                        strListe[i].len = s - lp;
                        i++;
                    }
                } else if (isStradoku && s == 8) {
                    // letzte Zelle in der Zeile erreicht
                    strListe[i].lzPos.z = z;
                    strListe[i].lzPos.s = 8;
                    strListe[i].len = s - lp + 1;
                    i++;
                } else {
                    // keine Sperrzelle - eine StrBereich
                    if (!isStradoku && s < 8
                            && (stradokuFeld[z][s + 1] & SZELLE) != SZELLE) {
                        isStradoku = true;
                        strListe[i].isZeile = true;
                        strListe[i].ezPos.z = z;
                        strListe[i].ezPos.s = s;
                        lp = s;
                    }
                }
            }
        }
        // dann in den Spalten
        for (s = 0; s < 9; s++) {
            isStradoku = false;
            lp = 0;
            for (z = 0; z < 9; z++) {
                // Sperrzelle
                if ((stradokuFeld[z][s] & SZELLE) == SZELLE) {
                    // eine StrBereich ist offen
                    if (isStradoku) {
                        // StrBereich ist zu Ende
                        isStradoku = false;
                        strListe[i].lzPos.z = z - 1;
                        strListe[i].lzPos.s = s;
                        strListe[i].len = z - lp;
                        i++;
                    }
                } else if (isStradoku && z == 8) {
                    // letzte Zelle in der Spalte erreicht
                    strListe[i].lzPos.z = 8;
                    strListe[i].lzPos.s = s;
                    strListe[i].len = z - lp + 1;
                    i++;
                } else {
                    // keine Sperrzelle - eine StrBereich
                    if (!isStradoku && z < 8
                            && (stradokuFeld[z + 1][s] & SZELLE) != SZELLE) {
                        isStradoku = true;
                        strListe[i].isZeile = false;
                        strListe[i].ezPos.z = z;
                        strListe[i].ezPos.s = s;
                        lp = z;
                    }
                }
            }
        }
        setPositionen();
    }

    /**
     * Setzt für alle Stradokus in der Liste die Positionen.
     */
    private void setPositionen() {
        int ndx = 0;
        while (strListe[ndx].len > 0) {
            StrBereich str = strListe[ndx];
            str.index = ndx;
            str.pos = new Position[str.len];
            for (int i = 0; i < str.len; i++) {
                str.pos[i] = new Position();
            }
            int pz = str.ezPos.z;
            int ps = str.ezPos.s;
            for (int i = 0; i < str.len; i++) {
                str.pos[i].z = pz;
                str.pos[i].s = ps;
                if (str.isZeile) {
                    ps++;
                } else {
                    pz++;
                }
            }
            ndx++;
        }
    }  
    
    /**
     * @return generierte Bereichsliste
     */
    public StrBereich[] getStradokuListe() {
        return strListe;
    }
    
    /**
     * @return generiertes zweidimensonales Stradoku Feld
     */
    public int[][] getStradokuFeld() {
        return stradokuFeld;
    }    
}
