/**
 * LogLevelLoeser.java ist Teil des Programmes kodelasStradoku
 *
 * Erzeugt am:                  07.01.2022 18:00
 * Letzte Änderung:             06.09.2024 14:40
 *
 * Copyright (C) Konrad Demmel, 2019-2024
 */
package stradoku;

import java.io.IOException;

/**
 * Aufgabe: StrBereich-Aufgabe lösen, den Level festlegen und alle Einzelschritte der Lösung
 * dokumentieren.
 */
public class TippLoeser implements GlobaleObjekte {

   private static Stradoku strApp;
   private final int[][] strFeld;
   private final int[][] afgFeld;
   private final GlobaleObjekte.StrBereich[] strListe;
   private final StradokuListenGenerator init;
   private boolean hinweis;
   private final String[] ZP = {"A", "B", "C", "D", "E", "F", "G", "H", "J"};
   private final String[] KW = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
   private final String SP = "&nbsp;";
   private final String SP2 = "&nbsp;&nbsp;";
   private final String SP3 = SP + SP2;
   private final String SP5 = SP2 + SP3;
   private final String SPm = "&nbsp;&nbsp;- ";
   private final String ZS2 = "<br><br>";
   private String bPos;
   private String hnwText;
   private int ziel;

   /**
    * Konstruktor
    *
    * @param strap Referenzs auf die StradokuApp-Klasse
    * @param aktStr Referenz auf das aktuelle StrBereich
    * @throws java.io.IOException
    */
   public TippLoeser(Stradoku strap, int[] aktStr) throws IOException {
      strApp = strap;
      init = new StradokuListenGenerator(aktStr);
      afgFeld = new int[9][9];
      strFeld = init.getStradokuFeld();
      for (int z = 0; z < 9; z++) {
         for (int s = 0; s < 9; s++) {
            if (strFeld[z][s] <= 9 || strFeld[z][s] >= SZELLE) {
               afgFeld[z][s] = strFeld[z][s];
            } else {
               afgFeld[z][s] = 0;
            }
         }
      }
      strListe = init.getStradokuListe();
      hnwText = "Kein Hinweis möglich!";
      hinweis = false;
      ziel = -1;
   }

   /**
    * Aufgabe lösen und dabei festzustellen, welchen Level die Aufgabe hat.
    *
    * @throws java.io.IOException
    */
   public boolean loeseLogAufgabe() throws IOException {
      int geaendert;
      int i;
      int anzahl = 0;
      while (strListe[anzahl].len > 0) {
         anzahl++;
      }
      setGeloestFlag();
      do {
         i = 0;
         geaendert = 0;
         // alle ungelösten Bereiche der Liste checken
         while (strListe[i].len > 0) {
            if (!strListe[i].geloest) {
               setBereichsposition(i);
               int tmpgeaendert = geaendert;
               geaendert += setSingles();
               if (hinweis) {
                  break;
               }
               if (strListe[i].len == 2) {
                  geaendert += check2erStra(strListe[i]);
                  if (hinweis) {
                     break;
                  }
               } else if (strListe[i].len >= 2
                     && strListe[i].len <= 8) {
                  geaendert += check38erStra(strListe[i]);
                  if (hinweis) {
                     break;
                  }
               } else {
                  geaendert += check9erStra(strListe[i]);
                  if (hinweis) {
                     break;
                  }
               }
               if (!strListe[i].geloest && geaendert > tmpgeaendert) {
                  checkGeloest(strListe[i]);
                  if (hinweis) {
                     break;
                  }
               }
            }
            i++;
         }
         i = 0;
         while (strListe[i].len > 0) {
            if (hinweis) {
               break;
            }
            i++;
            if (strListe[i].geloest) {
               i++;
               continue;
            }
            int sknd = get_sichereKnd(strListe[i]);
            geaendert += setSichereEinzelKandidaten(strListe[i], sknd);
            if (hinweis) {
               break;
            }
            if (!hinweis) {
               geaendert += nackteGruppen(2);
            }
            if (!hinweis) {
               geaendert += nackteGruppen(3);
            }
            if (!hinweis) {
               geaendert += versteckteGruppen(2);
            }
            if (!hinweis) {
               geaendert += versteckteGruppen(3);
            }
         }
      } while (geaendert > 0);

      strApp.setLoeTipps(ziel, hnwText);
      return hinweis;
   }

   /**
    * Wertet Straßen der Länge Zwei aus.
    *
    * @param stra Infos füe eine 2er-StrBereich
    * @return Anzahl der vorgenommenen Änderungen
    */
   private int check2erStra(StrBereich stra) throws IOException {
      int w, knd;
      int n1, n2;
      int vKnd, mKnd;                 // vorhandene und mögliche Kandidaten
      int mod = 0;
      int geaendert = 0;
      int[] wrt = new int[stra.len];
      Position[] pos = stra.pos;

      for (int i = 0; i < stra.len; i++) {
         wrt[i] = strFeld[pos[i].z][pos[i].s] & KLIST;
      }
      if (wrt[0] <= LWRT && wrt[1] > LWRT
            || wrt[0] > LWRT && wrt[1] <= LWRT) {
         mod = 1;
      } else if (wrt[0] > LWRT && wrt[1] > LWRT) {
         mod = 2;
      }
      switch (mod) {
         case 0:
            return 0;
         case 1:
            // eine Zelle ist bereits gelöst
            if (wrt[0] > LWRT) {
               vKnd = wrt[0];
               n1 = getAnzahlKnd(wrt[0]);
               if (n1 > 1) {
                  wrt[0] &= (KMASKE[wrt.length][wrt[1] & ZAHL]);
                  n2 = getAnzahlKnd(wrt[0]);
                  geaendert += n1 - n2;
               } else {
                  n2 = 1;
                  geaendert++;
               }
               if (n2 == 1) {
                  w = dezKnd(wrt[0] & AKND);
                  if (!hinweis) {
                     ziel = ZI81[pos[0].z][pos[0].s];
                     hnwText = "Bereich: " + bPos + ZS2
                           + "Schritt:" + SP3 + ZP[pos[0].z] + (pos[0].s + 1)
                           + " >" + SP2 + " = " + w + ZS2
                           + "Grund:" + SP3 + "Bereichssingle";
                     hinweis = true;
                     return 0;
                  }
               }
               if (n2 < n1) {
                  mKnd = (wrt[0] | LWF | n2);
                  if (!hinweis) {
                     String erg = getKndString(vKnd, mKnd);
                     ziel = ZI81[pos[0].z][pos[0].s];
                     String info;
                     if (Integer.parseInt(erg) < 10) {
                        info = "verlorener Kandidat";
                     } else {
                        info = "verlorene Kandidaten";
                     }
                     hnwText = "Bereich: " + bPos + ZS2
                           + "Schritt:" + SP3 + ZP[pos[0].z] + (pos[0].s + 1)
                           + " > " + SPm + erg + ZS2
                           + "Grund:" + SP3 + info;
                     hinweis = true;
                     return 0;
                  }
               }
               geaendert += setSingles();
            } else {
               vKnd = wrt[1];
               n1 = getAnzahlKnd(wrt[1]);
               if (n1 > 1) {
                  wrt[1] &= (KMASKE[wrt.length][wrt[0] & ZAHL]);
                  n2 = getAnzahlKnd(wrt[1]);
                  geaendert += n1 - n2;
               } else {
                  n2 = 1;
                  geaendert++;
               }
               if (n2 == 1) {
                  w = dezKnd(wrt[1] & AKND);
                  if (!hinweis) {
                     ziel = ZI81[pos[1].z][pos[1].s];
                     hnwText = "Bereich: " + bPos + ZS2
                           + "Schritt:" + SP3 + ZP[pos[1].z] + (pos[1].s + 1)
                           + " > " + SP2 + " = " + w + ZS2
                           + "Grund:" + SP3 + "Bereichssingle";
                     hinweis = true;
                     return 0;
                  }
               }
               if (n2 < n1) {
                  mKnd = (wrt[1] | LWF | n2);
                  if (!hinweis) {
                     ziel = ZI81[pos[1].z][pos[1].s];
                     String erg = getKndString(vKnd, mKnd);
                     String info;
                     if (Integer.parseInt(erg) < 10) {
                        info = "verlorener Kandidat";
                     } else {
                        info = "verlorene Kandidaten";
                     }
                     hnwText = "Bereich: " + bPos + ZS2
                           + "Schritt:" + SP3 + ZP[pos[1].z] + (pos[1].s + 1)
                           + " > " + SPm + erg + ZS2
                           + "Grund:" + SP3 + info;
                     hinweis = true;
                     return 0;
                  }
               }
               geaendert += setSingles();
            }
            break;
         case 2:
            // beide Zellen sind noch frei - 1. Zelle
            n1 = getAnzahlKnd(wrt[0]);
            mKnd = wrt[0] & ((wrt[1] & ~LWRT) >> 1);
            mKnd |= wrt[0] & ((wrt[1] & ~LWRT) << 1);
            n2 = getAnzahlKnd(mKnd);
            geaendert += n1 - n2;
            if (mKnd > 0) {
               if (n2 == 1) {
                  knd = dezKnd(mKnd & AKND);
                  if (!hinweis) {
                     ziel = ZI81[pos[0].z][pos[0].s];
                     hnwText = "Bereich: " + bPos + ZS2
                           + "Schritt:" + SP3 + ZP[pos[0].z] + (pos[0].s + 1)
                           + " >" + SP2 + " = " + knd + ZS2
                           + "Grund:" + SP3 + "indirekter Single";
                     hinweis = true;
                     return 0;
                  }
               } else if (n1 > n2) {
                  mKnd |= (LWF | n2);
                  vKnd = strFeld[pos[0].z][pos[0].s];
                  if (!hinweis) {
                     ziel = ZI81[pos[0].z][pos[0].s];
                     String erg = getKndString(vKnd, mKnd);
                     String info;
                     if (Integer.parseInt(erg) < 10) {
                        info = "verlorener Kandidat";
                     } else {
                        info = "verlorene Kandidaten";
                     }
                     hnwText = "Bereich: " + bPos + ZS2
                           + "Schritt:" + SP3 + ZP[pos[0].z] + (pos[0].s + 1)
                           + " > " + SPm + erg + ZS2
                           + "Grund:" + SP3 + info;
                     hinweis = true;
                     return 0;
                  }
                  geaendert += setSingles();
               }
            }
            // 2. Zelle
            n1 = getAnzahlKnd(wrt[1]);
            mKnd = wrt[1] & ((wrt[0] & ~LWRT) >> 1);
            mKnd |= wrt[1] & ((wrt[0] & ~LWRT) << 1);
            n2 = getAnzahlKnd(mKnd);
            geaendert += n1 - n2;
            if (mKnd > 0) {
               if (n2 < n1) {
                  mKnd |= (LWF | n2);
                  vKnd = strFeld[pos[1].z][pos[1].s];
                  if (!hinweis) {
                     ziel = ZI81[pos[1].z][pos[1].s];
                     String erg = getKndString(vKnd, mKnd);
                     String info;
                     if (Integer.parseInt(erg) < 10) {
                        info = "verlorener Kandidat";
                     } else {
                        info = "verlorene Kandidaten";
                     }
                     hnwText = "Bereich: " + bPos + ZS2
                           + "Schritt:" + SP3 + ZP[pos[1].z] + (pos[1].s + 1)
                           + " > " + SPm + erg + ZS2
                           + "Grund:" + SP3 + info;
                     hinweis = true;
                     return 0;
                  }
                  geaendert += setSingles();
               }
            }
      }
      int sKnd = get_sichereKnd(stra);
      if (sKnd > 0) {
         geaendert += delKnd4Reihe(stra, sKnd);
      }
      return geaendert;
   }

   /**
    * Wertet Bereiche mit einer Länge von 3 bis 8 Zellen aus.
    *
    * @param stra Infos füe auszuwertenden Bereich
    * @return Anzahl der vorgenommenen Änderungen
    */
   private int check38erStra(StrBereich stra) throws IOException {
      int n1, n2;
      int geaendert = 0;
      int[] wrt = new int[stra.len];
      Position[] pos = stra.pos;
      for (int i = 0; i < stra.len; i++) {
         wrt[i] = strFeld[pos[i].z][pos[i].s] & KLIST;
      }
      int mKnd = get_moeglicheKnd(stra);
      if (mKnd > 0) {                                    // nur wenn auswertbare Kandidaten
         // alle Zellen des Bereichs prüfen
         for (int i = 0; i < stra.len; i++) {
            if (wrt[i] < LWRT) {                         // keine Kandidaten 
               continue;
            }
            n1 = getAnzahlKnd(wrt[i]);                   // eingangs vorhandene Kandidaten
            wrt[i] &= mKnd;                              // über noch mögliche Kandidaten korrigieren
            n2 = getAnzahlKnd(wrt[i]);
            geaendert += n1 - n2;
            if (n2 < n1) {
               int vKnd = strFeld[pos[i].z][pos[i].s];
               wrt[i] |= LWF + n2;
               if (!hinweis) {
                  ziel = ZI81[pos[i].z][pos[i].s];
                  String erg = getKndString(vKnd, wrt[i]);
                  String info;
                  if (Integer.parseInt(erg) < 10) {
                     info = "verlorener Kandidat";
                  } else {
                     info = "verlorene Kandidaten";
                  }
                  hnwText = "Bereich: " + bPos + ZS2
                        + "Schritt:" + SP3 + ZP[pos[i].z] + (pos[i].s + 1)
                        + " > " + SPm + erg + ZS2
                        + "Grund:" + SP3 + info;
                  hinweis = true;
                  return 0;
               } else if (n2 == 1) {
                  int w = dezKnd(wrt[i] & AKND);
                  if (!hinweis) {
                     // 09
                     ziel = ZI81[pos[i].z][pos[i].s];
                     hnwText = "Bereich: " + bPos + ZS2
                           + "Schritt:" + SP3 + ZP[pos[i].z]
                           + (pos[i].s + 1) + " = " + w + ZS2
                           + "Grund:" + SP3 + "Bereichssingle";
                     hinweis = true;
                     return 0;
                  }
               }
               geaendert += setSingles();
            }
         }
      }
      int sKnd = get_sichereKnd(stra);
      if (sKnd > 0) {
         geaendert += delKnd4Reihe(stra, sKnd);
         if (!hinweis) {
            geaendert += setSichereEinzelKandidaten(stra, sKnd);
         }    
      }
      return geaendert;
   }

   /**
    * Wertet Straßen der Länge Neun aus.
    *
    * @param stra Infos füe eine 9er-StrBereich
    * @return Anzahl der vorgenommenen Änderungen
    */
   private int check9erStra(StrBereich stra) throws IOException {
      int geaendert = 0;
      int[] wrt = new int[9];
      Position[] pos = stra.pos;

      // Zellwerte eintragen und dabei Singles abfangen
      for (int i = 0; i < stra.len; i++) {
         wrt[i] = strFeld[pos[i].z][pos[i].s] & KLIST;
         if (wrt[i] > LWF) {
            int nk = getAnzahlKnd(wrt[i]);
            if (nk == 1) {
               int w = dezKnd(wrt[i] & AKND);
               if (!hinweis) {
                  // 10
                  ziel = ZI81[pos[i].z][pos[i].s];
                  hnwText = "Bereich: " + bPos + ZP[pos[i].z] + (pos[i].s + 1)
                        + "Schritt:" + SP3 + ZP[pos[i].z] + (pos[i].s + 1)
                        + "> = " + w + ZS2
                        + "Grund:" + SP3 + "Bereichssingle";
                  hinweis = true;
                  return 0;
               }
            }
         }
      }
      // Vorkommen der Kandidaten zählen
      for (int k = 1; k <= 9; k++) {
         int c = 0;
         int li = -1;
         for (int i = 0; i < stra.len; i++) {
            if ((strFeld[pos[i].z][pos[i].s] & (LWF << k)) > 0) {
               c++;
               li = i;
            }
         }
         // Unikate ausschließen
         if (c == 1 && strFeld[pos[li].z][pos[li].s] != (LWF | k)) {
            if (!hinweis) {
               ziel = ZI81[pos[li].z][pos[li].s];
               hnwText = "Bereich: " + bPos + ZS2
                     + "Schritt:" + SP3 + ZP[pos[li].z] + (pos[li].s + 1)
                     + " >" + SP2 + " = " + k + ZS2
                     + "Grund:" + SP3 + "Bereichssingle";
               hinweis = true;
               return 0;
            }
         }
      }
      if (geaendert == 0) {
         geaendert += setSichereEinzelKandidaten(stra, AKND);
      }
      return geaendert;
   }

   /**
    * Prüft das gesamte StrBereich-Feld auf nackte Zweier- und Dreiergruppen und wertet diese aus.
    *
    * @param ngrp Gruppengröße (2 - 4)
    * @return Anzahl der Änderungen
    */
   private int nackteGruppen(int ngrp) throws IOException {
      int geaendert = 0;
      int[] wrt = new int[9];
      int[] test = new int[9];      // Liste der freien Zellen
      int z, s;
      int relz;                     // Zähler relevante Zellen
      int freiz;                    // Zähler der Zellen mit Kandidaten
      int nk, n1, n2;
      int zelle;

      // alle horizontalen und vertikale Reihen bearbeiten
      for (int r = 0; r < 18; r++) {
         // erst Zeile für Zeile, dann Spalte für Spalte
         relz = 0;                      // Zähler relevante Zellen
         freiz = 0;                     // Zähler zu lösende Zellen
         for (int i = 0; i < 9; i++) {
            // Reihe einlesen und auswerten
            zelle = ZELLINDEX_2D[r][i];
            z = zelle & ZM;
            s = zelle >>> SM;
            wrt[i] = strFeld[z][s] & KLIST;
            if (wrt[i] >= SZELLE || (wrt[i] & AKND) == 0) {
               continue;
            }
            nk = getAnzahlKnd(wrt[i]);
            freiz++;
            if (nk <= ngrp) {
               test[relz] = i;     // Indexe von Zellen mit < oder == Kandidaten wie n-Gruppe
               relz++;             // Anzahl der relevanten Zellen
            }
         }
         if (relz >= ngrp) {         // mindest soviele relevante Zellen wie n-Gruppe
            boolean gefunden = false;
            int ergb = 0;
            switch (ngrp) {
               case 2:
                  for (int a = 0; a < relz - 1; a++) {
                     for (int b = a + 1; b < relz; b++) {
                        if (wrt[test[a]] == wrt[test[b]]) {
                           ergb = AKND & wrt[test[a]];
                           gefunden = true;
                           break;
                        }
                     }
                     if (gefunden && freiz > ngrp) {
                        for (int i = 0; i < 9; i++) {
                           if (((wrt[i] & AKND) > 0) && (wrt[i] & AKND) != ergb) {
                              zelle = ZELLINDEX_2D[r][i];
                              z = zelle & ZM;
                              s = zelle >>> SM;
                              if ((strFeld[z][s] | ergb) == ergb) {
                                 continue;
                              }
                              n1 = getAnzahlKnd(strFeld[z][s]);
                              int vKnd = strFeld[z][s];
                              strFeld[z][s] &= ~ergb;
                              n2 = getAnzahlKnd(strFeld[z][s]);
                              if (n1 > n2) {
                                 if (!hinweis) {
                                    ziel = ZI81[z][s];
                                    String gru = getKndString(0, ergb);
                                    String reihe = r < 9 ? "Zeile: " + SP5 + ZP[z]
                                          : "Spalte: " + SP2 + (s + 1);
                                    String erg = getKndString(vKnd, ~ergb);
                                    hnwText = reihe + ZS2
                                          + "Schritt:" + SP3 + ZP[z] + (s + 1) + SP2
                                          + "> " + SP2 + "- " + erg + ZS2
                                          + "Grund:" + SP3 + "n-Gruppe mit " + gru;
                                    hinweis = true;
                                    return 0;
                                 }
                                 geaendert = n1 - n2;
                                 geaendert += setSingles();
                              }
                           }
                        }
                     }
                     if (gefunden) {
                        break;
                     }
                  }
                  break;
               case 3:
                  for (int a = 0; a <= relz - 3; a++) {
                     for (int b = a + 1; b <= relz - 2; b++) {
                        for (int c = b + 1; c <= relz - 1; c++) {
                           ergb = AKND & (wrt[test[a]]
                                 | wrt[test[b]] | wrt[test[c]]);
                           int anz = NKND[ergb >>> 5];
                           if (anz == ngrp) {
                              gefunden = true;
                              break;
                           }
                        }
                        if (gefunden) {
                           break;
                        }
                     }
                     if (gefunden && freiz > ngrp) {
                        for (int i = 0; i < 9; i++) {
                           if ((((wrt[i] & AKND) | ergb) == ergb)
                                 || ((wrt[i] & AKND) == 0)) {
                              continue;
                           }
                           zelle = ZELLINDEX_2D[r][i];
                           z = zelle & ZM;
                           s = zelle >>> SM;
                           int vKnd = strFeld[z][s];
                           n1 = getAnzahlKnd(strFeld[z][s]);
                           strFeld[z][s] &= ~ergb;
                           n2 = getAnzahlKnd(strFeld[z][s]);
                           if (n1 > n2) {
                              if (!hinweis) {
                                 ziel = ZI81[z][s];
                                 String reihe = r < 9 ? "Zeile: " + SP5 + ZP[z]
                                       : "Spalte: " + SP2 + (s + 1);
                                 String gru = getKndString(0, ergb);
                                 String erg = getKndString(0, ergb & vKnd);
                                 hnwText = reihe + ZS2
                                       + "Schritt:" + SP3 + ZP[z] + (s + 1) + SP2
                                       + "> " + SP2 + "- " + erg + ZS2
                                       + "Grund:" + SP3 + "n-Gruppe mit " + gru;
                                 hinweis = true;
                                 return 0;
                              }
                              geaendert = n1 - n2;
                           }
                        }
                        break;
                     }
                  }
                  break;
            }
         }
      }
      return geaendert;
   }

   /**
    * Prüft auf versteckte Zweier- und Dreiergruppen und wertet diese aus.
    *
    * @param stra Referenz zur überprüfenden Gruppe
    * @param ngrp Gruppengröße (2 oder 3)
    * @return Anzahl der Änderungen
    */
   private int versteckteGruppen(int ngrp) {
      BereichsAuswertung brAuswrt = new BereichsAuswertung();
      int geaendert = 0;
      int knd, nknd;
      int[] grpZelle = new int[ngrp];
      int z, s;
      int n1, n2;
      int[] wrt;
      int bi = 0;
      int ungeloest;

      nbereich:
      while (strListe[bi].len > 0) {
         StrBereich stra = strListe[bi++];
         if (stra.geloest || stra.len <= ngrp) {
            continue;
         } else {
            wrt = new int[stra.len];
            Position[] pos = stra.pos;
            ungeloest = 0;
            for (int i = 0; i < stra.len; i++) {
               wrt[i] = strFeld[pos[i].z][pos[i].s] & KLIST;
               if (wrt[i] >= MINK) {
                  ungeloest++;
               }
            }
            if (ungeloest <= ngrp) {
               continue;
            }
         }
         knd = getKandidaten(wrt) >> 5;

         nknd = NKND[knd];
         int kombinationen = brAuswrt.getKndKombintionen(ngrp, nknd);
         int[] kndMasken = new int[kombinationen];
         brAuswrt.erzeugeKndMasken(kndMasken, knd, ngrp);
         // alle möglichen Kombinationen durchchecken
         for (int m = 0; m < kombinationen; m++) {
            int mGruppe = kndMasken[m];
            int zaehler = 0;
            for (int i = 0; i < wrt.length; i++) {
               if ((wrt[i] & mGruppe) > 0) {
                  zaehler++;
               }
               if (zaehler > ngrp) {
                  break;
               }
            }
            if (zaehler != ngrp) {
               continue;
            }
            int p = 0;
            boolean zusatzKnd = false;
            for (int i = 0; i < wrt.length; i++) {
               if ((wrt[i] & mGruppe) > 0) {
                  grpZelle[p] = i;
                  p++;
                  if ((wrt[i] & (~mGruppe & AKND)) > 0) {
                     zusatzKnd = true;
                  }
               }
            }
            if (!zusatzKnd) {
               continue nbereich;
            }
            for (int i = 0; i < grpZelle.length; i++) {
               int sKnd = get_sichereKnd(stra);
               if ((sKnd & mGruppe) != mGruppe) {
                  continue nbereich;
               }
               n1 = getAnzahlKnd(wrt[grpZelle[i]]);
               int knd1 = wrt[grpZelle[i]];
               wrt[grpZelle[i]] &= mGruppe;
               n2 = getAnzahlKnd(wrt[grpZelle[i]]);
               int knd2 = wrt[grpZelle[i]];
               int ergb = knd1 - knd2;
               if (n1 > n2) {
                  if (!hinweis) {
                     z = stra.pos[grpZelle[i]].z;
                     s = stra.pos[grpZelle[i]].s;
                     ziel = ZI81[z][s];
                     String sErg = getKndString(0, ergb);
                     String sGru = getKndString(0, mGruppe);
                     setBereichsposition(stra.index);
                     hnwText = "Bereich: " + bPos + ZS2
                           + "Schritt:" + SP3 + ZP[z] + (s + 1) + SP2 + "> "
                           + SP2 + "- " + sErg + ZS2
                           + "Grund: " + SP3 + "v-Gruppe mit " + sGru;
                     hinweis = true;
                     return 1;
                  }
                  geaendert += n1 - n2;
               }
            }
         }
      }
      return geaendert;
   }

   /**
    * Durchsucht das gesamte Feld nach Zellen mit nur noch einem Kandidaten (Zellen-Single) und
    * weist solche der jeweiligen Zelle als Lösungswert zu.
    *
    * @return Anzahl der Zuweisungen
    */
   private int setSingles() throws IOException {
      int geaendert = 0;
      boolean single;
      int[] wrt = new int[9];
      do {
         single = false;
         for (int r = 0; r < 18; r++) {              // 9 Zeilen und 9 Spalten -> 18 Reihen
            for (int i = 0; i < 9; i++) {           // für jede Reihe alle querenden Reihen
               int zelle = ZELLINDEX_2D[r][i];
               int z = zelle & ZM;
               int s = zelle >>> SM;
               wrt[i] = strFeld[z][s] & KLIST;
               if (wrt[i] >= SZELLE || (wrt[i] & AKND) == 0) {
                  continue;
               }
               int nk = getAnzahlKnd(wrt[i]);
               if (nk == 1) {
                  int lw = dezKnd(wrt[i] & AKND);
                  if (!hinweis) {
                     ziel = ZI81[z][s];
                     hnwText = "Zelle:" + SP5 + ZP[z] + (s + 1) + ZS2
                           + "Schritt:" + SP3 + ZP[z] + (s + 1)
                           + " >" + SP2 + " = " + lw + ZS2
                           + "Grund:" + SP3 + "Single";
                     hinweis = true;
                     return 0;
                  }
               }
            }
         }
      } while (single);
      return geaendert;
   }

   /**
    * Tipp wenn ein sicherer Kandidat in einem Bereich nur genau einmal vertreten ist
    * @param stra zu überprüfender Bereich
    * @param sknd sichere Kandidaten dieses Bereiches
    * @return  Anzahl der Änderungen
    * @throws IOException 
    */   
   private int setSichereEinzelKandidaten(StrBereich stra, int sknd) throws IOException {
      int sKnd = sknd;
      if (sKnd < 1) {
         return 0;
      }
      setBereichsposition(stra.index);
      int geaendert = 0;
      int k;
      int w;
      KndPosition kPos[] = new KndPosition[10];
      for (int i = 0; i < 10; i++) {
         kPos[i] = new KndPosition();
      }
      int min = get_minKnd(sKnd);
      int dmin = dezKnd(min);
      int max = get_maxKnd(sKnd);
      if (stra.isZeile) {
         int z = stra.ezPos.z;
         // jede Zelle der Zeile 
         for (int s = stra.ezPos.s; s <= stra.lzPos.s; s++) {
            // ist sicherer Kandidat vertreten
            for (k = min, w = dmin; k <= max; k <<= 1, w++) {
               // wenn ja, für Wert Anzahl und (letzte)Position ermitteln 
               if ((strFeld[z][s] & k) > 0 && (sKnd & k) > 0) {
                  kPos[w].n++;
                  kPos[w].p = s;
               }
            }
         }
         for (k = min, w = dmin; k <= max; k <<= 1, w++) {
            if (kPos[w].n == 1) {
               strFeld[z][kPos[w].p] = w;
               if (!hinweis) {
                  ziel = ZI81[z][kPos[w].p];
                  hnwText = "Bereich: " + bPos + ZS2
                        + "Schritt:" + SP3 + ZP[z] + (kPos[w].p + 1)
                        + " >" + SP2 + " = " + w + ZS2
                        + "Grund:" + SP3 + "Bereichssingle";
                  hinweis = true;
                  return 0;
               }
            }
         }
      } else {                                                        // für Spalten
         int s = stra.ezPos.s;
         for (int z = stra.ezPos.z; z <= stra.lzPos.z; z++) {
            for (k = min, w = dmin; k <= max; k <<= 1, w++) {
               if ((strFeld[z][s] & k) > 0) {
                  kPos[w].n++;
                  kPos[w].p = z;
               }
            }
         }
         for (k = min, w = dmin; k <= max; k <<= 1, w++) {
            if (kPos[w].n == 1) {
               if (!hinweis) {
                  ziel = ZI81[kPos[w].p][s];
                  hnwText = "Bereich: " + bPos + ZS2
                        + "Schritt:" + SP3 + ZP[kPos[w].p] + (s + 1)
                        + " >" + SP2 + "= " + w + ZS2
                        + "Grund:" + SP3 + "Bereichssingle";
                  hinweis = true;
                  return 0;
               }
            }
         }
      }
      return geaendert;
   }

   /**
    * Entfernt für die Reihe, in der ein StrBereich liegt, sichere Kandidaten außerhalb des
    * StrBereichs.
    *
    * @param stra StrBereich, für die entfernt werden soll
    * @param knd Kandidaten, die entfernt werden sollen
    * @return - Anzahl der entfernten Kandidaten
    */
   private int delKnd4Reihe(StrBereich stra, int knd) throws IOException {
      int geaendert = 0;
      int sp, zl;
      int sperre_a, sperre_e;
      int n1, n2;
      setBereichsposition(stra.index);
      if (stra.isZeile) {
         zl = stra.ezPos.z;
         sperre_a = stra.ezPos.s;
         sperre_e = stra.lzPos.s;
         for (int s = 0; s < 9; s++) {
            if (strFeld[zl][s] < SZELLE && (s < sperre_a || s > sperre_e)) {
               n1 = getAnzahlKnd(strFeld[zl][s]);
               if ((strFeld[zl][s] & knd) > 0) {
                  int vKnd = strFeld[zl][s];
                  strFeld[zl][s] &= ~knd;
                  n2 = getAnzahlKnd(strFeld[zl][s]);
                  if (n2 < n1 && !hinweis) {
                     ziel = ZI81[zl][s];
                     String erg = getKndString(vKnd, strFeld[zl][s]);
                     String info;
                     if (Integer.parseInt(erg) < 10) {
                        info = "sicherer Kandidat";
                     } else {
                        info = "sichere Kandidaten";
                     }
                     hnwText = "Bereich: " + bPos + ZS2
                           + "Schritt: " + SP2 + ZP[zl] + (s + 1)
                           + " > " + SPm + erg + ZS2
                           + "Grund:" + SP3 + info;
                     hinweis = true;
                     return 0;
                  }
                  if (n2 != n1) {
                     geaendert++;
                  }
                  setSingles();
               }
            }
         }
      } else {
         sp = stra.ezPos.s;
         sperre_a = stra.ezPos.z;
         sperre_e = stra.lzPos.z;
         for (int z = 0; z < 9; z++) {
            if (z < sperre_a || z > sperre_e) {
               n1 = getAnzahlKnd(strFeld[z][sp]);
               if ((strFeld[z][sp] & knd) > 0) {
                  int vKnd = strFeld[z][sp];
                  strFeld[z][sp] &= ~knd;
                  n2 = getAnzahlKnd(strFeld[z][sp]);
                  if (!hinweis) {
                     ziel = ZI81[z][sp];
                     String erg = getKndString(vKnd, strFeld[z][sp]);
                     String info;
                     if (Integer.parseInt(erg) < 10) {
                        info = "sicherer Kandidat";
                     } else {
                        info = "sichere Kandidaten";
                     }
                     hnwText = "Bereich: " + bPos + ZS2
                           + "Schritt: " + SP2 + ZP[z] + (sp + 1)
                           + " > " + SPm + erg + ZS2
                           + "Grund:" + SP3 + info;
                     hinweis = true;
                     return 0;
                  }
                  if (n2 != n1) {
                     geaendert++;
                  }
                  setSingles();
               }
            }
         }
      }
      return geaendert;
   }

   /**
    * Prüft, mit welchen der vorhandenen Kandidaten noch eine StrBereich gebildet werden kann.
    *
    * @param stra zu überprüfende StrBereich
    * @return mögliche Kandidaten
    */
   private int get_moeglicheKnd(StrBereich stra) {
      int mk, tk, knd = 0;
      int min = 10, max = 0;
      int c = 0;
      int[] wrt = new int[stra.len];

      for (int i = 0; i < stra.len; i++) {
         wrt[i] = strFeld[stra.pos[i].z][stra.pos[i].s] & KLIST;
         // Vorgabe- oder Lösungswert
         if (wrt[i] < LWRT) {
            knd |= LWF << (wrt[i] & ZAHL);
            min = (wrt[i] & ZAHL) < min ? (wrt[i] & ZAHL) : min;
            max = (wrt[i] & ZAHL) > max ? (wrt[i] & ZAHL) : max;
         } else {
            knd |= (wrt[i] & ~LWRT);
            c++;
         }
      }
      // in 3er StrBereich fehlt nur noch ein Wert
      if (c == 1 && stra.len == 3) {
         if (max - min == 1) {
            mk = (LWF << max + 1) | (LWF << min - 1);
         } else {
            mk = LWF << ((max + min) / 2);
         }
      } else {
         if (min < 10) {
            knd &= ((KMASKE[stra.len][min] & KMASKE[stra.len][max]) & AKND);
         }
         int n1 = getAnzahlKnd(knd);
         int gruppen = 0;
         c = 0;
         tk = 0;
         mk = 0;
         for (int k = MINK; k <= MAXK; k <<= 1) {
            if ((knd & k) == k) {
               tk |= k;
               c++;
            } else if (c >= stra.len) {
               mk |= tk;
               gruppen++;
               if (gruppen == 2) {
                  break;
               }
               tk = 0;
               c = 0;
            } else {
               tk = 0;
               c = 0;
            }
            if (k == MAXK && c >= stra.len) {
               mk |= tk;
               gruppen++;
            }
         }
         if (gruppen == 1) {
            int n2 = getAnzahlKnd(mk);
            if (n1 == n2 && n2 > stra.len) {
               min = dezKnd(get_minKnd(mk));
               max = dezKnd(get_maxKnd(mk));
               // wir haben größten und kleinsten Kandidaten
               for (int i = 0; i < stra.len; i++) {
                  if (getAnzahlKnd(wrt[i]) > 1
                        && getAnzahlKnd(wrt[i]) < 9) {
                     int zmin = dezKnd(get_minKnd(wrt[i]));
                     int zmax = dezKnd(get_maxKnd(wrt[i]));
                     if (zmax < max) {
                        max = (zmax + stra.len - 1) > 9
                              ? max : zmax + stra.len - 1;
                        for (int k = max + 1; k <= 9; k++) {
                           mk &= ~(LWF << k);
                        }
                     }
                     if (zmin > min) {
                        min = (zmin - stra.len) < 1
                              ? min : zmin - stra.len + 1;
                        for (int k = min - 1; k >= 1; k--) {
                           mk &= ~(LWF << k);
                        }
                     }
                  }
               }
            }
         } else if (gruppen == 2) {
            // prüfen, ob in einer Zelle nur Kandidaten einer Gruppe sind
            int grp1 = mk & ~tk;
            int grp2 = tk;
            mk = 0;
            for (int it = 0; it < stra.len; it++) {
               int cg1 = 0;
               int cg2 = 0;
               for (int i = 0; i < stra.len; i++) {
                  if (i == it) {
                     continue;
                  }
                  if ((wrt[it] & grp1) > 0) {
                     cg1++;
                  }
                  if ((wrt[it] & grp2) > 0) {
                     cg2++;
                  }
               }
               if (cg1 == 0) {
                  mk = grp2;
               }
               if (cg2 == 0) {
                  mk = grp1;
               }
               if (mk > 0) {
                  break;
               }
            }
         }
         int n2 = getAnzahlKnd(mk);
         if (n1 == n2 && n2 > stra.len) {
            min = dezKnd(get_minKnd(mk));
            max = dezKnd(get_maxKnd(mk));
            for (int i = 0; i < stra.len; i++) {
               if (getAnzahlKnd(wrt[i]) > 1
                     && getAnzahlKnd(wrt[i]) < 4) {
                  int zmin = dezKnd(get_minKnd(wrt[i]));
                  int zmax = dezKnd(get_maxKnd(wrt[i]));
                  if (zmax < max) {
                     max = (zmax + stra.len - 1) > 9
                           ? max : zmax + stra.len - 1;
                     for (int k = max + 1; k <= 9; k++) {
                        mk &= ~(LWF << k);
                     }
                  }
                  if (zmin > min) {
                     min = (zmin - stra.len) < 1
                           ? min : zmin - stra.len + 1;
                     for (int k = min - 1; k >= 1; k--) {
                        mk &= ~(LWF << k);
                     }
                  }
               }
            }
         }
      }
      return mk;
   }

   /**
    * Ermittelt die sicheren Kandidaten für eine StrBereich.
    *
    * @param stra auszuwertende StrBereich
    * @return sichere Kandidaten 0 wenn keine sicheren vorhanden
    */
   private int get_sichereKnd(StrBereich stra) {
      int sk = 0;             // sichere Kandidaten
      int vk;                 // Anzahl der vorhandenen Kandidaten
      int len = stra.len;
      int knd = 0, min = 10, max = 0;
      int[] wrt = new int[stra.len];

      for (int i = 0; i < stra.len; i++) {
         wrt[i] = strFeld[stra.pos[i].z][stra.pos[i].s] & KLIST;
         // Vorgabe- oder Lösungswert
         if (wrt[i] < LWRT) {
            knd |= LWF << (wrt[i] & ZAHL);
            min = (wrt[i] & ZAHL) < min ? (wrt[i] & ZAHL) : min;
            max = (wrt[i] & ZAHL) > max ? (wrt[i] & ZAHL) : max;
         } else {
            knd |= wrt[i];
         }
      }
      // wie viele Kandidaten sind vorhanden
      knd &= AKND;
      vk = getAnzahlKnd(knd);
      if (vk == len) {
         // zugewiesene oder Lösungswerte entfernen
         for (int i = 0; i < stra.len; i++) {
            int wert = strFeld[stra.pos[i].z][stra.pos[i].s];
            if (wert <= LWF) {
               knd &= ~(LWF << (wert & ZAHL));
            }
         }
         return knd;
      }
      if (vk - len == 1) {
         int tmp1 = knd & (knd << 1);
         int tmp2 = knd & (knd >> 1);
         sk = tmp1 & tmp2;
      } else if (vk - len == 2) {
         int tmp1 = knd & (knd << 2);
         int tmp2 = knd & (knd >> 2);
         sk = tmp1 & tmp2;
      } else if (vk - len == 3) {
         int tmp1 = knd & (knd << 3);
         int tmp2 = knd & (knd >> 3);
         sk = tmp1 & tmp2;
      } else if (len >= 5) {
         sk = LWF << 5;
      } else if (min < 10 || max > 0) {
         sk |= (LWF << min);
         sk |= (LWF << max);
      }
      for (int i = 0; i < stra.len; i++) {
         if (wrt[i] < LWRT) {
            int wt = wrt[i] & ZAHL;
            sk &= ~(LWF << wt);
         }
      }
      return sk;
   }

   /**
    * Ermittelt die Anzahl der in einer Zelle noch vertretenen Kandidaten.
    *
    * @param knd vertretene Kandidaten bitcodiert
    * @return Anzahl der vertretenen Kandidaten
    */
   private int getAnzahlKnd(int knd) {
      knd &= AKND;
      return (NKND[knd >>> 5]);
   }

   /**
    * Ermittelt die in einem Bereich noch vorhandenen Kandidaten.
    *
    * @param r erste Punkt der Reihe, bei Zeilen von 0 bis 8, bei Spalten 9 bis 17.
    * @return alle in der Reihe noch vertretenen Kandidaten
    */
   private int getKandidaten(int[] wrt) {
      int kandidaten = 0;
      for (int i = 0; i < wrt.length; i++) {
         if (wrt[i] > LWF) {
            kandidaten |= wrt[i] & AKND;
         }
      }
      return kandidaten;
   }

   /**
    * Gibt den kleinsten Kandidaten einer Zelle zurück.
    *
    * @param knd Kandidaten der Zelle bitcodiert
    * @return kleinster Kandidat bitcodiert
    */
   private int get_minKnd(int knd) {
      int min = MINKND[knd >> 5];
      return (LWF << min);
   }

   /**
    * Gibt den größten Kandidaten einer Zelle zurück.
    *
    * @param knd Kandidaten der Zelle bitcodiert
    * @return größter Kandidat bitcodiert
    */
   private int get_maxKnd(int knd) {
      int max = MAXKND[knd >> 5];
      return (LWF << max);
   }

   /**
    * Prüft alle Bereiche und setzt für gelöste Bereiche das geloest-Flag.
    */
   private void setGeloestFlag() {
      int i = 0;
      boolean geloest;
      while (strListe[i].len > 0) {
         if (!strListe[i].geloest) {
            geloest = true;
            for (int p = 0; p < strListe[i].len; p++) {
               if (strFeld[strListe[i].pos[p].z][strListe[i].pos[p].s] >= LWRT) {
                  geloest = false;
                  break;
               }
            }
            if (geloest) {
               strListe[i].geloest = true;
            }
         }
         i++;
      }
   }

   /**
    * Überprüft einen Bereich ob er gelöst ist und setzt gegebenenfalls das geloest-Flag.
    *
    * @param stra zu prüfender Bereich
    */
   private void checkGeloest(StrBereich stra) {
      if (!stra.geloest) {
         boolean geloest = true;
         for (int p = 0; p < stra.len; p++) {
            if (strFeld[stra.pos[p].z][stra.pos[p].s] > LWRT) {
               geloest = false;
               break;
            }
         }
         if (geloest) {
            strListe[stra.index].geloest = true;
         }
      }
   }

   /**
    * Ermittelt für eine Zelle auszuschließende Kandidaten und gibt diese als String zurück.
    *
    * @param kv ursprünglich vorhandene bitcodierte Kandidaten
    * @param kn davon verbleibende bitcodierte Kandidaten
    * @return String mit den auszuschließenden Kandidaten
    */
   private String getKndString(int kv, int km) {
      String ks = "";
      int tmp;
      if (kv > 0) {
         tmp = (kv & ~km) >> 5;
      } else {
         tmp = km >> 5;
      }
      int k = 1;
      for (int m = 1; m <= 0x100; m <<= 1, k++) {
         if ((tmp & m) == m) {
            ks += KW[k];
         }
      }
      return ks;
   }

   /**
    * Erzeugt für den jeweils aktuellen Bereich den Bezeichner
    *
    * @param i Index des aktuellen Bereiches
    */
   private void setBereichsposition(int i) {
      if (strListe[i].isZeile) {
         bPos = ZP[strListe[i].ezPos.z]
               + (strListe[i].ezPos.s + 1) + (strListe[i].lzPos.s + 1);
      } else {
         bPos = ZP[strListe[i].ezPos.z]
               + (strListe[i].ezPos.s + 1) + ZP[strListe[i].lzPos.z];
      }
   }
   
   /**
    * Übergibt für einen binär definierten Kandidaten den Dezimalwert
    *
    * @param binKnd Binaerwert
    * @return Dezimalwert
    */
   private int dezKnd(int binKnd) {
      switch (binKnd) {
         case 0x20:
            return 1;
         case 0x40:
            return 2;
         case 0x80:
            return 3;
         case 0x100:
            return 4;
         case 0x200:
            return 5;
         case 0x400:
            return 6;
         case 0x800:
            return 7;
         case 0x1000:
            return 8;
         case 0x2000:
            return 9;
      }
      return 0;
   }   

}

