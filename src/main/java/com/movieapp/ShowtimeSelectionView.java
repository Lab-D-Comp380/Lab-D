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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ShowtimeSelectionView {

    private final Movie movie;
    private final Consumer<String> onShowtimeSelected;
    private final Runnable onBack;

    private static final String[] SHOWTIMES = {"12:45pm", "3:30pm", "6:20pm", "9:15pm"};

    public ShowtimeSelectionView(Movie movie,
                                 Consumer<String> onShowtimeSelected,
                                 Runnable onBack) {
        this.movie = movie;
        this.onShowtimeSelected = onShowtimeSelected;
        this.onBack = onBack;
    }

    private Button createTimeButton(String time, List<Button> allButtons) {
        Button button = new Button(time);
        button.getStyleClass().add("time-button");

        button.setOnAction(event -> {
            for (Button other : allButtons) {
                if (other != button) {
                    String original = (String) other.getUserData();
                    other.setText(original);
                    other.getStyleClass().remove("time-button-selected");
                }
            }
            button.setText("Selected");
            button.getStyleClass().add("time-button-selected");
            if (onShowtimeSelected != null) {
                onShowtimeSelected.accept(time);
            }
        });

        button.setUserData(time);
        return button;
    }

    private StackPane createMovieImage(double width, double height) {
        ImageView imageView = new ImageView();
        if (movie.getPosterFilename() != null) {
            var posterStream = getClass().getResourceAsStream("/posters/" + movie.getPosterFilename());
            if (posterStream != null) {
                imageView.setImage(new Image(posterStream));
            }
        }
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);

        StackPane box = new StackPane(imageView);
        box.setPrefSize(width, height);
        box.setMinSize(width, height);
        box.setMaxSize(width, height);
        box.getStyleClass().add("blank-movie-image");
        return box;
    }

    public Parent createView() {
        Button back = new Button("\u2190 Back");
        back.getStyleClass().add("auth-link");
        back.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });
        HBox backBar = new HBox(back);
        backBar.setAlignment(Pos.CENTER_LEFT);
        backBar.setPadding(new Insets(18, 18, 0, 18));

        HBox topBar = new HBox(28);
        topBar.setPadding(new Insets(18));
        topBar.setAlignment(Pos.CENTER);

        Label theaterFilter = new Label("📍 Theater");
        Label dateFilter = new Label("📅 Today");
        Label movieFilter = new Label("🎞 " + movie.getTitle());
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

        StackPane smallMovieImage = createMovieImage(75, 75);

        Label titleLabel = new Label(movie.getTitle());
        titleLabel.getStyleClass().add("showtime-title");

        Label detailsLabel = new Label(movie.getDetailsLabel());
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

        List<Button> timeButtons = new ArrayList<>();
        for (String time : SHOWTIMES) {
            timeButtons.add(createTimeButton(time, timeButtons));
        }

        HBox times = new HBox(14);
        times.getChildren().addAll(timeButtons);

        VBox leftSide = new VBox(
                24,
                notice,
                movieHeader,
                theaterTitle,
                new VBox(6, formatLabel, features, times));

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

        Label movieInfo = new Label("🎞 Movie Info  >");
        movieInfo.getStyleClass().add("movie-info-text");

        VBox rightSide = new VBox(
                24,
                bigMovieImage,
                rightTitle,
                rightDetails,
                rightDivider,
                movieInfo);

        rightSide.setPadding(new Insets(35, 45, 35, 20));
        rightSide.setPrefWidth(480);

        HBox mainContent = new HBox(leftSide, rightSide);

        VBox page = new VBox(backBar, topBar, promoBar, mainContent);
        page.getStyleClass().add("showtime-page");

        return page;
    }
}
