package com.movieapp;

// ---------- SNACK ORDER CLASS ----------
public class SnackOrder {

    private int snackOrderId;
    private int bookingId;
    private int snackId;
    private int quantity;

    // Constructor
    public SnackOrder(int snackOrderId,
                      int bookingId,
                      int snackId,
                      int quantity) {

        this.snackOrderId = snackOrderId;
        this.bookingId = bookingId;
        this.snackId = snackId;
        this.quantity = quantity;
    }
    
    // Constructor for new snack orders (Database)
    public SnackOrder(int bookingId,
                      int snackId,
                      int quantity) {

        this(0, bookingId, snackId, quantity);
    }

    // ---------- GETTERS ----------

    public int getSnackOrderId() {
        return snackOrderId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public int getSnackId() {
        return snackId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}