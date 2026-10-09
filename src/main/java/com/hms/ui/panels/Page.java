package com.hms.ui.panels;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

/** A screen shown in the main window's content area. Reloads its data whenever it is opened. */
public abstract class Page extends JPanel {

    protected Page(String columns, String rows) {
        super(new MigLayout("insets 28 32 28 32, fill", columns, rows));
    }

    public abstract void refresh();
}
