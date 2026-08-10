package com.movieapp;

import java.util.Set;

public class ReviewValidator {

    /** The possible outcomes of validating a review submission. */
    public enum Result {
        NO_MOVIE_SELECTED,
        NOT_BOOKED,
        INVALID_RATING,
        VALID
    }

    /**
     * @param selectedMovieId the id of the chosen movie, or null if none chosen
     * @param bookedMovieIds  the set of movie ids the user has booked
     * @param rating          the star rating the user selected (0 = none)
     * @return the validation result
     */
    public Result validate(Integer selectedMovieId,
                           Set<Integer> bookedMovieIds,
                           int rating) {
        // 1. A movie must be selected.
        if (selectedMovieId == null) {
            return Result.NO_MOVIE_SELECTED;
        }
        // 2. The user must have booked this movie to review it.
        if (bookedMovieIds == null || !bookedMovieIds.contains(selectedMovieId)) {
            return Result.NOT_BOOKED;
        }
        // 3. A star rating of at least 1 is required.
        if (rating < 1) {
            return Result.INVALID_RATING;
        }
        // 4. All checks passed.
        return Result.VALID;
    }
    /**
     * Renders a star rating as a string of filled and empty stars.
     * @param rating the rating to render (expected 0 to 5)
     * @return a 5-character string of filled (★) and empty (☆) stars
     */
    public String filledStars(int rating) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            sb.append(i <= rating ? "\u2605" : "\u2606");
        }
        return sb.toString();
    }
}