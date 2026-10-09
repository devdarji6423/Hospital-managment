package com.hms.ui.panels;

import com.formdev.flatlaf.FlatClientProperties;
import com.hms.Services;
import com.hms.dao.DataException;
import com.hms.model.Appointment;
import com.hms.model.Appointment.Status;
import com.hms.model.NextOfKin;
import com.hms.model.Patient;
import com.hms.ui.Icons;
import com.hms.ui.Theme;
import com.hms.ui.UI;
import com.hms.ui.components.Card;
import com.hms.ui.components.Tables;
import com.hms.ui.dialogs.AppointmentDialog;
import com.hms.util.Validator;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import net.miginfocom.swing.MigLayout;

/** Look up a patient by visiting number and see their full profile and visit history (the old "Previous Record"). */
public class RecordsPage extends Page {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.UK);

    private final Services services;
    private final JTextField number = UI.searchField("Enter an 8-digit visiting number");
    private final JPanel result = new JPanel(new MigLayout("insets 0, fill", "[grow,fill]", "[grow,fill]"));
    private final Tables.RowModel<Appointment> history = new Tables.RowModel<>(
            new String[] {"Date", "Time", "Doctor", "Reason", "Status", "Cost"},
            a -> a.date().format(DATE), a -> a.time().toString(), Appointment::doctorName, Appointment::reason,
            Appointment::status, a -> String.format(Locale.UK, "£%.2f", a.cost()));
    private Patient current;

    public RecordsPage(Services services) {
        super("[grow,fill]", "[]2[]18[]16[grow,fill]");
        this.services = services;
        add(UI.title("Patient Records"), "wrap");
        add(UI.subtitle("Find a patient's profile, next of kin and full appointment history by visiting number."), "wrap");
        JButton find = UI.primary("Find record", "search");
        find.addActionListener(e -> lookup());
        number.addActionListener(e -> lookup());
        JPanel bar = new JPanel(new MigLayout("insets 0", "[360!]8[]"));
        bar.setOpaque(false);
        bar.add(number, "h 36!, growx");
        bar.add(find);
        add(bar, "growx 0, wrap");
        result.setOpaque(false);
        add(result);
        showEmpty("Search for a patient to see their record.", "Tip: the visiting number is shown when a patient is registered,"
                + " and in the first column of the Patients screen.");
    }

    @Override
    public void refresh() {
        if (current != null) {
            show(current.visitingNumber());
        }
    }

    /** Opens the record for a visiting number (also used for screenshots). */
    public void show(String visitingNumber) {
        number.setText(visitingNumber);
        lookup();
    }

    private void lookup() {
        String q = number.getText().trim();
        if (q.isEmpty()) {
            return;
        }
        try {
            Optional<Patient> p = services.patients.findByVisitingNumber(q);
            if (p.isEmpty()) {
                current = null;
                showEmpty("No patient found with visiting number " + q + ".", "Check the number and try again.");
                return;
            }
            current = p.get();
            render(current, services.patients.findKin(current.id()).orElse(null), services.appointments.forPatient(current.id()));
        } catch (DataException e) {
            UI.error(this, e.getMessage());
        }
    }

    private void showEmpty(String headline, String detail) {
        result.removeAll();
        Card c = new Card(new MigLayout("insets 40, fill, wrap", "[center,grow]", "push[]12[]6[]push"));
        c.add(new JLabel(Icons.get("records", 48, Theme.muted())));
        JLabel h = new JLabel(headline);
        h.putClientProperty(FlatClientProperties.STYLE, "font: bold +3");
        c.add(h);
        c.add(UI.subtitle(detail));
        result.add(c);
        result.revalidate();
        result.repaint();
    }

    private void render(Patient p, NextOfKin kin, List<Appointment> visits) {
        result.removeAll();
        JPanel grid = new JPanel(new MigLayout("insets 0, fill, gap 16", "[340!,fill][grow,fill]", "[grow,fill]"));
        grid.setOpaque(false);

        Card profile = new Card(new MigLayout("insets 0, fillx, wrap 2", "[]12[grow]", ""));
        JLabel name = new JLabel(p.fullName());
        name.putClientProperty(FlatClientProperties.STYLE, "font: bold +6");
        profile.add(name, "span 2");
        JLabel badge = new JLabel("#" + p.visitingNumber());
        badge.setForeground(Theme.ACCENT);
        badge.putClientProperty(FlatClientProperties.STYLE, "font: bold");
        profile.add(badge, "span 2, gapbottom 12");
        row(profile, "Age", p.age() + " (born " + p.dob().format(Validator.UK_DATE) + ")");
        row(profile, "Gender", p.gender());
        row(profile, "Phone", p.phone());
        row(profile, "Email", p.email() == null ? "—" : p.email());
        row(profile, "Address", "<html>" + p.address() + "</html>");
        row(profile, "Condition", p.conditionName() == null ? "—" : p.conditionName());
        row(profile, "Disability", p.disability() ? (p.disabilityNote() == null ? "Yes" : p.disabilityNote()) : "No");
        profile.add(UI.section("Next of kin"), "span 2, gaptop 14");
        if (kin == null) {
            profile.add(UI.subtitle("Not recorded"), "span 2");
        } else {
            row(profile, "Name", kin.fullName() + " (" + kin.relationship() + ")");
            row(profile, "Phone", kin.phone());
        }
        JLabel notice = new JLabel("<html>" + Validator.ageNotice(p.age()) + "</html>");
        notice.putClientProperty(FlatClientProperties.STYLE, "font: -1");
        notice.setForeground(Theme.ACCENT);
        profile.add(notice, "span 2, gaptop 14, wmax 300");
        grid.add(profile, "aligny top, growy 0");

        double spent = visits.stream().filter(a -> a.status() == Status.COMPLETED).mapToDouble(Appointment::cost).sum();
        long done = visits.stream().filter(a -> a.status() == Status.COMPLETED).count();
        long upcoming = visits.stream().filter(a -> a.status() == Status.SCHEDULED).count();
        Card hist = new Card(new MigLayout("insets 0, fill", "[grow,fill][]", "[]4[]12[grow,fill]"));
        hist.add(UI.section("Appointment history"));
        JButton book = UI.button("Book appointment", "plus");
        book.addActionListener(e -> {
            AppointmentDialog d = new AppointmentDialog(SwingUtilities.getWindowAncestor(this), services, null, p);
            d.setVisible(true);
            if (d.saved()) {
                refresh();
            }
        });
        hist.add(book, "wrap");
        hist.add(UI.subtitle(done + " completed visits · " + upcoming + " upcoming · "
                + String.format(Locale.UK, "£%,.2f", spent) + " billed"), "span 2, wrap");
        history.setRows(visits);
        JTable t = Tables.create(history);
        t.setAutoCreateRowSorter(false);
        Tables.widths(t, 105, 65, 150, 150, 105, 75);
        if (visits.isEmpty()) {
            JLabel none = UI.subtitle("No appointments yet.");
            none.setHorizontalAlignment(SwingConstants.CENTER);
            hist.add(none, "span 2");
        } else {
            hist.add(Tables.scroll(t), "span 2");
        }
        grid.add(hist);

        result.add(grid);
        result.revalidate();
        result.repaint();
    }

    private static void row(JPanel panel, String label, String value) {
        JLabel l = new JLabel(label);
        l.setForeground(Theme.muted());
        panel.add(l, "aligny top");
        panel.add(new JLabel(value == null ? "—" : value), "wmax 230");
    }
}
