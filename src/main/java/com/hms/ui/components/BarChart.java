package com.hms.ui.components;

import com.hms.ui.Theme;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.UIManager;

/** Minimal bar chart (no chart library needed) for the dashboard's appointment trend. */
public class BarChart extends JComponent {

    private int[] values = new int[0];
    private String[] labels = new String[0];
    private int highlight = -1;

    public BarChart() {
        setPreferredSize(new Dimension(400, 220));
    }

    /** @param highlightIndex bar drawn in full accent colour (e.g. today), or -1 */
    public void setData(int[] values, String[] labels, int highlightIndex) {
        this.values = values;
        this.labels = labels;
        this.highlight = highlightIndex;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        FontMetrics fm = g2.getFontMetrics();
        int top = 18;
        int bottom = getHeight() - fm.getHeight() - 6;
        int left = 28;
        int width = getWidth() - left - 4;
        int max = 1;
        for (int v : values) {
            max = Math.max(max, v);
        }
        int step = Math.max(1, (int) Math.ceil(max / 4.0));
        int scaleMax = step * 4;

        g2.setStroke(new BasicStroke(1f));
        for (int i = 0; i <= 4; i++) {
            int y = bottom - (bottom - top) * i / 4;
            g2.setColor(UIManager.getColor("Component.borderColor"));
            g2.drawLine(left, y, left + width, y);
            g2.setColor(Theme.muted());
            String s = String.valueOf(step * i);
            g2.drawString(s, left - fm.stringWidth(s) - 8, y + fm.getAscent() / 2 - 1);
        }
        if (values.length == 0) {
            g2.dispose();
            return;
        }
        double slot = (double) width / values.length;
        int barW = (int) Math.max(4, slot * 0.58);
        for (int i = 0; i < values.length; i++) {
            int x = left + (int) (slot * i + (slot - barW) / 2);
            int h = (int) ((bottom - top) * (values[i] / (double) scaleMax));
            g2.setColor(i == highlight ? Theme.ACCENT : Theme.withAlpha(Theme.ACCENT, 120));
            if (h > 0) {
                g2.fillRoundRect(x, bottom - h, barW, h, 6, 6);
            }
            if (values[i] > 0) {
                g2.setColor(UIManager.getColor("Label.foreground"));
                String v = String.valueOf(values[i]);
                g2.drawString(v, x + (barW - fm.stringWidth(v)) / 2, bottom - h - 4);
            }
            if (i < labels.length && (values.length <= 14 || i % 2 == 0)) {
                g2.setColor(Theme.muted());
                g2.drawString(labels[i], x + (barW - fm.stringWidth(labels[i])) / 2, getHeight() - 4);
            }
        }
        g2.dispose();
    }
}
