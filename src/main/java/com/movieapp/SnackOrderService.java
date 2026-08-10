package com.movieapp;

import java.sql.SQLException;

// ---------- SNACK ORDER SERVICE ----------
public class SnackOrderService {

    private final SnackOrderRepository snackOrderRepository =
            new SnackOrderRepository();

    // ---------- SAVE A SNACK ORDER ----------
    public boolean saveSnackOrder(SnackOrder snackOrder) {

        try {

            snackOrderRepository.createSnackOrder(snackOrder);
            return true;

        }
        catch (SQLException e) {

            System.err.println("Failed to save snack order: "
                    + e.getMessage());

            return false;
        }
    }

}