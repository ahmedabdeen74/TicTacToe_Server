package tictactoeserver;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class ServerUIController implements Initializable {

    @FXML
    private Button startBtn;
    @FXML
    private Button stopBtn;
    @FXML
    private Button showPieChartBtn; // Button for showing the pie chart
    @FXML
    private ListView<String> onlinePlayersList;
    @FXML
    private ListView<String> ingamePlayersList;
    @FXML
    private Label selectedItem; // Label to display selected player

    private ObservableSet<String> playerData;
    private ObservableSet<String> gamerData;
    private Stage stage;
    private ServerManager serverManager;
    private String selectedPlayer = "";

    private static final int TOTAL_EXPECTED_USERS = 100; // Define total expected users for pie chart

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        playerData = FXCollections.observableSet();
        gamerData = FXCollections.observableSet();

        setupListView(onlinePlayersList, playerData);
        setupListView(ingamePlayersList, gamerData);

        // Disable stop and pie chart buttons initially
        stopBtn.setDisable(true);
        showPieChartBtn.setDisable(true);
    }

    private void setupListView(ListView<String> listView, ObservableSet<String> dataSet) {
        listView.getSelectionModel().selectedItemProperty().addListener((observable) -> {
            selectedPlayer = listView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                selectedItem.setText(selectedPlayer != null ? selectedPlayer : "");
            }
        });

        dataSet.addListener((SetChangeListener.Change<? extends String> c) -> {
            Platform.runLater(() -> {
                if (c.wasAdded()) {
                    listView.getItems().add(c.getElementAdded());
                }
                if (c.wasRemoved()) {
                    listView.getItems().remove(c.getElementRemoved());
                }
            });
        });
    }

    @FXML
    private void startButtonClicked(ActionEvent event) {
        if (serverManager == null) {
            serverManager = new ServerManager(this);
            System.out.println("Server started!");
            startBtn.setDisable(true);
            stopBtn.setDisable(false);
            showPieChartBtn.setDisable(false); // Enable pie chart button when server starts
        }
    }

    @FXML
    private void stopButtonClicked(ActionEvent event) {
        if (serverManager != null) {
            serverManager.stopServer();
            serverManager = null;
            System.out.println("Server stopped!");
            startBtn.setDisable(false);
            stopBtn.setDisable(true);
            showPieChartBtn.setDisable(true); // Disable pie chart button when server stops
        }
    }

    @FXML
    private void showPieChartButtonClicked(ActionEvent event) {
        // Launch the Piechart application
        Platform.runLater(() -> {
            Piechart piechart = new Piechart(playerData, gamerData, TOTAL_EXPECTED_USERS);
            piechart.start(new Stage());
        });
    }

    public void addOnlinePlayer(String username) {
        Platform.runLater(() -> playerData.add(username));
    }

    public void removeOnlinePlayer(String username) {
        Platform.runLater(() -> playerData.remove(username));
    }

    public void addInGamePlayer(String username) {
        Platform.runLater(() -> gamerData.add(username));
    }

    public void removeInGamePlayer(String username) {
        if(gamerData.contains(username)){
            Platform.runLater(() -> gamerData.remove(username));
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;

        stage.setOnCloseRequest(event -> {
            try {
                if (serverManager != null) {
                    serverManager.stopServer();
                    System.out.println("Server stopped.");
                }
            } finally {
                Platform.exit();
                System.exit(0);
            }
        });
    }
}
