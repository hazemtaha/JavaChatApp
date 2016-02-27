/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.User;
import java.sql.Statement;
/**
 *
 * @author kan
 */
public class DbHandler {

    private Connection dbConnection;

    public DbHandler() {
        String stringConnection = "jdbc:mysql://localhost:3306/chatApp";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            dbConnection = DriverManager.getConnection(stringConnection, "root", "iti");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public User login(String email, String password) {
        try {
            dbConnection.createStatement();
            PreparedStatement login = dbConnection.prepareStatement("select * from users where email = ? and password = ?");
            login.setString(1, email);
            login.setString(2, password);
            ArrayList<User> userData = generateList(login.executeQuery());
            if (userData.isEmpty()) {
                return null;
            } else {
                return userData.get(0);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    
    
    //New method Declaration to enter the data into the database 
    
    public void register(String firstName, String lastName, String email, String password) {
        //open the connection with the database
        //as we will put try and catch for any exceptions through our connection
        try{
            Statement stmt = dbConnection.createStatement();
            PreparedStatement register = dbConnection.prepareStatement("insert into user (ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, AGE) values (1, ?, ?, ?, ?, 23)"); 
            register.setString(1, firstName);
            register.setString(2, lastName);
            register.setString(3, email);
            register.setString(4, password);
            
            generateList(register.executeQuery());
         
            
          }catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

    public void updateStatus(User user) {
        try {
            PreparedStatement upStatus = dbConnection.prepareStatement("update users set status = ? where u_id = ?");
            upStatus.setInt(1, user.getStatus());
            upStatus.setInt(2, user.getId());
            upStatus.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public User getContactList(User user) {
        try {
            PreparedStatement query = dbConnection.prepareStatement(""
                    + "select users.* from users,contact_list where "
                    + "contact_list.friend_id = users.u_id and contact_list.u_id = ?");
            query.setInt(1, user.getId());
            ArrayList<User> contactList = generateList(query.executeQuery());
            user.setContactList(contactList);
            return user;
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ArrayList<User> generateList(ResultSet rs) {
        if (rs != null) {
            try {
                ArrayList<User> users = new ArrayList<User>();
                while (rs.next()) {
                    users.add(new User(rs.getInt("u_id"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("email"), rs.getString("password"), rs.getInt("status"), null));
                }
                return users;
            } catch (SQLException ex) {
                Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
