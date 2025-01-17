/**
 * LevelLoeser.java ist Teil des Programmes kodelasStradoku
 * Erzeugt am:                  14.02.2019 12:05
 * Letzte Änderung:             03.03.2019 19:30
 *
 * Copyright (C) Konrad Demmel, 2019-2021
 */

package stradoku;


/**
 * Erzeugt für ein Stradoku Testwerte, über die das Stradoku im 
 * BackTracking Verfahren gelöst werden kann.
 * @author Konrad  
 */
public class BackTrackLoeser implements GlobaleObjekte {   
    private final BTStatus[] statusListe;
    private BTStatus status; 
    int li;     
    int d;
    
    /**
     * Konstruktor
     */
    public BackTrackLoeser() {
        statusListe = new BTStatus[50];
        for (int i = 0; i < statusListe.length; i++) {
            statusListe[i] = new BTStatus();
        }
        li = -1;
        d = 0;
    }

    /**
     * private Klasse zur Aufnahme der Testwerte
     */
    private class BTStatus {
        int[][] feld;    
        int testZelle;
        int nmWerte;
        
        public BTStatus(){
            feld = new int[9][9];
            testZelle = -1;
            nmWerte = -1;
        }
    }
    
    /**
     * Erzeugt Testwerte für die BackTracking Lösungssuche.
     * @param aufgabe Array-Aufgabe mit Kandidaten, bis zu denen gelöst wurde
     * @param error Flag für aufgetreteen Fehler
     *  - keine Fehler: neue Zelle ermitteln
     *  - bei Fehler:   nächsten Kandidat der letzten Zelle prüfen, keiner
     *                  mehr vorhanden - auf vorhergend geprüfte Zellen mit
     *                  noch ungeprüften Kandidaten zurück gehen
     * @return true, wenn in Aufgabe Testwert eingesetzt wurde
     */
    public boolean backtrackLoesung(int[][] aufgabe, boolean error){
        boolean fehler = error;
        d++;
        do {
            if (fehler  && statusListe[li].nmWerte == 0) {
                fehler = zurückgehen(aufgabe);
            } 
            else if (fehler && statusListe[li].nmWerte > 0) {
                fehler = setze_folgeTestwert(aufgabe);
            } else {
                fehler = setze_neuenTestwert(aufgabe);
            }
            if (li >= statusListe.length-1) {
                return false;
            }
        } while (fehler);
        return true;
    }

    /**
     * Ist ein Fehler aufgetreten, wird so lange zuück gegangen, bis eine
     * Zelle gefunden wird, für die noch ungetestete Kandidaten vorhanden sind.
     * Wird ein solcher gefunden, wird er als Testwert gesetzt.
     * @param aufgabe aktuelles Stradoku Array
     * @return true wenn ungetesteter Kandidat vorhanden
     */
    private boolean zurückgehen(int[][] aufgabe) {
        int i, w;
        if (li <= 0) return false;
        do {
            li--;
            status = statusListe[li];
            if (status.nmWerte > 0) {
                i = status.testZelle;
                w = MINKND[status.nmWerte];
                status.nmWerte &= ~(K_MASKE[w]);
                kopieren(status.feld, aufgabe);
                aufgabe[Z[i]][S[i]] = w + LWF;
                
                return bereinigeFeld(aufgabe, i, w);
            }
        } while (li > 1);       
        return true;
    }
    
    /**
     * Übernimt alle Aufgaben für das Setzen eines Folge-Testkandidaten.
     * @param aufgabe Stradokuaufgabe, in die der Testwert gesetzt wird
     * @return true bei Fehler durch leere Zellen, sonst false
     */
    private boolean setze_folgeTestwert(int[][] aufgabe) {
        int i, w;
        if (li >= statusListe.length-1) {
            return true;
        }
        status = statusListe[li];
        i = status.testZelle;
        w = MINKND[status.nmWerte];
        status.nmWerte &= ~(K_MASKE[w]);
        kopieren(status.feld, aufgabe);
        aufgabe[Z[i]][S[i]] = w + LWF;
        
        return bereinigeFeld(aufgabe, i, w); 
    }
    
    /**
     * Übernimt alle Aufgaben für das Setzen eines Testkandidaten.
     * @param aufgabe Stradokuaufgabe, in die der Testwert gesetzt wird
     * @return true bei Fehler durch leere Zellen, sonst false
     */
    private boolean setze_neuenTestwert(int[][] aufgabe) {
        int i, w;
        if (li >= statusListe.length-1) {
            return true;
        }
        li++;
        status = statusListe[li];
        kopieren(aufgabe, status.feld);
        i = getnextminZelle(status.feld);
        if (i < 0) 
            return true;
        status.testZelle = i;
        w = MINKND[aufgabe[Z[i]][S[i]] >>> 5];
        status.nmWerte = (aufgabe[Z[i]][S[i]] & ~(LWF << w)) >>> 5;
        aufgabe[Z[i]][S[i]] = w + LWF;
        
        return bereinigeFeld(aufgabe, i, w);         
    }
    
    /**
     * Sucht im Stradokufeld eine Zelle mit der geringsten Anzahl an Kandidaten.
     * @param feld  das zu durchsuchende Stradoku-Feld
     * @return Index der gefundenen Zelle, keine mit max 4 Knd gefunden: -1
     */
    private int getnextminZelle(int[][] feld) {
        int max = 2;
        do {
            for (int z = 0; z < 9; z++) {
                for (int s = 0; s < 9; s++) {
                    if (feld[z][s] < SZELLE && feld[z][s] > LWRT && 
                                NKND[feld[z][s] >>> 5] <= max){
                        return 9*z+s;
                    }
                }
            }
            max++;
        } while (max < 9);
        return -1;
    }

    /**
     * Bereinigt nach einer Wertzuweisung die Kandidatenliste
     * @param strado zu bereinigendes Stradoku Array
     * @param pos Zelle die neuen Wert erhielt
     * @param knd Kandidat, der zugewiesen wurde
     * @return true wenn Bereinigung erfolgreich, sonst false
     */
    private boolean bereinigeFeld(int[][] strado, int pos, int knd) {
        int z = Z[pos];
        int s = S[pos];
        // aus der Zeile entfernen
        for (int i = 0; i < 9; i++) {
            if (i == s) {
                continue;
            }
            if ((strado[z][i] & LK_MASKE[knd]) == LK_MASKE[knd]) {
                strado[z][i] &= ~LK_MASKE[knd];
                strado[z][i] -= 1;
                if (strado[z][i] <= 0 || strado[z][i] == 16) {
                    return true;
                }
            }
        } 
        // aus der Spalte entfernen
        for (int i = 0; i < 9; i++) {
            if (i == z) {
                continue;
            }
            if ((strado[i][s] & LK_MASKE[knd]) == LK_MASKE[knd]) {
                strado[i][s] &= ~LK_MASKE[knd];
                strado[i][s] -= 1;
                if (strado[z][i] <= 0 || strado[z][i] == 16) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Kopiert die Werte und nicht nur die Reverenzen für ein Stradokufeld.
     * @param aufgabe die zu kopierende Aufgabe als zweidimensionales Array
     * @param ziel der Bereich, in den kopiert wird als zweidimensionales Array
     */
    @SuppressWarnings("ManualArrayToCollectionCopy")
    private void kopieren(int[][] aufgabe, int[][] ziel) {
        for (int z = 0; z < 9; z++) {
            for (int s = 0; s < 9; s++) {
                ziel[z][s] = aufgabe[z][s];
            }
        }
    }
    
}
