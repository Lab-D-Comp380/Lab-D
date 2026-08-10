package com.movieapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.function.Consumer;

public class ShowtimeSelectionView {

    private final Movie movie;
    private final ReviewService reviewService;
    private final Consumer<String> onShowtimeChosen;
    private final Runnable onBack;

    public ShowtimeSelectionView(Movie movie,
                                 ReviewService reviewService,
                                 Consumer<String> onShowtimeChosen,
                                 Runnable onBack) {
        this.movie = movie;
        this.reviewService = reviewService;
        this.onShowtimeChosen = onShowtimeChosen;
        this.onBack = onBack;
    }

    private Button createTimeButton(String time) {
        Button button = new Button(time);
        button.getStyleClass().add("time-button");
        button.setOnAction(event -> {
            if (onShowtimeChosen != null) {
                onShowtimeChosen.accept(time);
            }
        });
        return button;
    }

    // Poster if we have one, otherwise the styled blank placeholder.
    private StackPane createMovieImage(double width, double height) {
        if (movie != null && movie.getPosterFilename() != null) {
            var posterStream = getClass().getResourceAsStream("/posters/" + movie.getPosterFilename());
            if (posterStream != null) {
                ImageView poster = new ImageView(new Image(posterStream));
                poster.setPreserveRatio(true);   // don't stretch the poster
                poster.setFitWidth(width);
                poster.setFitHeight(height);

                StackPane box = new StackPane(poster);
                box.setPrefSize(width, height);
                box.setMinSize(width, height);
                box.setMaxSize(width, height);
                // Clip so a tall poster can't spill outside the box.
                Rectangle clip = new Rectangle(width, height);
                box.setClip(clip);
                return box;
            }
        }
        Label movieLabel = new Label(movie != null ? movie.getTitle() : "Movie");
        movieLabel.getStyleClass().add("blank-movie-text");
        StackPane box = new StackPane(movieLabel);
        box.setPrefSize(width, height);
        box.setMinSize(width, height);
        box.setMaxSize(width, height);
        box.getStyleClass().add("blank-movie-image");
        return box;
    }

    private String movieTitle() {
        return movie != null ? movie.getTitle() : "Movie";
    }

    private String movieDetails() {
        return movie != null ? movie.getDetailsLabel() : "2 HR 0 MIN | PG-13";
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
        backBar.setPadding(new Insets(12, 0, 0, 18));

        HBox topBar = new HBox(28);
        topBar.setPadding(new Insets(18));
        topBar.setAlignment(Pos.CENTER);

        Label theaterFilter = new Label("\uD83D\uDCCD Theater");
        Label dateFilter = new Label("\uD83D\uDCC5 Today");
        Label movieFilter = new Label("\uD83C\uDF9E Movie");
        Label offeringFilter = new Label("\u2637 Premium Offerings");

        theaterFilter.getStyleClass().add("filter-text");
        dateFilter.getStyleClass().add("filter-text");
        movieFilter.getStyleClass().add("filter-text");
        offeringFilter.getStyleClass().add("filter-text");

        topBar.getChildren().addAll(theaterFilter, dateFilter, movieFilter, offeringFilter);
        topBar.getStyleClass().add("top-bar");

        Label promoBar = new Label("\u2B50 Rate your movies after watching in \"My Reviews\"");
        promoBar.getStyleClass().add("promo-bar");
        promoBar.setMaxWidth(Double.MAX_VALUE);

        Label notice = new Label("\uD83C\uDF9E Movies start 25-30 minutes after showtime.");
        notice.getStyleClass().add("notice-text");

        StackPane smallMovieImage = createMovieImage(75, 75);

        Label titleLabel = new Label(movieTitle());
        titleLabel.getStyleClass().add("showtime-title");

        Label detailsLabel = new Label(movieDetails());
        detailsLabel.getStyleClass().add("showtime-details");

        VBox movieText = new VBox(6, titleLabel, detailsLabel);

        HBox movieHeader = new HBox(18, smallMovieImage, movieText);
        movieHeader.setAlignment(Pos.CENTER_LEFT);

        Label theaterTitle = new Label("\uD83D\uDCCD Main Theater");
        theaterTitle.getStyleClass().add("theater-title");

        Label formatLabel = new Label("DIGITAL");
        formatLabel.getStyleClass().add("format-title");

        Label features = new Label("Reserved Seating     Closed Caption     Audio Description");
        features.getStyleClass().add("feature-text");

        HBox times = new HBox(
                14,
                createTimeButton("12:45pm"),
                createTimeButton("3:30pm"),
                createTimeButton("6:20pm"),
                createTimeButton("9:15pm"));

        Label nearby = new Label("NEARBY THEATRES");
        nearby.getStyleClass().add("nearby-title");

        Label line = new Label("");
        line.getStyleClass().add("divider-line");
        line.setMaxWidth(Double.MAX_VALUE);

        Label secondTheater = new Label("\uD83D\uDCCD Nearby Theater");
        secondTheater.getStyleClass().add("theater-title");

        Label primeLabel = new Label("PREMIUM SHOWING");
        primeLabel.getStyleClass().add("format-title");

        Label secondFeatures = new Label("Reserved Seating     Closed Caption     Audio Description");
        secondFeatures.getStyleClass().add("feature-text");

        HBox secondTimes = new HBox(
                14,
                createTimeButton("1:15pm"),
                createTimeButton("4:45pm"));

        VBox leftSide = new VBox(
                24,
                notice,
                movieHeader,
                theaterTitle,
                new VBox(6, formatLabel, features, times),
                nearby,
                line,
                secondTheater,
                new VBox(6, primeLabel, secondFeatures, secondTimes));

        leftSide.setPadding(new Insets(35, 45, 35, 45));
        leftSide.setPrefWidth(760);

        StackPane bigMovieImage = createMovieImage(420, 240);

        Label rightTitle = new Label(movieTitle());
        rightTitle.getStyleClass().add("right-title");

        Label rightDetails = new Label(movieDetails());
        rightDetails.getStyleClass().add("showtime-details");

        Label rightDivider = new Label("");
        rightDivider.getStyleClass().add("right-divider");
        rightDivider.setMaxWidth(Double.MAX_VALUE);

        Label movieInfo = new Label("\uD83C\uDF9E Movie Info  >");
        movieInfo.getStyleClass().add("movie-info-text");

        HBox scores = new HBox(28, movieInfo);
        scores.setAlignment(Pos.CENTER_LEFT);

        // Real audience score from user reviews. Hidden when there are no reviews.
        if (movie != null && reviewService != null) {
            int count = reviewService.getReviewCount(movie.getMovieId());
            if (count > 0) {
                double avg = reviewService.getAverageRating(movie.getMovieId());
                Label audienceScore = new Label(String.format("\u2605 %.1f\nAudience", avg));
                audienceScore.getStyleClass().add("score-text");
                scores.getChildren().add(audienceScore);
            }
        }

        VBox rightSide = new VBox(
                24,
                bigMovieImage,
                rightTitle,
                rightDetails,
                rightDivider,
                scores);

        rightSide.setPadding(new Insets(35, 45, 35, 20));
        rightSide.setPrefWidth(480);

        HBox mainContent = new HBox(leftSide, rightSide);

        VBox page = new VBox(backBar, topBar, promoBar, mainContent);
        page.getStyleClass().add("showtime-page");

        return page;
    }
}