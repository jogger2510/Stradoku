/**
 * StradokuString2Matrix.java ist Teil des Programmes kodelasStradoku
 * Erzeugt am:                  13.11.2017 23:00
 * Letzte Änderung:             29.01.2022 17:40       
 * 
 * Copyright (C) Konrad Demmel, 2017 - 2022
*/

package stradoku;

/**
 * Aufgabe: Umwandlung eines Strings mit einer Stradoku-Aufgabe in ein
 *          int[81]-Array mit allen Vorgabe- bzw. Sperrwerten und Sperrzellen
 *
 * Eingabeformat:
 * Einzeiliger Werte-Strings aus 2x81 Zeichen
 * In den ersten 81 Zeichen werden die Vorgabe- und Sperrwerte 1 - 9 erwartet.
 * Leere Zellen müssen mit 0 angezeigt werden.
 * In den zweiten 81 Zeichen werden die Sperrwerte mit 1 angezeigt.
 * Es darf auch eine URL vorangestellt sein, z.B. 
 * http://www.str8ts.com/str8ts.htm?bd=0000670045039000.......
 * 
 * 2 x 81 Zeichen
 * 
 * 000067004503900000001009600008000200...010011100100000000000100010011000...
 * 
 * oder 1 x 81 Zeichen
 * 
 * is50s086s0s000000s00h70ss4320s0007s0s403s500s0s0000e0070sa30s08s000040b0...
 * 
 * dabei steht 's' für eine leere Sperrzelle 
 * und die Buchstaben 'a' bis 'i' für Sperrzellen mit den Werten von 1 bis 9.
 * So steht 'a' für eine Sperrzelle mit dem Sperrwert 1.
 */
public class StradokuString2Matrix {
    // Bitflag für Sperrzellen
    private static final int SZELLE = 0x800000;
    
    /**
     * Aufgabe: einen Zeichen-String mit einer Stradoku-Aufgabe auszuwerten 
     * und als Ergebnis die Sperrzellen, Werte und Kandidaten in das 
     * Array aktSdk einzutragen.
     * @param init der auszuwertende Stringt
     * @param aufgabe 81er-int-Array für alle Zellinformationen
     * @return false, wenn Fehler aufgetren ist, sonst true
     */
    public static boolean makeStradokuString2Matrx(String init, int[] aufgabe) {
        boolean erfolg = true;
        int startpos = init.indexOf('=')+1;
        String str = init.substring(startpos);
        int len = str.length();
        // String im internen Format
        if (len == 81) {
            importStradokuStringIntern(str, aufgabe);
        // String aus Archiv
        } else if (len == 83) {
            str = str.substring(2);
            importStradokuStringIntern(str, aufgabe);
        // String im externen Format, auch als Link 
        } else if (len == 162) {
            importStradokuStringExtern(str, aufgabe);
        } else if (len == 243){
            String vwerte = str.substring(len-243, len-162);
            String szellen = str.substring(len-81);
            importStradokuStringExtern(vwerte + szellen, aufgabe);
        } else {
            erfolg = false;
        }            
        return erfolg;
    }
    
    /**
     * Aufgabe: Stradoku im internen Stringformat übernehmen 
     * @param init Aufgabenstring
     * @param aufgabe Int-Array für die Übergabe der aufgabe 
     */    
    private static void importStradokuStringIntern (String init, int[] aufgabe) {
        for (int i = 0; i < 81; i++) {
            char w = init.charAt(i);
            if (w == 's') {
                aufgabe[i] = SZELLE;
            }
            else if (w >= 'a' && w <= 'i') {
                aufgabe[i] = SZELLE | (w - 0x60);                 
            }
            else {
                aufgabe[i] = w - 0x30; 
            }
        }
    }
    
    /**
     * Aufgabe: Stradoku im externen Stringformat übernehmen 
     * @param init Aufgabenstring
     * @param aufgabe Int-Array für die Übergabe der aufgabe 
     */    
    private static void importStradokuStringExtern (String init, int[] aufgabe) { 
        String werte = init.substring(0, 81);
        String zellen = init.substring(81,162);
        for (int i = 0; i < 81; i++) {
            char w = werte.charAt(i);
            char z = zellen.charAt(i);
            if (z == '1') {
                if (w == '0') {
                    aufgabe[i] = SZELLE;
                } else {
                    aufgabe[i] = SZELLE + Character.getNumericValue(w);
                }
            } else {
                aufgabe[i] = Character.getNumericValue(w);
            }
        }        
    }   
}
