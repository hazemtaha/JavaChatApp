/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient.media;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author kan
 */
public class Microphone {

    private AudioFormat audioFormat;
    private TargetDataLine line;
    private boolean stopped = false;

    public Microphone() {
        audioFormat = new AudioFormat(16000, 16, 1, true, true);
        try {
            line = AudioSystem.getTargetDataLine(audioFormat);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Microphone.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void read(OutputStream writer) {
        stopped = false;
        try {
            line.open();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Microphone.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        line.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int bytesRead = 0;
                byte[] buffer = new byte[1024];
                while (!stopped) {
                    bytesRead = line.read(buffer, 0, buffer.length);
                    try {
                        writer.write(buffer, 0, bytesRead);
                    } catch (IOException ex) {
                        Logger.getLogger(Microphone.class.getName()).log(Level.SEVERE, null, ex);
                        break;
                    }
                }
            }
        }).start();

    }

    public void close() {
        stopped = true;
        line.stop();
        line.close();
        System.out.println("Stopped");
    }

}
