/**
 * EindeutigkeitChecken.java ist Teil des Programmes kodelasStradoku
 * 
 * Erzeugt am:                  14.03.2019 19:30
 * Letzte Änderung:             31.03.2020 15:00         
 * 
 * Copyright (C) Konrad Demmel, 2019-2021
 */

package stradoku;

import javax.swing.SwingWorker;
import static stradoku.GlobaleObjekte.LWF;
import static stradoku.GlobaleObjekte.LWRT;
import static stradoku.GlobaleObjekte.kopieren;

/**
 * Überprüft Stradoku auf Eindeutigkeit
 */
public class EindeutigkeitChecken extends SwingWorker<Boolean, Object>
                                implements GlobaleObjekte {
    private LevelSolver strLoeser;
    private final int[] aufgabe;
    private final int[] loesung;
    private boolean eindeutig;
    HinweisWarten hnwDialog;
    
    /**
     * Konstruktor
     * @param mf Referenz auf Hauptklasse der Anwendung
     * @param hnw Referenz auf Warten-Dialog
     * @param af Bitcodierte Stradoku Aufgabe
     * @param ls Bitcodierte Stradoku Lösung
     */
    EindeutigkeitChecken(Stradoku mf, HinweisWarten hnw, int[] af, int[] ls) {
        aufgabe = new int[81];
        kopieren(af, aufgabe, true);
        loesung = new int[81];
        kopieren(ls, loesung, false);
        eindeutig = true;
        if (hnw != null){
            hnwDialog = hnw;
            hnwDialog.zeigeHinweis("<html><center><b>Eindeutigkeit wird überprüft."
                    + "<br><br>Bitte solange warten.</b></center></html>");        
        }
    }
    
    /**
     * Überschreibt die entsprechende Methode der Superklasse SwingWorker
     * und führt im Hintergrund die Eindeutigkeitsprüfung für ein Stradoku aus.
     * Sie steuert außerdem ein Fortschrittsanzeige.
     * @return true wenn eindeutig, sonst false
     */
    @Override
    public Boolean doInBackground() {
        int afg[] = new int[81];
        int lsg[] = new int[81];
        kopieren(aufgabe, afg, false);
        for (int i = 0; i < 81; i++) { 
            hnwDialog.setFortschritt((int)(i*1.25));
            int lw = loesung[i] & ~LWF;
            if (loesung[i] <= LWRT && loesung[i] > 9) {
                for (int k = 1; k <= 9; k++) {
                    if (lw != k) {
                        afg[i] = k;
                        strLoeser = new LevelSolver(null, afg, lsg, false);
                        int lev = strLoeser.loeseAufgabe();
                        if (lev >= 0) {
                            eindeutig = false;
                            hnwDialog.setVisible(false);
                            return eindeutig;
                        } else {
                            kopieren(aufgabe, afg, false);
                        }
                    }
                }
            }
        }
        eindeutig = true;
        hnwDialog.setVisible(false);
        return eindeutig;
    }    
    
    /**
     * Überprüft für jede zu lösende Zelle, ob das Stradoku 
     * mit mehr als einem Wert gelöst werden kann.
     * Wird nur beim Import einer Liste verwendet
     * @return true wenneindeutig, sonst false
     */
    public Boolean checkEindeutigkeit() {
        int afg[] = new int[81];
        int lsg[] = new int[81];
        kopieren(aufgabe, afg, false);
        for (int i = 0; i < 81; i++) {
            int lw = loesung[i] & ~LWF;
            if (loesung[i] <= LWRT && loesung[i] > 9) {
                for (int k = 1; k <= 9; k++) {
                    if (lw != k) {
                        afg[i] = k;
                        strLoeser = new LevelSolver(null, afg, lsg, false);
                        int lev = strLoeser.loeseAufgabe();
                        if (lev >= 0) {
                            return false;
                        } else {
                            kopieren(aufgabe, afg, false);
                        }
                    }
                }
            }
        }
        return true;
    }    
    
    /**
     * Beantwortet für das zuletzt überprüfte Stradoku die Frage auf Eindeutigkeit.
     * @return true wenn eindeutig, sonst false
     */
    public boolean is_eindeutig() {
        return eindeutig;
    }
}
