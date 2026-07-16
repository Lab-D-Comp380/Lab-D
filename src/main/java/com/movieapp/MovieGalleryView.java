package com.movieapp;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class MovieGalleryView {

    private final MovieService movieService;
    private final Consumer<Movie> onMovieSelected;

    public MovieGalleryView(MovieService movieService, Consumer<Movie> onMovieSelected) {
        this.movieService = movieService;
        this.onMovieSelected = onMovieSelected;
    }

    private VBox createMovieCard(Movie movie) {
        ImageView poster = new ImageView();
        if (movie.getPosterFilename() != null) {
            var posterStream = getClass().getResourceAsStream("/posters/" + movie.getPosterFilename());
            if (posterStream != null) {
                poster.setImage(new Image(posterStream));
            }
        }

        poster.setFitWidth(220);
        poster.setFitHeight(330);

        Label titleLabel = new Label(movie.getTitle());
        titleLabel.getStyleClass().add("movie-title");

        Label detailsLabel = new Label(movie.getDetailsLabel());
        detailsLabel.getStyleClass().add("movie-details");

        Label releaseLabel = new Label(movie.getReleaseDateLabel());
        releaseLabel.getStyleClass().add("movie-details");

        Button selectButton = new Button("Select Movie");
        selectButton.getStyleClass().add("ticket-button");
        selectButton.setOnAction(event -> {
            if (onMovieSelected != null) {
                onMovieSelected.accept(movie);
            }
        });

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

        List<Movie> movies = movieService.getMovies();
        HBox cards = new HBox(16);
        cards.setAlignment(Pos.TOP_LEFT);

        for (Movie movie : movies) {
            cards.getChildren().add(createMovieCard(movie));
        }

        if (movies.isEmpty()) {
            Label emptyLabel = new Label("No movies available.");
            emptyLabel.getStyleClass().add("movie-details");
            cards.getChildren().add(emptyLabel);
        }

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
