package com.hms.model;

/** A medical problem a patient can be registered with (e.g. "Asthma"). */
public record Condition(long id, String name) {
    @Override
    public String toString() {
        return name;
    }
}
