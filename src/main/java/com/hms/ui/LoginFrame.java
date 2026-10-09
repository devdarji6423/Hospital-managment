package com.hms.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.hms.Services;
import com.hms.dao.DataException;
import com.hms.model.User;
import com.hms.ui.dialogs.RegisterDialog;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.Optional;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import net.miginfocom.swing.MigLayout;

/** Sign-in window: branded panel on the left, form on the right. */
public class LoginFrame extends JFrame {

    private final Services services;
    private final JTextField username = new JTextField();
    private final JPasswordField password = new JPasswordField();
    private final JLabel message = new JLabel(" ");

    public LoginFrame(Services services) {
        super("Brunel Multi-Speciality Hospital · Sign in");
        this.services = services;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(new JPanel(new MigLayout("insets 0, fill", "[45%!][grow]", "[grow]")));
        add(brandPanel(), "grow");
        add(formPanel(), "grow");
        setSize(new Dimension(960, 600));
        setMinimumSize(new Dimension(820, 540));
        setLocationRelativeTo(null);
    }

    private JPanel brandPanel() {
        JPanel p = new JPanel(new MigLayout("insets 48, fill", "[grow]", "push[]8[]24[]push[]")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(0x0F766E), getWidth(), getHeight(), new Color(0x0E7490)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillOval(getWidth() - 220, -80, 320, 320);
                g2.fillOval(-120, getHeight() - 200, 300, 300);
                g2.dispose();
            }
        };
        JLabel logo = new JLabel(Icons.get("logo", 44, Color.WHITE));
        JLabel name = new JLabel("<html>Brunel Multi-Speciality<br>Hospital</html>");
        name.setForeground(Color.WHITE);
        name.putClientProperty(FlatClientProperties.STYLE, "font: bold +14");
        JLabel tag = new JLabel("<html>Patient registration, appointments and records in one place.</html>");
        tag.setForeground(new Color(255, 255, 255, 210));
        tag.putClientProperty(FlatClientProperties.STYLE, "font: +2");
        JLabel footer = new JLabel("Hospital Management System · v2.0");
        footer.setForeground(new Color(255, 255, 255, 160));
        p.add(logo, "wrap");
        p.add(name, "wrap");
        p.add(tag, "growx, wmax 340, wrap");
        p.add(footer);
        return p;
    }

    private JPanel formPanel() {
        JPanel p = new JPanel(new MigLayout("insets 0 64 0 64, fillx, wrap", "[grow, fill]", "push[]4[]28[]6[]14[]6[]6[]16[]12[]24[]push"));
        JLabel title = new JLabel("Welcome back");
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +12");
        JLabel sub = UI.subtitle("Sign in to continue to your workspace");

        UI.placeholder(username, "Username");
        username.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, Icons.get("user", 16));
        UI.placeholder(password, "Password");
        message.setForeground(Theme.RED);

        JButton signIn = UI.primary("Sign in", null);
        signIn.addActionListener(e -> signIn());
        getRootPane().setDefaultButton(signIn);

        JButton register = new JButton("New staff member? Create an account");
        register.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        register.setForeground(Theme.ACCENT);
        register.addActionListener(e -> new RegisterDialog(this, services.users).setVisible(true));

        JLabel hint = UI.subtitle("Demo login:  admin  /  admin123");
        hint.putClientProperty(FlatClientProperties.STYLE, "font: -1");

        p.add(title);
        p.add(sub);
        p.add(new JLabel("Username"));
        p.add(username, "h 38!");
        p.add(new JLabel("Password"));
        p.add(password, "h 38!");
        p.add(message);
        p.add(signIn, "h 40!");
        p.add(register, "alignx center, growx 0");
        p.add(hint, "alignx center, growx 0");
        return p;
    }

    private void signIn() {
        String user = username.getText().trim();
        char[] pass = password.getPassword();
        try {
            if (user.isEmpty() || pass.length == 0) {
                showError("Enter your username and password");
                return;
            }
            Optional<User> result = services.users.authenticate(user, pass);
            if (result.isEmpty()) {
                showError("Incorrect username or password");
                password.setText("");
                password.requestFocusInWindow();
                return;
            }
            dispose();
            new MainFrame(services, result.get()).setVisible(true);
        } catch (DataException e) {
            UI.error(this, e.getMessage());
        } finally {
            Arrays.fill(pass, '\0');
        }
    }

    private void showError(String text) {
        message.setText(text);
        UI.markError(username, username.getText().isBlank());
        UI.markError(password, true);
    }

    /** For screenshots and tests. */
    void fill(String user, String pass) {
        username.setText(user);
        password.setText(pass);
    }
}
