package com.movieapp;

import java.sql.SQLException;

// ---------- BOOKING SERVICE ----------
public class BookingService {

    private final BookingRepository bookingRepository = new BookingRepository();

    // Creates a new booking.
    public boolean createBooking(Booking booking) {

        try {

            bookingRepository.createBooking(booking);

            return true;

        } catch (SQLException e) {

            System.err.println("Failed to create booking: " + e.getMessage());

            return false;
        }
    }
}
