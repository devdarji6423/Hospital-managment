package com.hms.dao;

import com.hms.db.Database;
import com.hms.model.Doctor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoctorDao {

    private final Database db;

    public DoctorDao(Database db) {
        this.db = db;
    }

    public List<Doctor> findAll() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT id, name, specialty, phone, email, consultation_fee FROM doctors ORDER BY name";
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Doctor(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getDouble(6)));
            }
            return list;
        } catch (SQLException e) {
            throw new DataException("Could not load doctors", e);
        }
    }

    public Doctor save(Doctor d) {
        boolean insert = d.id() == 0;
        String sql = insert
                ? "INSERT INTO doctors(name, specialty, phone, email, consultation_fee) VALUES (?,?,?,?,?)"
                : "UPDATE doctors SET name=?, specialty=?, phone=?, email=?, consultation_fee=? WHERE id=?";
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, d.name());
            ps.setString(2, d.specialty());
            ps.setString(3, d.phone());
            ps.setString(4, d.email());
            ps.setDouble(5, d.consultationFee());
            if (!insert) {
                ps.setLong(6, d.id());
            }
            ps.executeUpdate();
            if (insert) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    return new Doctor(keys.getLong(1), d.name(), d.specialty(), d.phone(), d.email(), d.consultationFee());
                }
            }
            return d;
        } catch (SQLException e) {
            throw new DataException("Could not save doctor", e);
        }
    }

    public void delete(long id) {
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement("DELETE FROM doctors WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("FOREIGN KEY")) {
                throw new DataException("This doctor has appointments, so they can't be deleted. Cancel or reassign them first.");
            }
            throw new DataException("Could not delete doctor", e);
        }
    }
}
