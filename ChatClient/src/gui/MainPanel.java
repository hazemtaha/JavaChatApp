/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import chatclient.ClientConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import utils.EmailValidator;
import utils.Message;
import utils.User;
import utils.interfaces.MessageType;
import utils.Message;
import utils.User;
import utils.interfaces.MessageType;
import utils.interfaces.UserStatues;
import utils.listhandlers.ContactCellRenderer;

/**
 *
 * @author kan
 */
public class MainPanel extends javax.swing.JPanel {

    private DefaultListModel listModel;
    private ArrayList<PrivateChatWindow> chats;
    private ArrayList<GroupChatWindow> groupChats;
    private AppMain parent;
    private PrivateChatWindow chatRoom;
    private GroupChatWindow groupChatRoom;

    /**
     * Creates new form MainPanel
     */
    public MainPanel() {
    }

    public MainPanel(AppMain parent) {
        initComponents();
        this.parent = parent;
        chats = new ArrayList<>();
        groupChats = new ArrayList<>();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        createGroupChatBtn = new javax.swing.JButton();
        deleteFriendBtn = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        statusComboBox = new javax.swing.JComboBox<>();
        nameLabel = new javax.swing.JLabel();
        addFriendBtn = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        contactsList = new javax.swing.JList<>();

        createGroupChatBtn.setText("Create Group Chat");
        createGroupChatBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createGroupChatBtnActionPerformed(evt);
            }
        });

        deleteFriendBtn.setText("Delete friend");
        deleteFriendBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteFriendBtnActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Ubuntu", 3, 15)); // NOI18N
        jLabel14.setText("Friends list");

        statusComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Online", "Away", "Busy" }));
        statusComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusComboBoxActionPerformed(evt);
            }
        });

        nameLabel.setText("Mostafa Medhat");

        addFriendBtn.setText("Add Friend");
        addFriendBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFriendBtnActionPerformed(evt);
            }
        });

        contactsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                contactsListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(contactsList);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(nameLabel)
                        .addGap(18, 18, 18)
                        .addComponent(statusComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                        .addComponent(addFriendBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(deleteFriendBtn))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(0, 426, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(createGroupChatBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                            .addGap(68, 68, 68)
                            .addComponent(jLabel14))
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(statusComboBox)
                            .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(addFriendBtn)
                            .addComponent(deleteFriendBtn))
                        .addGap(61, 61, 61)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(createGroupChatBtn)
                .addGap(135, 135, 135))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 553, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 706, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 682, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void createGroupChatBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createGroupChatBtnActionPerformed
        List<User> usersList = contactsList.getSelectedValuesList();
        removeOfflineUsers(usersList);
        int chatId = generateChatId(usersList);
        if (usersList.size() == 1) {
            if ((chatRoom = isOpened(usersList.get(0).getId())) == null) {
                chatRoom = new PrivateChatWindow(usersList.get(0), this);
                chats.add(chatRoom);
                chatRoom.setVisible(true);
            } else {
                chatRoom.setVisible(true);
            }
        } else if (usersList.size() > 1) {
            if ((groupChatRoom = isGroupOpened(chatId)) == null) {
                groupChatRoom = new GroupChatWindow(usersList, this, chatId);
                groupChats.add(groupChatRoom);
                groupChatRoom.setVisible(true);
            } else {
                groupChatRoom.setVisible(true);
            }
        } else {
            String errorMsg = "Sorry you can't create a group chat of offline users";
            JOptionPane.showMessageDialog(this, errorMsg, "Warning", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_createGroupChatBtnActionPerformed

    private void statusComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusComboBoxActionPerformed
        String selectedStatus = (String) statusComboBox.getSelectedItem();
        System.out.println(selectedStatus);
        switch (selectedStatus) {
            case "Online":
                updateStatus(UserStatues.AVAILABLE);
                break;
            case "Busy":
                updateStatus(UserStatues.BUSY);
                break;
            case "Away":
                updateStatus(UserStatues.AWAY);
                break;
        }
    }//GEN-LAST:event_statusComboBoxActionPerformed

    private void addFriendBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFriendBtnActionPerformed
/////////////***************************************************************************************************
        // ADD FRIEND HERE ;
        JFrame frame = new JFrame("InputDialog Example #2");
        String friendMail = JOptionPane.showInputDialog(
                frame,
                "Enter your friend's Email Address",
                "ADD FRIEND",
                JOptionPane.PLAIN_MESSAGE
        );
        //here i send this message as email to the server to handle it , then the server will recieve it
        //check for Email validation
        EmailValidator emailValidator = new EmailValidator();
        //trim() ->> Returns a copy of the string, with leading and trailing whitespace omitted.
        if (!emailValidator.validate(friendMail.trim())) {
            JOptionPane.showMessageDialog(frame, "Invalid Email ID");
        } else {
            //create an object to declare the message type
            Message email = new Message(MessageType.VALIDATE_EMAIL, friendMail);
            //as the MainPanel is a child from the AppMain we had an object from the app main the get the current connection
            parent.getConnection().sendClientMsg(email);
        }
    }//GEN-LAST:event_addFriendBtnActionPerformed

    private void contactsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_contactsListMouseClicked
        if (evt.getClickCount() == 2 && contactsList.getSelectedValue() instanceof User) {
            User user = (User) contactsList.getSelectedValue();
            if (isOpened(user.getId()) == null) {
                chatRoom = new PrivateChatWindow(user, this);
                chats.add(chatRoom);
                chatRoom.setVisible(true);
            }
        }

    }//GEN-LAST:event_contactsListMouseClicked

    private void deleteFriendBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteFriendBtnActionPerformed
        // get the selected user as an object
        User uDelete = contactsList.getSelectedValue();
        System.out.print(uDelete.getFirstName());
        //create an object to declare the message type
        Message selectedUser = new Message(MessageType.DELETE, uDelete);
        //as the MainPanel is a child from the AppMain we had an object from the app main the get the current connection
        parent.getConnection().sendClientMsg(selectedUser);

    }//GEN-LAST:event_deleteFriendBtnActionPerformed

    public PrivateChatWindow isOpened(int chatId) {
        for (PrivateChatWindow chat : chats) {
            if (chat.getChatUser().getId() == chatId) {
                return chat;
            }
        }
        return null;
    }

    public GroupChatWindow isGroupOpened(int chatId) {
        for (GroupChatWindow chat : groupChats) {
            if (chat.getChatId() == chatId) {
                return chat;
            }
        }
        return null;
    }

    public void addChat(PrivateChatWindow chatWindow) {
        chats.add(chatWindow);
    }

    public void addGroupChat(GroupChatWindow chatWindow) {
        groupChats.add(chatWindow);
    }

    public void removeChat(PrivateChatWindow chatWindow) {
        chats.remove(chatWindow);
    }

    public void removeGroupChat(GroupChatWindow chatWindow) {
        groupChats.remove(chatWindow);
    }

    public void setNameLabel(User user) {
        nameLabel.setText(user.getFirstName() + " " + user.getLastName());
    }

    public void loadContacts(User user) {
        ArrayList<User> contacts = user.getContactList();
        listModel = new DefaultListModel();
        for (User contact : contacts) {
            listModel.addElement(contact);
        }
        contactsList.setModel(listModel);
        contactsList.setCellRenderer(new ContactCellRenderer());
        toggleVoice(user);
    }

    public void refreshList() {
        listModel.removeAllElements();
        contactsList.removeAll();
    }

    public void toggleVoice(User user) {
        ArrayList<User> users = user.getContactList();
        for (PrivateChatWindow chat : chats) {
            if (users.get(users.indexOf(chat.getChatUser())).getStatus() == UserStatues.UNAVAILABLE) {
                chat.disableVoice();
            } else {
                chat.enableVoice();
            }
        }

    }

    public ClientConnection getConnection() {
        return parent.getConnection();
    }

    public int generateChatId(List<User> userList) {
        int chatId = 0;
        for (User user : userList) {
            chatId += user.getId();
        }
        return chatId;
    }

    public void updateStatus(int status) {
        getConnection().getUser().setStatus(status);
        getConnection().sendClientMsg(new Message(MessageType.STATE_CHANGE, status));
        System.out.println(getConnection().getUser().getStatus());
        System.out.println(status);
    }

    public List<User> removeOfflineUsers(List<User> usersList) {
        Iterator<User> listIterator = usersList.iterator();
        while (listIterator.hasNext()) {
            User user = listIterator.next();
            if (user.getStatus() == UserStatues.UNAVAILABLE) {
                listIterator.remove();
//                usersList.remove(user);
            }
        }
        return usersList;
    }

    public void destroyChats() {
        for (PrivateChatWindow chat : chats) {
            chat.dispose();
        }
        for (GroupChatWindow chat : groupChats) {
            chat.dispose();
        }
        chats.clear();
        groupChats.clear();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFriendBtn;
    private javax.swing.JList<User> contactsList;
    private javax.swing.JButton createGroupChatBtn;
    private javax.swing.JButton deleteFriendBtn;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JComboBox<String> statusComboBox;
    // End of variables declaration//GEN-END:variables
}
