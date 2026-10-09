package com.hms.dao;

import com.hms.db.Database;
import com.hms.model.Condition;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConditionDao {

    private final Database db;

    public ConditionDao(Database db) {
        this.db = db;
    }

    public List<Condition> findAll() {
        List<Condition> list = new ArrayList<>();
        try (Connection c = db.connect();
             PreparedStatement ps = c.prepareStatement("SELECT id, name FROM conditions ORDER BY name COLLATE NOCASE");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Condition(rs.getLong(1), rs.getString(2)));
            }
            return list;
        } catch (SQLException e) {
            throw new DataException("Could not load medical conditions", e);
        }
    }

    /** Adds a condition; duplicates (ignoring case) are rejected. */
    public Condition add(String name) {
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new DataException("Condition name is required");
        }
        try (Connection c = db.connect()) {
            try (PreparedStatement check = c.prepareStatement("SELECT 1 FROM conditions WHERE name = ?")) {
                check.setString(1, trimmed);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        throw new DataException("\"" + trimmed + "\" is already in the list");
                    }
                }
            }
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO conditions(name) VALUES (?)")) {
                ps.setString(1, trimmed);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    return new Condition(keys.getLong(1), trimmed);
                }
            }
        } catch (SQLException e) {
            throw new DataException("Could not add condition", e);
        }
    }

    public void delete(long id) {
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement("DELETE FROM conditions WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataException("Could not delete condition", e);
        }
    }
}
