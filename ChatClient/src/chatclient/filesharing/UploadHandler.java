/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient.filesharing;

import gui.PrivateChatWindow;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kan
 */
public class UploadHandler extends Thread {

    private String ipAddress;
    private Socket socket;
    private FileInputStream reader;
    private OutputStream writer;
    private File file;
    private PrivateChatWindow chatRoom;

    public UploadHandler(String ipAddress, File file, PrivateChatWindow chatRoom) {
        this.file = file;
        this.ipAddress = ipAddress;
        this.chatRoom = chatRoom;
        try {
            socket = new Socket(InetAddress.getByName(ipAddress), 5000);
            writer = socket.getOutputStream();
            reader = new FileInputStream(file);
        } catch (IOException ex) {
            Logger.getLogger(UploadHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int count;
        try {
            while ((count = reader.read(buffer)) >= 0) {
                writer.write(buffer, 0, count);
            }
            writer.flush();
            writer.close();
            reader.close();
            socket.close();
            chatRoom.AppendMsg("File Sent Successfully :)" + '\n');

            System.out.println("Upload Is Done");
        } catch (IOException ex) {
            Logger.getLogger(UploadHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
