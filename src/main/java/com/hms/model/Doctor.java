package com.hms.model;

public record Doctor(long id, String name, String specialty, String phone, String email, double consultationFee) {
    @Override
    public String toString() {
        return name + " (" + specialty + ")";
    }
}
