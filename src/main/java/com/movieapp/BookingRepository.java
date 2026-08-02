package com.movieapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BookingRepository {

    public int createBooking(Booking booking) throws SQLException {
        String sql = """
                INSERT INTO bookings
                (username, movie_id, ticket_count, theater, showtime, seats, payment_method, card_last_four)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, booking.getUsername());
            statement.setInt(2, booking.getMovieId());
            statement.setInt(3, booking.getTicketCount());
            statement.setString(4, booking.getTheater());
            statement.setString(5, booking.getShowtime());
            statement.setString(6, booking.getSeats());
            statement.setString(7, booking.getPaymentMethod());
            statement.setString(8, booking.getCardLastFour());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("Failed to retrieve booking id after insert.");
    }
}
