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

    private Label label;
    @FXML
    private Button startBtn;
    @FXML
    private Button stopBtn;
    @FXML
    private Button showPieChartBtn; // New button for showing the pie chart
    @FXML
    private ListView<String> onlinePlayersList;
    private ObservableSet<String> playerData;

    private Stage stage;
    
    String selectedPlayer = "";
    
    private ServerManager serverManager;

    private Label selectedItem;

    @FXML
    private ListView<String> ingamePlayersList;
     private ObservableSet<String> gamerData;
    
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        playerData = FXCollections.observableSet();

        onlinePlayersList.getSelectionModel().selectedItemProperty().addListener((observable) -> {
            selectedPlayer = onlinePlayersList.getSelectionModel().getSelectedItem();
            selectedItem.setText(selectedPlayer);
        });

        playerData.addListener((SetChangeListener.Change<? extends String> c) -> {
            if (c.wasAdded()) {
                onlinePlayersList.getItems().add(c.getElementAdded());
            }
            if (c.wasRemoved()) {
                onlinePlayersList.getItems().remove(c.getElementRemoved());
            }

        }); 
        
         gamerData = FXCollections.observableSet();

        ingamePlayersList.getSelectionModel().selectedItemProperty().addListener((observable) -> {
            selectedPlayer = ingamePlayersList.getSelectionModel().getSelectedItem();
            selectedItem.setText(selectedPlayer);
        });
       

        gamerData.addListener((SetChangeListener.Change<? extends String> c) -> {
            if (c.wasAdded()) {
                ingamePlayersList.getItems().add(c.getElementAdded());
            }
            if (c.wasRemoved()) {
                ingamePlayersList.getItems().remove(c.getElementRemoved());
            }
        }); 

    }

    @FXML
    private void startButtonClicked(ActionEvent event) {
        if (serverManager == null) {
            serverManager = new ServerManager(this);
            System.out.println("Server started!");
            startBtn.setDisable(true);
            stopBtn.setDisable(false);
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
        }
    }

    @FXML
    private void showPieChartButtonClicked(ActionEvent event) {
        // Launch the Piechart application
        Platform.runLater(() -> {
            Piechart piechart = new Piechart();
            piechart.start(new Stage());
            for (String player : playerData) {
                piechart.addOnlinePlayer(player);
            }
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

      public void addInGamePlayer(String username) {
        Platform.runLater(() -> {
            gamerData.add(username);
        });
    }
    public void removeInGamePlayer(String username) {
        Platform.runLater(() -> {
            gamerData.remove(username);
        });
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
