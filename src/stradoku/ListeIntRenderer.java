/**
 * IntRenderer.java ist Teil des Programmes kodelasStradoku
 * Erzeugt am       08.10.2010, 19:20
 * Geändert am:     28.05.2018 14:00
 *
 * Copyright (C) Konrad Demmel, 2010 - 2020
 */

package stradoku;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * Dieser Renderer ist nur für die Darstellung der Spalte 2
 * mit den Aufgabe-Nummern verantwortlich.
 */
public class ListeIntRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;
    private final Color colorSelected;
    private final Color colorNormal;
    private final JTable list;
    private final ListenFrame lstFrame;

    /**
     * Konstruktor
     * @param lstFrm Referenz auf StradokuListenfenster
     * @param lst Referenz auf die Liste
     * @param mod Referenz auf ListenModel
     */
    public ListeIntRenderer(ListenFrame lstFrm, JTable lst, ListenModel mod) {
        super();
        this.colorSelected = new Color(51, 153, 255);
        this.colorNormal = new Color(255, 255, 255);
        list = lst;
        lstFrame = lstFrm;
        setFont(new Font("Monospaced", Font.PLAIN, 12));
        setOpaque(true);
    }

    /**
     * Renderer für Spalte mit Zeilennummer
     * @param table Referenz für Stradoku Liste
     * @param value Wert der zu rendernden Zelle
     * @param isSelected true, wenn Zelle mit hervorgehobener Auswahl gerendert werden soll
     * @param hasFocus wenn true, kann die Zelle entsprechend gerendert werden
     * @param row Zeilenindex der Zelle
     * @param column Spaltenindex der Zelle
     * @return gerenderte Komponente
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setVerticalAlignment(JLabel.BOTTOM);
        if (isSelected) {
            setBackground(colorSelected);
            setForeground(colorNormal);
        }
        else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        if ((Integer)value < 10) {
            setText("000" + value.toString());
        }
        else if ((Integer)value < 100) {
            setText("00" + value.toString());
        }
        else if ((Integer)value < 1000) {
            setText("0" + value.toString());
        }
        else {
            setText(value.toString());
        }
        lstFrame.setTxtMark(list.getSelectedRowCount());
        return this;
    }
}
