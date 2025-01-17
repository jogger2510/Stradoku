/**
 * BereichsAuswertung.java ist Teil des Programmes kodelasStradoku

 * Erzeugt am:                  25.06.2012 09:27
 * Letzte Änderung:             23.01.2018 22:10
 * 
 * Copyright (C) Konrad Demmel, 2010 - 2024
 */

package stradoku;

/**
 * Stellt Auwertemethoden zur Verfügung, die von verschiedenen Klassen 
 * benötigt werden.
 */
public class BereichsAuswertung implements GlobaleObjekte {
    // Anzahl der möglichen Kombinationen für 2er, 3er und 4er Gruppe
    // bei n Kandidaten
    private final int[][] KNDKOMB = {
        {0, 0, 1, 3, 6, 10, 15, 21, 28, 36},
        {0, 0, 0, 1, 4, 10, 20, 35, 56, 84},
        {0, 0, 0, 0, 1, 5, 15, 35, 70, 126}
    };
    
    private final int[] kndArray = new int[10];
    
    /**
     * erstellt für die Kandidaten eines Bereiches alle möglichen Masken für eine Gruppe
     * @param maske Array, in dem die Masken zurück gegeben werden
     * @param knd alle Kandidaten, die in diesem Bereich vertreten sind
     * @param n Angabe, für welche n-Gruppe die Masken erzeugt werden sollen
     * @return für welche n-Gruppe wurde Maske erzeugt, 0 ist keine
     */
    public boolean erzeugeKndMasken(int[] maske, int knd, int n) {   
        boolean fertig = false;
        int max = setKndArray(knd) - 1;
        if (n == 2) {
            int aMax = max - 1;
            int bMax = max;
            int a = 0;
            int b = 1;
            int i = 0;
            do {
                int knd1 = kndArray[a];
                int knd2 = kndArray[b];
                maske[i] = LK_MASKE[knd1] | LK_MASKE[knd2];
                i++;
                if (b < bMax) {
                    b++;
                } else if (a < aMax) {
                    a++;
                    b = a + 1;
                }
                else {
                    fertig = true;
                }
            } while (!fertig);
        }
        else if (n == 3) {
            int aMax = max - 2;
            int bMax = max - 1;
            int cMax = max;
            int a = 0;
            int b = 1;
            int c = 2;
            int i = 0;
            do {
                int knd1 = kndArray[a];
                int knd2 = kndArray[b];
                int knd3 = kndArray[c];
                maske[i] = LK_MASKE[knd1] | LK_MASKE[knd2] | LK_MASKE[knd3];
                i++;
                if (c < cMax) {
                    c++;
                } 
                else if (b < bMax) {
                    b++;
                    c = b + 1;
                }
                else if (a < aMax) {
                    a++;
                    b = a + 1;
                    c = a + 2;
                }
                else {
                    fertig = true;
                }
            } while (!fertig);            
        }
        return true;
    }
    
    /**
     * Übergibt für 2er, 3er und 4er Gruppe die Anzahl der mit den noch
     * vorhandenen Kandidaten möglichen Kombinationen
     * @param grp für welche Gruppe
     * @param knd Anzahl der noch vorhandenen Kandidaten
     * @return Anzahl möglichen Kombinationen
     */
    public int getKndKombintionen(int grp, int knd) {
        return KNDKOMB[grp - 2][knd];
    }
    
    /**
     * Erstellt ein Array mit den noch vorhandenen Kandidaten in aufsteigender
     * Reihenfolge.
     * @param knd noch vorhandene Kandidaten
     * @return Anzahl der Kandidaten
     */
    private int setKndArray(int knd) {
        int n = 0;
        int kandidaten = knd;
        for (int k = 1; k < 10; k++) {
            if ((kandidaten & 1) == 1) {
                kndArray[n] = k;                
                n++;
            }
            kandidaten = kandidaten >>> 1;
        }
        return n;
    }
}
