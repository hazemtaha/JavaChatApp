/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author kan
 */
public class Message implements Serializable {

    private String type;
    private Object data;
    private User sender;
    private ArrayList<Integer> reciever;

    public Message(String type) {
        this.type = type;
    }

    public Message(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Message(String type, Object data, ArrayList<Integer> reciever) {
        this.type = type;
        this.data = data;
        this.reciever = reciever;
        this.sender = sender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ArrayList<Integer> getReciever() {
        return reciever;
    }

    public void setReciever(ArrayList<Integer> reciever) {
        this.reciever = reciever;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

}
