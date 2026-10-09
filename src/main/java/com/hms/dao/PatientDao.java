package com.hms.dao;

import com.hms.db.Database;
import com.hms.model.NextOfKin;
import com.hms.model.Patient;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientDao {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String SELECT = """
            SELECT p.*, c.name AS condition_name
            FROM patients p LEFT JOIN conditions c ON c.id = p.condition_id
            """;

    private final Database db;

    public PatientDao(Database db) {
        this.db = db;
    }

    /** Finds patients whose name, visiting number, phone or postcode contains the text. Empty text returns all. */
    public List<Patient> search(String text) {
        String q = text == null ? "" : text.trim();
        String sql = SELECT + """
                WHERE ? = '' OR (p.first_name || ' ' || p.last_name) LIKE ? OR p.visiting_number LIKE ?
                   OR p.phone LIKE ? OR p.postal_code LIKE ? OR c.name LIKE ?
                ORDER BY p.created_at DESC, p.id DESC
                """;
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            String like = "%" + q + "%";
            ps.setString(1, q);
            for (int i = 2; i <= 6; i++) {
                ps.setString(i, like);
            }
            List<Patient> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DataException("Could not load patients", e);
        }
    }

    public Optional<Patient> findByVisitingNumber(String visitingNumber) {
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(SELECT + " WHERE p.visiting_number = ?")) {
            ps.setString(1, visitingNumber.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataException("Could not look up patient", e);
        }
    }

    /** Inserts or updates the patient and their next of kin (kin may be null) in one transaction. */
    public Patient save(Patient p, NextOfKin kin) {
        try (Connection c = db.connect()) {
            c.setAutoCommit(false);
            try {
                Patient saved = p.id() == 0 ? insert(c, p) : update(c, p);
                saveKin(c, saved.id(), kin);
                c.commit();
                return saved;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataException("Could not save patient", e);
        }
    }

    public void delete(long id) {
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement("DELETE FROM patients WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataException("Could not delete patient", e);
        }
    }

    public Optional<NextOfKin> findKin(long patientId) {
        String sql = "SELECT id, patient_id, full_name, relationship, phone, email FROM next_of_kin WHERE patient_id = ?";
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(new NextOfKin(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getString(4),
                                rs.getString(5), rs.getString(6)))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataException("Could not load next of kin", e);
        }
    }

    private Patient insert(Connection c, Patient p) throws SQLException {
        String visitingNumber = newVisitingNumber(c);
        String sql = """
                INSERT INTO patients(visiting_number, title, first_name, last_name, dob, gender, email, phone,
                    house_number, street, city, postal_code, condition_id, disability, disability_note)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                """;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, visitingNumber);
            bind(ps, p, 2);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return p.withId(keys.getLong(1), visitingNumber);
            }
        }
    }

    private Patient update(Connection c, Patient p) throws SQLException {
        String sql = """
                UPDATE patients SET title=?, first_name=?, last_name=?, dob=?, gender=?, email=?, phone=?,
                    house_number=?, street=?, city=?, postal_code=?, condition_id=?, disability=?, disability_note=?
                WHERE id=?
                """;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            int next = bind(ps, p, 1);
            ps.setLong(next, p.id());
            ps.executeUpdate();
            return p;
        }
    }

    private static int bind(PreparedStatement ps, Patient p, int i) throws SQLException {
        ps.setString(i++, p.title());
        ps.setString(i++, p.firstName());
        ps.setString(i++, p.lastName());
        ps.setString(i++, p.dob().toString());
        ps.setString(i++, p.gender());
        ps.setString(i++, p.email());
        ps.setString(i++, p.phone());
        ps.setString(i++, p.houseNumber());
        ps.setString(i++, p.street());
        ps.setString(i++, p.city());
        ps.setString(i++, p.postalCode());
        if (p.conditionId() == null) {
            ps.setNull(i++, Types.INTEGER);
        } else {
            ps.setLong(i++, p.conditionId());
        }
        ps.setInt(i++, p.disability() ? 1 : 0);
        ps.setString(i++, p.disabilityNote());
        return i;
    }

    private static void saveKin(Connection c, long patientId, NextOfKin kin) throws SQLException {
        try (PreparedStatement del = c.prepareStatement("DELETE FROM next_of_kin WHERE patient_id = ?")) {
            del.setLong(1, patientId);
            del.executeUpdate();
        }
        if (kin == null) {
            return;
        }
        String sql = "INSERT INTO next_of_kin(patient_id, full_name, relationship, phone, email) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.setString(2, kin.fullName());
            ps.setString(3, kin.relationship());
            ps.setString(4, kin.phone());
            ps.setString(5, kin.email());
            ps.executeUpdate();
        }
    }

    /** A random 8-digit number that no other patient has. */
    private static String newVisitingNumber(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM patients WHERE visiting_number = ?")) {
            while (true) {
                String candidate = String.valueOf(10_000_000 + RANDOM.nextInt(90_000_000));
                ps.setString(1, candidate);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return candidate;
                    }
                }
            }
        }
    }

    private static Patient map(ResultSet rs) throws SQLException {
        long conditionId = rs.getLong("condition_id");
        Long cond = rs.wasNull() ? null : conditionId;
        return new Patient(
                rs.getLong("id"),
                rs.getString("visiting_number"),
                rs.getString("title"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                LocalDate.parse(rs.getString("dob")),
                rs.getString("gender"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("house_number"),
                rs.getString("street"),
                rs.getString("city"),
                rs.getString("postal_code"),
                cond,
                rs.getString("condition_name"),
                rs.getInt("disability") == 1,
                rs.getString("disability_note"));
    }
}
