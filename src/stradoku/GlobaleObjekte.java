/**
 * GlobaleObjekte.java ist Teil des Programmes KODELAs Str9ts

 * Umgearbeitet am:             03.07.2017 12:00
 * Letzte Änderung:             24.01.2024 12:30
 *
 * Copyright (C) Konrad Demmel, 2018-2024
 */

package stradoku;

import java.io.File;

/**
 * Zusammenstellung verschiedener Konstanten, die von verschiedenen Klassen
 * benötigt werden.
 */
public interface GlobaleObjekte {

    /**
     * Liefert für jeden Kandidaten die Kandidatenmaske, beginnend mit Bit 1 für
     * Kandidat 1.
     */
    public static final int[] K_MASKE = {0, 0x1, 0x2, 0x4, 0x8,
        0x10, 0x20, 0x40, 0x80, 0x100};

    /**
     * Liefert für jeden Kandidaten die Kandidatenmaske, beginnend mit Bit 5 für
     * Kandidat 1.
     */
    public static final int[] LK_MASKE = {0, 0x20, 0x40, 0x80,
        0x100, 0x200, 0x400, 0x800, 0x1000, 0x2000};

    /**
     * Liefert für jeden notierten Kandidaten die Kandidatenmaske, beginnend mit Bit
     * 14 für Kandidat 1.
     */
    public static final int[] NK_MASKE = {0, 0x4000, 0x8000, 0x10000,
        0x20000, 0x40000, 0x80000, 0x100000, 0x200000, 0x400000};

    /**
     * Liefert für jeden notierten Kandidaten die Kandidatenmaske, beginnend mit Bit
     * 14 für Kandidat 1.
     */
    public static final int[] STR_KMASK = {
        0x2, 0x5, 0xA, 0x14, 0x28, 0x50, 0xA0, 0x140, 0x80};
    
    /**
     * Container für den Status alle 5 Navigationstasten
     */
    public boolean[] NAVISTATUS = 
            new boolean[] {false, false, false, false, false};

    public boolean[] KNAVISTATUS = 
            new boolean[] {false, false, false, false, false};
    /**
     * Liefert die Maske für alle Kandidaten an ihrer Punkt, also Kandidat 1
 in Bit 5. Bit 0 mit Bit 3 ist für den Kandidatenzähler, Bit 4 ist das
     * generelle Kandidatenflag.
     */
    public static final int AKND = 0x3FE0;              // alle Kandidaten
    public static final int ANKND = 0x7FC000;           // alle notierten Kandidaten
    public static final int KLIST = 0x3FFF;             // Maske für Kandidatenliste
    public static final int KND_LWF = 0x3FF0;           // AKND und Lösungswertflag
    public static final int ZAHL = 0xF;                 // Maske für Werte und Kandidatenzähler
    public static final int LWF = 0x10;                 // Lösungswertflag
    public static final int LWRT = LWF | ZAHL;
    public static final int RDW = 0xFFFFCD00;
    public static final int MINK = 0x20;
    public static final int MAXK = 0x2000;

    public static final int EDIT_NEU = 1;               // Neueingabe
    public static final int EDIT_AFG = 2;               // Aufgabe bearbeiten
    public static final int EDIT_LOW = 4;               // Aufgabe mit aktuellen Lösungswerten
    public static final int EDIT_LOS = 8;               // Lösung bearbeiten
    
    // Maske für die folgenden Farbbits
    public static final int M_OKER      = 0x4000000;
    public static final int M_VIOLETT   = 0x8000000;
    public static final int M_GRUEN     = 0x10000000;
    public static final int M_BLAU      = 0x20000000;
    public static final int M_ROSA      = 0x40000000;

    // Positionen aller Flags für die einzelnen Farbmarkierungen
    public static final int IS_MARK = 0x7C000000;

    // Maske für die Sperrzelle
    public static final int SZELLE = 0x800000;
    
    // Masken für Navitasten und andere Einstellungen
    public static final int NAVI_START = 0x1;
    public static final int NAVI_ZURUECK = 0x2;
    public static final int NAVI_VOR = 0x4;
    public static final int NAVI_AKTPOS = 0x8;
    public static final int NAVI_GELOEST = 0x10;
    public static final int ZEIGE_KND = 0x20;
    public static final int ZEIGE_NOTIZ = 0x40;
    public static final int PUNKT_KND = 0x80;
    public static final int USED_KLISTE = 0x100;
    public static final int USED_NOTIZEN = 0x200;
    public static final int MARKIERUNG = 0x400;
    public static final int ZEIGE_MARKIERUNG = 0x800;
    public static final int GELOEST = 0x1000;
    public static final int USED_FILTER = 0x2000;
    public static final int USED_TESTMOD = 0x4000;
    public static final int GESTARTET = 0x8000;

    // Kandidatenmodis und -Filter
    public static final int KNDMODLISTE = 1;
    public static final int KNDMODNOTIZ = 2;
//    public static final int FM_KLISTE = 0x3FE0;         // Maske für Filter der Listenkandidaten
//    public static final int FM_NOTIZEN = 0x7FC000;      // Maske für Filter der notierten Kandidaten
    

    /**
     * Liefert für jede mögliche Kandidatenbelegung einer Zelle die Anzahl der
     * vertretenen Kandidaten. Die reine Kandidatenmaske, beginnend mit Bit 0
     * für den Kandidaten 1.
     */
    public static final int[] NKND = {
        0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4,     // 0
        1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,     // 16
        1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,     // 32
        2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,     // 48
        1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,     // 64
        2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,     // 80
        2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,     // 96
        3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,     // 112
        1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,     // 128
        2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,     // 144
        2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,     // 160
        3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,     // 176
        2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,     // 192
        3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,     // 208
        3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,     // 224
        4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8,     // 240
        1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,     // 256
        2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,     // 272
        2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,     // 288
        3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,     // 304
        2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,     // 320
        3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,     // 336
        3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,     // 352
        4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8,     // 368
        2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,     // 384
        3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,     // 400
        3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,     // 416
        4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8,     // 432
        3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,     // 448
        4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8,     // 464
        4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8,     // 480
        5, 6, 6, 7, 6, 7, 7, 8, 6, 7, 7, 8, 7, 8, 8, 9      // 496
    };

    /**
     * Liefert für jede mögliche Kandidatenbelegung einer Zelle den kleinsten
     * der vertretenen Kandidaten. Die reine Kandidatenmaske, beginnend mit 
     * Bit 0 für den Kandidaten 1.
     */
    public static final int[] MINKND = {
        0, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 0
        5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 16
        6, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 32
        5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 48
        7, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 64
        5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 80
        6, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 96
        5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 112
        8, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 128
        5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 144
        6, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 160
        5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 176
        7, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 192
        5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 208
        6, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 224
        5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 240
        9, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 256
        5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 272
        6, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 288
        5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 304
        7, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 320
        5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 336
        6, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 352
        5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 368
        8, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 384
        5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 400
        6, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 416
        5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 432
        7, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 448
        5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 464
        6, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,     // 480
        5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1      // 496
    };

     /**
     * Liefert für jede mögliche Kandidatenbelegung einer Zelle den größten
     * der vertretenen Kandidaten. Die reine Kandidatenmaske, beginnend mit 
     * Bit 0 für den Kandidaten 1.
     */
    public static final int[] MAXKND = {
        0, 1, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4,     // 0
        5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,     // 16
        6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,     // 32
        6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,     // 48
        7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,     // 64
        7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,     // 80
        7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,     // 96
        7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,     // 112
        8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,     // 128
        8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,     // 144
        8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,     // 160
        8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,     // 176
        8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,     // 192
        8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,     // 208
        8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,     // 224
        8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,     // 240
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,     // 256
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,     // 272
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,     // 288
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,     // 304
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,     // 320
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,     // 336
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,     // 352
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,     // 368
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,     // 384
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,     // 400
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,     // 416
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,     // 432
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,     // 448
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,     // 464
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,     // 480
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9      // 496

    };

    /**
     * Liefert für den Zellindex einer Zelle den Index der Spalte, zu dem die
     * Zelle gehört.
     */
    public static final int[] S = {
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        0, 1, 2, 3, 4, 5, 6, 7, 8
    };

    /**
     * Liefert für den Zellindex einer Zelle den Index der Zeile, zu dem die
     * Zelle gehört.
     */
    public static final int[] Z = {
        0, 0, 0, 0, 0, 0, 0, 0, 0,
        1, 1, 1, 1, 1, 1, 1, 1, 1,
        2, 2, 2, 2, 2, 2, 2, 2, 2,
        3, 3, 3, 3, 3, 3, 3, 3, 3,
        4, 4, 4, 4, 4, 4, 4, 4, 4,
        5, 5, 5, 5, 5, 5, 5, 5, 5,
        6, 6, 6, 6, 6, 6, 6, 6, 6,
        7, 7, 7, 7, 7, 7, 7, 7, 7,
        8, 8, 8, 8, 8, 8, 8, 8, 8
    };

    public final int SIK = 9;
    public final int ZIK = 18;
    public final int ZM = 15;
    public final int SM = 4;

    /**
     * Liefert für ein eindimensionales Stradoku Feld für jede
     * Zelle eines zweidimensionalen Feldes den zugehörigen Zellindex.
     */
    public static final int[][] ZELLINDEX_1D = {
        // für die Spalten
        {0,  9, 18, 27, 36, 45, 54, 63, 72},
        {1, 10, 19, 28, 37, 46, 55, 64, 73},
        {2, 11, 20, 29, 38, 47, 56, 65, 74},
        {3, 12, 21, 30, 39, 48, 57, 66, 75},
        {4, 13, 22, 31, 40, 49, 58, 67, 76},
        {5, 14, 23, 32, 41, 50, 59, 68, 77},
        {6, 15, 24, 33, 42, 51, 60, 69, 78},
        {7, 16, 25, 34, 43, 52, 61, 70, 79},
        {8, 17, 26, 35, 44, 53, 62, 71, 80},
        // für die Zeilen
        { 0,  1,  2,  3,  4,  5,  6,  7,  8},
        { 9, 10, 11, 12, 13, 14, 15, 16, 17},
        {18, 19, 20, 21, 22, 23, 24, 25, 26},
        {27, 28, 29, 30, 31, 32, 33, 34, 35},
        {36, 37, 38, 39, 40, 41, 42, 43, 44},
        {45, 46, 47, 48, 49, 50, 51, 52, 53},
        {54, 55, 56, 57, 58, 59, 60, 61, 62},
        {63, 64, 65, 66, 67, 68, 69, 70, 71},
        {72, 73, 74, 75, 76, 77, 78, 79, 80}
    }; 
    
    public static final int [][] ZI81 = {
        { 0,  1,  2,  3,  4,  5,  6,  7,  8},
        { 9, 10, 11, 12, 13, 14, 15, 16, 17},
        {18, 19, 20, 21, 22, 23, 24, 25, 26},
        {27, 28, 29, 30, 31, 32, 33, 34, 35},
        {36, 37, 38, 39, 40, 41, 42, 43, 44},
        {45, 46, 47, 48, 49, 50, 51, 52, 53},
        {54, 55, 56, 57, 58, 59, 60, 61, 62},
        {63, 64, 65, 66, 67, 68, 69, 70, 71},
        {72, 73, 74, 75, 76, 77, 78, 79, 80}        
    };
    
    /**
     * Liefert für ein zweidimensionales Stradoku Feld
     * für jede Zelle eines Bereiches den zugehörigen Zellindex.
     */
    public static final int[][] ZELLINDEX_2D = {
        // für Zeilen
        {0, 16, 32, 48, 64, 80, 96, 112, 128}, 
        {1, 17, 33, 49, 65, 81, 97, 113, 129}, 
        {2, 18, 34, 50, 66, 82, 98, 114, 130}, 
        {3, 19, 35, 51, 67, 83, 99, 115, 131}, 
        {4, 20, 36, 52, 68, 84, 100, 116, 132}, 
        {5, 21, 37, 53, 69, 85, 101, 117, 133}, 
        {6, 22, 38, 54, 70, 86, 102, 118, 134}, 
        {7, 23, 39, 55, 71, 87, 103, 119, 135}, 
        {8, 24, 40, 56, 72, 88, 104, 120, 136}, 
        // für Spalten
        {0,   1,  2,  3,  4,  5,  6,  7,  8}, 
        {16, 17, 18, 19, 20, 21, 22, 23, 24}, 
        {32, 33, 34, 35, 36, 37, 38, 39, 40}, 
        {48, 49, 50, 51, 52, 53, 54, 55, 56}, 
        {64, 65, 66, 67, 68, 69, 70, 71, 72}, 
        {80, 81, 82, 83, 84, 85, 86, 87, 88}, 
        {96, 97, 98, 99, 100, 101, 102, 103, 104}, 
        {112, 113, 114, 115, 116, 117, 118, 119, 120}, 
        {128, 129, 130, 131, 132, 133, 134, 135, 136}        
    };
    
    /**
     * Liefert die Bezeichner für jede Zelle.
     */
    public static final String[] ZELLNAME = {
        "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9",
        "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9",
        "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9",
        "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9",
        "E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9",
        "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9",
        "G1", "G2", "G3", "G4", "G5", "G6", "G7", "G8", "G9",
        "H1", "H2", "H3", "H4", "H5", "H6", "H7", "H8", "H9",
        "J1", "J2", "J3", "J4", "J5", "J6", "J7", "J8", "J9"
    };
    
    /**
     * Liefert für jede Bereichslänge und einen sicheren Wert des Bereiches
     * die dafür noch möglichen Kandidaten.
     * Der Wert in KMASKE[4][3] (0x7E0 - binär: 0111 1110 0000) bedeutet 
     * zum Beispiel, dass in einem Bereich mit 4 Zellen und der sicheren 3
     * nur die Kandidaten 1,2,3,4,5,6 mögliche Kandidaten sind.
     */
    public static final int[][] KMASKE = {
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {2,0x60,0xE0,0x1C0,0x380,0x700,0xE00,0x1C00,0x3800,0x3000},
        {3,0xE0,0x1E0,0x3E0,0x7C0,0xF80,0x1F00,0x3E00,0x3C00,0x3800},
        {4,0x1E0,0x3E0,0x7E0,0xFE0,0x1FC0,0x3F80,0x3F00,0x3E00,0x3C00},
        {5,0x3E0,0x7E0,0xFE0,0x1FE0,0x3FE0,0x3FC0,0x3F80,0x3F00,0x3E00},
        {6,0x7E0,0xFE0,0x1FF0,0x3FF0,0x3FE0,0x3FE0,0x3FC0,0x3F80,0x3F00},
        {7,0xFE0,0x1FE0,0x3FE0,0x3FE0,0x3FE0,0x3FE0,0x3FE0,0x3FC0,0x3F80},
        {8,0x1FE0,0x3FE0,0x3FE0,0x3FE0,0x3FE0,0x3FE0,0x3FE0,0x3FE0,0x3FC0},
        {9,0x3FC0,0x3FA0,0x3F60,0x3EE0,0x3DE0,0x3BE0,0x37E0,0x2FE0,0x1FE0}};

    /**
     * Liefert für eine Rechtsdrehung des StrBereich Feldes um 90° die Indexe.
     */
    public static final int[] GET_R_INDEX = {
        72, 63, 54, 45, 36, 27, 18,  9, 0,
        73, 64, 55, 46, 37, 28, 19, 10, 1,
        74, 65, 56, 47, 38, 29, 20, 11, 2,
        75, 66, 57, 48, 39, 30, 21, 12, 3,
        76, 67, 58, 49, 40, 31, 22, 13, 4,
        77, 68, 59, 50, 41, 32, 23, 14, 5,
        78, 69, 60, 51, 42, 33, 24, 15, 6,
        79, 70, 61, 52, 43, 34, 25, 16, 7,
        80, 71, 62, 53, 44, 35, 26, 17, 8
    };    

    /**
     * Liefert für eine Spiegelung des StrBereich Feldes die Indexe
     */
    public static final int[] GET_S_INDEX = {
         8,  7,  6,  5,  4,  3,  2,  1,	 0,
        17, 16,	15, 14,	13, 12,	11, 10,	 9,
        26, 25,	24, 23,	22, 21,	20, 19,	18,
        35, 34,	33, 32,	31, 30,	29, 28,	27,
        44, 43,	42, 41,	40, 39,	38, 37,	36,
        53, 52,	51, 50,	49, 48,	47, 46,	45,
        62, 61,	60, 59,	58, 57,	56, 55,	54,
        71, 70,	69, 68,	67, 66,	65, 64,	63,
        80, 79,	78, 77,	76, 75,	74, 73,	72
    };    

    /**
     * Index für Zellposition
     */
    public static final int[][] MNK = {
        {1,2,3,4},
        {0,2,3,4},
        {0,1,3,4},
        {0,1,2,4},
        {0,1,2,3}};
        
    /**
     * Klasse für die Position eines Punktes oder eines Stradoku im Archiv
     */
    public class Position {
        int z;
        int s;
        public Position() {
            this.z = -1;
            this.s = -1;
        }
    }  
    
    /**
     * Klasse für die Position eines Kandidaten in einem Bereich
     */
    public class KndPosition {
        int n;                   
        int p;
        public KndPosition() {
            this.n = 0;
            this.p = 0;
        }
    }  
    
    /**
     * Klasse für die Definition eines StrBereich durch die Positionsangabe der jeweils ersten und
     * letzten Zelle des StrBereich, deren Länge und ob sie gelöst ist
     */
    public class StrBereich {
        int len;                // Anzahl der Zellen
        int index;              // Position in der Liste
        boolean isZeile;        // in Zeile oder Spalte
        boolean geloest;        // StrBereich gelöst
        Position[] pos;         // Positionen der Zellen
        Position ezPos;
        Position lzPos;

        public StrBereich(int laenge) {
            this.len = laenge;
            this.index = -1;
            this.isZeile = false;
            this.geloest = false;
            this.ezPos = new Position();
            this.lzPos = new Position();
            this.pos = new Position[len];
            for (int i = 0; i < len; i++) {
                this.pos[i] = new Position();
            }
        }
    }
    
    /**
     * Vertauscht die Werte der Vorgabe- und Sperrzellen.
     * @param str Referenz der zu behandelnden StrBereich-Aufgabe
     */
    public static void tauscheZellWerte(int[] str) {
        for (int i = 0; i < 81; i++) {
            int zw = str[i];
            if (zw >= 1 && zw <= 9) {
                str[i] = 10 - zw;
            }
            else if (zw > SZELLE) {
                zw -= SZELLE; 
                if (zw >= 1 && zw <= 9) {
                    str[i] = 10 - zw + SZELLE;
                }
            }
        }
    } 
    
    /**
     * Rotiert eine StrBereich-Aufgabe um 90° nach rechts.
     * @param str Referenz der zu behandelnden StrBereich-Aufgabe
     */
    public static void rotiereFeld(int[] str) {
        int[] tmp = new int[81];
        System.arraycopy(str, 0, tmp, 0, 81);
        for (int i = 0; i < 81; i++) {
            str[i] = tmp[GET_R_INDEX[i]];
        }
    }
    
    /**
     * siegelt eine StrBereich-Aufgabe horizontal
     * @param str Referenz der zu behandelnden StrBereich-Aufgabe
     */
    public static void spiegleFeld(int[] str) {
        int[] tmp = new int[81];
        System.arraycopy(str, 0, tmp, 0, 81);
        for (int i = 0; i < 81; i++) {
            str[i] = tmp[GET_S_INDEX[i]];
        }
    }

    /**
     * Kopiert die Werte und nicht nur die Referenzen für ein Stradokufeld.
     * @param aufgabe   die zu kopierende Aufgabe als eindimensionales Array
     * @param ziel      der Bereich, in den kopiert wird als eindimensionales Array
     * @param afg       wenn true, Zellen mit Kandidaten werden auf 0 gesetzt
     */
    @SuppressWarnings("ManualArrayToCollectionCopy")
    public static void kopieren(int[]aufgabe, int ziel[], boolean afg) {
        if (afg) {
            for (int i = 0; i < aufgabe.length; i++) {
                if (aufgabe[i] <= 9 || aufgabe[i] >= SZELLE) {
                    ziel[i] = aufgabe[i];                
                } else {
                    ziel[i] = 0;
                }
            }
        } else {
            for (int i = 0; i < aufgabe.length; i++) {
                ziel[i] = aufgabe[i];
            }
        }
    }    

    /**
     * Binär-Filter für Dateidialog
     */
    class StrBinFilter extends javax.swing.filechooser.FileFilter {
        @Override
        public boolean accept(File f) {
            return f.isDirectory()
                    || f.getName().toLowerCase().endsWith(".str");
        }
        @Override
        public String getDescription() {
            return "Stradoku Binär-Datei (*.str)";
        }
    }

    /**
     * Grafik-Filter für Dateidialog
     */
    class StrPngFilter extends javax.swing.filechooser.FileFilter {
        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith(".png");
        }
        @Override
        public String getDescription() {
            return "Stradoku Bild-Datei (*.png)";
        }
    }
    
}
