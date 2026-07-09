package com.movieapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ShowtimeApp extends Application {

    @Override
    public void start(Stage stage) {
        Movie sample = new Movie(1, "Skybound", "Action", 108, "PG-13", LocalDate.of(2026, 6, 24), "skybound.png");
        ShowtimeSelectionView view = new ShowtimeSelectionView(sample, (theater, time) -> {}, () -> {});

        Scene scene = new Scene(view.createView(), 1200, 800);

        var cssUrl = getClass().getResource("/com/movieapp/showtime.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setTitle("Showtime Selection");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
