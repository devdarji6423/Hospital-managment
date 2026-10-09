package com.hms.ui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.Color;
import java.awt.Font;
import java.util.prefs.Preferences;
import javax.swing.UIManager;

/** Look and feel setup: FlatLaf with a teal accent, light or dark, remembered between runs. */
public final class Theme {

    public static final Color ACCENT = new Color(0x0D9488);
    public static final Color BLUE = new Color(0x3B82F6);
    public static final Color VIOLET = new Color(0x8B5CF6);
    public static final Color AMBER = new Color(0xF59E0B);
    public static final Color GREEN = new Color(0x16A34A);
    public static final Color RED = new Color(0xDC2626);

    private static final Preferences PREFS = Preferences.userNodeForPackage(Theme.class);
    private static final String DARK_KEY = "darkMode";

    private Theme() {
    }

    public static void install() {
        FlatLaf.registerCustomDefaultsSource("com.hms.themes");
        UIManager.put("defaultFont", new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        applyLaf(isDark());
    }

    public static boolean isDark() {
        try {
            return PREFS.getBoolean(DARK_KEY, false);
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static void setDark(boolean dark) {
        try {
            PREFS.putBoolean(DARK_KEY, dark);
        } catch (RuntimeException ignored) {
            // preferences unavailable: the choice just won't be remembered
        }
        FlatAnimatedLafChange.showSnapshot();
        applyLaf(dark);
        FlatLaf.updateUI();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    private static void applyLaf(boolean dark) {
        FlatLaf.setGlobalExtraDefaults(java.util.Map.of("@accentColor", "#0D9488"));
        if (dark) {
            FlatMacDarkLaf.setup();
        } else {
            FlatMacLightLaf.setup();
        }
    }

    /** Colour for secondary text in the current theme. */
    public static Color muted() {
        Color c = UIManager.getColor("Label.disabledForeground");
        return c != null ? c : Color.GRAY;
    }

    public static Color withAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }
}
