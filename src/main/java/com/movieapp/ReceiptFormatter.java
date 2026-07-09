package com.movieapp;

public final class ReceiptFormatter {

    private ReceiptFormatter() {
    }

    public static String format(BookingReceipt receipt) {
        String paymentLine = receipt.paymentMethod();
        if (receipt.cardLastFour() != null && !receipt.cardLastFour().isBlank()) {
            paymentLine += " (****" + receipt.cardLastFour() + ")";
        }

        return """
                BOOKING CONFIRMATION #%d

                Movie:    %s
                Details:  %s
                Theater:  %s
                Showtime: %s
                Seats:    %s

                Payment:  %s
                Email:    %s
                Account:  %s

                Thank you for your purchase!
                """.formatted(
                receipt.bookingId(),
                receipt.movieTitle(),
                receipt.movieDetails(),
                receipt.theater(),
                receipt.showtime(),
                String.join(", ", receipt.seats()),
                paymentLine,
                receipt.email(),
                receipt.username()
        );
    }
}
