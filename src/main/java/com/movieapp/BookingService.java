package com.movieapp;

import java.sql.SQLException;
import java.util.Optional;

import javafx.concurrent.Task;

public class BookingService {

    private final BookingRepository bookingRepository;
    private final EmailSender emailSender;
    private final SnackOrderService snackOrderService;

    // ---------- DEFAULT CONSTRUCTOR ----------
    public BookingService() {
        this(
            new BookingRepository(),
            new SnackOrderService(),
            new MockEmailService()
        );
    }

    // ---------- EMAIL CONSTRUCTOR ----------
    public BookingService(EmailSender emailSender) {
        this(
            new BookingRepository(),
            new SnackOrderService(),
            emailSender
        );
    }

    // ---------- TESTING CONSTRUCTOR ----------
    public BookingService(
        BookingRepository bookingRepository,
        SnackOrderService snackOrderService,
        EmailSender emailSender) {
            this.bookingRepository = bookingRepository;
            this.snackOrderService = snackOrderService;
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
            for (SnackOrder snackOrder : session.getSnackOrders()) {
                SnackOrder order = new SnackOrder(bookingId, snackOrder.getSnackId(), snackOrder.getQuantity());
                snackOrderService.saveSnackOrder(order);
            }    

            BookingReceipt receipt = new BookingReceipt(
                    bookingId,
                    session.getUsername(),
                    session.getMovie().getTitle(),
                    session.getMovie().getDetailsLabel(),
                    session.getTheater(),
                    session.getShowtime(),
                    new java.util.ArrayList<>(session.getSeats()),
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

    protected void sendReceiptEmail(BookingReceipt receipt) {
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
