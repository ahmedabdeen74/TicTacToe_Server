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
        Connection con =  DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        int check = checkPlayer(player);
        if(check>0)
        {
            System.out.println("Player Aleady Exist"+player.get("username").toString());
            return 0;
        }
        else
        {
            System.out.println("Hello From createPlayer");
            PreparedStatement stmt = con.prepareStatement(
                "INSERT INTO players (username,email, password, status) VALUES (?,?, ?, ?)"
            );
            String password= player.get("password").toString();
            String newPass=getEncryptedPassWord(password);
            String email = player.get("email") != null ? player.get("email").toString() : "default@example.com";


            stmt.setString(1, player.get("username").toString()); 
            stmt.setString(2, email); 
            stmt.setString(3,newPass); 
            stmt.setString(4, "online"); 

            res = stmt.executeUpdate();
            System.out.println("res = "+ res);
            stmt.close();
            con.close();

            return res;
        }
    }
    
//     public static int loginPlayer(JSONObject player) throws SQLException {
        
//         DriverManager.registerDriver(new ClientDriver());
//         Connection con =  DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
       
//         PreparedStatement stmt = con.prepareStatement(
//             "SELECT * FROM Players WHERE username = ?"
//         );
        
//         ResultSet rs = stmt.executeQuery();
        
        
        
//         return 0;
//     }

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
       PreparedStatement stmt=con.prepareStatement("UPDATE players SET username = ?, email = ?, password = ?,status=? WHERE User_name = ?" ,
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
    String query = "SELECT * FROM Players WHERE username = ?";
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
     System.out.println("Hello from the SelectIndex");
    stmt.close();
    con.close();
    return result; 
}
    public static int checkPlayer(JSONObject player) throws SQLException {
    String query = "SELECT * FROM Players WHERE username = ?";
    Connection con=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
    PreparedStatement stmt = con.prepareStatement(query);
    stmt.setString(1, player.get("username").toString());
    ResultSet rs=stmt.executeQuery();
    int result=0;
   
     while(rs.next()){
                result=1;
                }
    System.out.println("Hello from the SelectIndex");
    stmt.close();
    con.close();
    return result; 
}
   public static int validatePlayer(JSONObject player) throws SQLException {
        System.out.println("hello from login");
        String username = player.get("username").toString();
        String inputPassword = player.get("password").toString();
        int result = 0;
        String query = "SELECT * FROM Players WHERE username = ?";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            String storedPassword="";
           // String encryptedInputPassword="";

            if (rs.next()) {
            storedPassword = getDncryptedPassWord(rs.getString("password"));
            if (storedPassword.equals(inputPassword)) {
                System.out.println("Correct password");
                result = 1;
            } else {
                System.out.println("Incorrect password");
            }
        } else {
            System.out.println("Username does not exist: " + username);
        }
    }

    return result;
}
    

    /*public static String getEncryptedPassword(String password) {
        StringBuilder encryptedPassword = new StringBuilder();
        for (char c : password.toCharArray()) {
            c += 3;
            encryptedPassword.append(c);
        }
        return encryptedPassword.toString();
    }*/

   /* public static int doesUsernameExist(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM Players WHERE username = ?";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return 0;
    }*/
}


     
     

