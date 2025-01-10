/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.derby.jdbc.ClientDriver;
import org.json.simple.JSONObject;


/**
 *
 * @author Mohamed Sameh
 */
public class DAO {
    static final String DB_URL = "jdbc:derby://localhost:1527/TicTacToe";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "root";
    
    public static int createPlayer(JSONObject player) throws SQLException {
        int res = 0;

        DriverManager.registerDriver(new ClientDriver());
        Connection con =  DriverManager.getConnection(DB_URL, 
           DB_USER, DB_PASSWORD);
       
        PreparedStatement stmt = con.prepareStatement(
            "INSERT INTO players (username, password, status) VALUES (?, ?, ?)"
        );
        
        stmt.setString(1, player.get("username").toString()); 
        stmt.setString(2, player.get("password").toString()); 
        stmt.setString(3, "online"); 
        
        res = stmt.executeUpdate();
        System.out.println("res = "+ res);
        stmt.close();
        con.close();
        
        return res;
    }
}
