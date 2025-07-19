import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Dashboard {

    private VBox layout;
    private Label totalSalesLabel = new Label("Total Sales: ₹0.00");
    private Label totalProfitLabel = new Label("Total Profit: ₹0.00");
    private Label totalOrdersLabel = new Label("Total Orders: 0");
    private TableView<String[]> tableView = new TableView<>();
    private List<String[]> fullData = new ArrayList<>(); // Store all loaded data
    private TextField searchField;

    public Dashboard() {
        Database.initializeDatabase();

        layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f9f9f9;");

        // Buttons
        Button uploadButton = new Button("Upload CSV");
        uploadButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        uploadButton.setOnAction(e -> handleFileUpload());

        Button viewChartsBtn = new Button("View Charts");
        viewChartsBtn.setStyle("-fx-background-color: #0078d4; -fx-text-fill: white;");
        viewChartsBtn.setOnAction(e -> new ChartsPage().showCharts());

        Button exportButton = new Button("Export to CSV");
        exportButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        exportButton.setOnAction(e -> exportTableDataToCSV());

        // KPI
        HBox kpiBox = new HBox(30, totalSalesLabel, totalProfitLabel, totalOrdersLabel);
        kpiBox.setAlignment(Pos.CENTER);
        totalSalesLabel.setStyle("-fx-font-size: 16;");
        totalProfitLabel.setStyle("-fx-font-size: 16;");
        totalOrdersLabel.setStyle("-fx-font-size: 16;");

        // Filters
        ComboBox<String> regionFilter = new ComboBox<>();
        regionFilter.getItems().addAll("All", "East", "West", "Central", "South", "North");
        regionFilter.setValue("All");

        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        Button applyFilterBtn = new Button("Apply Filter");
        applyFilterBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white;");
        applyFilterBtn.setOnAction(e -> {
            String region = regionFilter.getValue();
            String start = (startDatePicker.getValue() != null) ? startDatePicker.getValue().toString() : null;
            String end = (endDatePicker.getValue() != null) ? endDatePicker.getValue().toString() : null;
            loadFilteredData(region, start, end);
        });

        HBox filterBox = new HBox(10, new Label("Region:"), regionFilter,
                new Label("Start:"), startDatePicker,
                new Label("End:"), endDatePicker,
                applyFilterBtn);
        filterBox.setAlignment(Pos.CENTER);

        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((obs, oldText, newText) -> applySearchFilter(newText));

        layout.getChildren().addAll(uploadButton, viewChartsBtn, exportButton, kpiBox, filterBox, searchField, new Separator(), tableView);
    }

    public VBox getView() {
        return layout;
    }

    private void handleFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Order CSV");
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            importCSV(file);
            updateKPI();
            loadTableData();
        }
    }

    private void importCSV(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             Connection conn = Database.getConnection()) {

            String line;
            reader.readLine(); // skip header

            String sql = "INSERT INTO orders (order_id, order_date, customer_name, region, category, sub_category, product_name, quantity, sales, profit) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 10) continue;

                pstmt.setString(1, data[0]);
                pstmt.setString(2, data[1]);
                pstmt.setString(3, data[2]);
                pstmt.setString(4, data[3]);
                pstmt.setString(5, data[4]);
                pstmt.setString(6, data[5]);
                pstmt.setString(7, data[6]);
                pstmt.setInt(8, Integer.parseInt(data[7]));
                pstmt.setDouble(9, Double.parseDouble(data[8]));
                pstmt.setDouble(10, Double.parseDouble(data[9]));
                pstmt.addBatch();
            }

            pstmt.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateKPI() {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs1 = stmt.executeQuery("SELECT SUM(sales) FROM orders");
            totalSalesLabel.setText("Total Sales: ₹" + String.format("%.2f", rs1.getDouble(1)));

            ResultSet rs2 = stmt.executeQuery("SELECT SUM(profit) FROM orders");
            totalProfitLabel.setText("Total Profit: ₹" + String.format("%.2f", rs2.getDouble(1)));

            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) FROM orders");
            totalOrdersLabel.setText("Total Orders: " + rs3.getInt(1));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTableData() {
        loadFilteredData("All", null, null);
    }

    private void loadFilteredData(String region, String startDate, String endDate) {
        tableView.getItems().clear();
        tableView.getColumns().clear();
        fullData.clear();

        String query = "SELECT * FROM orders WHERE 1=1";
        if (!region.equals("All")) {
            query += " AND region = '" + region + "'";
        }
        if (startDate != null && endDate != null) {
            query += " AND order_date BETWEEN '" + startDate + "' AND '" + endDate + "'";
        }

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int columnCount = rs.getMetaData().getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                final int colIndex = i - 1;
                TableColumn<String[], String> col = new TableColumn<>(rs.getMetaData().getColumnName(i));
                col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[colIndex]));
                tableView.getColumns().add(col);
            }

            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getString(i);
                }
                fullData.add(row); // Save full data
            }

            applySearchFilter(searchField.getText());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void applySearchFilter(String keyword) {
        ObservableList<String[]> filteredData = FXCollections.observableArrayList();

        for (String[] row : fullData) {
            for (String cell : row) {
                if (cell != null && cell.toLowerCase().contains(keyword.toLowerCase())) {
                    filteredData.add(row);
                    break;
                }
            }
        }

        tableView.setItems(filteredData);
    }

    private void exportTableDataToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV");
        fileChooser.setInitialFileName("exported_orders.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                for (TableColumn<?, ?> col : tableView.getColumns()) {
                    writer.write(col.getText() + ",");
                }
                writer.write("\n");

                for (String[] row : tableView.getItems()) {
                    for (String cell : row) {
                        writer.write(cell + ",");
                    }
                    writer.write("\n");
                }

                showAlert("Success", "Exported successfully to: " + file.getName());

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Export failed!");
            }
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

