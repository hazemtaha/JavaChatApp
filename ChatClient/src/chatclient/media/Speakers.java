/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient.media;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author kan
 */
public class Speakers {

    private AudioFormat audioFormat;
    private SourceDataLine line;
    private boolean stopped = false;

    public Speakers() {
        audioFormat = new AudioFormat(16000, 16, 1, true, true);
        try {
            line = AudioSystem.getSourceDataLine(audioFormat);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Microphone.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void write(InputStream reader) {
        stopped = false;
        try {
            line.open();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Speakers.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        line.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int bytesRead = 0;
                byte[] buffer = new byte[1024];
                while (!stopped) {
                    try {
                        bytesRead = reader.read(buffer, 0, buffer.length);

                    } catch (SocketException ex) {
                        Logger.getLogger(Speakers.class.getName()).log(Level.SEVERE, null, ex);
                        break;
                    } catch (IOException ex) {
                        Logger.getLogger(Speakers.class.getName()).log(Level.SEVERE, null, ex);
                        break;
                    }
                    if (bytesRead != -1) {
                        line.write(buffer, 0, bytesRead);
                    }
//                    System.out.println(bytesRead);
                }
            }
        }).start();
    }

    public void close() {
        stopped = true;
        line.stop();
        line.close();
        System.out.println("Closed");
    }

}
