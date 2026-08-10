package com.movieapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// ---------- SNACK REPOSITORY ----------
public class SnackRepository {

    // ---------- GET ALL SNACKS ----------
    public List<Snack> findAll() throws SQLException {

        String sql = """
                SELECT id, name, category, price
                FROM snacks
                ORDER BY category, name
                """;

        List<Snack> snacks = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {

                snacks.add(new Snack(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price")
                ));
            }
        }

        return snacks;
    }

    // ---------- GET ONE SNACK ----------
    public Snack getSnackById(int snackId) throws SQLException {
        for (Snack snack : findAll()) {
            if (snack.getSnackId() == snackId) {
                return snack;
            }
    }
    return null;
}
}