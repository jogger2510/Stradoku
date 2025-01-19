/*
 * ThreadPrintStrSerie.java ist Teil des Programmes kodelasStradoku
 * Erzeugt am:          19.10.2010 22:40
 * Letzte Änderung am:  05.08.2020 22:50
 * 
 * Copyright (C) Konrad Demmel, 2010 - 2021
 */

package stradoku;

import javax.swing.JOptionPane;

/**
 * Übernimt den Ausdruck von Stradokus
 */
class ThreadPrintStrSerie extends Thread {

    private final HinweisWarten msgHinweisWarten;
    private final int anzahl;
    private final ListenFrame liste;
    private final ListenModel listeDaten;
    private String[][] stradokuInfo;

    /**
     * Konstruktor
     * @param lst Referenz zur Klasse des Listenfensters
     * @param lD Referenz zum ListenModel
     * @param msg Referenz zur HinweisWarten Klasse
     * @param anz Anzahl der einzulesenden Stradokus
     */
    ThreadPrintStrSerie(
            ListenFrame lst, ListenModel lD, HinweisWarten msg, int anz) {
        liste = lst;
        listeDaten = lD;
        msgHinweisWarten = msg;
        anzahl = anz;
    }

    @Override
    public void run() {
        if (anzahl > 0) {
            stradokuInfo = new String[anzahl][3];
            int i = 0;
            for (int z = 0; z < listeDaten.getRowCount(); z++) {
                if (liste.isSelect(z)) {
                    stradokuInfo[i][0] = liste.getNummer(z);
                    stradokuInfo[i][1] = liste.getStradoku(z);
                    stradokuInfo[i][2] = "" + liste.getLevel(z);
                    i++;
                }
            }
        }                      
        else {
            stradokuInfo = null;
        }
        PrintStrSerie pSS = new PrintStrSerie();
        if (!pSS.printStradokuSerie(stradokuInfo)) {
            JOptionPane.showMessageDialog(null,
                    "Unerwarteter Fehler beim Stradoku-Ausruck!",
                    "Hinweis", 1);
        }
        liste.deselectPrintSdk();
        msgHinweisWarten.setVisible(false);
    }
}
