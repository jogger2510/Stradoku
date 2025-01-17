/**
 * LevelSolver.java ist Teil des Programmes kodelasStradoku
 *
 * Erzeugt am:                  01.08.2017 17:37
 * Letzte Änderung:             06.11.2024 11:15
 *
 * Copyright (C) Konrad Demmel, 2019-2024
 */
package stradoku;

/**
 * Aufgabe: StrBereich-Aufgabe lösen und den Level festlegen.
 */
public class LevelSolver implements GlobaleObjekte {

   private static StradokuOrg str;
   private final int[] loesung;
   private final int LWERT = 0x1F;
   private final int[] aufgabe;
   private final int[][] strFeld;
   private final int[][] afgFeld;
   private final StrBereich[] strListe;
   private final StradokuListenGenerator init;
   private BackTrackLoeser btLoeser;
   private boolean backtracking;
   private int level = -1;
   private int bewertung;

   /**
    * Konstruktor
    *
    * @param ostr Referenzs auf die StradokuOrg-Klasse
    * @param aktStr Referenz auf das aktuelle StrBereich
    * @param ziel Referenz auf Ziel-Array
    * @param kndListe true bei vorhandener Kandidatenliste
    */
   public LevelSolver(StradokuOrg ostr,
         int[] aktStr, int[] ziel, boolean kndListe) {
      str = ostr;
      aufgabe = aktStr;
      loesung = ziel;
      init = new StradokuListenGenerator(aktStr);
      afgFeld = new int[9][9];
      strFeld = init.getStradokuFeld();
      backtracking = false;
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
      if (!kndListe) {
         setKandidaten();
      }
   }

   /**
    * Aufgabe lösen und dabei festzustellen, welchen Level die Aufgabe hat.
    *
    * @return festgestellter Level, ungelöst -1.
    */
   public int loeseAufgabe() {
      if (aufgabe == null) {
         return -1;
      } 
      else if (freieZellen() == 0) {
         return 0;
      }
      int geaendert;
      int ungeloest;
      int i;
      int d = 0; 
      int anzahl = 0;
      int freieZellen = 0;
      backtracking = false;
      while (strListe[anzahl].len > 0) {
         anzahl++;
      }
      setGeloest();
      do {
         do {
            d++;
            if (d > 10000) {
               return -1;
            }
            i = 0;
            geaendert = 0;
            // alle ungelösten Bereiche der Liste checken
            while (strListe[i].len > 0) {
               if (!strListe[i].geloest) {
                  int tmpgeaendert = geaendert;
                  if (strListe[i].len == 2) {
                     geaendert += check2erStra(strListe[i]);
                  } else if (strListe[i].len >= 2
                        && strListe[i].len < 9) {
                     geaendert += check38erStra(strListe[i]);
                  } else if (d > 1) {
                     geaendert += check9erStra(strListe[i]);
                  }
                  if (!strListe[i].geloest && geaendert > tmpgeaendert) {
                     checkGeloest(strListe[i]);
                  }
               }
               i++;
            }
            if (!backtracking && d > 2 && geaendert == 0) {
               int grp = nackteGruppen(2);
               if (grp > 0) {
                  geaendert += grp;
                  bewertung += 4;
               } else {
                  grp += nackteGruppen(3);
                  if (grp > 0) {
                     geaendert += grp;
                     bewertung += 6;
                  }
               }
            }
            geaendert += setTrueffel();
            i = 0;
            int check = 0;
            while (strListe[i].len > 0) {
               check = checkKandidatenfolge(strListe[i], backtracking);
               if (check >= 0) {
                  geaendert += check;
               }
               i++;
            }
            if (check == -1) {      // also Fehler durch leere Zelle
               if (backtracking) {
                  btLoeser.backtrackLoesung(strFeld, true);
               } else {
                  entferneLoesungswerte();
                  return -1;
               }
            }
         } while (geaendert > 0);

         for (int z = 0; z < 9; z++) {
            System.arraycopy(strFeld[z], 0, loesung, z * 9, 9);
         }
         // Prüfung auf ungelöste Zellen
         ungeloest = 0;
         freieZellen = 0;
         for (i = 0; i < 81; i++) {
            if (loesung[i] == 0
                  || (loesung[i] == 16) || (loesung[i] & AKND) > 0) {
               ungeloest++;
               break;
            } else if ((loesung[i] & LWF) > 0) {
               freieZellen++;
            }
         }
         level = -1;
         if (ungeloest == 0) {
            bewertung += (freieZellen - 30 > 3 ? freieZellen - 30 : 4) / 4 + d;
            if (bewertung < 5) {
               level = 1;
            } else if (bewertung < 8) {
               level = 2;
            } else if (bewertung < 12) {
               level = 3;
            } else if (!backtracking) {
               level = 4;
            } else {
               level = 5;
            }
            if (level == 2 && freieZellen > 45) {
               level++;
            } else if (level == 3 && freieZellen > 52) {
               level++;
            }
         } else {
            if (!backtracking) {
               btLoeser = new BackTrackLoeser();
               backtracking = true;
            }
            if (!endCheck()) {
               if (!btLoeser.backtrackLoesung(strFeld, false)) {
                  return -1;
               }
            } else {
               entferneLoesungswerte();
               return -1;
            }
         }
         if (level == 5) {
            backtracking = false;
         }
      } while (backtracking);
      if (str != null) {
         str.setLoesungsZellen(freieZellen);
      }
      if (endCheck()) {
         return level;
      } else {
         return -1;
      }
   }

   /**
    * Wertet Straßen der Länge Zwei aus.
    *
    * @param stra Infos füe eine 2er-StrBereich
    * @return Anzahl der vorgenommenen Änderungen
    */
   private int check2erStra(StrBereich stra) {
      int w, knd;
      int n1, n2;
      int mKnd;
      int mod = 0;
      int geaendert = 0;
      int[] wrt = new int[stra.len];
      Position[] pos = stra.pos;

      for (int i = 0; i < stra.len; i++) {
         wrt[i] = strFeld[pos[i].z][pos[i].s] & KLIST;
      }
      if (wrt[0] <= LWERT && wrt[1] > LWERT
            || wrt[0] > LWERT && wrt[1] <= LWERT) {
         mod = 1;
      } else if (wrt[0] > LWERT && wrt[1] > LWERT) {
         mod = 2;
      }
      switch (mod) {
         case 0:
            return 0;
         case 1:
            // erste Zelle ist nicht gelöst
            if (wrt[0] > LWERT) {
               n1 = getAnzahlKnd(wrt[0]);
               if (n1 > 1) {
                  wrt[0] &= (KMASKE[wrt.length][wrt[1] & ZAHL]);
                  n2 = getAnzahlKnd(wrt[0]);
                  geaendert += n1 - n2;
               } else {
                  n2 = 1;
                  geaendert++;
               }
               // nur noch ein Kandidat vertreten
               if (n2 == 1) {
                  w = getKndWert(wrt[0]);
                  strFeld[pos[0].z][pos[0].s] = w | LWF;
                  geaendert += delKnd4Feld(LWF << w, pos[0].z, pos[0].s);
               } else if (n2 < n1) {
                  strFeld[pos[0].z][pos[0].s] = (wrt[0] | LWF | n2);
               }
            } else {
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
                  w = getKndWert(wrt[1]);
                  strFeld[pos[1].z][pos[1].s] = LWF | w;
                  geaendert += delKnd4Feld(LWF << w, pos[1].z, pos[1].s);
               } else if (n2 < n1) {
                  strFeld[pos[1].z][pos[1].s] = (wrt[1] | LWF | n2);
               }
            }
            break;
         case 2:
            // beide Zellen sind noch frei - 1. Zelle
            n1 = getAnzahlKnd(wrt[0]);
            mKnd = wrt[0] & ((wrt[1] & ~LWERT) >> 1);
            mKnd |= wrt[0] & ((wrt[1] & ~LWERT) << 1);
            n2 = getAnzahlKnd(mKnd);
            geaendert += n1 - n2;
            if (mKnd > 0) {
               if (n2 == 1) {
                  knd = getKndWert(mKnd);
                  strFeld[pos[0].z][pos[0].s] = LWF | knd;
                  geaendert += delKnd4Feld(LWF << knd, pos[0].z, pos[0].s);
               } else if (n1 > n2) {
                  mKnd |= (LWF | n2);
                  strFeld[pos[0].z][pos[0].s] = mKnd;
               }
            }
            // 2. Zelle
            n1 = getAnzahlKnd(wrt[1]);
            mKnd = wrt[1] & ((wrt[0] & ~LWERT) >> 1);
            mKnd |= wrt[1] & ((wrt[0] & ~LWERT) << 1);
            n2 = getAnzahlKnd(mKnd);
            geaendert += n1 - n2;
            if (mKnd > 0) {
               if (n2 == 1) {
                  knd = getKndWert(mKnd);
                  strFeld[pos[1].z][pos[1].s] = LWF | knd;
                  geaendert += delKnd4Feld(LWF << knd, pos[1].z, pos[1].s);
               } else if (n2 < n1) {
                  mKnd |= (LWF | n2);
                  strFeld[pos[1].z][pos[1].s] = mKnd;
               }
            }
      }
      int sKnd = get_sicheretKnd(stra);
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
   private int check38erStra(StrBereich stra) {
      int n1, n2;
      int geaendert = 0;
      int[] wrt = new int[stra.len];
      Position[] pos = stra.pos;
      // Zellwerte eintragen
      for (int i = 0; i < stra.len; i++) {
         wrt[i] = strFeld[pos[i].z][pos[i].s] & KLIST;
      }
      int mKnd = get_moeglicheKnd(stra);
      if (mKnd > 0) {
         for (int i = 0; i < stra.len; i++) {
            if (wrt[i] > LWERT) {
               n1 = getAnzahlKnd(wrt[i]);
               wrt[i] &= mKnd;
               n2 = getAnzahlKnd(wrt[i]);
               geaendert += n1 - n2;
               if (n2 == 1) {
                  int w = getKndWert(wrt[i]);
                  strFeld[pos[i].z][pos[i].s] = LWF + w;
                  geaendert++;
                  geaendert += delKnd4Feld(wrt[i], pos[i].z, pos[i].s);
                  for (int iw = 0; iw < stra.len; iw++) {
                     wrt[iw] = strFeld[pos[iw].z][pos[iw].s];
                  }
               } else if (n2 < n1) {
                  wrt[i] |= LWF + n2;
                  strFeld[pos[i].z][pos[i].s] = wrt[i];
               }
            }
         }
      }
      int sKnd = get_sicheretKnd(stra);
      if (sKnd > 0) {
         geaendert += delKnd4Reihe(stra, sKnd);
      }
      return geaendert;
   }

   /**
    * Wertet Straßen der Länge Neun aus.
    *
    * @param stra Infos füe eine 9er-StrBereich
    * @return Anzahl der vorgenommenen Änderungen
    */
   private int check9erStra(StrBereich stra) {
      int geaendert = 0;
      int[] wrt = new int[9];
      Position[] pos = stra.pos;

      // Zellwerte eintragen und dabei Singles abfangen
      for (int i = 0; i < stra.len; i++) {
         wrt[i] = strFeld[pos[i].z][pos[i].s] & KLIST;
         if (wrt[i] > LWF) {
            int nk = getAnzahlKnd(wrt[i]);
            if (nk == 1) {
               int w = getKndWert(wrt[i]);
               strFeld[pos[i].z][pos[i].s] = LWF | w;
               geaendert++;
               geaendert += delKnd4Feld(LWF << w, pos[i].z, pos[i].s);
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
         if (c == 1) {
            strFeld[pos[li].z][pos[li].s] = LWF | k;
            geaendert++;
            geaendert += delKnd4Feld(LWF << k, pos[li].z, pos[li].s);
         }
      }
      if (!backtracking && geaendert == 0) {
         geaendert += versteckteGruppen(stra, 2);
      }
      if (!backtracking && geaendert == 0) {
         geaendert += versteckteGruppen(stra, 3);
      }
      return geaendert;
   }

   /**
    * Prüft für ein StrBereich die Kandidaten- bzw. Wertefolge auf die erforderliche
    * unterbrechungsfreie Länge
    *
    * @param stra aktueller StrBereich-Bereich
    * @return Anzahl von durchgeführten Änderungen, -1 wenn leere Zelle
    */
   private int checkKandidatenfolge(StrBereich stra, boolean lev5) {
      int geaendert = 0;
      int knd;
      int[] wrt = new int[stra.len];
      Position[] pos = stra.pos;
      // Kandidaten eintragen
      for (int i = 0; i < stra.len; i++) {
         wrt[i] = strFeld[pos[i].z][pos[i].s] & KLIST;
         if (wrt[i] <= LWERT) {
            wrt[i] = LWF << (wrt[i] & ZAHL);    // Vorgabewert
         } else {
            wrt[i] &= AKND;
         }
      }
      // alle Kandidaten durchprüfen
      for (int k = 0x20; k <= 0x2000; k <<= 1) {
         int tmpK;
         for (int i = 0; i < stra.len; i++) {
            tmpK = wrt[i];
            if (wrt[i] > LWERT && (wrt[i] & k) == k) {
               wrt[i] = k;
               knd = 0;
               for (int j = 0; j < stra.len; j++) {
                  knd |= wrt[j];
               }
               if (isFolgestoerung(knd, stra.len)) {
                  wrt[i] = tmpK & ~k;
                  strFeld[pos[i].z][pos[i].s] &= ~k;
                  strFeld[pos[i].z][pos[i].s]--;
                  geaendert++;
               } else {
                  wrt[i] = tmpK;
               }
            }
         }
      }
      // auf Fehler checken, wichtig für Extreme!
      if (lev5) {
         for (int z = 0; z < 9; z++) {
            for (int s = 0; s < 9; s++) {
               if (strFeld[z][s] == 0 || strFeld[z][s] == 16) {
                  geaendert = -1;
                  break;
               }
            }
         }
      }
      return geaendert;
   }

   /**
    * Überprüft, ob eine bitcodierte Kandidatenfolge ohne Unterbrechung eine geforderte Länge hat.
    *
    * @param knd zu prüfende Kandidatenfolge
    * @param len geforderte Länge
    * @return true bei falscher Länge, sonst false
    */
   private boolean isFolgestoerung(int knd, int len) {
      int c = 0;
      for (int k = MINK; k <= MAXK; k <<= 1) {
         if ((knd & k) == k) {
            c++;
         } else {
            c = 0;
         }
         if (c >= len) {
            break;
         }
      }
      return c < len;
   }

   /**
    * Prüft das gesamte StrBereich-Feld auf nackte Zweier- und Dreiergruppen und wertet diese aus.
    * @param ngrp Gruppengröße (2 - 4)
    * @return Anzahl der Änderungen
    */
    private int nackteGruppen(int ngrp) {
        int geaendert = 0;
        int[] wrt = new int[9];
        int[] test = new int[9];    // Liste der freien Zellen
        int z, s;
        int crz;                    // Zähler relevante Zellen
        int cfz;                    // Zähler Kandidaten-Zellen
        int nk, n1, n2;
        int zelle;

        // alle horizontalen und vertikale Reihen bearbeiten
        for (int r = 0; r < 18; r++) {
            // erst Zeile für Zeile, dann Spalte für Spalte
            crz = 0;                    // Zähler relevante Zellen
            cfz = 0;                    // Zähler Kandidaten-Zellen
            for (int i = 0; i < 9; i++) {
                // Reihe einlesen und auswerten
                zelle = ZELLINDEX_2D[r][i];
                z = zelle & 15;
                s = zelle >>> 4;
                wrt[i] = strFeld[z][s] & AKND;
                if (wrt[i] >= SZELLE || (wrt[i] & AKND) == 0) {
                    continue;
                }
                nk = getAnzahlKnd(wrt[i]);
                cfz++;
                if (nk <= ngrp) {
                    test[crz] = i;
                    crz++;
                }
            }
            if (crz >= ngrp) {
                boolean gefunden = false;
                int ergb = 0;

                switch (ngrp) {
                    case 2:
                        for (int a = 0; a < crz - 1; a++) {
                            for (int b = a + 1; b < crz; b++) {
                                if (wrt[test[a]] == wrt[test[b]]) {
                                    ergb = AKND & wrt[test[a]];
                                    gefunden = true;
                                    break;
                                }
                            }
                            if (gefunden && cfz > ngrp) {
                                for (int i = 0; i < 9; i++) {
                                    if (((wrt[i] & AKND) > 0)
                                            && (wrt[i] & AKND) != ergb) {
                                        zelle = ZELLINDEX_2D[r][i];
                                        z = zelle & 15;
                                        s = zelle >>> 4;
                                        if ((strFeld[z][s] | ergb) == ergb) {
                                            continue;
                                        }
                                        n1 = getAnzahlKnd(strFeld[z][s]);
                                        strFeld[z][s] &= ~ergb;
                                        n2 = getAnzahlKnd(strFeld[z][s]);
                                        if (n1 > n2) {
                                            strFeld[z][s] &= ~ZAHL;
                                            strFeld[z][s] |= n2;
                                            geaendert = n1 - n2;
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
                        for (int a = 0; a <= crz - 3; a++) {
                            for (int b = a + 1; b <= crz - 2; b++) {
                                for (int c = b + 1; c <= crz - 1; c++) {
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
                            if (gefunden && cfz > ngrp) {
                                for (int i = 0; i < 9; i++) {
                                    if ((((wrt[i] & AKND) | ergb) == ergb)
                                            || ((wrt[i] & AKND) == 0)) {
                                        continue;
                                    }
                                    zelle = ZELLINDEX_2D[r][i];
                                    z = zelle & 15;
                                    s = zelle >>> 4;
                                    n1 = getAnzahlKnd(strFeld[z][s]);
                                    strFeld[z][s] &= ~ergb;
                                    n2 = getAnzahlKnd(strFeld[z][s]);
                                    if (n1 > n2) {
                                        strFeld[z][s] &= ~ZAHL;         // Anzahl Knd ausblenden
                                        strFeld[z][s] |= n2;            // und neue einsetzen
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
   private int versteckteGruppen(StrBereich stra, int ngrp) {
      BereichsAuswertung brAuswrt = new BereichsAuswertung();
      int geaendert = 0;
      int knd, nknd;
      int[] grpZelle = new int[ngrp];
      int r, z, s;
      int n1, n2;

      if (stra.isZeile) {
         r = stra.ezPos.z;
      } else {
         r = stra.ezPos.s + 9;
      }
      knd = getKandidaten(r);
      if (knd == 0) {
         return 0;
      }
      nknd = NKND[knd];
      if (nknd <= ngrp) {
         return 0;
      }
      int kombinationen = brAuswrt.getKndKombintionen(ngrp, nknd);
      int[] kndMasken = new int[kombinationen];
      brAuswrt.erzeugeKndMasken(kndMasken, knd, ngrp);
      // alle möglichen Kombinationen durchchecken
      for (int m = 0; m < kombinationen; m++) {
         int mGruppe = kndMasken[m];
         int zaehler = 0;
         for (int i = 0; i < 9; i++) {
            if (r < 9) {
               z = r;
               s = i;
            } else {
               z = i;
               s = r - 9;
            }
            if ((strFeld[z][s] & mGruppe) > 0) {
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
         for (int i = 0; i < 9; i++) {
            int zelle = ZELLINDEX_2D[r][i];
            z = zelle & 15;
            s = zelle >>> 4;
            if ((strFeld[z][s] & mGruppe) > 0) {
               grpZelle[p] = zelle;
               p++;
               if ((strFeld[z][s] & (~mGruppe & AKND)) > 0) {
                  zusatzKnd = true;
               }
            }
         }
         if (!zusatzKnd) {
            continue;
         }
         for (int i = 0; i < grpZelle.length; i++) {
            z = grpZelle[i] & 15;
            s = grpZelle[i] >>> 4;
            n1 = getAnzahlKnd(strFeld[z][s]);
            strFeld[z][s] &= mGruppe;
            n2 = getAnzahlKnd(strFeld[z][s]);
            strFeld[z][s] |= n2;
            geaendert += n1 - n2;
         }
      }
      if (geaendert > 0) {
         bewertung += ngrp * 2;
      }
      return geaendert;
   }

   /**
    * Durchsucht das gesamte Feld nach Zellen mit nur noch einem Kandidaten (Trüffel) und weist
    * solche der jeweiligen Zelle als Lösungswert zu.
    *
    * @return Anzahl der Zuweisungen
    */
   private int setTrueffel() {
      int gefunden = 0;
      boolean trueffel;
      int[] wrt = new int[9];
      do {
         trueffel = false;
         for (int r = 0; r < 18; r++) {
            for (int i = 0; i < 9; i++) {
               int zelle = ZELLINDEX_2D[r][i];
               int z = zelle & 15;
               int s = zelle >>> 4;
               wrt[i] = strFeld[z][s] & AKND;
               if (wrt[i] >= SZELLE || (wrt[i] & AKND) == 0) {
                  continue;
               }
               int nk = getAnzahlKnd(wrt[i]);
               // zuweisbarer Kandidat gefunden
               if (nk == 1) {
                  int lw = getKndWert(wrt[i]);
                  strFeld[z][s] = lw | LWF;
                  gefunden++;
                  trueffel = true;
                  gefunden += delKnd4Feld(LWF << lw, z, s);
               }
            }
         }
      } while (trueffel);
      return gefunden;
   }

   /**
    * Entfernt für einen gesetzten Wert in dessn Sichtbereich alle auszuschließende Kandidaten
    *
    * @param knd auszuschließender Kandidat als Bitmaske
    * @param zl Zeile des gesetzten Wertes
    * @param sp Spalte des gesetzten Wertes
    * @return Anzahl der Änderungen
    */
   private int delKnd4Feld(int knd, int zl, int sp) {
      int geaendert = 0;
      int n1, n2;
      knd &= AKND;
      for (int s = 0; s < 9; s++) {
         n1 = getAnzahlKnd(strFeld[zl][s]);
         if ((strFeld[zl][s] & knd) == knd) {
            strFeld[zl][s] &= ~knd;
            strFeld[zl][s] -= 1;
            n2 = getAnzahlKnd(strFeld[zl][s]);
            if (n2 != n1) {
               geaendert++;
            }
         }
      }
      for (int z = 0; z < 9; z++) {
         n1 = getAnzahlKnd(strFeld[z][sp]);
         if ((strFeld[z][sp] & (AKND & knd)) == knd) {
            strFeld[z][sp] &= ~knd;
            strFeld[z][sp] -= 1;
            n2 = getAnzahlKnd(strFeld[z][sp]);
            if (n2 != n1) {
               geaendert++;
            }
         }
      }
      return geaendert;
   }

   /**
    * Entfernt für die Reihe, in der eine StrBereich liegt, bestimmte Kandidaten außerhalb der
    * StrBereich.
    *
    * @param stra StrBereich, für die entfernt werden soll
    * @param knd Kandidaten, die entfernt werden sollen
    * @return - Anzahl der entfernten Kandidaten
    */
   private int delKnd4Reihe(StrBereich stra, int knd) {
      int entfernt = 0;
      int spalte, zeile;
      int sperre_a, sperre_e;
      int n1, n2;

      if (stra.isZeile) {
         zeile = stra.ezPos.z;
         sperre_a = stra.ezPos.s;
         sperre_e = stra.lzPos.s;
         for (int s = 0; s < 9; s++) {
            if (s < sperre_a || s > sperre_e) {
               n1 = getAnzahlKnd(strFeld[zeile][s]);
               if ((strFeld[zeile][s] & knd) > 0) {
                  strFeld[zeile][s] &= ~knd;
                  n2 = getAnzahlKnd(strFeld[zeile][s]);
                  strFeld[zeile][s] &= ~ZAHL;
                  strFeld[zeile][s] += n2;
                  if (n2 != n1) {
                     entfernt++;
                  }
               }
            }
         }
      } else {
         spalte = stra.ezPos.s;
         sperre_a = stra.ezPos.z;
         sperre_e = stra.lzPos.z;
         for (int z = 0; z < 9; z++) {
            if (z < sperre_a || z > sperre_e) {
               n1 = getAnzahlKnd(strFeld[z][spalte]);
               if ((strFeld[z][spalte] & knd) > 0) {
                  strFeld[z][spalte] &= ~knd;
                  n2 = getAnzahlKnd(strFeld[z][spalte]);
                  strFeld[z][spalte] &= ~ZAHL;
                  strFeld[z][spalte] += n2;
                  if (n2 != n1) {
                     entfernt++;
                  }
               }
            }
         }
      }

      return entfernt;
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
         if (wrt[i] < LWERT) {
            knd |= LWF << (wrt[i] & ZAHL);
            min = (wrt[i] & ZAHL) < min ? (wrt[i] & ZAHL) : min;
            max = (wrt[i] & ZAHL) > max ? (wrt[i] & ZAHL) : max;
         } else {
            knd |= (wrt[i] & ~LWERT);
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
               min = getKndWert(get_minKnd(mk));
               max = getKndWert(get_maxKnd(mk));
               // wir haben größten und kleinsten Kandidaten
               for (int i = 0; i < stra.len; i++) {
                  if (getAnzahlKnd(wrt[i]) > 1
                        && getAnzahlKnd(wrt[i]) < 9) {
                     int zmin = getKndWert(get_minKnd(wrt[i]));
                     int zmax = getKndWert(get_maxKnd(wrt[i]));
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
            min = getKndWert(get_minKnd(mk));
            max = getKndWert(get_maxKnd(mk));
            for (int i = 0; i < stra.len; i++) {
               if (getAnzahlKnd(wrt[i]) > 1
                     && getAnzahlKnd(wrt[i]) < 4) {
                  int zmin = getKndWert(get_minKnd(wrt[i]));
                  int zmax = getKndWert(get_maxKnd(wrt[i]));
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
   private int get_sicheretKnd(StrBereich stra) {
      int sk = 0;             // sichere Kandidaten
      int vk;                 // Anzahl der vorhandenen Kandidaten
      int len = stra.len;
      int knd = 0, min = 10, max = 0;
      int[] wrt = new int[stra.len];

      for (int i = 0; i < stra.len; i++) {
         wrt[i] = strFeld[stra.pos[i].z][stra.pos[i].s] & KLIST;
         // Vorgabe- oder Lösungswert
         if (wrt[i] < LWERT) {
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
    * Ermittelt den Wert für einen Kandidaten
    *
    * @param knd bitcodierte Kandidaten
    * @return ermittelter Zuweisungswert
    */
   private int getKndWert(int knd) {
      knd &= AKND;
      switch (knd) {
         case 32:
            return 1;
         case 64:
            return 2;
         case 128:
            return 3;
         case 256:
            return 4;
         case 512:
            return 5;
         case 1024:
            return 6;
         case 2048:
            return 7;
         case 4096:
            return 8;
         case 8192:
            return 9;
      }
      return 0;
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
    * Ermittelt die in einer Reihe, Zeile oder Spalte, noch vorhandenen Kandidaten.
    *
    * @param r erste Position der Reihe, bei Zeilen von 0 bis 8, bei Spalten 9 bis 17.
    * @return alle in der Reihe noch vertretenen Kandidaten
    */
   private int getKandidaten(int r) {
      int kandidaten = 0;
      int z, s;
      for (int h = 0; h < 9; h++) {
         if (r < 9) {
            // Zeile
            z = r;
            s = h;
         } else {
            // Spalte
            z = h;
            s = r - 9;
         }
         kandidaten |= strFeld[z][s] & AKND;
      }
      return kandidaten >>> 5;
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
    * Erstellt für Str9ts Aufgabe die Kandidatenliste
    */
   private void setKandidaten() {
      // erst alle Kandidaten setzen
      for (int z = 0; z < 9; z++) {
         for (int s = 0; s < 9; s++) {
            if (strFeld[z][s] == 0
                  || (strFeld[z][s] < SZELLE && strFeld[z][s] > 9)) {
               strFeld[z][s] = 0x3FF9;
            }
         }
      }
      // Kandidaten für Vorgabe- und Sperrwerte entfernen
      for (int z = 0; z < 9; z++) {
         for (int s = 0; s < 9; s++) {
            if ((strFeld[z][s] & LWF) == 0
                  && (strFeld[z][s] & ZAHL) > 0) {
               entferneKandidaten((strFeld[z][s] & ZAHL), z, s);
            }
         }
      }
      for (int z = 0; z < 9; z++) {
         System.arraycopy(strFeld[z], 0, aufgabe, z * 9, 9);
      }
   }

   /**
    * Entfernt für eine Zelle mit einem Sperr- oder Vorgabewert die Kandidaten.
    *
    * @param k zu entfernender Kandidat
    * @param zl Zeile aus der k zu entfernen ist
    * @param sp Spalte aus der k zu entfernen ist
    */
   private void entferneKandidaten(int k, int zl, int sp) {
      int knd = LWF << k;
      for (int s = 0; s < 9; s++) {
         if ((strFeld[zl][s] & knd) == knd) {
            strFeld[zl][s] &= ~knd;
            strFeld[zl][s] -= 1;
         }
      }
      for (int z = 0; z < 9; z++) {
         if ((strFeld[z][sp] & (AKND & knd)) == knd) {
            strFeld[z][sp] &= ~knd;
            strFeld[z][sp] -= 1;
         }
      }
   }

   /**
    * Entfernt alle Lösungswerte
    */
   private void entferneLoesungswerte() {
      for (int i = 0; i < 81; i++) {
         if ((loesung[i] & LWF) > 0) {
            loesung[i] = 0;
         }
      }
   }

   /**
    * Überprüft das aktuelle Stradoku auf doppelte Werte in den Reihen
    *
    * @return true, wenn kein doppelten Werte gefunden, sonst false
    */
   private boolean endCheck() {
      // Alle Reihen auf doppelte Werte prüfen
      int test, k;
      // in den Zeilen
      for (int z = 0; z < 9; z++) {
         test = 0;
         for (int s = 0; s < 9; s++) {
            if ((strFeld[z][s] & AKND) > 0) {
               continue;
            }
            k = strFeld[z][s] & ZAHL;
            if (k == 0) {
               continue;
            }
            if ((test & (1 << k)) > 0) {
               return false;
            } else {
               test |= (1 << k);
            }
         }
      }
      // in den Spalten
      for (int s = 0; s < 9; s++) {
         test = 0;
         for (int z = 0; z < 9; z++) {
            k = strFeld[z][s] & ZAHL;
            if (k == 0) {
               continue;
            }
            if ((test & (1 << k)) > 0) {
               return false;
            } else {
               test |= (1 << k);
            }
         }
      }
      return true;
   }

   /**
    * Prüft alle Bereiche und setzt für gelöste Bereiche das geloest-Flag.
    */
   private void setGeloest() {
      int i = 0;
      boolean geloest;
      while (strListe[i].len > 0) {
         if (!strListe[i].geloest) {
            geloest = true;
            for (int p = 0; p < strListe[i].len; p++) {
               if (strFeld[strListe[i].pos[p].z][strListe[i].pos[p].s] > LWRT) {
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
            if (strFeld[stra.pos[p].z][stra.pos[p].s] >= ZAHL) {
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
    * Überprüft die Anzahl freier Zellen.
    * @return Anzahl
    */
   private int freieZellen() {
      int frei = 0;
      for (int i = 0; i <= 80; i++) {
         if (aufgabe[i] < SZELLE && aufgabe[i] > 9) {
            frei++;
         }
      }         
      return frei;
   }
}
