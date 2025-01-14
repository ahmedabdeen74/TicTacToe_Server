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
    
    
    public ServerManager(ServerUIController controller) {

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
    
    static List<String> onlinePlayers = new ArrayList<>();
    
    static Map<String, ClientHandler> onlinePlayerSocs = new HashMap<>();
    
    private boolean running = true;
    ServerUIController controlerUI;
     
    public ClientHandler(Socket soc, ServerUIController cont) {
        controlerUI = cont;
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
            while ((data = dis.readLine()) != null) {
                System.out.println("Received: " + data);
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
                        onlinePlayerSocs.put(username, this);
                        System.out.println("Hello----------- " +  username + " Resgistered successfully");

                        result.put("type", "register");
                        result.put("status", ""+res);
                        onlinePlayers.add(username);
                        controlerUI.addOnlinePlayer(username);
                        
                   }
                   else 
                   {
                        System.out.println("Hello-----------" +  jsonMsg.get("username").toString() + "Resgistered failed");
                        result.put("type", "register");
                        result.put("status", ""+res);     
                   }
                   sendJSONResponse(result);
                   broadcastOnlineList();
                } catch (SQLException ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }

                break;
            case "login":
//             createPlayer();
               break;
               
            case "sendGameReq":
                String challenged = jsonMsg.get("challenged").toString();
                String challenger = jsonMsg.get("challenger").toString();
                System.out.println("player "+  challenged + "is challanged");
                Map<String, String> req = new HashMap<>();

                req.put("type", "receiveGameReq");
                req.put("challenger", challenger);
                
                JSONObject reqJSON = new JSONObject();
                reqJSON.putAll(req);
                onlinePlayerSocs.get(challenged).ps.println(reqJSON.toJSONString());
                break;
            default:
                System.out.println("Unhandled message type: " + jsonMsg.get("type").toString());
            }
       
       }
    
    public void sendJSONResponse(Map<String, String> fields) {
        JSONObject data = new JSONObject();
        data.putAll(fields);
        System.out.println("Sending response to " +  jsonMsg.get("username").toString());
        this.ps.println(data.toJSONString());
    }
    
    private void broadcastOnlineList(){
        JSONObject message = new JSONObject();
        message.put("type", "onlinePlayers");
        message.put("players", onlinePlayers);
        
        for (ClientHandler client : clients) {
            client.ps.println(message.toJSONString());
        }  
    }
    
    
    private void cleanup() {
        try {
            // Remove the player from the onlinePlayers list
            if (jsonMsg != null && jsonMsg.containsKey("username")) {
                String username = jsonMsg.get("username").toString();
                onlinePlayers.remove(username);
                System.out.println("Player removed: " + username);
                onlinePlayerSocs.remove(username, soc);
                controlerUI.removeOnlinePlayer(username);
                broadcastOnlineList();
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