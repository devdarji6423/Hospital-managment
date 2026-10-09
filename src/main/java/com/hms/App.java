package com.hms;

import com.hms.db.Database;
import com.hms.ui.LoginFrame;
import com.hms.ui.Theme;
import com.hms.ui.UI;
import javax.swing.SwingUtilities;

/** Entry point: applies the theme, opens (or creates) the database, and shows the login window. */
public final class App {

    private App() {
    }

    public static void main(String[] args) {
        System.setProperty("flatlaf.uiScale.allowScaleDown", "true");
        SwingUtilities.invokeLater(() -> {
            Theme.install();
            try {
                Services services = new Services(Database.get());
                new LoginFrame(services).setVisible(true);
            } catch (RuntimeException e) {
                UI.error(null, "The database could not be opened:\n" + e.getMessage());
                System.exit(1);
            }
        });
    }
}
