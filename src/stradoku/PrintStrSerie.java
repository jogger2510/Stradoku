/*
 * PrintStrSerie.java ist Teil des Programmes kodelasStradoku
 * Erzeugt am:                  18.10.2010 23:50
 * Zuletzt geändert:            12.12.2017 12:00
 * 
 * Copyright (C) Konrad Demmel, 2010 - 2018
 */

package stradoku;

import java.io.File;
import java.io.FileOutputStream;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;

/**
 * Steuert den Serienausdruck von Stradoku-Aufgben
 */
public class PrintStrSerie {

    /**
     * Initialisiert den Drucker und steuert den seitenweisen Ausdruck von
     * in der Regel vier Stradoku.
     * @param info Referenz auf die Klasse StrInfo mit den Daten für den Ausdruck
     * @return true wenn Drucker initialisiert werden konnte, sonst false
     */
    public boolean printStradokuSerie(String[][] info, ListenFrame liste) {
        // Set DocFlavor and print attributes:
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        // Druckbereich mit Punkt links oben (x, y) sowie Breite 
        // und Höhe in Millimeter festlegen.
        aset.add(new MediaPrintableArea(6, 6, 200, 285, MediaPrintableArea.MM));
        aset.add(MediaSizeName.ISO_A4);
        try {
            // Drucker initialisieren
            PrintService prserv = null;
            PrintService[] prservices =  PrintServiceLookup.lookupPrintServices(flavor, aset);
            if (prservices != null && prservices.length > 0)
                prserv = prservices[0];
            PrintService prservDflt = PrintServiceLookup.lookupDefaultPrintService();
            if (prservDflt != null)
                prserv = prservDflt;
            if (prserv != null) {
                prserv = ServiceUI.printDialog( null, 50, 50, prservices, prservDflt, null, aset );
                if (prserv != null) {
                    PrintStrSeite stradokuSeite = new PrintStrSeite(info);
                    DocPrintJob dpj = prserv.createPrintJob();
                    Doc doc = new SimpleDoc(stradokuSeite, flavor, null);
                    dpj.print(doc, aset);
                    return true;
                }
            }
        } catch (PrintException pe) {
        }
        FileOutputStream fos = null;
        try {
            StreamPrintServiceFactory[] prservFactories = StreamPrintServiceFactory.lookupStreamPrintServiceFactories(
                flavor, DocFlavor.BYTE_ARRAY.POSTSCRIPT.getMimeType() );
            if( null == prservFactories || 0 >= prservFactories.length ) return false;
            fos = new FileOutputStream(liste.strApp.getHomePath() + File.separator + "PrintFile.ps" );
            StreamPrintService sps = prservFactories[0].getPrintService( fos );
            PrintStrSeite stradokuSeite = new PrintStrSeite(info);
            DocPrintJob dpj = sps.createPrintJob();
            Doc doc = new SimpleDoc(stradokuSeite, flavor, null);
            dpj.print(doc, aset);
        } catch( Exception ie ) {}
        finally {
            try {
                fos.close();
            } catch (Exception e) {}
        }
        return false;
    }
}
