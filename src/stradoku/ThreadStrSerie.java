/*
 * ThreadStrSerie.java ist Teil des Programmes kodelasStradoku

 * Erzeugt am:                  12.04.2010 17:29
 * Zuletzt geändert am:         05.02.2020 11:15
 * 
 * Copyright (C) Konrad Demmel, 2010 - 2020
 */

package stradoku;

/**
 * Steuert die Bereitstellung einer Stradoku-Serie. Die Stradoku werden von der 
 * Archiv Klasse abgerufen.
 */
class ThreadStrSerie extends Thread implements Runnable {

    private final int anzahl;
    private final int lev;
    private final ListenFrame strListe;
    private final Archiv archiv;
    private final int[] iafg;
    private final int[] straFeld;

    /**
     * Konstruktor initialisiert Serienerstellung.
     * @param arch Referenz für Archiv-Klasse 
     * @param sListe Referenz zur Stradoku-Liste
     * @param anz Anzahl der liefernden Stradokus
     * @param level Level der zu liefernden Stradokus
     */
    ThreadStrSerie(Archiv arch, ListenFrame sListe, int anz, int level) {
        archiv = arch;
        strListe = sListe;
        anzahl = anz;
        lev = level;
        iafg = null;
        straFeld = new int[81];
    }

    /**
     * Steuert die Erzeugung und Speicherung der Serie
     */
    @Override
    public void run() {
        int aktAnzahl;
        int max = 0;
        int startAnzahl = strListe.getAnzahl();
        startAnzahl = (startAnzahl > 0) ? startAnzahl : 0;        
        strListe.setSerie(true);
        do {
            String aufgabe = archiv.getAufgabe(iafg, lev);
            StradokuString2Matrix.makeStradokuString2Matrx(aufgabe, straFeld);
            // Aufgabe in Liste speichern
            char c = '0';
            int frz = 0;
            // freie Zellen zählen
            for(int j = aufgabe.length()-1; j >= 0; j--){
                if(aufgabe.charAt(j) == c) frz++;
            }
            strListe.addStradoku(aufgabe, "" + lev, frz, "");
            if (max++ > anzahl * 4) {
                break;
            }
            aktAnzahl = strListe.getAnzahl();
        } while (aktAnzahl - startAnzahl < anzahl);
        strListe.setAnzahl();
        strListe.setSerie(false);
        strListe.speichernListe();
    }
}
