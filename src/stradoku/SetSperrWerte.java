/**
 * SperrWerteSet.java ist Teil des Programmes kodelasStradoku
 *
 * Erstellt am:                 11.03.2022 14:45
 * Letzte Änderung:             25.04.2024 11:20
 *
 * Copyright (C) Konrad Demmel, 2017 - 2024
 */
package stradoku;

/**
 * Fügt im Bearbeitungsmodus mögliche SperrWerte ein.
 *
 * @author Konrad
 */
public class SetSperrWerte implements GlobaleObjekte {
    private static final int[] BWERTE = {0,2,4,8,16,32,64,128,256,512};
    private static StradokuOrg str;
    private final int[] loesung;

    /**
     * Konstruktor
     *
     * @param ostr Referenzs auf die StradokuOrg-Klasse
     * @param loe Referenz auf Lösung des aktuellen Stradoku
     */
    public SetSperrWerte(StradokuOrg ostr, int[] loe) {
        str = ostr;
        loesung = loe;
    }

    /**
     * Setzt für eine Stradokuaufnahme im Bearbeitungsmodus mögliche Sperrwerte.
     * @return true wenn mindestens ein Sperrwert gesetzt wurde, sonst false
     */
    public boolean setSperrwerte() {
        int z, s;
        int aWrt;
        boolean set = false;
        for (int i = 0; i <= 80; i++) {
            if (loesung[i] == SZELLE) {                              // nur leere Sperrzellen
                aWrt = 0;
                int wrt;
                z = (i / 9) * 9;
                s = i % 9;
                for (int j = z; j <= z + 8; j++) {                  // erst in der Zeile
                    if (j == i) {
                        continue;
                    }
                    wrt = loesung[j] & ZAHL;
                    if (wrt > 0) {
                        aWrt |= 1 << wrt;
                    }
                }
                for (int j = s; j < s + 73; j += 9) {               // dann in der Spalte
                    if (j == i) {
                        continue;
                    }
                    wrt = loesung[j] & ZAHL;
                    if (wrt > 0) {
                        aWrt |= 1 << wrt;
                    }
                }
                int fWrt = 0x3FE & ~aWrt;
                if (fWrt > 0) {                                     // mindestens 1 freier Wert
                    for (int w = 9; w > 0; w--) {
                        if ((fWrt & BWERTE[w]) == BWERTE[w]) {
                            str.setWert(i, w);
                            loesung[i] |= w;
                            break;
                        }
                    }
                    set = true;
                }
            }
        }
        return set;
    }
    
}
