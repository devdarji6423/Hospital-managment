package com.hms.ui.panels;

import com.hms.Services;
import com.hms.dao.DataException;
import com.hms.model.Appointment;
import com.hms.model.Appointment.Status;
import com.hms.ui.UI;
import com.hms.ui.components.Tables;
import com.hms.ui.dialogs.AppointmentDialog;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AppointmentsPage extends Page {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("EEE dd MMM yyyy", Locale.UK);

    private final Services services;
    private final JTextField search = UI.searchField("Search by patient, visiting number or doctor");
    private final JComboBox<Object> statusFilter = new JComboBox<>(new Object[] {"All statuses", Status.SCHEDULED, Status.COMPLETED, Status.CANCELLED});
    private final JLabel count = UI.subtitle("");
    private final Tables.RowModel<Appointment> model = new Tables.RowModel<>(
            new String[] {"Date", "Time", "Patient", "Visiting no.", "Doctor", "Reason", "Status", "Cost"},
            a -> a.date().format(DATE), a -> a.time().toString(), Appointment::patientName, Appointment::visitingNumber,
            Appointment::doctorName, Appointment::reason, Appointment::status, a -> String.format(Locale.UK, "£%.2f", a.cost()));
    private final JTable table = Tables.create(model);

    public AppointmentsPage(Services services) {
        super("[grow,fill]", "[]2[]18[]12[grow,fill]");
        this.services = services;
        table.setAutoCreateRowSorter(false); // keep chronological order from the database

        add(UI.title("Appointments"), "split 2");
        JButton book = UI.primary("Book appointment", "plus");
        book.addActionListener(e -> open(null));
        add(book, "growx 0, gapleft push, wrap");
        add(count, "wrap");

        JButton edit = UI.button("Edit", "edit");
        edit.addActionListener(e -> editSelected());
        JButton complete = UI.button("Mark completed", "check");
        complete.addActionListener(e -> setStatus(Status.COMPLETED));
        JButton cancel = UI.danger("Cancel", "cancel");
        cancel.addActionListener(e -> setStatus(Status.CANCELLED));
        add(search, "split 5, h 36!, growx");
        add(statusFilter, "growx 0, w 150!");
        add(edit, "growx 0");
        add(complete, "growx 0");
        add(cancel, "growx 0, wrap");

        Tables.widths(table, 135, 60, 190, 100, 160, 170, 110, 80);
        Tables.onDoubleClick(table, this::editSelected);
        add(Tables.scroll(table));

        statusFilter.addActionListener(e -> refresh());
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
            Object f = statusFilter.getSelectedItem();
            List<Appointment> rows = services.appointments.search(search.getText(), f instanceof Status s ? s : null);
            model.setRows(rows);
            long scheduled = rows.stream().filter(a -> a.status() == Status.SCHEDULED).count();
            count.setText(rows.size() + " appointments · " + scheduled + " scheduled");
        } catch (DataException e) {
            UI.error(this, e.getMessage());
        }
    }

    private Appointment selected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UI.info(this, "No appointment selected", "Select an appointment in the table first.");
            return null;
        }
        return model.get(table.convertRowIndexToModel(row));
    }

    private void editSelected() {
        Appointment a = selected();
        if (a != null) {
            open(a);
        }
    }

    private void open(Appointment a) {
        AppointmentDialog d = new AppointmentDialog(SwingUtilities.getWindowAncestor(this), services, a, null);
        d.setVisible(true);
        if (d.saved()) {
            refresh();
        }
    }

    private void setStatus(Status status) {
        Appointment a = selected();
        if (a == null || a.status() == status) {
            return;
        }
        if (status == Status.CANCELLED && !UI.confirm(this, "Cancel appointment",
                "Cancel " + a.patientName() + "'s appointment with " + a.doctorName() + " on " + a.date().format(DATE) + "?")) {
            return;
        }
        try {
            services.appointments.updateStatus(a.id(), status);
            refresh();
        } catch (DataException e) {
            UI.error(this, e.getMessage());
        }
    }
}
