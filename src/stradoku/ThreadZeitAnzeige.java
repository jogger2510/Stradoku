/*
 * ThreadZeitAnzeige.java ist Teil des Programmes kodelasStradoku
 * Erzeugt am:              18.10.2011, 19:35
 * Letzte Änderung am:      20.03.2022, 19:50
 * 
 * Copyright (C) Konrad Demmel, 2010 - 2018
 */

package stradoku;

/**
 * Übernimt die Zeitanzeige in der Statuszeile
 */
public class ThreadZeitAnzeige extends Thread {
    
    private final Stradoku strApp;
    private long startZeit;
    private long loesungsZeit;
    private boolean anzeigen;

    /**
     * Konstruktor
     * @param mf Referenz zu Hauptklasse
     */
    public ThreadZeitAnzeige(Stradoku mf) {
        strApp = mf;
    }

    /**
     * Steuert die Zeitanzeige
     */
    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        while(true) {
            try {
                sleep (1000);
                if(anzeigen) {
                    zeitAusgeben();
                }
            }
            catch (InterruptedException e) {
                // da machem wir gar nichts
            }
        }
    }

    /**
     * Gibt die Zeit aus
     */
    private void zeitAusgeben() {
        loesungsZeit = System.currentTimeMillis() - startZeit;
        long zeit = loesungsZeit / 60000;       // zu Minuten umrechnen
        int std = (int)zeit / 60;
        int min = (int)zeit - std * 60;
        strApp.setZeit(std, min);
    }

    /**
     * Startet die Zeitanzeige
     * @param sz Startzeit
     */
    public void zeitAnzeigeStart(long sz) {
        startZeit = sz;
        anzeigen = true;
        zeitAusgeben();
        if (getState() == Thread.State.NEW) {
            start();
        }
    }

    /**
     * Bricht die Zeitanzeige ab
     * @return bisher verbrauchte Zeit
     */
    public long zeitAnzeigeStop() {   
        zeitAusgeben();
        anzeigen = false;
        return loesungsZeit;
    }

    /**
     * Liefert die verbrauchte Zeit
     * @return verbrauchte Zeit
     */
    public long getLoesungsZeit() {
        return System.currentTimeMillis() - startZeit;
    }
}
