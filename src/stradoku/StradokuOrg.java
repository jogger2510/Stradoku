/**
 * StradokuOrg.java ist Teil des Programmes kodelasStradoku
 *
 * Umgearbeitet am:             03.07.2017 12:00
 * Letzte Änderung:             19.12.2024 19:45
 *
 * Copyright (C) Konrad Demmel, 2017 - 2024
 */
package stradoku;

import java.io.IOException;
import javax.swing.*;

/**
 * <p>
 * Die Klasse StradokuOrg ist Informations- und Aktionspool für das aktuelle Stradoku.</p>
 * <p>
 * Zu diesen Informationen zählen die Aufgabe selbst und die Lösung dazu, der augenblickliche
 * Lösungsstatus, also welchen Zellen bereits Lösungswerte zugewiesen und welche Kandidaten in den
 * noch freien Zellen möglich sind und der Lösungsverlauf.</p>
 * <p>
 * Die Klasse StradokuOrg übernimmt und verarbeitet auch alle Eingaben und liefert alle für
 * dieAusgabe erforderlichen Informationen.</p>
 * <p>
 * Dazu wird auf drei Arrys zurückgegriffen, aufgabe[], loesung[] und als wichtigstes
 * stradoku[].</p>
 * <p>
 * Während die beiden Arrays aufgabe[] und loesung[] statisch sind, das heißt, ihr Inhalt wird nach
 * der Initialisierung nicht mehr verändert, wird das Array stradoku[] dynamisch geführt und in ihm,
 * ausgehend von der Aufgabe, der Lösungsverlauf fortgeschrieben.</p>
 * <p>
 * Im Array stradoku[] enthalten die Zellwerte in Abhängigkeit ihrer Belegung folgende
 * Informationen:
 * <TABLE BORDER="0" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
 *
 * <TR><TD>&nbsp;&nbsp;-</TD>
 * <TD>Ist es ein Vorgabewert, so ist nur dieser, eventuell auch eine Farbmarkierung
 * eingetragen.</TD></TR>
 *
 * <TR><TD VALIGN="TOP">&nbsp;&nbsp;-</TD>
 * <TD>Für leere Zellen wird dafür ein Erkennungsbit gesetzt, die möglichen Kandidaten bittcodiert,
 * ihre Anzahl eingetragen und eine eventuelle Farbmarkierung gesetzt.</TD>
 *
 * <TR><TD VALIGN="TOP">&nbsp;&nbsp;-</TD>
 * <TD>Für Sperrzellen wird ebenfalls ein Erkennungsbit gesetzt.</TD></TR>
 * </TABLE>
 *
 * <p>
 * Dafür wird folgende Codierung verwendet:</p>
 *
 * <TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
 *
 * <TR><TD><B>Bits</B></TD><TD><B>Beschreibung</B></TD></TR>
 *
 * <TR><TD>0 - 3</TD><TD>enthalten bei Zellen mit Vorgabewerten (auch Sperrzellen) und bei gelösten
 * Zellen den Wert, bei freien Zellen die Anzahl der noch möglich erscheinenden Kandidaten. Frei ist
 * eine Zelle, wenn ihr Wert größer als 0xF ist.</TD></TR>
 *
 * <TR><TD>4</TD><TD>ist bei allen ursprünglich leeren Zellen gesetzt</TD></TR>
 *
 * <TR><TD>5 - 13</TD><TD>werden für die in der Zelle noch vertreten Kandidaten gesetzt, also Bit 5
 * für den Kandidaten 1, Bit 6 für 2 usw. bis Bit 13 für 9</TD></TR>
 *
 * <TR><TD>14 - 22</TD><TD>für die vom Anwender gesetzten Notizen</TD></TR>
 *
 * <TR><TD>23</TD><TD>für eine Sperrzelle</TD></TR>
 *
 * <TR><TD>24, 25</TD><TD>Reserve</TD></TR>
 *
 * <TR><TD>26 - 30</TD><TD>Farbwerte</TD></TR>
 *
 * <TR><TD>26</TD><TD>Flag für Oker als Farbmarkierung</TD></TR>
 *
 * <TR><TD>27</TD><TD>Flag für Violett als Farbmarkierung</TD></TR>
 *
 * <TR><TD>28</TD><TD>Flag für Grün als Farbmarkierung</TD></TR>
 *
 * <TR><TD>29</TD><TD>Flag für Blau als Farbmarkierung</TD></TR>
 *
 * <TR><TD>30</TD><TD>Flag für Rosa als Farbmarkierung</TD></TR>
 *
 * <TR><TD>31</TD><TD>Reserve</TD></TR>
 * </TABLE>
 * <p>
 * Nach diesem Schema werden auch die Arrays aufgabe[] und loesung[] erstellt, wobei in loesung[]
 * nur das Flag für den Vorgabewert gesetzt ist.</p>
 * <p>
 * aktStr ist eine Referenz auf ein eindimensionales Integer-Array und kann auf jedes der drei
 * initialisierten Arrays verweisen.</p>
 * <p>
 * Es gehört aber auch zu ihrer Aufgabe, den Lösungsverlauf zu registrieren und damit zu
 * ermöglichen, dass Lösungsschritte zurückgenommen und auch wiederholt werden können.</p>
 *
 * <p>
 * --------------------------------------------------------------------------------</p>
 *
 * <p>
 * Hinweise zu der Verlaufs-Gestaltung:</p>
 * <p>
 * Es ist zwischen vom Anwender unmittelbar ausgeführten Lösungsschritten und den sich daraus
 * eventuell ergebenden mittelbaren Lösungsschritten zu unterscheiden. Zu letzten zählen die
 * Kandidatenausschlüsse im Zusammenhang mit einer Wertzuweiseung.</p>
 *
 * <p>
 * Jeder Lösungsschritt wird in einem Integer-Wert gespeichert, alle zusammen in dem Array
 * strVerlauf[].</p>
 *
 * <p>
 * Belegung des Arrays strVerlauf:</p>
 *
 * <TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
 *
 * <TR><TD>
 * <B>Bits</B></TD><TD><B>Hex-Maske</B></TD><TD><B>Verwendung</B></TD><TD><B>Bemerkung</B></TD></TR>
 *
 * <TR><TD>0 - 3</TD><TD>0xF</TD><TD>Lösungswerte und Kandidaten</TD><TD>ZAHL</TD></TR>
 *
 * <TR><TD>4</TD><TD>0x10</TD><TD>Flag für Lösungszelle ohne Vorgabewert</TD><TD>LWFL</TD></TR>
 *
 * <TR><TD>5</TD><TD>0x20</TD><TD>Lösungswert wurde gesetzt</TD><TD>SETZEN</TD></TR>
 *
 * <TR><TD>6</TD><TD>0x40</TD><TD>Lösungswert wurde entfernt</TD><TD>ENTFERNEN</TD></TR>
 *
 * <TR><TD>7</TD><TD>0x80</TD><TD>Flag für Folgeausschluss eines
 * Kandidaten</TD><TD>LK_FOLGE</TD></TR>
 *
 * <TR><TD>8</TD><TD>0x100</TD><TD>Status der KndListe wurde geändert</TD><TD>LKND</TD></TR>
 *
 * <TR><TD>9</TD><TD>0x200</TD><TD>Kandidaten der Liste werden angezeigt</TD><TD>LKND</TD></TR>
 *
 * <TR><TD>10</TD><TD>0x400</TD><TD>Flag für Kandidatenanzeige</TD><TD>KLISTE</TD></TR>
 *
 * <TR><TD>11</TD><TD>0x400</TD><TD>Flag für Notizenanzeige</TD><TD>NOTIZEN</TD></TR>
 *
 * <TR><TD>12-15</TD><TD>0xF800</TD><TD>Reserve</TD><TD></TD></TR>
 *
 * <TR><TD>16-22</TD><TD>0x1000</TD><TD>Kandidatenbelegung einer Zelle</TD><TD></TD></TR>
 *
 * <TR><TD>23-31</TD><TD></TD><TD>Reseve</TD><TD></TD></TR>
 * </TABLE>
 *
 * <p>
 * Die Variable verlaufsNdx zeigt auf den jeweils letzten Eintrag in dieser Liste. Die Variable
 * letzterVerlaufsNdx zeigt auf den letzten Lösungsschritt.</p>
 *
 * @author Konrad Demmel
 */
public class StradokuOrg implements GlobaleObjekte {

   private static Stradoku strApp;
   private static StradokuBoard strBoard;
   private static LevelSolver strLoeser;
   private static SetSperrWerte strSperrwerte;
   private static final int ALLEK = 0x3FF9;        // 9 K-Bits, Flag-Bit leere Zelle, 9 als Zähler
   private static final int LWFL = 0x10;           // Flag für Zelle mit Lösungswert
   private static final int SETZEN = 0x20;         // Flags für für Verlauf
   private static final int ENTFERNEN = 0x40;
   private static final int LK_FOLGE = 0x80;
   private static final int NUR_KL = 0x100;
   private static final int LKND = 0x200;
   private static final int KLISTE = 0x400;
   private static final int NOTIZEN = 0x800;
   private static final int KMF = 10;              // links/rechts Shift für Kandidatenmodus
   private static final int KMODE = 0xC00;
   private static final int ZI = 16;               // Versatz für Zellindex in strVerlauf
   private static final int LOESUNG_NDX = 999;
   private static final int NDX_MASK = 0x7FC000;   // Maske für Zellindex in Verlauf-Array
   private final int[] aufgabe;
   private final int[] loesung;
   private final int[] stradoku;
   private final int[] tmpStradoku;
   private final int[] RandomNdx;
   public int[] aktStr;
   private final int[] strVerlauf;
   private int verlaufSize = 820;              // alt = 640
   private int verlaufsNdx;
   private int letzterVerlaufsNdx;
   private int saveVerlaufsNdx;
   private int geloestNdx;
   private int freieZellen;
   private int loesungsZellen;
   private int level;
   private int filterKnd = 0;
   private boolean loesungsModus;
   private boolean geloest;
   private boolean eindeutig;
   private boolean gestartet;

   /**
    * Konstruktor
    *
    * @param mfrm Referenz zu Hauptfenster
    */
   public StradokuOrg(Stradoku mfrm) {
      strApp = mfrm;
      aufgabe = new int[81];
      stradoku = new int[81];
      loesung = new int[81];
      tmpStradoku = new int[81];
      strVerlauf = new int[verlaufSize];
      RandomNdx = new int[81];
      initRandomNdx();
      verlaufsNdx = 0;
      saveVerlaufsNdx = 0;
      letzterVerlaufsNdx = verlaufsNdx;
      geloestNdx = LOESUNG_NDX;
      gestartet = false;
   }

   /**
    * Übergibt Referenz auf die Klasse StradokuBoard.
    *
    * @param strBrd übergebene Referenz
    */
   public void setStrBoard(StradokuBoard strBrd) {
      strBoard = strBrd;
   }

   /**
    * Erzeugt ein leeres Aufgabenfeld.
    */
   public void setzeStradokuNeu() {
      for (int i = 0; i < 81; i++) {
         aufgabe[i] = 0;
      }
      freieZellen = 81;
   }

   /**
    * Überprüft nach der Neueingabe oder Bearbeitung eines Stradoku, ob und mit welchem Level die
    * Aufgabe gelöst werden kann.
    *
    * @param check16 Flag für Prüfung auf Archivtauglichkeit
    * @param wHinw Flag für Anzeige von Warten-Hinweis
    * @return eindeutig gelöst: Level (1-5), nicht eindeutig gelöst: 0 nicht gelöst, also
    * fehlerhaft: -1 bei Prüfung auf Archivtauglichkeit: tauglich 1 - 9
    */
   public int loeseStradoku(boolean check16, boolean wHinw) {
      if (getSperrzellen() < 1) {
         return -1;
      }
      filterKnd = 0;
      System.arraycopy(aufgabe, 0, stradoku, 0, 81);
      erstelleKndListe(stradoku);
      for (int i = 0; i < 81; i++) {
         loesung[i] = 0;
      }
      strLoeser = new LevelSolver(this, stradoku, loesung, true);
      level = strLoeser.loeseAufgabe();
      System.arraycopy(aufgabe, 0, stradoku, 0, 81);
      erstelleKndListe(stradoku);
      if (level == 5) {
         HinweisWarten hnw = new HinweisWarten(strApp);
         EindeutigkeitChecken echeck = new EindeutigkeitChecken(
               strApp, hnw, aufgabe, loesung);
         echeck.execute();
         if (wHinw) {
            hnw.setVisible(true);
         }
         eindeutig = echeck.is_eindeutig();
         if (!eindeutig) {
            level = 0;
         }
         strApp.setStatusBarHinweis("", false);
      } if (check16 && level > 0) {
         ArchivLevel aLevel = new ArchivLevel();
         level = aLevel.getArchivLevel(stradoku, loesung, level);
      }
      return level;
   }

   /**
    * Löst das aktuelle Stradoku mit enem Level von 1 bis 4 und gibt dabei die einzelnen
    * Lösungsschritte in die Datei "LogStradoku + Name" aus.
    *
    * @throws java.io.IOException
    */
   public boolean loeseLogStradoku() throws IOException {
      boolean hinweis = false;
      if (verlaufsNdx == 0 || verlaufsNdx <= letzterVerlaufsNdx) {
         TippLoeser strLogLoeser = new TippLoeser(strApp, stradoku);
         hinweis = strLogLoeser.loeseLogAufgabe();
      }
      return hinweis;
   }

   /**
    * Entfernt nach Beendigung des Bearbeitungsmodus die Kandidaten aus aufgabe[] und übernimmt
    * einige Verwaltungsaufgaben.
    */
   public void uebernehmeNeueAufgabe() {
      loesungsZellen = 0;
      for (int i = 0; i < 81; i++) {
         if ((aufgabe[i] & SZELLE) == SZELLE) {
            continue;
         }
         if (aufgabe[i] > 9) {// also Kandidaten
            aufgabe[i] = 0;
            loesungsZellen++;
         } else {
            aufgabe[i] &= ZAHL;
         }
      }
      freieZellen = loesungsZellen;
      strApp.set_geaendert(false);
      initStradoku();
   }

   /**
    * makeStradokuString2Matrx übernimmt eine Stradoku-Aufgabe als String und steuert dessen
    * Auswertung als aktuelle Aufgabe.
    *
    * @param boardStr der auszuwertende String
    * @param eCheck Flag für Check auf Eindeutigkeit
    * @return true wenn Auswertung möglich war, ansonsten false
    */
   public boolean importStradokuString(String boardStr, boolean eCheck) {
      boolean erfolg = false;
      if (StradokuString2Matrix.makeStradokuString2Matrx(boardStr, aufgabe)) {
         aktStr = aufgabe;
         initStradoku();
         level = loeseStradoku(false, true);
         if (level <= 0) {
            String meldung = "";
            if (level == 0) {
               meldung = "<html>Diese Eingabe ist fehlerhaft.<br>"
                     + "Sie kann nicht oder nicht eindeutig gelöst werden.</html>";
            } else if (level < 0) {
               meldung = "<html>Diese Eingabe ist fehlerhaft.</html>";
            }
            JOptionPane.showMessageDialog(strApp,
                  meldung, "Hinweis", 1);
         }
         if (level < 0) {
            return false;
         }
         entferneMarkierungen();
         filterKnd = 0;
         strApp.resetNaviPosition();
         strApp.setLevel(level);
         strApp.set_usedKListe(false);
         strApp.set_usedNotizen(false);
         strApp.set_usedTestmod(false);
         strBoard.setSelect(40);
         strApp.set_geaendert(false);
         strApp.setTestModus(false);
         strApp.setTipps(0);
         erfolg = true;
      }
      return erfolg;
   }

   /**
    * Initialisiert für die Aufgabe in aufgabe[] das stradoku[] Array, trägt die Kandidaten nach und
    * setzt loesung[]
    *
    * @param rek Flag für rekursive Auswertung
    */
   private void initStradoku() {
      freieZellen = 0;
      verlaufsNdx = 0;
      strApp.setTipps(0);
      letzterVerlaufsNdx = verlaufsNdx;
      geloestNdx = 0;
      saveVerlaufsNdx = 0;
      gestartet = false;
      resetVerlauf();
      // alle Kandidaten setzen
      for (int i = 0; i < 81; i++) {
         if ((aufgabe[i] & LWF) != 0) {
            aufgabe[i] = 0;
         }
         if (aufgabe[i] == 0) {
            stradoku[i] = ALLEK;
            freieZellen++;
         } else {
            stradoku[i] = aufgabe[i];
         }
      }
      // für Vorgabe- und Sperrwerte Kandidaten entfernen 
      for (int i = 0; i < 81; i++) {
         if ((stradoku[i] & ~SZELLE) >= 1 && (stradoku[i] & ~SZELLE) <= 9) {
            bereinigeKandidaten(stradoku, stradoku[i], i, false);
         }
      }
      strApp.resetKandAnzeige(true);
      strApp.setStatusBarFehler(false);
      strApp.setNaviStatus(false, false, false, false, true);
      strApp.set_geaendert(false);
      strBoard.setSelect(40);
      loesungsZellen = freieZellen;
      aktStr = stradoku;
   }

   /**
    * Entfernt für eine Zelle alle durch einen gesetzten Wert unmöglichen Kandidaten aus allen
    * Zellen im Sichtbereich dieser Zelle.
    *
    * @param strfld zu bearbeitendes Stradokufeld
    * @param knd zu entfernender Kandidat
    * @param pos Index der Zelle, welcher der Wert zugewiesen wurde
    * @param folgeA Flag für Einbeziehung des Verlaufs
    */
   private void bereinigeKandidaten(
         int strfld[], int knd, int pos, boolean folgeA) {
      knd &= ~SZELLE;
      if (knd == 0) {
         return;
      }
      int z = (pos / 9) * 9;
      int s = pos % 9;
      int kndMode = strApp.getKndModus();
      int kmstatus = kndMode << KMF;
      // erst aus der Zeile entfernen
      for (int i = z; i <= z + 8; i++) {
         if (i == pos) {
            continue;
         }
         if ((strfld[i] & LK_MASKE[knd]) == LK_MASKE[knd]) {
            strfld[i] &= ~LK_MASKE[knd];
            strfld[i] -= 1;
            if (folgeA) {
               verlaufsNdx++;
               strVerlauf[verlaufsNdx] = i << ZI;
               strVerlauf[verlaufsNdx] |= LK_FOLGE;
               strVerlauf[verlaufsNdx] |= knd;
               strVerlauf[verlaufsNdx] |= kmstatus;
            }
         }
         if ((stradoku[i] & NK_MASKE[knd]) == NK_MASKE[knd]) {
            stradoku[i] &= ~NK_MASKE[knd];
         }
      }
      // jetzt aus der Spalte entfernen
      for (int i = s; i < s + 73; i += 9) {
         if (i == pos) {
            continue;
         }
         if ((strfld[i] & LK_MASKE[knd]) == LK_MASKE[knd]) {
            strfld[i] &= ~LK_MASKE[knd];
            strfld[i] -= 1;
            if (folgeA) {
               verlaufsNdx++;
               strVerlauf[verlaufsNdx] = i << ZI;
               strVerlauf[verlaufsNdx] |= LK_FOLGE;
               strVerlauf[verlaufsNdx] |= knd;
               strVerlauf[verlaufsNdx] |= kmstatus;
            }
         }
         if ((stradoku[i] & NK_MASKE[knd]) == NK_MASKE[knd]) {
            stradoku[i] &= ~NK_MASKE[knd];
         }
      }
   }

   /**
    * Setzt für ein neues Stradoku alle Parameter auf den Ausgangszustand.
    */
   public void clearStradoku() {
      for (int i = 0; i < 81; i++) {
         aufgabe[i] = 0;
         stradoku[i] = 0;
         loesung[i] = 0;
      }
   }

   /**
    * Initialisiert das Verlaufsarray und einige damit zusammenhängende Einstellungen.
    */
   private void initVerlauf() {
      for (int i = 0; i < verlaufSize; i++) {
         strVerlauf[i] = 0;
      }
      strVerlauf[0] |= 40 << ZI;
      verlaufsNdx = 0;
      geloestNdx = 0;
      saveVerlaufsNdx = 0;
   }

   /**
    * Erstellt die Kandidatenliste
    *
    * @param liste Referenz für akt. Stradoku Array
    */
   private void erstelleKndListe(int[] liste) {
      for (int j = 0; j < 81; j++) {
         if (liste[j] > 9 && liste[j] < SZELLE || liste[j] < 1) {
            liste[j] = ALLEK;
         }
      }
      for (int j = 0; j < 81; j++) {
         int knd = 0;
         if (liste[j] <= 9) {
            knd = liste[j];
         } else if (liste[j] > SZELLE) {
            knd = liste[j] - SZELLE;
         }
         if (knd > 0) {
            // erst aus der Zeile entfernen
            int z = (j / 9) * 9;
            for (int i = z; i <= z + 8; i++) {
               if ((liste[i] & LK_MASKE[knd]) == LK_MASKE[knd]) {
                  liste[i] &= ~LK_MASKE[knd];
                  liste[i] -= 1;
               }
            }
            // jetzt aus der Spalte entfernen
            int s = j % 9;
            for (int i = s; i < s + 73; i += 9) {
               if ((liste[i] & LK_MASKE[knd]) == LK_MASKE[knd]) {
                  liste[i] &= ~LK_MASKE[knd];
                  liste[i] -= 1;
               }
            }
         }
      }
   }

   /**
    * Setzt oder entfernt das Flag für eine Sperrzelle.
    *
    * @param i indiziert die betroffene Zelle.
    */
   public void setzeSperrzelle(int i) {
      if ((aufgabe[i] & SZELLE) == SZELLE) {  // Sperrzelle gesetzt?
         aufgabe[i] -= SZELLE;
         freieZellen++;
      } else {
         aufgabe[i] += SZELLE;
         freieZellen--;
      }
      strApp.setFehlerFreieZellen(freieZellen);
   }

   /**
    * Abfrage auf Sperrzelle
    *
    * @param i Index für abgefragte Zelle
    * @return true wenn Sperrzelle
    */
   public boolean isSperrzelle(int i) {
      return (aufgabe[i] & SZELLE) == SZELLE;
   }

   /**
    * Behandelt die Eingabe eines Wertes (Vorgabe-, Sperr- oder Lösungswert).
    *
    * @param i Index der Zelle, für die die Eingabe erfolgen soll
    * @param wrt Wert der Eingabe
    * @return Flag für gültige und ausgeführte Eingabe
    */
   public boolean setWert(int i, int wrt) {
      strApp.set_geaendert(true);
      strApp.labelHinweisfeld.setText("");
      if (loesungsModus) {
         if (wrt < 1) {
            return false;
         }
         if (((stradoku[i] & SZELLE) == SZELLE) || ((stradoku[i] & AKND) == 0)) {
            return false;
         }
         if (strApp.getTestModus()) {
            setTestWert(i, wrt);
            return true;
         }
         geloestNdx = 0;
         int kmstatus = strApp.getKndModus() << KMF;
         boolean kFehler = true;
         if (level == 0) {
            kFehler = isKandidatenfehler(i, wrt);
         }
         // für einen Neuanfang
         if (verlaufsNdx == 0 || !gestartet) {
            gestartet = true;
            setStart(0);
         }
         if (wrt == (loesung[i] & ZAHL) || !kFehler) {
            verlaufsNdx++;
            strVerlauf[verlaufsNdx] = i << ZI;          // Zellposition +
            strVerlauf[verlaufsNdx] |= LWFL;            // Lösungswertflag +
            strVerlauf[verlaufsNdx] |= kmstatus;        // Kandidatenstatus
            // Kandidaten aus Zuweisungszelle entfernen
            for (int k = 1; k <= 9; k++) {
               // für alle in Zuweisungszelle vertretenen Listenkandidaten
               if ((stradoku[i] & LK_MASKE[k]) == LK_MASKE[k]) {
                  verlaufsNdx++;
                  strVerlauf[verlaufsNdx] = i << ZI;  // Zellposition +
                  strVerlauf[verlaufsNdx] |= LKND;    // Knd-Listenflag +
                  strVerlauf[verlaufsNdx] |= k;       // Kandidat +
                  strVerlauf[verlaufsNdx] |= kmstatus;// Kandidatenstatus
               }
            }
            // Folgeausschlüsse für beide Kandidatenmodi
            bereinigeKandidaten(stradoku, wrt, i, true);
            // jetzt den Zuweisungswert selbst 
            verlaufsNdx++;
            strVerlauf[verlaufsNdx] = i << ZI;          // Zellposition +
            strVerlauf[verlaufsNdx] |= SETZEN;          // Setzen-Flag +
            strVerlauf[verlaufsNdx] |= wrt;             // Zuweisungswert +
            strVerlauf[verlaufsNdx] |= kmstatus;        // Kandidatenstatus
            letzterVerlaufsNdx = verlaufsNdx;
            stradoku[i] = wrt;
            stradoku[i] |= LWFL;
            freieZellen--;
            if (freieZellen >= 0) {
               strApp.setStatusBarHinweis(
                     ZELLNAME[i] + " die " + wrt + " zugewiesen", true);
               strApp.setNaviStatus(
                     true, true, false, false, true);
            } if(!geloest && freieZellen == 0){
               strVerlauf[verlaufsNdx] &= ~0xC00;  // Flags für KndModus entfernen
               geloestNdx = letzterVerlaufsNdx;              
               geloest = true;
               strApp.geloest();
            }
            return true;

         } else {
            JOptionPane.showMessageDialog(strApp,
                  "Dieser Zelle kann die " + wrt
                  + " nicht zugewiesen werden.",
                  "Hinweis", 2);
            strApp.setStatusBarFehler(true);
            return false;
         }
      } else {  // also Bearbeitungsmodus 
         if (wrt < 0) {
            return false;
         }
         if (aufgabe[i] < SZELLE) {
            if (wrt == 0) {
               freieZellen++;
            } else {
               freieZellen--;
            }
         }
         aufgabe[i] &= ~ZAHL;
         aufgabe[i] |= wrt;
         loesung[i] = aufgabe[i];
         strApp.setFehlerFreieZellen(freieZellen);
         return true;
      }
   }

   /**
    * Wertet für den Testmodus die Eingabe eines Wertes rekursiv aus.
    *
    * @param pos Index der Zelle, für die die Eingabe erfolgen soll
    * @param knd Wert der Eingabe
    */
   private void setTestWert(int pos, int knd) {
      stradoku[pos] &= ~ANKND;
      stradoku[pos] |= NK_MASKE[knd];
      int z = (pos / 9) * 9;
      int s = pos % 9;
      int single;
      
      // in Log-Datei eintragen
      strApp.setStatusBarHinweis(ZELLNAME[pos] + " die " + knd
            + " als LWert gesetzt", true);
      
      // betroffene Kandidaten aus der Zeile entfernen
      for (int i = z; i <= z + 8; i++) {
         if ((i != pos) && (stradoku[i] & NK_MASKE[knd]) == NK_MASKE[knd]) {
            stradoku[i] &= ~NK_MASKE[knd];
            single = checkNotKandSingle(i);
            if (single > 0) {
               setTestWert(i, single);
            } 
         }
      }
      // jetzt Kandidaten auch aus der Spalte entfernen    
      for (int i = s; i < s + 73; i += 9) {
         if ((i != pos) && (stradoku[i] & NK_MASKE[knd]) == NK_MASKE[knd]) {
            stradoku[i] &= ~NK_MASKE[knd];
            single = checkNotKandSingle(i);
            if (single > 0) {
               setTestWert(i, single);
            }         
         }
      }
   }

   /**
    * Setzt oder entfernt im Notizenmodus einen Kandidaten
    *
    * @param i Zelle, in der der Kandidat gesetzt oder entfernt werden soll
    * @param k der zu setzende oder zu entfernende Kandidat
    * @param isShift Flag für gedrückte Shift-Taste
    * @return true wenn Kandidat gesetzt oder entfernt werden konnte
    */
   public boolean notiereKandidat(int i, int k, boolean isShift) {
      strApp.setStatusBarHinweis("", false);
      strApp.set_usedNotizen(true);
      if (isKandidatenfehler(i, k)) {
         JOptionPane.showMessageDialog(strApp,
               "In dieser Zelle kann der Kandidat " + k
               + " nicht vertreten sein.",
               "Hinweis", 2);
         strApp.setStatusBarFehler(true);
         return false;
      }
      // für Neuanfang   
      if (verlaufsNdx == 0 || !gestartet) {
         gestartet = true;
         setStart(0);
      }
      if (strApp.getKndModus() != 2) {
         strApp.setNotizenMode(true);
      }
      // Kandidat bereits gesetzt entfernen
      if (isShift && (stradoku[i] & NK_MASKE[k]) == NK_MASKE[k]) {
         strApp.setStatusBarHinweis(ZELLNAME[i] + " die " + k
               + " als Notiz entfernt", true);         
         stradoku[i] &= ~NK_MASKE[k];
         if (strApp.getTestModus()) {
            int single = checkNotKandSingle(i);
            if (single > 0) {
               setTestWert(i, single);
            }
         }
      } // nicht gesetzt, dann jetzt
      else {
         stradoku[i] |= NK_MASKE[k];
         strApp.setStatusBarHinweis(ZELLNAME[i] + " die " + k
               + " als Notiz gesetzt", true);
      }
      strApp.set_geaendert(true);
      strBoard.repaint();
      return true;
   }

   /**
    * Kopiert im Notizenmodus alle Kandidaten aus der Kandidatenliste als Notizen in den
    * Notizenbereich.
    */
   public void copyKandidaten() {
      strBoard.requestFocusInWindow();
      if (strApp.getKndModus() == KNDMODNOTIZ) {
         for (int i = 0; i < 81; i++) {
            int tmp = stradoku[i] & AKND;
            if (tmp > LWF && tmp < SZELLE) {
               stradoku[i] &= ~ANKND;
               stradoku[i] |= (tmp << 9);
            }
         }
      }
      strApp.set_geaendert(true);
      strApp.set_usedKListe(true);
      if (strApp.getTestModus()) {
         strApp.setStatusBarHinweis(
            "Alle Knd aus KListe übernommen", true);
      } 
      strBoard.repaint();
   }

   /**
    * Entfernt aus einer Zelle akke Notizen;
    *
    * @param pos Zelle aus der Notizen entfernt werden sollen
    */
   public void entferneZellNotizen(int pos) {
      stradoku[pos] &= ~ANKND;
      strBoard.repaint();
   }

   /**
    * Prüft ob in einem der beiden KndModi ein Kandidat noch vertreten ist
    *
    * @param k der zu prüfende Kandidat
    * @param kmod der Kandidatenmodus für den geprüft werden soll
    * @return true, wenn Kandidat vertreten, sonst false
    */
   public boolean lifeKandidat(int k, int kmod) {
      boolean life = false;
      for (int i = 0; i < 81; i++) {
         int tmp = stradoku[i] & AKND;
         if (tmp <= LWF || tmp >= SZELLE) {
            continue;
         }
         if (kmod == 2) {
            if ((stradoku[i] & (1 << k + 13)) > 0) {
               life = true;
               break;
            }
         } else {
            if ((stradoku[i] & (1 << k + 4)) > 0) {
               life = true;
               break;
            }
         }
      }
      return life;
   }

   /**
    * Für die Kandidaten beider KndListen wird ein Filter gesetzt oder entfernt. Die Eintragung und
    * Auswertung des Filters erfolgt über die Variable filterKnd. Dabei wird unterschieden, ob der
    * Filter für die Kandidatenliste oder für notierte Kandidaten gelten soll.
    *
    * @param kf zu filternder Kandidat
    */
   public void setKndFilter(int kf) {
      if (kf == filterKnd) {
         filterKnd = 0;
      } else {
         filterKnd = kf;
      }
      strApp.markKndTaste(filterKnd);
      strApp.set_usedKndFi(true);
      int kndMode = strApp.getKndModus();
      if (kndMode == 0) {
         return;
      }
      strBoard.requestFocusInWindow();
      if (filterKnd > 0) {
         if (lifeKandidat(kf, kndMode)) {
            strApp.setStatusBarHinweis("Filter für den Kandidaten "
                  + filterKnd + " gesetzt", false);
         } else {
            strApp.setStatusBarHinweis("Kein Filter gesetzt, "
                  + "Kandidat " + filterKnd + " nicht vertreten", false);
         }
      } else {
         strApp.setStatusBarHinweis("Kein Filter gesetzt", false);
      }
      strBoard.repaint();
   }

   /**
    * Setzt gespeicherte Filtereinstellung
    *
    * @param kf gefilteter Kandidat
    */
   public void setFilterKnd(int kf) {
      filterKnd = kf;
      strApp.markKndTaste(filterKnd);
   }

   /**
    * Gibt die aktuell gesetzten Kandidatenfilter zurück.
    *
    * @return gefilteter Kandidat
    */
   public int getFilterKnd() {
      return filterKnd;
   }

   /**
    * Entfernt alle Kandidaten aus den Notizen
    *
    * @param szInfo true wenn in Statuszeile Meldung ausgegeben werden soll Meldung
    */
   public void entferneNotizen(boolean szInfo) {
      for (int i = 0; i < 81; i++) {
         stradoku[i] &= ~ANKND;
      }
      if (szInfo) {
         strApp.setStatusBarHinweis("Alle Notizen entfernt", false);
      }
      strBoard.repaint();
   }

   /**
    * Ausschließen eines Kandidaten aus Kandidatenliste
    *
    * @param i Position in der Kandidatenliste
    * @param k auszuschließender Kandidat
    * @param info Flag ob Anzeige erfolgen soll
    * @return true wenn kein Fehler aufgetreten ist
    */
   public boolean entferneKandidat(int i, int k, boolean info) {
      if (info) {
         strApp.set_usedKListe(true);
         strApp.setStatusBarHinweis("", false);
         strApp.set_geaendert(true);
      }
      if (level > 0 && loesung[i] <= (LWF + k) && (loesung[i] & ZAHL) == k) {
         JOptionPane.showMessageDialog(strApp,
               "Der Kandidat " + k + " darf aus dieser "
               + "Zelle nicht entfernt werden.");
         strApp.setStatusBarFehler(true);
         return false;
      }
      if (((stradoku[i] & LK_MASKE[k]) == LK_MASKE[k])
            && ((stradoku[i] & ZAHL) > 1)) {
         int kmstatus = 0;
         kmstatus |= strApp.getKndModus() << KMF;
         stradoku[i] &= ~LK_MASKE[k];
         stradoku[i]--;
         strApp.setStatusBarHinweis(
               ZELLNAME[i] + " Kandidat " + k + " entfernt", true);
         // für einen Neuanfang
         if (verlaufsNdx == 0 || !gestartet) {
            gestartet = true;
            setStart(0);
         }
         verlaufsNdx++;
         strVerlauf[verlaufsNdx] = i << ZI;
         strVerlauf[verlaufsNdx] |= ENTFERNEN;
         strVerlauf[verlaufsNdx] |= k;
         strVerlauf[verlaufsNdx] |= kmstatus;
         letzterVerlaufsNdx = verlaufsNdx;
         strApp.setNaviStatus(true,
               verlaufsNdx > 0, false, false, true);
         strBoard.repaint();
         return true;
      } else {
         return false;
      }
   }

   /**
    * Leitet die Erzeugung einer neuen Aufgabe ein.
    *
    * @param archiv Reverenz auf die Archiv-Klasse
    * @param lvl Level für zu generierendes Stradoku
    * @return level des erzeugten Stradoku
    */
   public int erzeugeStradoku(Archiv archiv, int lvl) {
      clearStradoku();
      initVerlauf();
      archiv.getAufgabe(aufgabe, lvl);
      strLoeser = new LevelSolver(this, aufgabe, loesung, false);
      level = strLoeser.loeseAufgabe();
      initStradoku();
      setFlagLeereZellenInLoesung();
      strApp.setStatusBarHinweis("Stradoku mit "
            + freieZellen + " freien Zellen generiert", false);
      geloest = false;
      strApp.set_geaendert(false);
      return level;
   }

   /**
    * Gibt den bitcodierten Wert für eine Zelle des Stradoku-Arrays zurück.
    *
    * @param i Index der Zelle, für die der Wert übergeben werden soll
    * @param lMod true für LösungsMode, sonst false
    * @return bitcodierter Zellwert
    */
   public int getZelle(int i, boolean lMod) {
      if (i >= 0) {
         if (lMod) {
            return aktStr[i];
         } else {
            return aufgabe[i];
         }
      }
      return -1;
   }

   /**
    * Wird von den Methoden bearbeiteSetzenButton() und bearbeiteEntferneKndButton() der Klasse
    * StradokuApp, sowie von der Methode formMousePressed() der Klasse StradokuBoard aufgerufen und
    * für den Eingabe-Check verwendet.
    *
    * @param i Index der betroffenen Zelle
    * @param w Wert der gesetzt oder Kandidat, der entfernt werden soll
    * @return true wenn w ein Lösungswert ist, ansonsten false
    */
   public boolean isLoesungsWert(int i, int w) {
      return ((loesung[i] & ZAHL) == w);
   }

   /**
    * Wird von den Methoden ladenStradoku() und speichernStradoku() der Klasse BinDateiEinAus
    * aufgerufen.
    *
    * @return Referenz auf das Array mit der aktuellen Aufgabe
    */
   @SuppressWarnings("ReturnOfCollectionOrArrayField")
   public int[] getAufgabe() {
      return aufgabe;
   }

   /**
    * Wird von den Methoden ladenStradoku() und speichernStradoku() der Klasse BinDateiEinAus
    * aufgerufen.
    *
    * @return Referenz auf das Array mit dem aktuell angezeigten Lösungsstatus
    */
   @SuppressWarnings("ReturnOfCollectionOrArrayField")
   public int[] getAktStradoku() {
      return stradoku;
   }

   /**
    * Wird von den Methoden ladenStradoku() und speichernStradoku() der Klasse BinDateiEinAus
    * aufgerufen.
    *
    * @return Referenz auf das Array mit der Lösung der aktuellen Aufgabe
    */
   @SuppressWarnings("ReturnOfCollectionOrArrayField")
   public int[] getLoesung() {
      return loesung;
   }

   /**
    * Gibt die aktuelle Aufgabe als String zurück
    *
    * @return Aufgabe
    */
   public String getStringAufgabe() {
      MakeStradokuString mafg = new MakeStradokuString(this);
      return mafg.getStradokuString(true);
   }

   /**
    * Übergibt den Lösungsstring
    *
    * @return Lösung
    */
   public String getStringLoesung() {
      String loesungString = "";
      for (int i = 0; i < 81; i++) {
         if ((loesung[i] & SZELLE) == SZELLE) {
            if ((loesung[i] & ZAHL) == 0) {
               loesungString += "s";
            } else {
               loesungString += (char) (96 + (loesung[i] & ZAHL));
            }
         } else {
            loesungString += loesung[i] & ~LWFL;
         }
      }
      return loesungString;
   }

   /**
    * Übergibt den Level des aktuellen Stradoku
    *
    * @return Level
    */
   public int getLevel() {
      return level;
   }

   /**
    * Setzt für das aktuelle Stradoku den Level
    *
    * @param lv zu setzender Level
    */
   public void setLevel(int lv) {
      level = lv;
   }

   /**
    * @param anzahl aus der Sicherungsdatei gelesene Anzahl von Vorgabewerten
    */
   public void setLoesungsZellen(int anzahl) {
      loesungsZellen = anzahl;
   }

   /**
    * Abfrage der Lösungszellen einer Stradoku Aufgabe
    *
    * @return Anzahl insgesamt zu lösenden Zellen
    */
   public int getLoesungsZellen() {
      int lz = 0;
      for (int i = 0; i < 81; i++) {
         if (aufgabe[i] == 0) {
            lz++;
         }
      }
      return lz;
   }

   /**
    * Setzt Anzahl der aktuell freien Zellen
    *
    * @param anzahl Anzahl der noch ungeloesten Zellen
    */
   public void setFreiZellen(int anzahl) {
      freieZellen = anzahl;
   }

   /**
    * Abfrage der aktuell freien Zellen einer Stradoku Aufgabe
    *
    * @param loem Flag für die Abfrage, true wenn für stradoku[], false für aufgabe[]
    * @return Anzahl der noch ungelösten Zellen
    */
   public int getFreiZellen(Boolean loem) {
      if (loem) {
         return freieZellen;
      } else {
         int freizellen = 0;
         for (int i = 0; i <= 80; i++) {
            if (aufgabe[i] == 0) {
               freizellen++;
            }
         }
         return freizellen;
      }
   }

   /**
    * Wird von der Methode speichernStradoku() in der Klasse BinDateiEinAus aufgerufen.
    *
    * @return Referenz auf Verlaufs-Array
    */
   @SuppressWarnings("ReturnOfCollectionOrArrayField")
   public int[] getVerlauf() {
      return strVerlauf;
   }

   /**
    * Abfrage des aktuellen Verlaufs Index
    *
    * @return aktueller Index für Verlaufs-Array
    */
   public int getVerlaufsNdx() {
      return verlaufsNdx;
   }

   /**
    * Abfrage nach aktuell letzten Verlaufs Index
    *
    * @return letzer Index im Verlaufs-Array
    */
   public int getLetzterVerlaufsNdx() {
      return letzterVerlaufsNdx;
   }

   /**
    * Liefert eine gespeichert Navigationsposition.
    *
    * @return Positionsindex
    */
   public int getSavePosNdx() {
      return saveVerlaufsNdx;
   }

   /**
    * Übernimmt eine gespeichert Navigationsposition.
    *
    * @param ndx Zu übernehmende Position
    */
   public void setSavePosNdx(int ndx) {
      saveVerlaufsNdx = ndx;
      strApp.setNaviPosition(ndx);
   }

   /**
    * Wird von der Methode ladenStradoku() in der Klasse BinDateiEinAus aufgerufen.
    *
    * @param glst aus der Sicherungsdatei gelesener Wert für Verlaufsindex der gelösten Aufgabe
    */
   public void setGeloestNdx(int glst) {
      geloestNdx = glst;
      if (geloestNdx == 9999) {
         aktStr = loesung;
      }
   }

   /**
    * Wird von der Methode speichernStradoku() in der Klasse BinDateiEinAus aufgerufen.
    *
    * @return letzter Index für gelöste Aufgabe im Verlaufs-Array
    */
   public int getGeloestNdx() {
      return geloestNdx;
   }

   /**
    * Wird von der Methode ladenStradoku() in der Klasse BinDateiEinAus aufgerufen und hat die
    * Aufgabe, aus dem aktuellen Stradoku-Array das Array aufgbe[] zu erstellen.
    */
   public void setAufgabe() {
      for (int i = 0; i < 81; i++) {
         if ((stradoku[i] & SZELLE) == SZELLE) {
            aufgabe[i] = stradoku[i];
         } else if ((stradoku[i] & LWFL) == LWFL) {
            aufgabe[i] = 0;
         } else {
            aufgabe[i] = stradoku[i] & ZAHL;
         }
      }
   }

   /**
    * Übernimt Stradoku und kopiert es in das stradoku Array
    *
    * @param str aus Sicherungsdatei gelesenes Array
    */
   public void setStradoku(int[] str) {
      System.arraycopy(str, 0, stradoku, 0, 81);
      aktStr = stradoku;
   }

   /**
    * Übernimt Stradoku Lösung und kopiert sie in das loesung Array
    *
    * @param lng aus der Sicherungsdatei gelesenes Array mit der Lösung
    */
   public void setLoesung(int[] lng) {
      System.arraycopy(lng, 0, loesung, 0, 81);
   }

   /**
    * Setzt aktStr[] auf loesung[]
    */
   public void showLoesung() {
      aktStr = loesung;
   }

   /**
    * Übernimt gespeicherten Verlauf als aktuellen Verlauf
    *
    * @param vrlf aus der Sicherungsdatei gelesenes Verlaufs-Array
    */
   public void setVerlauf(int[] vrlf) {
      System.arraycopy(vrlf, 0, strVerlauf, 0, verlaufSize);
   }

   /**
    * Abfrage nach der Verlaufsgröße
    *
    * @return Verlaufsgröße
    */
   public int getVerlaufSize() {
      return strVerlauf.length;
   }

   /**
    * Setzt die Verlaufsgröße.
    *
    * @param len neue Verlaufsgröße
    */
   public void setVerlaufSize(int len) {
      verlaufSize = len;
   }

   /**
    * Setzt Verlaufsindex
    *
    * @param ndx aus der Sicherungsdatei gelesener Index für Verlaufsarray
    */
   public void setVerlaufsNdx(int ndx) {
      verlaufsNdx = ndx;
   }

   /**
    * Setzt letzten Verlaufsindex
    *
    * @param lndx aus der Sicherungsdatei gelesener Index für die letzte Position im Verlaufsarray
    */
   public void setLetzterVerlaufsNdx(int lndx) {
      letzterVerlaufsNdx = lndx;
   }

   /**
    * Ermöglicht den Wechsel zwischen Lösungs- und Bearbeitungsmodus
    *
    * @param modus Flag für den zu setzenden Lösungs-Modus
    */
   public void setLoesungsModus(boolean modus) {
      loesungsModus = modus;
   }

   /**
    * Lösungsstatus setzen
    *
    * @param glst true wenn gelöst
    */
   public void setGeloest(boolean glst) {
      geloest = glst;
   }

   /**
    * Setzt das Bit-Flag für die ursprünglich leeren Zellen in der Lösung einer Aufgabe.
    */
   private void setFlagLeereZellenInLoesung() {
      for (int i = 0; i < 81; i++) {
         if (aufgabe[i] == 0) {
            loesung[i] |= LWFL;
         }
      }
   }

   /**
    * Wird nach Betätigung einer der drei Navigationstasten 'Start', 'Aktuell' oder 'Lösung' sowie
    * nach einem Klick auf die Positionstaste aufgerufen.
    *
    * @param ziel betätigte Taste: 1 = Start, 2 = aktuelle Position, 4 = Lösung
    */
   public void gotoNaviPosition(int ziel) {
      if (strApp.getTestModus()) {
         return;
      }
      filterKnd = 0;
      aktStr = stradoku;
      setGeloestNdx(0);
//      entferneNotizen(true);
      switch (ziel) {
         case 1: // Start    
            entferneMarkierungen();
            strApp.markKndTaste(0);
            while (verlaufsNdx > 0) {
               schrittZurueck(false);
            }
            strApp.setNaviStatus(false, false,
                  letzterVerlaufsNdx > 0,
                  letzterVerlaufsNdx > 0,
                  true);
            strApp.setStatusBarHinweis(
                  "Ausgangsposition - ungelöste Zellen: " + freieZellen
                  + " (von " + loesungsZellen + ")", false);
            setNaviKandidatenMode(false);
            strApp.resetKandAnzeige(true);
            break;
         case 2: // aktuelle Lösungsposition
            if (letzterVerlaufsNdx != 0) {
               while (verlaufsNdx < letzterVerlaufsNdx) {
                  schrittWiederholen(false);
               }
            }
            strApp.setNaviStatus(verlaufsNdx > 0,
                  verlaufsNdx > 0,
                  false,
                  false,
                  geloestNdx < LOESUNG_NDX);
            strApp.setStatusBarHinweis("Aktuelle Lösungsposition:  "
                  + getFreiZellen(true) + " ungelöste Zellen "
                  + " (von " + getLoesungsZellen() + ")", false);
            setNaviKandidatenMode(false);
            setSelection(false);
            break;
         case 3: // Gespeicherte Lösungsposition
            int pos = saveVerlaufsNdx;
            if (pos == 0) {
               pos = letzterVerlaufsNdx;
            }
            if (strApp.getNaviPosition() == geloestNdx) {
               while (verlaufsNdx > 0) {
                  schrittZurueck(false);
               }
            }
            if (pos > verlaufsNdx) {
               while (verlaufsNdx < pos) {
                  schrittWiederholen(false);
               }
               strApp.setNaviStatus(verlaufsNdx > 0, // zurück zu Start
                     verlaufsNdx > 0,
                     letzterVerlaufsNdx > pos,
                     letzterVerlaufsNdx > pos,
                     verlaufsNdx < LOESUNG_NDX);
               strApp.setStatusBarHinweis("Gespeicherte Lösungsposition - "
                     + "Freie Zellen: " + freieZellen, false);
            } else if (pos < verlaufsNdx) {
               while (verlaufsNdx > pos) {
                  schrittZurueck(false);
               }
               strApp.setNaviStatus(verlaufsNdx > 0,
                     verlaufsNdx > 0,
                     letzterVerlaufsNdx > 0,
                     letzterVerlaufsNdx > 0,
                     verlaufsNdx < LOESUNG_NDX);
               strApp.setStatusBarHinweis(
                     "Gespeicherte Lösungsposition - Freie Zellen: " + freieZellen, false);
            }
            strBoard.setSelect((strVerlauf[verlaufsNdx] & NDX_MASK) >>> ZI);
            setGeloestNdx(verlaufsNdx);
            setNaviKandidatenMode(false);
            break;
         case 4: // Lösung zeigen
            entferneMarkierungen();
            strApp.markKndTaste(0);
            aktStr = loesung;
            setGeloestNdx(LOESUNG_NDX);
            strApp.setNaviStatus(true,
                  false,
                  false,
                  verlaufsNdx > 0,
                  false);
            strBoard.setKandidatenModus(0);
            strApp.setNaviPosition(LOESUNG_NDX);
            strApp.setStatusBarHinweis("Lösung", false);
            strApp.repaint();
            break;
      }
   }

   /**
    * Setzt den Kandidatenmodus für den Verlauf
    */
   private void setNaviKandidatenMode(boolean anzeige) {
      int kmod = (strVerlauf[verlaufsNdx] & KMODE) >> KMF;
      if (strApp.getKndModus() != kmod) {
         strApp.setKndModus(kmod, anzeige);
      }
   }

   /**
    * Wird bei Betätigung der Navigtionstaste 'Schritt zurück' oder bei 'zurück zur Ausgangsstelung'
    * für jeden Schritt aufgerufen
    *
    * @param anzeige Flag ob Schritt angezeigt werden soll
    */
   public void schrittZurueck(boolean anzeige) {
      if (strApp.getTestModus()) {
         return;
      }
      if (anzeige && geloest) {
         strApp.labelHinweisfeld.setText("");
      }
      aktStr = stradoku;
      strApp.markKndTaste(0);
      // testhalber für vor Version 4.1 gespeicherte Stradokus
      if ((strVerlauf[verlaufsNdx] & NUR_KL) == NUR_KL) {
         verlaufsNdx--;
         return;
      }
      setGeloestNdx(0);
      int kndmode = strVerlauf[verlaufsNdx] & KMODE;
      int lwert = strVerlauf[verlaufsNdx] & ZAHL;
      int i = (strVerlauf[verlaufsNdx] & NDX_MASK) >>> ZI;
      // Wertzuweisung für eine Zelle zurücknehmen
      if ((strVerlauf[verlaufsNdx] & SETZEN) == SETZEN) {
         int kw;
         // Wert selbst entfernen
         stradoku[i] -= lwert;
         verlaufsNdx--;
         int pos = i;
         // Folgeausschlüsse in Kandidatenliste rückgängig machen
         while ((strVerlauf[verlaufsNdx] & LK_FOLGE) == LK_FOLGE) {
            if ((strVerlauf[verlaufsNdx] & LK_FOLGE) == LK_FOLGE) {
               kw = strVerlauf[verlaufsNdx] & ZAHL;
               i = (strVerlauf[verlaufsNdx] & NDX_MASK) >>> ZI;
               stradoku[i] |= 1 << (kw + 4);
               stradoku[i]++;
               verlaufsNdx--;
            }
         }
         // Listenkandidaten für Zuweisungszelle wieder zurücksetzen
         while ((strVerlauf[verlaufsNdx] & LKND) == LKND) {
            if ((strVerlauf[verlaufsNdx] & LKND) == LKND) {
               i = (strVerlauf[verlaufsNdx] & NDX_MASK) >>> ZI;
               kw = strVerlauf[verlaufsNdx] & ZAHL;
               int knd = 1 << (kw + 4);
               if ((stradoku[i] & knd) != knd) {
                  stradoku[i] |= knd;
                  stradoku[i]++;
               }
               verlaufsNdx--;
            }
         }
         freieZellen++;
         if ((strVerlauf[verlaufsNdx] & LWFL) == LWFL) {
            verlaufsNdx--;
         }
         if (anzeige) {
            String meldung = " Zelle " + ZELLNAME[pos]
                  + " Lösungswert " + lwert + " entfernt.";
            if ((strVerlauf[verlaufsNdx] & KMODE) != kndmode) {
               if ((strVerlauf[verlaufsNdx] & KMODE) == KLISTE) {
                  strApp.setStatusBarHinweis(
                        "Kandidatenliste aktiviert und in " + meldung, false);
               } else if ((strVerlauf[verlaufsNdx] & KMODE) == NOTIZEN) {
                  strApp.setStatusBarHinweis(
                        "Notizenmodus aktiviert und in " + meldung, false);
               } else if ((strVerlauf[verlaufsNdx] & KMODE) == 0) {
                  strApp.setStatusBarHinweis(
                        "Kandidatenanzeige beendet und in " + meldung, false);
               }
            } else {
               strApp.setStatusBarHinweis("In" + meldung, false);
            }
         }
      } else if ((strVerlauf[verlaufsNdx] & ENTFERNEN) == ENTFERNEN) {
         stradoku[i] |= 1 << (lwert + 4);
         stradoku[i]++;
         verlaufsNdx--;
         if (anzeige) {
            String meldung = "";
            if ((strVerlauf[verlaufsNdx] & KMODE) != kndmode) {
               if ((strVerlauf[verlaufsNdx] & KMODE) == KLISTE) {
                  meldung = "Kandidatenliste aktiviert und in ";
               } else if ((strVerlauf[verlaufsNdx] & KMODE) == NOTIZEN) {
                  meldung = "Notizenmodus aktiviert und in ";
               } else if ((strVerlauf[verlaufsNdx] & KMODE) == 0) {
                  meldung = "Kandidatenanzeige beendet und in ";
               }
            }
            strApp.setStatusBarHinweis(meldung + ZELLNAME[i]
                  + " Kandidat " + lwert + " wieder gesetzt.", false);
         }
      }
      if (anzeige) {
         setNaviKandidatenMode(false);
         aktNaviAnzeige(true);
      }
   }

   /**
    * Wird nach Betätigung der Navigtionstaste 'zurückgenommenen Schritt wieder ausführen'
    * aufgerufen.
    *
    * @param anzeige Flag ob Schritt angezeigt werden soll
    */
   public void schrittWiederholen(boolean anzeige) {
      if (strApp.getTestModus()) {
         return;
      }
      aktStr = stradoku;
      strApp.markKndTaste(0);
      int kw;
      int i;
      int pos;
      int kndmode = strVerlauf[verlaufsNdx] & KMODE;
      entferneNotizen(anzeige);
      setGeloestNdx(0);
      verlaufsNdx++;
      // testhalber für vor Version 4.1 gespeicherte Stradokus
      if ((strVerlauf[verlaufsNdx] & NUR_KL) == NUR_KL) {
         return;
      }
      // entfernten Lösungswert wieder einsetzen
      if ((strVerlauf[verlaufsNdx] & LWFL) == LWFL) {
         boolean isZellSingle = true;
         pos = (strVerlauf[verlaufsNdx] & NDX_MASK) >>> ZI;

         verlaufsNdx++;
         // in der Zuweisungszelle vorhandene Listen-Kandidaten entfernen
         while ((strVerlauf[verlaufsNdx] & LKND) == LKND) {
            kw = strVerlauf[verlaufsNdx] & ZAHL;
            i = (strVerlauf[verlaufsNdx] & NDX_MASK) >>> ZI;
            stradoku[i] &= ~(LK_MASKE[kw]);
            stradoku[i]--;
            verlaufsNdx++;
         }
         // Folgeausschlüsse von Listenkandidaten wieder herstellen
         while ((strVerlauf[verlaufsNdx] & LK_FOLGE) == LK_FOLGE) {
            kw = strVerlauf[verlaufsNdx] & ZAHL;
            i = (strVerlauf[verlaufsNdx] & NDX_MASK) >>> ZI;
            stradoku[i] &= ~(LK_MASKE[kw]);
            stradoku[i]--;
            verlaufsNdx++;
         }
         kw = strVerlauf[verlaufsNdx] & ZAHL;
         if (isZellSingle) {
            i = pos;
         } else {
            i = (strVerlauf[verlaufsNdx] & NDX_MASK) >>> ZI;
         }
         // Wert selbst wieder setzen
         stradoku[i] = kw;
         stradoku[i] |= LWFL;
         freieZellen--;
         if (anzeige) {
            String meldung = ZELLNAME[pos]
                  + " Lösungswert " + kw + " gesetzt.";
            if ((strVerlauf[verlaufsNdx] & KMODE) != kndmode) {
               if ((strVerlauf[verlaufsNdx] & KMODE) == KLISTE) {
                  strApp.setStatusBarHinweis(
                        "Kandidatenliste aktiviert und in" + meldung, false);
               } else if ((strVerlauf[verlaufsNdx] & KMODE) == NOTIZEN) {
                  strApp.setStatusBarHinweis(
                        "Notizenmodus aktiviert und in" + meldung, false);
               } else if ((strVerlauf[verlaufsNdx] & KMODE) == 0) {
                  strApp.setStatusBarHinweis(
                        "Kandidatenanzeige beendet und in" + meldung, false);
               }
            } else {
               strApp.setStatusBarHinweis(meldung, false);
            }
         }
      } // entfernte bzw. gesetzte Kandidaten behandeln
      else if ((strVerlauf[verlaufsNdx] & ENTFERNEN) == ENTFERNEN) {
         kw = strVerlauf[verlaufsNdx] & ZAHL;
         i = (strVerlauf[verlaufsNdx] & NDX_MASK) >>> ZI;
         stradoku[i] &= ~(LK_MASKE[kw]);
         stradoku[i]--;
         if (anzeige) {
            String meldung = "";
            if ((strVerlauf[verlaufsNdx] & KMODE) != kndmode) {
               if ((strVerlauf[verlaufsNdx] & KMODE) == KLISTE) {
                  meldung = "Kandidatenliste aktiviert und in ";
               } else if ((strVerlauf[verlaufsNdx] & KMODE) == NOTIZEN) {
                  meldung = "Notizenmodus aktiviert und in ";
               } else if ((strVerlauf[verlaufsNdx] & KMODE) == 0) {
                  meldung = "Kandidatenanzeige beendet und in ";
               }
            }
            String msg = ZELLNAME[i]
                  + " Kandidat " + kw + " entfernt.";
            strApp.setStatusBarHinweis(meldung + msg, false);
         }
      }
      if (anzeige) {
         setNaviKandidatenMode(false);
         aktNaviAnzeige(false);
      }
   }

   /**
    * Aktualisiert für den Verlauf die diversen Anzeigen
    *
    * @param back Flag für Rücknavigation
    */
   private void aktNaviAnzeige(boolean back) {
      if (back) {
         strApp.setNaviStatus(verlaufsNdx > 0,
               verlaufsNdx > 0,
               verlaufsNdx < letzterVerlaufsNdx,
               verlaufsNdx < letzterVerlaufsNdx, true);
         if (verlaufsNdx == 0) {
            strApp.resetKandAnzeige(true);
            strApp.set_usedKListe(false);
            strApp.set_usedNotizen(false);
            strApp.set_usedTestmod(false);
         } else {
            setNaviKandidatenMode(false);
         }
         setSelection(true);
      } else {
         strApp.setNaviStatus(true, verlaufsNdx > 0,
               verlaufsNdx < letzterVerlaufsNdx,
               verlaufsNdx < letzterVerlaufsNdx,
               (freieZellen) > 0);
         setSelection(false);
      }
      strApp.repaint();
      entferneMarkierungen();
      filterKnd = 0;
   }

   void setStart(int kmstatus) {
      geloest = false;
      if (verlaufsNdx == 0 || !gestartet) {
         strApp.setStartZeit(0);
         gestartet = true;
      }
      strApp.setFehlerFreieZellen(0);
      strApp.resetPosSicherung();
      strApp.resetFehler();
      strApp.rsetTipps();
      if (kmstatus == 0) {
         strApp.set_usedNotizen(false);
         strApp.set_usedKListe(false);
         strApp.set_usedTestmod(false);
      }
   }

   /**
    * Übernimmt die Verwaltungsaufgaben für die Kandidatenanzeige im Zusammenhang mit einer
    * Verlaufs-Bewegung.
    *
    * @param zurueck Flag, welches die Navigationsrichtung anzeigt
    */
   private void setSelection(boolean zurueck) {
      int pos = verlaufsNdx;
      if (zurueck) {
         pos++;
      }
      strBoard.setSelect((strVerlauf[pos] & NDX_MASK) >>> ZI);
   }

   /**
    * Prüft, ob eine bestimmten Zelle mit einem bestimmten Wert als Vorgabe- Lösungs- oder Sperrwert
    * belegt ist.
    *
    * @param i Index der zu prüfenden Zelle
    * @param w Wert, auf den die Zelle zu prüfen ist
    * @return true, wenn die Zelle i mit dem Wert w belegt ist, sonst false
    */
   public boolean isWert(int i, int w) {
      if (loesungsModus) {
         return (stradoku[i] == w);
      } else {
         return ((aufgabe[i] & ~SZELLE) == w);
      }
   }

   /**
    * Prüft ob in einer Zelle ein bestimmter Kandidat vertreten ist.
    *
    * @param i Index der zu prüfenden Zelle
    * @param k der zu prüfende Kandidat
    * @param fmod Modus, für den die Abfrage gilt
    * @return true wenn der Kandidat vertreten ist, sonst false
    */
   public boolean isKandidat(int i, int k, int fmod) {
      if (fmod == 1) {
         return (stradoku[i] & LK_MASKE[k]) == LK_MASKE[k];
      } else if (fmod == 2) {
         return (aufgabe[i] & NK_MASKE[k]) == NK_MASKE[k];
      }
      return false;
   }

   /**
    * Abfrage nach ungelöster Zelle
    *
    * @param i Index der abzufragenden Zelle
    * @return true wenn die Zelle ungelöst ist
    */
   public boolean isUngeloest(int i) {
      return (stradoku[i] >= MINK && stradoku[i] <= MAXK);
   }

   /**
    * Setzt Markierungen.
    *
    * @param ndx Zellindex
    * @param farbe zu setzende Farbe
    * @param set Flag für das Setzen der Markierung
    * @return Anzahl der markierten Zellen
    */
   public int setMarkierung(int ndx, int farbe, boolean set) {
      int gesetzt = 0;
      if (strApp.zeigtLoesung()) {
         loesung[ndx] &= ~IS_MARK;                               // entfernen
         if ((loesung[ndx] != SZELLE) && farbe > 0) {
            loesung[ndx] |= farbe;                              // neu setzen
            gesetzt++;
         }
      } else {
         stradoku[ndx] &= ~IS_MARK;
         if ((stradoku[ndx] != SZELLE) && farbe > 0) {
            stradoku[ndx] |= farbe;
            gesetzt++;
         }
      }
      return gesetzt;
   }

   /**
    * Entfernt alle gestzten Markierungen.
    */
   public void entferneMarkierungen() {
      for (int i = 0; i < 81; i++) {
         stradoku[i] &= ~IS_MARK;
         aufgabe[i] &= ~IS_MARK;             // nur noch für ältere gespeicherte Markierungen
         loesung[i] &= ~IS_MARK;
      }
      strBoard.repaint();
   }

   /**
    * Abfrage ob das aktuelle Stradoku gelöst ist.
    *
    * @return true wenn gelöst, sonst false
    */
   public boolean getGeloest() {
      return geloest;
   }

   /**
    * Überprüft, ob ein Kandidat ohne Konflickt mit bereits vorhandenen Vorgabe-, Sperr- oder
    * Lösungswerten notiert werden kann.
    *
    * @param pos Zelle, in welcher der Kandidat gesetzt werden soll
    * @param knd Kandidat, der gesetzt werden soll
    * @return true wenn der Kandidat gesetzt werden kann, sonst false
    */
   private boolean isKandidatenfehler(int pos, int knd) {
      int wrt = stradoku[pos];
      if (((wrt & SZELLE) == SZELLE)
            || // Sperrzelle
            (wrt < ZAHL)
            || // Vorgabewert
            ((wrt & LWF) == LWF && (wrt & AKND) == 0)) {    // Lösungswert
         return true;
      }
      int z = (pos / 9) * 9;
      for (int i = z; i <= z + 8; i++) {
         wrt = stradoku[i];
         if ((wrt & ZAHL) == knd) {
            if ((wrt & SZELLE) == SZELLE
                  || // Sperrzelle
                  wrt < ZAHL
                  || // Vorgabewert
                  ((wrt & LWF) == LWF && (wrt & AKND) == 0)) { // Lösungswert
               return true;
            }
         }
      }
      int s = pos % 9;
      for (int i = s; i < s + 73; i += 9) {
         wrt = stradoku[i];
         if ((wrt & ZAHL) == knd) {
            if ((wrt & SZELLE) == SZELLE
                  || // Sperrzelle
                  wrt < ZAHL
                  || // Vorgabewert
                  ((wrt & LWF) == LWF && (wrt & AKND) == 0)) { // Lösungswert
               return true;
            }
         }
      }
      return false;
   }

   /**
    * Überträgt entweder das aktuelle Stradoku oder die Lösung nach aufgabe[]
    *
    * @param lng true wenn Lösung übertragen werden soll
    */
   public void stradoku2aufgabe(boolean lng) {
      if (lng) {
         for (int i = 0; i < 81; i++) {
            if ((stradoku[i] & KND_LWF) == LWF) {
               aufgabe[i] = stradoku[i] & ZAHL;
            }
         }
      } else {
         for (int i = 0; i < 81; i++) {
            if ((loesung[i] & KND_LWF) == LWF) {
               aufgabe[i] = loesung[i] & ZAHL;
            }
         }
      }
      strBoard.repaint();
   }

   /**
    * Prüft Anzahl der vorhandenen Sperrzellen
    *
    * @return Anzahl der Sperrwerte
    */
   private int getSperrzellen() {
      int sw = 0;
      for (int i = 0; i < 81; i++) {
         if ((aufgabe[i] & SZELLE) == SZELLE) {
            sw++;
         }
      }
      return sw;
   }

   /**
    * Eindeutigkeitsabfrage
    *
    * @return true wenn aktuelles Stradoku eindeutig lösbar ist
    */
   public boolean isEindeutig() {
      if (level > 0) {
         eindeutig = true;
      }
      return eindeutig;
   }

   /**
    * Wird aufgerufen, wenn im Bearbeitungsmodus die Entf-Taste gedrückt wurde und entfernt Lösungs-
    * und Sperrwerte.
    *
    * @param all true, wenn gleichzeitig die Strg-Taste gedückt wurde. Es werden alle Werte und auch
    * die Sperrzelen entfernt.
    */
   public void entferneVorgaben(boolean all) {
      if (strApp.getEditModus() > 0) {
         if (all) {
            for (int i = 0; i < 81; i++) {
               aufgabe[i] = 0;
            }
            freieZellen = 81;
         } else {
            freieZellen = 0;
            for (int i = 0; i < 81; i++) {
               aufgabe[i] &= SZELLE;
               loesung[i] = aufgabe[i];
               if (aufgabe[i] == 0) {
                  freieZellen++;
               }
            }
         }
         strApp.setFehlerFreieZellen(freieZellen);
         strBoard.repaint();
      }
   }

   /**
    * Wird aufgerufen, wenn im Bearbeitungsmodus die Einfg-Taste gedrückt wurde und setzt die
    * Anzeige auf die Ausgangssituation zurück.
    */
   public void restoreVorgaben() {
      int mod = strApp.getEditModus();
      freieZellen = 0;
      if (mod > EDIT_NEU) {                       // bei Neu gibt es nichts zurückzusetzen
         if (mod == EDIT_AFG) {                    // Normalfall behandeln
            for (int i = 0; i < 81; i++) {
               if ((tmpStradoku[i] & LWF) == 0) {
                  aufgabe[i] = tmpStradoku[i] & ZAHL;
               } else {
                  aufgabe[i] = 0;
                  freieZellen++;
               }
               if ((tmpStradoku[i] & SZELLE) == SZELLE) {
                  aufgabe[i] = tmpStradoku[i];             //|= SZELLE;
                  loesung[i] = aufgabe[i];
               }
            }
         } else if (mod == EDIT_LOW) {           // mit Lösungswerten
            for (int i = 0; i < 81; i++) {
               if ((tmpStradoku[i] & LWF) == 0
                     || (tmpStradoku[i] & KND_LWF) == LWF) {
                  aufgabe[i] = tmpStradoku[i] & ZAHL;
               } else {
                  aufgabe[i] = 0;
                  freieZellen++;
               }
               if ((tmpStradoku[i] & SZELLE) == SZELLE) {
                  aufgabe[i] |= SZELLE;
                  loesung[i] = aufgabe[i];
               }
            }
         } else {                                // also die Lösung
            for (int i = 0; i < 81; i++) {
               aufgabe[i] = tmpStradoku[i] & ZAHL;
               if ((tmpStradoku[i] & SZELLE) == SZELLE) {
                  aufgabe[i] |= SZELLE;
                  loesung[i] = aufgabe[i];
               }
            }
         }
         strApp.setFehlerFreieZellen(freieZellen);
         strBoard.repaint();
      }
   }

   /**
    * Setzt das Verlaufsarray zurück.
    */
   public void resetVerlauf() {
      for (int i = 0; i < strVerlauf.length; i++) {
         strVerlauf[i] = 0;
      }
   }

   /**
    * Prüft, ob alle freien Zellen mit NotizenKandidaten gelöst sind
    *
    * @return true wenn in allen freien Zellen genau ein Notizenkandidat vertreten ist, der dem
    * Lösungswert entspricht.
    */
   public boolean isNotizenLoesung() {
      boolean erg = true;
      int lWert;
      for (int i = 0; i < 81; i++) {
         if ((stradoku[i] & LWF) == LWF && (stradoku[i] & AKND) > 0) {
            int tmp = stradoku[i] & ANKND;
            lWert = getKndSingle(tmp >> 14) | LWF;
            if (lWert != loesung[i]) {
               erg = false;
               break;
            }
         }
      }
      return erg;
   }

   /**
    * Ermittelt für eine Zelle ob ein Single vertreten ist
    *
    * @param pos Index der zu prüfenden Zelle
    * @return gefundener Single, sonst 0
    */
   int checkNotKandSingle(int pos) {
      int erg = 0;
      int count = 0;
      int nKnd = (stradoku[pos] & ANKND) >> 14;
      for (int i = 1; i <= 9; i++, nKnd >>= 1) {
         if ((nKnd & 1) == 1) {
            erg = i;
            count++;
         }
         if (count > 1) {
            erg = 0;
            break;
         }
      }
      return erg;
   }

   /**
    * ermittelt den Lösungswert für einen Kandidaten-Single
    *
    * @param knd Kandidaten-Bits einer Zelle nach rechts geschoben
    * @return wenn Single, dann Lösaungswert, sonst 0
    */
   public int getKndSingle(int knd) {
      int kSingle = 0;
      switch (knd) {
         case 0x01:
            kSingle = 1;
            break;
         case 0x02:
            kSingle = 2;
            break;
         case 0x04:
            kSingle = 3;
            break;
         case 0x08:
            kSingle = 4;
            break;
         case 0x10:
            kSingle = 5;
            break;
         case 0x20:
            kSingle = 6;
            break;
         case 0x40:
            kSingle = 7;
            break;
         case 0x80:
            kSingle = 8;
            break;
         case 0x100:
            kSingle = 9;
            break;
      }
      return kSingle;
   }

   /**
    * Sichert für den Bearbeitungsmodus das stradoku-Array
    *
    * @param loes true wenn die Lösung bearbeitet werden soll, sonst false
    */
   public void sikStradoku(boolean loes) {
      if (loes) {
         System.arraycopy(loesung, 0, tmpStradoku, 0, 81);
         freieZellen = 0;
      } else {
         System.arraycopy(stradoku, 0, tmpStradoku, 0, 81);
      }
      strApp.statusBarLevel.setText("");
   }

   /**
    * Setzt im Bearbeitungsmodus mögliche Sperrzellenwerte
    *
    * @param loe Flag für Bearbeitung der Lösung
    */
   public void setSperrZellenWerte(boolean loe) {
      // zuerst prüfen, ob eindeutig lösbar
      int lev = loeseStradoku(false, true);
      if (lev == 0) {
         strSperrwerte = new SetSperrWerte(this, aufgabe);
      } else if (level > 0) {
         strSperrwerte = new SetSperrWerte(this, loesung);
      }
      if (strSperrwerte.setSperrwerte()) {
         strApp.statusBarHinweis.setText("Mögliche Sperrwerte hinzugefügt.");
         strBoard.repaint();
      } else {
         strApp.statusBarHinweis.setText("Keine weiteren Sperrwerte möglich.");

      }
   }

   /**
    * Abfrage ob gerade die Lösung gezeigt wird
    *
    * @return true, wenn lösung gezeigt wird, sonst false
    */
   public boolean isLoesung() {
      return aktStr == loesung;
   }

   public boolean isLoesungswert(int pos, int knd) {
      return (loesung[pos] & ZAHL) == knd;
   }

   /**
    * Entfernt alle möglichen Werte so lange dabei der aktuelle Level erhalten bleibt.
    */
   public void setMaxSchwer() {
      if (loesungsModus || (level < 1)) {
         strApp.setStatusBarHinweis("Aktion kann mit dem Level " + level
               + " nicht durchgeführt werden.", false);
         return;
      } 
      int tmpWert;
      int fz = freieZellen;
      int counter = 0;
      int aktLevel = level;
      boolean arche = strApp.get_archivgeeignet();
      // erst Lösungswerte
      for (int n = 0; n < 81; n++) {
         int i = RandomNdx[n];
         tmpWert = aufgabe[i] & ZAHL;
         if (aufgabe[i] < SZELLE && tmpWert > 0) {
            // Wert der Zelle entfernen
            aufgabe[i] -= tmpWert;
            // Lösungsversuch
            aktStr = aufgabe;
            int tlevel = loeseStradoku(arche, true);
            // Versuch fehlgeschlagen - Änderung zurücknehmen 
            if (tlevel != aktLevel) {
               aufgabe[i] += tmpWert;
               level = aktLevel;
            } // Versuch erfolgreich - Zähler setzen
            else {
               counter++;
            }
         }
      }
      int efz = counter;
      fz += counter;
      counter = 0;
      freieZellen = fz;
      // jetzt Sperrwerte
      for (int n = 0; n < 81; n++) {
         int i = RandomNdx[n];
         tmpWert = aufgabe[i] & ZAHL;
         if (aufgabe[i] > SZELLE && tmpWert > 0) {
            // Wert der Zelle entfernen
            aufgabe[i] -= tmpWert;
            // Lösungsversuch
            aktStr = aufgabe;
            int tlevel = loeseStradoku(arche, true);
            // Versuch fehlgeschlagen - Ändrung zurücknehmen 
            if (tlevel != aktLevel) {
               aufgabe[i] += tmpWert;
               level = aktLevel;
            } // Versuch erfolgreich - Zähler setzen
            else {
               counter++;
            }
         }
      }
      String vorWerte;
      String speWerte;
      if (efz == 1) {
         vorWerte = efz + " Vorgabewert und ";
      } else {
         vorWerte = efz + " Vorgabewerte und ";
      }
      if (counter == 1) {
         speWerte = counter + " Sperrwert";
      } else {
         speWerte = counter + " Sperrwerte";
      }
      strApp.setStatusBarHinweis("Entfernt wurden: " + vorWerte + speWerte, false);
      strApp.statusBarFehler.setText("" + fz);
      strApp.statusBarLevel.setText("" + level);
      strBoard.requestFocus();
   }

   /**
    * setzt den Status "gestartet" für den Start des Lösungsvorgangs
    * @param g 
    */
   public void setGestartet(boolean g) {
      gestartet = g;
   }

   /**
    * übergibt den Status "gestartet" für den Start des Lösungsvorgangs
    * @return 
    */
   public boolean getGestartet() {
      return gestartet;
   }

   /**
    * initialisiert das IndexArray für einen Zufallsindex eines Stradoku
    */
   private void initRandomNdx() {
      int test[] = new int[81];
      for (int i = 0; i <= 80; i++) {
         RandomNdx[i] = -1;
         test[i] = -1;
      }
      int count = 0;
      while (count < 81) {
         int n = (int) (Math.random() * 81);
         if (RandomNdx[count] == -1 && test[n] == -1) {
            RandomNdx[count] = n;
            test[n] = 1;
            count++;
         }
      }
   }

}
