package com.movieapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class BookingRepository {

    public Booking createBooking(Booking booking) throws SQLException {
        String sql = """
                INSERT INTO bookings
                (username, movie_id, showtime, seats, payment_method, card_last_four, email)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, booking.getUsername());
            statement.setInt(2, booking.getMovieId());
            statement.setString(3, booking.getShowtime());
            statement.setString(4, booking.getSeats());
            statement.setString(5, booking.getPaymentMethod());
            statement.setString(6, booking.getCardLastFour());
            statement.setString(7, booking.getEmail());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    LocalDateTime bookedAt = fetchBookedAt(connection, id);
                    return new Booking(
                            id,
                            booking.getUsername(),
                            booking.getMovieId(),
                            booking.getShowtime(),
                            booking.getSeats(),
                            booking.getPaymentMethod(),
                            booking.getCardLastFour(),
                            booking.getEmail(),
                            bookedAt
                    );
                }
            }
        }

        throw new SQLException("Failed to retrieve generated booking id.");
    }

    private LocalDateTime fetchBookedAt(Connection connection, int bookingId) throws SQLException {
        String sql = "SELECT booked_at FROM bookings WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Timestamp timestamp = rs.getTimestamp("booked_at");
                    return timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now();
                }
            }
        }

        return LocalDateTime.now();
    }
}
