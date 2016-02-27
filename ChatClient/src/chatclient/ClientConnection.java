/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import gui.AppMain;
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
import javax.swing.JFrame;
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
    private JFrame chatApp;
    private User user;

    public ClientConnection(JFrame chatApp) {
//        try {
        this.chatApp = chatApp;
//            socket = new Socket(InetAddress.getLocalHost(), 8000);
//            objWriter = new ObjectOutputStream(socket.getOutputStream());
//            objWriter.flush();
//            objReader = new ObjectInputStream(socket.getInputStream());
//            //        start(); //shoud be called when instanciated
//        } catch (IOException ex) {
//            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
//        }

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
                                    ((PrivateChatWindow) chatApp).AppendMsg(msg);
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
}
