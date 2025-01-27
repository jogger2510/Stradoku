/*
 * Util.java ist Teil des Programmes Stradoku
 * 
 * Copyright (C) 2025 Gero Dittmer
 */

package stradoku;

public final class Util {
    
    /**
     * Konstruktor
     */
    public Util() {
    }

    public static int getNum(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException nfe) {}
        return 0;
    }

    public static int getNum(String s, int default_val) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException nfe) {}
        return default_val;
    }
}
