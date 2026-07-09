package com.movieapp;

import java.time.LocalDateTime;

public class Booking {

    private int bookingId;
    private String username;
    private int movieId;
    private String showtime;
    private String seats;
    private String paymentMethod;
    private String cardLastFour;
    private String email;
    private LocalDateTime bookedAt;

    public Booking(int bookingId,
                   String username,
                   int movieId,
                   String showtime,
                   String seats,
                   String paymentMethod,
                   String cardLastFour,
                   String email,
                   LocalDateTime bookedAt) {
        this.bookingId = bookingId;
        this.username = username;
        this.movieId = movieId;
        this.showtime = showtime;
        this.seats = seats;
        this.paymentMethod = paymentMethod;
        this.cardLastFour = cardLastFour;
        this.email = email;
        this.bookedAt = bookedAt;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getUsername() {
        return username;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getShowtime() {
        return showtime;
    }

    public String getSeats() {
        return seats;
    }

    public int getTicketCount() {
        if (seats == null || seats.isBlank()) {
            return 0;
        }
        return seats.split(",").length;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getCardLastFour() {
        return cardLastFour;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public String getPaymentDisplay() {
        if (cardLastFour != null && !cardLastFour.isBlank()) {
            return paymentMethod + " ending in " + cardLastFour;
        }
        return paymentMethod;
    }
}
