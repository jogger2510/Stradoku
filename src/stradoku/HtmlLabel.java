/**
 * HtmlLabel.java ist Teil des Programmes kodelasStradoku
 *
 * HTML-fähige Label-Klasse.
 * Da JLabel zwar in der Lage ist HTML-Code darzustellen, nicht aber
 * ausführbare Hyperlinks, stellt diese Klasse eine erweiterte Funktionalität
 * dafür bereit.
 * Sie ist ein erweiterter JEditorPane mit integrierter Hyperlink-Unterstützung
 * und ermöglicht, Hyperlinks in Host-Systeme der Standard-Browser
 * funktionsfähig darzustellen.
 * Um die Funktionalität nachzvollziehen, empfiehlt sich ein Blick in die
 * JLabel Dokumentation.
 *
 * Beispiele in UpdateDialog.java
 *
 * Übernommen und angepasst am: 01.12.2011 13:30
 * Letzte Änderung:             06.02.2020 11:40
 * 
 * Übernommen von kappesf (www.java-forum.org)
 * 
* Copyright (C) kappesf und Konrad Demmel, 2010 - 2020
* 
* Meine Anfrage dazu vom 30.11.2011 im Java Forum:
* https://www.java-forum.org/thema/aktiver-link-in-einem-dialog.128034/#post-834161
 */

package stradoku;

import java.awt.Desktop;
import java.awt.Font;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;

/**
 * Erweitert die HTML-Möglichkeiten von Java für die Anzeige von Hyperlinks.
 */
public class HtmlLabel extends JEditorPane {

    private final javax.swing.JDialog udDlg;

    /**
     * Eingebettete Klasse für die Hyperlink-Erweiterung
     */
    private class MyHyperlinkListener implements HyperlinkListener {

        @Override
        public void hyperlinkUpdate(final HyperlinkEvent evt) {
            final HyperlinkEvent.EventType typ = evt.getEventType();
            if (typ == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    try {
                        String urlFF = evt.getURL().toString();
                        urlFF = urlFF.replace("\\", "/");
                        urlFF = urlFF.replace(" ", "%20");
                        Desktop.getDesktop().browse(new URI(urlFF));
                        udDlg.setVisible(false);
                        udDlg.dispose();
                    } catch (final IOException ex) {
                        System.err.println("hyperlink not found!");
                    }
                } catch (URISyntaxException ex) {
                    Logger.getLogger(HtmlLabel.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Konstruktor
     * @param dlg Referenz zu Dialog mit Hyperlink
     * @param htmlText anzuzeigender Link-Text
     */
    public HtmlLabel(javax.swing.JDialog dlg, final String htmlText) {
        udDlg = dlg;
        setContentType("text/html");
        setText(htmlText);
        final Font font = UIManager.getFont("Label.font");
        final String bodyRule = "body { font-family: "
                + font.getFamily() + "; " + "font-size: "
                + font.getSize() + "pt; }";
        ((HTMLDocument) getDocument()).getStyleSheet().addRule(bodyRule);
        setBorder(null);
        setOpaque(false);
        setEditable(false);
        addHyperlinkListener(new MyHyperlinkListener());
        setFocusable(false);
    }
}
