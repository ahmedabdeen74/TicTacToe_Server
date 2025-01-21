package tictactoeserver;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Piechart extends Application {

    private ObservableList<PieChart.Data> pieChartData;
    private ObservableSet<String> playerData;
    private int onlineUsers;
    private int offlineUsers;

    @Override
    public void start(Stage stage) {
        playerData = FXCollections.observableSet();
        pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Online Users", onlineUsers),
            new PieChart.Data("Offline Users", offlineUsers)
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
        playerData.addListener((SetChangeListener.Change<? extends String> c) -> {
            updateUserCounts(playerData.size(), 0); // Assuming offline users are 0 for simplicity
        });
    }

    private void updateUserCounts(int newOnlineUsers, int newOfflineUsers) {
        onlineUsers = newOnlineUsers;
        offlineUsers = newOfflineUsers;

        Platform.runLater(() -> {
            pieChartData.get(0).setPieValue(onlineUsers);
            pieChartData.get(1).setPieValue(offlineUsers);
        });
    }

    public void addOnlinePlayer(String username) {
        Platform.runLater(() -> {
            playerData.add(username);
        });
    }

    public void removeOnlinePlayer(String username) {
        Platform.runLater(() -> {
            playerData.remove(username);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
