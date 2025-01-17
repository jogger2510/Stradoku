/*
 * UpdateCheck.java ist Teil des Programmes kodelasStradoku
 * Erzeugt am:                  09.12.2011 19:23
 * Letzte Änderung:             06.02.2020 14:00
 * 
 * Copyright (C) Konrad Demmel, 2010 - 2020
 */

package stradoku;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JOptionPane;

/**
 * Update-Abfrage
 */
public class UpdateCheck extends Thread implements Runnable{

    private final String programmVersion;
    private final Stradoku mainFrame;
    private final int prversion;

    /**
     * Konstruktor
     * @param mf Referenz auf Hauptfenster
     * @param pver String zu Programmversion
     */
    UpdateCheck(Stradoku mf, String pver) {
        mainFrame = mf;
        programmVersion = pver;
        prversion = getNum(programmVersion);
    }

    /**
     * Ermittelt die Update-Situation und gibt Ergebnis aus.
     */
    @Override
    public void run() {
        String spversion = downloadVersion("http://kodela.w4f.eu/strpversion");
        if (spversion.length() >= 3) {
            int stand = 0;
            if (prversion < getNum(spversion)) {
                stand = 1;
            }
            // Programmversion und eingetragene letzte Version sind gleich
            if (stand == 0) {
                JOptionPane.showMessageDialog(mainFrame, "<html><b>" +
                        "Sie haben die neueste Version von kodelasStradoku.",
                        "Hinweis", 1);
            }
            else {
                UpdateDialog upDlg = new UpdateDialog(mainFrame, programmVersion);
                upDlg.zeigeDialog();
            }
        }
        else {
            JOptionPane.showMessageDialog(mainFrame, "<html><b>" +
                    "Leider konnte Ihre Programm-Version mit den Daten<br>" +
                    "auf dem Programm-Server nicht abgeglichen werden.",
                    "Hinweis", 1);
        }
    }

    /**
     * Liefert den Inhalt aus der angegebenen Hyperlink-Quelle
     * @param url Hyperlink-Quelle
     * @return Text aus Hyperlink-Quelle
     */
    private String downloadVersion(String url) {
        String version = "";
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url)
                .openConnection();
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
        } catch (IOException e) {
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
        return Integer.parseUnsignedInt(version);
    }
}
