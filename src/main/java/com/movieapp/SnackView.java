package com.movieapp;


import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// ---------- SNACK VIEW ----------
public class SnackView {

    private final SnackService snackService;
    private final PurchaseSession session;

    private final Runnable onContinue;
    private final Runnable onSkip;

    // ---------- CONSTRUCTOR ----------
    public SnackView(
            SnackService snackService,
            PurchaseSession session,
            Runnable onContinue,
            Runnable onSkip) {

        this.snackService = snackService;
        this.session = session;
        this.onContinue = onContinue;
        this.onSkip = onSkip;
    }

    // ---------- CREATE VIEW ----------
    public Parent createView() {

        Label title = new Label("Choose Your Snacks");
        title.getStyleClass().add("page-title");

        VBox page = new VBox(20);
        page.setPadding(new Insets(25));
        page.setAlignment(Pos.TOP_CENTER);
        page.getStyleClass().add("page");

        page.getChildren().add(title);

        // Start fresh each time this screen opens
        session.clearSnackOrders();

        List<Snack> snacks = snackService.getSnacks();
        for (Snack snack : snacks) {
            Label snackLabel = new Label(
                    snack.getName() +
                    " ($" + String.format("%.2f", snack.getPrice()) + ")"
            );
            snackLabel.getStyleClass().add("movie-details");

            Button minusButton = new Button("-");
            Button plusButton = new Button("+");

            minusButton.getStyleClass().add("ticket-button");
            plusButton.getStyleClass().add("ticket-button");

            minusButton.setPrefSize(40,40);
            plusButton.setPrefSize(40,40);

            Label quantityLabel = new Label("0");
            quantityLabel.setMinWidth(30);
            quantityLabel.setAlignment(Pos.CENTER);

            HBox quantityRow = new HBox(20);
            quantityRow.setAlignment(Pos.CENTER_LEFT);
            quantityRow.setMaxWidth(700);
            snackLabel.setMinWidth(260);
            snackLabel.setPrefWidth(260);
            minusButton.setPrefWidth(45);
            plusButton.setPrefWidth(45);
            quantityLabel.setPrefWidth(40);
            quantityLabel.setAlignment(Pos.CENTER);
            quantityRow.getChildren().addAll(
                snackLabel,
                minusButton,
                quantityLabel,
                plusButton);

            quantityRow.setAlignment(Pos.CENTER);

            plusButton.setOnAction(e -> {
                int quantity = Integer.parseInt(quantityLabel.getText());
                quantity++;
                quantityLabel.setText(String.valueOf(quantity));
                boolean found = false;
                for (SnackOrder order : session.getSnackOrders()) {
                    if (order.getSnackId() == snack.getSnackId()) {
                        order.setQuantity(quantity);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    session.addSnackOrder(new SnackOrder(0, snack.getSnackId(), quantity)
                    );
                }

            });

            minusButton.setOnAction(e -> {
                int quantity = Integer.parseInt(quantityLabel.getText());
                if (quantity == 0) {
                    return;
                }
                quantity--;
                quantityLabel.setText(String.valueOf(quantity));
                if (quantity == 0) {
                    session.removeSnackOrder(snack.getSnackId());
                } else {
                    for (SnackOrder order : session.getSnackOrders()) {
                        if (order.getSnackId() == snack.getSnackId()) {
                            order.setQuantity(quantity);
                            break;
                        }
                    }
                }

            });

            page.getChildren().add(quantityRow);
            Label divider = new Label
            ("------------------------------------------------------------------------------------------------------------------------------------------");
            divider.getStyleClass().add("movie-details");
            page.getChildren().add(divider);
        }

        Button continueButton = new Button("Continue to Payment");
        continueButton.getStyleClass().add("ticket-button");

        continueButton.setOnAction(e -> {
            if (onContinue != null) {
                onContinue.run();
            }
        });

        Button skipButton = new Button("Skip Snacks");
        skipButton.getStyleClass().add("ticket-button");

        skipButton.setOnAction(e -> {
            session.clearSnackOrders();
            if (onSkip != null) {
                onSkip.run();
            }
        });

        page.getChildren().addAll(
                continueButton,
                skipButton
        );

        return page;
    }
}