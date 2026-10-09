package com.hms.ui;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Component;
import java.awt.Font;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/** Small helpers so the screens share one visual vocabulary. */
public final class UI {

    private UI() {
    }

    public static JLabel title(String text) {
        JLabel l = new JLabel(text);
        l.putClientProperty(FlatClientProperties.STYLE, "font: bold +10");
        return l;
    }

    public static JLabel subtitle(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Theme.muted());
        return l;
    }

    public static JLabel section(String text) {
        JLabel l = new JLabel(text);
        l.putClientProperty(FlatClientProperties.STYLE, "font: bold +2");
        return l;
    }

    public static JButton primary(String text, String icon) {
        JButton b = new JButton(text);
        if (icon != null) {
            b.setIcon(Icons.get(icon, 16, java.awt.Color.WHITE));
        }
        b.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        b.putClientProperty(FlatClientProperties.STYLE,
                "background: $Component.accentColor; foreground: #ffffff; font: bold; margin: 6,14,6,14;"
                        + "hoverBackground: darken($Component.accentColor,6%); pressedBackground: darken($Component.accentColor,12%)");
        return b;
    }

    public static JButton button(String text, String icon) {
        JButton b = new JButton(text);
        if (icon != null) {
            b.setIcon(Icons.get(icon, 16));
        }
        b.putClientProperty(FlatClientProperties.STYLE, "margin: 6,12,6,12");
        return b;
    }

    public static JButton danger(String text, String icon) {
        JButton b = button(text, icon);
        if (icon != null) {
            b.setIcon(Icons.get(icon, 16, Theme.RED));
        }
        b.setForeground(Theme.RED);
        return b;
    }

    public static JTextField searchField(String placeholder) {
        JTextField f = new JTextField();
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        f.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, Icons.get("search", 16));
        f.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        return f;
    }

    public static <T extends JComponent> T placeholder(T field, String text) {
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, text);
        return field;
    }

    public static void markError(JComponent c, boolean error) {
        c.putClientProperty(FlatClientProperties.OUTLINE, error ? FlatClientProperties.OUTLINE_ERROR : null);
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Something went wrong", JOptionPane.ERROR_MESSAGE);
    }

    public static void info(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String title, String message) {
        return JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static void bold(JLabel l) {
        l.setFont(l.getFont().deriveFont(Font.BOLD));
    }

    public static void flat(AbstractButton b) {
        b.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON);
    }
}
