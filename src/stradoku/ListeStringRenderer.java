/*
 * ListeStringRenderer.java ist Teil des Programmes kodelasStradoku
 * Erzeugt am:                  12.10.2010 10:20
 * Letzte Änderung:             28.05.2018 14:00
 * 
 * Copyright (C) Konrad Demmel, 2010 - 2021
 */

package stradoku;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Farbeinstellungen für die Datenfelder
 */
public class ListeStringRenderer extends DefaultTableCellRenderer {

    private final Color colorSelected = new Color(51, 153, 255);
    private final Color colorWeiss;
    private final Color colorSchwarz;
    private final JTable strListe;

    /**
     * Konstruktor
     * @param lst Referenz zur Liste
     * @param mod micht verwendet
     */
    public ListeStringRenderer(JTable lst, ListenModel mod) {
        super();
        strListe = lst;
        colorSchwarz = strListe.getForeground();
        colorWeiss = strListe.getBackground();
        setFont(new Font("Monospaced", Font.PLAIN, 12));
        setOpaque(true);
    }
    
    /**
     * Regelt die Farbeinstellung für eine Zelle
     * @param table Referenz zur Liste
     * @param value Wert der Zelle
     * @param isSelected Flag für Selektion
     * @param hasFocus Fläg für Markierung
     * @param row Zeile
     * @param column Spalte
     * @return geänderte Zelle
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setVerticalAlignment(JLabel.BOTTOM);
        if (isSelected) {
            if (column == 5) {
                if (hasFocus) {
                    setBackground(colorWeiss);
                    setForeground(colorSchwarz);
                    strListe.editCellAt(row, 5);
                    strListe.getEditorComponent().requestFocusInWindow();
                }
                else {
                    setBackground(colorSelected);
                    setForeground(colorWeiss);
                }
            }
            else {
                setBackground(colorSelected);
                setForeground(colorWeiss);                
            }
        }
        else {
            setForeground(colorSchwarz);
            setBackground(colorWeiss);
        }
//        if (column < 5) {
            setText(value.toString());
//        }

        return this;
    }
}
