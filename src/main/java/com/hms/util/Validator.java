package com.hms.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

/** Input rules shared by the forms. Each check returns an error message, or null when the value is fine. */
public final class Validator {

    public static final DateTimeFormatter UK_DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private static final Pattern EMAIL =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$");
    private static final Pattern NAME = Pattern.compile("^[\\p{L}][\\p{L} .'-]*$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9 ]{10,15}$");
    private static final Pattern UK_POSTCODE =
            Pattern.compile("^[A-Z]{1,2}[0-9][A-Z0-9]? ?[0-9][A-Z]{2}$", Pattern.CASE_INSENSITIVE);

    private Validator() {
    }

    public static String name(String label, String value) {
        if (isBlank(value)) {
            return label + " is required";
        }
        if (!NAME.matcher(value.trim()).matches()) {
            return label + " can only contain letters, spaces, hyphens and apostrophes";
        }
        return null;
    }

    public static String required(String label, String value) {
        return isBlank(value) ? label + " is required" : null;
    }

    /** Email is optional; when given it must look like an address. */
    public static String email(String value) {
        if (isBlank(value)) {
            return null;
        }
        return EMAIL.matcher(value.trim()).matches() ? null : "Email address is not valid";
    }

    public static String phone(String value) {
        if (isBlank(value)) {
            return "Phone number is required";
        }
        return PHONE.matcher(value.trim()).matches() ? null : "Phone number should be 10 to 15 digits";
    }

    public static String postcode(String value) {
        if (isBlank(value)) {
            return "Postcode is required";
        }
        return UK_POSTCODE.matcher(value.trim()).matches() ? null : "Postcode is not a valid UK postcode";
    }

    public static String dateOfBirth(String value) {
        if (isBlank(value)) {
            return "Date of birth is required";
        }
        LocalDate dob = parseUkDate(value);
        if (dob == null) {
            return "Date of birth must be a real date in dd/mm/yyyy format";
        }
        if (dob.isAfter(LocalDate.now())) {
            return "Date of birth cannot be in the future";
        }
        if (dob.isBefore(LocalDate.now().minusYears(130))) {
            return "Date of birth is too far in the past";
        }
        return null;
    }

    public static String money(String label, String value) {
        try {
            double v = Double.parseDouble(value.trim());
            return v < 0 ? label + " cannot be negative" : null;
        } catch (NumberFormatException | NullPointerException e) {
            return label + " must be a number";
        }
    }

    public static LocalDate parseUkDate(String value) {
        try {
            return LocalDate.parse(value.trim(), UK_DATE);
        } catch (DateTimeParseException | NullPointerException e) {
            return null;
        }
    }

    public static String normalisePostcode(String value) {
        String compact = value.trim().toUpperCase().replace(" ", "");
        return compact.length() > 3
                ? compact.substring(0, compact.length() - 3) + " " + compact.substring(compact.length() - 3)
                : compact;
    }

    /** The age-based notice shown after registration (the original app's feature, with its ordering bug fixed). */
    public static String ageNotice(int age) {
        if (age >= 70) {
            return "Eligible for a free meal when visiting the hospital.";
        }
        if (age < 13) {
            return "Will be assigned a doctor specialised in paediatrics, and must be accompanied by an adult.";
        }
        if (age < 18) {
            return "Must be accompanied by an adult when visiting the hospital.";
        }
        return "Welcome to the hospital.";
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
