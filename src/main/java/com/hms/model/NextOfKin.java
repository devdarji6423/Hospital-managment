package com.hms.model;

public record NextOfKin(long id, long patientId, String fullName, String relationship, String phone, String email) {
}
