/**
 * HilfeDialog.java ist Teil des Programmes kodelasStradoku

 * Erzeugt am:                  23.01.2011, 23:54
 * Zuletzt geändert:            28.12.2019, 14:20
 * 
 * Copyright (C) Konrad Demmel, 2010 - 2020
 * 
 * Für die Bibliothek von javax.hlp sind die drei Dateien jh.jar, 
 * hsviewer.jar und StradokuHelp.jar anzugeben!
 *
 * Online-Dokumentation zu diesem Thema:
 * https://www.data2type.de/xml-xslt-xslfo/docbook/xhtml-erzeugen/weitere-ausgabemoeglichkeiten/javahelp-ausgeben/
 * http://www.scheb-online.de/JavaHelp.htm
 * https://javaee.github.io/javahelp/
 * https://en.wikipedia.org/wiki/DocBook
 * https://www.cs.hs-rm.de/~knauf/SWTProjekt2008/javahelp/#fullcode
 * https://www.cs.hs-rm.de/~knauf/SWTProjekt2008/javahelp/
 * https://docs.oracle.com/cd/E19253-01/819-0913/author/toc.html 
 * https://www.java-forum.org/thema/jhelp-anzeige-im-navigator.186063/post-1198286
 * Unter dobusish/system/custom-xsl/javahelp-commons.xsl die Zeilen (sind mehrere)
 * <tocitem target="{$id}"> durch <tocitem target="{$id}" expand="true"> ersetzen
*/

package stradoku;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.net.URL;
import javax.help.BadIDException;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.help.JHelp;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

/**
 * Zeigt die Hilfedatei oder einzelne Abschnitte davon.
 */
public class HilfeDialog extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;
    private static Stradoku strApp;

    /**
     * Konstruktor
     * @param mf    Referenz auf StradokuApp
     */
    public HilfeDialog(Stradoku mf) {
        strApp = mf;
    }

    /**
     * Öffnet den Hilfedialog
     * @param aktID anzuzeigenden Bereich
     * @param naviBereich true wenn Navibereich angezeigt werden soll
     */
    public void zeigeHilfe(String aktID, boolean naviBereich) {
        JHelp help;
        URL url;
        try {
            ClassLoader cl = Stradoku.class.getClassLoader();
            url = HelpSet.findHelpSet(cl, "jhelpset.hs");
            help = new JHelp(new HelpSet(cl, url));
        } catch (HeadlessException | HelpSetException | BadIDException ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), "Fehlermeldung", 1);
            return;
        }
        // Darzustellenden Abschnitt festlegen, ID muss im XML existieren!
        help.setCurrentID(aktID);
        help.setNavigatorDisplayed(naviBereich);
        help.setFont(new java.awt.Font("Serif", 0, 12));
        BorderLayout layout = (BorderLayout) help.getLayout();
        JSplitPane splitPane = (JSplitPane)layout.getLayoutComponent(help, BorderLayout.CENTER);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("kodelasStradoku - Infos");
        frame.setIconImage(strApp.getIconImage());
        frame.setLocation(strApp.getPosX()-5, strApp.getPosY());
        frame.setSize(1080, 725);
        frame.getContentPane().add(help);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        // Breite des Navigators
        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(265));
    }
}
