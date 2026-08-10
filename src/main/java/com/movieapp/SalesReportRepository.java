package com.movieapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Aggregates ticket counts from bookings, grouped by movie.
public class SalesReportRepository {

    public record MovieTicketTotal(String title, int ticketsSold) {
    }

    public List<MovieTicketTotal> findTicketTotalsByMovie() throws SQLException {
        String sql = """
                SELECT m.title,
                       COALESCE(SUM(b.ticket_count), 0) AS tickets_sold
                FROM movies m
                LEFT JOIN bookings b ON b.movie_id = m.id
                GROUP BY m.id, m.title
                ORDER BY m.title
                """;

        List<MovieTicketTotal> totals = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                totals.add(new MovieTicketTotal(
                        rs.getString("title"),
                        rs.getInt("tickets_sold")
                ));
            }
        }

        return totals;
    }
}
