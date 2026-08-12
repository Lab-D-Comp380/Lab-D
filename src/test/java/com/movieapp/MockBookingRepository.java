package com.movieapp;

import java.sql.SQLException;

// ---------- TEST BOOKING REPOSITORY ----------
public class MockBookingRepository extends BookingRepository {

    private boolean shouldThrowException = false;

    // ---------- SIMULATE DATABASE FAILURE ----------
    public void setShouldThrowException(boolean shouldThrowException) {
        this.shouldThrowException = shouldThrowException;
    }

    // ---------- CREATE BOOKING ----------
    @Override
    public int createBooking(Booking booking) throws SQLException {
        if (shouldThrowException) {
            throw new SQLException("Test database failure.");
        }
        return 1;
    }
}
