import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class ChartsPage {

    public void showCharts() {

        Stage stage = new Stage();

        ObservableList<String[]> data = Dashboard.globalData;

        Map<String, Double> categorySales = new HashMap<>();

        for (String[] row : data) {
            String category = row[4];
            double sales = Double.parseDouble(row[8]);

            categorySales.put(category,
                    categorySales.getOrDefault(category, 0.0) + sales);
        }

        // 📊 BAR CHART
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Sales by Category");

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (String cat : categorySales.keySet()) {
            series.getData().add(new XYChart.Data<>(cat, categorySales.get(cat)));
        }

        barChart.getData().add(series);

        // 🥧 PIE CHART
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Category Distribution");

        for (String cat : categorySales.keySet()) {
            pieChart.getData().add(new PieChart.Data(cat, categorySales.get(cat)));
        }

        pieChart.setLabelsVisible(true);

        // 📈 LINE CHART
        CategoryAxis xLine = new CategoryAxis();
        NumberAxis yLine = new NumberAxis();

        LineChart<String, Number> lineChart = new LineChart<>(xLine, yLine);
        lineChart.setTitle("Sales Trend");

        XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();

        int i = 1;
        for (String[] row : data) {
            lineSeries.getData().add(
                    new XYChart.Data<>("Order " + i, Double.parseDouble(row[8]))
            );
            i++;
        }

        lineChart.getData().add(lineSeries);

        // 🌑 ROOT LAYOUT
        VBox layout = new VBox(20, barChart, pieChart, lineChart);
        layout.setStyle("-fx-background-color: #121212; -fx-padding: 20;");

        Scene scene = new Scene(layout, 900, 650);
        stage.setScene(scene);

        // 🔥 FIX ALL WHITE BACKGROUND ISSUES
        scene.getRoot().setStyle("""
            -fx-background-color: #121212;
        """);

        // Charts background fix
        barChart.setStyle("-fx-background-color: transparent;");
        pieChart.setStyle("-fx-background-color: transparent;");
        lineChart.setStyle("-fx-background-color: transparent;");

        // Axis text
        xAxis.setStyle("-fx-tick-label-fill: white;");
        yAxis.setStyle("-fx-tick-label-fill: white;");
        xLine.setStyle("-fx-tick-label-fill: white;");
        yLine.setStyle("-fx-tick-label-fill: white;");

        // Titles
        barChart.setTitle("Sales by Category");
        pieChart.setTitle("Category Distribution");
        lineChart.setTitle("Sales Trend");

        // 🔥 COLORS (CLEAN & PREMIUM)
        barChart.lookupAll(".default-color0.chart-bar").forEach(n ->
                n.setStyle("-fx-bar-fill: #e01cc0;"));

        lineChart.lookupAll(".chart-series-line").forEach(n ->
                n.setStyle("-fx-stroke: #e01cc0;"));

        lineChart.lookupAll(".chart-line-symbol").forEach(n ->
                n.setStyle("-fx-background-color: #1890da, #0d0aac;"));

        pieChart.lookupAll(".default-color0.chart-pie").forEach(n ->
                n.setStyle("-fx-pie-color: #0f47dfe2;"));

        pieChart.lookupAll(".default-color1.chart-pie").forEach(n ->
                n.setStyle("-fx-pie-color: rgb(243, 5, 176) ;"));

        stage.setTitle("Analytics Dashboard");
        stage.show();
    }
}