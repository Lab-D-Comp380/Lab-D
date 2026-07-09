package com.movieapp;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Optional;

public class UserService {

    private final UserRepository userRepository = new UserRepository();

    public Optional<String> login(String username, String password) {
        try {
            Optional<String> passwordHash = userRepository.findPasswordHashByUsername(username);
            if (passwordHash.isEmpty()) {
                return Optional.of("No account found. Try registering.");
            }
            if (!BCrypt.checkpw(password, passwordHash.get())) {
                return Optional.of("Incorrect password.");
            }
            return Optional.empty();
        } catch (SQLException e) {
            return Optional.of("Database error. Is MySQL running? (docker compose up -d)");
        }
    }

    public Optional<String> register(String username, String email, String password) {
        try {
            if (userRepository.existsByUsername(username)) {
                return Optional.of("That username is taken.");
            }
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
            userRepository.insert(username, email, passwordHash);
            return Optional.empty();
        } catch (SQLException e) {
            return Optional.of("Database error. Is MySQL running? (docker compose up -d)");
        }
    }

    public Optional<String> findEmailByUsername(String username) {
        try {
            return userRepository.findEmailByUsername(username);
        } catch (SQLException e) {
            System.err.println("Failed to load email for user: " + e.getMessage());
            return Optional.empty();
        }
    }
}
