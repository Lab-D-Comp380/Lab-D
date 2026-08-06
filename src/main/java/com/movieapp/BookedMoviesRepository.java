package com.movieapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Reads which movies a user has booked, so we can limit reviews to watched films.
// Kept separate from BookingRepository (owned by the backend team) to avoid editing their file.
public class BookedMoviesRepository {

    // Returns the distinct movies this user has booked, so they can be reviewed.
    public List<Movie> findBookedMovies(String username) throws SQLException {
        String sql = """
                SELECT DISTINCT m.id, m.title, m.genre, m.duration_minutes,
                       m.rating, m.release_date, m.poster_filename
                FROM bookings b
                JOIN movies m ON b.movie_id = m.id
                WHERE b.username = ?
                ORDER BY m.title
                """;

        List<Movie> movies = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date releaseDate = rs.getDate("release_date");
                    java.time.LocalDate localReleaseDate =
                            releaseDate != null ? releaseDate.toLocalDate() : null;

                    movies.add(new Movie(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("genre"),
                            rs.getInt("duration_minutes"),
                            rs.getString("rating"),
                            localReleaseDate,
                            rs.getString("poster_filename")
                    ));
                }
            }
        }

        return movies;
    }
}