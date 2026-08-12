package com.movieapp;

import java.sql.SQLException;

public class MockEmailSender implements EmailSender {

    private boolean emailSent = false;

    @Override
    public void sendReceipt(String to, String subject, String body) {
        emailSent = true;
    }

    public boolean wasEmailSent() {
        return emailSent;
    }
}