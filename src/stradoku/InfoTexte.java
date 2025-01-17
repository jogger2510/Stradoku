/**
 * InfoTexte.java ist Teil des Programmes kodelasStradoku
 *
 * Erzeugt am:                  24.09.2018 12:00
 * Letzte Änderung:             17.12.2020 12:50
 *
 * Copyright (C) Konrad Demmel, 2018-2020
 * 
 */package stradoku;

/**
 * Diverse Info-Texte
 */
public interface InfoTexte {
    // einbinden mit zeigeTipp(String)
    public final String ERSTINFO = "<html><br>"
        + "<h3 style=\"margin-left: 13px\">Willkommen zu kodelasStradoku</h3><br>"
        + "<p style=\"margin-left: 13px\"><b>Damit der Start leichter fällt, werden hier</p>"
        + "<p style=\"margin-left: 13px\"><b>für den Anfang Eingabehilfen angezeigt.</p><br>"
        + "<p style=\"margin-left: 13px\"><b>Diese Infos können über die Menüoption</p><br>"
        + "<p style=\"margin-left: 32px\"><b>\"Hilfe-Eingabehinweise zeigen\"</p><br>"
        + "<p style=\"margin-left: 13px\"><b>ausgeblendet werden.</p><br>"
        + "<h3 style=\"margin-left: 90px\">\"Ok\"</h3>"
        + "</html>";

    public final String EINGABEMODUS = "<html>"
        + "<p style=\"margin-top: 7px\"><b>Der Lösungsmodus ist aktiviert</b>"
        + "</p>"
        + "<p style=\"margin-top: 6px\">Lösungswerte werden wie folgt gesetzt:"
        + "</p>"
        + "<ol style=\"margin-left: 12px\">"
        + "<li><p style=\"margin-bottom: 3px\">über die Tastatur,</p></li>"
        + "<li><p style=\"margin-bottom: 3px\">über den Werte-Ziffernblock "
        + "hier oben rechts,</p></li>"
        + "<li><p style=\"margin-bottom: 3px\">mit gedrückter <b>Strg-Taste</b>"
        + " durch einen Mausklick auf die entsprechende Kandidatenposition. "
        + "Kandidaten müssen dafür nicht angezeigt werden und</p></li>"
        + "<li><p>in jedem der beiden Kandidatenmodi auch per Doppelklick "
        + "auf eine Kandidatenposition."
        + "</p></li></ol><p>In den ersten beiden Fällen ist "
        + "vorher die Zelle zu selektieren.</p></html>";

    public final String KNDLISTENMODUS = "<html>"
        + "<p style=\"margin-top: 7px\"><b>Die Kandidatenliste ist aktiviert."
        + "</b></p>"
        + "<p style=\"margin-top: 6px\">Die angezeigten Kandidaten sind noch "
        + "nicht ausgeschlossene Lösungswerte. Gibt es in einer Zelle nur noch "
        + "einen Kandidaten, muss dieser der Lösungswert sein.</p>"
        + "<p>So können Kandidaten ausgeschlossen werden:</p>"
        + "<ol style=\"margin-left: 12px\">"
        + "<li><p>mit gedrückter <b>Shift-Taste</b> über die Tastatur "
        + "oder per Mausklick.</p></li>"
        + "<li><p>über den Kandidaten-Ziffernblock hier oben links "
        + "auf einen Kandidaten.</p></li></ol><p>In den ersten beiden Fällen "
        + "ist vorher die Zelle zu selektieren.</p></html>";
        
    public final String NOTIZENMODUS = "<html>"
        + "<p><b>Der Notizenmodus ist aktiviert.</b></p>"
        + "<p style=\"margin-top: 6px\">Kandidaten, welche als Lösungswerte "
        + "in Betracht kommen, können eintragen und auch wieder entfernt werden."
        + "</p>"
        + "<p style=\"margin-top: 3px\">So können Kandidaten eingetragen werden:"
        + "</p>"
        + "<ol style=\"margin-left: 12px\">"
        + "<li><p style=\"margin-bottom: 3px\">mit gedrückter <b>Shift-Taste</b>"
        + " über die Tastatur oder per Mausklick auf eine Kandidatenposition"
        + "</p></li>"
        + "<li><p>über den Kandidaten-Ziffernblock hier oben links"
        + "</p></li></ol>"
        + "<p>In den ersten beiden Fällen ist vorher die Zelle zu selektieren."
        + "</p><p>Das Entfernen von notierten Kandidaten erfolgt in gleicher "
        + "Weise.</p></html>";    
    
        public final String EDITMODUS = "<html><br>"
        + "<p style=\"margin-bottom: 10px\"><b>Der Bearbeitungsmodus ist "
        + "aktiviert</b></p>"
        + "<p style=\"margin-bottom: 6px\">Die zchwarzen <b>Sperrzellen</b> "
        + "werden mit der <b>S</b>-Taste oder bei "
        + "gedrückter <b>Shift</b>-Taste durch einen Mausklick in die "
        + "jeweilige Zelle gesetzt oder entfernt."
        + "</p><p style=\"margin-bottom: 6px\">Vorgabe- und Sperrwerte werden "
        + "wie im Lösungsmodus gesetzt. Sie können auch entfernt werden"
        + "</p><p>Das Entfernen von Werten (auch Sperrwerten) erfolgt "
        + "in gleicher Weise wie das Setzen, über die Tastatur (Leertaste "
        + "oder '0'), die Zifferntasten des Werte-Blocks oder mit gedrückter "
        + "<b>Strg-Taste</b> und einem Mausklick in die Zelle.</p></html>";
}
