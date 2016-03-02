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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import utils.Message;
import utils.interfaces.MessageType;
import utils.interfaces.UserStatues;

/**
 *
 * @author kan
 */
public class DbHandler {

    private Connection dbConnection;

    public DbHandler() {
        String stringConnection = "jdbc:mysql://localhost:3306/chatApp";
//        String stringConnection = "jdbc:mysql://localhost:3306/JAVACHAT";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            dbConnection = DriverManager.getConnection(stringConnection, "root", "iti");
//            dbConnection = DriverManager.getConnection(stringConnection, "root", "0160");
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
    public void register(String firstName, String lastName, String age, String email, String password) {
        //open the connection with the database
        //as we will put try and catch for any exceptions through our connection
        try {
            Statement stmt = dbConnection.createStatement();
            PreparedStatement register = dbConnection.prepareStatement("insert into users (firstName, lastName, age, email, password, status) values (?, ?, ?, ?, ?, ?)");
            register.setString(1, firstName);
            register.setString(2, lastName);
            register.setString(3, age);
            register.setString(4, email);
            register.setString(5, password);
            register.setInt(6, UserStatues.UNAVAILABLE);
            register.executeUpdate();

        } catch (SQLException ex) {
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
    ///////////////***********************************************************************
    //here i recieve the email message
    
    public User addFriend (String email, User user){
        //first i need to check if this email exist in data base or not
        //create the query to select from the database
        //i am recieving the input email and the existing user
         try{
             String query = "SELECT email FROM users";
             Statement st = dbConnection.createStatement();
             
             ResultSet rs = st.executeQuery(query);
             
             while (rs.next()){
                 //first check on the database if the mail exist
                 if (!email.equals(rs)) {
                          //so it is exist ** another check if it is the same mail or not
                          
                          if (email.equals(user.getEmail())) {
                             System.out.println("you can't add yourself");

                             //sendMsg(new Message(MessageType.AUTH_YES, user));
                             
                             break;
                           }else{
                             System.out.println("ok you are good to go");
                                    //third check if this user already on the contact list
                                 if (user.getContactList().contains(email)) {
                                     System.out.println("you already added this user");
                                                                            }else{
                                     //here is the start of adding this friend to his list
                                     try{
                                     PreparedStatement userObj = dbConnection.prepareStatement("SELECT * FROM users WHERE email = ?");
                                     userObj.setString(1, email);
                                     ArrayList<User> singleUser = generateList(userObj.executeQuery());
                                        
                                     //i will call the function to pass u_id and friend_id
                                     System.out.println(user.getId());
                                     //insert from the reciever to add the sender
                                     insertFriend(user.getId(), ((User)singleUser.get(0)).getId());
                                     
                                     //another insert from the sender to add the reciever
                                     
                                     insertFriend(((User)singleUser.get(0)).getId(),user.getId());


                                     //get the first element of the array which is the first user
                                     return singleUser.get(0);     
                                     //here i recieved this user data.    
                                 //    System.out.println(((User)singleUser.get(0)).getId());   
                                   //  System.out.println(((User)singleUser.get(0)).getLastName());
                                     }catch(SQLException ex){}
                                     break;
                                 }
 
                          }
                     
                      //String mailList = rs.getString("email");
                     //System.out.format("%s\n", mailList);
                 }else{
                       JFrame frame = new JFrame("InputDialog Example #2");
                       JOptionPane.showMessageDialog(frame, "This email is not exist on the database");
                       break;
                 
                      }
                              }
             
             
             
             } catch (SQLException ex){
             Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
                                        }
        //System.out.println(email);
        
        
        return null;
    }
    public void insertFriend (int userId,int friendId){
        try {
            PreparedStatement addFriend = dbConnection.prepareStatement("insert into contact_list (u_id, friend_id) values (?, ?)");
            addFriend.setInt(1, userId);
            addFriend.setInt(2, friendId);
            addFriend.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    
    ///////////////////********************************************************************
}
