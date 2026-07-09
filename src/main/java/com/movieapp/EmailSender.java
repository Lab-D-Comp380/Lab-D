package com.movieapp;

public interface EmailSender {

    void sendReceipt(String toEmail, String subject, String body) throws Exception;
}
