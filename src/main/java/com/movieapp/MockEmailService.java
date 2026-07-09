package com.movieapp;

public class MockEmailService implements EmailSender {

    @Override
    public void sendReceipt(String toEmail, String subject, String body) {
        System.out.println("=== RECEIPT EMAIL (mock) ===");
        System.out.println("To: " + toEmail);
        System.out.println("Subject: " + subject);
        System.out.println(body);
        System.out.println("============================");
    }
}
