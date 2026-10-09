package com.hms.ui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.hms.dao.DataException;
import com.hms.dao.UserDao;
import com.hms.ui.Theme;
import com.hms.ui.UI;
import com.hms.util.Validator;
import java.awt.Window;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

/** Create a staff account (this screen was missing from the original project). */
public class RegisterDialog extends JDialog {

    private final UserDao users;
    private final JTextField fullName = new JTextField();
    private final JTextField username = new JTextField();
    private final JComboBox<String> role = new JComboBox<>(new String[] {"Receptionist", "Nurse", "Doctor", "Administrator"});
    private final JPasswordField password = new JPasswordField();
    private final JPasswordField confirm = new JPasswordField();
    private final JLabel error = new JLabel(" ");

    public RegisterDialog(Window owner, UserDao users) {
        super(owner, "Create an account", ModalityType.APPLICATION_MODAL);
        this.users = users;
        JPanel p = new JPanel(new MigLayout("insets 24, wrap 1, fillx", "[grow, fill, 340]", "[]2[]18[][]10[][]10[][]10[][]10[][]8[]16[]"));
        JLabel t = new JLabel("Create a staff account");
        t.putClientProperty(FlatClientProperties.STYLE, "font: bold +6");
        p.add(t);
        p.add(UI.subtitle("Passwords are stored as salted PBKDF2 hashes."));
        p.add(new JLabel("Full name"));
        p.add(fullName);
        p.add(new JLabel("Username"));
        p.add(username);
        p.add(new JLabel("Role"));
        p.add(role);
        p.add(new JLabel("Password (at least 6 characters)"));
        p.add(password);
        p.add(new JLabel("Confirm password"));
        p.add(confirm);
        error.setForeground(Theme.RED);
        p.add(error);
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());
        JButton create = UI.primary("Create account", null);
        create.addActionListener(e -> create());
        p.add(cancel, "split 2, growx 0, gapleft push");
        p.add(create, "growx 0");
        setContentPane(p);
        getRootPane().setDefaultButton(create);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void create() {
        char[] pass = password.getPassword();
        char[] again = confirm.getPassword();
        try {
            String problem = Validator.name("Full name", fullName.getText());
            if (problem == null && !username.getText().trim().matches("[A-Za-z0-9._-]{3,30}")) {
                problem = "Username must be 3 to 30 letters, numbers, dots, dashes or underscores";
            }
            if (problem == null && pass.length < 6) {
                problem = "Password must be at least 6 characters";
            }
            if (problem == null && !Arrays.equals(pass, again)) {
                problem = "Passwords do not match";
            }
            if (problem != null) {
                error.setText(problem);
                return;
            }
            users.create(username.getText().trim(), fullName.getText().trim(), (String) role.getSelectedItem(), pass);
            UI.info(this, "Account created", "Account created. You can now sign in as " + username.getText().trim() + ".");
            dispose();
        } catch (DataException e) {
            error.setText(e.getMessage());
        } finally {
            Arrays.fill(pass, '\0');
            Arrays.fill(again, '\0');
        }
    }
}
