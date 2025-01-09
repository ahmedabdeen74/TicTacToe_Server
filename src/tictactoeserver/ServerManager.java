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
import java.util.HashMap;
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
    Map<Socket, DTOPlayer> onlinePlayers = new HashMap<>();
    
    public ServerManager(){
        try {
            serverGame = new ServerSocket(5005);
            System.out.println(" ----------- Start the Server -----------");
            while(true){
                clientSocket = serverGame.accept();
                new ClientHandler(clientSocket);
            }
        } catch (IOException ex) {
            try {
                Logger.getLogger(ServerManager.class.getName()).log(Level.SEVERE, null, ex);
                serverGame.close();
            } catch (IOException ex1) {
                ex.printStackTrace();
            }
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
                jsonHandle(data);
            }
        } catch (IOException | ParseException ex) {
            System.out.println("Client disconnected: " + soc.getInetAddress());
        } finally {
            cleanup(); // Cleanup resources
        }
    }
    
    private void cleanup() {
        try {
            if (ps != null) ps.close();
            if (dis != null) dis.close();
            if (soc != null) soc.close();
            clients.remove(this); // Remove from active clients
            System.out.println("Client removed: " + soc.getInetAddress());
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void jsonHandle(String data) throws ParseException{
        JSONParser parser = new JSONParser();
            
        jsonMsg = (JSONObject) parser.parse(data);
            
        switch(jsonMsg.get("type").toString()){
            case "register":
                try {
                 System.out.println("Regsitration-----------");
                   int res = DAO.createPlayer(jsonMsg);
                   Map<String, String> result = new HashMap<>();
                   
                   if(res == 1){
                        System.out.println("Hello-----------" +  jsonMsg.get("username").toString() + " Resgistered successfully");

                        result.put("type", "register");
                        result.put("status", ""+res);              
                   }else {
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
    
}