/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import chatclient.filesharing.DownloadHandler;
import chatclient.filesharing.UploadHandler;
import chatclient.media.Speakers;
import chatclient.media.handlers.RecieveHandler;
import chatclient.media.handlers.SendHandler;
import gui.AppMain;
import gui.GroupChatWindow;
import gui.MainPanel;
import gui.Notification;
import gui.PrivateChatWindow;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
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
    public static boolean isConnected = false;
    private RecieveHandler reciverHandler;
    private SendHandler senderHandler;

    public ClientConnection(AppMain chatApp) {
        this.chatApp = chatApp;
    }

    @Override
    public void run() {
        while (true) {
            try {
                socket = new Socket(InetAddress.getLocalHost(), 8000);
//                socket = new Socket("10.0.1.95", 8000);
//                socket = new Socket("192.168.1.5", 8000);
                objWriter = new ObjectOutputStream(getSocket().getOutputStream());
                getObjWriter().flush();
                objReader = new ObjectInputStream(getSocket().getInputStream());
                isConnected = true;
                while (isConnected) {
                    try {
                        Object obj = getObjReader().readObject();
                        if (obj instanceof Message) {
                            Message msg = (Message) obj;
                            System.out.println(msg.getType());
                            // here we handle different types of messages from server
                            // add case for each type
                            switch (msg.getType()) {
                                case MessageType.AUTH_YES:
                                    System.out.println("Authorized");
                                    setUser((User) msg.getData());
                                    JPanel prentPanel = ((AppMain) chatApp).getPanelGroup();
                                    ((CardLayout) prentPanel.getLayout()).show(prentPanel, "mainPanel");
                                    ((MainPanel) ((AppMain) chatApp).getMainPanel()).setNameLabel(user);
                                    ((MainPanel) ((AppMain) chatApp).getMainPanel()).loadContacts(user);
                                    JMenuItem logOut = new JMenuItem("Logout", 'l');
                                    KeyStroke logOutKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK);
                                    logOut.setAccelerator(logOutKeyStroke);
                                    ((JMenu) ((AppMain) chatApp).getMainMenu()).add(logOut);
                                    logOut.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            sendClientMsg(new Message(MessageType.DISCONNECT));
                                            ((JMenu) ((AppMain) chatApp).getMainMenu()).remove(logOut);
                                            ((CardLayout) prentPanel.getLayout()).show(prentPanel, "loginPanel");
                                        }
                                    });
                                    File sessionStorage = new File(String.valueOf("chats_" + user.getId()));
                                    if (!sessionStorage.isDirectory()) {
                                        sessionStorage.mkdir();
                                    }
                                    break;
                                case MessageType.AUTH_NO:
                                    String errorMsg;
                                    if (msg.getData().equals("1")) {
                                        errorMsg = "Already Logged In";
                                    } else {
                                        errorMsg = "Invalid email or password";
                                    }
                                    JOptionPane.showMessageDialog(((AppMain) chatApp), errorMsg, "Error Message", JOptionPane.ERROR_MESSAGE);
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
                                    User friend = (User) msg.getData();
                                    user.getContactList().add(friend);
                                    ((MainPanel) ((AppMain) chatApp).getMainPanel()).refreshList();
                                    ((MainPanel) ((AppMain) chatApp).getMainPanel()).loadContacts(user);

                                    break;
                                case MessageType.UPDATE_CONTACT_LIST:
                                    if (msg.getData() instanceof User) {
                                        user.getContactList().remove(msg.getData());
                                        ((MainPanel) ((AppMain) chatApp).getMainPanel()).refreshList();
                                        ((MainPanel) ((AppMain) chatApp).getMainPanel()).loadContacts(user);
                                    } else {
                                        updateContactStatus((Hashtable<String, Integer>) msg.getData());
                                    }
                                    break;
                                case MessageType.FILE_REQUEST:
                                    MainPanel parentPanel = (MainPanel) chatApp.getMainPanel();
                                    if (msg.getSender().getId() == user.getId()) {
                                        recieverId = msg.getReciever().get(0);
                                    } else {
                                        recieverId = msg.getSender().getId();
                                    }
                                    PrivateChatWindow chatRoom;
                                    if (parentPanel.isOpened(recieverId) == null) {
                                        chatRoom = new PrivateChatWindow(msg.getSender(), parentPanel);
                                        parentPanel.addChat(chatRoom);
                                        chatRoom.setVisible(true);
                                    } else {
                                        chatRoom = parentPanel.isOpened(recieverId);
                                    }
                                    String dialogMsg = msg.getSender().getFirstName() + " " + msg.getSender().getLastName()
                                            + " is attempting to send you new file, Wanna Accept ?";
                                    int choice = JOptionPane.showConfirmDialog(chatRoom, dialogMsg, "New File", JOptionPane.YES_NO_OPTION);
                                    if (choice == JOptionPane.YES_OPTION) {
                                        JFileChooser saveDialog = new JFileChooser();
                                        saveDialog.setSelectedFile((File) ((Hashtable<String, Object>) msg.getData()).get("filePath"));
                                        if (saveDialog.showSaveDialog(chatRoom) == JFileChooser.APPROVE_OPTION) {
                                            File savePath = saveDialog.getSelectedFile();
                                            msg.setType(MessageType.FILE_RESPONSE);
                                            int fileSize = Math.round((long) ((Hashtable<String, Object>) msg.getData()).get("fileSize") / 1024);
                                            DownloadHandler downloadHandler = new DownloadHandler(savePath.getPath(), fileSize, chatRoom);
                                            downloadHandler.start();
                                            sendClientMsg(msg);
                                            System.out.println(savePath);
                                        }
                                    }
                                    break;
                                case MessageType.FILE_RESPONSE:
                                    parentPanel = (MainPanel) chatApp.getMainPanel();
                                    if (msg.getSender().getId() == user.getId()) {
                                        recieverId = msg.getReciever().get(0);
                                    } else {
                                        recieverId = msg.getSender().getId();
                                    }
                                    chatRoom = parentPanel.isOpened(recieverId);
                                    File file = (File) ((Hashtable<String, Object>) msg.getData()).get("filePath");
                                    String ipAddress = (String) ((Hashtable<String, Object>) msg.getData()).get("recieverIp");
                                    UploadHandler uploadHandler = new UploadHandler(ipAddress, file, chatRoom);
                                    uploadHandler.start();
                                    break;

                                case MessageType.ANNOUNCEMENT:
                                    new Notification(msg.getData().toString(), Notification.ANNOUNCEMENT);
                                    break;

                                case MessageType.EMAIL_INVALID:
                                    JFrame frame = new JFrame("JOptionPane showMessageDialog example");
                                    // show a joptionpane dialog using showMessageDialog
                                    JOptionPane.showMessageDialog(frame, "Invalid Email Address");
                                    break;
                                case MessageType.VOICE_REQUEST:
                                    msg.getReciever().remove(0);
                                    msg.getReciever().add(msg.getSender().getId());
                                    System.out.println("Sender is : " + msg.getSender());
                                    mainPanel = (MainPanel) chatApp.getMainPanel();
                                    recieverId = msg.getSender().getId();
                                    if (mainPanel.isOpened(recieverId) == null) {
                                        chatRoom = new PrivateChatWindow(msg.getSender(), mainPanel);
                                        mainPanel.addChat(chatRoom);
                                        chatRoom.setVisible(true);
                                    } else {
                                        chatRoom = mainPanel.isOpened(recieverId);
                                    }
                                    choice = JOptionPane.showConfirmDialog(chatRoom, msg.getSender().toString()
                                            + "Is Calling You . . .", "Private Call", JOptionPane.OK_CANCEL_OPTION);
                                    if (choice == JOptionPane.OK_OPTION) {
                                        String ip = ((ArrayList<String>) msg.getData()).get(0);
                                        int port = Integer.parseInt(((ArrayList<String>) msg.getData()).get(1));
                                        reciverHandler = new RecieveHandler(port);
                                        reciverHandler.start();
                                        senderHandler = new SendHandler(ip, (port - 10));
                                        senderHandler.start();
                                        msg.setType(MessageType.VOICE_RESPONSE);
                                        chatRoom.activateMic();
                                    } else {
                                        msg.setType(MessageType.VOICE_TERMINATE);
                                    }
                                    sendClientMsg(msg);
                                    break;
                                case MessageType.VOICE_GRANTED:
                                    String ip = ((ArrayList<String>) msg.getData()).get(0);
                                    int port = Integer.parseInt(((ArrayList<String>) msg.getData()).get(1));
                                    System.out.println("Granted Ip : " + ip);
                                    System.out.println("Granted Port : " + port);
                                    reciverHandler = new RecieveHandler(port - 10);
                                    reciverHandler.start();
                                    senderHandler = new SendHandler(ip, port);
                                    senderHandler.start();
                                    break;
                                case MessageType.VOICE_TERMINATE:
                                    mainPanel = (MainPanel) chatApp.getMainPanel();
                                    recieverId = msg.getSender().getId();
                                    System.out.println(mainPanel.isOpened(recieverId) != null);
                                    if ((chatRoom = mainPanel.isOpened(recieverId)) != null) {
                                        chatRoom.deActivateMic();
                                    }
                                    if (getReciverHandler() != null) {
                                        getReciverHandler().releaseSpeakers();
                                    }
                                    if (getSenderHandler() != null) {
                                        getSenderHandler().releaseMic();
                                    }
                                    break;
                                case MessageType.REGISTER_OK:
                                    prentPanel = ((AppMain) chatApp).getPanelGroup();
                                    ((CardLayout) prentPanel.getLayout()).show(prentPanel, "loginPanel");
                                    break;
                                case MessageType.REGISTER_DENIED:
                                    JOptionPane.showMessageDialog(chatApp, "Email Already Registred", "Error", JOptionPane.ERROR_MESSAGE);
                                    break;

                            }
                        }
                    } catch (EOFException | SocketException ex) {
//                        Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
                        break;
                    } catch (IOException | ClassNotFoundException ex) {
//                        Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (SocketException ex) {
                ((MainPanel) chatApp.getMainPanel()).destroyChats();
                ((CardLayout) chatApp.getPanelGroup().getLayout()).
                        show(chatApp.getPanelGroup(), "loginPanel");
                isConnected = false;
//                Logger.getLogger(ClientConn ection.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
//                Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void sendClientMsg(Message msg) {
        try {
            if (isConnected) {
                getObjWriter().writeObject(msg);
                getObjWriter().flush();

            }
        } catch (IOException ex) {
            Logger.getLogger(ClientConnection.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateContactStatus(Hashtable<String, Integer> data) {
        for (User user : user.getContactList()) {
            if (user.getId() == data.get("userId")) {
                user.setStatus(data.get("status"));
                String status = "";
                switch (data.get("status")) {
                    case 0:
                        status = "Offline";
                        break;
                    case 1:
                        status = "Online";
                        break;
                    case 2:
                        status = "Busy";
                        break;
                    case 3:
                        status = "Away";
                        break;
                }
                new Notification(user + " Is Now " + status);
            }
        }
        ((MainPanel) ((AppMain) chatApp).getMainPanel()).refreshList();
        ((MainPanel) ((AppMain) chatApp).getMainPanel()).loadContacts(user);
    }

    public ClientConnection getConnection() {
        return this;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectInputStream getObjReader() {
        return objReader;
    }

    public ObjectOutputStream getObjWriter() {
        return objWriter;
    }

    public RecieveHandler getReciverHandler() {
        return reciverHandler;
    }

    public SendHandler getSenderHandler() {
        return senderHandler;
    }
}
