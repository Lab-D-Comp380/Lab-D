package com.movieapp;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

// ---------- REVIEW SERVICE ----------
public class ReviewService {

    private final ReviewRepository reviewRepository = new ReviewRepository();

    // Saves a review. Returns true on success.
    public boolean submitReview(String username, int movieId, int rating, String text) {
        try {
            Review review = new Review(0, username, movieId, rating, text);
            reviewRepository.insert(review);
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to save review: " + e.getMessage());
            return false;
        }
    }

    public List<Review> getReviewsForMovie(int movieId) {
        try {
            return reviewRepository.findByMovie(movieId);
        } catch (SQLException e) {
            System.err.println("Failed to load reviews: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public double getAverageRating(int movieId) {
        try {
            return reviewRepository.averageForMovie(movieId);
        } catch (SQLException e) {
            System.err.println("Failed to load average rating: " + e.getMessage());
            return 0.0;
        }
    }

    public int getReviewCount(int movieId) {
        try {
            return reviewRepository.countForMovie(movieId);
        } catch (SQLException e) {
            System.err.println("Failed to load review count: " + e.getMessage());
            return 0;
        }
    }
}