package com.movieapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class PaymentView {

    public record PaymentDetails(String paymentMethod, String cardLastFour) {}

    private final Movie movie;
    private final String showtime;
    private final String seatsSummary;
    private final Consumer<PaymentDetails> onPaymentConfirmed;
    private final Runnable onBack;
    private final String initialError;

    public PaymentView(Movie movie,
                       String showtime,
                       String seatsSummary,
                       Consumer<PaymentDetails> onPaymentConfirmed,
                       Runnable onBack) {
        this(movie, showtime, seatsSummary, onPaymentConfirmed, onBack, null);
    }

    public PaymentView(Movie movie,
                       String showtime,
                       String seatsSummary,
                       Consumer<PaymentDetails> onPaymentConfirmed,
                       Runnable onBack,
                       String initialError) {
        this.movie = movie;
        this.showtime = showtime;
        this.seatsSummary = seatsSummary;
        this.onPaymentConfirmed = onPaymentConfirmed;
        this.onBack = onBack;
        this.initialError = initialError;
    }

    public Parent createView() {
        Button back = new Button("\u2190 Back");
        back.getStyleClass().add("auth-link");
        back.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });

        Label heading = new Label("Payment");
        heading.getStyleClass().add("page-title");

        Label summary = new Label(movie.getTitle() + "  \u2022  " + showtime + "  \u2022  Seats: " + seatsSummary);
        summary.getStyleClass().add("movie-details");
        summary.setWrapText(true);

        ComboBox<String> paymentMethod = new ComboBox<>();
        paymentMethod.getItems().addAll("Credit Card", "Debit Card", "PayPal");
        paymentMethod.setValue("Credit Card");
        paymentMethod.setMaxWidth(Double.MAX_VALUE);

        TextField cardholderName = new TextField();
        cardholderName.setPromptText("Cardholder name");

        TextField cardNumber = new TextField();
        cardNumber.setPromptText("Card number");

        TextField expiry = new TextField();
        expiry.setPromptText("MM/YY");

        PasswordField cvv = new PasswordField();
        cvv.setPromptText("CVV");

        VBox cardFields = new VBox(10, cardholderName, cardNumber, expiry, cvv);

        Label error = new Label();
        error.getStyleClass().add("auth-error");
        error.setVisible(false);
        error.setManaged(false);
        error.setWrapText(true);
        if (initialError != null && !initialError.isBlank()) {
            showError(error, initialError);
        }

        Runnable updateCardVisibility = () -> {
            boolean cardBased = isCardBased(paymentMethod.getValue());
            cardFields.setVisible(cardBased);
            cardFields.setManaged(cardBased);
        };
        paymentMethod.setOnAction(e -> updateCardVisibility.run());
        updateCardVisibility.run();

        Button confirm = new Button("Confirm Purchase");
        confirm.getStyleClass().add("ticket-button");
        confirm.setMaxWidth(Double.MAX_VALUE);
        confirm.setOnAction(e -> {
            String method = paymentMethod.getValue();
            if (method == null || method.isBlank()) {
                showError(error, "Select a payment method.");
                return;
            }

            String lastFour = null;
            if (isCardBased(method)) {
                String validationError = validateCard(cardholderName.getText(), cardNumber.getText(), expiry.getText(), cvv.getText());
                if (validationError != null) {
                    showError(error, validationError);
                    return;
                }
                String digits = cardNumber.getText().replaceAll("\\D", "");
                lastFour = digits.substring(digits.length() - 4);
            }

            error.setVisible(false);
            error.setManaged(false);

            if (onPaymentConfirmed != null) {
                onPaymentConfirmed.accept(new PaymentDetails(method, lastFour));
            }
        });

        VBox panel = new VBox(14,
                new Label("Payment method"), paymentMethod,
                cardFields,
                error,
                confirm);
        panel.setMaxWidth(420);

        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        VBox page = new VBox(18, new HBox(back), heading, summary, panel, spacer);
        page.setPadding(new Insets(24));
        page.setAlignment(Pos.TOP_CENTER);
        page.getStyleClass().add("page");
        return page;
    }

    private boolean isCardBased(String method) {
        return "Credit Card".equals(method) || "Debit Card".equals(method);
    }

    private String validateCard(String name, String number, String expiryValue, String cvvValue) {
        if (name == null || name.isBlank()) {
            return "Enter the cardholder name.";
        }

        String digits = number != null ? number.replaceAll("\\D", "") : "";
        if (digits.length() < 13 || digits.length() > 19) {
            return "Enter a valid card number.";
        }

        if (expiryValue == null || !expiryValue.matches("\\d{2}/\\d{2}")) {
            return "Enter expiry as MM/YY.";
        }

        if (cvvValue == null || !cvvValue.matches("\\d{3,4}")) {
            return "Enter a valid CVV.";
        }

        return null;
    }

    private void showError(Label error, String message) {
        error.setText(message);
        error.setVisible(true);
        error.setManaged(true);
    }
}
