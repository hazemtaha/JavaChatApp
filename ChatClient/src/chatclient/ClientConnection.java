/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import gui.AppMain;
import gui.GroupChatWindow;
import gui.MainPanel;
import gui.PrivateChatWindow;
import java.awt.CardLayout;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import utils.Message;
import utils.User;
import utils.interfaces.MessageType;

/**
 *
 * @author kan
 */
public class ClientConnection extends Thread {

    private Socket socket;
    private ObjectInputStream objReader;
    private ObjectOutputStream objWriter;
    private AppMain chatApp;
    private User user;

    public ClientConnection(AppMain chatApp) {
        this.chatApp = chatApp;
    }

    @Override
    public void run() {
        while (true) {
            try {
                socket = new Socket(InetAddress.getLocalHost(), 8000);
                objWriter = new ObjectOutputStream(socket.getOutputStream());
                objWriter.flush();
                objReader = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    try {
                        Object obj = objReader.readObject();
                        if (obj instanceof Message) {
                            Message msg = (Message) obj;
                            System.out.println(msg.getType());
                            // here we handle different types of messages from server
                            // add case for each type
                            switch (msg.getType()) {
                                case MessageType.AUTH_YES:
                                    System.out.println("Authorized");
                                    user = (User) msg.getData();
                                    JPanel prentPanel = ((AppMain) chatApp).getPanelGroup();
                                    ((CardLayout) prentPanel.getLayout()).show(prentPanel, "mainPanel");
                                    ((MainPanel) ((AppMain) chatApp).getMainPanel()).setNameLabel(user);
                                    ((MainPanel) ((AppMain) chatApp).getMainPanel()).loadContacts(user);
                                    break;
                                case MessageType.AUTH_NO:
                                    ((AppMain) chatApp).setErrorLabel("Invalid email or password");
                                    break;
                                case MessageType.MESSAGE:
                                    MainPanel mainPanel = (MainPanel) chatApp.getMainPanel();
                                    Integer recieverId;
                                    if (msg.getSender().getId() == user.getId()) {
                                        recieverId = msg.getReciever().get(0);
                                    } else {
                                        recieverId = msg.getSender().getId();
                                    }
                                    if (msg.getReciever().size() == 1) {
                                        PrivateChatWindow chatRoom;
                                        if (mainPanel.isOpened(recieverId) == null) {
                                            chatRoom = new PrivateChatWindow(msg.getSender(), mainPanel);
                                            mainPanel.addChat(chatRoom);
                                            chatRoom.setVisible(true);
                                        } else {
                                            chatRoom = mainPanel.isOpened(recieverId);
                                        }
                                        chatRoom.AppendMsg(msg);
                                    } else if (msg.getReciever().size() > 1) {
                                        GroupChatWindow chatRoom;
                                        int chatId = mainPanel.generateChatId(msg.getUserList());
                                        System.out.println(chatId);
                                        if (mainPanel.isGroupOpened(chatId) == null) {
                                            chatRoom = new GroupChatWindow(msg.getUserList(), mainPanel, chatId);
                                            mainPanel.addGroupChat(chatRoom);
                                            chatRoom.setVisible(true);
                                        } else {
                                            chatRoom = mainPanel.isGroupOpened(chatId);
                                        }
                                        chatRoom.AppendMsg(msg);
                                    }
                                    break;
                                    
                                case MessageType.EMAIL_VALID:
                                //    ((MainPanel)((AppMain)chatApp).getMainPanel()).
                                    
                                    break;
                            }
                        }
                    } catch (EOFException ex) {
                        break;
                    } catch (IOException | ClassNotFoundException ex) {
                        Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (ConnectException ex) {
                System.out.println("Server Is Offline");
                continue;
            } catch (IOException ex) {
                Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void sendClientMsg(Message msg) {
        try {
            objWriter.writeObject(msg);
            objWriter.flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientConnection.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ClientConnection getConnection() {
        return this;
    }

    public int getUserId() {
        return user.getId();
    }
}
