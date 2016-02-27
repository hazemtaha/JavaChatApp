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
                        case MessageType.TEST:
                            System.out.println(msg.getData());
                            break;
                        case MessageType.LOGIN:
//                            System.out.println("Entered");
                            Hashtable<String, String> credentials
                                    = (Hashtable< String, String>) msg.getData();
//                            System.out.println(credentials.get("email"));
                            user = dbHandler.login(credentials.get("email"), credentials.get("password"));
                            System.out.println(user.getFirstName());
                            if (user != null) {
                                user.setStatus(UserStatues.AVAILABLE);
                                System.out.println("Id :" + user.getId());
                                dbHandler.updateStatus(user);
                                visitors.remove(this);
                                clients.put(user.getId(), this);
                                System.out.println("Size in Initialization : " + clients.size());
                                System.out.println("Last Value : " + clients.get(user.getId()));
                                sendMsg(new Message(MessageType.AUTH_YES, user));
                            } else {
                                sendMsg(new Message(MessageType.AUTH_NO));
                            }
                            break;
                        case MessageType.MESSAGE:
                            msg.setSender(user);
                            echoChatMsg(msg);
                            break;
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
        System.out.println("Size in Echo " + clients.size());
        clients.get(user.getId()).sendMsg(msg);
        for (Integer userId : userIds) {
            System.out.println("User Id in Loop :" + userId);
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

}
