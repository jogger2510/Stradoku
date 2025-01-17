/*
 * PrintStrSeite.java ist Teil des Programmes kodelasStradoku
 * Erzeugt am:           18.10.2010 19:30
 * Zuletzt geändert am:  19.06.2019 22:50
 * 
 * Copyright (C) Konrad Demmel, 2010 - 2020
 */

package stradoku;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * Erzeugt und druckt eine Seite DIN A4 mit vier Stradoku
 * Der Aufruf erfolgt über das Modul PrintStrSerie. Dort erfolgt auch
 * die Initialisierung des Druckers.
 */
public class PrintStrSeite implements Printable {

    private static final int RES_MPL = 4;            // 1 = 72 dpi; 4 = 288 dpi
    private static final int LINKS = 0;
    private static final int RECHTS = 1190;
    private static final int OBEN = 0;
    private static final int UNTEN = 1680;
    // Koordinaten für die Ausgabe der vier Felder
    private static final Point[] POS = {
        new Point(LINKS, OBEN),
        new Point(RECHTS, OBEN),
        new Point(LINKS, UNTEN),
        new Point(RECHTS, UNTEN)};
    // Korrekturwerte für die Ausgabe der Sperrzellen und Ziffern
    private static final int[] KWERT = 
            {122, 236, 350, 464, 578, 692, 806, 920, 1034};
    private String[] nDS = new String[3]; // kompletter Datensats für ein Stradoku
    private final String[][] straInfo;  // Datensaätze für auszudruckende Stradokus
    private final int anzahl;
    private int dSI;    // Index für ein Stradoku in straInfo
    private int pos;
    private int pair;
    private static final Color SZ_DGRAU = new Color(100, 100, 100);

    /**
     * Konstruktor
     * @param inf Referenz auf die Klasse SdkInfo
     * Format des Aufgabenstrings mit 81 Zeichen:
     * "s320s06sgs20s0700005i000s020070sh00sbs00540sss00sa00047..."
     * - die Ziffer "0" steht für eine leere Zelle
     * - die Ziffern "1" - "9" stehen für Vorgabewerte
     * - der Buchstabe "s" steht für leere Sperrzelle
     * - die Busctaben "a" - "i" stehen für Sperrwerte ("a"=1, "i"=9)
     */
    public PrintStrSeite(String[][] inf) {
        straInfo = inf;
        anzahl = straInfo == null ? 1 : straInfo.length;
        dSI = 0;
        pair = 0;
    }

    /**
     * Überschreibt print() von Printable().
     *
     * @param g Graphik-Objekt
     * @param pf Seitenformat
     * @param iPage Seitenindex
     * @return PAGE_EXISTS wenn Seite gedruckt, sonst NO_SUCH_PAGE throws PrinterException
     * @throws java.awt.print.PrinterException Drucker-Fehler abfangen
     */
    @Override
    public int print(Graphics g, PageFormat pf, int iPage)
            throws PrinterException {
        int fontSize;
        if (iPage != 0) {
            return NO_SUCH_PAGE;
        }
        pair++;
        if (pair % 2 != 0) {
            return PAGE_EXISTS;
        }
        try {
            Graphics2D g2 = (Graphics2D) g;
            g2.translate(17, 17);               // Koordinatensystem verschieben
            g2.scale(1.0 / RES_MPL, 1.0 / RES_MPL);
            g.setColor(Color.black);
            for (pos = 0; pos < 4; pos++) {
                int x = POS[pos].x;
                int y = POS[pos].y;
                zeichneStrFeld(g2, x, y);
                if (straInfo == null) {
                    nDS = null;
                }
                else {
                    nDS = straInfo[dSI];
                }
                if (dSI + 1 < anzahl) {                   
                    dSI++;
                }
                fontSize = 12;
                g.setFont(new Font("Arial", Font.PLAIN, fontSize * RES_MPL));
                setzeTitel(g2, x, y);
                fontSize = 18;
                g.setFont(new Font("Arial", Font.BOLD, fontSize * RES_MPL));
                setzeWerte(g2, x, y);
            }
            g.dispose();
        }
        catch (Exception ex) {
            throw new PrinterException(ex.getMessage());
        }

        return PAGE_EXISTS;
    }

    /**
     * Zeichnet ein leeres Stradokufeld.
     * @param g2 Graphikobjekt
     * @param x0 Nullpunkt für X-Koordinaten
     * @param y0 Nullpunkt für Y-Koordinaten
     */
    private void zeichneStrFeld(Graphics g2, int x0, int y0) {
        // alle waagrechten Linien
        g2.fillRect(x0, y0 + 300, 1044, 10);
        g2.fillRect(x0, y0 + 422, 1044, 2);
        g2.fillRect(x0, y0 + 536, 1044, 2);
        g2.fillRect(x0, y0 + 650, 1044, 2);
        g2.fillRect(x0, y0 + 764, 1044, 2);
        g2.fillRect(x0, y0 + 878, 1044, 2);
        g2.fillRect(x0, y0 + 992, 1044, 2);
        g2.fillRect(x0, y0 + 1106, 1044, 2);
        g2.fillRect(x0, y0 + 1220, 1044, 2);
        g2.fillRect(x0, y0 + 1334, 1044, 10);
        // alle senkrechten Linien
        g2.fillRect(x0, y0 + 300, 10, 1044);
        g2.fillRect(x0 + 122, y0 + 300, 2, 1044);
        g2.fillRect(x0 + 236, y0 + 300, 2, 1044);
        g2.fillRect(x0 + 350, y0 + 300, 2, 1044);
        g2.fillRect(x0 + 464, y0 + 300, 2, 1044);
        g2.fillRect(x0 + 578, y0 + 300, 2, 1044);
        g2.fillRect(x0 + 692, y0 + 300, 2, 1044);
        g2.fillRect(x0 + 806, y0 + 300, 2, 1044);
        g2.fillRect(x0 + 920, y0 + 300, 2, 1044);
        g2.fillRect(x0 + 1034, y0 + 300, 10, 1044);
    }

    /**
     * Setzt die Überschrift für ein Stradokufeld.
     * @param g2 Graphikobjekt
     * @param x0 Nullpunkt für X-Koordinaten
     * @param y0 Nullpunkt für Y-Koordinaten
     */
    private void setzeTitel(Graphics g2, int x0, int y0) {
        if (nDS == null) {
            return;
        }
        g2.drawString(" Stradoku "
                     + nDS[0] + " - Level "
                     + nDS[2], x0 + 255, y0 + 180);  // mit x0 + 150 in Mitte
    }

    /**
     * Setzt die Werte in ein Stradokufeld.
     * @param g2 Graphikobjekt
     * @param x0 Nullpunkt für X-Koordinaten
     * @param y0 Nullpunkt für Y-Koordinaten
     */
    private void setzeWerte(Graphics g2, int x0, int y0) {
        if (nDS == null) {
            return;
        }
        String aufgabe = nDS[1];
        for (int i = 0; i <= 80; i++) {                 // für alle Zellen
            char w = aufgabe.charAt(i);                 // Zeichen lesen
            if (w == '0') {                             // leere Zelle
                continue;
            }
            if (w < 'a') {                              // Vorgabewert
                g2.setColor(Color.black);
                g2.drawString(
                    "" + w, x0 + KWERT[i%9]-76, y0+KWERT[i/9]+272 );
            }
            else {
                g2.setColor(SZ_DGRAU);
                g2.fillRect(x0+KWERT[i%9]-112, y0+KWERT[i/9]+188, 112, 112);
                if (w < 's') {                          // Sperrwert
                    w -= '0';
                    g2.setColor(Color.white);
                    g2.drawString(
                    "" + w, x0 + KWERT[i%9]-76, y0+KWERT[i/9]+272 );
                }
            }
        }
        g2.setColor(Color.black);
    }
}
