package com.hms;

import com.hms.dao.AppointmentDao;
import com.hms.dao.ConditionDao;
import com.hms.dao.DoctorDao;
import com.hms.dao.PatientDao;
import com.hms.dao.UserDao;
import com.hms.db.Database;

/** The data-access objects the screens share, created once at start-up. */
public final class Services {

    public final UserDao users;
    public final PatientDao patients;
    public final DoctorDao doctors;
    public final AppointmentDao appointments;
    public final ConditionDao conditions;

    public Services(Database db) {
        this.users = new UserDao(db);
        this.patients = new PatientDao(db);
        this.doctors = new DoctorDao(db);
        this.appointments = new AppointmentDao(db);
        this.conditions = new ConditionDao(db);
    }
}
