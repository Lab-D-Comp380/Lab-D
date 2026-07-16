package com.movieapp;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailService implements EmailSender {

    private final String smtpHost;
    private final String smtpPort;
    private final String fromAddress;

    public EmailService() {
        Properties props = loadProperties();
        this.smtpHost = props.getProperty("mail.smtp.host", "localhost");
        this.smtpPort = props.getProperty("mail.smtp.port", "1025");
        this.fromAddress = props.getProperty("mail.from", "noreply@movieapp.local");
    }

    @Override
    public void sendReceipt(String toEmail, String subject, String body) throws Exception {
        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.host", smtpHost);
        mailProps.put("mail.smtp.port", smtpPort);

        Session session = Session.getInstance(mailProps);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromAddress));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = EmailService.class.getResourceAsStream("/application.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            System.err.println("Could not load mail settings: " + e.getMessage());
        }
        return props;
    }
}
