package com.movieapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// ---------- REVIEW REPOSITORY ----------
public class ReviewRepository {

    // Saves a new review.
    public void insert(Review review) throws SQLException {
        String sql = """
                INSERT INTO reviews (username, movie_id, rating, review_text)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, review.getUsername());
            statement.setInt(2, review.getMovieId());
            statement.setInt(3, review.getRating());
            statement.setString(4, review.getReviewText());
            statement.executeUpdate();
        }
    }

    // All reviews for one movie, newest first.
    public List<Review> findByMovie(int movieId) throws SQLException {
        String sql = """
                SELECT id, username, movie_id, rating, review_text
                FROM reviews
                WHERE movie_id = ?
                ORDER BY created_at DESC
                """;

        List<Review> reviews = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, movieId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    reviews.add(new Review(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getInt("movie_id"),
                            rs.getInt("rating"),
                            rs.getString("review_text")
                    ));
                }
            }
        }

        return reviews;
    }

    // Average rating for a movie, or 0 if it has no reviews.
    public double averageForMovie(int movieId) throws SQLException {
        String sql = "SELECT AVG(rating) AS avg_rating FROM reviews WHERE movie_id = ?";

        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, movieId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble("avg_rating");
                    return rs.wasNull() ? 0.0 : avg;
                }
                return 0.0;
            }
        }
    }

    // How many reviews a movie has.
    public int countForMovie(int movieId) throws SQLException {
        String sql = "SELECT COUNT(*) AS review_count FROM reviews WHERE movie_id = ?";

        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, movieId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("review_count");
                }
                return 0;
            }
        }
    }
}