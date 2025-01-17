/*
 * ListeBoolRenderer.java ist Teil des Programmes kodelasStradoku
 * 
 * Copyright (C) Konrad Demmel, 2010 - 2020
 */
package stradoku;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Dieser Renderer ist nur für die Darstellung der Spalte 1 mit 
 * den CheckBoxen für Auswahl der auszudruckenden Aufgaben verantwortlich.
 */
class ListeBoolRenderer extends JCheckBox implements TableCellRenderer {

    private static final long serialVersionUID = 1L;
    private final JTable list;
    private final ListenFrame lstFrame;
    private final Color colorSelected = new Color(51, 153, 255);
    private int selected;

    /**
     * Konstruktor
     * @param lstFrm Referenz auf Hauptklasse der Liste
     * @param lst Referenz auf die Liste
     * @param mod ListenModel nicht genutzt
     */
    ListeBoolRenderer(ListenFrame lstFrm, JTable lst, ListenModel mod) {
        super();
        list = lst;
        lstFrame = lstFrm;
        setHorizontalAlignment(JLabel.CENTER);
    }

    /**
     * Wertet alle CheckBox Einträge der Liste aus
     * @param table Referenz auf die Liste
     * @param value Wert eines Listenelements
     * @param isSelected Flag für Selektion
     * @param hasFocus Flag für den Focus
     * @param row Zeile des Eintrages
     * @param column Spalte des Eintrages
     * @return ausgewertetes Listenelement
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(colorSelected);
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        int anz = list.getRowCount();
        selected = 0;
        for (int z = 0; z < anz; z++) {
            if ((Boolean) list.getValueAt(z, 0) == true) {
                selected++;
            }
        }
        setSelected((value != null && ((Boolean) value)));
        lstFrame.setTxtSelekt(selected);
        return this;
    }
}
