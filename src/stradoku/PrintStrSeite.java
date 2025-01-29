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

    private static final int Raster = 102;
    private static final int Aussenlinie = 6;
    private static final int Innenlinie = 2;
    private static final int Hoehe = 10 * Raster + 2 * Aussenlinie - Innenlinie;
    private static final int Breite = 9 * Raster + 2 * Aussenlinie - Innenlinie;
    private String[] nDS = new String[3]; // kompletter Datensats für ein Stradoku
    private final String[][] straInfo;  // Datensaätze für auszudruckende Stradokus
    private final int anzahl;
    private final int seiten;
    private final int perPage;
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
    public PrintStrSeite(String[][] inf, int perPage) {
        straInfo = inf;
        anzahl = straInfo == null ? 1 : straInfo.length;
        seiten = (anzahl + perPage - 1) / perPage;
        this.perPage = perPage;
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
        if (iPage >= seiten) {
            return NO_SUCH_PAGE;
        }
        int base = iPage * perPage;
        try {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate( pf.getImageableX(), pf.getImageableY() ); // Koordinatensystem verschieben
            g.setColor(Color.black);
            // Höhe eines Felds 1160
            // Breite eines Felds 1044
            // 
            double iWdth = pf.getImageableWidth();
            double iHght = pf.getImageableHeight();
            boolean portrait = iHght > iWdth;
            int totalheight = 0;
            int totalwidth = 0;
            switch (perPage) {
                case 1:
                    totalheight = Hoehe;
                    totalwidth = Breite;
                    break;
                    case 2: if (portrait) {
                        totalheight = 2 * Hoehe + Raster;
                        totalwidth = Breite;
                    } else {
                        totalheight = Hoehe;
                        totalwidth = 2 * Breite + Raster;
                    }
                    break;
                    case 3: if (portrait) {
                        totalheight = 3 * Hoehe + 2 * Raster;
                        totalwidth = Breite;
                    } else {
                        totalheight = Hoehe;
                        totalwidth = 3 * Breite + 2 * Raster;
                    }
                    break;
                case 4:
                    totalheight = 2 * Hoehe + Raster;
                    totalwidth = 2 * Breite + Raster;
                    break;
                default:
                    break;
            }
            double iResMul = Math.max(totalwidth / iWdth, totalheight / iHght);
            g2.scale(1.0 / iResMul, 1.0 / iResMul);
            int x = 0, y = 0;
            for (int pos = 0; pos < perPage; pos++) {
                switch (perPage) {
                    case 1:
                        x = 0;
                        y = 0;
                        break;
                        case 2: if (portrait) {
                            y = pos * (Hoehe + Raster);
                            x = 0;
                        } else {
                            y = 0;
                            x = pos * (Breite + Raster);
                        }
                        break;
                        case 3: if (portrait) {
                            y = pos * (Hoehe + Raster);
                            x = 0;
                        } else {
                            y = 0;
                            x = pos * (Breite + Raster);
                        }
                        break;
                    case 4:
                        x = pos % 2 == 0 ? 0 : Breite + Raster;
                        y = pos / 2 * (Hoehe + Raster);
                        break;
                    default:
                        break;
                }
                zeichneStrFeld(g2, x, y);
                int index = base;
                if (straInfo == null || base++ >= anzahl) 
                    continue;
                nDS = straInfo[index];
                setzeTitel(g2, x, y, new Font("Serif", Font.PLAIN, 36));
                setzeWerte(g2, x, y, new Font("SansSerif", Font.PLAIN, 72));
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
        y0 += Raster;
        g2.fillRect(x0, y0, Breite, Aussenlinie);
        int y1 =  y0 + Raster + Aussenlinie - Innenlinie;
        g2.fillRect(x0, y1, Breite, Innenlinie);
        g2.fillRect(x0, y1 += Raster, Breite, Innenlinie);
        g2.fillRect(x0, y1 += Raster, Breite, Innenlinie);
        g2.fillRect(x0, y1 += Raster, Breite, Innenlinie);
        g2.fillRect(x0, y1 += Raster, Breite, Innenlinie);
        g2.fillRect(x0, y1 += Raster, Breite, Innenlinie);
        g2.fillRect(x0, y1 += Raster, Breite, Innenlinie);
        g2.fillRect(x0, y1 += Raster, Breite, Innenlinie);
        g2.fillRect(x0, y1 + Raster, Breite, Aussenlinie);
        // alle senkrechten Linien
        g2.fillRect(x0, y0, Aussenlinie, Breite);
        g2.fillRect(x0 += Raster + Aussenlinie - Innenlinie, y0, Innenlinie, Breite);
        g2.fillRect(x0 += Raster, y0, Innenlinie, Breite);
        g2.fillRect(x0 += Raster, y0, Innenlinie, Breite);
        g2.fillRect(x0 += Raster, y0, Innenlinie, Breite);
        g2.fillRect(x0 += Raster, y0, Innenlinie, Breite);
        g2.fillRect(x0 += Raster, y0, Innenlinie, Breite);
        g2.fillRect(x0 += Raster, y0, Innenlinie, Breite);
        g2.fillRect(x0 += Raster, y0, Innenlinie, Breite);
        g2.fillRect(x0 + Raster, y0, Aussenlinie, Breite);
    }

    /**
     * Setzt die Überschrift für ein Stradokufeld.
     * @param g2 Graphikobjekt
     * @param x0 Nullpunkt für X-Koordinaten
     * @param y0 Nullpunkt für Y-Koordinaten
     */
    private void setzeTitel(Graphics g2, int x0, int y0, Font font) {
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        centerText(g2, " Stradoku " + nDS[0] + " - Level " + nDS[2], fm, x0, Breite, y0 + (Raster - fm.getHeight()) / 2 + fm.getAscent());
    }

    /**
     * Setzt die Werte in ein Stradokufeld.
     * @param g2 Graphikobjekt
     * @param x0 Nullpunkt für X-Koordinaten
     * @param y0 Nullpunkt für Y-Koordinaten
     */
    private void setzeWerte(Graphics g2, int x0, int y0, Font font) {
        String aufgabe = nDS[1];
        int i = 0;
        y0 += Aussenlinie + Raster;
        x0 += Aussenlinie;
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int vertical = y0 + (Raster - Innenlinie - fm.getHeight()) / 2 + fm.getAscent();
        for (int y = 0; y < 9; y++) {
            int x1 = x0;
            for (int x = 0; x < 9; x++) {
                char w = aufgabe.charAt(i++);                 // Zeichen lesen
                if (w != '0') {                             // leere Zelle
                    if (w < 'a') {                              // Vorgabewert
                        g2.setColor(Color.black);
                        centerText(g2, "" + w, fm, x1, Raster - Innenlinie , vertical);
                    }
                    else {
                        g2.setColor(SZ_DGRAU);
                        g2.fillRect(x1, y0, Raster - Innenlinie, Raster - Innenlinie);
                        if (w < 's') {                          // Sperrwert
                            w -= '0';
                            g2.setColor(Color.white);
                            centerText(g2, "" + w, fm, x1, Raster - Innenlinie , vertical);
                        }
                    }
                }
                x1 += Raster;
            }
            y0 += Raster;
            vertical += Raster;
        }
        g2.setColor(Color.black);
    }

    private void centerText(Graphics g2, String s, FontMetrics fm, int left, int width, int y) {
        int x = (width - fm.stringWidth(s)) / 2;
        g2.drawString(s, x + left, y);
    }
}
