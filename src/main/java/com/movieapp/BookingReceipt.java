package com.movieapp;

import java.util.List;

public record BookingReceipt(
        int bookingId,
        String username,
        String movieTitle,
        String movieDetails,
        String theater,
        String showtime,
        List<String> seats,
        String paymentMethod,
        String cardLastFour,
        String email
) {
}
