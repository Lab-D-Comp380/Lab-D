package com.movieapp;

// ---------- MOVIE CLASS ----------
public class Movie {

    // Movie information
    private int movieId;
    private String title;
    private String genre;
    private int duration;

    // Creates a new movie object with the provided information.
    public Movie(int movieId, String title, String genre, int duration) {

        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
    }

    // ---------- GETTERS ----------

    // Returns the movie ID.
    public int getMovieId() {
        return movieId;
    }

    // Returns the movie title.
    public String getTitle() {
        return title;
    }

    // Returns the movie genre.
    public String getGenre() {
        return genre;
    }

    // Returns the movie duration in minutes.
    public int getDuration() {
        return duration;
    }

    // Returns a formatted string describing the movie.
    @Override
    public String toString() {
        return "Movie ID: " + movieId +
               ", Title: " + title +
               ", Genre: " + genre +
               ", Duration: " + duration + " minutes";
    }
}
