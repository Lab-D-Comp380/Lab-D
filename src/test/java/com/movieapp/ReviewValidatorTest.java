package com.movieapp;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ReviewValidator.validate().
 *
 * These cover every independent path through the method (see the flow graph in
 * the Phase 3 report, section 2.2). No database or UI is needed - the logic is
 * pure, so these run instantly.
 *
 * @author Kyle Dilanchian
 */
public class ReviewValidatorTest {

    private final ReviewValidator validator = new ReviewValidator();

    // Path 1: no movie selected -> NO_MOVIE_SELECTED
    @Test
    void noMovieSelectedReturnsNoMovie() {
        ReviewValidator.Result result =
                validator.validate(null, Set.of(1, 2), 5);
        assertEquals(ReviewValidator.Result.NO_MOVIE_SELECTED, result);
    }

    // Path 2: movie selected but not booked -> NOT_BOOKED
    @Test
    void unbookedMovieReturnsNotBooked() {
        ReviewValidator.Result result =
                validator.validate(9, Set.of(1, 2), 5);
        assertEquals(ReviewValidator.Result.NOT_BOOKED, result);
    }

    // Path 3: booked movie but rating below 1 -> INVALID_RATING
    @Test
    void bookedMovieWithNoRatingReturnsInvalidRating() {
        ReviewValidator.Result result =
                validator.validate(1, Set.of(1, 2), 0);
        assertEquals(ReviewValidator.Result.INVALID_RATING, result);
    }

    // Path 4: booked movie with a valid rating -> VALID
    @Test
    void bookedMovieWithRatingIsValid() {
        ReviewValidator.Result result =
                validator.validate(1, Set.of(1, 2), 4);
        assertEquals(ReviewValidator.Result.VALID, result);
    }

    // Boundary: rating of exactly 1 is the lowest valid rating.
    @Test
    void ratingOfExactlyOneIsValid() {
        ReviewValidator.Result result =
                validator.validate(1, Set.of(1), 1);
        assertEquals(ReviewValidator.Result.VALID, result);
    }

    // Edge: an empty booked set means nothing can be reviewed.
    @Test
    void emptyBookedSetReturnsNotBooked() {
        ReviewValidator.Result result =
                validator.validate(1, Set.of(), 5);
        assertEquals(ReviewValidator.Result.NOT_BOOKED, result);
    }

    // Edge: a null booked set is treated as "not booked", not a crash.
    @Test
    void nullBookedSetReturnsNotBooked() {
        ReviewValidator.Result result =
                validator.validate(1, null, 5);
        assertEquals(ReviewValidator.Result.NOT_BOOKED, result);
    }
    
    // ----- filledStars (second method tested for 2.1) -----

    @Test
    void fiveStarsAreAllFilled() {
        assertEquals("\u2605\u2605\u2605\u2605\u2605", validator.filledStars(5));
    }

    @Test
    void zeroStarsAreAllEmpty() {
        assertEquals("\u2606\u2606\u2606\u2606\u2606", validator.filledStars(0));
    }

    @Test
    void threeStarsAreThreeFilledTwoEmpty() {
        assertEquals("\u2605\u2605\u2605\u2606\u2606", validator.filledStars(3));
    }

    @Test
    void oneStarIsOneFilled() {
        assertEquals("\u2605\u2606\u2606\u2606\u2606", validator.filledStars(1));
    }

    @Test
    void alwaysReturnsFiveCharacters() {
        // No matter the rating, the output should always be 5 stars long.
        assertEquals(5, validator.filledStars(4).length());
        assertEquals(5, validator.filledStars(0).length());
    }
}