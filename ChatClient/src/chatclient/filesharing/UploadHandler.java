/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient.filesharing;

import gui.PrivateChatWindow;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
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
    private FileOutputStream writer;
    private File file;
    private PrivateChatWindow chatRoom;
    private int fileSize;

    public UploadHandler(String ipAddress, File file, PrivateChatWindow chatRoom) {
        this.file = file;
        this.ipAddress = ipAddress;
        this.chatRoom = chatRoom;
        fileSize = Math.round(file.length() / 1024);
        try {
            socket = new Socket(InetAddress.getByName(ipAddress), 5000);
            writer = (FileOutputStream) socket.getOutputStream();
            reader = new FileInputStream(file);
        } catch (IOException ex) {
            Logger.getLogger(UploadHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int count;
        int progress = 0;
        chatRoom.setProgressMaximum(fileSize);
        chatRoom.toggleProgressBar();
        try {
            while ((count = reader.read(buffer)) >= 0) {
                writer.write(buffer, 0, count);
                writer.flush();
                chatRoom.setProgress(++progress);
            }
            writer.close();
            reader.close();
            socket.close();
            chatRoom.AppendMsg("File Sent Successfully :)" + '\n');
            chatRoom.toggleProgressBar();
        } catch (EOFException | ConnectException ex) {
            Logger.getLogger(UploadHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UploadHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
