/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kan
 */
public class ClientDispatcher extends Thread {

    private ServerSocket dispatcher;

    @Override
    public void run() {
        try {
            dispatcher = new ServerSocket(8000);
        } catch (IOException ex) {
            Logger.getLogger(ClientDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true) {
            try {
                Socket client = getDispatcher().accept();
                System.out.println("New Client");
                new ClientHandler(client).start();
            } catch (IOException ex) {
                Logger.getLogger(ClientDispatcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * @return the dispatcher
     */
    public ServerSocket getDispatcher() {
        return dispatcher;
    }

}
