package com.movieapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // A label (just text on screen)
        Label title = new Label("Movie Tickets");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: #f4b942; -fx-font-weight: bold;");

        // A button you can click
        Button button = new Button("Click me");
        button.setOnAction(e -> title.setText("You clicked it!"));

        // A vertical box that stacks the label and button, centered
        VBox layout = new VBox(20, title, button);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #16131c;");

        // Put it all in a window and show it
        Scene scene = new Scene(layout, 600, 400);
        stage.setTitle("Movie Tickets");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}