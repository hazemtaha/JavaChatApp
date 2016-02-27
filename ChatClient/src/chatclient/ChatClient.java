/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import gui.PrivateChatWindow;

/**
 *
 * @author kan
 */
public class ChatClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        PrivateChatWindow privateChatBox = new PrivateChatWindow();
        privateChatBox.setVisible(true);
    }

}
