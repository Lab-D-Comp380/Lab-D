package com.movieapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// ---------- SNACK ORDER REPOSITORY ----------
public class SnackOrderRepository {

    // ---------- SAVE A SNACK ORDER ----------
    public void createSnackOrder(SnackOrder snackOrder) throws SQLException {

        String sql = """
                INSERT INTO snack_orders
                (booking_id, snack_id, quantity)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, snackOrder.getBookingId());
            statement.setInt(2, snackOrder.getSnackId());
            statement.setInt(3, snackOrder.getQuantity());

            statement.executeUpdate();
        }
    }

}