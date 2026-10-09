-- Hospital Management System schema (SQLite)

CREATE TABLE IF NOT EXISTS users (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    username      TEXT    NOT NULL UNIQUE COLLATE NOCASE,
    full_name     TEXT    NOT NULL,
    role          TEXT    NOT NULL DEFAULT 'Receptionist',
    password_hash TEXT    NOT NULL,
    salt          TEXT    NOT NULL,
    created_at    TEXT    NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS conditions (
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT    NOT NULL UNIQUE COLLATE NOCASE
);

CREATE TABLE IF NOT EXISTS doctors (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    name             TEXT    NOT NULL,
    specialty        TEXT    NOT NULL,
    phone            TEXT,
    email            TEXT,
    consultation_fee REAL    NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS patients (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    visiting_number TEXT    NOT NULL UNIQUE,
    title           TEXT    NOT NULL,
    first_name      TEXT    NOT NULL,
    last_name       TEXT    NOT NULL,
    dob             TEXT    NOT NULL,           -- ISO yyyy-MM-dd
    gender          TEXT,
    email           TEXT,
    phone           TEXT    NOT NULL,
    house_number    TEXT,
    street          TEXT,
    city            TEXT,
    postal_code     TEXT,
    condition_id    INTEGER REFERENCES conditions(id) ON DELETE SET NULL,
    disability      INTEGER NOT NULL DEFAULT 0,
    disability_note TEXT,
    created_at      TEXT    NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS next_of_kin (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    patient_id   INTEGER NOT NULL UNIQUE REFERENCES patients(id) ON DELETE CASCADE,
    full_name    TEXT    NOT NULL,
    relationship TEXT    NOT NULL,
    phone        TEXT    NOT NULL,
    email        TEXT
);

CREATE TABLE IF NOT EXISTS appointments (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    patient_id INTEGER NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    doctor_id  INTEGER NOT NULL REFERENCES doctors(id) ON DELETE RESTRICT,
    date       TEXT    NOT NULL,                -- ISO yyyy-MM-dd
    time       TEXT    NOT NULL,                -- HH:mm
    reason     TEXT,
    status     TEXT    NOT NULL DEFAULT 'Scheduled',
    cost       REAL    NOT NULL DEFAULT 0,
    notes      TEXT
);

CREATE INDEX IF NOT EXISTS idx_appointments_date    ON appointments(date);
CREATE INDEX IF NOT EXISTS idx_appointments_patient ON appointments(patient_id);
