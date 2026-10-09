package com.hms.ui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.hms.Services;
import com.hms.dao.DataException;
import com.hms.model.Condition;
import com.hms.model.NextOfKin;
import com.hms.model.Patient;
import com.hms.ui.Icons;
import com.hms.ui.Theme;
import com.hms.ui.UI;
import com.hms.util.Validator;
import java.awt.Window;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

/** Register a new patient or edit an existing one, including their next of kin. */
public class PatientDialog extends JDialog {

    private final Services services;
    private final Patient existing;
    private boolean saved;

    private final JComboBox<String> title = new JComboBox<>(new String[] {"Mr", "Mrs", "Ms", "Miss", "Master", "Dr", "Mx"});
    private final JTextField first = new JTextField();
    private final JTextField last = new JTextField();
    private final JTextField dob = UI.placeholder(new JTextField(), "dd/mm/yyyy");
    private final JComboBox<String> gender = new JComboBox<>(new String[] {"Female", "Male", "Other", "Prefer not to say"});
    private final JTextField email = UI.placeholder(new JTextField(), "name@example.com");
    private final JTextField phone = UI.placeholder(new JTextField(), "07700 900123");
    private final JTextField house = new JTextField();
    private final JTextField street = new JTextField();
    private final JTextField city = new JTextField();
    private final JTextField postcode = UI.placeholder(new JTextField(), "UB8 3PH");
    private final JComboBox<Condition> condition = new JComboBox<>();
    private final JCheckBox disability = new JCheckBox("Patient has a disability");
    private final JTextField disabilityNote = UI.placeholder(new JTextField(), "Details (e.g. wheelchair user)");
    private final JTextField kinName = new JTextField();
    private final JComboBox<String> kinRelation = new JComboBox<>(
            new String[] {"Spouse", "Partner", "Mother", "Father", "Son", "Daughter", "Brother", "Sister", "Guardian", "Friend", "Other"});
    private final JTextField kinPhone = new JTextField();
    private final JTextField kinEmail = new JTextField();
    private final JLabel error = new JLabel(" ");

    public PatientDialog(Window owner, Services services, Patient existing) {
        super(owner, existing == null ? "Register patient" : "Edit patient", ModalityType.APPLICATION_MODAL);
        this.services = services;
        this.existing = existing;

        JPanel form = new JPanel(new MigLayout("insets 24 28 8 28, fillx, wrap 4, hidemode 3",
                "[right]10[grow,fill,240]28[right]10[grow,fill,240]", ""));
        JLabel heading = new JLabel(existing == null ? "Register a new patient" : existing.fullName());
        heading.putClientProperty(FlatClientProperties.STYLE, "font: bold +6");
        form.add(heading, "span 4, alignx left");
        form.add(UI.subtitle(existing == null
                ? "A unique 8-digit visiting number is generated when you save."
                : "Visiting number #" + existing.visitingNumber()), "span 4, alignx left, gapbottom 8");

        section(form, "Personal details");
        form.add(new JLabel("Title"));
        form.add(title);
        form.add(new JLabel("Date of birth"));
        form.add(dob);
        form.add(new JLabel("First name"));
        form.add(first);
        form.add(new JLabel("Last name"));
        form.add(last);
        form.add(new JLabel("Gender"));
        form.add(gender, "wrap");

        section(form, "Contact and address");
        form.add(new JLabel("Phone"));
        form.add(phone);
        form.add(new JLabel("Email"));
        form.add(email);
        form.add(new JLabel("House no."));
        form.add(house);
        form.add(new JLabel("Street"));
        form.add(street);
        form.add(new JLabel("City / town"));
        form.add(city);
        form.add(new JLabel("Postcode"));
        form.add(postcode);

        section(form, "Medical");
        JButton newCondition = new JButton(Icons.get("plus", 14));
        newCondition.setToolTipText("Add a condition that isn't in the list");
        newCondition.addActionListener(e -> addCondition());
        form.add(new JLabel("Condition"));
        form.add(condition, "split 2, growx");
        form.add(newCondition, "growx 0");
        form.add(disability, "skip 1, wrap");
        form.add(disabilityNote, "skip 3");
        disability.addActionListener(e -> {
            disabilityNote.setEnabled(disability.isSelected());
            if (!disability.isSelected()) {
                disabilityNote.setText("");
            }
        });

        section(form, "Next of kin");
        form.add(new JLabel("Full name"));
        form.add(kinName);
        form.add(new JLabel("Relationship"));
        form.add(kinRelation);
        form.add(new JLabel("Phone"));
        form.add(kinPhone);
        form.add(new JLabel("Email"));
        form.add(kinEmail);

        error.setForeground(Theme.RED);
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());
        JButton save = UI.primary(existing == null ? "Register patient" : "Save changes", "check");
        save.addActionListener(e -> save());
        JPanel footer = new JPanel(new MigLayout("insets 12 28 20 28, fillx", "[grow][][]"));
        footer.add(error, "growx");
        footer.add(cancel);
        footer.add(save);

        JPanel root = new JPanel(new MigLayout("insets 0, fill, gap 0", "[grow,fill]", "[grow,fill][]"));
        JScrollPane sp = new JScrollPane(form);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        root.add(sp, "wrap");
        root.add(new JSeparator(), "wrap");
        root.add(footer);
        setContentPane(root);
        getRootPane().setDefaultButton(save);

        loadConditions(existing == null ? null : existing.conditionId());
        if (existing != null) {
            fill(existing);
        } else {
            disabilityNote.setEnabled(false);
        }
        pack();
        setLocationRelativeTo(owner);
    }

    public boolean saved() {
        return saved;
    }

    private static void section(JPanel form, String text) {
        form.add(UI.section(text), "span 4, split 2, alignx left, gaptop 14");
        form.add(new JSeparator(), "growx, gapleft 8, wrap 8");
    }

    private void loadConditions(Long selectId) {
        condition.removeAllItems();
        condition.addItem(new Condition(0, "— None —"));
        for (Condition c : services.conditions.findAll()) {
            condition.addItem(c);
            if (selectId != null && c.id() == selectId) {
                condition.setSelectedItem(c);
            }
        }
    }

    private void addCondition() {
        String name = JOptionPane.showInputDialog(this, "Name of the medical condition:", "Add condition",
                JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.isBlank()) {
            return;
        }
        try {
            Condition c = services.conditions.add(name);
            loadConditions(c.id());
        } catch (DataException e) {
            UI.error(this, e.getMessage());
        }
    }

    private void fill(Patient p) {
        title.setSelectedItem(p.title());
        first.setText(p.firstName());
        last.setText(p.lastName());
        dob.setText(p.dob().format(Validator.UK_DATE));
        gender.setSelectedItem(p.gender());
        email.setText(p.email());
        phone.setText(p.phone());
        house.setText(p.houseNumber());
        street.setText(p.street());
        city.setText(p.city());
        postcode.setText(p.postalCode());
        disability.setSelected(p.disability());
        disabilityNote.setText(p.disabilityNote());
        disabilityNote.setEnabled(p.disability());
        services.patients.findKin(p.id()).ifPresent(k -> {
            kinName.setText(k.fullName());
            kinRelation.setSelectedItem(k.relationship());
            kinPhone.setText(k.phone());
            kinEmail.setText(k.email());
        });
    }

    private void save() {
        Map<JComponent, String> problems = new LinkedHashMap<>();
        check(problems, first, Validator.name("First name", first.getText()));
        check(problems, last, Validator.name("Last name", last.getText()));
        check(problems, dob, Validator.dateOfBirth(dob.getText()));
        check(problems, phone, Validator.phone(phone.getText()));
        check(problems, email, Validator.email(email.getText()));
        check(problems, street, Validator.required("Street", street.getText()));
        check(problems, city, Validator.required("City or town", city.getText()));
        check(problems, postcode, Validator.postcode(postcode.getText()));
        boolean hasKin = !kinName.getText().isBlank() || !kinPhone.getText().isBlank();
        if (hasKin) {
            check(problems, kinName, Validator.name("Next of kin name", kinName.getText()));
            check(problems, kinPhone, Validator.phone(kinPhone.getText()));
            check(problems, kinEmail, Validator.email(kinEmail.getText()));
        } else {
            check(problems, kinName, null);
            check(problems, kinPhone, null);
            check(problems, kinEmail, null);
        }
        if (!problems.isEmpty()) {
            List<String> messages = new ArrayList<>(problems.values());
            error.setText(messages.get(0) + (messages.size() > 1 ? "  (+" + (messages.size() - 1) + " more)" : ""));
            problems.keySet().iterator().next().requestFocusInWindow();
            return;
        }

        Condition cond = (Condition) condition.getSelectedItem();
        Patient p = new Patient(
                existing == null ? 0 : existing.id(),
                existing == null ? null : existing.visitingNumber(),
                (String) title.getSelectedItem(),
                first.getText().trim(),
                last.getText().trim(),
                Validator.parseUkDate(dob.getText()),
                (String) gender.getSelectedItem(),
                blankToNull(email.getText()),
                phone.getText().trim(),
                house.getText().trim(),
                street.getText().trim(),
                city.getText().trim(),
                Validator.normalisePostcode(postcode.getText()),
                cond == null || cond.id() == 0 ? null : cond.id(),
                cond == null || cond.id() == 0 ? null : cond.name(),
                disability.isSelected(),
                disability.isSelected() ? blankToNull(disabilityNote.getText()) : null);
        NextOfKin kin = hasKin
                ? new NextOfKin(0, p.id(), kinName.getText().trim(), (String) kinRelation.getSelectedItem(),
                        kinPhone.getText().trim(), blankToNull(kinEmail.getText()))
                : null;
        try {
            Patient stored = services.patients.save(p, kin);
            saved = true;
            dispose();
            if (existing == null) {
                UI.info(getOwner(), "Patient registered",
                        stored.fullName() + " has been registered.\n\nVisiting number:  " + stored.visitingNumber()
                                + "\n\n" + Validator.ageNotice(stored.age()));
            }
        } catch (DataException e) {
            UI.error(this, e.getMessage());
        }
    }

    private static void check(Map<JComponent, String> problems, JComponent field, String message) {
        UI.markError(field, message != null);
        if (message != null) {
            problems.put(field, message);
        }
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
