package com.movieapp;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

// Builds MovieSale rows and summary stats from booking totals.
public class SalesReportService {

    // Keep in sync with PaymentView ticket price.
    static final double TICKET_PRICE = 12.50;

    private final SalesReportRepository salesReportRepository = new SalesReportRepository();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    public List<MovieSale> getMovieSales() {
        try {
            return salesReportRepository.findTicketTotalsByMovie().stream()
                    .map(this::toMovieSale)
                    .toList();
        } catch (SQLException e) {
            System.err.println("Failed to load sales report: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<MovieSale> getMovieSales(String movieTitle) {
        List<MovieSale> allSales = getMovieSales();
        if (movieTitle == null || "All Movies".equals(movieTitle)) {
            return allSales;
        }
        return allSales.stream()
                .filter(sale -> sale.getMovie().equals(movieTitle))
                .toList();
    }

    public List<String> getMovieTitles() {
        return getMovieSales().stream()
                .map(MovieSale::getMovie)
                .toList();
    }

    public int getTotalTickets(List<MovieSale> sales) {
        return sales.stream().mapToInt(MovieSale::getTicketsSold).sum();
    }

    public double getTotalRevenue(List<MovieSale> sales) {
        return getTotalTickets(sales) * TICKET_PRICE;
    }

    public double getAverageTicket(List<MovieSale> sales) {
        int tickets = getTotalTickets(sales);
        if (tickets == 0) {
            return 0.0;
        }
        return getTotalRevenue(sales) / tickets;
    }

    public String formatCurrency(double amount) {
        return currencyFormat.format(amount);
    }

    private MovieSale toMovieSale(SalesReportRepository.MovieTicketTotal total) {
        double revenue = total.ticketsSold() * TICKET_PRICE;
        return new MovieSale(
                total.title(),
                total.ticketsSold(),
                formatCurrency(TICKET_PRICE),
                formatCurrency(revenue)
        );
    }
}
