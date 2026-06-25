package com.movieapp;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MovieGalleryView {

    private VBox createMovieCard(
            String title,
            String details,
            String releaseDate,
            String posterFile
    ) {
       ImageView poster = new ImageView();
var posterStream = getClass().getResourceAsStream("/posters/" + posterFile);
if (posterStream != null) {
    poster.setImage(new Image(posterStream));
}

        poster.setFitWidth(220);
        poster.setFitHeight(330);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("movie-title");

        Label detailsLabel = new Label(details);
        detailsLabel.getStyleClass().add("movie-details");

        Label releaseLabel = new Label("Released " + releaseDate);
        releaseLabel.getStyleClass().add("movie-details");

        Button selectButton = new Button("Select Movie");
        selectButton.getStyleClass().add("ticket-button");

        selectButton.setOnAction(event ->
                selectButton.setText("Selected")
        );

        VBox card = new VBox(
                8,
                poster,
                titleLabel,
                detailsLabel,
                releaseLabel,
                selectButton
        );

        card.setPrefWidth(220);
        card.getStyleClass().add("movie-card");

        return card;
    }

    public Parent createView() {
        Label pageTitle = new Label("Movies");
        pageTitle.getStyleClass().add("page-title");

        Label sectionTitle = new Label("Featured Movies");
        sectionTitle.getStyleClass().add("section-title");

        HBox cards = new HBox(
                16,
                createMovieCard(
                        "Skybound",
                        "1 HR 48 MIN | PG-13",
                        "June 24, 2026",
                        "skybound.png"
                ),
                createMovieCard(
                        "Pixel Quest",
                        "1 HR 42 MIN | PG",
                        "June 19, 2026",
                        "pixelquest.png"
                ),
                createMovieCard(
                        "Echo Point",
                        "2 HR 05 MIN | PG-13",
                        "June 12, 2026",
                        "echopoint.png"
                ),
                createMovieCard(
                        "Midnight Signal",
                        "1 HR 51 MIN | R",
                        "May 15, 2026",
                        "midnightsignal.png"
                ),
                createMovieCard(
                        "The Last Orbit",
                        "1 HR 36 MIN | PG-13",
                        "June 19, 2026",
                        "lastorbit.png"
                )
        );

        cards.setAlignment(Pos.TOP_LEFT);

        VBox page = new VBox(
                18,
                pageTitle,
                sectionTitle,
                cards
        );

        page.setSpacing(18);
        page.getStyleClass().add("page");

        return page;
    }
}
