/**
 * Stradoku.java ist die Hauptklasse der Anwendung
 *
 * Dieses Programm unterstützt das Lösen von Str8ts-Aufgaben und
 * ermöglicht Str8ts-Aufgaben zu generieren und zu bearbeiten.
 *
 * Erstellt/umgearbeitet am:    03.07.2017 12:00
 * Letzte Dateiänderung:        31.12.2024 13:20
 * Letzte Projektänderung:      31.12.2024 13:20
 *
 * Copyright (C) Konrad Demmel, 2017-2024
 *
 * Dieses Programm ist freie Software. Es kann unter den Bedingungen der
 * aktuellen GNU General Public License, wie von der Free Software Foundation
 * veröffentlicht, weitergeben und/oder modifiziert werden.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/>.
 */
package stradoku;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import static java.lang.Math.abs;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hauptklasse der Anwendung
 */
public final class Stradoku extends JFrame
      implements GlobaleObjekte, InfoTexte {

   private static final long serialVersionUID = 1L;

   private String VERSION = "6.6.4";
   private String name = "kodelasStradoku";
   private String appName = name + " V " + VERSION;
   private String infoString = "Konfigurations-Datei für " + name + " " + VERSION;
   private String iniDat = "stradoku.cfg";
   private String iniStr = "letztes.str";
   private String unbenannt = "unbenannt";
   private String strFolder = File.separator + "Aufgaben";
   private String stradoku_L2 = "is50s086s0s000000s00h70ss432"
         + "0s0007s0s403s500s0s0000e0070sa30s08s000040b0s800s04sf";
   private StradokuOrg strOrg;
   private StradokuBoard strBoard;
   private ImportStradoku importStr;
   private ExportStradoku exportStr;
   private FromToClipboard clipboard;
   private Archiv archiv;
   private ProgrammInfo prgInfo = null;
   private HilfeDialog jHilfe = null;
   private String lf = System.getProperty("line.separator");
   private boolean loesungsModus;
   private boolean testModus = false;
   private boolean zeigeKnd;
   private boolean zifferpunkt;
   private int kndModus = 0;
   private final int KLISTE = 1;
   private final int KNOTIZ = 2;
   private int level;
   private int fehler = 0;
   private int tipps;
   private int posSicherung;
   private int naviPosition;
   private String strPath;                                         // gespeichertes Stradoku
   private String homePath;                                        // Arbeitsverzeichnis
   private String filePath = null;                                 // aktueller Speicher-Ordner 
   private String pngFolder = null;
   private String loeTipps = null;
   private JButton[] kandidatTaste = new JButton[10];
   private JRadioButtonMenuItem[] levelName = new JRadioButtonMenuItem[6];
   private int editModus;
   private final int MINUTE = 60000;
   private int tmp_freieZellen;
   private ListenFrame strListe;
   private Color grau = new Color(192, 192, 192);
   private Color weisz = new Color(255, 255, 255);
   private long loesungsZeit;
   private ThreadStrSerie strSerieThread;
   private ThreadZeitAnzeige uhr;
   private VerlaufsLogger vLogg;
   private boolean isArchivFehler = false;
   private boolean usedNotizen = false;
   private boolean usedKListe = false;
   private boolean usedTestmod = false;
   private boolean usedKndFi = false;
   private boolean gespikt = false;
   private boolean zeigeInfo = true;
   private boolean archivtauglich = false;
   private boolean geaendert = false;
   private boolean isTipp = false;
   private boolean erstinfo = false;
   private boolean zeigtLoesung = false;

   public int[] test;

   /**
    * Konstruktor
    *
    * @param args Übergabe eines binär gespeicherten Stradoku möglich.
    */
   public Stradoku(String args[]) {
      super();
      try {
         initComponents();
         try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         } catch (Exception e) {
         }
         setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
         homePath = System.getProperty("user.dir");
        File jarFile;
          try {
              jarFile = new File(Stradoku.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                homePath = jarFile.getParentFile().getPath();
          } catch (URISyntaxException ex) {
          }
         pngFolder = homePath + File.separator + "PNGs";
         initKandidatenTasten();
         initLevelNamen();
         getContentPane().setBackground(new Color(102, 102, 0));
         strOrg = new StradokuOrg(this);
         strBoard = new StradokuBoard(this, strOrg);
         importStr = new ImportStradoku(this);
         exportStr = new ExportStradoku(this);
         stradokuFeld.add(strBoard);
         strOrg.setStrBoard(strBoard);
         strPath = unbenannt;
         loesungsModus = true;
         strOrg.setLoesungsModus(loesungsModus);
         strOrg.resetVerlauf();
         strBoard.setLoesungsModus(loesungsModus);
         kndModus = 0;
         fehler = 0;
         tipps = 0;
         editModus = 0;
         posSicherung = 0;
         labelHinweisfeld.setFont(new Font("Serif", Font.PLAIN, 12));
         statusBarZeit.setText("00:00");
         uhr = new ThreadZeitAnzeige(this);
         labelKandidaten.setForeground(grau);
         for (int i = 1; i <= 9; i++) {
            kandidatTaste[i].setEnabled(false);
            kandidatTaste[i].setToolTipText("");
         }
         int parameter = args.length;
         File kf = new File(homePath, iniDat);
         if (kf.canRead()) {
            String strad;
            if (parameter == 1) {
               strad = args[0];
               filePath = strad.substring(0, strad.lastIndexOf(File.separator));
            } else {
               strad = "";
               filePath = homePath + strFolder;
            }
            ladenKonfiguration(strad, true);
         } else {
            setTitle(appName + " - " + strPath);
            setStartZeit(0);
            strOrg.importStradokuString(stradoku_L2, true);
            levelBox.setSelectedIndex(level - 1);
            labelHinweisfeld.setText(ERSTINFO);
            strBoard.setFilterColor(30);
            erstinfo = true;
            setLevelBox(level);
            setLevelMenu(level);
         }
         setLevel(level);
         strBoard.setKndAnzeige(maKndAnzeige.isSelected());
         strBoard.requestFocusInWindow();
         strListe = new ListenFrame(this);
         setStatusBarHinweis("Aktuelle Lösungsposition:  "
               + strOrg.getFreiZellen(true) + " ungelöste Zellen "
               + " (von " + strOrg.getLoesungsZellen() + ")", true);
      } catch (HeadlessException e) {
         JOptionPane.showMessageDialog(null,
               "Beim Starten dieses Programmes ist ein "
               + "unbekannter Fehler aufgetreten.",
               "Hinweis", 1);
      }
   }

   /**
    * Aktuelle Konfiguration wird gesichert.
    */
   @SuppressWarnings("CallToThreadDumpStack")
   public void sichernKonfiguration() {
      exportStr.strBinSpeichern(homePath + File.separator + iniStr);
      File cfgfile = new File(homePath + File.separator + iniDat);
      try (FileWriter wr = new FileWriter(cfgfile)) {
         wr.write(infoString + lf);
         wr.write("xPosition=" + getX() + lf);
         wr.write("yPosition=" + getY() + lf);
         wr.write("LetztesStradoku=" + strPath + lf);
         wr.write("EingabeHinweise=" + (zeigeInfo ? "1" : "0") + lf);
         wr.write("FilterFarbe=" + strBoard.getFilterColor() + lf);
         wr.write("LoesungsTipps=" + getTipps() + lf);
         wr.flush();
      } catch (IOException e) {
      }
   }

   /**
    * Übernimmt Einstellungen aus Konfigurationsdatei.
    *
    * @param para True wenn beim Aufruf Parameter übergeben wurden
    * @param zeigen True wenn Fenster an letzter Punkt gezeigt werden soll
    * @return True wenn kein Fehler aufgetreten ist
    */
   @SuppressWarnings("CallToThreadDumpStack")
   public boolean ladenKonfiguration(String para, boolean zeigen) {
      if (para.isEmpty()) {
         importStr.strBinLaden(homePath + File.separator + iniStr);
      } else {
         importStr.strBinLaden(para);
      }
      BufferedReader in = null;
      try {
         in = new BufferedReader(new FileReader(homePath + File.separator + iniDat));
         String zeile = in.readLine();           // ersten Eintrag ignorieren
         if (!zeile.contains("Konfigurations-Datei")) {
            return false;
         }
         String[] eintrag = {"xpos", "ypos",
            "letz", "eing", /*"posi",*/ "filt", "loes"};
         int x = 0, y = 0;
         int nPos = -1;
         String wert;
         while (eintrag.length - 1 > nPos) {
            for (int i = 0; i < eintrag.length; i++) {
               zeile = in.readLine();
               if (zeile == null) {
                  break;
               }
               String e = zeile.substring(0, 4);
               if (e.length() < 1) {
                  break;
               }
               if (e.toLowerCase().equals(eintrag[i])) {
                  nPos = i;
                  switch (nPos) {
                     case 0:
                        wert = zeile.substring(zeile.indexOf('=') + 1);
                        x = abs(Integer.parseInt(wert));
                        break;
                     case 1:
                        wert = zeile.substring(zeile.indexOf('=') + 1);
                        y = abs(Integer.parseInt(wert));
                        if (y < 0) {
                           y = abs(y);
                        }
                        break;
                     case 2:
                        if (para.isEmpty()) {
                           strPath = zeile.substring(zeile.indexOf('=') + 1);
                        } else {
                           strPath = para;
                           filePath = para.substring(0, para.lastIndexOf(File.separator));
                        }
                        setTitle(appName + " - " + strPath);
                        break;
                     case 3:
                        wert = zeile.substring(zeile.indexOf('=') + 1);
                        zeigeInfo = wert.equals("1");
                        mhEingabeHinweise.setSelected(zeigeInfo);
                        zeigeHinweis(zeigeInfo);
                        break;
                     case 4:
                        wert = zeile.substring(zeile.indexOf('=') + 1);
                        int kf = Integer.parseInt(wert);
                        if (kf >= 0 && kf <= 60) {
                           strBoard.setFilterColor(kf);
                        } else {
                           strBoard.setFilterColor(30);
                        }
                        break;
                     case 5:
                        tipps = Integer.parseInt(zeile.substring(zeile.indexOf('=') + 1));
                        break;
                  }
               }
            }
         }
         File file = new File(homePath + File.separator + "archivtauglich");
         archivtauglich = file.exists();
         if (zeigen) {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] devs = env.getScreenDevices();
            if (devs.length < 2) {                                      // nur ein Monitor
               int width1 = devs[0].getDisplayMode().getWidth();       // Breite prim. Monitor
               if (x < 0) {                                            // zuletzt links
                  x += width1;                                        // nach recht schieben
               } else if (x > width1) {                                // zuletzt rechts
                  x -= width1;                                        // nach links schieben
               }
            }
            setLocation(x, y);
         }
      } catch (IOException e) {
         return false;
      } finally {
         try {
            in.close();
         } catch (Exception e) {
         }
      }
      return true;
   }

   /**
    * Stellt Bezug zur Klasse StradokuBoard her.
    *
    * @return Verweis zu StradokuBoard
    */
   public StradokuBoard getStradokuBoard() {
      return strBoard;
   }

   /**
    * Stellt Bezug zur Klasse StradokuOrg her.
    *
    * @return Verweis zu StradokuOrg
    */
   public StradokuOrg getStradokuKlasse() {
      return strOrg;
   }

   /**
    * Stellt Bezug zur Klasse StradokuOrg her.
    *
    * @return Verweis zu StradokuOrg
    */
   public ListenFrame getStradokuListe() {
      return strListe;
   }

   /**
    * Gibt in der Statuszeile und bedingt auch in der Log-Datei einen Hinweis aus.
    * @param hinweis auszugebende Hinweis
    * @param log für Ausgabe in Logdatei
    */
   public void setStatusBarHinweis(String hinweis, boolean log) {
      statusBarHinweis.setText(hinweis);
      statusBarHinweis.setText(hinweis);
      if (log) {
         if (vLogg == null) {
            vLogg = new VerlaufsLogger(homePath);
         }
         String pos = "" + naviPosition;
         if (naviPosition < 10) {
            pos = "00" + naviPosition;
         } else if (naviPosition < 100) {
            pos = "0" + naviPosition;
         }
         String action = pos + ": " + hinweis + "\r\n";
         vLogg.logAusgabe(action);
      }
   }

   /**
    * Setzt den Testmodus.
    *
    * @param mod true oder false
    */
   public void setTestModus(boolean mod) {
      testModus = mod;
      if (testModus) {
         usedTestmod = true;
         menuNavi.setEnabled(false);
      } else {
         menuNavi.setEnabled(true);
      }
      modMenuBearbeitenTestmodus();
   }

   /**
    * Übergibt Status von testModus
    *
    * @return true oder false
    */
   public boolean getTestModus() {
      return testModus;
   }

   /**
    * Aktualisiert Fehler und deren Anzeige in der Statuszeile.
    *
    * @param set bei True Incrementierung, ansonsten Reset
    */
   public void setStatusBarFehler(boolean set) {
      if (set) {
         fehler++;
      } else {
         fehler = 0;
      }
      statusBarFehler.setText("" + fehler);
   }

   /**
    * Setzt das Level-Menü.
    *
    * @param lvl zu setzender Level
    */
   public void setLevelMenu(int lvl) {
      levelGroup.clearSelection();
      if (lvl > 5 || lvl < 0) {
         return;
      }
      levelName[lvl].setSelected(true);
      levelBox.setSelectedIndex(lvl);
   }

   /**
    * Setzt in der Levelliste neuen Level.
    *
    * @param lvl zu setzender Level
    */
   public void setLevelBox(int lvl) {
      levelBox.setSelectedIndex(lvl);
   }

   /**
    * Setzt oder entfernt über die Werte-Buttons eingegebenen Werte.
    *
    * @param w zu setzender oder zu entfernender Wert
    */
   private void bearbeiteSetzenButton(int w) {
      if (loesungsModus) {
         // nur setzen
         strOrg.setWert(strBoard.getSelect(), w);
      } else {
         if (strOrg.isWert(strBoard.getSelect(), w)) {
            // Wert bereits gesetzt, also entfernen
            strOrg.setWert(strBoard.getSelect(), 0);
         } else {
            // setzen
            strOrg.setWert(strBoard.getSelect(), w);
         }
      }
      strBoard.requestFocusInWindow();
      repaint();
   }

   /**
    * Entfernt oder setzt über die Kandidatentasten eingegebene Kandidaten aus/in der akteullen
    * Zelle.
    *
    * @param k eingegebener Kandidat
    */
   private void bearbeiteKndButton(int k, boolean isShift) {
      int i = strBoard.getSelect();
      int zelle = strOrg.getZelle(i, loesungsModus);
      // Sperrzellen, Vorgabe- und gelöste Zellen übergehen
      if ((zelle & SZELLE) == SZELLE || (zelle & LWF) == 0
            || (zelle & AKND) == 0) {
         return;
      }
      strBoard.requestFocusInWindow();
      if (loesungsModus) {
         if (kndModus == KNOTIZ) {
            if (testModus) {
               strOrg.notiereKandidat(i, k, true);
            } else {
               strOrg.notiereKandidat(i, k, isShift);
            }
         } else if (kndModus == KLISTE) {
            if (!strOrg.isLoesungsWert(i, k)) {
               strOrg.entferneKandidat(i, k, true);
            } else {
               JOptionPane.showMessageDialog(this,
                     "Der Kandidat " + k
                     + " kann aus dieser Zelle nicht entfernt werden.",
                     "Hinweis", 1);
               setStatusBarFehler(true);
            }
         }
         repaint();
      }
   }

   /**
    * Öffnet ein gespeichertes Stradoku.
    */
   private void strLaden() {
      importStr.strLaden();
      geaendert = false;
   }

   /**
    * Importiert eine Liste mit Stradoku-Zeilen in die interne Stradoku-Liste
    *
    * @throws IOException wenn ein Ein- oder Ausgabefehler auftritt
    */
   private void strListenImport() throws IOException {
      if (editModus > 0) {
         return;
      }
      JFileChooser fc = new JFileChooser(homePath);
      fc.setAcceptAllFileFilterUsed(false);
      fc.setDialogTitle("Stradoku-Liste öffnen");
      fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
      int returnVal = fc.showOpenDialog(this);
      String aPfad = "";
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         try {
            aPfad = fc.getSelectedFile().getPath();
            strListe.setSerie(true);
            strListe.setAnzahl();
         } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(this,
                  "Gewählte Datei kann nicht importiert werden.");
         }
      } else {
         return;
      }
      AbfrageImportListe strListeImport;
      strListeImport = new AbfrageImportListe(this, true);
      int modus = strListeImport.zeigeDialog();
      boolean umbenannt = false;
      if (modus == 2) {
         strListe.loescheStrTabelle();
         File lfile = new File(homePath, "stradoku.lst");
         if (lfile.exists()) {
            lfile.renameTo(new File(homePath, "stradoku_sik.lst"));
            umbenannt = true;
         }
      } else if (modus == 0) {
         return;
      }
      HinweisWarten hnw = new HinweisWarten(this);
      SwImportStrListe limport = new SwImportStrListe(
            this, hnw, strListe, aPfad);
      limport.execute();
      hnw.setVisible(true);
      if (limport.erfolg) {
         if (umbenannt) {
            File lfile = new File(homePath, "stradoku_sik.lst");
            if (lfile.exists()) {
               Files.delete(Paths.get("stradoku_sik.lst"));
            }
            strListe = new ListenFrame(this);
         }
         statusBarHinweis.setText("Gewählte Stradoku Liste importiert.");
      } else {
         if (umbenannt) {
            File lfile = new File(homePath, "stradoku_sik.lst");
            if (lfile.exists()) {
               lfile.renameTo(new File(homePath, "stradoku.lst"));
            }
            strListe = new ListenFrame(this);
         }
      }
   }

   /**
    * Speichert ein Stradoku als Binädatei.
    */
   private void strSpeichern(boolean sab) {
      if (strPath.lastIndexOf(".str") > 0) {
         File sf = new File(strPath);
         if (sf.exists() && sab) {
            String neuerName = strPath.substring(
                  strPath.lastIndexOf(File.separatorChar) + 1);
            String[] yesNoOptions = {"Ja", "Nein"};
            int op = JOptionPane.showOptionDialog(this,
                  "Die Datei '" + neuerName
                  + "' ist bereits vorhanden. "
                  + "Soll sie überschrieben werden?",
                  "Sicherheitsabfrage",
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null, yesNoOptions, yesNoOptions[1]);
            if (op == JOptionPane.NO_OPTION) {
               return;
            }
         }
         exportStr.strBinSpeichern(strPath);
      } else {
         exportStr.exportStradoku();
      }
      statusBarHinweis.setText("Stradoku gespeichert.");
      geaendert = false;
   }

   /**
    * Speichert das aktuelle Stradoku unter einem neuen Namen.
    */
   private void strSpeichernAls() {
      exportStr.exportStradoku();
      geaendert = false;
   }

   /**
    * Speichert das aktuelle Stradoku aln PNG-Datei
    *
    * @param rahmen Flag für Ausgabe, true mit Rahmen, false ohne
    * @throws IOException wenn ein Ein- oder Ausgabefehler auftritt
    */
   private void speichernBild(Boolean rahmen) throws IOException {
      exportStr.strBildSpeichern(strPath, rahmen);
   }

   /**
    * Wechselt in den Bearbeitungmodus. Ist dieser bereits aktiv, wird in den Lösungsmodus
    * gewechselt.
    *
    * @param mod Flags für Bearbeitungsmodus neu: 8, Aufgabe: 4, mit Lösungswerten: 6, mit Lösung:
    * 7)
    * @param frg Flag für Sicherheitsabfrage
    */
   public void bearbeiteAufgabe(int mod, boolean frg) {
      boolean abbruch = true;
      int lz;
      int lev;
      if (loesungsModus) {
         if (strOrg.isLoesung()) {
            setNavi(1);
         }
         editModus = mod;
         if (zeigeInfo) {
            zeigeHinweis(true);
         }
         int op;
         if (frg) {
            String meldung;
            if (mod == EDIT_NEU) {
               meldung = "Soll das aktuelle Stradoku für die Eingabe "
                     + "einer neuen Aufgabe gelöscht werden?";
            } else {
               meldung = "Soll das aktuelle Stradoku verändert werden?";
            }
            String[] yesNoOptions = {"Ja", "Nein"};
            op = JOptionPane.showOptionDialog(this, meldung,
                  "Sicherheitsabfrage",
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null, yesNoOptions, yesNoOptions[1]);
         } else {
            op = JOptionPane.YES_OPTION;
         }
         if (op == JOptionPane.YES_OPTION) {
            sichernKonfiguration();
            strOrg.sikStradoku(mod == EDIT_LOS);
            if (mod == EDIT_LOW) {
               strOrg.stradoku2aufgabe(true);
            } else if (mod == EDIT_LOS) {
               strOrg.stradoku2aufgabe(false);
            }
            setBearbeitungsModus(editModus == EDIT_NEU, false);
         } else {
            // User will abbrechen
            editModus = 0;
         }
      } else {
         // Eingabe soll beendet werden
         symLeisteStrEditieren.setSelected(true);
         boolean beenden = true;
         lev = strOrg.loeseStradoku(archivtauglich, true);
         lz = strOrg.getLoesungsZellen();
         int in;
         String meldung;
         boolean gueltig;
         EingabeBeendenDialog dlg = new EingabeBeendenDialog(this);
         // alles außer nicht archivtauglich
         if (lev <= 5) {
            if (lev < 0) {
               meldung = "Diese Aufgabe kann nicht als Stradoku gelöst werden. "
                     + "Sie ist fehlerhaft.";
               gueltig = false;
            } else if (lev == 0) {
               if (strOrg.getFreiZellen(false) > 0) {
                  meldung = "<html>Diese Aufgabe kann gelöst werden, "
                        + "jedoch nicht eindeutig.";
                  gueltig = true;
               } else {
                  meldung = "<html>Diese Aufgabe hat keine freien Zellen, "
                        + "die gelöst werden könnten.";
                  gueltig = true;
               }
            } else {
               meldung = "<html>Diese Aufgabe ist fehlerfrei. "
                     + "Sie hat <b>genau eine Lösung</b>, "
                     + lz + " freie Zellen "
                     + "und " + "den <b>Level " + lev + "</b>.";
               gueltig = true;
            }
         } else {
            // also gelöst aber nicht archivgeeignet
            int lev1 = lev & ZAHL;
            int lev2 = 0;
            if ((lev & 0x10) > 0) {
               lev2 = lev1 - 1;
            } else if ((lev & 0x20) > 0) {
               lev2 = lev1 + 1;
            } else if ((lev & 0x40) > 0) {
              lev2 = -1;
            }
            lev &= 0xF;
            if (lev2 > 0) {
               meldung = "<html>Diese Aufgabe hat <b>genau eine Lösung</b>, "
                     + lz + " freie Zellen und die<br><b>Levels "
                     + lev1 + " - " + lev2 + "</b>. - "
                     + "Sie ist nicht archivgeeignet!";
            } else {
               meldung = "<html>Diese Aufgabe hat <b>genau eine Lösung</b>, "
                     + lz + " freie Zellen und den<br><b>Level " + lev1 + "</b> - "
                     + "Sie ist nicht archivgeeignet!";               
            }
            gueltig = true;
         }
         in = dlg.zeigeDialog(meldung, gueltig);
         if (in == dlg.UBERARBEITEN) {
            beenden = false;
            abbruch = false;
            setStatusBarLevel(lev);
            setStatusBarHinweis("Bearbeitungsmodus", false);
         } else if (in == dlg.UEBERNAHME) {
            strOrg.uebernehmeNeueAufgabe();
            strOrg.setVerlaufsNdx(0);
            strBoard.setSelect(40);
            strPath = "unbenannt";
            usedKListe = false;
            usedKndFi = false;
            usedNotizen = false;
            usedTestmod = false;
            fehler = 0;
            gespikt = false;
            setStartZeit(0);
            setLevel(lev);
            beenden = true;
            abbruch = false;
         } else if (in == dlg.ABBRUCH) {
            beenden = true;
            abbruch = true;
         }
         if (beenden && !abbruch) {
            resetBearbeitungsModus(true);
         } else if (abbruch) {
            ladenKonfiguration("", false);
            resetBearbeitungsModus(mod == EDIT_NEU);
            statusBarZeit.setEnabled(true);
         } else {
            statusBarLevel.setText("" + lev);
            statusBarFehler.setText("" + lz);
            if (editModus == EDIT_NEU) {
               tmp_freieZellen = strOrg.getLoesungsZellen();
               labelFehler.setText("Freie Zellen:");
               statusBarFehler.setText("" + tmp_freieZellen);
               labelLevel.setText("Level:");
            }
         }
      }
      if (zeigeInfo) {
         zeigeHinweis(true);
      }
      strBoard.requestFocusInWindow();
      repaint();
   }

   /**
    * Erledigt Verwaltungsaufgaben für den Wechsel in den Bearbeitungsmodus.
    *
    * @param neu Flag für neu zu erstellendes Stradoku
    */
   private void setBearbeitungsModus(boolean neu, boolean loe) {
      loesungsModus = false;
      sikNaviStatus();
      int tmp = strBoard.getSelect();
      setNavi(1);                             // verändert auch die Selection
      strBoard.setSelect(tmp);
      strBoard.setLoesungsModus(false);
      strOrg.setLoesungsModus(false);
      if (kndModus == KNOTIZ) {
         setNotizenMode(true);
      }
      test = strOrg.getAktStradoku();
      if (neu) {
         symLeisteStrEditieren.setEnabled(false);
         mdEingabe.setText("Eingabe beenden");
         symLeisteStrEingabe.setToolTipText("Eingabe beenden");
         mbEditieren.setEnabled(false);
         symLeisteKndListenMode.setEnabled(false);
         symLeisteNotizenMode.setEnabled(false);
//         statusBarLevel.setText(""+0);
         statusBarFehler.setText(""+81);
         labelLevel.setText("");
         labelFehler.setText("Freie Zellen:");
         strOrg.setzeStradokuNeu();
         strBoard.setSelect(0);
         level = 0;
      } else {
         symLeisteStrEingabe.setEnabled(false);
         mdEingabe.setEnabled(false);
         mbEditieren.setText("Bearbeitung beenden");
         symLeisteStrEditieren.setToolTipText("Bearbeitung beenden");
         tmp_freieZellen = strOrg.getLoesungsZellen();
         labelFehler.setText("Freie Zellen:");
         statusBarFehler.setText("" + tmp_freieZellen);
         labelLevel.setText("Level:");
      }
      setTitle(appName + " - " + unbenannt);
      labelLevel.setText("Level:");
      statusBarLevel.setText("" + level);
      symLeisteTipp.setEnabled(false);
      symLeisteKndListenMode.setEnabled(false);
      symLeisteNotizenMode.setEnabled(false);
      symLeisteStrEingabe.setSelected(true);
      mdiErzeugen.setEnabled(false);
      symLeisteStrErzeugen.setEnabled(false);
      symLeisteSerieErzeugen.setEnabled(false);
      symLeisteStrLaden.setEnabled(false);
      symLeisteStrSpeichern.setEnabled(false);
      symLeisteListeZeigen.setEnabled(false);
      symLeisteStrZuListe.setEnabled(false);
      symLeisteStrVonListe.setEnabled(false);
      levelBox.setEnabled(false);
      naviPos.setEnabled(false);
      mdSerie.setEnabled(false);
      mdDateiLaden.setEnabled(false);
      mdSpeichern.setEnabled(false);
      mdSpeichernAls.setEnabled(false);
      mdStrInListe.setEnabled(false);
      mdStrAusListe.setEnabled(false);
      mdiBeendenOhneSicherung.setEnabled(false);
      mdBeendenMitSicherung.setEnabled(false);
      mdListenImport.setEnabled(false);
      mnPosSpeichern.setEnabled(false);
      mdiEinfuegen.setEnabled(false);
      mbLoesungBearbeiten.setEnabled(false);
      mbEditierenMitLW.setEnabled(false);
      menuAnzeige.setEnabled(false);
      menuLevel.setEnabled(false);
      menuNavi.setEnabled(false);
      mbTestMode.setEnabled(false);
      menuFarbMarkierung.setEnabled(false);
      statusBarZeit.setEnabled(false);
      statusBarHinweis.setText("Bearbeitungsmodus");
      labelHinweisfeld.setText("");
      sikNaviStatus();
      setNaviStatus(false, false, false, false, false);
   }

   /**
    * Erledigt Verwaltungsaufgaben beim Verlassen des Bearbeitungsmodus.
    *
    * @param neu Flag für neu zu erstellendes Stradoku
    */
   private void resetBearbeitungsModus(boolean neu) {
      loesungsModus = true;
      strBoard.setLoesungsModus(true);
      strOrg.setLoesungsModus(true);
      strOrg.setGestartet(false);
      if (neu) {
         strOrg.uebernehmeNeueAufgabe();
         strOrg.setVerlaufsNdx(0);
         strPath = "unbenannt";
         symLeisteStrEingabe.setToolTipText("Neues Stradoku eingeben");
         mdEingabe.setText("Neues Stradoku eingeben");
         symLeisteKndListenMode.setEnabled(true);
         symLeisteNotizenMode.setEnabled(true);
         mbEditieren.setEnabled(true);
      } else {
         symLeisteStrEditieren.setToolTipText("Aktuelles Stradoku bearbeiten");
         mbEditieren.setEnabled(true);
         mbEditieren.setText("Aktuelles Stradoku bearbeiten");
      }
      labelLevel.setText("Level:");
      statusBarLevel.setText("" + level);
      labelFehler.setText("Eingabefehler:");
      symLeisteTipp.setEnabled(true);
      statusBarFehler.setText("" + fehler);
      symLeisteKndListenMode.setEnabled(true);
      symLeisteNotizenMode.setEnabled(true);
      symLeisteStrEingabe.setSelected(false);
      symLeisteStrEingabe.setEnabled(true);
      symLeisteStrEditieren.setSelected(false);
      symLeisteStrEditieren.setEnabled(true);
      loesungsModus = true;
      editModus = 0;
      mdEingabe.setEnabled(true);
      mdiErzeugen.setEnabled(true);
      mdSerie.setEnabled(true);
      mdDateiLaden.setEnabled(true);
      mdSpeichern.setEnabled(true);
      mdSpeichernAls.setEnabled(true);
      mdStrInListe.setEnabled(true);
      mdStrAusListe.setEnabled(true);
      mdListenImport.setEnabled(true);
      mdiBeendenOhneSicherung.setEnabled(true);
      mdBeendenMitSicherung.setEnabled(true);
      mdiEinfuegen.setEnabled(true);
      mbLoesungBearbeiten.setEnabled(true);
      mbEditierenMitLW.setEnabled(true);
      menuAnzeige.setEnabled(true);
      menuLevel.setEnabled(true);
      menuNavi.setEnabled(true);
      menuFarbMarkierung.setEnabled(true);
      mbTestMode.setEnabled(true);
      levelBox.setEnabled(true);
      symLeisteStrErzeugen.setEnabled(true);
      symLeisteSerieErzeugen.setEnabled(true);
      symLeisteListeZeigen.setEnabled(true);
      symLeisteStrZuListe.setEnabled(true);
      symLeisteStrVonListe.setEnabled(true);
      symLeisteStrLaden.setEnabled(true);
      symLeisteStrSpeichern.setEnabled(true);
      setNaviStatus(KNAVISTATUS);
      setNaviStatus(NAVISTATUS);
      naviPos.setEnabled(true);
      setStatusBarHinweis("Lösungsmodus - "
            + strOrg.getFreiZellen(true) + " freie Zellen", false);
      statusBarZeit.setEnabled(true);
      setTitle(appName + " - " + strPath);
      symLeisteStrEditieren.setToolTipText("Aktuelles Stradoku bearbeiten");
      resetKandAnzeige(true);
      strOrg.setFilterKnd(0);
      setLevel(level);
   }

   /**
    * Generiert und zeigt ein neues Stradoku mit dem aktuell eingestelltem Level und fügt dieses
    * auch in die Zwischenablage ein.
    */
   private void aufgabeErstellen() {
      if (editModus > 0) {
         return;
      }
      int aktLevel = levelBox.getSelectedIndex();
      if (aktLevel <= 0) {
         JOptionPane.showMessageDialog(this,
               "Es muss erst ein gültiger Level eingestellt werden.\n"
               + "Wählen Sie einen Level zwischen 1 und 5.\n ",
               "Fehlermeldung", JOptionPane.ERROR_MESSAGE);
         return;
      }
      if (archiv == null) {
         archiv = new Archiv(homePath);
      }
      String fehlerTxt = archiv.isError();
      if (fehlerTxt != null) {
         JOptionPane.showMessageDialog(this,
               "Wegen eines Archivfehlers (" + fehlerTxt
               + ") kann keine Aufgabe generiert werden.",
               "Fehlermeldung", JOptionPane.ERROR_MESSAGE);
         isArchivFehler = true;
         return;
      }
      resetKandAnzeige(true);
      labelHinweisfeld.setText("");
      entferneTipp();
      strOrg.entferneMarkierungen();
      strOrg.setFilterKnd(0);
      level = strOrg.erzeugeStradoku(archiv, aktLevel);
      setLevel(level);
      strPath = unbenannt;
      setTitle(appName + " - " + strPath);
      setStatusBarLevel(level);
      usedKListe = false;
      usedKndFi = false;
      usedNotizen = false;
      usedTestmod = false;
      fehler = 0;
      gespikt = false;
      setStartZeit(0);
      strBoard.requestFocusInWindow();
      setNaviStatus(false, false, false, false, true);
      if (zeigeInfo) {
         zeigeHinweis(true);
      }
      if (archiv != null && !isArchivFehler) {
         archiv.schlieszeArchiv();
      }
      repaint();
   }

   /**
    * Ermittelt die Position des aktuellen Stradokus im Archiv.
    *
    * @return
    */
   public int getArchivPosition() {
      if (archiv == null) {
         archiv = new Archiv(homePath);
      }
      Position pos = archiv.getArchivPosition(strOrg.getAufgabe(), level);
      if (pos.z >= 0) {
         statusBarHinweis.setText(
               "Archivposition des aktuellen Stradoku: " + pos.z + " / " + pos.s);
      } else {
         statusBarHinweis.setText(
               "Das aktuelle Stradoku wurde im Archiv nicht gefunden!");
      }
      return pos.z;
   }

   /**
    * Überprüft das Archiv auf doppelte Einträge
    *
    * @throws IOException
    */
   public void checkDoppelEintraege() throws IOException {
      if (archiv == null) {
         archiv = new Archiv(homePath);
      }
      uhr.zeitAnzeigeStop();
      statusBarHinweis.setText(
            "Archiv wird auf doppelte sowie fehlerhafte Einträge überprüft.        "
            + "BITTE WARTEN !");
      archiv.checkDoppelVorkommen();
      statusBarHinweis.setText(
            "Überprüfung beendet. Das Ergebnis ist in der Datei:                   "
            + "ArchivCheck.txt.");
   }

   /**
    * Erstellt eine Serie Stradoku mit dem aktuell eingestelltem Level und fügt diese in die
    * Stradoku-Liste ein.
    */
   public void aufgabenSerieErstellen() {
      labelHinweisfeld.setText("");
      int aktLevel = levelBox.getSelectedIndex();
      int max = 200;
      if (editModus > 0) {
         return;
      }
      if (aktLevel == 0) {
         JOptionPane.showMessageDialog(this,
               "Es muss erst ein gültiger Level eingestellt werden.\n"
               + "Wählen Sie einen Level zwischen 1 und 5.\n",
               "Fehlermeldung", JOptionPane.ERROR_MESSAGE);
         return;
      }
      if (archiv == null) {
         archiv = new Archiv(homePath);
      }
      String fehlerTxt = archiv.isError();
      if (fehlerTxt != null) {
         JOptionPane.showMessageDialog(this,
               "Wegen eines Archivfehlers (" + fehlerTxt
               + ") kann kene Aufgabenserie erstellt werden.",
               "Fehlermeldung", JOptionPane.ERROR_MESSAGE);
         isArchivFehler = true;
         return;
      }
      symLeisteSerieErzeugen.setSelected(false);
      AbfrageSerieErzeugen strSerie = new AbfrageSerieErzeugen(this, max, true);
      int anzahl = 0;
      int[] arg = {aktLevel, anzahl};
      int ergebnis = strSerie.zeigeDialog(arg);
      anzahl = arg[1];
      if (ergebnis != 1) {
         return;
      }
      if (anzahl == 0 || anzahl > max) {
         JOptionPane.showMessageDialog(this,
               "\nIn einem Durchgang werden maximal " + max
               + " Aufgaben erzeugt.\n\n"
               + "Geben Sie Ihre Anforderung bitte neu ein.\n ",
               "Hinweis", 1);
         return;
      }
      strSerieThread = new ThreadStrSerie(archiv, strListe, anzahl, aktLevel);
      strSerieThread.start();
      strListe.setAnzahl();
      if (archiv != null && !isArchivFehler) {
         archiv.schlieszeArchiv();
      }
   }

   /**
    * Zeigt die Stradokuliste.
    */
   private void listeZeigen() {
      if (editModus > 0) {
         return;
      }
      strListe.setLocation(getX(), getY());
      strListe.setVisible(true);
      strBoard.requestFocusInWindow();
   }

   /**
    * Übernimmt nach Abfrage aus der Liste ein Stradoku in das Stradokufeld.
    */
   private void aufgabeAusListeLaden() {
      if (editModus > 0) {
         return;
      }
      AbfrageAusListeLaden strLaden = new AbfrageAusListeLaden(this, true);
      int anzahl = strListe.getAnzahl();
      int[] arg = {anzahl, 0};
      boolean gewaehlt = strLaden.zeigeDialog(arg);
      if (gewaehlt) {
         int auswahl = arg[1];
         if (auswahl > 0 && auswahl <= anzahl) {
            auswahl--;
            String strg = strListe.getStradoku(auswahl);
            if (strOrg.importStradokuString(strg, false)) {
               strPath = "A" + strListe.getNummer(auswahl);
               setImportStatus(strListe.getBemerkung(auswahl));
               setTitle(appName + " - " + strPath);
               usedKListe = false;
               usedKndFi = false;
               usedNotizen = false;
               testModus = false;
               fehler = 0;
               gespikt = false;
               modMenuBearbeitenTestmodus();
               usedTestmod = false;
               labelHinweisfeld.setText("");
               setStartZeit(0);
               setStatusBarLevel(level);
               setFehlerFreieZellen(0);
               resetKandAnzeige(true);
               strOrg.setFilterKnd(0);
               repaint();
               strBoard.requestFocusInWindow();
            }
         } else {
            JOptionPane.showMessageDialog(this,
                  "Eine Stradoku-Aufgabe mit der Nummer "
                  + auswahl + " gibt es nicht.",
                  "Hinweis", 1);
         }
      }
   }

   /**
    * Übernimmt von der Stradokuliste ein dort selektiertes Stradoku.
    *
    * @param nummer Zeile in der Liste für Anzeige in Kopfleiste
    * @param aufgabe Stradoku als String-Zeile
    * @param bemerkung Bemerkung für Anzeige in Statuszeile
    */
   public void setAufgabeAusListe(
         String nummer, String aufgabe, String bemerkung) {
      if (editModus > 0) {
         return;
      }
      strOrg.entferneMarkierungen();
      strOrg.setFilterKnd(0);
      if (strOrg.importStradokuString(aufgabe, false)) {
         strPath = "A" + nummer;
         setTitle(appName + " - " + strPath);
         labelHinweisfeld.setText("");
         setFehlerFreieZellen(0);
         usedKListe = false;
         usedKndFi = false;
         usedNotizen = false;
         usedTestmod = false;
         fehler = 0;
         gespikt = false;
         setStartZeit(0);
         strBoard.requestFocusInWindow();
         setImportStatus(bemerkung);
      }
   }

   /**
    * Gibt für übernommenes Stradoku Bemerkung in der Statuszeile aus.
    *
    * @param bemerkung Text der Bemerkung
    */
   private void setImportStatus(String bemerkung) {
      String hinweis = "Stradoku mit " + strOrg.getFreiZellen(true)
            + " freien Zellen übernommen.";
      if (bemerkung.length() > 0) {
         hinweis += " - (" + bemerkung + ")";
      }
      setStatusBarHinweis(hinweis, false);
   }

   /**
    * Überträgt aktuelles Stradoku in die Stradokuliste.
    */
   private void aufgabeInListeSpeichern() {
      if (strOrg.isEindeutig()) {
         int num = strListe.addStradoku(
               strOrg.getStringAufgabe(),
               "" + strOrg.getLevel(),
               strOrg.getLoesungsZellen(), "");
         if (num >= 0) {
            String tmp = "A" + vorNullen(num, 4);
            if (!strPath.contains(".str")) {
               strPath = tmp;
               setTitle(appName + " - " + strPath);
            }
            statusBarHinweis.setText(
                  "Stradoku in Liste unter " + tmp + " eingetragen.");
            strListe.setAnzahl();
         } else {
            statusBarHinweis.setText("Stradoku wurde nicht eingetragen.");
         }
         repaint();
         strBoard.requestFocusInWindow();
      } else {
         JOptionPane.showMessageDialog(this,
               "<html><br>Diese Stradoku-Aufgabe ist nicht eindeutig lösbar"
               + "<br>und wird daher nicht in die Liste übernommen."
               + "<br>&nbsp;",
               "Eindeutigkeitsprüfung", 2);
      }
   }

   /**
    * Übergibt Status der Navi-Startposition für das Speichern des Stradoku.
    *
    * @return Navi-Startposition
    */
   public boolean getNaviStart() {
      return naviTasteStart.isEnabled();
   }

   /**
    * Übergibt Status der Navi-Rückposition für das Speichern des Stradoku.
    *
    * @return Navi-Rückposition
    */
   public boolean getNaviZurueck() {
      return naviTasteZurueck.isEnabled();
   }

   /**
    * Übergibt Status der Navi-Vorposition für das Speichern des Stradoku.
    *
    * @return Navi-Vorposition
    */
   public boolean getNaviVor() {
      return naviTasteVor.isEnabled();
   }

   /**
    * Übergibt Status der aktuellen Navi-Punkt für das Speichern des Stradoku.
    *
    * @return aktuelle Navi-Punkt
    */
   public boolean getNaviAktPos() {
      return naviTasteAktPos.isEnabled();
   }

   /**
    * Übergibt Status der Navi-Gelöst-Punkt für das Speichern des Stradoku.
    *
    * @return Navi-Gelöst-Punkt
    */
   public boolean getNaviGeloest() {
      return naviTasteLoesung.isEnabled();
   }

   /**
    * Navi-Tasten werden initialisiert
    */
   public void sikNaviStatus() {
      KNAVISTATUS[0] = naviTasteStart.isEnabled();
      KNAVISTATUS[1] = naviTasteZurueck.isEnabled();
      KNAVISTATUS[2] = naviTasteVor.isEnabled();
      KNAVISTATUS[3] = naviTasteAktPos.isEnabled();
      KNAVISTATUS[4] = naviTasteLoesung.isEnabled();
   }

   /**
    * Navistatus wird gesetzt
    */
   public void setNaviStatus() {
      setNaviStatus(NAVISTATUS);
   }

   /**
    * Zuordnung der einzelnen Tasten
    *
    * @param start Starttaste
    * @param links Zurücktaste
    * @param rechts Vortaste
    * @param aktuell Taste für aktuelle Punkt
    * @param ende Lösungstaste
    */
   public void setNaviStatus(boolean start, boolean links, boolean rechts,
         boolean aktuell, boolean ende) {
      NAVISTATUS[0] = start;
      NAVISTATUS[1] = links;
      NAVISTATUS[2] = rechts;
      NAVISTATUS[3] = aktuell;
      NAVISTATUS[4] = ende;
      setNaviStatus(NAVISTATUS);
   }

   /**
    * Setzt für alle Navi-Parameter die aktuellen Positionen.
    *
    * @param navStat Boolean-Array mit den Statuswerten
    */
   public void setNaviStatus(boolean[] navStat) {
      naviTasteStart.setEnabled(navStat[0]);
      naviTasteZurueck.setEnabled(navStat[1]);
      naviTasteVor.setEnabled(navStat[2]);
      naviTasteAktPos.setEnabled(navStat[3]);
      naviTasteLoesung.setEnabled(navStat[4]);
      mnStart.setEnabled(navStat[0]);
      mnRueck.setEnabled(navStat[1]);
      mnVor.setEnabled(navStat[2]);
      mniAktuell.setEnabled(navStat[3]);
      mnLoesung.setEnabled(navStat[4]);
      naviPosition = strOrg.getVerlaufsNdx();
      int pos = strOrg.getGeloestNdx();
      naviPos.setText("" + (pos > 0 ? pos : naviPosition));
   }

   /**
    * Sichert für alle Navi-Parameter die aktuellen Positionen.
    *
    * @param navStat Boolean-Array mit den Statuswerten
    */
   public void getNaviStatus(boolean[] navStat) {
      naviTasteStart.setEnabled(navStat[0]);
      naviTasteZurueck.setEnabled(navStat[1]);
      naviTasteVor.setEnabled(navStat[2]);
      naviTasteAktPos.setEnabled(navStat[3]);
      naviTasteLoesung.setEnabled(navStat[4]);
      mnStart.setEnabled(navStat[0]);
      mnRueck.setEnabled(navStat[1]);
      mnVor.setEnabled(navStat[2]);
      mniAktuell.setEnabled(navStat[3]);
      mnLoesung.setEnabled(navStat[4]);
      naviPosition = strOrg.getVerlaufsNdx();
      int pos = strOrg.getGeloestNdx();
      naviPos.setText("" + (pos > 0 ? pos : naviPosition));
   }

   /**
    * Setzt alle Navi-Positionen auf die Startwerte zurück.
    */
   public void resetNaviPosition() {
      Stradoku.this.setNaviStatus(false, false, false, false, true);
      mnSpeicherPos.setEnabled(false);
   }

   /**
    * gespeicherte Lösungsposition setzen
    *
    * @param pos Positionsindex
    */
   public void setNaviPosition(int pos) {
      naviPos.setText("" + pos);
      naviPosition = pos;
   }

   /**
    * Index für aktuelle Lösungsposition zurück geben
    *
    * @return aktuelle Lösungspositio
    */
   public int getNaviPosition() {
      return naviPosition;
   }

   /**
    * Gibt den Info-String zu diesem Programm zurück.
    *
    * @return InfoString
    */
   public String getInfoString() {
      return infoString;
   }

   /**
    * Gibt die Länge des Infostrings zurück
    *
    * @return Länge InfoString
    */
   public int getLenInfoString() {
      return infoString.length();
   }

   /**
    * Gibt die Anzahl der bisher gemachten Fehler beim Lösen des aktuellen Stradoku zurück.
    *
    * @return Anzahl der Fehler
    */
   public int getFehler() {
      return fehler;
   }

   /**
    * Setzt Eingabefehler auf Null
    */
   public void resetFehler() {
      fehler = 0;
   }

   /**
    * Setzt die Anzahl der bisher gemachten Fehler und gibt eine Info in der Statuszeile aus.
    *
    * @param flr Fehlerzahl
    */
   public void setFehlerFreieZellen(int flr) {
      fehler = flr;
      statusBarFehler.setText("" + fehler);
   }

   /**
    * Gibt die aktuelle Anzahl der Lösungshinweise zurück.
    *
    * @return Anzahl der Lösungshinweise
    */
   public int getTipps() {
      return tipps;
   }

   /**
    * Setzt Lösungshinweise zurück.
    */
   public void rsetTipps() {
      tipps = 0;
   }

   /**
    * Setzt die Anzahl der Lösungshinweise.
    *
    * @param tp
    */
   public void setTipps(int tp) {
      tipps = tp;
   }

   /**
    * Gibt die X-Punkt der linken obere Ecke des Anwendungsfensters zurück.
    *
    * @return X-Punkt
    */
   public int getPosX() {
      return getX();
   }

   /**
    * Gibt die Y-Punkt der linken obere Ecke des Anwendungsfensters zurück.
    *
    * @return Y-Punkt
    */
   public int getPosY() {
      return getY();
   }

   /**
    * Setzt die Levelanzeige in der Statusbar.
    *
    * @param lv anzuzeigender Level
    */
   public void setStatusBarLevel(int lv) {
      statusBarLevel.setText("" + lv);
   }

   /**
    * Gibt den Status für die Anzeige der Kandidaten zurück.
    *
    * @return Status der Kandidatenanzeige
    */
   public boolean getZeigeKnd() {
      return zeigeKnd;
   }

   /**
    * Aktiviert bzw. deaktiviert den KndListenMode.
    *
    * @param anzeige true, wenn der Moduswechsel angezeigt werden soll
    */
   public void setKndListenMode(boolean anzeige) {
      if (kndModus != KLISTE) {
         // Listenmodus aktivieren
         if (kndModus == KNOTIZ) {
            setNotizenMode(false);
         }
         kndModus = KLISTE;
//         strOrg.entferneNotizen(false);
         usedKListe = true;
         strBoard.setKandidatenModus(1);
         symLeisteKndListenMode.setSelected(true);
         symLeisteNotizenMode.setSelected(false);
         symLeisteKndListenMode.setIcon(new javax.swing.ImageIcon(
               getClass().getResource("/stradoku/img/knd_red_pas.png")));
         symLeisteKndListenMode.setToolTipText(
               "Anzeige der Kandidatenliste beenden");
         labelKandidaten.setForeground(weisz);
         for (int i = 1; i <= 9; i++) {
            kandidatTaste[i].setEnabled(true);
            kandidatTaste[i].setToolTipText("Kandidat entfernen");
         }
         if (anzeige) {
            setStatusBarHinweis("Kandidatenlistenmodus aktiviert", false);
            
         }
         setTestModus(false);
      } else {
         // Listenmodus deaktivieren
         labelHinweisfeld.setText("");
         kndModus = 0;
         strBoard.setKandidatenModus(0);
         symLeisteKndListenMode.setIcon(
               new javax.swing.ImageIcon(getClass().getResource(
                     "/stradoku/img/knd_red_akt.png")));
         symLeisteKndListenMode.setToolTipText(
               "Kandidatenliste anzeigen");
         symLeisteKndListenMode.setSelected(false);
      }
      // Kl-Mode in Verlauf eintragen in Bearbeitung
      if (kndModus == 0) {
         labelKandidaten.setForeground(grau);
         strOrg.setFilterKnd(0);
         markKndTaste(0);
         for (int i = 1; i <= 9; i++) {
            kandidatTaste[i].setEnabled(false);
            kandidatTaste[i].setToolTipText("");
         }
         if (anzeige) {
            setStatusBarHinweis("Kandidatenlistenmodus deaktiviert", false);
         }
      }
      if (zeigeInfo) {
         zeigeHinweis(true);
      }
      setLevel(level);
      strBoard.requestFocusInWindow();
      repaint();
   }

   /**
    * Aktiviert bzw. deaktiviert den Notizenmodus.
    */
   public void setNotizenMode(boolean anzeige) {
      if (kndModus != KNOTIZ) {
         // NotizenMode aktivieren
         if (kndModus == KLISTE) {
            setKndListenMode(false);
         }
         kndModus = KNOTIZ;
         strBoard.setKandidatenModus(2);
         symLeisteNotizenMode.setIcon(new javax.swing.ImageIcon(
               getClass().getResource("/stradoku/img/kl_passiv.png")));
         symLeisteNotizenMode.setToolTipText(
               "Kandidaten-Notiermodus beenden");
         labelKandidaten.setForeground(weisz);
         symLeisteKndListenMode.setSelected(false);
         symLeisteNotizenMode.setSelected(true);
         for (int i = 1; i <= 9; i++) {
            kandidatTaste[i].setEnabled(true);
            kandidatTaste[i].setToolTipText("Kandidat setzen/entfernen");
         }
         if (anzeige) {
            setStatusBarHinweis("Notizenmodus aktiviert", false);
         }
      } else {
         // NotizenMode deaktivieren
         if (testModus) {
            setTestModus(false);
//            strOrg.entferneNotizen(true);
         }
         kndModus = 0;
         strBoard.setKandidatenModus(0);
         symLeisteNotizenMode.setIcon(
               new javax.swing.ImageIcon(getClass().getResource(
                     "/stradoku/img/kl_aktiv.png")));
         symLeisteNotizenMode.setToolTipText(
               "Kandidaten-Notiernmodus aktivieren");
         symLeisteNotizenMode.setSelected(false);
      }
      if (kndModus == 0) {
         labelKandidaten.setForeground(grau);
         strOrg.setFilterKnd(0);
         markKndTaste(0);
         for (int i = 1; i <= 9; i++) {
            kandidatTaste[i].setEnabled(false);
            kandidatTaste[i].setToolTipText("");
         }
         if (anzeige) {
            setStatusBarHinweis("Notizenmodus deaktiviert", false);
         }
         symLeisteKndListenMode.setSelected(false);
         symLeisteNotizenMode.setSelected(false);
      }
      if (zeigeInfo) {
         zeigeHinweis(true);
      }
      setLevel(level);
      strBoard.requestFocusInWindow();
      repaint();
   }

   /**
    * Setzt beide Kandidatenmodi zurück.
    *
    * @param anzeige true wenn Änderung in Statuszeile angezeigt werden soll
    */
   public void resetKandAnzeige(boolean anzeige) {
      if (kndModus == KLISTE) {
         setKndListenMode(anzeige);
         symLeisteKndListenMode.setSelected(true);
      } else if (kndModus == KNOTIZ) {
         setNotizenMode(anzeige);
         symLeisteNotizenMode.setSelected(true);
      } else {
         strBoard.setKandidatenModus(0);
         symLeisteKndListenMode.setIcon(
               new javax.swing.ImageIcon(getClass().getResource(
                     "/stradoku/img/knd_red_akt.png")));
         symLeisteKndListenMode.setToolTipText(
               "Kandidatenliste anzeigen");
         symLeisteNotizenMode.setIcon(
               new javax.swing.ImageIcon(getClass().getResource(
                     "/stradoku/img/kl_aktiv.png")));
         symLeisteNotizenMode.setToolTipText(
               "Kandidaten-Notiernmodus aktivieren");
         symLeisteKndListenMode.setEnabled(true);
         symLeisteKndListenMode.setSelected(false);
         symLeisteNotizenMode.setSelected(false);
      }
      symLeisteTipp.setEnabled(true);
      zeigeKnd = false;
   }

   /**
    * Abfrage des aktuellen Kandidatenmodus
    *
    * @return 0=keiner, 1=Listenmodus, 2=Notitzenmosus
    */
   public int getKndModus() {
      return kndModus;
   }

   /**
    * Setzt den aktuellen Kandidatenmodus neu
    *
    * @param mode zu setzender Modus
    * @param anzeige true, wenn der Moduswechsel angezeigt werden soll
    */
   public void setKndModus(int mode, boolean anzeige) {
      if (mode == 1) {
         setKndListenMode(anzeige);
         symLeisteKndListenMode.setSelected(true);
         symLeisteNotizenMode.setSelected(false);
      } else if (mode == 2) {
         setNotizenMode(anzeige);
         symLeisteKndListenMode.setSelected(false);
         symLeisteNotizenMode.setSelected(true);
      } else {
         resetKandAnzeige(anzeige);
      }
   }

   /**
    * Gibt den Anzeigemodus für die Kandidaten zurück.
    *
    * @return true wenn Ziffern-, false wenn Punktanzeige
    */
   public boolean getKndAnzeigeMod() {
      return zifferpunkt;
   }

   /**
    * Setzt für die Kandidatenanzeige die Punkt-Darstellung.
    *
    * @param modKnd : true für Punktedarstellung, false für Ziffern
    */
   public void setKndAnzeigeMod(boolean modKnd) {
      zifferpunkt = modKnd;
      strBoard.setKndAnzeige(zifferpunkt);
      maKndAnzeige.setSelected(modKnd);
   }

   /**
    * Übergibt das aktuelle Stradoku der Zwischenablage.
    *
    * @param intern bestimmt das Format (siehe MakeStradokuString())
    * @param anzeige True für Übernahme in Statuszeile angezeigen
    */
   private void kopierenZuClipboard(boolean intern, boolean anzeige) {
      clipboard = new FromToClipboard(this, strOrg, strBoard);
      clipboard.strToClipboard(intern, anzeige);
   }

   /**
    * Kopiert das aktuelle Stradoku-Feld in die Zwischenablage.
    *
    * @param rand Flag ob der Rahmen mit kopiert wer den soll
    */
   private void kopierenFeld(boolean rand) {
      clipboard = new FromToClipboard(this, strOrg, strBoard);
      clipboard.imgToClipboard(rand);
   }

   /**
    * Übernimmt von der Zwischenablage ein Stradoku und veranlasst, dass dieses als aktuelles
    * Stradoku angezeigt wird.
    */
   private void einfuegenVonClipboard() {
      sichernKonfiguration();
      clipboard = new FromToClipboard(this, strOrg, strBoard);
      if (clipboard.strFromClipboard()) {
         setStrName(unbenannt);
         setTitel();
         setTestModus(false);
         usedTestmod = false;
         usedNotizen = false;
         usedKListe = false;
         usedKndFi = false;
         gespikt = false;
         fehler = 0;
         rsetTipps();
         modMenuBearbeitenTestmodus();
      } else {
         ladenKonfiguration("", false);
      }
   }

   /**
    * Übernimmt einen Level und aktualisiert damit alle Levelanzeigen.
    *
    * @param lvl neuer Level
    */
   public void setLevel(int lvl) {
      level = (lvl > 5) ? lvl & ZAHL : lvl;
      setLevelBox(level);
      setLevelMenu(level);
      setStatusBarLevel(level);
   }

   /**
    * Füllt numerischen Wert mit führenden Nullen auf.
    *
    * @param wrt -aufzufüllender Wert
    * @param len Ziellänge
    * @return String mit dem aufgefüllten numerischen Wert
    */
   private String vorNullen(int wrt, int len) {
      String strg = "";
      String tmpStrg = "" + wrt;
      int n = len - tmpStrg.length();
      for (int i = 0; i < n; i++) {
         strg += "0";
      }
      strg += tmpStrg;
      return strg;
   }

   /**
    * Bildet für die Anzeige in der Kopfleiste den Titel.
    */
   public void setTitel() {
      setTitle(appName + " - " + strPath);
   }

   /**
    * Abfrage des Programmnamens
    *
    * @return Anwendungsname
    */
   public String getAppName() {
      return appName;
   }

   /**
    * Setzt den Pfad des aktuellen Stradokus
    *
    * @param path vollständiger Pfad des aktuellen bereits gespeicherten Stradokus
    */
   public void setStrPath(String path) {
      strPath = path;
   }

   /**
    * Übergibt den Pfad für das aktuell zu speichernde Stradoku.
    *
    * @return Pfad, Pfad in den gespeichert werden soll
    */
   public String getFilePath() {
      return filePath;
   }

   /**
    * Setzt den Pfad für zu speichernde Stradokus.
    *
    * @param dir zu setzender Pfad
    */
   public void setFilePath(String dir) {
      filePath = dir;
   }

   /**
    * Gibt das Arbeitsverzeichnis des Programms zurück.
    *
    * @return Arbeitsverzeichnis
    */
   public String getHomePath() {
      return homePath;
   }

   /**
    * Gibt den Standardordner für Stradoku zurück.
    *
    * @return Stradokuordner
    */
   public String getStrFolder() {
      return strFolder;
   }

   /**
    * Gibt den Dateinamen eines bereits gespeicherten Stradoku zurück.
    *
    * @return Dateiname
    */
   public String getStrName() {
      return strPath;
   }

   /**
    * Übernimmt den Dateinamen eines Stradoku.
    *
    * @param str_Name Dateiname
    */
   public void setStrName(String str_Name) {
      if (str_Name == null) {
         strPath = unbenannt;
      } else {
         strPath = str_Name;
      }
   }

   /**
    * Wertet die Navigationstasten Start, Lösungsposition und Lösung aus.
    *
    * @param aufgabe 1 = Start, 2 = LPosition, 3 = gespeicherte Punkt, 4 = Lösung
    */
   private void setNavi(int aufgabe) {
      if (testModus) {
         return;
      }
      labelHinweisfeld.setText("");
      strOrg.gotoNaviPosition(aufgabe);
      switch (aufgabe) {
         case 1:
            symLeisteKndListenMode.setEnabled(true);
            symLeisteNotizenMode.setEnabled(true);
            symLeisteTipp.setEnabled(true);
            zeigtLoesung = false;
            break;
         case 4:
            symLeisteKndListenMode.setEnabled(false);
            symLeisteNotizenMode.setEnabled(false);
            symLeisteTipp.setEnabled(false);
            kndModus = 0;
            symLeisteTipp.setEnabled(false);
            zeigtLoesung = true;
            break;
         default:
            symLeisteKndListenMode.setEnabled(true);
            symLeisteNotizenMode.setEnabled(true);
            symLeisteTipp.setEnabled(true);
            zeigtLoesung = false;
            break;
      }
      repaint();
      strBoard.requestFocusInWindow();
   }

   /**
    * Gibt einen Hinweis aus oder löscht einen solchen.
    *
    * @param hnw auszugebender Text
    */
   public void zeigeHinweis(boolean hnw) {
      if (hnw) {
         if (loesungsModus) {
            if (kndModus == 0) {
               labelHinweisfeld.setText(EINGABEMODUS);
            } else if (kndModus == 1) {
               labelHinweisfeld.setText(KNDLISTENMODUS);
            } else {
               labelHinweisfeld.setText(NOTIZENMODUS);
            }
         } else {
            labelHinweisfeld.setText(EDITMODUS);
         }
      } else {
         labelHinweisfeld.setText("");
      }
   }

   /**
    * Entfernt einen Lösungshinweis, ändert das QuickInfo füt die Tipp-Taste und deaktiviert die
    * Symboltaste für Zeigen der Lösungstechniken.
    */
   public void entferneTipp(/*boolean entferneMarkierung*/) {
      labelHinweisfeld.setText("");
   }

   /**
    * Gibt die für die Navigation gespeicherte Verlaufsposition zurück.
    *
    * @return Positionsindex
    */
   public int getPosSicherung() {
      return posSicherung;
   }

   /**
    * Setzt die für die Navigation gespeicherte Verlaufsposition zurück.
    */
   public void resetPosSicherung() {
      posSicherung = 0;
   }

   /**
    * Gibt die Programmversion zurück.
    *
    * @return Programmversion
    */
   public String getVersion() {
      return VERSION;
   }

   /**
    * Setzt in der Statuszeile die bisher für das aktuelle Stradoku benötigte Lösungszeit (Auflösung
    * eine Minute).
    */
   public void setLoesungsZeit() {
      loesungsZeit = uhr.getLoesungsZeit();
      uhr.zeitAnzeigeStop();
   }

   /**
    * Übergibt für das binäre Speichern die bisherige Lösungszeit.
    *
    * @return verbrauchte Lösungszeit
    */
   public long getLoesungsZeit() {
      if (strOrg.getGeloest()) {
         return loesungsZeit;
      } else {
         return uhr.getLoesungsZeit();
      }
   }

   /**
    * Setzt die Startzeit und startet mit der übergebenen Zeit die Zeitanzeige.
    *
    * @param zeit die zu setztende Zeit
    */
   public void setStartZeit(long zeit) {
      loesungsZeit = zeit;
      uhr.zeitAnzeigeStart(System.currentTimeMillis() - zeit);
      if (strOrg.getGeloest() || !strOrg.getGestartet()) {
         uhr.zeitAnzeigeStop();
      }
   }

   /**
    * Setzt die bisher verbrauchte Lösungszeit.
    *
    * @param std Stunden
    * @param min Minuten
    */
   public void setZeit(int std, int min) {
      statusBarZeit.setText(""
            + (std > 9 ? std : "0" + std) + ":"
            + (min > 9 ? min : "0" + min));
   }

   /**
    * Initialisierung der Kandidaten-Tasten
    */
   private void initKandidatenTasten() {
      kandidatTaste[0] = null;
      kandidatTaste[1] = kandidat_1;
      kandidatTaste[2] = kandidat_2;
      kandidatTaste[3] = kandidat_3;
      kandidatTaste[4] = kandidat_4;
      kandidatTaste[5] = kandidat_5;
      kandidatTaste[6] = kandidat_6;
      kandidatTaste[7] = kandidat_7;
      kandidatTaste[8] = kandidat_8;
      kandidatTaste[9] = kandidat_9;
   }

   /**
    * Markiert Taste von gefilterten Kandidaten
    *
    * @param fKnd
    */
   public void markKndTaste(int fKnd) {
      int markerKnd;
      if ((fKnd & ANKND) > 0) {
         markerKnd = strOrg.getKndSingle(fKnd >> 14);
      } else if ((fKnd & AKND) > 0) {
         markerKnd = strOrg.getKndSingle(fKnd >> 5);
      } else {
         markerKnd = fKnd;
      }
      for (int i = 1; i <= 9; i++) {
         kandidatTaste[i].setForeground(Color.BLUE);
      }
      if (markerKnd > 0) {
         kandidatTaste[markerKnd].setForeground(Color.magenta);
      }
   }

   /**
    * Initialisierung der Level-Namen
    */
   private void initLevelNamen() {
      levelName[0] = ml_0;
      levelName[1] = ml_1;
      levelName[2] = ml_2;
      levelName[3] = ml_3;
      levelName[4] = ml_4;
      levelName[5] = ml_5;
   }

   /**
    * Prüft, ob die Anwendung beendet werden kann.
    *
    * @return True wenn Beendung möglich
    */
   private boolean beendeAnwendung() {
      boolean isExit = true;
      if (editModus != 0) {
         String[] yesNoOptions = {"Ja", "Nein"};
         int op = JOptionPane.showOptionDialog(this,
               "\n<html><b>Der Bearbeitungsmodus ist aktiv.</b>\n\n"
               + "Wenn das Programm beendet werden soll,\n"
               + "wird er deaktiviert.\n\n"
               + "Soll das Programm wirklich beendet werden?\n\n",
               "Sicherheitsabfrage",
               JOptionPane.YES_NO_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null, yesNoOptions, yesNoOptions[1]);
         if (op == JOptionPane.NO_OPTION) {
            isExit = false;
         }
      } else if (testModus) {
         String[] yesNoOptions = {"Ja", "Nein"};
         int op = JOptionPane.showOptionDialog(this,
               "\n<html><b>Der Testmodus ist aktiv.</b>\n\n"
               + "Wenn das Programm beendet werden soll,\n"
               + "wird er deaktiviert.\n\n"
               + "Soll das Programm wirklich beendet werden?\n\n",
               "Sicherheitsabfrage",
               JOptionPane.YES_NO_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null, yesNoOptions, yesNoOptions[1]);
         if (op == JOptionPane.NO_OPTION) {
            isExit = false;
         }
      } else {
         if (geaendert && strPath.lastIndexOf(".str") > 0) {
            String[] yesNoOptions = {"Ja", "Nein"};
            int op = JOptionPane.showOptionDialog(this,
                  "\n<html><b>Dieses Stradoku wurde verändert.</b>\n\n"
                  + "Soll es gespeichert werden?\n\n",
                  "Sicherheitsabfrage",
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null, yesNoOptions, yesNoOptions[1]);
            if (op == JOptionPane.YES_OPTION) {
               strSpeichern(false);
            }
         }
         sichernKonfiguration();
      }
      if (archiv != null && !isArchivFehler) {
         archiv.schlieszeArchiv();
      }
      return isExit;
   }

   /**
    * Für die Speicherung eine Bildes wird das Stradokufeld übergeben.
    *
    * @return Stradokufeld
    */
   public Component getStradokuFeld() {
      return stradokuFeld;
   }

   /**
    * Setzt den Standardpfad für die Speicherung eines Bildes.
    *
    * @param pfad Standardpfad für Bilder des Dudokufeldes
    */
   public void setPngPfad(String pfad) {
      pngFolder = pfad;
   }

   /**
    * Übergibt den Standardpfad für die Speicherung eines Bildes.
    *
    * @return Standardpfad für Bilder des Dudokufeldes
    */
   public String getPngPfad() {
      return pngFolder;
   }

   /**
    * Antwortet auf Frage nach Neueingabe
    *
    * @return -True bei
    */
   public boolean isNeueingabe() {
      return editModus == EDIT_NEU;
   }

   /**
    * Setzt die aktuelle Lösungsposition.
    */
   private void setSpeicherPos() {
      posSicherung = strOrg.getVerlaufsNdx();
      strOrg.setSavePosNdx(posSicherung);
      mnSpeicherPos.setEnabled(true);
      statusBarHinweis.setText("Aktuelle Lösungsposition ("
            + posSicherung + ") gespeichert");
   }

   /**
    * Markiert die selektierte Zelle farbig.
    *
    * @param farbe Farbe, in der markiert wird
    */
   private void setzeMarkierung(int farbe) {
      strOrg.setMarkierung(strBoard.getSelect(), farbe, true);
      repaint();
   }

   /**
    * Übernimmt nach der Lösung einer Aufgabe die erforderlichen Aktionen.
    */
   public void geloest() {
      setStatusBarHinweis("Diese Aufgabe ist gelöst.", true);
      setNaviStatus(true, true, false, false, false);
      setKndModus(0, true);
      setLoesungsZeit();
      long zeit = uhr.zeitAnzeigeStop() / MINUTE;
      int std = (int) zeit / 60;
      int min = (int) zeit - std * 60;
      String zet;
      if (std > 0) {
         zet = String.valueOf(std) + " Std. " + String.valueOf(min) + " Min.";
      } else {
         zet = String.valueOf(min) + " Min.";
      }
      String not = usedNotizen ? "ja" : "nein";
      String kli = usedKListe ? "ja" : "nein";
      String kafi = usedKndFi ? "ja" : "nein";
      String tmod = usedTestmod ? "ja" : "nein";
      String efe = String.valueOf(fehler);
      String spi = gespikt ? "ja" : "nein";
      String tip = tipps > 0 ? "ja, " + tipps + " x" : "nein";
      if ("0".equals(efe)) {
         efe = "keine";
      }
      labelHinweisfeld.setText(
            "<html>"
            + "<h3>Gratulation - die Aufgabe ist gelöst!</h3>"
            + "<br><table>"
            + "<tr><td><b>Benötigte Zeit:</td><td>" + zet + "</td></tr>"
            + "<tr><td><b>Notizen verwendet:</td><td>" + not + "</td></tr>"
            + "<tr><td><b>Kandidatenliste verwendet:</td><td>" + kli + "</td></tr>"
            + "<tr><td><b>Kandidatenfilter verwendet:</td><td>" + kafi + "</td></tr>"
            + "<tr><td><b>Testmodus verwendet:</td><td>" + tmod + "</td></tr>"
            + "<tr><td><b>Lösungstipps genutzt:</td><td>" + tip + "</td></tr>"
            + "<tr><td><b>Du hast gespickt:</td><td>" + spi + "</td></tr>"
            + "<tr><td><b>Eingabefehler:</td><td>" + efe + "</td></tr>"
            + "</table></html>");
      kndModus = 0;
      strOrg.setFilterKnd(0);
      strBoard.setSelect(-1);
      resetKandAnzeige(true);
   }

   /**
    * Setzt Flag geaendert und stellt die Positionssicherung zurück
    *
    * @param set true, wenn geaendert gesetzt werden soll, sonst false
    */
   public void set_geaendert(boolean set) {
      geaendert = set;
      if (geaendert) {
         posSicherung = 0;
      }
   }

   /**
    * Anfrage ob Anwender mit Notizen gearbeitet hat.
    *
    * @return true, wenn Notizen verwendet wurden, sonst false
    */
   public boolean get_usedNotizen() {
      return usedNotizen;
   }

   /**
    * Setzt usedNotizen bei der Öffnung einer gespeicherten Aufgabe
    *
    * @param used zu setzendes Flag
    */
   public void set_usedNotizen(boolean used) {
      usedNotizen = used;
   }

   /**
    * Anfrage ob Anwender mit der Kandidatenliste gearbeitet hat.
    *
    * @return true, wenn Notizen verwendet wurden, sonst false
    */
   public boolean get_usedKListe() {
      return usedKListe;
   }

   /**
    * Setzt used_KListe bei der Öffnung einer gespeicherten Aufgabe
    *
    * @param used zu setzendes Flag
    */
   public void set_usedKListe(boolean used) {
      usedKListe = used;
   }

   /**
    * Anfrage ob Anwender mit der Kandidatenliste gearbeitet hat.
    *
    * @return true, wenn Notizen verwendet wurden, sonst false
    */
   public boolean get_usedKndFi() {
      return usedKndFi;
   }

   /**
    * Setzt used_KListe
    *
    * @param used zu setzendes Flag
    */
   public void set_usedKndFi(boolean used) {
      usedKndFi = used;
   }

   /**
    * Setzt used_KListe bei der Öffnung einer gespeicherten Aufgabe
    *
    * @param used zu setzendes Flag
    */
   public void set_usedTestmod(boolean used) {
      usedTestmod = used;
   }

   /**
    * Abfrage, ob für die Lösung der Testmodus eingesetzt wurde
    *
    * @return true, wenn er eingesetzt wurde, sonst false
    */
   public boolean get_usedTestmod() {
      return usedTestmod;
   }

   /**
    * Übergibt den Status für den Lösungsmodus
    *
    * @return true wenn Lösungsmodus aktiv, sonst false
    */
   public boolean getLoesungsModus() {
      return loesungsModus;
   }

   /**
    * Gibt Info ob ein Import von Stradoku erchivtauglich erfolgen soll. Archivtauglich ist ein
    * Stradoku, wenn es in allen 16 Varianten die selbes Lösung hat.
    *
    * @return true wenn archivtauglich
    */
   public boolean get_archivtauglich() {
      return archivtauglich;
   }

   /**
    * Abfrage des Bearbeitungsmodus
    *
    * @return true wenn Eingabe- oder Bearbeitungsmodus
    */
   public int getEditModus() {
      return editModus;
   }

   /**
    * Rendert die Ausgabe für die Levelbox so, dass der Level 0 ausgegraut ersheint.
    */
   class ComplexCellRenderer implements ListCellRenderer {

      protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

      @Override
      public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

         JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(
               list, value, index, isSelected, cellHasFocus);
         Color theForeground;
         if (index == 0) {
            theForeground = Color.LIGHT_GRAY;
         } else {
            theForeground = Color.BLACK;
         }
         String theText = "";
         if (value != null) {
            theText = value.toString();
         }
         renderer.setForeground(theForeground);
         renderer.setText(theText);
         return renderer;
      }
   }

   /**
    * Aktiviert bzw. deaktiviert den Testmodus
    */
   void modMenuBearbeitenTestmodus() {
      if (testModus) {
         mbTestMode.setSelected(true);
         mbTestMode.setText("Testmodus deaktivieren");
      } else {
         mbTestMode.setSelected(false);
         mbTestMode.setText("Testmodus aktivieren");
      }
   }

   /**
    * Steuert die Ausgabe von Lösungshinweise
    *
    * @param ziel Position der zu ändernden Zelle
    * @param ltps Beschreibung des Lösungstipps
    */
   public void setLoeTipps(int ziel, String ltps) {
      if (ltps.length() == 0) {
         labelHinweisfeld.setText(
               "<html><br><h3>&nbsp;&nbsp;&nbsp;&nbsp;Keine Lösungstipps für Level 5 !<br>");
      } else {
         if (kndModus != KLISTE) {
            setKndModus(KLISTE, true);
         }
         loeTipps = ltps;
         String kopfZeile;
         kopfZeile = "<html><br><h2>Möglicher Lösungsschritt<br></h2><br><br><font size=\"5\">";
         labelHinweisfeld.setText(kopfZeile + loeTipps);
         if (ziel >= 0) {
            strBoard.setSelect(ziel);
            strBoard.setShowSelector();
            setzeMarkierung(M_OKER);
            repaint();
         }
      }
   }

   /**
    * Abfrage ob Lösungstipp gezeigt wird
    *
    * @return
    */
   public boolean get_isTipp() {
      return isTipp;
   }

   /**
    * Stellt Flag für Lösungstipp zurück
    */
   public void reset_isTipp() {
      isTipp = false;
   }

   /**
    * Startet bzw. entfernt den Testmodus
    */
   public void makeTestModus() {
      if (kndModus != 1 && !testModus) {
         setKndListenMode(true);
      }
      if (!testModus) {
         mbTestMode.setSelected(true);
         symLeisteTipp.setEnabled(false);
         mbTestMode.setText("Testmodus deaktivieren");
         setNotizenMode(true);
         strOrg.entferneNotizen(false);
         strOrg.copyKandidaten();
         set_usedTestmod(true);
         symLeisteNotizenMode.setEnabled(false);
         menuNavi.setEnabled(false);
         testModus = true;
         setStatusBarHinweis("Testmodus aktiviert", true);         
      } else {
         mbTestMode.setSelected(false);
         mbTestMode.setText("Testmodus aktivieren");
//         strOrg.entferneNotizen(false);
         setKndListenMode(true);
         testModus = false;
         menuNavi.setEnabled(true);
         symLeisteNotizenMode.setEnabled(true);
         symLeisteTipp.setEnabled(true);
         setStatusBarHinweis("Testmodus deaktiviert", true);
      }
   }

   /**
    * Gibt zurück, ob die Lösung gezeigt wird
    *
    * @return true, wenn sie gezeigt wird
    */
   public boolean zeigtLoesung() {
      return zeigtLoesung;
   }

   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        levelGroup = new javax.swing.ButtonGroup();
        jMenuItem1 = new javax.swing.JMenuItem();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JSeparator();
        stradokuFeld = new javax.swing.JPanel();
        toolBar = new javax.swing.JToolBar();
        seperatorLabel8 = new javax.swing.JLabel();
        symLeisteStrLaden = new javax.swing.JButton();
        symLeisteStrSpeichern = new javax.swing.JButton();
        seperatorLabel1 = new javax.swing.JLabel();
        naviTasteStart = new javax.swing.JButton();
        naviTasteZurueck = new javax.swing.JButton();
        naviPos = new javax.swing.JButton();
        naviTasteVor = new javax.swing.JButton();
        naviTasteAktPos = new javax.swing.JButton();
        naviTasteLoesung = new javax.swing.JButton();
        seperatorLabel2 = new javax.swing.JLabel();
        ListCellRenderer renderer = new ComplexCellRenderer();
        levelBox = new javax.swing.JComboBox();
        seperatorLabel3 = new javax.swing.JLabel();
        symLeisteStrErzeugen = new javax.swing.JButton();
        symLeisteSerieErzeugen = new javax.swing.JButton();
        seperatorLabel4 = new javax.swing.JLabel();
        symLeisteStrEditieren = new javax.swing.JToggleButton();
        symLeisteStrEingabe = new javax.swing.JToggleButton();
        seperatorLabel5 = new javax.swing.JLabel();
        symLeisteListeZeigen = new javax.swing.JButton();
        symLeisteStrZuListe = new javax.swing.JButton();
        symLeisteStrVonListe = new javax.swing.JButton();
        seperatorLabel6 = new javax.swing.JLabel();
        symLeisteNotizenMode = new javax.swing.JToggleButton();
        symLeisteKndListenMode = new javax.swing.JToggleButton();
        seperatorLabel7 = new javax.swing.JLabel();
        symLeisteTipp = new javax.swing.JToggleButton();
        panelStatusBar = new java.awt.Panel();
        statusBarHinweis = new java.awt.Label();
        labelLevel = new java.awt.Label();
        labelFehler = new java.awt.Label();
        statusBarFehler = new java.awt.Label();
        statusBarZeit = new java.awt.Label();
        statusBarLevel = new java.awt.Label();
        labelTitel = new javax.swing.JLabel();
        hinweisPanel = new javax.swing.JPanel();
        labelHinweisfeld = new javax.swing.JLabel();
        labelKandidaten = new javax.swing.JLabel();
        kandidat_1 = new javax.swing.JButton();
        kandidat_2 = new javax.swing.JButton();
        kandidat_3 = new javax.swing.JButton();
        kandidat_4 = new javax.swing.JButton();
        kandidat_5 = new javax.swing.JButton();
        kandidat_6 = new javax.swing.JButton();
        kandidat_9 = new javax.swing.JButton();
        kandidat_8 = new javax.swing.JButton();
        kandidat_7 = new javax.swing.JButton();
        labelWerte = new javax.swing.JLabel();
        setzeWert_1 = new javax.swing.JButton();
        setzeWert_2 = new javax.swing.JButton();
        setzeWert_3 = new javax.swing.JButton();
        setzeWert_4 = new javax.swing.JButton();
        setzeWert_5 = new javax.swing.JButton();
        setzeWert_6 = new javax.swing.JButton();
        setzeWert_7 = new javax.swing.JButton();
        setzeWert_8 = new javax.swing.JButton();
        setzeWert_9 = new javax.swing.JButton();
        menuZeile = new javax.swing.JMenuBar();
        menuDatei = new javax.swing.JMenu();
        mdEingabe = new javax.swing.JMenuItem();
        mdiErzeugen = new javax.swing.JMenuItem();
        mdSerie = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mdDateiLaden = new javax.swing.JMenuItem();
        mdSpeichern = new javax.swing.JMenuItem();
        mdSpeichernAls = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mdKopierenIntFormat = new javax.swing.JMenuItem();
        mdKopierenExtFormat = new javax.swing.JMenuItem();
        mdiEinfuegen = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        mdBildSpeichern_mR = new javax.swing.JMenuItem();
        mdBildSpeichern_oR = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        mdMaxFeld2PNG = new javax.swing.JMenuItem();
        mdMinFeld2PNG = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mdStrInListe = new javax.swing.JMenuItem();
        mdStrAusListe = new javax.swing.JMenuItem();
        mdListenImport = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        mdiBeendenOhneSicherung = new javax.swing.JMenuItem();
        mdBeendenMitSicherung = new javax.swing.JMenuItem();
        menuBearbeiten = new javax.swing.JMenu();
        mbEditieren = new javax.swing.JMenuItem();
        mbEditierenMitLW = new javax.swing.JMenuItem();
        mbLoesungBearbeiten = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        mbTestMode = new javax.swing.JCheckBoxMenuItem();
        menuAnzeige = new javax.swing.JMenu();
        maStart = new javax.swing.JMenuItem();
        maAktuell = new javax.swing.JMenuItem();
        maLoesung = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        maKndListe = new javax.swing.JMenuItem();
        maNotizen = new javax.swing.JMenuItem();
        maKndAnzeige = new javax.swing.JCheckBoxMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        maListeZeigen = new javax.swing.JMenuItem();
        menuLevel = new javax.swing.JMenu();
        ml_0 = new javax.swing.JRadioButtonMenuItem();
        ml_1 = new javax.swing.JRadioButtonMenuItem();
        ml_2 = new javax.swing.JRadioButtonMenuItem();
        ml_3 = new javax.swing.JRadioButtonMenuItem();
        ml_4 = new javax.swing.JRadioButtonMenuItem();
        ml_5 = new javax.swing.JRadioButtonMenuItem();
        menuNavi = new javax.swing.JMenu();
        mnStart = new javax.swing.JMenuItem();
        mnRueck = new javax.swing.JMenuItem();
        mnVor = new javax.swing.JMenuItem();
        mniAktuell = new javax.swing.JMenuItem();
        mnLoesung = new javax.swing.JMenuItem();
        mnPosSpeichern = new javax.swing.JMenuItem();
        mnSpeicherPos = new javax.swing.JMenuItem();
        menuFarbMarkierung = new javax.swing.JMenu();
        mmBlau = new javax.swing.JMenuItem();
        mmGruen = new javax.swing.JMenuItem();
        mmOker = new javax.swing.JMenuItem();
        mmRosa = new javax.swing.JMenuItem();
        mmViolett = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        mmEntfernen = new javax.swing.JMenuItem();
        mmEntferneAlle = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JPopupMenu.Separator();
        menuInfo = new javax.swing.JMenu();
        mhInfoProgramm = new javax.swing.JMenuItem();
        mhInfoKurztasten = new javax.swing.JMenuItem();
        mhInfoMaustasten = new javax.swing.JMenuItem();
        mhEingabeHinweise = new javax.swing.JCheckBoxMenuItem();
        jSeparator15 = new javax.swing.JPopupMenu.Separator();
        mhlnfoStrategien = new javax.swing.JMenuItem();
        LoesungsTipp = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        mhInfoUpdate = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        mhInfoUeber = new javax.swing.JMenuItem();

        jMenuItem1.setText("jMenuItem1");

        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText("jCheckBoxMenuItem1");

        jMenuItem2.setText("jMenuItem2");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setForeground(new java.awt.Color(0, 0, 0));
        setIconImage(getToolkit().getImage(Stradoku.class.getResource("/stradoku/img/stradoku.png")));
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        setName("hauptframe"); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        stradokuFeld.setForeground(new java.awt.Color(240, 240, 240));
        stradokuFeld.setPreferredSize(new java.awt.Dimension(467, 467));

        javax.swing.GroupLayout stradokuFeldLayout = new javax.swing.GroupLayout(stradokuFeld);
        stradokuFeld.setLayout(stradokuFeldLayout);
        stradokuFeldLayout.setHorizontalGroup(
            stradokuFeldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 467, Short.MAX_VALUE)
        );
        stradokuFeldLayout.setVerticalGroup(
            stradokuFeldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 467, Short.MAX_VALUE)
        );

        toolBar.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(236, 232, 232), new java.awt.Color(198, 193, 193)));
        toolBar.setEnabled(false);
        toolBar.setMaximumSize(new java.awt.Dimension(836, 36));
        toolBar.setMinimumSize(new java.awt.Dimension(278, 36));
        toolBar.setPreferredSize(new java.awt.Dimension(835, 36));

        seperatorLabel8.setMaximumSize(new java.awt.Dimension(9, 32));
        seperatorLabel8.setMinimumSize(new java.awt.Dimension(9, 32));
        seperatorLabel8.setPreferredSize(new java.awt.Dimension(9, 32));
        toolBar.add(seperatorLabel8);

        symLeisteStrLaden.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/ordner.png"))); // NOI18N
        symLeisteStrLaden.setToolTipText("Stradoku-Datei öffnen");
        symLeisteStrLaden.setFocusable(false);
        symLeisteStrLaden.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        symLeisteStrLaden.setMaximumSize(new java.awt.Dimension(32, 32));
        symLeisteStrLaden.setMinimumSize(new java.awt.Dimension(32, 32));
        symLeisteStrLaden.setPreferredSize(new java.awt.Dimension(32, 32));
        symLeisteStrLaden.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        symLeisteStrLaden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                symLeisteStrLadenActionPerformed(evt);
            }
        });
        toolBar.add(symLeisteStrLaden);

        symLeisteStrSpeichern.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/speichern.png"))); // NOI18N
        symLeisteStrSpeichern.setToolTipText("aktuelles Stradoku speichern");
        symLeisteStrSpeichern.setFocusable(false);
        symLeisteStrSpeichern.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        symLeisteStrSpeichern.setMaximumSize(new java.awt.Dimension(32, 32));
        symLeisteStrSpeichern.setMinimumSize(new java.awt.Dimension(32, 32));
        symLeisteStrSpeichern.setPreferredSize(new java.awt.Dimension(32, 32));
        symLeisteStrSpeichern.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        symLeisteStrSpeichern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speichernStradokuActionPerformed(evt);
            }
        });
        toolBar.add(symLeisteStrSpeichern);

        seperatorLabel1.setMaximumSize(new java.awt.Dimension(20, 32));
        seperatorLabel1.setMinimumSize(new java.awt.Dimension(20, 32));
        seperatorLabel1.setPreferredSize(new java.awt.Dimension(20, 32));
        toolBar.add(seperatorLabel1);

        naviTasteStart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/start.png"))); // NOI18N
        naviTasteStart.setToolTipText("zurück zur Ausgangsstellung - Strg+Pfeil Auf");
        naviTasteStart.setFocusable(false);
        naviTasteStart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        naviTasteStart.setMaximumSize(new java.awt.Dimension(20, 32));
        naviTasteStart.setMinimumSize(new java.awt.Dimension(20, 32));
        naviTasteStart.setPreferredSize(new java.awt.Dimension(20, 32));
        naviTasteStart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        naviTasteStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeigeStartActionPerformed(evt);
            }
        });
        toolBar.add(naviTasteStart);

        naviTasteZurueck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/links.png"))); // NOI18N
        naviTasteZurueck.setToolTipText("einen Lösungsschritt zurück - Strg+Pfeil Links");
        naviTasteZurueck.setFocusable(false);
        naviTasteZurueck.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        naviTasteZurueck.setMaximumSize(new java.awt.Dimension(16, 32));
        naviTasteZurueck.setMinimumSize(new java.awt.Dimension(16, 32));
        naviTasteZurueck.setPreferredSize(new java.awt.Dimension(16, 32));
        naviTasteZurueck.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        naviTasteZurueck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeigeZurueckActionPerformed(evt);
            }
        });
        toolBar.add(naviTasteZurueck);

        naviPos.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N
        naviPos.setText("0");
        naviPos.setToolTipText("<html><b>Index-Anzeige der aktuellen Lösungsposition</b><ul>\n<li>Klick mit linker Maustaste+<b>Strg</b>: Lösungsposition speichern\n<li>Klick mit linker Maustaste+<b>Umschalt</b>: Lösungsposition zeigen\n<li>Klick mit linker Maustaste: Zu gespeicherter Position gehen</ul></html>\n");
        naviPos.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        naviPos.setFocusable(false);
        naviPos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        naviPos.setMaximumSize(new java.awt.Dimension(40, 21));
        naviPos.setMinimumSize(new java.awt.Dimension(40, 21));
        naviPos.setPreferredSize(new java.awt.Dimension(40, 21));
        naviPos.setVerifyInputWhenFocusTarget(false);
        naviPos.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        naviPos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                makeSavePosition(evt);
            }
        });
        naviPos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                naviPosActionPerformed(evt);
            }
        });
        toolBar.add(naviPos);

        naviTasteVor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/rechts.png"))); // NOI18N
        naviTasteVor.setToolTipText("zurückgenommenen Lösungsschritt wieder ausführen - Strg+Pfeil Rechts");
        naviTasteVor.setFocusable(false);
        naviTasteVor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        naviTasteVor.setMaximumSize(new java.awt.Dimension(20, 32));
        naviTasteVor.setMinimumSize(new java.awt.Dimension(18, 32));
        naviTasteVor.setPreferredSize(new java.awt.Dimension(18, 32));
        naviTasteVor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        naviTasteVor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeigeVorActionPerformed(evt);
            }
        });
        toolBar.add(naviTasteVor);

        naviTasteAktPos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/aktuell.png"))); // NOI18N
        naviTasteAktPos.setToolTipText("zur aktuellen Lösungsposition gehen - Strg+Pfeil Ab");
        naviTasteAktPos.setFocusable(false);
        naviTasteAktPos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        naviTasteAktPos.setMaximumSize(new java.awt.Dimension(26, 32));
        naviTasteAktPos.setMinimumSize(new java.awt.Dimension(24, 32));
        naviTasteAktPos.setPreferredSize(new java.awt.Dimension(25, 32));
        naviTasteAktPos.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        naviTasteAktPos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeigeAktPosActionPerformed(evt);
            }
        });
        toolBar.add(naviTasteAktPos);

        naviTasteLoesung.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/ende.png"))); // NOI18N
        naviTasteLoesung.setToolTipText("Lösung zeigen - Alt+Ende");
        naviTasteLoesung.setFocusable(false);
        naviTasteLoesung.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        naviTasteLoesung.setMaximumSize(new java.awt.Dimension(20, 32));
        naviTasteLoesung.setMinimumSize(new java.awt.Dimension(20, 32));
        naviTasteLoesung.setPreferredSize(new java.awt.Dimension(20, 32));
        naviTasteLoesung.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        naviTasteLoesung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeigeGeloestActionPerformed(evt);
            }
        });
        toolBar.add(naviTasteLoesung);

        seperatorLabel2.setMaximumSize(new java.awt.Dimension(20, 32));
        seperatorLabel2.setMinimumSize(new java.awt.Dimension(20, 32));
        seperatorLabel2.setPreferredSize(new java.awt.Dimension(20, 32));
        seperatorLabel2.setRequestFocusEnabled(false);
        toolBar.add(seperatorLabel2);

        levelBox.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        levelBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Level 0 - Ungültig", "Level 1 - Leicht", "Level 2 - Mittel", "Level 3 - Schwer", "Level 4 - Teuflisch", "Level 5 - Extrem" }));
        levelBox.setToolTipText("Level-Auswahl");
        levelBox.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        levelBox.setFocusable(false);
        levelBox.setMaximumSize(new java.awt.Dimension(145, 24));
        levelBox.setMinimumSize(new java.awt.Dimension(120, 24));
        levelBox.setPreferredSize(new java.awt.Dimension(135, 24));
        levelBox.setVerifyInputWhenFocusTarget(false);
        levelBox.setRenderer(renderer);
        levelBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                levelBoxActionPerformed(evt);
            }
        });
        toolBar.add(levelBox);

        seperatorLabel3.setMaximumSize(new java.awt.Dimension(20, 32));
        seperatorLabel3.setMinimumSize(new java.awt.Dimension(20, 32));
        seperatorLabel3.setPreferredSize(new java.awt.Dimension(20, 32));
        toolBar.add(seperatorLabel3);

        symLeisteStrErzeugen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/neu.png"))); // NOI18N
        symLeisteStrErzeugen.setToolTipText("Neues Stradoku mit eingestelltem Level generieren - (Strg+N)");
        symLeisteStrErzeugen.setFocusable(false);
        symLeisteStrErzeugen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        symLeisteStrErzeugen.setMaximumSize(new java.awt.Dimension(32, 32));
        symLeisteStrErzeugen.setMinimumSize(new java.awt.Dimension(32, 32));
        symLeisteStrErzeugen.setPreferredSize(new java.awt.Dimension(32, 32));
        symLeisteStrErzeugen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        symLeisteStrErzeugen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                erzeugeStradokuActionPerformed(evt);
            }
        });
        toolBar.add(symLeisteStrErzeugen);

        symLeisteSerieErzeugen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/serie.png"))); // NOI18N
        symLeisteSerieErzeugen.setToolTipText("Stradoku-Serie mit eingestelltem Level generieren");
        symLeisteSerieErzeugen.setFocusable(false);
        symLeisteSerieErzeugen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        symLeisteSerieErzeugen.setMaximumSize(new java.awt.Dimension(32, 32));
        symLeisteSerieErzeugen.setMinimumSize(new java.awt.Dimension(32, 32));
        symLeisteSerieErzeugen.setPreferredSize(new java.awt.Dimension(32, 32));
        symLeisteSerieErzeugen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        symLeisteSerieErzeugen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                symLeisteSerieErzeugenActionPerformed(evt);
            }
        });
        toolBar.add(symLeisteSerieErzeugen);

        seperatorLabel4.setMaximumSize(new java.awt.Dimension(20, 32));
        seperatorLabel4.setMinimumSize(new java.awt.Dimension(20, 32));
        seperatorLabel4.setPreferredSize(new java.awt.Dimension(20, 32));
        toolBar.add(seperatorLabel4);

        symLeisteStrEditieren.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/aendern.png"))); // NOI18N
        symLeisteStrEditieren.setToolTipText("Aktuelles Stradoku verändern");
        symLeisteStrEditieren.setFocusable(false);
        symLeisteStrEditieren.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        symLeisteStrEditieren.setMaximumSize(new java.awt.Dimension(32, 32));
        symLeisteStrEditieren.setMinimumSize(new java.awt.Dimension(32, 32));
        symLeisteStrEditieren.setOpaque(true);
        symLeisteStrEditieren.setPreferredSize(new java.awt.Dimension(32, 32));
        symLeisteStrEditieren.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        symLeisteStrEditieren.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                symLeisteStrEditierenActionPerformed(evt);
            }
        });
        toolBar.add(symLeisteStrEditieren);

        symLeisteStrEingabe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/eingeben.png"))); // NOI18N
        symLeisteStrEingabe.setToolTipText("Neues Stradoku eingeben");
        symLeisteStrEingabe.setFocusable(false);
        symLeisteStrEingabe.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        symLeisteStrEingabe.setMaximumSize(new java.awt.Dimension(32, 32));
        symLeisteStrEingabe.setMinimumSize(new java.awt.Dimension(32, 32));
        symLeisteStrEingabe.setOpaque(true);
        symLeisteStrEingabe.setPreferredSize(new java.awt.Dimension(32, 32));
        symLeisteStrEingabe.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        symLeisteStrEingabe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                symLeisteStrEingabeActionPerformed(evt);
            }
        });
        toolBar.add(symLeisteStrEingabe);

        seperatorLabel5.setMaximumSize(new java.awt.Dimension(20, 32));
        seperatorLabel5.setMinimumSize(new java.awt.Dimension(20, 32));
        seperatorLabel5.setPreferredSize(new java.awt.Dimension(20, 32));
        toolBar.add(seperatorLabel5);

        symLeisteListeZeigen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/liste5.png"))); // NOI18N
        symLeisteListeZeigen.setToolTipText("Stradoku-Liste zeigen - Strg+L");
        symLeisteListeZeigen.setFocusable(false);
        symLeisteListeZeigen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        symLeisteListeZeigen.setMaximumSize(new java.awt.Dimension(32, 32));
        symLeisteListeZeigen.setMinimumSize(new java.awt.Dimension(32, 32));
        symLeisteListeZeigen.setPreferredSize(new java.awt.Dimension(32, 32));
        symLeisteListeZeigen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        symLeisteListeZeigen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listeZeigen(evt);
            }
        });
        toolBar.add(symLeisteListeZeigen);

        symLeisteStrZuListe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/liste7.png"))); // NOI18N
        symLeisteStrZuListe.setToolTipText("Aktuelles Stradoku in Liste aufnehmen - Strg+W");
        symLeisteStrZuListe.setFocusable(false);
        symLeisteStrZuListe.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        symLeisteStrZuListe.setMaximumSize(new java.awt.Dimension(32, 32));
        symLeisteStrZuListe.setMinimumSize(new java.awt.Dimension(32, 32));
        symLeisteStrZuListe.setPreferredSize(new java.awt.Dimension(32, 32));
        symLeisteStrZuListe.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        symLeisteStrZuListe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aufgabeInListeSpeichern(evt);
            }
        });
        toolBar.add(symLeisteStrZuListe);

        symLeisteStrVonListe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/liste6.png"))); // NOI18N
        symLeisteStrVonListe.setToolTipText("Stradoku aus Liste übernehmen - Strg+H");
        symLeisteStrVonListe.setFocusable(false);
        symLeisteStrVonListe.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        symLeisteStrVonListe.setMaximumSize(new java.awt.Dimension(32, 32));
        symLeisteStrVonListe.setMinimumSize(new java.awt.Dimension(32, 32));
        symLeisteStrVonListe.setPreferredSize(new java.awt.Dimension(32, 32));
        symLeisteStrVonListe.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        symLeisteStrVonListe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aufgabeAusListeLaden(evt);
            }
        });
        toolBar.add(symLeisteStrVonListe);

        seperatorLabel6.setMaximumSize(new java.awt.Dimension(20, 32));
        seperatorLabel6.setMinimumSize(new java.awt.Dimension(20, 32));
        seperatorLabel6.setPreferredSize(new java.awt.Dimension(20, 32));
        toolBar.add(seperatorLabel6);

        symLeisteNotizenMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/kl_aktiv.png"))); // NOI18N
        symLeisteNotizenMode.setToolTipText("Notizenmodus aktivieren");
        symLeisteNotizenMode.setFocusPainted(false);
        symLeisteNotizenMode.setFocusable(false);
        symLeisteNotizenMode.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        symLeisteNotizenMode.setMaximumSize(new java.awt.Dimension(32, 32));
        symLeisteNotizenMode.setMinimumSize(new java.awt.Dimension(32, 32));
        symLeisteNotizenMode.setOpaque(true);
        symLeisteNotizenMode.setPreferredSize(new java.awt.Dimension(32, 32));
        symLeisteNotizenMode.setRequestFocusEnabled(false);
        symLeisteNotizenMode.setVerifyInputWhenFocusTarget(false);
        symLeisteNotizenMode.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        symLeisteNotizenMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                symLeisteNotizenModeActionPerformed(evt);
            }
        });
        toolBar.add(symLeisteNotizenMode);

        symLeisteKndListenMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/knd_red_akt.png"))); // NOI18N
        symLeisteKndListenMode.setToolTipText("Kandidatenliste führen");
        symLeisteKndListenMode.setFocusPainted(false);
        symLeisteKndListenMode.setFocusable(false);
        symLeisteKndListenMode.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        symLeisteKndListenMode.setMaximumSize(new java.awt.Dimension(32, 32));
        symLeisteKndListenMode.setMinimumSize(new java.awt.Dimension(32, 32));
        symLeisteKndListenMode.setOpaque(true);
        symLeisteKndListenMode.setPreferredSize(new java.awt.Dimension(32, 32));
        symLeisteKndListenMode.setRequestFocusEnabled(false);
        symLeisteKndListenMode.setVerifyInputWhenFocusTarget(false);
        symLeisteKndListenMode.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        symLeisteKndListenMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                symLeisteKndListenModeActionPerformed(evt);
            }
        });
        toolBar.add(symLeisteKndListenMode);

        seperatorLabel7.setMaximumSize(new java.awt.Dimension(20, 32));
        seperatorLabel7.setMinimumSize(new java.awt.Dimension(20, 32));
        seperatorLabel7.setPreferredSize(new java.awt.Dimension(20, 32));
        toolBar.add(seperatorLabel7);

        symLeisteTipp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/tipp.png"))); // NOI18N
        symLeisteTipp.setToolTipText("Lösungstipp geben");
        symLeisteTipp.setFocusPainted(false);
        symLeisteTipp.setFocusable(false);
        symLeisteTipp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        symLeisteTipp.setMaximumSize(new java.awt.Dimension(32, 32));
        symLeisteTipp.setMinimumSize(new java.awt.Dimension(32, 32));
        symLeisteTipp.setOpaque(true);
        symLeisteTipp.setPreferredSize(new java.awt.Dimension(32, 32));
        symLeisteTipp.setRequestFocusEnabled(false);
        symLeisteTipp.setVerifyInputWhenFocusTarget(false);
        symLeisteTipp.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        symLeisteTipp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loesungsTippActionPerformed(evt);
            }
        });
        toolBar.add(symLeisteTipp);

        panelStatusBar.setBackground(new java.awt.Color(224, 226, 235));
        panelStatusBar.setName(""); // NOI18N

        statusBarHinweis.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        statusBarHinweis.setMaximumSize(new java.awt.Dimension(480, 24));
        statusBarHinweis.setMinimumSize(new java.awt.Dimension(480, 24));
        statusBarHinweis.setName(""); // NOI18N

        labelLevel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        labelLevel.setMaximumSize(new java.awt.Dimension(32767, 24));
        labelLevel.setMinimumSize(new java.awt.Dimension(34, 24));
        labelLevel.setText("Level:");

        labelFehler.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        labelFehler.setMaximumSize(new java.awt.Dimension(32767, 24));
        labelFehler.setMinimumSize(new java.awt.Dimension(85, 24));
        labelFehler.setName(""); // NOI18N
        labelFehler.setPreferredSize(new java.awt.Dimension(87, 24));
        labelFehler.setText("Eingabefehler:");

        statusBarFehler.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        statusBarFehler.setMaximumSize(new java.awt.Dimension(32767, 24));
        statusBarFehler.setMinimumSize(new java.awt.Dimension(16, 24));
        statusBarFehler.setPreferredSize(new java.awt.Dimension(16, 20));
        statusBarFehler.setText("0");
        statusBarFehler.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                statusBarFehlerMouseClicked(evt);
            }
        });

        statusBarZeit.setAlignment(java.awt.Label.CENTER);
        statusBarZeit.setBackground(new java.awt.Color(51, 0, 0));
        statusBarZeit.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        statusBarZeit.setForeground(new java.awt.Color(51, 204, 0));
        statusBarZeit.setMaximumSize(new java.awt.Dimension(40, 21));
        statusBarZeit.setMinimumSize(new java.awt.Dimension(40, 21));
        statusBarZeit.setText("00:00");
        statusBarZeit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                statusBarZeitMouseClicked(evt);
            }
        });

        statusBarLevel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        statusBarLevel.setMaximumSize(new java.awt.Dimension(32767, 24));
        statusBarLevel.setMinimumSize(new java.awt.Dimension(10, 24));
        statusBarLevel.setPreferredSize(new java.awt.Dimension(16, 24));
        statusBarLevel.setText("0");

        javax.swing.GroupLayout panelStatusBarLayout = new javax.swing.GroupLayout(panelStatusBar);
        panelStatusBar.setLayout(panelStatusBarLayout);
        panelStatusBarLayout.setHorizontalGroup(
            panelStatusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelStatusBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusBarHinweis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusBarLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelFehler, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusBarFehler, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(statusBarZeit, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelStatusBarLayout.setVerticalGroup(
            panelStatusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusBarZeit, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
            .addGroup(panelStatusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(statusBarHinweis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(statusBarFehler, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(labelLevel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(statusBarLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(labelFehler, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        panelStatusBarLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {statusBarFehler, statusBarLevel});

        statusBarZeit.getAccessibleContext().setAccessibleDescription("");

        labelTitel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/Titel.png"))); // NOI18N

        hinweisPanel.setBackground(new java.awt.Color(102, 102, 102));
        hinweisPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        hinweisPanel.setMaximumSize(new java.awt.Dimension(286, 286));
        hinweisPanel.setMinimumSize(new java.awt.Dimension(286, 286));
        hinweisPanel.setPreferredSize(new java.awt.Dimension(286, 266));
        hinweisPanel.setRequestFocusEnabled(false);
        hinweisPanel.setVerifyInputWhenFocusTarget(false);

        labelHinweisfeld.setBackground(new java.awt.Color(102, 102, 102));
        labelHinweisfeld.setFont(new java.awt.Font("Serif", 0, 13)); // NOI18N
        labelHinweisfeld.setForeground(new java.awt.Color(255, 255, 255));
        labelHinweisfeld.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelHinweisfeld.setToolTipText("");
        labelHinweisfeld.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelHinweisfeld.setAlignmentX(0.5F);
        labelHinweisfeld.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        labelHinweisfeld.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                hinweisFeldMausKlick(evt);
            }
        });

        javax.swing.GroupLayout hinweisPanelLayout = new javax.swing.GroupLayout(hinweisPanel);
        hinweisPanel.setLayout(hinweisPanelLayout);
        hinweisPanelLayout.setHorizontalGroup(
            hinweisPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hinweisPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelHinweisfeld, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );
        hinweisPanelLayout.setVerticalGroup(
            hinweisPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hinweisPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelHinweisfeld, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        labelKandidaten.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        labelKandidaten.setForeground(new java.awt.Color(255, 255, 255));
        labelKandidaten.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelKandidaten.setText("Kandidaten");

        kandidat_1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        kandidat_1.setForeground(new java.awt.Color(0, 0, 204));
        kandidat_1.setText("1");
        kandidat_1.setToolTipText("");
        kandidat_1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        kandidat_1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        kandidat_1.setMaximumSize(new java.awt.Dimension(25, 25));
        kandidat_1.setOpaque(true);
        kandidat_1.setPreferredSize(new java.awt.Dimension(25, 25));
        kandidat_1.setSize(new java.awt.Dimension(30, 30));
        kandidat_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kandidat_ActionPerformed(evt);
            }
        });

        kandidat_2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        kandidat_2.setForeground(new java.awt.Color(0, 0, 204));
        kandidat_2.setText("2");
        kandidat_2.setToolTipText("");
        kandidat_2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        kandidat_2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        kandidat_2.setMaximumSize(new java.awt.Dimension(25, 25));
        kandidat_2.setMinimumSize(new java.awt.Dimension(20, 20));
        kandidat_2.setOpaque(true);
        kandidat_2.setPreferredSize(new java.awt.Dimension(25, 25));
        kandidat_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kandidat_ActionPerformed(evt);
            }
        });

        kandidat_3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        kandidat_3.setForeground(new java.awt.Color(0, 0, 204));
        kandidat_3.setText("3");
        kandidat_3.setToolTipText("");
        kandidat_3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        kandidat_3.setMargin(new java.awt.Insets(0, 0, 0, 0));
        kandidat_3.setMaximumSize(new java.awt.Dimension(25, 25));
        kandidat_3.setMinimumSize(new java.awt.Dimension(20, 20));
        kandidat_3.setOpaque(true);
        kandidat_3.setPreferredSize(new java.awt.Dimension(25, 25));
        kandidat_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kandidat_ActionPerformed(evt);
            }
        });

        kandidat_4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        kandidat_4.setForeground(new java.awt.Color(0, 0, 204));
        kandidat_4.setText("4");
        kandidat_4.setToolTipText("");
        kandidat_4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        kandidat_4.setMargin(new java.awt.Insets(0, 0, 0, 0));
        kandidat_4.setMaximumSize(new java.awt.Dimension(25, 25));
        kandidat_4.setMinimumSize(new java.awt.Dimension(20, 20));
        kandidat_4.setOpaque(true);
        kandidat_4.setPreferredSize(new java.awt.Dimension(25, 25));
        kandidat_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kandidat_ActionPerformed(evt);
            }
        });

        kandidat_5.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        kandidat_5.setForeground(new java.awt.Color(0, 0, 204));
        kandidat_5.setText("5");
        kandidat_5.setToolTipText("");
        kandidat_5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        kandidat_5.setMargin(new java.awt.Insets(0, 0, 0, 0));
        kandidat_5.setMaximumSize(new java.awt.Dimension(25, 25));
        kandidat_5.setMinimumSize(new java.awt.Dimension(20, 20));
        kandidat_5.setOpaque(true);
        kandidat_5.setPreferredSize(new java.awt.Dimension(25, 25));
        kandidat_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kandidat_ActionPerformed(evt);
            }
        });

        kandidat_6.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        kandidat_6.setForeground(new java.awt.Color(0, 0, 204));
        kandidat_6.setText("6");
        kandidat_6.setToolTipText("");
        kandidat_6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        kandidat_6.setMargin(new java.awt.Insets(0, 0, 0, 0));
        kandidat_6.setMaximumSize(new java.awt.Dimension(25, 25));
        kandidat_6.setMinimumSize(new java.awt.Dimension(20, 20));
        kandidat_6.setOpaque(true);
        kandidat_6.setPreferredSize(new java.awt.Dimension(25, 25));
        kandidat_6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kandidat_ActionPerformed(evt);
            }
        });

        kandidat_9.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        kandidat_9.setForeground(new java.awt.Color(0, 0, 204));
        kandidat_9.setText("9");
        kandidat_9.setToolTipText("");
        kandidat_9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        kandidat_9.setMargin(new java.awt.Insets(0, 0, 0, 0));
        kandidat_9.setMaximumSize(new java.awt.Dimension(25, 25));
        kandidat_9.setMinimumSize(new java.awt.Dimension(20, 20));
        kandidat_9.setOpaque(true);
        kandidat_9.setPreferredSize(new java.awt.Dimension(25, 25));
        kandidat_9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kandidat_ActionPerformed(evt);
            }
        });

        kandidat_8.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        kandidat_8.setForeground(new java.awt.Color(0, 0, 204));
        kandidat_8.setText("8");
        kandidat_8.setToolTipText("");
        kandidat_8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        kandidat_8.setMargin(new java.awt.Insets(0, 0, 0, 0));
        kandidat_8.setMaximumSize(new java.awt.Dimension(25, 25));
        kandidat_8.setMinimumSize(new java.awt.Dimension(20, 20));
        kandidat_8.setOpaque(true);
        kandidat_8.setPreferredSize(new java.awt.Dimension(25, 25));
        kandidat_8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kandidat_ActionPerformed(evt);
            }
        });

        kandidat_7.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        kandidat_7.setForeground(new java.awt.Color(0, 0, 204));
        kandidat_7.setText("7");
        kandidat_7.setToolTipText("");
        kandidat_7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        kandidat_7.setMargin(new java.awt.Insets(0, 0, 0, 0));
        kandidat_7.setMaximumSize(new java.awt.Dimension(25, 25));
        kandidat_7.setMinimumSize(new java.awt.Dimension(20, 20));
        kandidat_7.setOpaque(true);
        kandidat_7.setPreferredSize(new java.awt.Dimension(25, 25));
        kandidat_7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kandidat_ActionPerformed(evt);
            }
        });

        labelWerte.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        labelWerte.setForeground(new java.awt.Color(255, 255, 255));
        labelWerte.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelWerte.setText("Werte");

        setzeWert_1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        setzeWert_1.setText("1");
        setzeWert_1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setzeWert_1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        setzeWert_1.setMaximumSize(new java.awt.Dimension(25, 25));
        setzeWert_1.setMinimumSize(new java.awt.Dimension(20, 20));
        setzeWert_1.setOpaque(true);
        setzeWert_1.setPreferredSize(new java.awt.Dimension(25, 25));
        setzeWert_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setzeWert_ActionPerformed(evt);
            }
        });

        setzeWert_2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        setzeWert_2.setText("2");
        setzeWert_2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setzeWert_2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        setzeWert_2.setMaximumSize(new java.awt.Dimension(25, 25));
        setzeWert_2.setMinimumSize(new java.awt.Dimension(20, 20));
        setzeWert_2.setOpaque(true);
        setzeWert_2.setPreferredSize(new java.awt.Dimension(25, 25));
        setzeWert_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setzeWert_ActionPerformed(evt);
            }
        });

        setzeWert_3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        setzeWert_3.setText("3");
        setzeWert_3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setzeWert_3.setMargin(new java.awt.Insets(0, 0, 0, 0));
        setzeWert_3.setMaximumSize(new java.awt.Dimension(25, 25));
        setzeWert_3.setMinimumSize(new java.awt.Dimension(20, 20));
        setzeWert_3.setOpaque(true);
        setzeWert_3.setPreferredSize(new java.awt.Dimension(25, 25));
        setzeWert_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setzeWert_ActionPerformed(evt);
            }
        });

        setzeWert_4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        setzeWert_4.setText("4");
        setzeWert_4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setzeWert_4.setMargin(new java.awt.Insets(0, 0, 0, 0));
        setzeWert_4.setMaximumSize(new java.awt.Dimension(25, 25));
        setzeWert_4.setMinimumSize(new java.awt.Dimension(20, 20));
        setzeWert_4.setOpaque(true);
        setzeWert_4.setPreferredSize(new java.awt.Dimension(25, 25));
        setzeWert_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setzeWert_ActionPerformed(evt);
            }
        });

        setzeWert_5.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        setzeWert_5.setText("5");
        setzeWert_5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setzeWert_5.setMargin(new java.awt.Insets(0, 0, 0, 0));
        setzeWert_5.setMaximumSize(new java.awt.Dimension(25, 25));
        setzeWert_5.setMinimumSize(new java.awt.Dimension(20, 20));
        setzeWert_5.setOpaque(true);
        setzeWert_5.setPreferredSize(new java.awt.Dimension(25, 25));
        setzeWert_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setzeWert_ActionPerformed(evt);
            }
        });

        setzeWert_6.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        setzeWert_6.setText("6");
        setzeWert_6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setzeWert_6.setMargin(new java.awt.Insets(0, 0, 0, 0));
        setzeWert_6.setMaximumSize(new java.awt.Dimension(25, 25));
        setzeWert_6.setMinimumSize(new java.awt.Dimension(20, 20));
        setzeWert_6.setOpaque(true);
        setzeWert_6.setPreferredSize(new java.awt.Dimension(25, 25));
        setzeWert_6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setzeWert_ActionPerformed(evt);
            }
        });

        setzeWert_7.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        setzeWert_7.setText("7");
        setzeWert_7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setzeWert_7.setMargin(new java.awt.Insets(0, 0, 0, 0));
        setzeWert_7.setMaximumSize(new java.awt.Dimension(25, 25));
        setzeWert_7.setMinimumSize(new java.awt.Dimension(20, 20));
        setzeWert_7.setOpaque(true);
        setzeWert_7.setPreferredSize(new java.awt.Dimension(25, 25));
        setzeWert_7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setzeWert_ActionPerformed(evt);
            }
        });

        setzeWert_8.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        setzeWert_8.setText("8");
        setzeWert_8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setzeWert_8.setMargin(new java.awt.Insets(0, 0, 0, 0));
        setzeWert_8.setMaximumSize(new java.awt.Dimension(25, 25));
        setzeWert_8.setMinimumSize(new java.awt.Dimension(20, 20));
        setzeWert_8.setOpaque(true);
        setzeWert_8.setPreferredSize(new java.awt.Dimension(25, 25));
        setzeWert_8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setzeWert_ActionPerformed(evt);
            }
        });

        setzeWert_9.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        setzeWert_9.setText("9");
        setzeWert_9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setzeWert_9.setMargin(new java.awt.Insets(0, 0, 0, 0));
        setzeWert_9.setMaximumSize(new java.awt.Dimension(25, 25));
        setzeWert_9.setMinimumSize(new java.awt.Dimension(20, 20));
        setzeWert_9.setOpaque(true);
        setzeWert_9.setPreferredSize(new java.awt.Dimension(25, 25));
        setzeWert_9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setzeWert_ActionPerformed(evt);
            }
        });

        menuZeile.setAlignmentX(5.0F);
        menuZeile.setFont(menuZeile.getFont());
        menuZeile.setMaximumSize(new java.awt.Dimension(243, 30));
        menuZeile.setMinimumSize(new java.awt.Dimension(243, 24));
        menuZeile.setOpaque(false);
        menuZeile.setPreferredSize(new java.awt.Dimension(243, 25));

        menuDatei.setText("Datei");
        menuDatei.setToolTipText("");
        menuDatei.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N

        mdEingabe.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mdEingabe.setFont(mdEingabe.getFont());
        mdEingabe.setText("Neues Stradoku eingeben");
        mdEingabe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mdEingabeActionPerformed(evt);
            }
        });
        menuDatei.add(mdEingabe);

        mdiErzeugen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mdiErzeugen.setFont(mdiErzeugen.getFont());
        mdiErzeugen.setText("Neues Stradoku generieren");
        mdiErzeugen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                erzeugeStradokuActionPerformed(evt);
            }
        });
        menuDatei.add(mdiErzeugen);

        mdSerie.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mdSerie.setText("Stradoku-Serie generieren");
        mdSerie.setToolTipText("");
        mdSerie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mdSerieActionPerformed(evt);
            }
        });
        menuDatei.add(mdSerie);
        menuDatei.add(jSeparator2);

        mdDateiLaden.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mdDateiLaden.setText("Stradoku öffnen");
        mdDateiLaden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mdDateiLadenActionPerformed(evt);
            }
        });
        menuDatei.add(mdDateiLaden);

        mdSpeichern.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mdSpeichern.setText("Stradoku speichern");
        mdSpeichern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speichernStradokuActionPerformed(evt);
            }
        });
        menuDatei.add(mdSpeichern);

        mdSpeichernAls.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mdSpeichernAls.setText("Stradoku speichern unter ...");
        mdSpeichernAls.setActionCommand("Stradoku unter neuem Namen speichern");
        mdSpeichernAls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mdSpeichernAlsActionPerformed(evt);
            }
        });
        menuDatei.add(mdSpeichernAls);
        menuDatei.add(jSeparator3);

        mdKopierenIntFormat.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mdKopierenIntFormat.setText("Stradoku in Zwischenablage kopieren");
        mdKopierenIntFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mdKopierenIntFormatActionPerformed(evt);
            }
        });
        menuDatei.add(mdKopierenIntFormat);

        mdKopierenExtFormat.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mdKopierenExtFormat.setText("Stradoku im externen Format kopieren");
        mdKopierenExtFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mdKopierenExtFormatActionPerformed(evt);
            }
        });
        menuDatei.add(mdKopierenExtFormat);

        mdiEinfuegen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mdiEinfuegen.setText("Stradoku von Zwischenablage einfügen");
        mdiEinfuegen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mdiEinfuegenActionPerformed(evt);
            }
        });
        menuDatei.add(mdiEinfuegen);
        menuDatei.add(jSeparator12);

        mdBildSpeichern_mR.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mdBildSpeichern_mR.setText("Stradoku-Feld mit Rahmen speichern");
        mdBildSpeichern_mR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDateiBildMitSpeichernActionPerformed(evt);
            }
        });
        menuDatei.add(mdBildSpeichern_mR);

        mdBildSpeichern_oR.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mdBildSpeichern_oR.setText("Stradoku-Feld ohne Rahmen speichern");
        mdBildSpeichern_oR.setActionCommand("Stradoku als Bild ohne Rahmen speichern");
        mdBildSpeichern_oR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDateiBildOhneSpeichernActionPerformed(evt);
            }
        });
        menuDatei.add(mdBildSpeichern_oR);
        menuDatei.add(jSeparator11);

        mdMaxFeld2PNG.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mdMaxFeld2PNG.setText("Stradoku-Feld mit Rahmen kopieren");
        mdMaxFeld2PNG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mdMaxFeld2PNGActionPerformed(evt);
            }
        });
        menuDatei.add(mdMaxFeld2PNG);

        mdMinFeld2PNG.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mdMinFeld2PNG.setText("Stradoku-Feld ohne Rahmen kopieren");
        mdMinFeld2PNG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mdMinFeld2PNGActionPerformed(evt);
            }
        });
        menuDatei.add(mdMinFeld2PNG);
        menuDatei.add(jSeparator1);

        mdStrInListe.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mdStrInListe.setText("Stradoku in Liste aufnehmen");
        mdStrInListe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aufgabeInListeSpeichern(evt);
            }
        });
        menuDatei.add(mdStrInListe);

        mdStrAusListe.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mdStrAusListe.setText("Stradoku aus Liste laden");
        mdStrAusListe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aufgabeAusListeLaden(evt);
            }
        });
        menuDatei.add(mdStrAusListe);

        mdListenImport.setText("Stradoku Liste importieren");
        mdListenImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mdListenImportActionPerformed(evt);
            }
        });
        menuDatei.add(mdListenImport);
        menuDatei.add(jSeparator5);

        mdiBeendenOhneSicherung.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mdiBeendenOhneSicherung.setText("Beenden ohne Sicherung");
        mdiBeendenOhneSicherung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mdiBeendenOhneSicherungActionPerformed(evt);
            }
        });
        menuDatei.add(mdiBeendenOhneSicherung);

        mdBeendenMitSicherung.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mdBeendenMitSicherung.setText("Beenden mit Sicherung");
        mdBeendenMitSicherung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mdBeendenMitSicherungActionPerformed(evt);
            }
        });
        menuDatei.add(mdBeendenMitSicherung);

        menuZeile.add(menuDatei);

        menuBearbeiten.setText("Bearbeiten");
        menuBearbeiten.setToolTipText("");
        menuBearbeiten.setAutoscrolls(true);
        menuBearbeiten.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N

        mbEditieren.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mbEditieren.setText("Stradoku bearbeiten");
        mbEditieren.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mbEditierenActionPerformed(evt);
            }
        });
        menuBearbeiten.add(mbEditieren);

        mbEditierenMitLW.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mbEditierenMitLW.setText("Stradoku mit Lösungswerten bearbeiten");
        mbEditierenMitLW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mbEditierenMitLWActionPerformed(evt);
            }
        });
        menuBearbeiten.add(mbEditierenMitLW);

        mbLoesungBearbeiten.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mbLoesungBearbeiten.setText("Lösung bearbeiten");
        mbLoesungBearbeiten.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mbLoesungBearbeitenActionPerformed(evt);
            }
        });
        menuBearbeiten.add(mbLoesungBearbeiten);
        menuBearbeiten.add(jSeparator8);

        mbTestMode.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mbTestMode.setText("Testmodus aktivieren");
        mbTestMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mbTestModeCheck(evt);
            }
        });
        menuBearbeiten.add(mbTestMode);

        menuZeile.add(menuBearbeiten);

        menuAnzeige.setText("Anzeige");
        menuAnzeige.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N

        maStart.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        maStart.setText("Startposition");
        maStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeigeStartActionPerformed(evt);
            }
        });
        menuAnzeige.add(maStart);

        maAktuell.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        maAktuell.setText("Aktuelle Position");
        maAktuell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeigeAktPosActionPerformed(evt);
            }
        });
        menuAnzeige.add(maAktuell);

        maLoesung.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        maLoesung.setText("Lösung zeigen");
        maLoesung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeigeGeloestActionPerformed(evt);
            }
        });
        menuAnzeige.add(maLoesung);
        menuAnzeige.add(jSeparator7);

        maKndListe.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, 0));
        maKndListe.setText("Kandidatenliste führen");
        maKndListe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maKndListeActionPerformed(evt);
            }
        });
        menuAnzeige.add(maKndListe);

        maNotizen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, 0));
        maNotizen.setText("Notizenmodus aktivieren");
        maNotizen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maNotizenActionPerformed(evt);
            }
        });
        menuAnzeige.add(maNotizen);

        maKndAnzeige.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, 0));
        maKndAnzeige.setText("<html>Kandidaten als Punkte zeigen&#160;&#160;&#160;</html>");
        maKndAnzeige.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maKndAnzeigeActionPerformed(evt);
            }
        });
        menuAnzeige.add(maKndAnzeige);
        menuAnzeige.add(jSeparator9);

        maListeZeigen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        maListeZeigen.setText("Stradoku-Liste zeigen");
        maListeZeigen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maListeZeigenActionPerformed(evt);
            }
        });
        menuAnzeige.add(maListeZeigen);

        menuZeile.add(menuAnzeige);

        menuLevel.setText("Level");
        levelGroup.add(menuLevel);
        menuLevel.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N

        ml_0.setText("Level 0 - Ungültig");
        ml_0.setEnabled(false);
        menuLevel.add(ml_0);

        levelGroup.add(ml_1);
        ml_1.setSelected(true);
        ml_1.setText("Level 1 - Leicht");
        ml_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLevelActionPerformed(evt);
            }
        });
        menuLevel.add(ml_1);

        levelGroup.add(ml_2);
        ml_2.setText("Level 2 - Mittel");
        ml_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLevelActionPerformed(evt);
            }
        });
        menuLevel.add(ml_2);

        levelGroup.add(ml_3);
        ml_3.setText("Level 3 - Schwer");
        ml_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLevelActionPerformed(evt);
            }
        });
        menuLevel.add(ml_3);

        levelGroup.add(ml_4);
        ml_4.setText("Level 4 - Teuflisch");
        ml_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLevelActionPerformed(evt);
            }
        });
        menuLevel.add(ml_4);

        levelGroup.add(ml_5);
        ml_5.setText("Level 5 - Extrem");
        ml_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLevelActionPerformed(evt);
            }
        });
        menuLevel.add(ml_5);

        menuZeile.add(menuLevel);

        menuNavi.setText("Navigieren");
        menuNavi.setActionCommand("  Navigieren");
        menuNavi.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N

        mnStart.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnStart.setText("Ausgangsstellung");
        mnStart.setToolTipText("");
        mnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeigeStartActionPerformed(evt);
            }
        });
        menuNavi.add(mnStart);

        mnRueck.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_LEFT, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnRueck.setText("Schritt zurück");
        mnRueck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeigeZurueckActionPerformed(evt);
            }
        });
        menuNavi.add(mnRueck);

        mnVor.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnVor.setText("Schritt vor");
        mnVor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeigeVorActionPerformed(evt);
            }
        });
        menuNavi.add(mnVor);

        mniAktuell.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mniAktuell.setText("Aktuelle Lösungsposition");
        mniAktuell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeigeAktPosActionPerformed(evt);
            }
        });
        menuNavi.add(mniAktuell);

        mnLoesung.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_END, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnLoesung.setText("Lösung zeigen");
        mnLoesung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeigeGeloestActionPerformed(evt);
            }
        });
        menuNavi.add(mnLoesung);

        mnPosSpeichern.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnPosSpeichern.setText("Aktuelle Lösungsposition speichern");
        mnPosSpeichern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnPosSpeichernActionPerformed(evt);
            }
        });
        menuNavi.add(mnPosSpeichern);

        mnSpeicherPos.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnSpeicherPos.setText("<html>Gespeicherte Lösungsposition zeigen&#160;&#160;&#160;</html>");
        mnSpeicherPos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnSpeicherPosActionPerformed(evt);
            }
        });
        menuNavi.add(mnSpeicherPos);

        menuZeile.add(menuNavi);

        menuFarbMarkierung.setText("Markierung");
        menuFarbMarkierung.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N

        mmBlau.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, 0));
        mmBlau.setText("Blau für aktuelle Zelle");
        mmBlau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mmBlauActionPerformed(evt);
            }
        });
        menuFarbMarkierung.add(mmBlau);

        mmGruen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, 0));
        mmGruen.setText("Grün für aktuelle Zelle");
        mmGruen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mmGruenActionPerformed(evt);
            }
        });
        menuFarbMarkierung.add(mmGruen);

        mmOker.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, 0));
        mmOker.setText("Oker für aktuelle Zelle");
        mmOker.setActionCommand("");
        mmOker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mmOkerActionPerformed(evt);
            }
        });
        menuFarbMarkierung.add(mmOker);

        mmRosa.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, 0));
        mmRosa.setText("Rosa für aktuelle Zelle");
        mmRosa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mmRosaActionPerformed(evt);
            }
        });
        menuFarbMarkierung.add(mmRosa);

        mmViolett.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, 0));
        mmViolett.setText("Violett für aktuelle Zelle");
        mmViolett.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mmViolettActionPerformed(evt);
            }
        });
        menuFarbMarkierung.add(mmViolett);
        menuFarbMarkierung.add(jSeparator10);

        mmEntfernen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, 0));
        mmEntfernen.setText("Markierung für Zelle entfernen");
        mmEntfernen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mmEntfernenActionPerformed(evt);
            }
        });
        menuFarbMarkierung.add(mmEntfernen);

        mmEntferneAlle.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, 0));
        mmEntferneAlle.setText("Alle Markierungen entfernen");
        mmEntferneAlle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mmEntferneAlleActionPerformed(evt);
            }
        });
        menuFarbMarkierung.add(mmEntferneAlle);
        menuFarbMarkierung.add(jSeparator14);

        menuZeile.add(menuFarbMarkierung);

        menuInfo.setText("Hilfe");
        menuInfo.setToolTipText("");
        menuInfo.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N

        mhInfoProgramm.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        mhInfoProgramm.setText("<html>Infos zu diesem Programm &#160;</html>");
        mhInfoProgramm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mhInfoProgrammActionPerformed(evt);
            }
        });
        menuInfo.add(mhInfoProgramm);

        mhInfoKurztasten.setText("Verwendung der Kurztasten");
        mhInfoKurztasten.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mhInfoKurztastenActionPerformed(evt);
            }
        });
        menuInfo.add(mhInfoKurztasten);

        mhInfoMaustasten.setText("Verwendung der Maustasten");
        mhInfoMaustasten.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mhInfoMaustastenActionPerformed(evt);
            }
        });
        menuInfo.add(mhInfoMaustasten);

        mhEingabeHinweise.setSelected(true);
        mhEingabeHinweise.setText("Eingabehinweise zeigen");
        mhEingabeHinweise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mhEingabeHinweiseActionPerformed(evt);
            }
        });
        menuInfo.add(mhEingabeHinweise);
        menuInfo.add(jSeparator15);

        mhlnfoStrategien.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        mhlnfoStrategien.setText("Lösungsstrategien zeigen");
        mhlnfoStrategien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mhlnfoStrategienActionPerformed(evt);
            }
        });
        menuInfo.add(mhlnfoStrategien);

        LoesungsTipp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, 0));
        LoesungsTipp.setText("Lösungstipp geben");
        LoesungsTipp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loesungsTippActionPerformed(evt);
            }
        });
        menuInfo.add(LoesungsTipp);
        menuInfo.add(jSeparator6);

        mhInfoUpdate.setText("Prüfen auf Update");
        mhInfoUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mhInfoUpdateActionPerformed(evt);
            }
        });
        menuInfo.add(mhInfoUpdate);
        menuInfo.add(jSeparator4);

        mhInfoUeber.setText("Über KodelasStradoku");
        mhInfoUeber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mhInfoUeberActionPerformed(evt);
            }
        });
        menuInfo.add(mhInfoUeber);

        menuZeile.add(menuInfo);

        setJMenuBar(menuZeile);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(stradokuFeld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(hinweisPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(60, 60, 60)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(kandidat_4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(kandidat_7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(kandidat_1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addComponent(kandidat_5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(kandidat_6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addComponent(kandidat_2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(kandidat_3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(kandidat_8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(kandidat_9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addComponent(labelKandidaten, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(44, 44, 44)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(setzeWert_1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(setzeWert_2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(setzeWert_3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(setzeWert_4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(setzeWert_5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(setzeWert_6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(setzeWert_7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(setzeWert_8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(setzeWert_9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(labelWerte, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(108, 108, 108)
                                .addComponent(labelTitel)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addComponent(panelStatusBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {kandidat_1, kandidat_2, kandidat_3, kandidat_4, kandidat_5, kandidat_6, kandidat_7, kandidat_8, kandidat_9, setzeWert_1, setzeWert_2, setzeWert_3, setzeWert_4, setzeWert_5, setzeWert_6, setzeWert_7, setzeWert_8, setzeWert_9});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(stradokuFeld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(labelTitel)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(labelWerte)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(setzeWert_1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(setzeWert_2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(setzeWert_3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(setzeWert_4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(setzeWert_5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(setzeWert_6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(setzeWert_7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(setzeWert_8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(setzeWert_9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(labelKandidaten)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(kandidat_1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(kandidat_4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(kandidat_5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(kandidat_6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(kandidat_7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(kandidat_9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(kandidat_8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(kandidat_2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(kandidat_3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(18, 18, 18)
                        .addComponent(hinweisPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addComponent(panelStatusBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {kandidat_1, kandidat_2, kandidat_3, kandidat_4, kandidat_5, kandidat_6, kandidat_7, kandidat_8, kandidat_9, setzeWert_1, setzeWert_2, setzeWert_3, setzeWert_4, setzeWert_5, setzeWert_6, setzeWert_7, setzeWert_8, setzeWert_9});

        pack();
    }// </editor-fold>//GEN-END:initComponents

   /**
    * Reagiert auf angeklickten Schließen-Button und leitet die Beendigung der Anwendung ein.
    *
    * @param evt WindowsEreignis für Button 'Schließen'
    */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
       if (beendeAnwendung()) {
          System.exit(0);
       }
    }//GEN-LAST:event_formWindowClosing

   /**
    * Leitet eine Eingabe über den Werteblock für das Setzen oder Entfernen eines Wertes im
    * Stradokufeld ein.
    *
    * @param evt Aktionsereignis für Button 'Bearbeiten'
    */
    private void setzeWert_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setzeWert_ActionPerformed
       bearbeiteSetzenButton(
             Integer.parseInt(((JButton) evt.getSource()).getText()));
    }//GEN-LAST:event_setzeWert_ActionPerformed

   /**
    * Reagiert auf eine Eingabe über den Kandidatenblock für das Setzen oder Entfernen eines
    * Kandidaten im Stradokufeld wenn einer der beiden Kandidatenmodi aktiviert ist.
    *
    * @param evt Aktionsereignis für Button 'Kandidatenmodus'
    */
    private void kandidat_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kandidat_ActionPerformed
       if (kndModus > 0) {
          int knd = Integer.parseInt(((JButton) evt.getSource()).getText());
          if ((evt.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
             strOrg.setKndFilter(knd);
          } else {
             bearbeiteKndButton(knd, (evt.getModifiers() & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK);
          }
       }
    }//GEN-LAST:event_kandidat_ActionPerformed

   /**
    * Leitet die Beendigung der Anwendung ohne Speicherung des aktuellen Status ein.
    *
    * @param evt Aktionsereignis für Menüoption 'Schließen'
    */
    private void mdiBeendenOhneSicherungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mdiBeendenOhneSicherungActionPerformed
       System.exit(0);
    }//GEN-LAST:event_mdiBeendenOhneSicherungActionPerformed

   /**
    * Leitet die Aktion für einen Wechsel der Stradoku-Feldanzeige zum Ausgangszustand ein.
    *
    * @param evt Aktionsereignis für Navi-Button 'Startposition'
    */
    private void zeigeStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zeigeStartActionPerformed
       if (!testModus) {
          setNavi(1);
       }
    }//GEN-LAST:event_zeigeStartActionPerformed

   /**
    * Leitet die Aktion für die Anzeige der Lösung im Stradoku-Feld ein.
    *
    * @param evt Aktionsereignis für Navi-Button 'Lösung zeigen'
    */
    private void zeigeGeloestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zeigeGeloestActionPerformed
       if (!testModus) {
          if (strOrg.getVerlaufsNdx() > 0) {
             gespikt = true;
          }
          setNavi(4);
          strBoard.setSelect(-1);
       }
    }//GEN-LAST:event_zeigeGeloestActionPerformed

   /**
    * Leitet die Aktion für die Anzeige der aktuellen Lösungposition im Stradoku-Feld ein.
    *
    * @param evt Aktionsereignis für Navi-Button 'Aktuelle Punkt'
    */
    private void zeigeAktPosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zeigeAktPosActionPerformed
       if (!testModus) {
          setNavi(2);
       }
    }//GEN-LAST:event_zeigeAktPosActionPerformed

   /**
    * Leitet einen Schritt zurück in der Anzeige des Lösungsstatus ein.
    *
    * @param evt Aktionsereignis für Navi-Button 'Schritt zurück'
    */
    private void zeigeZurueckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zeigeZurueckActionPerformed
       if (!testModus) {
          strOrg.schrittZurueck(true);
       }
    }//GEN-LAST:event_zeigeZurueckActionPerformed

   /**
    * Leitet Speicherung eines Bildes des aktuellen Stradoku mit Rahmen ein.
    *
    * @param evt Aktionsereignis für Menüoption 'Stradoku mit Rahmen speichern'
    */
    private void menuDateiBildMitSpeichernActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDateiBildMitSpeichernActionPerformed
       try {
          speichernBild(true);
       } catch (IOException ex) {
          Logger.getLogger(Stradoku.class.getName()).log(Level.SEVERE, null, ex);
       }
    }//GEN-LAST:event_menuDateiBildMitSpeichernActionPerformed

   /**
    * Leitet das Öffnen einer Stradoku-Datei ein.
    *
    * @param evt Aktionsereignis für Menüoption 'Stradoku öffnen'
    */
    private void mdDateiLadenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mdDateiLadenActionPerformed
       strLaden();
    }//GEN-LAST:event_mdDateiLadenActionPerformed

   /**
    * Leitet das Speichern eines Str9ts unter neuem Namen ein.
    *
    * @param evt Aktionsereignis für Menüoption 'Speichern unter ...'
    */
    private void mdSpeichernAlsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mdSpeichernAlsActionPerformed
       strSpeichernAls();
    }//GEN-LAST:event_mdSpeichernAlsActionPerformed

   /**
    * Ruft Level-Menübehandlung auf
    *
    * @param evt Aktionsereignis für Levelbox
    */
    private void levelBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_levelBoxActionPerformed
       if (!testModus) {
          int lev = levelBox.getSelectedIndex();
          if (lev == 0) {
             levelBox.setSelectedIndex(strOrg.getLevel());
          } else {
             setLevelMenu(lev);
          }
       }
    }//GEN-LAST:event_levelBoxActionPerformed

   /**
    * Leitet auf eine Eingabe der entsprechenden Menüoption die manuelle Eingabe einer neuen
    * Stradoku-Aufgabe ein.
    *
    * @param evt Aktionsereignis für Button 'Neueingabe'
    */
    private void mdEingabeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mdEingabeActionPerformed
       bearbeiteAufgabe(EDIT_NEU, true);
    }//GEN-LAST:event_mdEingabeActionPerformed

   /**
    * Leitet auf eine Eingabe der entsprechenden Menüoption das Kopieren des aktuellen Stradoku im
    * internen Format in die Zwischenablage ein.
    *
    * @param evt Aktionsereignis für Menüoption 'Stradoku in Zwischenablage kopieren'
    */
    private void mdKopierenIntFormatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mdKopierenIntFormatActionPerformed
       kopierenZuClipboard(true, true);
    }//GEN-LAST:event_mdKopierenIntFormatActionPerformed

   /**
    * Leitet auf eine Eingabe der entsprechenden Menüoption die Übernahme eins Stradoku aus der
    * Zwischenablage ein.
    *
    * @param evt Aktionsereignis für 'Stradoku aus Zwischenablage einfügen'
    */
    private void mdiEinfuegenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mdiEinfuegenActionPerformed
       einfuegenVonClipboard();
    }//GEN-LAST:event_mdiEinfuegenActionPerformed

   /**
    * Leitet auf eine Eingabe der entsprechenden Menüoption den Wechsel vom Lösungs- in den
    * Bearbeitungsmodus ein.
    *
    * @param evt Aktionsereignis für Button 'Stradoku bearbeiten'
    */
    private void mbEditierenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mbEditierenActionPerformed
       bearbeiteAufgabe(EDIT_AFG, true);
    }//GEN-LAST:event_mbEditierenActionPerformed

   /**
    * Leitet auf eine Eingabe der entsprechenden Menüoption die Generierung eines neuen Stradoku
    * ein.
    *
    * @param evt Aktionsereignis für Button 'Stradoku neu eingeben'
    */
    private void erzeugeStradokuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_erzeugeStradokuActionPerformed
       if (!testModus) {
          aufgabeErstellen();
       }
    }//GEN-LAST:event_erzeugeStradokuActionPerformed

   /**
    * Leitet auf eine Eingabe der entsprechenden Menüoption den Wechsel der Kandidatenanzeige
    * (Punkte / Ziffern) ein.
    *
    * @param evt Aktionsereignis für Menüoption 'Punkt- oder Ziffernanzeige für Kandidaten'
    */
    private void maKndAnzeigeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maKndAnzeigeActionPerformed
       setKndAnzeigeMod(maKndAnzeige.isSelected());
       if (zifferpunkt) {
          statusBarHinweis.setText("Anzeige der Kandidaten als Punkte eingestellt.");
       } else {
          statusBarHinweis.setText("Anzeige der Kandidaten als Ziffern eingestellt.");
       }
    }//GEN-LAST:event_maKndAnzeigeActionPerformed

   /**
    * Leitet auf eine Eingabe der entsprechenden Menüoption den Wechsel des aktuell eingestellten
    * Levels ein. Damit wird der Level für zu erzeugende Stradoku festgelegt.
    *
    * @param evt Aktionsereignis für Levwelmenü
    */
    private void menuLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuLevelActionPerformed
       setLevelMenu(Integer.parseInt(evt.getActionCommand().substring(6, 7)));
    }//GEN-LAST:event_menuLevelActionPerformed

   /**
    * Leitet nach einem Klick auf das entsprechende Symbol in der Symbolleiste die Generierung einer
    * Stradoku-Serie ein.
    *
    * @param evt Aktionsereignis für Button 'Serie erstellen'
    */
    private void symLeisteSerieErzeugenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_symLeisteSerieErzeugenActionPerformed
       if (!testModus) {
          aufgabenSerieErstellen();
       }
    }//GEN-LAST:event_symLeisteSerieErzeugenActionPerformed

   /**
    * Leitet auf eine Eingabe der entsprechenden Menüoption die Generierung einer Stradoku-Serie
    * ein.
    *
    * @param evt Aktionsereignis für Menüoption 'Serie erstellen'
    */
    private void mdSerieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mdSerieActionPerformed
       if (!testModus) {
          aufgabenSerieErstellen();
       }
    }//GEN-LAST:event_mdSerieActionPerformed

   /**
    * Steuert nach einem Klick auf das entsprechende Symbol in der Symbolleiste für die manuelle
    * Eingabe eines neuen Stradoku den Wechsel vom Lösungsmodus zum Bearbeitungsmodus und umgekehrt.
    *
    * @param evt Aktionsereignis für SymbolButton 'Stradoku eingeben'
    */
    private void symLeisteStrEingabeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_symLeisteStrEingabeActionPerformed
       if (!testModus) {
          if ((evt.getModifiers()
                & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
             bearbeiteAufgabe(EDIT_NEU, false);
          } else {
             bearbeiteAufgabe(EDIT_NEU, true);
          }
       }
    }//GEN-LAST:event_symLeisteStrEingabeActionPerformed

   /**
    * Steuert nach einem Klick auf das entsprechende Symbol in der Symbolleiste für die manuelle
    * Bearbeitung des aktuellen Stradoku den Wechsel vom Lösungsmodus zum Bearbeitungsmodus und
    * umgekehrt.
    *
    * @param evt Aktionsereignis für SymbolButton 'Stradoku bearbeiten'
    */
    private void symLeisteStrEditierenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_symLeisteStrEditierenActionPerformed
       if (!testModus) {
          if ((evt.getModifiers()
                & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
             bearbeiteAufgabe(EDIT_AFG, false);
          } else {
             bearbeiteAufgabe(EDIT_AFG, true);
          }
       }
    }//GEN-LAST:event_symLeisteStrEditierenActionPerformed

   /**
    * Leitet die Anzeiger der Infos über die Verwendung der Maustaste im Stradoku-Feld und der
    * Stradoku-Liste ein.
    *
    * @param evt Aktionsereignis für Menüoptinon 'Info zur Maustaste'
    */
    private void mhInfoMaustastenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mhInfoMaustastenActionPerformed
       if (jHilfe == null) {
          jHilfe = new HilfeDialog(this);
       }
       jHilfe.zeigeHilfe("mausTasten", true);
    }//GEN-LAST:event_mhInfoMaustastenActionPerformed

   /**
    * Leitet nach einem Klick auf das entsprechende Symbol in der Symbolleiste das Öffnen eines
    * gespeicherten Stradoku ein.
    *
    * @param evt Aktionsereignis für SymbolButton 'Stradoku öffnen'
    */
    private void symLeisteStrLadenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_symLeisteStrLadenActionPerformed
       if (!testModus) {
          strLaden();
       }
    }//GEN-LAST:event_symLeisteStrLadenActionPerformed

   /**
    * Leitet nach einem Klick auf das entsprechende Symbol in der Symbolleiste das Speichern des
    * aktuellen Stradoku als Datei ein.
    *
    * @param evt Aktionsereignis für SymbolButton 'Stradoku speichern'
    */
    private void speichernStradokuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speichernStradokuActionPerformed
       if (!testModus) {
          strSpeichern(true);
       }
    }//GEN-LAST:event_speichernStradokuActionPerformed

   /**
    * Leitet nach einem Klick auf das entsprechende Symbol in der Symbolleiste die Anzeige der
    * Stradoku-Liste ein.
    *
    * @param evt Aktionsereignis für Menüoption 'StradokuListe zeigen'
    */
    private void listeZeigen(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listeZeigen
       if (!testModus) {
          listeZeigen();
       }
    }//GEN-LAST:event_listeZeigen

   /**
    * Leitet nach einem Klick auf das entsprechende Symbol in der Symbolleiste die Übergabe des
    * aktuellen Stradoku in die Liste ein.
    *
    * @param evt Aktionsereignis für SymbolButton 'Stradoku zur Liste hinzufügen'
    */
    private void aufgabeInListeSpeichern(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aufgabeInListeSpeichern
       if (!testModus) {
          aufgabeInListeSpeichern();
       }
    }//GEN-LAST:event_aufgabeInListeSpeichern

   /**
    * Leitet nach einem Klick auf das entsprechende Symbol in der Symbolleiste die Übernahme eines
    * Stradoku aus der Liste ein.
    *
    * @param evt Aktionsereignis für SymbolButton 'Stradoku aus Liste übernehmen'
    */
    private void aufgabeAusListeLaden(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aufgabeAusListeLaden
       if (!testModus) {
          aufgabeAusListeLaden();
       }
    }//GEN-LAST:event_aufgabeAusListeLaden

   /**
    * Leitet auf eine Eingabe der entsprechenden Menüoption oder F1 die Anzeige der Hilfedatei ein.
    *
    * @param evt Aktionsereignis für Menüoption 'Hilfe zeigen'
    */
    private void mhInfoProgrammActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mhInfoProgrammActionPerformed
       if (jHilfe == null) {
          jHilfe = new HilfeDialog(this);
       }
       jHilfe.zeigeHilfe("strInfo", true);
    }//GEN-LAST:event_mhInfoProgrammActionPerformed

   /**
    * Leitet auf eine Eingabe der entsprechenden Menüoption die Anzeige des Infofensters zu diesem
    * Programm ein.
    *
    * @param evt Aktionsereignis für Menüoption 'Info zum Programm'
    */
    private void mhInfoUeberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mhInfoUeberActionPerformed
       if (prgInfo == null) {
          prgInfo = new ProgrammInfo(this);
       }
       prgInfo.zeigeDialog();
    }//GEN-LAST:event_mhInfoUeberActionPerformed

   /**
    * Wird über die Menüoption "Navigieren - Gespeicherte Lösungsposition zeigen" aufgerufen und
    * leitet die Anzeige einer gespeicherten Lösungsposition ein.
    *
    * @param evt Aktionsereignis für SymbolButton 'Lösungsposition'
    */
    private void mnSpeicherPosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnSpeicherPosActionPerformed
       strOrg.gotoNaviPosition(3);
       statusBarHinweis.setText("Gespeicherte Lösungsposition");
    }//GEN-LAST:event_mnSpeicherPosActionPerformed

   /**
    * Leitet auf eine Eingabe der entsprechenden Menüoption das Kopieren des aktuellen Stradoku im
    * externen Format in die Zwischenablage ein.
    *
    * @param evt Aktionsereignis für Menüoption 'Stradoku im ext. Format in Zwischenablage'
    */
    private void mdKopierenExtFormatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mdKopierenExtFormatActionPerformed
       kopierenZuClipboard(false, true);
    }//GEN-LAST:event_mdKopierenExtFormatActionPerformed

   /**
    * Setzt bei einem Klick mit gedrückter Strg-Taste auf die Zeitanzeige diese auf 0.
    *
    * @param evt Aktionsereignis für Klick auf Zeitanzeige
    */
    private void statusBarZeitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statusBarZeitMouseClicked
       if (evt.isControlDown()) {
          setStartZeit(0);
       }
    }//GEN-LAST:event_statusBarZeitMouseClicked

   /**
    * Leitet auf eine Eingabe der entsprechenden Menüoption die Anzeige der Hilfe zu den Kurztasten
    * ein.
    *
    * @param evt Aktionsereignis für Menüoption 'Info zu Kurztasten'
    */
    private void mhInfoKurztastenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mhInfoKurztastenActionPerformed
       if (jHilfe == null) {
          jHilfe = new HilfeDialog(this);
       }
       jHilfe.zeigeHilfe("kurzTasten", true);
    }//GEN-LAST:event_mhInfoKurztastenActionPerformed

   /**
    * Leitet auf eine Eingabe der entsprechenden Menüoption nach Sicherung des aktuellen Status die
    * Beendigung des Programms ein.
    *
    * @param evt Aktionsereignis für Menüoption 'Beenden mit Sicherung'
    */
    private void mdBeendenMitSicherungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mdBeendenMitSicherungActionPerformed
       beendeAnwendung();
       System.exit(0);
    }//GEN-LAST:event_mdBeendenMitSicherungActionPerformed

   /**
    * Leitet auf eine Eingabe der entsprechenden Menüoption einen Update-Check ein.
    *
    * @param evt Aktionsereignis für Menüoption 'Update prüfen'
    */
    private void mhInfoUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mhInfoUpdateActionPerformed
       UpdateCheck updateCheck = new UpdateCheck(this, VERSION);
       updateCheck.start();
    }//GEN-LAST:event_mhInfoUpdateActionPerformed

   /**
    * Setzt auf einen Klick mit gedrückter Strg-Taste auf die Fehleranzeige in der Statuszeile diese
    * auf 0 zurück.
    *
    * @param evt Aktionsereignis für Klick auf EingabeFehleranzeige
    */
    private void statusBarFehlerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statusBarFehlerMouseClicked
       if (evt.isControlDown()) {
          setStatusBarFehler(false);
       }
    }//GEN-LAST:event_statusBarFehlerMouseClicked

   /**
    * Setzt auf eine Eingabe der entsprechenden Menüoption die Markierung auf Blau.
    *
    * @param evt Aktionsereignis für Menüoption 'Farbe Blau'
    */
    private void mmBlauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mmBlauActionPerformed
       setzeMarkierung(M_BLAU);
    }//GEN-LAST:event_mmBlauActionPerformed

   /**
    * Setzt auf eine Eingabe der entsprechenden Menüoption die Markierung auf Grün.
    *
    * @param evt Aktionsereignis für Menüoption 'Farbe Grün'
    */
    private void mmGruenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mmGruenActionPerformed
       setzeMarkierung(M_GRUEN);
    }//GEN-LAST:event_mmGruenActionPerformed

   /**
    * Setzt auf eine Eingabe der entsprechenden Menüoption die Markierung auf Rosa.
    *
    * @param evt Aktionsereignis für Menüoption 'Farbe Rosa'
    */
    private void mmRosaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mmRosaActionPerformed
       setzeMarkierung(M_ROSA);
    }//GEN-LAST:event_mmRosaActionPerformed

   /**
    * Setzt auf eine Eingabe der entsprechenden Menüoption die Markierung auf Violett.
    *
    * @param evt Aktionsereignis für Menüoption 'Farbe Violett'
    */
    private void mmViolettActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mmViolettActionPerformed
       setzeMarkierung(M_VIOLETT);
    }//GEN-LAST:event_mmViolettActionPerformed

   /**
    * Entfernt auf eine Eingabe der entsprechenden Menüoption die Markierung aus der selektierten
    * Zelle.
    *
    * @param evt Aktionsereignis für Menüoption 'Markierung für Zelle entfernen'
    */
    private void mmEntfernenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mmEntfernenActionPerformed
       strOrg.setMarkierung(strBoard.getSelect(), 0, false);
       repaint();
    }//GEN-LAST:event_mmEntfernenActionPerformed

   /**
    * Entfernt auf eine Eingabe der entsprechenden Menüoption alle Markierungen.
    *
    * @param evt Aktionsereignis für Menüoption 'alle Markierungen entfernen'
    */
    private void mmEntferneAlleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mmEntferneAlleActionPerformed
       strOrg.entferneMarkierungen();
       repaint();
    }//GEN-LAST:event_mmEntferneAlleActionPerformed

   /**
    * Steuert nach einem Klick auf das entsprechende Symbol in der Symbolleiste den aktuellen
    * Kandidatenmodus und die Anzeige beider Symbol-Buttons dazu.
    *
    * @param evt Aktionsereignis für SymbolButton 'Kandidatenmodus Notizen'
    */
    private void symLeisteNotizenModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_symLeisteNotizenModeActionPerformed
       if (!testModus) {
          setNotizenMode(true);
       }
    }//GEN-LAST:event_symLeisteNotizenModeActionPerformed

   /**
    * Leitet Speicherung eines Bildes des aktuellen Stradoku ohne Rahmen ein.
    *
    * @param evt Aktionsereignis für Menüoption 'Stradoku als PNG ohne Rahmen speichern'
    */
    private void menuDateiBildOhneSpeichernActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDateiBildOhneSpeichernActionPerformed
       try {
          speichernBild(false);
       } catch (IOException ex) {
          Logger.getLogger(Stradoku.class.getName()).log(Level.SEVERE, null, ex);
       }
    }//GEN-LAST:event_menuDateiBildOhneSpeichernActionPerformed

   /**
    * Leitet den Import einer Stradoku-Liste ein.
    *
    * @param evt Aktionsereignis für Menüoption 'Stradoku-Liste importieren'
    */
    private void mdListenImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mdListenImportActionPerformed
       try {
          strListenImport();
       } catch (IOException ex) {
          Logger.getLogger(Stradoku.class.getName()).log(Level.SEVERE, null, ex);
       }
    }//GEN-LAST:event_mdListenImportActionPerformed
   /**
    * Leitet auf Eingabe der entsprechenden Menüoption die Anzeige der Stradoku-Liste ein.
    *
    * @param evt Aktionsereignis für Menüoption 'Liste zeigen'
    */
    private void maListeZeigenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maListeZeigenActionPerformed
       listeZeigen();
    }//GEN-LAST:event_maListeZeigenActionPerformed

   /**
    * Wird bei Mausklick auf Verlaufsposition-Button aufgerufen und ermöglicht mit gedrückter
    * Strg-Taste das Speichern der aktuellen Lösungsposition und ohne die Anzeige einer
    * gespeicherten Lösungsposition.
    *
    * @param evt Aktionsereignis für SymbolButton 'Lösungsposition speichern'
    */
    private void makeSavePosition(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_makeSavePosition
       if (evt.isControlDown()) {
          setSpeicherPos();
          strBoard.setSelect(40);
          statusBarHinweis.setText("Aktuelle Lösungsposition ("
                + posSicherung + ") gespeichert");
          return;
       }
       if (evt.isShiftDown()) {
          posSicherung = strOrg.getSavePosNdx();
          statusBarHinweis.setText("Gespeicherte Lösungsposition: "
                + posSicherung);
       } else {
          setNavi(1);
          strOrg.gotoNaviPosition(3);
          strBoard.setSelect(40);
          statusBarHinweis.setText("Gespeicherte Lösungsposition");
       }
    }//GEN-LAST:event_makeSavePosition

   /**
    * Wird bei Menüauswahl für "Navigieren Aktuelle Lösungsposition speichern" aufgerufen.
    *
    * @param evt Aktionsereignis für SymbolButton 'Lösungsposition'
    */
    private void mnPosSpeichernActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnPosSpeichernActionPerformed
       setSpeicherPos();
    }//GEN-LAST:event_mnPosSpeichernActionPerformed

   /**
    * Leitet auf Auswahl der entsprechenden Menüoption die Bearbeitung des aktuellen Stradoku mit
    * den vorhandenen Lösungswerten als Vorgabewerte ein.
    *
    * @param evt Aktionsereignis für Menüoption 'Lösung bearbeiten
    */
    private void mbEditierenMitLWActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mbEditierenMitLWActionPerformed
       bearbeiteAufgabe(EDIT_LOW, true);
    }//GEN-LAST:event_mbEditierenMitLWActionPerformed

   /**
    * Steuert auf die entsprechende Menüoption die Anzeige von Infos zur Eingabe von Werten und
    * Kandidaten.
    *
    * @param evt Aktionsereignis für Menüoption 'EingabeInfos zeigen'
    */
    private void mhEingabeHinweiseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mhEingabeHinweiseActionPerformed
       zeigeInfo = !zeigeInfo;
       zeigeHinweis(zeigeInfo);
    }//GEN-LAST:event_mhEingabeHinweiseActionPerformed

   /**
    * Steuert nach Eingabe über die entsprechende Menüoption oder Symbolbutton die Anzeige der
    * nächsten einer zurückgenommenen Lösungssituation.
    *
    * @param evt Aktionsereignis für 'zurückgenommener Lösungsschritt wiederholen'
    */
    private void zeigeVorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zeigeVorActionPerformed
       if (!testModus) {
          strOrg.schrittWiederholen(true);
       }
    }//GEN-LAST:event_zeigeVorActionPerformed

   /**
    * Wechselt nach Auswahl der entsprechenden Menüoption mit allen Lösungswerten als Vorgabewerte
    * in den Bearbeitungsmodus.
    *
    * @param evt Aktionsereignis für Menüoption 'Stradoku mit Lösungswerten bearbeiten'
    */
    private void mbLoesungBearbeitenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mbLoesungBearbeitenActionPerformed
       bearbeiteAufgabe(EDIT_LOS, true);
    }//GEN-LAST:event_mbLoesungBearbeitenActionPerformed

   /**
    * Leitet die Änderung des aktuellen Modus für die Anzeige der Kandidatenliste ein.
    *
    * @param evt Aktionsereignis für SymbolButton 'Kandidatenanzeige'
    */
    private void symLeisteKndListenModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_symLeisteKndListenModeActionPerformed
       if (!testModus && ((evt.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK)) {
          symLeisteNotizenMode.setEnabled(false);
          symLeisteTipp.setEnabled(false);
          makeTestModus();
       } else {
          symLeisteTipp.setEnabled(true);
          symLeisteNotizenMode.setEnabled(true);
          if (testModus) {
             makeTestModus();
          } else {
            setKndListenMode(true);
          }
       }
    }//GEN-LAST:event_symLeisteKndListenModeActionPerformed

   /**
    * Öffnet nach Auswahl der entsprechenden Menüoption die Hilfedatei und zeigt den Abschnitt zu
    * den Lösungsstrategien an.
    *
    * @param evt Aktionsereignis für Menüoption 'Lösungsstrategien zeigen'
    */
    private void mhlnfoStrategienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mhlnfoStrategienActionPerformed
       if (jHilfe == null) {
          jHilfe = new HilfeDialog(this);
       }
       jHilfe.zeigeHilfe("lStrat", true);
    }//GEN-LAST:event_mhlnfoStrategienActionPerformed

    private void mmOkerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mmOkerActionPerformed
       setzeMarkierung(M_OKER);
    }//GEN-LAST:event_mmOkerActionPerformed

    private void maKndListeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maKndListeActionPerformed
       setKndListenMode(true);
    }//GEN-LAST:event_maKndListeActionPerformed

    private void maNotizenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maNotizenActionPerformed
       if (!testModus) {
          setNotizenMode(true);
       }
    }//GEN-LAST:event_maNotizenActionPerformed

    private void hinweisFeldMausKlick(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hinweisFeldMausKlick
       if (erstinfo) {
          labelHinweisfeld.setText(EINGABEMODUS);
          erstinfo = false;
       } else {
          labelHinweisfeld.setText("");
       }
       strOrg.entferneMarkierungen();
       repaint();
    }//GEN-LAST:event_hinweisFeldMausKlick

    private void mdMaxFeld2PNGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mdMaxFeld2PNGActionPerformed
       kopierenFeld(true);
    }//GEN-LAST:event_mdMaxFeld2PNGActionPerformed

    private void mdMinFeld2PNGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mdMinFeld2PNGActionPerformed
       kopierenFeld(false);
    }//GEN-LAST:event_mdMinFeld2PNGActionPerformed

    private void mbTestModeCheck(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mbTestModeCheck
       if (!testModus) {
          symLeisteNotizenMode.setEnabled(false);
          symLeisteTipp.setEnabled(false);
          makeTestModus();
       } else {
          symLeisteTipp.setEnabled(true);
          symLeisteNotizenMode.setEnabled(true);
          makeTestModus();
          setKndListenMode(true);
       }
    }//GEN-LAST:event_mbTestModeCheck

    private void loesungsTippActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loesungsTippActionPerformed
       try {
          isTipp = true;
          labelHinweisfeld.setText("");
          strOrg.entferneMarkierungen();
          if (strOrg.loeseLogStradoku()) {
             tipps++;
          }
       } catch (IOException ex) {
          Logger.getLogger(Stradoku.class.getName()).log(Level.SEVERE, null, ex);
       }
    }//GEN-LAST:event_loesungsTippActionPerformed

   private void naviPosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_naviPosActionPerformed
      // TODO add your handling code here:
   }//GEN-LAST:event_naviPosActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem LoesungsTipp;
    private javax.swing.JPanel hinweisPanel;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JPopupMenu.Separator jSeparator14;
    private javax.swing.JPopupMenu.Separator jSeparator15;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JButton kandidat_1;
    private javax.swing.JButton kandidat_2;
    private javax.swing.JButton kandidat_3;
    private javax.swing.JButton kandidat_4;
    private javax.swing.JButton kandidat_5;
    private javax.swing.JButton kandidat_6;
    private javax.swing.JButton kandidat_7;
    private javax.swing.JButton kandidat_8;
    private javax.swing.JButton kandidat_9;
    private java.awt.Label labelFehler;
    public javax.swing.JLabel labelHinweisfeld;
    private javax.swing.JLabel labelKandidaten;
    private java.awt.Label labelLevel;
    private javax.swing.JLabel labelTitel;
    private javax.swing.JLabel labelWerte;
    public javax.swing.JComboBox levelBox;
    private javax.swing.ButtonGroup levelGroup;
    private javax.swing.JMenuItem maAktuell;
    private javax.swing.JCheckBoxMenuItem maKndAnzeige;
    private javax.swing.JMenuItem maKndListe;
    private javax.swing.JMenuItem maListeZeigen;
    private javax.swing.JMenuItem maLoesung;
    private javax.swing.JMenuItem maNotizen;
    private javax.swing.JMenuItem maStart;
    private javax.swing.JMenuItem mbEditieren;
    private javax.swing.JMenuItem mbEditierenMitLW;
    private javax.swing.JMenuItem mbLoesungBearbeiten;
    private javax.swing.JCheckBoxMenuItem mbTestMode;
    private javax.swing.JMenuItem mdBeendenMitSicherung;
    private javax.swing.JMenuItem mdBildSpeichern_mR;
    private javax.swing.JMenuItem mdBildSpeichern_oR;
    private javax.swing.JMenuItem mdDateiLaden;
    private javax.swing.JMenuItem mdEingabe;
    private javax.swing.JMenuItem mdKopierenExtFormat;
    private javax.swing.JMenuItem mdKopierenIntFormat;
    private javax.swing.JMenuItem mdListenImport;
    private javax.swing.JMenuItem mdMaxFeld2PNG;
    private javax.swing.JMenuItem mdMinFeld2PNG;
    private javax.swing.JMenuItem mdSerie;
    private javax.swing.JMenuItem mdSpeichern;
    private javax.swing.JMenuItem mdSpeichernAls;
    private javax.swing.JMenuItem mdStrAusListe;
    private javax.swing.JMenuItem mdStrInListe;
    private javax.swing.JMenuItem mdiBeendenOhneSicherung;
    private javax.swing.JMenuItem mdiEinfuegen;
    private javax.swing.JMenuItem mdiErzeugen;
    private javax.swing.JMenu menuAnzeige;
    public javax.swing.JMenu menuBearbeiten;
    private javax.swing.JMenu menuDatei;
    private javax.swing.JMenu menuFarbMarkierung;
    private javax.swing.JMenu menuInfo;
    private javax.swing.JMenu menuLevel;
    private javax.swing.JMenu menuNavi;
    private javax.swing.JMenuBar menuZeile;
    private javax.swing.JCheckBoxMenuItem mhEingabeHinweise;
    private javax.swing.JMenuItem mhInfoKurztasten;
    private javax.swing.JMenuItem mhInfoMaustasten;
    private javax.swing.JMenuItem mhInfoProgramm;
    private javax.swing.JMenuItem mhInfoUeber;
    private javax.swing.JMenuItem mhInfoUpdate;
    private javax.swing.JMenuItem mhlnfoStrategien;
    private javax.swing.JRadioButtonMenuItem ml_0;
    private javax.swing.JRadioButtonMenuItem ml_1;
    private javax.swing.JRadioButtonMenuItem ml_2;
    private javax.swing.JRadioButtonMenuItem ml_3;
    private javax.swing.JRadioButtonMenuItem ml_4;
    private javax.swing.JRadioButtonMenuItem ml_5;
    private javax.swing.JMenuItem mmBlau;
    private javax.swing.JMenuItem mmEntferneAlle;
    private javax.swing.JMenuItem mmEntfernen;
    private javax.swing.JMenuItem mmGruen;
    private javax.swing.JMenuItem mmOker;
    private javax.swing.JMenuItem mmRosa;
    private javax.swing.JMenuItem mmViolett;
    private javax.swing.JMenuItem mnLoesung;
    private javax.swing.JMenuItem mnPosSpeichern;
    private javax.swing.JMenuItem mnRueck;
    private javax.swing.JMenuItem mnSpeicherPos;
    private javax.swing.JMenuItem mnStart;
    private javax.swing.JMenuItem mnVor;
    private javax.swing.JMenuItem mniAktuell;
    private javax.swing.JButton naviPos;
    public javax.swing.JButton naviTasteAktPos;
    public javax.swing.JButton naviTasteLoesung;
    public javax.swing.JButton naviTasteStart;
    public javax.swing.JButton naviTasteVor;
    public javax.swing.JButton naviTasteZurueck;
    private java.awt.Panel panelStatusBar;
    private javax.swing.JLabel seperatorLabel1;
    private javax.swing.JLabel seperatorLabel2;
    private javax.swing.JLabel seperatorLabel3;
    private javax.swing.JLabel seperatorLabel4;
    private javax.swing.JLabel seperatorLabel5;
    private javax.swing.JLabel seperatorLabel6;
    private javax.swing.JLabel seperatorLabel7;
    private javax.swing.JLabel seperatorLabel8;
    private javax.swing.JButton setzeWert_1;
    private javax.swing.JButton setzeWert_2;
    private javax.swing.JButton setzeWert_3;
    private javax.swing.JButton setzeWert_4;
    private javax.swing.JButton setzeWert_5;
    private javax.swing.JButton setzeWert_6;
    private javax.swing.JButton setzeWert_7;
    private javax.swing.JButton setzeWert_8;
    private javax.swing.JButton setzeWert_9;
    public java.awt.Label statusBarFehler;
    public java.awt.Label statusBarHinweis;
    public java.awt.Label statusBarLevel;
    public java.awt.Label statusBarZeit;
    private javax.swing.JPanel stradokuFeld;
    private javax.swing.JToggleButton symLeisteKndListenMode;
    private javax.swing.JButton symLeisteListeZeigen;
    private javax.swing.JToggleButton symLeisteNotizenMode;
    public javax.swing.JButton symLeisteSerieErzeugen;
    private javax.swing.JToggleButton symLeisteStrEditieren;
    private javax.swing.JToggleButton symLeisteStrEingabe;
    public javax.swing.JButton symLeisteStrErzeugen;
    private javax.swing.JButton symLeisteStrLaden;
    private javax.swing.JButton symLeisteStrSpeichern;
    private javax.swing.JButton symLeisteStrVonListe;
    private javax.swing.JButton symLeisteStrZuListe;
    private javax.swing.JToggleButton symLeisteTipp;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables

   /**
    * Startpunkt für dies Anwendung.
    *
    * @param args : beim Aufruf über die Kommandozeile übergebenen Argumente
    */
   @SuppressWarnings("Convert2Lambda")
   public static void main(final String args[]) {
      try {
         UIManager.setLookAndFeel(
               UIManager.getSystemLookAndFeelClassName());
      } catch (ClassNotFoundException
            | InstantiationException
            | IllegalAccessException
            | UnsupportedLookAndFeelException ex) {
         JOptionPane.showMessageDialog(null,
               "Es ist ein Fehler aufgetreten. \n\n"
               + ex.getMessage(),
               "Hinweis", 1);
      }
      EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            new Stradoku(args).setVisible(true);
         }
      });
   }
}
