package com.movieapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// ---------- BOOKING REPOSITORY ----------
public class BookingRepository {

    // Saves a booking to the database.
    public void createBooking(Booking booking) throws SQLException {

        String sql = """
                INSERT INTO bookings
                (username, movie_id, ticket_count)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, booking.getUsername());
            statement.setInt(2, booking.getMovieId());
            statement.setInt(3, booking.getTicketCount());

            statement.executeUpdate();
        }
    }
}
