package com.movieapp;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Pattern;

public class UserService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

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
            if (!isValidEmail(email)) {
                return Optional.of("Enter a valid email address.");
            }
            if (userRepository.existsByEmail(email)) {
                return Optional.of("That email is already registered.");
            }
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
            userRepository.insert(username, passwordHash, email);
            return Optional.empty();
        } catch (SQLException e) {
            return Optional.of("Database error. Is MySQL running? (docker compose up -d)");
        }
    }

    public Optional<String> findEmailByUsername(String username) {
        try {
            return userRepository.findEmailByUsername(username);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
}
