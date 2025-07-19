import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.layout.Pane;

public class WelcomeScreen {
    public Pane getView() {
        Label title = new Label("E-Commerce Analyzer");
        title.setFont(new Font("Segoe UI", 28));
        title.setTextFill(Color.DARKBLUE);

        Button startButton = new Button("Go to Dashboard");
        startButton.setStyle("-fx-background-color: #0078D4; -fx-text-fill: white; -fx-font-size: 16;");
        startButton.setOnAction(e -> {
            Dashboard dashboard = new Dashboard();
            Scene dashboardScene = new Scene(dashboard.getView(), 1000, 600);
            Main.getPrimaryStage().setScene(dashboardScene); // We'll fix this reference in a later step
        });

        VBox layout = new VBox(30, title, startButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f4f4f4;");
        return layout;
    }
}

