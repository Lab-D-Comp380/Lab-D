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

import java.util.function.BiConsumer;

public class ShowtimeSelectionView {

    private final Movie movie;
    private final BiConsumer<String, String> onShowtimeSelected;
    private final Runnable onBack;

    public ShowtimeSelectionView(Movie movie,
                                 BiConsumer<String, String> onShowtimeSelected,
                                 Runnable onBack) {
        this.movie = movie;
        this.onShowtimeSelected = onShowtimeSelected;
        this.onBack = onBack;
    }

    private Button createTimeButton(String theater, String time) {
        Button button = new Button(time);
        button.getStyleClass().add("time-button");
        button.setOnAction(event -> {
            if (onShowtimeSelected != null) {
                onShowtimeSelected.accept(theater, time);
            }
        });
        return button;
    }

    private StackPane createMovieImage(double width, double height) {
        StackPane box = new StackPane();
        box.setPrefSize(width, height);
        box.setMinSize(width, height);
        box.setMaxSize(width, height);
        box.getStyleClass().add("blank-movie-image");

        if (movie.getPosterFilename() != null) {
            var posterStream = getClass().getResourceAsStream("/posters/" + movie.getPosterFilename());
            if (posterStream != null) {
                ImageView poster = new ImageView(new Image(posterStream));
                poster.setFitWidth(width);
                poster.setFitHeight(height);
                poster.setPreserveRatio(true);
                box.getChildren().add(poster);
                return box;
            }
        }

        Label movieLabel = new Label(movie.getTitle());
        movieLabel.getStyleClass().add("blank-movie-text");
        box.getChildren().add(movieLabel);
        return box;
    }

    public Parent createView() {
        Button back = new Button("\u2190 Back");
        back.getStyleClass().add("auth-link");
        back.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        HBox topBar = new HBox(28);
        topBar.setPadding(new Insets(18));
        topBar.setAlignment(Pos.CENTER);

        Label theaterFilter = new Label("\ud83d\udccd Theater");
        Label dateFilter = new Label("\ud83d\udcc5 Today");
        Label movieFilter = new Label("\ud83c\udfac " + movie.getTitle());
        Label offeringFilter = new Label("\u2637 Premium Offerings");

        theaterFilter.getStyleClass().add("filter-text");
        dateFilter.getStyleClass().add("filter-text");
        movieFilter.getStyleClass().add("filter-text");
        offeringFilter.getStyleClass().add("filter-text");

        topBar.getChildren().addAll(theaterFilter, dateFilter, movieFilter, offeringFilter);
        topBar.getStyleClass().add("top-bar");

        Label promoBar = new Label("\ud83c\udff7 Members save on tickets today   Sign In or Join");
        promoBar.getStyleClass().add("promo-bar");
        promoBar.setMaxWidth(Double.MAX_VALUE);

        Label notice = new Label("\ud83c\udfac Movies start 25-30 minutes after showtime.");
        notice.getStyleClass().add("notice-text");

        StackPane smallMovieImage = createMovieImage(75, 75);

        Label titleLabel = new Label(movie.getTitle());
        titleLabel.getStyleClass().add("showtime-title");

        Label detailsLabel = new Label(movie.getDetailsLabel());
        detailsLabel.getStyleClass().add("showtime-details");

        VBox movieText = new VBox(6, titleLabel, detailsLabel);

        HBox movieHeader = new HBox(18, smallMovieImage, movieText);
        movieHeader.setAlignment(Pos.CENTER_LEFT);

        String mainTheater = "Main Theater";
        Label theaterTitle = new Label("\ud83d\udccd " + mainTheater);
        theaterTitle.getStyleClass().add("theater-title");

        Label formatLabel = new Label("DIGITAL");
        formatLabel.getStyleClass().add("format-title");

        Label features = new Label("Reserved Seating     Closed Caption     Audio Description");
        features.getStyleClass().add("feature-text");

        HBox times = new HBox(
                14,
                createTimeButton(mainTheater, "12:45pm"),
                createTimeButton(mainTheater, "3:30pm"),
                createTimeButton(mainTheater, "6:20pm"),
                createTimeButton(mainTheater, "9:15pm"));

        Label nearby = new Label("NEARBY THEATRES");
        nearby.getStyleClass().add("nearby-title");

        Label line = new Label("");
        line.getStyleClass().add("divider-line");
        line.setMaxWidth(Double.MAX_VALUE);

        String nearbyTheater = "Nearby Theater";
        Label secondTheater = new Label("\ud83d\udccd " + nearbyTheater);
        secondTheater.getStyleClass().add("theater-title");

        Label primeLabel = new Label("PREMIUM SHOWING");
        primeLabel.getStyleClass().add("format-title");

        Label secondFeatures = new Label("Reserved Seating     Closed Caption     Audio Description");
        secondFeatures.getStyleClass().add("feature-text");

        HBox secondTimes = new HBox(
                14,
                createTimeButton(nearbyTheater, "1:15pm"),
                createTimeButton(nearbyTheater, "4:45pm"));

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

        Label rightTitle = new Label(movie.getTitle());
        rightTitle.getStyleClass().add("right-title");

        Label rightDetails = new Label(movie.getDetailsLabel());
        rightDetails.getStyleClass().add("showtime-details");

        Label rightDivider = new Label("");
        rightDivider.getStyleClass().add("right-divider");
        rightDivider.setMaxWidth(Double.MAX_VALUE);

        Label movieInfo = new Label("\ud83c\udfac Movie Info  >");
        movieInfo.getStyleClass().add("movie-info-text");

        Label criticScore = new Label("Score\nCritics");
        criticScore.getStyleClass().add("score-text");

        Label audienceScore = new Label("Score\nAudience");
        audienceScore.getStyleClass().add("score-text");

        HBox scores = new HBox(28, movieInfo, criticScore, audienceScore);
        scores.setAlignment(Pos.CENTER_LEFT);

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

        VBox page = new VBox(new HBox(back), topBar, promoBar, mainContent);
        page.getStyleClass().add("showtime-page");

        return page;
    }
}
