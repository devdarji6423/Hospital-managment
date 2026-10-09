package com.hms.ui.panels;

import com.hms.Services;
import com.hms.dao.DataException;
import com.hms.model.Appointment;
import com.hms.model.DashboardStats;
import com.hms.ui.Theme;
import com.hms.ui.UI;
import com.hms.ui.components.BarChart;
import com.hms.ui.components.Card;
import com.hms.ui.components.StatCard;
import com.hms.ui.components.Tables;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JTable;
import net.miginfocom.swing.MigLayout;

public class DashboardPage extends Page {

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("EEE d MMM", Locale.UK);

    private final Services services;
    private final StatCard patients = new StatCard("Registered patients", "patients", Theme.ACCENT);
    private final StatCard today = new StatCard("Appointments today", "calendar", Theme.BLUE);
    private final StatCard doctors = new StatCard("Doctors on staff", "doctor", Theme.VIOLET);
    private final StatCard revenue = new StatCard("Revenue this month", "revenue", Theme.AMBER);
    private final BarChart chart = new BarChart();
    private final Tables.RowModel<Appointment> upcoming = new Tables.RowModel<>(
            new String[] {"Date", "Time", "Patient", "Doctor"},
            a -> a.date().format(DAY), a -> a.time().toString(), Appointment::patientName, Appointment::doctorName);

    public DashboardPage(Services services, Consumer<String> navigate) {
        super("[grow,fill][grow,fill][grow,fill][grow,fill]", "[]2[]20[]16[grow,fill]");
        this.services = services;

        add(UI.title("Dashboard"), "span 4, wrap");
        add(UI.subtitle(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.UK))), "span 4, wrap");
        add(patients);
        add(today);
        add(doctors);
        add(revenue, "wrap");

        Card chartCard = new Card(new MigLayout("insets 0, fill", "[grow,fill]", "[]12[grow,fill]"));
        chartCard.add(UI.section("Appointments, last 14 days"), "wrap");
        chartCard.add(chart);
        add(chartCard, "span 2");

        Card upcomingCard = new Card(new MigLayout("insets 0, fill", "[grow,fill][]", "[]10[grow,fill]"));
        upcomingCard.add(UI.section("Upcoming appointments"));
        JButton all = UI.button("View all", null);
        UI.flat(all);
        all.setForeground(Theme.ACCENT);
        all.addActionListener(e -> navigate.accept("Appointments"));
        upcomingCard.add(all, "wrap");
        JTable table = Tables.create(upcoming);
        table.setAutoCreateRowSorter(false);
        Tables.widths(table, 105, 60, 170, 150);
        upcomingCard.add(Tables.scroll(table), "span 2");
        add(upcomingCard, "span 2");
    }

    @Override
    public void refresh() {
        try {
            DashboardStats s = services.appointments.stats();
            patients.setValue(String.valueOf(s.patients()), "in the system");
            today.setValue(String.valueOf(s.appointmentsToday()), s.upcoming() + " scheduled from today");
            doctors.setValue(String.valueOf(s.doctors()), "across all departments");
            revenue.setValue(String.format(Locale.UK, "£%,.0f", s.revenueThisMonth()), "from completed visits");

            int days = 14;
            int[] counts = services.appointments.dailyCounts(days);
            String[] labels = new String[days];
            LocalDate start = LocalDate.now().minusDays(days - 1L);
            for (int i = 0; i < days; i++) {
                LocalDate d = start.plusDays(i);
                labels[i] = String.valueOf(d.getDayOfMonth());
            }
            chart.setData(counts, labels, days - 1);
            upcoming.setRows(services.appointments.upcoming(12));
        } catch (DataException e) {
            UI.error(this, e.getMessage());
        }
    }
}
