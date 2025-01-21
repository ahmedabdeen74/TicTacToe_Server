package tictactoeserver;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Piechart extends Application {

    private ObservableList<PieChart.Data> pieChartData;
    private int onlineUsers = 10; // Example initial value
    private int offlineUsers = 5;  // Example initial value

    @Override
    public void start(Stage stage) {
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

        // Simulate data update
        //simulateDataUpdate();
    }
     

    private void updateUserCounts(int newOnlineUsers, int newOfflineUsers) {
        onlineUsers = newOnlineUsers;
        offlineUsers = newOfflineUsers;

        Platform.runLater(() -> {
            pieChartData.get(0).setPieValue(onlineUsers);
            pieChartData.get(1).setPieValue(offlineUsers);
        });
    }


   
    public static void main(String[] args) {
        launch(args);
    }
}
