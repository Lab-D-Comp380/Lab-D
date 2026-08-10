package com.movieapp;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
// ---------- SNACK SERVICE ----------
public class SnackService {

    private final SnackRepository snackRepository = new SnackRepository();
    // ---------- LOAD ALL SNACKS ----------
    public List<Snack> getSnacks() {

        try {
            return snackRepository.findAll();
        }
        catch (SQLException e) {

            System.err.println("Failed to load snacks: " + e.getMessage());

            return Collections.emptyList();
        }
    }

    public Snack findSnackById(int id) {

        return getSnacks().stream()
                .filter(snack -> snack.getSnackId() == id)
                .findFirst()
                .orElse(null);
    }
}