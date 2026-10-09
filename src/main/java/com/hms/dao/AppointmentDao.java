package com.hms.dao;

import com.hms.db.Database;
import com.hms.model.Appointment;
import com.hms.model.Appointment.Status;
import com.hms.model.DashboardStats;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDao {

    private static final String SELECT = """
            SELECT a.*, p.title || ' ' || p.first_name || ' ' || p.last_name AS patient_name,
                   p.visiting_number, d.name AS doctor_name
            FROM appointments a
            JOIN patients p ON p.id = a.patient_id
            JOIN doctors d ON d.id = a.doctor_id
            """;

    private final Database db;

    public AppointmentDao(Database db) {
        this.db = db;
    }

    /**
     * Lists appointments, newest first.
     * @param text   matches patient name, visiting number or doctor; empty for all
     * @param status null for every status
     */
    public List<Appointment> search(String text, Status status) {
        String q = text == null ? "" : text.trim();
        String sql = SELECT + """
                WHERE (? = '' OR patient_name LIKE ? OR p.visiting_number LIKE ? OR d.name LIKE ?)
                  AND (? IS NULL OR a.status = ?)
                ORDER BY a.date DESC, a.time DESC
                """;
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            String like = "%" + q + "%";
            ps.setString(1, q);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            String s = status == null ? null : status.toString();
            ps.setString(5, s);
            ps.setString(6, s);
            return list(ps);
        } catch (SQLException e) {
            throw new DataException("Could not load appointments", e);
        }
    }

    public List<Appointment> forPatient(long patientId) {
        try (Connection c = db.connect();
             PreparedStatement ps = c.prepareStatement(SELECT + " WHERE a.patient_id = ? ORDER BY a.date DESC, a.time DESC")) {
            ps.setLong(1, patientId);
            return list(ps);
        } catch (SQLException e) {
            throw new DataException("Could not load patient history", e);
        }
    }

    /** Scheduled appointments from today onwards, soonest first. */
    public List<Appointment> upcoming(int limit) {
        String sql = SELECT + " WHERE a.status = 'Scheduled' AND a.date >= ? ORDER BY a.date, a.time LIMIT ?";
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, LocalDate.now().toString());
            ps.setInt(2, limit);
            return list(ps);
        } catch (SQLException e) {
            throw new DataException("Could not load upcoming appointments", e);
        }
    }

    /** True when the doctor already has a scheduled appointment at that exact slot. */
    public boolean isDoctorBooked(long doctorId, LocalDate date, LocalTime time, long exceptAppointmentId) {
        String sql = "SELECT 1 FROM appointments WHERE doctor_id=? AND date=? AND time=? AND status='Scheduled' AND id<>?";
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, doctorId);
            ps.setString(2, date.toString());
            ps.setString(3, time.toString());
            ps.setLong(4, exceptAppointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataException("Could not check doctor availability", e);
        }
    }

    public long save(Appointment a) {
        boolean insert = a.id() == 0;
        String sql = insert
                ? "INSERT INTO appointments(patient_id, doctor_id, date, time, reason, status, cost, notes) VALUES (?,?,?,?,?,?,?,?)"
                : "UPDATE appointments SET patient_id=?, doctor_id=?, date=?, time=?, reason=?, status=?, cost=?, notes=? WHERE id=?";
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, a.patientId());
            ps.setLong(2, a.doctorId());
            ps.setString(3, a.date().toString());
            ps.setString(4, a.time().toString());
            ps.setString(5, a.reason());
            ps.setString(6, a.status().toString());
            ps.setDouble(7, a.cost());
            ps.setString(8, a.notes());
            if (!insert) {
                ps.setLong(9, a.id());
            }
            ps.executeUpdate();
            if (insert) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    return keys.getLong(1);
                }
            }
            return a.id();
        } catch (SQLException e) {
            throw new DataException("Could not save appointment", e);
        }
    }

    public void updateStatus(long id, Status status) {
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement("UPDATE appointments SET status=? WHERE id=?")) {
            ps.setString(1, status.toString());
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataException("Could not update appointment", e);
        }
    }

    public void delete(long id) {
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement("DELETE FROM appointments WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataException("Could not delete appointment", e);
        }
    }

    public DashboardStats stats() {
        LocalDate today = LocalDate.now();
        String t = today.toString();
        try (Connection c = db.connect()) {
            int patients = (int) scalar(c, "SELECT COUNT(*) FROM patients");
            int doctors = (int) scalar(c, "SELECT COUNT(*) FROM doctors");
            int todayCount = (int) scalar(c, "SELECT COUNT(*) FROM appointments WHERE status <> 'Cancelled' AND date = ?", t);
            int upcoming = (int) scalar(c, "SELECT COUNT(*) FROM appointments WHERE status = 'Scheduled' AND date >= ?", t);
            double revenue = scalar(c,
                    "SELECT COALESCE(SUM(cost), 0) FROM appointments WHERE status = 'Completed' AND date BETWEEN ? AND ?",
                    today.withDayOfMonth(1).toString(), today.withDayOfMonth(today.lengthOfMonth()).toString());
            return new DashboardStats(patients, doctors, todayCount, upcoming, revenue);
        } catch (SQLException e) {
            throw new DataException("Could not load dashboard figures", e);
        }
    }

    /** Appointment counts for each of the last {@code days} days (oldest first), for the dashboard chart. */
    public int[] dailyCounts(int days) {
        int[] counts = new int[days];
        LocalDate start = LocalDate.now().minusDays(days - 1L);
        String sql = "SELECT date, COUNT(*) FROM appointments WHERE status <> 'Cancelled' AND date BETWEEN ? AND ? GROUP BY date";
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, start.toString());
            ps.setString(2, LocalDate.now().toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idx = (int) (LocalDate.parse(rs.getString(1)).toEpochDay() - start.toEpochDay());
                    if (idx >= 0 && idx < days) {
                        counts[idx] = rs.getInt(2);
                    }
                }
            }
            return counts;
        } catch (SQLException e) {
            throw new DataException("Could not load appointment trend", e);
        }
    }

    private static double scalar(Connection c, String sql, String... params) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }
        }
    }

    private static List<Appointment> list(PreparedStatement ps) throws SQLException {
        List<Appointment> out = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Appointment(
                        rs.getLong("id"),
                        rs.getLong("patient_id"),
                        rs.getString("patient_name"),
                        rs.getString("visiting_number"),
                        rs.getLong("doctor_id"),
                        rs.getString("doctor_name"),
                        LocalDate.parse(rs.getString("date")),
                        LocalTime.parse(rs.getString("time")),
                        rs.getString("reason"),
                        Status.fromLabel(rs.getString("status")),
                        rs.getDouble("cost"),
                        rs.getString("notes")));
            }
        }
        return out;
    }
}
