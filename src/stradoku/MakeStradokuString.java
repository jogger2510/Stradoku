/*
 * MakeStradokuString.java ist Teil des Programmes kodelasStradoku
 * Erzeugt am:                  03.07.2017, 12:00
 * Letzte Änderung:             08.08.2018, 22:15

 * Copyright (C) Konrad Demmel, 2010 - 2018  
*/

package stradoku;

import static stradoku.GlobaleObjekte.SZELLE;
import static stradoku.GlobaleObjekte.ZAHL;

/**
 * Erstellt für die aktuelle Stradoku Aufgabe einen String, entweder im 
 * 81stelligen internen oder im 162igen externen Format.
 */
public class MakeStradokuString {

    private static StradokuOrg stra; 
    private static int[] afg;

    /**
     * Konstruktor
     * @param str Referenz auf aktuelle Stradoku-Aufgabe
     */
    public MakeStradokuString(StradokuOrg str) {
        stra = str;
    }

    /**
     * Liefert vom aktuellen Stradoku
     * einen Textstring, entweder mit 81 Zeichen
     * (interner Mode) oder mit 162 Zeichen (externer Mode).
     * @param intern true, wenn interner Modus, sonst false
     * @return Textstring im gewünschten Modus
     */
    public String getStradokuString(boolean intern) {
        String aufgabe;
        afg = stra.getAufgabe();                    
        if (intern) {                                   
            aufgabe = getsimpleStringAufgabe(afg);
        }
        else {
            aufgabe = getexterneStringAufgabe(afg);
        }
        return aufgabe;                    
    }

    /**
     * Abfrage nach dem String einer Stradoku Aufgabe im internen Format
     * @param afg Referenz auf das Array mit der Aufgabe
     * @return Aufgabenstring
     */
    private String getsimpleStringAufgabe(int afg[]) {
        StringBuilder aufgabe = new StringBuilder("");
        for (int i = 0; i < 81; i++) {
            if (afg[i] == 0) {
                aufgabe.append("0");
            } else if ((afg[i] & SZELLE) == SZELLE) {
                if ((afg[i] & ZAHL) == 0) {
                    aufgabe.append("s");
                } else {
                    aufgabe.append((char) (96 + (afg[i] & ZAHL)));
                }
            } else {
                aufgabe.append(afg[i]);
            }
        }
        return aufgabe.toString();
    }   

    /**
     * Abfrage nach dem String einer Stradoku Aufgabe im externen Format
     * @param afg Referenz auf das Array mit der Aufgabe
     * @return Aufgabenstring
     */    
    private String getexterneStringAufgabe(int afg[]) {
        StringBuilder aufgabe = new StringBuilder("");
        for (int i = 0; i < 81; i++) {
            aufgabe.append(afg[i] & ZAHL);
        }
        for (int i = 0; i < 81; i++) {
            if ((afg[i] & SZELLE) == SZELLE) {
                aufgabe.append("1");
            } else {
                aufgabe.append("0");
            }            
        }        
        return aufgabe.toString();
    }       
}
