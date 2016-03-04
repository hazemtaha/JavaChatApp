/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient.filesharing;

import gui.PrivateChatWindow;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
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
    private int fileSize;

    public DownloadHandler(String savePath, int fileSize, PrivateChatWindow chatRoom) {
        this.savePath = savePath;
        this.chatRoom = chatRoom;
        this.fileSize = fileSize;
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
                FileInputStream reader = (FileInputStream) socket.getInputStream();
                FileOutputStream writer = new FileOutputStream(savePath);
                byte[] buffer = new byte[1024];
                int count;
                int progress = 0;
                chatRoom.setProgressMaximum(fileSize);
                chatRoom.toggleProgressBar();
                while ((count = reader.read(buffer)) >= 0) {
                    writer.write(buffer, 0, count);
                    writer.flush();
                    chatRoom.setProgress(++progress);
                }
                writer.close();
                reader.close();
                socket.close();
                download.close();
                chatRoom.AppendMsg("File Downloaded Successfully :)" + '\n');
                chatRoom.toggleProgressBar();
                break;
            } catch (EOFException | ConnectException ex) {
                Logger.getLogger(UploadHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DownloadHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
