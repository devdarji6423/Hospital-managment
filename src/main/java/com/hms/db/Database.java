package com.hms.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Owns the location of the SQLite database and hands out connections.
 * The schema is created on first use, and demo data is seeded into an empty database.
 */
public final class Database {

    private static Database instance;

    private final String url;

    private Database(Path file) {
        this.url = "jdbc:sqlite:" + file.toAbsolutePath();
    }

    /** Uses ./data/hospital.db, or the path in the HMS_DB environment variable. */
    public static synchronized Database get() {
        if (instance == null) {
            String override = System.getenv("HMS_DB");
            Path file = override != null && !override.isBlank()
                    ? Paths.get(override)
                    : Paths.get("data", "hospital.db");
            instance = open(file, true);
        }
        return instance;
    }

    /** Opens (and initialises) a database at the given file. Used directly by tests. */
    public static Database open(Path file, boolean seedDemoData) {
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create database folder for " + file, e);
        }
        Database db = new Database(file);
        db.initSchema();
        if (seedDemoData) {
            DemoData.seedIfEmpty(db);
        }
        return db;
    }

    /** Replaces the shared instance (tests). */
    public static synchronized void use(Database db) {
        instance = db;
    }

    public Connection connect() throws SQLException {
        Connection c = DriverManager.getConnection(url);
        try (Statement s = c.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON");
        }
        return c;
    }

    private void initSchema() {
        String sql;
        try (InputStream in = Database.class.getResourceAsStream("/com/hms/db/schema.sql")) {
            if (in == null) {
                throw new IllegalStateException("schema.sql missing from classpath");
            }
            sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read schema.sql", e);
        }
        try (Connection c = connect(); Statement s = c.createStatement()) {
            for (String stmt : stripComments(sql).split(";")) {
                if (!stmt.isBlank()) {
                    s.execute(stmt);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot initialise database schema", e);
        }
    }

    boolean isEmpty() throws SQLException {
        try (Connection c = connect(); Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM users")) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }

    private static String stripComments(String sql) {
        StringBuilder out = new StringBuilder();
        for (String line : sql.split("\n")) {
            int i = line.indexOf("--");
            out.append(i >= 0 ? line.substring(0, i) : line).append('\n');
        }
        return out.toString();
    }
}
