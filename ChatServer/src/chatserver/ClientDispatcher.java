/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import gui.ServerFrame;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kan
 */
public class ClientDispatcher extends Thread {

    private ServerSocket dispatcher;
    private ServerFrame serverApp;
    private Thread controller;

    public ClientDispatcher(ServerFrame serverApp) {
        this.serverApp = serverApp;
        controller = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!ServerFrame.isOnline) {
                        try {
                            dispatcher.close();
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
        try {
            dispatcher = new ServerSocket(8000);
            controller.start();
        } catch (IOException ex) {
            Logger.getLogger(ClientDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (ServerFrame.isOnline) {
            try {
                Socket client = getDispatcher().accept();
                new ClientHandler(client, serverApp).start();
            } catch (SocketException ex) {
                break;
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
