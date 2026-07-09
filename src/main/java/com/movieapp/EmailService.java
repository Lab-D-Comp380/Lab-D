package com.movieapp;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;

public class EmailService {

    private static final DateTimeFormatter RECEIPT_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.US);

    private final String smtpHost;
    private final int smtpPort;
    private final String fromAddress;

    public EmailService() {
        Properties props = loadProperties();
        this.smtpHost = props.getProperty("mail.smtp.host", "localhost");
        this.smtpPort = Integer.parseInt(props.getProperty("mail.smtp.port", "1025"));
        this.fromAddress = props.getProperty("mail.from", "noreply@movieapp.local");
    }

    public void sendReceipt(Movie movie, Booking booking) throws MessagingException {
        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.host", smtpHost);
        mailProps.put("mail.smtp.port", String.valueOf(smtpPort));
        mailProps.put("mail.smtp.auth", "false");
        mailProps.put("mail.smtp.starttls.enable", "false");

        Session session = Session.getInstance(mailProps);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromAddress));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(booking.getEmail()));
        message.setSubject("Your Movie Ticket Receipt — " + movie.getTitle());
        message.setText(buildReceiptBody(movie, booking), "UTF-8");

        Transport.send(message);
    }

    public String buildReceiptBody(Movie movie, Booking booking) {
        String bookedAt = booking.getBookedAt() != null
                ? booking.getBookedAt().format(RECEIPT_TIME_FORMAT)
                : "N/A";

        return """
                Movie Ticket Receipt
                --------------------

                Booking ID: %d
                Movie: %s
                Showtime: %s
                Seat(s): %s
                Payment Method: %s
                Email: %s
                Time: %s

                Thank you for your purchase!
                """.formatted(
                booking.getBookingId(),
                movie.getTitle(),
                booking.getShowtime(),
                booking.getSeats(),
                booking.getPaymentDisplay(),
                booking.getEmail(),
                bookedAt
        );
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = EmailService.class.getResourceAsStream("/application.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            System.err.println("Could not load mail properties: " + e.getMessage());
        }
        return props;
    }
}
