import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.*;

public class Dashboard {

    public static ObservableList<String[]> globalData = FXCollections.observableArrayList();

    private VBox layout;
    private Label totalSalesLabel = new Label("₹0.00");
    private Label totalProfitLabel = new Label("₹0.00");
    private Label totalOrdersLabel = new Label("0");
    private TableView<String[]> tableView = new TableView<>();

    public Dashboard() {

        layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #121212;");

        // 🔹 Buttons
        Button uploadButton = new Button("Upload CSV");
        uploadButton.setStyle("-fx-background-color: #00c853; -fx-text-fill: white; -fx-background-radius: 8;");
        uploadButton.setOnAction(e -> handleFileUpload());

        Button chartBtn = new Button("View Charts");
        chartBtn.setStyle("-fx-background-color: #2962ff; -fx-text-fill: white; -fx-background-radius: 8;");
        chartBtn.setOnAction(e -> new ChartsPage().showCharts());

        HBox buttonBox = new HBox(10, uploadButton, chartBtn);

        // 🔹 KPI Cards
        HBox kpiBox = new HBox(20,
                createCard("Total Sales", totalSalesLabel),
                createCard("Total Profit", totalProfitLabel),
                createCard("Total Orders", totalOrdersLabel)
        );
        kpiBox.setAlignment(Pos.CENTER);

        // 🔹 Table Style
        tableView.setStyle("""
            -fx-background-color: #1e1e1e;
            -fx-control-inner-background: #1e1e1e;
            -fx-table-cell-border-color: #2c2c2c;
            -fx-text-fill: white;
        """);

        layout.getChildren().addAll(buttonBox, kpiBox, tableView);
    }

    public VBox getView() {
        return layout;
    }

    // 📂 Upload CSV
    private void handleFileUpload() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            loadCSV(file);
        }
    }

    // 🔥 Load CSV
    private void loadCSV(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            tableView.getColumns().clear();
            tableView.getItems().clear();

            String headerLine = br.readLine();
            if (headerLine == null) return;

            String[] headers = headerLine.split(",");

            for (int i = 0; i < headers.length; i++) {
                final int colIndex = i;

                TableColumn<String[], String> column = new TableColumn<>(headers[i]);
                column.setCellValueFactory(data ->
                        new javafx.beans.property.SimpleStringProperty(
                                colIndex < data.getValue().length ? data.getValue()[colIndex] : ""
                        )
                );

                tableView.getColumns().add(column);
            }

            ObservableList<String[]> data = FXCollections.observableArrayList();

            String line;
            double totalSales = 0;
            double totalProfit = 0;
            int totalOrders = 0;

            while ((line = br.readLine()) != null) {

                String[] values = line.split(",");

                if (values.length < 10) continue;

                data.add(values);
                totalOrders++;

                try {
                    totalSales += Double.parseDouble(values[8].trim());
                    totalProfit += Double.parseDouble(values[9].trim());
                } catch (Exception ignored) {}
            }

            tableView.setItems(data);
            globalData = data;

            totalSalesLabel.setText("₹" + String.format("%.2f", totalSales));
            totalProfitLabel.setText("₹" + String.format("%.2f", totalProfit));
            totalOrdersLabel.setText(String.valueOf(totalOrders));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🎨 Premium Cards
    private VBox createCard(String title, Label valueLabel) {

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #aaaaaa;");

        valueLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00ff99;");

        VBox card = new VBox(8, titleLabel, valueLabel);
        card.setPadding(new Insets(15));
        card.setPrefWidth(200);
        card.setAlignment(Pos.CENTER);

        card.setStyle("""
            -fx-background-color: #1e1e1e;
            -fx-border-color: #2c2c2c;
            -fx-border-radius: 15;
            -fx-background-radius: 15;
        """);

        return card;
    }
}