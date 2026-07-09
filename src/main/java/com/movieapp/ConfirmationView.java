package com.movieapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ConfirmationView {

    private final BookingReceipt receipt;
    private final Runnable onBackToMovies;

    public ConfirmationView(BookingReceipt receipt, Runnable onBackToMovies) {
        this.receipt = receipt;
        this.onBackToMovies = onBackToMovies;
    }

    public Parent createView() {
        Label heading = new Label("Booking Confirmed!");
        heading.getStyleClass().add("page-title");

        Label subtitle = new Label("A copy of your receipt has been sent to " + receipt.email());
        subtitle.getStyleClass().add("movie-details");
        subtitle.setWrapText(true);

        VBox receiptCard = new VBox(10,
                receiptRow("Confirmation #", String.valueOf(receipt.bookingId())),
                receiptRow("Movie", receipt.movieTitle()),
                receiptRow("Details", receipt.movieDetails()),
                receiptRow("Theater", receipt.theater()),
                receiptRow("Showtime", receipt.showtime()),
                receiptRow("Seats", String.join(", ", receipt.seats())),
                receiptRow("Payment", formatPayment()),
                receiptRow("Email", receipt.email())
        );
        receiptCard.getStyleClass().add("receipt-card");
        receiptCard.setMaxWidth(520);

        Button backButton = new Button("Back to Movies");
        backButton.getStyleClass().add("ticket-button");
        backButton.setOnAction(e -> {
            if (onBackToMovies != null) {
                onBackToMovies.run();
            }
        });

        VBox page = new VBox(20, heading, subtitle, receiptCard, backButton);
        page.setPadding(new Insets(24));
        page.setAlignment(Pos.TOP_CENTER);
        page.getStyleClass().add("page");
        return page;
    }

    private HBox receiptRow(String label, String value) {
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("receipt-label");
        labelNode.setMinWidth(120);

        Label valueNode = new Label(value);
        valueNode.getStyleClass().add("receipt-value");
        valueNode.setWrapText(true);

        HBox row = new HBox(12, labelNode, valueNode);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private String formatPayment() {
        if (receipt.cardLastFour() != null && !receipt.cardLastFour().isBlank()) {
            return receipt.paymentMethod() + " (****" + receipt.cardLastFour() + ")";
        }
        return receipt.paymentMethod();
    }
}
