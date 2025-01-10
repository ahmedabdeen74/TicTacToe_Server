/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import dto.DTOPlayer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        JSONObject check=selectIndexPlayer(player);
        if(check.get("username").toString() == null ? player.get("username").toString() == null : check.get("username").toString().equals(player.get("username").toString()))
        {
            System.out.println("Player Aleady Exist"+check.get("username").toString());
            return 0;
        }
        else{
            PreparedStatement stmt = con.prepareStatement(
                "INSERT INTO players (username,email, password, status) VALUES (?,?, ?, ?)"
            );
            String password= player.get("password").toString();
            String newPass=getEncryptedPassWord(password);

            stmt.setString(1, player.get("username").toString()); 
            stmt.setString(2, player.get("email").toString()); 
            stmt.setString(3,newPass); 
            stmt.setString(4, "online"); 

            res = stmt.executeUpdate();
            System.out.println("res = "+ res);
            stmt.close();
            con.close();

            return res;
        }
    }
    //Encrypt anf Decrypt
    public static String getEncryptedPassWord(String newPass)
    {
       StringBuilder modifiedPass = new StringBuilder();

        for (char c : newPass.toCharArray()) { 
            c += 3; 
            modifiedPass.append(c); 
        }

        newPass = modifiedPass.toString();
        return newPass;
    }
      public static String getDncryptedPassWord(String newPass)
    {
       StringBuilder modifiedPass = new StringBuilder();

        for (char c : newPass.toCharArray()) { 
            c -= 3; 
            modifiedPass.append(c); 
        }

        newPass = modifiedPass.toString();
        return newPass;
    }
    
    public static int updateStudent(JSONObject player) throws SQLException{
     DriverManager.registerDriver(new ClientDriver());
       Connection con=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
       PreparedStatement stmt=con.prepareStatement("UPDATE player SET username = ?, email = ?, password = ?,status=? WHERE User_name = ?" ,
             ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        
           stmt.setString(1, player.get("username").toString()); 
            stmt.setString(2, player.get("email").toString()); 
            stmt.setString(3, player.get("password").toString()); 
            stmt.setString(4, player.get("status").toString()); 
             stmt.setString(5, player.get("username").toString());
            int rs=stmt.executeUpdate();
             stmt.close();
             con.close();
            return rs;
       
    }
   public static JSONObject selectIndexPlayer(JSONObject player) throws SQLException {
    String query = "SELECT * FROM Player WHERE User_name = ?";
     Connection con=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
    PreparedStatement stmt = con.prepareStatement(query);
    stmt.setString(1, player.get("username").toString());
    ResultSet rs=stmt.executeQuery();
    JSONObject result=new JSONObject();
     while(rs.next()){
                
                result.put("username",rs.getString("username"));
                result.put("email",rs.getString("email"));
                result.put("password",rs.getString("password"));
                result.put("status",rs.getString("status"));
                }
    stmt.close();
    con.close();
    return result; 
}
}
