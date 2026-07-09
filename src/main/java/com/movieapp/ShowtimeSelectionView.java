
package com.movieapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ShowtimeSelectionView {

    public ShowtimeSelectionView() {
        // Keeping Movie here so your gallery button can still pass the selected movie,
        // but this screen will show blank placeholder info.
    }

    private Button createTimeButton(String time) {
        Button button = new Button(time);
        button.getStyleClass().add("time-button");

        button.setOnAction(event -> {
            button.setText("Selected");
        });

        return button;
    }

    private StackPane createBlankMovieImage(double width, double height) {
        Label movieLabel = new Label("Movie");
        movieLabel.getStyleClass().add("blank-movie-text");

        StackPane box = new StackPane(movieLabel);
        box.setPrefSize(width, height);
        box.setMinSize(width, height);
        box.setMaxSize(width, height);
        box.getStyleClass().add("blank-movie-image");

        return box;
    }

    public Parent createView() {
        HBox topBar = new HBox(28);
        topBar.setPadding(new Insets(18));
        topBar.setAlignment(Pos.CENTER);

        Label theaterFilter = new Label("📍 Theater");
        Label dateFilter = new Label("📅 Today");
        Label movieFilter = new Label("🎞 Movie");
        Label offeringFilter = new Label("☷ Premium Offerings");

        theaterFilter.getStyleClass().add("filter-text");
        dateFilter.getStyleClass().add("filter-text");
        movieFilter.getStyleClass().add("filter-text");
        offeringFilter.getStyleClass().add("filter-text");

        topBar.getChildren().addAll(theaterFilter, dateFilter, movieFilter, offeringFilter);
        topBar.getStyleClass().add("top-bar");

        Label promoBar = new Label("🏷 Members save on tickets today   Sign In or Join");
        promoBar.getStyleClass().add("promo-bar");
        promoBar.setMaxWidth(Double.MAX_VALUE);

        Label notice = new Label("🎞 Movies start 25-30 minutes after showtime.");
        notice.getStyleClass().add("notice-text");

        StackPane smallMovieImage = createBlankMovieImage(75, 75);

        Label titleLabel = new Label("Movie");
        titleLabel.getStyleClass().add("showtime-title");

        Label detailsLabel = new Label("2 HR 0 MIN | PG-13");
        detailsLabel.getStyleClass().add("showtime-details");

        VBox movieText = new VBox(6, titleLabel, detailsLabel);

        HBox movieHeader = new HBox(18, smallMovieImage, movieText);
        movieHeader.setAlignment(Pos.CENTER_LEFT);

        Label theaterTitle = new Label("📍 Main Theater");
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

        Label secondTheater = new Label("📍 Nearby Theater");
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

        StackPane bigMovieImage = createBlankMovieImage(420, 240);

        Label rightTitle = new Label("Movie");
        rightTitle.getStyleClass().add("right-title");

        Label rightDetails = new Label("2 HR 0 MIN | PG-13");
        rightDetails.getStyleClass().add("showtime-details");

        Label rightDivider = new Label("");
        rightDivider.getStyleClass().add("right-divider");
        rightDivider.setMaxWidth(Double.MAX_VALUE);

        Label movieInfo = new Label("🎞 Movie Info  >");
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

        VBox page = new VBox(topBar, promoBar, mainContent);
        page.getStyleClass().add("showtime-page");

        return page;
    }
}