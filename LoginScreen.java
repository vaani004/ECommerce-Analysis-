import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginScreen {

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f0f8ff;");
        layout.setPadding(new Insets(30));

        Label title = new Label("Login to E-Commerce Analyzer");
        title.setStyle("-fx-font-size: 20px; -fx-text-fill: #333;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: #0078d4; -fx-text-fill: white;");

        Label message = new Label();

        loginBtn.setOnAction(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();
            if (user.equals("admin") && pass.equals("1234")) {
                WelcomeScreen welcome = new WelcomeScreen();
                primaryStage.setScene(new Scene(welcome.getView(), 600, 400));
            } else {
                message.setText("Invalid credentials!");
                message.setStyle("-fx-text-fill: red;");
            }
        });

        layout.getChildren().addAll(title, usernameField, passwordField, loginBtn, message);
        primaryStage.setScene(new Scene(layout, 400, 300));
    }
}

