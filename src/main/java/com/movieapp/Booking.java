package com.movieapp;

public class Booking {

    private int bookingId;
    private String username;
    private int movieId;
    private int ticketCount;
    private String theater;
    private String showtime;
    private String seats;
    private String paymentMethod;
    private String cardLastFour;

    public Booking(int bookingId,
                   String username,
                   int movieId,
                   int ticketCount,
                   String theater,
                   String showtime,
                   String seats,
                   String paymentMethod,
                   String cardLastFour) {
        this.bookingId = bookingId;
        this.username = username;
        this.movieId = movieId;
        this.ticketCount = ticketCount;
        this.theater = theater;
        this.showtime = showtime;
        this.seats = seats;
        this.paymentMethod = paymentMethod;
        this.cardLastFour = cardLastFour;
    }

    public int getBookingId() {
        return bookingId;
    }

    public String getUsername() {
        return username;
    }

    public int getMovieId() {
        return movieId;
    }

    public int getTicketCount() {
        return ticketCount;
    }

    public String getTheater() {
        return theater;
    }

    public String getShowtime() {
        return showtime;
    }

    public String getSeats() {
        return seats;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getCardLastFour() {
        return cardLastFour;
    }
}
