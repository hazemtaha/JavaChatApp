/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;
import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;
import static java.lang.reflect.Array.set;
import javax.swing.UIManager;

/**
 *
 * @author kan
 */
public class ChatClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new SyntheticaBlackEyeLookAndFeel());

} catch (Exception e) {
                System.err.println("Look and feel not set.");
}
    }

}
