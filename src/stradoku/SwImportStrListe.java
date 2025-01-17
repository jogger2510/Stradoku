/**
 * SwImportStrListe.java ist Teil des Programmes kodelasStradoku
 * Erzeugt am:                  06.01.2018 11:22
 * Letzte Änderung:             03.10.2023 09:50
 *
 * Import-Formate:
 *
 * 1. esterne Format von Andrew Stuart mit 2x81 Zeichen
 * 2. internes Format wie für Archiv verwendet mit 83 Zeichen
 * 3. interne Format mit 81 Zeichen
 *
 * Copyright (C) Konrad Demmel, 2018 - 2022
 */
package stradoku;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import static stradoku.GlobaleObjekte.SZELLE;
import static stradoku.GlobaleObjekte.rotiereFeld;
import static stradoku.GlobaleObjekte.spiegleFeld;
import static stradoku.GlobaleObjekte.tauscheZellWerte;

/**
 *
 * @author Konrad
 */
public class SwImportStrListe extends SwingWorker<Boolean, Integer>
      implements Runnable, GlobaleObjekte {

   private final Stradoku strApp;
   private final ListenFrame strListe;
   private final ListenModel listeDaten;
   private String s_aufgabe;
   private int[] a_aufgabe;
   private int[] a_loesung;
   private int level;
   private int loesungswerte;
   public boolean erfolg;
   // für Archivtauglichkeit
   private final boolean archivtauglich;
   private FileWriter fw;
   private BufferedWriter bw;
   private boolean eintrag;
   private HinweisWarten hnwDialog;
   private final String aktPfad;

   /**
    * Konstruktor
    *
    * @param mf Referenz auf Hauptklasse
    * @param hnw
    * @param sListe Referenz auf Listenklasse
    * @param pfad Verzeichnis in der die Liste liegt
    * @throws IOException wenn ein Ein- oder Ausgabefehler auftritt
    */
   public SwImportStrListe(Stradoku mf, HinweisWarten hnw,
         ListenFrame sListe, String pfad) throws IOException {

      strApp = mf;
      strListe = sListe;
      listeDaten = new ListenModel(strApp);
      aktPfad = pfad;
      erfolg = true;
      if (strApp.get_archivtauglich()) {
         archivtauglich = true;
         fw = new FileWriter("unberuecksichtigt.txt");
         bw = new BufferedWriter(fw);
         eintrag = false;
      } else {
         archivtauglich = false;
      }
      if (hnw != null) {
         hnwDialog = hnw;
         hnwDialog.zeigeHinweis("<html><center><b>Stradoku-Liste wird importiert."
               + "<br><br>Bitte solange warten.</b></center></html>");
      }
   }

   @Override
   public Boolean doInBackground() throws Exception {
      try {
         if (strListeLaden(aktPfad)) {
            strListe.setSerie(false);
            strListe.speichernListe();
            strListe.setAnzahl();
         } else {
            JOptionPane.showMessageDialog(strApp,
                  "Gewählte Datei kann nicht importiert werden.");
            erfolg = false;
            hnwDialog.setVisible(erfolg);
            return false;
         }
      } catch (HeadlessException ex) {
         JOptionPane.showMessageDialog(strApp,
               "Gewählte Datei kann nicht importiert werden.");
         erfolg = false;
         hnwDialog.setVisible(erfolg);
         return false;
      }
      return true;
   }

   /**
    * Liest Liste Zeile für Zeile ein
    *
    * @param pfad Verzeichnis für Quellenliste
    * @return true wenn ohne Fehler beendet, sonst false
    * @throws java.io.FileNotFoundException
    */
   public boolean strListeLaden(String pfad) throws FileNotFoundException, IOException {
      String zeile;
      int zNr = 0;
      try (FileReader fr = new FileReader(pfad);
            BufferedReader br = new BufferedReader(fr)) {
         do {
            zNr++;
            zeile = br.readLine();
         } while (zeile != null);
         br.close();
         fr.close();
      } catch (IOException ex) {
      }
      float fortschritt = 100f / zNr;
      try (
         FileReader fr = new FileReader(pfad);
         BufferedReader br = new BufferedReader(fr)) {
         zeile = br.readLine();
         // erst eventuelles BOM für UTF-8 Kodierung ignorieren  
         // siehe hier: https://javabeginners.de/String/Sonderzeichen.php
         if (zeile == null) {
            br.close();
            erfolg = false;
            return false;
         }
         if (zeile.startsWith("\uFEFF")) {
            zeile = zeile.substring(1);
         }
         zNr = 0;
         do {
            zNr++;
            hnwDialog.fortschrittsAnzeige.setValue((int) (zNr * fortschritt));
            if (zeile.length() >= 162) {
               add_extStradoku(zeile);
            } else if (zeile.length() >= 81) {
               add_internStradoku(zeile, zNr);
            } else {
               br.close();
               erfolg = false;
               return false;
            }
            zeile = br.readLine();
         } while (zeile != null);
         br.close();
         hnwDialog.setVisible(false);
         if (archivtauglich) {
            bw.close();
            fw.close();
            if (!eintrag) {
               Files.delete(Paths.get("unberuecksichtigt.txt"));
            }
         }
      } catch (IOException ex) {
         JOptionPane.showMessageDialog(strApp,
               "Ein Fehler ist aufgetreten. \n\n"
               + ex.getMessage(),
               "Hinweis", 1);
         erfolg = false;
         return false;
      }
      return true;
   }

   /**
    * Erstellt aus eingelesener Zeile mit Stradoku Aufgabe im externen Format für die Feststellung
    * des Levels die Arrayform der Aufgabe und für die Anzeige in der Liste die Stringform im
    * internen Format.
    *
    * @param zeile Stradoku Aufgabe
    * @return false bei aufgetretenem Fehler
    */
   private boolean add_extStradoku(String zeile) {
      a_aufgabe = new int[81];
      a_loesung = new int[81];
      s_aufgabe = "";
      String werte = zeile.substring(0, 81);
      String zellen = zeile.substring(81, 162);
      char w, z;
      loesungswerte = 0;
      for (int i = 0; i < 81; i++) {
         w = werte.charAt(i);
         z = zellen.charAt(i);
         if (z == '1') {
            if (w == '0') {
               a_aufgabe[i] = SZELLE;
               s_aufgabe += 's';
            } else {
               a_aufgabe[i] = SZELLE + Character.getNumericValue(w);
               s_aufgabe += (char) (w + 0x30);
            }
         } else {
            a_aufgabe[i] = Character.getNumericValue(w);
            s_aufgabe += w;
            if (w == '0') {
               loesungswerte++;
            }
         }
      }
      LevelSolver loeser = new LevelSolver(null, a_aufgabe, a_loesung, false);
      level = loeser.loeseAufgabe();
      int num = -1;
      if (level > 0) {
         num = strListe.addStradoku(s_aufgabe, "" + level, loesungswerte, "");
         listeDaten.setGeaendert();
      }
      return num < 0;
   }

   /**
    * Erstellt aus eingelesener Zeile mit Stradoku-Aufgabe im internen oder im 
    * Archiv-Format für die Feststellung des Levels die Arrayform der Aufgabe 
    * und für die Anzeige in der Liste die Stringform im internen Format.
    *
    * @param zeile Stradoku Aufgabe
    * @param len Zeilenlänge 81=intern, 83=Archivformat, 162=extern,
    * @throws IOException wenn ein Ein- oder Ausgabefehler auftritt
    * @return false bei aufgetretenem Fehler
    */
   private boolean add_internStradoku(String zeile, int zn) throws IOException {
      a_aufgabe = new int[81];
      a_loesung = new int[81];
      int len = zeile.length();
      if (len == 81) {                                    // Standardlänge
         s_aufgabe = zeile;
      } else if (len == 83) {                              // Daten aus dem Archiv
         s_aufgabe = zeile.substring(2, 83);
      } else {                                            // nicht gewollte Leerzeichen am Ende 
         s_aufgabe = zeile.substring(0, 81);
      }
      char w;
      loesungswerte = 0;
      for (int i = 0; i < 81; i++) {
         w = s_aufgabe.charAt(i);
         if (w >= '0' && w <= '9') {
            a_aufgabe[i] = w - 0x30;
            if (w == '0') {
               loesungswerte++;
            }
         } else {
            if (w == 's') {
               a_aufgabe[i] = SZELLE;
            } else if (w >= 'a' && w <= 'i') {
               a_aufgabe[i] = SZELLE | (w - 0x60);
            } else {
               a_aufgabe[i] = w - 0x30;
            }
         }
      }
      LevelSolver loeser = new LevelSolver(null, a_aufgabe, a_loesung, false);
      level = loeser.loeseAufgabe();
      if (level < 1) {
         bw.write("Zeile: " + zn + " " + s_aufgabe + " nicht gelöst\n");         
         return false;
      }
      // hier Schleife für Archivtauglichkeit
      if (archivtauglich && level > 0 && level <= 5) {
         int fixLevel = level;
         for (int i = 0; i < 16; i++) {
            rotiereFeld(a_aufgabe);
            if (i == 3 || i == 11) {
               tauscheZellWerte(a_aufgabe);
            } else if (i == 7) {
               spiegleFeld(a_aufgabe);
            }
            loeser = new LevelSolver(null, a_aufgabe, a_loesung, false);
            int tmpLevel = loeser.loeseAufgabe();
            if (tmpLevel != fixLevel) {
               bw.write(s_aufgabe + " " + fixLevel + " - " + tmpLevel + " : Zeile " + zn + "\n");
               eintrag = true;
               return false;
            }
         }
      }
      int num = strListe.addStradoku(s_aufgabe, "" + level, loesungswerte, "");
      strListe.setAnzahlDS();
      listeDaten.setGeaendert();
      return num < 0;
   }

}
