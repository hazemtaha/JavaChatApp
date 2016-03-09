/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient.media.handlers;

import chatclient.media.Speakers;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kan
 */
public class RecieveHandler extends Thread {

    private ServerSocket reciver;
    private Speakers speakers;
    private InputStream reader;
    private Socket socket;
    private int port;

    public RecieveHandler(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            reciver = new ServerSocket(port);
            socket = reciver.accept();
            reader = socket.getInputStream();
            speakers = new Speakers();
            speakers.write(reader);
            System.out.println("Writing");
        } catch (IOException ex) {
            Logger.getLogger(RecieveHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void releaseSpeakers() {
        speakers.close();
        try {
            reader.close();
            socket.close();
            System.out.println("speaker released");
        } catch (IOException ex) {
            Logger.getLogger(RecieveHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
