package com.movieapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MyReviewsView {

    private final String username;
    private final ReviewService reviewService;
    private final MovieService movieService;
    private final BookedMoviesRepository bookedMoviesRepository;
    private final Runnable onBack;

    // Ids of movies the user has booked (can write a review for).
    private final Set<Integer> bookedMovieIds = new HashSet<>();

    // Current selection state
    private Movie selectedMovie;
    private int selectedRating = 0;

    // Left column nodes we refresh as the user interacts
    private final VBox detailBox = new VBox(14);
    private final Label averageLabel = new Label();
    private final VBox reviewList = new VBox(10);
    private final TextArea reviewTextArea = new TextArea();
    private final Label formError = new Label();
    private final HBox starRow = new HBox(6);

    // Right column: poster + details panel
    private final VBox posterPanel = new VBox(16);

    public MyReviewsView(String username,
                         ReviewService reviewService,
                         MovieService movieService,
                         Runnable onBack) {
        this.username = username;
        this.reviewService = reviewService;
        this.movieService = movieService;
        this.bookedMoviesRepository = new BookedMoviesRepository();
        this.onBack = onBack;
    }

    public Parent createView() {
        // Back link
        Button back = new Button("\u2190 Back");
        back.getStyleClass().add("ticket-button");
        back.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });
        HBox backBar = new HBox(back);
        backBar.setAlignment(Pos.CENTER_LEFT);

        Label heading = new Label("Reviews");
        heading.getStyleClass().add("page-title");

        Label sub = new Label("Browse reviews for any movie. Book a ticket to leave your own.");
        sub.getStyleClass().add("movie-details");

        // Load which movies the user has booked (for write access).
        loadBookedMovieIds();

        // Movie picker shows ALL movies now, not just booked ones.
        List<Movie> allMovies = movieService.getMovies();

        ComboBox<Movie> moviePicker = new ComboBox<>();
        moviePicker.setPromptText("Choose a movie");
        moviePicker.getItems().addAll(allMovies);
        moviePicker.getStyleClass().add("movie-picker");
        moviePicker.setStyle("-fx-font-size: 14px;");
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
            refreshPoster();
        });

        // LEFT column: picker + reviews + form
        VBox leftColumn = new VBox(18, moviePicker, detailBox);
        leftColumn.setAlignment(Pos.TOP_LEFT);
        leftColumn.setPrefWidth(560);
        leftColumn.setMinWidth(560);

        // RIGHT column: poster panel
        posterPanel.setAlignment(Pos.TOP_CENTER);
        posterPanel.setPadding(new Insets(0, 0, 0, 40));
        HBox.setHgrow(posterPanel, Priority.ALWAYS);
        showPosterPlaceholder();

        HBox columns = new HBox(leftColumn, posterPanel);
        columns.setAlignment(Pos.TOP_LEFT);

        VBox page = new VBox(18, backBar, heading, sub, columns);
        page.setPadding(new Insets(24));
        page.setAlignment(Pos.TOP_LEFT);
        page.getStyleClass().add("page");

        detailBox.setVisible(false);
        detailBox.setManaged(false);

        return page;
    }

    private void loadBookedMovieIds() {
        try {
            List<Movie> booked = bookedMoviesRepository.findBookedMovies(username);
            for (Movie m : booked) {
                bookedMovieIds.add(m.getMovieId());
            }
        } catch (SQLException e) {
            System.err.println("Failed to load booked movies: " + e.getMessage());
        }
    }

    // ----- RIGHT PANEL: poster + details -----

    private void showPosterPlaceholder() {
        posterPanel.getChildren().clear();
        Label hint = new Label("Select a movie to see details");
        hint.getStyleClass().add("movie-details");
        posterPanel.getChildren().add(hint);
    }

    private void refreshPoster() {
        posterPanel.getChildren().clear();
        if (selectedMovie == null) {
            showPosterPlaceholder();
            return;
        }

        double w = 300;
        double h = 450;

        ImageView poster = new ImageView();
        boolean loaded = false;
        if (selectedMovie.getPosterFilename() != null) {
            var posterStream = getClass().getResourceAsStream("/posters/" + selectedMovie.getPosterFilename());
            if (posterStream != null) {
                poster.setImage(new Image(posterStream));
                poster.setPreserveRatio(true);
                poster.setFitWidth(w);
                poster.setFitHeight(h);
                loaded = true;
            }
        }

        StackPane posterBox = new StackPane();
        posterBox.setPrefSize(w, h);
        posterBox.setMinSize(w, h);
        posterBox.setMaxSize(w, h);
        if (loaded) {
            Rectangle clip = new Rectangle(w, h);
            posterBox.setClip(clip);
            posterBox.getChildren().add(poster);
        } else {
            Label ph = new Label(selectedMovie.getTitle());
            ph.getStyleClass().add("blank-movie-text");
            posterBox.getStyleClass().add("blank-movie-image");
            posterBox.getChildren().add(ph);
        }

        Label title = new Label(selectedMovie.getTitle());
        title.getStyleClass().add("section-title");

        Label details = new Label(selectedMovie.getDetailsLabel());
        details.getStyleClass().add("movie-details");

        Label release = new Label(selectedMovie.getReleaseDateLabel());
        release.getStyleClass().add("movie-details");

        VBox info = new VBox(6, title, details, release);
        info.setAlignment(Pos.CENTER);

        posterPanel.getChildren().addAll(posterBox, info);
    }

    // ----- LEFT PANEL: reviews + (gated) form -----

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
            averageLabel.setText("No reviews yet");
        } else {
            averageLabel.setText(String.format("\u2605 %.1f  (%d review%s)",
                    avg, count, count == 1 ? "" : "s"));
        }

        // Existing reviews (everyone can see these)
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

        detailBox.getChildren().addAll(
                averageLabel,
                labeled("Reviews"),
                reviewList
        );

        // The write form only appears if the user has booked this movie.
        if (bookedMovieIds.contains(selectedMovie.getMovieId())) {
            addWriteForm();
        } else {
            Label locked = new Label("\uD83C\uDFAB Book a ticket for this movie to leave a review.");
            locked.getStyleClass().add("movie-details");
            detailBox.getChildren().add(locked);
        }
    }

    // Builds and adds the star + text + submit form.
    private void addWriteForm() {
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
                formHeading,
                starRow,
                reviewTextArea,
                formError,
                submit
        );
    }

    private void buildStarRow() {
        starRow.getChildren().clear();
        starRow.setAlignment(Pos.CENTER_LEFT);
        for (int i = 1; i <= 5; i++) {
            final int value = i;
            Button star = new Button("\u2606");
            star.getStyleClass().add("star-button");
            star.setOnAction(e -> {
                selectedRating = value;
                paintStars();
            });
            starRow.getChildren().add(star);
        }
    }

    private void paintStars() {
        for (int i = 0; i < starRow.getChildren().size(); i++) {
            Button star = (Button) starRow.getChildren().get(i);
            star.setText(i < selectedRating ? "\u2605" : "\u2606");
        }
    }

    private void handleSubmit() {
        if (selectedMovie == null) {
            return;
        }
        // Safety: never save if the user hasn't booked this movie.
        if (!bookedMovieIds.contains(selectedMovie.getMovieId())) {
            showFormError("You need a ticket for this movie to review it.");
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

    private final ReviewValidator reviewValidator = new ReviewValidator();
    private String filledStars(int rating) {
        return reviewValidator.filledStars(rating);
    }

    private Label labeled(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("section-title");
        return l;
    }
}