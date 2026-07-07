package com.movieapp;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class App extends Application {

    private Stage stage;
    private final UserService userService = new UserService();
    private final MovieService movieService = new MovieService();
    private boolean databaseAvailable;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Movie Tickets");
        stage.setWidth(1250);
        stage.setHeight(700);
        stage.setMinWidth(1250);
        stage.setMinHeight(700);

        databaseAvailable = DatabaseConfig.testConnection();
        databaseAvailable = DatabaseConfig.testConnection();
        showLoginScreen();   
        stage.show();
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

            registerButton.setDisable(true);
            Task<Optional<String>> task = new Task<>() {
                @Override
                protected Optional<String> call() {
                    return userService.register(u, p);
                }
            };

            task.setOnSucceeded(e -> {
                registerButton.setDisable(false);
                Optional<String> registerError = task.getValue();
                if (registerError.isPresent()) {
                    showError(error, registerError.get());
                } else {
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

        setScreen(makePanel(title, username, passwordRow, error, registerButton, goLogin));
    }

    // ---------- MAIN SCREEN ----------
    private void showMainScreen(String username) {
        MovieGalleryView gallery = new MovieGalleryView(movieService);
        javafx.scene.Parent galleryView = gallery.createView();

        javafx.scene.Scene scene = new javafx.scene.Scene(galleryView);

        var cssUrl = getClass().getResource("/com/movieapp/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.out.println("WARNING: style.css not found — gallery will be unstyled.");
        }

        stage.setScene(scene);
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

    private void setScreen(VBox panel) {
        VBox root = new VBox(panel);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("auth-root");

        Scene scene = new Scene(root);
        var cssUrl = getClass().getResource("/com/movieapp/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
        stage.setScene(scene);
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
