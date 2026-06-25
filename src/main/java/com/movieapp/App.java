package com.movieapp;

import javafx.application.Application;
import javafx.geometry.Insets;
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

import java.util.HashMap;
import java.util.Map;

public class App extends Application {

    private Stage stage;

    // Our "fake database" of users: username -> password.
    // Lives in memory, so it resets each time you restart the app.
    private final Map<String, String> users = new HashMap<>();

    @Override
public void start(Stage stage) {
    this.stage = stage;
    stage.setTitle("Movie Tickets");
    stage.setWidth(1250);
    stage.setHeight(700);
    stage.setMinWidth(1250);
    stage.setMinHeight(700);
    showLoginScreen();
    stage.show();
}
    // ---------- LOGIN SCREEN ----------
    private void showLoginScreen() {
        Label title = makeTitle("Sign In");

        TextField username = makeField("Username");
        PasswordField password = makePasswordField("Password");
        Node passwordRow = withShowHide(password);  // password field + show/hide toggle
        Label error = makeError();

        Button loginButton = makeButton("Log In");
        Runnable doLogin = () -> {
            String u = username.getText().trim();
            String p = password.getText();

            if (u.isBlank() || p.isBlank()) {
                showError(error, "Enter a username and password.");
            } else if (!users.containsKey(u)) {
                showError(error, "No account found. Try registering.");
            } else if (!users.get(u).equals(p)) {
                showError(error, "Incorrect password.");
            } else {
                showMainScreen(u);   
            }
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

        Button registerButton = makeButton("Register");
        Runnable doRegister = () -> {
            String u = username.getText().trim();
            String p = password.getText();

            if (u.length() < 3) {
                showError(error, "Username must be at least 3 characters.");
            } else if (p.length() < 4) {
                showError(error, "Password must be at least 4 characters.");
            } else if (users.containsKey(u)) {
                showError(error, "That username is taken.");
            } else {
                users.put(u, p);      // save the new user
                showMainScreen(u);    // log them straight in
            }
        };
        registerButton.setOnAction(e -> doRegister.run());
        password.setOnAction(e -> doRegister.run());
        username.setOnAction(e -> doRegister.run());

        Button goLogin = makeLink("Already have an account? Log in");
        goLogin.setOnAction(e -> showLoginScreen());

        setScreen(makePanel(title, username, passwordRow, error, registerButton, goLogin));
    }

    // ---------- MAIN SCREEN (now shows the movie gallery) ----------
private void showMainScreen(String username) {
    MovieGalleryView gallery = new MovieGalleryView();
    javafx.scene.Parent galleryView = gallery.createView();

    javafx.scene.Scene scene = new javafx.scene.Scene(galleryView);

    // Load the stylesheet only if it's actually found, so a missing/misnamed
    // file doesn't crash the screen switch.
    var cssUrl = getClass().getResource("/com/movieapp/style.css");
    if (cssUrl != null) {
        scene.getStylesheets().add(cssUrl.toExternalForm());
    } else {
        System.out.println("WARNING: style.css not found — gallery will be unstyled.");
    }

    stage.setScene(scene);
}

    // ---------- SHOW/HIDE PASSWORD ----------
    // Returns the password field stacked with a TextField that mirrors it,
    // plus a checkbox to toggle which one is visible.
    private Node withShowHide(PasswordField password) {
        TextField visible = new TextField();
        visible.setPromptText(password.getPromptText());
        visible.setManaged(false);
        visible.setVisible(false);

        // Keep the two fields in sync as you type.
        visible.textProperty().bindBidirectional(password.textProperty());

        CheckBox toggle = new CheckBox("Show password");
        toggle.setStyle("-fx-text-fill: #9a93a8;");
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
        root.setStyle("-fx-background-color: #292828;");
       stage.setScene(new Scene(root));
    }

    private VBox makePanel(Node... items) {
        VBox panel = new VBox(14, items);
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setMaxWidth(340);
        panel.setPadding(new Insets(40));
        panel.setStyle("-fx-background-color: #000000; -fx-background-radius: 14;");
        return panel;
    }

    private Label makeTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 26px; -fx-text-fill: #27851f; -fx-font-weight: bold;");
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
        l.setStyle("-fx-text-fill: #ff6b6b;");
        l.setVisible(false);
        l.setManaged(false);
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
        b.setStyle("-fx-background-color: #27851f; -fx-text-fill: #000000; -fx-font-weight: bold;");
        return b;
    }

    private Button makeLink(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: #067a0c;");
        return b;
    }

    public static void main(String[] args) {
        launch();
    }
}