package com.hms.ui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.hms.Services;
import com.hms.dao.DataException;
import com.hms.model.Appointment;
import com.hms.model.Appointment.Status;
import com.hms.model.Doctor;
import com.hms.model.Patient;
import com.hms.ui.Theme;
import com.hms.ui.UI;
import com.hms.util.Validator;
import java.awt.Window;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

/** Book a new appointment or edit one; prevents double-booking a doctor's slot. */
public class AppointmentDialog extends JDialog {

    private final Services services;
    private final Appointment existing;
    private boolean saved;

    private final JComboBox<Patient> patient = new JComboBox<>();
    private final JComboBox<Doctor> doctor = new JComboBox<>();
    private final JTextField date = UI.placeholder(new JTextField(), "dd/mm/yyyy");
    private final JComboBox<LocalTime> time = new JComboBox<>();
    private final JTextField reason = UI.placeholder(new JTextField(), "e.g. Follow-up");
    private final JComboBox<Status> status = new JComboBox<>(Status.values());
    private final JTextField cost = new JTextField();
    private final JTextArea notes = new JTextArea(3, 20);
    private final JLabel error = new JLabel(" ");

    /** @param preselectPatient patient to book for (from the records screen), or null */
    public AppointmentDialog(Window owner, Services services, Appointment existing, Patient preselectPatient) {
        super(owner, existing == null ? "Book appointment" : "Edit appointment", ModalityType.APPLICATION_MODAL);
        this.services = services;
        this.existing = existing;

        for (LocalTime t = LocalTime.of(8, 0); !t.isAfter(LocalTime.of(18, 0)); t = t.plusMinutes(15)) {
            time.addItem(t);
        }
        List<Patient> patients = services.patients.search("");
        patients.forEach(patient::addItem);
        services.doctors.findAll().forEach(doctor::addItem);
        doctor.addActionListener(e -> {
            Doctor d = (Doctor) doctor.getSelectedItem();
            if (d != null && this.existing == null) {
                cost.setText(String.format(Locale.UK, "%.2f", d.consultationFee()));
            }
        });

        JPanel p = new JPanel(new MigLayout("insets 24 28 20 28, fillx, wrap 2", "[right]12[grow,fill,320]", ""));
        JLabel heading = new JLabel(existing == null ? "Book an appointment" : "Edit appointment");
        heading.putClientProperty(FlatClientProperties.STYLE, "font: bold +6");
        p.add(heading, "span 2, alignx left, gapbottom 12");
        p.add(new JLabel("Patient"));
        p.add(patient);
        p.add(new JLabel("Doctor"));
        p.add(doctor);
        p.add(new JLabel("Date"));
        p.add(date, "split 2");
        p.add(time, "w 100!");
        p.add(new JLabel("Reason"));
        p.add(reason);
        p.add(new JLabel("Status"));
        p.add(status);
        p.add(new JLabel("Cost (£)"));
        p.add(cost);
        p.add(new JLabel("Notes"), "aligny top");
        notes.setLineWrap(true);
        notes.setWrapStyleWord(true);
        p.add(new JScrollPane(notes));
        error.setForeground(Theme.RED);
        p.add(error, "span 2, gaptop 6");
        JButton cancel = new JButton("Close");
        cancel.addActionListener(e -> dispose());
        JButton save = UI.primary(existing == null ? "Book appointment" : "Save changes", "check");
        save.addActionListener(e -> save());
        p.add(cancel, "span 2, split 2, growx 0, gapleft push");
        p.add(save, "growx 0");
        setContentPane(p);
        getRootPane().setDefaultButton(save);

        if (existing != null) {
            select(patient, x -> x.id() == existing.patientId());
            select(doctor, x -> x.id() == existing.doctorId());
            date.setText(existing.date().format(Validator.UK_DATE));
            time.setSelectedItem(existing.time());
            reason.setText(existing.reason());
            status.setSelectedItem(existing.status());
            cost.setText(String.format(Locale.UK, "%.2f", existing.cost()));
            notes.setText(existing.notes());
        } else {
            if (preselectPatient != null) {
                select(patient, x -> x.id() == preselectPatient.id());
            }
            date.setText(LocalDate.now().plusDays(1).format(Validator.UK_DATE));
            time.setSelectedItem(LocalTime.of(9, 0));
            status.setSelectedItem(Status.SCHEDULED);
            Doctor d = (Doctor) doctor.getSelectedItem();
            if (d != null) {
                cost.setText(String.format(Locale.UK, "%.2f", d.consultationFee()));
            }
        }
        pack();
        setLocationRelativeTo(owner);
    }

    public boolean saved() {
        return saved;
    }

    private static <T> void select(JComboBox<T> box, java.util.function.Predicate<T> match) {
        for (int i = 0; i < box.getItemCount(); i++) {
            if (match.test(box.getItemAt(i))) {
                box.setSelectedIndex(i);
                return;
            }
        }
    }

    private void save() {
        Patient p = (Patient) patient.getSelectedItem();
        Doctor d = (Doctor) doctor.getSelectedItem();
        LocalDate day = Validator.parseUkDate(date.getText());
        LocalTime slot = (LocalTime) time.getSelectedItem();
        Status st = (Status) status.getSelectedItem();
        String problem = null;
        if (p == null) {
            problem = "Register a patient first";
        } else if (d == null) {
            problem = "Add a doctor first";
        } else if (day == null) {
            problem = "Date must be a real date in dd/mm/yyyy format";
        } else if (Validator.money("Cost", cost.getText()) != null) {
            problem = Validator.money("Cost", cost.getText());
        } else if (st == Status.SCHEDULED && existing == null && day.isBefore(LocalDate.now())) {
            problem = "New appointments can't be booked in the past";
        } else if (st == Status.SCHEDULED && services.appointments.isDoctorBooked(d.id(), day, slot, existing == null ? 0 : existing.id())) {
            problem = d.name() + " is already booked at " + slot + " on that day";
        }
        UI.markError(date, day == null);
        if (problem != null) {
            error.setText(problem);
            return;
        }
        try {
            services.appointments.save(new Appointment(existing == null ? 0 : existing.id(), p.id(), null, null, d.id(), null,
                    day, slot, reason.getText().trim(), st, Double.parseDouble(cost.getText().trim()),
                    notes.getText().isBlank() ? null : notes.getText().trim()));
            saved = true;
            dispose();
        } catch (DataException e) {
            UI.error(this, e.getMessage());
        }
    }
}
