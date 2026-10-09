package com.hms.db;

import com.hms.dao.AppointmentDao;
import com.hms.dao.ConditionDao;
import com.hms.dao.DataException;
import com.hms.dao.DoctorDao;
import com.hms.dao.PatientDao;
import com.hms.dao.UserDao;
import com.hms.model.Appointment;
import com.hms.model.Appointment.Status;
import com.hms.model.Condition;
import com.hms.model.Doctor;
import com.hms.model.NextOfKin;
import com.hms.model.Patient;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Fills a brand-new database with a demo login and realistic sample records, so the app is explorable on first run. */
final class DemoData {

    static final String DEMO_USER = "admin";
    static final String DEMO_PASSWORD = "admin123";

    private DemoData() {
    }

    static void seedIfEmpty(Database db) {
        try {
            if (!db.isEmpty()) {
                return;
            }
        } catch (SQLException e) {
            throw new DataException("Could not inspect database", e);
        }

        new UserDao(db).create(DEMO_USER, "Demo Administrator", "Administrator", DEMO_PASSWORD.toCharArray());

        ConditionDao conditions = new ConditionDao(db);
        List<Condition> conds = new ArrayList<>();
        for (String name : new String[] {"Asthma", "Back pain", "Diabetes (Type 2)", "Fracture", "General check-up",
                "Hypertension", "Migraine", "Pregnancy care", "Skin rash", "Sports injury"}) {
            conds.add(conditions.add(name));
        }

        DoctorDao doctorDao = new DoctorDao(db);
        List<Doctor> doctors = new ArrayList<>();
        Object[][] doctorRows = {
                {"Dr. Amelia Hart", "General Practice", 45.0},
                {"Dr. Rohan Mehta", "Cardiology", 120.0},
                {"Dr. Sofia Alvarez", "Paediatrics", 60.0},
                {"Dr. James Okafor", "Orthopaedics", 95.0},
                {"Dr. Hannah Lee", "Dermatology", 80.0},
                {"Dr. Tomasz Nowak", "Neurology", 130.0},
        };
        for (Object[] r : doctorRows) {
            String name = (String) r[0];
            String email = name.substring(4).toLowerCase().replace(' ', '.') + "@hospital.example";
            doctors.add(doctorDao.save(new Doctor(0, name, (String) r[1], "020 7946 0" + (100 + doctors.size()),
                    email, (Double) r[2])));
        }

        Object[][] patientRows = {
                {"Mr", "Oliver", "Bennett", "1958-03-14", "Male", "Hypertension", "Grace Bennett", "Spouse"},
                {"Ms", "Priya", "Sharma", "1991-07-22", "Female", "Migraine", "Arjun Sharma", "Brother"},
                {"Mrs", "Eleanor", "Clarke", "1949-11-02", "Female", "Diabetes (Type 2)", "Michael Clarke", "Son"},
                {"Master", "Leo", "Thompson", "2016-05-30", "Male", "Asthma", "Sarah Thompson", "Mother"},
                {"Mr", "Daniel", "Adeyemi", "1987-01-09", "Male", "Sports injury", "Ife Adeyemi", "Spouse"},
                {"Miss", "Chloe", "Evans", "2008-09-17", "Female", "Skin rash", "Mark Evans", "Father"},
                {"Mr", "Ahmed", "Hassan", "1975-12-25", "Male", "Back pain", "Layla Hassan", "Spouse"},
                {"Mrs", "Isabella", "Rossi", "1993-04-11", "Female", "Pregnancy care", "Marco Rossi", "Spouse"},
                {"Mr", "George", "Wilson", "1940-06-03", "Male", "General check-up", "Emma Wilson", "Daughter"},
                {"Ms", "Mei", "Chen", "1999-02-28", "Female", "Fracture", "Li Chen", "Mother"},
                {"Dr", "Samuel", "Price", "1968-08-19", "Male", "Hypertension", "Ruth Price", "Spouse"},
                {"Miss", "Ava", "Murphy", "2012-10-05", "Female", "Asthma", "Kate Murphy", "Mother"},
        };
        String[][] addresses = {
                {"12", "Kingston Lane", "Uxbridge", "UB8 3PH"}, {"4", "Station Road", "Hayes", "UB3 4AA"},
                {"27", "Park Avenue", "Ealing", "W5 2QB"}, {"9", "Mill Street", "Slough", "SL1 1AA"},
                {"88", "High Street", "Hillingdon", "UB10 0BJ"}, {"3", "Church Close", "Ruislip", "HA4 7DE"},
        };
        PatientDao patientDao = new PatientDao(db);
        List<Patient> patients = new ArrayList<>();
        int i = 0;
        for (Object[] r : patientRows) {
            String[] addr = addresses[i % addresses.length];
            Long condId = conds.stream().filter(c -> c.name().equals(r[5])).findFirst().map(Condition::id).orElse(null);
            String first = (String) r[1];
            String last = (String) r[2];
            Patient p = new Patient(0, null, (String) r[0], first, last, LocalDate.parse((String) r[3]), (String) r[4],
                    first.toLowerCase() + "." + last.toLowerCase() + "@mail.example", "07700 900" + String.format("%03d", 100 + i),
                    addr[0], addr[1], addr[2], addr[3], condId, (String) r[5], i == 8, i == 8 ? "Uses a wheelchair" : null);
            NextOfKin kin = new NextOfKin(0, 0, (String) r[6], (String) r[7], "07700 900" + String.format("%03d", 500 + i), null);
            patients.add(patientDao.save(p, kin));
            i++;
        }

        // A spread of past (completed/cancelled) and future (scheduled) appointments.
        AppointmentDao appts = new AppointmentDao(db);
        Random rnd = new Random(42);
        String[] reasons = {"Initial consultation", "Follow-up", "Test results", "Prescription review", "Routine check"};
        LocalDate today = LocalDate.now();
        for (int day = -20; day <= 10; day++) {
            int perDay = day == 0 ? 4 : rnd.nextInt(3);
            for (int k = 0; k < perDay; k++) {
                Patient p = patients.get(rnd.nextInt(patients.size()));
                Doctor d = doctors.get(rnd.nextInt(doctors.size()));
                LocalDate date = today.plusDays(day);
                LocalTime time = LocalTime.of(9 + rnd.nextInt(8), rnd.nextBoolean() ? 0 : 30);
                if (appts.isDoctorBooked(d.id(), date, time, 0)) {
                    continue;
                }
                Status status = day < 0 ? (rnd.nextInt(8) == 0 ? Status.CANCELLED : Status.COMPLETED) : Status.SCHEDULED;
                appts.save(new Appointment(0, p.id(), null, null, d.id(), null, date, time,
                        reasons[rnd.nextInt(reasons.length)], status, d.consultationFee(), null));
            }
        }
    }
}
