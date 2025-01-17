/*
 * ListenFrame.java ist Teil des Programmes kodelasStradoku
 * Erzeugt am:                  11.05.2010 11:19
 * Zuletzt geändert am:         17.09.2024 23:10
 * 
 * Copyright (C) Konrad Demmel, 2010 - 2023
 */

package stradoku;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * ListFrame ist die Hauptklasse für die Stradoku-Aufgabenliste.
 */
public class ListenFrame extends JFrame {

    private JTable jTable;
    private ListenModel listenModel;
    private final TableRowSorter<TableModel> sorter;
    private Stradoku strApp;
    private ThreadPrintStrSerie printSdkSerieThread;
    private HinweisWarten msgHinweisWarten;
    private boolean serie = false;
    private HilfeDialog jHilfe = null;
    
    /**
     * Konstruktor
     * @param mf Referenz auf StradokuApp
     */
    public ListenFrame(Stradoku mf) {
        super("Stradoku-Liste");                    // Text für Kopfleiste
        initComponents();
        strApp = mf;
        setIconImage(strApp.getIconImage());
        listenModel = new ListenModel(mf);
        jTable = new JTable(listenModel) {
            // damit die Einträge in den Zellen nicht hart am linken Rand stehen
            @Override
            public Component prepareRenderer(final TableCellRenderer renderer,
                    final int row, final int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (c instanceof JLabel) {
                    // Abstand links zum Zellrand setzen
                    if (column == 3) {
                        ((JLabel) c).setBorder(
                                BorderFactory.createEmptyBorder(0, 19, 0, 0));
                    } else if (column == 4) {
                        ((JLabel) c).setBorder(
                                BorderFactory.createEmptyBorder(0, 16, 0, 0));
                    } 
                    else {                                          // Spalten 2 und 5
                        ((JLabel) c).setBorder(
                                BorderFactory.createEmptyBorder(0, 8, 0, 0));
                    }                    
                }
                return c;
            }
        };
        jTable.setRowHeight(jTable.getRowHeight() + 4); // Zeile etwas höher
        jTable.getColumnModel().getColumn(0).setPreferredWidth(
                listenModel.getSpaltenBreite(0));
        jTable.getColumnModel().getColumn(1).setPreferredWidth(
                listenModel.getSpaltenBreite(1));
        jTable.getColumnModel().getColumn(2).setPreferredWidth(
                listenModel.getSpaltenBreite(2));
        jTable.getColumnModel().getColumn(3).setPreferredWidth(
                listenModel.getSpaltenBreite(3));
        jTable.getColumnModel().getColumn(4).setPreferredWidth(
                listenModel.getSpaltenBreite(4));
        jTable.getColumnModel().getColumn(5).setPreferredWidth(
                listenModel.getSpaltenBreite(5));
        jTable.setDefaultEditor(String.class, 
                new ListeEditor(this, jTable));
        jTable.setDefaultRenderer(Boolean.class, 
                new ListeBoolRenderer(this, jTable, listenModel));
        jTable.setDefaultRenderer(Integer.class, 
                new ListeIntRenderer(this, jTable, listenModel));
        jTable.setDefaultRenderer(String.class, 
                new ListeStringRenderer(jTable, listenModel));
        jTable.setFont(new Font("Courier New", Font.PLAIN, 12));
        JTableHeader header = jTable.getTableHeader();
        header.setFont(new Font("Default", Font.BOLD, 12));
        header.setUpdateTableInRealTime(true);
        header.setReorderingAllowed(true);
        header.setBackground(Color.BLACK);
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (me.isControlDown() || me.isShiftDown() || me.isAltDown()) {
                    return;
                }
                if (me.getButton() != 1) {          // nur linke Maustaste behandeln
                    return;
                }
                Point p = me.getPoint();
                int zeile = jTable.rowAtPoint(p);
                int spalte = jTable.columnAtPoint(p);
                if (me.getClickCount() != 2) {       // war es kein Doppelklick
                    jTable.changeSelection(zeile, spalte, false, false);
                    return;
                }
                if (spalte >= 0 && spalte < 5) {
                    uebergebeAufgabe(zeile);
                }
            }
        };
        jTable.addMouseListener(mouseHandler);
        KeyAdapter keyHandler;
        keyHandler = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                int code = evt.getKeyCode();
                if (code > KeyEvent.VK_F2 && code < KeyEvent.VK_F10
                        || code == KeyEvent.VK_CONTROL
                        || code == KeyEvent.VK_SHIFT
                        || code == KeyEvent.VK_ALT
                        || code == KeyEvent.VK_NUM_LOCK
                        || code == KeyEvent.VK_BACK_SPACE
                        || code == KeyEvent.VK_TAB
                        || code == KeyEvent.VK_CAPS_LOCK
                        || evt.isControlDown() &&
                            (code == KeyEvent.VK_L 
                            || code == KeyEvent.VK_B 
                            || code == KeyEvent.VK_E)) {
                    return;
                }
                if (code == KeyEvent.VK_F1) {
                    zeigeHilfe();
                    evt.consume();
                    return;
                }
                else if (code == KeyEvent.VK_TAB) {
                    int row = jTable.getSelectedRow();
                    if (getStradoku(row).length() == 81) {
                        jTable.changeSelection(row, 5, false, false);
                    }
                    evt.consume();
                    return;
                }
                else if (code == KeyEvent.VK_LEFT || 
                        code == KeyEvent.VK_RIGHT) {
                    evt.consume();
                    return;
                }                
                if (evt.isControlDown()) {
                    // mit Strg+C in Zwischenablage kopieren
                    if (code == KeyEvent.VK_C) {
                        int z = jTable.getSelectedRow();
                        int afgNr = (int)jTable.getValueAt(z, 1) - 1;
                        if (z < 0) {
                            labelHinweis.setText("Es ist keine Zeile "+
                                    "mit einem Stradoku markiert!");
                            evt.consume();
                            return;
                        }
                        String clipStr = (String) listenModel.getValueAt(afgNr, 2);
                        try {
                            Clipboard clip = Toolkit.getDefaultToolkit().
                                    getSystemClipboard();
                            StringSelection content = 
                                    new StringSelection(clipStr);
                            clip.setContents(content, null);
                            labelHinweis.setText("Das Stradoku in Zeile "+(z + 1)
                                    +" wurde in die Zwischenablage kopiert.");
                            evt.consume();                                      
                        } catch (HeadlessException ex) {
                            JOptionPane.showMessageDialog(strApp,
                                    "Folgender Fehler ist aufgetreten: \n\n"
                                            +ex.getMessage(),
                                    "Hinweis", 1);
                        }
                    } else if (code == KeyEvent.VK_F){
                        sucheStradoku();
                        evt.consume();
                    }
                    // mit Strg+Return übernehmen
                    else if (code == KeyEvent.VK_ENTER) {
                        if (!(jTable.getSelectedRowCount() == 1)) {
                            JOptionPane.showMessageDialog(strApp,
                                    "Es ist mehr als eine Aufgaben markiert.",
                                    "Hinweis", 1);
                            return;            
                        }
                        int row = jTable.getSelectedRow();
                        int col = jTable.getSelectedColumn();
                        if (col >= 0 && col <= 5) {
                            evt.consume();
                            uebergebeAufgabe(row);
                            evt.consume();
                        }
                    }
                    else if (code == KeyEvent.VK_L) {
                        int row = jTable.getSelectedRow();
                        jTable.changeSelection(row, 2, false, false);
                        evt.consume();
                        setVisible(false);
                    }
                    return;
                }
                if (code == KeyEvent.VK_SPACE) {
                    // Einträge über Spacetaste selektieren
                    if (jTable.getSelectedColumn() < 5) {
                        int row = jTable.getSelectedRow();
                        boolean select = 
                                (Boolean) listenModel.getValueAt(row, 0);
                        if (select) {
                            listenModel.setValueAt(false, row, 0);
                        }
                        else {
                            listenModel.setValueAt(true, row, 0);
                        }
                        if (evt.isShiftDown()) {
                            if (row > 0) {
                                row--;
                            }
                        }
                        else {
                            if (row < listenModel.getRowCount() - 1) {
                                row++;
                            }
                        }
                        jTable.scrollRectToVisible(jTable.
                                getCellRect(row, 0, true));
                        jTable.getSelectionModel().
                                setSelectionInterval(row, row);
                        evt.consume();
                    }
                    return;
                }
                if (evt.isShiftDown() && code == KeyEvent.VK_END) {
                    int row = jTable.getSelectedRow();
                    int rows = jTable.getRowCount() - 1;
                    jTable.setRowSelectionInterval(row, rows);
                    evt.consume();
                }
                else if (evt.isShiftDown() && code == KeyEvent.VK_HOME) {
                    int row = jTable.getSelectedRow();
                    jTable.setRowSelectionInterval(row, 0);
                    evt.consume();
                }
                else if (code == KeyEvent.VK_END) {
                    int row = jTable.getRowCount() - 1;
                    jTable.changeSelection(row, 2, false, false);
                    evt.consume();
                }
                else if (code == KeyEvent.VK_HOME) {
                    jTable.changeSelection(0, 2, false, false);
                    evt.consume();
                }
            }
        };
        jTable.addKeyListener(keyHandler);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        symbolLeiste.setRollover(true);
        scrollPanel.setViewportView(jTable);
        // Der TableRowSorter wird die Daten des Models sortieren
        sorter = new TableRowSorter<>();
        jTable.setRowSorter(sorter);
        sorter.setModel(listenModel);
        labelAnzahlDS.setText("" + listenModel.getRowCount());
        labelAnzahlM.setText("0");
        labelAnzahlS.setText("0");
        jTable.changeSelection(0, 2, false, false);
        pack();
        setVisible(false);
    }
    
    /**
     * Fügt der Liste eine Aufgabe hinzu
     * @param afg Stradoku als String
     * @param lvl Level
     * @param lz leere Zellen
     * @param bmk Bemerkung
     * @return Nummer unter der das Stradoku in der Liste geführt wird
     */
    public int addStradoku(String afg, String lvl, int lz, String bmk) {
        if (serie) {
            listenModel.setMsg_vorhanden(false);
        } else {
            listenModel.setMsg_vorhanden(true);
        }
        int nrSdk = listenModel.addStradoku(afg, lvl, lz, bmk);
        // nur wenn in der Liste nicht vorhanden
        if (nrSdk >= 0) {
            listenModel.setGeaendert();
            if (!serie) {
                jTable.changeSelection(nrSdk - 1, 2, false, false);
                listenModel.datenSpeichern(
                        strApp.getHomePath(), "stradoku.lst");
            }
        }
        return nrSdk;
    }

    /**
     * Speichert die interne Stradokuliste
     * @param pfad Speicherort
     * @param datei Dateiname
     */
    public void speichernListe(String pfad, String datei) {
        listenModel.datenSpeichern(pfad, datei);
    }

    public void speichernListe() {
        listenModel.datenSpeichern(strApp.getHomePath(), "stradoku.lst");
    }

    /**
     * Wird aufgerufen, wenn per Dialog ein Stradoku aus der Liste 
     * übernommen werden soll.
     * @param num Nummer, unter der das Stradoku in der Liste geführt wird
     * @return String der Aufgabennummer
     */
    public String getStradoku(int num) {
        String aufgabe = (String) listenModel.getValueAt(num, 2);
        return aufgabe;
    }

    public String getNummer(int num) {
        String afgNr;
        int nr = (Integer) listenModel.getValueAt(num, 1);
        if (nr < 10) {
            afgNr = "000" + nr;
        }
        else {
            if (nr < 100) {
                afgNr = "00" + nr;
            }
            else {
                if (nr < 1000) {
                    afgNr = "0" + nr;
                }
                else {
                    afgNr = "" + nr;
                }
            }
        }
        return afgNr;
    }

    public String getBemerkung(int num) {
        return (String) listenModel.getValueAt(num, 5);    
    }
    
    public boolean isSelect(int z) {
        return (Boolean) listenModel.getValueAt(z, 0);
    }

    public int getLevel(int num) {
        // nur erste Ziffer (ev. einmal Level + Wert)
        String tmp = (String) listenModel.getValueAt(num, 3); 
        return Integer.parseInt(tmp.substring(0, 1));
    }

    public void setLevel(int z, String lvl) {
        listenModel.setValueAt(lvl, z, 3);
    }

    public int getAnzahl() {
        int anzahl;
        if (listenModel.getRowCount() == 1 && "" == listenModel.getValueAt(0, 3)){
            anzahl = 0;
        } else {
            anzahl = listenModel.getRowCount();
        }
        return anzahl;
    }
    
    /**
     * Löscht alle Datensätze der Stradoku Liste
     */
    public void loescheStrTabelle() {
        int ds = listenModel.getRowCount() - 1;
        for (int i = ds; i >= 0; i-- ) {
            listenModel.deleteStradokuZeile(i);
        }
    }
    
    private void selektAufgaben() {
        int anz = listenModel.getRowCount();
        boolean[] pFlag = new boolean[anz];
        for (int z = 0; z < anz; z++) {
            pFlag[z] = jTable.isCellSelected(z, 0);
        }
        for (int z = 0; z < anz; z++) {
            if (pFlag[z] && (Boolean) jTable.getValueAt(z, 0) == false) {
                jTable.setValueAt(true, z, 0);
            }
        }
        labelAnzahlS.setText("" + listenModel.getSelektStradoku());
    }

    /**
     * Übergibt von der Stradokuliste ein selektiertes Stradoku dem Lösungsbereich.
     * @param row Zeile mit dem zu übergebenden Stradoku
     */
    private void uebergebeAufgabe(int row) {
        if (!(jTable.getSelectedRowCount() == 1)) {
            JOptionPane.showMessageDialog(strApp,
                    "Es ist mehr als eine Aufgaben markiert.",
                    "Hinweis", 1);
            return;            
        }
        int afgNr = (int)jTable.getValueAt(row, 1) - 1;
        String aufgabe = (String)jTable.getValueAt(row, 2);
        String bemerkung = (String)jTable.getValueAt(row, 5);
        strApp.setAufgabeAusListe(getNummer(afgNr), aufgabe, bemerkung);
        setVisible(false);
    }

    /**
     * Selektierte Stradoku als Serie ausdrucken
     */
    private void druckeSerie() {
        int anzahl = listenModel.getSelektStradoku();
        if (anzahl < 1) {
            // Abfrage, ob leere Seite gedruckt werden soll
            int wahl = JOptionPane.showConfirmDialog(strApp,
                "<html><br><brJOptionPane>Es sind keine Aufgaben für den "+
                        "Ausdruck selektiert.<br><br>Soll ein Blatt mit "+
                        "vier leeren Stradokusfeldern gedruckt werden?<br><br>",
                "Abfrage zu leerem Stradokufeld",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.NO_OPTION);
            if (wahl != JOptionPane.YES_OPTION) {
                return;
            }
        }
        msgHinweisWarten = new HinweisWarten(strApp);
        String hnw = "<html><center><b>Drucker wird initialisiert.<br><br>"
                + "Bitte solange warten.</b></center></html>";
        msgHinweisWarten.zeigeHinweis(hnw);
        printSdkSerieThread = new ThreadPrintStrSerie(this,
                listenModel, msgHinweisWarten, listenModel.getSelektStradoku());
        printSdkSerieThread.start();
        msgHinweisWarten.setVisible(true);
    }

    /**
     * Aufgabe: Selektierte Stradoku aus der Liste entfernen. Selektierte 
     *          Stradoku am Ende der Liste werden komplett entfernt. Bei
     *          selektierten Stradoku, denen noch unselektierte Stradoku folgen, 
     *          werden mit Ausnahme der Stradoku-Nummer alle Einträge
     *          entfernt.
     */
    private void deleteStradoku() {
        if (listenModel.getSelektStradoku() < 1) {
            JOptionPane.showMessageDialog(strApp,
                    "Es sind keine Aufgaben zum Entfernen "+
                            "aus der Liste selektiert.",
                    "Hinweis", 1);
            return;
        }
        int zMaxNdx = listenModel.getRowCount() - 1;
        int anzahl = 0;
        for (int z = zMaxNdx; z > -1; z--) {
            if (isSelect(z)) {
                anzahl++;
            }
        } 
        String infoText;
        if (anzahl == 1) {
            infoText = "<html><br><br>Soll mit dem zu entfernenden Stradoku auch "+
                    "die Zeile dieses Stradoku entfernt werden?<br><br>";
        } else {
            infoText = "<html><br><br>Sollen mit den zu entfernenden Stradokus auch "+
                    "die Zeilen dieser Stradoku entfernt werden?<br><br>";            
        }
        int wahl = JOptionPane.showConfirmDialog(this,
            infoText + "<b>Wichtig:</b> In diesem Fall werden die übrigen "+
                    "Stradoku neu durchnummeriert. Damit stimmt<br>"+
                    "möglicherweise die Nummerierung bereits ausgedruckter "+
                    "Stradoku nicht mehr mit der neuen<br>"+
                    "Nummerierung in der Liste überein.<br><br>", 
            "Abfrage zum Entfernen von Stradoku",
            JOptionPane.YES_NO_CANCEL_OPTION);
        if (wahl == JOptionPane.CANCEL_OPTION) {
            return;
        }
        int tmp = zMaxNdx;
        if (wahl == JOptionPane.YES_OPTION) {       // gnadenlos entfernen  
            for (int z = zMaxNdx; z > -1; z--) {
                if (isSelect(z)) {
                    listenModel.deleteStradokuZeile(z);
                    tmp--;
                }
            } 
            if (tmp > -1) {
                listenModel.neuNummerieren();
            }
        }
        else if (wahl == JOptionPane.NO_OPTION) {       // Ordnung nicht stören
            int z;
            int lPos = 0;
            for (z = zMaxNdx; z > -1; z--) {
                if (isSelect(z)) {
                    listenModel.deleteStradoku(z);
                    tmp --;
                    lPos = z;
                }
            }
            jTable.changeSelection(lPos, 2, false, false);
        }
        if (listenModel.getRowCount() == 0) {
            listenModel.setDefaultData();
        }
        listenModel.setGeaendert();
        setAnzahl();
        speichernListe();
        repaint();
    }

    public void setTxtMark(int n) {
        labelAnzahlM.setText("" + n);
    }

    public void setTxtSelekt(int n) {
        labelAnzahlS.setText("" + n);
    }

    public void setAnzahl() {
        labelAnzahlDS.setText("" + listenModel.getRowCount());
        labelAnzahlS.setText("" + listenModel.getSelektStradoku());
        labelAnzahlM.setText("" + 0);
    }

    public void setSerie(boolean sr) {
        serie = sr;
    }
    
    private void sucheStradoku() { 
        String suche = JOptionPane.showInputDialog(this, 
                "<html><br><b>Markieren Sie eine Zeile, ab der gesucht werden soll!</b></html>\n" +
                "\nGeben Sie beliebig viele fortlaufende Zellwerte für das zu\n" + 
                "suchende Stradoku ein, beginnend mit der ersten Zelle oben\n" +
                "links im Stradokufeld.\n\n" +
                "Für Zellen ohne Vorgabewerte muss eine Null gesetzt werden.\n" +
                "Vorgabewerte werden direkt eingegeben.\n" +
                "Für eine Sperrzelle ohne Sperrwert wird ein 's' eingegeben.\n"+
                "Für Sperrwerte werden die Buchstaben 'a' bis 'i' eingegeben.          \n" +
                "('a'=1, 'b'=2 ... 'h'=8 und 'i'=9)\n\n", 
                "Abfrage für Stradokusuche", 
                JOptionPane.QUESTION_MESSAGE);
        if (suche != null && suche.length() > 0) {
            sucheStradoku(suche);
        }
    }

    private void sucheStradoku(String uebergabe) {
        String afg;
        String suchString = uebergabe;
        if (suchString.length() > 81) {
            suchString = suchString.substring(0, 81);
        }
        int gefunden = 0;
        boolean weitersuche = false;
        for (int i = 0; i < jTable.getRowCount(); i++) {
            afg = jTable.getValueAt(i, 2).toString();
            if (afg.length() < 81) {       // leere Zeile
                continue;
            }
            if (suchString.equalsIgnoreCase(afg.substring(0, suchString.length()))) {
                jTable.changeSelection(i, 0, false, false);
                gefunden++;
                if (suchString.equalsIgnoreCase(afg.substring(0, suchString.length()))) {
                    String nr = jTable.getValueAt(i, 1).toString();
                    String[] yesNoOptions = {"Ja", "Nein"};
                    int op = JOptionPane.showOptionDialog(this,
                            "Die Aufgabe Nr. " + nr + " entspricht der "
                            + "Sucheingabe\nSoll weiter gesucht werden?",
                            "Abfrage auf Weitersuche",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null, yesNoOptions, yesNoOptions[1]);
                    if (op == JOptionPane.NO_OPTION) {
                        break;
                    } else {
                        weitersuche = true;
                    }
                }
            }
        }
        if (gefunden == 0) {
            JOptionPane.showMessageDialog(this,
                    "Für die eingegebene Zeichenfolge wurde "
                    + "kein Stradoku gefunden.");
        } else if (gefunden > 1 || weitersuche) {
            JOptionPane.showMessageDialog(this,
                    "Kein weiteres Stradokus für die Sucheingabe gefunden.");
        } 
    }
    
    public void deselectPrintSdk () {
        int zeile = jTable.getSelectedRow();
        listenModel.deselektPrintSelekt();
        labelAnzahlS.setText("" + listenModel.getSelektStradoku());
        jTable.changeSelection(zeile, 2, false, false);     
    }
    
    public void setAnzahlDS() {
        labelAnzahlDS.setText("" + listenModel.getRowCount());
    }

//    public void setSelection() {
//        jTable.changeSelection(jTable.getSelectedRow(), 0, false, false);
//    }
    
    private void zeigeHilfe() {
        if (jHilfe == null) {
            jHilfe = new HilfeDialog(strApp);
        }
        jHilfe.zeigeHilfe("listenFenster", true);          
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        symbolLeiste = new javax.swing.JToolBar();
        seperatorLabel8 = new javax.swing.JLabel();
        buttonClose = new javax.swing.JButton();
        seperatorLabel10 = new javax.swing.JLabel();
        buttonUebernehmen = new javax.swing.JButton();
        buttonSelect = new javax.swing.JButton();
        seperatorLabel9 = new javax.swing.JLabel();
        buttonDeselect = new javax.swing.JButton();
        seperatorLabel2 = new javax.swing.JLabel();
        buttonDelete = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        seperatorLabel1 = new javax.swing.JLabel();
        buttonPrint = new javax.swing.JButton();
        seperatorLabel11 = new javax.swing.JLabel();
        buttonSuche = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        buttonHilfe = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        seperatorLabel12 = new javax.swing.JLabel();
        scrollPanel = new javax.swing.JScrollPane();
        labelDatenSaetze = new java.awt.Label();
        labelAnzahlDS = new java.awt.Label();
        labelMarkiert = new java.awt.Label();
        labelAnzahlM = new java.awt.Label();
        labelSelektiert = new java.awt.Label();
        labelAnzahlS = new java.awt.Label();
        labelHinweis = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImages(null);
        setMaximumSize(new java.awt.Dimension(1840, 1640));
        setName("strListenFrame"); // NOI18N
        setPreferredSize(new java.awt.Dimension(850, 642));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowDeactivated(java.awt.event.WindowEvent evt) {
                formWindowDeactivated(evt);
            }
        });

        symbolLeiste.setFloatable(false);
        symbolLeiste.setRollover(true);
        symbolLeiste.setMaximumSize(new java.awt.Dimension(829, 30));
        symbolLeiste.setMinimumSize(new java.awt.Dimension(829, 30));
        symbolLeiste.setPreferredSize(new java.awt.Dimension(829, 28));
        symbolLeiste.setVerifyInputWhenFocusTarget(false);

        seperatorLabel8.setMaximumSize(new java.awt.Dimension(9, 32));
        seperatorLabel8.setMinimumSize(new java.awt.Dimension(9, 32));
        seperatorLabel8.setPreferredSize(new java.awt.Dimension(9, 32));
        symbolLeiste.add(seperatorLabel8);

        buttonClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/str.png"))); // NOI18N
        buttonClose.setToolTipText("Liste schließen und Stradokufeld anzeigen - Strg+L");
        buttonClose.setFocusable(false);
        buttonClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCloseActionPerformed(evt);
            }
        });
        symbolLeiste.add(buttonClose);

        seperatorLabel10.setMaximumSize(new java.awt.Dimension(9, 32));
        seperatorLabel10.setMinimumSize(new java.awt.Dimension(9, 32));
        seperatorLabel10.setPreferredSize(new java.awt.Dimension(9, 32));
        symbolLeiste.add(seperatorLabel10);

        buttonUebernehmen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/uebernehmen.png"))); // NOI18N
        buttonUebernehmen.setToolTipText("Markiertes Stradoku an Fenster mit Stradokufeld übergeben - Doppelklick oder Strg+Enter");
        buttonUebernehmen.setFocusable(false);
        buttonUebernehmen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonUebernehmen.setMaximumSize(new java.awt.Dimension(27, 27));
        buttonUebernehmen.setMinimumSize(new java.awt.Dimension(27, 27));
        buttonUebernehmen.setPreferredSize(new java.awt.Dimension(27, 27));
        buttonUebernehmen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonUebernehmen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUebernehmenActionPerformed(evt);
            }
        });
        symbolLeiste.add(buttonUebernehmen);

        buttonSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/selekt.png"))); // NOI18N
        buttonSelect.setToolTipText("markierte Stradokus für den Ausdruck selektieren");
        buttonSelect.setFocusable(false);
        buttonSelect.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSelect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectActionPerformed(evt);
            }
        });
        symbolLeiste.add(buttonSelect);

        seperatorLabel9.setMaximumSize(new java.awt.Dimension(9, 32));
        seperatorLabel9.setMinimumSize(new java.awt.Dimension(9, 32));
        seperatorLabel9.setPreferredSize(new java.awt.Dimension(9, 32));
        symbolLeiste.add(seperatorLabel9);

        buttonDeselect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/deselekt.png"))); // NOI18N
        buttonDeselect.setToolTipText("hebt alle Selektierungen auf");
        buttonDeselect.setFocusable(false);
        buttonDeselect.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonDeselect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonDeselect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeselectActionPerformed(evt);
            }
        });
        symbolLeiste.add(buttonDeselect);

        seperatorLabel2.setMaximumSize(new java.awt.Dimension(9, 32));
        seperatorLabel2.setMinimumSize(new java.awt.Dimension(9, 32));
        seperatorLabel2.setPreferredSize(new java.awt.Dimension(9, 32));
        symbolLeiste.add(seperatorLabel2);

        buttonDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/delete.png"))); // NOI18N
        buttonDelete.setToolTipText("selektierte Stradoku löschen");
        buttonDelete.setFocusable(false);
        buttonDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });
        symbolLeiste.add(buttonDelete);

        jLabel2.setMaximumSize(new java.awt.Dimension(9, 32));
        jLabel2.setMinimumSize(new java.awt.Dimension(9, 32));
        jLabel2.setPreferredSize(new java.awt.Dimension(9, 32));
        symbolLeiste.add(jLabel2);

        seperatorLabel1.setMaximumSize(new java.awt.Dimension(9, 32));
        seperatorLabel1.setMinimumSize(new java.awt.Dimension(9, 32));
        seperatorLabel1.setPreferredSize(new java.awt.Dimension(9, 32));
        symbolLeiste.add(seperatorLabel1);

        buttonPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/drucker.png"))); // NOI18N
        buttonPrint.setToolTipText("selektierte Stradokus ausdrucken");
        buttonPrint.setFocusable(false);
        buttonPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPrintActionPerformed(evt);
            }
        });
        symbolLeiste.add(buttonPrint);

        seperatorLabel11.setMaximumSize(new java.awt.Dimension(9, 32));
        seperatorLabel11.setMinimumSize(new java.awt.Dimension(9, 32));
        seperatorLabel11.setPreferredSize(new java.awt.Dimension(9, 32));
        symbolLeiste.add(seperatorLabel11);

        buttonSuche.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/strSuche.png"))); // NOI18N
        buttonSuche.setToolTipText("Stradokus suchen - (Strg+F)");
        buttonSuche.setFocusable(false);
        buttonSuche.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSuche.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonSuche.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSucheActionPerformed(evt);
            }
        });
        symbolLeiste.add(buttonSuche);
        symbolLeiste.add(filler2);

        buttonHilfe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stradoku/img/fragezeichen.png"))); // NOI18N
        buttonHilfe.setToolTipText("Hilfe für das Listenfenster aufrufen");
        buttonHilfe.setFocusable(false);
        buttonHilfe.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonHilfe.setMaximumSize(new java.awt.Dimension(27, 27));
        buttonHilfe.setMinimumSize(new java.awt.Dimension(27, 27));
        buttonHilfe.setPreferredSize(new java.awt.Dimension(27, 27));
        buttonHilfe.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonHilfe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonHilfeActionPerformed(evt);
            }
        });
        symbolLeiste.add(buttonHilfe);
        symbolLeiste.add(filler1);

        seperatorLabel12.setMaximumSize(new java.awt.Dimension(100, 32));
        seperatorLabel12.setMinimumSize(new java.awt.Dimension(0, 32));
        seperatorLabel12.setPreferredSize(new java.awt.Dimension(20, 32));
        symbolLeiste.add(seperatorLabel12);

        scrollPanel.setBorder(null);
        scrollPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        scrollPanel.setPreferredSize(new java.awt.Dimension(830, 640));

        labelDatenSaetze.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        labelDatenSaetze.setMaximumSize(new java.awt.Dimension(62, 15));
        labelDatenSaetze.setMinimumSize(new java.awt.Dimension(62, 15));
        labelDatenSaetze.setText("Datensätze:");

        labelAnzahlDS.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        labelAnzahlDS.setMaximumSize(new java.awt.Dimension(36, 19));
        labelAnzahlDS.setMinimumSize(new java.awt.Dimension(36, 19));
        labelAnzahlDS.setName(""); // NOI18N
        labelAnzahlDS.setText("0000");

        labelMarkiert.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        labelMarkiert.setForeground(new java.awt.Color(0, 0, 0));
        labelMarkiert.setText("Markiert:");

        labelAnzahlM.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        labelAnzahlM.setForeground(new java.awt.Color(0, 0, 0));
        labelAnzahlM.setMaximumSize(new java.awt.Dimension(36, 19));
        labelAnzahlM.setMinimumSize(new java.awt.Dimension(36, 19));
        labelAnzahlM.setText("0000");

        labelSelektiert.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        labelSelektiert.setText("Selektiert:");

        labelAnzahlS.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        labelAnzahlS.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        labelAnzahlS.setMaximumSize(new java.awt.Dimension(36, 19));
        labelAnzahlS.setMinimumSize(new java.awt.Dimension(36, 19));
        labelAnzahlS.setText("0000");

        labelHinweis.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        labelHinweis.setText("Doppelklick oder Strg+Enter: Stradoku an Fenster mit Stradokufeld übergeben");
        labelHinweis.setMaximumSize(new java.awt.Dimension(290, 20));
        labelHinweis.setMinimumSize(new java.awt.Dimension(290, 20));
        labelHinweis.setPreferredSize(new java.awt.Dimension(290, 20));
        labelHinweis.setRequestFocusEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(symbolLeiste, javax.swing.GroupLayout.PREFERRED_SIZE, 845, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelDatenSaetze, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelAnzahlDS, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelMarkiert, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelAnzahlM, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(labelSelektiert, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelAnzahlS, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addComponent(labelHinweis, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(symbolLeiste, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(scrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 13, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelAnzahlM, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelAnzahlDS, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelHinweis, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelDatenSaetze, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelMarkiert, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelSelektiert, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelAnzahlS, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelAnzahlDS, labelAnzahlM, labelAnzahlS, labelDatenSaetze, labelHinweis, labelMarkiert, labelSelektiert});

        getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCloseActionPerformed
        setVisible(false);
    }//GEN-LAST:event_buttonCloseActionPerformed

    private void formWindowDeactivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeactivated
        if (jTable.isEditing()) {
            jTable.getCellEditor().stopCellEditing();
        }
        listenModel.datenSpeichern(strApp.getHomePath(), "stradoku.lst");
    }//GEN-LAST:event_formWindowDeactivated

    private void buttonSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectActionPerformed
        int zeile = jTable.getSelectedRow();
        selektAufgaben();
        jTable.changeSelection(zeile, 2, false, false);
    }//GEN-LAST:event_buttonSelectActionPerformed

    private void buttonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPrintActionPerformed
        int zeile = jTable.getSelectedRow();
        druckeSerie();
        jTable.changeSelection(zeile, 2, false, false);
    }//GEN-LAST:event_buttonPrintActionPerformed

    private void buttonDeselectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeselectActionPerformed
        deselectPrintSdk();
    }//GEN-LAST:event_buttonDeselectActionPerformed

    private void buttonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteActionPerformed
        deleteStradoku();
    }//GEN-LAST:event_buttonDeleteActionPerformed

    private void buttonSucheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSucheActionPerformed
        sucheStradoku();
    }//GEN-LAST:event_buttonSucheActionPerformed

    private void buttonHilfeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonHilfeActionPerformed
        zeigeHilfe();
    }//GEN-LAST:event_buttonHilfeActionPerformed

    private void buttonUebernehmenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUebernehmenActionPerformed
        uebergebeAufgabe(jTable.getSelectedRow());
    }//GEN-LAST:event_buttonUebernehmenActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClose;
    private javax.swing.JButton buttonDelete;
    private javax.swing.JButton buttonDeselect;
    private javax.swing.JButton buttonHilfe;
    private javax.swing.JButton buttonPrint;
    private javax.swing.JButton buttonSelect;
    private javax.swing.JButton buttonSuche;
    private javax.swing.JButton buttonUebernehmen;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel jLabel2;
    private java.awt.Label labelAnzahlDS;
    private java.awt.Label labelAnzahlM;
    private java.awt.Label labelAnzahlS;
    private java.awt.Label labelDatenSaetze;
    private javax.swing.JLabel labelHinweis;
    private java.awt.Label labelMarkiert;
    private java.awt.Label labelSelektiert;
    private javax.swing.JScrollPane scrollPanel;
    private javax.swing.JLabel seperatorLabel1;
    private javax.swing.JLabel seperatorLabel10;
    private javax.swing.JLabel seperatorLabel11;
    private javax.swing.JLabel seperatorLabel12;
    private javax.swing.JLabel seperatorLabel2;
    private javax.swing.JLabel seperatorLabel8;
    private javax.swing.JLabel seperatorLabel9;
    private javax.swing.JToolBar symbolLeiste;
    // End of variables declaration//GEN-END:variables
}
