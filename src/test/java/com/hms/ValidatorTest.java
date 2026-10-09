package com.hms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hms.util.Validator;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ValidatorTest {

    @Test
    void namesRejectDigitsAndBlanks() {
        assertNull(Validator.name("First name", "Mary-Jane"));
        assertNull(Validator.name("Last name", "O'Brien"));
        assertNotNull(Validator.name("First name", "J0hn"));
        assertNotNull(Validator.name("First name", "  "));
    }

    @Test
    void emailIsOptionalButMustBeValidWhenGiven() {
        assertNull(Validator.email(""));
        assertNull(Validator.email("a.b@nhs.example.uk"));
        assertNotNull(Validator.email("not-an-email"));
    }

    @Test
    void ukPostcodes() {
        assertNull(Validator.postcode("UB8 3PH"));
        assertNull(Validator.postcode("sw1a1aa"));
        assertNotNull(Validator.postcode("12345"));
        assertEquals("SW1A 1AA", Validator.normalisePostcode("sw1a1aa"));
    }

    @Test
    void dateOfBirthMustBeRealAndInThePast() {
        assertNull(Validator.dateOfBirth("29/02/2000"));
        assertNotNull(Validator.dateOfBirth("31/02/2000"));
        assertNotNull(Validator.dateOfBirth("2000-01-01"));
        assertNotNull(Validator.dateOfBirth(LocalDate.now().plusDays(1).format(Validator.UK_DATE)));
    }

    @Test
    void phoneNumbers() {
        assertNull(Validator.phone("07700 900123"));
        assertNull(Validator.phone("+447700900123"));
        assertNotNull(Validator.phone("12ab"));
    }

    @Test
    void ageNoticeChecksYoungestGroupFirst() {
        // the original app tested "under 18" before "under 13", so the paediatrics message could never appear
        assertEquals(true, Validator.ageNotice(8).contains("paediatrics"));
        assertEquals(true, Validator.ageNotice(15).contains("accompanied"));
        assertEquals(true, Validator.ageNotice(75).contains("free meal"));
    }
}
