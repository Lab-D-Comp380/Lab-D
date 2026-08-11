package com.movieapp;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SalesReportService summary helpers.
 *
 * These cover ticket totals, revenue, and currency formatting without needing
 * MySQL or the UI. getMovieSales() hits the database, so it is not covered here.
 */
public class SalesReportServiceTest {

    private final SalesReportService service = new SalesReportService();

    private List<MovieSale> sampleSales() {
        return List.of(
                new MovieSale("Skybound", 2, "$12.50", "$25.00"),
                new MovieSale("Pixel Quest", 3, "$12.50", "$37.50")
        );
    }

    @Test
    void totalTicketsSumsAllRows() {
        assertEquals(5, service.getTotalTickets(sampleSales()));
    }

    @Test
    void totalTicketsIsZeroForEmptyList() {
        assertEquals(0, service.getTotalTickets(List.of()));
    }

    @Test
    void totalRevenueUsesFixedTicketPrice() {
        // 5 tickets * $12.50 = $62.50
        assertEquals(62.50, service.getTotalRevenue(sampleSales()), 0.001);
    }

    @Test
    void totalRevenueIsZeroForEmptyList() {
        assertEquals(0.0, service.getTotalRevenue(List.of()), 0.001);
    }

    @Test
    void averageTicketIsTicketPriceWhenTicketsExist() {
        assertEquals(SalesReportService.TICKET_PRICE,
                service.getAverageTicket(sampleSales()), 0.001);
    }

    @Test
    void averageTicketIsZeroWhenNoTickets() {
        assertEquals(0.0, service.getAverageTicket(List.of()), 0.001);
    }

    @Test
    void formatCurrencyUsesUsLocale() {
        assertEquals("$12.50", service.formatCurrency(12.50));
        assertEquals("$0.00", service.formatCurrency(0.0));
    }

    @Test
    void singleMovieRowTotalsMatchThatRow() {
        List<MovieSale> oneMovie = List.of(
                new MovieSale("Echo Point", 4, "$12.50", "$50.00")
        );
        assertEquals(4, service.getTotalTickets(oneMovie));
        assertEquals(50.0, service.getTotalRevenue(oneMovie), 0.001);
        assertEquals(12.50, service.getAverageTicket(oneMovie), 0.001);
    }
}
