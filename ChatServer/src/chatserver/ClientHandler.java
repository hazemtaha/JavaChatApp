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
            objWriter = new ObjectOutputStream(this.getSocket().getOutputStream());
            objWriter.flush();
            objReader = new ObjectInputStream(this.getSocket().getInputStream());
            visitors.add(this);
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        mainLoop:
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
                                Hashtable<String, Integer> data = new Hashtable<>();
                                data.put("userId", user.getId());
                                data.put("status", user.getStatus());
                                sendMsgToMultiple(new Message(MessageType.UPDATE_CONTACT_LIST, data),
                                        user.getContactList());
                            } else {
                                sendMsg(new Message(MessageType.AUTH_NO));
                            }
                            break;

                        //case in Regiser Type @mos
                        case MessageType.REGISTER:
                            //recieving the incoming message into the new Hashtable
                            //recieving the incoming message into the new Hashtable
                            Hashtable<String, String> userData = (Hashtable< String, String>) msg.getData();
                            //we already take an object from the User Class and DbHandler class
                            dbHandler.register(userData.get("firstName"), userData.get("lastName"), userData.get("age"), userData.get("email"), userData.get("password"));

                            break;
                        case MessageType.MESSAGE:
                            msg.setSender(user);
                            if (msg.getReciever().size() > 1) {
                                msg.setUserList(generateUserList(msg.getReciever()));
                            }
                            echoChatMsg(msg);
                            break;
                        case MessageType.STATE_CHANGE:
                            user.setStatus((int) msg.getData());
                            dbHandler.updateStatus(user);
                            System.out.println("User : " + user.getStatus());
                            Hashtable<String, Integer> data = new Hashtable<>();
                            data.put("userId", user.getId());
                            data.put("status", user.getStatus());
                            sendMsgToMultiple(new Message(MessageType.UPDATE_CONTACT_LIST, data), user.getContactList());
                            break;
                        case MessageType.FILE_REQUEST:
                            int recieverId = ((ArrayList<Integer>) msg.getReciever()).get(0);
                            msg.setSender(user);
                            clients.get(recieverId).sendMsg(msg);
                            break;
                        case MessageType.FILE_RESPONSE:
                            String reciverIp = getSocket().getInetAddress().getHostAddress();
                            System.out.println(reciverIp);
                            ((Hashtable<String, Object>) msg.getData()).put("recieverIp", reciverIp);
                            clients.get(msg.getSender().getId()).sendMsg(msg);
                            break;
                        case MessageType.DISCONNECT:
                            user.setStatus(UserStatues.UNAVAILABLE);
                            dbHandler.updateStatus(user);
                            clients.remove(user.getId());
                            visitors.add(this);
                            Hashtable<String, Integer> userInfo = new Hashtable<>();
                            userInfo.put("userId", user.getId());
                            userInfo.put("status", user.getStatus());
                            sendMsgToMultiple(new Message(MessageType.UPDATE_CONTACT_LIST, userInfo),
                                    user.getContactList());
                            break;
                    }
                }
            } catch (EOFException ex) {
                user.setStatus(UserStatues.UNAVAILABLE);
                dbHandler.updateStatus(user);
                clients.remove(user.getId());
                Hashtable<String, Integer> data = new Hashtable<>();
                data.put("userId", user.getId());
                data.put("status", user.getStatus());
                sendMsgToMultiple(new Message(MessageType.UPDATE_CONTACT_LIST, data),
                        user.getContactList());
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

    synchronized public void sendMsg(Message msg) {
        try {
            objWriter.writeObject(msg);
            objWriter.flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMsgToMultiple(Message msg, ArrayList<User> recievers) {
        for (User reciever : recievers) {
            if (clients.containsKey(reciever.getId())) {
                System.out.println("Found the user");
                clients.get(reciever.getId()).sendMsg(msg);
            }
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

    public Socket getSocket() {
        return socket;
    }

}
