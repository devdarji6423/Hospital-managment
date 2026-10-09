package com.hms.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.hms.ui.Icons;
import com.hms.ui.Theme;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

/** Dashboard tile: coloured icon badge, big number and a caption. */
public class StatCard extends Card {

    private final JLabel value = new JLabel("–");
    private final JLabel caption = new JLabel();

    public StatCard(String title, String icon, Color color) {
        super(new MigLayout("insets 0, gap 4 2", "[]14[grow]", "[][]"));
        JComponent badge = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.withAlpha(color, 38));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                Icons.get(icon, 22, color).paintIcon(this, g2, (getWidth() - 22) / 2, (getHeight() - 22) / 2);
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(46, 46));

        JLabel t = new JLabel(title);
        t.setForeground(Theme.muted());
        value.putClientProperty(FlatClientProperties.STYLE, "font: bold +12");
        caption.setForeground(Theme.muted());
        caption.putClientProperty(FlatClientProperties.STYLE, "font: -1");

        add(badge, "spany 3, aligny center");
        add(t, "wrap");
        add(value, "wrap");
        add(caption, "skip 1");
    }

    public void setValue(String text, String captionText) {
        value.setText(text);
        caption.setText(captionText);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (caption != null) {
            caption.setForeground(Theme.muted());
        }
    }
}
