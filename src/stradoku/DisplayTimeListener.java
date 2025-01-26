/*
 * DisplayTimeListener.java ist Teil des Programmes Stradoku
 * 
 * Copyright (C) 2025 Gero Dittmer
 */

package stradoku;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Übernimt die Zeitanzeige in der Statuszeile
 */
public class DisplayTimeListener implements ActionListener {
    
    private final Stradoku strApp;
    private final Timer timer = new Timer(1000, this);
    private long startZeit;
    private long loesungsZeit;

    /**
     * Konstruktor
     * @param mf Referenz zu Hauptklasse
     */
    public DisplayTimeListener(Stradoku mf) {
        strApp = mf;
        timer.setRepeats(true); // immer wieder feuern!
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        zeitAusgeben();
    }

    /**
     * Gibt die Zeit aus
     */
    private void zeitAusgeben() {
        loesungsZeit = System.currentTimeMillis() - startZeit;
        strApp.setZeit((int)(loesungsZeit / 1000));
    }

    /**
     * Startet die Zeitanzeige
     * @param sz Startzeit
     */
    public void zeitAnzeigeStart(long sz) {
        timer.start();
        startZeit = sz;
        zeitAusgeben();
    }

    /**
     * Bricht die Zeitanzeige ab
     * @return bisher verbrauchte Zeit
     */
    public long zeitAnzeigeStop() {   
        timer.stop();
        zeitAusgeben();
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
