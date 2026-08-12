package com.movieapp;

// ---------- Mock SNACK ORDER SERVICE ----------
public class MockSnackOrderService extends SnackOrderService {

    private int savedOrders = 0;

    // ---------- SAVE SNACK ORDER ----------
    @Override
    public boolean saveSnackOrder(SnackOrder order) {
        savedOrders++;
        return true;
    }

    // ---------- GET NUMBER OF SAVED ORDERS ----------
    public int getSavedOrders() {
        return savedOrders;
    }
}
