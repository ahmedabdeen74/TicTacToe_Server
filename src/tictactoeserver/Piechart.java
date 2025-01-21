package tictactoeserver;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Piechart extends Application {

    private ObservableList<PieChart.Data> pieChartData;
    private ObservableSet<String> onlinePlayerData;
    private ObservableSet<String> ingamePlayerData;
    private int onlineUsers;
    private int offlineUsers;
    private int ingameUsers;

    @Override
    public void start(Stage stage) {
        onlinePlayerData = FXCollections.observableSet();
        ingamePlayerData = FXCollections.observableSet();
        pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Online Users", onlineUsers),
            new PieChart.Data("Offline Users", offlineUsers),
            new PieChart.Data("In-Game Users", ingameUsers)
        );

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("User Status Distribution");

        StackPane root = new StackPane(pieChart);
        Scene scene = new Scene(root, 400, 300);

        stage.setTitle("Tic Tac Toe Server");
        stage.setScene(scene);
        stage.show();

        setupDataListeners();
    }

    private void setupDataListeners() {
        onlinePlayerData.addListener((SetChangeListener.Change<? extends String> c) -> {
            updateUserCounts(onlinePlayerData.size(), offlineUsers, ingameUsers);
        });

        ingamePlayerData.addListener((SetChangeListener.Change<? extends String> c) -> {
            updateUserCounts(onlineUsers, offlineUsers, ingamePlayerData.size());
        });
    }

    private void updateUserCounts(int newOnlineUsers, int newOfflineUsers, int newIngameUsers) {
        onlineUsers = newOnlineUsers;
        offlineUsers = newOfflineUsers;
        ingameUsers = newIngameUsers;

        Platform.runLater(() -> {
            pieChartData.get(0).setPieValue(onlineUsers);
            pieChartData.get(1).setPieValue(offlineUsers);
            pieChartData.get(2).setPieValue(ingameUsers);
        });
    }

    public void addOnlinePlayer(String username) {
        Platform.runLater(() -> {
            onlinePlayerData.add(username);
            offlineUsers--; // Decrease offline count when a player goes online
        });
    }

    public void removeOnlinePlayer(String username) {
        Platform.runLater(() -> {
            onlinePlayerData.remove(username);
            offlineUsers++; // Increase offline count when a player goes offline
        });
    }

    public void addIngamePlayer(String username) {
        Platform.runLater(() -> {
            ingamePlayerData.add(username);
        });
    }

    public void removeIngamePlayer(String username) {
        Platform.runLater(() -> {
            ingamePlayerData.remove(username);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
