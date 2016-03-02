/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Message;
import utils.User;
import utils.interfaces.MessageType;
import utils.interfaces.UserStatues;

/**
 *
 * @author kan
 */
public class ClientHandler extends Thread {

    private static ArrayList<ClientHandler> visitors
            = new ArrayList<ClientHandler>();
    private static Hashtable<Integer, ClientHandler> clients
            = new Hashtable<Integer, ClientHandler>();
    private ObjectInputStream objReader;
    private ObjectOutputStream objWriter;
    private Socket socket;
    private User user;
    private DbHandler dbHandler;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        dbHandler = new DbHandler();
        try {
            objWriter = new ObjectOutputStream(this.socket.getOutputStream());
            objWriter.flush();
            objReader = new ObjectInputStream(this.socket.getInputStream());
            visitors.add(this);
            //this.start(); should be started when instanciated
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object obj = objReader.readObject();
                if (obj instanceof Message) {
                    Message msg = (Message) obj;
                    System.out.println(msg.getType());
                    // here we'll handle different kind of requests based on the message type
                    switch (msg.getType()) {
                        case MessageType.LOGIN:
                            Hashtable<String, String> credentials
                                    = (Hashtable< String, String>) msg.getData();
                            user = dbHandler.login(credentials.get("email"), credentials.get("password"));
                            if (user != null) {
                                user = dbHandler.getContactList(user);
                                user.setStatus(UserStatues.AVAILABLE);
                                System.out.println("Contact List Size : " + user.getContactList().size());
                                dbHandler.updateStatus(user);
                                visitors.remove(this);
                                clients.put(user.getId(), this);
                                sendMsg(new Message(MessageType.AUTH_YES, user));
                            } else {
                                sendMsg(new Message(MessageType.AUTH_NO));
                            }
                            break;

                        //case in Regiser Type @mos
                        case MessageType.REGISTER:
                            //recieving the incoming message into the new Hashtable
                            Hashtable<String, String> userData = (Hashtable< String, String>) msg.getData();
                            //we already take an object from the User Class and DbHandler class
                            dbHandler.register(userData.get("firstName"), userData.get("lastName"), userData.get("age"), userData.get("email"), userData.get("password"));

                            break;
                        // i need to delete the one i'm going to send to from reciever list and add the sender
                        // handle multiple chats
                        case MessageType.MESSAGE:
                            msg.setSender(user);
                            if (msg.getReciever().size() > 1) {
                                msg.setUserList(generateUserList(msg.getReciever()));
                            }
                            echoChatMsg(msg);
                            break;
                         //recieving the emial here   
                        case MessageType.VALIDATE_EMAIL:
                            //recieve the message of mail here in string
                            
                            String addMail = (String) msg.getData();
                            //here i send the mail and the existing user
                            //create an object from the user
                            User friend = dbHandler.addFriend(addMail, user);
                            
                            //friend = dbHandler.getContactList(friend);
                            
                            //get the friend from the data base to add him on the existing user
                            user = dbHandler.getContactList(friend);
                            
                            //here i send the message to the user with the recent cotact list
                            sendMsg(new Message(MessageType.EMAIL_VALID, user));
                            
                            //here i have the user 
                            // now the user went to the existing user
                            //user.getContactList().add(friend);
                            
                            
                    }
                }
            } catch (EOFException ex) {
                user.setStatus(UserStatues.UNAVAILABLE);
                dbHandler.updateStatus(user);
                clients.remove(user.getId());
                break;
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void echoChatMsg(Message msg) {
        ArrayList<Integer> userIds = msg.getReciever();
        clients.get(user.getId()).sendMsg(msg);
        System.out.println("Sender Is " + user.getFirstName());
        for (Integer userId : userIds) {
            System.out.println("User Id in Loop :" + userId);
            if (msg.getReciever().size() > 1) {
                msg.setUserList(generateUserList(msg.getReciever()));
                msg.setUserList(addReciever(msg.getSender(), msg.getUserList()));
                msg.setUserList(deleteReciever(clients.get(userId).getUser(), msg.getUserList()));
            }
            clients.get(userId).sendMsg(msg);
        }
    }

    public void sendMsg(Message msg) {
        try {
            objWriter.writeObject(msg);
            objWriter.flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public User getUser() {
        return user;
    }

    public ArrayList<User> deleteReciever(User user, ArrayList<User> recieverList) {
        recieverList.remove(user);
        return recieverList;
    }

    public ArrayList<User> addReciever(User user, ArrayList<User> recieverList) {
        recieverList.add(user);
        return recieverList;
    }

    public ArrayList<User> generateUserList(ArrayList<Integer> userIds) {
        ArrayList<User> userList = new ArrayList<>();
        for (Integer userId : userIds) {
            userList.add(clients.get(userId).getUser());
        }
        return userList;
    }

}
