/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import gui.ServerFrame;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
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

    public static ArrayList<ClientHandler> visitors
            = new ArrayList<ClientHandler>();
    public static Hashtable<Integer, ClientHandler> clients
            = new Hashtable<Integer, ClientHandler>();

    /**
     * @return the visitors
     */
    public static ArrayList<ClientHandler> getVisitors() {
        return visitors;
    }

    /**
     * @return the clients
     */
    public static Hashtable<Integer, ClientHandler> getClients() {
        return clients;
    }
    private ObjectInputStream objReader;
    private ObjectOutputStream objWriter;
    private Socket socket;
    private User user;
    private DbHandler dbHandler;
    private ServerFrame serverApp;
    private Thread controller;

    public ClientHandler(Socket socket, ServerFrame serverApp) {
        this.socket = socket;
        this.serverApp = serverApp;
        dbHandler = new DbHandler();
        try {
            objWriter = new ObjectOutputStream(this.getSocket().getOutputStream());
            objWriter.flush();
            objReader = new ObjectInputStream(this.getSocket().getInputStream());
            visitors.add(this);
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        controller = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!ServerFrame.isOnline) {
                        try {
                            socket.close();
                            break;
                        } catch (IOException ex) {
                            Logger.getLogger(ClientDispatcher.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    try {
                        sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ClientDispatcher.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        });
    }

    @Override
    public void run() {
        controller.start();
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
                                if (clients.containsKey(user.getId())) {
                                    sendMsg(new Message(MessageType.AUTH_NO, "1"));
                                } else {
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

                                    String numberOnline = dbHandler.countConnected();
                                    String numOnline = dbHandler.countOnline();
                                    serverApp.connectedLbl.setText(numberOnline);
                                    serverApp.onlineLbl.setText(numOnline);
                                    /// offline messages
                                    if (dbHandler.checkOfflineMsgs(user.getId())) {
                                        ArrayList<Message> offMsgs = dbHandler.pullOfflineMsgs(user.getId());
                                        for (Message offMsg : offMsgs) {
                                            sendMsg(offMsg);
                                        }
                                    }
                                }
                            } else {
                                sendMsg(new Message(MessageType.AUTH_NO, "0"));
                            }
                            break;

                        //case in Regiser Type @mos
                        case MessageType.REGISTER:
                            //recieving the incoming message into the new Hashtable
                            Hashtable<String, String> userData = (Hashtable< String, String>) msg.getData();
                             {
                                try {
                                    //we already take an object from the User Class and DbHandler class
                                    dbHandler.register(userData.get("firstName"), userData.get("lastName"), userData.get("age"), userData.get("email"), userData.get("password"));
                                    sendMsg(new Message(MessageType.REGISTER_OK));
                                } catch (SQLException ex) {
//                                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                                    sendMsg(new Message(MessageType.REGISTER_DENIED));
                                }
                            }
                            String numberReg = dbHandler.countRegistered();
                            serverApp.regLbl.setText(numberReg);

                            break;
                        case MessageType.MESSAGE:
                            msg.setSender(user);
                            if (msg.getReciever().size() > 1) {
                                msg.setUserList(generateUserList(msg));
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
                            if (friend == null) {
                                sendMsg(new Message(MessageType.EMAIL_INVALID));

                            } else {
                                sendMsg(new Message(MessageType.EMAIL_VALID, friend));

                            }

                            //friend = dbHandler.getContactList(friend);
                            //get the friend from the data base to add him on the existing user
                            //user = dbHandler.getContactList(user);
                            //here i send the message to the user with the recent cotact list
                            //here i have the user
                            // now the user went to the existing user
                            //user.getContactList().add(friend);
                            //here i have the user
                            // now the user went to the existing user
                            //user.getContactList().add(friend);
                            break;
                        case MessageType.STATE_CHANGE:
                            user.setStatus((int) msg.getData());
                            dbHandler.updateStatus(user);
                            System.out.println("User : " + user.getStatus());
                            Hashtable<String, Integer> data = new Hashtable<>();
                            data.put("userId", user.getId());
                            data.put("status", user.getStatus());
                            sendMsgToMultiple(new Message(MessageType.UPDATE_CONTACT_LIST, data), user.getContactList());
                            String numOnline = dbHandler.countOnline();
                            String numBusy = dbHandler.countBusy();
                            String numAway = dbHandler.countAway();
                            serverApp.onlineLbl.setText(numOnline);
                            serverApp.busyLbl.setText(numBusy);
                            serverApp.awayLbl.setText(numAway);
                            break;
                        case MessageType.FILE_REQUEST:
                            int recieverId = ((ArrayList<Integer>) msg.getReciever()).get(0);
                            msg.setSender(user);
                            getClients().get(recieverId).sendMsg(msg);
                            break;
                        case MessageType.FILE_RESPONSE:
                            String reciverIp = getSocket().getInetAddress().getHostAddress();
                            System.out.println(reciverIp);
                            ((Hashtable<String, Object>) msg.getData()).put("recieverIp", reciverIp);
                            getClients().get(msg.getSender().getId()).sendMsg(msg);
                            break;
                        case MessageType.DISCONNECT:
                            user.setStatus(UserStatues.UNAVAILABLE);
                            dbHandler.updateStatus(user);
                            getClients().remove(user.getId());
                            getVisitors().add(this);
                            Hashtable<String, Integer> userInfo = new Hashtable<>();
                            userInfo.put("userId", user.getId());
                            userInfo.put("status", user.getStatus());
                            sendMsgToMultiple(new Message(MessageType.UPDATE_CONTACT_LIST, userInfo),
                                    user.getContactList());
                            String numberOnline = dbHandler.countConnected();
                            serverApp.connectedLbl.setText(numberOnline);
                            String numOnline2 = dbHandler.countOnline();
                            String numBusy2 = dbHandler.countBusy();
                            String numAway2 = dbHandler.countAway();
                            serverApp.onlineLbl.setText(numOnline2);
                            serverApp.busyLbl.setText(numBusy2);
                            serverApp.awayLbl.setText(numAway2);

                            break;

                        case MessageType.DELETE:
                            //recieve the message of the selected user here
                            User selectedUser = (User) msg.getData();
                            dbHandler.deleteFriend(selectedUser, user);
                            //get the contact list of existing user and selected user
                            user = dbHandler.getContactList(user);
                            selectedUser = dbHandler.getContactList(selectedUser);
                            //update contact list for both of them
                            sendMsg(new Message(MessageType.UPDATE_CONTACT_LIST, selectedUser));
                            if (clients.containsKey(selectedUser.getId())) {
                                clients.get(selectedUser.getId()).sendMsg(new Message(MessageType.UPDATE_CONTACT_LIST, user));
                            }

                            break;
                        case MessageType.VOICE_REQUEST:
                            msg.setType(MessageType.VOICE_REQUEST);
                            msg.setSender(user);
                            String ip = getSocket().getInetAddress().getHostAddress();
//                            int port = (int) (1050 * ((int) (Math.random() * 5)));
                            int port = 1050 + (int) (Math.random() * ((15000 - 1050) + 1));
                            ArrayList<String> msgData = new ArrayList<>();
                            msgData.add(ip);
                            msgData.add(String.valueOf(port));
                            System.out.println("IP is :" + ip);
                            System.out.println("Port is :" + port);
                            msg.setData(msgData);
                            clients.get(msg.getReciever().get(0)).sendMsg(msg);
                            break;
                        case MessageType.VOICE_RESPONSE:
                            msg.setType(MessageType.VOICE_GRANTED);
                            msg.setSender(user);
                            ip = getSocket().getInetAddress().getHostAddress();
                            msgData = new ArrayList<>();
                            msgData.add(ip);
                            msgData.add(((ArrayList<String>) msg.getData()).get(1));
                            msg.setData(msgData);
                            clients.get(msg.getReciever().get(0)).sendMsg(msg);
                            break;
                        case MessageType.VOICE_TERMINATE:
                            msg.getReciever().remove(0);
                            msg.getReciever().add(msg.getSender().getId());
                            msg.setSender(user);
                            clients.get(msg.getReciever().get(0)).sendMsg(msg);
                            break;
                    }
                }
            } catch (EOFException | SocketException ex) {
                if (user != null) {
                    user.setStatus(UserStatues.UNAVAILABLE);
                    dbHandler.updateStatus(user);
                    clients.remove(user.getId());
                    Hashtable<String, Integer> data = new Hashtable<>();
                    data.put("userId", user.getId());
                    data.put("status", user.getStatus());
                    sendMsgToMultiple(new Message(MessageType.UPDATE_CONTACT_LIST, data),
                            user.getContactList());
                    String numberOnline = dbHandler.countConnected();
                    serverApp.connectedLbl.setText(numberOnline);
                    String numOnline = dbHandler.countOnline();
                    String numBusy = dbHandler.countBusy();
                    String numAway = dbHandler.countAway();
                    serverApp.onlineLbl.setText(numOnline);
                    serverApp.busyLbl.setText(numBusy);
                    serverApp.awayLbl.setText(numAway);
                } else {
                    visitors.remove(this);
                }
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
        getClients().get(user.getId()).sendMsg(msg);
        System.out.println("Sender Is " + user.getFirstName());
        for (Integer userId : userIds) {
            System.out.println("User Id in Loop :" + userId);
            if (msg.getReciever().size() > 1) {
                msg.setUserList(generateUserList(msg));
                msg.setUserList(addReciever(msg.getSender(), msg.getUserList()));
                msg.setUserList(deleteReciever(getClients().get(userId).getUser(), msg.getUserList()));
            }
            if (clients.containsKey(userId)) {
                clients.get(userId).sendMsg(msg);
            } else {
                dbHandler.addOfflineMsg(msg);
            }
        }
    }

    synchronized public void sendMsg(Message msg) {
        try {
            objWriter.writeObject(msg);
            objWriter.flush();

        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMsgToMultiple(Message msg, ArrayList<User> recievers) {
        for (User reciever : recievers) {
            if (getClients().containsKey(reciever.getId())) {
                System.out.println("Found the user");
                getClients().get(reciever.getId()).sendMsg(msg);
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

    public ArrayList<User> generateUserList(Message msg) {
        ArrayList<User> userList = new ArrayList<>();
        for (Integer userId : msg.getReciever()) {
            if (clients.containsKey(userId)) {
                userList.add(clients.get(userId).getUser());
            }
        }
        return userList;
    }

    public Socket getSocket() {
        return socket;
    }

}
