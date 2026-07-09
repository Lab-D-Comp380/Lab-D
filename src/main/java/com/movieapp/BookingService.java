package com.movieapp;

import java.sql.SQLException;
import java.util.Optional;

public class BookingService {

    private final BookingRepository bookingRepository = new BookingRepository();

    public Optional<Booking> createBooking(Booking booking) {
        try {
            return Optional.of(bookingRepository.createBooking(booking));
        } catch (SQLException e) {
            System.err.println("Failed to create booking: " + e.getMessage());
            return Optional.empty();
        }
    }
}
