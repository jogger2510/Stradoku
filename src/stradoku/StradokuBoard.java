/**
 * StradokuBoard.java ist Teil des Programmes kodelasStradoku
 *
 * Umgearbeitet am:             03.07.2017 12:00
 * Letzte Änderung:             29.12.2024 18:55
 *
 * Copyright (C) Konrad Demmel, 2017 - 2024
 */
package stradoku;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * StradokuBoard behandelt alle Ein- und Ausgaben für das Stradokusboard
 */
public class StradokuBoard extends javax.swing.JPanel implements GlobaleObjekte {

   private static final Color MF_BLAU = new Color(200, 220, 255);
   private static final Color MF_GRUEN = new Color(200, 255, 200);
   private static final Color MF_OKER = new Color(255, 220, 160);
   private static final Color MF_ROT = new Color(255, 180, 180);
   private static final Color MF_VIOLETT = new Color(255, 200, 255);
   private static Color MF_FILTER = new Color(180, 180, 180);
   private static final Color SZ_DGRAU = new Color(80, 80, 80);
   private static final Color LWERT = new Color(153, 51, 0);
   private static final Color SEL_B = new Color(240, 0, 0);
   private static final Color SEL_W = new Color(220, 0, 0);
   private static final Color HGREEN = new Color(220, 235, 220);
   private static final Font WERT_FONT = new Font("Arial", Font.BOLD, 26);
   private static final Font KND_FONT = new Font("Ebrima", Font.PLAIN, 13);
   private static final Font KNDP_FONT = new Font("Arial", Font.BOLD, 18);
   private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 12);

   private static final int ALINIE = 3;
   private static final int BORDER = 21 + ALINIE;
   private static final int RAND = BORDER + ALINIE;
   private static final int ZELLSIZE = 45;
   private static final int ZLINIE = 1;
   private static final int BEREICHE = 9;
   private static final int FELDSIZE = ZELLSIZE * BEREICHE + 8 * ZLINIE;
   private static final int BOARDSIZE = FELDSIZE + 2 * RAND;
   private static final int[] ZELLPOSLO = {0, 46, 92, 138, 184, 230, 276, 322, 368};
   private static final int[] ZELLPOSRU = {45, 91, 137, 183, 229, 275, 321, 367, 413};
   // Korrekturwerte für die Ausgabe der Kandidaten
   private static final int[] XKKF = {1, 3, 19, 35, 3, 19, 35, 3, 19, 35};
   private static final int[] YKKF = {0, 12, 12, 12, 27, 27, 27, 42, 42, 42};
//    private static final int[] XKKF = {1, 5, 19, 33, 5, 19, 33, 5, 19, 33};
//    private static final int[] YKKF = {0, 14, 14, 14, 27, 27, 27, 40, 40, 40};

   private final int[] linepos = {72, 118, 164, 210, 256, 302, 348, 394};
   private static final int TEXT_XOFFSET = 15;
   private static final int TEXT_YOFFSET = 33;
   // "umgebogene" Tastenwerte für Ziffernblock mit Shift
   private static final int VK_LR_ARROW = 153;                 // Links- Rechts-Pfeiltaste
   private static final int VK_SHIFTNUM_0 = 155;
   private static final int VK_SHIFTNUM_1 = 35;
   private static final int VK_SHIFTNUM_2 = 40;
   private static final int VK_SHIFTNUM_3 = 34;
   private static final int VK_SHIFTNUM_4 = 37;
   private static final int VK_SHIFTNUM_5 = 12;
   private static final int VK_SHIFTNUM_6 = 39;
   private static final int VK_SHIFTNUM_7 = 36;
   private static final int VK_SHIFTNUM_8 = 38;
   private static final int VK_SHIFTNUM_9 = 33;
   private static final long serialVersionUID = 1L;

   private final Stradoku strApp;
   private final StradokuOrg strOrg;
   private int select;
   private int zelle;
   private int wert;
   private int kndMod;
   private int anzKnd;
   private int kndFilter;
   private int fckf;                                         // Konfigurationswert für Filterfarbe
   private boolean isKListe;
   private boolean isNotiz;
   private boolean loesungsModus;
   private boolean kndPnkt;
   private boolean showSelector;

   /**
    * Konstruktor
    *
    * @param mFrame Referenz zum Hauptfenster
    * @param strKlasse Referenz zur StradokuOrg-Klasse
    */
   public StradokuBoard(Stradoku mFrame, StradokuOrg strKlasse) {
      setPreferredSize(new Dimension(469, 469));

      addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent evt) {
            myMousePressed(evt);
         }
      });

      addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent evt) {
            try {
               myKeyPressed(evt);
            } catch (IOException ex) {
               Logger.getLogger(StradokuBoard.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      });

      setFocusTraversalKeysEnabled(false);
      setSize(new Dimension(BOARDSIZE, BOARDSIZE));
      setBackground(Color.WHITE);
      strApp = mFrame;
      strOrg = strKlasse;
      showSelector = true;
      select = 40;
      setFocusable(true);
   }

   /**
    * Überschreibt paintComponent des Erblassers und veranlasst alle Ausgaben für das Stradoku-Feld
    *
    * @param g Referenz auf aktuelles Grafikobjekt
    */
   @Override
   public void paintComponent(Graphics g) {
      ausgebenBoard(g);
      ausgebenWerte(g);
   }

   /**
    * Zeichnet den Rahmen mit Beschriftung und das Stradoku-Feld
    *
    * @param g Referenz auf aktuelles Graphikobjekt
    */
   private void ausgebenBoard(Graphics g) {
      // Umfassung ausgeben
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, BOARDSIZE, BOARDSIZE);
      g.setColor(new Color(0, 102, 0));
      g.fillRect(ALINIE, ALINIE,
            BOARDSIZE - 2 * ALINIE, BOARDSIZE - 2 * ALINIE);
      g.setColor(new Color(204, 204, 204));
      g.setFont(LABEL_FONT);
      for (int i = 0; i < 9; i++) {
         g.drawString("" + (i + 1), 46 + ZELLPOSLO[i], 19);
         g.drawString("" + (i + 1), 46 + ZELLPOSLO[i], BOARDSIZE - 9);
         if (i < 8) {
            g.drawString("" + (char) (i + 65),
                  10, 55 + ZELLPOSLO[i]);
         } else {
            g.drawString("" + (char) (i + 66),
                  10, 55 + ZELLPOSLO[i]);
         }
         g.drawString("" + (i + 1), BOARDSIZE - 17, 55 + ZELLPOSLO[i]);
      }
      // Stradoku-Feld ausgeben
      g.setColor(Color.BLACK);
      g.fillRect(RAND - ALINIE, RAND - ALINIE,
            FELDSIZE + 2 * ALINIE, FELDSIZE + 2 * ALINIE);
      if (loesungsModus && !strApp.getTestModus()) {
         g.setColor(Color.WHITE);
      } else {
         g.setColor(HGREEN);
      }
      g.fillRect(RAND, RAND, FELDSIZE, FELDSIZE);
      g.setColor(Color.BLACK);

      for (int i = 0; i < BEREICHE; i++) {
         int basisPos = ZELLPOSRU[i] + RAND;
         g.drawLine(basisPos, RAND, basisPos, BOARDSIZE - RAND - 1);
         g.drawLine(RAND, basisPos, BOARDSIZE - RAND - 1, basisPos);
      }
   }

   /**
    * Gibt die Werte und Sperrzellen für das Stradoku-Feld aus.
    *
    * @param g - Referenz auf Graphikobjekt
    */
   private void ausgebenWerte(Graphics g) {
      kndFilter = strOrg.getFilterKnd();
      if (kndFilter > 0) {
         strApp.set_usedKndFi(true);
      }
      for (int i = 0; i < 81; i++) {
         int farbe;
         int z = i / 9;
         int s = i % 9;
         int yPos = ZELLPOSLO[z] + TEXT_YOFFSET + RAND;
         int xPos = ZELLPOSLO[s] + RAND + TEXT_XOFFSET;

         zelle = strOrg.getZelle(i, loesungsModus);
         boolean isSperrzelle = strOrg.isSperrzelle(i);
         boolean loesungsWert = false;

         if ((zelle & AKND) > 0x1F) {
            wert = 0;
            kndMod = 0;
            if (isNotiz) {
               kndMod = 2;
            } else if (isKListe) {
               kndMod = 1;
            }
         } else {
            wert = zelle & ZAHL;
            if ((zelle & LWF) == LWF) {
               loesungsWert = true;
            }
         }
         // Zuerst Hintergrund einfärben 
         if (isSperrzelle) {
            g.setColor(SZ_DGRAU);
            g.fillRect(ZELLPOSLO[s] + RAND,
                  ZELLPOSLO[z] + RAND,
                  ZELLSIZE, ZELLSIZE);
         } else if ((zelle & IS_MARK) > 0) {
            farbe = zelle & IS_MARK;
            getColor(g, farbe);
            g.fillRect(ZELLPOSLO[s] + RAND, ZELLPOSLO[z] + RAND,
                  ZELLSIZE, ZELLSIZE);
         } else if (kndFilter > 0) {
            if (isKandidat(kndMod, zelle, kndFilter)) {
               g.setColor(MF_FILTER);
               g.fillRect(ZELLPOSLO[s] + RAND, ZELLPOSLO[z] + RAND,
                     ZELLSIZE, ZELLSIZE);
            }
         }
         if (select == i && showSelector) {
            if ((zelle & SZELLE) == SZELLE) {
               g.setColor(SEL_B);
            } else {
               g.setColor(SEL_W);
            }
            g.drawRect(ZELLPOSLO[s] + RAND + 1,
                  ZELLPOSLO[z] + RAND + 1,
                  ZELLSIZE - 3, ZELLSIZE - 3);
         }
         // Jetzt kommen die Werte selbst dran
         if (wert > 9) {
            return;
         }
         if (wert > 0) {
            g.setFont(WERT_FONT);
            if (loesungsWert) {
               g.setColor(LWERT);
            } else if (isSperrzelle) {
               if (((zelle & IS_MARK) > 0)) {
                  farbe = zelle & IS_MARK;
                  getColor(g, farbe);
               } else {
                  g.setColor(Color.WHITE);
               }
            } else {
               g.setColor(Color.BLACK);
            }
            g.drawString("" + wert, xPos, yPos);
         } else if (!isSperrzelle && (isKListe || isNotiz)) {
            yPos = ZELLPOSLO[z] + RAND;
            xPos = ZELLPOSLO[s] + RAND;
            if (!kndPnkt) {
               g.setFont(KND_FONT);
            } else {
               g.setFont(KNDP_FONT);
            }
            g.setColor(Color.BLACK);
            for (int k = 1; k <= 9; k++) {
               if (isKandidat(kndMod, zelle, k)) {
                  if (!kndPnkt) {
                     g.drawString("" + k,
                           xPos + XKKF[k], yPos + YKKF[k]);
                  } else {
                     g.drawString(".",
                           xPos + XKKF[k] + 2, yPos + YKKF[k] - 2);
                  }
               }
            }
         }
      }
   }

   /**
    * Erledigt alle Mauseingaben im Bereich des Stradoku-Feldes
    *
    * @param evt Mausereignis
    */
   private void myMousePressed(MouseEvent evt) {
      int button = evt.getButton();
      if (button != 1) {
         evt.consume();
         return;
      }
      // Position der Mauseingabe bestimmen
      int x = evt.getX();
      int y = evt.getY();
      if (isLinienklick(x, y)) {
         evt.consume();
         return;
      }
      int i = getZellNdx(x, y);                           // zu behandlnde Zelle
      if (i < 0) {                                        // außerhalb einer Zelle?
         showSelector = !showSelector;
         evt.consume();
         repaint();
         return;
      }
      // Doppelklick auf schwarze Zelle abfangen ??
      if (evt.isShiftDown()
            && evt.getClickCount() > 1) {
         evt.consume();
         return;
      }
      showSelector = true;
      select = i;
      // für Selektierung der Zelle
      if (!evt.isControlDown()
            && !evt.isShiftDown()
            && evt.getClickCount() == 1) {
         evt.consume();
         repaint();
         return;
      }

      int klickPos = getPosKandidat(i, x, y);
      int k;  // = klickPos;
      zelle = strOrg.getZelle(i, loesungsModus);
      if (loesungsModus) {
         if (isNotiz && evt.isControlDown() && evt.isShiftDown()) {
            strOrg.entferneZellNotizen(select);
            evt.consume();
            repaint();
            return;
         }
         // Sperrzellen, mit oder ohne Werte, Vorgabewerte und gelöste Zellen werden übergangen
         if ((zelle & SZELLE) == SZELLE || (zelle & LWF) == 0
               || (zelle & AKND) == 0) {
            evt.consume();
            repaint();
            return;
         }
         anzKnd = getAnzahlKandidaten(zelle);        // je nach Modus für KListe oder Notizen
         // Werteingabe
         if (evt.isControlDown() || evt.getClickCount() == 2) {
            if (anzKnd == 1) {
               k = getSingleKandidat(zelle);
            } else {
               k = klickPos;
            }
            strOrg.setWert(i, k);
         } else if (evt.isShiftDown()) {                             // Kandidaten behandeln
            k = klickPos;
            if (!isKListe) {
               strOrg.notiereKandidat(i, k, evt.isShiftDown());
            } else {
               if (isKandidat(kndMod, zelle, k)) {
                  strOrg.entferneKandidat(i, k, true);
               }
            }
         }
      } else {                               // also Bearbeitungsmodus
         if (evt.isControlDown() || evt.getClickCount() == 2) {
            wert = zelle & ZAHL;
            if (wert > 0) {
               strOrg.setWert(i, 0);
            } else {
               strOrg.setWert(i, klickPos);
            }
         } else if (evt.isShiftDown()) {
            strOrg.setzeSperrzelle(i);
         }
      }
      evt.consume();
      repaint();
   }

   /**
    * Erledigt alle Tastatureingaben im Bereich des Stradoku-Feldes, soweit sie nicht als
    * Tastaturereignisse übernommen werden.
    *
    * @param evt Tastaturereignis
    */
   private void myKeyPressed(KeyEvent evt) throws IOException {
      int code = evt.getKeyCode();
      if (code > KeyEvent.VK_F2 && code < KeyEvent.VK_F10
            || code == KeyEvent.VK_CONTROL
            || code == KeyEvent.VK_SHIFT
            || code == KeyEvent.VK_NUM_LOCK
            || code == KeyEvent.VK_BACK_SPACE
            || code == KeyEvent.VK_TAB
            || code == KeyEvent.VK_CAPS_LOCK
            || evt.isControlDown() && (code == KeyEvent.VK_L
            || code == KeyEvent.VK_B
            || code == KeyEvent.VK_E)) {
         return;
      }
      boolean consum = false;
      
      if (code == KeyEvent.VK_SPACE) {
            showSelector = !showSelector;
            consum = true;
      }
      if (loesungsModus && code == KeyEvent.VK_K && evt.isShiftDown()
            && strApp.getKndModus() == KNDMODNOTIZ) {
         strOrg.copyKandidaten();
         consum = true;
      } else if (code == KeyEvent.VK_F10 && evt.isControlDown()) {
         strApp.checkDoppelEintraege();
         consum = true;
      } else if (code == KeyEvent.VK_F12 && evt.isControlDown()) {
         strApp.getArchivPosition();
         consum = true;
      } else if (!loesungsModus) {
         if (code == KeyEvent.VK_S) {
            strOrg.setzeSperrzelle(select);
            consum = true;
         } else if (code == KeyEvent.VK_F11 && evt.isControlDown()) {
            strOrg.setMaxSchwer();
            consum = true;
         } else if (code == KeyEvent.VK_DELETE && evt.isShiftDown()) {
            strOrg.entferneVorgaben(false);
            consum = true;
         } else if (code == KeyEvent.VK_DELETE && evt.isAltDown()) {
            strOrg.entferneVorgaben(true);
            consum = true;
         } else if (code == KeyEvent.VK_DELETE && evt.isControlDown()) {
            strOrg.restoreVorgaben();
            consum = true;
         } else if (code == KeyEvent.VK_DELETE) {
            strOrg.setWert(select, 0);
            consum = true;
         } else if (code == KeyEvent.VK_INSERT) {
            strOrg.setSperrZellenWerte(true);
            consum = true;
         } 
//         else if (code == KeyEvent.VK_SPACE) {
//            showSelector = !showSelector;
//            consum = true;
//         } 
      }
      if (consum) {
         evt.consume();
         repaint();
         return;
      }
      int eWert = -1;
      boolean isShift = evt.isShiftDown();
      if (evt.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
         eWert = getEingabewert(code, true, true);
         if (eWert >= 0 && eWert <= 9) {
            isShift = true;
         }
      }
      if (eWert == -1) {
         eWert = getEingabewert(code, true, false);
      }
      if (eWert == -1) {
         eWert = getEingabewert(code, false, false);
      }
      if (eWert == -1) {
         eWert = code;
      }
      if (loesungsModus) {
         // Filter über SpitzeKlammer-Taste setzen
         if (code == VK_LR_ARROW && (isKListe || isNotiz)) {
            kndFilter = strOrg.getFilterKnd();
            if (evt.isShiftDown()) {
               kndFilter--;
               if (kndFilter < 0) {
                  kndFilter = 9;
               }
            } else {
               kndFilter++;
               if (kndFilter > 9) {
                  kndFilter = 0;
               }
            }
            strOrg.setKndFilter(kndFilter);
            repaint();
            return;
         }
         if (eWert >= 0 && eWert <= 9) {
            if (isShift) {
               if (eWert == 0) {
                  strOrg.entferneNotizen(true);
                  return;
               } else if (strApp.getKndModus() > 0) {
                  if (isNotiz) {
                     strOrg.notiereKandidat(select, eWert, true);
                  } else {
                     strOrg.entferneKandidat(select, eWert, true);
                  }
               }
               return;
            } else if (evt.isControlDown()) {
               int kmod = strApp.getKndModus();
               if (kmod >= 0) {
                  kndFilter = eWert;
                  strOrg.setKndFilter(kndFilter);
               }
            } else {
               strOrg.setWert(select, eWert);
            }
         } else if (code == KeyEvent.VK_SPACE) {
            if (showSelector && !strApp.getTestModus() && select >= 0) {
               if (isKListe || isNotiz) {
                  zelle = strOrg.getZelle(select, loesungsModus);
                  anzKnd = getAnzahlKandidaten(zelle);
                  if (anzKnd == 1) {
                     int k = getSingleKandidat(zelle);
                     strOrg.setWert(select, k);
                  }
               }
            }
         }
//         if (code == KeyEvent.VK_SPACE && anzKnd != 1) {
//            showSelector = !showSelector;            
//         }
      } else {
         if (eWert >= 0 && eWert <= 9) {
            if (strOrg.isWert(select, eWert)) {
               // Wert bereits gesetzt, also entfernen
               strOrg.setWert(select, 0);
            } else {
               // setzen
               strOrg.setWert(select, eWert);
            }
         }
      }
      if (!evt.isControlDown() && (code >= KeyEvent.VK_PAGE_UP && code <= KeyEvent.VK_DOWN)) {
         auswertenNavigation(false, code);
      }
      repaint();
   }

   /**
    * Erledigt die Bewegung der selektierten Zelle
    *
    * @param strg Flag für Strg-Taste
    * @param taste Taste, die gedrückt wurde
    * @return true bei einer gültigen Bewegung, sonst false
    */
   private boolean auswertenNavigation(boolean strg, int taste) {
      showSelector = true;
      if (strg) {
         switch (taste) {
            case KeyEvent.VK_END:
               select = 80;
               break;
            case KeyEvent.VK_HOME:
               select = 0;
               break;
            default:
               return false;
         }
      } else {
         switch (taste) {
            case KeyEvent.VK_PAGE_UP:
               select = (select % 9);
               break;
            case KeyEvent.VK_PAGE_DOWN:
               select = (select % 9) + 72;
               break;
            case KeyEvent.VK_UP:
               select -= 9;
               if (select < 0) {
                  select += 81;
               }
               break;
            case KeyEvent.VK_DOWN:
               select += 9;
               if (select > 80) {
                  select -= 81;
               }
               break;
            case KeyEvent.VK_END:
               select = ((select / 9 + 1) * 9) - 1;
               break;
            case KeyEvent.VK_HOME:
               select = (select / 9) * 9;
               break;
            case KeyEvent.VK_LEFT:
               select--;
               if (select < 0) {
                  select = 80;
               }
               break;
            case KeyEvent.VK_RIGHT:
               select++;
               if (select > 80) {
                  select = 0;
               }
               break;
            default:
               return false;
         }
      }
      return true;
   }

   /**
    * Stellt fest, zu welcher Zelle im Schnittbereich von xpos und ypos liegt,
    *
    * @param xpos x-Position des letzten Mausklicks
    * @param ypos y-Position des letzten Mausklicks
    * @return Zellindex im Stradoku-Raster
    */
   private int getZellNdx(int xpos, int ypos) {
      int xp = xpos;
      int yp = ypos;
      if (xp <= RAND || xp >= BOARDSIZE - RAND
            || yp <= RAND || yp >= BOARDSIZE - RAND) {
         return -1;
      }
      xp -= RAND;
      yp -= RAND;
      int zeile = -1;
      int spalte = -1;
      int i = 0;
      boolean gefunden = false;
      do {
         if (yp >= ZELLPOSLO[i] && yp <= ZELLPOSRU[i]) {
            zeile = i;
            gefunden = true;
         } else {
            i++;
         }
      } while (!gefunden && i < 9);
      gefunden = false;
      i = 0;
      do {
         if (xp >= ZELLPOSLO[i] && xp <= ZELLPOSRU[i]) {
            spalte = i;
            gefunden = true;
         } else {
            i++;
         }
      } while (!gefunden && i < 9);
      if (zeile == -1 || spalte == -1) {
         return -1;
      } else {
         return zeile * 9 + spalte;
      }
   }

   /**
    * Ermittelt welcher Wert an einer bestimmten Zellposition angezeigt wird.
    *
    * @param i Index der Zelle, für die der Wert ermittelt werden soll
    * @param xpos X-Position der Abfragestelle
    * @param ypos Y-Position der Abfragestelle
    * @return Wert, welcher der angegebenen Position entspricht
    */
   private int getPosKandidat(int i, int xpos, int ypos) {
      int x = xpos;
      int y = ypos;
      x -= RAND + ZELLPOSLO[i % 9];
      y -= RAND + ZELLPOSLO[i / 9];
      x /= 15;
      y /= 15;
      int pos = y * 3 + x + 1;
      if (pos < 1 || pos > 9) {
         pos = 0;
      }
      return pos;
   }

   /**
    * Übergibt die Position der selektierten Zelle
    *
    * @return Zellposition
    */
   public int getSelect() {
      return select;
   }

   /**
    * Setzt aktive Zelle
    *
    * @param sel Zellposition
    */
   public void setSelect(int sel) {
      select = sel;
   }

   /**
    * Setzt den Kandidatenmodus
    *
    * @param modus keiner=0, Liste=1; Notizen=0
    */
   public void setKandidatenModus(int modus) {
      switch (modus) {
         case 1:
            isKListe = true;
            isNotiz = false;
            break;
         case 2:
            isNotiz = true;
            isKListe = false;
            break;
         default:
            isKListe = false;
            isNotiz = false;
            break;
      }
      repaint();
   }

   /**
    * Prüft, ob der Kandidat k in zelle vertreten ist.
    *
    * @param zelle Zellenwert
    * @param k zu prüfender Kandidat
    * @return true, wenn k in zelle vertreten ist, ansonsten false
    */
   private boolean isKandidat(int kmode, int zelle, int k) {
      boolean erg = false;
      if (kmode == KNDMODLISTE) {
         if (((zelle & AKND) >> (4 + k) & 1) == 1) {
            erg = true;
         }
      } else if (kmode == KNDMODNOTIZ) {
         if (((zelle & ANKND) >> (13 + k) & 1) == 1) {
            erg = true;
         }
      }
      return erg;
   }

   /**
    * Übernimmt den aktuellen Lösungsmodus bzw. Bearbeitungsmodus
    *
    * @param lm true wenn Lösungsmodus, sonst Bearbeitungsmodus
    */
   public void setLoesungsModus(boolean lm) {
      loesungsModus = lm;
   }

   /**
    * Übernimmt den Modus für die Kandidatenanzeige (Punkte oder Ziffern)
    *
    * @param punkte Flag für die Kandidatenanzeige
    */
   public void setKndAnzeige(boolean punkte) {
      kndPnkt = punkte;
      repaint();
   }

   /**
    * Liefert Stärke der Feldumrandung
    *
    * @return Breite der Feldumrandung
    */
   public static int getStrBorder() {
      return BORDER;
   }

   /**
    * Setzt die Klassenvariable "kndFilter" für einen bestimmten Filterwert.
    *
    * @param f der zu setztende Filterwert
    */
   public void setKndFilter(int f) {
      kndFilter = f;
   }

   /**
    * Prüft, ob es ein Mausklick auf eine Linie war.
    *
    * @param x horizontale Position des Klicks
    * @param y vertikale Position des Klicks
    * @return true, wenn auf eine Linie geklickt wurde, sonst false
    */
   private boolean isLinienklick(int x, int y) {
      boolean lineklick = false;
      for (int i = 0; i <= 7; i++) {
         if (x == linepos[i] || y == linepos[i]) {
            lineklick = true;
            break;
         }
      }
      return lineklick;
   }

   /**
    * Liefert den letzten in einer Zelle noch angezeigten Kandidaten
    *
    * @param knd die bitkodierten Kandidaten der Zelle
    * @return der gesuchte letzte Kandidat
    */
   private int getSingleKandidat(int zelle) {
      int pos = 0;
      if (isKListe) {
         pos = (zelle & AKND) >> 5;
      } else if (isNotiz) {
         pos = (zelle & ANKND) >> 14;
      }
      int wrt = 1;
      while (pos != 1) {
         pos >>= 1;
         wrt++;
      }
      return wrt;
   }

   /**
    * Korrigiert Tastatureingabe unter Berücksichtigung der Sondertasten
    *
    * @param code Tastaturcode
    * @param numblock Flag für aktivierten Nummernblock
    * @param shift Flag für gedrückte Shift Taste
    * @return korrigierte Tastaturcode
    */
   private int getEingabewert(int code, boolean numblock, boolean shift) {
      int eWert = -1;
      if (numblock) {
         if (shift) {
            switch (code) {
               case VK_SHIFTNUM_0:
                  eWert = 0;
                  break;
               case VK_SHIFTNUM_1:
                  eWert = 1;
                  break;
               case VK_SHIFTNUM_2:
                  eWert = 2;
                  break;
               case VK_SHIFTNUM_3:
                  eWert = 3;
                  break;
               case VK_SHIFTNUM_4:
                  eWert = 4;
                  break;
               case VK_SHIFTNUM_5:
                  eWert = 5;
                  break;
               case VK_SHIFTNUM_6:
                  eWert = 6;
                  break;
               case VK_SHIFTNUM_7:
                  eWert = 7;
                  break;
               case VK_SHIFTNUM_8:
                  eWert = 8;
                  break;
               case VK_SHIFTNUM_9:
                  eWert = 9;
                  break;
            }
         } else {
            switch (code) {
               case KeyEvent.VK_NUMPAD0:
                  eWert = 0;
                  break;
               case KeyEvent.VK_NUMPAD1:
                  eWert = 1;
                  break;
               case KeyEvent.VK_NUMPAD2:
                  eWert = 2;
                  break;
               case KeyEvent.VK_NUMPAD3:
                  eWert = 3;
                  break;
               case KeyEvent.VK_NUMPAD4:
                  eWert = 4;
                  break;
               case KeyEvent.VK_NUMPAD5:
                  eWert = 5;
                  break;
               case KeyEvent.VK_NUMPAD6:
                  eWert = 6;
                  break;
               case KeyEvent.VK_NUMPAD7:
                  eWert = 7;
                  break;
               case KeyEvent.VK_NUMPAD8:
                  eWert = 8;
                  break;
               case KeyEvent.VK_NUMPAD9:
                  eWert = 9;
                  break;
            }
         }
      } else {
         switch (code) {
            case KeyEvent.VK_0:
               eWert = 0;
               break;
            case KeyEvent.VK_1:
               eWert = 1;
               break;
            case KeyEvent.VK_2:
               eWert = 2;
               break;
            case KeyEvent.VK_3:
               eWert = 3;
               break;
            case KeyEvent.VK_4:
               eWert = 4;
               break;
            case KeyEvent.VK_5:
               eWert = 5;
               break;
            case KeyEvent.VK_6:
               eWert = 6;
               break;
            case KeyEvent.VK_7:
               eWert = 7;
               break;
            case KeyEvent.VK_8:
               eWert = 8;
               break;
            case KeyEvent.VK_9:
               eWert = 9;
               break;
         }
      }
      return eWert;
   }

   /**
    * Setzt gewählte Farbmarkierung
    *
    * @param g Grafikobjekt
    * @param farbe jeweilige Farbe
    */
   private void getColor(Graphics g, int farbe) {
      switch (farbe) {
         case M_ROSA:
            g.setColor(MF_ROT);
            break;
         case M_BLAU:
            g.setColor(MF_BLAU);
            break;
         case M_GRUEN:
            g.setColor(MF_GRUEN);
            break;
         case M_VIOLETT:
            g.setColor(MF_VIOLETT);
            break;
         case M_OKER:
            g.setColor(MF_OKER);
            break;
      }
   }

   /**
    * Setzt die Intensivitätswert für die graue Filterfarbe.
    *
    * @param cf
    */
   public void setFilterColor(int cf) {
      fckf = cf;
      MF_FILTER = new Color(180 + cf, 180 + cf, 180 + cf);
   }

   /**
    * Abfrage des Intensivitätswerts für die graue Filterfarbe
    *
    * @return aktueller Intensivitätswert
    */
   public int getFilterColor() {
      return fckf;
   }

   /**
    * Liefert die Anzahl der aktuell angezeigten Kandidaten für eine Zelle
    *
    * @param zelle Zu prüfende Zelle
    * @return Anzahl der Notizen
    */
   private int getAnzahlKandidaten(int zelle) {
      int wrt = 0;
      if (isKListe) {
         wrt = (zelle & AKND) >> 5;
      } else if (isNotiz) {
         wrt = (zelle & ANKND) >> 14;
      }
      return NKND[wrt];
   }

   /**
    * Setzt showSelect auf true.
    */
   public void setShowSelector() {
      showSelector = true;
   }
   
}
