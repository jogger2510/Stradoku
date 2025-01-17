/**
 * ArchivLevel.java ist Teil des Programmes kodelasStradoku
 *
 * Erstellt am:                 28.01.2020 19:05
 * Letzte Änderung:             14.10.2023 23:50
 *
 * Copyright (C) Konrad Demmel, 2020-2021
 */
package stradoku;

//import static stradoku.GlobaleObjekte.kopieren;
import static stradoku.GlobaleObjekte.kopieren;
import static stradoku.GlobaleObjekte.rotiereFeld;
import static stradoku.GlobaleObjekte.spiegleFeld;
import static stradoku.GlobaleObjekte.tauscheZellWerte;

/**
 * Aufgabe von ArchivLevel ist es, zum einen den Level für ein Stradoku zu ermitteln und
 * andererseits festzustellen, ob dieser Level für alle 16 Varianten des Stradoku gleich ist. 
 * Nur dann ist ein Stradoku archivtauglich.
 */
public class ArchivLevel implements GlobaleObjekte {

   private final int[] a_aufgabe = new int[81];
   private final int[] a_loesung = new int[81];
   private int level;

   /**
    * Prüfung auf Gleicheit des Levels aller Varianten
    *
    * @param aufgabe Referenz auf zu prüfendes Stradoku
    * @param loesung Referenz auf zurückzugebende Lösung
    * @param lvl Level von vorausgegangener Lösung
    * @return Ergebnis der Prüfung - wenn nicht archivtauglich wird dem Level ein Flag für
    * Abweichung hinzugefügt
    */
   public int getArchivLevel(int[] aufgabe, int[] loesung, int lvl) {
      level = lvl;
      if (level > 0 && level <= 5) {
         int fixLevel = level;
         kopieren(aufgabe, a_aufgabe, true);
         // hier Schleife für Archivtauglichkeit
         for (int i = 0; i < 16; i++) {
            rotiereFeld(a_aufgabe);
            if (i == 3 || i == 11) {
               tauscheZellWerte(a_aufgabe);
            } else if (i == 7) {
               spiegleFeld(a_aufgabe);
            }
            LevelSolver loeser = new LevelSolver(null,
                  a_aufgabe, a_loesung, false);
            int tmpLevel = loeser.loeseAufgabe();
            if (tmpLevel != level) {
               if (tmpLevel > 0) {
                  if (tmpLevel < fixLevel) {
                     level = fixLevel + 0x10;        // Flag für leichter
                  } else if (tmpLevel > fixLevel) {
                     level = fixLevel + 0x20;        // Flag für schwerer
                  }               
               } else {
                  level = fixLevel + 0x40;           // Flag für nicht zu lösen;
               } 
               break;
            }
         }
      }
      return level;
   }

}
