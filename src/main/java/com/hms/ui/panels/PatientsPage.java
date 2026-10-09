package com.hms.ui.panels;

import com.hms.Services;
import com.hms.dao.DataException;
import com.hms.model.Patient;
import com.hms.ui.UI;
import com.hms.ui.components.Tables;
import com.hms.ui.dialogs.PatientDialog;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PatientsPage extends Page {

    private final Services services;
    private final JTextField search = UI.searchField("Search by name, visiting number, phone, postcode or condition");
    private final JLabel count = UI.subtitle("");
    private final Tables.RowModel<Patient> model = new Tables.RowModel<>(
            new String[] {"Visiting no.", "Name", "Age", "Gender", "Phone", "Condition", "City"},
            Patient::visitingNumber, Patient::fullName, Patient::age, Patient::gender, Patient::phone,
            p -> p.conditionName() == null ? "—" : p.conditionName(), Patient::city);
    private final JTable table = Tables.create(model);

    public PatientsPage(Services services) {
        super("[grow,fill]", "[]2[]18[]12[grow,fill]");
        this.services = services;

        add(UI.title("Patients"), "split 2");
        JButton add = UI.primary("Register patient", "plus");
        add.addActionListener(e -> openDialog(null));
        add(add, "growx 0, gapleft push, wrap");
        add(count, "wrap");

        JButton edit = UI.button("Edit", "edit");
        edit.addActionListener(e -> editSelected());
        JButton delete = UI.danger("Delete", "trash");
        delete.addActionListener(e -> deleteSelected());
        JButton export = UI.button("Export CSV", "download");
        export.addActionListener(e -> exportCsv());
        add(search, "split 4, h 36!, growx");
        add(edit, "growx 0");
        add(delete, "growx 0");
        add(export, "growx 0, wrap");

        Tables.widths(table, 100, 210, 50, 70, 130, 160, 120);
        Tables.onDoubleClick(table, this::editSelected);
        add(Tables.scroll(table));

        Timer debounce = new Timer(200, e -> refresh());
        debounce.setRepeats(false);
        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { debounce.restart(); }
            public void removeUpdate(DocumentEvent e) { debounce.restart(); }
            public void changedUpdate(DocumentEvent e) { debounce.restart(); }
        });
    }

    @Override
    public void refresh() {
        try {
            List<Patient> rows = services.patients.search(search.getText());
            model.setRows(rows);
            count.setText(rows.size() + (rows.size() == 1 ? " patient" : " patients")
                    + (search.getText().isBlank() ? " registered" : " found"));
        } catch (DataException e) {
            UI.error(this, e.getMessage());
        }
    }

    private Patient selected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UI.info(this, "No patient selected", "Select a patient in the table first.");
            return null;
        }
        return model.get(table.convertRowIndexToModel(row));
    }

    private void editSelected() {
        Patient p = selected();
        if (p != null) {
            openDialog(p);
        }
    }

    private void openDialog(Patient existing) {
        PatientDialog d = new PatientDialog(javax.swing.SwingUtilities.getWindowAncestor(this), services, existing);
        d.setVisible(true);
        if (d.saved()) {
            refresh();
        }
    }

    private void deleteSelected() {
        Patient p = selected();
        if (p == null) {
            return;
        }
        if (UI.confirm(this, "Delete patient", "Delete " + p.fullName() + " (#" + p.visitingNumber()
                + ")?\nTheir next of kin and appointment history will also be removed.")) {
            try {
                services.patients.delete(p.id());
                refresh();
            } catch (DataException e) {
                UI.error(this, e.getMessage());
            }
        }
    }

    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("patients.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path file = chooser.getSelectedFile().toPath();
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(file, StandardCharsets.UTF_8))) {
            out.println("visiting_number,title,first_name,last_name,date_of_birth,gender,email,phone,address,condition");
            for (Patient p : model.rows()) {
                out.println(String.join(",", csv(p.visitingNumber()), csv(p.title()), csv(p.firstName()), csv(p.lastName()),
                        csv(p.dob().toString()), csv(p.gender()), csv(p.email()), csv(p.phone()), csv(p.address()),
                        csv(p.conditionName())));
            }
            UI.info(this, "Export complete", "Saved " + model.getRowCount() + " patients to " + file.getFileName());
        } catch (IOException e) {
            UI.error(this, "Could not write the file: " + e.getMessage());
        }
    }

    static String csv(String v) {
        if (v == null) {
            return "";
        }
        return v.contains(",") || v.contains("\"") || v.contains("\n") ? "\"" + v.replace("\"", "\"\"") + "\"" : v;
    }
}
