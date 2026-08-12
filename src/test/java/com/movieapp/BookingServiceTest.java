package com.movieapp;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class BookingServiceTest {

    @Test
    void constructorCreatesBookingService() {
        BookingService service = new BookingService();
        assertNotNull(service);
    }

    @Test
    void sendReceiptEmailRunsWithoutThrowing() {
        MockEmailSender sender = new MockEmailSender();
        BookingService service =
            new BookingService(sender);
            BookingReceipt receipt = new BookingReceipt(
                1,
                "bashir",
                "Skybound",
                "Action • 108 min",
                "Theater 1",
                "7:00 PM",
                java.util.List.of("A1", "A2"),
                "Credit Card",
                "1234",
                "test@test.com"
            );
            assertDoesNotThrow(() ->
            service.sendReceiptEmail(receipt)
            );
    }

    @Test
    void completePurchaseReturnsReceiptWhenSuccessful() {
        MockBookingRepository repository = new MockBookingRepository();
        MockSnackOrderService snackService = new MockSnackOrderService();
        MockBookingService service =
            new MockBookingService(
                    repository,
                    snackService,
                    new MockEmailSender()
            );
        Movie movie = new Movie(
            1,
            "Skybound",
            "Action",
            108,
            "PG-13",
            java.time.LocalDate.now(),
            "skybound.png"
        );
        PurchaseSession session = new PurchaseSession();
        session.setUsername("bashir");
        session.setEmail("test@test.com");
        session.setMovie(movie);
        session.setTheater("Theater 1");
        session.setShowtime("7:00 PM");
        session.setSeats(List.of("A1", "A2"));
        session.setPaymentMethod("Credit Card");
        session.setCardLastFour("1234");

        // Add one snack
        session.addSnackOrder(new SnackOrder(
            0,
            0,
            1,
            1
        ));
        Optional<BookingReceipt> receipt =service.completePurchase(session);
        assertTrue(receipt.isPresent());
        assertEquals("bashir",
            receipt.get().username());
        assertEquals("Skybound",
            receipt.get().movieTitle());
        assertTrue(service.wasEmailSent());
        assertEquals(
            1,
            snackService.getSavedOrders()
        );
    }
    
    @Test
    void completePurchaseReturnsEmptyWhenDatabaseFails() {
        MockBookingRepository repository = new MockBookingRepository();
        repository.setShouldThrowException(true);
        MockSnackOrderService snackService = new MockSnackOrderService();
        MockBookingService service =
            new MockBookingService(repository, snackService, new MockEmailSender());
        Movie movie = new Movie(
            1,
            "Skybound",
            "Action",
            108,
            "PG-13",
            java.time.LocalDate.now(),
            "skybound.png"
        );
        PurchaseSession session = new PurchaseSession();
        session.setUsername("bashir");
        session.setEmail("test@test.com");
        session.setMovie(movie);
        session.setTheater("Theater 1");
        session.setShowtime("7:00 PM");
        session.setSeats(List.of("A1", "A2"));
        session.setPaymentMethod("Credit Card");
        session.setCardLastFour("1234");
        Optional<BookingReceipt> receipt = service.completePurchase(session);
        assertTrue(receipt.isEmpty());
        assertFalse(service.wasEmailSent());
        assertEquals(0, snackService.getSavedOrders());
    }

    @Test
    void completePurchaseReturnsReceiptWithoutSnacks() {
        MockBookingRepository repository =
            new MockBookingRepository();
            MockSnackOrderService snackService =
            new MockSnackOrderService();
            MockBookingService service =
            new MockBookingService(
                    repository,
                    snackService,
                    new MockEmailSender()
            );
            Movie movie = new Movie(
            1,
            "Skybound",
            "Action",
            108,
            "PG-13",
            java.time.LocalDate.now(),
            "skybound.png"
        );
        PurchaseSession session = new PurchaseSession();
        session.setUsername("bashir");
        session.setEmail("test@test.com");
        session.setMovie(movie);
        session.setTheater("Theater 1");
        session.setShowtime("7:00 PM");
        session.setSeats(List.of("A1", "A2"));
        session.setPaymentMethod("Credit Card");
        session.setCardLastFour("1234");
        Optional<BookingReceipt> receipt = service.completePurchase(session);
        assertTrue(receipt.isPresent());
        assertEquals("Skybound",receipt.get().movieTitle());
        assertEquals(0, snackService.getSavedOrders());
        assertTrue(service.wasEmailSent());
    }
}