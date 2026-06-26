package com.movieapp;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MovieRepository {

    public List<Movie> findAll() throws SQLException {
        String sql = """
                SELECT id, title, genre, duration_minutes, rating, release_date, poster_filename
                FROM movies
                ORDER BY release_date DESC
                """;

        List<Movie> movies = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                Date releaseDate = rs.getDate("release_date");
                LocalDate localReleaseDate = releaseDate != null ? releaseDate.toLocalDate() : null;

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

        return movies;
    }
}
