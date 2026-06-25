package com.movieapp;

import java.util.ArrayList;
import java.util.List;

// ---------- MOVIE SERVICE ----------
public class MovieService {

    // Stores all available movies.
    private List<Movie> movies = new ArrayList<>();

    // Adds sample movie data for testing.
    public MovieService() {

        movies.add(
            new Movie(
                1,
                "Fake Movie1",
                "Action",
                180
            )
        );

        movies.add(
            new Movie(
                2,
                "Fake Movie2",
                "Animation",
                90
            )
        );

        movies.add(
            new Movie(
                3,
                "Fake Mmovie3",
                "Sci-Fi",
                150
            )
        );
    }

    // ---------- MOVIE OPERATIONS ----------

    // Adds a movie to the collection.
    public void addMovie(Movie movie) {
        movies.add(movie);
    }

    // Returns all movies currently available.
    public List<Movie> getMovies() {
        return movies;
    }

    // Searches for a movie using its ID.
    public Movie findMovieById(int id) {

        for (Movie movie : movies) {

            if (movie.getMovieId() == id) {
                return movie;
            }
        }

        // Returns null if no matching movie is found.
        return null;
    }
}