package com.movieapp;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class MovieService {

    private final MovieRepository movieRepository = new MovieRepository();

    public List<Movie> getMovies() {
        try {
            return movieRepository.findAll();
        } catch (SQLException e) {
            System.err.println("Failed to load movies: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Movie findMovieById(int id) {
        return getMovies().stream()
                .filter(movie -> movie.getMovieId() == id)
                .findFirst()
                .orElse(null);
    }
}
