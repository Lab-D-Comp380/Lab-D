package com.movieapp;

// ---------- REVIEW CLASS ----------
public class Review {

    private final int reviewId;
    private final String username;
    private final int movieId;
    private final int rating;       // 1 to 5
    private final String reviewText;

    public Review(int reviewId,
                  String username,
                  int movieId,
                  int rating,
                  String reviewText) {
        this.reviewId = reviewId;
        this.username = username;
        this.movieId = movieId;
        this.rating = rating;
        this.reviewText = reviewText;
    }

    public int getReviewId() {
        return reviewId;
    }

    public String getUsername() {
        return username;
    }

    public int getMovieId() {
        return movieId;
    }

    public int getRating() {
        return rating;
    }

    public String getReviewText() {
        return reviewText;
    }
}