package com.hms.ui.panels;

import com.hms.Services;
import com.hms.dao.DataException;
import com.hms.model.Condition;
import com.hms.model.User;
import com.hms.ui.Theme;
import com.hms.ui.UI;
import com.hms.ui.components.Card;
import java.nio.file.Paths;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

/** Manage the list of medical conditions, and see account and app details. */
public class SettingsPage extends Page {

    private final Services services;
    private final DefaultListModel<Condition> conditions = new DefaultListModel<>();
    private final JList<Condition> list = new JList<>(conditions);
    private final JTextField newCondition = UI.placeholder(new JTextField(), "New condition, e.g. Arthritis");

    public SettingsPage(Services services, User user) {
        super("[grow,fill]16[340!,fill]", "[]2[]18[grow,fill]");
        this.services = services;
        add(UI.title("Settings"), "span 2, wrap");
        add(UI.subtitle("Reference data and information about this installation."), "span 2, wrap");

        Card cond = new Card(new MigLayout("insets 0, fill", "[grow,fill][]", "[]4[]12[]10[grow,fill]8[]"));
        cond.add(UI.section("Medical conditions"), "span 2, wrap");
        cond.add(UI.subtitle("These appear in the Condition list when registering a patient."), "span 2, wrap");
        JButton add = UI.primary("Add", "plus");
        add.addActionListener(e -> addCondition());
        newCondition.addActionListener(e -> addCondition());
        cond.add(newCondition, "h 34!");
        cond.add(add, "wrap");
        list.setFixedCellHeight(32);
        JScrollPane sp = new JScrollPane(list);
        cond.add(sp, "span 2, grow, wrap");
        JButton remove = UI.danger("Remove selected", "trash");
        remove.addActionListener(e -> removeCondition());
        cond.add(remove, "span 2, alignx right");
        add(cond, "grow");

        Card about = new Card(new MigLayout("insets 0, fillx, wrap 2", "[]12[grow]", ""));
        about.add(UI.section("Signed in as"), "span 2");
        about.add(muted("Name"));
        about.add(new JLabel(user.fullName()));
        about.add(muted("Username"));
        about.add(new JLabel(user.username()));
        about.add(muted("Role"));
        about.add(new JLabel(user.role()));
        about.add(UI.section("About"), "span 2, gaptop 18");
        about.add(muted("Version"));
        about.add(new JLabel("2.0"));
        about.add(muted("Database"));
        String db = System.getenv("HMS_DB") != null ? System.getenv("HMS_DB") : Paths.get("data", "hospital.db").toAbsolutePath().toString();
        JLabel dbLabel = new JLabel(Paths.get(db).getFileName().toString());
        dbLabel.setToolTipText(db);
        about.add(dbLabel);
        about.add(muted("Built with"));
        about.add(new JLabel("<html>Java 17, Swing, FlatLaf, SQLite</html>"), "wmax 220");
        add(about, "aligny top, growy 0");
    }

    @Override
    public void refresh() {
        try {
            conditions.clear();
            services.conditions.findAll().forEach(conditions::addElement);
        } catch (DataException e) {
            UI.error(this, e.getMessage());
        }
    }

    private void addCondition() {
        if (newCondition.getText().isBlank()) {
            return;
        }
        try {
            services.conditions.add(newCondition.getText());
            newCondition.setText("");
            refresh();
        } catch (DataException e) {
            UI.error(this, e.getMessage());
        }
    }

    private void removeCondition() {
        Condition c = list.getSelectedValue();
        if (c == null) {
            UI.info(this, "Nothing selected", "Select a condition in the list first.");
            return;
        }
        if (UI.confirm(this, "Remove condition", "Remove \"" + c.name() + "\"?\nPatients with this condition will show none.")) {
            services.conditions.delete(c.id());
            refresh();
        }
    }

    private static JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Theme.muted());
        return l;
    }
}
