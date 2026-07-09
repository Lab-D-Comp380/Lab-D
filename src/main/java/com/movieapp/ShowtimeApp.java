package com.movieapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ShowtimeApp extends Application {

    @Override
    public void start(Stage stage) {
        ShowtimeSelectionView view = new ShowtimeSelectionView();

        Scene scene = new Scene(view.createView(), 1200, 800);

        scene.getStylesheets().add(
                getClass().getResource("/showtime.css").toExternalForm());

        stage.setTitle("Showtime Selection");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}