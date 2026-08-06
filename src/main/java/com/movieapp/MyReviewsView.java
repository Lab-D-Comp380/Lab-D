package com.movieapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

// "My Reviews" screen: pick a movie you've booked, see its average and reviews,
// and leave a new star rating + written review.
public class MyReviewsView {

    private final String username;
    private final ReviewService reviewService;
    private final BookedMoviesRepository bookedMoviesRepository;
    private final Runnable onBack;

    // Current selection state
    private Movie selectedMovie;
    private int selectedRating = 0;

    // Nodes we refresh as the user interacts
    private final VBox detailBox = new VBox(14);
    private final Label averageLabel = new Label();
    private final VBox reviewList = new VBox(10);
    private final TextArea reviewTextArea = new TextArea();
    private final Label formError = new Label();
    private final HBox starRow = new HBox(6);

    public MyReviewsView(String username,
                         ReviewService reviewService,
                         Runnable onBack) {
        this.username = username;
        this.reviewService = reviewService;
        this.bookedMoviesRepository = new BookedMoviesRepository();
        this.onBack = onBack;
    }

    public Parent createView() {
        // Back link
        Button back = new Button("\u2190 Back");
        back.getStyleClass().add("auth-link");
        back.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });
        HBox backBar = new HBox(back);
        backBar.setAlignment(Pos.CENTER_LEFT);

        Label heading = new Label("My Reviews");
        heading.getStyleClass().add("page-title");

        Label sub = new Label("Review movies you've booked.");
        sub.getStyleClass().add("movie-details");

        // Movie picker (only movies the user has booked)
        List<Movie> bookedMovies = loadBookedMovies();

        ComboBox<Movie> moviePicker = new ComboBox<>();
        moviePicker.setPromptText("Choose a movie you've booked");
        moviePicker.getItems().addAll(bookedMovies);
        // Show the title in the dropdown instead of the object reference.
        moviePicker.setCellFactory(list -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Movie movie, boolean empty) {
                super.updateItem(movie, empty);
                setText((empty || movie == null) ? null : movie.getTitle());
            }
        });
        moviePicker.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Movie movie, boolean empty) {
                super.updateItem(movie, empty);
                setText((empty || movie == null) ? null : movie.getTitle());
            }
        });

        moviePicker.setOnAction(e -> {
            selectedMovie = moviePicker.getValue();
            refreshDetail();
        });

        VBox page = new VBox(18, backBar, heading, sub, moviePicker, detailBox);
        page.setPadding(new Insets(24));
        page.setAlignment(Pos.TOP_LEFT);
        page.getStyleClass().add("page");

        // Empty state if the user hasn't booked anything yet.
        if (bookedMovies.isEmpty()) {
            Label empty = new Label("You haven't booked any movies yet. Book a movie to review it.");
            empty.getStyleClass().add("movie-details");
            page.getChildren().add(empty);
            moviePicker.setDisable(true);
        }

        detailBox.setVisible(false);
        detailBox.setManaged(false);

        return page;
    }

    private List<Movie> loadBookedMovies() {
        try {
            return bookedMoviesRepository.findBookedMovies(username);
        } catch (SQLException e) {
            System.err.println("Failed to load booked movies: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    // Rebuilds the detail area for the currently selected movie.
    private void refreshDetail() {
        if (selectedMovie == null) {
            detailBox.setVisible(false);
            detailBox.setManaged(false);
            return;
        }

        detailBox.setVisible(true);
        detailBox.setManaged(true);
        detailBox.getChildren().clear();
        selectedRating = 0;

        // Average rating summary
        double avg = reviewService.getAverageRating(selectedMovie.getMovieId());
        int count = reviewService.getReviewCount(selectedMovie.getMovieId());
        averageLabel.getStyleClass().setAll("section-title");
        if (count == 0) {
            averageLabel.setText("No reviews yet \u2014 be the first!");
        } else {
            averageLabel.setText(String.format("\u2605 %.1f  (%d review%s)",
                    avg, count, count == 1 ? "" : "s"));
        }

        // Existing reviews
        reviewList.getChildren().clear();
        List<Review> reviews = reviewService.getReviewsForMovie(selectedMovie.getMovieId());
        if (reviews.isEmpty()) {
            Label none = new Label("No reviews to show.");
            none.getStyleClass().add("movie-details");
            reviewList.getChildren().add(none);
        } else {
            for (Review r : reviews) {
                reviewList.getChildren().add(reviewCard(r));
            }
        }

        // The submit form
        Label formHeading = new Label("Leave a review");
        formHeading.getStyleClass().add("section-title");

        buildStarRow();

        reviewTextArea.clear();
        reviewTextArea.setPromptText("Write your thoughts (optional)...");
        reviewTextArea.setWrapText(true);
        reviewTextArea.setPrefRowCount(3);
        reviewTextArea.setMaxWidth(520);

        formError.getStyleClass().setAll("auth-error");
        formError.setVisible(false);
        formError.setManaged(false);

        Button submit = new Button("Submit Review");
        submit.getStyleClass().add("ticket-button");
        submit.setOnAction(e -> handleSubmit());

        detailBox.getChildren().addAll(
                averageLabel,
                labeled("Reviews"),
                reviewList,
                formHeading,
                starRow,
                reviewTextArea,
                formError,
                submit
        );
    }

    // Clickable 1-5 stars.
    private void buildStarRow() {
        starRow.getChildren().clear();
        starRow.setAlignment(Pos.CENTER_LEFT);
        for (int i = 1; i <= 5; i++) {
            final int value = i;
            Button star = new Button("\u2606");   // empty star
            star.getStyleClass().add("star-button");
            star.setOnAction(e -> {
                selectedRating = value;
                paintStars();
            });
            starRow.getChildren().add(star);
        }
    }

    // Fills stars up to the selected rating.
    private void paintStars() {
        for (int i = 0; i < starRow.getChildren().size(); i++) {
            Button star = (Button) starRow.getChildren().get(i);
            star.setText(i < selectedRating ? "\u2605" : "\u2606");  // filled vs empty
        }
    }

    private void handleSubmit() {
        if (selectedMovie == null) {
            return;
        }
        if (selectedRating < 1) {
            showFormError("Please pick a star rating.");
            return;
        }

        String text = reviewTextArea.getText().trim();
        boolean ok = reviewService.submitReview(
                username, selectedMovie.getMovieId(), selectedRating, text);

        if (ok) {
            refreshDetail();   // reload averages + list, reset the form
        } else {
            showFormError("Could not save your review. Is MySQL running?");
        }
    }

    private void showFormError(String message) {
        formError.setText(message);
        formError.setVisible(true);
        formError.setManaged(true);
    }

    private VBox reviewCard(Review r) {
        Label stars = new Label(filledStars(r.getRating()));
        stars.getStyleClass().add("review-stars");

        Label author = new Label("by " + r.getUsername());
        author.getStyleClass().add("movie-details");

        HBox header = new HBox(10, stars, author);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(4, header);

        String text = r.getReviewText();
        if (text != null && !text.isBlank()) {
            Label body = new Label(text);
            body.getStyleClass().add("review-text");
            body.setWrapText(true);
            body.setMaxWidth(520);
            card.getChildren().add(body);
        }

        card.getStyleClass().add("review-card");
        card.setMaxWidth(520);
        card.setPadding(new Insets(10));
        return card;
    }

    private String filledStars(int rating) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            sb.append(i <= rating ? "\u2605" : "\u2606");
        }
        return sb.toString();
    }

    private Label labeled(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("section-title");
        return l;
    }
}