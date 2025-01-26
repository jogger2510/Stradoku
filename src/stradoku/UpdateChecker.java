/*
 * UpdateChecker.java ist Teil des Programmes Stradoku
 * 
 * Copyright (C) 2025 Gero Dittmer
 */

package stradoku;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.HttpURLConnection;
import java.net.URI;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Checks
 */
public class UpdateChecker implements ActionListener {
    
    private boolean success;
    private final int prversion;
    private boolean updateavailable;
    private final Timer timer = new Timer(4 * 1000, this);
    private final Stradoku strApp;

    /**
     * Konstruktor
     * @param mf Referenz zu Hauptklasse
     */
    public UpdateChecker(Stradoku mf, String pver) {
        timer.setRepeats(false);
        timer.start();
        prversion = getNum(pver);
        strApp = mf;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String spversion = downloadVersion("https://github.com/jogger2510/Stradoku/raw/refs/heads/main/version.txt");
        if (success && spversion.length() >= 3) {
            updateavailable = prversion < getNum(spversion);
            if (updateAvailable()) strApp.setUpdate();
        }
    }

    public boolean updateAvailable () { return updateavailable; }

    public boolean checked () { return success; }
    
    /**
     * Liefert den Inhalt aus der angegebenen Hyperlink-Quelle
     * @param url Hyperlink-Quelle
     * @return Text aus Hyperlink-Quelle
     */
    private String downloadVersion(String url) {
        success = true;
        String version = "";
        try {
            HttpURLConnection con = (HttpURLConnection) new URI(url).toURL().openConnection();
            con.connect();
            InputStream stream = con.getInputStream();
            InputStreamReader isr =
                    new InputStreamReader(stream, "ISO-8859-1");
            StringBuilder buffer = new StringBuilder();
            int c = isr.read();
            while (c != -1) {
                buffer.append((char) c);
                c = isr.read();
            }
            version = buffer.toString();
        } catch (Exception e) {
            success = false;
            timer.setDelay(3600 * 1000);
            timer.start();
        }
        return version;
    }
    
    /**
     * Wertet String aus zu numerischem Wert
     * @param version auszuwertender String
     * @return numerischer Wert
     */
    private int getNum(String version) {
        StringBuilder tmp = new StringBuilder();
        int len = version.length();
        int i = 0;
        char c;
        while (i < len) {
            c = version.charAt(i++);
            if (c >= '0' && c <= '9') {
                tmp.append(c);
            }
        }
        if (tmp.length() == 2) {
            tmp.append("0");
        }
        version = tmp.toString();
        try {
            return Integer.parseUnsignedInt(version);
        } catch (NumberFormatException nfe) {
        }
        return 0;
    }
}
