/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient.filesharing;

import gui.PrivateChatWindow;
import java.io.FileOutputStream;
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
public class DownloadHandler extends Thread {

    private String savePath;
    private ServerSocket download;
    private PrivateChatWindow chatRoom;

    public DownloadHandler(String savePath, PrivateChatWindow chatRoom) {
        this.savePath = savePath;
        this.chatRoom = chatRoom;
    }

    @Override
    public void run() {
        try {
            download = new ServerSocket(5000);
        } catch (IOException ex) {
            Logger.getLogger(DownloadHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true) {
            try {
                Socket socket = download.accept();
                InputStream reader = socket.getInputStream();
                FileOutputStream writer = new FileOutputStream(savePath);
                byte[] buffer = new byte[1024];
                int count;
                while ((count = reader.read(buffer)) >= 0) {
                    writer.write(buffer, 0, count);
                }
                writer.flush();
                writer.close();
                reader.close();
                socket.close();
                chatRoom.AppendMsg("File Downloaded Successfully :)" + '\n');
                System.out.println("DownLoad Is Done");
                break;
            } catch (IOException ex) {
                Logger.getLogger(DownloadHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
