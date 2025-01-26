/*
 * ThreadPrintStrSerie.java ist Teil des Programmes kodelasStradoku
 * Erzeugt am:          19.10.2010 22:40
 * Letzte Änderung am:  05.08.2020 22:50
 * 
 * Copyright (C) Konrad Demmel, 2010 - 2021
 */

package stradoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Übernimt den Ausdruck von Stradokus
 */
class ThreadPrintStrSerie extends Thread {

    private final int anzahl;
    private final ListenFrame liste;
    private final ListenModel listeDaten;
    private String[][] stradokuInfo;

    /**
     * Konstruktor
     * @param lst Referenz zur Klasse des Listenfensters
     * @param lD Referenz zum ListenModel
     * @param msg Referenz zur HinweisWarten Klasse
     * @param anz Anzahl der einzulesenden Stradokus
     */
    ThreadPrintStrSerie(
            ListenFrame lst, ListenModel lD, int anz) {
        liste = lst;
        listeDaten = lD;
        anzahl = anz;
    }

    @Override
    public void run() {
        if (anzahl > 0) {
            stradokuInfo = new String[anzahl][3];
            int i = 0;
            for (int z = 0; z < listeDaten.getRowCount(); z++) {
                if (liste.isSelect(z)) {
                    stradokuInfo[i][0] = liste.getNummer(z);
                    stradokuInfo[i][1] = liste.getStradoku(z);
                    stradokuInfo[i][2] = "" + liste.getLevel(z);
                    i++;
                }
            }
        }                      
        else {
            stradokuInfo = null;
        }
        PrintStrSerie pSS = new PrintStrSerie();
        boolean fail;
        if (fail = !pSS.printStradokuSerie(stradokuInfo, liste)) {
            if (!System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                String home = liste.strApp.getHomePath();
                if (liste.strApp.getPostScript()) {
                    ProcessBuilder builder = new ProcessBuilder();
                    builder.command("lpr", "-l", "PrintFile.ps");
                    builder.redirectErrorStream(true);
                    builder.directory(new File(home));
                    try {
                        Process process = builder.start();
                        BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {System.out.println(line);}
                       int exitCode = process.waitFor();
                       if (exitCode == 0) {
                            File file = new File(home, "PrintFile.ps");
                            file.delete();
                            fail = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            // JOptionPane.showMessageDialog(null,
            //         "Unerwarteter Fehler beim Stradoku-Ausruck!",
            //         "Hinweis", 1);
        }
        if (fail) liste.strApp.statusBarHinweis.setText("Druckfehler: Druckdatei gespeichert!");
        liste.deselectPrintSdk();
    }
}