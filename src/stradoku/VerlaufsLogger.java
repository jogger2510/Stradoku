/* Verlaufslogger.java ist Teil des Programmes kodelasStradoku
 *
 * Erstellt am:                 05.01.2024 13:00    
 * Letzte Änderung:             
 *
 * Copyright (C) Konrad Demmel, 2024
 */
package stradoku;

import java.io.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Konrad
 */
public class VerlaufsLogger {
    
    File file;
    FileWriter logFile;    
    

    /**
     * Konstruktor
     * @param pfad
     */
    public VerlaufsLogger(String pfad) {
        file = new File(pfad, "verlauf.log");
        if (file.exists())
            file.delete();
    }
    
    public void logAusgabe(String txt) {
        try {
            logFile = new FileWriter(file, true);
            logFile.append(txt);
            logFile.close();
        } catch (IOException ex) {
            Logger.getLogger(VerlaufsLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
