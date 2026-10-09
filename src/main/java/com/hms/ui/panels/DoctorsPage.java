package com.hms.ui.panels;

import com.hms.Services;
import com.hms.dao.DataException;
import com.hms.model.Doctor;
import com.hms.ui.UI;
import com.hms.ui.components.Tables;
import com.hms.ui.dialogs.DoctorDialog;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class DoctorsPage extends Page {

    private final Services services;
    private final JLabel count = UI.subtitle("");
    private final Tables.RowModel<Doctor> model = new Tables.RowModel<>(
            new String[] {"Name", "Specialty", "Phone", "Email", "Consultation fee"},
            Doctor::name, Doctor::specialty, Doctor::phone, Doctor::email,
            d -> String.format(Locale.UK, "£%.2f", d.consultationFee()));
    private final JTable table = Tables.create(model);

    public DoctorsPage(Services services) {
        super("[grow,fill]", "[]2[]18[]12[grow,fill]");
        this.services = services;
        add(UI.title("Doctors"), "split 2");
        JButton add = UI.primary("Add doctor", "plus");
        add.addActionListener(e -> open(null));
        add(add, "growx 0, gapleft push, wrap");
        add(count, "wrap");
        JButton edit = UI.button("Edit", "edit");
        edit.addActionListener(e -> editSelected());
        JButton delete = UI.danger("Delete", "trash");
        delete.addActionListener(e -> deleteSelected());
        add(edit, "split 2, growx 0");
        add(delete, "growx 0, wrap");
        Tables.widths(table, 200, 160, 140, 260, 120);
        Tables.onDoubleClick(table, this::editSelected);
        add(Tables.scroll(table));
    }

    @Override
    public void refresh() {
        try {
            model.setRows(services.doctors.findAll());
            count.setText(model.getRowCount() + " doctors on staff");
        } catch (DataException e) {
            UI.error(this, e.getMessage());
        }
    }

    private Doctor selected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UI.info(this, "No doctor selected", "Select a doctor in the table first.");
            return null;
        }
        return model.get(table.convertRowIndexToModel(row));
    }

    private void editSelected() {
        Doctor d = selected();
        if (d != null) {
            open(d);
        }
    }

    private void open(Doctor d) {
        DoctorDialog dialog = new DoctorDialog(SwingUtilities.getWindowAncestor(this), services, d);
        dialog.setVisible(true);
        if (dialog.saved()) {
            refresh();
        }
    }

    private void deleteSelected() {
        Doctor d = selected();
        if (d != null && UI.confirm(this, "Delete doctor", "Remove " + d.name() + " from the staff list?")) {
            try {
                services.doctors.delete(d.id());
                refresh();
            } catch (DataException e) {
                UI.error(this, e.getMessage());
            }
        }
    }
}
