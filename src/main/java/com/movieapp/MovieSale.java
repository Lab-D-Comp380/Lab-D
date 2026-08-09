package com.movieapp;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MovieSale {

    private final StringProperty movie;
    private final IntegerProperty ticketsSold;
    private final StringProperty ticketPrice;
    private final StringProperty revenue;

    public MovieSale(
            String movie,
            int ticketsSold,
            String ticketPrice,
            String revenue
    ) {
        this.movie = new SimpleStringProperty(movie);
        this.ticketsSold = new SimpleIntegerProperty(ticketsSold);
        this.ticketPrice = new SimpleStringProperty(ticketPrice);
        this.revenue = new SimpleStringProperty(revenue);
    }

    public StringProperty movieProperty() {
        return movie;
    }

    public IntegerProperty ticketsSoldProperty() {
        return ticketsSold;
    }

    public StringProperty ticketPriceProperty() {
        return ticketPrice;
    }

    public StringProperty revenueProperty() {
        return revenue;
    }

    public String getMovie() {
        return movie.get();
    }

    public int getTicketsSold() {
        return ticketsSold.get();
    }

    public String getTicketPrice() {
        return ticketPrice.get();
    }

    public String getRevenue() {
        return revenue.get();
    }
}
