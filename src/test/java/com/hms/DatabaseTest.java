package com.hms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hms.dao.DataException;
import com.hms.db.Database;
import com.hms.model.Appointment;
import com.hms.model.Appointment.Status;
import com.hms.model.Doctor;
import com.hms.model.NextOfKin;
import com.hms.model.Patient;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DatabaseTest {

    @TempDir
    Path dir;

    private Services services;

    @BeforeEach
    void setUp() {
        services = new Services(Database.open(dir.resolve("test.db"), false));
    }

    @Test
    void demoDataIsSeededIntoANewDatabase() {
        Services seeded = new Services(Database.open(dir.resolve("demo.db"), true));
        assertTrue(seeded.users.authenticate("admin", "admin123".toCharArray()).isPresent());
        assertEquals(12, seeded.patients.search("").size());
        assertEquals(6, seeded.doctors.findAll().size());
    }

    @Test
    void usersLogInWithHashedPasswords() {
        services.users.create("nurse1", "Nina Nurse", "Nurse", "pa55word".toCharArray());
        assertTrue(services.users.authenticate("NURSE1", "pa55word".toCharArray()).isPresent());
        assertFalse(services.users.authenticate("nurse1", "wrong".toCharArray()).isPresent());
        assertThrows(DataException.class,
                () -> services.users.create("nurse1", "Other", "Nurse", "x12345".toCharArray()));
    }

    @Test
    void patientGetsUniqueVisitingNumberAndKin() {
        Patient saved = services.patients.save(patient("Ada"), new NextOfKin(0, 0, "Bob Smith", "Spouse", "07700 900999", null));
        assertTrue(saved.visitingNumber().matches("\\d{8}"));
        assertEquals("Bob Smith", services.patients.findKin(saved.id()).orElseThrow().fullName());
        assertEquals("Ada", services.patients.findByVisitingNumber(saved.visitingNumber()).orElseThrow().firstName());
        assertEquals(1, services.patients.search("ada").size());
        assertEquals(0, services.patients.search("zzz").size());
    }

    @Test
    void duplicateConditionsAreRejectedIgnoringCase() {
        services.conditions.add("Asthma");
        assertThrows(DataException.class, () -> services.conditions.add("asthma"));
    }

    @Test
    void appointmentsTrackHistoryAndPreventDoubleBooking() {
        Patient p = services.patients.save(patient("Ada"), null);
        Doctor d = services.doctors.save(new Doctor(0, "Dr. Test", "Cardiology", null, null, 100));
        LocalDate day = LocalDate.now().plusDays(3);
        long id = services.appointments.save(new Appointment(0, p.id(), null, null, d.id(), null, day,
                LocalTime.of(10, 0), "Check", Status.SCHEDULED, 100, null));

        assertTrue(services.appointments.isDoctorBooked(d.id(), day, LocalTime.of(10, 0), 0));
        assertFalse(services.appointments.isDoctorBooked(d.id(), day, LocalTime.of(10, 0), id));

        services.appointments.updateStatus(id, Status.CANCELLED);
        assertFalse(services.appointments.isDoctorBooked(d.id(), day, LocalTime.of(10, 0), 0));
        assertEquals(Status.CANCELLED, services.appointments.forPatient(p.id()).get(0).status());
    }

    @Test
    void doctorWithAppointmentsCannotBeDeleted() {
        Patient p = services.patients.save(patient("Ada"), null);
        Doctor d = services.doctors.save(new Doctor(0, "Dr. Test", "Cardiology", null, null, 100));
        services.appointments.save(new Appointment(0, p.id(), null, null, d.id(), null, LocalDate.now(),
                LocalTime.NOON, null, Status.COMPLETED, 100, null));
        assertThrows(DataException.class, () -> services.doctors.delete(d.id()));
    }

    @Test
    void deletingPatientRemovesTheirAppointmentsAndKin() {
        Patient p = services.patients.save(patient("Ada"), new NextOfKin(0, 0, "Bob", "Spouse", "07700 900999", null));
        Doctor d = services.doctors.save(new Doctor(0, "Dr. Test", "Cardiology", null, null, 100));
        services.appointments.save(new Appointment(0, p.id(), null, null, d.id(), null, LocalDate.now(),
                LocalTime.NOON, null, Status.SCHEDULED, 100, null));
        services.patients.delete(p.id());
        assertTrue(services.appointments.forPatient(p.id()).isEmpty());
        assertTrue(services.patients.findKin(p.id()).isEmpty());
        services.doctors.delete(d.id()); // no longer blocked
    }

    private static Patient patient(String first) {
        return new Patient(0, null, "Ms", first, "Lovelace", LocalDate.of(1990, 12, 10), "Female", null,
                "07700 900123", "1", "High Street", "London", "SW1A 1AA", null, null, false, null);
    }
}
