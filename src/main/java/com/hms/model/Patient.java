package com.hms.model;

import java.time.LocalDate;
import java.time.Period;

public record Patient(
        long id,
        String visitingNumber,
        String title,
        String firstName,
        String lastName,
        LocalDate dob,
        String gender,
        String email,
        String phone,
        String houseNumber,
        String street,
        String city,
        String postalCode,
        Long conditionId,
        String conditionName,
        boolean disability,
        String disabilityNote) {

    public String fullName() {
        return title + " " + firstName + " " + lastName;
    }

    public int age() {
        return Period.between(dob, LocalDate.now()).getYears();
    }

    public String address() {
        return String.join(", ",
                (nz(houseNumber) + " " + nz(street)).trim(), nz(city), nz(postalCode)).replaceAll("(, )+$", "");
    }

    public Patient withId(long newId, String newVisitingNumber) {
        return new Patient(newId, newVisitingNumber, title, firstName, lastName, dob, gender, email, phone,
                houseNumber, street, city, postalCode, conditionId, conditionName, disability, disabilityNote);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + "  #" + visitingNumber;
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }
}
