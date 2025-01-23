package tictactoeserver;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Piechart extends Application {

    private final ObservableSet<String> onlinePlayerData;
    private final ObservableSet<String> ingamePlayerData;
    private final int totalExpectedUsers;
    private PieChart pieChart;

    public Piechart(ObservableSet<String> onlinePlayerData, ObservableSet<String> ingamePlayerData, int totalExpectedUsers) {
        this.onlinePlayerData = onlinePlayerData;
        this.ingamePlayerData = ingamePlayerData;
        this.totalExpectedUsers = totalExpectedUsers;
    }

    @Override
    public void start(Stage stage) {
        pieChart = new PieChart();
        pieChart.setTitle("User Status Distribution");

        updatePieChart(); // Set up the initial chart
        setupDataListeners(); // Dynamically update chart as data changes

        StackPane root = new StackPane(pieChart);
        Scene scene = new Scene(root, 400, 300);

        stage.setTitle("Tic Tac Toe Server - User Status");
        stage.setScene(scene);
        stage.show();
    }

    private void setupDataListeners() {
        // Update the pie chart whenever player data changes
        onlinePlayerData.addListener((SetChangeListener.Change<? extends String> change) -> updatePieChart());
        ingamePlayerData.addListener((SetChangeListener.Change<? extends String> change) -> updatePieChart());
    }

    private void updatePieChart() {
        // Calculate user counts
        int onlineUsers = onlinePlayerData.size();
        int ingameUsers = ingamePlayerData.size();
        int offlineUsers = Math.max(totalExpectedUsers - onlineUsers - ingameUsers, 0);

        // Update the chart on the JavaFX Application thread
        Platform.runLater(() -> {
            pieChart.getData().clear();
            PieChart.Data onlineData = new PieChart.Data("Online Users", onlineUsers);
            PieChart.Data ingameData = new PieChart.Data("In-Game Users", ingameUsers);
            PieChart.Data offlineData = new PieChart.Data("Offline Users", offlineUsers);

            // Add tooltips for better interactivity
            addTooltip(onlineData, "Online Users: " + onlineUsers);
            addTooltip(ingameData, "In-Game Users: " + ingameUsers);
            addTooltip(offlineData, "Offline Users: " + offlineUsers);

            pieChart.getData().addAll(onlineData, ingameData, offlineData);
        });
    }

    private void addTooltip(PieChart.Data data, String tooltipText) {
        Tooltip tooltip = new Tooltip(tooltipText);
        Tooltip.install(data.getNode(), tooltip);
    }

    public void addOnlinePlayer(String player) {
        if (player != null && !player.isEmpty()) {
            Platform.runLater(() -> onlinePlayerData.add(player));
        }
    }

    public void removeOnlinePlayer(String player) {
        if (player != null && !player.isEmpty()) {
            Platform.runLater(() -> onlinePlayerData.remove(player));
        }
    }

    public void addIngamePlayer(String player) {
        if (player != null && !player.isEmpty()) {
            Platform.runLater(() -> ingamePlayerData.add(player));
        }
    }

    public void removeIngamePlayer(String player) {
        if (player != null && !player.isEmpty()) {
            Platform.runLater(() -> ingamePlayerData.remove(player));
        }
    }
}
