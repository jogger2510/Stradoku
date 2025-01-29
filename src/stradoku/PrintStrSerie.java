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

/**
 * Steuert den Serienausdruck von Stradoku-Aufgben
 */
public class PrintStrSerie {

    /**
     * Initialisiert den Drucker und steuert den seitenweisen Ausdruck von
     * in der Regel vier Stradoku.
     * @param info Referenz auf die Klasse StrInfo mit den Daten für den Ausdruck
     * @param liste Referenz auf Frame
     * @return true wenn Drucker initialisiert werden konnte, sonst false
     */
    public boolean printStradokuSerie(String[][] info, ListenFrame liste, int perPage) {
        // Set DocFlavor and print attributes:
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        // aset.add(MediaSizeName.ISO_A4);
        try {
            // Drucker initialisieren
            PrintService prserv = null;
            PrintService[] prservices =  PrintServiceLookup.lookupPrintServices(flavor, null);
            if (prservices != null && prservices.length > 0)
                prserv = prservices[0];
            PrintService prservDflt = PrintServiceLookup.lookupDefaultPrintService();
            if (prservDflt != null)
                prserv = prservDflt;
            if (prserv != null) {
                aset.add(liste.strApp.PrintArea);
                prserv = ServiceUI.printDialog(null, 50, 50, prservices, prserv, flavor, aset);
                if (prserv == null) return true;
                try {
                    liste.strApp.PrintArea = (MediaPrintableArea) aset.get(MediaPrintableArea.class);
                } catch (Exception e) {
                }
                PrintStrSeite stradokuSeite = new PrintStrSeite(info, perPage);
                DocPrintJob dpj = prserv.createPrintJob();
                Doc doc = new SimpleDoc(stradokuSeite, flavor, null);
                dpj.print(doc, aset);
                return true;
            }
        } catch (PrintException pe) { pe.printStackTrace(); }
        FileOutputStream fos = null;
        try {
            StreamPrintServiceFactory[] prservFactories = StreamPrintServiceFactory.lookupStreamPrintServiceFactories(
                flavor, DocFlavor.BYTE_ARRAY.POSTSCRIPT.getMimeType() );
            if( null == prservFactories || 0 >= prservFactories.length ) return false;
            fos = new FileOutputStream(liste.strApp.getHomePath() + File.separator + "PrintFile.ps" );
            StreamPrintService sps = prservFactories[0].getPrintService(fos);
            PrintStrSeite stradokuSeite = new PrintStrSeite(info, perPage);
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
