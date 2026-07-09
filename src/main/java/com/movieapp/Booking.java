package com.movieapp;

// ---------- BOOKING CLASS ----------
public class Booking {

    private int bookingId;
    private String username;
    private int movieId;
    private int ticketCount;

    // Constructor
    public Booking(int bookingId,
                   String username,
                   int movieId,
                   int ticketCount) {

        this.bookingId = bookingId;
        this.username = username;
        this.movieId = movieId;
        this.ticketCount = ticketCount;
    }

    // ---------- GETTERS ----------
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

    @Override
    public String toString() {
        return "Booking #" + bookingId +
               " | User: " + username +
               " | Movie ID: " + movieId +
               " | Tickets: " + ticketCount;
    }
}
