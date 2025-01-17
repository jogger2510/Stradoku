/*
 * ListEditor.java ist Teil des Programmes kodelasStradoku

 * Erzeugt am:                  12.05.2010, 20:46
 * Zueltzt geändert am:         14.12.2017, 23:00
 * 
 * Copyright (C) Konrad Demmel, 2010 - 2021
 */

package stradoku;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;

/**
 * Behandelt den editierbaren Eintrag für die Bemerkung
 */
public class ListeEditor extends JTextField 
        implements TableCellEditor, DocumentListener {
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<CellEditorListener> listeners;

    @SuppressWarnings("LeakingThisInConstructor")
    // Konstruktor
    public ListeEditor(ListenFrame listFrame, JTable strListe) {
        this.listeners = new ArrayList<>();
        KeyAdapter keyHandler = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                int row = strListe.getSelectedRow();
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    stopCellEditing();
                    strListe.changeSelection(row, 2, false, false);
                    strListe.requestFocusInWindow();
                    evt.consume();
                }
                else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    strListe.changeSelection(row, 2, false, false);
                    evt.consume();
                }
                else if (evt.isControlDown() && 
                        evt.getKeyCode() == KeyEvent.VK_L) {
                    stopCellEditing();
                    strListe.changeSelection(row, 2, false, false);
                    strListe.requestFocusInWindow();
                    evt.consume();
                    listFrame.setVisible(false);
                }
            }
        };
        addKeyListener(keyHandler);
        // Abstand von links - siehe auch Kl. ListenFrame Z. 71
        setBorder(BorderFactory.createEmptyBorder(5, 8, 0, 0));
        // gleicher Font wie im übrigen Bereich
        setFont(new Font("Courier New", Font.PLAIN, 12));
        // Der Editor hört sich selbst ab, so kann er auf 
        // jede Benutzereingabe reagieren
        getDocument().addDocumentListener(this);
    }

    /**
     * Legt einen Anfangswert für den Editor fest.
     * @param table - die JTable, die den Editor zum Bearbeiten auffordert; 
     * @param value - Wert der zu bearbeitenden Zelle
     * @param isSelected - true wenn Zelle hervorgehoben
     * @param row - Zeile der Zelle
     * @param column - Spalte der Zelle
     * @return - Gibt die Komponente zurück, die der Komponentenhierarchie 
     *           des Clients hinzugefügt werden soll.
     */
    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected,
            int row, int column) {
        setText(value.toString());
        return this;
    }

    /**
     * Fügt der Liste einen Listener hinzu, der benachrichtigt wird, 
     * wenn der Editor stoppt oder die Bearbeitung abbricht.
     * @param l - zu benachrichtigender Listener
     */
    @Override
    public void addCellEditorListener(CellEditorListener l) {
        listeners.add(l);
    }

    /**
     * Weist den Editor an, die Bearbeitung abzubrechen und 
     * keine teilweise bearbeiteten Werte zu akzeptieren.
     */
    @Override
    public void cancelCellEditing() {
        // Falls abgebrochen wird, werden alle Listeners informiert
        ChangeEvent event = new ChangeEvent(this);
        for (CellEditorListener listener : listeners.toArray(
                new CellEditorListener[listeners.size()])) {
            listener.editingCanceled(event);
        }
    }

    /**
     * Fragt den Werte des Feldes ab
     * @return - Wert des Feldes
     */
    @Override
    public Object getCellEditorValue() {
        // Gibt den aktuellen Wert des Editors zurück
        return getText();
    }

    /**
     * Auf Doppelklick wird geprüft
     * @param anEvent - zu prüfendes Ereignis
     * @return - false wenn nur ein Mausklick, sonst true
     */
    @Override
    public boolean isCellEditable(EventObject anEvent) {
        // Im Falle eines MouseEvents, muss ein Doppelklick erfolgen, um den 
        // Editor zu aktivieren. Ansonsten genügt ein einfacher Klick.
        if (anEvent instanceof MouseEvent) {
            return ((MouseEvent) anEvent).getClickCount() > 1;
        }
        return true;
    }

    /**
     * Entfernt einen Listener aus der Liste, der benachrichtigt wird
     * @param l - zu entfernender Listener
     */
    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        listeners.remove(l);
    }

    // wird nicht verwendet
    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    /**
     * Informiert alle Observer, dass der Wert gespeichert werden soll.
     * @return - informiert den Aufrufer, dass der Input korrekt ist.
     */
    @Override
    public boolean stopCellEditing() {
        ChangeEvent event = new ChangeEvent(this);
        for (CellEditorListener listener : listeners.toArray(
                new CellEditorListener[listeners.size()])) {
            listener.editingStopped(event);
        }
        return true;
    }
    
    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
    }

    public void update() {
    }
}
