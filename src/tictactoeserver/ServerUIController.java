/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

/**
 *
 * @author Mohamed Sameh
 */
public class ServerUIController implements Initializable {
    
    private Label label;
    @FXML
    private Button startBtn;
    @FXML
    private Button stopBtn;
    @FXML
    private ListView<String> onlinePlayersList;
    private ObservableList<String> playerData;
    
    private ServerManager serverManager;
    private Thread serverThread;

    
    
    @FXML
    private void startButtonClicked(ActionEvent event) {
        
        if (serverManager == null) {

            serverManager = new ServerManager();
           
            System.out.println("Server started!");
            startBtn.setDisable(true); // Disable the Start button
            stopBtn.setDisable(false); // Enable the Stop button
        }
    }

    @FXML
    private void stopButtonClicked(ActionEvent event) {
  

        serverManager.stopServer();

        serverManager = null;
                
        System.out.println("Server stopped!");
        startBtn.setDisable(false);
        stopBtn.setDisable(true);
       
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        // Initialize the ObservableList and bind it to the ListView
        System.out.println("I'm controller");
        playerData = FXCollections.observableArrayList();
        
        onlinePlayersList.setItems(playerData);

        // Initial data display (optional)
        updateOnlinePlayers();
    }    
    
    public void updateOnlinePlayers() {
        System.out.println("Online : ");
        Platform.runLater(() -> {
            playerData.clear(); // Clear the current list
            ClientHandler.onlinePlayers.values().forEach(player -> {
                playerData.add("Username: " + player.getUsername() + ", Status: " + player.getStatus());
            });
            onlinePlayersList.setItems(playerData); // Update the ListView
        });
    }

}
