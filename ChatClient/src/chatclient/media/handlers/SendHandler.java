/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient.media.handlers;

import chatclient.filesharing.UploadHandler;
import chatclient.media.Microphone;
import gui.PrivateChatWindow;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kan
 */
public class SendHandler extends Thread {

    private OutputStream writer;
    private String ipAddress;
    private int port;
    private Socket socket;
    private Microphone mic;

    public SendHandler(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;

    }

    @Override
    public void run() {
        while (socket == null) {
            try {
                socket = new Socket(InetAddress.getByName(ipAddress), port);
                writer = socket.getOutputStream();
                mic = new Microphone();
                mic.read(writer);
                System.out.println("Recording");
            } catch (SocketException ex) {
                Logger.getLogger(UploadHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(UploadHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Done Connecting");
    }

    public void releaseMic() {
        mic.close();
        try {
            writer.flush();
            writer.close();
            socket.close();
            System.out.println("mic released");
        } catch (IOException ex) {
            Logger.getLogger(SendHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
