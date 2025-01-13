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
    
    public ServerManager() {
        System.out.println("running " + running);
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
                            ClientHandler handler = new ClientHandler(clientSocket);
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
             // Interrupt each client handler thread (if they are running)
            for(ClientHandler handler : clientHandlers) 
            {
                handler.stopHandler(); // Make sure to stop client processing
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}

class ClientHandler extends Thread{
    
    // ear
    public DataInputStream dis;
    // mouth
    public PrintStream ps;
    public Socket soc;
    public String data;
    private JSONObject jsonMsg;
    static Vector<ClientHandler> clients = new Vector<ClientHandler>();
    static Map<String, DTOPlayer> onlinePlayers = new HashMap<>();
     private boolean running = true;
     
    public ClientHandler(Socket soc) {
        try {
            this.soc = soc;
            dis =  new DataInputStream(soc.getInputStream());
            ps = new PrintStream(soc.getOutputStream());
            ClientHandler.clients.add(this);
            start();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                data = dis.readLine(); // Read data from client
                if (data == null || data.isEmpty()) {
                    System.out.println("Break ");
                    break; // Client disconnected or sent an empty message
                }
                handleJSON(data);
            }
        } catch (IOException | ParseException ex) {
            System.out.println("Client disconnected: " + soc.getInetAddress());
        } finally {
            cleanup(); // Cleanup resources
        }
    }
    
    public void stopHandler() {
           running = false; 
           try {
               if (soc != null && !soc.isClosed()) 
               {
                   soc.close(); // Close the client socket to end the connection
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    private void handleJSON(String data) throws ParseException{
        JSONParser parser = new JSONParser();
            
        jsonMsg = (JSONObject) parser.parse(data);
            

        switch(jsonMsg.get("type").toString()){
            case "register":
                try {
                 System.out.println("Regsitration-----------");
                   int res = DAO.createPlayer(jsonMsg);
                   Map<String, String> result = new HashMap<>();
                   
                   if(res == 1)
                   {
                        String username = jsonMsg.get("username").toString();
                        System.out.println("Hello-----------" +  username + " Resgistered successfully");

                        result.put("type", "register");
                        result.put("status", ""+res);    
                        DTOPlayer player = new DTOPlayer(username, "online", 0, soc);
                        onlinePlayers.put(username, player);
                        System.out.println(onlinePlayers);
                   }
                   else 
                   {
                        System.out.println("Hello-----------" +  jsonMsg.get("username").toString() + "Resgistered failed");
                        result.put("type", "register");
                        result.put("status", ""+res);     
                   }
                   sendJSONResponse(result);
                } catch (SQLException ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }

                break;
                case "login":
                    try {
                 System.out.println("Login-----------");
                   int res = DAO.validatePlayer(jsonMsg);
                   Map<String, String> result = new HashMap<>();
                   
                   if(res == 1)
                   {
                        String username = jsonMsg.get("username").toString();
                        System.out.println("Hello-----------" +  username + " Login successfully");

                        result.put("type", "login");
                        result.put("status", ""+res);    
                        DTOPlayer player = new DTOPlayer(username, "online", 0, soc);
                        onlinePlayers.put(username, player);
                        System.out.println(onlinePlayers);
                   }
                   else 
                   {
                        System.out.println("Hello-----------" +  jsonMsg.get("username").toString() + "Login failed");
                        result.put("type", "login");
                        result.put("status", ""+res);     
                   }
                   sendJSONResponse(result);
                } catch (SQLException ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }

//                    createPlayer();
                    break;
            }
       
       }
    
    public void sendJSONResponse(Map<String, String> fields) {
        JSONObject data = new JSONObject();
        data.putAll(fields);
        System.out.println("Sending response to " +  jsonMsg.get("username").toString());
        this.ps.println(data.toJSONString());
    }
    
    private void cleanup() {
        try {
            // Remove the player from the onlinePlayers map
            if (jsonMsg != null && jsonMsg.containsKey("username")) {
                String username = jsonMsg.get("username").toString();
                onlinePlayers.remove(username);
                System.out.println("Player removed: " + username);
            }
            
            if (ps != null) ps.close();
            if (dis != null) dis.close();
            if (soc != null) soc.close();
            
            clients.remove(this); // Remove from active clients
            System.out.println("Client removed: " + soc.getInetAddress());
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}