package com.hms;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hms.util.PasswordHasher;
import org.junit.jupiter.api.Test;

class PasswordHasherTest {

    @Test
    void matchesOnlyTheRightPassword() {
        String salt = PasswordHasher.newSalt();
        String hash = PasswordHasher.hash("s3cret!".toCharArray(), salt);
        assertTrue(PasswordHasher.matches("s3cret!".toCharArray(), salt, hash));
        assertFalse(PasswordHasher.matches("s3cret".toCharArray(), salt, hash));
    }

    @Test
    void samePasswordDifferentSaltGivesDifferentHash() {
        assertNotEquals(PasswordHasher.hash("pw".toCharArray(), PasswordHasher.newSalt()),
                PasswordHasher.hash("pw".toCharArray(), PasswordHasher.newSalt()));
    }
}
