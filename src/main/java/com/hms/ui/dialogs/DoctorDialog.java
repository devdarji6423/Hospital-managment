package com.hms.ui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.hms.Services;
import com.hms.dao.DataException;
import com.hms.model.Doctor;
import com.hms.ui.Theme;
import com.hms.ui.UI;
import com.hms.util.Validator;
import java.awt.Window;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class DoctorDialog extends JDialog {

    private static final String[] SPECIALTIES = {"General Practice", "Cardiology", "Dermatology", "Neurology",
            "Obstetrics", "Oncology", "Orthopaedics", "Paediatrics", "Psychiatry", "Radiology"};

    private final Services services;
    private final Doctor existing;
    private boolean saved;
    private final JTextField name = UI.placeholder(new JTextField(), "Dr. Jane Smith");
    private final JComboBox<String> specialty = new JComboBox<>(SPECIALTIES);
    private final JTextField phone = new JTextField();
    private final JTextField email = new JTextField();
    private final JTextField fee = new JTextField("50.00");
    private final JLabel error = new JLabel(" ");

    public DoctorDialog(Window owner, Services services, Doctor existing) {
        super(owner, existing == null ? "Add doctor" : "Edit doctor", ModalityType.APPLICATION_MODAL);
        this.services = services;
        this.existing = existing;
        specialty.setEditable(true);

        JPanel p = new JPanel(new MigLayout("insets 24 28 20 28, fillx, wrap 2", "[right]12[grow,fill,300]", ""));
        JLabel heading = new JLabel(existing == null ? "Add a doctor" : "Edit doctor");
        heading.putClientProperty(FlatClientProperties.STYLE, "font: bold +6");
        p.add(heading, "span 2, alignx left, gapbottom 12");
        p.add(new JLabel("Full name"));
        p.add(name);
        p.add(new JLabel("Specialty"));
        p.add(specialty);
        p.add(new JLabel("Phone"));
        p.add(phone);
        p.add(new JLabel("Email"));
        p.add(email);
        p.add(new JLabel("Fee (£)"));
        p.add(fee);
        error.setForeground(Theme.RED);
        p.add(error, "span 2, gaptop 6");
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());
        JButton save = UI.primary("Save", "check");
        save.addActionListener(e -> save());
        p.add(cancel, "span 2, split 2, growx 0, gapleft push");
        p.add(save, "growx 0");
        setContentPane(p);
        getRootPane().setDefaultButton(save);

        if (existing != null) {
            name.setText(existing.name());
            specialty.setSelectedItem(existing.specialty());
            phone.setText(existing.phone());
            email.setText(existing.email());
            fee.setText(String.format(Locale.UK, "%.2f", existing.consultationFee()));
        }
        pack();
        setLocationRelativeTo(owner);
    }

    public boolean saved() {
        return saved;
    }

    private void save() {
        String spec = String.valueOf(specialty.getSelectedItem()).trim();
        String problem = Validator.required("Name", name.getText());
        if (problem == null && spec.isEmpty()) {
            problem = "Specialty is required";
        }
        if (problem == null && !phone.getText().isBlank()) {
            problem = Validator.phone(phone.getText());
        }
        if (problem == null) {
            problem = Validator.email(email.getText());
        }
        if (problem == null) {
            problem = Validator.money("Fee", fee.getText());
        }
        if (problem != null) {
            error.setText(problem);
            return;
        }
        try {
            services.doctors.save(new Doctor(existing == null ? 0 : existing.id(), name.getText().trim(), spec,
                    phone.getText().trim(), email.getText().trim(), Double.parseDouble(fee.getText().trim())));
            saved = true;
            dispose();
        } catch (DataException e) {
            UI.error(this, e.getMessage());
        }
    }
}
