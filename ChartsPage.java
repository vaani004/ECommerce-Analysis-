import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class ChartsPage {

    public void showCharts() {
        Stage stage = new Stage();
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(15));

        layout.getChildren().addAll(
                createTopProductsChart(),
                createCategoryPieChart(),
                createMonthlySalesLineChart()
        );

        Button close = new Button("Close");
        close.setOnAction(e -> stage.close());
        layout.getChildren().add(close);

        Scene scene = new Scene(layout, 900, 800);
        scene.getStylesheets().add("chart-style.css"); // Optional for CSS theme
        stage.setTitle("E-Commerce Sales Charts");
        stage.setScene(scene);
        stage.show();
    }

    // 1. BarChart: Top 5 Products
    private BarChart<String, Number> createTopProductsChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Top 5 Selling Products");
        xAxis.setLabel("Product");
        yAxis.setLabel("Total Sales");

        chart.setCategoryGap(20);
        chart.setBarGap(10);
        chart.setStyle("-fx-bar-fill: #2196f3;");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Sales");

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT product_name, SUM(sales) as total FROM orders GROUP BY product_name ORDER BY total DESC LIMIT 5")) {

            while (rs.next()) {
                String name = rs.getString("product_name");
                double value = rs.getDouble("total");

                XYChart.Data<String, Number> data = new XYChart.Data<>(name, value);
                series.getData().add(data);

                Tooltip.install(data.getNode(), new Tooltip(name + ": ₹" + String.format("%.2f", value)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        chart.getData().add(series);
        return chart;
    }

    // 2. PieChart: Sales by Category
    private PieChart createCategoryPieChart() {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT category, SUM(sales) as total FROM orders GROUP BY category")) {

            while (rs.next()) {
                pieData.add(new PieChart.Data(rs.getString("category"), rs.getDouble("total")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PieChart chart = new PieChart(pieData);
        chart.setTitle("Sales by Category");
        chart.setLabelsVisible(true);
        chart.setClockwise(true);
        chart.setStartAngle(90);

        for (PieChart.Data data : chart.getData()) {
            Tooltip tooltip = new Tooltip(data.getName() + ": ₹" + String.format("%.2f", data.getPieValue()));
            Tooltip.install(data.getNode(), tooltip);
        }

        return chart;
    }

    // 3. LineChart: Monthly Sales Trend
    private LineChart<String, Number> createMonthlySalesLineChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Monthly Sales Trend");
        xAxis.setLabel("Month-Year");
        yAxis.setLabel("Total Sales");

        chart.setCreateSymbols(true);
        chart.setLegendVisible(true);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Sales");

        String query = "SELECT strftime('%m-%Y', order_date) as month_year, SUM(sales) as total " +
                       "FROM orders GROUP BY month_year ORDER BY order_date";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String date = rs.getString("month_year");
                double total = rs.getDouble("total");

                XYChart.Data<String, Number> data = new XYChart.Data<>(date, total);
                series.getData().add(data);

                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        Tooltip.install(newNode, new Tooltip(date + ": ₹" + String.format("%.2f", total)));
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        chart.getData().add(series);
        return chart;
    }
}


