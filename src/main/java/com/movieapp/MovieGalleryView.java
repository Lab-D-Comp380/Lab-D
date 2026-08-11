package com.movieapp;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MovieGalleryView {

    private static final String ALL_GENRES = "All Genres";

    private final MovieService movieService;
    private final Consumer<Movie> onMovieChosen;
    public MovieGalleryView(MovieService movieService, Consumer<Movie> onMovieChosen) {
        this.movieService = movieService;
        this.onMovieChosen = onMovieChosen;
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
            if (onMovieChosen != null) {
                onMovieChosen.accept(movie);
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

        Label genreLabel = new Label("Genre");
        genreLabel.getStyleClass().add("field-label");

        ComboBox<String> genreFilter = new ComboBox<>();
        genreFilter.getStyleClass().add("movie-picker");
        genreFilter.setPrefWidth(200);
        genreFilter.setItems(FXCollections.observableArrayList(buildGenreOptions(movies)));
        genreFilter.setValue(ALL_GENRES);

        HBox cards = new HBox(16);
        cards.setAlignment(Pos.TOP_LEFT);

        Runnable refreshCards = () -> populateCards(cards, movies, genreFilter.getValue());
        genreFilter.setOnAction(e -> refreshCards.run());
        refreshCards.run();

        HBox filterRow = new HBox(12, genreLabel, genreFilter);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        VBox page = new VBox(
                18,
                pageTitle,
                sectionTitle,
                filterRow,
                cards
        );

        page.setSpacing(18);
        page.getStyleClass().add("page");

        return page;
    }

    private List<String> buildGenreOptions(List<Movie> movies) {
        Set<String> genres = new LinkedHashSet<>();
        genres.add(ALL_GENRES);
        for (Movie movie : movies) {
            if (movie.getGenre() != null && !movie.getGenre().isBlank()) {
                genres.add(movie.getGenre());
            }
        }
        return new ArrayList<>(genres);
    }

    private void populateCards(HBox cards, List<Movie> movies, String selectedGenre) {
        cards.getChildren().clear();

        List<Movie> visible = movies.stream()
                .filter(movie -> matchesGenre(movie, selectedGenre))
                .toList();

        for (Movie movie : visible) {
            cards.getChildren().add(createMovieCard(movie));
        }

        if (visible.isEmpty()) {
            Label emptyLabel = new Label(
                    ALL_GENRES.equals(selectedGenre) || selectedGenre == null
                            ? "No movies available."
                            : "No movies in this genre."
            );
            emptyLabel.getStyleClass().add("movie-details");
            cards.getChildren().add(emptyLabel);
        }
    }

    private boolean matchesGenre(Movie movie, String selectedGenre) {
        if (selectedGenre == null || ALL_GENRES.equals(selectedGenre)) {
            return true;
        }
        return selectedGenre.equalsIgnoreCase(movie.getGenre());
    }
}
