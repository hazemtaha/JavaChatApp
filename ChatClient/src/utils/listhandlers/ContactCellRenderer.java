/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.listhandlers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import utils.User;

/**
 *
 * @author kan
 */
public class ContactCellRenderer extends JLabel implements ListCellRenderer<User> {

    public ContactCellRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends User> list, User user, int index, boolean isSelected, boolean cellHasFocus) {
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/gui/resources/"
                + user.getStatus() + ".png"));
        setBorder(new EmptyBorder(10, 5, 0, 0));
        setIcon(imageIcon);
        setIconTextGap(10);
        setText(user.getFirstName() + " " + user.getLastName());
        if (isSelected) {
            setBackground(Color.GRAY);
            setForeground(Color.DARK_GRAY);
        } else {
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
        }
        return this;
    }

}
