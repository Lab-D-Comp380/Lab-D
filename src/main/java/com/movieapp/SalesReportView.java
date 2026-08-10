package com.movieapp;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class SalesReportView {

    private Runnable onBack;

    public void show(Stage stage, Runnable onBack) {
        this.onBack = onBack;
        show(stage);
    }

    public void show(Stage stage) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #050505;");

        VBox mainContent = new VBox(18);
        mainContent.setPadding(new Insets(30));
        mainContent.setAlignment(Pos.TOP_LEFT);

        Label cinemaLabel = new Label("CINEMA");
        cinemaLabel.setTextFill(Color.web("#00BFFF"));
        cinemaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        Button backButton = new Button("← Back");
        backButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #00BFFF;" +
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 0;"
        );
        backButton.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        Label title = new Label("Sales Report");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 34));

        Label subtitle = new Label("View movie ticket sales and revenue.");
        subtitle.setTextFill(Color.WHITE);
        subtitle.setFont(Font.font("Arial", 14));

        ComboBox<String> movieSelector = new ComboBox<>();
        movieSelector.setItems(FXCollections.observableArrayList(
                "All Movies",
                "Skybound",
                "Pixel Quest",
                "Echo Point",
                "Midnight Signal",
                "The Last Orbit"
        ));
        movieSelector.setValue("All Movies");
        movieSelector.setPrefWidth(220);
        movieSelector.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #00BFFF;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 4;" +
                "-fx-background-radius: 4;"
        );

        Label summaryTitle = new Label("Sales Summary");
        summaryTitle.setTextFill(Color.WHITE);
        summaryTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Separator summarySeparator = new Separator();
        summarySeparator.setPrefWidth(180);
        summarySeparator.setMaxWidth(180);

        HBox summaryCards = new HBox(20);

        VBox revenueCard = createCard("Total Revenue", "$5,233.00");
        VBox ticketCard = createCard("Tickets Sold", "371");
        VBox averageCard = createCard("Average Ticket", "$14.10");

        summaryCards.getChildren().addAll(
                revenueCard,
                ticketCard,
                averageCard
        );

        Label movieSalesTitle = new Label("Movie Sales");
        movieSalesTitle.setTextFill(Color.WHITE);
        movieSalesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Separator movieSeparator = new Separator();
        movieSeparator.setPrefWidth(150);
        movieSeparator.setMaxWidth(150);

        TableView<MovieSale> table = new TableView<>();
        table.setPrefWidth(900);
        table.setPrefHeight(290);

        TableColumn<MovieSale, String> movieColumn =
                new TableColumn<>("Movie");
        movieColumn.setCellValueFactory(
                data -> data.getValue().movieProperty()
        );

        TableColumn<MovieSale, Number> ticketsColumn =
                new TableColumn<>("Tickets Sold");
        ticketsColumn.setCellValueFactory(
                data -> data.getValue().ticketsSoldProperty()
        );

        TableColumn<MovieSale, String> priceColumn =
                new TableColumn<>("Ticket Price");
        priceColumn.setCellValueFactory(
                data -> data.getValue().ticketPriceProperty()
        );

        TableColumn<MovieSale, String> revenueColumn =
                new TableColumn<>("Revenue");
        revenueColumn.setCellValueFactory(
                data -> data.getValue().revenueProperty()
        );

        movieColumn.setPrefWidth(300);
        ticketsColumn.setPrefWidth(190);
        priceColumn.setPrefWidth(190);
        revenueColumn.setPrefWidth(210);

        table.getColumns().addAll(
                movieColumn,
                ticketsColumn,
                priceColumn,
                revenueColumn
        );

        var allSales = FXCollections.observableArrayList(
                new MovieSale("Skybound", 85, "$15.00", "$1,275.00"),
                new MovieSale("Pixel Quest", 64, "$13.00", "$832.00"),
                new MovieSale("Echo Point", 91, "$15.00", "$1,365.00"),
                new MovieSale("Midnight Signal", 58, "$14.00", "$812.00"),
                new MovieSale("The Last Orbit", 73, "$13.00", "$949.00")
        );

        // Load live booking totals from the database when available.
        SalesReportService salesReportService = new SalesReportService();
        List<MovieSale> liveSales = salesReportService.getMovieSales();
        if (!liveSales.isEmpty()) {
            allSales.setAll(liveSales);
            updateCardValue(revenueCard, salesReportService.formatCurrency(salesReportService.getTotalRevenue(liveSales)));
            updateCardValue(ticketCard, String.valueOf(salesReportService.getTotalTickets(liveSales)));
            updateCardValue(averageCard, salesReportService.formatCurrency(salesReportService.getAverageTicket(liveSales)));
        }

        table.setItems(FXCollections.observableArrayList(allSales));

        table.setStyle(
                "-fx-background-color: #111111;" +
                "-fx-control-inner-background: #111111;" +
                "-fx-table-cell-border-color: #333333;" +
                "-fx-border-color: #444444;"
        );

        Button refreshButton = new Button("Refresh Report");
        refreshButton.setStyle(
                "-fx-background-color: #F0264F;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 25;" +
                "-fx-padding: 12 25 12 25;" +
                "-fx-cursor: hand;"
        );

        movieSelector.setOnAction(event -> {
            String selectedMovie = movieSelector.getValue();

            if ("All Movies".equals(selectedMovie)) {
                table.setItems(FXCollections.observableArrayList(allSales));
                updateCardValue(revenueCard, salesReportService.formatCurrency(salesReportService.getTotalRevenue(allSales)));
                updateCardValue(ticketCard, String.valueOf(salesReportService.getTotalTickets(allSales)));
                updateCardValue(averageCard, salesReportService.formatCurrency(salesReportService.getAverageTicket(allSales)));
            } else {
                var filtered = allSales.filtered(
                        sale -> sale.getMovie().equals(selectedMovie)
                );
                table.setItems(filtered);
                updateCardValue(revenueCard, salesReportService.formatCurrency(salesReportService.getTotalRevenue(filtered)));
                updateCardValue(ticketCard, String.valueOf(salesReportService.getTotalTickets(filtered)));
                updateCardValue(averageCard, salesReportService.formatCurrency(salesReportService.getAverageTicket(filtered)));
            }
        });

        refreshButton.setOnAction(event -> {
            List<MovieSale> refreshedSales = salesReportService.getMovieSales();
            if (!refreshedSales.isEmpty()) {
                allSales.setAll(refreshedSales);
            }
            movieSelector.setValue("All Movies");
            table.setItems(FXCollections.observableArrayList(allSales));
            updateCardValue(revenueCard, salesReportService.formatCurrency(salesReportService.getTotalRevenue(allSales)));
            updateCardValue(ticketCard, String.valueOf(salesReportService.getTotalTickets(allSales)));
            updateCardValue(averageCard, salesReportService.formatCurrency(salesReportService.getAverageTicket(allSales)));
        });

        mainContent.getChildren().addAll(
                cinemaLabel,
                backButton,
                title,
                subtitle,
                movieSelector,
                summaryTitle,
                summarySeparator,
                summaryCards,
                movieSalesTitle,
                movieSeparator,
                table,
                refreshButton
        );

        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1250, 760);

        stage.setTitle("Cinema Sales Report");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createCard(String cardTitle, String value) {

        Label titleLabel = new Label(cardTitle);
        titleLabel.setTextFill(Color.LIGHTGRAY);
        titleLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 14)
        );

        Label valueLabel = new Label(value);
        valueLabel.setTextFill(Color.WHITE);
        valueLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 26)
        );

        VBox card = new VBox(10);
        card.getChildren().addAll(
                titleLabel,
                valueLabel
        );

        card.setPadding(new Insets(20));
        card.setPrefWidth(230);
        card.setPrefHeight(100);

        card.setStyle(
                "-fx-background-color: #111111;" +
                "-fx-border-color: #555555;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;"
        );

        return card;
    }

    private void updateCardValue(VBox card, String value) {
        if (card.getChildren().size() > 1 && card.getChildren().get(1) instanceof Label valueLabel) {
            valueLabel.setText(value);
        }
    }
}
