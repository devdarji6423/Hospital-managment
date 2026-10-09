package com.hms.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.hms.Services;
import com.hms.model.User;
import com.hms.ui.panels.AppointmentsPage;
import com.hms.ui.panels.DashboardPage;
import com.hms.ui.panels.DoctorsPage;
import com.hms.ui.panels.Page;
import com.hms.ui.panels.PatientsPage;
import com.hms.ui.panels.RecordsPage;
import com.hms.ui.panels.SettingsPage;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import net.miginfocom.swing.MigLayout;

/** The single application window: navigation sidebar on the left, the selected page on the right. */
public class MainFrame extends JFrame {

    private final Services services;
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final Map<String, Page> pages = new LinkedHashMap<>();
    private final Map<String, JToggleButton> navButtons = new LinkedHashMap<>();
    private final ButtonGroup navGroup = new ButtonGroup();

    public MainFrame(Services services, User user) {
        super("Brunel Multi-Speciality Hospital");
        this.services = services;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        addPage("Dashboard", "dashboard", new DashboardPage(services, this::show));
        addPage("Patients", "patients", new PatientsPage(services));
        addPage("Appointments", "calendar", new AppointmentsPage(services));
        addPage("Doctors", "doctor", new DoctorsPage(services));
        addPage("Patient Records", "records", new RecordsPage(services));
        addPage("Settings", "settings", new SettingsPage(services, user));

        JPanel root = new JPanel(new MigLayout("insets 0, gap 0, fill", "[230!][grow]", "[grow]"));
        root.add(sidebar(user), "growy");
        root.add(content, "grow");
        setContentPane(root);

        setSize(new Dimension(1320, 820));
        setMinimumSize(new Dimension(1100, 680));
        setLocationRelativeTo(null);
        show("Dashboard");
    }

    /** Switches to the named page and reloads its data. */
    public void show(String name) {
        Page page = pages.get(name);
        page.refresh();
        cards.show(content, name);
        navButtons.get(name).setSelected(true);
    }

    private void addPage(String name, String icon, Page page) {
        pages.put(name, page);
        content.add(page, name);
        JToggleButton b = new JToggleButton(name, Icons.get(icon, 18));
        b.setHorizontalAlignment(SwingConstants.LEADING);
        b.setFocusable(false);
        b.setIconTextGap(12);
        b.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON);
        b.putClientProperty(FlatClientProperties.STYLE,
                "margin: 9,14,9,14; arc: 10; selectedBackground: fade($Component.accentColor,16%);"
                        + "selectedForeground: $Component.accentColor; font: +1");
        b.setSelectedIcon(Icons.get(icon, 18, Theme.ACCENT));
        b.addActionListener(e -> show(name));
        navGroup.add(b);
        navButtons.put(name, b);
    }

    private JPanel sidebar(User user) {
        JPanel side = new JPanel(new MigLayout("insets 20 14 16 14, wrap 1, fillx", "[grow, fill]", "[]24[]push[][]"));
        side.putClientProperty(FlatClientProperties.STYLE, "background: darken($Panel.background,3%)");
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, javax.swing.UIManager.getColor("Component.borderColor")));

        JLabel brand = new JLabel("Brunel HMS", Icons.get("logo", 26, Theme.ACCENT), SwingConstants.LEADING);
        brand.setIconTextGap(10);
        brand.putClientProperty(FlatClientProperties.STYLE, "font: bold +5");
        side.add(brand, "gapleft 8");

        JPanel nav = new JPanel(new MigLayout("insets 0, wrap 1, gap 0 4, fillx", "[grow, fill]"));
        nav.setOpaque(false);
        navButtons.values().forEach(nav::add);
        side.add(nav);

        JLabel who = new JLabel("<html><b>" + escape(user.fullName()) + "</b><br><span style='color:gray'>"
                + escape(user.role()) + "</span></html>", Icons.get("user", 20), SwingConstants.LEADING);
        who.setIconTextGap(10);
        side.add(who, "gapleft 8, gapbottom 8");

        JButton theme = new JButton(Theme.isDark() ? "Light mode" : "Dark mode",
                Icons.get(Theme.isDark() ? "sun" : "moon", 16));
        UI.flat(theme);
        theme.setFocusable(false);
        theme.setHorizontalAlignment(SwingConstants.LEADING);
        theme.addActionListener(e -> {
            boolean dark = !Theme.isDark();
            Theme.setDark(dark);
            theme.setText(dark ? "Light mode" : "Dark mode");
            theme.setIcon(Icons.get(dark ? "sun" : "moon", 16));
        });
        JButton signOut = new JButton("Sign out", Icons.get("logout", 16, Theme.RED));
        UI.flat(signOut);
        signOut.setFocusable(false);
        signOut.setForeground(Theme.RED);
        signOut.addActionListener(e -> {
            dispose();
            new LoginFrame(services).setVisible(true);
        });
        side.add(theme, "split 2, growx");
        side.add(signOut, "growx");
        return side;
    }

    static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /** For screenshots. */
    public Page page(String name) {
        return pages.get(name);
    }
}
