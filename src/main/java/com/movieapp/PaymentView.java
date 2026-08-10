package com.movieapp;

import java.util.function.Consumer;

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

public class PaymentView {

    private static final double TICKET_PRICE = 12.50;
    public record PaymentDetails(String paymentMethod, String cardLastFour) {}

    private final Movie movie;
    private final String showtime;
    private final String seatsSummary;
    private final SnackService snackService;
    private final PurchaseSession session;
    private final Consumer<PaymentDetails> onPaymentConfirmed;
    private final Runnable onBack;
    private final String initialError;

    public PaymentView(Movie movie,
                       String showtime,
                       String seatsSummary,
                       SnackService snackService,
                       PurchaseSession session,
                       Consumer<PaymentDetails> onPaymentConfirmed,
                       Runnable onBack) {
        this(movie, showtime, seatsSummary, snackService, session, onPaymentConfirmed, onBack, null);
    }


    public PaymentView(Movie movie,
                       String showtime,
                       String seatsSummary,
                       SnackService snackService,
                       PurchaseSession session,
                       Consumer<PaymentDetails> onPaymentConfirmed,
                       Runnable onBack,
                       String initialError) {
        this.movie = movie;
        this.showtime = showtime;
        this.seatsSummary = seatsSummary;
        this.snackService = snackService;
        this.session = session;
        this.onPaymentConfirmed = onPaymentConfirmed;
        this.onBack = onBack;
        this.initialError = initialError;
    }

    public Parent createView() {
        Button back = new Button("\u2190 Back");
        back.getStyleClass().add("ticket-button");
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

        Label paymentMethodLabel = new Label("Payment method");
        paymentMethodLabel.getStyleClass().add("field-label");

        VBox panel = new VBox(14,
                paymentMethodLabel, paymentMethod,
                cardFields,
                error,
                confirm);
        panel.setMaxWidth(420);
        panel.setMinWidth(420);

        // Right side: order summary
        VBox orderSummary = buildOrderSummary();

        HBox columns = new HBox(50, panel, orderSummary);
        columns.setAlignment(Pos.TOP_CENTER);

        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        VBox page = new VBox(18, new HBox(back), heading, summary, columns, spacer);
        page.setPadding(new Insets(24));
        page.setAlignment(Pos.TOP_CENTER);
        page.getStyleClass().add("page");
        return page;
    }

    private VBox buildOrderSummary() {
        // Poster
        var posterBox = new javafx.scene.layout.StackPane();
        double w = 220, h = 330;
        posterBox.setPrefSize(w, h);
        posterBox.setMinSize(w, h);
        posterBox.setMaxSize(w, h);

        boolean loaded = false;
        if (movie != null && movie.getPosterFilename() != null) {
            var posterStream = getClass().getResourceAsStream("/posters/" + movie.getPosterFilename());
            if (posterStream != null) {
                var poster = new javafx.scene.image.ImageView(new javafx.scene.image.Image(posterStream));
                poster.setPreserveRatio(true);
                poster.setFitWidth(w);
                poster.setFitHeight(h);
                posterBox.setClip(new javafx.scene.shape.Rectangle(w, h));
                posterBox.getChildren().add(poster);
                loaded = true;
            }
        }
        if (!loaded) {
            Label ph = new Label(movie != null ? movie.getTitle() : "Movie");
            ph.getStyleClass().add("blank-movie-text");
            posterBox.getStyleClass().add("blank-movie-image");
            posterBox.getChildren().add(ph);
        }

        Label summaryTitle = new Label("Order Summary");
        summaryTitle.getStyleClass().add("section-title");

        int seatCount = countSeats();
        double total = seatCount * TICKET_PRICE;

        // ---------- SNACK TOTAL ----------
        double snackTotal = 0;
        for (SnackOrder order : session.getSnackOrders()) {
            Snack snack = snackService.findSnackById(order.getSnackId());
            if (snack != null) {
                snackTotal += snack.getPrice() * order.getQuantity();
            }
        }

        // ---------- GRAND TOTAL ----------
        double grandTotal = total + snackTotal;

        VBox lines = new VBox(8);
        lines.getChildren().add(summaryRow(
            "Movie",
            movie != null ? movie.getTitle() : "-"
        ));
        lines.getChildren().add(summaryRow(
            "Showtime",
            showtime
        ));
        lines.getChildren().add(summaryRow(
            "Seats",
            seatsSummary
        ));
        lines.getChildren().add(summaryRow(
            "Tickets",
            seatCount + " × $" + String.format("%.2f", TICKET_PRICE)
        ));
        // ---------- SNACKS ----------
        if (!session.getSnackOrders().isEmpty()) {
            lines.getChildren().add(summaryRow("", "----------------"));
            lines.getChildren().add(summaryRow("Snacks", ""));
            for (SnackOrder order : session.getSnackOrders()) {
                Snack snack = snackService.findSnackById(order.getSnackId());
                if (snack != null) {
                    lines.getChildren().add(summaryRow
                        (snack.getName(),order.getQuantity() +" × $"+String.format("%.2f", snack.getPrice()))
                    );
                }
            }
        }
        Label divider = new Label("");
        divider.getStyleClass().add("right-divider");
        divider.setMaxWidth(Double.MAX_VALUE);

        HBox totalRow = new HBox();
        Label totalLabel = new Label("Total");
        totalLabel.getStyleClass().add("field-label");
        Region push = new Region();
        HBox.setHgrow(push, javafx.scene.layout.Priority.ALWAYS);
        Label totalValue = new Label("$" + String.format("%.2f", grandTotal));
        totalValue.getStyleClass().add("order-total");
        totalRow.getChildren().addAll(totalLabel, push, totalValue);

        VBox box = new VBox(16, posterBox, summaryTitle, lines, divider, totalRow);
        box.setMaxWidth(280);
        box.setMinWidth(280);
        return box;
    }

    private HBox summaryRow(String label, String value) {
        Label l = new Label(label);
        l.getStyleClass().add("movie-details");
        l.setMinWidth(80);
        Label v = new Label(value != null ? value : "-");
        v.getStyleClass().add("summary-value");
        v.setWrapText(true);
        HBox row = new HBox(10, l, v);
        return row;
    }

    private int countSeats() {
        if (seatsSummary == null || seatsSummary.isBlank()) return 0;
        return seatsSummary.split(",").length;
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
