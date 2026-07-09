package com.movieapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ReceiptConfirmationView {

    private static final DateTimeFormatter RECEIPT_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.US);

    private final Movie movie;
    private final Booking booking;
    private final boolean emailSent;
    private final Runnable onBackToMovies;

    public ReceiptConfirmationView(Movie movie,
                                   Booking booking,
                                   boolean emailSent,
                                   Runnable onBackToMovies) {
        this.movie = movie;
        this.booking = booking;
        this.emailSent = emailSent;
        this.onBackToMovies = onBackToMovies;
    }

    public Parent createView() {
        Label heading = new Label("Booking Confirmed");
        heading.getStyleClass().add("page-title");

        Label subtitle = new Label("Your receipt");
        subtitle.getStyleClass().add("section-title");

        String bookedAt = booking.getBookedAt() != null
                ? booking.getBookedAt().format(RECEIPT_TIME_FORMAT)
                : "N/A";

        VBox receipt = new VBox(8,
                receiptRow("Booking ID", String.valueOf(booking.getBookingId())),
                receiptRow("Movie", movie.getTitle()),
                receiptRow("Showtime", booking.getShowtime()),
                receiptRow("Seat(s)", booking.getSeats()),
                receiptRow("Payment Method", booking.getPaymentDisplay()),
                receiptRow("Email", booking.getEmail()),
                receiptRow("Time", bookedAt)
        );
        receipt.getStyleClass().add("auth-panel");
        receipt.setMaxWidth(480);
        receipt.setPadding(new Insets(20));

        Label emailStatus = new Label(emailSent
                ? "A copy was sent to " + booking.getEmail()
                : "Booking saved, but the email could not be sent. Check that Mailpit is running.");
        emailStatus.getStyleClass().add(emailSent ? "movie-details" : "auth-error");
        emailStatus.setWrapText(true);
        emailStatus.setMaxWidth(480);

        Button back = new Button("Back to Movies");
        back.getStyleClass().add("ticket-button");
        back.setOnAction(e -> {
            if (onBackToMovies != null) onBackToMovies.run();
        });

        VBox page = new VBox(18, heading, subtitle, receipt, emailStatus, back);
        page.setPadding(new Insets(24));
        page.setAlignment(Pos.TOP_CENTER);
        page.getStyleClass().add("page");
        return page;
    }

    private Label receiptRow(String label, String value) {
        Label row = new Label(label + ": " + value);
        row.getStyleClass().add("movie-details");
        row.setWrapText(true);
        return row;
    }
}
