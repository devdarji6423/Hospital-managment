package com.hms.dao;

import com.hms.db.Database;
import com.hms.model.User;
import com.hms.util.PasswordHasher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDao {

    private final Database db;

    public UserDao(Database db) {
        this.db = db;
    }

    /** Returns the user when the username and password match. */
    public Optional<User> authenticate(String username, char[] password) {
        String sql = "SELECT id, username, full_name, role, password_hash, salt FROM users WHERE username = ?";
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordHasher.matches(password, rs.getString("salt"), rs.getString("password_hash"))) {
                    return Optional.of(new User(rs.getLong("id"), rs.getString("username"),
                            rs.getString("full_name"), rs.getString("role")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataException("Could not check login", e);
        }
    }

    public boolean exists(String username) {
        try (Connection c = db.connect();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM users WHERE username = ?")) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataException("Could not look up user", e);
        }
    }

    public User create(String username, String fullName, String role, char[] password) {
        if (exists(username)) {
            throw new DataException("The username \"" + username + "\" is already taken");
        }
        String salt = PasswordHasher.newSalt();
        String sql = "INSERT INTO users(username, full_name, role, password_hash, salt) VALUES (?,?,?,?,?)";
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, fullName.trim());
            ps.setString(3, role);
            ps.setString(4, PasswordHasher.hash(password, salt));
            ps.setString(5, salt);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new User(keys.getLong(1), username.trim(), fullName.trim(), role);
            }
        } catch (SQLException e) {
            throw new DataException("Could not create user", e);
        }
    }
}
