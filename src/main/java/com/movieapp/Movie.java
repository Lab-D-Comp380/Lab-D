package com.movieapp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// ---------- MOVIE CLASS ----------
public class Movie {

    private static final DateTimeFormatter RELEASE_FORMAT =
            DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.US);

    private int movieId;
    private String title;
    private String genre;
    private int duration;
    private String rating;
    private LocalDate releaseDate;
    private String posterFilename;

    public Movie(int movieId, String title, String genre, int duration) {
        this(movieId, title, genre, duration, null, null, null);
    }

    public Movie(
            int movieId,
            String title,
            String genre,
            int duration,
            String rating,
            LocalDate releaseDate,
            String posterFilename
    ) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.posterFilename = posterFilename;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getDuration() {
        return duration;
    }

    public String getRating() {
        return rating;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public String getPosterFilename() {
        return posterFilename;
    }

    public String getDetailsLabel() {
        int hours = duration / 60;
        int minutes = duration % 60;
        String durationText = hours > 0
                ? hours + " HR " + String.format("%02d", minutes) + " MIN"
                : minutes + " MIN";
        if (rating == null || rating.isBlank()) {
            return durationText;
        }
        return durationText + " | " + rating;
    }

    public String getReleaseDateLabel() {
        if (releaseDate == null) {
            return "";
        }
        return "Released " + releaseDate.format(RELEASE_FORMAT);
    }

    @Override
    public String toString() {
        return "Movie ID: " + movieId +
               ", Title: " + title +
               ", Genre: " + genre +
               ", Duration: " + duration + " minutes";
    }
}
