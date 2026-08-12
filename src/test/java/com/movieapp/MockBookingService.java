package com.movieapp;

// ---------- Mock BOOKING SERVICE ----------
public class MockBookingService extends BookingService {

    private boolean emailSent = false;

    public MockBookingService(
            BookingRepository bookingRepository,
            SnackOrderService snackOrderService,
            EmailSender emailSender) {

        super(bookingRepository, snackOrderService, emailSender);
    }

    @Override
    protected void sendReceiptEmail(BookingReceipt receipt) {

        // Don't start a JavaFX Task during testing.
        emailSent = true;
    }

    public boolean wasEmailSent() {
        return emailSent;
    }
}