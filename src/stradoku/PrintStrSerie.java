/*
 * PrintStrSerie.java ist Teil des Programmes kodelasStradoku
 * Erzeugt am:                  18.10.2010 23:50
 * Zuletzt geändert:            12.12.2017 12:00
 * 
 * Copyright (C) Konrad Demmel, 2010 - 2018
 */

package stradoku;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;

/**
 * Steuert den Serienausdruck von Stradoku-Aufgben
 */
public class PrintStrSerie {

    private int seiten;

    /**
     * Initialisiert den Drucker und steuert den seitenweisen Ausdruck von
     * in der Regel vier Stradoku.
     * @param info Referenz auf die Klasse StrInfo mit den Daten für den Ausdruck
     * @return true wenn Drucker initialisiert werden konnte, sonst false
     */
    public boolean printStradokuSerie(String[][] info) {
        if (info == null) {
            seiten = 1;
        }
        else {
            seiten = (info.length + 3) / 4;
        }
        // Set DocFlavor and print attributes:
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        // Druckbereich mit Punkt links oben (x, y) sowie Breite 
        // und Höhe in Millimeter festlegen.
        aset.add(new MediaPrintableArea(6, 6, 200, 285, MediaPrintableArea.MM));
        aset.add(MediaSizeName.ISO_A4);
        try {
            // Drucker initialisieren
            PrintService prservDflt = 
                    PrintServiceLookup.lookupDefaultPrintService();
            PrintService[] prservices = 
                    PrintServiceLookup.lookupPrintServices(flavor, aset);
            if (prservices == null || prservices.length <= 0) {
                if (prservDflt == null) {
                    return false;
                }
            }
            PrintService prserv = 
                    PrintServiceLookup.lookupDefaultPrintService();
            if (prserv != null) {
                PrintStrSeite stradokuSeite = new PrintStrSeite(info);
                for (int s = 0; s < seiten; s++) {
                    DocPrintJob dpj = prserv.createPrintJob();
                    Doc doc = new SimpleDoc(stradokuSeite, flavor, null);
                    dpj.print(doc, aset);
                }
            }
            else {
                return false;
            }
        } catch (PrintException pe) {
            return false;
        }
        return true;
    }
}
