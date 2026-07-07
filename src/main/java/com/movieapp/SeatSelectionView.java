package com.movieapp;

import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/*Seat selection screen — a clickable cinema seat grid.*/
public class SeatSelectionView {

    private final Movie movie;
    private final String showtime;
    private final Consumer<List<String>> onSeatsChosen;
    private final Runnable onBack;

    private final List<String> selectedSeats = new ArrayList<>();

    // Grid size.
    private static final int ROWS = 6;
    private static final int COLS = 8;

    // Pre-taken seats (row, col) — visual only.
    private static final int[][] TAKEN = {
        {0, 2}, {0, 3}, {1, 5}, {2, 1}, {3, 6}, {3, 7}, {4, 0}, {5, 4}
    };

    public SeatSelectionView(Movie movie,
                             String showtime,
                             Consumer<List<String>> onSeatsChosen,
                             Runnable onBack) {
        this.movie = movie;
        this.showtime = showtime;
        this.onSeatsChosen = onSeatsChosen;
        this.onBack = onBack;
    }

    public Parent createView() {
        // Back link
        Button back = new Button("\u2190 Back");
        back.getStyleClass().add("auth-link");
        back.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });
        HBox backBar = new HBox(back);
        backBar.setAlignment(Pos.CENTER_LEFT);

        // Headings
        Label heading = new Label("Pick Your Seats");
        heading.getStyleClass().add("page-title");

        String subText = movie.getTitle();
        if (showtime != null && !showtime.isBlank()) {
            subText += "  \u2022  " + showtime;
        }
        Label sub = new Label(subText);
        sub.getStyleClass().add("movie-details");

        // Curved "SCREEN" arc
        Arc arc = new Arc(0, 0, 300, 55, 20, 140);  // centerX, centerY, radiusX, radiusY, start angle, length
        arc.setType(ArcType.OPEN);
        arc.setFill(null);
        arc.setStroke(javafx.scene.paint.Color.web("#6c7378"));
        arc.setStrokeWidth(2);

        Label screenLabel = new Label("S C R E E N");
        screenLabel.getStyleClass().add("screen-banner");

        StackPane screen = new StackPane(arc, screenLabel);
        StackPane.setAlignment(screenLabel, Pos.BOTTOM_CENTER);
        screen.setMaxWidth(Double.MAX_VALUE);

        // Live summary + continue button
        Label summary = new Label("No seats selected");
        summary.getStyleClass().add("movie-details");

        Button continueBtn = new Button("Continue");
        continueBtn.getStyleClass().add("ticket-button");
        continueBtn.setDisable(true);
        continueBtn.setOnAction(e -> {
            if (onSeatsChosen != null) {
                onSeatsChosen.accept(new ArrayList<>(selectedSeats));
            }
        });

        // Build the seat grid
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                String seatId = seatName(r, c);
                Button seat = new Button();
                seat.setPrefSize(56, 56);

                if (isTaken(r, c)) {
                    seat.getStyleClass().add("seat-taken");
                    seat.setDisable(true);
                } else {
                    seat.getStyleClass().add("seat");
                    seat.setOnAction(e -> {
                        toggleSeat(seatId, seat);
                        updateSummary(summary, continueBtn);
                    });
                }
                grid.add(seat, c, r);
            }
        }

        HBox footer = new HBox(16, summary, spacer(), continueBtn);
        footer.setAlignment(Pos.CENTER_LEFT);

        // Spacer 
        Region pushDown = new Region();
        VBox.setVgrow(pushDown, javafx.scene.layout.Priority.ALWAYS);

        VBox page = new VBox(18, backBar, heading, sub, screen, grid, legend(), pushDown, footer);
        page.setPadding(new Insets(24));
        page.setAlignment(Pos.TOP_CENTER);
        page.getStyleClass().add("page");
        return page;
    }

    // Select or deselect a seat.
    private void toggleSeat(String seatId, Button seat) {
        if (selectedSeats.contains(seatId)) {
            selectedSeats.remove(seatId);
            seat.getStyleClass().remove("seat-selected");
        } else {
            selectedSeats.add(seatId);
            seat.getStyleClass().add("seat-selected");
        }
    }

    // Refresh the summary text and enable/disable Continue.
    private void updateSummary(Label summary, Button continueBtn) {
        if (selectedSeats.isEmpty()) {
            summary.setText("No seats selected");
            continueBtn.setDisable(true);
        } else {
            summary.setText("Selected: " + String.join(", ", selectedSeats)
                + "   (" + selectedSeats.size() + ")");
            continueBtn.setDisable(false);
        }
    }

    // A legend explaining seat colors.
    private HBox legend() {
        HBox row = new HBox(18,
            legendItem("seat", "Available"),
            legendItem("seat-selected", "Selected"),
            legendItem("seat-taken", "Taken"));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 0, 6, 0));
        return row;
    }

    private HBox legendItem(String styleClass, String text) {
        Region swatch = new Region();
        swatch.setPrefSize(18, 18);
        swatch.getStyleClass().add(styleClass);
        Label l = new Label(text);
        l.getStyleClass().add("movie-details");
        HBox item = new HBox(6, swatch, l);
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
    }

    private Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, javafx.scene.layout.Priority.ALWAYS);
        return r;
    }

    // Seat labels: row letter + seat number
    private String seatName(int row, int col) {
        char rowLetter = (char) ('A' + row);
        return "" + rowLetter + (col + 1);
    }

    private boolean isTaken(int row, int col) {
        for (int[] t : TAKEN) {
            if (t[0] == row && t[1] == col) return true;
        }
        return false;
    }
}