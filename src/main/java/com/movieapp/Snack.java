package com.movieapp;

// ---------- SNACK CLASS ----------
public class Snack {

    private int snackId;
    private String name;
    private String category;
    private double price;

    // Constructor
    public Snack(int snackId,
                 String name,
                 String category,
                 double price) {

        this.snackId = snackId;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    // ---------- GETTERS ----------

    public int getSnackId() {
        return snackId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getPriceLabel() {
        return String.format("$%.2f", price);
    }
}
