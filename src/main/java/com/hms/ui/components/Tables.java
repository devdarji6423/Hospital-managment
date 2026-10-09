package com.hms.ui.components;

import com.hms.model.Appointment.Status;
import com.hms.ui.Theme;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/** Table helpers: a read-only model backed by a list of rows, consistent styling, and the status pill renderer. */
public final class Tables {

    private Tables() {
    }

    public static final class RowModel<T> extends AbstractTableModel {
        private final String[] columns;
        private final List<Function<T, Object>> getters;
        private List<T> rows = List.of();

        @SafeVarargs
        public RowModel(String[] columns, Function<T, Object>... getters) {
            this.columns = columns;
            this.getters = List.of(getters);
        }

        public void setRows(List<T> rows) {
            this.rows = List.copyOf(rows);
            fireTableDataChanged();
        }

        public List<T> rows() {
            return rows;
        }

        public T get(int modelRow) {
            return rows.get(modelRow);
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int c) {
            return columns[c];
        }

        @Override
        public Object getValueAt(int r, int c) {
            return getters.get(c).apply(rows.get(r));
        }

        @Override
        public Class<?> getColumnClass(int c) {
            return rows.isEmpty() ? Object.class : nonNullClass(c);
        }

        private Class<?> nonNullClass(int c) {
            for (T row : rows) {
                Object v = getters.get(c).apply(row);
                if (v != null) {
                    return v.getClass();
                }
            }
            return Object.class;
        }
    }

    public static JTable create(AbstractTableModel model) {
        JTable t = new JTable(model);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setFillsViewportHeight(true);
        t.setAutoCreateRowSorter(true);
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().putClientProperty("FlatLaf.style", "cellMargins: 2,10,2,10");
        if (t.getTableHeader().getDefaultRenderer() instanceof DefaultTableCellRenderer r) {
            r.setHorizontalAlignment(SwingConstants.LEADING);
        }
        t.setDefaultRenderer(Status.class, new StatusRenderer());
        DefaultTableCellRenderer padded = padded();
        t.setDefaultRenderer(Object.class, padded);
        t.setDefaultRenderer(String.class, padded);
        t.setDefaultRenderer(Integer.class, padded);
        return t;
    }

    public static JScrollPane scroll(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(javax.swing.UIManager.getColor("Component.borderColor")));
        return sp;
    }

    /** Calls the action when a row is double-clicked. */
    public static void onDoubleClick(JTable t, Runnable action) {
        t.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && t.rowAtPoint(e.getPoint()) >= 0) {
                    action.run();
                }
            }
        });
    }

    public static void widths(JTable t, int... widths) {
        for (int i = 0; i < widths.length && i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private static DefaultTableCellRenderer padded() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean sel, boolean focus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, sel, false, row, col);
                l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                l.setHorizontalAlignment(SwingConstants.LEADING);
                return l;
            }
        };
    }

    /** Draws an appointment status as a coloured pill. */
    static final class StatusRenderer extends DefaultTableCellRenderer {
        private Color pill = Color.GRAY;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean sel, boolean focus, int row, int col) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, sel, false, row, col);
            Status s = (Status) value;
            pill = switch (s) {
                case SCHEDULED -> Theme.BLUE;
                case COMPLETED -> Theme.GREEN;
                case CANCELLED -> Theme.RED;
            };
            l.setForeground(pill);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            l.putClientProperty("FlatLaf.style", "font: bold -1");
            return l;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = Math.min(getWidth() - 12, 96);
            int h = 22;
            g2.setColor(Theme.withAlpha(pill, 34));
            g2.fillRoundRect((getWidth() - w) / 2, (getHeight() - h) / 2, w, h, h, h);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
