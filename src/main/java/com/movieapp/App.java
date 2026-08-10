package com.movieapp;

import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private Stage stage;
    private final UserService userService = new UserService();
    private final MovieService movieService = new MovieService();
    private final BookingService bookingService = new BookingService();
    private final SnackService snackService = new SnackService();
    private final ReviewService reviewService = new ReviewService();
    private PurchaseSession session = new PurchaseSession();
    private final java.util.Map<String, List<String>> bookedSeatsByShowing = new java.util.HashMap<>();
    private boolean databaseAvailable;

    // Who is logged in/ need when we save the booking.
    private String currentUsername;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Movie Tickets");
        stage.setWidth(1250);
        stage.setHeight(700);
        stage.setMinWidth(1250);
        stage.setMinHeight(700);

        databaseAvailable = DatabaseConfig.testConnection();
        showLoginScreen();
        stage.show();
        stage.setOnCloseRequest(event -> DatabaseConfig.shutdown());
    }

    // ---------- LOGIN SCREEN ----------
    private void showLoginScreen() {
        Label title = makeTitle("Sign In");

        TextField username = makeField("Username");
        PasswordField password = makePasswordField("Password");
        Node passwordRow = withShowHide(password);
        Label error = makeError();

        if (!databaseAvailable) {
            showError(error, "Cannot connect to MySQL. Run: docker compose up -d");
        }

        Button loginButton = makeButton("Log In");
        loginButton.setDisable(!databaseAvailable);

        Runnable doLogin = () -> {
            String u = username.getText().trim();
            String p = password.getText();

            if (u.isBlank() || p.isBlank()) {
                showError(error, "Enter a username and password.");
                return;
            }

            loginButton.setDisable(true);
            Task<Optional<String>> task = new Task<>() {
                @Override
                protected Optional<String> call() {
                    return userService.login(u, p);
                }
            };

            task.setOnSucceeded(e -> {
                loginButton.setDisable(false);
                Optional<String> loginError = task.getValue();
                if (loginError.isPresent()) {
                    showError(error, loginError.get());
                } else {
                    currentUsername = u;
                    session.setUsername(u);
                    session.setEmail(userService.getEmail(u));
                    showMainScreen(u);
                }
            });

            task.setOnFailed(e -> {
                loginButton.setDisable(false);
                showError(error, "Database error. Is MySQL running? (docker compose up -d)");
            });

            new Thread(task).start();
        };

        loginButton.setOnAction(e -> doLogin.run());
        password.setOnAction(e -> doLogin.run());
        username.setOnAction(e -> doLogin.run());

        Button goRegister = makeLink("No account? Register");
        goRegister.setOnAction(e -> showRegisterScreen());

       setScreen(makePanel(title, username, passwordRow, error, loginButton, goRegister));
    }

    // ---------- REGISTER SCREEN ----------
    private void showRegisterScreen() {
        Label title = makeTitle("Create Account");

        TextField username = makeField("Choose a username (3+ chars)");
        TextField email = makeField("Email address");
        PasswordField password = makePasswordField("Choose a password (4+ chars)");
        Node passwordRow = withShowHide(password);
        Label error = makeError();

        if (!databaseAvailable) {
            showError(error, "Cannot connect to MySQL. Run: docker compose up -d");
        }

        Button registerButton = makeButton("Register");
        registerButton.setDisable(!databaseAvailable);

        Runnable doRegister = () -> {
            String u = username.getText().trim();
            String p = password.getText();

            if (u.length() < 3) {
                showError(error, "Username must be at least 3 characters.");
                return;
            }
           if (p.length() < 4) {
                showError(error, "Password must be at least 4 characters.");
                return;
            }
            String em = email.getText().trim();
            if (!isValidEmail(em)) {
                showError(error, "Enter a valid email address.");
                return;
            }

            registerButton.setDisable(true);
            Task<Optional<String>> task = new Task<>() {
                @Override
                protected Optional<String> call() {
                    return userService.register(u, p, em);
                }
            };

            task.setOnSucceeded(e -> {
                registerButton.setDisable(false);
                Optional<String> registerError = task.getValue();
                if (registerError.isPresent()) {
                    showError(error, registerError.get());
                } else {
                    currentUsername = u;
                    session.setUsername(u);
                    session.setEmail(em);
                    showMainScreen(u);
                }
            });

            task.setOnFailed(e -> {
                registerButton.setDisable(false);
                showError(error, "Database error. Is MySQL running? (docker compose up -d)");
            });

            new Thread(task).start();
        };

        registerButton.setOnAction(e -> doRegister.run());
        password.setOnAction(e -> doRegister.run());
        username.setOnAction(e -> doRegister.run());

        Button goLogin = makeLink("Already have an account? Log in");
        goLogin.setOnAction(e -> showLoginScreen());

        setScreen(makePanel(title, username, email, passwordRow, error, registerButton, goLogin));
    }

    // ---------- MAIN SCREEN (gallery) ----------
    private void showMainScreen(String username) {
        MovieGalleryView gallery = new MovieGalleryView(
                movieService,
                movie -> showShowtimeScreen(movie),
                this::showSalesReport
        );

        Button myReviews = new Button("My Reviews");
        myReviews.getStyleClass().add("ticket-button");
        myReviews.setOnAction(e -> showMyReviewsScreen());

        MenuItem logOutItem = new MenuItem("Log Out");
        logOutItem.setOnAction(e -> logOut());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> exitApp());

        MenuButton menu = new MenuButton("\u2630");   // hamburger icon   
        menu.getItems().addAll(logOutItem, exitItem);
        menu.getStyleClass().add("settings-menu");

        HBox topBar = new HBox(12, myReviews, menu);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new javafx.geometry.Insets(12, 24, 0, 24));
        topBar.getStyleClass().add("gallery-wrapper");

        Parent galleryContent = gallery.createView();
        VBox wrapper = new VBox(topBar, galleryContent);
        wrapper.getStyleClass().add("gallery-wrapper");
        VBox.setVgrow(galleryContent, javafx.scene.layout.Priority.ALWAYS);
        setContent(wrapper);
    
    }

    // Logs the user out and returns to the login screen.
    private void logOut() {
        currentUsername = null;
        session = new PurchaseSession();   // clear any in-progress booking
        showLoginScreen();
    }

    // Closes the application.
    private void exitApp() {
        DatabaseConfig.shutdown();   
        javafx.application.Platform.exit();
    }

    // ---------- MY REVIEWS SCREEN ----------
    private void showMyReviewsScreen() {
        MyReviewsView view = new MyReviewsView(
                currentUsername,
                reviewService,
                movieService,
                () -> showMainScreen(currentUsername)
        );
        setContent(view.createView());
    }

    // ---------- SALES REPORT SCREEN----------
    private void showSalesReport() {
        SalesReportView view = new SalesReportView(
            () -> showMainScreen(currentUsername)
        );
        setContent(view.createView());
    }

    // ---------- SHOWTIME SCREEN ----------
    private void showShowtimeScreen(Movie movie) {
        session.setMovie(movie);
        ShowtimeSelectionView view = new ShowtimeSelectionView(
                movie,
                reviewService,
                showtime -> {session.setShowtime(showtime);
                                session.setTheater("Main Theater");
                                showSeatScreen(movie, showtime);
                },
                () -> showMainScreen(currentUsername)              // back to gallery
        );
        setContent(view.createView());
    }

    // ---------- SEAT SCREEN ----------
    private void showSeatScreen(Movie movie, String showtime) {
        String key = showingKey(movie, showtime);
        List<String> alreadyBooked = bookedSeatsByShowing.getOrDefault(key, new java.util.ArrayList<>());
        SeatSelectionView view = new SeatSelectionView(
                movie,
                showtime,
                session.getSeats(),
                alreadyBooked,
                seats -> {session.setSeats(seats);
                            showSnackScreen();
                },
                () -> showShowtimeScreen(movie)
        );
        setContent(view.createView());
    }

    // Builds a unique key for a movie + showtime combination.
    private String showingKey(Movie movie, String showtime) {
        return movie.getMovieId() + "|" + showtime;
    }

    // ---------- SNACK SCREEN ----------
    private void showSnackScreen() {
        SnackView view = new SnackView(snackService,session,() -> showPaymentScreen(),() -> {
                session.clearSnackOrders();
                showPaymentScreen();
            }
        );
        setContent(view.createView());
    }

    // ---------- Payment Screen -----------
    private void showPaymentScreen(){
        String seatsSummary = String.join(", ", session.getSeats());
        PaymentView view = new PaymentView(
                session.getMovie(),
                session.getShowtime(),
                seatsSummary,
                snackService,
                session,
                details -> {
                    session.setPaymentMethod(details.paymentMethod());
                    session.setCardLastFour(details.cardLastFour());
                    completePurchase();
                },
                () -> showSeatScreen(session.getMovie(), session.getShowtime())
        );
        setAppScene(view.createView(), false);
    }

    private void completePurchase() {
        Optional<BookingReceipt> receipt = bookingService.completePurchase(session);
        if (receipt.isPresent()) {
            // Lock the seats we just booked for this movie + showtime (this run only).
            String key = showingKey(session.getMovie(), session.getShowtime());
            List<String> booked = bookedSeatsByShowing.getOrDefault(key, new java.util.ArrayList<>());
            booked.addAll(session.getSeats());
            bookedSeatsByShowing.put(key, booked);
            session.setSeats(new java.util.ArrayList<>());   // clear the editable selection
            showConfirmation(receipt.get());
        } else {
            showPaymentWithError("Could not complete booking. Please try again.");
        }
    }

    private void showPaymentWithError(String message) {
        String seatsSummary = String.join(", ", session.getSeats());
        PaymentView view = new PaymentView(
                session.getMovie(),
                session.getShowtime(),
                seatsSummary,
                snackService,
                session,
                details -> {
                    session.setPaymentMethod(details.paymentMethod());
                    session.setCardLastFour(details.cardLastFour());
                    completePurchase();
                },
                () -> showSeatScreen(session.getMovie(), session.getShowtime())
        );
        setAppScene(view.createView(), false);
    }

    private void showConfirmation(BookingReceipt receipt) {
        ConfirmationView view = new ConfirmationView(receipt, () -> showMainScreen(currentUsername));
        setAppScene(view.createView(), false);
    }

    private void setAppScene(Parent root, boolean includeShowtimeCss) {
        Scene scene = new Scene(root);
        addStylesheet(scene, "/com/movieapp/style.css");
        if (includeShowtimeCss) {
            addStylesheet(scene, "/com/movieapp/showtime.css");
        }
        stage.setScene(scene);
    }

    private void addStylesheet(Scene scene, String resourcePath) {
        var cssUrl = getClass().getResource(resourcePath);
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.indexOf('@') < email.lastIndexOf('.');
    }
    

    // ---------- SHOW/HIDE PASSWORD ----------
    private Node withShowHide(PasswordField password) {
        TextField visible = new TextField();
        visible.setPromptText(password.getPromptText());
        visible.setManaged(false);
        visible.setVisible(false);

        visible.textProperty().bindBidirectional(password.textProperty());

        CheckBox toggle = new CheckBox("Show password");
        toggle.getStyleClass().add("auth-toggle");
        toggle.setOnAction(e -> {
            boolean show = toggle.isSelected();
            visible.setManaged(show);
            visible.setVisible(show);
            password.setManaged(!show);
            password.setVisible(!show);
        });

        StackPane stack = new StackPane(password, visible);
        return new VBox(8, stack, toggle);
    }

    // ---------- HELPERS ----------

    // Swap the main content while keeping the shared stylesheet attached.
    private void setContent(Parent content) {
        Scene scene = new Scene(content);
        attachStyles(scene);
        stage.setScene(scene);
    }

    private void setScreen(VBox panel) {
        VBox root = new VBox(panel);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("auth-root");

        Scene scene = new Scene(root);
        attachStyles(scene);
        stage.setScene(scene);
    }

    private void attachStyles(Scene scene) {
        var cssUrl = getClass().getResource("/com/movieapp/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.out.println("WARNING: style.css not found — screens will be unstyled.");
        }
    }

        private VBox makePanel(Node... items) {
        VBox panel = new VBox(14, items);
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setMaxWidth(340);
        panel.getStyleClass().add("auth-panel");
        return panel;
    }

    private Label makeTitle(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("auth-title");
        return l;
    }

    private TextField makeField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        return f;
    }

    private PasswordField makePasswordField(String prompt) {
        PasswordField f = new PasswordField();
        f.setPromptText(prompt);
        return f;
    }

    private Label makeError() {
        Label l = new Label();
        l.getStyleClass().add("auth-error");
        l.setVisible(false);
        l.setManaged(false);
        l.setWrapText(true);
        l.setMaxWidth(260);
        return l;
    }

    private void showError(Label error, String message) {
        error.setText(message);
        error.setVisible(true);
        error.setManaged(true);
    }

    private Button makeButton(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.getStyleClass().add("auth-button");
        return b;
    }

    private Button makeLink(String text) {
        Button b = new Button(text);
        b.getStyleClass().add("auth-link");
        return b;
    }

    public static void main(String[] args) {
        launch();
    }
}