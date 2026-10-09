package com.hms.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record Appointment(
        long id,
        long patientId,
        String patientName,
        String visitingNumber,
        long doctorId,
        String doctorName,
        LocalDate date,
        LocalTime time,
        String reason,
        Status status,
        double cost,
        String notes) {

    public enum Status {
        SCHEDULED("Scheduled"), COMPLETED("Completed"), CANCELLED("Cancelled");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        public static Status fromLabel(String label) {
            for (Status s : values()) {
                if (s.label.equalsIgnoreCase(label)) {
                    return s;
                }
            }
            return SCHEDULED;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public Appointment withStatus(Status newStatus) {
        return new Appointment(id, patientId, patientName, visitingNumber, doctorId, doctorName,
                date, time, reason, newStatus, cost, notes);
    }
}
