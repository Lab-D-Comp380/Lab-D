package com.movieapp;

import javafx.concurrent.Task;

import java.sql.SQLException;
import java.util.Optional;

public class BookingService {

    private final BookingRepository bookingRepository = new BookingRepository();
    private final EmailSender emailSender;

    public BookingService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public Optional<BookingReceipt> completePurchase(PurchaseSession session) {
        try {
            Booking booking = new Booking(
                    0,
                    session.getUsername(),
                    session.getMovie().getMovieId(),
                    session.getSeats().size(),
                    session.getTheater(),
                    session.getShowtime(),
                    String.join(", ", session.getSeats()),
                    session.getPaymentMethod(),
                    session.getCardLastFour()
            );

            int bookingId = bookingRepository.createBooking(booking);

            BookingReceipt receipt = new BookingReceipt(
                    bookingId,
                    session.getUsername(),
                    session.getMovie().getTitle(),
                    session.getMovie().getDetailsLabel(),
                    session.getTheater(),
                    session.getShowtime(),
                    session.getSeats(),
                    session.getPaymentMethod(),
                    session.getCardLastFour(),
                    session.getEmail()
            );

            sendReceiptEmail(receipt);
            return Optional.of(receipt);

        } catch (SQLException e) {
            System.err.println("Failed to complete purchase: " + e.getMessage());
            return Optional.empty();
        }
    }

    private void sendReceiptEmail(BookingReceipt receipt) {
        Task<Void> emailTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                emailSender.sendReceipt(
                        receipt.email(),
                        "Your ticket confirmation #" + receipt.bookingId(),
                        ReceiptFormatter.format(receipt)
                );
                return null;
            }
        };

        emailTask.setOnFailed(e -> {
            System.err.println("Receipt email failed: " + emailTask.getException().getMessage());
            System.err.println("View the on-screen receipt or check Mailpit at http://localhost:8025");
        });

        new Thread(emailTask).start();
    }
}
