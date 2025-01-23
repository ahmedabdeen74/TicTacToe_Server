/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import db.DAO;
import dto.DTOPlayer;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Mohamed Sameh
 */
public class ServerManager {
    
    private ServerSocket serverGame;
    Socket clientSocket;
    private boolean running = true;
    private List<ClientHandler> clientHandlers = new ArrayList<>();
    
    public ServerManager(ServerUIController controller) {

        try {
            serverGame = new ServerSocket(5005); // Start server on port 5005
            System.out.println("----------- Start the Server -----------");

            // Start a new thread to handle client connections
            new Thread(() -> {
                while (running) {
                    try {
                        // Blocking call waiting for client connections
                        clientSocket = serverGame.accept();
                        if (clientSocket != null) 
                        {
                            ClientHandler handler = new ClientHandler(clientSocket, controller);
                            clientHandlers.add(handler); 
                        }
                    } catch (IOException e) {
                        if (!running) 
                        {
                            // Break the loop if server is stopping
                            break;
                        }
                        e.printStackTrace(); 
                    }
                }
            }).start();
        } catch (IOException ex) {
            ex.printStackTrace(); 
        }
    }
    
    public void stopServer() {
        running = false;
        try {
            if(serverGame != null && !serverGame.isClosed()) 
            {
                serverGame.close();
                System.out.println("----------- Server Stopped -----------");
            }
             // Interrupt each client handler thread 
            for(ClientHandler handler : clientHandlers) 
            {
                handler.stopHandler(); // Make sure to stop each client processing
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}






