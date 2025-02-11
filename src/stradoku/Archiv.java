/**
 * Archiv.java ist Teil des Programmes kodelasStradoku
 *
 * Erstellt am:                 06.04.2018 19:00
 * Letzte Änderung:             27.01.2025 16:15
 *
 * Copyright (C) Konrad Demmel, 2019-2025
 */
package stradoku;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import static stradoku.GlobaleObjekte.rotiereFeld;
import static stradoku.GlobaleObjekte.spiegleFeld;
import static stradoku.GlobaleObjekte.tauscheZellWerte;

/**
 * Diese Klasse dient als Ersatz für einen Stradoku-Genarator. Sie liefert aus einer 
 * umfangreichen Sammlung von Stradoku-Aufgaben eine aus, die durch Rotation und Werte-Sturz
 * verfremdet wird. Damit ist es möglich, von einem Stradoku 16 Varianten zu erzeugen.
 *
 * Die Aufgaben stehen in komprimierter Form in einer externen gezipten Datei zur Verfügung,
 * die nach jeder Abfrage aktualisiert wird.
 *
 * Die Datensätze für eine Aufgabe bestehen aus Zeilen mit 85 Zeichen und sind wie folgt 
 * gegliedert:
 * 
 * - 1. Stelle für den Level 
 * - 2. Stelle für den Status der Veränderung
 * - 81 Stellen für die Aufgabe selbst 
 * - 2 Stellen für die Zeilenschaltung 
 * - Summe: 85 Zeichen
 *
 * Die Datensätze müssen nach dem Level in aufsteigender Folge sortiert sein.
 */
public class Archiv implements GlobaleObjekte {

   /**
    * Struktur für alle Elemente für einer Stradoku-Aufgabe
    */
   private class Aufgabe {
      int level;
      int status;     // letzter Veränderungszustand
      String aufgabe;
   }

   /**
    * Struktur für Level-Bereiche der Stradoku-Aufgaben
    */
   private class Bereich {
      int start;
      int ende;
   }

   /**
    * verwendete Variable
    */
   String userDir;
   String zipName;
   String zipDatei;
   String errorTxt = null;
   int nAufgaben;
   final int ZLAENGE = 85;
   int error = 0;
   int doppel;
   int ungeeignet;
   Aufgabe[] aufgaben;
   Bereich[] lpos = new Bereich[6];
   private FileWriter fw;
   private BufferedWriter bw;

   /**
    * Konstruktor
    *
    * @param pfad Pfad für das Archiv
    */
   public Archiv(String pfad) {
      userDir = pfad;
      ZipFile zipFile = null;
      zipName = userDir + File.separator + "arche.zip";
      File file1 = new File(zipName);
      try {
         // prüfen, ob und welche Datei existiert
         if (file1.exists() && !file1.isDirectory())
            zipFile = new ZipFile(zipName);
      } catch (IOException ex) {
         System.err.println("Fehler beim Öffnen der ZIP-Datei");
         error = 1;
         errorTxt = "die Datei " + zipName + " konnte nicht gefunden werden";
      }
      if (zipFile != null) {
         @SuppressWarnings("unchecked")
         Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>) zipFile.entries();
         ZipEntry zipEntry = enu.nextElement();
         zipDatei = zipEntry.getName();
         BufferedInputStream bis = null;
         byte[] buffer = null;
         try {
            bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
            int avail = bis.available() + 20;    // +20 falls am Ende kein LF
            nAufgaben = avail / ZLAENGE;
            aufgaben = new Aufgabe[nAufgaben];
            if (avail > 0) {
               buffer = new byte[avail];
               bis.read(buffer, 0, avail);
            }
         } catch (IOException ex) {
            System.err.println("Fehler beim Einlesen der ZIP-Datei");
            error = 2;
            errorTxt = "Fehler beim Einlesen der ZIP-Datei";
         } finally {
            try {
               if (bis != null) {
                  bis.close();
               }
            } catch (Exception ex) {
               System.err.println("Fehler beim Schließen der ZIP-Datei");
               error = 3;
               errorTxt = "Fehler beim Schließen der ZIP-Datei";
            }
         }
         byte[] tmp = new byte[81];
         int bi = 0;
         for (int i = 1; i < 6; i++) {
            lpos[i] = new Bereich();
         }
         lpos[1].start = 0;
         lpos[5].ende = nAufgaben - 1;
         int level = 1;
         for (int i = 0; i < nAufgaben; i++) {
            aufgaben[i] = new Aufgabe();
            aufgaben[i].level = buffer[bi++] - 48;
            if (aufgaben[i].level > level) {
               lpos[level].ende = i - 1;
               lpos[++level].start = i;
            }
            aufgaben[i].status = buffer[bi++] - 48;
            System.arraycopy(buffer, bi, tmp, 0, 81);
            aufgaben[i].aufgabe = new String(tmp);
            bi += 83;
         }
      }
   }

   /**
    * Gibt aus dem Archiv eine modifizierte Aufgabe für einen bestimmten Level als Int-Arrays
    * zurück.
    *
    * @param aufgabe Referenz für die zurückzugebende Aufgabe
    * @param level Level, für die zurückzugebende aufgabe
    * @return die generierte Aufgabe als String
    */
   public String getAufgabe(int[] aufgabe, int level) {
      int[] matrixAfg = new int[81];
      int i;
      // zufällige Aufgabe holen
      Random rand = new Random();
      int bas = lpos[level].ende - lpos[level].start + 1;
      i = rand.nextInt(bas) + lpos[level].start;
      String sAufgabe = aufgaben[i].aufgabe;
      // Strings zu int-Arrays konvertieren
      StradokuString2Matrix.makeStradokuString2Matrx(sAufgabe, matrixAfg);
      // gefundene Aufgabe modifizieren
      rotiereFeld(matrixAfg);
      if (aufgaben[i].status == 3 || aufgaben[i].status == 11) {
         tauscheZellWerte(matrixAfg);
      } else if (aufgaben[i].status == 7 || aufgaben[i].status == 15) {
         spiegleFeld(matrixAfg);
      }
      // Aufgabe für Archiv aktualisieren
      aufgaben[i].aufgabe = getStringAufgabe(matrixAfg);
      // Archiv-Status der Aufgabe aktualisieren
      aufgaben[i].status++;
      if (aufgaben[i].status == 16) {
         aufgaben[i].status = 0;
      }
      if (aufgabe != null) {
         System.arraycopy(matrixAfg, 0, aufgabe, 0, 81);
      }
      return aufgaben[i].aufgabe;
   }

   /**
    * Erstellt für eine Stradoku-Aufgabe/Lösung einen String im internen Format.
    *
    * @param str Referenz der zu behandelnden Aufgabe/Lösung
    * @return erzeuigter String
    */
   private String getStringAufgabe(int[] str) {
      String strString = "";
      for (int i = 0; i < 81; i++) {
         if (str[i] == 0) {
            strString += "0";
         } else if ((str[i] & SZELLE) == SZELLE) {
            if ((str[i] & ZAHL) == 0) {
               strString += "s";
            } else {
               strString += (char) (96 + (str[i] & ZAHL));
            }
         } else {
            strString += str[i];
         }
      }
      return strString;
   }

   /**
    * Beantwortet Anfrage welcher Archivfehler aufgetreten ist.
    *
    * @return Beschreibung bei Fehler, sonst Null
    */
   public String isError() {
      if (error > 0) {
         return errorTxt;
      }
      return null;
   }

   /**
    * Ermittelt Punkt eines Stradokus im Archiv.
    *
    * @param strad Stradoku, für das die Punkt zu ermitteln ist
    * @param level Level des zu prüfenden Stradokus
    * @return gefundene Punkt, 0 wenn nicht gefunden
    */
   public Position getArchivPosition(int[] strad, int level) {
      Position pos = new Position();
      // die zu testende Aufgabe als String
      String sAufgabe = getStringAufgabe(strad);
      // Aufgabenobjekt für jeweils nächstes Stradoku erstellen
      Aufgabe afg = new Aufgabe();
      afg.level = level;
      // Aufgabe als binäres Objekt
      int[] tbafg = new int[81];
      boolean gefunden = false;
      for (int i = lpos[level].start; i <= lpos[level].ende; i++) {
         // Stradoku an Punkt i einlesen
         afg.aufgabe = aufgaben[i].aufgabe;
         afg.status = 0;
         // alle Varianten prüfen
         do {
            if (afg.aufgabe.equals(sAufgabe)) {
               pos.z = i + 1;
               pos.s = aufgaben[i].status;
               gefunden = true;
               break;
            } else {
               makenextVariant(afg, tbafg);
            }
         } while (afg.status < 16 && !gefunden);
         if (gefunden) {
            break;
         }
      }
      return pos;
   }

   /**
    * Überprüft, ob ein Stradoku im Archiv mehrfach vertreten ist. Dafür wird jeder Eintrag gelesen
    * und mit allen 16 Vasrianten aller übrigen Einträge mit dem selben Level verglichen. Wird ein
    * Doppel gefunden, werden beide Positionen in die Datei doppelVorkommen.txt eingetragen.
    *
    * @throws java.io.IOException
    */
   public void fehlerCheck() throws IOException {
      fw = new FileWriter(userDir + File.separator + "ArchivCheck.txt");
      bw = new BufferedWriter(fw);
      bw.write("Überprüfung des Archivs auf doppelte Einträge und Eignung\n\n");
      Aufgabe afg = new Aufgabe();
      doppel = 0;
      ungeeignet = 0;
      
      // Archiv komplett durchlaufen
      for (int i = 0; i < nAufgaben; i++) {
         afg.aufgabe = aufgaben[i].aufgabe;
         afg.level = aufgaben[i].level;
         afg.status = aufgaben[i].status;
         // auf doppelvorkommem prüfen
         checkLevelgruppe(i, afg);
         // Ausgangssituation wieder herstellen
         afg.aufgabe = aufgaben[i].aufgabe;
         afg.level = aufgaben[i].level;
         afg.status = aufgaben[i].status;         
         // auf Archiveignung prüfen
         checkArchivtauglich(i, afg.level, afg.aufgabe);
      }
      if (doppel == 0) {
         bw.write("Es wurden keine doppelten Einträge gefunden.\n");
      } else if (doppel == 1) {
         bw.write("\nEs wurde ein doppelter Eintrag gefunden.\n");
      } else {
         bw.write("\nEs wurden " + doppel + " doppelte Einträge gefunden.\n\n");
      }
      if (ungeeignet == 0) {
         bw.write("Es wurden keine ungeeigneten Einträge gefunden.\n\n");
      } else if (ungeeignet == 1) {
         bw.write("\nEs wurde ein ungeeigneter Eintrag gefunden.\n\n");
      } else {
         bw.write("\nEs wurden " + ungeeignet + " ungeeignete Einträge gefunden.\n\n");
      }  
      bw.write("Überprüfung des Archivs abgeschlossen.");
      bw.close();
      fw.close();
   }

   private void checkArchivtauglich(int pos, int level, String aufgabe) throws IOException {
      int[] aAufgabe = new int[81];
      StradokuString2Matrix.makeStradokuString2Matrx(aufgabe, aAufgabe);
      ArchivLevel aLevel = new ArchivLevel();
      if (aLevel.getArchivLevel(aAufgabe, level) != level) {
         printArchivEignung(pos + 1);
         ungeeignet++;
      }
   }

   /**
    * Überprüft für einen Level, ob im Archiv ein Stradoku mit diesem Level noch einmal enthalten
    * ist. Falls ja, wird die Ausgabe der Doppelpositionen in eine Datei veranlasst.
    *
    * @param pos Archivposition für das zu prüfende Stradoku
    * @param afg alle Infos für das zu prüfende Stradoku
    */
   private void checkLevelgruppe(int pos, Aufgabe afg) throws IOException {
      // alle Stradokus mit dem jeweiligen Level prüfen
      String taufgabe = afg.aufgabe;
      int tlevel = afg.level;
      for (int i = lpos[tlevel].start; i <= lpos[tlevel].ende; i++) {
         if (i != pos) {
            int[] tbafg = new int[81];
            // Stradoku an Punkt i einlesen
            afg.aufgabe = aufgaben[i].aufgabe;
            afg.status = 0;
            // alle Varianten prüfen
            do {
               if (afg.aufgabe.equals(taufgabe)) {
                  if (pos > i) {
                     break;
                  }
                  printDoppelinfo(pos + 1, i + 1);
                  doppel++;
               }
               makenextVariant(afg, tbafg);
            } while (afg.status < 16);
         }
      }
   }

   /**
    * Erzeugt die jeweils nächste Stradoku-Variante
    *
    * @param afg Referenz auf Ausgangsvariante
    * @param tbafg Referenz auf Stradoku-Raster
    */
   private void makenextVariant(Aufgabe afg, int[] tbafg) {
      StradokuString2Matrix.makeStradokuString2Matrx(afg.aufgabe, tbafg);
      // gefundene Aufgabe modifizieren
      rotiereFeld(tbafg);
      if (afg.status == 3 || afg.status == 11) {
         tauscheZellWerte(tbafg);
      } else if (afg.status == 7 || afg.status == 15) {
         spiegleFeld(tbafg);
      }
      // Archiv-Status der Aufgabe aktualisieren
      afg.aufgabe = getStringAufgabe(tbafg);
      afg.status++;
   }

   /**
    * Gibt die Information über ein gefundenes Doppel-Stradoku in Textdatei aus
    *
    * @param pos1 geprüftes Stradoku
    * @param pos2 gefundenes Doppel dazu
    */
   private void printDoppelinfo(int pos1, int pos2) throws IOException {
      bw.write("doppelt: " + " " + pos1 + " und " + pos2 + "\n");
   }

   /**
    * veranlass die Ausgabe für nicht archivtagliche Stradokus
    */
      private void printArchivEignung(int line) throws IOException {
      bw.write("Zeile: " + " " + line + " nicht archivtauglich\n");
   }

   /**
    * Speichert das Archiv als gezipte Datei.
    *
    * @param buffer Archiv als Bit-Array
    */
   private void saveZip(byte[] buffer) {
      ZipOutputStream zipOut = null;
      try {
         zipOut = new ZipOutputStream(
               new FileOutputStream(zipName));
         ZipEntry ze = new ZipEntry(zipDatei);
         zipOut.putNextEntry(ze);
         zipOut.write(buffer, 0, buffer.length);
         zipOut.closeEntry();
      } catch (IOException ex) {
         System.err.println("Fehler beim Schreiben des Archivs");
      } finally {
         try {
            if (zipOut != null) {
               zipOut.close();
            }
         } catch (Exception ex) {
            System.err.println("Fehler beim Schließen der ZIP-Datei");
         }
      }
   }

   /**
    * Schließt bei Programmende das Archiv im jeweils aktuellen Zustand.
    */
   public void schlieszeArchiv() {
      byte[] buffer = new byte[nAufgaben * ZLAENGE];
      int bi = 0;
      for (int i = 0; i < nAufgaben; i++) {
         buffer[bi++] = (byte) (aufgaben[i].level + 48);
         buffer[bi++] = (byte) (aufgaben[i].status + 48);
         System.arraycopy(aufgaben[i].aufgabe.getBytes(), 0, buffer, bi, 81);
         bi += 81;
         buffer[bi++] = 13;
         buffer[bi++] = 10;
      }
      saveZip(buffer);
   }
}
